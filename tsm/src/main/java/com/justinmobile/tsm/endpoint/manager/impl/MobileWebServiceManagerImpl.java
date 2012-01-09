package com.justinmobile.tsm.endpoint.manager.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.cms2ac.domain.Task;
import com.justinmobile.tsm.cms2ac.engine.TransactionHelper;
import com.justinmobile.tsm.cms2ac.manager.TaskManager;
import com.justinmobile.tsm.endpoint.manager.MobileWebServiceManager;
import com.justinmobile.tsm.endpoint.webservice.dto.Status;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppOperate;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.impl.MobileWebServiceImpl;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.process.mocam.MocamResult;
import com.justinmobile.tsm.process.mocam.MocamResult.ApduName;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;

@Service("mobileWebServiceManager")
public class MobileWebServiceManagerImpl implements MobileWebServiceManager {
	protected static final Logger log = LoggerFactory.getLogger(MobileWebServiceImpl.class);

	private static Map<String, LocalTransaction> transCache = new ConcurrentHashMap<String, LocalTransaction>();

	// @Autowired
	// private SysUserManager userManager;
	//
	// @Autowired
	// private CustomerManager customerManager;

	@Autowired
	private TransactionHelper transactionHelper;

	@Autowired
	private LocalTransactionManager localTransactionManager;

	@Autowired
	private TaskManager taskManager;

	@Override
	public ResExecAPDU execAPDU(ReqExecAPDU reqExecAPDU) {
		ResExecAPDU apdu = new ResExecAPDU();
		String sessionId = reqExecAPDU.getSessionID();

		Status status = new Status();
		apdu.setStatus(status);
		List<AppOperate> appAids = reqExecAPDU.getAppList().getAppOperate();

		String cardNo = reqExecAPDU.getCardNo();
		String commonType = reqExecAPDU.getCommonType();
		// 取第一个操作
		if (StringUtils.isBlank(sessionId)) {
			// 第一次请求时，检查卡片和用户是否存在，是否登录等
			checkCustomer(reqExecAPDU);
			sessionId = transactionHelper.createSession(appAids, cardNo, commonType, SpringSecurityUtils.getCurrentUserName());
			LocalTransaction localTransaction = localTransactionManager.getBySessionId(sessionId);
			localTransaction.setBeginTime(Calendar.getInstance());
		}

		// 开始处理流程，结果保存在参数apdu中
		execAPDU(reqExecAPDU, apdu, sessionId);

		Task task = localTransactionManager.getBySessionId(sessionId).getTask();
		taskManager.saveOrUpdate(task);

		return apdu;
	}

	private void checkCustomer(ReqExecAPDU reqExecAPDU) {
		// String userName = SpringSecurityUtils.getCurrentUserName();
		// if (StringUtils.isBlank(userName)) {
		// throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
		// }
		// SysUser user = userManager.getUserByName(userName);
		// if (user == null) {
		// throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
		// }
		// String roleName = user.getSysRole().getRoleName();
		// if (SpecialRoleType.CUSTOMER.name().equals(roleName)) {
		// Customer c = customerManager.getCustomerByUserName(userName);
		// if (c == null) {
		// throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
		// }
		// List<CustomerCardInfo> customerCardInfos = c.getCustomerCardInfos();
		// if (CollectionUtils.isEmpty(customerCardInfos)) {
		// throw new
		// PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
		// }
		// boolean hasCard = false;
		// for (CustomerCardInfo customerCardInfo : customerCardInfos) {
		// String cCardNo = customerCardInfo.getCard().getCardNo();
		// if (cCardNo.equals(reqExecAPDU.getCardNo())) {
		// hasCard = true;
		// }
		// }
		// if (!hasCard) {
		// throw new
		// PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
		// }
		// }
	}

	private void execAPDU(ReqExecAPDU reqExecAPDU, ResExecAPDU apdu, String sessionId) {
		Status status = new Status();
		apdu.setStatus(status);
		try {
			try {
				apdu = processTrans(reqExecAPDU, apdu, sessionId);
				status.setStatusCode(PlatformMessage.SUCCESS.getCode());
				status.setStatusDescription(PlatformMessage.SUCCESS.getMessage());
			} catch (PlatformException e) {
				throw e;
			} catch (Exception e) {
				throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
			}
		} catch (PlatformException e) {// 如果当前流程抛出异常，处理异常后执行任务的下一流程（如果还有流程需要执行）或解说流程（如果没有流程需要执行）
			e.printStackTrace();
			String errorCode = e.getErrorCode().getErrorCode();
			status.setStatusCode(errorCode);
			status.setStatusDescription(e.getMessage());
			if (StringUtils.isNotBlank(sessionId)) {// 如果有localTransaction，修改结果
				LocalTransaction localTransaction = endFaildTrans(sessionId, e, errorCode);
				transactionHelper.completDesiredOperation(localTransaction);

				Task task = localTransaction.getTask();
				task.increaseFailTransCount();// 任务的失败流程数+1
				processNextTransactionOrEndTask(reqExecAPDU, apdu, task, MocamResult.getLastResult(localTransaction.getAid()),
						transactionHelper.getNextSeqNum());// 执行任务的下一流程（如果还有流程需要执行）或解说流程（如果没有流程需要执行）

				removeAllTranscationFormCache(localTransaction.getMainTransaction());
			}
		}
	}

	private ResExecAPDU processTrans(ReqExecAPDU reqExecAPDU, ResExecAPDU apdu, String sessionId) {
		MocamProcessor processor = null;
		MocamResult result = new MocamResult();

		LocalTransaction localTransaction = transCache.get(sessionId);
		if (null == localTransaction) {
			localTransaction = localTransactionManager.getBySessionId(sessionId);
			transCache.put(sessionId, localTransaction);
		}

		processor = transactionHelper.routeProcessor(localTransaction.getProcedureName());
		result = processor.process(localTransaction, reqExecAPDU);
		if (localTransaction.isComplete()) {// 当前流程已经完成
			localTransaction.setEndTime(Calendar.getInstance());

			transactionHelper.completDesiredOperation(localTransaction);
			removeAllTranscationFormCache(localTransaction);

			Task task = localTransaction.getTask();
			if (PlatformMessage.SUCCESS.getCode().equals(localTransaction.getResult())) {
				task.increaseSuccTransCount();
			} else {
				task.increaseFailTransCount();
			}
			apdu = processNextTransactionOrEndTask(reqExecAPDU, apdu, task, result, transactionHelper.getNextSeqNum());// 执行任务的下一流程（如果还有流程需要执行）或解说流程（如果没有流程需要执行）
		} else {// 当前流程未完成，设置当前流程的执行结果
			buildResApdu(reqExecAPDU, apdu, localTransaction, transactionHelper.getNextSeqNum(), result);
		}

		log.debug("\n" + "下行报文前会话状态：" + localTransaction.getSessionStatus() + "(" + localTransaction.getLocalSessionId() + ")" + "\n");
		return apdu;
	}

	private LocalTransaction endFaildTrans(String sessionId, PlatformException e, String errorCode) {
		LocalTransaction localTransaction = localTransactionManager.getBySessionId(sessionId);
		localTransaction.setResult(errorCode);// 设置状态码
		localTransaction.setFailMessage(StringUtils.isBlank(e.getMessage()) ? e.getErrorCode().getDefaultMessage() : e.getMessage());
		localTransaction.setExecutionStatusAsCompeleted();// 将当前流程及其子流程的执行状态设为“已执行”
		localTransaction.setEndTime(Calendar.getInstance());
		localTransactionManager.saveOrUpdate(localTransaction);
		return localTransaction;
	}

	/**
	 * 从缓存中移出当前流程及其子流程
	 * 
	 * @param localTransaction
	 */
	private void removeAllTranscationFormCache(LocalTransaction localTransaction) {
		for (LocalTransaction subTransaction : localTransaction.getSubTransactions()) {
			removeAllTranscationFormCache(subTransaction);
		}

		transCache.remove(localTransaction.getLocalSessionId());
	}

	private ResExecAPDU processNextTransactionOrEndTask(ReqExecAPDU reqExecAPDU, ResExecAPDU apdu, Task task, MocamResult result,
			String nextSeqNum) {
		LocalTransaction localTransaction = task.getCurrentTransaction();
		task.increaseCurrentTransIndex();// 任务的当前流程索引+1
		if (task.hasTrancationToExecut()) {// 如果任务还有流程需要执行，执行流程
			LocalTransaction currentTransaction = task.getCurrentTransaction();
			currentTransaction.setBeginTime(Calendar.getInstance());
			String sessionId = currentTransaction.getLocalSessionId();

			execAPDU(reqExecAPDU, apdu, sessionId);
		} else {// 否则结束任务
			task.setFinished(Boolean.TRUE);
			task.setEndTime(Calendar.getInstance());
			buildResApdu(reqExecAPDU, apdu, localTransaction, nextSeqNum, result);
		}

		return apdu;
	}

	private void buildResApdu(ReqExecAPDU reqExecAPDU, ResExecAPDU apdu, LocalTransaction localTransaction, String nextSeqNum,
			MocamResult result) {
		apdu.addApdus(result.getApdus());
		apdu.setCommandID(Operation.valueOf(localTransaction.getProcedureName()).getCommandId());
		apdu.setCurrentAppAid(localTransaction.getAid());
		apdu.setProgress(result.getProgress());
		apdu.setProgressPercent(result.getProgressPercent());
		apdu.setSeqNum(nextSeqNum);
		apdu.setSessionID(localTransaction.getLocalSessionId());
		apdu.setTimeStamp(CalendarUtils.getFormatNow());
		ApduName apduName = result.getApduName();
		if (apduName != null) {
			apdu.setApduName(apduName.name());
		} else {
			apdu.setApduName(ApduName.Complete.name());
		}
	}
}
