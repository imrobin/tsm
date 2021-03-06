package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationImage;
import com.justinmobile.tsm.application.domain.ApplicationStyle;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.GradeStatistics;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.utils.SystemConfigUtils;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppInfo", namespace = NameSpace.CM)
public class AppInfo {
	
	@XmlElement(namespace = NameSpace.CM,name = "AppAID")
	private String appAid;
    
	@XmlElement(namespace = NameSpace.CM,name = "AppType")
	private Integer appType;
	
	@XmlElement(namespace = NameSpace.CM)
	private String appName;

	@XmlElement(namespace = NameSpace.CM,name = "AppVersion")
	private String appVersion;
    
	@XmlElement(namespace = NameSpace.CM,name = "AppDesc")
	private String appDesc;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppProvider")
	private String appProvider;
	
	@XmlElement(namespace = NameSpace.CM,name = "Province")
	private String province;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppDownloadCount")
	private Integer appDownloadCount;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppStatus")
	private Integer appStatus;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppIssuingDate")
	private String appIssuingDate;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppCharge")
	private String appCharge;
	
	@XmlElement(namespace = NameSpace.CM)
	private Long appNvm;

	@XmlElement(namespace = NameSpace.CM)
	private Integer appRam;
	
	@XmlElement(namespace = NameSpace.CM)
	private Integer commentTotalCount;
	
	@XmlElement(namespace = NameSpace.CM)
	private Integer countGrade;
	
	@XmlElement(namespace = NameSpace.CM)
	private Integer isUpdatable;
	
	@XmlElement(namespace = NameSpace.CM,name = "ClientID")
	private String clientId;
	
	@XmlElement(namespace = NameSpace.CM,name = "ClientSize")
	private String clientSize;
	
	public String getClientSize() {
		return clientSize;
	}

	public void setClientSize(String clientSize) {
		this.clientSize = clientSize;
	}
	@XmlElement(namespace = NameSpace.CM)
	private String remark;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppClassify")
	private String appClassify;
	
	@XmlElement(namespace = NameSpace.CM,name = "SmallCardURL")
	private String smallCardUrl;
    
	@XmlElement(namespace = NameSpace.CM,name = "PictureURLList")
	private PictureURLList pictureUrlList;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppLogoURL")
	private String appLogoUrl;
	
	@XmlElement(namespace = NameSpace.CM,name = "AppCardStyle")
	private String appCardStyle;
	
	public String getAppAid() {
		return appAid;
	}

	public void setAppAid(String appAid) {
		this.appAid = appAid;
	}
    
	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public Long getAppNvm() {
		return appNvm;
	}

	public void setAppNvm(Long appNvm) {
		this.appNvm = appNvm;
	}

	public Integer getAppRam() {
		return appRam;
	}

	public void setAppRam(Integer appRam) {
		this.appRam = appRam;
	}

	public Integer getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Integer appStatus) {
		this.appStatus = appStatus;
	}

	public String getAppIssuingDate() {
		return appIssuingDate;
	}

	public void setAppIssuingDate(String appIssuingDate) {
		this.appIssuingDate = appIssuingDate;
	}

	public Integer getAppDownloadCount() {
		return appDownloadCount;
	}

	public void setAppDownloadCount(Integer appDownloadCount) {
		this.appDownloadCount = appDownloadCount;
	}

	public String getAppCharge() {
		return appCharge;
	}

	public void setAppCharge(String appCharge) {
		this.appCharge = appCharge;
	}

	public Integer getCommentTotalCount() {
		return commentTotalCount;
	}

	public void setCommentTotalCount(Integer commentTotalCount) {
		this.commentTotalCount = commentTotalCount;
	}

	public Integer getCountGrade() {
		return countGrade;
	}

	public void setCountGrade(Integer countGrade) {
		this.countGrade = countGrade;
	}

	public String getClientId() {
		return clientId;
	}
	
	public Integer getIsUpdatable() {
		return isUpdatable;
	}

	public void setIsUpdatable(Integer isUpdatable) {
		this.isUpdatable = isUpdatable;
	}

	public String getAppDesc() {
		return appDesc;
	}

	
	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}

	
	public String getAppProvider() {
		return appProvider;
	}

	
	public void setAppProvider(String appProvider) {
		this.appProvider = appProvider;
	}

	
	public String getProvince() {
		return province;
	}

	
	public void setProvince(String province) {
		this.province = province;
	}

	
	public String getRemark() {
		return remark;
	}

	
	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	public String getAppClassify() {
		return appClassify;
	}

	
	public void setAppClassify(String appClassify) {
		this.appClassify = appClassify;
	}

	
	public String getSmallCardURL() {
		return smallCardUrl;
	}

	
	public void setSmallCardURL(String smallCardUrl) {
		this.smallCardUrl = smallCardUrl;
	}

	public PictureURLList getPictureUrlList() {
		return pictureUrlList;
	}

	public void setPictureURLList(PictureURLList pictureUrlList) {
		this.pictureUrlList = pictureUrlList;
	}
	
	public String getAppLogoUrl() {
		return appLogoUrl;
	}

	public void setAppLogoURL(String appLogoUrl) {
		this.appLogoUrl = appLogoUrl;
	}

	public String getAppCardStyle() {
		return appCardStyle;
	}

	
	public void setAppCardStyle(String appCardStyle) {
		this.appCardStyle = appCardStyle;
	}

	
	public void setClientID(String clientId) {
		this.clientId = clientId;
	}
    public void buildSimple(Application app,String sysType,Integer isUpdatable){
    	if(app.getLastestAppVersion()!=null){
			this.setAppVersion(app.getLastestVersion());
		}
    	this.buildSimpleInfo(app, sysType, isUpdatable);
    }
	public void build(Application app, String sysType, Integer isUpdatable) {
		this.buildInfo(app, sysType, isUpdatable);
		if(app.getLastestAppVersion()!=null){
			this.setAppVersion(app.getLastestVersion());
		}
	}

	public void build(ApplicationVersion av, String sysType, Integer isUpdatable) {
		this.buildInfo(av.getApplication(), sysType, isUpdatable);
		if(av.getVersionNo()!=null){
		this.setAppVersion(av.getVersionNo());
		}
		if(av.getNonVolatileSpace() != null){
			this.setAppNvm(av.getNonVolatileSpace());
		}
		if(av.getVolatileSpace() != null){
			this.setAppRam(av.getVolatileSpace());
		}
	}
	public void buildSimple(ApplicationVersion av,String sysType,Integer isUpdatable){
		this.buildSimpleInfo(av.getApplication(), sysType, isUpdatable);
		if(av.getVersionNo()!=null){
			this.setAppVersion(av.getVersionNo());
		}
	}
	private void buildSimpleInfo(Application app,String sysType,Integer isUpdatable){
		this.setAppAid(app.getAid());
		this.setAppName(app.getName());
		Space appSpace = app.getLastestSpace();
		this.setAppNvm(appSpace.getNvm());
		this.setAppRam(appSpace.getRam());
		this.setAppCharge("0");
		GradeStatistics statistics = app.getStatistics();
		if (statistics == null) {
			this.setCountGrade(0);
		} else {
			this.setCountGrade(statistics.getAvgNumber());
		}
		this.setIsUpdatable(isUpdatable);
		ApplicationVersion appVersion = app.getLastestAppVersion();
		if (appVersion != null) {
			ApplicationClientInfo client = appVersion.getClient(sysType);
			if (client != null) {
				this.setClientID(String.valueOf(client.getId()));
				this.setClientSize(String.valueOf(client.getSize()));
			}
		}
		//目前只有一种应用样式
		Iterator<ApplicationStyle> it = app.getApplicationStyle().iterator(); 
		if(it.hasNext()){
			 ApplicationStyle as = it.next(); 
			 this.setAppCardStyle(as.getStyleUrl());
		}else{
			this.setAppCardStyle("");
		}
		this.setAppClassify(app.getChildType().getName());
		this.setAppLogoURL(SystemConfigUtils.getServiceUrl()+"html/application/?m=getAppPcImg&appId="+app.getId());
		this.setSmallCardURL(SystemConfigUtils.getServiceUrl()+"html/application/?m=getAppMobileImgByAid&aId="+app.getAid());
		this.setAppDesc(app.getDescription());
	}
	private void buildInfo(Application app, String sysType, Integer isUpdatable){
		this.buildSimpleInfo(app, sysType, isUpdatable);
		this.setRemark(app.getDescription());
		this.setAppType(app.getForm());
		this.setAppProvider(app.getSp().getName());
		this.setProvince(app.getLocation());
		this.setAppDownloadCount(app.getDownloadCount());
		this.setAppStatus(app.getStatus());
		if (app.getPublishDate() != null) {
			this.setAppIssuingDate(CalendarUtils.parsefomatCalendar(app.getPublishDate(), CalendarUtils.SHORT_FORMAT_LINE));
		}
		this.setCommentTotalCount(app.getComments().size());
		this.setSmallCardURL(SystemConfigUtils.getServiceUrl()+"html/application/?m=getAppMobileImgByAid&aId="+app.getAid());
		pictureUrlList = new PictureURLList();
		for(ApplicationImage appi:app.getApplicationImages()){
			pictureUrlList.add(SystemConfigUtils.getServiceUrl()+"html/application/?m=getAppImg&appImgId="+appi.getId());
		}
		this.setPictureURLList(pictureUrlList);
		StringBuilder sb  = new StringBuilder();
		for(ApplicationStyle as:app.getApplicationStyle()){
			sb.append(as.getStyleUrl()).append(",");
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		this.setAppCardStyle(sb.toString());
	}
}
