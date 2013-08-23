package de.franziskuskiefer.android.httplibrary;

import java.util.HashMap;

public interface Callback {

	void finished(String s);
	void finished(HashMap<String, String> s);
	
}
