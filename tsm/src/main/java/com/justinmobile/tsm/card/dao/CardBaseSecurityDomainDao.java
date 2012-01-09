package com.justinmobile.tsm.card.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;

public interface CardBaseSecurityDomainDao extends EntityDao<CardBaseSecurityDomain, Long> {

	CardBaseSecurityDomain getByCardBaseAndSd(CardBaseInfo cbi, SecurityDomain sd);
	
	List<CardBaseSecurityDomain> getByCardBase(CardBaseInfo cbi);
	
	List<CardBaseSecurityDomain> getUninstallSdByCardBase(CardBaseInfo cbi);

}