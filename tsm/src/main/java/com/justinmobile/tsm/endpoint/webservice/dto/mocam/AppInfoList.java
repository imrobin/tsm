package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.manager.ApplicationManager;
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
				info.buildSimple(app, sysType, isUpdatable);
				this.add(info);
			}
		}
	}
	public void addAllFullInfo(List<Application> apps, String sysType, Integer isUpdatable, String cardNo, ApplicationManager applicationManager) {

		if (CollectionUtils.isNotEmpty(apps)) {
			for (Application app : apps) {
				AppInfo info = new AppInfo();
				String status = applicationManager.getLocationMobileStatusForMobile(cardNo, app.getLocation());
				info.build(app, sysType, isUpdatable);
				info.setProvince(status); //添加了号段判定功能，notInMobileSection不在号段内，mobileSection.getProvince() + "," + appLocation表示非本地应用
				this.add(info);
			}
		}
	}
	public void addMyAppInfo(ApplicationVersion applicationVersion, String sysType) {
		if (applicationVersion != null) {
			AppInfo info = new AppInfo();
			info.build(applicationVersion, sysType, null);
			this.add(info);
		}
	}
}
