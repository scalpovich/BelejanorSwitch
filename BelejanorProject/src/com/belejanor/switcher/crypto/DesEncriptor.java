package com.belejanor.switcher.crypto;

public class DesEncriptor { 
	DES tdes = new DES();
    @SuppressWarnings("unused")
	private static final int BLOCK_128 = 16;

     public DesEncriptor() {
    	 
    	 super();
     }

    public String encrypt3DES(String str, String key) throws Exception { String enc = null;
	  try {
	    if (key.length() == 32) {
		      byte[] keys = new byte[16];
		      keys = Util.hex2byte(key);
		      byte[] k1 = new byte[8];
		      byte[] k2 = new byte[8];
		      System.arraycopy(keys, 0, k1, 0, 8);
		      tdes.setKey(k1);
		      byte[] encode_1 = tdes.encrypt(Util.hex2byte(str));
		      System.arraycopy(keys, 8, k2, 0, 8);
		      tdes.setKey(k2);
		      byte[] encode_2 = tdes.decrypt(encode_1);
		      tdes.setKey(k1);
		      byte[] encode_3 = tdes.encrypt(encode_2);
		      enc = Util.toHEX1(encode_3);
		      
	    }else if (key.length() == 16) {
	    	
		      byte[] keys = new byte[16];
		      keys = Util.hex2byte(key);
		      byte[] k1 = new byte[8];
		      System.arraycopy(keys, 0, k1, 0, 8);
		      tdes.setKey(k1);
		      byte[] encode_1 = tdes.encrypt(Util.hex2byte(str));
		      byte[] encode_2 = tdes.decrypt(encode_1);
		      tdes.setKey(k1);
		      byte[] encode_3 = tdes.encrypt(encode_2);
		      enc = Util.toHEX1(encode_3);
	    } else if (key.length() == 48) {
	    	
		      byte[] keys = new byte[24];
		      keys = Util.hex2byte(key);
		      byte[] k1 = new byte[8];
		      byte[] k2 = new byte[8];
		      byte[] k3 = new byte[8];
		      System.arraycopy(keys, 0, k1, 0, 8);
		      tdes.setKey(k1);
		      byte[] encode_1 = tdes.encrypt(Util.hex2byte(str));
		      System.arraycopy(keys, 8, k2, 0, 8);
		      tdes.setKey(k2);
		      byte[] encode_2 = tdes.decrypt(encode_1);
		      System.arraycopy(keys, 16, k3, 0, 8);
		      tdes.setKey(k3);
		      byte[] encode_3 = tdes.encrypt(encode_2);
		      enc = Util.toHEX1(encode_3);
		      
	    } else {
	    	
	    	throw new java.security.InvalidKeyException("ERROR: Longitud de la llave incorrecta");
	    }
	  } catch (Exception ex) {
	    
		  throw ex;
	  }
	  return enc;
}

@SuppressWarnings("unused")
private static final int BLOCK_192 = 24;
public String dencrypt3DES(String str, String key) throws Exception { 
	  String denc = "";
	  try {
	    if (key.length() == 32) {
		      byte[] keys = new byte[16];
		      keys = Util.hex2byte(key);
		      byte[] k1 = new byte[8];
		      byte[] k2 = new byte[8];
		      System.arraycopy(keys, 0, k1, 0, 8);
		      tdes.setKey(k1);
		      byte[] decode_3 = tdes.decrypt(Util.hex2byte(str));
		      System.arraycopy(keys, 8, k2, 0, 8);
		      tdes.setKey(k2);
		      byte[] decode_2 = tdes.encrypt(decode_3);
		      tdes.setKey(k1);
		      byte[] decode_1 = tdes.decrypt(decode_2);
		      denc = Util.toHEX1(decode_1);
		      System.out.println("Desencripta TipleDES: " + str + " --> " + denc);
		      
	    } else if (key.length() == 16) {
		      byte[] keys = new byte[16];
		      keys = Util.hex2byte(key);
		      byte[] k1 = new byte[8];
		      System.arraycopy(keys, 0, k1, 0, 8);
		      tdes.setKey(k1);
		      byte[] decode_3 = tdes.decrypt(Util.hex2byte(str));
		      byte[] decode_2 = tdes.encrypt(decode_3);
		      tdes.setKey(k1);
		      byte[] decode_1 = tdes.decrypt(decode_2);
		      denc = Util.toHEX1(decode_1);
		      System.out.println("Desencripta TipleDES: " + str + " --> " + denc);
		      
	    } else if (key.length() == 48) {
	    	
		      byte[] keys = new byte[24];
		      keys = Util.hex2byte(key);
		      byte[] k1 = new byte[8];
		      byte[] k2 = new byte[8];
		      byte[] k3 = new byte[8];
		      System.arraycopy(keys, 16, k3, 0, 8);
		      tdes.setKey(k3);
		      byte[] decode_3 = tdes.decrypt(Util.hex2byte(str));
		      System.arraycopy(keys, 8, k2, 0, 8);
		      tdes.setKey(k2);
		      byte[] decode_2 = tdes.encrypt(decode_3);
		      System.arraycopy(keys, 0, k1, 0, 8);
		      tdes.setKey(k1);
		      byte[] decode_1 = tdes.decrypt(decode_2);
		      denc = Util.toHEX1(decode_1);
		      System.out.println("Desencripta TipleDES: " + str + " --> " + denc);
		      
	    } else {
	    	throw new java.security.InvalidKeyException("ERROR: Longitud de la llave incorrecta");
	    }
	  } catch (Exception ex) {
		  throw ex;
	  }
	  return denc;
	}
}

