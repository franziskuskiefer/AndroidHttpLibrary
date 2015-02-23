package de.franziskuskiefer.android.httplibrary.async;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.http.conn.ssl.StrictHostnameVerifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import de.franziskuskiefer.android.httplibrary.Util;

public class MyHostnameVerifier implements HostnameVerifier {

	private StrictHostnameVerifier verifier = new StrictHostnameVerifier();
	private SharedPreferences preferences;
	
	public MyHostnameVerifier(Context ctx) {
//		preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	@Override
	public boolean verify(String arg0, SSLSession arg1) {
		// FIXME: ignore everything here for now!
		
//		Log.d("HTTPSConnection", "SSL Old Session ID: "+getTlsId());
//		Log.d("HTTPSConnection", "SSL Session ID: "+Base64.encodeToString(arg1.getId(), Base64.DEFAULT));
//		
//		String tlsId = getTlsId();
//		boolean correctHostname = true;
//		if (tlsId == ""){
//			Log.d("HTTPSConnection", "First Run ! -> Set sslID");
//			setTlsId(arg1.getId());
//		} else if (!tlsId.equals(Base64.encodeToString(arg1.getId(), Base64.DEFAULT))) {
//			Log.d("HTTPSConnection", "TLS Session changed!");
//			// have to run sake to rule out MitM attacks
//			correctHostname = false;
//			setTlsId(arg1.getId());
//		}

		if (Util.DEV)
			Log.d("HTTPSConnection", "Hostname verification: "+verifier.verify(arg0, arg1));
		return verifier.verify(arg0, arg1);

//		Log.d("HTTPSConnection", "SSL Old Session ID: "+Base64.encodeToString(arg1.getId(), Base64.DEFAULT));
		
//		return correctHostname;
	}
	
	private String getTlsId(){
		return preferences.getString("tlsid", "");
	}
	
	private void setTlsId(byte[] tlsId){
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("tlsid", Base64.encodeToString(tlsId, Base64.DEFAULT));
		editor.commit();
	}

}
