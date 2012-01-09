package com.justinmobile.tsm.card.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardBaseSecurityDomainDao;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;

@Repository("cardBaseSecurityDomainDao")
public class CardBaseSecurityDomainDaoHibernate extends EntityDaoHibernate<CardBaseSecurityDomain, Long> implements CardBaseSecurityDomainDao {

	@Override
	public CardBaseSecurityDomain getByCardBaseAndSd(CardBaseInfo cbi, SecurityDomain sd) {
		String hql = "from " + CardBaseSecurityDomain.class.getName() + " as cbsd where cbsd.cardBaseInfo = ? and cbsd.securityDomain = ?";
		return findUniqueEntity(hql, cbi,sd);
	}
    
	@Override
	public List<CardBaseSecurityDomain> getByCardBase(CardBaseInfo cbi) {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select cbsd from ").append(CardBaseSecurityDomain.class.getName()).append(" as cbsd where cbsd.cardBaseInfo = :cardBase");
		hql.append(" and cbsd.preset=").append(CardBaseSecurityDomain.PRESET);
		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("cardBase",cbi);
		return find(hql.toString(),values);
	}
    
	@Override
	public List<CardBaseSecurityDomain> getUninstallSdByCardBase(CardBaseInfo cbi) {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select cbsd from ").append(CardBaseSecurityDomain.class.getName()).append(" as cbsd where cbsd.cardBaseInfo = :cardBase");
		hql.append(" and cbsd.preset=").append(CardBaseSecurityDomain.UNPRESET);
		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("cardBase",cbi);
		return find(hql.toString(),values);
	}

}