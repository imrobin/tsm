package com.justinmobile.tsm.cms2ac.engine;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.intToHexBytes;
import static com.justinmobile.core.utils.ByteUtils.longToHexBytes;
import static com.justinmobile.core.utils.ByteUtils.subArray;
import static com.justinmobile.core.utils.ByteUtils.toHexString;
import static com.justinmobile.core.utils.ConvertUtils.hexString2ByteArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.LvObject;
import com.justinmobile.core.utils.MutilTagTlvObject;
import com.justinmobile.core.utils.MutilTagTlvObject.ValueEntry;
import com.justinmobile.core.utils.TlvObject;
import com.justinmobile.core.utils.security.Sha1Utils;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.domain.Apdu;
import com.justinmobile.tsm.cms2ac.domain.ApduCommand;
import com.justinmobile.tsm.cms2ac.domain.ApduResult;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.dto.CardSpaceInfo;
import com.justinmobile.tsm.cms2ac.dto.CardUniqueIdentifier;
import com.justinmobile.tsm.cms2ac.dto.KeyCounterInfo;
import com.justinmobile.tsm.cms2ac.dto.SdInfo;
import com.justinmobile.tsm.cms2ac.dto.SdKeyInfo;
import com.justinmobile.tsm.cms2ac.dto.TlvDto;
import com.justinmobile.tsm.cms2ac.exception.ApduException;
import com.justinmobile.tsm.cms2ac.response.GetDataReadTokenResponse;
import com.justinmobile.tsm.cms2ac.security.scp02.Scp02Param;
import com.justinmobile.tsm.cms2ac.security.scp02.Scp02Service;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Service("apduEngine")
public class ApduEngine {

	private static final Logger log = LoggerFactory.getLogger(ApduEngine.class);

	private static final int LOAD_CMD_MAX_DATA_LENGTH = 0xC3;

	private static final int LOAD_CMD_MAX_DATA_LENGTH_BY_MOBILE_MOCAM = 0xA0;

	/**
	 * 卡发行商编号
	 */
	public static final int GET_DATA_CMD_P1P2_PROVIDER_CODE = 0x0042;

	/**
	 * 卡唯一标识
	 */
	public static final int GET_DATA_CMD_P1P2_CARD_ID = 0x0044;

	/**
	 * 卡数据
	 */
	public static final int GET_DATA_CMD_P1P2_CARD_DATA = 0x0066;

	/**
	 * 辅助安全域数据
	 */
	public static final int GET_DATA_CMD_P1P2_SD_DATA = 0x0072;

	/**
	 * 卡资源数据
	 */
	public static final int GET_DATA_CMD_P1P2_CARD_RESOURCE = 0xFF20;

	/**
	 * 安全域维护的默认密钥版本号对应计数器
	 */
	public static final int GET_DATA_CMD_P1P2_KEY_COUNTER = 0x00C0;

	/**
	 * 卡提供商密钥信息
	 */
	public static final int GET_DATA_CMD_P1P2_PROVIDER_KEY_INFO = 0x00E2;

	/**
	 * 当前安全域密钥信息
	 */
	public static final int GET_DATA_CMD_P1P2_CURRENT_SD_KEY_INFO = 0x00E0;

	/**
	 * TOKEN信息
	 */
	public static final int GET_DATA_CMD_P1P2_TOKEN = 0x1048;

	/**
	 * SE绑定手机号信息
	 */
	public static final int GET_DATA_CMD_P1P2_MSISDN = 0x2F14;

	/**
	 * 安全域剩余空间安全域内已安装的应用
	 */
	public static final int GET_DATA_CMD_P1P2_INSTALLED_APP = 0x2F00;

	public static final int DELETE_CMD_DATA_TYPE_SD_APP = 1;

	public static final int DELETE_CMD_DATA_TYPE_LOAD_FILE = 2;

	public static final int DELETE_CMD_DATA_TYPE_KEY = 3;

	public static final byte SET_STATUS_CMD_LOCKED = (byte) 0x80;

	public static final byte SET_STATUS_CMD_UNLOCKED = 0x00;

	public static final String VENDOR_COSW = "0A";

	public static final String VENDOR_EAST_COM_PEASE = "1F";

	public static final String FUNCTION_REGEX_IN_PESON_CMD_TEMPLATE = "#(\\w+)\\(([^\\(\\)]*?(?=\\)))\\)";

	/**
	 * 个人化方式二/方式三指令分隔符
	 */
	public static final String PESON_CMD_SEPARATOR = "\n";

	/**
	 * 个人化方式二/方式三指令明文tag
	 */
	public static final String PESON_CMD_PLAIN_TAG = "00";

	/**
	 * 个人化方式二/方式三指令密文tag
	 */
	public static final String PESON_CMD_CIPHER_TAG = "01";

	public static final Map<String, String> cardVendorAidMap = new HashMap<String, String>();

	static {
		cardVendorAidMap.put(VENDOR_COSW, "A0000000090001");
		cardVendorAidMap.put(VENDOR_EAST_COM_PEASE, "A0000000871002006811112206080033");
	}

	@Autowired
	private Scp02Service scp02Service;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	@Autowired
	private CardInfoManager cardManager;

	@Autowired
	private CardSecurityDomainManager cardSecurityDomainManager;

	@Autowired
	private GsmApduHelper gsmApduHelper;

	@Autowired
	private TransactionHelper transactionHelper;

	@Autowired
	private KeyProfileHelper keyProfileHelper;

	public ApduCommand buildGetDataCmd(Cms2acParam cms2acParam, int dataType) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xCA);
		cmd.setP1((byte) ((dataType & 0xFF00) >> 8));
		cmd.setP2((byte) (dataType & 0x00FF));
		ApduResult lastApduResult = cms2acParam.getLastApduResult();
		boolean isRepeatedApdu = lastApduResult != null && lastApduResult.getSw1() == (byte) 0x6C;
		if (isRepeatedApdu) {
			cmd.setLe(lastApduResult.getSw2());
		} else {
			cmd.setLe((byte) 0x00);
		}
		if (Constants.CMS2AC_SCP_80.equals(cms2acParam.getScp())) {
			cmd.setType(ApduCommand.CMD_TYPE_TWO);
		} else {
			cmd.setType(ApduCommand.CMD_TYPE_SIX);
		}
		// cmd.setType(ApduCommand.CMD_TYPE_TWO);
		cmd.setRawHex(toHexString(cmd.toByteArray()));

		// if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
		// cmd = mashalSecurePack(cmd, cms2acParam);
		// }

		return cmd;
	}

	public ApduCommand buildGetDataCmdWithSecurity(Cms2acParam cms2acParam, int dataType) {
		ApduCommand cmd = buildGetDataCmd(cms2acParam, dataType);

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			cmd = mashalSecurePack(cmd, cms2acParam);
		}

		return cmd;
	}

	/**
	 * 解析响应：密钥信息(00E0)
	 * 
	 * @param cms2acParam
	 * @return 密钥信息
	 */
	public List<byte[]> parseGetKeyInfoRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			TlvObject responseData = TlvObject.parse(result.getData(), 2, 1);
			MutilTagTlvObject keyInfo = MutilTagTlvObject.parse(responseData.getByTag(ConvertUtils.int2HexString(
					GET_DATA_CMD_P1P2_CURRENT_SD_KEY_INFO, 2 * 2)));
			return keyInfo.getValueByTag("0C");
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}
	}

	/**
	 * 解析响应：卡资源数据(FF20)
	 * 
	 * @param cms2acParam
	 * @return 卡资源数据
	 */
	public CardSpaceInfo parseGetCardSpaceRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] data = TlvDto.findTlvSingleValue(result.getData(), GET_DATA_CMD_P1P2_CARD_RESOURCE);
			CardSpaceInfo cardSpaceInfo = new CardSpaceInfo(data);
			return cardSpaceInfo;
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}
	}

	/**
	 * 解析响应：卡发行商编号(0042)
	 * 
	 * @param cms2acParam
	 * @return 卡商编号
	 */
	public byte[] parseGetProviderCodeRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			return TlvDto.findTlvSingleValue(result.getData(), GET_DATA_CMD_P1P2_PROVIDER_CODE);
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}

	}

	/**
	 * 解析响应：卡唯一标识(0044)
	 * 
	 * @param cms2acParam
	 * @return 卡唯一标识
	 */
	public CardUniqueIdentifier parseGetCardIdRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			return new CardUniqueIdentifier(TlvDto.findTlvSingleValue(result.getData(), GET_DATA_CMD_P1P2_CARD_ID));
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}

	}

	/**
	 * 解析响应：当前安全域密钥信息(00E0)
	 * 
	 * @param cms2acParam
	 * @return 当前安全域密钥信息
	 */
	public List<SdKeyInfo> parseGetCurrentSdKeyInfoRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] data = TlvDto.findTlvSingleValue(result.getData(), GET_DATA_CMD_P1P2_CURRENT_SD_KEY_INFO);
			List<byte[]> keyInfoBytes = TlvDto.findTlvMutilValue(data, "C0");

			List<SdKeyInfo> keyInfoList = new ArrayList<SdKeyInfo>();
			for (byte[] bytes : keyInfoBytes) {
				keyInfoList.add(new SdKeyInfo(bytes));
			}
			return keyInfoList;
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}
	}

	/**
	 * 解析响应：卡提供商密钥信息(00E2)
	 * 
	 * @param cms2acParam
	 * @return 卡提供商密钥信息(密钥版本)
	 */
	public byte[] parseGetCardProviderKeyInfoRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			return TlvDto.findTlvSingleValue(result.getData(), GET_DATA_CMD_P1P2_PROVIDER_KEY_INFO);
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}

	}

	/**
	 * 解析响应：安全域剩余空间和安全域内已安装的应用的信息(2F00)
	 * 
	 * @param cms2acParam
	 * @return 安全域剩余空间
	 */
	public SdInfo parseGetInstalledAppRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] findTlvValue = TlvDto.findTlvValue(result.getData(), GET_DATA_CMD_P1P2_INSTALLED_APP);
			return new SdInfo(findTlvValue);
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}

	}

	/**
	 * 解析响应：安全域维护的默认密钥版本号对应计数器(00C0)
	 * 
	 * @param cms2acParam
	 * @return 安全域维护的默认密钥版本号对应计数器
	 */
	public KeyCounterInfo parseGetKeyCounterInfoRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] findTlvValue = TlvDto.findTlvValue(result.getData(), GET_DATA_CMD_P1P2_KEY_COUNTER);
			return new KeyCounterInfo(findTlvValue);
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}
	}

	// TODO
	// 0066 卡数据
	// 0072 辅助安全域数据
	// 2F10卡的版本信息

	public Space parseGetSdSpaceRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] data = result.getData();
			return buildSpaceInfo(data, cms2acParam.getCurrentSecurityDomain());
		} else {
			throw new ApduException("parseGetSdSpaceRsp", getErrorCode(result));
		}
	}

	private Space buildSpaceInfo(byte[] data, SecurityDomain sd) {
		Space space = new Space();
		if (sd.getSpaceRule() == SecurityDomain.FIXED_SPACE) {
			byte[] findTlvValue = TlvDto.findTlvValue(data, GET_DATA_CMD_P1P2_INSTALLED_APP);
			SdInfo sdInfo = new SdInfo(findTlvValue);
			space.setNvm(sdInfo.getNoneVolatileDataSpace());
			space.setRam(sdInfo.getVolatileDataSpace());
		} else if (sd.getSpaceRule() == SecurityDomain.UNFIXABLE_SPACE) {
			CardSpaceInfo cardSpaceInfo = new CardSpaceInfo(data);
			space.setNvm(cardSpaceInfo.getFreeNoneVolatile());
			space.setRam(cardSpaceInfo.getFreeVolatile());
		}
		return space;
	}

	public ApduCommand buildInstallForLoadCmd(Cms2acParam cms2acParam) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE6);
		cmd.setP1((byte) (0x02));
		cmd.setP2((byte) (0x00));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(getInstallForLoadData(cms2acParam));
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	public ApduCommand buildInstallForLoadCmd(Cms2acParam cms2acParam, List<ApduCommand> cmdBatch) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE6);
		cmd.setP1((byte) (0x02));
		cmd.setP2((byte) (0x00));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(getInstallForLoadData(cms2acParam));
		setCmdLc(cmd);
		cmd.setLe((byte) 0x00);
		cmd.setRawHex(toHexString(cmd.toByteArray()));

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			cmd = mashalSecurePack(cmd, cms2acParam, cmdBatch);
		}
		return cmd;
	}

	public ApduCommand buildInstallForInstallCmd(Cms2acParam cms2acParam, boolean isSecurityDomain, boolean loadFileSd) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE6);
		cmd.setP1((byte) (0x0C));
		cmd.setP2((byte) (0x00));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(getInstallForInstallData(cms2acParam, isSecurityDomain, loadFileSd));
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	public ApduCommand buildInstallForExtraditeCmd(Cms2acParam cms2acParam) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE6);
		cmd.setP1((byte) (0x10));
		cmd.setP2((byte) (0x00));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(getInstallForExtraditeData(cms2acParam));
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	public void parseInstallRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] installRsp = result.getData();

			checkInsallResult(installRsp);
		} else {
			throw new ApduException("parseInstallRsp", getErrorCode(result));
		}
	}

	public void parseRspWithSecurity(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] installRsp = result.getData();

			checkInsallResult(installRsp);
		} else {
			throw new ApduException("parseRsp", getErrorCode(result));
		}
	}

	public List<ApduCommand> buildLoadCmdBatch(Cms2acParam cms2acParam) {
		LocalTransaction trans = cms2acParam.getLocalTransaction();
		LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDownload(trans);
		String cms2acCapFileHex = loadFileVersion.getCapFileHex();
		return extractLoadCmdBatch(cms2acParam, cms2acCapFileHex);
	}

	/**
	 * 组建Load指令批，在组建时会用到loadCmdBatch的最后一条ICV，并将组建的Load指令放在cmdBatch中
	 * 
	 * @param cms2acParam
	 * @param cmdBatch
	 *            ApduCommand的List，用于在组建本批第一条Load指令是会用到loadCmdBatch中上批指令最后一条的ICV
	 */
	public void buildLoadCmdBatch(Cms2acParam cms2acParam, List<ApduCommand> cmdBatch) {
		LocalTransaction trans = cms2acParam.getLocalTransaction();
		LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDownload(trans);
		String cms2acCapFileHex = loadFileVersion.getCapFileHex();
		extractLoadCmdBatch(cms2acParam, cms2acCapFileHex, cmdBatch);
	}

	private List<ApduCommand> extractLoadCmdBatch(Cms2acParam cms2acParam, String cms2acCapFileHex) {
		byte[] cms2acCapFileBytes = generateCapFileBytes(cms2acCapFileHex);
		List<byte[]> exeCodeByteList = null;
		exeCodeByteList = segmentalize(cms2acCapFileBytes, LOAD_CMD_MAX_DATA_LENGTH_BY_MOBILE_MOCAM);
		List<ApduCommand> apduCmdBatch = new ArrayList<ApduCommand>();
		ApduCommand apduCommand = null;
		int cursor = 0;
		for (byte[] cmdData : exeCodeByteList) {
			if (cursor == exeCodeByteList.size() - 1) {
				break;
			}
			apduCommand = getLoadCmd(cms2acParam, cmdData, cursor, false, apduCmdBatch);
			apduCommand.setCms2acParam(cms2acParam);
			apduCmdBatch.add(apduCommand);
			cursor++;
		}
		byte[] lastCmdData = exeCodeByteList.get(exeCodeByteList.size() - 1);
		apduCommand = getLoadCmd(cms2acParam, lastCmdData, cursor, true, apduCmdBatch);
		apduCmdBatch.add(apduCommand);

		return apduCmdBatch;
	}

	/**
	 * 将Cap文件组建为Load指令批，在组建时会用到loadCmdBatch的最后一条ICV，并将组建的Load指令放在apduCmdBatch中
	 * 
	 * @param cms2acParam
	 * @param cms2acCapFileHex
	 * @param apduCmdBatch
	 *            ApduCommand的List，用于在组建本批第一条Load指令是会用到loadCmdBatch中上批指令最后一条的ICV
	 */
	public void extractLoadCmdBatch(Cms2acParam cms2acParam, String cms2acCapFileHex, List<ApduCommand> apduCmdBatch) {
		byte[] cms2acCapFileBytes = generateCapFileBytes(cms2acCapFileHex);
		LocalTransaction localTransaction = cms2acParam.getLocalTransaction();
		List<byte[]> exeCodeByteList = null;
		if (localTransaction.getCommType() == Constants.COMM_TYPE_MOCAM) {
			exeCodeByteList = segmentalize(cms2acCapFileBytes, LOAD_CMD_MAX_DATA_LENGTH_BY_MOBILE_MOCAM);
		} else {
			exeCodeByteList = segmentalize(cms2acCapFileBytes, LOAD_CMD_MAX_DATA_LENGTH);
		}
		ApduCommand apduCommand = null;
		int cursor = 0;
		for (byte[] cmdData : exeCodeByteList) {
			if (cursor == exeCodeByteList.size() - 1) {
				break;
			}
			apduCommand = getLoadCmd(cms2acParam, cmdData, cursor, false, apduCmdBatch);
			apduCommand.setCms2acParam(cms2acParam);
			apduCmdBatch.add(apduCommand);
			cursor++;
		}
		byte[] lastCmdData = exeCodeByteList.get(exeCodeByteList.size() - 1);
		apduCommand = getLoadCmd(cms2acParam, lastCmdData, cursor, true, apduCmdBatch);
		apduCmdBatch.add(apduCommand);

	}

	private byte[] generateCapFileBytes(String capFileHex) {
		byte[] capFileDataBytes = ConvertUtils.hexString2ByteArray(capFileHex);

		int capFileDataLength = capFileDataBytes.length;
		byte[] capFileDataLengthBytes = computeBerLength(capFileDataLength);

		byte[] capFileDataTag = new byte[] { (byte) 0xC4 };

		byte[] capFileBytes = contactArray(capFileDataTag, capFileDataLengthBytes);
		capFileBytes = contactArray(capFileBytes, capFileDataBytes);
		return capFileBytes;
	}

	private byte[] computeBerLength(int capFileLength) {
		byte[] lenBytes = null;
		byte[] tagLenBytes = null;
		byte[] dataLenBytes = null;
		if (capFileLength > 0 && capFileLength <= 0x7F) {
			lenBytes = intToHexBytes(capFileLength, 1);
		} else if (capFileLength >= 0x80 && capFileLength <= 0xFF) {
			tagLenBytes = new byte[] { (byte) 0x81 };
			dataLenBytes = intToHexBytes(capFileLength, 1);
			lenBytes = contactArray(tagLenBytes, dataLenBytes);
		} else if (capFileLength >= 0x0100 && capFileLength <= 0xFFFF) {
			tagLenBytes = new byte[] { (byte) 0x82 };
			dataLenBytes = intToHexBytes(capFileLength, 2);
			lenBytes = contactArray(tagLenBytes, dataLenBytes);
		} else if (capFileLength >= 0x010000 && capFileLength <= 0xFFFFFF) {
			tagLenBytes = new byte[] { (byte) 0x83 };
			dataLenBytes = intToHexBytes(capFileLength, 3);
			lenBytes = contactArray(tagLenBytes, dataLenBytes);
		} else {
			throw new IllegalArgumentException("cap should not be so large : " + capFileLength);
		}
		return lenBytes;
	}

	public void parseLoadRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] loadRsp = result.getData();

			checkLoadResult(loadRsp);
		} else {
			throw new ApduException("parseLoadRsp", getErrorCode(result));
		}
	}

	public ApduCommand buildDeleteCmd(Cms2acParam cms2acParam, int dataType, boolean isSecurityDomain) {
		ApduCommand cmd = buildDeleteCmdHead(dataType);
		cmd.setData(getDeleteData(cms2acParam, dataType, isSecurityDomain));
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	public List<ApduCommand> buildDeleteCmd(Cms2acParam cms2acParam, int dataType, Collection<String> aids) {
		List<ApduCommand> cmds = Lists.newArrayList();

		if (CollectionUtils.isNotEmpty(aids)) {
			for (String aid : aids) {
				ApduCommand cmd = buildDeleteCmdHead(dataType);
				cmd.setData(getTlvBytes((byte) 0x4F, aid));
				setCmdLc(cmd);
				cmd.setLe((byte) 0x00);
				cmd.setRawHex(toHexString(cmd.toByteArray()));

				if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
					cmd = mashalSecurePack(cmd, cms2acParam, cmds);
				}

				cmds.add(cmd);
			}
		}

		return cmds;
	}

	private ApduCommand buildDeleteCmdHead(int dataType) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE4);
		cmd.setP1((byte) (0x00));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);
		if (dataType == DELETE_CMD_DATA_TYPE_LOAD_FILE) {
			cmd.setP2((byte) 0x80);
		} else {
			cmd.setP2((byte) 0x00);
		}
		return cmd;
	}

	private ApduCommand buildDeleteCmdFoot(Cms2acParam cms2acParam, ApduCommand cmd) {
		setCmdLc(cmd);
		cmd.setLe((byte) 0x00);
		cmd.setRawHex(toHexString(cmd.toByteArray()));

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			cmd = mashalSecurePack(cmd, cms2acParam);
		}
		return cmd;
	}

	public boolean parseDeleteRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			// byte[] deleteRsp = result.getData();
			return true;
			// checkDeleteResult(deleteRsp);
		} else {
			throw new ApduException("parseDeleteRsp", getErrorCode(result));
		}
	}

	private ApduCommand mashalSecurePack(ApduCommand cmd, Cms2acParam cms2acParam) {
		return mashalSecurePack(cmd, cms2acParam, null);
	}

	private ApduCommand mashalSecurePack(ApduCommand cmd, Cms2acParam cms2acParam, List<ApduCommand> apduCmdBatch) {
		Scp02Param scp02Param = new Scp02Param(cms2acParam.getScp02i(), cms2acParam.getScp02SecurityLevel());
		if (scp02Param.isNoSecurity() && cmd.getIns() != (byte) 0x82) {
			return cmd;
		}

		cmd.setCla((byte) (cmd.getCla() | 0x04));

		if (isCMac(cmd, scp02Param)) {
			cmd.setLc((byte) (cmd.getLc() + 8));
			if (hasNoData(cmd)) {
				cmd.setType(ApduCommand.CMD_TYPE_THREE);
			}
			byte[] cMac = generateCMac(cmd, cms2acParam, scp02Param, apduCmdBatch);
			byte[] dataPlusMac = contactArray(cmd.getData(), cMac);
			cmd.setCmac(toHexString(cMac));
			cmd.setData(dataPlusMac);
		}

		if (isCDecryption(cmd, scp02Param)) {
			byte[] encryptedData = encryptData(cmd.getRawData(), cms2acParam, scp02Param);
			cmd.setLc((byte) (encryptedData.length + 8));// 要加密必然有mac，所以密文长度+mac长度
			byte[] encDataPlusMac = contactArray(encryptedData, hexString2ByteArray(cmd.getCmac()));
			cmd.setData(encDataPlusMac);
		}

		cmd.setSecurityHex(toHexString(cmd.toByteArray()));

		return cmd;
	}

	private boolean hasNoData(ApduCommand cmd) {
		return cmd.getType() == ApduCommand.CMD_TYPE_ONE || cmd.getType() == ApduCommand.CMD_TYPE_TWO
				|| cmd.getType() == ApduCommand.CMD_TYPE_SIX;
	}

	private boolean isCMac(ApduCommand cmd, Scp02Param scp02Param) {
		return scp02Param.isCMac() || cmd.getIns() == (byte) 0x82;
	}

	private boolean isCDecryption(ApduCommand cmd, Scp02Param scp02Param) {
		return scp02Param.isCDecryption() && cmd.getIns() != (byte) 0x82 && cmd.getType() != ApduCommand.CMD_TYPE_ONE
				&& cmd.getType() != ApduCommand.CMD_TYPE_TWO && cmd.getType() != ApduCommand.CMD_TYPE_SIX;
	}

	private byte[] encryptData(byte[] encSource, Cms2acParam cms2acParam, Scp02Param scp02Param) {
		if (scp02Param.getIndication() != Scp02Param.SCP02_I_15) {
			throw new PlatformException(PlatformErrorCode.INVALID_SCP02_I);
		}
		// byte[] paddedEncSource = SecureUtil.scp02Padding(encSource);
		return scp02Service.encryptData(encSource, cms2acParam);
	}

	private byte[] generateCMac(ApduCommand cmd, Cms2acParam cms2acParam, Scp02Param scp02Param, List<ApduCommand> apduCmdBatch) {
		if (scp02Param.getIndication() != Scp02Param.SCP02_I_15) {
			throw new PlatformException(PlatformErrorCode.INVALID_SCP02_I);
		}
		byte[] initVector = null;
		if (cmd.getFirstPair()) {
			initVector = hexString2ByteArray(Apdu.DEFAULT_ICV);
		} else {
			initVector = generateMacIcv(cms2acParam, apduCmdBatch);
		}

		log.debug(toHexString(initVector));

		byte[] macSource = cmd.noLeByteArray();
		byte[] macNextIcv = scp02Service.computeMacNextIcv(macSource, initVector, cms2acParam, true);

		byte[] mac = subArray(macNextIcv, 0, 8);
		byte[] nextIcv = subArray(macNextIcv, 8, 16);
		cmd.setIcv(toHexString(nextIcv));
		return mac;
	}

	/**
	 * 生成计算mac所需要的icv，如果apduCmdBatch不会为空，将使用apduCmdBatch中最后一条指令的icv，否则，
	 * 使用cms2acParam中最后一条指令的icv
	 * 
	 * @param cms2acParam
	 * @param apduCmdBatch
	 * @return 计算mac所需要的icv，byte[]的形式
	 */
	private byte[] generateMacIcv(Cms2acParam cms2acParam, List<ApduCommand> apduCmdBatch) {
		ApduCommand lastApduCommand = null;
		if (apduCmdBatch == null || apduCmdBatch.size() == 0) {
			lastApduCommand = cms2acParam.getLastApduCommand();
		} else {
			lastApduCommand = apduCmdBatch.get(apduCmdBatch.size() - 1);
		}
		byte[] icv = hexString2ByteArray(lastApduCommand.getIcv());
		return icv;
	}

	private String getErrorCode(ApduResult apduResult) {
		String sw1 = toHexString(new byte[] { apduResult.getSw1() });
		String sw2 = toHexString(new byte[] { apduResult.getSw2() });
		return sw1 + sw2;
	}

	private ApduResult unmashalSecurePack(ApduResult apduResult, Cms2acParam cms2acParam) {
		Scp02Param scp02Param = new Scp02Param(cms2acParam.getScp02i(), cms2acParam.getScp02SecurityLevel());

		if (scp02Param.isRMac()) {
			return extractRawResult();
		} else {
			return apduResult;
		}
	}

	private ApduResult extractRawResult() {
		// TODO Auto-generated method stub
		return null;
	}

	private byte[] getInstallForInstallData(Cms2acParam cms2acParam, boolean isSecurityDomain, boolean loadFileSd) {
		LocalTransaction trans = cms2acParam.getLocalTransaction();
		String installAid = null;
		LoadFile loadFile = null;
		LoadModule loadModule = null;

		String privilege = null;
		String installParams = null;
		SecurityDomain securityDomain = null;

		if (Operation.CREATE_SD.name().equals(trans.getProcedureName())) {
			installAid = trans.getAid();
			securityDomain = securityDomainManager.getByAid(installAid);
			if (SystemConfigUtils.isCms2acRuntimeEnvironment()) {
				loadModule = securityDomain.getLoadModule();
				loadFile = loadModule.getLoadFileVersion().getLoadFile();
			} else {
				loadModule = new LoadModule();
				loadModule.setAid(SystemConfigUtils.getMockIsdModuleAid());
				loadFile = new LoadFile();
				loadFile.setAid(SystemConfigUtils.getMockIsdFileAid());
			}
			privilege = toHexString(intToHexBytes(securityDomain.getPrivilege(), 1));
			installParams = getInstallParams(securityDomain);
		} else {
			if (isSecurityDomain) {
				String appAid = trans.getAid();
				securityDomain = null;
				if (loadFileSd) {
					securityDomain = transactionHelper.getLoadFileRelatedSd(appAid, trans);
				} else {
					securityDomain = transactionHelper.getAppRelatedSd(appAid);
				}
				installAid = securityDomain.getAid();
				loadModule = securityDomain.getLoadModule();
				loadFile = loadModule.getLoadFileVersion().getLoadFile();
				privilege = toHexString(intToHexBytes(securityDomain.getPrivilege(), 1));
				installParams = getInstallParams(securityDomain);
			} else {
				Applet applet = transactionHelper.getCurrentApplet(trans);
				installAid = applet.getAid();
				loadModule = applet.getLoadModule();
				loadFile = loadModule.getLoadFileVersion().getLoadFile();
				privilege = toHexString(intToHexBytes(applet.getPrivilege(), 1));
				installParams = applet.getInstallParams();
			}
		}

		String loadFileAid = loadFile.getAid();
		String loadModuleAid = loadModule.getAid();

		byte[] loadFileAidBytes = getLvBytes(loadFileAid);
		byte[] loadModuleAidBytes = getLvBytes(loadModuleAid);
		byte[] appAidBytes = getLvBytes(installAid);
		byte[] privilegeBytes = getLvBytes(privilege);
		byte[] installParamsBytes = alterString2LvBytes(installParams);

		byte[] installData = new byte[0];
		installData = contactArray(installData, loadFileAidBytes);
		installData = contactArray(installData, loadModuleAidBytes);
		installData = contactArray(installData, appAidBytes);
		installData = contactArray(installData, privilegeBytes);
		installData = contactArray(installData, installParamsBytes);

		byte[] tokenBytes = new byte[] { 0x00 };
		installData = contactArray(installData, tokenBytes);

		return installData;
	}

	public String getInstallParams(SecurityDomain securityDomain) {
		Long noneVolatileSpace = securityDomain.getNoneVolatileSpace();
		Integer volatileSpace = securityDomain.getVolatileSpace();

		// C7和C8是安全域的可变和不可变空间，C9中的参数49是设置安全域的管理空间
		byte[] sdNonVolatileSpace = longToHexBytes(noneVolatileSpace, 2);
		byte[] sdVolatileSpace = intToHexBytes(volatileSpace, 2);
		TlvDto c8Tlv = new TlvDto((byte) 0xC8, sdNonVolatileSpace);
		TlvDto c7Tlv = new TlvDto((byte) 0xC7, sdVolatileSpace);
		TlvDto efTlv = new TlvDto((byte) 0xEF, c7Tlv.toString() + c8Tlv.toString());
		String c9Tlv = securityDomain.getInstallParams();
		return c9Tlv.toString() + efTlv.toString();
	}

	private byte[] getLvBytes(String loadFileAid) {
		byte[] valueBytes = hexString2ByteArray(loadFileAid);
		byte[] lengthBytes = intToHexBytes(valueBytes.length, 1);
		return contactArray(lengthBytes, valueBytes);
	}

	private void checkInsallResult(byte[] installRsp) {
		// TODO Auto-generated method stub

	}

	private byte[] getInstallForLoadData(Cms2acParam cms2acParam) {
		byte[] installData = new byte[0];
		LocalTransaction trans = cms2acParam.getLocalTransaction();

		LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDownload(trans);
		LoadFile loadFile = loadFileVersion.getLoadFile();

		String loadFileAid = loadFile.getAid();
		String sdAid;
		if (!SystemConfigUtils.isCms2acRuntimeEnvironment() && loadFile.getSd().isIsd()) {// 如果当前运行环境不是CMS2AC并行安全域是主安全域，使用配置文件
			sdAid = SystemConfigUtils.getMockIsdAppAid();
		} else {// 否则，使用数据库配置
			sdAid = loadFile.getSd().getAid();
		}
		String loadFileHash = loadFileVersion.getHash();
		String loadParams = loadFileVersion.getLoadParams();

		byte[] loadFileAidBytes = getLvBytes(loadFileAid);
		byte[] sdAidBytes = getLvBytes(sdAid);
		byte[] loadFileHashBytes = alterString2LvBytes(loadFileHash);
		byte[] loadParamsBytes = alterString2LvBytes(loadParams);

		installData = contactArray(installData, loadFileAidBytes);
		installData = contactArray(installData, sdAidBytes);
		installData = contactArray(installData, loadFileHashBytes);
		installData = contactArray(installData, loadParamsBytes);

		byte[] tokenBytes = new byte[] { 0x00 };
		installData = contactArray(installData, tokenBytes);

		return installData;
	}

	private byte[] getInstallForExtraditeData(Cms2acParam cms2acParam) {
		byte[] installData = new byte[0];
		LocalTransaction localTransaction = cms2acParam.getLocalTransaction();
		String appAid = localTransaction.getAid();

		Application app = applicationManager.getByAid(appAid);
		String sdAid = app.getSd().getAid();

		byte[] appAidBytes = getLvBytes(appAid);
		byte[] sdAidBytes = getLvBytes(sdAid);

		installData = contactArray(installData, sdAidBytes);
		installData = contactArray(installData, hexString2ByteArray("00"));
		installData = contactArray(installData, appAidBytes);
		installData = contactArray(installData, hexString2ByteArray("0000"));

		byte[] tokenBytes = new byte[] { 0x00 };
		installData = contactArray(installData, tokenBytes);

		return installData;
	}

	private byte[] alterString2LvBytes(String hexString) {
		if (hexString == null) {
			hexString = "";
		}
		return getLvBytes(hexString);
	}

	private ApduCommand getLoadCmd(Cms2acParam cms2acParam, byte[] cmdData, int seqNo, boolean isLast, List<ApduCommand> apduCmdBatch) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE8);
		if (isLast) {
			cmd.setP1((byte) (0x80));
		} else {
			cmd.setP1((byte) (0x00));
		}
		cmd.setP2((byte) (seqNo));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(cmdData);
		setCmdLc(cmd);
		cmd.setLe((byte) 0x00);
		cmd.setRawHex(toHexString(cmd.toByteArray()));

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			cmd = mashalSecurePack(cmd, cms2acParam, apduCmdBatch);
		}
		return cmd;
	}

	private List<byte[]> segmentalize(byte[] capFile, int maxDataLength) {
		int mod = capFile.length % maxDataLength;
		int counter = (capFile.length - mod) / maxDataLength;
		List<byte[]> exeCodeByteList = new ArrayList<byte[]>();

		for (int i = 0; i < counter; i++) {// 将cap文件按照maxDataLength进行切分
			byte[] buf = subArray(capFile, i * maxDataLength, (i + 1) * maxDataLength);
			exeCodeByteList.add(buf);
		}
		if (mod != 0) {// 将cap文件按照maxDataLength进行切分后的剩余部分
			byte[] buf = subArray(capFile, counter * maxDataLength, capFile.length);
			exeCodeByteList.add(buf);
		}
		return exeCodeByteList;
	}

	private void checkLoadResult(byte[] loadRsp) {
		// TODO Auto-generated method stub

	}

	private byte[] getDeleteData(Cms2acParam cms2acParam, int dataType, boolean isSecurityDomain) {
		byte[] deleteData = new byte[0];

		LocalTransaction localTransaction = cms2acParam.getLocalTransaction();
		String procedureName = localTransaction.getProcedureName();
		if (dataType == DELETE_CMD_DATA_TYPE_SD_APP) {
			String deleteAid = null;
			if (Operation.DELETE_APP.name().equals(procedureName)) {
				if (isSecurityDomain) {
					deleteAid = localTransaction.getAid();
				} else {
					Applet applet = transactionHelper.getCurrentApplet(localTransaction);
					deleteAid = applet.getAid();
				}
			} else {
				deleteAid = localTransaction.getAid();
			}
			deleteData = getTlvBytes((byte) 0x4F, deleteAid);
		} else if (dataType == DELETE_CMD_DATA_TYPE_LOAD_FILE) {
			LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDelete(cms2acParam.getLocalTransaction());
			String loadFileAid = loadFileVersion.getLoadFile().getAid();
			deleteData = getTlvBytes((byte) 0x4F, loadFileAid);
		} else if (dataType == DELETE_CMD_DATA_TYPE_KEY) {
			throw new PlatformException(PlatformErrorCode.INVALID_APDU_PARAM);
		} else {
			throw new PlatformException(PlatformErrorCode.INVALID_APDU_PARAM);
		}
		// byte[] tokenBytes = new byte[] { 0x00 };
		// deleteData = contactArray(deleteData, tokenBytes);

		return deleteData;
	}

	private byte[] getTlvBytes(byte tag, String value) {
		byte[] buf = new byte[0];
		byte[] tagBytes = new byte[] { tag };
		buf = contactArray(buf, tagBytes);

		byte[] valueBytes = hexString2ByteArray(value);
		byte[] lengthBytes = intToHexBytes(valueBytes.length, 1);
		buf = contactArray(buf, lengthBytes);
		buf = contactArray(buf, valueBytes);

		return buf;
	}

	@SuppressWarnings("unused")
	private void checkDeleteResult(byte[] deleteRsp) {
		// TODO Auto-generated method stub

	}

	public ApduCommand buildSetStatusCmd(Cms2acParam cms2acParam, byte status, boolean isLoadFileSd) {
		LocalTransaction localTransaction = cms2acParam.getLocalTransaction();
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xF0);
		boolean isLockCard = (LocalTransaction.Operation.LOCK_CARD == Operation.valueOf(localTransaction.getProcedureName()))
				|| (LocalTransaction.Operation.UNLOCK_CARD == Operation.valueOf(localTransaction.getProcedureName()));
		boolean isLockIsd = (((LocalTransaction.Operation.LOCK_SD == Operation.valueOf(localTransaction.getProcedureName()))) && (securityDomainManager
				.getByAid(localTransaction.getAid()).isIsd()))
				|| (((LocalTransaction.Operation.UNLOCK_SD == Operation.valueOf(localTransaction.getProcedureName()))) && (securityDomainManager
						.getByAid(localTransaction.getAid()).isIsd()));
		if (isLockCard || isLockIsd) {// 如果是对主安全域进行操作，p1应该是0x80
			cmd.setP1((byte) (0x80));
		} else {// 如果不是对主安全域进行操作，p1应该是0x40
			cmd.setP1((byte) (0x40));
		}
		cmd.setP2(status);
		cmd.setType(ApduCommand.CMD_TYPE_THREE);

		byte[] cmdData = getSetStatusData(cms2acParam, isLoadFileSd);
		cmd.setData(cmdData);
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	private byte[] getSetStatusData(Cms2acParam cms2acParam, boolean isLoadFileSd) {
		LocalTransaction trans = cms2acParam.getLocalTransaction();
		String procedureName = trans.getProcedureName();
		String setStatusAid = null;
		Operation operation = Operation.valueOf(procedureName);
		switch (operation) {
		case LOCK_APP:
		case UNLOCK_APP:
		case UPDATE_KEY:
		case LOCK_SD:
		case UNLOCK_SD:
		case LOCK_CARD:
		case UNLOCK_CARD:
			setStatusAid = cms2acParam.getLocalTransaction().getAid();
			break;
		case DOWNLOAD_APP:
			ApplicationVersion appVersion = applicationVersionManager.getAidAndVersionNo(trans.getAid(), trans.getAppVersion());
			LoadFileVersion loadFileVersion = transactionHelper.getCurrentLoadFileVersionToDownload(trans);
			LoadFile loadFile = loadFileVersion.getLoadFile();
			if (isLoadFileSd) {
				setStatusAid = loadFile.getSd().getAid();
			} else {
				setStatusAid = appVersion.getApplication().getSd().getAid();
			}
			break;
		default:
			throw new PlatformException(PlatformErrorCode.INVALID_TRANS_TYPE);
		}
		return hexString2ByteArray(setStatusAid);
	}

	public void parseSetStatusRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			return;
		} else {
			throw new ApduException("parseSetStatusRsp", getErrorCode(result));
		}
	}

	public ApduCommand buildPutKeyCmd(Cms2acParam cms2acParam, boolean newVersion) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xD8);
		cmd.setP1(getPutKeyP1(cms2acParam, newVersion));
		cmd.setP2(getPutKeyP2(cms2acParam));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		byte[] cmdData = getPutKeyData(cms2acParam);
		cmd.setData(cmdData);
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	private byte getPutKeyP1(Cms2acParam cms2acParam, boolean newVersion) {
		if (newVersion) {
			return 0x00;
		} else {
			int keyVersion = cms2acParam.getCurrentSecurityDomain().getCurrentKeyVersion();
			return (byte) (0x00 | keyVersion);
		}
	}

	private byte getPutKeyP2(Cms2acParam cms2acParam) {
		SecurityDomain sd = cms2acParam.getCurrentSecurityDomain();
		KeyProfile scp02EncKey = sd.getEncKey();
		return (byte) (0x80 | scp02EncKey.getIndex());
	}

	private byte[] getPutKeyData(Cms2acParam cms2acParam) {
		SecurityDomain updateSd = securityDomainManager.getByAid(cms2acParam.getLocalTransaction().getAid());
		return keyProfileHelper.getPutKeyData(cms2acParam, updateSd);
	}

	public void parsePutKeyRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			// keyProfileHelper.updateCardSdKey(cms2acParam);
			return;
		} else {
			throw new ApduException("parsePutKeyRsp", getErrorCode(result));
		}
	}

	private void setCmdLc(ApduCommand cmd) {
		cmd.setLc((byte) cmd.getData().length);
	}

	public List<ApduCommand> buildPersonalizeCmdBatch(Cms2acParam cms2acParam, byte[] fileContent) {
		List<ApduCommand> cmdBatch = new ArrayList<ApduCommand>();
		int cursor = 0;
		while (cursor < fileContent.length) {
			int apduLength = binaryToInt(subArray(fileContent, cursor, cursor + 2));
			byte[] apduBytes = subArray(fileContent, cursor + 2, cursor + 2 + apduLength);

			ApduCommand apduCommand = new ApduCommand();
			apduCommand.setRawHex(toHexString(apduBytes));
			cmdBatch.add(apduCommand);
			cursor += 2 + apduLength;
		}
		return cmdBatch;
	}

	public ApduCommand buildPPDownloadCmd(String ota3Comand) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0xA0);
		cmd.setIns((byte) 0xC2);
		cmd.setP1((byte) (0x00));
		cmd.setP2((byte) 0x00);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(gsmApduHelper.getPPDownloadData(ota3Comand));
		setCmdLc(cmd);
		return cmd;
	}

	public byte[] parsePPDownloadRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (!cms2acParam.hasNoSecurity()) {
			throw new PlatformException(PlatformErrorCode.APDU_PP_DOWNLOAD_ERROR);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] ppDownloadRsp = result.getData();
			if (ArrayUtils.isEmpty(ppDownloadRsp)) {
				return ppDownloadRsp;
			} else {
				return gsmApduHelper.parsePPDownloadRsp(ppDownloadRsp);
			}
		} else {
			throw new ApduException("parsePPDownloadRsp", getErrorCode(result));
		}
	}

	public ApduCommand buildTerminalResponseCmd(String proactiveCmdType) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0xA0);
		cmd.setIns((byte) 0x14);
		cmd.setP1((byte) (0x00));
		cmd.setP2((byte) 0x00);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(gsmApduHelper.getTerminalRespnseData(proactiveCmdType));
		setCmdLc(cmd);
		return cmd;
	}

	public void parseTerminalResponseRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
		} else {
			throw new ApduException("parseTerminalResponseRsp", getErrorCode(result));
		}
	}

	public ApduCommand buildInitUpdateCmd(Cms2acParam cms2acParam) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0x50);
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cms2acParam.getLocalTransaction().getCardNo(),
				cms2acParam.getCurrentSecurityDomain().getAid());
		byte keyVersion = cardSecurityDomain.getCurrentKeyVersion().byteValue();
		cmd.setP1(keyVersion);
		cmd.setP2((byte) 0x00);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		String hostRandom = "0000000000000000";
		// toHexString(scp02Service.generateRandom(8));
		cms2acParam.setHostRandom(hostRandom);
		byte[] cmdData = hexString2ByteArray(hostRandom);
		cmd.setData(cmdData);
		setCmdLc(cmd);
		cmd.setLe((byte) 0x00);
		cmd.setRawHex(toHexString(cmd.toByteArray()));

		if (!Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			throw new PlatformException(PlatformErrorCode.INVALID_SCP);
		}
		return cmd;
	}

	public void parseInitUpdateRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (!Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			throw new PlatformException(PlatformErrorCode.INVALID_SCP);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			byte[] initUpdateRsp = result.getData();
			analyzeInitUpdateRsp(cms2acParam, initUpdateRsp);
		} else {
			throw new ApduException("parseInitUpdateRsp", getErrorCode(result));
		}
	}

	private void analyzeInitUpdateRsp(Cms2acParam cms2acParam, byte[] initUpdateRsp) {
		int cardKeyVersion = binaryToInt(subArray(initUpdateRsp, 10, 11));
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cms2acParam.getLocalTransaction().getCardNo(),
				cms2acParam.getCurrentSecurityDomain().getAid());

		int scp02KeyVersion = cardSecurityDomain.getCurrentKeyVersion();
		if (cardKeyVersion != scp02KeyVersion) {
			throw new PlatformException(PlatformErrorCode.APDU_INIT_UPDATE_ERROR);
		}

		String cardScp = toHexString(subArray(initUpdateRsp, 11, 12));
		if (!Constants.CMS2AC_SCP_02.equals(cardScp)) {
			throw new PlatformException(PlatformErrorCode.APDU_INIT_UPDATE_ERROR);
		}

		int scp02Counter = binaryToInt(subArray(initUpdateRsp, 12, 14));
		cms2acParam.setScp02Counter(scp02Counter);

		String cardRandom = toHexString(subArray(initUpdateRsp, 14, 20));
		cms2acParam.setCardRandom(cardRandom);

		byte[] realCardEncBytes = subArray(initUpdateRsp, 20, 28);
		verifyCardEncBytes(cms2acParam, realCardEncBytes);
	}

	private void verifyCardEncBytes(Cms2acParam cms2acParam, byte[] realCardEncBytes) {
		byte[] cardEncSource = getCardEncSource(cms2acParam);
		// byte[] paddedCardEncSource = SecureUtil.scp02Padding(cardEncSource);
		byte[] encryptedBytes = scp02Service.encryptData(cardEncSource, cms2acParam);
		byte[] expectedCardEncBytes = extractAuthSignature(encryptedBytes);

		if (!ArrayUtils.isEquals(expectedCardEncBytes, realCardEncBytes)) {
			throw new PlatformException(PlatformErrorCode.APDU_INIT_UPDATE_ERROR);
		}
	}

	private byte[] extractAuthSignature(byte[] encryptedBytes) {
		int end = encryptedBytes.length;
		int start = end - 8;
		return subArray(encryptedBytes, start, end);
	}

	private byte[] getCardEncSource(Cms2acParam cms2acParam) {
		byte[] cardEncSource = new byte[0];

		byte[] hostRandom = hexString2ByteArray(cms2acParam.getHostRandom());
		cardEncSource = contactArray(cardEncSource, hostRandom);

		byte[] scp02Counter = intToHexBytes(cms2acParam.getScp02Counter(), 2);
		cardEncSource = contactArray(cardEncSource, scp02Counter);

		byte[] cardRandom = hexString2ByteArray(cms2acParam.getCardRandom());
		cardEncSource = contactArray(cardEncSource, cardRandom);

		return cardEncSource;
	}

	public ApduCommand buildExtAuthCmd(Cms2acParam cms2acParam) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x84);
		cmd.setIns((byte) 0x82);
		cmd.setP1((byte) (cms2acParam.getScp02SecurityLevel().intValue()));
		cmd.setP2((byte) 0x00);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		byte[] cmdData = getExtAuthCmdData(cms2acParam);
		cmd.setData(cmdData);
		setCmdLc(cmd);
		cmd.setLe((byte) 0x00);
		cmd.setRawHex(toHexString(cmd.toByteArray()));

		if (!Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			throw new PlatformException(PlatformErrorCode.INVALID_SCP);
		}
		cmd.setFirstPair(true);
		cmd = mashalSecurePack(cmd, cms2acParam);

		return cmd;
	}

	private byte[] getExtAuthCmdData(Cms2acParam cms2acParam) {
		byte[] hostEncSource = getHostEncSource(cms2acParam);
		// byte[] paddedHostEncSource = SecureUtil.scp02Padding(hostEncSource);
		byte[] encryptedBytes = scp02Service.encryptData(hostEncSource, cms2acParam);
		byte[] hostEncBytes = extractAuthSignature(encryptedBytes);
		return hostEncBytes;
	}

	private byte[] getHostEncSource(Cms2acParam cms2acParam) {
		byte[] hostEncSource = new byte[0];

		byte[] scp02Counter = intToHexBytes(cms2acParam.getScp02Counter(), 2);
		hostEncSource = contactArray(hostEncSource, scp02Counter);

		byte[] cardRandom = hexString2ByteArray(cms2acParam.getCardRandom());
		hostEncSource = contactArray(hostEncSource, cardRandom);

		byte[] hostRandom = hexString2ByteArray(cms2acParam.getHostRandom());
		hostEncSource = contactArray(hostEncSource, hostRandom);

		return hostEncSource;
	}

	public void parseExtAuthRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {

		} else {
			throw new ApduException("parseExtAuthRsp", getErrorCode(result));
		}
	}

	public ApduCommand buildSelectCmd(Cms2acParam cms2acParam) {
		LocalTransaction localTransaction = cms2acParam.getLocalTransaction();
		boolean hasNoSecurity = cms2acParam.hasNoSecurity();
		boolean persoPrepared = transactionHelper.isPersoPrepared(localTransaction);
		String selectAid = null;
		if (hasNoSecurity) {// 理论上不应该到这里了
			throw new PlatformException(PlatformErrorCode.INVALID_SESSION_STATUS);
			// selectAid = getUsimAppAid(cms2acParam);
		} else if (persoPrepared || Operation.PERSONALIZE_APP.equals(localTransaction.getProcedureName())) {
			selectAid = localTransaction.getAid();
		} else {
			selectAid = cms2acParam.getCurrentSecurityDomain().getAid();
		}
		if (!(hasNoSecurity || persoPrepared || Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp()))) {
			throw new PlatformException(PlatformErrorCode.INVALID_SCP);
		}
		return buildSelectCmd(cms2acParam, selectAid);
	}

	public ApduCommand buildSelectCmd(Cms2acParam cms2acParam, String selectAid) {
		if (StringUtils.isEmpty(selectAid)) {
			throw new PlatformException(PlatformErrorCode.SELECT_APP_ERROR);
		}

		SecurityDomain sd = securityDomainManager.getByAid(selectAid);
		if (!SystemConfigUtils.isCms2acRuntimeEnvironment() && (null != sd && sd.isIsd())) {// 如果运行环境不是CMS2AC并且选择的是主安全域，使用配置文件指定的AID
			selectAid = SystemConfigUtils.getMockIsdAppAid();
		}

		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x00);
		cmd.setIns((byte) 0xA4);
		cmd.setP1((byte) (0x04));
		cmd.setP2((byte) (0x00));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);
		cmd.setData(hexString2ByteArray(selectAid));
		setCmdLc(cmd);
		cmd.setLe((byte) 0x00);
		cmd.setRawHex(toHexString(cmd.toByteArray()));
		return cmd;
	}

	// TODO 得到卡片usim的卡商暂时没用处了
	// private String getUsimAppAid(Cms2acParam cms2acParam) {
	// String cardNo = cms2acParam.getLocalTransaction().getCardNo();
	// CardInfo cardInfo = cardInfoManager.getByCardNo(cardNo);
	// String cardVendor = cardInfo.getCardBaseInfo().getCardManufacturer();
	// return cardVendorAidMap.get(cardVendor);
	// }

	public void parseSelectRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if ((result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00)
				|| (result.getSw1() == (byte) 0x62 && result.getSw2() == (byte) 0x83)) {// 主安全域被锁定后下发select，会返回6283
		} else {
			throw new ApduException("parseInstallRsp", getErrorCode(result));
		}
	}

	public ApduCommand buildInstallForExtraditeTokenCmd(Cms2acParam cms2acParam, byte[] tokenBytes) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE6);
		cmd.setP1((byte) (0x10));
		cmd.setP2((byte) (0x00));
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setData(getInstallForExtraditeDataToken(cms2acParam, tokenBytes));
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	private byte[] getInstallForExtraditeDataToken(Cms2acParam cms2acParam, byte[] tokenBytes) {
		byte[] installData = new byte[0];
		LocalTransaction localTransaction = cms2acParam.getLocalTransaction();
		String appAid = localTransaction.getAid();

		Application app = applicationManager.getByAid(appAid);
		String sdAid = app.getSd().getAid();

		byte[] appAidBytes = getLvBytes(appAid);
		byte[] sdAidBytes = getLvBytes(sdAid);

		installData = contactArray(installData, sdAidBytes);
		installData = contactArray(installData, hexString2ByteArray("00"));
		installData = contactArray(installData, appAidBytes);
		installData = contactArray(installData, hexString2ByteArray("0000"));

		// byte[] tokenBytes = new byte[] { 0x00 };
		installData = contactArray(installData, getLvBytes(tokenBytes));

		return installData;
	}

	private byte[] getLvBytes(byte[] value) {
		byte[] length = intToHexBytes(value.length, 1);
		return contactArray(length, value);
	}

	public List<ApduCommand> buildStoreDataCmd(Cms2acParam cms2acParam, List<byte[]> datas) {
		List<ApduCommand> result = new ArrayList<ApduCommand>();
		for (int i = 0; i < datas.size(); i++) {
			ApduCommand cmd = new ApduCommand();
			cmd.setCla((byte) 0x80);
			cmd.setIns((byte) 0xE2);
			cmd.setP1((byte) 0x00);
			cmd.setP2((byte) i);
			cmd.setType(ApduCommand.CMD_TYPE_FIVE);
			cmd.setData(datas.get(i));
			setCmdLc(cmd);
			cmd.setRawHex(toHexString(cmd.toByteArray()));
			result.add(cmd);
		}

		ApduCommand cmd = result.get(result.size() - 1);// 获得最后一条
		cmd.setP1((byte) 0x80);// 将最后一条StoreData的P1字段设为0x80，表示没有下一指令
		cmd.setRawHex(toHexString(cmd.toByteArray()));

		result = mashalSecureForCommands(cms2acParam, result);

		return result;
	}

	private List<ApduCommand> mashalSecureForCommands(Cms2acParam cms2acParam, List<ApduCommand> result) {
		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			List<ApduCommand> securedCommands = new ArrayList<ApduCommand>();
			for (ApduCommand apduCommand : result) {
				apduCommand = mashalSecurePack(apduCommand, cms2acParam, securedCommands);
				securedCommands.add(apduCommand);
			}
			result = securedCommands;
		}
		return result;
	}

	public void parseStoreDataRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			return;
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));
		}
	}

	public byte[] parseOta3StoreDataRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		return new byte[] { result.getSw1(), result.getSw2() };
	}

	public ApduCommand buildStoreDataCmd(Cms2acParam cms2acParam, String data) {

		List<byte[]> datas = new ArrayList<byte[]>();
		datas.add(hexString2ByteArray(data));

		List<ApduCommand> cmds = buildStoreDataCmd(cms2acParam, datas);
		return cmds.get(0);
	}

	private ApduCommand converHexString2ApduCommand(String cmdStr) {
		log.debug("\n" + cmdStr + "\n");

		byte[] cmdBytes = hexString2ByteArray(cmdStr);

		ApduCommand cmd = new ApduCommand();
		cmd.setCla(cmdBytes[0]);
		cmd.setIns(cmdBytes[1]);
		cmd.setP1(cmdBytes[2]);
		cmd.setP2(cmdBytes[3]);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		cmd.setLc(cmdBytes[4]);
		cmd.setData(subArray(cmdBytes, 5, 5 + cmdBytes[4]));

		cmd.setRawHex(toHexString(cmd.toByteArray()));
		return cmd;
	}

	public ApduCommand buildInstallPersoCmd(Cms2acParam cms2acParam, String personalAid) {
		ApduCommand cmd = new ApduCommand();
		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xE6);// install
		cmd.setP1((byte) 0x20);// for personalize
		cmd.setP2((byte) 0x00);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		byte[] aid = hexString2ByteArray(personalAid);

		byte[] data = contactArray(new byte[] { (byte) aid.length }, aid);
		data = contactArray(new byte[] { (byte) 0x00, (byte) 0x00 }, data);
		data = contactArray(data, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00 });

		cmd.setData(data);
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	public void parseInstallPersoRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
		} else {
			throw new ApduException("parseInstallRsp", getErrorCode(result));
		}

	}

	public ApduCommand buildGetStatusPackageCmd(Cms2acParam cms2acParam) {
		ApduCommand cmd = new ApduCommand();

		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xF2);// get status
		cmd.setP1((byte) 0x20);// package
		cmd.setP2((byte) 0x00);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		byte[] data = { 0x4f, 0x00 };
		cmd.setData(data);
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	public ApduCommand buildGetStatusAppletCmd(Cms2acParam cms2acParam) {
		ApduCommand cmd = new ApduCommand();

		cmd.setCla((byte) 0x80);
		cmd.setIns((byte) 0xF2);// get status
		cmd.setP1((byte) 0x40);// applet
		cmd.setP2((byte) 0x00);
		cmd.setType(ApduCommand.CMD_TYPE_FIVE);

		byte[] data = { 0x4f, 0x00 };
		cmd.setData(data);
		cmd = buildDeleteCmdFoot(cms2acParam, cmd);
		return cmd;
	}

	/**
	 * 返回格式为：package aid:状态
	 * 
	 * @param cms2acParam
	 * @return
	 */
	public Map<String, Integer> parseGetStatusPackageRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {

			byte[] data = result.getData();
			int totalLen = data.length;
			short offset = 0;
			String aid;
			int lifeCycle;
			byte aidLen;
			while (totalLen > 0) {
				aidLen = data[offset];
				offset += 1;// point to aid start
				byte[] aidData = ByteUtils.subArray(data, offset, offset + aidLen);

				offset += aidLen; // point to life cycle
				lifeCycle = data[offset];
				offset += 2;// point to the next aidLen

				aid = toHexString(aidData);

				map.put(aid, lifeCycle);

				totalLen = totalLen - aidLen - 3;
			}
		} else {
			throw new ApduException("parseInstallRsp", getErrorCode(result));
		}
		return map;
	}

	/**
	 * 返回格式为：应用aid:状态
	 * 
	 * @param cms2acParam
	 * @return
	 */
	public Map<String, Integer> parseGetStatusAppletRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {

			byte[] data = result.getData();
			int totalLen = data.length;
			short offset = 0;
			String aid;
			int lifeCycle;
			byte aidLen;
			while (totalLen > 0) {
				aidLen = data[offset];
				offset += 1;// point to aid start
				byte[] aidData = ByteUtils.subArray(data, offset, offset + aidLen);

				offset += aidLen; // point to life cycle
				lifeCycle = data[offset];
				offset += 2;// point to the next aidLen

				aid = toHexString(aidData);

				map.put(aid, lifeCycle);

				totalLen = totalLen - aidLen - 3;
			}
		} else {
			throw new ApduException("parseInstallRsp", getErrorCode(result));
		}
		return map;
	}

	public List<ApduCommand> buildWriteTokenCmd(Cms2acParam cms2acParam) {
		CardInfo card = cardManager.getByCardNo(cms2acParam.getLocalTransaction().getCardNo());

		List<byte[]> datas = new ArrayList<byte[]>();

		TlvObject tag1047 = new TlvObject();
		{
			TlvObject tag01 = new TlvObject();
			tag01.add("01", ConvertUtils.hexString2ByteArray(card.getToken()));
			tag1047.add(ConvertUtils.int2HexString(GET_DATA_CMD_P1P2_TOKEN, 2 * 2), tag01);
		}
		datas.add(ConvertUtils.hexString2ByteArray(tag1047.build()));

		return buildStoreDataCmd(cms2acParam, datas);
	}

	public ApduCommand buildReadTokenCmd(Cms2acParam cms2acParam) {
		return buildGetDataCmdWithSecurity(cms2acParam, GET_DATA_CMD_P1P2_TOKEN);
	}

	public GetDataReadTokenResponse parseReadTokenRsp(Cms2acParam cms2acParam) {
		ApduResult result = cms2acParam.getLastApduResult();

		if (Constants.CMS2AC_SCP_02.equals(cms2acParam.getScp())) {
			result = unmashalSecurePack(result, cms2acParam);
		}

		if (result.getSw1() == (byte) 0x90 && result.getSw2() == (byte) 0x00) {
			GetDataReadTokenResponse response = new GetDataReadTokenResponse();
			response.setData(result.getData());
			response.parseData();

			return response;
		} else {
			throw new ApduException("getTransProofResult", getErrorCode(result));

		}
	}

	/**
	 * 构建应用访问安全域(方式二)的Apdu指令
	 * 
	 * @param cms2acParam
	 * 
	 * @param string
	 *            个人化指令模板
	 * 
	 * @return 个人化指令
	 */
	public List<ApduCommand> buildAppToSdCmd(Cms2acParam cms2acParam, String fileContent) {
		List<String> cmds = parseFileContent(cms2acParam, fileContent);

		List<ApduCommand> cmdBatch = new ArrayList<ApduCommand>();

		for (String cmdStr : cmds) {
			ApduCommand cmd = converHexString2ApduCommand(cmdStr);
			cmdBatch.add(cmd);
		}

		cmdBatch = mashalSecureForCommands(cms2acParam, cmdBatch);

		return cmdBatch;
	}

	/**
	 * 构建安全域访问应用(方式三，store data)的Apdu指令
	 * 
	 * @param cms2acParam
	 * @param fileContent
	 *            个人化指令模板
	 * @param mocamPersoLength
	 *            每条指令数据域最大长度
	 * @return 个人化指令
	 */
	public List<ApduCommand> buildSdToAppCmd(Cms2acParam cms2acParam, String fileContent, int mocamPersoLength) {
		List<String> cmds = parseFileContent(cms2acParam, fileContent);// 指令的数据

		List<byte[]> cmdsBytes = new ArrayList<byte[]>();
		for (String cmd : cmds) {
			log.debug("\n" + cmd + "\n");
			cmdsBytes.add(ConvertUtils.hexString2ByteArray(cmd));
		}

		return buildStoreDataCmd(cms2acParam, cmdsBytes);
	}

	/**
	 * 解析个人化指令模板
	 * 
	 * @param cms2acParam
	 * @param fileContent
	 *            个人化指令模板
	 * @return 解析后的个人化指令
	 */
	private List<String> parseFileContent(Cms2acParam cms2acParam, String fileContent) {
		Application application = applicationManager.getByAid(cms2acParam.getLocalTransaction().getAid());
		List<String> transformedApdus = new ArrayList<String>();

		// 首先使用tk对进行个人化指令的报文进行解密
		String plaintextHexString = fileContent;
		if (SystemConfigUtils.needPersoPackageDecrypt()) {// 如果需要使用TK对报文解密，执行解密
			byte[] plaintext = scp02Service.decryptPersoData(ConvertUtils.hexString2ByteArray(fileContent), application);
			plaintextHexString = ConvertUtils.byteArray2HexString(plaintext);

			// 处理明文报文
			int paddingBeginIndex = plaintextHexString.lastIndexOf("80");// 报文中最后一个0x80开始的是填充数据，需要丢弃
			plaintextHexString = plaintextHexString.substring(0, paddingBeginIndex);
		}

		if (SystemConfigUtils.needValidateIntegrality()) {
			byte[] messageWithDigest = ConvertUtils.hexString2ByteArray(plaintextHexString);
			int digestBeginIndex = messageWithDigest.length - 20;
			byte[] message = ArrayUtils.subarray(messageWithDigest, 0, digestBeginIndex);
			byte[] digestInMessage = ArrayUtils.subarray(messageWithDigest, digestBeginIndex, messageWithDigest.length);

			Sha1Utils sha1Utils = new Sha1Utils();
			byte[] digestCalc = sha1Utils.getDigestOfBytes(message);
			if (!ArrayUtils.isEquals(digestInMessage, digestCalc)) {
				// TODO throw a exception
			}

			plaintextHexString = ConvertUtils.byteArray2HexString(message);
		}

		List<byte[]> apdus = LvObject.parse(plaintextHexString, 2).getValues();
		for (byte[] apud : apdus) {
			// 开始处理转加密
			MutilTagTlvObject tlv = MutilTagTlvObject.parse(apud);
			List<ValueEntry> cipherDatas = tlv.getByTag(PESON_CMD_CIPHER_TAG);

			for (ValueEntry cipherData : cipherDatas) {
				byte[] value = cipherData.getValue();
				byte[] transformedValue = value;
				if (SystemConfigUtils.needTransformEncrypt()) {// 如果需要使用KEK对敏感数据进行转加密，执行转加密
					cipherData.setValue(transformedValue);
				}
			}

			List<ValueEntry> plainDatas = tlv.getByTag(PESON_CMD_PLAIN_TAG);

			StringBuffer dataBuffer = new StringBuffer();
			int plain = 0;
			int cipher = 0;
			// 对明文数据和转加密密文数据按照在指令模板中的顺序进行归并排序，保证不会因为个人化指令处理导致顺序与原来不同
			for (; (plain < plainDatas.size()) && (cipher < cipherDatas.size());) {
				if (plainDatas.get(plain).getIndex() < cipherDatas.get(cipher).getIndex()) {
					dataBuffer.append(ConvertUtils.byteArray2HexString(plainDatas.get(plain).getValue()));
					plain++;
				} else {
					dataBuffer.append(ConvertUtils.byteArray2HexString(plainDatas.get(cipher).getValue()));
					cipher++;
				}
			}
			// 循环结束后，应该将明文数据或者转加密密文数据中的一个添加完成
			if (plain < plainDatas.size()) {// 如果还有明文数据，将剩余明文数据添加到结果中
				dataBuffer.append(ConvertUtils.byteArray2HexString(plainDatas.get(plain).getValue()));
				plain++;
			}
			if (cipher < cipherDatas.size()) {// 如果还有转加密密文数据，将剩余转加密密文数据添加到结果中
				dataBuffer.append(ConvertUtils.byteArray2HexString(cipherDatas.get(cipher).getValue()));
				cipher++;
			}

			transformedApdus.add(dataBuffer.toString());
		}

		return transformedApdus;
	}

	public List<ApduCommand> buildStoreMobileNo(Cms2acParam cms2acParam) {
		String mobileNo = cms2acParam.getLocalTransaction().getMobileNo();

		List<byte[]> datas = new ArrayList<byte[]>();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < mobileNo.length(); i++) {
			buf.append("0" + mobileNo.charAt(i));
		}
		mobileNo = buf.toString();

		TlvObject tag2F14 = new TlvObject();
		tag2F14.add(ConvertUtils.int2HexString(GET_DATA_CMD_P1P2_MSISDN, 2 * 2), mobileNo);
		datas.add(ConvertUtils.hexString2ByteArray(tag2F14.build()));

		return buildStoreDataCmd(cms2acParam, datas);
	}
}
