package com.justinmobile.tsm.endpoint.webservice.dto.sp;

import java.util.List;

import com.justinmobile.tsm.application.domain.Application.PersonalType;
import com.justinmobile.tsm.endpoint.webservice.dto.Personalization;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

public class PreOperationResponse implements PersonalizationResponse {

	private String seqNum;

	private String timeStamp;

	private String providerSessionId;

	private Status status;

	private List<Personalization> personalizations;

	private PersonalType personalType;

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

	public String getProviderSessionId() {
		return providerSessionId;
	}

	public void setProviderSessionId(String providerSessionId) {
		this.providerSessionId = providerSessionId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public List<Personalization> getPersonalizations() {
		return personalizations;
	}

	public void setPersonalizations(List<Personalization> personalizations) {
		this.personalizations = personalizations;
	}

	public boolean isSuccess() {
		if (null == status) {
			return false;
		} else {
			return status.isProviderWebServiceSuccess();
		}
	}

	public PersonalType getPersonalType() {
		return personalType;
	}

	public void setPersonalType(PersonalType personalType) {
		this.personalType = personalType;
	}
}
