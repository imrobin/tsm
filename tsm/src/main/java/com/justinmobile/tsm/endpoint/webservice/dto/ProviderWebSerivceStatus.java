package com.justinmobile.tsm.endpoint.webservice.dto;

import com.justinmobile.core.message.PlatformMessage;

public class ProviderWebSerivceStatus extends Status {

	protected String statusCode;

	protected String statusDescription;

	public ProviderWebSerivceStatus() {
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

	public boolean isSuccess() {
		return PlatformMessage.WEB_SERVICE_SUCCESS.getCode().equals(statusCode);
	}
}
