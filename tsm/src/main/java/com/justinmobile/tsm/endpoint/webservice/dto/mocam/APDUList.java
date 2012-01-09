package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "APDUList", namespace = NameSpace.CM)
public class APDUList {

	@XmlElement(name = "APDU", namespace = NameSpace.CM)
	private List<String> apdu = Lists.newArrayList();

	public List<String> getApdu() {
		return apdu;
	}

	public void setApdu(List<String> apdu) {
		this.apdu = apdu;
	}

}
