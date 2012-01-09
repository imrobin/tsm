package com.justinmobile.tsm.transaction.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;

public interface DesiredOperationDao extends EntityDao<DesiredOperation, Long> {

	Page<DesiredOperation> findPageByParam(Page<DesiredOperation> page, Map<String, String> paramMap);

	DesiredOperation getDObyUserAidOptStatus(String ccid, String aid, String opttype, int status);

	Page<DesiredOperation> findPageByCustomerParam(Page<DesiredOperation> page, String executionStatus, Customer customer);

	DesiredOperation getDObyCCIandAidOptStatus(CustomerCardInfo cci, String aid, String opttype, int status);

	/**
	 * 获取指定卡上未执行的且需要强制执行的预定操作
	 * 
	 * @param customerCardInfo
	 * @return 查询结果
	 */
	List<DesiredOperation> getFocedOperationByCustomerCardThatNotExcute(CustomerCardInfo customerCard);

	/**
	 * 根据AID、ProcedureName和CustomerCard查找状态为“未执行”的唯一结果
	 * 
	 * @param aid
	 * @param procedureName
	 * @param customerCard
	 * @return 查询结果<br/>
	 *         null-如果没有满足条件的记录
	 */
	DesiredOperation getByAidAndProcedureNameAndCustomerCardThatNotExcuted(String aid, String procedureName, CustomerCardInfo customerCard);
}