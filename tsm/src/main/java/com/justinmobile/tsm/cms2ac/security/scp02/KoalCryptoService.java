package com.justinmobile.tsm.cms2ac.security.scp02;

import org.springframework.stereotype.Service;

import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.exception.CryptoException;
import com.koal.otaHsm.OTAAPI;
import com.koal.otaHsm.OTAAPIImpl;

@Service("koalCryptoService")
public class KoalCryptoService extends HsmCryptoService {

	public static final String VENDOR_NAME = "Koal";

	@Override
	protected byte[] internalComputeMac(byte[] paddedMacSource, byte[] fullDisperseFactor, byte[] sessionSalt,
			int hsmKeyIndex, int alg) throws CryptoException {
		if (paddedMacSource == null || paddedMacSource.length % 8 != 0) {
			throw new CryptoException("error paddedMacSource length");
		}

		OTAAPI otaApi = getOtaApi();

		int divNum = parseDivNum(fullDisperseFactor);
		int sessionKeyFlag = parseSessionKeyFlag(sessionSalt);
		byte[] mac = new byte[8];
		try {
			int hsmResult = otaApi.HsmGenerateMAC(hsmKeyIndex, Constants.CARD_SD_KEY_VERSION, alg, PADDING_EXTERNAL,
					divNum, fullDisperseFactor, sessionKeyFlag, sessionSalt, paddedMacSource.length, paddedMacSource,
					MAC_EIGHT_BYTES, mac);
			if (hsmResult == 0) {
				return mac;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	@Override
	public byte[] retailTripleDesCbc2KeyMacNextIcv(byte[] paddedMacSource, byte[] initVector,
			byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex) throws CryptoException {
		OTAAPI otaApi = getOtaApi();

		int divNum = parseDivNum(fullDisperseFactor);
		int sessionKeyFlag = parseSessionKeyFlag(sessionSalt);
		byte[] mac = new byte[8];
		byte[] nextIcv = new byte[8];
		try {
			int hsmResult = otaApi.HsmGenerateCMAC(hsmKeyIndex, Constants.CARD_SD_KEY_VERSION, ALG_MAC_3DES_CBC,
					PADDING_EXTERNAL, divNum, fullDisperseFactor, sessionKeyFlag, sessionSalt, initVector,
					paddedMacSource.length, paddedMacSource, MAC_EIGHT_BYTES, mac, nextIcv);
			if (hsmResult == 0) {
				return ByteUtils.contactArray(mac, nextIcv);
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected byte[] hsmEncryptDecrypt(byte[] paddedSource, byte[] fullDisperseFactor, byte[] sessionSalt,
			int hsmKeyIndex, int alg, int operate) throws CryptoException {
		try {
			OTAAPI otaApi = getOtaApi();

			int divNum = parseDivNum(fullDisperseFactor);
			int sessionKeyFlag = parseSessionKeyFlag(sessionSalt);
			int[] cipheredDataLength = new int[1];
			byte[] cipheredData = new byte[paddedSource.length];

			int hsmResult = otaApi.HsmDataEncryptOrDecrypt(Constants.CARD_SD_KEY_VERSION, hsmKeyIndex, alg, operate,
					PADDING_EXTERNAL, divNum, fullDisperseFactor, sessionKeyFlag, sessionSalt, paddedSource.length,
					paddedSource, cipheredDataLength, cipheredData);
			if (hsmResult == 0) {
				return cipheredData;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public byte[] exportKey(byte[] fullDisperseFactor, int hsmKeyIndex) throws CryptoException {
		try {
			OTAAPI otaApi = getOtaApi();

			int divNum = parseDivNum(fullDisperseFactor);
			int[] cardSdKeyLength = new int[1];
			byte[] cardSdKey = new byte[16];
			int hsmResult = otaApi.GenerateAndExportSDKey(Constants.CARD_SD_KEY_VERSION, hsmKeyIndex, ALG_ENC_3DES_ECB,
					divNum, fullDisperseFactor, cardSdKeyLength, cardSdKey);
			if (hsmResult == 0) {
				return cardSdKey;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public byte[] generateCheckValue(byte[] keyValue) throws CryptoException {
		try {
			OTAAPI otaApi = getOtaApi();

			byte[] checkValue = new byte[8];
			int hsmResult = otaApi.HsmGenerateCheckValue(keyValue, checkValue);
			if (hsmResult == 0) {
				return checkValue;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public byte[] generateRandom(int randomLength) throws CryptoException {
		try {
			OTAAPI otaApi = getOtaApi();

			byte[] random = new byte[randomLength];
			int hsmResult = otaApi.GenerateRandom(randomLength, random);
			if (hsmResult == 0) {
				return random;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private OTAAPI getOtaApi() {
		try {
			return new OTAAPIImpl();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
