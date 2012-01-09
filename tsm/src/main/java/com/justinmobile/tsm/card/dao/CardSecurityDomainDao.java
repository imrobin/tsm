package com.justinmobile.tsm.card.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;

public interface CardSecurityDomainDao extends EntityDao<CardSecurityDomain, Long> {

	CardSecurityDomain getByCardNoAid(String cardNo, String aid);

	/**
	 * 根据卡查找卡上应用状态在状态集合中的记录
	 * 
	 * @param card
	 *            卡
	 * @param status
	 *            卡上应用集合
	 * @return 满足条件的记录
	 */
	List<CardSecurityDomain> getByCardThatInStatus(CardInfo card, Set<Integer> status);
	/**
	 * 获取需要计费的CardSecurityDomain
	 */
	List<CardSecurityDomain> getByLastFeeTime(Date end);
	
	List<SecurityDomain> getSdByCardNo(String cardNo);
}