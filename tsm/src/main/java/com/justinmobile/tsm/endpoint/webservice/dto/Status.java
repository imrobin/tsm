package com.justinmobile.tsm.endpoint.webservice.dto;

import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Status", namespace = NameSpace.CM)
public class Status {

	@XmlElement(name = "StatusCode", namespace = NameSpace.CM)
	protected String statusCode;

	@XmlElement(name = "StatusDescription", namespace = NameSpace.CM)
	protected String statusDescription;

	public Status() {
		super();
		this.statusCode = PlatformMessage.WEB_SERVICE_SUCCESS.getCode();
		this.statusDescription = PlatformMessage.WEB_SERVICE_SUCCESS.getMessage();
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public boolean isProviderWebServiceSuccess() {
		return PlatformMessage.WEB_SERVICE_SUCCESS.getCode().equals(statusCode);
	}
}
