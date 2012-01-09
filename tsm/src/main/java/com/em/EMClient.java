package com.em;

/*
 * author: 		guizy
 * Date:		2008-06-24
 * Last Modify:	2008-06-24
 */
import com.em.UnionUtil;
import java.io.File;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.watchdata.util.HexStr;
//import com.watchdata.nfcota.util.RsaUtil;

public class EMClient {
	private static EMClient _instance = null;
	private static Log logger = LogFactory.getLog("EMClient.class");
	private String fn;
	private String HsmHost = "";
	private int HsmPort = 0; // 密码机服务端口
	private int HsmMessaLen = 0; // 密码机消息长度
	private static int messhead = 0; // 消息头内容,自增长
	private int hexMessFlag = 0; // mac数据类型Hex标识
	private final String shapadStr = "3021300906052B0E03021A05000414";
	private final String md5padStr = "3020300C06082A864886F70D020505000410";

	private static final String CONFIG_PATH = "config/log4j.properties";
	private String logpath = CONFIG_PATH;
	private String filename = "";

	public void setLogPath(String sVal) {
		if (sVal == null)
			logpath = CONFIG_PATH;
		else if (sVal.trim() == "")
			logpath = CONFIG_PATH;
		else
			logpath = sVal.trim();
	}

	public String getLogPath() {
		return logpath.trim();
	}

	/**
	 * 
	 * @throws Exception
	 */
	private EMClient() throws Exception {

		// this("c:/share/hsmapi.conf");
		this("config/emconfig.properties");

	}

	/**
	 * 
	 * @param fn
	 * @throws Exception
	 */
	private EMClient(String fn) throws Exception {
		this.fn = fn;
		loadConfig();
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void loadConfig() throws Exception {
		File f = new File(fn);
		if (!f.exists()) {
			throw new Exception("加密机配置文件不存在！fn:" + fn);
		}
		// 读取加密机信息
		GetConf gc = new GetConf(fn);
		logger.error("---------=");
		HsmHost = gc.getValue("hsmip");
		logger.error("HsmHost=" + HsmHost);
		HsmPort = Integer.parseInt(gc.getValue("hsmport"));
		logger.error("port=" + HsmPort);
		if (gc.getValue("hsmmesslen") == null || gc.getValue("hsmmesslen").length() == 0)
			HsmMessaLen = 0;
		else
			HsmMessaLen = Integer.parseInt(gc.getValue("hsmmesslen"));
		logger.error("bbb");
		gc.clear();
	}

	/**
	 * 
	 * @return
	 */
	public static EMClient getInstance(String configuration) {
		if (_instance == null) {
			try {
				_instance = new EMClient(configuration);
			} catch (Exception ex) {
				logger.error("创建加密机实力异常！", ex);
			}
		}
		return _instance;
	}

	/**
	 * 
	 * @param fn
	 * @return
	 * @throws Exception
	 */
	public static EMClient newInstance(String fn) throws Exception {
		if (_instance != null) {
			_instance.release();
			_instance = null;
		}
		_instance = new EMClient(fn);
		return _instance;
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void release() throws Exception {
		// destoty
	}

	public synchronized String genHsmMessageHead() {
		String HsmMessageHead = "";
		long maxVal = 99999999;
		long val = 0;
		if (HsmMessaLen <= 0)
			return "";
		messhead++;
		val = Long.parseLong("1" + UnionUtil.LeftAddZero("0", HsmMessaLen)) - 1;
		if (messhead >= 99999999 || messhead >= val)
			messhead = 0;
		HsmMessageHead += UnionUtil.LeftAddZero(Integer.toString(messhead), HsmMessaLen);
		return HsmMessageHead;
	}

	private String commWithHsm(int cmdLen, String cmdBuff, String CommRet, EMResult result) throws Exception {
		String cmdStr = "";
		String outStr = "";
		String messHead = "";

		result.setRecode(-1);
		if (this.HsmMessaLen < 0)
			this.HsmMessaLen = 0;
		messHead = this.genHsmMessageHead();
		/* 计算长度 */
		logger.info("commWithHsm len[" + (cmdLen + messHead.length()) + "]");
		cmdStr = getPackageLen(cmdLen + messHead.length()) + messHead + cmdBuff;
		/* 数据交换 */
		logger.info("cmdStr=[" + cmdStr + "]");
		outStr = ExchangeData(cmdStr);
		logger.info("outStr=[" + outStr + "]");
		/* 数据检验 */
		// String retcode=this.CheckResult(outStr, CommRet);
		String retcode = this.CheckMessHeadAndResult(outStr, messHead, CommRet);
		if (retcode == null) {
			result.setRecode(-1);
			return null;
		}
		logger.info("commWithHsm retcode=[" + retcode + "]");
		if (retcode.equals("00"))
			result.setRecode(0);
		else {
			if (Integer.parseInt(retcode) == 0) { // 防止非"00"的" 0"等情况
				result.setRecode(-1);
				return null;
			}
			result.setRecode(Integer.parseInt(retcode));
		}
		return outStr.substring(this.HsmMessaLen);
	}

	/********************************* <接口实现Start> **************************************/
	public synchronized EMResult HsmGenerateRSAKey(String vkIndex, int lenOfVK) throws Exception {
		EMResult result = new EMResult();
		result = this.HSM_34(vkIndex, lenOfVK);
		return result;
	}

	public synchronized EMResult HsmGenSignature(int vkIndex, int flag, int dataLen, byte[] data

	) throws Exception {
		EMResult result = new EMResult();

		byte[] digs = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			digs = md.digest(data);
			logger.info("digs=[" + UnionUtil.Bytes2HexString(digs) + "]");
		} catch (Exception e) {
		}
		String dataStr = shapadStr + UnionUtil.Bytes2HexString(digs);
		logger.info("der digs=[" + dataStr + "]");
		byte[] zz = UnionUtil.HexString2Bytes(dataStr);
		dataStr = new String(zz, "ISO-8859-1");
		dataLen = dataStr.length();
		result = this.HSM_37(1, vkIndex, dataLen, dataStr);

		return result;
	}

	public synchronized EMResult HsmVerifySignature(int vkIndex, int flag, int dataLen, byte[] data, int signLen, byte[] signature)
			throws Exception {
		EMResult result = new EMResult();
		EMResult res = new EMResult();
		byte[] tmpPk = new byte[1024 + 1];
		String signatureStr = new String(signature, "ISO-8859-1");

		// 导出的公钥符合DER编码
		res = this.HSM_C7(vkIndex, 2);
		tmpPk = res.getPk();
		// String tmpPkstr = new String(tmpPk);
		// tmpIkstrHex= UnionUtil.Bytes2HexString(tmpPk);

		String pkStr = new String(tmpPk, "ISO-8859-1");
		// sha-1 dataStr

		byte[] digs = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			digs = md.digest(data);
			logger.info("digs=[" + UnionUtil.Bytes2HexString(digs) + "]");
		} catch (Exception e) {
		}
		String dataStr = shapadStr + UnionUtil.Bytes2HexString(digs);
		logger.info("der digs=[" + dataStr + "]");
		byte[] zz = UnionUtil.HexString2Bytes(dataStr);
		dataStr = new String(zz, "ISO-8859-1");
		dataLen = dataStr.length();
		result = this.HSM_38(1, signLen, signatureStr, dataLen, dataStr, pkStr);
		return result;
	}

	public synchronized EMResult getHSMGenerateMAC(int MAK_len, int MAK_type, byte[] MAK_mk, int MAC_len, byte[] MACdata

	) throws Exception {
		EMResult result = new EMResult();
		String sData = new String(MAK_mk);
		if (MACdata.length != MAC_len)
			throw new Exception("getHSMGenerateMAC,数据长度错！");
		byte[] mac8m = UnionUtil.AllRightZreoTo8Multiple(MACdata);
		MAC_len = mac8m.length;
		String sData2 = "";

		if (hexMessFlag == 1) {
			sData2 = UnionUtil.Bytes2HexString(mac8m);
			MAC_len = MAC_len * 2;
		} else
			sData2 = new String(mac8m, "ISO-8859-1");
		if (MAK_len != 2 && MAK_len != 3)
			throw new Exception("getHSMGenerateMAC,密钥长度不在规定范围内");
		int mackeylen = 0;
		if (MAK_len == 2)
			mackeylen = 1;
		else
			mackeylen = 2;
		// result=this.HSM_82(MAK_len,MAK_type,sData,MAC_len,sData2);
		if (MAK_type != 1 && MAK_type != 2 && MAK_type != 3)
			throw new Exception("getHSMGenerateMAC,MAC算法标识不在规定范围内");
		if (MAK_type == 2)
			result = this.HSM_MU(mackeylen, 0, 1, hexMessFlag, sData, "", MAC_len, sData2);
		else if (MAK_type == 3)
			result = this.HSM_MS(mackeylen, 0, 1, hexMessFlag, sData, "", MAC_len, sData2);
		else
			throw new Exception("getHSMGenerateMAC,加密机不支持该MAC算法标识！");
		return result;
	}

	public synchronized EMResult getHSMGenerateVerify(int MAK_len, int MAK_type, byte[] MAK_mk, byte[] Mac, int MAC_len, byte[] MACdata)
			throws Exception {
		EMResult result = new EMResult();
		String sData = new String(MAK_mk);
		// String sData2=new String(MACdata);
		if (MACdata.length != MAC_len)
			throw new Exception("getHSMGenerateVerify,数据长度错！");
		byte[] mac8m = UnionUtil.AllRightZreoTo8Multiple(MACdata);
		MAC_len = mac8m.length;
		String sData2 = "";

		if (hexMessFlag == 1) {
			sData2 = UnionUtil.Bytes2HexString(mac8m);
			MAC_len = MAC_len * 2;
		} else
			sData2 = new String(mac8m, "ISO-8859-1");

		String sData3 = new String(Mac);
		if (MAK_len != 2 && MAK_len != 3)
			throw new Exception("getHSMGenerateVerify,密钥长度不在规定范围内");
		int mackeylen = 0;
		if (MAK_len == 2)
			mackeylen = 1;
		else
			mackeylen = 2;
		// result=this.HSM_82(MAK_len,MAK_type,sData,MAC_len,sData2);
		if (MAK_type != 1 && MAK_type != 2 && MAK_type != 3)
			throw new Exception("getHSMGenerateVerify,MAC算法标识不在规定范围内");
		if (MAK_type == 2)
			result = this.HSM_MU(mackeylen, 0, 1, hexMessFlag, sData, "", MAC_len, sData2);
		else if (MAK_type == 3)
			result = this.HSM_MS(mackeylen, 0, 1, hexMessFlag, sData, "", MAC_len, sData2);
		else
			throw new Exception("getHSMGenerateVerify,加密机不支持该MAC算法标识！");

		if (result.getRecode() != 0)
			return result;

		String myMac = new String(result.getData());
		myMac = myMac.substring(0, sData3.length());
		if (myMac.equals(sData3) == false) // mac校验没通过01
		{
			logger.info("mac校验没通过.myMac[" + myMac + "]" + " Mac[" + sData3 + "]");
			result.setRecode(1);
		}
		return result;
	}

	public synchronized int HsmGenarateRandom(int RandomLen, byte[] Rand) throws Exception {
		EMResult result = new EMResult();
		// String sData3=new String(DivData);
		result = this.HSM_R1(RandomLen);
		// KeysLen[0]=result.getLsDataLen();
		System.arraycopy(result.getLsData(), 0, Rand, 0, result.getLsData().length);

		return 0;
	}

	public synchronized int GenerateAndExportSDKey(int KeyVer, int KeyIndex, int AlgFlag, int DivNum, byte[] DivData, int[] KeysLen,
			byte[] Keys) throws Exception {
		EMResult result = new EMResult();
		String sData3 = UnionUtil.byte2hex(DivData);
		// String sData3=new String(DivData);
		// result =
		// this.HSM_U9(KeyVer,KeyIndex,AlgFlag,sData3.length()/32,DivNum,sData3,"ZMK");
		result = this.HSM_U9(KeyVer, KeyIndex, AlgFlag, 1, DivNum, sData3, "ZMK");
		KeysLen[0] = result.getLsDataLen();
		System.arraycopy(result.getLsData(), 0, Keys, 0, result.getLsData().length);

		return 0;
	}

	public synchronized int GenerateCheckValue(byte[] Keys, byte[] CheckValue) throws Exception {
		EMResult result = new EMResult();
		String sData3 = UnionUtil.byte2hex(Keys);
		// String sData3=new String(Keys);
		result = this.HSM_W4(2, sData3);
		System.arraycopy(result.getLsData(), 0, CheckValue, 0, result.getLsData().length);

		return 0;
	}

	public synchronized int HsmGenerateMAC(int KeyID, int KeyVer, int KeyIndex, int AlgFlag, int PadFlag, int DivNum, byte[] DivData,
			int SessionKeyFlag, byte[] SkeySeed, int DataLen, byte Data[], int MACDataLen, byte[] MACData) throws Exception {
		EMResult result = new EMResult();
		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);

		// String sData1=new String(DivData);
		// String sData2=new String(SkeySeed);
		// String sData3=new String(Data);
		result = this.HSM_W0(1, AlgFlag, KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, PadFlag, "0000000000000000", DataLen, sData3,
				MACDataLen, "");

		System.arraycopy(result.getLsData(), 0, MACData, 0, result.getLsData().length);
		return 0;
	}

	public synchronized int HsmVerifyMAC(int KeyID, int KeyVer, int KeyIndex, int AlgFlag, int PadFlag, int DivNum, byte[] DivData,
			int SessionKeyFlag, byte[] SkeySeed, int DataLen, byte Data[], int MACDataLen, byte[] MACData) throws Exception {
		EMResult result = new EMResult();

		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);
		String sData4 = UnionUtil.byte2hex(MACData);
		/*
		 * String sData1=new String(DivData); String sData2=new
		 * String(SkeySeed); String sData3=new String(Data); String sData4=new
		 * String(MACData); }
		 */
		result = this.HSM_W0(2, AlgFlag, KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, PadFlag, "0000000000000000", DataLen, sData3,
				MACDataLen, sData4);
		return 0;
	}

	public synchronized int HsmGenerateCMAC(int KeyID, int KeyVer, int KeyIndex, int AlgFlag, int PadFlag, int DivNum, byte[] DivData,
			int SessionKeyFlag, byte[] SkeySeed, byte[] IcvData, int DataLen, byte Data[], int MACDataLen, byte[] MACData, byte[] ICVResult)
			throws Exception {
		EMResult result = new EMResult();

		// String sData1=new String(DivData);
		// String sData2=new String(SkeySeed);
		// String sData3=new String(Data);
		// String sData4=new String(IcvData);
		//
		// String sData5=new String(MACData);
		// String sData6=new String(ICVResult);
		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);
		String sData4 = UnionUtil.byte2hex(IcvData);

		result = this.HSM_W0(3, AlgFlag, KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, PadFlag, sData4, DataLen, sData3, MACDataLen,
				"1234");
		System.arraycopy(result.getLsData(), 0, MACData, 0, result.getLsData().length);
		System.arraycopy(result.getICV(), 0, ICVResult, 0, result.getICV().length);
		return 0;
	}

	public synchronized int HsmDataEncryptOrDecrypt(int KeyID, int KeyVer, int KeyIndex, int AlgFlag, int PadFlag, int DivNum,
			byte[] DivData, int SessionKeyFlag, byte[] SkeySeed, int DataLen, byte Data[], int[] CipheredDataLen, byte[] CipheredData)
			throws Exception {
		EMResult result = new EMResult();

		// String sData1=UnionUtil.Bytes2HexString(DivData);
		// String sData2=UnionUtil.Bytes2HexString(SkeySeed);
		// String sData3=UnionUtil.Bytes2HexString(Data);

		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);

		// String sData2=new String(SkeySeed);
		// String sData3=new String(Data);

		// result = this.HSM_W2(AlgFlag, 0, "MK-SMI", null, KeyIndex, DivNum,
		// sData1, SessionKeyFlag, sData2, 0, 0, null, 0, null, PadFlag,
		// DataLen, sData3);
		result = this.HSM_W2(AlgFlag, 0, "MK-AC", null, KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, 0, 0, null, 0, null, PadFlag,
				DataLen, sData3);

		CipheredDataLen[0] = result.getLsDataLen();
		System.arraycopy(result.getLsData(), 0, CipheredData, 0, result.getLsData().length);

		return 0;
	}

	/*
	 * add by lisq 2011-12-12
	 */

	/**
	 * 用指定的密钥经过若干级分散（可选）和过程密钥计算（可选）后，使用指定的算法和填充方式对 输入数据进行加密操作。
	 * 
	 * @param KeyIndex
	 *            密钥索引
	 * @param AlgFlag
	 *            算法标识：AES，0x88;3DES-ECB，0x81； 3DES-CBC， 0x82；DES-CBC：0x84
	 * @param PadFlag
	 *            填充标识：0，外部填充；1，内部填充
	 * @param DivNum
	 *            离散次数
	 * @param DivData
	 *            离散数据
	 * @param SessionKeyFlag
	 *            过程密钥标识：0，无过程密钥；1，有过程密钥
	 * @param SkeySeed
	 *            过程密钥
	 * @param DataLen
	 *            明文数据长度
	 * @param Data
	 *            明文数据
	 * @param CipheredDataLen
	 *            密文数据长度
	 * @param CipheredData
	 *            密文
	 * @return
	 * @throws Exception
	 */
	public synchronized int HsmDataEncrypt(int KeyVer, int KeyIndex, int AlgFlag, int PadFlag, int OperateFlag, int DivNum, byte[] DivData,
			int SessionKeyFlag, byte[] SkeySeed, int DataLen, byte Data[], int[] CipheredDataLen, byte[] CipheredData) throws Exception {
		EMResult result = new EMResult();

		// String sData1=UnionUtil.Bytes2HexString(DivData);
		// String sData2=UnionUtil.Bytes2HexString(SkeySeed);
		// String sData3=UnionUtil.Bytes2HexString(Data);

		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);

		// String sData2=new String(SkeySeed);
		// String sData3=new String(Data);
		result = this.HSM_W2(AlgFlag, 0, "MK-AC", null, KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, 0, 0, null, 0, null, PadFlag,
				DataLen, sData3);

		CipheredDataLen[0] = result.getLsDataLen();
		System.arraycopy(result.getLsData(), 0, CipheredData, 0, result.getLsData().length);

		return 0;
	}

	/**
	 * 用指定的密钥经过若干级分散（可选）和过程密钥计算（可选）后，使用指定的算法和填充方式对 输入数据进行解密操作。
	 * 
	 * @param KeyIndex
	 *            密钥索引
	 * @param AlgFlag
	 *            算法标识：AES，0x88;3DES-ECB，0x81； 3DES-CBC， 0x82；DES-CBC：0x84
	 * @param PadFlag
	 *            填充标识：0，外部填充；1，内部填充
	 * @param DivNum
	 *            离散次数
	 * @param DivData
	 *            离散数据
	 * @param SessionKeyFlag
	 *            过程密钥标识：0，无过程密钥；1，有过程密钥
	 * @param SkeySeed
	 *            过程密钥
	 * @param DataLen
	 *            密文数据长度
	 * @param Data
	 *            密文数据
	 * @param CipheredDataLen
	 *            明文数据长度
	 * @param CipheredData
	 *            明文
	 * @return
	 * @throws Exception
	 */
	public synchronized int HsmDataDecrypt(int KeyLen, byte[] Key, int AlgFlag, int OperateFlag, int PadFlag, int DivNum, byte[] DivData,
			int SessionKeyFlag, byte[] SkeySeed, int DataLen, byte Data[], int[] OutputDataLen, byte[] OutputData) throws Exception {
		EMResult result = new EMResult();

		// String sData1=UnionUtil.Bytes2HexString(DivData);
		// String sData2=UnionUtil.Bytes2HexString(SkeySeed);
		// String sData3=UnionUtil.Bytes2HexString(Data);

		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);
		// String mkKey = new String(Key);
		String mkKey = UnionUtil.Bytes2HexString(Key);

		// String sData2=new String(SkeySeed);
		// String sData3=new String(Data);
		result = this.HSM_W2(AlgFlag, 1, "MK-AC", mkKey, 0, DivNum, sData1, SessionKeyFlag, sData2, 0, 0, null, 0, null, PadFlag, DataLen,
				sData3);

		OutputDataLen[0] = result.getLsDataLen();
		System.arraycopy(UnionUtil.HexString2Bytes(new String(result.getLsData(), "ISO-8859-1")), 0, OutputData, 0,
				UnionUtil.HexString2Bytes(new String(result.getLsData(), "ISO-8859-1")).length);

		return 0;
	}

	/**
	 * 将采用Key1加密的数据转换为Key2加密，该接口主要用于对密钥的转加密
	 * 
	 * @param Key1Index
	 *            根密钥1索引号
	 * @param AlgFlag
	 *            算法标识：AES，0x88;3DES-ECB，0x81； 3DES-CBC， 0x82；DES-CBC：0x84
	 * @param Pad1Flag
	 *            填充标识：0，外部填充；1，内部填充
	 * @param Key1DivNum
	 *            根密钥1离散次数
	 * @param Key1DivData
	 *            根密钥1离散数据
	 * @param SessionKey1Flag
	 *            过程标识：0，无过程密钥；1，有过程密钥
	 * @param Skey1Seed
	 *            过程密钥
	 * @param Key2Index
	 *            根密钥2索引号
	 * @param Key2DivNum
	 *            根密钥2离散次数
	 * @param Key2DivData
	 *            根密钥2离散数据
	 * @param Session2KeyFlag
	 *            过程标识：0，无过程密钥；1，有过程密钥
	 * @param Skey2Seed
	 *            过程密钥
	 * @param inDataLen
	 *            根密钥1加密的数据密文长度
	 * @param bInDataByKey1
	 *            根密钥1加密的数据密文
	 * @param OutDataLen
	 *            根密钥2加密的数据密文长度
	 * @param bOutDataByKey2
	 *            根密钥2加密的数据密文
	 * @return
	 * @throws Exception
	 */
	public synchronized int HsmTranslateKey1ToKey2(int Key1ID, int Key1Ver, int Key1Index, int AlgFlag, int Pad1Flag, int Div1Num,
			byte[] Div1Data, int SessionKey1Flag, byte[] Skey1Seed, int Key2ID, int Key2Ver, int Key2Index, int Key2AlgFlag, int Div2Num,
			byte[] Div2Data, int Session2KeyFlag, byte[] Skey2Seed, int inDataLen, byte[] bInDataByKey1, int[] OutDataLen,
			byte[] bOutDataByKey2) throws Exception {
		EMResult result = new EMResult();

		// String sData1=UnionUtil.Bytes2HexString(DivData);
		// String sData2=UnionUtil.Bytes2HexString(SkeySeed);
		// String sData3=UnionUtil.Bytes2HexString(Data);

		String sDivData1 = UnionUtil.byte2hex(Div1Data);
		String sSkeySeed1 = UnionUtil.byte2hex(Skey1Seed);
		String sbInDataByKey1 = UnionUtil.byte2hex(bInDataByKey1);
		String sDivData2 = UnionUtil.byte2hex(Div2Data);
		String sSkeySeed2 = UnionUtil.byte2hex(Skey2Seed);

		// String sData2=new String(SkeySeed);
		// String sData3=new String(Data);
		result = this.HSM_W2(AlgFlag, 2, "MK-AC", null, Key1Index, Div1Num, sDivData1, SessionKey1Flag, sSkeySeed1, Key1Index, Div1Num,
				sDivData1, SessionKey1Flag, sSkeySeed1, Pad1Flag, inDataLen, sbInDataByKey1);

		OutDataLen[0] = result.getLen();
		System.arraycopy(result.getLsData(), 0, bOutDataByKey2, 0, result.getLsData().length);

		return 0;
	}

	/*
	 * add by lisq 2011-12-12 end
	 */

	/* 加密机计算校验值 */
	/*
	 * public synchronized int GenerateCheckValue(byte[] Keys)throws Exception {
	 * EMResult result=new EMResult(); String sData3=new String(Keys); result =
	 * this.HSM_W3(sData3); System.arraycopy(result.getLsData(), 0, Keys,0,
	 * result.getLsData().length); return 0; }
	 */
	/* 软加密计算校验值 */
	/*
	 * public synchronized int GenerateCheckValue2(byte[] Keys,byte[]
	 * CheckValue)throws Exception {
	 * 
	 * EMResult result=new EMResult(); String sData3=new String(Keys); String
	 * sData4=new String(CheckValue); result = this.HSM_W4(Keys);
	 * System.arraycopy(result.getLsData(), 0, Keys,0,
	 * result.getLsData().length); return 0; }
	 */

	/* 中央后台 */
	public synchronized int HSMSha_1(int datalen, byte[] data, byte[] hash) throws Exception {
		EMResult result = new EMResult();
		String sData1 = UnionUtil.byte2hex(data);
		result = this.HSM_GM(datalen, sData1);
		System.arraycopy(result.getLsData(), 0, hash, 0, result.getLsData().length);

		return 0;
	}

	public synchronized int HsmMACGen(int KeyIndex, int KeyVer, int PadFlag, int DivNum, byte[] DivData, int SessionKeyFlag,
			byte[] SkeySeed, byte[] IcvData, int DataLen, byte Data[], int MACDataLen, byte[] MACData) throws Exception {
		EMResult result = new EMResult();

		// String sData1=new String(DivData);
		// String sData2=new String(SkeySeed);
		// String sData3=new String(Data);
		// String sData4=new String(IcvData);
		//
		// String sData5=new String(MACData);
		// String sData6=new String(ICVResult);
		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);
		String sData4 = UnionUtil.byte2hex(IcvData);
		int AlgFlag;

		if (SessionKeyFlag == 1) {
			AlgFlag = 1;
		} else {
			AlgFlag = 0;
		}

		result = this.HSM_U3(1, AlgFlag, KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, PadFlag, sData4, DataLen, sData3, MACDataLen,
				"1234");
		System.arraycopy(result.getLsData(), 0, MACData, 0, result.getLsData().length);
		return 0;
	}

	public synchronized int HsmTACGen(int KeyIndex, int KeyVer, int PadFlag, int DivNum, byte[] DivData, int SessionKeyFlag,
			byte[] SkeySeed, byte[] IcvData, int DataLen, byte Data[], int MACDataLen, byte[] MACData) throws Exception {
		EMResult result = new EMResult();

		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);
		String sData4 = UnionUtil.byte2hex(IcvData);

		result = this.HSM_U3(1, 2, KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, PadFlag, sData4, DataLen, sData3, MACDataLen, "1234");
		System.arraycopy(result.getLsData(), 0, MACData, 0, result.getLsData().length);
		return 0;
	}

	public synchronized int HsmDataEncryptDecrypt(int KeyVer, int KeyIndex, int DivNum, byte[] DivData, int SessionKeyFlag,
			byte[] SkeySeed, int Alg_Flag, int Alg_ID, byte[] IvData, int DataLen, byte Data[], int[] CipheredDataLen, byte[] CipheredData)
			throws Exception {
		EMResult result = new EMResult();

		// String sData1=UnionUtil.Bytes2HexString(DivData);
		// String sData2=UnionUtil.Bytes2HexString(SkeySeed);
		// String sData3=UnionUtil.Bytes2HexString(Data);

		String sData1 = UnionUtil.byte2hex(DivData);
		String sData2 = UnionUtil.byte2hex(SkeySeed);
		String sData3 = UnionUtil.byte2hex(Data);

		result = this.HSM_U1(Alg_Flag, Alg_ID, "MK-SMI", KeyIndex, DivNum, sData1, SessionKeyFlag, sData2, DataLen, sData3,
				"0000000000000000");

		CipheredDataLen[0] = result.getLsDataLen();
		System.arraycopy(result.getLsData(), 0, CipheredData, 0, result.getLsData().length);

		return 0;
	}

	public synchronized int HsmImportKey(int KeyVer, int KeyIndex, int MkDvsNum, byte[] MkDvsData, int CckVer, int CckIndex, int CckDvsNum,
			byte[] CckDvsData, int SessionKeyFlag, byte[] SkeySeed, int KeyHeaderLen, byte[] KeyHeader, byte[] IvData, int MacHeaderLen,
			byte[] MacHeader, int[] CipheredDataLen, byte[] CipheredData, byte[] Mac) throws Exception {
		EMResult result = new EMResult();
		// String sData1=UnionUtil.Bytes2HexString(DivData);

		String sData1 = UnionUtil.byte2hex(MkDvsData);
		String sData2 = UnionUtil.byte2hex(CckDvsData);
		String sData3 = UnionUtil.byte2hex(SkeySeed);
		String sData4 = UnionUtil.byte2hex(KeyHeader);
		String sData5 = UnionUtil.byte2hex(IvData);
		String sData6 = UnionUtil.byte2hex(MacHeader);

		result = this.HSM_U2('T', 1, 2, "MK-SMI", KeyIndex, MkDvsNum, sData1, 1, CckIndex, CckDvsNum, sData2, SessionKeyFlag, sData3,
				"0000000000000000", KeyHeaderLen / 2, sData4, KeyHeaderLen / 2, sData5, MacHeaderLen / 2, sData6, MacHeaderLen / 2);

		System.arraycopy(result.getMac(), 0, Mac, 0, result.getMac().length);
		CipheredDataLen[0] = result.getLsDataLen();
		System.arraycopy(result.getLsData(), 0, CipheredData, 0, result.getLsData().length);

		return 0;
	}

	// add by changzx 2010 - 8 -27
	/*
	 * 根据指定参数要求分散得到一个或多个卡片子密钥采用保护密钥加密（没有填充）,并计算密钥校验值输出。
	 */
	public synchronized int HsmGenerateMulKeyAndCheck(int AlgFlag, int SessionKeyFlag,// 0：不使用过程密钥
																						// 1：使用过程密钥
			int EncKeyID, int EncKeyVer, int EncKeyIndex, int EncKeyDvsNum, byte[] EncKeyDvsData, byte[] Seed, // 过程因子
			int KeyNum, byte[] MulKeyID, byte[] MulKeyVer, int[] MulKeyIndex, int KeyDvsNum,
			/*
			 * String[] KeyDvsData, String[] MulKeys, String[] MulCheckValue
			 */
			byte[] KeyDvsData, byte[] MulKeys, byte[] MulCheckValue) throws Exception {// 调用U2指令

		int cnt = KeyNum;
		int EncryptPadLen = 0, offset = 0, mulKeyOffset = 0, mulKeyCheValOffset = 0;
		EMResult result = new EMResult();
		int AlgMode = 2; // 加密并计算mac
		int Scheme = 2; // M/Chip4（CBC模式，强制填充X80，可以外带加密填充数据）（带长度指引的CBC模式，强制填充0x80，外带加密填充数据）
		String KeyType = "ZEK";
		String DivData = UnionUtil.byte2hex(EncKeyDvsData);
		int CckType = 1;// 保护密钥类型 TK
		int CckDivNum = KeyDvsNum;
		int KeyIndex = EncKeyIndex;
		// String CckDivData= UnionUtil.byte2hex(KeyDvsData);
		String CckDivData = new String(KeyDvsData);
		String[] MulKeysStr = new String[cnt];
		for (int i = 0; i < cnt; i++) {
			MulKeysStr[i] = CckDivData.substring(offset, offset + 16);
			offset += 16;
		}
		String IV_CBC = "0000000000000000";

		char mechism = 'T';
		/*
		 * System.out.println("KeyDvsData.length =" + KeyDvsData.length +
		 * "\nKeyDvsData" + new String(KeyDvsData));
		 */
		for (int i = 0; i < cnt; i++) {
			KeyIndex = MulKeyIndex[i];
			/*
			 * CckDivData = CckDivData.substring(offset,offset+16); offset +=
			 * 16;
			 */
			CckDivData = MulKeysStr[i];
			result = this.HSM_U2(mechism, AlgMode, Scheme, KeyType, KeyIndex, CckDivNum, CckDivData, CckType, EncKeyIndex, EncKeyDvsNum,
					DivData, SessionKeyFlag, Seed.toString(), IV_CBC, EncryptPadLen, null, 0, null, 0, null, 0);

			/*
			 * MulKeys[i] = new String(result.getLsData()); MulCheckValue[i] =
			 * new String(result.getICV());
			 */
			// System.arraycopy(src, srcPos, dest, destPos, length)
			System.arraycopy(result.getLsData(), 0, MulKeys, mulKeyOffset, result.getLsData().length);
			mulKeyOffset += result.getLsData().length;

			System.arraycopy(result.getICV(), 0, MulCheckValue, mulKeyCheValOffset, result.getICV().length);
			mulKeyCheValOffset += result.getICV().length;

		}

		return 0;
	}

	/*
	 * 导出公钥明文或密文：
	 */

	public synchronized int HsmExportPK(int PKIndex, int PKType, int Encflag, int EncKeyVer, int EncKeyIndex, int PadDataLen,
			byte[] PadData, int[] PKDataLen, byte[] PKData, int[] PKCheckLen, byte[] PKCheckValue) throws Exception {
		EMResult res = new EMResult();
		byte[] tmpPk = new byte[1024 + 1];

		int alg_Flag = 1; // 加密模式 3DES-CBC 模式
		String KeyType = "MK-AC";
		// 调用C7指令获取公钥明文
		String DivData2 = null;
		String SKeySeed2 = null;
		String tmpIkstrHex = null;
		String tmpIkstr = null;

		if (Encflag == 1) {
			res = this.HSM_C7(PKIndex, PKType);
			tmpPk = res.getPk();
			String tmpPkstr = new String(tmpPk);
			tmpIkstrHex = UnionUtil.Bytes2HexString(tmpPk);

			PKDataLen[0] = 256;
			System.arraycopy(tmpPk, 0, PKData, 0, tmpPk.length);
		} else if (Encflag == 2) {
			PKDataLen[0] = 16;
			tmpIkstr = "10001";
			tmpIkstrHex = "1000180000000000";
			System.arraycopy(tmpIkstr.getBytes(), 0, PKData, 0, tmpIkstr.getBytes().length);

		}
		// 调用U1

		res = this.HSM_U1(3, 0x90, KeyType,
		// 写死计算公钥MAC的索引，000，密钥值全0
		// EncKeyIndex,
				0, 0, DivData2, 0, SKeySeed2, tmpIkstrHex.length(), tmpIkstrHex, null);
		PKCheckLen[0] = 16;
		System.arraycopy(res.getLsData(), 0, PKCheckValue, 0, res.getLsDataLen());

		return 0;
	}

	/**/

	/********************************* <接口实现End> **************************************/

	/********************************* <私有方法Start> **************************************/

	private EMResult HSM_34(String vkIndex, int lenOfVK) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "34";
		cmdLen += 2;

		if ((vkIndex == null) || ((lenOfVK != 256) && (lenOfVK != 512) && (lenOfVK != 1024) && (lenOfVK != 2048) && (lenOfVK != 4096))) {
			throw new Exception("HSM_34,参数错！");
		}

		cmdBuff += UnionUtil.LeftAddZero("" + lenOfVK, 4);
		cmdLen += 4;

		this.CheckValue(vkIndex.length(), 2, 2, "vkIndex索引长度为2");
		if (Integer.parseInt(vkIndex) != 99)
			this.CheckValue(Integer.parseInt(vkIndex), 0, 20, "vkIndex索引为[1-20]的整数");

		cmdBuff += vkIndex;
		cmdLen += 2;

		outStr = commWithHsm(cmdLen, cmdBuff, "3500", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;

		int vklen = Integer.parseInt(outStr.substring(offset, offset + 4));
		logger.info("vklen=[" + vklen + "]");
		offset += 4;
		offset += vklen;
		logger.info("offset=[" + offset + "] outStrlen=[" + outStr.length() + "]");
		OutData.setPk(UnionUtil.BytesCopy(outBytes, offset));

		return OutData;
	}

	private EMResult HSM_37(int flag, int vkIndex, int dataLen, String data) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "37";
		cmdLen += 2;

		// this.CheckNull(flag,"HSM_37,填充标识不许为空!");
		// this.CheckLength(flag, 1, "vkIndex索引长度为1");
		// this.CheckNull(vkIndex,"HSM_37,vkIndex索引不许为空!");
		// this.CheckLength(vkIndex, 2, "vkIndex索引长度为2");

		// 填充方式

		cmdBuff += Integer.toString(flag);
		cmdLen++;

		this.CheckValue(vkIndex, 0, 20, "vkIndex索引为[1-20]的整数");
		cmdBuff += Integer.toString(vkIndex);
		cmdLen += 2;

		// 数据长度
		cmdBuff += UnionUtil.LeftAddZero("" + dataLen, 4);
		cmdLen += 4;

		// 数据
		cmdBuff += data;
		cmdLen += data.length();

		outStr = commWithHsm(cmdLen, cmdBuff, "3800", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;
		int signlen = Integer.parseInt(outStr.substring(offset, offset + 4));
		offset += 4;

		OutData.setSignature(UnionUtil.BytesCopy(outBytes, offset, signlen));

		return OutData;
	}

	private EMResult HSM_38(int flag, int signLen, String signature, int dataLen, String data, String pk) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "38";
		cmdLen += 2;

		// this.CheckNull(flag,"HSM_38,填充标识不许为空!");
		// this.CheckLength(flag, 1, "HSM_38,填充标识长度为1");

		// 填充方式
		cmdBuff += Integer.toString(flag);
		cmdLen++;

		// 签名长度
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(signLen), 4);
		cmdLen += 4;

		// 签名
		cmdBuff += signature;
		cmdLen += signature.length();
		logger.info("HSM_38 signature.length()=[" + signature.length() + "]");
		// 分隔符
		cmdBuff += ";";
		cmdLen += 1;

		// 数据长度
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(dataLen), 4);
		cmdLen += 4;

		// 分格符
		cmdBuff += data;
		cmdLen += data.length();

		cmdBuff += ";";
		cmdLen += 1;

		// 公钥
		cmdBuff += pk;
		cmdLen += pk.length();

		outStr = commWithHsm(cmdLen, cmdBuff, "3900", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		return OutData;
	}

	private EMResult HSM_CC(int zpk1Length, String zpk1, int zpk2Length, String zpk2, String pinBlockByZPK1, String pinFormat1,
			String pinFormat2, String accNo1, int lenOfAccNo1, String accNo2, int lenOfAccNo2) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "CC";
		cmdLen += 2;

		this.CheckNull(zpk1, "HSM_CC,zpk1不许为空!");
		this.CheckNull(zpk2, "HSM_CC,zpk2不许为空!");
		this.CheckNull(pinFormat1, "HSM_CC,pinFormat1不许为空!");
		this.CheckNull(pinBlockByZPK1, "HSM_CC,pinBlockByZPK1不许为空!");
		this.CheckNull(pinFormat2, "HSM_CC,pinFormat2不许为空!");
		this.CheckNull(accNo1, "HSM_CC,accNo1不许为空!");
		this.CheckNull(accNo2, "HSM_CC,accNo2不许为空!");

		// 源ZPK1
		String keyStr = getRacalKeyString(zpk1Length, zpk1);
		cmdBuff += keyStr;
		cmdLen += keyStr.length();

		// 目标ZPK2
		keyStr = getRacalKeyString(zpk2Length, zpk2);
		cmdBuff += keyStr;
		cmdLen += keyStr.length();

		// 最大密码长度
		cmdBuff += "12";
		cmdLen += 2;

		cmdBuff += pinBlockByZPK1;
		cmdLen += pinBlockByZPK1.length();

		// 源PIN block格式
		cmdBuff += pinFormat1;
		cmdLen += pinFormat1.length();

		// 目标PIN block格式
		cmdBuff += pinFormat2;
		cmdLen += pinFormat2.length();

		// 帐号或卡号
		cmdBuff += get12LenAccountNumber(lenOfAccNo1, accNo1);
		cmdLen += 12;

		// 目标帐号或卡号
		if (pinFormat2.equals("10")) {
			cmdBuff += get12LenAccountNumber(lenOfAccNo2, accNo2);
			cmdLen += 12;
		}

		outStr = commWithHsm(cmdLen, cmdBuff, "CD00", OutData);
		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;
		offset += 2; // 密码长度

		String destPinBlockFrm = outStr.substring(outStr.length() - 2);
		int pinBlk2len = 16;
		if (destPinBlockFrm.equals("09"))
			pinBlk2len = 40;
		// 目标PIN block

		OutData.setPinBlockByZPK2(UnionUtil.BytesCopy(outBytes, offset, pinBlk2len));
		return OutData;
	}

	private EMResult HSM_JG(int zpkLength, String zpk, String pinFormat, String accNo, int lenOfAccNo, String pinByLmk) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "JG";
		cmdLen += 2;

		this.CheckNull(zpk, "HSM_JG,zpk不许为空!");
		this.CheckNull(pinFormat, "HSM_JG,pinFormat不许为空!");
		this.CheckNull(accNo, "HSM_JG,accNo不许为空!");
		this.CheckNull(pinByLmk, "HSM_JG,pinByLmk不许为空!");

		// ZPK
		String keyStr = getRacalKeyString(zpkLength, zpk);
		cmdBuff += keyStr;
		cmdLen += keyStr.length();

		// PIN块格式
		cmdBuff += pinFormat;
		cmdLen += pinFormat.length();

		// 帐号或卡号
		cmdBuff += get12LenAccountNumber(lenOfAccNo, accNo);
		cmdLen += 12;

		// pin
		cmdBuff += pinByLmk;
		cmdLen += pinByLmk.length();

		outStr = commWithHsm(cmdLen, cmdBuff, "JH00", OutData);
		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;
		// 目标PIN block

		OutData.setPinBlockByZPK(UnionUtil.BytesCopy(outBytes, offset, 16));
		return OutData;
	}

	private EMResult HSM_GI(String encyFlag, String padMode, String lmkType, int keyLength, int lenOfDesKeyByPK, String desKeyByPK,
			String vkIndex) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "GI";
		cmdLen += 2;

		cmdBuff += encyFlag;
		cmdLen += 2;

		cmdBuff += padMode;
		cmdLen += 2;

		cmdBuff += lmkType;
		cmdLen += 4;

		this.CheckValue(lenOfDesKeyByPK, 1, 9999, "lenOfDesKeyByPK数据长度为[1-9999]的整数");
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(lenOfDesKeyByPK).toUpperCase(), 4);
		cmdLen += 4;

		cmdBuff += desKeyByPK;
		cmdLen += lenOfDesKeyByPK;
		// 分隔符
		cmdBuff += ";";
		cmdLen++;
		// 私钥索引

		this.CheckValue(Integer.parseInt(vkIndex), 0, 20, "vkIndex索引为[1-20]的整数");
		cmdBuff += vkIndex;
		cmdLen += 2;

		/*
		 * // 分隔符 cmdBuff += ";"; cmdLen++;
		 * 
		 * // ZMK加密的密钥密文长度标志 cmdBuff += getKeyXYZScheme(keyLength); cmdLen++;
		 * 
		 * cmdBuff += getKeyXYZScheme(keyLength); cmdLen++;
		 */

		outStr = commWithHsm(cmdLen, cmdBuff, "GJ00", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;
		byte[] initValue = UnionUtil.BytesCopy(outBytes, offset, 16);
		logger.info("HSM_GI::initValueStr=[" + (new String(initValue, "ISO-8859-1")) + "]");
		offset += 16;

		byte[] desKeyByLMK = getKeyBytesFromBValWithXYZ(outBytes, offset);
		if (desKeyByLMK.length != keyLength) {
			// DES密钥（LMK） 16H或32H或1A+32H或1A+48H 由DES密钥类型指定的LMK对下加密的DES密钥。
			desKeyByLMK = UnionUtil.BytesCopy(outBytes, offset, keyLength);
		}
		OutData.setData(desKeyByLMK);
		if (outBytes[offset] == 'X' || outBytes[offset] == 'Y' || outBytes[offset] == 'Z') {
			offset += 1;
		}
		logger.info("HSM_GI::desKeyByLMKStr=[" + (new String(desKeyByLMK, "ISO-8859-1")) + "]");

		offset += desKeyByLMK.length;
		byte[] checkValue = UnionUtil.BytesCopy(outBytes, offset, 16);
		OutData.setMac(checkValue);
		logger.info("HSM_GI::checkValueStr=[" + (new String(checkValue, "ISO-8859-1")) + "]");
		return OutData;

	}

	private EMResult HSM_H3(String vkIndex, String fillType, String zpk, String pan, String desKeyByPK) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "H3";
		cmdLen += 2;

		this.CheckLength(vkIndex, 2, "索引数据长度为2");
		this.CheckValue(Integer.parseInt(vkIndex), 0, 20, "索引号为[1-20]的整数");
		cmdBuff += vkIndex;
		cmdLen += 2;

		cmdBuff += fillType;
		cmdLen += 1;

		String keyStr = getRacalKeyString(zpk.length(), zpk);
		cmdBuff += keyStr;
		cmdLen += keyStr.length();

		this.CheckValue(pan.length(), 4, 20, "pan数据长度为[1-20]的整数");
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(pan.length()).toUpperCase(), 2);
		cmdLen += 2;

		cmdBuff += pan;
		cmdLen += pan.length();

		cmdBuff += desKeyByPK;
		cmdLen += desKeyByPK.length();

		outStr = commWithHsm(cmdLen, cmdBuff, "H400", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;
		offset += 2; // pin明文的长度
		byte[] pinByZpk = UnionUtil.BytesCopy(outBytes, offset, 32);
		logger.info("HSM_H3::pinByZpkStr=[" + (new String(pinByZpk, "ISO-8859-1")) + "]");
		OutData.setPinBlockByZPK2(pinByZpk);

		return OutData;

	}

	private EMResult HSM_H5(String srctype, String zpk1, String pan, String destype, String destKey, String pin) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "H5";
		cmdLen += 2;

		// 源KEY类型
		cmdBuff += srctype;
		cmdLen += 1;

		// 源ZPK1/PVK1
		String keyStr = getRacalKeyString(zpk1.length(), zpk1);
		cmdBuff += keyStr;
		cmdLen += keyStr.length();

		// 帐号长度
		this.CheckValue(pan.length(), 4, 20, "pan数据长度为[1-20]的整数");
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(pan.length()).toUpperCase(), 2);
		cmdLen += 2;

		// 帐号（PAN）
		cmdBuff += pan;
		cmdLen += pan.length();

		// 目的KEY类型
		cmdBuff += destype;
		cmdLen += 1;

		// 目的ZPK２/PVK
		keyStr = getRacalKeyString(destKey.length(), destKey);
		cmdBuff += keyStr;
		cmdLen += keyStr.length();

		// PIN密文
		cmdBuff += pin;
		cmdLen += pin.length();

		outStr = commWithHsm(cmdLen, cmdBuff, "H600", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;
		byte[] pinByZpk = UnionUtil.BytesCopy(outBytes, offset, 32);
		logger.info("HSM_H3::pinByZpkStr=[" + (new String(pinByZpk, "ISO-8859-1")) + "]");
		OutData.setPinBlockByZPK2(pinByZpk);

		return OutData;

	}

	private EMResult HSM_U5(int Alg_flag, String keyType) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		int cmdLen = 0;
		int offset = 0;

		cmdBuff = "U5";
		cmdLen += 2;

		keyLenType = getXYZFlagFromMobRule(Alg_flag);

		cmdBuff += keyLenType;
		cmdLen += 1;

		keyScheme = getKeyScheme(keyType);
		cmdBuff += keyScheme;
		cmdLen += 3;

		outStr = commWithHsm(cmdLen, cmdBuff, "U600", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;
		OutData.setMac(UnionUtil.BytesCopy(outBytes, outBytes.length - 16));

		switch (Alg_flag) {
		case 0x01:
			OutData.setKEK(UnionUtil.BytesCopy(outBytes, offset, 16));
			offset += 16;
			break;
		case 0x02:
			OutData.setKEK(UnionUtil.BytesCopy(outBytes, offset, 32));
			offset += 32;
			break;
		case 0x03:
			OutData.setKEK(UnionUtil.BytesCopy(outBytes, offset, 48));
			offset += 48;
			break;
		default:
			throw new Exception("KEY长度不在规定范围内");
		}
		OutData.setLmkKek(getKeyBytesFromBValWithXYZ(outBytes, offset));

		return OutData;
	}

	private EMResult HSM_U9(int KeyVer, int KeyIndex, int AlgFlag, int KeyNum, int DivNum, String DivData, String keyType) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		String Lscheme2 = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "U9";
		cmdLen += 2;

		// Alg flag
		keyLenType = getLSFlagFromMobRule(AlgFlag);
		cmdBuff += keyLenType;
		cmdLen += 1;

		// key type
		keyScheme = getKeyScheme(keyType);
		cmdBuff += keyScheme;
		cmdLen += 3;

		// root-key
		cmdBuff += "K";
		cmdLen += 1;

		cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(KeyIndex), 3);
		cmdLen += 3;

		// 固定产生密钥数量１
		cmdBuff += KeyNum;
		cmdLen += 1;
		// cmdBuff+=Integer.toString(DivData.length()/32);
		// cmdLen+=1;

		cmdBuff += Integer.toString(DivNum);
		cmdLen += 1;
		// Div data
		int i;
		for (i = 1; i <= KeyNum; i++) {
			cmdBuff += DivData;
		}
		// cmdLen +=32*DivNum;
		cmdLen += 32 * DivNum * KeyNum;

		outStr = commWithHsm(cmdLen, cmdBuff, "UA00", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;

		switch (KeyNum) {
		case 1:
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 32));
			offset += 32;
			break;
		case 2:
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 64));
			offset += 64;
			break;
		case 3:
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 96));
			offset += 96;
			break;
		// 省略掉4-9
		default:
			throw new Exception("产生密钥数量不在规定范围内");
		}

		return OutData;
	}

	private EMResult HSM_W4(int AlgFlag, String Keys) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		String Lscheme2 = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "W4";
		cmdLen += 2;

		// key type
		keyLenType = getXYZFlagFromMobRule(AlgFlag);
		cmdBuff += keyLenType;
		cmdLen += 1;

		// root-key
		cmdBuff += Keys;
		cmdLen += 32;

		outStr = commWithHsm(cmdLen, cmdBuff, "W508", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;

		switch (AlgFlag) {
		case 1:
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 16));
			offset += 16;
			break;
		case 2:
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 16));
			offset += 16;
			break;
		case 3:
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 16));
			offset += 16;
			break;
		// 省略掉4-9
		default:
			throw new Exception("产生密钥数量不在规定范围内");
		}

		return OutData;
	}

	// 中央后台

	private EMResult HSM_GM(int datalen, String data) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "GM01";
		cmdLen += 4;

		// rand number's bytes
		// String tempcmd=new String(Integer.toHexString(RandBytes));
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(datalen), 5);
		// cmdBuff+=tempcmd;
		cmdLen += 5;
		cmdBuff += data;
		cmdLen += datalen;

		outStr = commWithHsm(cmdLen, cmdBuff, "GN00", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;

		OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, datalen));
		offset += datalen;

		return OutData;
	}

	private EMResult HSM_U3(int AlgFlag, int Scheme_2, int KeyIndex, int DivNum, String DivData, int processFlag, String processData,
			int macFillType, String IV, int macDataLen, String macData, int macSelfLenFlag, String srcMAC) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		String Lscheme2 = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "U3";
		cmdLen += 2;

		cmdBuff += Integer.toHexString(AlgFlag);
		cmdLen += 1;

		// Scheme_2
		// 0:标准计算3DES-MAC;1:使用过程密钥计算DESMAC;2:计算TAC
		keyLenType = getMACFlagFromMobRule_Center(Scheme_2);
		cmdBuff += keyLenType;
		cmdLen += 1;

		// root-key
		cmdBuff += "K";
		cmdLen += 1;

		cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(KeyIndex), 3);
		cmdLen += 3;

		// 增加离散次数为0
		cmdBuff += Integer.toString(DivNum);
		cmdLen += 1;
		if (DivNum >= 1) {
			cmdBuff += DivData;
			cmdLen += 16 * DivNum;

		}

		// process flag
		// cmdBuff +=Integer.toString(processFlag);
		// cmdLen+=1;
		if (processFlag == 1) {
			// process data
			cmdBuff += processData;
			cmdLen += processData.length();
		}

		// macFillType
		cmdBuff += Integer.toString(macFillType);
		cmdLen += 1;

		// IV
		cmdBuff += IV;
		cmdLen += IV.length();

		this.CheckValue(macDataLen, 1, 999, "MAC数据长度为[1-999]的整数");
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(macDataLen / 2), 3);
		cmdLen += 3;

		// macData
		cmdBuff += macData;
		cmdLen += macData.length();

		// macSelfLenFlag
		cmdBuff += Integer.toString(macSelfLenFlag);
		cmdLen += 1;

		// source MAC
		if (AlgFlag == 2) {
			// process srcMAC
			cmdBuff += srcMAC;
			cmdLen += srcMAC.length();
		}

		outStr = commWithHsm(cmdLen, cmdBuff, "U400", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;
		if (AlgFlag != 2) {
			switch (macSelfLenFlag) {
			case 1:
				OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 8));
				offset += 8;
				break;
			case 2:
				OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 16));
				offset += 16;
				break;

			}
		}

		return OutData;
	}

	private EMResult HSM_U1(int AlgFlag, int Alg_ID,
	// int Scheme,
			String KeyType, int KeyIndex, int DivNum, String DivData, int processFlag, String processData,
			// int dataFillType,
			int dataLen, String data, String IV) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		String Lscheme2 = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "U1";
		cmdLen += 2;

		// Scheme
		// keyLenType = getLSFlagFromMobRule(AlgFlag);
		// cmdBuff+=keyLenType;
		// cmdLen+=1;

		if (AlgFlag == 1) {
			/* 加密 */
			// 3DES-ECB
			if (Alg_ID == 0x81) {
				cmdBuff += Integer.toString(0);
				cmdLen += 1;
				cmdBuff += UnionUtil.LeftAddZero(Integer.toString(1), 2);
				cmdLen += 2;

			}
			// 3DES-CBC
			if (Alg_ID == 0x82) {
				cmdBuff += Integer.toString(0);
				cmdLen += 1;
				cmdBuff += UnionUtil.LeftAddZero(Integer.toString(2), 2);
				cmdLen += 2;

			}
			// DES-ECB
			if (Alg_ID == 0x83) {
				if (processFlag == 1) {
					cmdBuff += Integer.toString(6);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(1), 2);
					cmdLen += 2;
				} else {
					cmdBuff += Integer.toString(4);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(1), 2);
					cmdLen += 2;
				}
			}
			// DES-CBC
			if (Alg_ID == 0x84) {
				if (processFlag == 1) {
					cmdBuff += Integer.toString(6);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(1), 2);
					cmdLen += 2;
				} else {
					cmdBuff += Integer.toString(4);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(2), 2);
					cmdLen += 2;
				}

			}

		} else {
			// 3DES-ECB
			if (Alg_ID == 0x81) {
				cmdBuff += Integer.toString(1);
				cmdLen += 1;
				cmdBuff += UnionUtil.LeftAddZero(Integer.toString(1), 2);
				cmdLen += 2;

			}
			// 3DES-CBC
			if (Alg_ID == 0x82) {
				cmdBuff += Integer.toString(1);
				cmdLen += 1;
				cmdBuff += UnionUtil.LeftAddZero(Integer.toString(2), 2);
				cmdLen += 2;

			}
			// DES-ECB
			if (Alg_ID == 0x83) {
				if (processFlag == 1) {
					cmdBuff += Integer.toString(6);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(1), 2);
					cmdLen += 2;
				} else {
					cmdBuff += Integer.toString(5);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(1), 2);
					cmdLen += 2;
				}

			}
			// DES-CBC
			if (Alg_ID == 0x84) {
				if (processFlag == 1) {
					cmdBuff += Integer.toString(6);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(2), 2);
					cmdLen += 2;
				} else {
					cmdBuff += Integer.toString(5);
					cmdLen += 1;
					cmdBuff += UnionUtil.LeftAddZero(Integer.toString(2), 2);
					cmdLen += 2;
				}

			} else {
				cmdBuff += Integer.toString(3);
				cmdLen += 1;
				cmdBuff += UnionUtil.LeftAddZero(Integer.toString(2), 2);
				cmdLen += 2;
			}

		}
		// cmdBuff+=Integer.toString(AlgFlag);
		// cmdLen+=1;

		// cmdBuff+=Integer.toString(Scheme);
		// cmdLen+=1;

		// key type
		// keyScheme=getKeyScheme(KeyType);
		cmdBuff += "109";
		cmdLen += 3;
		// cmdBuff+=KeyType;
		// cmdLen+=3;

		// root-key
		cmdBuff += "K";
		cmdLen += 1;

		cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(KeyIndex), 3);
		cmdLen += 3;

		// 增加离散次数为0
		cmdBuff += Integer.toString(DivNum);
		cmdLen += 1;
		if (DivNum >= 1) {
			cmdBuff += DivData;
			cmdLen += 16 * DivNum;

		}

		/*
		 * cmdBuff +=Integer.toString(DivNum); cmdLen+=1;
		 * 
		 * //Div data cmdBuff+=DivData; cmdLen +=32*DivNum;
		 */
		// process flag
		// cmdBuff +=Integer.toString(processFlag);
		// cmdLen+=1;

		if (processFlag == 1) {
			// process data
			cmdBuff += processData;
			cmdLen += processData.length();
		}

		//
		// dataFillType
		// cmdBuff +=Integer.toString(dataFillType);
		// cmdLen+=1;
		if (AlgFlag == 1) {
			cmdBuff += "01";
			cmdLen += 2;
		} else if (AlgFlag == 3) {
			cmdBuff += "02";
			cmdLen += 2;
		}
		// DataLen
		// cmdBuff +=UnionUtil.LeftAddZero(Integer.toString(dataLen),3);
		// cmdLen+=3;
		if (keyLenType == "1" || keyLenType == "2") {
			cmdBuff += UnionUtil.LeftAddZero(Integer.toString((dataLen / 2) + 16), 3);
			cmdLen += 3;
		} else {
			cmdBuff += UnionUtil.LeftAddZero(Integer.toString(dataLen / 2), 3);
			cmdLen += 3;
		}

		if (keyLenType == "1" || keyLenType == "2") {
			// IV
			cmdBuff += IV;
			cmdLen += IV.length();
		}

		// Data
		cmdBuff += data;
		cmdLen += data.length();

		outStr = commWithHsm(cmdLen, cmdBuff, "U200", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");

		offset = 4;
		byte[] bytesLen = UnionUtil.BytesCopy(outBytes, offset, 3);
		int iLen = Integer.parseInt(new String(bytesLen)) * 2;
		offset += 3;

		if (AlgFlag == 1) {
			OutData.setLen(iLen);
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, iLen));
			offset += iLen;
		} else if (AlgFlag == 3) {
			OutData.setLen(iLen * 2);
			OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, iLen * 2));
			offset += iLen * 2;
		}

		return OutData;
	}

	private EMResult HSM_U2(char mechism, // 安全机制
			int AlgMode, // 模式标志 0-仅加密 1-加密并计算MAC 2-加密并计算密钥校验值
			int Scheme, // 方案ID 0
			String KeyType, // 根密钥类型
			int KeyIndex, // 根密钥索引号
			int DivNum, // 离散次数
			String DivData, // 离散数据
			int CckType, // 保护密钥类型
			int CckIndex, // 保护密钥索引
			int CckDivNum, // 保护密钥离散次数
			String CckDivData, // 离散银子
			int processFlag, // 过程密钥标识
			String processData, // 过程因子
			String IV_CBC, // IV 向量 scheme 0 ,3时有
			int EncryptPadLen, // scheme 为 2，3时有，填充数据
			String EncryptPad, int EncryptPadOffset, String IV_MAC, // algMode
			// 为1时有
			int MacPadLen, // MAC 填充数据长度
			String MacPad, int MacPadOffset) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		String Lscheme2 = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "U2";
		cmdLen += 2;
		/*
		 * if(processFlag==1) { cmdBuff +="S"; cmdLen+=1; } else { cmdBuff
		 * +="T"; cmdLen+=1; }
		 */
		cmdBuff += mechism;
		cmdLen += 1;

		cmdBuff += Integer.toString(AlgMode);
		cmdLen += 1;

		cmdBuff += Integer.toString(Scheme);
		cmdLen += 1;
		// cmdBuff +=Scheme;
		// cmdLen +=1;

		// keyScheme=getKeyScheme(KeyType);
		cmdBuff += "109";
		cmdLen += 3;

		// root-key
		cmdBuff += "K";
		cmdLen += 1;

		cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(KeyIndex), 3);
		cmdLen += 3;

		// 增加离散次数为0
		cmdBuff += Integer.toString(DivNum);
		// cmdBuff +=DivNum;
		cmdLen += 1;
		if (DivNum >= 1) {
			cmdBuff += DivData;
			cmdLen += 16 * DivNum;
		}

		// cck-key

		cmdBuff += Integer.toString(CckType);
		cmdLen += 1;

		cmdBuff += "K";
		cmdLen += 1;

		cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(CckIndex), 3);
		cmdLen += 3;

		// 增加离散次数为0
		cmdBuff += Integer.toString(CckDivNum);
		// cmdBuff +=CckDivNum;
		cmdLen += 1;
		if (CckDivNum >= 1) {
			cmdBuff += CckDivData;
			cmdLen += 16 * CckDivNum;
		}

		// process flag
		if (processFlag == 1 && mechism != 'T') {
			// process data
			cmdBuff += "Y";
			cmdLen += 1;
			cmdBuff += processData;
			cmdLen += processData.length();
		}

		// 模式标识int AlgMode,
		// 方案IDint Scheme,
		if (Scheme == 0 || Scheme == 3) {
			cmdBuff += IV_CBC;
			cmdLen += IV_CBC.length();
		}

		if (Scheme == 2 || Scheme == 3) {
			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(EncryptPadLen), 4);
			cmdLen += 4;

			// cmdBuff +=EncryptPad;
			// cmdLen +=EncryptPadLen*2;

			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(EncryptPadOffset), 4);
			cmdLen += 4;
		}

		if (AlgMode == 1) {
			cmdBuff += IV_MAC;
			cmdLen += IV_MAC.length();

			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(MacPadLen), 4);
			cmdLen += 4;

			cmdBuff += MacPad;
			cmdLen += MacPadLen * 2;

			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(MacPadOffset), 4);
			cmdLen += 4;
		}

		outStr = commWithHsm(cmdLen, cmdBuff, "U300", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;

		if (AlgMode == 1) {
			OutData.setMac(UnionUtil.BytesCopy(outBytes, offset, 16));
			offset += 16;
		}

		byte[] bytesLen = UnionUtil.BytesCopy(outBytes, offset, 4);
		int iLen = Integer.parseInt(new String(bytesLen), 16) * 2;
		// int iLen=48;
		offset += 4;

		OutData.setLen(iLen);
		OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, iLen));
		offset += iLen;

		if (AlgMode == 2)// 校验值
		{

			OutData.setICV(UnionUtil.BytesCopy(outBytes, offset, 16));

		}
		/*
		 * OutData.setKEK(UnionUtil.BytesCopy(outBytes,offset+16,4)); offset +=
		 * 4;
		 * 
		 * OutData.setLsData(UnionUtil.BytesCopy(outBytes,offset+16+4,48));
		 * offset += 48;
		 */

		return OutData;
	}

	private EMResult HSM_R1(int RandBytes) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "R1";
		cmdLen += 2;

		// rand number's bytes
		// String tempcmd=new String(Integer.toHexString(RandBytes));
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(RandBytes), 5);
		// cmdBuff+=tempcmd;
		cmdLen += 5;

		outStr = commWithHsm(cmdLen, cmdBuff, "R200", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;

		OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, RandBytes));
		offset += RandBytes;

		return OutData;
	}

	private EMResult HSM_W0(int AlgFlag, int Scheme, int KeyIndex,
			// int KeyNum,
			int DivNum, String DivData, int processFlag, String processData, int macFillType, String IV, int macDataLen, String macData,
			int macSelfLenFlag, String srcMAC) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		String Lscheme2 = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "W0";
		cmdLen += 2;

		cmdBuff += Integer.toHexString(AlgFlag);
		cmdLen += 1;

		// Scheme
		keyLenType = getMACFlagFromMobRule(Scheme);
		cmdBuff += keyLenType;
		cmdLen += 1;

		// root-key
		cmdBuff += "K";
		cmdLen += 1;

		cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(KeyIndex), 3);
		cmdLen += 3;

		// 增加离散次数为0
		cmdBuff += Integer.toString(DivNum);
		cmdLen += 1;
		if (DivNum >= 1) {
			cmdBuff += DivData;
			cmdLen += 32 * DivNum;

		}

		/*
		 * cmdBuff +=Integer.toString(DivNum); cmdLen+=1;
		 * 
		 * //Div data cmdBuff+=DivData; cmdLen +=32*DivNum;
		 */
		// process flag
		cmdBuff += Integer.toString(processFlag);
		cmdLen += 1;
		if (processFlag == 1) {
			// process data
			cmdBuff += processData;
			cmdLen += processData.length();
		}
		//
		// macFillType
		cmdBuff += Integer.toString(macFillType);
		cmdLen += 1;

		// IV
		cmdBuff += IV;
		cmdLen += IV.length();

		// macDataLen
		// cmdBuff
		// +=UnionUtil.LeftAddZero(Integer.toHexString(macDataLen*2),4).toUpperCase();
		cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(macData.length()), 4).toUpperCase();
		cmdLen += 4;
		System.out.println("aaaa111111=" + macDataLen);
		System.out.println("aaaa=" + Integer.toHexString(macDataLen));
		System.out.println("aaaacccc=" + UnionUtil.LeftAddZero(Integer.toHexString(macDataLen), 4).toUpperCase());

		// macData
		cmdBuff += macData;
		cmdLen += macData.length();

		// macSelfLenFlag
		cmdBuff += Integer.toString(macSelfLenFlag);
		cmdLen += 1;

		// source MAC
		if (AlgFlag == 2) {
			// process srcMAC
			cmdBuff += srcMAC;
			cmdLen += srcMAC.length();
		}

		outStr = commWithHsm(cmdLen, cmdBuff, "W100", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;
		if (AlgFlag != 2) {
			switch (macSelfLenFlag) {
			case 1:
				// OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 8));
				OutData.setLsData(UnionUtil.HexString2Bytes(new String(UnionUtil.BytesCopy(outBytes, offset, 8), "ISO-8859-1")));
				offset += 8;
				break;
			case 2:
				// OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, 16));
				OutData.setLsData(UnionUtil.HexString2Bytes(new String(UnionUtil.BytesCopy(outBytes, offset, 16), "ISO-8859-1")));
				offset += 16;
				break;

			}
		}

		if (AlgFlag == 3) {
			switch (macSelfLenFlag) {
			case 1:
				// OutData.setICV(UnionUtil.BytesCopy(outBytes, offset, 8));
				OutData.setICV(UnionUtil.HexString2Bytes(new String(UnionUtil.BytesCopy(outBytes, offset, 8), "ISO-8859-1")));
				offset += 8;
				break;
			case 2:
				// OutData.setICV(UnionUtil.BytesCopy(outBytes, offset, 16));
				OutData.setICV(UnionUtil.HexString2Bytes(new String(UnionUtil.BytesCopy(outBytes, offset, 16), "ISO-8859-1")));
				offset += 16;
				break;

			}
		}

		return OutData;
	}

	// result = this.HSM_W2(AlgFlag,OperateFlag,"ZMK",
	// KeyIndex,DivNum,sData1,SessionKeyFlag,sData2,PadFlag,DataLen,sData3);
	private EMResult HSM_W2(int AlgFlag, int Scheme, String KeyType, String mkKey, int Key1Index, int Div1Num, String Div1Data,
			int process1Flag, String process1Data, int Key2Index, int Div2Num, String Div2Data, int process2Flag, String process2Data,
			int dataFillType, int dataLen, String data) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;
		String keyScheme = null;
		String Lscheme2 = null;
		int cmdLen = 0;
		int offset = 0;

		// command
		cmdBuff = "W2";
		cmdLen += 2;

		// Scheme
		keyLenType = getLSFlagFromMobRule(AlgFlag);
		cmdBuff += keyLenType;
		cmdLen += 1;

		cmdBuff += Integer.toString(Scheme);
		cmdLen += 1;

		// key type
		keyScheme = getKeyScheme(KeyType);
		cmdBuff += keyScheme;
		cmdLen += 3;
		// cmdBuff+=Integer.toString(KeyType);
		// cmdLen+=3;

		// root-key
		if (mkKey != null && mkKey.length() != 0) {
			cmdBuff += getKeyXYZScheme(mkKey.length()) + mkKey;
			cmdLen += mkKey.length() + 1;
		} else {
			cmdBuff += "K";
			cmdLen += 1;

			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(Key1Index), 3);
			cmdLen += 3;
		}

		// 增加离散次数为0
		cmdBuff += Integer.toString(Div1Num);
		cmdLen += 1;
		if (Div1Num >= 1) {
			cmdBuff += Div1Data;
			cmdLen += 32 * Div1Num;

		}

		/*
		 * cmdBuff +=Integer.toString(DivNum); cmdLen+=1;
		 * 
		 * //Div data cmdBuff+=DivData; cmdLen +=32*DivNum;
		 */
		// process flag
		cmdBuff += Integer.toString(process1Flag);
		cmdLen += 1;

		if (process1Flag == 1) {
			// process data
			cmdBuff += process1Data;
			cmdLen += process1Data.length();
		}
		// add by lisq 2011-12-12
		if (Scheme == 2) {
			// root-key
			cmdBuff += "K";
			cmdLen += 1;

			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(Key2Index), 3);
			cmdLen += 3;

			// 增加离散次数为0
			cmdBuff += Integer.toString(Div2Num);
			cmdLen += 1;
			if (Div2Num >= 1) {
				cmdBuff += Div2Data;
				cmdLen += 32 * Div2Num;

			}

			// process flag
			cmdBuff += Integer.toString(process2Flag);
			cmdLen += 1;

			if (process2Flag == 1) {
				// process data
				cmdBuff += process2Data;
				cmdLen += process2Data.length();
			}
		}
		// add by lisq 2011-12-12 end
		// dataFillType
		cmdBuff += Integer.toString(dataFillType);
		cmdLen += 1;

		// DataLen
		// cmdBuff +=UnionUtil.LeftAddZero(Integer.toString(dataLen),3);
		// cmdBuff += String.format("%03d", dataLen);
		cmdBuff += String.format("%03d", data.length());
		cmdLen += 3;
		/*
		 * if (keyLenType == "1" || keyLenType == "2") { cmdBuff +=
		 * UnionUtil.LeftAddZero(Integer.toString(dataLen + 16), 3); cmdLen +=
		 * 3; } else { cmdBuff +=
		 * UnionUtil.LeftAddZero(Integer.toString(dataLen), 3); cmdLen += 3; }
		 */

		// Data
		cmdBuff += data;
		cmdLen += data.length();

		outStr = commWithHsm(cmdLen, cmdBuff, "W300", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");

		offset = 4;
		byte[] bytesLen = UnionUtil.BytesCopy(outBytes, offset, 3);
		int iLen = Integer.parseInt(new String(bytesLen));
		offset += 3;

		OutData.setLen(iLen);
		OutData.setLsData(UnionUtil.BytesCopy(outBytes, offset, iLen));
		offset += iLen;

		return OutData;
	}

	private EMResult HSM_W3(String Kyes) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String keyLenType = null;

		int cmdLen = 0;
		int offset = 0;

		cmdBuff = "W3X";
		cmdLen += 3;

		cmdBuff += Kyes;
		cmdLen += 32;

		/*
		 * keyLenType = getXYZFlagFromMobRule(Alg_flag);
		 * 
		 * cmdBuff+=keyLenType; cmdLen+=1;
		 * 
		 * 
		 * switch(Alg_flag) { case 0x01: cmdBuff+=Kyes; cmdLen +=16; break; case
		 * 0x02: cmdBuff+=Kyes; cmdLen +=32; break; case 0x03: cmdBuff+=Kyes;
		 * cmdLen +=48; break; default: throw new Exception("KEY长度不在规定范围内"); }
		 */

		outStr = commWithHsm(cmdLen, cmdBuff, "W400", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		offset = 4;
		OutData.setMac(UnionUtil.BytesCopy(outBytes, outBytes.length - 16));

		return OutData;
	}

	private EMResult HSM_A0(int WK_len, String keyType, int Mode, int KEK_len, String KEK_mk) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;
		int WK_len2;

		cmdBuff = "A0";
		cmdLen += 2;

		/* 模式标识 0=产生密钥,1=产生密钥并在ZMK下加密,2＝产生密钥并在公钥下加密 */
		if (Mode != 0 && Mode != 1) {
			throw new Exception("模式标识不在规定范围内");
		}

		cmdBuff += Mode;
		cmdLen += 1;

		/* 输出密钥类型 */
		String keyScheme = getKeyScheme(keyType);
		cmdBuff += keyScheme;
		cmdLen += 3;

		/* 密钥(长度）方案 */
		String keyLenType = getXYZFlagFromMobRule(WK_len);

		cmdBuff += keyLenType;
		cmdLen += 1;

		if (Mode == 1) {
			/* 32位ZMK/X+32位ZMK/A+3H */
			/*
			 * 仅当模式标识为1时才显示该ZMK域。
			 * 用来产生随机密钥的LMK对下加密的密钥。1A+3H表示使用K+3位索引方式读取加密机内保存密钥。
			 */
			if (KEK_mk.length() != 32 && KEK_mk.length() != 33) {
				throw new Exception("KEK_mk长度不对!");
			}

			cmdBuff += KEK_mk;
			cmdLen += KEK_mk.length();

			cmdBuff += keyLenType;
			cmdLen += 1;

		}

		outStr = commWithHsm(cmdLen, cmdBuff, "A100", OutData);
		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;

		OutData.setMac(UnionUtil.BytesCopy(outBytes, outBytes.length - 16));

		OutData.setLmkWk(getKeyBytesFromBValWithXYZ(outBytes, offset));
		if (outBytes[offset] == 'X' || outBytes[offset] == 'Y' || outBytes[offset] == 'Z') {
			offset += 1;
		}
		offset += OutData.getLmkWk().length;

		if (Mode == 1) {
			OutData.setKekWk(getKeyBytesFromBValWithXYZ(outBytes, offset));
		}
		return OutData;
	}

	private EMResult HSM_MS(int MAK_len, int Mode, int mackeyType, int messType, String MAK_mk, String IV_Mac, int MAC_len, String MAC_data

	) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String aaa = null;
		String bbb = null;
		int cmdLen = 0;

		cmdBuff = "MS";
		cmdLen += 2;

		/* 模式标识 */
		if (Mode != 0 && Mode != 1 && Mode != 2 && Mode != 3)
			throw new Exception("HSM_MS,模式标识不在规定范围内");

		cmdBuff += Mode;
		cmdLen += 1;

		/*
		 * 密钥类型模式： 0=终端认证密钥 1=区域认证密钥
		 */
		if (mackeyType != 0 && mackeyType != 1)
			throw new Exception("HSM_MS,密钥类型不在规定范围内");

		cmdBuff += mackeyType;
		cmdLen += 1;

		/*
		 * 密钥长度： 0=单倍长度DES密钥 1=双倍长度DES密钥
		 */
		if (MAK_len != 0 && MAK_len != 1)
			throw new Exception("HSM_MS,密钥长度不在规定范围内");

		cmdBuff += MAK_len;
		cmdLen += 1;

		/* 0=消息数据为二进制 1=消息数据为扩展十六进制钥 */
		if (messType != 0 && messType != 1)

			throw new Exception("HSM_MS,密钥长度不在规定范围内");

		cmdBuff += messType;
		cmdLen += 1;

		/* 密钥 */
		/*
		 * 对应LMK对下加密的密钥。 TAK－LMK对（16-17）下加密 ZAK－LMK对（26-27）下加密
		 */
		cmdBuff += MAK_mk;
		cmdLen += MAK_mk.length();

		/* IV_MAC 初始值，仅当消息块号为2或3时显示 */
		if (Mode == 2 || Mode == 3) {
			if (IV_Mac.length() != 8)
				throw new Exception("HSM_MS,IV_Mac错");
			cmdBuff += IV_Mac;
			cmdLen += IV_Mac.length();
		}

		/* MAC数据长度 */
		this.CheckValue(MAC_len, 1, 9999, "MAC数据长度为[1-9999]的整数");
		if (messType == 0) {
			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(MAC_len).toUpperCase(), 4);
		} else {
			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(MAC_len / 2).toUpperCase(), 4);
		}
		cmdLen += 4;

		/* MAC 数据 */
		if (MAC_len > 0) {
			this.CheckNull(MAC_data, "MAC数据内容为空");
			cmdBuff += MAC_data;
			cmdLen += MAC_data.length();
		}

		outStr = commWithHsm(cmdLen, cmdBuff, "MT00", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;

		OutData.setData(UnionUtil.BytesCopy(outBytes, offset));

		return OutData;
	}

	private EMResult HSM_MU(int MAK_len, int Mode, int mackeyType, int messType, String MAK_mk, String IV_Mac, int MAC_len, String MAC_data

	) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;

		int cmdLen = 0;

		cmdBuff = "MU";
		cmdLen += 2;

		/* 模式标识 */
		if (Mode != 0 && Mode != 1 && Mode != 2 && Mode != 3)
			throw new Exception("HSM_MU,模式标识不在规定范围内");

		cmdBuff += Mode;
		cmdLen += 1;

		/*
		 * 密钥类型模式： 0=终端认证密钥 1=区域认证密钥
		 */
		if (mackeyType != 0 && mackeyType != 1)
			throw new Exception("HSM_MU,密钥类型不在规定范围内");

		cmdBuff += mackeyType;
		cmdLen += 1;

		/*
		 * 密钥长度： 0=单倍长度DES密钥 1=双倍长度DES密钥
		 */
		if (MAK_len != 0 && MAK_len != 1)
			throw new Exception("HSM_MU,密钥长度不在规定范围内");

		cmdBuff += MAK_len;
		cmdLen += 1;

		/* 0=消息数据为二进制 1=消息数据为扩展十六进制钥 */
		if (messType != 0 && messType != 1)

			throw new Exception("HSM_MU,密钥长度不在规定范围内");

		cmdBuff += messType;
		cmdLen += 1;

		/* 密钥 */
		/*
		 * 对应LMK对下加密的密钥。 TAK－LMK对（16-17）下加密 ZAK－LMK对（26-27）下加密
		 */
		cmdBuff += MAK_mk;
		cmdLen += MAK_mk.length();

		/* IV_MAC 初始值，仅当消息块号为2或3时显示 */
		if (Mode == 2 || Mode == 3) {
			if (IV_Mac.length() != 8)
				throw new Exception("HSM_MU,IV_Mac错");
			cmdBuff += IV_Mac;
			cmdLen += IV_Mac.length();
		}

		/* MAC数据长度 */
		this.CheckValue(MAC_len, 1, 9999, "MAC数据长度为[1-9999]的整数");
		if (messType == 0) {
			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(MAC_len).toUpperCase(), 4);
		} else {
			cmdBuff += UnionUtil.LeftAddZero(Integer.toHexString(MAC_len / 2).toUpperCase(), 4);
		}
		cmdLen += 4;

		/* MAC 数据 */
		if (MAC_len > 0) {
			this.CheckNull(MAC_data, "MAC数据内容为空");
			cmdBuff += MAC_data;
			cmdLen += MAC_data.length();
		}

		outStr = commWithHsm(cmdLen, cmdBuff, "MV00", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");
		int offset = 4;

		OutData.setData(UnionUtil.BytesCopy(outBytes, offset));

		return OutData;
	}

	/**
	 * POSP-test
	 */
	private EMResult HSM_19(int Alg_flag) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String Alg_flag2 = null;
		int cmdLen = 0;

		cmdBuff = "19";
		cmdLen += 2;

		// this.CheckNull(Alg_flag, "输入参数[Alg_flag]为空");
		// cmdBuff+=Alg_flag;
		// cmdLen+=1;

		/* 根据移动定义算出加密机对应的加密算法标志和 */
		switch (Alg_flag) {
		case 0x01:
			Alg_flag2 = "X";
			break;
		case 0x02:
			Alg_flag2 = "Y";
			break;
		case 0x03:
			Alg_flag2 = "Z";
			break;
		default:
			throw new Exception("算法选项不在规定范围内");
		}
		cmdBuff += Alg_flag2;
		cmdLen += 1;

		/* 计算长度 */
		cmdStr = getPackageLen(cmdLen) + cmdBuff;

		/* 数据交换 */
		outStr = ExchangeData(cmdStr);

		/* 数据检验 */
		String retcode = this.CheckResult(outStr, "1A00");
		if (retcode == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (retcode.endsWith("00")) {
			OutData.setMac(outStr.substring(outStr.length() - 16, outStr.length()).getBytes());

			switch (Alg_flag) {
			case 0x01:
				OutData.setLmkKek(outStr.substring(4, 16 + 4).getBytes());
				OutData.setKEK(outStr.substring(20, 16 + 20).getBytes());

				break;
			case 0x02:
				OutData.setLmkKek(outStr.substring(4, 32 + 4).getBytes());
				OutData.setKEK(outStr.substring(36, 32 + 36).getBytes());
				break;
			case 0x03:
				OutData.setLmkKek(outStr.substring(4, 48 + 4).getBytes());
				OutData.setKEK(outStr.substring(52, 48 + 52).getBytes());
				break;
			default:
				throw new Exception("KEY长度不在规定范围内");
			}

			OutData.setRecode(0);
		} else {
			OutData.setRecode(Integer.parseInt(retcode));
		}
		return OutData;
	}

	private EMResult HSM_1D(int WK_len, int WK_type, int KEK_len, String KEK_mk

	) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;
		int WK_len2;

		cmdBuff = "1D";
		cmdLen += 2;

		/* 根据移动定义算出加密机对应的加密算法标志和 */
		switch (WK_len) {
		case 0x01:
			WK_len2 = 1;
			break;
		case 0x02:
			WK_len2 = 2;
			break;
		case 0x03:
			WK_len2 = 3;
			break;
		default:
			throw new Exception("算法选项不在规定范围内");
		}
		cmdBuff += WK_len2;
		cmdLen += 1;

		/* KEK_MK */
		cmdBuff += KEK_mk;
		cmdLen += KEK_mk.length();

		/* 计算长度 */
		cmdStr = getPackageLen(cmdLen) + cmdBuff;

		/* 数据交换 */
		outStr = ExchangeData(cmdStr);

		/* 数据检验 */
		String retcode = this.CheckResult(outStr, "1A00");
		if (retcode == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (retcode.endsWith("00")) {
			switch (WK_len) {
			case 0x01:
				OutData.setLmkWk(outStr.substring(4, 16 + 4).getBytes());
				OutData.setKekWk(outStr.substring(20, 16 + 20).getBytes());

				break;
			case 0x02:
				OutData.setLmkWk(outStr.substring(4, 32 + 4).getBytes());
				OutData.setKekWk(outStr.substring(36, 32 + 36).getBytes());
				break;
			case 0x03:
				OutData.setLmkWk(outStr.substring(4, 48 + 4).getBytes());
				OutData.setKekWk(outStr.substring(52, 48 + 52).getBytes());
				break;
			default:
				throw new Exception("KEY长度不在规定范围内");
			}
			// OutData.setData(outStr.substring(4, outStr.length()).getBytes());
			OutData.setRecode(0);
		} else {
			OutData.setRecode(Integer.parseInt(retcode));
		}
		return OutData;
	}

	private EMResult HSM_82(int MAK_len, int MAK_type, String MAK_mk, int MAC_len, String MAC_data

	) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String aaa = null;
		String bbb = null;
		int cmdLen = 0;

		cmdBuff = "82";
		cmdLen += 2;

		/* 根据移动定义算出加密机对应的加密算法标志和 */
		switch (MAK_type) {
		case 0x01:
			aaa = "L";
			break;
		case 0x02:
			aaa = "M";
			break;
		case 0x03:
			aaa = "N";
			break;
		default:
			throw new Exception("算法选项不在规定范围内");
		}
		cmdBuff += aaa;
		cmdLen += 1;

		cmdBuff += "L";
		cmdLen += 1;

		switch (MAK_len) {
		case 0x02:
			bbb = "U";
			break;
		case 0x03:
			bbb = "T";
			break;
		default:
			throw new Exception("MAK长度选项不在规定范围内");
		}
		cmdBuff += bbb;
		cmdLen += 1;
		/* MAK_MK */
		cmdBuff += MAK_mk;
		cmdLen += MAK_mk.length();

		/* MAC数据长度 */
		this.CheckValue(MAC_len, 1, 999, "MAC数据长度为[1-999]的整数");
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(MAC_len), 3);
		cmdLen += 3;

		/* MAC 数据 */
		if (MAC_len > 0) {
			this.CheckNull(MAC_data, "MAC数据内容为空");
			cmdBuff += MAC_data;
			cmdLen += MAC_data.length();
		}

		/* 计算长度 */
		cmdStr = getPackageLen(cmdLen) + cmdBuff;

		/* 数据交换 */
		outStr = ExchangeData(cmdStr);

		/* 数据检验 */
		String retcode = this.CheckResult(outStr, "8300");
		if (retcode == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (retcode.endsWith("00")) {
			OutData.setData(outStr.substring(4, outStr.length()).getBytes());
			OutData.setRecode(0);
		} else {
			OutData.setRecode(Integer.parseInt(retcode));
		}
		return OutData;
	}

	private EMResult HSM_83(int MAK_len, int MAK_type, String MAK_mk, String Mac, int MAC_len, String MAC_data

	) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		String aaa = null;
		String bbb = null;
		int cmdLen = 0;

		cmdBuff = "83";
		cmdLen += 2;

		/* 根据移动定义算出加密机对应的加密算法标志和 */
		switch (MAK_type) {
		case 0x01:
			aaa = "L";
			break;
		case 0x02:
			aaa = "M";
			break;
		case 0x03:
			aaa = "N";
			break;
		default:
			throw new Exception("算法选项不在规定范围内");
		}
		cmdBuff += aaa;
		cmdLen += 1;

		cmdBuff += "L";
		cmdLen += 1;

		switch (MAK_len) {
		case 0x02:
			bbb = "U";
			break;
		case 0x03:
			bbb = "T";
			break;
		default:
			throw new Exception("MAK长度选项不在规定范围内");
		}
		cmdBuff += bbb;
		cmdLen += 1;
		/* MAK_MK */
		cmdBuff += MAK_mk;
		cmdLen += MAK_mk.length();

		/* MAC */
		cmdBuff += Mac;
		cmdLen += 8;

		/* MAC数据长度 */
		this.CheckValue(MAC_len, 1, 999, "MAC数据长度为[1-999]的整数");
		cmdBuff += UnionUtil.LeftAddZero(Integer.toString(MAC_len), 3);
		cmdLen += 3;

		/* MAC 数据 */
		if (MAC_len > 0) {
			this.CheckNull(MAC_data, "MAC数据内容为空");
			cmdBuff += MAC_data;
			cmdLen += MAC_data.length();
		}

		/* 计算长度 */
		cmdStr = getPackageLen(cmdLen) + cmdBuff;

		/* 数据交换 */
		outStr = ExchangeData(cmdStr);

		/* 数据检验 */
		String retcode = this.CheckResult(outStr, "8400");
		if (retcode == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (retcode.endsWith("00")) {
			OutData.setData(outStr.substring(4, outStr.length()).getBytes());
			OutData.setRecode(0);
		} else {
			OutData.setRecode(Integer.parseInt(retcode));
		}
		return OutData;
	}

	/*
	 * 导出明文公钥
	 */
	private EMResult HSM_C7(int pkindex, int pktype) throws Exception {
		EMResult OutData = new EMResult();
		String cmdBuff = "";
		String cmdStr = null;
		String outStr = null;
		int cmdLen = 0;

		cmdBuff = "C7";
		cmdLen += 2;

		if (pkindex < 0 || pkindex > 20) {
			throw new Exception("HSM_C7,参数错！");
		}

		cmdBuff += Integer.toString(pkindex);
		cmdLen += 2;

		outStr = commWithHsm(cmdLen, cmdBuff, "C800", OutData);

		if (outStr == null) {
			OutData.setRecode(-1);
			return OutData;
		}
		if (OutData.getRecode() != 0) {
			return OutData;
		}

		byte[] outBytes = outStr.getBytes("ISO-8859-1");

		if (pktype == 1) {
			// 取裸公钥
			int offset = 10;
			int offsetEnd = 128;
			OutData.setPk(UnionUtil.BytesCopy(outBytes, offset, offsetEnd));
		} else if (pktype == 2) {
			// 取DER编码的公钥
			int offset = 4;
			OutData.setPk(UnionUtil.BytesCopy(outBytes, offset));
		}
		return OutData;
	}

	/**
	 * 
	 * @param ver
	 * @param index
	 * @param hsmVer
	 * @param hsmIndex
	 * @param hsmGroupNo
	 */
	private void CaclByVerAndIndex(int ver, int index, int[] hsmVer, int[] hsmIndex, int[] hsmGroupNo) {
		int TotalIndex = 0;
		TotalIndex = ver * index;

		hsmVer[0] = TotalIndex / 320;
		hsmIndex[0] = (TotalIndex % 320) / 10;
		hsmGroupNo[0] = (TotalIndex % 320) % 10 + 1;

	}

	/**
	 * 
	 * @param ver
	 * @param index
	 * @param hsmVer
	 * @param hsmIndex
	 * @param hsmGroupNo
	 */
	/*
	 * private void CaclByVerAndIndexEx(int ver,int index,int[] hsmVer,int[]
	 * hsmIndex,int[] hsmGroupNo) { int TotalIndex=0; TotalIndex=index;
	 * 
	 * hsmVer[0]=0; hsmGroupNo[0]=TotalIndex/16; hsmIndex[0]=TotalIndex%16+1;
	 * 
	 * }
	 */

	private void CaclByVerAndIndexEx(int ver, int index, int[] hsmVer, int[] hsmIndex, int[] hsmGroupNo) {
		int TotalIndex = 0;
		String sIndex = Integer.toString(index);
		int iIndex = Integer.parseInt(sIndex);
		TotalIndex = iIndex;

		hsmVer[0] = TotalIndex / 320;
		hsmGroupNo[0] = (TotalIndex % 320) / 10;
		hsmIndex[0] = (TotalIndex % 320) % 10 + 1;
	}

	/* 计算报长度 */
	private String getPackageLen(int cmdLen) throws Exception {
		Integer h1, h2;
		byte head[] = { 0, 0 };
		String cmdLenStr = null;

		h1 = new Integer(cmdLen / 256);
		h2 = new Integer(cmdLen % 256);
		head[0] = h1.byteValue();
		head[1] = h2.byteValue();
		cmdLenStr = new String(head, "ISO-8859-1");
		return cmdLenStr;
	}

	/* 和加密机进行数据交换 */
	private String ExchangeData(String cmdStr) {
		UnionSocket sock = null;
		String outStr = null;

		try {
			sock = new UnionSocket();
			if (!sock.connectHSM(HsmHost, HsmPort)) {
				logger.error("加密机连接失败");
				return null;
			}
			outStr = sock.ExchangeData(cmdStr);
			if (sock != null) {
				sock.Close();
				sock = null;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return outStr;
	}

	/* 获取接收数据的消息头及返回值和前4个字符并检验是否返回返回码 */
	private String CheckMessHeadAndResult(String recv, String MessHead, String cmdHeaderSrc) {
		String MessHeadDest = "";
		String cmdHeaderDest = "";
		String retcode = "";

		if (recv == null) {
			logger.error("接收数据为空");
			return null;
		}

		if (this.HsmMessaLen <= 0)
			return CheckResult(recv, cmdHeaderSrc);

		if (recv.trim() == "" || recv.trim().length() < (this.HsmMessaLen + 4)) {
			logger.error("接收数据长度小于" + (this.HsmMessaLen + 4));
			return null;
		}

		MessHeadDest = recv.substring(0, this.HsmMessaLen);
		if (MessHead.equals(MessHeadDest) == false) {
			logger.error("接收数据的消息头不一致.收到的MessHead[" + MessHead + " 期望的MessHeadDest:[" + MessHeadDest + "]");
			return null;
		} else
			return CheckResult(recv.substring(this.HsmMessaLen), cmdHeaderSrc);
	}

	/* 获取接收数据的返回值和前4个字符并检验是否返回返回码 */
	private String CheckResult(String recv, String cmdHeaderSrc) {
		String cmdHeaderDest = "";
		String retcode = "";

		if (recv == null) {
			logger.error("接收数据为空");
			return null;
		}
		if (recv.trim() == "" || recv.trim().length() < 4) {
			logger.error("接收数据长度小于4");
			return null;
		}
		cmdHeaderDest = recv.substring(0, 4);
		if (cmdHeaderDest.equals(cmdHeaderSrc)) {
			retcode = "00";
		} else {
			retcode = recv.substring(2, 4);
		}
		return retcode;
	}

	/* 检验输入参数是否为空，并抛出异常 */
	private void CheckNull(String sParam, String sThrowMsg) throws Exception {
		if (sParam == null) {
			throw new Exception(sThrowMsg);
		}
	}

	/* 检验输入参数长度是否合法，并抛出异常 */
	private void CheckLength(String sParam, int len, String sThrowMsg) throws Exception {
		if (sParam.length() != len) {
			throw new Exception(sThrowMsg);
		}
	}

	/* 检验输入参数是否在给出的区间内，并抛出异常 */
	private void CheckValue(int iParam, int iStart, int iEnd, String sThrowMsg) throws Exception {
		if (iParam < iStart || iParam > iEnd) {
			throw new Exception(sThrowMsg);
		}
	}

	/* 转换CBCD码到十六进制字符串，并返回字节数 */
	private String ConvertCBCD2String(String sSrc, int[] len) {
		if (sSrc == null)
			return null;

		String Str = sSrc.trim();
		int len1 = Str.length();

		len[0] = len1 / 2;
		byte bytStr[] = UnionUtil.HexString2Bytes(Str);
		byte bytDest[] = new byte[len1];
		UnionUtil.CBCD2HexStr(bytStr, bytDest, len1 / 2);
		return new String(bytDest);

	}

	/* 转换CBCD码到十六进制字符串，并返回字节数 */
	private String ConvertCBCD2String(byte[] bytSrc, int[] len) {
		if (bytSrc.length <= 0)
			return null;

		int len1 = bytSrc.length;
		len[0] = len1;
		byte bytDest[] = new byte[len1 * 2];
		UnionUtil.CBCD2HexStr(bytSrc, bytDest, len1);

		return new String(bytDest);
	}

	/* 根据移动定义算出加密机对应的密钥长度标志 */
	private String getXYZFlagFromMobRule(int Alg_flag) throws Exception {
		String Flag = "";
		switch (Alg_flag) {
		case 1:
			Flag = "Z";
			break;
		case 2:
			Flag = "X";
			break;
		case 3:
			Flag = "Y";
			break;
		default:
			throw new Exception("密钥长度标识不在规定范围内");
		}
		return Flag;
	}

	/* 根据移动定义算出加密机对应的离散算法标志 */
	private String getLSFlagFromMobRule(int Alg_flag) throws Exception {
		String Flag = "";
		switch (Alg_flag) {
		case 0x88:
			Flag = "0";
			break;
		case 0x82:
			Flag = "1";
			break;
		case 0x84:
			Flag = "2";
			break;
		case 0x83:
			Flag = "3";
			break;
		case 0x81:
			Flag = "4";
			break;
		default:
			throw new Exception("密钥长度标识不在规定范围内");
		}
		return Flag;
	}

	/* 根据移动定义算出加密机对应的MAC算法标志 */
	private String getMACFlagFromMobRule(int Alg_flag) throws Exception {
		String Flag = "";
		switch (Alg_flag) {
		case 0x88:
			Flag = "0";
			break;
		case 0x82:
			Flag = "4";
			break;
		case 0x84:
			Flag = "1";
			break;
		case 0x83:
			Flag = "3";
			break;
		case 0x80:
			Flag = "2";
			break;
		case 0x81:
			Flag = "5";
			break;
		default:
			throw new Exception("密钥长度标识不在规定范围内");
		}
		return Flag;
	}

	/* 根据移动定义算出加密机对应的密钥类型 */
	private String getKeyTypeFromMobType(int iKeyType) throws Exception {
		String keyType = "";
		switch (iKeyType) {
		case 0x01: // ：密钥加密密钥
			keyType = "ZMK";
			break;
		case 0x02: // ：PIN加密密钥
			keyType = "ZPK";
			break;
		case 0x03: // ：MAC计算密钥
			keyType = "ZAK";
			break;
		case 0x04: // ：数据加密密钥
			keyType = "ZEK";
			break;
		default:
			throw new Exception("密钥类型不在规定范围内");
		}
		return keyType;
	}

	private String getKeyScheme(String keyType) throws Exception {
		String keyScheme = "";
		if (keyType.equalsIgnoreCase("ZMK"))
			keyScheme = "000";
		else if (keyType.equalsIgnoreCase("ZPK"))
			keyScheme = "001";
		else if (keyType.equalsIgnoreCase("TAK"))
			keyScheme = "003";
		else if (keyType.equalsIgnoreCase("ZAK"))
			keyScheme = "008";
		else if (keyType.equalsIgnoreCase("ZEK"))
			keyScheme = "00A";
		else if (keyType.equalsIgnoreCase("MK-SMI"))
			keyScheme = "209";
		else if (keyType.equalsIgnoreCase("MK-AC"))
			keyScheme = "109";
		else
			throw new Exception("密钥类型不在规定范围内");
		return keyScheme;
	}

	private String getKeyFromKeyValueWithXYZ(String keyValue) throws Exception {
		int keylen = 16;
		int offset = 0;
		String xyzStr = "";
		String key = "";
		if (keyValue == null || keyValue.length() == 0)
			return "";
		xyzStr = keyValue.substring(0, 1);
		if (xyzStr.equalsIgnoreCase("Z")) {
			keylen = 16;
			offset = 1;
		} else if (xyzStr.equalsIgnoreCase("X")) {
			keylen = 32;
			offset = 1;
		} else if (xyzStr.equalsIgnoreCase("Y")) {
			keylen = 48;
			offset = 1;
		} else {
			keylen = 16;
			offset = 0;
		}
		key = keyValue.substring(offset, offset + keylen);
		return key;
	}

	private byte[] getKeyBytesFromBValWithXYZ(byte[] bytes, int offset) throws Exception {
		if (bytes == null || bytes.length == 0)
			return null;
		byte[] keyBytes = null;
		switch (bytes[offset]) {
		case 'Z':
			keyBytes = UnionUtil.BytesCopy(bytes, offset + 1, 16);
			break;
		case 'X':
			keyBytes = UnionUtil.BytesCopy(bytes, offset + 1, 32);
			break;
		case 'Y':
			keyBytes = UnionUtil.BytesCopy(bytes, offset + 1, 48);
			break;
		default:
			keyBytes = UnionUtil.BytesCopy(bytes, offset, 16);
			break;
		}
		return keyBytes;
	}

	private String getRacalKeyString(int keyLength, String keyValue) throws Exception {
		String keyString = "";
		if (keyLength < 0 || keyLength != keyValue.length()) {
			throw new Exception("密钥长度错！");
		}
		switch (keyLength) {
		case 16:
			keyString = keyValue;
			break;
		case 32:
			keyString += "X";
			keyString += keyValue;
			break;
		case 48:
			keyString += "Y";
			keyString += keyValue;
			break;
		default:
			throw new Exception("密钥长度不在规定范围内");
		}
		return keyString;
	}

	private String get12LenAccountNumber(int lenOfAccNo, String accNo) throws Exception {
		String accNoOf12Len = "";
		if (lenOfAccNo < 0 || lenOfAccNo != accNo.length()) {
			throw new Exception("帐号长度错！");
		}
		if (lenOfAccNo >= 13)
			accNoOf12Len = accNo.substring(lenOfAccNo - 13, lenOfAccNo - 1);
		else {
			accNoOf12Len = UnionUtil.LeftAddZero(accNoOf12Len, 12);
		}
		return accNoOf12Len;
	}

	private String getKeyXYZScheme(int length) throws Exception {
		String keyScheme = "";
		switch (length) {
		case 16:
			keyScheme = "Z";
			break;
		case 32:
			keyScheme = "X";
			break;
		case 48:
			keyScheme = "Y";
			break;
		default:
			throw new Exception("密钥长度不在规定范围内");
		}
		return keyScheme;
	}

	/* 根据移动定义算出加密机对应的MAC算法标志--中央后台部分 */
	private String getMACFlagFromMobRule_Center(int Alg_flag) throws Exception {
		String Flag = "";
		switch (Alg_flag) {
		case 0:
			Flag = "0";
			break;
		case 1:
			Flag = "1";
			break;
		case 2:
			Flag = "2";
			break;
		default:
			throw new Exception("密钥长度标识不在规定范围内");
		}
		return Flag;
	}

	private String getDivNumScheme(int Div_Num) throws Exception {
		String LScheme = "";
		switch (Div_Num) {
		case 1:
			LScheme = "1";
			break;
		case 2:
			LScheme = "2";
			break;
		case 3:
			LScheme = "3";
			break;
		default:
			throw new Exception("离散次数不在规定范围内");
		}
		return LScheme;
	}

	/********************************* <私有方法End> **************************************/
}
