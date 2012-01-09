package com.justinmobile.tsm.endpoint.webservice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DomainKey", namespace = NameSpace.CM)
public class DomainKey {

	@XmlElement(name = "KeyID", namespace = NameSpace.CM)
	private Integer keyId;

	@XmlElement(name = "KeyType", namespace = NameSpace.CM)
	private Integer keyType;

	@XmlElement(name = "KeyValue", namespace = NameSpace.CM)
	private String keyValue;

	@XmlElement(name = "KeyCheck", namespace = NameSpace.CM)
	private String keyCheck;

	public Integer getKeyId() {
		return keyId;
	}

	public void setKeyId(Integer keyId) {
		this.keyId = keyId;
	}

	public Integer getKeyType() {
		return keyType;
	}

	public void setKeyType(Integer keyType) {
		this.keyType = keyType;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getKeyCheck() {
		return keyCheck;
	}

	public void setKeyCheck(String keyCheck) {
		this.keyCheck = keyCheck;
	}
}
