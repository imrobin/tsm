package com.justinmobile.tsm.cms2ac.security.scp02;

import static com.justinmobile.core.utils.ByteUtils.subArray;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.dto.Key;
import com.justinmobile.tsm.cms2ac.exception.CryptoException;
import com.justinmobile.tsm.cms2ac.manager.HsmkeyConfigManager;

public class Scp02ServiceHsm extends AbstractScp02Service {

	private HsmCryptoService hsmCryptoService;

	private HsmkeyConfigManager hsmKeyConfigManager;

	@Override
	protected byte[] internalComputeMac(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam, byte[] fullDisperseFactor,
			byte[] sessionSalt) throws CryptoException {
		int hsmMacKeyIndex = getHsmMacKeyIndex(cms2acParam);
		byte[] mac = hsmCryptoService.retailTripleDesCbc2KeyMac(paddedMacSource, fullDisperseFactor, sessionSalt, hsmMacKeyIndex);
		return mac;
	}

	@Override
	protected byte[] internalComputeMacNextIcv(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam,
			byte[] fullDisperseFactor, byte[] sessionSalt) throws CryptoException {
		int hsmMacKeyIndex = getHsmMacKeyIndex(cms2acParam);
		byte[] macNextIcv = hsmCryptoService.retailTripleDesCbc2KeyMacNextIcv(paddedMacSource, initVector, fullDisperseFactor, sessionSalt,
				hsmMacKeyIndex);
		return macNextIcv;
	}

	@Override
	protected byte[] internalEncryptData(byte[] paddedEncSource, byte[] initVector, Cms2acParam cms2acParam, byte[] fullDisperseFactor,
			byte[] sessionSalt) throws CryptoException {

		int hsmEncKeyIndex = getHsmEncKeyIndex(cms2acParam);
		byte[] encryptedData = hsmCryptoService.encryptTripleDesCbc(paddedEncSource, fullDisperseFactor, sessionSalt, hsmEncKeyIndex);
		return encryptedData;
	}

	@Override
	protected byte[] internalEncryptKey(byte[] encSource, Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt)
			throws CryptoException {
		int hsmDekKeyIndex = getHsmDekKeyIndex(cms2acParam);
		byte[] encryptedKey = hsmCryptoService.encryptTripleDesEcb(encSource, fullDisperseFactor, sessionSalt, hsmDekKeyIndex);
		return encryptedKey;
	}

	@Override
	protected byte[] internalExportKey(KeyProfile keyProfile, byte[] scp02FullDisperseFactor) throws CryptoException {
		HsmkeyConfig scp02MacHsmKeyConfig = hsmKeyConfigManager.getByKeyProfileVendor(keyProfile, KeyouCryptoService.VENDOR_NAME);
		int hsmKeyIndex = scp02MacHsmKeyConfig.getIndex();

		if (this.disperseLevel == ZERO_LEVEL_DISPERSE) {

			throw new CryptoException("disperseLevel should be larger than zero");

		} else if (this.disperseLevel == ONE_LEVEL_DISPERSE) {

			byte[] singleDisperseFactor = getSingleDisperseFactor(scp02FullDisperseFactor);
			return hsmCryptoService.exportKey(singleDisperseFactor, hsmKeyIndex);

		} else if (this.disperseLevel == TWO_LEVEL_DISPERSE) {

			byte[] doubleDisperseFactor = getDoubleDisperseFactor(scp02FullDisperseFactor);
			return hsmCryptoService.exportKey(doubleDisperseFactor, hsmKeyIndex);

		} else {

			return hsmCryptoService.exportKey(scp02FullDisperseFactor, hsmKeyIndex);

		}
	}

	public byte[] generateKeyCheckValue(byte[] keyValue, Cms2acParam cms2acParam) {
		try {
			byte[] encryptedBytes = hsmCryptoService.generateCheckValue(keyValue);
			byte[] checkValue = subArray(encryptedBytes, 0, 3);
			return checkValue;
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	public byte[] generateRandom(int randomLength) {
		try {
			return hsmCryptoService.generateRandom(randomLength);
		} catch (CryptoException e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	public byte[] decrypt(byte[] decryptSource, Cms2acParam cms2acParam) {
		// TODO Auto-generated method stub
		return null;
	}

	private int getHsmMacKeyIndex(Cms2acParam cms2acParam) {
		KeyProfile scp02MacKey = cms2acParam.getKid();
		HsmkeyConfig scp02MacHsmKeyConfig = hsmKeyConfigManager.getByKeyProfileVendor(scp02MacKey, KeyouCryptoService.VENDOR_NAME);
		return scp02MacHsmKeyConfig.getIndex();
	}

	private int getHsmEncKeyIndex(Cms2acParam cms2acParam) {
		KeyProfile scp02EncKey = cms2acParam.getKic();
		HsmkeyConfig scp02MacHsmKeyConfig = hsmKeyConfigManager.getByKeyProfileVendor(scp02EncKey, KeyouCryptoService.VENDOR_NAME);
		return scp02MacHsmKeyConfig.getIndex();
	}

	private int getHsmDekKeyIndex(Cms2acParam cms2acParam) {
		KeyProfile scp02DekKey = cms2acParam.getDek();
		HsmkeyConfig scp02MacHsmKeyConfig = hsmKeyConfigManager.getByKeyProfileVendor(scp02DekKey, KeyouCryptoService.VENDOR_NAME);
		return scp02MacHsmKeyConfig.getIndex();
	}

	public void setKeyouCryptoService(KeyouCryptoService keyouCryptoService) {
		this.hsmCryptoService = keyouCryptoService;
	}

	public void setHsmKeyConfigManager(HsmkeyConfigManager hsmKeyConfigManager) {
		this.hsmKeyConfigManager = hsmKeyConfigManager;
	}

	public void setHsmCryptoService(HsmCryptoService hsmCryptoService) {
		this.hsmCryptoService = hsmCryptoService;
	}

	public byte[] getKeyCheckValue(byte[] keyValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte[] internalAcquireToken(byte[] src, Cms2acParam cms2acParam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte[] internalExportEncKeyAndCheckValue(Cms2acParam cms2acParam, KeyProfile keyProfile, byte[] exportDisperseFactor,
			byte[] encryptDisperseFactor, byte[] sessionSalt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte[] internalTransformEncrypt(byte[] ciphertext, Key kek, Key dek) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte[] internalDecryptPersoData(byte[] ciphertext, Key tk) {
		// TODO Auto-generated method stub
		return null;
	}
}
