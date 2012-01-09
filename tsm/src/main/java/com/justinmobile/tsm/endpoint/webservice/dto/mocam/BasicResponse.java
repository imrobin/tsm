package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BasicResponse", namespace = NameSpace.CM)
public class BasicResponse {
	
	@XmlElement(namespace = NameSpace.CM, name="CommandID")
	private String commandID;
	
	@XmlElement(namespace = NameSpace.CM, name="ExecStatus")
	private Status status;
	
	@XmlElement(name = "SessionID", namespace = NameSpace.CM)
	private String sessionID;

	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

}
