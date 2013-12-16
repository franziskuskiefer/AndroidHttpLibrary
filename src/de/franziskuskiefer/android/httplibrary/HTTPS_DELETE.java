package de.franziskuskiefer.android.httplibrary;

public class HTTPS_DELETE extends HTTPSConnection {

	public HTTPS_DELETE(Callback callback, boolean app){
		super(callback, app);
		setREQUEST_METHOD(DELETE);
	}

}
