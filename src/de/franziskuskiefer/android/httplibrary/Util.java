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
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

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

	public static String listToJsonArray(List<String> in){
		JSONArray list = new JSONArray();
		for (String string : in) {
			list.put(string);
		}
		return list.toString();
	}

	public static JSONObject listToJsonObject(String name, List<String> in){
		try {
			JSONObject o = new JSONObject();
			JSONArray list = new JSONArray();
			for (String string : in) {
				list.put(string);
			}
			o.put(name, list);
			return o;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("HTTPSConnection", "JSONEception ("+Thread.currentThread().getStackTrace()[2].getLineNumber()+") - "+e.getLocalizedMessage());
		}

		return null;
	}

}
