package com.justinmobile.tsm.cms2ac.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.cms2ac.dao.HsmkeyConfigDao;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

@Repository("hsmkeyConfigDao")
public class HsmkeyConfigDaoHibernate extends EntityDaoHibernate<HsmkeyConfig, Long> implements HsmkeyConfigDao {

	@Override
	public void remove(Long id) {
		Session session = this.getSession();
		String delete = "delete from APPLICATION_KEY_PROFILE_HSMKEY where HSMKEY_CONFIG_ID = " + id;
		SQLQuery sqlQuery = session.createSQLQuery(delete);
		int amount = sqlQuery.executeUpdate();
		this.logger.debug("\nAPPLICATION_KEY_PROFILE_HSMKEY : " + "[" + amount + "]\n" + delete);

		delete = "delete from KEY_PROFILE_HSMKEY where HSMKEY_CONFIG_ID = " + id;
		amount = session.createSQLQuery(delete).executeUpdate();
		this.logger.debug("\nKEY_PROFILE_HSMKEY : " + "[" + amount + "]\n" + delete);

		delete = "delete from KEY_PROFILE_HSMKEY_APPLY where HSMKEY_CONFIG_ID = " + id;
		sqlQuery = session.createSQLQuery(delete);
		amount = sqlQuery.executeUpdate();
		this.logger.debug("\nKEY_PROFILE_HSMKEY_APPLY : " + "[" + amount + "]\n" + delete);

		delete = "delete from HSMKEY_CONFIG where ID = " + id;
		amount = session.createSQLQuery(delete).executeUpdate();
		this.logger.debug("\nHSMKEY_CONFIG : " + "[" + amount + "]\n" + delete);
	}

	@Override
	public HsmkeyConfig getByKeyProfileVendor(ApplicationKeyProfile keyProfile, String vendorName) {
		String hql = "select hsmConf from " + HsmkeyConfig.class.getName()
				+ " as hsmConf left join hsmConf.applicationKeyProfiles as akp where hsmConf.vendor = :vendorName and akp = :keyProfile";

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("vendorName", vendorName);
		values.put("keyProfile", keyProfile);

		return findUnique(hql, values);
	}

	@Override
	public HsmkeyConfig getByKeyProfileVendor(KeyProfile keyProfile, String vendorName) {
		String hql = "select hsmConf from " + HsmkeyConfig.class.getName()
				+ " as hsmConf left join hsmConf.keyProfiles as kp where hsmConf.vendor = :vendorName and kp = :keyProfile";

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("vendorName", vendorName);
		values.put("keyProfile", keyProfile);

		return findUnique(hql, values);
	}
}