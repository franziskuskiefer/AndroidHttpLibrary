package de.franziskuskiefer.android.httplibrary.sync;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.util.Log;
import de.franziskuskiefer.android.httplibrary.CookieHandler;
import de.franziskuskiefer.android.httplibrary.Util;

public abstract class HTTPSConnection {

	protected static final String GET = "GET";
	protected static final String POST = "POST";
	protected static final String DELETE = "DELETE";

	private static final int CONNECT_TIMEOUT = 15000;
	private static final int READ_TIMEOUT = 10000;
	private static final String HTTPS = "https";
	private String REQUEST_METHOD;
	private boolean mobileApp = false;

//	private final CookieHandler cookieHandler;

	protected String textParameters = null;
	protected HashMap<String, String> parameters = null;
	protected String sakeURL = null;
	
	private HostnameVerifier verifier;

	protected HTTPSConnection(Context ctx, boolean app){
		this.mobileApp = app;
		
//		cookieHandler = new CookieHandler(ctx);
		verifier = new MyStrcitHostnameVerifier();
	}

	protected HTTPSConnection(Context ctx, boolean app, String textParams){
		this(ctx, app);
		this.textParameters = textParams;
	}

	protected HTTPSConnection(Context ctx, boolean app, HashMap<String, String> params) {
		this(ctx, app);
		this.parameters = params;

	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a HashMap or an empty HashMap
	public HashMap<String, String> execute(String url) throws MalformedURLException, IOException {
		// we only handle HTTPS connections here !!!
		if (url.toLowerCase(Locale.UK).startsWith(HTTPS)) {
			Log.d("HTTPSConnection", "url: "+url);

			HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
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
			Certificate[] serverCertificates = conn.getServerCertificates();
			String fingerprint = Util.getSHA1Fingerprint(serverCertificates[0]);
			result.put("fingerprint", fingerprint);
			
			Log.d("HTTPSConnection", "header: "+result.get("headers"));
//			cookieHandler.setCookie(result.get("headers"));
			return result;
		}

		return new HashMap<String, String>();
	}

	protected abstract HashMap<String, String> doRequest(HttpsURLConnection conn) throws IOException;

	protected void setREQUEST_METHOD(String rEQUEST_METHOD) {
		REQUEST_METHOD = rEQUEST_METHOD;
	}

}
