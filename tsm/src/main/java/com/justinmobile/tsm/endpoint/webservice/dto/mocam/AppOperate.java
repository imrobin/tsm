package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Command", namespace = NameSpace.CM)
public class AppOperate {

	@XmlElement(name = "AppAID", namespace = NameSpace.CM)
	private String appAid;

	@XmlElement(name = "AppVersion", namespace = NameSpace.CM)
	private String appVersion;

	@XmlElement(name = "CommandID", namespace = NameSpace.CM)
	private Integer operation;

	@XmlElement(name = "OriginalSEID", namespace = NameSpace.CM)
	private String originalCardNo;

	public String getAppAid() {
		return appAid;
	}

	public void setAppAid(String appAid) {
		this.appAid = appAid;
	}

	public Integer getOperation() {
		return operation;
	}

	public void setOperation(Integer operation) {
		this.operation = operation;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getOriginalCardNo() {
		return originalCardNo;
	}

	public void setOriginalCardNo(String originalCardNo) {
		this.originalCardNo = originalCardNo;
	}

}
