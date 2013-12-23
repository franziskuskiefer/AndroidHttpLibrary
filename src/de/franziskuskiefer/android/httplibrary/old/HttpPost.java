package de.franziskuskiefer.android.httplibrary.old;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import de.franziskuskiefer.android.httplibrary.Callback;
import de.franziskuskiefer.android.httplibrary.Util;

public class HttpPost extends AsyncTask<String, Void, HashMap<String, String>> {

	private Callback caller;
	private HashMap<String, String> parameters;

	public HttpPost(Callback callback, HashMap<String, String> params) {
		this.caller = callback;
		this.parameters = params;
	}

	@Override
	protected HashMap<String, String> doInBackground(String... urls) {
		Log.d("POW", "url(post): "+urls[0]);
		// urls come from the execute() call
		try {
			return postRequest(urls[0]);
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

	private HashMap<String, String> postRequest(String url) throws IOException {
		
		try {
			HashMap<String, String> result = new HashMap<String, String>();
			
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			// add parameter list
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> it = this.parameters.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> val = it.next();
				postParameters.add(new BasicNameValuePair(val.getKey(), val.getValue()));
			}

			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			String encodedParams = Util.stream2string(new UrlEncodedFormEntity(postParameters).getContent());
			Log.d("CONNECTOR_DEBUG", "encodedParams: " + Uri.encode(encodedParams));
			writer.write(encodedParams);
			writer.close();
			os.close();
			
			int response = conn.getResponseCode();
			Log.d("CONNECTOR_DEBUG", "The response is: " + response);
			InputStream is = conn.getInputStream();

			// Convert the InputStream into a string
			String website = Util.stream2string(is);
			System.out.println("Post Result: "+website);
			result.put("Result", website);
			result.put("Params", Uri.encode(encodedParams));

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
}