package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommandList", namespace = NameSpace.CM)
public class AppList {

	@XmlElement(name = "Command", namespace = NameSpace.CM)
	private List<AppOperate> appOperate;

	public List<AppOperate> getAppOperate() {
		return appOperate;
	}

	public void setAppOperate(List<AppOperate> appOperate) {
		this.appOperate = appOperate;
	}

}
