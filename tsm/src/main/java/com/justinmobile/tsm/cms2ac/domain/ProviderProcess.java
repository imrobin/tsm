package com.justinmobile.tsm.cms2ac.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "PROVIDER_PROCESS")
public class ProviderProcess {

	private Long id;

	private String seqNum;

	private String sessionId;

	private String timeStamp;

	private Integer commType;

	private Integer cmdType;

	private Integer endFlag;

	private String msisdn;

	private String seId;

	private String appAid;

	private String fileContent;

	private String domainAid;

	private String ssdDapSign;

	private Integer eventId;

	/**
	 * 异步操作是否已经访问<br/>
	 * true-已访问<br/>
	 * false-未访问
	 */
	private Boolean visited = Boolean.FALSE;

	public enum BusinessEvent {
		CANCEL_SERVICE(1), // 退网
		UNSUBSCRIBE_BUSINESS(2), // 退订
		SUBSCRIBE_BUSINESS(3)// 订购
		;

		BusinessEvent(int value) {
			this.value = value;
		}

		int value;

		public int getValue() {
			return this.value;
		}

		public static BusinessEvent valueOf(int value) {
			for (BusinessEvent businessEvent : BusinessEvent.values()) {
				if (value == businessEvent.getValue()) {
					return businessEvent;
				}
			}
			return null;
		}
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_PROVIDER_PROCESS") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Integer getCommType() {
		return commType;
	}

	public void setCommType(Integer commType) {
		this.commType = commType;
	}

	public Integer getCmdType() {
		return cmdType;
	}

	public void setCmdType(Integer cmdType) {
		this.cmdType = cmdType;
	}

	public Integer getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(Integer endFlag) {
		this.endFlag = endFlag;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getSeId() {
		return seId;
	}

	public void setSeId(String seId) {
		this.seId = seId;
	}

	public String getAppAid() {
		return appAid;
	}

	public void setAppAid(String appAid) {
		this.appAid = appAid;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public Boolean getVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}

	public String getDomainAid() {
		return domainAid;
	}

	public void setDomainAid(String domainAid) {
		this.domainAid = domainAid;
	}

	public String getSsdDapSign() {
		return ssdDapSign;
	}

	public void setSsdDapSign(String ssdDapSign) {
		this.ssdDapSign = ssdDapSign;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
}
