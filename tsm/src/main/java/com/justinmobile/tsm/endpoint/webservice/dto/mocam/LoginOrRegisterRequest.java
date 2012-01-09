package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoginOrRegisterRequest", namespace = NameSpace.CM)
public class LoginOrRegisterRequest extends BasicRequest {

	@XmlElement(namespace = NameSpace.CM, name="SEID")
	private String cardNo;

	@XmlElement(namespace = NameSpace.CM, name="IMSI")
	private String imsi;
	
	@XmlElement(namespace = NameSpace.CM, name="AppInfoList")
	private AppInfoList appInfoList;

	@XmlElement(namespace = NameSpace.CM, name="ClientInfoList")
	private ClientInfoList clientInfoList;

	@XmlElement(namespace = NameSpace.CM, name="ChallengeNo")
	private String challengeNo;
	
	@XmlElement(namespace = NameSpace.CM, name="POSID")
	private String posId;
	
	@XmlElement(namespace = NameSpace.CM, name="IMEI")
	private String imei;
	
	@XmlElement(namespace = NameSpace.CM, name="MemoryInfo")
	private MemoryInfo memoryInfo;

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public AppInfoList getAppInfoList() {
		return appInfoList;
	}

	public void setAppInfoList(AppInfoList appInfoList) {
		this.appInfoList = appInfoList;
	}

	public ClientInfoList getClientInfoList() {
		return clientInfoList;
	}

	public void setClientInfoList(ClientInfoList clientInfoList) {
		this.clientInfoList = clientInfoList;
	}

	public String getChallengeNo() {
		return challengeNo;
	}

	public void setChallengeNo(String challengeNo) {
		this.challengeNo = challengeNo;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public MemoryInfo getMemoryInfo() {
		return memoryInfo;
	}

	public void setMemoryInfo(MemoryInfo memoryInfo) {
		this.memoryInfo = memoryInfo;
	}

}
