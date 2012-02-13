package com.justinmobile.tsm.endpoint.dao;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.endpoint.domain.PushSms;

public interface PushSmsDao extends EntityDao<PushSms, Long> {

	PushSms getByPushSerial(String pushSerial);
}
