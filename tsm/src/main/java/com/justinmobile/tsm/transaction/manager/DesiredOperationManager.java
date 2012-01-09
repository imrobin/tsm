package com.justinmobile.tsm.transaction.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Transactional
public interface DesiredOperationManager extends EntityManager<DesiredOperation> {

	Page<DesiredOperation> findPageByParam(Page<DesiredOperation> page, Map<String, String> paramMap);

	DesiredOperation createDO(String aid, String opttype, String userName, String ccid, String cardNo);

	Page<DesiredOperation> findPageByCustomerParam(Page<DesiredOperation> page, String currentUserName, String executionStatus);

	void change(Long doId, String sessionId, int flag, String result, String cardNo);

	/**
	 * 获取指定卡上未执行的且需要强制执行的预定操作
	 * 
	 * @param customerCardInfo
	 * @return 查询结果
	 */
	List<DesiredOperation> getFocedOperationByCustomerCardThatNotExcute(CustomerCardInfo customerCardInfo);

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

	/**
	 * 根据session id获取唯一结果
	 * 
	 * @param sessionId
	 * @return
	 */
	DesiredOperation getBySessionId(String sessionId);

	/**
	 * 为任务添加customerCardInfoid
	 * 
	 * @param doIds
	 * @param cci
	 */
	void setCustomerCardInfo(String doIds, CustomerCardInfo cci);

	DesiredOperation getDoIdByAidAndOpt(String aid, String opt, String cardNo);

	/**
	 * 根据用户-卡绑定关系、aid、对应的操作和执行状态查找唯一记录
	 * 
	 * @param customerCard
	 *            用户-卡绑定关系
	 * @param aid
	 *            aid
	 * @param operation
	 *            对应的操作
	 * @param status
	 *            执行状态
	 * @return
	 */
	DesiredOperation getByCustomerCardIdAndAidAndOperationAndStatuts(CustomerCardInfo customerCard, String aid, Operation operation,
			int status);

}