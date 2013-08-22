package de.franziskuskiefer.android.httplibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class HttpGet extends AsyncTask<String, Void, String> {

	private Callback caller;

	public HttpGet(Callback callback) {
		this.caller = callback;
	}

	@Override
	protected String doInBackground(String... urls) {

		// params comes from the execute() call: params[0] is the url.
		try {
			return getRequest(urls[0]);
		} catch (IOException e) {
			return "Unable to retrieve web page. URL may be invalid.";
		}
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		this.caller.finished(result);
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String getRequest(String myurl) throws IOException {
		InputStream is = null;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Log.d("CONNECTOR_DEBUG", "The response is: " + response);
			is = conn.getInputStream();

			// Convert the InputStream into a string
			String contentAsString = stream2string(is);
			return contentAsString;

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}
	
	// Reads an InputStream and converts it to a String.
	private String stream2string(InputStream stream) throws IOException, UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));    
		
		String line, result = "";
        while((line = reader.readLine()) != null){
            result += line;
        }
        
		return result;
	}

}
