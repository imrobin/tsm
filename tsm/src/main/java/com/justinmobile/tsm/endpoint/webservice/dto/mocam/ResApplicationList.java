package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApplicationListResponse", namespace = NameSpace.CM)
public class ResApplicationList {
	
	@XmlElement(namespace = NameSpace.CM, name="CommandID")
	private String commandID;
	
	@XmlElement(namespace = NameSpace.CM, name="ExecStatus")
	private Status status;
	
	@XmlElement(name = "SessionID", namespace = NameSpace.CM)
	private String sessionID;

	@XmlElement(namespace = NameSpace.CM, name="PageNumber")
	private Integer nextPageNumber;
	
	@XmlElement(namespace = NameSpace.CM, name="TotalPages")
	private Integer totalPage;
	
	@XmlElement(namespace = NameSpace.CM, name="AppInfoList")
	private AppInfoList appInfoList;

	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getNextPageNumber() {
		return nextPageNumber;
	}

	public void setNextPageNumber(Integer nextPageNumber) {
		this.nextPageNumber = nextPageNumber;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public AppInfoList getAppInfoList() {
		return appInfoList;
	}

	public void setAppInfoList(AppInfoList appInfoList) {
		this.appInfoList = appInfoList;
	}
	
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

}
