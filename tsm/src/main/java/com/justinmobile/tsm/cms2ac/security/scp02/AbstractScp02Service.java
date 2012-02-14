package com.justinmobile.tsm.cms2ac.security.scp02;

import static com.justinmobile.core.utils.ByteUtils.subArray;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.dto.Dek;
import com.justinmobile.tsm.cms2ac.dto.Key;
import com.justinmobile.tsm.cms2ac.engine.DisperseKeyHelper;
import com.justinmobile.tsm.cms2ac.engine.SecureUtil;
import com.justinmobile.tsm.cms2ac.exception.CryptoException;

public abstract class AbstractScp02Service implements Scp02Service {

	public static final int ZERO_LEVEL_DISPERSE = 0;

	public static final int ONE_LEVEL_DISPERSE = 1;

	public static final int TWO_LEVEL_DISPERSE = 2;

	public static final int THREE_LEVEL_DISPERSE = 3;

	public int disperseLevel = ZERO_LEVEL_DISPERSE;

	protected DisperseKeyHelper disperseKeyHelper;

	public byte[] computeMac(byte[] macSource, byte[] initVector, Cms2acParam cms2acParam, boolean isCmac) {
		try {
			byte[] paddedMacSource = SecureUtil.scp02Padding(macSource);

			byte[] scp02TripleDisperseFactor = getScp02FullDisperseFactor(cms2acParam, KeyProfile.SCP02_MAC_TYPE);
			byte[] sessionSalt = disperseKeyHelper.getScp02MacSessionSalt(cms2acParam, isCmac);
			return internalComputeMac(paddedMacSource, initVector, cms2acParam, scp02TripleDisperseFactor, sessionSalt);
		} catch (CryptoException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected abstract byte[] internalComputeMac(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam,
			byte[] fullDisperseFactor, byte[] sessionSalt) throws CryptoException;

	public byte[] computeMacNextIcv(byte[] macSource, byte[] initVector, Cms2acParam cms2acParam, boolean isCmac) {
		try {
			byte[] paddedMacSource = SecureUtil.scp02Padding(macSource);

			byte[] scp02TripleDisperseFactor = getScp02FullDisperseFactor(cms2acParam, KeyProfile.SCP02_MAC_TYPE);
			byte[] sessionSalt = disperseKeyHelper.getScp02MacSessionSalt(cms2acParam, isCmac);
			return internalComputeMacNextIcv(paddedMacSource, initVector, cms2acParam, scp02TripleDisperseFactor, sessionSalt);
		} catch (CryptoException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	protected abstract byte[] internalComputeMacNextIcv(byte[] paddedMacSource, byte[] initVector, Cms2acParam cms2acParam,
			byte[] fullDisperseFactor, byte[] sessionSalt) throws CryptoException;

	public byte[] encryptData(byte[] encSource, Cms2acParam cms2acParam) {
		try {
			byte[] initVector = ConvertUtils.hexString2ByteArray("0000000000000000");
			byte[] paddedEncSource = SecureUtil.scp02Padding(encSource);

			byte[] scp02TripleDisperseFactor = getScp02FullDisperseFactor(cms2acParam, KeyProfile.SCP02_ENC_TYPE);
			byte[] sessionSalt = disperseKeyHelper.getScp02EncSessionSalt(cms2acParam);
			return internalEncryptData(paddedEncSource, initVector, cms2acParam, scp02TripleDisperseFactor, sessionSalt);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	protected abstract byte[] internalEncryptData(byte[] paddedEncSource, byte[] initVector, Cms2acParam cms2acParam,
			byte[] fullDisperseFactor, byte[] sessionSalt) throws CryptoException;

	public byte[] encryptKey(byte[] encSource, Cms2acParam cms2acParam) {
		try {
			byte[] scp02TripleDisperseFactor = getScp02FullDisperseFactor(cms2acParam, KeyProfile.SCP02_DEK_TYPE);
			byte[] sessionSalt = null;
			if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
				sessionSalt = disperseKeyHelper.getScp02DekSessionSalt(cms2acParam);
			} else {
				sessionSalt = new byte[0];
			}
			return internalEncryptKey(encSource, cms2acParam, scp02TripleDisperseFactor, sessionSalt);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	protected abstract byte[] internalEncryptKey(byte[] encSource, Cms2acParam cms2acParam, byte[] fullDisperseFactor, byte[] sessionSalt)
			throws CryptoException;

	// public byte[] decrypt(byte[] decryptSource, Cms2acParam cms2acParam) {
	// return null;
	// }

	public byte[] exportKey(KeyProfile keyProfile, Cms2acParam cms2acParam) {
		try {
			byte[] scp02FullDisperseFactor = getScp02FullDisperseFactor(cms2acParam, keyProfile.getType());
			return internalExportKey(keyProfile, scp02FullDisperseFactor);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	public byte[] exportKey(KeyProfile keyProfile, String mobileNo) {
		try {
			byte[] scp02FullDisperseFactor = getScp02FullDisperseFactor(keyProfile, mobileNo);
			return internalExportKey(keyProfile, scp02FullDisperseFactor);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	public byte[] acquireToken(byte[] src, Cms2acParam cms2acParam) {
		try {
			return internalAcquireToken(src, cms2acParam);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	protected abstract byte[] internalAcquireToken(byte[] src, Cms2acParam cms2acParam) throws CryptoException;

	private byte[] getScp02FullDisperseFactor(KeyProfile keyProfile, String mobileNo) {
		byte[] fullDisperseFactor = new byte[0];
		if (this.disperseLevel != ZERO_LEVEL_DISPERSE) {
			fullDisperseFactor = disperseKeyHelper.buildScp02FullDisperseFactor(keyProfile, mobileNo);
		}
		return fullDisperseFactor;
	}

	protected abstract byte[] internalExportKey(KeyProfile keyProfile, byte[] scp02FullDisperseFactor) throws CryptoException;

	protected byte[] getScp02FullDisperseFactor(Cms2acParam cms2acParam, int keyType) {
		byte[] fullDisperseFactor = new byte[0];
		if (this.disperseLevel != ZERO_LEVEL_DISPERSE) {
			fullDisperseFactor = disperseKeyHelper.buildScp02FullDisperseFactor(cms2acParam, keyType);
		}
		return fullDisperseFactor;
	}

	// protected byte[] getScp80FullDisperseFactor(Cms2acParam cms2acParam, int
	// keyType) {
	// byte[] fullDisperseFactor = new byte[0];
	// if (this.disperseLevel != ZERO_LEVEL_DISPERSE) {
	// fullDisperseFactor =
	// disperseKeyHelper.buildScp80FullDisperseFactor(cms2acParam, keyType);
	// }
	// return fullDisperseFactor;
	// }

	protected byte[] getFirstDisperseFactor(byte[] fullDisperseFactor) {
		return subArray(fullDisperseFactor, 0, 16);
	}

	protected byte[] getSecondDisperseFactor(byte[] fullDisperseFactor) {
		return subArray(fullDisperseFactor, 16, 32);
	}

	protected byte[] getThirdDisperseFactor(byte[] tripleDisperseFactor) {
		return subArray(tripleDisperseFactor, 32, 48);
	}

	protected byte[] getSingleDisperseFactor(byte[] tripleDisperseFactor) {
		return subArray(tripleDisperseFactor, 32, 48);
	}

	protected byte[] getDoubleDisperseFactor(byte[] tripleDisperseFactor) {
		return subArray(tripleDisperseFactor, 16, 48);
	}

	protected byte[] getDisperseFactorByDisperseLevel(byte[] tripleDisperseFactor) {
		if (this.disperseLevel == ZERO_LEVEL_DISPERSE) {
			return new byte[0];
		} else if (this.disperseLevel == ONE_LEVEL_DISPERSE) {
			return getSingleDisperseFactor(tripleDisperseFactor);
		} else if (this.disperseLevel == TWO_LEVEL_DISPERSE) {
			return getDoubleDisperseFactor(tripleDisperseFactor);
		} else {
			return tripleDisperseFactor;
		}
	}

	public byte[] exportEncKeyAndCheckValue(KeyProfile keyProfile, Cms2acParam cms2acParam) {
		try {
			byte[] disperseFactor = getScp02FullDisperseFactor(cms2acParam, keyProfile.getType());

			byte[] sessionSalt = null;
			if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
				sessionSalt = disperseKeyHelper.getScp02DekSessionSalt(cms2acParam);
			} else {
				sessionSalt = new byte[0];
			}

			return internalExportEncKeyAndCheckValue(cms2acParam, keyProfile, disperseFactor, disperseFactor, sessionSalt);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.ENCRYPT_MODULE_ERROR, e);
		}
	}

	protected abstract byte[] internalExportEncKeyAndCheckValue(Cms2acParam cms2acParam, KeyProfile keyProfile,
			byte[] exportDisperseFactor, byte[] encryptDisperseFactor, byte[] sessionSalt);

	public void setDisperseKeyHelper(DisperseKeyHelper disperseKeyHelper) {
		this.disperseKeyHelper = disperseKeyHelper;
	}

	public void setDisperseLevel(int disperseLevel) {
		this.disperseLevel = disperseLevel;
	}

	@Override
	public byte[] decryptPersoData(byte[] ciphertext, Application application) {
		Key tkKey = new Key();
		tkKey.addKeyProfile("tk", application.getTk());
		tkKey.setAlgorithm(application.getPersoCmdTransferSecureAlgorithm());

		return internalDecryptPersoData(ciphertext, tkKey);

	}

	protected abstract byte[] internalDecryptPersoData(byte[] ciphertext, Key tk);

	@Override
	public byte[] transformEncrypt(byte[] ciphertext, Application application, Cms2acParam cms2acParam) {
		Key dekKey = new Dek();
		dekKey.setDisperseFactor(getScp02FullDisperseFactor(cms2acParam, KeyProfile.SCP02_DEK_TYPE));
		dekKey.setSessionSalt(disperseKeyHelper.getScp02DekSessionSalt(cms2acParam));
		dekKey.addKeyProfile("dek", cms2acParam.getCurrentSecurityDomain().getDekKey());

		Key kekKey = new Key();
		kekKey.addKeyProfile("kek", application.getKek());
		kekKey.setAlgorithm(application.getPersoCmdSensitiveDataSecureAlgorithm());

		return internalTransformEncrypt(ciphertext, kekKey, dekKey);
	}

	protected abstract byte[] internalTransformEncrypt(byte[] ciphertext, Key kek, Key dek);
}
