package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResLoginOrRegister", namespace = NameSpace.CM)
public class ResLoginOrRegister {

	@XmlElement(name = "MOCAMVersion", namespace = NameSpace.CM)
	private String mocamVersion;

	@XmlElement(name = "URL", namespace = NameSpace.CM)
	private String url;
	
	@XmlElement(name = "CommandID", namespace = NameSpace.CM)
	private String commandID;

	@XmlElement(name = "TimeStamp", namespace = NameSpace.CM)
	private String timeStamp;

	@XmlElement(name = "ExecStatus", namespace = NameSpace.CM)
	private Status status;

	@XmlElement(name = "SessionID", namespace = NameSpace.CM)
	private String sessionID;

	@XmlElement(name = "APDUList", namespace = NameSpace.CM)
	private APDUList apduList = new APDUList();

	@XmlElement(name = "Progress", namespace = NameSpace.CM)
	private String progress;

	@XmlElement(name = "ProgressPercent", namespace = NameSpace.CM)
	private String progressPercent;

	@XmlElement(name = "ApduName", namespace = NameSpace.CM)
	private String apduName;
	
	@XmlElement(namespace = NameSpace.CM, name="AppInfoList")
	private AppInfoList appInfoList;

	@XmlElement(namespace = NameSpace.CM, name="ClientInfoList")
	private ClientInfoList clientInfoList;
	
	@XmlElement(namespace = NameSpace.CM, name="MSISDN")
	private String mobileNo;

	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public APDUList getApduList() {
		return apduList;
	}

	public void setApduList(APDUList apduList) {
		this.apduList = apduList;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getProgressPercent() {
		return progressPercent;
	}

	public void setProgressPercent(String progressPercent) {
		this.progressPercent = progressPercent;
	}

	public String getApduName() {
		return apduName;
	}

	public void setApduName(String apduName) {
		this.apduName = apduName;
	}

	public void addApdus(List<String> apdus) {
		this.getApduList().getApdu().addAll(apdus);
	}

	public String getMocamVersion() {
		return mocamVersion;
	}

	public void setMocamVersion(String mocamVersion) {
		this.mocamVersion = mocamVersion;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
	
	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
	
	

}
