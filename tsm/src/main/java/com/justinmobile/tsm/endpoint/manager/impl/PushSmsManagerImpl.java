package com.justinmobile.tsm.endpoint.manager.impl;

import org.springframework.stereotype.Service;

import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.endpoint.dao.PushSmsDao;
import com.justinmobile.tsm.endpoint.domain.PushSms;
import com.justinmobile.tsm.endpoint.manager.PushSmsManager;

@Service("pushSmsManager")
public class PushSmsManagerImpl extends EntityManagerImpl<PushSms, PushSmsDao>
		implements PushSmsManager {

}
