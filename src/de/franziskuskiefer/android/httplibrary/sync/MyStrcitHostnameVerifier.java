package de.franziskuskiefer.android.httplibrary.sync;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.http.conn.ssl.StrictHostnameVerifier;

public class MyStrcitHostnameVerifier implements HostnameVerifier {

	private StrictHostnameVerifier verifier = new StrictHostnameVerifier();
	
	@Override
	public boolean verify(String arg0, SSLSession arg1) {
		boolean correctHostname = verifier.verify(arg0, arg1);
		return correctHostname;
	}
	
}
