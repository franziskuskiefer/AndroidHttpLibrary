package de.franziskuskiefer.android.httplibrary;

public class HTTPS_GET extends HTTPSConnection {
	
	public HTTPS_GET(Callback callback, boolean app){
		super(callback, app);
		setREQUEST_METHOD(GET);
	}

}
