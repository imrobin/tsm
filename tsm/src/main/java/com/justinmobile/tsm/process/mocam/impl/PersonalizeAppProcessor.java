package com.justinmobile.tsm.process.mocam.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.Application.PersonalType;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Service("personalizeAppProcessor")
public class PersonalizeAppProcessor extends PublicOperationProcessor implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	protected MocamResult processTrans(LocalTransaction localTransaction) {
		return null;
	}

	@Override
	public MocamResult process(LocalTransaction localTransaction, ReqExecAPDU reqExecAPDU) {

		MocamResult result = null;

		PersonalType personalType = localTransaction.getPersonalType();
		if (null == personalType) {// 如果个人化类型未指定，使用应用配置的个人化类型作为默认值
			Application application = applicationManager.getByAid(localTransaction.getAid());
			personalType = Application.PersonalType.valueOf(application.getPersonalType());
		}

		if (PersonalType.NOT_NECESSARY == personalType) {
			throw new PlatformException(PlatformErrorCode.TRANS_PERSO_APP_NOT_NECESSARY);
		}

		MocamProcessor processor = (MocamProcessor) applicationContext.getBean(personalType.getBeanName());
		result = processor.process(localTransaction, reqExecAPDU);

		return result;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void check(LocalTransaction localTransaction) {
	}
}
