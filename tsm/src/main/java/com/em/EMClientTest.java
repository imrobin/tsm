//package com.watchdata.newOtahsm;

//import com.watchdata.nfcota.util.RsaUtil;
package com.em;

/*
 * author: 		guizy 
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */
import com.em.EMClient;
import com.em.EMResult;
import com.em.UnionUtil;

//import com.em.DoubleDes;
/*
 * author: 		guizy
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */

//import com.em.DoubleDes;

public class EMClientTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String sMac = "";
		EMClient hsm = EMClient.getInstance("config/emconfig.properties");
		EMResult result = new EMResult();

		// OTA用API()
		// GenarateRandom(hsm);
		// testgetOTAHsmGenerateMAC(hsm);
		// testgetOTAHsmVerifyMAC(hsm);
		// testgetOTAHsmGenerateCMAC(hsm);
		// testgetOTAHsmDataEncryptOrDecrypt(hsm);
		// testHsmGenRSAPair(hsm, result);
		// testHsmGenSignature(hsm,result);
		// testHsmVerifySignature(hsm,result);
		//testGenerateMulKeyAndCheck(hsm);
		// testHsmExportPK(hsm);
		//testHsmDataEncryp(hsm);
		testHsmDataDecrypt(hsm);
		//testHsmTranslateKey1ToKey2(hsm);
		
		hsm = null;
		result = null;
	}

	// 4.1. 产生随机数接口 //
	public static void GenarateRandom(EMClient hsm) {
		byte[] Keys = new byte[1024];
		try {
			int ret = hsm.HsmGenarateRandom(2, Keys);
			if (ret == 0) {
				System.out.println("Keys=[" + new String(Keys, "ISO-8859-1")
						+ "]");
				System.out.println("KeysHexStr=["
						+ UnionUtil.Bytes2HexString(Keys) + "]");
				System.out.println("Test GenarateRandom OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test GenarateRandom false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.2. 分散导出多个密钥密文并计算校验值//
	public static void testGenerateMulKeyAndCheck(EMClient hsm) {

		int AlgFlag = 0x82;
		int SessionKeyFlag = 0;
		int EncKeyID = 0;
		int EncKeyVer = 0;
		int EncKeyIndex = 0x17;
		int EncKeyDvsNum = 1;
		String EncKeyDvsData = "1111111111111111";
		String Seed = "2222222222222222";

		int KeyNum = 3;

		byte[] MulKeyID = new byte[0];
		byte[] MulKeyVer = new byte[0];
		// 导出0x17,0x18,0x20密钥
		// byte[] MulKeyIndex={0x17,0x18,0x20};
		int[] MulKeyIndex = { 0x17, 0x18, 0x20 };
		int KeyDvsNum = 1;
		// 0x17的分散因子是：AAAAAAAAAAAAAAAA
		// 0x18的分散因子是：BBBBBBBBBBBBBBBB
		// 0x20的分散因子是：CCCCCCCCCCCCCCCC
		byte[] KeyDvsData = "AAAAAAAAAAAAAAAABBBBBBBBBBBBBBBBCCCCCCCCCCCCCCCC"
				.getBytes();
		// String[] KeyDvsData =
		// {"AAAAAAAAAAAAAAAA","BBBBBBBBBBBBBBBB","CCCCCCCCCCCCCCCC"};
		byte[] EncKeyDvsData2 = UnionUtil.hex2byte(EncKeyDvsData);
		byte[] Seed2 = UnionUtil.hex2byte(Seed);
		// byte [] KeyDvsData2 = UnionUtil.hex2byte(KeyDvsData);

		/*
		 * String[] MulKeys = new String[KeyNum]; String[] MulCheckValues = new
		 * String[KeyNum];
		 */

		byte[] MulKeys = new byte[32 * 3];
		byte[] MulCheckValues = new byte[16  * 3];
		
		try {
			// int ret=hsm.HsmGenerateMulKeyAndCheck(AlgFlag,SessionKeyFlag,
			// EncKeyID,EncKeyVer, EncKeyIndex, EncKeyDvsNum, EncKeyDvsData2,
			// Seed2, KeyNum,MulKeyID, MulKeyVer, MulKeyIndex, KeyDvsNum,
			// KeyDvsData2, MulKeys, MulCheckValue);
			int ret = hsm
					.HsmGenerateMulKeyAndCheck(AlgFlag, SessionKeyFlag,
							EncKeyID, EncKeyVer, EncKeyIndex, EncKeyDvsNum,
							EncKeyDvsData2, Seed2, KeyNum, MulKeyID, MulKeyVer,
							MulKeyIndex, KeyDvsNum, KeyDvsData, MulKeys,
							MulCheckValues);
			if (ret == 0) {
				/*
				 * byte[] aa=new byte[32+1]; System.arraycopy(MulKeys, 0, aa, 0,
				 * 32); System.out.println("MulKeys="+new String(aa));
				 * 
				 * byte[] bb=new byte[16+1]; System.arraycopy(MulCheckValue, 0,
				 * bb, 0, 16); System.out.println("MulKeys="+new String(bb));
				 */
				System.out.println("Test testGenerateMulKeyAndCheck OK!");
				/*
				 * for(int i=0;i<KeyNum;i++){
				 * System.out.println("MulKeys="+MulKeys[i]+
				 * "     "+"MulCheckValues ="+MulCheckValues[i]); }
				 */
				System.out.println("MulKeys =" + new String(MulKeys)
						+ "\nMulCheckValues = " + new String(MulCheckValues));
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test testGenerateMulKeyAndCheck false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.3. 数据加密接口//
	public static void testgetOTAHsmDataEncryptOrDecrypt(EMClient hsm) {
		int KeyID = 0;
		int KeyVer = 0;
		int KeyIndex = 0x21;
		int AlgFlag = 0x82;
		int OperateFlag = 0;
		int PadFlag = 0;
		int DivNum = 0;
		int[] CipheredDataLen = new int[1];
		byte[] CipheredData = new byte[1024];
		String DivData = "11111111111111112222222222222222";
		int SessionKeyFlag = 1;
		String SkeySeed = "33333333333333334444444444444444";
		int DataLen = 16;
		String Data = "09A0000000038698";

		byte[] DivData2 = UnionUtil.hex2byte(DivData);
		byte[] SkeySeed2 = UnionUtil.hex2byte(SkeySeed);
		byte[] Data2 = UnionUtil.hex2byte(Data);

		try {
			int ret = hsm.HsmDataEncryptOrDecrypt(KeyID, KeyVer, KeyIndex,
					AlgFlag, PadFlag, DivNum, DivData2, SessionKeyFlag,
					SkeySeed2, DataLen, Data2, CipheredDataLen, CipheredData);
			if (ret == 0) {
				byte[] cc = new byte[CipheredDataLen[0]];
				System.out.println("CipheredDataLen=" + CipheredDataLen[0]);
				System.arraycopy(CipheredData, 0, cc, 0, CipheredDataLen[0]);
				System.out.println("CipheredData=" + new String(cc));
				System.out.println("Test getOTAHsmDataEncryptOrDecrypt OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test getOTAHsmDataEncryptOrDecrypt false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.4. 产生MAC接口//
	public static void testgetOTAHsmGenerateMAC(EMClient hsm) {

		int KeyID = 0;
		int KeyVer = 0;
		int KeyIndex = 0x21;
		int AlgFlag = 0x82;
		int PadFlag = 1;
		int DivNum = 1;
		String DivData = "11111111111111112222222222222222";
		int SessionKeyFlag = 0;
		String SkeySeed = "11111111111111112222222222222222";
		int DataLen = 26;
		String Data = "84820000102490814CCB347401";

		byte[] DivData2 = UnionUtil.hex2byte(DivData);
		byte[] SkeySeed2 = UnionUtil.hex2byte(SkeySeed);
		byte[] Data2 = UnionUtil.hex2byte(Data);
		int MACDataLen = 2;
		byte[] MACData = new byte[20];
		try {
			int ret = hsm.HsmGenerateMAC(KeyID, KeyVer, KeyIndex, AlgFlag,
					PadFlag, DivNum, DivData2, SessionKeyFlag, SkeySeed2,
					DataLen, Data2, MACDataLen, MACData);

			if (ret == 0) {
				byte[] cc = new byte[MACDataLen * 8];
				System.out.println("MACDataLen=" + MACDataLen * 8);
				System.arraycopy(MACData, 0, cc, 0, (MACDataLen * 8));
				System.out.println("MACData=" + new String(cc));
				System.out.println("Test getOTAHsmGenerateMAC OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test getOTAHsmGenerateMAC false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.5. 验证MAC接口//
	public static void testgetOTAHsmVerifyMAC(EMClient hsm) {
		int KeyID = 0;
		int KeyVer = 0;
		int KeyIndex = 0x21;
		int AlgFlag = 0x82;
		int PadFlag = 1;
		int DivNum = 1;
		String DivData = "1234567890ABCDEF1234567890ABCDEF";
		int SessionKeyFlag = 0;
		String SkeySeed = "11111111111111112222222222222222";
		int DataLen = 26;
		String Data = "84820000102490814CCB347401";
		int MACDataLen = 2;
		String MACData = "552414947C4955F7";

		byte[] DivData2 = UnionUtil.hex2byte(DivData);
		byte[] SkeySeed2 = UnionUtil.hex2byte(SkeySeed);
		byte[] Data2 = UnionUtil.hex2byte(Data);
		byte[] MACData2 = UnionUtil.hex2byte(MACData);

		try {
			int ret = hsm.HsmVerifyMAC(KeyID, KeyVer, KeyIndex, AlgFlag,
					PadFlag, DivNum, DivData2, SessionKeyFlag, SkeySeed2,
					DataLen, Data2, MACDataLen, MACData2);
			if (ret == 0) {
				System.out.println("Test getOTAHsmVerifyMAC OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test getOTAHsmVerifyMAC false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.6. 产生C-MAC接口//
	public static void testgetOTAHsmGenerateCMAC(EMClient hsm) {
		int KeyID = 0;
		int KeyVer = 0;
		int KeyIndex = 33;
		int AlgFlag = 0x82;
		int PadFlag = 0x00;
		int DivNum = 0;
		String DivData = "0000000000000001FFFFFFFFFFFFFFFE00A000000018434DFF33FFFF89000000490630005008310C0000000000000000";
		int SessionKeyFlag = 0x01;
		String SkeySeed = "01820001000000000000000000000000";
		String IcvData = "0000000000000000";
		int DataLen = 32;
		String Data = "000111D29315803F64913BCB83F41352";
		byte[] data = UnionUtil.hex2byte(Data);
		byte[] DivData2 = UnionUtil.hex2byte(DivData);
		byte[] SkeySeed2 = UnionUtil.hex2byte(SkeySeed);
		byte[] IcvData2 = UnionUtil.hex2byte(IcvData);

		int MACDataLen = 2;
		byte[] MACData = new byte[20];
		byte[] ICVResult = new byte[20];

		try {
			int ret = hsm.HsmGenerateCMAC(KeyID, KeyVer, KeyIndex, AlgFlag,
					PadFlag, DivNum, DivData2, SessionKeyFlag, SkeySeed2,
					IcvData2, DataLen, data, MACDataLen, MACData, ICVResult);
			if (ret == 0) {
				byte[] cc = new byte[MACDataLen * 8];
				byte[] dd = new byte[MACDataLen * 8];
				System.out.println("MACDataLen=" + MACDataLen * 8);
				System.arraycopy(MACData, 0, cc, 0, (MACDataLen * 8));
				System.out.println("MACData=" + new String(cc));
				System.arraycopy(ICVResult, 0, dd, 0, (MACDataLen * 8));
				System.out.println("ICVResult=" + new String(dd));
				System.out.println("Test getOTAHsmGenerateCMAC OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test getOTAHsmGenerateCMAC false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.7. 产生RSA密钥 //
	public static void testHsmGenRSAPair(EMClient hsm, EMResult result) {
		String vkIndex = "20";
		int lenOfVK = 1024;
		try {
			result = hsm.HsmGenerateRSAKey(vkIndex, lenOfVK);
			if (result.getRecode() == 0) {
				System.out.println("PKHexString=["
						+ UnionUtil.Bytes2HexString(result.getPk()) + "]");

				System.out.println("Test testHsmGenRSAPair OK!");
			} else {
				System.out.println("Error code=" + result.getRecode());
				System.out.println("Test testHsmGenRSAPair false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 4.8. 计算签名 //
	public static void testHsmGenSignature(EMClient hsm, EMResult result) {
		int flag = 0x90;
		int vkIndex = 20;
		int dataLen = 16;

		String dataStr = "1234567890ABCDEFFEDCBA0987654321";

		try {
			result = hsm.HsmGenSignature(vkIndex, flag, 16, dataStr.getBytes());
			if (result.getRecode() == 0) {
				System.out
						.println("signature=["
								+ new String(result.getSignature(),
										"ISO-8859-1") + "]");
				System.out.println("signatureHexString=["
						+ UnionUtil.Bytes2HexString(result.getSignature())
						+ "]");
				System.out.println("Test getHsmGenSignature OK!");
			} else {
				System.out.println("Error code=" + result.getRecode());
				System.out.println("Test getHsmGenSignature false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 4.9. 验证签名 //
	public static void testHsmVerifySignature(EMClient hsm, EMResult result) {
		int vkIndex = 20;
		int flag = 0x90;
		int signLen = 0;
		byte[] signature = null;
		int dataLen = 16;
		String dataStr = "1234567890ABCDEFFEDCBA0987654321";
		String signatureStr = "61647349292EAE540F2DDF8D061009AED3762B087D1447AB2812CB143D1B81EF56761293ED133C50AFC7B01829E9563D430206B63CA1870C26913000DB6FCA1E1E01982CA6A9405259F6F1886D37BF6F1B7B7A139C636B38091D5DF01B9D7C2A81D48DD9636612053C924AE578DBBF3865F3402D043BBA8BF5B46111900FAE20";
		signLen = signatureStr.length() / 2;
		signature = UnionUtil.HexString2Bytes(signatureStr);

		try {
			result = hsm.HsmVerifySignature(vkIndex, flag, dataLen, dataStr
					.getBytes(), signLen, signature);
			if (result.getRecode() == 0) {
				System.out.println("Test HsmVerifySignature OK!");
			} else {
				System.out.println("Error code=" + result.getRecode());
				System.out.println("Test HsmVerifySignature false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 4.10. 导出RSA公钥 //
	public static void testHsmExportPK(EMClient hsm) {

		int PKIndex = 20;
		int PKType = 1;
		int EncKeyID = 2;
		int EncKeyVer = 0;
		int EncKeyIndex = 0;
		int PadDataLen = 16;
		String PadData = "3333333333333333";
		byte[] PadData2 = UnionUtil.hex2byte(PadData);

		int[] PKDataLen = new int[10];
		byte[] PKData = new byte[256 + 1];
		int[] PKCheckLen = new int[10];
		byte[] PKCheckValue = new byte[16 + 1];
		try {
			int ret = hsm.HsmExportPK(PKIndex, PKType, EncKeyID, EncKeyVer,
					EncKeyIndex, PadDataLen, PadData2, PKDataLen, PKData,
					PKCheckLen, PKCheckValue);
			if (ret == 0) {
				byte[] aa = new byte[256 + 1];
				byte[] bb = new byte[16 + 1];
				byte[] cc = new byte[PKDataLen[0]];
				byte[] dd = new byte[PKCheckLen[0]];
				System.out.println("PKDataLen=" + PKDataLen[0]);
				System.arraycopy(PKData, 0, aa, 0, PKDataLen[0]);
				System.arraycopy(PKCheckValue, 0, bb, 0, 16);
				System.out.println("PKData=" + new String(aa));
				System.out.println("PKDataHex=["
						+ UnionUtil.Bytes2HexString(aa) + "]");

				System.out.println("PKCheckLen=" + PKCheckLen[0]);
				System.out.println("PKCheckValue=" + new String(bb));
				System.out.println("Test testHsmExportPK OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test testHsmExportPK false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testHsmDataEncryp(EMClient hsm) {
		
		int KeyVer = 0;
		int KeyIndex = 0x21;
		int AlgFlag = 0x82;
		int OperateFlag = 0;
		int PadFlag = 0;
		int DivNum = 1;
		int[] CipheredDataLen = new int[1];
		byte[] CipheredData = new byte[1024];
		String DivData = "11111111111111112222222222222222";
		int SessionKeyFlag = 1;
		String SkeySeed = "33333333333333334444444444444444";
		int DataLen = 16;
		String Data = "09A0000000038698";

		byte[] DivData2 = UnionUtil.hex2byte(DivData);
		byte[] SkeySeed2 = UnionUtil.hex2byte(SkeySeed);
		byte[] Data2 = UnionUtil.hex2byte(Data);

		try {
			int ret = hsm.HsmDataEncrypt(KeyVer, KeyIndex, AlgFlag, OperateFlag, PadFlag, DivNum, DivData2, SessionKeyFlag,
					SkeySeed2, DataLen, Data2, CipheredDataLen, CipheredData);
			if (ret == 0) {
				byte[] cc = new byte[CipheredDataLen[0]];
				System.out.println("CipheredDataLen=" + CipheredDataLen[0]);
				System.arraycopy(CipheredData, 0, cc, 0, CipheredDataLen[0]);
				System.out.println("CipheredData=" + new String(cc));
				System.out.println("Test HsmDataEncrypt OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test HsmDataEncrypt false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testHsmDataDecrypt(EMClient hsm) {

		int KeyLen = 32;
		byte[] mkKey = "6A4F4C591884A4A0ED0CDB3B6AF947A9".getBytes();
		int AlgFlag = 0x82;
		int OperateFlag = 0;
		int PadFlag = 0;
		int DivNum = 1;
		int[] PlainDataLen = new int[1];
		byte[] PlainData = new byte[1024];
		String DivData = "11111111111111112222222222222222";
		int SessionKeyFlag = 1;
		String SkeySeed = "33333333333333334444444444444444";
		int DataLen = 16;
		String Data = "09A0000000038698";

		byte[] DivData2 = UnionUtil.hex2byte(DivData);
		byte[] SkeySeed2 = UnionUtil.hex2byte(SkeySeed);
		byte[] Data2 = UnionUtil.hex2byte(Data);

		try {
			int ret = hsm.HsmDataDecrypt(KeyLen, mkKey, AlgFlag, OperateFlag, PadFlag, DivNum, DivData2, SessionKeyFlag,
					SkeySeed2, DataLen, Data2, PlainDataLen, PlainData);
			if (ret == 0) {
				byte[] cc = new byte[PlainDataLen[0]];
				System.out.println("PlainDataLen=" + PlainDataLen[0]);
				System.arraycopy(PlainData, 0, cc, 0, PlainDataLen[0]);
				System.out.println("PlainData=" + new String(cc));
				System.out.println("Test HsmDataDecrypt OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test HsmDataDecrypt false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testHsmTranslateKey1ToKey2(EMClient hsm) {
		
		int Key1ID = 0;
		int Key1Ver = 0;
		int Key1Index = 0x21;
		int Key1AlgFlag = 0x82;
		int PadFlag = 0;
		int Key1DivNum = 1;
		int[] DataByKey2Len = new int[1];
		byte[] DataByKey2 = new byte[1024];
		String Key1DivData = "11111111111111112222222222222222";
		int SessionKey1Flag = 1;
		String Skey1Seed = "33333333333333334444444444444444";
		int Key2ID = 0;
		int Key2Ver = 0;
		int Key2Index = 0x22;
		int Key2AlgFlag = 0;
		int Key2DivNum = 1;
		String Key2DivData = "11111111111111112222222222222222";
		int SessionKey2Flag = 1;
		String Skey2Seed = "33333333333333334444444444444444";
		int DataByKey1Len = 16;
		String DataByKey1 = "09A0000000038698";

		byte[] bKey1DivData = UnionUtil.hex2byte(Key1DivData);
		byte[] bSkey1Seed = UnionUtil.hex2byte(Skey1Seed);
		byte[] bKey2DivData = UnionUtil.hex2byte(Key2DivData);
		byte[] bSkey2Seed = UnionUtil.hex2byte(Skey2Seed);
		byte[] inDataByKey1 = UnionUtil.hex2byte(DataByKey1);

		try {
			int ret = hsm.HsmTranslateKey1ToKey2(Key1ID, Key1Ver, Key1Index, Key1AlgFlag, PadFlag, Key1DivNum, 
					bKey1DivData, SessionKey1Flag, bSkey2Seed, Key2ID, Key2Ver, Key2Index, Key2AlgFlag, Key2DivNum, bKey2DivData,
					SessionKey2Flag, bSkey2Seed, DataByKey1Len, inDataByKey1, DataByKey2Len, DataByKey2);
			if (ret == 0) {
				byte[] cc = new byte[DataByKey2Len[0]];
				System.out.println("DataByKey2Len=" + DataByKey2Len[0]);
				System.arraycopy(DataByKey2, 0, cc, 0, DataByKey2Len[0]);
				System.out.println("DataByKey2=" + new String(cc));
				System.out.println("Test HsmTranslateKey1ToKey2 OK!");
			} else {
				System.out.println("Error code=" + ret);
				System.out.println("Test HsmTranslateKey1ToKey2 false!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
