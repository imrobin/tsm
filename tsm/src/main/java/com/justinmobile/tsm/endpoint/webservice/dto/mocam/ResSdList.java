package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDListResponse", namespace = NameSpace.CM)
public class ResSdList {
	
	@XmlElement(namespace = NameSpace.CM, name="CommandID")
	private String commandID;
	
	@XmlElement(namespace = NameSpace.CM, name="ExecStatus")
	private Status status;
	
	@XmlElement(namespace = NameSpace.CM, name="PageNumber")
	private Integer nextPageNumber;
	
	@XmlElement(namespace = NameSpace.CM, name="TotalPages")
	private Integer totalPage;
	
	@XmlElement(namespace = NameSpace.CM, name="SDInfoList")
	private SDInfoList sdInfoList;

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

	public SDInfoList getSdInfoList() {
		return sdInfoList;
	}

	public void setSdInfoList(SDInfoList sdInfoList) {
		this.sdInfoList = sdInfoList;
	}

}