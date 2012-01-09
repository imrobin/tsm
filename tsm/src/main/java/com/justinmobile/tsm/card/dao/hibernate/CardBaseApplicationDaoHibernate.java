package com.justinmobile.tsm.card.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardBaseApplicationDao;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Repository("cardBaseApplicationDao")
public class CardBaseApplicationDaoHibernate extends EntityDaoHibernate<CardBaseApplication, Long> implements CardBaseApplicationDao {

	@Override
	public List<CardBaseApplication> getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(CardBaseInfo cardBaseInfo, Application app) {
		String hql = "from "
				+ CardBaseApplication.class.getName()
				+ " as cba where cba.cardBase=:cardBase and cba.applicationVersion.application=:application and cba.applicationVersion.status=:status order by cba.applicationVersion.versionNo desc";

		Map<String, Object> vaules = new HashMap<String, Object>(3);
		vaules.put("cardBase", cardBaseInfo);
		vaules.put("application", app);
		vaules.put("status", ApplicationVersion.STATUS_PULISHED);

		return find(hql, vaules);
	}

	@Override
	public CardBaseApplication getByCardBaseAndAppver(CardBaseInfo cbi, ApplicationVersion appver) {
		String hql = "from " + CardBaseApplication.class.getName() + " as cba where cba.cardBase = ? and cba.applicationVersion=?";
		return findUniqueEntity(hql, cbi, appver);
	}
 
	@Override
	public List<CardBaseApplication> getByCardBase(CardBaseInfo cbi) {
		StringBuilder hql = new StringBuilder();
		hql.append("select cba from ").append(CardBaseApplication.class.getName()).append(" as cba where (cba.presetMode=2 or cba.presetMode=3)");
		hql.append(" and cba.cardBase.id = ?");
		hql.append(" and cba.applicationVersion.status =").append(ApplicationVersion.STATUS_PULISHED);
		return find(hql.toString(),cbi.getId());
	}

	@Override
	public CardBaseApplication getByApplicationAndCardbaseAndPresetMode(Application application, CardBaseInfo cbi) {
		String hql = "from " + CardBaseApplication.class.getName() + " as cba where cba.cardBase = ? and cba.applicationVersion.application = ? and (cba.presetMode = ? or cba.presetMode = ?)";
		return findUniqueEntity(hql,cbi,application,CardBaseApplication.MODE_CREATE,CardBaseApplication.MODE_PERSONAL);
	}

	@Override
	public List<CardBaseApplication> getBySDandCBIAndPreset(SecurityDomain securityDomain, CardBaseInfo cbi) {
		String hql = "from " + CardBaseApplication.class.getName() + " as cba where cba.cardBase = ? and cba.applicationVersion.application.sd = ? and (cba.presetMode = ? or cba.presetMode = ?)";
		return find(hql,cbi,securityDomain,CardBaseApplication.MODE_CREATE,CardBaseApplication.MODE_PERSONAL);
	}
}