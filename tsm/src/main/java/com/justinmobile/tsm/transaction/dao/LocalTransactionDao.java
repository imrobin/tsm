package com.justinmobile.tsm.transaction.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyLongValue;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

public interface LocalTransactionDao extends EntityDao<LocalTransaction, Long> {

	void changeStatus(String ids, String targetStatus);

	Page<LocalTransaction> findTransactionByUser(Page<LocalTransaction> page, Map<String, String> paramMap);

	/**
	 * 查找指定卡号处于“执行中”状态的trans
	 * 
	 * @param cardNo
	 *            指定的卡号
	 * @return
	 */
	List<LocalTransaction> getRunningTransByCardNo(String cardNo);
	
	List<KeyLongValue> getTransByAidAndVersion(String aid,String version,String start,String end);
	
	List<String> getCreateSD(String aid,String start,String end);

	Page<LocalTransaction> findCreateSdLocalTransactionForMobile(Page<LocalTransaction> page, Map<String, Object> paramMap);
}