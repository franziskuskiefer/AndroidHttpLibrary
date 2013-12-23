package de.franziskuskiefer.android.httplibrary.old;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;
import de.franziskuskiefer.android.httplibrary.Callback;
import de.franziskuskiefer.android.httplibrary.Util;

public class HttpGet extends AsyncTask<String, Void, HashMap<String, String>> {

	private static final String REQUEST_METHOD = "GET";
	private static final int CONNECT_TIMEOUT = 15000;
	private static final int READ_TIMEOUT = 10000;
	private static final String HTTPS = "https";
	private Callback caller;

	public HttpGet(Callback callback) {
		this.caller = callback;
	}

	@Override
	protected HashMap<String, String> doInBackground(String... urls) {
		// urls come from the execute() call
		try {
			return getRequest(urls[0]);
		} catch (IOException e) {
			HashMap<String, String> result = new HashMap<String, String>();
			result.put("Exception", "Unable to retrieve web page. URL may be invalid.");
			return result;
		}
	}

	// onPostExecute invoke caller's finished
	@Override
	protected void onPostExecute(HashMap<String, String> result) {
		this.caller.finished(result);
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a string
	private HashMap<String, String> getRequest(String myurl) throws IOException {
		InputStream is = null;

		try {
			HashMap<String, String> result = new HashMap<String, String>();
			
			HttpURLConnection conn = (HttpURLConnection) new URL(myurl).openConnection();
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setRequestMethod(REQUEST_METHOD);
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			if (myurl.toLowerCase(Locale.UK).startsWith(HTTPS)) {
				Certificate[] serverCertificates = ((HttpsURLConnection)conn).getServerCertificates();
				String fingerprint = Util.getSHA1Fingerprint(serverCertificates[0]);
				result.put("Fingerprint", fingerprint);
			}
			int response = conn.getResponseCode();
			is = conn.getInputStream();

			// Convert the InputStream into a string
			String website = Util.stream2string(is);
			result.put("Result", website);
			return result;

		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}
	
}
