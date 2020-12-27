package com.belejanor.switcher.crypto;

import java.util.Arrays;

public class DES
{
  public static int traceLevel = 0;
  public static String traceInfo = "";
  public static final int ROUNDS = 8;
  public static final int BLOCK_SIZE = 8;
  public static final int KEY_LENGTH = 8;
  public static final int NUM_SUBKEYS = 16;
  public byte[][] subkeys = new byte[16][8];
  
  public static final byte[] IP = {
    58, 50, 42, 34, 26, 18, 10, 2, 
    60, 52, 44, 36, 28, 20, 12, 4, 
    62, 54, 46, 38, 30, 22, 14, 6, 
    64, 56, 48, 40, 32, 24, 16, 8, 
    57, 49, 41, 33, 25, 17, 9, 1, 
    59, 51, 43, 35, 27, 19, 11, 3, 
    61, 53, 45, 37, 29, 21, 13, 5, 
    63, 55, 47, 39, 31, 23, 15, 7 };
  
  public static final byte[] FP = {
    40, 8, 48, 16, 56, 24, 64, 32, 
    39, 7, 47, 15, 55, 23, 63, 31, 
    38, 6, 46, 14, 54, 22, 62, 30, 
    37, 5, 45, 13, 53, 21, 61, 29, 
    36, 4, 44, 12, 52, 20, 60, 28, 
    35, 3, 43, 11, 51, 19, 59, 27, 
    34, 2, 42, 10, 50, 18, 58, 26, 
    33, 1, 41, 9, 49, 17, 57, 25 };
  





  public static final byte[] PC1 = {
    57, 49, 41, 33, 25, 17, 9, 
    1, 58, 50, 42, 34, 26, 18, 
    10, 2, 59, 51, 43, 35, 27, 
    19, 11, 3, 60, 52, 44, 36, 
    8, 16, 24, 32, 
    63, 55, 47, 39, 31, 23, 15, 
    7, 62, 54, 46, 38, 30, 22, 
    14, 6, 61, 53, 45, 37, 29, 
    21, 13, 5, 28, 20, 12, 4, 
    40, 48, 56, 64 };
  



  public static final byte[] keyrot = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };
  

  public static final byte[] PC2 = {
    14, 17, 11, 24, 1, 5, 
    3, 28, 15, 6, 21, 10, 
    23, 19, 12, 4, 26, 8, 
    16, 7, 27, 20, 13, 2, 
    41, 52, 31, 37, 47, 55, 
    30, 40, 51, 45, 33, 48, 
    44, 49, 39, 56, 34, 53, 
    46, 42, 50, 36, 29, 32 };
  

  public static final byte[] E = {
    32, 1, 2, 3, 4, 5, 
    4, 5, 6, 7, 8, 9, 
    8, 9, 10, 11, 12, 13, 
    12, 13, 14, 15, 16, 17, 
    16, 17, 18, 19, 20, 21, 
    20, 21, 22, 23, 24, 25, 
    24, 25, 26, 27, 28, 29, 
    28, 29, 30, 31, 32, 1 };
  





  public static final byte[][] Sbox = {
  
    { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 
    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 
    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 
    0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }, 
    
    { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 
    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 
    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 
    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }, 
    
    { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 
    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 
    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 
    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }, 
    
    { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 
    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 
    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 
    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }, 
    
    { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 
    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 
    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 
    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 }, 
    
    { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 
    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 
    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 
    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }, 
    
    { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 
    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 
    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 
    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }, 
    
    { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 
    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 
    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 
    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };
  


  public static final byte[] P = {
    16, 7, 20, 21, 
    29, 12, 28, 17, 
    1, 15, 23, 26, 
    5, 18, 31, 10, 
    2, 8, 24, 14, 
    32, 27, 3, 9, 
    19, 13, 30, 6, 
    22, 11, 4, 25 };
  








  public DES() {}
  







  public byte[] encrypt(byte[] plain)
  {
    if ((plain == null) || (plain.length != 8)) {
      return plain;
    }
    if (traceLevel > 0) traceInfo = ("encryptDES(" + Util.toHEX1(plain) + ")");
    int[] data = Util.byte2int(plain);
    
    data = perm64(data, IP);
    if (traceLevel > 1)
    {
      traceInfo = (traceInfo + "\n  IP:\tL0=" + Util.toHEX1(data[0]) + ", R0=" + Util.toHEX1(data[1]));
    }
    for (int i = 0; i < 16; i++) {
      if (traceLevel > 1)
      {

        traceInfo = (traceInfo + "\n  Rnd" + (i + 1) + "\tf(R" + i + "=" + Util.toHEX1(data[1]) + ", SK" + (i + 1) + "=" + Util.toHEX(subkeys[i]) + ") "); }
      int t = f(data[1], subkeys[i]);
      if (traceLevel > 1) traceInfo = (traceInfo + "= " + Util.toHEX1(t));
      t = data[0] ^ t;
      data[0] = data[1];
      data[1] = t;
    }
    int t = data[0];data[0] = data[1];data[1] = t;
    data = perm64(data, FP);
    if (traceLevel > 1)
    {
      traceInfo = (traceInfo + "\n  FP:\tL=" + Util.toHEX1(data[0]) + ", R=" + Util.toHEX1(data[1]));
    }
    byte[] res = Util.int2byte(data);
    if (traceLevel > 1) traceInfo += "\n";
    if (traceLevel > 0) traceInfo = (traceInfo + " returns " + Util.toHEX1(res) + "\n");
    return res;
  }
  
  public byte[] decrypt(byte[] cipher)
  {
    if ((cipher == null) || (cipher.length != 8)) {
      return cipher;
    }
    if (traceLevel > 0) traceInfo = ("decryptDES(" + Util.toHEX1(cipher) + ")");
    int[] data = Util.byte2int(cipher);
    
    data = perm64(data, IP);
    if (traceLevel > 1)
    {
      traceInfo = (traceInfo + "\n  IP:\tL0=" + Util.toHEX1(data[0]) + ", R0=" + Util.toHEX1(data[1]));
    }
    for (int i = 0; i < 16; i++) {
      if (traceLevel > 1)
      {

        traceInfo = (traceInfo + "\n  Rnd" + (i + 1) + "\tf(R" + i + "=" + Util.toHEX1(data[1]) + ", SK" + (16 - i) + "=" + Util.toHEX(subkeys[(15 - i)]) + ") "); }
      int t = f(data[1], subkeys[(15 - i)]);
      if (traceLevel > 1) traceInfo = (traceInfo + "= " + Util.toHEX1(t));
      t = data[0] ^ t;
      data[0] = data[1];
      data[1] = t;
    }
    int t = data[0];data[0] = data[1];data[1] = t;
    data = perm64(data, FP);
    if (traceLevel > 1)
    {
      traceInfo = (traceInfo + "\n  FP:\tL=" + Util.toHEX1(data[0]) + ", R=" + Util.toHEX1(data[1]));
    }
    byte[] res = Util.int2byte(data);
    if (traceLevel > 1) traceInfo += "\n";
    if (traceLevel > 0) traceInfo = (traceInfo + " returns " + Util.toHEX1(res) + "\n");
    return res;
  }
  
@SuppressWarnings("unused")
public void setKey(byte[] key)
  {
    int MASK28 = -16;
    
    if ((key == null) || (key.length != 8)) {
      return;
    }
    
    if (traceLevel > 0) { traceInfo = ("setKey(" + Util.toHEX1(key) + ")\n");
    }
    int[] cd = Util.byte2int(key);
    
    cd = perm64(cd, PC1);
    cd[0] &= 0xFFFFFFF0;
    cd[1] &= 0xFFFFFFF0;
    if (traceLevel > 3) { traceInfo = (traceInfo + "  PC1(Key)=" + Util.toHEX(cd) + "\n");
    }
    for (int i = 0; i < 16; i++) {
      cd[0] = rotl28(cd[0], keyrot[i]);
      cd[1] = rotl28(cd[1], keyrot[i]);
      keyperm(cd, i);
      

      if (traceLevel > 3)
      {
        traceInfo = (traceInfo + "  KeyRnd" + (i + 1) + "\tCD=" + Util.toHEX(cd) + "\tPC2(CD)=" + Util.toHEX(subkeys[i]) + "\n");
      }
    }
  }
  





























  public int f(int R, byte[] SK)
  {
    int b = 0;
    int out = 0;
    
    int[] e = new int[8];
    byte[] s = new byte[8];
    

    byte[] a = expand(R);
    
    for (int j = 0; j < 8; j++) {
      int rc = (a[j] ^ SK[j]) & 0xFF;
      e[j] = rc;
      rc = rc & 0x20 | 
        rc << 4 & 0x10 | 
        rc >>> 1 & 0xF;
      s[j] = Sbox[j][rc];
      b = b << 4 | s[j] & 0x3F;
    }
    
    out = perm32(b, P);
    
    if (traceLevel > 2) {
      traceInfo = (traceInfo + "\n\tE=" + Util.toHEX(a) + "  S=" + Util.toHEX(s) + "  P");
    }
    return out;
  }
  









  public void keyperm(int[] cd, int i)
  {
    byte KEYBIT1 = 32;
    
    int p = 0;
    

    for (int j = 0; j < 8; j++) {
      subkeys[i][j] = 0;
      byte mask = KEYBIT1;
      for (int k = 0; k < 6; k++) {
        if (keybit(cd, PC2[(p++)]) == 1) {
          int tmp57_55 = j; byte[] tmp57_54 = subkeys[i];tmp57_54[tmp57_55] = ((byte)(tmp57_54[tmp57_55] | mask)); }
        mask = (byte)(mask >>> 1);
      }
    }
  }
  









  public int keybit(int[] cd, int pos)
  {
    int d = (pos - 1) / 28;
    int o = 31 - (pos - 1) % 28;
    int b = cd[d] >>> o & 0x1;
    return b;
  }
  





  public int rotl28(int b, int n)
  {
    @SuppressWarnings("unused")
	int MASK28 = -16;
    return (b << n | b >>> 28 - n) & 0xFFFFFFF0;
  }
  
  public byte[] expand(int R)
  {
    @SuppressWarnings("unused")
	int MASK6 = 63;
    byte[] a = new byte[8];
    
    int t = R << 5 | R >>> 27;a[0] = ((byte)(t & 0x3F));
    t = R >>> 23;a[1] = ((byte)(t & 0x3F));
    t = R >>> 19;a[2] = ((byte)(t & 0x3F));
    t = R >>> 15;a[3] = ((byte)(t & 0x3F));
    t = R >>> 11;a[4] = ((byte)(t & 0x3F));
    t = R >>> 7;a[5] = ((byte)(t & 0x3F));
    t = R >>> 3;a[6] = ((byte)(t & 0x3F));
    t = R << 1 | R >>> 31;a[7] = ((byte)(t & 0x3F));
    return a;
  }


@SuppressWarnings("unused")
public int perm32(int in, byte[] perm)
  {
    int DESBIT1 = Integer.MIN_VALUE;
    int mask = Integer.MIN_VALUE;
    int out = 0;
    
    int p = 0;
    
    for (int i = 0; i < 32; i++) {
      int off = 31 - (perm[(p++)] - 1);
      if ((in >>> off & 0x1) == 1)
        out |= mask;
      mask >>>= 1;
    }
    return out;
  }
  
  public int[] perm64(int[] in, byte[] perm)
  {
    @SuppressWarnings("unused")
	int DESBIT1 = Integer.MIN_VALUE;
    int mask = Integer.MIN_VALUE;
    int[] out = new int[2];
    int p = 0;
    

    out[0] = 0;
    for (int i = 0; i < 32; i++) {
      if (bit(in, perm[(p++)]) == 1)
        out[0] |= mask;
      mask >>>= 1;
    }
    

    out[1] = 0;
    mask = Integer.MIN_VALUE;
    for (int i = 0; i < 32; i++) {
      if (bit(in, perm[(p++)]) == 1)
        out[1] |= mask;
      mask >>>= 1;
    }
    
    return out;
  }
  

  public int bit(int[] n, int pos)
  {
    pos--;
    int d = pos / 32;
    int o = 31 - pos % 32;
    int b = n[d] >>> o & 0x1;
    return b;
  }
  
  public static void self_test(String hkey, String hplain, String hcipher, int lev)
  {
	    byte[] key = Util.hex2byte(hkey);
	    byte[] plain = Util.hex2byte(hplain);
	    byte[] cipher = Util.hex2byte(hcipher);
	    
	    System.out.println();
	    
	    DES testDES = new DES();
	    traceLevel = lev;
	    testDES.setKey(key);
	    System.out.print(traceInfo);
	    
	    byte[] result = testDES.encrypt(plain);
	    System.out.print(traceInfo);
	    if (Arrays.equals(result, cipher)) {
	      System.out.print("Test OK\n");
	    } else {
	      System.out.print("Test Failed. Result was " + Util.toHEX(result) + "\n");
	    }
	    result = testDES.decrypt(cipher);
	    System.out.print(traceInfo);
	    if (Arrays.equals(result, plain)) {
	      System.out.print("Test OK\n");
	    } else {
	      System.out.print("Test Failed. Result was " + Util.toHEX(result) + "\n");
	    }
  }
  
  public static void main(String[] args)
  {
    int lev = 2;
    

    switch (args.length) {
    case 0: 
      break; case 1:  lev = Integer.parseInt(args[0]);
      break;
    case 3:  self_test(args[0], args[1], args[2], lev);
      System.exit(0);
      break;
    case 4:  lev = Integer.parseInt(args[3]);
      self_test(args[0], args[1], args[2], lev);
      System.exit(0);
      break;
    case 2: default:  System.out.println("Usage: DES [lev | key plain cipher {lev}]\n");
      System.exit(1);
    }
    
    
    self_test("5B5A57676A56676E", "675A69675E5A6B5A", 
      "974AFFBF86022D1F", lev);
  }
}
