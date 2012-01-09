package com.justinmobile.tsm.endpoint.webservice.dto.sp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonalizationCommand", namespace = NameSpace.CM)
public class PersonalizationCommand {

	@XmlElement(namespace = NameSpace.CM)
	private String sessionId;

	@XmlElement(namespace = NameSpace.CM)
	private String seqNum;

	@XmlElement(namespace = NameSpace.CM)
	private String timeStamp;

	@XmlElement(namespace = NameSpace.CM)
	private Status status;
	
	@XmlElement(namespace = NameSpace.CM)
	private Integer endflag;
	
	@XmlElement(namespace = NameSpace.CM)
	private String fileContent;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getEndflag() {
		return endflag;
	}

	public void setEndflag(Integer endflag) {
		this.endflag = endflag;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

}
