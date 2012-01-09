package com.justinmobile.tsm.card.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

public interface CardApplicationDao extends EntityDao<CardApplication, Long> {

	/**
	 * @Title: listRevertApps
	 * @Description:
	 * @param cci
	 * @return
	 */
	List<CardApplication> listRevertApps(CustomerCardInfo cci);

	CardApplication getByCardNoAid(String cardNo, String aid);

	List<CardApplication> getCardAppBySd(long sdId);

	/**
	 * 查找卡上指定安全域的应用列表
	 * 
	 * @param card
	 *            卡
	 * @param sd
	 *            指定安全域
	 * @return 应用列表
	 */
	List<CardApplication> getByCardAndApplicationSd(CardInfo card, SecurityDomain sd);

	CardApplication getByCardAndApplicationAndUndownload(CardInfo card, ApplicationVersion applicationVersion);

	CardApplication getByCardAndAppver(CardInfo card, ApplicationVersion applicationVersion);

	List<CardApplication> findAvailableList(CardInfo card);

	List<CardApplication> findDownloadList(CardInfo card);

	Long getAppVerSize(ApplicationVersion appVer);

	Page<CardApplication> findPageByCustomer(Page<CardApplication> page, Map<String, Object> queryParams);

	List<Map<String, Object>> findByCustomer(Page<Map<String, Object>> page, String mobileNo);

	List<CardApplication> getCaListNotDelAndNotMigratable(CardInfo card);

	List<CardApplication> getCaListMigratable(CardInfo card);

	CardApplication getAvailbleOrLockedByCardNoAid(String cardNo, String aid);

	List<CardApplication> getByLastFeeTime(Date end);

	List<CardApplication> getCardApplicationByUserAndAppId(CardInfo cardInfo, Application app);

	/**
	 * 根据卡获取指定状态的记录
	 * 
	 * @param card
	 *            卡
	 * @param status
	 *            指定状态
	 * @return 查询结果
	 */
	List<CardApplication> getByCardAndStatus(CardInfo card, int status);

	/**
	 * 查询状态为7.8的指定CARD的CARDAPPLICATION,给挂失功能查询
	 * @param card
	 * @return
	 */
	List<CardApplication> getForLostListByCardInfo(CardInfo card);
}