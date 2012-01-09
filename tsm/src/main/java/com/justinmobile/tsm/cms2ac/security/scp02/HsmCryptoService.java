package com.justinmobile.tsm.cms2ac.security.scp02;

import org.springframework.stereotype.Service;

import com.justinmobile.tsm.cms2ac.exception.CryptoException;

@Service("keyouCryptoService")
public abstract class HsmCryptoService {

	public static final String VENDOR_NAME = "Keyou";

	// 分散,加密
	public static final int ALG_ENC_3DES_ECB = 0x81;

	public static final int ALG_ENC_3DES_CBC = 0x82;

	public static final int ALG_ENC_DES_ECB = 0x83;

	public static final int ALG_ENC_DES_CBC = 0x84;

	// MAC
	public static final int ALG_MAC_DES_CBC = 0x80;

	public static final int ALG_MAC_3DES_CBC2 = 0x82;

	public static final int ALG_MAC_DES_ECB = 0x83;

	public static final int ALG_MAC_3DES_CBC = 0x84;

	public static final int ALG_SHA1 = 0x90;

	public static final int PADDING_EXTERNAL = 0;

	public static final int PADDING_INTERNAL = 1;

	public static final int SESSION_KEY_NONE = 0;

	public static final int SESSION_KEY_EXIST = 1;

	public static final int OPERATE_ENCRYPT = 0;

	public static final int OPERATE_DECRYPT = 1;

	public static final int MAC_FOUR_BYTES = 1;

	public static final int MAC_EIGHT_BYTES = 2;

	public static final int OPERATION_FLAG_ENCRYPT = 1;
	
	public static final int OPERATION_FLAG_DECRYPT = 0;

	public byte[] singleDesCbcMac(byte[] paddedMacSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex)
			throws CryptoException {
		return internalComputeMac(paddedMacSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, ALG_MAC_DES_CBC);
	}

	public byte[] retailTripleDesCbc2KeyMac(byte[] paddedMacSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex)
			throws CryptoException {
		return internalComputeMac(paddedMacSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, ALG_MAC_3DES_CBC);
	}

	public byte[] fullTripleDesCbc2KeyMac(byte[] paddedMacSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex)
			throws CryptoException {
		return internalComputeMac(paddedMacSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, ALG_MAC_3DES_CBC2);
	}

	protected abstract byte[] internalComputeMac(byte[] paddedMacSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex,
			int alg) throws CryptoException;

	public abstract byte[] retailTripleDesCbc2KeyMacNextIcv(byte[] paddedMacSource, byte[] initVector, byte[] fullDisperseFactor,
			byte[] sessionSalt, int hsmKeyIndex) throws CryptoException;

	public byte[] encryptTripleDesEcb(byte[] paddedEncSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex)
			throws CryptoException {
		return internalEncrypt(paddedEncSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, ALG_ENC_3DES_ECB);
	}

	public byte[] encryptTripleDesCbc(byte[] paddedEncSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex)
			throws CryptoException {
		return internalEncrypt(paddedEncSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, ALG_ENC_3DES_CBC);
	}

	private byte[] internalEncrypt(byte[] paddedEncSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex, int alg)
			throws CryptoException {
		return hsmEncryptDecrypt(paddedEncSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, alg, OPERATE_ENCRYPT);
	}

	public byte[] decryptTripleDesEcb(byte[] decryptSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex)
			throws CryptoException {
		return internalDecrypt(decryptSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, ALG_ENC_3DES_ECB);
	}

	public byte[] decryptTripleDesCbc(byte[] decryptSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex)
			throws CryptoException {
		return internalDecrypt(decryptSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, ALG_ENC_3DES_CBC);
	}

	private byte[] internalDecrypt(byte[] paddedEncSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex, int alg)
			throws CryptoException {
		return hsmEncryptDecrypt(paddedEncSource, fullDisperseFactor, sessionSalt, hsmKeyIndex, alg, OPERATE_DECRYPT);
	}

	protected abstract byte[] hsmEncryptDecrypt(byte[] paddedSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex,
			int alg, int operate) throws CryptoException;

	public abstract byte[] exportKey(byte[] fullDisperseFactor, int hsmKeyIndex) throws CryptoException;

	public abstract byte[] generateCheckValue(byte[] keyValue) throws CryptoException;

	public abstract byte[] generateRandom(int randomLength) throws CryptoException;

	protected int parseSessionKeyFlag(byte[] sessionSalt) {
		if (sessionSalt.length % 8 != 0) {
			throw new IllegalArgumentException("error length of sessionSalt");
		}
		return sessionSalt.length / 8 == 0 ? SESSION_KEY_NONE : SESSION_KEY_EXIST;
	}

	protected int parseDivNum(byte[] fullDisperseFactor) {
		if (fullDisperseFactor.length % 16 != 0) {
			throw new IllegalArgumentException("error length of fullDisperseFactor");
		}
		return fullDisperseFactor.length / 16;
	}
}
