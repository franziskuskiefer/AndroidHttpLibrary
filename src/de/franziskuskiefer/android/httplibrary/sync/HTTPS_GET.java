package de.franziskuskiefer.android.httplibrary.sync;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import de.franziskuskiefer.android.httplibrary.Util;

public class HTTPS_GET extends HTTPSConnection {
	
	public HTTPS_GET(Context ctx, boolean app){
		super(ctx, app);
		setREQUEST_METHOD(GET);
	}

	@Override
	protected HashMap<String, String> doRequest(HttpsURLConnection conn) throws IOException {
		InputStream is = null;
		try {
			HashMap<String, String> result = new HashMap<String, String>();
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Map<String, List<String>> headerFields = conn.getHeaderFields();
			is = conn.getInputStream();

			// Convert the InputStream into a string
			String website = Util.stream2string(is);
			result.put("body", website);
			JSONObject headers = new JSONObject();
			for (String key : headerFields.keySet()) {
				try {
					if (key != null)
						headers.put(key, Util.listToJsonArray(headerFields.get(key)));
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("HTTPSConnection", "JSONEception ("+Thread.currentThread().getStackTrace()[2].getLineNumber()+") - "+e.getLocalizedMessage());
				}
			}
			result.put("headers", headers.toString());
			
			return result;
		} finally {
			if (is != null) {
				is.close();
			} 
		}
	}
}
