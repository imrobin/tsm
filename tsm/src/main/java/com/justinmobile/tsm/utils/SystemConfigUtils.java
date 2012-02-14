package com.justinmobile.tsm.utils;

import java.util.ResourceBundle;

public class SystemConfigUtils {

	private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("config/systemConfig");

	public static boolean isTestRuntimeEnvironment() {
		return Boolean.parseBoolean(resourceBundle.getString("runtimeEnvironment.isTest"));
	}

	public static boolean isCms2acRuntimeEnvironment() {
		return Boolean.parseBoolean(resourceBundle.getString("runtimeEnvironment.isCms2ac"));
	}

	public static String getTsmUrl() {
		return resourceBundle.getString("tsm.url");
	}

	public static String getServiceName() {
		return resourceBundle.getString("tsm.serviceName");
	}

	public static boolean useMockBusinessPlatform() {
		return Boolean.parseBoolean(resourceBundle.getString("runtimeEnvironment.useMockBusinessPlatform"));
	}

	public static String getMockBusinessPlatformUrl() {
		return resourceBundle.getString("mockBusinessPlatform.url");
	}

	public static String getMockBusinessPlatformServiceName() {
		return resourceBundle.getString("mockBusinessPlatform.serviceName");
	}

	public static boolean isEncryptApplicationKey() {
		return Boolean.parseBoolean(resourceBundle.getString("application.isEncryptKey"));
	}

	public static boolean isPersoSecurityEnable() {
		return Boolean.parseBoolean(resourceBundle.getString("application.isPersoSecurityEnable"));
	}

	public static String getServiceUrl() {
		return resourceBundle.getString("tsm.service.url");
	}

	public static String getMockIsdAppAid() {
		return resourceBundle.getString("mock.isd.appAid");
	}

	public static String getMockIsdModuleAid() {
		return resourceBundle.getString("mock.isd.moduleAid");
	}

	public static String getMockIsdFileAid() {
		return resourceBundle.getString("mock.isd.fileAid");
	}
	
	public static String getPushSrcPort(){
		return resourceBundle.getString("push.srcPort");
	}
	
	public static String getPushDestPort(){
		return resourceBundle.getString("push.destPort");
	}
}
