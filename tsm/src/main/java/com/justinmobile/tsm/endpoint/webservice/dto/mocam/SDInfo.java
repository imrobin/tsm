package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDInfo", namespace = NameSpace.CM)
public class SDInfo {
	
	@XmlElement(namespace = NameSpace.CM, name="SDAID")
	private String sdAid;
	
	@XmlElement(namespace = NameSpace.CM, name="SDName")
	private String sdName;
	
	@XmlElement(namespace = NameSpace.CM, name="AppProvider")
	private String appProvider;
	
	@XmlElement(namespace = NameSpace.CM, name="Province")
	private String province;
	
	@XmlElement(namespace = NameSpace.CM, name="AppStatus")
	private Integer appStatus;
	
	@XmlElement(namespace = NameSpace.CM, name="IsIncludeApp")
	private Integer isIncludeApp;

	public String getSdAid() {
		return sdAid;
	}

	public void setSdAid(String sdAid) {
		this.sdAid = sdAid;
	}

	public String getSdName() {
		return sdName;
	}

	public void setSdName(String sdName) {
		this.sdName = sdName;
	}

	public String getAppProvider() {
		return appProvider;
	}

	public void setAppProvider(String appProvider) {
		this.appProvider = appProvider;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Integer appStatus) {
		this.appStatus = appStatus;
	}

	public Integer getIsIncludeApp() {
		return isIncludeApp;
	}

	public void setIsIncludeApp(Integer isIncludeApp) {
		this.isIncludeApp = isIncludeApp;
	}

}
