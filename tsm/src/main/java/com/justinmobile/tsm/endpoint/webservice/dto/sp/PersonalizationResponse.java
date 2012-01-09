package com.justinmobile.tsm.endpoint.webservice.dto.sp;

import java.util.List;

import com.justinmobile.tsm.application.domain.Application.PersonalType;
import com.justinmobile.tsm.endpoint.webservice.dto.Personalization;

public interface PersonalizationResponse {

	/**
	 * 获取个人化指令列表
	 * 
	 * @return 个人化指令列表
	 */
	public List<Personalization> getPersonalizations();

	public PersonalType getPersonalType();
}
