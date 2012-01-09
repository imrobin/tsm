package com.justinmobile.tsm.history.manager;

import java.util.Map;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.history.domain.SubscribeHistory;

public interface SubscribeHistoryManager extends EntityManager<SubscribeHistory> {
	/**
	 * 最近用户下载
	 * 
	 * @param isRecently
	 */
	Page<SubscribeHistory> recentlyDownLoad(Page<SubscribeHistory> page, Long appId, boolean isRecently);

	boolean hasSubscribed(Page<SubscribeHistory> pageSub, Long appId, boolean b);

	/**
	 * 获取用户卡最后一次订购应用版本的历史记录
	 * 
	 * @param customerCard
	 *            用户卡
	 * @param applicationVersion
	 *            应用版本
	 * @return 最后一次订购应用版本的历史记录<br/>
	 *         null-如果用户从未订购过该应用版本
	 */
	SubscribeHistory getLastSubscribeHistoryByCustomerCardAndApplicationVersion(CustomerCardInfo customerCard,
			ApplicationVersion applicationVersion);

	Page<SubscribeHistory> findPage(Page<SubscribeHistory> page, Map<String, Object> queryParams) throws PlatformException;

	/**
	 * 添加订购记录
	 * 
	 * @param card
	 *            订购应用的卡
	 * @param applicationVersion
	 *            订购的应用
	 */
	void subscribeApplication(CardInfo card, ApplicationVersion applicationVersion);

	/**
	 * 修改退订记录
	 * 
	 * @param card
	 *            退订应用的卡
	 * @param applicationVersion
	 *            退订的应用
	 */
	void unsubscribeApplication(CardInfo card, ApplicationVersion applicationVersion);

	Page<SubscribeHistory> listHistoryForCustomer(Page<SubscribeHistory> page, Map<String, Object> paramMap);
}