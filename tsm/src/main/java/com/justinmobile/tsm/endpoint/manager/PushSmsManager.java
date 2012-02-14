package com.justinmobile.tsm.endpoint.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.endpoint.domain.PushSms;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Transactional
public interface PushSmsManager extends EntityManager<PushSms> {

	PushSms getByPushSerial(String pushSerial);
	
    void sendPushSms(String cardNo,String aid,String version,Operation operation);
}
