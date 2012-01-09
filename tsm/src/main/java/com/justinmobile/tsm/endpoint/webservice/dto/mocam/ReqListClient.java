

package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReqListComment", namespace = NameSpace.CM)
public class ReqListClient extends PageRequest {
	
	@XmlElement(namespace = NameSpace.CM)
	private String appAID;
	
	public String getAppAID() {
		return appAID;
	}

	public void setAppAID(String appAID) {
		this.appAID = appAID;
	}

}



