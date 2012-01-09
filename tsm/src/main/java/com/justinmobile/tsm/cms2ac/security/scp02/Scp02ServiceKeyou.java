package com.justinmobile.tsm.cms2ac.security.scp02;

import static com.justinmobile.core.utils.ByteUtils.subArray;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.dto.Key;
import com.justinmobile.tsm.cms2ac.exception.CryptoException;
import com.justinmobile.tsm.cms2ac.manager.HsmkeyConfigManager;

public class Scp02ServiceKeyou extends AbstractScp02Service {

	private KeyouCryptoService keyouCryptoService;

	private HsmkeyConfigManager hsmKeyConfigManager;

	@Override
	protected byte[] internalComputeMac(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam, byte[] fullDisperseFactor,
			byte[] sessionSalt) throws CryptoException {
		int hsmMacKeyIndex = getHsmMacKeyIndex(cms2acParam);

		fullDisperseFactor = getDisperseFactorByDisperseLevel(fullDisperseFactor);

		byte[] mac = keyouCryptoService.retailTripleDesCbc2KeyMac(paddedMacSource, fullDisperseFactor, sessionSalt, hsmMacKeyIndex);
		return mac;
	}

	@Override
	protected byte[] internalComputeMacNextIcv(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam,
			byte[] fullDisperseFactor, byte[] sessionSalt) throws CryptoException {
		int hsmMacKeyIndex = getHsmMacKeyIndex(cms2acParam);

		fullDisperseFactor = getDisperseFactorByDisperseLevel(fullDisperseFactor);

		byte[] macNextIcv = keyouCryptoService.retailTripleDesCbc2KeyMacNextIcv(paddedMacSource, initVector, fullDisperseFactor,
				sessionSalt, hsmMacKeyIndex);
		return macNextIcv;
	}

	@Override
	protected byte[] internalEncryptData(byte[] paddedEncSource, byte[] initVector, Cms2acParam cms2acParam, byte[] fullDisperseFactor,
			byte[] sessionSalt) throws CryptoException {

		int hsmEncKeyIndex = getHsmEncKeyIndex(cms2acParam);

		fullDisperseFactor = getDisperseFactorByDisperseLevel(fullDisperseFactor);

		byte[] encryptedData = keyouCryptoService.encryptTripleDesCbc(paddedEncSource, fullDisperseFactor, sessionSalt, hsmEncKeyIndex);
		return encryptedData;
	}

	@Override
	protected byte[] internalEncryptKey(byte[] encSource, Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt)
			throws CryptoException {
		int hsmDekKeyIndex = getHsmDekKeyIndex(cms2acParam);

		fullDisperseFactor = getDisperseFactorByDisperseLevel(fullDisperseFactor);

		byte[] encryptedKey = keyouCryptoService.encryptTripleDesEcb(encSource, fullDisperseFactor, sessionSalt, hsmDekKeyIndex);
		return encryptedKey;
	}

	@Override
	protected byte[] internalExportKey(KeyProfile keyProfile, byte[] scp02FullDisperseFactor) throws CryptoException {
		HsmkeyConfig scp02MacHsmKeyConfig = hsmKeyConfigManager.getByKeyProfileVendor(keyProfile, KeyouCryptoService.VENDOR_NAME);
		int hsmKeyIndex = scp02MacHsmKeyConfig.getIndex();

		if (this.disperseLevel == ZERO_LEVEL_DISPERSE) {
			// TODO 联调临时设定
			// throw new
			// CryptoException("disperseLevel should be larger than zero");
			return new byte[0];

		} else if (this.disperseLevel == ONE_LEVEL_DISPERSE) {

			byte[] singleDisperseFactor = getSingleDisperseFactor(scp02FullDisperseFactor);
			return keyouCryptoService.exportKey(singleDisperseFactor, hsmKeyIndex);

		} else if (this.disperseLevel == TWO_LEVEL_DISPERSE) {

			byte[] doubleDisperseFactor = getDoubleDisperseFactor(scp02FullDisperseFactor);
			return keyouCryptoService.exportKey(doubleDisperseFactor, hsmKeyIndex);

		} else {

			return keyouCryptoService.exportKey(scp02FullDisperseFactor, hsmKeyIndex);

		}
	}

	public byte[] generateKeyCheckValue(byte[] keyValue, Cms2acParam cms2acParam) {
		try {
			byte[] encryptedBytes = keyouCryptoService.generateCheckValue(keyValue);
			byte[] checkValue = subArray(encryptedBytes, 0, 3);
			return checkValue;
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	public byte[] generateRandom(int randomLength) {
		try {
			return keyouCryptoService.generateRandom(randomLength);
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

	private int getHsmPublicKeyIndex(Cms2acParam cms2acParam) {
		KeyProfile publicExponent = cms2acParam.getPublicExponent();
		HsmkeyConfig scp02MacHsmKeyConfig = hsmKeyConfigManager.getByKeyProfileVendor(publicExponent, KeyouCryptoService.VENDOR_NAME);
		return scp02MacHsmKeyConfig.getIndex();
	}

	public void setKeyouCryptoService(KeyouCryptoService keyouCryptoService) {
		this.keyouCryptoService = keyouCryptoService;
	}

	public void setHsmKeyConfigManager(HsmkeyConfigManager hsmKeyConfigManager) {
		this.hsmKeyConfigManager = hsmKeyConfigManager;
	}

	public byte[] getKeyCheckValue(byte[] keyValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected byte[] internalAcquireToken(byte[] src, Cms2acParam cms2acParam) throws CryptoException {
		int hsmMacKeyIndex = getHsmPublicKeyIndex(cms2acParam);

		byte[] signature = keyouCryptoService.internalAcquireToken(src, hsmMacKeyIndex);

		return signature;
	}

	@Override
	protected byte[] internalExportEncKeyAndCheckValue(Cms2acParam cms2acParam, KeyProfile keyProfile, byte[] exportDisperseFactor,
			byte[] encryptDisperseFactor, byte[] sessionSalt) {
		int hsmExportKeyIndex = hsmKeyConfigManager.getByKeyProfileVendor(keyProfile, KeyouCryptoService.VENDOR_NAME).getIndex();
		int hsmEncryptKeyIndex = hsmKeyConfigManager.getByKeyProfileVendor(cms2acParam.getKic(), KeyouCryptoService.VENDOR_NAME).getIndex();

		encryptDisperseFactor = getDisperseFactorByDisperseLevel(encryptDisperseFactor);
		exportDisperseFactor = getDisperseFactorByDisperseLevel(exportDisperseFactor);

		try {
			return keyouCryptoService.exportKeyAndCheckValue(hsmExportKeyIndex, encryptDisperseFactor, hsmEncryptKeyIndex,
					exportDisperseFactor, sessionSalt);
		} catch (CryptoException e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	@Override
	protected byte[] internalTransformEncrypt(byte[] ciphertext, Key kek, Key dek) {
		ApplicationKeyProfile kekProfile = kek.getKeyProfile("kek");
		HsmkeyConfig kekConfig = hsmKeyConfigManager.getByKeyProfileVendor(kekProfile, KeyouCryptoService.VENDOR_NAME);
		kek.setKeyIndex(kekConfig.getIndex());
		kek.setKeyVersion(kekConfig.getVersion());

		KeyProfile dekProfile = dek.getKeyProfile("dek");
		HsmkeyConfig dekConfig = hsmKeyConfigManager.getByKeyProfileVendor(dekProfile, KeyouCryptoService.VENDOR_NAME);
		dek.setKeyIndex(dekConfig.getIndex());
		dek.setKeyVersion(dekConfig.getVersion());

		try {
			return keyouCryptoService.translateKey1ToKey2(ciphertext, kek, dek);
		} catch (CryptoException e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	@Override
	protected byte[] internalDecryptPersoData(byte[] ciphertext, Key tk) {
		ApplicationKeyProfile keyProfile = tk.getKeyProfile("tk");
		HsmkeyConfig hsmConfig = hsmKeyConfigManager.getByKeyProfileVendor(keyProfile, KeyouCryptoService.VENDOR_NAME);

		tk.setKeyIndex(hsmConfig.getIndex());
		tk.setKeyVersion(hsmConfig.getVersion());
		tk.setEncryptedKey(ConvertUtils.hexString2ByteArray(hsmConfig.getCiphertext()));

		try {
			return keyouCryptoService.decrypt(ciphertext, tk);
		} catch (CryptoException e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}
}
