package com.justinmobile.tsm.history.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.history.domain.SubscribeHistory;

public interface SubscribeHistoryDao extends EntityDao<SubscribeHistory, Long> {
	/**
	 * 获取最近下载
	 * 
	 * @param isRecently
	 */
	Page<SubscribeHistory> recentlyDownLoad(Page<SubscribeHistory> page, Long aid, boolean isRecently);

	/**
	 * 获取用户卡订购应用版本的历史记录列表，按照subscribeDate降序排序
	 * 
	 * @param customerCard
	 *            用户卡
	 * @param applicationVersion
	 *            应用版本
	 * @return 历史记录列表
	 */
	List<SubscribeHistory> getByCustomerCardAndApplicationVersionOrderBySubscribeDateDesc(CustomerCardInfo customerCard,
			ApplicationVersion applicationVersion);
    
	Page<SubscribeHistory> findPageByMultiQueryParams(Page<SubscribeHistory> page, Map<String, Object> queryParams);
	
	List<String> getCardNoByAppAndDate(Application app,String start,String end);
	Long getCountByAppAndDate(Application app,String start,String end);
	/**
	 * 查询出某段时间内只订购过该应用版本的用户数
	 */
	Long getCountByOnlyAppVerAndDate(ApplicationVersion appVer,String start,String end);
	/**
	 * 查询出某段时间内订购过该应用多个版本的用户而且最近的订购是该版本
	 */
	List<BigDecimal> getCustomerCardInfoByMultiAppVerAndDate(Application app,String start,String end);
	/**
	 * 查询出某段时间内某个用户最后订购的版本
	 */
	Long getAppVerIdByCustomerCardInfoAndDate(Application app,Long customerCardInfoId,String start,String end);

	Page<SubscribeHistory> listHistoryForCustomer(Page<SubscribeHistory> page, Map<String, Object> paramMap);

}