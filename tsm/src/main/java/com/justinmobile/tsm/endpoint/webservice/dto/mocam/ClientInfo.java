package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientInformation", namespace = NameSpace.CM)
public class ClientInfo {

	@XmlElement(namespace = NameSpace.CM, name="ClientName")
	private String clientName;

	@XmlElement(namespace = NameSpace.CM, name="ClientID")
	private String clientID;
	
	@XmlElement(namespace = NameSpace.CM, name="ClientClassName")
	private String clientClassName;

	@XmlElement(namespace = NameSpace.CM, name="AppAID")
	private String appAID;
	
	@XmlElement(namespace = NameSpace.CM, name="ClientVersion")
	private Long clientVersion;
	
	@XmlElement(namespace = NameSpace.CM, name="ClientSize")
	private Long clientSize;
	
	@XmlElement(namespace = NameSpace.CM, name="ClientLoadURL")
	private String clientLoadURL;
	
	@XmlElement(namespace = NameSpace.CM, name="IsUpdatable")
	private Integer isUpdatable;
	
	@XmlElement(namespace = NameSpace.CM, name="ClientPackageName")
	private String clientPackageName;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getAppAID() {
		return appAID;
	}

	public void setAppAID(String appAID) {
		this.appAID = appAID;
	}

	public Long getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(Long clientVersion) {
		this.clientVersion = clientVersion;
	}

	public Long getClientSize() {
		return clientSize;
	}

	public void setClientSize(Long clientSize) {
		this.clientSize = clientSize;
	}

	public String getClientLoadURL() {
		return clientLoadURL;
	}

	public void setClientLoadURL(String clientLoadURL) {
		this.clientLoadURL = clientLoadURL;
	}

	public Integer getIsUpdatable() {
		return isUpdatable;
	}

	public void setIsUpdatable(Integer isUpdatable) {
		this.isUpdatable = isUpdatable;
	}

	public String getClientClassName() {
		return clientClassName;
	}

	public void setClientClassName(String clientClassName) {
		this.clientClassName = clientClassName;
	}
    
	public String getClientPackageName() {
		return clientPackageName;
	}

	public void setClientPackageName(String clientPackageName) {
		this.clientPackageName = clientPackageName;
	}

	public void  build(String aid, ApplicationClientInfo applicationClientInfo) {
		this.setAppAID(aid);
		this.setClientID(String.valueOf(applicationClientInfo.getId()));
		this.setClientLoadURL(SystemConfigUtils.getServiceUrl()+applicationClientInfo.getFileUrl());
		this.setClientName(applicationClientInfo.getName());
		this.setClientSize(applicationClientInfo.getSize());
		this.setClientVersion(applicationClientInfo.getVersionCode().longValue());
		this.setIsUpdatable(0x00);
		this.setClientClassName(applicationClientInfo.getClientClassName());
		this.setClientPackageName(applicationClientInfo.getClientPackageName());
	}
}
