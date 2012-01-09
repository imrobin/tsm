package com.justinmobile.tsm.endpoint.webservice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Personalization", namespace = NameSpace.CM)
public class Personalization {

	@XmlElement(name = "AppAid", namespace = NameSpace.CM)
	private String aid;

	@XmlElement(name = "FileContent", namespace = NameSpace.CM)
	private String fileContent;

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public com.justinmobile.tsm.transaction.domain.Personalization convertEntity() {
		com.justinmobile.tsm.transaction.domain.Personalization personalization = new com.justinmobile.tsm.transaction.domain.Personalization();
		personalization.setAid(aid);
		personalization.setFileContent(fileContent);
		return personalization;
	}
}
