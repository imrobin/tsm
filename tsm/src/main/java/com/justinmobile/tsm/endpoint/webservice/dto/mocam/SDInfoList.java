package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDInfoList", namespace = NameSpace.CM)
public class SDInfoList {
	
	@XmlElement(namespace = NameSpace.CM, name="SDInfo")
	private List<SDInfo> sdInfo = Lists.newArrayList();

	public List<SDInfo> getSdInfo() {
		return sdInfo;
	}

	public void setSdInfo(List<SDInfo> sdInfo) {
		this.sdInfo = sdInfo;
	}
    public void addAll(List<SecurityDomain> result){
    	if(CollectionUtils.isNotEmpty(result)){
    		for (SecurityDomain sd : result) {
				SDInfo info = new SDInfo();
				SpBaseInfo sp = sd.getSp();
				info.setAppProvider(sp.getName());
				info.setProvince(sp.getLocationNo());
				info.setSdAid(sd.getAid());
				info.setSdName(sd.getSdName());
				Integer isIncludeApp = 0x00;
				info.setIsIncludeApp(isIncludeApp);
				info.setAppStatus(0);
				this.sdInfo.add(info);
			}
    	}
    }
	public void addAll(List<CardSecurityDomain> result, List<CardApplication> applications) {
		if (CollectionUtils.isNotEmpty(result)) {
			for (CardSecurityDomain cardSecurityDomain : result) {
				SDInfo info = new SDInfo();
				SecurityDomain sd = cardSecurityDomain.getSd();
				SpBaseInfo sp = sd.getSp();
				info.setAppProvider(sp.getName());
				switch (cardSecurityDomain.getStatus()) {
				case CardSecurityDomain.STATUS_PERSO:
				case CardSecurityDomain.STATUS_CREATED:
				case CardSecurityDomain.STATUS_KEY_UPDATED:
					info.setAppStatus(1);
					break;
				case CardSecurityDomain.STATUS_LOCK:
					info.setAppStatus(2);
					break;
				default:
					info.setAppStatus(0);
					break;
				}
				info.setProvince(sp.getLocationNo());
				info.setSdAid(sd.getAid());
				info.setSdName(sd.getSdName());
				Integer isIncludeApp = 0x00;
				for (CardApplication cardApplication : applications) {
					SecurityDomain appSd = cardApplication.getApplicationVersion().getApplication().getSd();
					if (appSd.equals(sd)) {
						isIncludeApp = 0x01;
						break;
					}
				}
				info.setIsIncludeApp(isIncludeApp);
				this.sdInfo.add(info);
			}
		}
	}

}
