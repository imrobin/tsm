package com.justinmobile.tsm.endpoint.webservice.dto.sp;

import java.util.List;

import com.justinmobile.tsm.application.domain.Application.PersonalType;
import com.justinmobile.tsm.endpoint.webservice.dto.Personalization;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;

public class OperationResultResponse implements PersonalizationResponse {

	public static Integer CONTINUE_OPT_NOT_HAS = 0;

	public static Integer CONTINUE_OPT_HAS = 1;

	/** 交易序号 */
	private String seqNum;

	/** 时间戳 */
	private String timeStamp;

	/** 操作结果 */
	private Status status;

	/** 是否有后续操作 */
	private Integer ifContinueOpt;

	// TODO delete
	private String fileContent;

	/** 个人化指令列表 */
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getIfContinueOpt() {
		return ifContinueOpt;
	}

	public void setIfContinueOpt(Integer ifContinueOpt) {
		this.ifContinueOpt = ifContinueOpt;
	}

	public boolean hasContinueOpt() {
		return CONTINUE_OPT_HAS.intValue() == this.ifContinueOpt;
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

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public PersonalType getPersonalType() {
		return personalType;
	}

	public void setPersonalType(PersonalType personalType) {
		this.personalType = personalType;
	}
}
