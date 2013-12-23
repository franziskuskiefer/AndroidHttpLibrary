package de.franziskuskiefer.android.httplibrary;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class CookieHandler {

	private String cookie = "";
	private SharedPreferences preferences;

	public CookieHandler(Context ctx){
		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		cookie = preferences.getString("cookie", "");
//		Log.d("HTTPSConnection", "cookie(constructor): "+cookie);
	}
	
	public void setCookie(String headers){
//		Log.d("HTTPSConnection", "cookie(pre-setCookie): "+cookie);
		try {
			JSONObject header = new JSONObject(headers);
			String cookieHeaderName = getCookieName(header);
			if (cookieHeaderName != null && cookieHeaderName != ""){
				String cookies = header.getString(cookieHeaderName);
				String cookieName = "userhandler=";
				this.cookie = cookieName+cookies.substring(cookies.indexOf(cookieName)+cookieName.length(), cookies.indexOf(";"));
				storeCookie();
//				Log.d("HTTPSConnection", "cookie(post-setCookie): "+cookie);
			} else {
				Log.d("HTTPSConnection", "no cookie set");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("HTTPSConnection", "ERROR:"+e.getLocalizedMessage());
		}
	}
	
	private String getCookieName(JSONObject header){
		return header.has("set-cookie") ? "set-cookie" : (header.has("Set-Cookie") ? "Set-Cookie" : "");
	}
	
	public String getCookie() {
//		Log.d("HTTPSConnection", "cookie(getCookie): "+cookie);
		return cookie;
	}

	private void storeCookie(){
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("cookie", cookie);
		editor.commit();
	}
	
	private void deleteCookie(){
		// remove cookie
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove("cookie");
		editor.commit();
	}
}
