package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecAPDUsRequest", namespace = NameSpace.CM)
@XmlRootElement(name = "ExecAPDUsRequest", namespace = NameSpace.CM)
public class ReqExecAPDU extends BasicRequest {

	@XmlElement(name = "TimeStamp", namespace = NameSpace.CM)
	private String timeStamp;

	@XmlElement(name = "SeqNum", namespace = NameSpace.CM)
	private String seqNum;

	@XmlElement(name = "CommandList", namespace = NameSpace.CM)
	private AppList appList;

	@XmlElement(name = "SEID", namespace = NameSpace.CM)
	private String cardNo;

	@XmlElement(name = "CurrentAppAid", namespace = NameSpace.CM)
	private String currentAppAid;

	@XmlElement(name = "CardPOR", namespace = NameSpace.CM)
	private CardPOR cardPOR;

	@XmlElement(name = "PushSerial", namespace = NameSpace.CM)
	private String pushSerial;

	@XmlElement(name = "MSISDN", namespace = NameSpace.CM)
	private String mobileNo;

	@XmlElement(name = "POSID", namespace = NameSpace.CM)
	private String posId;

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public AppList getAppList() {
		return appList;
	}

	public void setAppList(AppList appList) {
		this.appList = appList;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCurrentAppAid() {
		return currentAppAid;
	}

	public void setCurrentAppAid(String currentAppAid) {
		this.currentAppAid = currentAppAid;
	}

	public CardPOR getCardPOR() {
		return cardPOR;
	}

	public void setCardPOR(CardPOR cardPOR) {
		this.cardPOR = cardPOR;
	}

	public String getPushSerial() {
		return pushSerial;
	}

	public void setPushSerial(String pushSerial) {
		this.pushSerial = pushSerial;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}
}
