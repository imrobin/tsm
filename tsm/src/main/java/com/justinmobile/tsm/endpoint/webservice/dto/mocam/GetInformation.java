package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetInformationResponse", namespace = NameSpace.CM)
public class GetInformation extends BasicResponse {
	
	@XmlElement(namespace = NameSpace.CM, name="CommentList")
	private AppCommentList appCommentList;
	
	@XmlElement(namespace = NameSpace.CM, name="AppInformation")
	private AppInfo appInfo;
	
	@XmlElement(namespace = NameSpace.CM, name="ClientInfoList")
	private ClientInfoList clientInfoList; 

	@XmlElement(namespace = NameSpace.CM, name="MemoryInfo")
	private MemoryInfo memoryInfo;
	
	@XmlElement(namespace = NameSpace.CM, name="NextPageNumber")
	private Integer nextPageNumber;
	
	@XmlElement(namespace = NameSpace.CM, name="TotalPages")
	private Integer totalPages;

	public AppCommentList getAppCommentList() {
		return appCommentList;
	}

	public void setAppCommentList(AppCommentList appCommentList) {
		this.appCommentList = appCommentList;
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo appInfo) {
		this.appInfo = appInfo;
	}

	public ClientInfoList getClientInfoList() {
		return clientInfoList;
	}

	public void setClientInfoList(ClientInfoList clientInfoList) {
		this.clientInfoList = clientInfoList;
	}

	public MemoryInfo getMemoryInfo() {
		return memoryInfo;
	}

	public void setMemoryInfo(MemoryInfo memoryInfo) {
		this.memoryInfo = memoryInfo;
	}

	public Integer getNextPageNumber() {
		return nextPageNumber;
	}

	public void setNextPageNumber(Integer nextPageNumber) {
		this.nextPageNumber = nextPageNumber;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

}
