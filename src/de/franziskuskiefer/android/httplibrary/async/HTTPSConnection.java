package de.franziskuskiefer.android.httplibrary.async;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import de.franziskuskiefer.android.httplibrary.Callback;
import de.franziskuskiefer.android.httplibrary.Util;
import de.franziskuskiefer.android.httplibrary.sake.SakeHandler;
import de.franziskuskiefer.android.httplibrary.sync.HTTPS_GET;

public abstract class HTTPSConnection extends AsyncTask<String, Void, HashMap<String, String>> {

	protected static final String GET = "GET";
	protected static final String POST = "POST";
	protected static final String DELETE = "DELETE";

	private static final int CONNECT_TIMEOUT = 15000;
	private static final int READ_TIMEOUT = 10000;
	private static final String HTTPS = "https";
	private String REQUEST_METHOD;
	private Callback caller;
	private boolean mobileApp = false;

//	private final CookieHandler cookieHandler;

	protected String textParameters = null;
	protected HashMap<String, String> parameters = null;
	protected String sakeURL = null;
	
	private HostnameVerifier verifier;
	private Context ctx;
	private boolean tlsAuth = false;
	private String url;
	private HashMap<String, String> extraGet;

	protected HTTPSConnection(Callback callback, Context ctx, boolean app){
		this.caller = callback;
		this.mobileApp = app;
		this.ctx = ctx;
		
//		cookieHandler = new CookieHandler(ctx);
		verifier = new MyHostnameVerifier(ctx);
	}

	protected HTTPSConnection(Callback callback, Context ctx, boolean app, String textParams){
		this(callback, ctx, app);
		this.textParameters = textParams;
	}

	protected HTTPSConnection(Callback callback, Context ctx, boolean app, HashMap<String, String> params) {
		this(callback, ctx, app);
		this.parameters = params;

	}

	@Override
	protected HashMap<String, String> doInBackground(String... urls) {
		this.url = urls[0];
		
		// urls come from the execute() call
		try {
			if (urls.length > 1 && urls[1] != null) // store sake URL if available
				this.sakeURL = urls[1];
			
			return request(urls[0]);
		} catch (IOException e) {
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("Exception", "Unable to retrieve web page. URL may be invalid.");
			return result;
		}
	}

	// onPostExecute invoke caller's finished
	@Override
	protected void onPostExecute(HashMap<String, String> result) {
		
		/* failure in TLS -> try to authenticate the server using sake */
		// FIXME: what to do with this? For now I remove it
//		if (result.containsKey("Exception")){
//			Log.d("HTTPSConnection", "start sake in onPostExecute ...");
//			result = authenticateTLS(result);
//		}
		if (Util.DEV){
			for (String s : result.keySet()) {
				Log.d("HTTPSConnection", "Result ("+s+") "+result.get(s));
			}
		}
		this.caller.finished(result);
	}

	private HashMap<String, String> authenticateTLS(HashMap<String, String> result) {
		SakeHandler sake = new SakeHandler();
		sake.init(ctx, this);
		sake.start();
		try {
			sake.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("HTTPSConnection", "Error: "+e.getLocalizedMessage());
		} finally {
			if (this.tlsAuth){
				// could authenticate TLS session -> redo last query
				Log.i("HTTPSConnection", "Successfully authenticated TLS session :)");
				try {
					// XXX: this should only happen if this request is GET
					GetThread thread = new GetThread();
					thread.start();
					thread.join();
					result = this.extraGet;
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.e("HTTPSConnection", "Error: "+e.getLocalizedMessage());
				}
			} else {
				Log.i("HTTPSConnection", "Error on TLS authentication :(");
			}
		}
		
		return result;
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a HashMap or an empty HashMap
	private HashMap<String, String> request(String myurl) throws IOException {
		// we only handle HTTPS connections here !!!
		if (myurl.toLowerCase(Locale.UK).startsWith(HTTPS)) {
			Log.d("HTTPSConnection", "url: "+myurl);

			HttpsURLConnection conn = (HttpsURLConnection) new URL(myurl).openConnection();
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod(REQUEST_METHOD);
			conn.setDoInput(true);

			// header to indicate app to server
			conn.addRequestProperty("MobilePoWApp", this.mobileApp ? "1" : "0");

			// add auth cookie if available
//			if (cookieHandler.getCookie() != null && cookieHandler.getCookie() != "")
//				conn.addRequestProperty("Cookie", cookieHandler.getCookie());

			// add hostname verifier
			conn.setHostnameVerifier(verifier);

			HashMap<String, String> result = doRequest(conn);
//			cookieHandler.setCookie(result.get("headers"));
			return result;
		}

		return new HashMap<String, String>();
	}

	protected abstract HashMap<String, String> doRequest(HttpsURLConnection conn) throws IOException;

	protected void setREQUEST_METHOD(String rEQUEST_METHOD) {
		REQUEST_METHOD = rEQUEST_METHOD;
	}

	public void setAuth(boolean auth) {
		this.tlsAuth = auth;
	}

	private class GetThread extends Thread {
		
		@Override
		public void run() {
			super.run();
			try {
				extraGet = new HTTPS_GET(ctx, true).execute(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.e("HTTPSConnection", "URL Exception"+e.getLocalizedMessage());
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("HTTPSConnection", "IO Exception"+e.getLocalizedMessage());
			}
		}
		
	}
	
}
