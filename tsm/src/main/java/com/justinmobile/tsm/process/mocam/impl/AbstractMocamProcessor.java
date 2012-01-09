package com.justinmobile.tsm.process.mocam.impl;

import java.util.Calendar;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.security.manager.SysRoleManager;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.tsm.application.manager.AppletManager;
import com.justinmobile.tsm.application.manager.ApplicationClientInfoManager;
import com.justinmobile.tsm.application.manager.ApplicationLoadFileManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationServiceManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.manager.CardAppletManager;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;
import com.justinmobile.tsm.card.manager.CardClientManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.card.manager.CardLoadFileManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.ApduResult;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.engine.ApduEngine;
import com.justinmobile.tsm.cms2ac.engine.TransLockContainer;
import com.justinmobile.tsm.cms2ac.engine.TransactionHelper;
import com.justinmobile.tsm.cms2ac.manager.TaskManager;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.endpoint.webservice.ProviderService;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.fee.manager.FeeStatManager;
import com.justinmobile.tsm.history.manager.SubscribeHistoryManager;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.CommType;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.ExecutionStatus;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;
import com.justinmobile.tsm.utils.SystemConfigUtils;

public abstract class AbstractMocamProcessor implements MocamProcessor {

	protected static final Logger log = LoggerFactory.getLogger(AbstractMocamProcessor.class);

	@Autowired
	protected OracleSequenceDao sequenceDao;

	@Autowired
	protected TaskManager taskManager;

	@Autowired
	protected ApplicationLoadFileManager applicationLoadFileManager;

	@Autowired
	protected ApplicationManager applicationManager;

	@Autowired
	protected ApplicationVersionManager applicationVersionManager;

	@Autowired
	protected AppletManager appletManager;

	@Autowired
	protected SecurityDomainManager securityDomainManager;

	@Autowired
	protected CustomerCardInfoManager customerCardInfoManager;

	@Autowired
	protected LocalTransactionManager localTransactionManager;

	@Autowired
	protected CardInfoManager cardInfoManager;

	@Autowired
	protected CardLoadFileManager cardLoadFileManager;

	@Autowired
	protected CardAppletManager cardAppletManager;

	@Autowired
	protected CardApplicationManager cardApplicationManager;

	@Autowired
	protected CardBaseApplicationManager cardBaseApplicationManager;

	@Autowired
	protected CardSecurityDomainManager cardSecurityDomainManager;

	@Autowired
	protected SpBaseInfoManager spBaseInfoManager;

	@Autowired
	protected ApduEngine apduEngine;

	@Autowired
	protected TransactionHelper transactionHelper;

	@Autowired
	protected TransLockContainer transLockContainer;

	@Autowired
	protected SubscribeHistoryManager subscribeHistoryManager;

	@Autowired
	@Qualifier("providerCaller")
	protected ProviderService providerCaller;

	@Autowired
	protected FeeStatManager feeStatManager;

	@Autowired
	protected ApplicationServiceManager applicationServiceManager;

	@Autowired
	protected CustomerManager customerManager;

	@Autowired
	protected SysUserManager userManager;

	@Autowired
	protected SysRoleManager roleManager;

	@Autowired
	protected CardClientManager cardClientManager;

	@Autowired
	protected ApplicationClientInfoManager clientManager;

	/**
	 * 为当前流程创建子流程，并且添加到当前流程的子流程列表中。子流程的操作不需要提供版本号
	 * 
	 * @param localTransaction
	 *            当前流程
	 * @param aid
	 *            子流程操作对象的AID
	 * @param operation
	 *            子流程的操作类型
	 * @return
	 */
	protected LocalTransaction buildSubTransaction(LocalTransaction localTransaction, String aid, Operation operation) {
		return buildSubTransaction(localTransaction, aid, null, operation);
	}

	/**
	 * 为当前流程创建子流程，并且添加到当前流程的子流程列表中
	 * 
	 * @param localTransaction
	 *            当前流程
	 * @param aid
	 *            子流程操作对象的AID
	 * @param versionNo
	 *            子流程操作对象的版本（如果子流程是对安全域进行操作，此字段为null）
	 * @param operation
	 *            子流程的操作类型
	 * @return
	 */
	protected LocalTransaction buildSubTransaction(LocalTransaction localTransaction, String aid, String versionNo, Operation operation) {
		LocalTransaction subTransaction = transactionHelper.buildTransaction(localTransaction.getCardNo(),
				CommType.getNameByType(localTransaction.getCommType()), aid, versionNo, operation.getType());
		localTransaction.addSubTransaction(subTransaction);
		subTransaction.setProviderSessionId(localTransaction.getProviderSessionId());// 将父流程的业务平台会话ID赋值给子流程，如果子流程不需要预处理，既可以重用父流程的业务平台会话ID
		return subTransaction;
	}

	protected void endTransaction(LocalTransaction localTransaction, PlatformMessage message) {
		try {
			localTransaction.setResult(message);
			localTransaction.setExecutionStatus(ExecutionStatus.COMPLETED.getStatus());// 将当前流程及子流程的执行状态改为“已执行”
			localTransactionManager.saveOrUpdate(localTransaction);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR);
		}
	}

	protected void endSuccessTransaction(LocalTransaction localTransaction) {
		LocalTransaction superTransaction = localTransaction.getSuperTransaction();
		if (null != superTransaction) {
			superTransaction.increaseCurrentSubTransactionIndex();
		}
		endTransaction(localTransaction, PlatformMessage.SUCCESS);
	}

	protected MocamResult process(LocalTransaction localTransaction) {
		return process(localTransaction, null);
	}

	public MocamResult process(LocalTransaction localTransaction, ReqExecAPDU reqExecAPDU) {
		MocamResult result = null;

		if (localTransaction.hasSubTransactionToExcute()) {// 如果当前流程有子流程需要执行，执行子流程
			LocalTransaction subTransactionToExcute = localTransaction.getSubTransactionToExcute();// 获取待执行的子流程

			int operation = LocalTransaction.Operation.valueOf(subTransactionToExcute.getProcedureName()).getType();// 获取待执行的子流程的操作码
			MocamProcessor processor = transactionHelper.routeProcessor(operation);// 获取processor

			result = processor.process(subTransactionToExcute, reqExecAPDU);// 执行子流程
			if (subTransactionToExcute.isComplete()) {// 如果子流程执行完成
				log.debug("\n" + "子流程执行完成：" + subTransactionToExcute.getAid() + "，" + subTransactionToExcute.getProcedureName() + "\n");

				subTransactionToExcute.setEndTime(Calendar.getInstance());
				if (!localTransaction.isComplete()) {// 如果当前流程没有执行完成，执行当前流程。（子流程终止会导致父流程同步终止）
					result = processTrans(localTransaction);
				}
			}
		} else {// 如果当前流程没有子流程需要执行，执行当前流程
			if (null != reqExecAPDU) {
				buildCms2acResult(reqExecAPDU, localTransaction);// 组建卡操作结果
			}

			result = processTrans(localTransaction);// 执行当前流程
		}

		if (LocalTransaction.STATUS_EXECUTION_EXEUTORY == localTransaction.getExecutionStatus().intValue()) {// 如果当前流程的执行状态是“待执行”，修改当前流程的执行状态为“已执行”
			log.debug("\n" + "子流程执行开始：" + localTransaction.getAid() + "，" + localTransaction.getProcedureName() + "\n");
			localTransaction.setExecutionStatus(LocalTransaction.STATUS_EXECUTION_EXEUTING);
			localTransaction.setBeginTime(Calendar.getInstance());
		}
		localTransactionManager.saveOrUpdate(localTransaction);

		return result;
	}

	private void buildCms2acResult(ReqExecAPDU reqExecAPDU, LocalTransaction localTransaction) {
		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		if (null != cms2acParam) {
			int responseBatchNo = cms2acParam.getCommandBatchNo();
			CardPOR cardPOR = reqExecAPDU.getCardPOR();
			byte[] lastData = ConvertUtils.hexString2ByteArray(cardPOR.getLastData());
			int index = Integer.parseInt(cardPOR.getApduSum());
			ApduResult apduResult = new ApduResult(lastData, responseBatchNo, index);
			cms2acParam.getApduResults().add(apduResult);
		}
	}

	protected MocamResult processTrans(LocalTransaction localTransaction) {
		MocamResult result = null;
		switch (localTransaction.getSessionStatus()) {
		case SessionStatus.OPERATE_NOTIFY:
			result = operationResult(localTransaction, SessionStatus.COMPLETED);
			break;
		case SessionStatus.COMPLETED:
			result = endSuccessProcess(localTransaction);
			break;
		case SessionStatus.TERMINATE:
			transactionHelper.endFaildTrans(localTransaction);
			result = MocamResult.getLastResult(localTransaction.getAid());
			break;
		default:
			System.out.println(localTransaction.getSessionStatus());
			throw new PlatformException(PlatformErrorCode.INVALID_SESSION_STATUS);
		}
		return result;
	}

	abstract protected MocamResult operationResult(LocalTransaction localTransaction, int successSessionStatus);

	protected MocamResult endSuccessProcess(LocalTransaction localTransaction) {
		// 关闭当前事务
		endSuccessTransaction(localTransaction);

		// 完成计费
		if (!SystemConfigUtils.isTestRuntimeEnvironment()) {
			feeStatManager.genStatRecord(localTransaction);
		}

		// 组建MocamResult的空对象
		return MocamResult.getLastResult(localTransaction.getAid());
	}

	protected abstract void check(LocalTransaction localTransaction);

}
