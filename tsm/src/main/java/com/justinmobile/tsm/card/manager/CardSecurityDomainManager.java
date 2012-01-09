package com.justinmobile.tsm.card.manager;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

public interface CardSecurityDomainManager extends EntityManager<CardSecurityDomain> {

	CardSecurityDomain getByCardNoAid(String cardNo, String aid);

	CardSecurityDomain getbySdAndCard(CardInfo card, SecurityDomain securityDomain);

	CardSecurityDomain getISdByCci(CustomerCardInfo cci);

	/**
	 * 获取卡上所有安全域
	 * 
	 * @param card
	 *            卡
	 * @return 卡上安全域列表
	 */
	List<CardSecurityDomain> getByCard(CardInfo card);

	/**
	 * 删除安全域时对安全域下的应用做判断
	 * 
	 * @param resultMap
	 * @param cardNo
	 * @param sdId
	 */
	void checkAndGetDelAppForDelSd(Map<String, Object> resultMap, String cardNo, String sdId);

	void checkDel(String cardNo, String sdId);

	/**
	 * 根据卡号查找卡上所有安全域
	 * 
	 * @param cardNo
	 *            卡号
	 * @return 卡上所有安全域
	 */
	List<CardSecurityDomain> getByCardThatOnCard(String cardNo);

	/**
	 * 检查卡上安全域能否删除
	 * 
	 * @param card
	 *            卡
	 * @param securityDomain
	 *            安全域
	 * @throws ISD_CAN_NOT_DELETE
	 *             如果是主安全域
	 * @throws SD_NOT_DEL_BY_RULE
	 *             如果安全域的删除规则是“不能删除”
	 * @throws SD_IS_PRSET_FOR_NOT_DEL
	 *             如果安全域是预置的
	 */
	void checkDeletable(CardInfo card, SecurityDomain securityDomain);
	
	List<SecurityDomain> getSdByCardNo(String cardNo);
}