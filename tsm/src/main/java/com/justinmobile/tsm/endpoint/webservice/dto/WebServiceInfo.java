package com.justinmobile.tsm.endpoint.webservice.dto;

public class WebServiceInfo {

	public static final String SYSTME_PARAMS_TYPE = "system_config";

	private String url;

	private String serviceName;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public WebServiceInfo() {

	}

	public WebServiceInfo(String url, String serviceName) {
		this.url = url;
		this.serviceName = serviceName;
	}
}
