package com.justinmobile.tsm.endpoint.webservice.dto.sp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonalizationResult", namespace = NameSpace.CM)
public class PersonalizationResult {

	@XmlElement(namespace = NameSpace.CM)
	private String sessionId;

	@XmlElement(namespace = NameSpace.CM)
	private String seqNum;

	@XmlElement(namespace = NameSpace.CM)
	private String timeStamp;

	@XmlElement(namespace = NameSpace.CM)
	private String msisdn;

	@XmlElement(namespace = NameSpace.CM)
	private String appAID;

	@XmlElement(namespace = NameSpace.CM)
	private CardPOR cardPOR;

	@XmlElement(namespace = NameSpace.CM)
	private Integer sessionType;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getAppAID() {
		return appAID;
	}

	public void setAppAID(String appAID) {
		this.appAID = appAID;
	}

	public CardPOR getCardPOR() {
		return cardPOR;
	}

	public void setCardPOR(CardPOR cardPOR) {
		this.cardPOR = cardPOR;
	}

	public Integer getSessionType() {
		return sessionType;
	}

	public void setSessionType(Integer sessionType) {
		this.sessionType = sessionType;
	}

}
