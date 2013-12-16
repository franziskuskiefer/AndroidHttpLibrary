package de.franziskuskiefer.android.httplibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

public class Util {

	// Reads an InputStream and converts it to a String.
	public static String stream2string(InputStream stream) throws IOException, UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));    

		String line, result = "";
		while((line = reader.readLine()) != null){
			result += line;
		}

		return result;
	}
	
	public static String getSHA1Fingerprint(Certificate cert) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return byteArrayToHexString(md.digest(cert.getEncoded()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		return result;

	}
	
	public static String byteArrayToHexString(byte[] in){
		String result = "";
		for (byte b : in) {
			result += String.format("%02x", b);
		}
		return result;
	}

}
