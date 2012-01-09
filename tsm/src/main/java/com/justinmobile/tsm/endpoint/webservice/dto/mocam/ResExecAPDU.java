package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecAPDUsCmd", namespace = NameSpace.CM)
public class ResExecAPDU {

	@XmlElement(name = "CommandID", namespace = NameSpace.CM)
	private String commandID;

	@XmlElement(name = "TimeStamp", namespace = NameSpace.CM)
	private String timeStamp;

	@XmlElement(name = "ExecStatus", namespace = NameSpace.CM)
	private Status status;

	@XmlElement(name = "SessionID", namespace = NameSpace.CM)
	private String sessionID;

	@XmlElement(name = "SeqNum", namespace = NameSpace.CM)
	private String seqNum;

	@XmlElement(name = "APDUList", namespace = NameSpace.CM)
	private APDUList apduList = new APDUList();

	@XmlElement(name = "CurrentAppAid", namespace = NameSpace.CM)
	private String currentAppAid;

	@XmlElement(name = "Progress", namespace = NameSpace.CM)
	private String progress;

	@XmlElement(name = "ProgressPercent", namespace = NameSpace.CM)
	private String progressPercent;

	@XmlElement(name = "ApduName", namespace = NameSpace.CM)
	private String apduName;

	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
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

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public APDUList getApduList() {
		return apduList;
	}

	public void setApduList(APDUList apduList) {
		this.apduList = apduList;
	}

	public String getCurrentAppAid() {
		return currentAppAid;
	}

	public void setCurrentAppAid(String currentAppAid) {
		this.currentAppAid = currentAppAid;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getProgressPercent() {
		return progressPercent;
	}

	public void setProgressPercent(String progressPercent) {
		this.progressPercent = progressPercent;
	}

	public String getApduName() {
		return apduName;
	}

	public void setApduName(String apduName) {
		this.apduName = apduName;
	}

	public void addApdus(List<String> apdus) {
		this.getApduList().getApdu().addAll(apdus);
	}
}
