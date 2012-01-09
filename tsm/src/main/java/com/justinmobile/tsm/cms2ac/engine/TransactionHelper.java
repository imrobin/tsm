package com.justinmobile.tsm.cms2ac.engine;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.application.manager.AppletManager;
import com.justinmobile.tsm.application.manager.ApplicationLoadFileManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.ApplicationVersionManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardAppletManager;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseSecurityDomainManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.card.manager.CardLoadFileManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.Task;
import com.justinmobile.tsm.cms2ac.dto.SpaceInfo;
import com.justinmobile.tsm.cms2ac.manager.TaskManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.endpoint.webservice.ProviderService;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.AppOperate;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultMessage;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.OperationResultResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PreOperationMessage;
import com.justinmobile.tsm.endpoint.webservice.dto.sp.PreOperationResponse;
import com.justinmobile.tsm.process.mocam.MocamProcessor;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.CommType;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.ExecutionStatus;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.DesiredOperationManager;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;

@Service("transactionHelper")
public class TransactionHelper implements ApplicationContextAware {

	public static final String TRANS_SEQNUM = "TRANS_SEQNUM_ID";

	private ApplicationContext applicationContext;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	@Autowired
	private ApplicationManager applicationManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private TransLockContainer transLockContainer;

	@Autowired
	private CardLoadFileManager cardLoadFileManager;

	@Autowired
	private CardSecurityDomainManager cardSecurityDomainManager;

	@Autowired
	private ApplicationLoadFileManager applicationLoadFileManager;

	@Autowired
	private ApplicationVersionManager applicationVersionManager;

	@Autowired
	private CardAppletManager cardAppletManager;

	@Autowired
	private AppletManager appletManager;

	@Autowired
	private TaskManager taskManager;

	@Autowired
	private CardInfoManager cardManager;

	@Autowired
	private OracleSequenceDao sequenceDao;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@Autowired
	private LocalTransactionManager localTransactionManager;

	@Autowired
	private DesiredOperationManager desiredOperationManager;

	@Autowired
	private CardBaseSecurityDomainManager cardBaseSecurityDomainManager;

	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;

	@Autowired
	protected OracleSequenceDao oracleSequenceDao;

	@Autowired
	@Qualifier("providerCaller")
	protected ProviderService providerCaller;

	public OperationResultResponse operationResult(LocalTransaction localTransaction) {
		return providerCaller.operationResult(buildOperationResultMessage(localTransaction));
	}

	private OperationResultMessage buildOperationResultMessage(LocalTransaction localTransaction) {
		CardInfo card = cardManager.getByCardNo(localTransaction.getCardNo());
		CardPOR cardPOR = localTransaction.getCardPOR();

		OperationResultMessage message = new OperationResultMessage();
		message.setSeqNum(oracleSequenceDao.getNextSerialNoWithTime(TRANS_SEQNUM));
		message.setSessionId(localTransaction.getProviderSessionId());
		message.setSessionType(Operation.valueOf(localTransaction.getProcedureName()).getSessionType().getValue());
		message.setTimeStamp(CalendarUtils.getFormatNow());
		message.setOriginalSeqNum(null);
		message.setAid(localTransaction.getAid());
		message.setMsisdn(localTransaction.getMobileNo());
		if (null != cardPOR) {
			message.setReslutCode(cardPOR.getLastAPDUSW());
			message.setResultMsg(null);
		}
		message.setImsi(card.getImsi());
		message.setCardPOR(cardPOR);

		return message;
	}

	public PreOperationResponse preOperation(LocalTransaction localTransaction) {
		return providerCaller.preOperation(buildPreOperationMessage(localTransaction));
	}

	private PreOperationMessage buildPreOperationMessage(LocalTransaction localTransaction) {
		PreOperationMessage message = new PreOperationMessage();

		message.setSeqNum(oracleSequenceDao.getNextSerialNoWithTime(TRANS_SEQNUM));
		message.setSessionId(localTransaction.getLocalSessionId());
		message.setTimeStamp(CalendarUtils.getFormatNow());
		message.setCommType(localTransaction.getCommType());
		message.setMsisdn(localTransaction.getMobileNo());
		message.setSeId(localTransaction.getCardNo());
		message.setAppAid(localTransaction.getAid());
		message.setOpernType(Operation.valueOf(localTransaction.getProcedureName()).getType());
		message.setSessionType(Operation.valueOf(localTransaction.getProcedureName()).getSessionType().getValue());
		message.setImei(cardManager.getByCardNo(localTransaction.getCardNo()).getImei());

		return message;
	}

	public String createSession(List<AppOperate> appOperates, String cardNo, String commonType, String username) {
		try {
			CardInfo card = cardManager.getByCardNo(cardNo);
			if (null == card) {
				throw new PlatformException(PlatformErrorCode.CARD_NO_UNEXIST);
			}

			Task task = buildTask();
			taskManager.saveOrUpdate(task);// 为了获取ID

			CustomerCardInfo customerCard = customerCardInfoManager.getByCardThatStatusNotCanclledOrNotReplaced(card);
			if (null != customerCard) {
				checkFocedDesiredOperations(cardNo, commonType, task, customerCard);
				checkRunningOperations(cardNo, commonType, task, customerCard);
			}

			if (null != appOperates) {
				for (int i = 0; i < appOperates.size(); i++) {
					AppOperate appOperate = appOperates.get(i);
					LocalTransaction trans = buildTransaction(cardNo, commonType, appOperate.getAppAid(), appOperate.getAppVersion(),
							appOperate.getOperation(), customerCard, appOperate.getOriginalCardNo());
					trans.setTask(task);
					task.getLocalTransactions().add(trans);

					if (null != customerCard && !Operation.CARD_OPERATIONS.contains(Operation.valueOf(trans.getProcedureName()))) {
						DesiredOperation desiredOperation = desiredOperationManager.getByAidAndProcedureNameAndCustomerCardThatNotExcuted(
								trans.getAid(), trans.getProcedureName(), customerCard);
						if (null != desiredOperation) {
							desiredOperation.setSessionId(trans.getLocalSessionId());
							desiredOperation.setTaskId(task.getId());
							desiredOperationManager.saveOrUpdate(desiredOperation);
						}
					}
				}
			}

			task.setTransCount(task.getLocalTransactions().size());

			if (0 == task.getTransCount()) {// 如果任务中没有操作，抛出异常
				throw new PlatformException(PlatformErrorCode.APPLICAION_AID_NOT_EXIST);
			}

			taskManager.saveOrUpdate(task);
			return task.getLocalTransactions().get(0).getLocalSessionId();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	public LocalTransaction endFaildTrans(LocalTransaction localTransaction, PlatformException e) {
		// 结束当前流程
		localTransaction.setResult(e.getErrorCode().getErrorCode());// 设置状态码
		localTransaction.setFailMessage(StringUtils.isBlank(e.getMessage()) ? e.getErrorCode().getDefaultMessage() : e.getMessage());
		localTransaction.setExecutionStatusAsCompeleted();// 将当前流程及其子流程的执行状态设为“已执行”
		localTransaction.setEndTime(Calendar.getInstance());
		localTransactionManager.saveOrUpdate(localTransaction);

		return localTransaction;
	}

	/**
	 * 检查是否有未完成的操作，如果有，添加卡数据同步流程，并关闭未完成事务及所属任务
	 * 
	 * @param cardNo
	 * @param commonType
	 * @param task
	 * @param customerCard
	 */
	private void checkRunningOperations(String cardNo, String commonType, Task task, CustomerCardInfo customerCard) {
		List<LocalTransaction> localTransactions = localTransactionManager.getRunningTransByCardNo(cardNo);
		if (CollectionUtils.isNotEmpty(localTransactions)) {
			for (LocalTransaction localTransaction : localTransactions) {
				Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
				if (cms2acParam != null) {
					SecurityDomain sd = cms2acParam.getCurrentSecurityDomain();
					LocalTransaction trans = buildTransaction(cardNo, commonType, sd.getAid(), null, Operation.SYNC_CARD_SD, customerCard);
					task.getLocalTransactions().add(trans);
					trans.setTask(task);
				}
				// 处理事务
				localTransaction.setExecutionStatus(ExecutionStatus.COMPLETED.getStatus());
				localTransaction.setResult(PlatformMessage.TRANS_EXCEPTION_CLOSED);
				localTransaction.setFailMessage(PlatformMessage.TRANS_EXCEPTION_CLOSED.getMessage());
				localTransaction.setEndTime(Calendar.getInstance());
				localTransactionManager.saveOrUpdate(localTransaction);

				// 处理事务所属应用
				Task exceptionTask = getTask(localTransaction);
				exceptionTask.setEndTime(Calendar.getInstance());
				exceptionTask.increaseFailTransCount();
				exceptionTask.setFinished(true);
				taskManager.saveOrUpdate(exceptionTask);

				// 处理事务对应预置操作
				completDesiredOperation(localTransaction);
			}
		}
	}

	/**
	 * 检查是否有要求强制执行的预定操作，如果有，建立对应事务并添加到任务中
	 * 
	 * @param cardNo
	 * @param commonType
	 * @param task
	 * @param customerCard
	 */
	private void checkFocedDesiredOperations(String cardNo, String commonType, Task task, CustomerCardInfo customerCard) {
		List<DesiredOperation> desiredOperations = desiredOperationManager.getFocedOperationByCustomerCardThatNotExcute(customerCard);
		for (DesiredOperation desiredOperation : desiredOperations) {
			LocalTransaction localTransaction = buildTransaction(cardNo, commonType, desiredOperation.getAid(), null,
					Operation.valueOf(desiredOperation.getProcedureName()), customerCard);
			localTransactionManager.saveOrUpdate(localTransaction);

			desiredOperation.setSessionId(localTransaction.getLocalSessionId());
			desiredOperation.setTaskId(task.getId());
			desiredOperationManager.saveOrUpdate(desiredOperation);

			task.addTransaction(localTransaction);
		}
	}

	/**
	 * 完成事务对应的预定操作
	 * 
	 * @param localTransaction
	 */
	public void completDesiredOperation(LocalTransaction localTransaction) {
		DesiredOperation desiredOperation = desiredOperationManager.getBySessionId(localTransaction.getLocalSessionId());
		if (null != desiredOperation) {// 如果事务有对应的预定操作
			if (PlatformMessage.SUCCESS.getCode().equals(localTransaction.getResult())) {// 如果事务执行成功
				desiredOperation.setResult(PlatformMessage.SUCCESS.getDefaultMessage());
				desiredOperation.setIsExcuted(DesiredOperation.FINISH_EXCUTED);
			} else if (DesiredOperation.PREPROCESS_TURE != desiredOperation.getPreProcess().intValue()) {// 如果事务执行失败，并且强制执行操作
				desiredOperation.setResult(localTransaction.getFailMessage());
				desiredOperation.setIsExcuted(DesiredOperation.NOT_FINISH_EXCUTED);
			}
			desiredOperationManager.saveOrUpdate(desiredOperation);
		}
	}

	private Task getTask(LocalTransaction localTransaction) {
		Task task = localTransaction.getTask();
		if (null == localTransaction.getTask()) {
			task = getTask(localTransaction.getSuperTransaction());
		}
		return task;
	}

	private LocalTransaction buildTransaction(String cardNo, String commonType, String aid, String versionNo, Operation operation,
			CustomerCardInfo customerCard) {
		return buildTransaction(cardNo, commonType, aid, versionNo, operation.getType(), customerCard);
	}

	public LocalTransaction buildTransaction(String cardNo, String commonType, String aid, String versionNo, int operation) {
		CustomerCardInfo customerCard = customerCardInfoManager.getByCardNo(cardNo);
		if (customerCard == null) {
			throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
		}
		return buildTransaction(cardNo, commonType, aid, versionNo, operation, customerCard);
	}

	private LocalTransaction buildTransaction(String cardNo, String commonType, String aid, String versionNo, int operation,
			CustomerCardInfo customerCard) {
		return buildTransaction(cardNo, commonType, aid, versionNo, operation, customerCard, null);
	}

	private LocalTransaction buildTransaction(String cardNo, String commonType, String aid, String versionNo, int operationValue,
			CustomerCardInfo customerCard, String originalCardNo) {
		LocalTransaction trans = new LocalTransaction();
		String operationName = Operation.valueOf(operationValue);
		Operation operation = Operation.valueOf(operationName);
		String mobileNo;
		if (Operation.REGISTER == operation || Operation.REPLACE_MOBILE_NO == operation) {
			CardInfo card = cardManager.getByCardNo(cardNo);
			mobileNo = card.getMobileNo();
		} else {
			if (null == customerCard) {
				throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
			}
			mobileNo = customerCard.getMobileNo();
		}
		trans.setAid(aid);
		trans.setCardNo(cardNo);
		trans.setMobileNo(mobileNo);
		trans.setAppVersion(versionNo);
		trans.setLocalSessionId(getNextSessionId());
		String ueprof = StringUtils.substringBefore(commonType, "-");
		try {
			trans.setCommType(CommType.valueOf(ueprof).getType());
		} catch (IllegalArgumentException e) {
			throw new PlatformException(PlatformErrorCode.INVALID_COMM_TYPE);
		}
		trans.setProcedureName(operationName);
		trans.setOriginalCardNo(originalCardNo);
		trans.setExecutionStatus(ExecutionStatus.EXECUTORY.getStatus());
		trans.increaseExecuteNo();
		trans.setResult(PlatformMessage.TRANS_EXCESSIVING);
		return trans;
	}

	private Task buildTask() {
		Task task = new Task();
		task.setBeginTime(Calendar.getInstance());
		task.setCurrentTransIndex(1);
		task.setFinished(Boolean.FALSE);
		return task;
	}

	public boolean isPersoPrepared(LocalTransaction localTransaction) {
		if (localTransaction != null) {
			String sessionId = localTransaction.getLocalSessionId();
			TransLock transLock = transLockContainer.getTransLock(sessionId);
			if (transLock == null) {
				return false;
			}

			Integer sessionStatus = transLock.getSessionStatus();
			if (sessionStatus == null) {
				return false;
			}

			if (Operation.DOWNLOAD_APP.name().equals(localTransaction.getProcedureName())) {
				if (sessionStatus == SessionStatus.DOWNLOAD_APP_START_PERSO || sessionStatus == SessionStatus.DOWNLOAD_APP_PESO_APDU_RSP
						|| sessionStatus == SessionStatus.PERSONALIZE_APP_APDU_RSP) {
					return true;
				}
			}
			if (Operation.PERSONALIZE_APP.name().equals(localTransaction.getProcedureName())) {
				if (sessionStatus == SessionStatus.PERSONALIZE_APP_APDU_RSP) {
					return true;
				}
				return true;
			}
		}
		return false;
	}

	public MocamProcessor routeProcessor(String procedureName) {
		return routeProcessor(Operation.valueOf(procedureName));
	}

	public MocamProcessor routeProcessor(Operation operation) {
		String beanName = operation.getBeanName();
		return (MocamProcessor) applicationContext.getBean(beanName);
	}

	public MocamProcessor routeProcessor(Integer operationType) {
		return routeProcessor(Operation.valueOf(operationType));
	}

	public boolean isPersoTrans(LocalTransaction localTransaction, String newSessionId) {
		if (localTransaction != null) {
			String oldSessionId = localTransaction.getLocalSessionId();
			if (!StringUtils.equals(oldSessionId, newSessionId)) {
				return false;
			}

			TransLock transLock = transLockContainer.getTransLock(oldSessionId);
			if (transLock == null) {
				return false;
			}

			Integer sessionStatus = transLock.getSessionStatus();
			if (sessionStatus == null) {
				return false;
			}

			if (Operation.DOWNLOAD_APP.name().equals(localTransaction.getProcedureName())) {
				if (sessionStatus == SessionStatus.DOWNLOAD_APP_START_PERSO || sessionStatus == SessionStatus.DOWNLOAD_APP_PESO_APDU_RSP
						|| sessionStatus == SessionStatus.PERSONALIZE_APP_APDU_RSP) {
					return true;
				}
			}
			if (Operation.PERSONALIZE_APP.name().equals(localTransaction.getProcedureName())
					&& sessionStatus == SessionStatus.PERSONALIZE_APP_APDU_RSP) {
				return true;
			}
		}
		return false;
	}

	public void checkSpaceForSd(Space space, String cardNo, SecurityDomain toInsall) {
		long freeNoneVolatileSpace = space.getNvm();
		int freeVolatileSpace = space.getRam();
		SecurityDomain isd = securityDomainManager.getIsd();
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(cardNo, isd.getAid());
		cardSecurityDomain.setFreeVolatileSpace(freeVolatileSpace);
		cardSecurityDomain.setFreeNonVolatileSpace(freeNoneVolatileSpace);
		cardSecurityDomainManager.saveOrUpdate(cardSecurityDomain);

		if (toInsall.getSpaceRule() == SecurityDomain.FIXED_SPACE) {
			if (toInsall.getManagedNoneVolatileSpace() > freeNoneVolatileSpace || toInsall.getManagedVolatileSpace() > freeVolatileSpace) {
				throw new PlatformException(PlatformErrorCode.SD_SPACE_SCARCITY);
			}
		}
	}

	public void checkSpaceForApp(SpaceInfo sdSpaceInfo, String cardNo, String aid) {
		long freeNoneVolatileSpace = sdSpaceInfo.getFreeNoneVolatile();
		int freeVolatileSpace = sdSpaceInfo.getFreeVolatile();
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, aid);
		ApplicationVersion applicationVersion = cardApplication.getApplicationVersion();

		long needNoneVolatileSpace = applicationVersion.getNonVolatileSpace();
		int needVolatileSpace = applicationVersion.getVolatileSpace();
		if (needNoneVolatileSpace > freeNoneVolatileSpace || needVolatileSpace > freeVolatileSpace) {
			throw new PlatformException(PlatformErrorCode.SD_SPACE_SCARCITY);
		}
	}

	/**
	 * param =true 为可变，否则为不可变
	 * 
	 * @param appVersion
	 * @param param
	 * @return
	 */

	public SecurityDomain getSecurityDomainRelatedSd(String sdAid) {
		return securityDomainManager.getIsd();
	}

	public SecurityDomain getLoadFileRelatedSd(String appAid, LocalTransaction trans) {
		ApplicationVersion appVersion = applicationVersionManager.getAidAndVersionNo(trans.getAid(), trans.getAppVersion());
		Applet applet = appVersion.getApplet(trans.getCurrentAppletIndex().intValue());
		LoadModule loadModule = applet.getLoadModule();
		LoadFile loadFile = loadModule.getLoadFileVersion().getLoadFile();
		return loadFile.getSd();
	}

	public SecurityDomain getAppRelatedSd(String appAid) {
		Application app = applicationManager.getByAid(appAid);
		return app.getSd();
	}

	/**
	 * 卡上的安全域是否可以自动删除
	 * 
	 * @param card
	 *            卡
	 * @param sd
	 *            安全域
	 * @return true-可以自动删除<br/>
	 *         false-不能自动删除
	 */
	public boolean isSdNeedAutoDelete(CardInfo card, SecurityDomain sd) {
		// 如果是主安全域或者安全域的删除规则不为“自动删除”，返回false
		if (sd.isIsd() || SecurityDomain.AUTO_DELETE != sd.getDeleteRule()) {
			return false;
		}

		// 如果卡上安全记录不存在或者卡上安全域状态为“未安装”，返回false
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(card.getCardNo(), sd.getAid());
		if (cardSecurityDomain == null || CardSecurityDomain.STATUS_UNCREATE == cardSecurityDomain.getStatus()) {
			return false;
		}

		// 如果卡上安全域还有加载文件，返回false
		List<CardLoadFile> cardLoadFiles = cardLoadFileManager.getByCardAndLoadFileSd(card, sd);
		if (!CollectionUtils.isEmpty(cardLoadFiles)) {
			return false;
		}

		// 如果卡上安全域还有应用，返回false
		List<CardApplication> cardApplications = cardApplicationManager.getByCardAndApplicationSd(card, sd);
		for (CardApplication cardApplication : cardApplications) {
			if (CardApplication.STATUS_UNDOWNLOAD.intValue() != cardApplication.getStatus().intValue()) {
				return false;
			}
		}

		// 如果安全域不能删除
		if (!isDeletable(card, sd)) {
			return false;
		}

		// 所有检查都通过，返回true
		return true;

	}

	public CardSecurityDomain contactCardSecurityDomain(CardInfo cardInfo, SecurityDomain securityDomain, Integer status) {
		CardSecurityDomain cardSecurityDomain = new CardSecurityDomain();
		cardSecurityDomain.setCard(cardInfo);
		cardSecurityDomain.setSd(securityDomain);

		if (securityDomain.getSpaceRule() == SecurityDomain.FIXED_SPACE) {
			int freeVolatileSpace = securityDomain.getManagedVolatileSpace();
			long freeNoneVolatileSpace = securityDomain.getManagedNoneVolatileSpace();
			cardSecurityDomain.setFreeVolatileSpace(freeVolatileSpace);
			cardSecurityDomain.setFreeNonVolatileSpace(freeNoneVolatileSpace);
		}

		cardSecurityDomain.setStatus(status);
		cardSecurityDomain.setScp02SecurityLevel(securityDomain.getScp02SecurityLevel());

		return cardSecurityDomain;
	}

	public boolean needDeleteLoadFile(CardApplication deleteCardApp, LoadFileVersion loadFileVersion) {
		Application application = deleteCardApp.getApplicationVersion().getApplication();
		boolean deleteAppAndLoadFile = application.getDeleteRule() == Application.DELETE_RULE_DELETE_ALL;

		// 检查是否有依赖此加载文件版本的其他加载文件版本在卡上
		boolean isDepended = false;
		for (LoadFileVersion child : loadFileVersion.getChildren()) {
			if (null != cardLoadFileManager.getByCardAndLoadFileVersion(deleteCardApp.getCardInfo(), child)) {
				isDepended = true;
				break;
			}
		}

		return isLastCardAppOfLoadFile(deleteCardApp, loadFileVersion) && deleteAppAndLoadFile && (!isDepended);
	}

	public boolean isLastCardAppOfLoadFile(CardApplication deleteCardApp, LoadFileVersion loadFileVersion) {
		Set<ApplicationLoadFile> applicationLoadFiles = loadFileVersion.getApplicationLoadFiles();
		for (ApplicationLoadFile applicationLoadFile : applicationLoadFiles) {
			String aid = applicationLoadFile.getApplicationVersion().getApplication().getAid();
			String cardNo = deleteCardApp.getCardInfo().getCardNo();

			CardApplication cardAppWithSameLoadFile = cardApplicationManager.getByCardNoAid(cardNo, aid);
			if (cardAppWithSameLoadFile != null && !ObjectUtils.equals(cardAppWithSameLoadFile, deleteCardApp)) {
				if (cardAppWithSameLoadFile.getStatus().intValue() != CardApplication.STATUS_UNDOWNLOAD.intValue()) {
					return false;
				}
			}
		}
		return true;
	}

	public List<CardApplication> getCardAppWithSameLoadFile(String cardNo, LoadFileVersion loadFileVersion) {
		Set<ApplicationLoadFile> applicationLoadFiles = loadFileVersion.getApplicationLoadFiles();
		List<CardApplication> cardApps = Lists.newArrayList();
		for (ApplicationLoadFile applicationLoadFile : applicationLoadFiles) {
			String aid = applicationLoadFile.getApplicationVersion().getApplication().getAid();
			CardApplication cardApp = cardApplicationManager.getByCardNoAid(cardNo, aid);
			if (cardApp != null) {
				cardApps.add(cardApp);
			}
		}
		return cardApps;
	}

	/**
	 * 获取应用的个人化类型
	 * 
	 * @param localTransation
	 * @return 应用的个人化类型
	 */
	public int getPersonalType(LocalTransaction localTransation) {
		Application application = applicationManager.getByAid(localTransation.getAid());
		return application.getPersonalType();
	}

	/**
	 * 判断卡是否存在加载文件除指定版本之外的其他版本<br/>
	 * 以下情况为已存在<br/>
	 * <ol>
	 * <li>卡已预置加载文件其他版本</li>
	 * <li>卡已下载加载文件其他版本</li>
	 * </ol>
	 * 
	 * @param card
	 *            卡
	 * @param loadFileVersion
	 *            加载文件版本
	 * @return true-已存在<br/>
	 *         false-未存在
	 */
	public boolean isOtherVersionOfLoadFileExistOnCard(CardInfo card, LoadFileVersion loadFileVersion) {
		CardLoadFile cardLoadFile = cardLoadFileManager.getByAidAndCardNo(loadFileVersion.getLoadFile().getAid(), card.getCardNo());
		if (null == cardLoadFile) {// 如果检索结果为null，卡上没有加载文件的任何版本
			return false;
		} else {// 否则，卡上有加载文件的某一版本
			// 如果卡上版本与指定版本不相同，说明卡上有加载文件的其他版本，返回true
			return !loadFileVersion.equals(cardLoadFile.getLoadFileVersion());
		}
	}

	/**
	 * 验证卡上是否存在指定的加载文件版本
	 * 
	 * @param card
	 *            卡
	 * @param loadFileVersion
	 *            加载文件版本
	 * @return true-卡上存在指定的加载文件版本<br/>
	 *         false-卡上不存在
	 * @throws PlatformErrorCode.TRANS_DOWNLOAD_APP_OTHER_LOAD_FILE_VERSION_EXIST
	 *             卡上存在加载文件的其他版本
	 */
	public boolean isLoadFileVersionExistOnCard(CardInfo card, LoadFileVersion loadFileVersion) {
		// 先确定卡上无加载文件的其他版本
		if (isOtherVersionOfLoadFileExistOnCard(card, loadFileVersion)) {
			throw new PlatformException(PlatformErrorCode.TRANS_DOWNLOAD_APP_OTHER_LOAD_FILE_VERSION_EXIST);
		} else {
			return null != cardLoadFileManager.getByCardAndLoadFileVersion(card, loadFileVersion);
		}
	}

	/**
	 * 是否需要重新选择安全域？<br/>
	 * 当且仅当事务已经选择安全域并且当前所选择的安全域与待选择的安全域相同的情况下才可以不再选择安全域
	 * 
	 * @param localTransaction
	 * @param targetSd
	 *            待选择的安全域
	 * @return true-需要选择安全域<br/>
	 *         false-不需要选择安全域
	 */
	public boolean needSelectSd(LocalTransaction localTransaction, SecurityDomain targetSd) {
		boolean needSelectSd = true;

		Cms2acParam cms2acParam = localTransaction.getLastCms2acParam();
		if (null != cms2acParam) {
			SecurityDomain currentSd = cms2acParam.getCurrentSecurityDomain();
			if (null != currentSd) {
				needSelectSd = currentSd.equals(targetSd);
			}
		}

		return needSelectSd;
	}

	/**
	 * 获取下载顺序
	 * 
	 * @param localTransaction
	 * @return
	 */
	public List<ApplicationLoadFile> getDowloadOrder(LocalTransaction localTransaction) {
		ApplicationVersion applicationVersion = applicationVersionManager.getAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());
		List<ApplicationLoadFile> downloadOrder = applicationLoadFileManager.getAllByDownloadOrder(applicationVersion);
		return downloadOrder;
	}

	public LoadFileVersion getCurrentLoadFileVersionToDownload(LocalTransaction localTransaction) {
		List<ApplicationLoadFile> downloadOrder = getDowloadOrder(localTransaction);
		return downloadOrder.get(localTransaction.getCurrentLoadFileIndex() - 1).getLoadFileVersion();
	}

	/**
	 * 获取当前加载文件生成的待安装实例
	 * 
	 * @param localTransaction
	 * @return 待安装实例<br/>
	 *         null-如果当前加载文件没有实例需要安装或者所有实例都已安装
	 */
	public Applet getNextAppletToInstallFromCurrentLoadFile(LocalTransaction localTransaction) {
		LoadFileVersion loadFileVersion = getDowloadOrder(localTransaction).get(localTransaction.getCurrentLoadFileIndex() - 1)
				.getLoadFileVersion();
		ApplicationVersion applicationVersion = applicationVersionManager.getAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());

		Applet installApplet = null;
		for (Applet applet : applicationVersion.getApplets()) {// 对于应用版本的每一个实例
			if ((applet.getLoadModule().getLoadFileVersion().equals(loadFileVersion))
					&& !cardAppletManager.isIntallOnCard(localTransaction.getCardNo(), applet.getAid())) {// 如果实例来自于当前下载的加载文件且未安装
				installApplet = applet;// 将当前实例指定为待安装的实例
				break;// 跳出循环
			}
		}
		return installApplet;
	}

	public Applet getCurrentApplet(LocalTransaction localTransaction) {
		return appletManager.load(localTransaction.getCurrentAppletIndex());
	}

	/**
	 * 当前加载文件是否有待安装的实例
	 * 
	 * @param localTransaction
	 * @return true-有待安装的实例<br/>
	 *         false-没有待安装的实例
	 */
	public boolean hasAppletToInstallFromCurrentLoadFile(LocalTransaction localTransaction) {
		return null != getNextAppletToInstallFromCurrentLoadFile(localTransaction);
	}

	/**
	 * 获取当前加载文件生成的待删除实例
	 * 
	 * @param localTransaction
	 * @return 待删除实例<br/>
	 *         null-如果当前加载文件没有实例需要删除或者所有实例都已删除
	 */
	public Applet getNextAppletToDeleltFromCurrentLoadFile(LocalTransaction localTransaction) {
		LoadFileVersion loadFileVersion = getDeleteOrder(localTransaction).get(localTransaction.getCurrentLoadFileIndex() - 1)
				.getLoadFileVersion();
		ApplicationVersion applicationVersion = applicationVersionManager.getAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());

		// 卡上当前应用版本的所有实例
		List<CardApplet> cardApplets = cardAppletManager.getByCardNoAndApplicationVersionThatCreateLoadFileVersion(
				localTransaction.getCardNo(), applicationVersion, loadFileVersion);

		Applet deleteApplet = null;
		for (CardApplet cardApplet : cardApplets) {// 卡上当前应用版本的每一个实例
			Applet applet = cardApplet.getApplet();
			if (applet.getLoadModule().getLoadFileVersion().equals(loadFileVersion)) {// 如果实例来自于当前的加载文件
				deleteApplet = applet;// 将当前实例指定为待删除的实例
				break;// 跳出循环
			}
		}

		return deleteApplet;
	}

	/**
	 * 获取删除顺序
	 * 
	 * @param localTransaction
	 * @return
	 */
	public List<ApplicationLoadFile> getDeleteOrder(LocalTransaction localTransaction) {
		ApplicationVersion applicationVersion = applicationVersionManager.getAidAndVersionNo(localTransaction.getAid(),
				localTransaction.getAppVersion());
		List<ApplicationLoadFile> deleteOrder = getDeleteOrder(applicationVersion);
		return deleteOrder;
	}

	/**
	 * 获取指定应用版本的删除顺序
	 * 
	 * @param applicationVersion
	 *            指定的应用版本
	 * @return 删除顺序
	 */
	public List<ApplicationLoadFile> getDeleteOrder(ApplicationVersion applicationVersion) {
		return applicationLoadFileManager.getAllByDeleteOrder(applicationVersion);
	}

	/**
	 * 当前加载文件是否有待删除的实例
	 * 
	 * @param localTransaction
	 * @return true-有待删除装的实例<br/>
	 *         false-没有待删除的实例
	 */
	public boolean hasAppletToDeleteFromCurrentLoadFile(LocalTransaction localTransaction) {
		return null != getNextAppletToDeleltFromCurrentLoadFile(localTransaction);
	}

	public LoadFileVersion getCurrentLoadFileVersionToDelete(LocalTransaction localTransaction) {
		List<ApplicationLoadFile> deleteOrder = getDeleteOrder(localTransaction);
		return deleteOrder.get(localTransaction.getCurrentLoadFileIndex() - 1).getLoadFileVersion();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 卡上是否已创建安全域？”安全域已创建“是指安全域在卡上已经安装且状态为”已个人化“
	 * 
	 * @param card
	 *            卡
	 * @param sd
	 *            安全域
	 * @return true-卡上已创建安全域<br/>
	 *         false-卡上未创建安全域
	 */
	public boolean isSecurityDomainExistOnCard(CardInfo card, SecurityDomain sd) {
		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(card.getCardNo(), sd.getAid());
		if ((null != cardSecurityDomain) && (CardSecurityDomain.STATUS_PERSO == cardSecurityDomain.getStatus().intValue())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 安全域是否允许自动创建？
	 * 
	 * @param sd
	 *            安全域
	 * @return true-安全域允许自动创建<br/>
	 *         false-安全域不允许自动创建
	 */
	public boolean canSdAutoCreate(SecurityDomain sd) {
		return SecurityDomain.MODEL_COMMON == sd.getModel().intValue();
	}

	public String getNextSeqNum() {
		try {
			return sequenceDao.getNextSerialNoWithTime("TRANS_SEQNUM_ID");
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR);
		}
	}

	public String getNextSessionId() {
		try {
			return sequenceDao.getNextSerialNoWithTime("TRANS_SESSION_ID");
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR);
		}
	}

	public boolean isSdLocked(SecurityDomain sd, CardInfo card) {
		boolean isLocked = false;

		CardSecurityDomain cardSecurityDomain = cardSecurityDomainManager.getByCardNoAid(card.getCardNo(), sd.getAid());
		if ((null != cardSecurityDomain) && (CardSecurityDomain.STATUS_LOCK == cardSecurityDomain.getStatus().intValue())) {
			isLocked = true;
		}

		return isLocked;
	}

	public boolean isPreset(CardInfo card, SecurityDomain sd) {
		CardBaseSecurityDomain cardBaseSecurityDomain = cardBaseSecurityDomainManager.getBySdAndCardBaseId(sd, card.getCardBaseInfo());
		return (null != cardBaseSecurityDomain.getPreset()) && (CardBaseSecurityDomain.PRESET == cardBaseSecurityDomain.getPreset());
	}

	/**
	 * 卡上安全域是否能够被删除？<br/>
	 * 预置安全域不能被删除<br/>
	 * 删除规则为“不能删除”的安全域不能被删除
	 * 
	 * @param card
	 *            卡
	 * @param securityDomain
	 *            安全域
	 * @return true-可以删除<br/>
	 *         false-不能删除
	 */
	public boolean isDeletable(CardInfo card, SecurityDomain securityDomain) {
		if ((SecurityDomain.CANNOT_DELETE == securityDomain.getDeleteRule().intValue()) || isPreset(card, securityDomain)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 结束一个失败的流程
	 * 
	 * @param localTransaction
	 *            失败的流程
	 */
	public void endFaildTrans(LocalTransaction localTransaction) {
		// 结束当前流程
		localTransaction.setExecutionStatus(ExecutionStatus.COMPLETED.getStatus());// 将当前流程及其父流程的执行状态设为“已执行”
		localTransaction.setEndTime(Calendar.getInstance());

		LocalTransaction superTransaction = localTransaction.getSuperTransaction();
		if (null != superTransaction) {
			superTransaction.setFailMessage(localTransaction.getFailMessage());
			superTransaction.setResult(localTransaction.getResult());
			superTransaction.setSessionStatus(SessionStatus.TERMINATE);
			endFaildTrans(superTransaction);
		}
		localTransactionManager.saveOrUpdate(localTransaction);
	}

	public int getDeleteRule(CardInfo card, Application application) {
		CardBaseApplication cardBaseApplication = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(card.getCardBaseInfo(),
				application);
		if (null != cardBaseApplication) {
			if (CardBaseApplication.MODE_CREATE == cardBaseApplication.getPresetMode().intValue()) {
				return Application.DELETE_RULE_DELETE_DATA_ONLY;
			} else if (CardBaseApplication.MODE_PERSONAL == cardBaseApplication.getPresetMode().intValue()) {
				return Application.DELETE_RULE_CAN_NOT;
			} else {
				return Application.DELETE_RULE_DELETE_ALL;
			}
		} else {
			return application.getDeleteRule();
		}
	}

	/**
	 * 完成创建个人化子流程的父/子流程数据处理
	 * 
	 * @param localTransaction
	 *            父流程
	 * @param subTransaction
	 *            子流程
	 */
	public void buildSubPersonalizedAppTransaction(LocalTransaction localTransaction, LocalTransaction subTransaction) {
		subTransaction.addPersonalizations(localTransaction.getCurrentPersonalizations());
		subTransaction.setPersonalType(localTransaction.getPersonalType());
		localTransaction.increaseCurrentPersonlizationIndex();
	}
}
