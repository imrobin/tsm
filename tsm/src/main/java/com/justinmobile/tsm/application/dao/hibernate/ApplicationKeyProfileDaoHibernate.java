package com.justinmobile.tsm.application.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.ApplicationKeyProfileDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.dto.KeyInfo;

@Repository("applicationKeyProfileDao")
public class ApplicationKeyProfileDaoHibernate extends EntityDaoHibernate<ApplicationKeyProfile, Long> implements ApplicationKeyProfileDao {

	@Override
	public ApplicationKeyProfile getByApplictionAndKeyInfo(Application application, KeyInfo keyInfo) {
		String hql = "from "
				+ ApplicationKeyProfile.class.getName()
				+ " as akp where akp.application = :application and akp.keyId = :keyId and keyVersion = :keyVersion and keyIndex = :keyIndex and keyType = :keyType";

		Map<String, Object> values = new HashMap<String, Object>(5);
		values.put("application", application);
		values.put("keyId", keyInfo.getKeyId());
		values.put("keyVersion", keyInfo.getKeyVersion());
		values.put("keyIndex", keyInfo.getKeyIndex());
		values.put("keyType", keyInfo.getKeyType());

		return findUnique(hql, values);
	}
}