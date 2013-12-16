package de.franziskuskiefer.android.httplibrary;

import java.util.HashMap;

public class HTTPS_POST extends HTTPSConnection {

	public HTTPS_POST(Callback callback, boolean app){
		super(callback, app);
		setMethod();
	}
	
	public HTTPS_POST(Callback callback, boolean app, String textParams){
		super(callback, app, textParams);
		setMethod();
	}
	
	public HTTPS_POST(Callback callback, boolean app, HashMap<String, String> params){
		super(callback, app, params);
		setMethod();
	}
	
	private void setMethod(){
		setREQUEST_METHOD(POST);
	}

}