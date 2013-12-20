package de.franziskuskiefer.android.httplibrary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class HTTPS_POST extends HTTPSConnection {

	public HTTPS_POST(Callback callback, Context ctx, boolean app){
		super(callback, ctx, app);
		setMethod();
	}
	
	public HTTPS_POST(Callback callback, Context ctx, boolean app, String textParams){
		super(callback, ctx, app, textParams);
		setMethod();
	}
	
	public HTTPS_POST(Callback callback, Context ctx, boolean app, HashMap<String, String> params){
		super(callback, ctx, app, params);
		setMethod();
	}
	
	private void setMethod(){
		setREQUEST_METHOD(POST);
	}
	
	protected HashMap<String, String> doRequest(HttpsURLConnection conn) throws IOException {
		HashMap<String, String> result = new HashMap<String, String>();
		
		conn.setDoOutput(true);
		
		/* XXX: have to use text/plain to interact with todo api */
		String encodedParams;
		if (this.textParameters != null){
			conn.setRequestProperty("Content-Type", "text/plain"); 
			conn.setRequestProperty("charset", "utf-8");
			
			encodedParams=this.textParameters;
		} else {
			// add parameter list
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> it = this.parameters.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> val = it.next();
				postParameters.add(new BasicNameValuePair(val.getKey(), val.getValue()));
			}
			
			encodedParams = Util.stream2string(new UrlEncodedFormEntity(postParameters).getContent());
		}

		OutputStream os = conn.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		Log.d("CONNECTOR_DEBUG", "encodedParams: " + Uri.encode(encodedParams));
		writer.write(encodedParams);
		writer.close();
		os.close();
		
		/* response */
		int response = conn.getResponseCode();
		Log.d("CONNECTOR_DEBUG", "The response is: " + response);
		
		Map<String, List<String>> headerFields = conn.getHeaderFields();
		JSONObject headers = new JSONObject();
		for (String key : headerFields.keySet()) {
			try {
				if (key != null)
					headers.put(key, Util.listToJsonArray(headerFields.get(key)));
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("POWDEMO", "JSONEception ("+Thread.currentThread().getStackTrace()[2].getLineNumber()+") - "+e.getLocalizedMessage());
			}
		}
		result.put("headers", headers.toString());
		
		InputStream is = conn.getInputStream();

		// Convert the InputStream into a string
		String website = Util.stream2string(is);
		result.put("Result", website);
		result.put("Params", Uri.encode(encodedParams));

		return result;
	}

}