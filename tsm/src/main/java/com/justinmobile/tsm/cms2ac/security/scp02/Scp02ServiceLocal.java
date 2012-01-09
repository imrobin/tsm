package com.justinmobile.tsm.cms2ac.security.scp02;

import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.generateHexString;
import static com.justinmobile.core.utils.ByteUtils.subArray;
import static com.justinmobile.core.utils.ConvertUtils.hexString2ByteArray;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.dto.Key;
import com.justinmobile.tsm.cms2ac.engine.SecureUtil;
import com.justinmobile.tsm.cms2ac.exception.CryptoException;

public class Scp02ServiceLocal extends AbstractScp02Service {

	protected byte[] internalComputeMac(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam, byte[] fullDisperseFactor,
			byte[] sessionSalt) throws CryptoException {
		byte[] macSessionKey = generateMacSessionKey(cms2acParam, fullDisperseFactor, sessionSalt);

		byte[] kap = subArray(macSessionKey, 0, 8);
		byte[] kbp = subArray(macSessionKey, 8, 16);

		byte[] tripleDesCbcEnc = SecureUtil.retailTripleDesCbc2KeyMac(paddedMacSource, kap, kbp, initVector);

		return tripleDesCbcEnc;
	}

	protected byte[] internalComputeMacNextIcv(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam,
			byte[] scp02TripleDisperseFactor, byte[] sessionSalt) throws CryptoException {
		byte[] macSessionKey = generateMacSessionKey(cms2acParam, scp02TripleDisperseFactor, sessionSalt);

		byte[] kap = subArray(macSessionKey, 0, 8);
		byte[] kbp = subArray(macSessionKey, 8, 16);
		byte[] mac = SecureUtil.retailTripleDesCbc2KeyMac(paddedMacSource, kap, kbp, initVector);

		byte[] icvCipherKey = subArray(macSessionKey, 0, 8);
		byte[] nextIcv = SecureUtil.encryptDesEcb(icvCipherKey, mac);

		return contactArray(mac, nextIcv);
	}

	protected byte[] internalEncryptData(byte[] paddedEncSource, byte[] initVector, Cms2acParam cms2acParam, byte[] fullDisperseFactor,
			byte[] sessionSalt) throws CryptoException {
		byte[] cipherSessionKey = generateCipherSessionKey(cms2acParam, fullDisperseFactor, sessionSalt);

		byte[] kap = subArray(cipherSessionKey, 0, 8);
		byte[] kbp = subArray(cipherSessionKey, 8, 16);

		byte[] encryptedBytes = SecureUtil.encryptTripleDesCbc(kap, kbp, paddedEncSource, initVector);
		return encryptedBytes;
	}

	protected byte[] internalEncryptKey(byte[] encSource, Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt)
			throws CryptoException {
		byte[] dekSessionKey = generateDekSessionKey(cms2acParam, fullDisperseFactor, sessionSalt);
		byte[] kap = subArray(dekSessionKey, 0, 8);
		byte[] kbp = subArray(dekSessionKey, 8, 16);

		byte[] encryptedBytes = SecureUtil.encryptTripleDesEcb(kap, kbp, encSource);
		return encryptedBytes;
	}

	private byte[] internalEncryptSensitiveData(byte[] encSource, byte[] keyValue, byte[] fullDisperseFactor, byte[] sessionSalt)
			throws CryptoException {
		byte[] dekSessionKey = generateDekSessionKey(fullDisperseFactor, sessionSalt, keyValue);
		byte[] kap = subArray(dekSessionKey, 0, 8);
		byte[] kbp = subArray(dekSessionKey, 8, 16);

		byte[] encryptedBytes = SecureUtil.encryptTripleDesEcb(kap, kbp, encSource);
		return encryptedBytes;
	}

	public byte[] encryptIcv(byte[] encSource, Cms2acParam cms2acParam, boolean isCmac) {
		try {
			if (encSource.length != 8) {
				throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR);
			}
			byte[] scp02TripleDisperseFactor = getScp02FullDisperseFactor(cms2acParam, KeyProfile.SCP02_MAC_TYPE);
			byte[] sessionSalt = disperseKeyHelper.getScp02MacSessionSalt(cms2acParam, isCmac);
			return internalEncryptIcv(encSource, cms2acParam, scp02TripleDisperseFactor, sessionSalt);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	protected byte[] internalEncryptIcv(byte[] encSource, Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt)
			throws CryptoException {
		byte[] macSessionKey = generateMacSessionKey(cms2acParam, fullDisperseFactor, sessionSalt);

		byte[] icvCipherKey = subArray(macSessionKey, 0, 8);

		byte[] encryptedBytes = SecureUtil.encryptDesEcb(icvCipherKey, encSource);
		return encryptedBytes;
	}

	@Override
	protected byte[] internalExportKey(KeyProfile keyProfile, byte[] scp02FullDisperseFactor) {
		byte[] rawKey = hexString2ByteArray(keyProfile.getValue());
		return disperseSdKey(rawKey, scp02FullDisperseFactor);
	}

	public byte[] generateKeyCheckValue(byte[] keyValue, Cms2acParam cms2acParam) {
		try {
			byte[] encSource = hexString2ByteArray("0000000000000000");
			byte[] kap = subArray(keyValue, 0, 8);
			byte[] kbp = subArray(keyValue, 8, 16);

			byte[] encryptedBytes = SecureUtil.encryptTripleDesEcb(kap, kbp, encSource);
			byte[] checkValue = subArray(encryptedBytes, 0, 3);
			return checkValue;
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	public byte[] decrypt(byte[] decryptSource, Cms2acParam cms2acParam) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] generateCipherSessionKey(Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt) {
		String cms2acCipherKey = cms2acParam.getKic().getValue();
		return disperseKey(hexString2ByteArray(cms2acCipherKey), fullDisperseFactor, sessionSalt);
	}

	public byte[] generateMacSessionKey(Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt) {
		String cms2acMacKey = cms2acParam.getKid().getValue();
		return disperseKey(hexString2ByteArray(cms2acMacKey), fullDisperseFactor, sessionSalt);
	}

	public byte[] generateDekSessionKey(Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt) {
		String cms2acDekKey = cms2acParam.getDek().getValue();
		return generateDekSessionKey(fullDisperseFactor, sessionSalt, hexString2ByteArray(cms2acDekKey));
	}

	private byte[] generateDekSessionKey(byte[] fullDisperseFactor, byte[] sessionSalt, byte[] dekKey) {
		if (sessionSalt.length == 0) {
			return dekKey;
		} else {
			return disperseKey(dekKey, fullDisperseFactor, sessionSalt);
		}
	}

	public byte[] disperseKey(byte[] rawKey, byte[] fullDisperseFactor, byte[] sessionSalt) {
		try {
			byte[] sdKey = disperseSdKey(rawKey, fullDisperseFactor);
			byte[] sessionKey = disperseSessionKey(sdKey, sessionSalt);
			return sessionKey;
		} catch (CryptoException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] disperseSessionKey(byte[] cardKey, byte[] sessionSalt) throws CryptoException {
		if (cardKey == null || cardKey.length != 16) {
			throw new CryptoException("error cardKey");
		}
		if (sessionSalt == null || sessionSalt.length != 16) {
			throw new CryptoException("error session salt");
		}
		byte[] kap = subArray(cardKey, 0, 8);
		byte[] kbp = subArray(cardKey, 8, 16);
		byte[] initVector = hexString2ByteArray("0000000000000000");

		return SecureUtil.encryptTripleDesCbc(kap, kbp, sessionSalt, initVector);
	}

	public byte[] disperseSdKey(byte[] rawKey, byte[] tripleDisperseFactor) {
		try {
			if (this.disperseLevel == ZERO_LEVEL_DISPERSE) {
				return rawKey;
			} else if (this.disperseLevel == ONE_LEVEL_DISPERSE) {

				byte[] singleDisperseFactor = getSingleDisperseFactor(tripleDisperseFactor);
				return oneLevelDisperse(rawKey, singleDisperseFactor);

			} else if (this.disperseLevel == TWO_LEVEL_DISPERSE) {

				byte[] doubleDisperseFactor = getDoubleDisperseFactor(tripleDisperseFactor);
				return towLevelDisperse(rawKey, doubleDisperseFactor);

			} else {
				return threeLevelDisperse(rawKey, tripleDisperseFactor);
			}
		} catch (CryptoException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] oneLevelDisperse(byte[] cardKey, byte[] singleDisperseFactor) throws CryptoException {
		byte[] securityDomainKey = disperseSubKey(cardKey, singleDisperseFactor);
		return securityDomainKey;
	}

	private byte[] towLevelDisperse(byte[] provinceKey, byte[] doubleDisperseFactor) throws CryptoException {
		byte[] firstDisperseFactor = getFirstDisperseFactor(doubleDisperseFactor);
		byte[] cardKey = disperseSubKey(provinceKey, firstDisperseFactor);

		byte[] secondDisperseFactor = getSecondDisperseFactor(doubleDisperseFactor);
		byte[] securityDomainKey = disperseSubKey(cardKey, secondDisperseFactor);
		return securityDomainKey;
	}

	private byte[] threeLevelDisperse(byte[] rootKey, byte[] tripleDisperseFactor) throws CryptoException {
		byte[] firstDisperseFactor = getFirstDisperseFactor(tripleDisperseFactor);
		byte[] provinceKey = disperseSubKey(rootKey, firstDisperseFactor);

		byte[] secondDisperseFactor = getSecondDisperseFactor(tripleDisperseFactor);
		byte[] cardKey = disperseSubKey(provinceKey, secondDisperseFactor);

		byte[] thirdDisperseFactor = getThirdDisperseFactor(tripleDisperseFactor);
		byte[] securityDomainKey = disperseSubKey(cardKey, thirdDisperseFactor);

		return securityDomainKey;
	}

	private byte[] disperseSubKey(byte[] parentKey, byte[] disperseFactor) throws CryptoException {
		if (disperseFactor == null || disperseFactor.length != 16) {
			throw new CryptoException("error disperse factor");
		}
		byte[] left = subArray(parentKey, 0, 8);
		byte[] right = subArray(parentKey, 8, 16);

		return disperse(left, right, disperseFactor);
	}

	private byte[] disperse(byte[] leftKey, byte[] rightKey, byte[] factor) throws CryptoException {
		return SecureUtil.encryptTripleDesEcb(leftKey, rightKey, factor);
	}

	public byte[] generateRandom(int randomLength) {
		String randHex = generateHexString(8);
		return hexString2ByteArray(randHex);
	}

	public byte[] getKeyCheckValue(byte[] keyValue) {
		return null;
	}

	@Override
	protected byte[] internalAcquireToken(byte[] src, Cms2acParam cms2acParam) {
		return SecureUtil.signWithSha1AndPKCS1(src, cms2acParam);
	}

	@Override
	protected byte[] internalExportEncKeyAndCheckValue(Cms2acParam cms2acParam, KeyProfile keyProfile, byte[] exportDisperseFactor,
			byte[] encryptDisperseFactor, byte[] sessionSalt) {
		byte[] keyValue = internalExportKey(keyProfile, exportDisperseFactor);// 导出密钥
		byte[] checkValue = generateKeyCheckValue(keyValue, cms2acParam);// 计算校验值
		byte[] encKeyValue = new byte[0];
		try {
			encKeyValue = internalEncryptKey(keyValue, cms2acParam, encryptDisperseFactor, sessionSalt);// 加密密钥
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
		return contactArray(encKeyValue, checkValue);
	}

	@Override
	protected byte[] internalTransformEncrypt(byte[] ciphertext, Key kek, Key dek) {
		// 首先根据要求的算法对敏感数据进行解密
		ApplicationKeyProfile kekKeyProfile = kek.getKeyProfile("kek");
		byte[] plaintext = kek.getAlgorithm().decrypt(ciphertext, ConvertUtils.hexString2ByteArray(kekKeyProfile.getKeyValue()));

		// 使用dek密钥进行转加密
		KeyProfile dekKeyProfile = dek.getKeyProfile("dek");
		return internalEncryptSensitiveData(plaintext, ConvertUtils.hexString2ByteArray(dekKeyProfile.getValue()), dek.getDisperseFactor(),
				dek.getSessionSalt());
	}

	@Override
	protected byte[] internalDecryptPersoData(byte[] ciphertext, Key tk) {
		ApplicationKeyProfile kekKeyProfile = tk.getKeyProfile("tk");
		return tk.getAlgorithm().decrypt(ciphertext, ConvertUtils.hexString2ByteArray(kekKeyProfile.getKeyValue()));
	}

}
