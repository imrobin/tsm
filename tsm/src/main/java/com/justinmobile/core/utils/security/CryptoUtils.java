package com.justinmobile.core.utils.security;

import static com.justinmobile.core.utils.ByteUtils.append;
import static com.justinmobile.core.utils.ByteUtils.arrayXOR;
import static com.justinmobile.core.utils.ByteUtils.bitComplement;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.subArray;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.encode.EncodeUtils;

/**
 * 支持HMAC-SHA1消息签名 及 DES/AES对称加密的工具类.
 * 
 * 支持Hex与Base64两种编码方式.
 * 
 * @author peak
 */
public class CryptoUtils {

	private static final String DES = "DES";
	private static final String AES = "AES";
	private static final String HMACSHA1 = "HmacSHA1";

	private static final int DEFAULT_HMACSHA1_KEYSIZE = 160;// RFC2401
	private static final int DEFAULT_AES_KEYSIZE = 128;

	// -- HMAC-SHA1 funciton --//
	/**
	 * 使用HMAC-SHA1进行消息签名, 返回字节数组,长度为20字节.
	 * 
	 * @param input
	 *            原始输入字符串
	 * @param keyBytes
	 *            HMAC-SHA1密钥
	 */
	public static byte[] hmacSha1(String input, byte[] keyBytes) {
		try {
			SecretKey secretKey = new SecretKeySpec(keyBytes, HMACSHA1);
			Mac mac = Mac.getInstance(HMACSHA1);
			mac.init(secretKey);
			return mac.doFinal(input.getBytes());
		} catch (GeneralSecurityException e) {
			throw convertRuntimeException(e);
		}
	}

	/**
	 * 使用HMAC-SHA1进行消息签名, 返回Hex编码的结果,长度为40字符.
	 * 
	 * @see #hmacSha1(String, byte[])
	 */
	public static String hmacSha1ToHex(String input, byte[] keyBytes) {
		byte[] macResult = hmacSha1(input, keyBytes);
		return EncodeUtils.hexEncode(macResult);
	}

	/**
	 * 使用HMAC-SHA1进行消息签名, 返回Base64编码的结果.
	 * 
	 * @see #hmacSha1(String, byte[])
	 */
	public static String hmacSha1ToBase64(String input, byte[] keyBytes) {
		byte[] macResult = hmacSha1(input, keyBytes);
		return EncodeUtils.base64Encode(macResult);
	}

	/**
	 * 使用HMAC-SHA1进行消息签名, 返回Base64编码的URL安全的结果.
	 * 
	 * @see #hmacSha1(String, byte[])
	 */
	public static String hmacSha1ToBase64UrlSafe(String input, byte[] keyBytes) {
		byte[] macResult = hmacSha1(input, keyBytes);
		return EncodeUtils.base64UrlSafeEncode(macResult);
	}

	/**
	 * 校验Hex编码的HMAC-SHA1签名是否正确.
	 * 
	 * @param hexMac
	 *            Hex编码的签名
	 * @param input
	 *            原始输入字符串
	 * @param keyBytes
	 *            密钥
	 */
	public static boolean isHexMacValid(String hexMac, String input, byte[] keyBytes) {
		byte[] expected = EncodeUtils.hexDecode(hexMac);
		byte[] actual = hmacSha1(input, keyBytes);

		return Arrays.equals(expected, actual);
	}

	/**
	 * 校验Base64/Base64URLSafe编码的HMAC-SHA1签名是否正确.
	 * 
	 * @param base64Mac
	 *            Base64/Base64URLSafe编码的签名
	 * @param input
	 *            原始输入字符串
	 * @param keyBytes
	 *            密钥
	 */
	public static boolean isBase64MacValid(String base64Mac, String input, byte[] keyBytes) {
		byte[] expected = EncodeUtils.base64Decode(base64Mac);
		byte[] actual = hmacSha1(input, keyBytes);

		return Arrays.equals(expected, actual);
	}

	/**
	 * 生成HMAC-SHA1密钥,返回字节数组,长度为160位(20字节). HMAC-SHA1算法对密钥无特殊要求,
	 * RFC2401建议最少长度为160位(20字节).
	 */
	public static byte[] generateMacSha1Key() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(HMACSHA1);
			keyGenerator.init(DEFAULT_HMACSHA1_KEYSIZE);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw convertRuntimeException(e);
		}
	}

	/**
	 * 生成HMAC-SHA1密钥, 返回Hex编码的结果,长度为40字符.
	 * 
	 * @see #generateMacSha1Key()
	 */
	public static String generateMacSha1HexKey() {
		return EncodeUtils.hexEncode(generateMacSha1Key());
	}

	// -- DES function --//
	/**
	 * 使用DES加密原始字符串, 返回Hex编码的结果.
	 * 
	 * @param input
	 *            原始输入字符串
	 * @param keyBytes
	 *            符合DES要求的密钥
	 */
	public static String desEncryptToHex(String input, byte[] keyBytes) {
		byte[] encryptResult = des(input.getBytes(), keyBytes, Cipher.ENCRYPT_MODE);
		return EncodeUtils.hexEncode(encryptResult);
	}

	/**
	 * 使用DES加密原始字符串, 返回Base64编码的结果.
	 * 
	 * @param input
	 *            原始输入字符串
	 * @param keyBytes
	 *            符合DES要求的密钥
	 */
	public static String desEncryptToBase64(String input, byte[] keyBytes) {
		byte[] encryptResult = des(input.getBytes(), keyBytes, Cipher.ENCRYPT_MODE);
		return EncodeUtils.base64Encode(encryptResult);
	}

	/**
	 * 使用DES解密Hex编码的加密字符串, 返回原始字符串.
	 * 
	 * @param input
	 *            Hex编码的加密字符串
	 * @param keyBytes
	 *            符合DES要求的密钥
	 */
	public static String desDecryptFromHex(String input, byte[] keyBytes) {
		byte[] decryptResult = des(EncodeUtils.hexDecode(input), keyBytes, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}

	/**
	 * 使用DES解密Base64编码的加密字符串, 返回原始字符串.
	 * 
	 * @param input
	 *            Base64编码的加密字符串
	 * @param keyBytes
	 *            符合DES要求的密钥
	 */
	public static String desDecryptFromBase64(String input, byte[] keyBytes) {
		byte[] decryptResult = des(EncodeUtils.base64Decode(input), keyBytes, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}

	/**
	 * 使用DES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * @param inputBytes
	 *            原始字节数组
	 * @param keyBytes
	 *            符合DES要求的密钥
	 * @param mode
	 *            Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] des(byte[] inputBytes, byte[] keyBytes, int mode) {
		try {
			DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

			Cipher cipher = Cipher.getInstance(DES);
			cipher.init(mode, secretKey);
			return cipher.doFinal(inputBytes);
		} catch (GeneralSecurityException e) {
			throw convertRuntimeException(e);
		}
	}

	/**
	 * 生成符合DES要求的密钥, 长度为64位(8字节).
	 */
	public static byte[] generateDesKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(DES);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw convertRuntimeException(e);
		}
	}

	/**
	 * 生成符合DES要求的Hex编码密钥, 长度为16字符.
	 */
	public static String generateDesHexKey() {
		return EncodeUtils.hexEncode(generateDesKey());
	}

	// -- AES funciton --//
	/**
	 * 使用AES加密原始字符串, 返回Hex编码的结果.
	 * 
	 * @param input
	 *            原始输入字符串
	 * @param keyBytes
	 *            符合AES要求的密钥
	 */
	public static String aesEncryptToHex(String input, byte[] keyBytes) {
		byte[] encryptResult = aes(input.getBytes(), keyBytes, Cipher.ENCRYPT_MODE);
		return EncodeUtils.hexEncode(encryptResult);
	}

	/**
	 * 使用AES加密原始字符串, 返回Base64编码的结果.
	 * 
	 * @param input
	 *            原始输入字符串
	 * @param keyBytes
	 *            符合AES要求的密钥
	 */
	public static String aesEncryptToBase64(String input, byte[] keyBytes) {
		byte[] encryptResult = aes(input.getBytes(), keyBytes, Cipher.ENCRYPT_MODE);
		return EncodeUtils.base64Encode(encryptResult);
	}

	/**
	 * 使用AES解密Hex编码的加密字符串, 返回原始字符串.
	 * 
	 * @param input
	 *            Hex编码的加密字符串
	 * @param keyBytes
	 *            符合AES要求的密钥
	 */
	public static String aesDecryptFromHex(String input, byte[] keyBytes) {
		byte[] decryptResult = aes(EncodeUtils.hexDecode(input), keyBytes, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}

	/**
	 * 使用AES解密Base64编码的加密字符串, 返回原始字符串.
	 * 
	 * @param input
	 *            Base64编码的加密字符串
	 * @param keyBytes
	 *            符合AES要求的密钥
	 */
	public static String aesDecryptFromBase64(String input, byte[] keyBytes) {
		byte[] decryptResult = aes(EncodeUtils.base64Decode(input), keyBytes, Cipher.DECRYPT_MODE);
		return new String(decryptResult);
	}

	/**
	 * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
	 * 
	 * @param inputBytes
	 *            原始字节数组
	 * @param keyBytes
	 *            符合AES要求的密钥
	 * @param mode
	 *            Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
	 */
	private static byte[] aes(byte[] inputBytes, byte[] keyBytes, int mode) {
		try {
			SecretKey secretKey = new SecretKeySpec(keyBytes, AES);
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(mode, secretKey);
			return cipher.doFinal(inputBytes);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			throw convertRuntimeException(e);
		}
	}

	/**
	 * 生成AES密钥,返回字节数组,长度为128位(16字节).
	 */
	public static byte[] generateAesKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
			keyGenerator.init(DEFAULT_AES_KEYSIZE);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw convertRuntimeException(e);
		}
	}

	/**
	 * 生成AES密钥, 返回Hex编码的结果,长度为32字符.
	 * 
	 * @see #generateMacSha1Key()
	 */
	public static String generateAesHexKey() {
		return EncodeUtils.hexEncode(generateAesKey());
	}

	private static IllegalStateException convertRuntimeException(GeneralSecurityException e) {
		return new IllegalStateException("Security exception", e);
	}

	/**
	 * @param keyByte
	 *            encrypt key
	 * @param src
	 *            plain text
	 * @param algorithm
	 * @return secret text
	 * @throws PlatformException
	 */
	public static byte[] encryptMode(byte[] keyByte, byte[] src, String transformation) throws PlatformException {
		try {
			SecretKey deskey = new SecretKeySpec(keyByte, getAlgorithm(transformation));
			Cipher c1 = Cipher.getInstance(transformation);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, nsae);
		} catch (javax.crypto.NoSuchPaddingException nspe) {
			nspe.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, nspe);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, e);
		}
	}

	/**
	 * @param keyByte
	 *            decrypt key
	 * @param src
	 *            secret text
	 * @param transformation
	 * @return plain text
	 * @throws PlatformException
	 */
	public static byte[] decryptMode(byte[] keyByte, byte[] src, String transformation) throws PlatformException {
		try {
			SecretKey deskey = new SecretKeySpec(keyByte, getAlgorithm(transformation));
			Cipher c1 = Cipher.getInstance(transformation);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, nsae);
		} catch (javax.crypto.NoSuchPaddingException nspe) {
			nspe.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, nspe);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, e);
		}
	}

	/**
	 * @param kap
	 *            secret key for encrypting in the first and third step
	 * @param kbp
	 *            secret key for decrypting in the second step
	 * @param src
	 *            plain text
	 * @return secret text
	 * @throws PlatformException
	 */
	public static byte[] encryptTripleDES(byte[] kap, byte[] kbp, byte[] src) throws PlatformException {
		if (kap == null || kap.length != 8 || kbp == null || kbp.length != 8) {
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR);
		}
		src = olsysDesPadding(src);
		byte[] keyBytes = contactArray(kap, kbp);
		keyBytes = contactArray(keyBytes, kap);
		return encryptTripleDES(keyBytes, src, "ECB", "NoPadding");
	}

	public static byte[] encryptTripleDES(byte[] keyBytes, byte[] src, String mode, String padding) throws PlatformException {
		String transformation = "DESede" + "/" + mode + "/" + padding;
		return encryptMode(keyBytes, src, transformation);
	}

	/**
	 * @param kap
	 *            secret key for decrypting in the first and third step
	 * @param kbp
	 *            secret key for encrypting in the second step
	 * @param src
	 *            secret text
	 * @return plain text
	 * @throws PlatformException
	 */
	public static byte[] decryptTripleDES(byte[] kap, byte[] kbp, byte[] src) throws PlatformException {
		if (kap == null || kap.length != 8 || kbp == null || kbp.length != 8 || src == null || src.length % 8 != 0) {
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR);
		}
		byte[] keyBytes = contactArray(kap, kbp);
		keyBytes = contactArray(keyBytes, kap);
		return decryptTripleDES(keyBytes, src, "ECB", "NoPadding");
	}

	public static byte[] decryptTripleDES(byte[] keyBytes, byte[] src, String mode, String padding) throws PlatformException {
		String transformation = "DESede" + "/" + mode + "/" + padding;
		return decryptMode(keyBytes, src, transformation);
	}

	/**
	 * @param keyBytes
	 *            encrypt key
	 * @param src
	 *            plain text
	 * @return secret text
	 * @throws PlatformException
	 */
	public static byte[] encryptDES(byte[] keyBytes, byte[] src) throws PlatformException {
		if (keyBytes == null || keyBytes.length != 8) {
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR);
		}
		src = olsysDesPadding(src);
		return encryptMode(keyBytes, src, "DES/ECB/NoPadding");
	}

	/**
	 * @param keyBytes
	 *            decrypt key
	 * @param src
	 *            secret text
	 * @return plain text
	 * @throws PlatformException
	 */
	public static byte[] decryptDES(byte[] keyBytes, byte[] src) throws PlatformException {
		if (keyBytes == null || keyBytes.length != 8 || src == null || src.length % 8 != 0) {
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR);
		}
		return decryptMode(keyBytes, src, "DES/ECB/NoPadding");
	}

	private static String getAlgorithm(String transformation) {
		int index = transformation.indexOf("/");
		if (index == -1) {
			return transformation;
		} else {
			return transformation.substring(0, index);
		}
	}

	private static byte[] olsysDesPadding(byte[] src) {
		int mod = src.length % 8;
		if (mod != 0) {
			for (int i = 0; i < 8 - mod; i++) {
				src = append(src, (byte) 0x00);
			}
		}
		return src;
	}

	public static byte[] olsysMacPadding(byte[] src) {
		int mod = src.length % 8;
		if (mod != 0) {
			src = append(src, (byte) 0x80);
			mod = src.length % 8;
			if (mod != 0)
				for (int i = 0; i < 8 - mod; i++) {
					src = append(src, (byte) 0x00);
				}
		} else {
			src = contactArray(src, ConvertUtils.hexString2ByteArray("8000000000000000"));
		}
		return src;
	}

	public static byte[] encryptDESwithCBC(byte[] keyBytes, byte[] src) throws PlatformException {
		if (keyBytes == null || keyBytes.length != 8) {
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR);
		}
		src = olsysMacPadding(src);
		int count = src.length / 8;
		byte[] output = ConvertUtils.hexString2ByteArray("0000000000000000");
		for (int i = 0; i < count; i++) {
			int current = 8 * i;
			byte[] block = subArray(src, current, current + 8);
			byte[] toEncrypt = arrayXOR(output, block);
			output = encryptMode(keyBytes, toEncrypt, "DES/ECB/NoPadding");
		}
		return output;
	}

	public static byte[] disperse(byte[] leftKeybyte, byte[] rightKey, byte[] factor) throws PlatformException {
		byte[] left = encryptTripleDES(leftKeybyte, rightKey, factor);
		byte[] right = encryptTripleDES(leftKeybyte, rightKey, bitComplement(factor));
		return contactArray(left, right);
	}

	public static byte[] createMd5MessageDigest(byte[] input) throws PlatformException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input);
			return md.digest();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, e);
		}
	}

	public static boolean verifyMd5MessageDigest(byte[] input, byte[] origDigest) throws PlatformException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input);
			return MessageDigest.isEqual(md.digest(), origDigest);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.CRYPTO_ERROR, e);
		}
	}
}