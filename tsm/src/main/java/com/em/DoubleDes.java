package com.em;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DoubleDes {

	private static final String Algorithm = "DESede/ECB/NoPadding"; //定义 加密算法,可用 DES,DESede,Blowfish
	private static final String Algorithm1 = "DESede"; //定义 加密算法,可用 DES,DESede,Blowfish
	private static final String AlgorithmSingle = "DES/ECB/NoPadding"; //定义 加密算法,可用 DES,DESede,Blowfish
	private static final String AlgorithmSingle1 = "DES"; //定义 加密算法,可用 DES,DESede,Blowfish
	public static byte[] encryptModeSingle(byte[] keyByte) {
		try {
			String szSrc ="00000000000000000000000000000000";
			Cipher c1 = Cipher.getInstance(AlgorithmSingle);
			SecretKey deskey = new SecretKeySpec(keyByte, AlgorithmSingle1);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(szSrc.getBytes());
		}catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		}catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	public static byte[] encryptMode(byte[] keyByte) {
		try {
			String szSrc ="00000000000000000000000000000000";
			Cipher c1 = Cipher.getInstance(Algorithm);
			SecretKey deskey = new SecretKeySpec(keyByte, Algorithm1);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(AscToBcd(szSrc));
		}catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		}catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	public static byte[] decryptModeSingle(byte[] keyByte, byte[] src) {
		try {
			Cipher c1 = Cipher.getInstance(AlgorithmSingle);
			SecretKey deskey = new SecretKeySpec(keyByte, AlgorithmSingle1);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		}catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		}catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	public static byte[] decryptMode(byte[] keyByte, byte[] src) {
		try {
			Cipher c1 = Cipher.getInstance(Algorithm);
			SecretKey deskey = new SecretKeySpec(keyByte, Algorithm1);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		}catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		}catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	public static String byte2hex(byte[] b) {
		String hs="";
		String stmp="";

		for (int n=0;n<b.length;n++)
		{
			stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length()==1)
			{
				hs=hs+"0"+stmp;
			}else{
				hs=hs+stmp;
			}
			if (n<b.length-1)
			{
				hs=hs+"";
			}
		}
		return hs.toUpperCase();
	}
		public static byte[] AscToBcd(String source)
		{

		    if ( source==null)
		       return null;
		    int  len = source.length();
		    len =  len/2;
		    byte[]  dest = new byte[len];

		    for(int i=0;i<len;i++)
		    {
		       char  c1 =  source.charAt(i*2);
		       char  c2 =  source.charAt(i*2+1);
		       byte  b1,b2;
		       if ( (c1 >= '0') && ( c1 <='9' ) )
		           b1 = (byte)(c1 - '0');
		       else if( ( c1>='a') && ( c1<= 'z') )
		           b1 = (byte)(c1 - 'a' +0x0a );
		       else
		           b1 = (byte)(c1 - 'A' +0x0a );

		       if( (c2 >= '0') && (c2 <='9') )
		            b2 = (byte)(c2 - '0');
		        else if( (c2>='a') && (c2<='z') )
		            b2 = (byte)(c2 - 'a' +0x0a);
		        else
		            b2 = (byte)(c2 - 'A' +0x0a);

		        dest[i] = (byte)( (b1<<4)|b2 );
		    }
		    return dest;
		}
}
