package com.justinmobile.tsm.transaction.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Transactional
public interface LocalTransactionManager extends EntityManager<LocalTransaction>{

	void changeStatus(String ids, String targetStatus) throws PlatformException;

	Page<LocalTransaction> findTransactionByUser(Page<LocalTransaction> page, Map<String, String> paramMap) throws PlatformException;

	Page<LocalTransaction> findPage(Page<LocalTransaction> page, String orderBy, long id) throws PlatformException;
	
	LocalTransaction getBySessionId(String sessionId) throws PlatformException;

	List<LocalTransaction> getRunningTransByCardNo(String cardNo) throws PlatformException;

	/**
	 * 
	 * @param sessionId
	 * @return 
	 */
	boolean checkCardOptFinish(String sessionId);

	/**
	 * 查找指定手机号的创建安全域LOCALTRANS
	 * @param customer
	 * @param paramMap
	 * @return
	 */
	Page<LocalTransaction> findCreateSdLocalTransactionForMobile(Page<LocalTransaction> page, Map<String, Object> paramMap);
}