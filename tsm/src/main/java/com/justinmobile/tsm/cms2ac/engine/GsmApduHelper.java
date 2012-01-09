package com.justinmobile.tsm.cms2ac.engine;

import static com.justinmobile.core.utils.ByteUtils.binaryToInt;
import static com.justinmobile.core.utils.ByteUtils.contactArray;
import static com.justinmobile.core.utils.ByteUtils.intToHexBytes;
import static com.justinmobile.core.utils.ByteUtils.rightSubArray;
import static com.justinmobile.core.utils.ByteUtils.subArray;
import static com.justinmobile.core.utils.ByteUtils.toHexString;

import java.util.Calendar;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.cms2ac.Constants;
import com.justinmobile.tsm.cms2ac.exception.ApduResponseException;

@Service("gsmApduHelper")
public class GsmApduHelper {

	public static final String PROACTIVE_CMD_TYPE_DISPLAY_TEXT = "21";

	public static final String PROACTIVE_CMD_TYPE_SETUP_MENU = "25";

	public byte[] getPPDownloadData(String ota3Comand) {
		byte[] tag = new byte[] { (byte) 0xD1 };

		byte[] value = new byte[0];
		value = contactArray(value, getDeviceId());
		value = contactArray(value, getAddress());
		value = contactArray(value, getDeliverSmsTpdu(ota3Comand));

		return buildTlv(tag, value);
	}

	public byte[] getTerminalRespnseData(String proactiveCmdType) {
		return ConvertUtils.hexString2ByteArray("810301" + proactiveCmdType + "0082028182030100");
	}

	public byte[] parsePPDownloadRsp(byte[] ppDownloadRsp) {
		byte[] proactiveCmdData = extractTlvData((byte) 0xD0, ppDownloadRsp);
		byte[] commandDetails81or01 = extractTlvContext((byte) 0x81, (byte) 0x01, proactiveCmdData);
		byte[] proactiveCmdType = subArray(commandDetails81or01, 3, 4);
		byte[] buffer = rightSubArray(proactiveCmdData, commandDetails81or01.length);
		byte[] commandDetails82or02 = extractTlvContext((byte) 0x82, (byte) 0x02, buffer);
		buffer = rightSubArray(buffer, commandDetails82or02.length);
		byte[] commandDetails86or06 = extractTlvContext((byte) 0x86, (byte) 0x06, buffer);

		buffer = rightSubArray(buffer, commandDetails86or06.length);
		if (isSendSms(proactiveCmdType)) {
			byte[] submitSmsTpdu = buffer;
			byte[] tpduData = null;
			try {
				tpduData = extractTlvData((byte) 0x0B, submitSmsTpdu);
			} catch (Exception e) {
				tpduData = extractTlvData((byte) 0x8B, submitSmsTpdu);
			}
			if (tpduData == null) {
				throw new IllegalArgumentException("error smsTpdu format");
			}
			byte tpVpf = tpduData[0];
			int tpLength = binaryToInt(subArray(tpduData, 2, 3));
			//复杂的计算
			int pos = 2 + (tpLength + 1) / 2 + 2 + 2 + 1;
			if (tpVpf == (byte)0x01) {
				tpduData = rightSubArray(tpduData, pos);
			} else if (tpVpf == (byte)0x11) {
				tpduData = rightSubArray(tpduData, pos + 1);
			} else if (tpVpf == (byte)0x19) {
				tpduData = rightSubArray(tpduData, pos + 7);
			} else if (tpVpf == (byte)0x09) {
				tpduData = rightSubArray(tpduData, pos + 7);
			}
//			int udl = binaryToInt(subArray(tpduData, 10, 11));
//			return subArray(tpduData, 11, 11 + udl);
			return tpduData;
		} else {
			String cmdTypeHex = toHexString(proactiveCmdType);
			if (isSetupMenuCmd(cmdTypeHex) || isDisplayTextCmd(cmdTypeHex)) {
				return proactiveCmdType;
			} else {
				throw new ApduResponseException("parsePPDownloadRsp", "error proactive command");
			}
		}
	}

	public boolean isDisplayTextCmd(String proactiveCmdType) {
		return PROACTIVE_CMD_TYPE_DISPLAY_TEXT.equals(proactiveCmdType);
	}

	public boolean isSetupMenuCmd(String proactiveCmdType) {
		return PROACTIVE_CMD_TYPE_SETUP_MENU.equals(proactiveCmdType);
	}

	private boolean isSendSms(byte[] proactiveCmdType) {
		return proactiveCmdType[0] == (byte) 0x13;
	}

	private byte[] getDeviceId() {
		return ConvertUtils.hexString2ByteArray("02028381");
	}

	private byte[] getAddress() {
		return ConvertUtils.hexString2ByteArray("060891683108200345F0");
	}

	private byte[] getDeliverSmsTpdu(String ota3Comand) {
		byte[] tag = new byte[] { (byte) 0x0B };

		byte[] value = new byte[0];
		value = contactArray(value, getDeliverConfig());
		value = contactArray(value, getTpOa());
		value = contactArray(value, getTpPidDcs());
		value = contactArray(value, getTpScts());
		value = contactArray(value, getTpUdl(ota3Comand));
		value = contactArray(value, getTpUd(ota3Comand));

		return buildTlv(tag, value);
	}

	private byte[] getTpUd(String ota3Comand) {
		return ConvertUtils.hexString2ByteArray(ota3Comand);
	}

	private byte[] getTpUdl(String ota3Comand) {
		return intToHexBytes(ConvertUtils.hexString2ByteArray(ota3Comand).length, 1);
	}

	private byte[] getTpScts() {
		Calendar calendar = Calendar.getInstance();
		String scts = DateFormatUtils.format(calendar, "yyMMddHHmmss");
		scts += "32";
		byte[] rawSctsBytes = ConvertUtils.hexString2ByteArray(scts);
		return convertGsmBytes(rawSctsBytes);
	}

	private byte[] getTpPidDcs() {
		return ConvertUtils.hexString2ByteArray("7FF6");
	}

	private byte[] getTpOa() {
		byte[] tpOa = new byte[0];
		byte[] address = convertGsmBytes(ConvertUtils.hexString2ByteArray(Constants.CMS2AC_SP_ADN));
		byte[] addrSemiOctLen = intToHexBytes(address.length * 2, 1);

		tpOa = contactArray(tpOa, addrSemiOctLen);
		tpOa = contactArray(tpOa, intToHexBytes(0xA1, 1));
		tpOa = contactArray(tpOa, address);
		return tpOa;
	}

	private byte[] getDeliverConfig() {
		int tpMti = 0x00;
		int tpMms = 0x01;
		int tpSri = 0x00;
		int tpUdhi = 0x01;
		int tpRp = 0x00;
		int config = (tpRp << 7) | (tpUdhi << 6) | (tpSri << 5) | (tpMms << 2) | tpMti;
		return intToHexBytes(config, 1);
	}

	private byte[] buildTlv(byte[] tag, byte[] value) {
		byte[] tlv = tag;
		byte[] length = intToHexBytes(value.length, 1);
		tlv = contactArray(tlv, length);
		tlv = contactArray(tlv, value);
		return tlv;
	}

	private byte[] extractTlvData(byte tag, byte[] tlv) {
		if (tlv == null || tlv.length < 2) {
			throw new IllegalArgumentException("error tlv format");
		}
		if (tag != tlv[0]) {
			throw new IllegalArgumentException("tag mismatch");
		}

		int length = binaryToInt(subArray(tlv, 1, 2));
		if (length != tlv.length - 2) {
			throw new IllegalArgumentException("error tlv length");
		}
		return rightSubArray(tlv, 2);
	}
	
	private byte[] extractTlvContext(byte tag, byte sameTag, byte[] tlv) {
		if (tlv == null || tlv.length < 2) {
			throw new IllegalArgumentException("error tlv format");
		}
		if (tag != tlv[0]) {
			if (sameTag != tlv[0]) {
				throw new IllegalArgumentException("tag mismatch");
			}
		} 
		int length = binaryToInt(subArray(tlv, 1, 2));
		return subArray(tlv, 0, length + 2);
	}

	private byte[] convertGsmBytes(byte[] src) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src.length; i++) {
			String temp = toHexString(new byte[] { src[i] });
			sb.append(temp.charAt(1));
			sb.append(temp.charAt(0));

		}
		return ConvertUtils.hexString2ByteArray(sb.toString());
	}

	public static void main(String[] args) {
		GsmApduHelper gsmApduHelper = new GsmApduHelper();
		
		//		int config = binaryToInt(gsmApduHelper.getDeliverConfig());
//		System.out.println(Integer.toBinaryString(config));
//		System.out.println(toHexString(gsmApduHelper.getTpOa()));
//
//		String ota3Comand = "02700000191102000011B000100000000001008F692B3133D6A51E010E00";
//		System.out.println(toHexString(gsmApduHelper.getPPDownloadData(ota3Comand)));
		byte[] ppDownloadRsp = ConvertUtils.hexString2ByteArray("D05281030113008202818306090891683108701905F00B3C11000C810156040005007FF68F2E57014906005110075107000000000026EBE9F1D002D8090F15123449060051100751070A083E00623A0100000001");
		byte[] result = gsmApduHelper.parsePPDownloadRsp(ppDownloadRsp);
		System.out.println(toHexString(result));
	}
}
