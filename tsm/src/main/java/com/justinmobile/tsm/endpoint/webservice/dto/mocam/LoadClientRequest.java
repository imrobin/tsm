package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoadClientRequest", namespace = NameSpace.CM)
public class LoadClientRequest extends BasicRequest {
	
	@XmlElement(namespace = NameSpace.CM, name="SEID")
	private String cardNo;
	
	@XmlElement(namespace = NameSpace.CM, name="UpgradeType")
	private String upgradeType;
	
	@XmlElement(namespace = NameSpace.CM, name="AppAId")
	private String appAId;
	
	@XmlElement(namespace = NameSpace.CM, name="MobileOs")
	private String mobileOs;
	
	@XmlElement(namespace = NameSpace.CM, name="OsVersion")
	private String osVersion;

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(String upgradeType) {
		this.upgradeType = upgradeType;
	}

	public String getAppAId() {
		return appAId;
	}

	public void setAppAId(String appAId) {
		this.appAId = appAId;
	}

	public String getMobileOs() {
		return mobileOs;
	}

	public void setMobileOs(String mobileOs) {
		this.mobileOs = mobileOs;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

}
