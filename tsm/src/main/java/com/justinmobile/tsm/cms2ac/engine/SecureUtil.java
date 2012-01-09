package com.justinmobile.tsm.cms2ac.engine;

import static com.justinmobile.core.utils.ByteUtils.append;
import static com.justinmobile.core.utils.ByteUtils.arrayXOR;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.intToHexBytes;
import static com.justinmobile.core.utils.ByteUtils.subArray;
import static com.justinmobile.core.utils.ByteUtils.toHexString;
import static com.justinmobile.core.utils.security.CryptoUtils.decryptDES;
import static com.justinmobile.core.utils.security.CryptoUtils.decryptTripleDES;
import static com.justinmobile.core.utils.security.CryptoUtils.encryptMode;
import static com.justinmobile.core.utils.security.CryptoUtils.encryptTripleDES;

import java.math.BigInteger;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.security.CryptoUtils;
import com.justinmobile.core.utils.security.Sha1Utils;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.exception.CryptoException;

public class SecureUtil {

	public static byte[] singleDesCbcMac(byte[] keyBytes, byte[] paddedMacSource) throws CryptoException {
		byte[] initVector = ConvertUtils.hexString2ByteArray("0000000000000000");
		return singleDesCbcMac(keyBytes, paddedMacSource, initVector);
	}

	public static byte[] singleDesCbcMac(byte[] keyBytes, byte[] paddedMacSource, byte[] initVector) throws CryptoException {
		if (keyBytes == null || keyBytes.length != 8) {
			throw new CryptoException("error key length");
		}
		if (paddedMacSource.length % 8 != 0) {
			throw new CryptoException("error paddedMacSource length");
		}

		int count = paddedMacSource.length / 8;
		byte[] output = initVector;
		for (int i = 0; i < count; i++) {
			int current = 8 * i;
			byte[] block = subArray(paddedMacSource, current, current + 8);
			byte[] toEncrypt = arrayXOR(output, block);
			output = encryptMode(keyBytes, toEncrypt, "DES/ECB/NoPadding");
		}
		return output;
	}

	public static byte[] decryptAes(byte[] plaintext, byte[] keyBytes) throws CryptoException {
		if (keyBytes == null || keyBytes.length % 8 != 0) {
			throw new CryptoException("error key length");
		}
		if (plaintext.length % 8 != 0) {
			throw new CryptoException("error paddedEncSource length");
		}
		return ConvertUtils.hexString2ByteArray(CryptoUtils.aesDecryptFromHex(ConvertUtils.byteArray2HexString(plaintext), keyBytes));
	}

	public static byte[] encryptDesEcb(byte[] keyBytes, byte[] paddedEncSource) throws CryptoException {
		if (keyBytes == null || keyBytes.length != 8) {
			throw new CryptoException("error key length");
		}
		if (paddedEncSource.length % 8 != 0) {
			throw new CryptoException("error paddedEncSource length");
		}
		return encryptMode(keyBytes, paddedEncSource, "DES/ECB/NoPadding");
	}

	public static byte[] encryptDesCbc(byte[] keyBytes, byte[] paddedEncSource, byte[] initVector) throws CryptoException {
		if (keyBytes == null || keyBytes.length != 8) {
			throw new CryptoException("error key length");
		}
		if (paddedEncSource.length % 8 != 0) {
			throw new CryptoException("error paddedEncSource length");
		}

		int count = paddedEncSource.length / 8;
		byte[] output = new byte[0];
		byte[] encryptedBytes = initVector;
		for (int i = 0; i < count; i++) {
			int current = 8 * i;
			byte[] nextSource = subArray(paddedEncSource, current, current + 8);
			byte[] toEncrypt = arrayXOR(encryptedBytes, nextSource);
			encryptedBytes = encryptMode(keyBytes, toEncrypt, "DES/ECB/NoPadding");
			output = contactArray(output, encryptedBytes);
		}
		return output;
	}

	public static byte[] encryptTripleDesEcb(byte[] kap, byte[] kbp, byte[] paddedEncSource) throws CryptoException {
		if (kap == null || kap.length != 8 || kbp == null || kbp.length != 8) {
			throw new CryptoException("error kap and kbp");
		}

		if (paddedEncSource == null || paddedEncSource.length % 8 != 0) {
			throw new CryptoException("error paddedEncSource length");
		}

		byte[] keyBytes = contactArray(kap, kbp);
		keyBytes = contactArray(keyBytes, kap);
		return encryptTripleDES(keyBytes, paddedEncSource, "ECB", "NoPadding");
	}

	public static byte[] encryptTripleDesCbc(byte[] kap, byte[] kbp, byte[] paddedEncSource, byte[] initVector) throws CryptoException {
		if (kap == null || kap.length != 8 || kbp == null || kbp.length != 8) {
			throw new CryptoException("error kap and kbp");
		}
		if (paddedEncSource == null || paddedEncSource.length % 8 != 0) {
			throw new CryptoException("error paddedEncSource length");
		}
		if (initVector == null || initVector.length != 8) {
			throw new CryptoException("error initVector");
		}

		int count = paddedEncSource.length / 8;
		byte[] output = new byte[0];
		byte[] encryptedBytes = initVector;

		for (int i = 0; i < count; i++) {
			int current = 8 * i;
			byte[] nextSource = subArray(paddedEncSource, current, current + 8);
			byte[] toEncrypt = arrayXOR(encryptedBytes, nextSource);
			encryptedBytes = encryptTripleDesEcb(kap, kbp, toEncrypt);
			output = contactArray(output, encryptedBytes);
		}
		return output;
	}

	public static byte[] computeIEDa(int batchNo, int index, int count) {
		int ideaInt = batchNo << 16 | index << 8 | count;
		return intToHexBytes(ideaInt, 3);
	}

	public static int getPaddingCount(int rawDataLength) {
		int mod = rawDataLength % 8;
		if (mod == 0) {
			return 0;
		} else {
			return 8 - mod;
		}
	}

	public static byte[] getPaddingBytes(int rawDataLength, byte toPad) {
		int paddingCount = getPaddingCount(rawDataLength);
		return getPadByPaddingCount(toPad, paddingCount);
	}

	private static byte[] getPadByPaddingCount(byte toPad, int paddingCount) {
		byte[] paddingBytes = new byte[0];
		for (int i = 0; i < paddingCount; i++) {
			paddingBytes = append(paddingBytes, toPad);
		}
		return paddingBytes;
	}

	public static byte[] encryptPadding(byte[] src) {
		byte[] paddingBytes = getPaddingBytes(src.length, (byte) 0x00);
		return contactArray(src, paddingBytes);
	}

	public static byte[] padding(byte[] src, byte padByte, boolean isRight) {
		byte[] paddingBytes = getPaddingBytes(src.length, padByte);
		if (isRight) {
			return contactArray(src, paddingBytes);
		} else {
			return contactArray(paddingBytes, src);
		}
	}

	public static byte[] fixedPadding(byte[] src, int fixedLength, byte padByte, boolean isRight) {
		byte[] paddingBytes = getFixedPaddingBytes(src, fixedLength, padByte);
		if (isRight) {
			return contactArray(src, paddingBytes);
		} else {
			return contactArray(paddingBytes, src);
		}
	}

	private static byte[] getFixedPaddingBytes(byte[] src, int fixedLength, byte toPad) {
		int paddingCount = fixedLength - src.length;
		return getPadByPaddingCount(toPad, paddingCount);
	}

	public static byte[] macPadding(byte[] src) {
		byte[] paddingBytes = getPaddingBytes(src.length, (byte) 0x20);
		return contactArray(src, paddingBytes);
	}

	public static byte[] descryptUnpadding(byte[] decryptedBytes) {
		return unpadding(decryptedBytes, 0x00);
	}

	private static byte[] unpadding(byte[] decryptedBytes, int paddingByte) {
		int unpaddingPos = decryptedBytes.length;
		for (int i = decryptedBytes.length; i > 0; i--) {
			if (decryptedBytes[i] != paddingByte) {
				unpaddingPos = i;
				break;
			}
		}
		return subArray(decryptedBytes, 0, unpaddingPos);
	}

	public static byte[] decryptDesEcb(byte[] keyBytes, byte[] decryptSource) throws CryptoException {
		if (keyBytes == null || keyBytes.length != 8) {
			throw new CryptoException("error key length");
		}
		if (decryptSource.length % 8 != 0) {
			throw new CryptoException("error decryptSource length");
		}
		return decryptDES(keyBytes, decryptSource);
	}

	public static byte[] decryptDesCbc(byte[] keyBytes, byte[] decryptSource) throws CryptoException {
		if (keyBytes == null || keyBytes.length != 8) {
			throw new CryptoException("error key length");
		}
		if (decryptSource.length % 8 != 0) {
			throw new CryptoException("error decryptSource length");
		}

		int count = decryptSource.length / 8;
		byte[] output = new byte[0];
		byte[] nextDecryptSource = subArray(decryptSource, 0, 8);
		for (int i = 1; i < count; i++) {
			byte[] decrptedBytes = decryptDES(keyBytes, nextDecryptSource);
			byte[] nextResult = arrayXOR(decrptedBytes, nextDecryptSource);
			int current = 8 * i;
			nextDecryptSource = subArray(decryptSource, current, current + 8);
			output = contactArray(output, nextResult);
		}
		return output;
	}

	public static byte[] decryptTripleDesEcb(byte[] kap, byte[] kbp, byte[] decryptSource) throws CryptoException {
		if (kap == null || kap.length != 8 || kbp == null || kbp.length != 8) {
			throw new CryptoException("error kap and kbp");
		}
		if (decryptSource == null || decryptSource.length % 8 != 0) {
			throw new CryptoException("error decryptSource length");
		}

		return decryptTripleDES(kap, kbp, decryptSource);
	}

	public static byte[] decryptTripleDesCbc(byte[] kap, byte[] kbp, byte[] decryptSource) throws CryptoException {
		if (kap == null || kap.length != 8 || kbp == null || kbp.length != 8) {
			throw new CryptoException("error kap and kbp");
		}
		if (decryptSource == null || decryptSource.length % 8 != 0) {
			throw new CryptoException("error decryptSource length");
		}

		int count = decryptSource.length / 8;
		byte[] output = new byte[0];
		byte[] xorParam = ConvertUtils.hexString2ByteArray("0000000000000000");
		for (int i = 0; i < count; i++) {
			int current = 8 * i;
			byte[] currentDecryptSource = subArray(decryptSource, current, current + 8);
			byte[] decrptedBytes = decryptTripleDES(kap, kbp, currentDecryptSource);
			byte[] xorResult = arrayXOR(decrptedBytes, xorParam);
			xorParam = currentDecryptSource;
			output = contactArray(output, xorResult);
		}
		return output;
	}

	public static byte[] retailTripleDesCbc2KeyMac(byte[] paddedMacSource, byte[] kap, byte[] kbp, byte[] initVector)
			throws CryptoException {
		byte[] desSrc = subArray(paddedMacSource, 0, paddedMacSource.length - 8);
		byte[] desCbcEnc = SecureUtil.singleDesCbcMac(kap, desSrc, initVector);

		byte[] lastBlock = subArray(paddedMacSource, paddedMacSource.length - 8, paddedMacSource.length);
		byte[] toEncrypt = arrayXOR(desCbcEnc, lastBlock);
		byte[] tripleDesCbcEnc = SecureUtil.encryptTripleDesEcb(kap, kbp, toEncrypt);
		return tripleDesCbcEnc;
	}

	public static byte[] fullTripleDesCbc2KeyMac(byte[] paddedMacSource, byte[] kap, byte[] kbp, byte[] initVector) throws CryptoException {
		if (kap == null || kap.length != 8 || kbp == null || kbp.length != 8) {
			throw new CryptoException("error kap and kbp");
		}
		if (paddedMacSource == null || paddedMacSource.length % 8 != 0) {
			throw new CryptoException("error paddedMacSource length");
		}
		if (initVector == null || initVector.length != 8) {
			throw new CryptoException("error initVector");
		}

		int count = paddedMacSource.length / 8;
		byte[] output = initVector;
		for (int i = 0; i < count; i++) {
			int current = 8 * i;
			byte[] block = subArray(paddedMacSource, current, current + 8);
			byte[] toEncrypt = arrayXOR(output, block);
			output = encryptTripleDES(kap, kbp, toEncrypt);
		}
		return output;
	}

	public static byte[] scp02Padding(byte[] paddingSource) {
		int paddingCount = getPaddingCount(paddingSource.length);
		byte[] paddingBytes = null;
		if (paddingCount == 0) {
			paddingBytes = ConvertUtils.hexString2ByteArray("8000000000000000");
		} else if (paddingCount == 1) {
			paddingBytes = ConvertUtils.hexString2ByteArray("80");
		} else {
			byte[] firstPaddingBytes = ConvertUtils.hexString2ByteArray("80");
			byte[] secondPaddingBytes = getPadByPaddingCount((byte) 0x00, paddingCount - 1);
			paddingBytes = contactArray(firstPaddingBytes, secondPaddingBytes);
		}
		return contactArray(paddingSource, paddingBytes);
	}

	public static byte[] signWithSha1AndPKCS1(byte[] src, Cms2acParam cms2acParam) {
		byte[] digest = generateDegistBySha1(src);

		int targetLength = cms2acParam.getMod().getValue().length() / 2;
		byte[] paddedDigest = pkcs1Padding(digest, "3021300906052B0E03021A05000414", targetLength);

		byte[] result = signByRSA(paddedDigest, cms2acParam);

		return result;

	}

	private static byte[] signByRSA(byte[] paddedDigest, Cms2acParam cms2acParam) {
		return excuteRSA(paddedDigest, cms2acParam.getMod().getValue(), cms2acParam.getPrivateExponent().getValue());
	}

	private static byte[] excuteRSA(byte[] paddedDigest, String mod, String exponent) {
		BigInteger src = new BigInteger(paddedDigest);
		BigInteger result = src.modPow(new BigInteger(exponent, 16), new BigInteger(mod, 16));
		return ConvertUtils.hexString2ByteArray(result.toString(16));
	}

	private static byte[] pkcs1Padding(byte[] digest, String digestAlgorithmID, int targetLength) {
		String digestStr = toHexString(digest);
		String header = "0001";
		int currentLength = (header.length() + digestStr.length() + digestAlgorithmID.length()) / 2 + 1;
		int paddingLength = targetLength - currentLength;

		StringBuilder padding = new StringBuilder();
		for (int i = 0; i < paddingLength; i++) {
			padding.append("FF");
		}

		StringBuilder result = new StringBuilder();
		result = result.append(header).append(padding).append("00").append(digestAlgorithmID).append(digestStr);
		return ConvertUtils.hexString2ByteArray(result.toString());
	}

	private static byte[] generateDegistBySha1(byte[] src) {
		return (new Sha1Utils()).getDigestOfBytes(src);
	}

}
