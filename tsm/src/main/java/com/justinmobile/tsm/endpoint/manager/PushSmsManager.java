package com.justinmobile.tsm.endpoint.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.endpoint.domain.PushSms;

@Transactional
public interface PushSmsManager extends EntityManager<PushSms> {

	PushSms getByPushSerial(String pushSerial);

}
