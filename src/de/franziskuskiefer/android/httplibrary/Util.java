package de.franziskuskiefer.android.httplibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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

}
