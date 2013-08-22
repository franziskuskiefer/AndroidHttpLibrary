package de.franziskuskiefer.android.httplibrary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class HttpPost extends AsyncTask<String, Void, String> {

	private Callback caller;
	private HashMap<String, String> parameters;

	public HttpPost(Callback callback, HashMap<String, String> params) {
		this.caller = callback;
		this.parameters = params;
	}

	@Override
	protected String doInBackground(String... urls) {

		// params comes from the execute() call: params[0] is the url.
		try {
			return postRequest(urls[0]);
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
	private String postRequest(String url) throws IOException {
		HttpClient client = new DefaultHttpClient();
		org.apache.http.client.methods.HttpPost post = new org.apache.http.client.methods.HttpPost(url);

	    try {
	      List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	      
	      // add parameter list
	      Iterator<Entry<String, String>> it = this.parameters.entrySet().iterator();
	      while (it.hasNext()) {
	    	  Map.Entry<String, String> val = it.next();
	    	  postParameters.add(new BasicNameValuePair(val.getKey(), val.getValue()));
	      }
	      
	      post.setEntity(new UrlEncodedFormEntity(postParameters));
	      HttpResponse response = client.execute(post);
	      String result = Util.stream2string(response.getEntity().getContent());
	      
	      System.out.println("Post Result: "+result);
	      
	      return result;
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    
		return null;
	}

}