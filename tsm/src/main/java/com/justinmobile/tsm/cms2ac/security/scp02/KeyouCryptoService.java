package com.justinmobile.tsm.cms2ac.security.scp02;

import org.springframework.stereotype.Service;

import cn.keyou.otaHsm.OTAAPI;

import com.em.EMClient;
import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.dto.Key;
import com.justinmobile.tsm.cms2ac.exception.CryptoException;

@Service("keyouCryptoService")
public class KeyouCryptoService extends HsmCryptoService {

	public static final String VENDOR_NAME = EncryptorVendor.KEYOU.getValue();

	// private static final int KEY_ID = 0;// key
	// id，generateMAC和generateCMAC会用到，作用不明

	@Override
	protected byte[] internalComputeMac(byte[] paddedMacSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex, int alg)
			throws CryptoException {
		if (paddedMacSource == null || paddedMacSource.length % 8 != 0) {
			throw new CryptoException("error paddedMacSource length");
		}

		EMClient client = getEMClient();
		// OTAAPI api = getOtaApi();

		int divNum = parseDivNum(fullDisperseFactor);
		int sessionKeyFlag = parseSessionKeyFlag(sessionSalt);

		byte[] mac = new byte[8];

		try {
			int hsmResult;
			// hsmResult = api.HsmGenerateMAC(hsmKeyIndex,
			// Constants.CARD_SD_KEY_VERSION, alg, PADDING_EXTERNAL, divNum,
			// fullDisperseFactor,
			// sessionKeyFlag, sessionSalt, paddedMacSource.length,
			// paddedMacSource, MAC_EIGHT_BYTES, mac);
			hsmResult = client.HsmGenerateMAC(// 参数列表
					0// KeyId
					, Constants.CARD_SD_KEY_VERSION// KeyVersion
					, hsmKeyIndex// KeyIndex
					, alg // AlgFlag
					, PADDING_EXTERNAL// PadFlag
					, divNum// DivNum
					, fullDisperseFactor// DivData
					, sessionKeyFlag// SessionKeyFlag
					, sessionSalt// SkeySeed
					, paddedMacSource.length// DataLen
					, paddedMacSource// Data
					, MAC_EIGHT_BYTES// MACDataLen
					, mac// MACData
					);
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
	public byte[] retailTripleDesCbc2KeyMacNextIcv(byte[] paddedMacSource, byte[] initVector, byte[] fullDisperseFactor,
			byte[] sessionSalt, int hsmKeyIndex) throws CryptoException {
		EMClient client = getEMClient();
		// OTAAPI api = getOtaApi();

		int divNum = parseDivNum(fullDisperseFactor);
		int sessionKeyFlag = parseSessionKeyFlag(sessionSalt);
		byte[] mac = new byte[8];
		byte[] nextIcv = new byte[8];
		try {
			int hsmResult;
			// hsmResult = api.HsmGenerateCMAC(hsmKeyIndex,
			// Constants.CARD_SD_KEY_VERSION, ALG_MAC_3DES_CBC,
			// PADDING_EXTERNAL, divNum,
			// fullDisperseFactor, sessionKeyFlag, sessionSalt, initVector,
			// paddedMacSource.length, paddedMacSource, MAC_EIGHT_BYTES,
			// mac, nextIcv);
			hsmResult = client.HsmGenerateCMAC(// 参数列表
					0// KeyID
					, Constants.CARD_SD_KEY_VERSION, hsmKeyIndex// KeyIndex
					, ALG_MAC_3DES_CBC// AlgFlag
					, PADDING_EXTERNAL// PadFlag
					, divNum// DivNum
					, fullDisperseFactor// DivData
					, sessionKeyFlag// SessionKeyFlag
					, sessionSalt// SkeySeed
					, initVector// IcvData
					, paddedMacSource.length// DataLen
					, paddedMacSource// Data
					, MAC_EIGHT_BYTES// MACDataLen
					, mac// MACData
					, nextIcv// ICVResult
					);

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
	protected byte[] hsmEncryptDecrypt(byte[] paddedSource, byte[] fullDisperseFactor, byte[] sessionSalt, int hsmKeyIndex, int alg,
			int operate) throws CryptoException {
		try {
			EMClient client = getEMClient();
			// OTAAPI api = getOtaApi();

			int divNum = parseDivNum(fullDisperseFactor);
			int sessionKeyFlag = parseSessionKeyFlag(sessionSalt);
			int[] cipheredDataLength = new int[1];
			byte[] cipheredData = new byte[paddedSource.length];

			int hsmResult;

			// hsmResult =
			// api.HsmDataEncryptOrDecrypt(Constants.CARD_SD_KEY_VERSION,
			// hsmKeyIndex, alg, operate, PADDING_EXTERNAL, divNum,
			// fullDisperseFactor, sessionKeyFlag, sessionSalt,
			// paddedSource.length, paddedSource, cipheredDataLength,
			// cipheredData);

			// hsmResult = client.HsmDataEncryptOrDecrypt(// 参数列表
			// 0// KeyID
			// , Constants.CARD_SD_KEY_VERSION// KeyVer
			// , hsmKeyIndex// KeyIndex
			// , alg// AlgFlag
			// , PADDING_EXTERNAL// PadFlag
			// , divNum// DivNum
			// , fullDisperseFactor// DivData
			// , sessionKeyFlag// SessionKeyFlag
			// , sessionSalt// SkeySeed
			// , paddedSource.length// DataLen
			// , paddedSource// Data
			// , cipheredDataLength// CipheredDataLen
			// , cipheredData// CipheredData
			// );

			hsmResult = client.HsmDataEncrypt(Constants.CARD_SD_KEY_VERSION// KeyVer
					, hsmKeyIndex// KeyIndex
					, alg// AlgFlag
					, PADDING_EXTERNAL// PadFlag
					, OPERATE_ENCRYPT// OperateFlag
					, divNum// DivNum
					, fullDisperseFactor// DivData
					, sessionKeyFlag// SessionKeyFlag
					, sessionSalt// SkeySeed
					, paddedSource.length// DataLen
					, paddedSource// Data
					, cipheredDataLength// CipheredDataLen
					, cipheredData// CipheredData
					);
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
			EMClient client = getEMClient();
			// OTAAPI api = getOtaApi();

			int divNum = parseDivNum(fullDisperseFactor);
			int[] cardSdKeyLength = new int[1];
			byte[] cardSdKey = new byte[16];
			int hsmResult;
			// hsmResult =
			// api.GenerateAndExportSDKey(Constants.CARD_SD_KEY_VERSION,
			// hsmKeyIndex, ALG_ENC_3DES_ECB, divNum,
			// fullDisperseFactor, cardSdKeyLength, cardSdKey);

			hsmResult = client.GenerateAndExportSDKey(// 参数列表
					Constants.CARD_SD_KEY_VERSION// KeyVer
					, hsmKeyIndex// KeyIndex
					, ALG_ENC_3DES_ECB// AlgFlag
					, divNum// DivNum
					, fullDisperseFactor// DivData
					, cardSdKeyLength// KeysLen
					, cardSdKey// Keys
					);
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
	// TODO 未更改，相应的接口在EMClient中不存在
	public byte[] generateCheckValue(byte[] keyValue) throws CryptoException {
		try {
			OTAAPI otaApi = getOtaApi();
			// EMClient client = getEMClient();

			byte[] checkValue = new byte[8];
			int hsmResult = otaApi.HSMGenerateCheckValue(keyValue, checkValue);
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
			EMClient client = getEMClient();
			// OTAAPI otaApi = getOtaApi();

			byte[] random = new byte[randomLength];
			int hsmResult;
			// hsmResult = otaApi.GenarateRandom(randomLength, random);

			hsmResult = client.HsmGenarateRandom(// 参数列表
					randomLength// RandomLen
					, random// Rand
					);

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

	public byte[] internalAcquireToken(byte[] src, int hsmIndex) throws CryptoException {
		// try {
		// // EMClient client = getEMClient();
		//
		// byte[] signature = new byte[128];
		// int hsmResult = client.HsmGenSignature(hsmIndex, ALG_SHA1,
		// src.length, src, new int[] { signature.length },
		// signature);
		// if (hsmResult == 0) {
		// return signature;
		// } else {
		// throw new CryptoException("hsm error " + hsmResult);
		// }
		// } catch (CryptoException ce) {
		// throw ce;
		// } catch (Exception e) {
		// throw new IllegalStateException(e);
		// }
		return null;

	}

	byte[] exportKeyAndCheckValue(int encryptKeyIndex, byte[] encryptDisperseFactor, int exportKeIndex, byte[] exportDisperseFactor,
			byte[] sessionSalt) throws CryptoException {
		EMClient client = getEMClient();

		int sessionKeyFlag = parseSessionKeyFlag(sessionSalt);

		int encryptDivNum = parseDivNum(encryptDisperseFactor);
		byte[] keyID = new byte[] { Byte.parseByte(Integer.toString(1)) };
		byte[] keyVersion = new byte[] { (byte) 1 };

		int exportDivNum = parseDivNum(exportDisperseFactor);
		int[] exportKeyIndex = new int[] { exportKeIndex };

		try {
			byte[] keyValue = new byte[16];
			byte[] checkValue = new byte[8];

			int hsmResult = client.HsmGenerateMulKeyAndCheck(// 参数列表
					ALG_ENC_3DES_CBC,// 分散算法标识
					sessionKeyFlag,// 过程密钥标识
					0,// 保护密钥标识
					0,// 保护密钥版本
					encryptKeyIndex,// 保护密钥索引
					encryptDivNum,// 保护密钥索引
					encryptDisperseFactor,// 保护密钥分散因子
					sessionSalt,// 随机因子，如果过程密钥标识为0，忽略该字段
					1,// 本指令请求的密钥个数，1-50
					keyID,// 密钥标识
					keyVersion,// 密钥版本
					exportKeyIndex,// 密钥索引
					exportDivNum,// 密钥分散次数
					exportDisperseFactor,// 密钥分散因子
					keyValue,// 密钥密文
					checkValue// 密钥校验值
					);
			if (hsmResult == 0) {
				return ByteUtils.contactArray(keyValue, ByteUtils.subArray(checkValue, 0, 3));
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
		return OTAAPI.getInstance();
	}

	private EMClient getEMClient() {
		return EMClient.getInstance("config/emconfig.properties");
	}

	public byte[] decrypt(byte[] ciphertext, Key key) {
		try {
			EMClient client = getEMClient();

			int[] outputDataLength = new int[1];
			byte[] outputData = new byte[ciphertext.length];

			int hsmResult;

			hsmResult = client.HsmDataDecrypt(// 参数列表
					key.getEncryptedKey().length// KeyLen
					, key.getEncryptedKey()// Key
					, key.getAlgorithm().getValue()// AlgFlag
					, OPERATE_DECRYPT// OperateFlag
					, PADDING_EXTERNAL// PadFlag
					, parseDivNum(key.getDisperseFactor())// DivNum
					, key.getDisperseFactor()// DivData
					, parseSessionKeyFlag(key.getSessionSalt())// SessionKeyFlag
					, key.getSessionSalt()// SkeySeed
					, ciphertext.length// DataLen
					, ciphertext// Data
					, outputDataLength// OutputDataLength
					, outputData// OutputData
					);
			if (hsmResult == 0) {
				return outputData;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public byte[] encrypt(byte[] plaintext, Key key) {
		try {
			EMClient client = getEMClient();

			int[] outputDataLength = new int[1];
			byte[] outputData = new byte[plaintext.length];

			int hsmResult;

			hsmResult = client.HsmDataEncrypt(// 参数列表
					key.getKeyVersion()// KeyVer
					, key.getKeyIndex()// KeyIndex
					, key.getAlgorithm().getValue()// AlgFlag
					, PADDING_EXTERNAL// PadFlag
					, OPERATE_ENCRYPT// OperateFlag
					, parseDivNum(key.getDisperseFactor())// DivNum
					, key.getDisperseFactor()// DivData
					, parseSessionKeyFlag(key.getSessionSalt())// SessionKeyFlag
					, key.getSessionSalt()// SkeySeed
					, plaintext.length// DataLen
					, plaintext// Data
					, outputDataLength// CipheredDataLen
					, outputData// CipheredData
					);

			if (hsmResult == 0) {
				return outputData;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public byte[] translateKey1ToKey2(byte[] ciphertext, Key key1, Key key2) {
		try {
			EMClient client = getEMClient();

			int[] outputDataLength = new int[1];
			byte[] outputData = new byte[ciphertext.length];

			int hsmResult = client.HsmTranslateKey1ToKey2(key1.getKeyId()// Key1ID
					, key1.getKeyVersion()// Key1Ver
					, key1.getKeyIndex()// Key1Index
					, key1.getAlgorithm().getValue()// AlgFlag
					, PADDING_EXTERNAL// Pad1Flag
					, parseDivNum(key1.getDisperseFactor())// Div1Num
					, key1.getDisperseFactor()// Div1Data
					, parseSessionKeyFlag(key1.getSessionSalt())// SessionKey1Flag
					, key1.getSessionSalt()// Skey1Seed
					, key2.getKeyId()// Key2ID
					, key2.getKeyVersion()// Key2Ver
					, key2.getKeyIndex()// Key2Index
					, key2.getAlgorithm().getValue()// Key2AlgFlag
					, parseDivNum(key2.getDisperseFactor())// Div2Num
					, key2.getDisperseFactor()// Div2Data
					, parseSessionKeyFlag(key2.getSessionSalt())// Session2KeyFlag
					, key2.getSessionSalt()// Skey2Seed
					, ciphertext.length// inDataLen
					, ciphertext// bInDataByKey1
					, outputDataLength// OutDataLen
					, outputData// bOutDataByKey2
					);

			if (hsmResult == 0) {
				return outputData;
			} else {
				throw new CryptoException("hsm error " + hsmResult);
			}
		} catch (CryptoException ce) {
			throw ce;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
