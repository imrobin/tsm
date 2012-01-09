package com.justinmobile.tsm.card.dao.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.card.dao.CardSecurityDomainDao;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Repository("cardSecurityDomainDao")
public class CardSecurityDomainDaoHibernate extends EntityDaoHibernate<CardSecurityDomain, Long> implements CardSecurityDomainDao {

	@Override
	public CardSecurityDomain getByCardNoAid(String cardNo, String aid) {
		String hql = " from " + CardSecurityDomain.class.getName() + " as ca where ca.card.cardNo = ? and ca.sd.aid = ?";
		return findUniqueEntity(hql, cardNo, aid);
	}

	@Override
	public List<CardSecurityDomain> getByCardThatInStatus(CardInfo card, Set<Integer> statusSet) {
		StringBuilder hql = new StringBuilder(" from " + CardSecurityDomain.class.getName() + " as ca where ca.card = ? and ca.status in (");
		for (Integer status : statusSet) {
			hql.append(status).append(',');
		}
		hql.deleteCharAt(hql.length() - 1);
		hql.append(")");

		return find(hql.toString(), card);
	}
  
	@Override
	public List<CardSecurityDomain> getByLastFeeTime(Date end) {
		StringBuilder hql = new StringBuilder();
		Map<String,Object> values = new HashMap<String,Object>();
		values.put("end",end);
		hql.append("select csd from ").append(CardSecurityDomain.class.getName()).append(" as csd,");
		hql.append(CustomerCardInfo.class.getName()).append(" as cci");
		hql.append(" where cci.card=csd.card and ( cci.status<>").append(CustomerCardInfo.STATUS_CANCEL);
		hql.append(" or cci.status<>").append(CustomerCardInfo.STATUS_REPLACING).append(" )");
		hql.append(" and csd.lastFeeTime<:end");
		return find(hql.toString(),values);
		
	}
     
	@Override
	public List<SecurityDomain> getSdByCardNo(String cardNo) {
		StringBuilder hql = new StringBuilder();
		Map<String,Object> values = new HashMap<String,Object>();
		hql.append("select csd.sd from ").append(CardSecurityDomain.class.getName()).append(" as csd ");
		hql.append(" where csd.card.cardNo=:cardNo");
		hql.append(" and csd.status<>").append(CardSecurityDomain.STATUS_UNCREATE);
		values.put("cardNo", cardNo);
		return find(hql.toString(),values);
	}
}