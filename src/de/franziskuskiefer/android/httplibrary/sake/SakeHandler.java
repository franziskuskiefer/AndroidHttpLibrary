package de.franziskuskiefer.android.httplibrary.sake;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import de.franziskuskiefer.android.httplibrary.Util;
import de.franziskuskiefer.android.httplibrary.async.HTTPSConnection;
import de.franziskuskiefer.android.httplibrary.sync.HTTPS_POST;

public class SakeHandler extends Thread implements Constants {

//	private Sake sake;
	private String trans;
	private SharedPreferences preferences;
	private String sessionID;
	private Context ctx;
	private HTTPSConnection con;
	private String username;

	@Override
	public void run() {
		super.run();
		con.setAuth(execute());
	}
	
	private boolean execute() {
		this.preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		this.sessionID = preferences.getString(PREF_SESSION_ID, "");
		this.username = preferences.getString(PREF_USER_NAME, "");
		String secret = preferences.getString(PREF_AUTH_KEY, "");
		String successURL = preferences.getString(PREF_SUCCESS_URL, "");
		if (Util.DEV) {
			Log.d("HTTPSConnection", "secret: "+secret);
			Log.d("HTTPSConnection", "successURL: "+successURL);
		}
		
		if (secret != ""){
//			String m1 = setupSoke();
//			HashMap<String, String> serverM = sendSokeM1(m1, ctx);
//			HashMap<String, String> finalMessage = finishSoke(serverM, secret, ctx, successURL);
//			if (finalMessage != null){
//				String body = finalMessage.get("body");
//				if (body != null && sake.verifyServer(body.substring(body.indexOf(";")+1))){
//					return true;
//				}
//			}
		}
		
		return false;
	}

	// initialise new soke protocol and send first message to server
//	private String setupSoke() {
//		this.sake = new Sake();
//		return sake.init();
//	}

	// we started soke and this is the first server message -> create auth tokens and final message
	private HashMap<String, String> finishSoke(HashMap<String, String> s, String secret, Context ctx, String successURL) {
		String cert = s.get("fingerprint");

		// add sent URI parameters to transcript
		this.trans = "&POWClientExchange=" + s.get("Params");

		// get server result
		String jsonString = s.get("Result");

		if (jsonString != null && jsonString != ""){
			// add result to transcript
			this.trans += "&POWServerExchange=" + Uri.encode(jsonString);

//			try {
//				JSONObject json = new JSONObject(jsonString);
//				SakeResult sokeResult = sake.next(json.getString("serverPoint"), secret, trans, cert);
//				String auth = sokeResult.getA1();
//				storeKey(sokeResult.getKey());

//				Log.d("HTTPSConnection", "auth1: "+auth);
//
//				// get session cookie with auth
//				String finalURL = successURL+"?auth1="+auth+"&sessionID="+this.sessionID;
//				Log.d("HTTPSConnection", "finalURL: "+finalURL);
//				return new HTTPS_GET(ctx, true).execute(finalURL);
//			} catch (JSONException e) {
//				e.printStackTrace();
//				Log.d("HTTPSConnection", e.getLocalizedMessage());
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//				Log.d("HTTPSConnection", e.getLocalizedMessage());
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.d("HTTPSConnection", e.getLocalizedMessage());
//			}
		} else {
			Log.i("HTTPSConnection", "Error: Final SOKE message is empty!");
		}
		
		return null;
	}

	private HashMap<String, String> sendSokeM1(String m1, Context ctx) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("msg", "POWClientExchange");
		params.put("version", "POW_v1_tSAKE_2048_SHA256_certHash");
		params.put("sessionID", sessionID);
		params.put("username", username);
		params.put("clientMsg", m1);

		String authURL = preferences.getString(PREF_SAKE_URL, "");
		Log.i("HTTPSConnection", "do request to authURL "+authURL);
		
		try {
			return new HTTPS_POST(ctx, true, params).execute(authURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Log.e("HTTPSConnection", "Error: "+e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("HTTPSConnection", "Error: "+e.getLocalizedMessage());
		}
		
		return null;
	}
	
	private void storeKey(String key) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_AUTH_KEY, key);
		editor.commit();
	}

	public void init(Context ctx, HTTPSConnection con) {
		this.ctx = ctx;
		this.con = con;
	}
}
