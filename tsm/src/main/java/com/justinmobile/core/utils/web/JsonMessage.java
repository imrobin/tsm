package com.justinmobile.core.utils.web;

import com.justinmobile.core.domain.AbstractEntity;

public class JsonMessage {

	private Boolean success;
	
	private Object message;
	
	public JsonMessage() {
		this.success = Boolean.TRUE;
		this.message = "操作成功";
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}
	
	public <T extends AbstractEntity> void setMessage(T message, String excludeField, String includeCascadeField) {
		this.message = message.toMap(excludeField, includeCascadeField);
	}

}
