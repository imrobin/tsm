package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppInfoList", namespace = NameSpace.CM)
public class AppInfoList {
	
	@XmlElement(namespace = NameSpace.CM, name="AppInformation")
	private List<AppInfo> appInfo = new ArrayList<AppInfo>();

	public List<AppInfo> getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(List<AppInfo> appInfo) {
		this.appInfo = appInfo;
	}

	public void add(AppInfo info) {
		this.appInfo.add(info);
	}
	
	public void addAll(List<Application> apps, String sysType, Integer isUpdatable) {
		if (CollectionUtils.isNotEmpty(apps)) {
			for (Application app : apps) {
				AppInfo info = new AppInfo();
				info.build(app, sysType, isUpdatable);
				this.add(info);
			}
		}
	}


}
