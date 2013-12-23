package de.franziskuskiefer.android.httplibrary.sake;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECPoint;

import android.util.Log;
import de.franziskuskiefer.android.httplibrary.Util;

public class Sake {
	
	private final static BigInteger Mx = new BigInteger("8da36f68628a18107650b306f22b41448cb60fe5712dd57a", 16);
	private final static BigInteger My = new BigInteger("1f64a649852124528a09455de6aad151b4c0a9a8c2e8269c", 16);
	
	private final static X9ECParameters secp129r1 = NISTNamedCurves.getByName("P-192");
	private final static ECPoint M = secp129r1.getCurve().createPoint(Mx, My, false);
	
	// public key
	private ECPoint X;
	// secret key
	private BigInteger x;
	// second auth token to verify server
	private String a2;
	
	public Sake() {
		// TODO Auto-generated constructor stub
	}
	
	private byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public SakeResult next(String YString, String secret, String trans, String certHash) {
		
		// Y
		// XXX: we only accept affine X strings for now
		ECPoint Y = secp129r1.getCurve().decodePoint(hexStringToByteArray(YString));
		
		// hash password
		String h = getCommonSecret(secret);
		Log.d("HTTPSConnection", "hashed secret: "+h);
		BigInteger hNum = new BigInteger(h, 16).mod(secp129r1.getN());
		
		// Y <- Y - hM
		Y = Y.add(M.multiply(hNum).negate());
		
		// Z <- xY
		ECPoint key = Y.multiply(this.x);
		
		String keyString = Util.byteArrayToHexString(key.getEncoded());
		String auth1 = hashedKey(certHash, Util.byteArrayToHexString(key.getEncoded()), trans, secret);
		
		Log.d("HTTPSSession", "trans: "+trans);
		Log.d("HTTPSSession", "auth1: "+auth1);
		Log.d("HTTPSSession", "keyString: "+keyString);
		
		return new SakeResult(auth1 , keyString);
	}

	public String init(){

		SecureRandom secureRandom = new SecureRandom();
		do {
			x = new BigInteger(secp129r1.getN().bitLength(), secureRandom);
		} while (x.equals(BigInteger.ZERO) || x.compareTo(secp129r1.getN()) >= 0);
		X = secp129r1.getG().multiply(x);

		return "04"+X.getX().toBigInteger().toString(16)+X.getY().toBigInteger().toString(16);
	}
	
	public boolean verifyServer(String serverA2){
		return serverA2 != null && this.a2 != null && serverA2.equals(this.a2);
	}
	
	private String getCommonSecret(final String secret) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
//			ECPoint P = secp129r1.getCurve().decodePoint(hexStringToByteArray(secret));
//			byte[] digest = md.digest(P.getX().toBigInteger().toByteArray());
			byte[] digest = md.digest(secret.getBytes("UTF-8"));
			
			return Util.byteArrayToHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Log.d("SAKE", "Error: "+e.getLocalizedMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.d("SAKE", "Encoding Error: "+e.getLocalizedMessage());
		}
		
		return null;
	}
	
	private String hashedKey(String certHash, String sharedSecret, String trans, String pwdHash){
		MessageDigest md;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("POWServerHello=");
			sb.append(trans);
			Log.d("HTTPSConnection", "trans: "+trans);
			sb.append("&passHash=");
			sb.append(pwdHash);
			Log.d("HTTPSConnection", "pwdHash: "+pwdHash);
			sb.append("&certHash=");
			sb.append(certHash);
			Log.d("HTTPSConnection", "certHash: "+certHash);
			sb.append("&sharedSecret=");
			sb.append(sharedSecret);
			Log.d("HTTPSConnection", "sharedSecret: "+sharedSecret);
			
			md = MessageDigest.getInstance("SHA-256");
			md.update(sb.toString().getBytes());
			byte[] digest = md.digest();
			
			md.update(Util.byteArrayToHexString(digest).getBytes());
			md.update("&auth1".getBytes());
			byte[] a1 = md.digest();
			
			// compute second auth token and store it to for later use
			md.update(Util.byteArrayToHexString(digest).getBytes());
			md.update("&auth2".getBytes());
			this.a2 = Util.byteArrayToHexString(md.digest());
			
			return Util.byteArrayToHexString(a1);
		} catch (NoSuchAlgorithmException e) {
			Log.d("POW", e.getLocalizedMessage());
			e.printStackTrace();
		}

		return null;
	}

	public class SakeResult {

		String a1 = "";
		String key = "";

		public SakeResult(String a1, String key) {
			this.a1 = a1;
			this.key = key;
		}

		public String getA1() {
			return a1;
		}

		public String getKey() {
			return key;
		}

	}
}
