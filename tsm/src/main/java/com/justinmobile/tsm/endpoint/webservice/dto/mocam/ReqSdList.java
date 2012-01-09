package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SDListRequest", namespace = NameSpace.CM)
public class ReqSdList extends PageRequest {
	
	/** 卡号，为空显示所有发布的应用 */
	@XmlElement(namespace = NameSpace.CM, name="SEID")
	private String cardNo;
	
	/** 显示是否是可下载，还是已下载 false-可下载, true-已下载 */
	@XmlElement(namespace = NameSpace.CM, name="IsInstall")
	private Boolean isInstall; 
	
	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Boolean getIsInstall() {
		return isInstall;
	}

	public void setIsInstall(Boolean isInstall) {
		this.isInstall = isInstall;
	}

}
