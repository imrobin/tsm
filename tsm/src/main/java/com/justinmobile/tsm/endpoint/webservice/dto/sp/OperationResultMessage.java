package com.justinmobile.tsm.endpoint.webservice.dto.sp;

import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;

public class OperationResultMessage {

	/** 交易序号 */
	private String seqNum;

	/** 会话ID */
	private String sessionId;

	/** 会话类型 */
	private Integer sessionType;

	/** 时间戳 */
	private String timeStamp;

	/** 源包交易序号 */
	private String originalSeqNum;

	/** AID */
	private String aid;

	/** 手机号 */
	private String msisdn;

	/** 卡端操作结果返回码 */
	private String reslutCode;

	/** 卡端操作结果返回消息 */
	private String resultMsg;

	/** IMSI */
	private String imsi;

	/** IMEI */
	private String imei;

	/** 卡响应 */
	private CardPOR cardPOR;

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getSessionType() {
		return sessionType;
	}

	public void setSessionType(Integer sessionType) {
		this.sessionType = sessionType;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getOriginalSeqNum() {
		return originalSeqNum;
	}

	public void setOriginalSeqNum(String originalSeqNum) {
		this.originalSeqNum = originalSeqNum;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getReslutCode() {
		return reslutCode;
	}

	public void setReslutCode(String reslutCode) {
		this.reslutCode = reslutCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public CardPOR getCardPOR() {
		return cardPOR;
	}

	public void setCardPOR(CardPOR cardPOR) {
		this.cardPOR = cardPOR;
	}
}
