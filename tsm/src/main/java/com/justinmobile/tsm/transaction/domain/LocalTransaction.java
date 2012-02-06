package com.justinmobile.tsm.transaction.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.type.EnumType;

import com.justinmobile.core.dao.support.EnumUserType;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.tsm.application.domain.Application.PersonalType;
import com.justinmobile.tsm.cms2ac.SessionStatus;
import com.justinmobile.tsm.cms2ac.domain.Cms2acParam;
import com.justinmobile.tsm.cms2ac.domain.Task;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;

@Entity
@Table(name = "LOCAL_TRANSACTION")
public class LocalTransaction extends AbstractEntity {

	/** 执行状态：待执行 */
	public static final int STATUS_EXECUTION_EXEUTORY = 1;

	/** 执行状态：执行中 */
	public static final int STATUS_EXECUTION_EXEUTING = 2;

	/** 执行状态：已执行 */
	public static final int STATUS_EXECUTION_EXEUTED = 3;

	private static final long serialVersionUID = -942170477L;

	private Long id;

	/** 所属任务 */
	private Task task;

	/** 卡号 */
	private String cardNo;

	/** 原来的卡号，用于应用迁入操作 */
	private String originalCardNo;

	/** 手机号 */
	private String mobileNo;

	/** 正在操作的应用AID */
	private String aid;

	/** 要操作的文件执行顺序 */
	private Integer currentLoadFileIndex = 1;

	/** 要操作的实例执行顺序 */
	private Long currentAppletIndex = 1L;

	/** 应用版本 */
	private String appVersion;

	/**
	 * 承载方式<br/>
	 * 1:个人版PC客户端<br/>
	 * 2:专用版PC客户端<br/>
	 * 3:营业厅专用POS机<br/>
	 * 4:手机
	 */
	@ResourcesFormat(key = "lt.commType")
	private Integer commType;

	/** 会话ID */
	private String localSessionId;

	/** 流程名称 */
	@ResourcesFormat(key = "desiredoperation.procedureName")
	private String procedureName;

	/** 当前执行顺序号 */
	private Integer executeNo;

	/** 失败原因 */
	private String failMessage;

	/** 执行开始时间 */
	@DateFormat
	private Calendar beginTime = Calendar.getInstance();

	/** 执行结束时间 */
	@DateFormat
	private Calendar endTime;

	/** 会话状态 */
	private Integer sessionStatus = SessionStatus.INIT;

	/** 执行结果 */
	private String result;

	/** Cms2acParam */
	private List<Cms2acParam> cms2acParams = new ArrayList<Cms2acParam>();

	/** 应用的个人化数据 */
	private String fileContent;

	/**
	 * 执行状态<br/>
	 * 1-待执行<br/>
	 * 2-执行中<br/>
	 * 3-已执行
	 * */
	private Integer executionStatus = STATUS_EXECUTION_EXEUTORY;

	/** 当前流程的子流程 */
	private List<LocalTransaction> subTransactions = new ArrayList<LocalTransaction>();

	/** 当前流程的父流程 */
	private LocalTransaction superTransaction;

	/** 原来的应用版本，用于应用升级时记录升级前的版本 */
	private String originalAppVersion;

	/** 当前子流程的索引 */
	private Integer currentSubTransactionIndex = 0;

	/** 最大顺序值 */
	private Integer maxOrder;

	/** 业务平台会话ID */
	private String providerSessionId;

	/** 是否还有后续操作 */
	private Boolean hasContinusOpt;

	/** 卡操作结果，非持久化字段 */
	private CardPOR cardPOR;

	/**
	 * 会话类型<br/>
	 * 1-业务订购<br/>
	 * 2-业务退订<br/>
	 * 3-业务更新<br/>
	 * 4-业务迁移<br/>
	 * 5-业务锁定<br/>
	 * 6-业务解锁<br/>
	 * 7-安全域管理
	 */
	private Integer sessionType;

	private PersonalType personalType;

	private List<Personalizations> personalizations = new ArrayList<Personalizations>();

	/**
	 * 当前正在执行的个人化数据索引，最小值为0<br/>
	 * 如果currentPersonlizationIndex ==
	 * personlizations.size()，说明所有个人化指令都已经执行完成，流程可以进行后续步骤<br/>
	 * 如果currentPersonlizationIndex >
	 * personlizations.size()，说明currentPersonlizationIndex取值错误<br/>
	 * 如果currentPersonlizationIndex < personlizations.size()，说明有个人化指令都需要执行
	 */
	private Integer currentPersonlizationIndex = 0;

	public enum CommType {
		GPC(1), APC(2), POS(3), ME(4);

		private int type;

		CommType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static String getNameByType(int type) {
			CommType[] commTypes = CommType.values();
			for (CommType commType : commTypes) {
				if (commType.getType() == type) {
					return commType.name();
				}
			}
			return null;
		}

	}

	public enum ExecutionStatus {
		EXECUTORY(1), RUNNING(2), COMPLETED(3);

		private int status;

		ExecutionStatus(int status) {
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

	}

	public enum Operation {
		/** 创建安全域 */
		CREATE_SD(1, "mocamCreateSdProcessor", "100103", SessionType.SD_CREATE),
		/** 删除安全域 */
		DELETE_SD(2, "mocamDeleteSdProcessor", "100104", SessionType.SD_DELETE),
		/** 下载应用 */
		DOWNLOAD_APP(3, "mocamDownloadAppProcessor", "100101", SessionType.SERVICE_SUBSCRIBE),
		/** 删除应用 */
		DELETE_APP(4, "deleteAppProcessor", "100102", SessionType.SERVICE_UNSUBSCRIBE),
		/** 锁定应用 */
		LOCK_APP(5, "lockAppProcessor", "100106", SessionType.SERVICE_LOCK),
		/** 解锁应用 */
		UNLOCK_APP(6, "unlockAppProcessor", "100107", SessionType.SERVICE_UNLOCK),
		/** 升级安全域密钥 */
		UPDATE_KEY(7, "mocamUpdateKeyProcessor", "100105", SessionType.SD_CREATE),
		/** 管理应用个人化数据 */
		PERSONALIZE_APP(8, "personalizeAppProcessor", "100115", SessionType.PERSO_DATA_MANAGE),
		/** 锁定安全域 */
		LOCK_SD(9, "lockSdProcessor", "100109", SessionType.OTHER),
		/** 同步卡数据 */
		SYNC_CARD_SD(10, "syncCardSdProcessor", "", SessionType.OTHER),
		/** 解锁安全区域 */
		UNLOCK_SD(11, "unlockSdProcessor", "100110", SessionType.OTHER),
		/** 锁定卡 */
		LOCK_CARD(12, "lockCardProcessor", "100109", SessionType.OTHER),
		/** 解锁卡 */
		UNLOCK_CARD(13, "unlockCardProcessor", "", SessionType.OTHER),
		/** 迁出应用 */
		EMIGRATE_APP(14, "emigrateAppProcessor", "100111", SessionType.SERVICE_MIGRATE),
		/** 迁入应用 */
		IMMIGRATE_APP(15, "immigrateAppProcessor", "100112", SessionType.SERVICE_MIGRATE),
		/** 升级应用 */
		UPDATE_APP(16, "updateAppProcessor", "100108", SessionType.SERVICE_UPDATE),
		/** 升级时删除应用 */
		UPDATE_DELETE_APP(17, "updateDeleteAppProcessor", "", SessionType.SERVICE_UPDATE),
		/** 升级时下载应用 */
		UPDATE_DOWNLOAD_APP(18, "mocamDownloadAppProcessor", "", SessionType.SERVICE_UPDATE),
		/** 删除实例 */
		DELETE_APPLET(19, "deleteAppletProcessor", "", SessionType.OTHER),
		/** 删除文件 */
		DELETE_LOAD_FILE(20, "deleteLoadFileProcessor", "", SessionType.OTHER),
		/** 删除应用时获取个人化数据 */
		DELETE_APP_READ_PERSO_DATA(21, "personalizeAppProcessor", "", SessionType.SERVICE_UNSUBSCRIBE),
		/** 更新应用时获取个人化数据 */
		UPDATE_APP_READ_PERSO_DATA(22, "personalizeAppProcessor", "", SessionType.SERVICE_UPDATE),
		/** 电子钱包客户端注册 */
		REGISTER(22, "registerProcessor", "100002", SessionType.OTHER),
		/** 电子钱包客户端登录 */
		LOGIN(23, "loginProcessor", "100001", SessionType.OTHER),
		/** 下载时应用个人化 */
		DOWNLOAD_PERSONALIZE_APP(24, "personalizeAppProcessor", "", SessionType.SERVICE_SUBSCRIBE),
		/** 更换手机号 */
		REPLACE_MOBILE_NO(25, "replaceMobileNoProcessor", "100116", SessionType.REPLACE_MOBILE_NO),
		/** 通知业务平台更换手机号 */
		NOTIFY_REPLACE_MOBILE_NO(26, "personalizeAppProcessor", "", SessionType.REPLACE_MOBILE_NO),
		/** 更新TOKEN  */
		CHANGE_TOKEN(27,"changeTokenProcessor","100008",SessionType.OTHER),
		/** 未知操作 */
		UNKNOWN(-1, "", "", SessionType.OTHER);

		private int type;

		private String beanName;

		private String commandId;

		private SessionType sessionType;

		public static Set<Operation> CARD_OPERATIONS = new HashSet<Operation>();
		static {
			CARD_OPERATIONS.add(REGISTER);
			CARD_OPERATIONS.add(LOGIN);
			CARD_OPERATIONS.add(CHANGE_TOKEN);
		}

		Operation(int type, String beanName, String commandId, SessionType sessionType) {
			this.type = type;
			this.beanName = beanName;
			this.commandId = commandId;
			this.sessionType = sessionType;
		}

		public int getType() {
			return type;
		}

		public String getBeanName() {
			return beanName;
		}

		public String getCommandId() {
			return commandId;
		}

		public SessionType getSessionType() {
			return sessionType;
		}

		public static String valueOf(int type) {
			Operation[] operations = Operation.values();
			for (Operation operation : operations) {
				if (operation.getType() == type) {
					return operation.name();
				}
				if (operation.getCommandId().equals(String.valueOf(type))) {
					return operation.name();
				}
			}
			return null;
		}
	}

	public enum SessionType {
		/** 业务订购 */
		SERVICE_SUBSCRIBE(1, "业务订购"),
		/** 业务退订 */
		SERVICE_UNSUBSCRIBE(2, "业务退订"),
		/** 业务升级 */
		SERVICE_UPDATE(3, "业务升级"),
		/** 业务迁移 */
		SERVICE_MIGRATE(4, "业务迁移"),
		/** 业务锁定 */
		SERVICE_LOCK(5, "业务锁定"),
		/** 业务解锁 */
		SERVICE_UNLOCK(6, "业务解锁"),
		/** 安全域创建 */
		SD_CREATE(7, "安全域管理"),
		/** 安全域删除 */
		SD_DELETE(8, "安全域管理"),
		/** 安全域密钥更新 */
		SD_KEY_UPDATE(9, "安全域管理"),
		/** 个人化数据管理 */
		PERSO_DATA_MANAGE(10, "个人化数据管理"),
		/** BOSS换号 */
		REPLACE_MOBILE_NO(11, "BOSS换号"),
		/** 其他会话类型 */
		OTHER(0, "其他");

		public static final Set<SessionType> SESSION_TYPE_APPLICATION = new HashSet<SessionType>();
		static {
			SESSION_TYPE_APPLICATION.add(SERVICE_SUBSCRIBE);
			SESSION_TYPE_APPLICATION.add(SERVICE_UNSUBSCRIBE);
			SESSION_TYPE_APPLICATION.add(SERVICE_UPDATE);
			SESSION_TYPE_APPLICATION.add(SERVICE_MIGRATE);
			SESSION_TYPE_APPLICATION.add(SERVICE_LOCK);
			SESSION_TYPE_APPLICATION.add(SERVICE_UNLOCK);
			SESSION_TYPE_APPLICATION.add(PERSO_DATA_MANAGE);
			SESSION_TYPE_APPLICATION.add(REPLACE_MOBILE_NO);
		}

		public static final Set<SessionType> SESSION_TYPE_SECURITY_DOMAIN = new HashSet<SessionType>();
		static {
			SESSION_TYPE_SECURITY_DOMAIN.add(SD_CREATE);
			SESSION_TYPE_SECURITY_DOMAIN.add(SD_DELETE);
			SESSION_TYPE_SECURITY_DOMAIN.add(SD_KEY_UPDATE);
		}

		private int value;

		private String description;

		SessionType(int value, String description) {
			this.value = value;
			this.description = description;
		}

		public int getValue() {
			return this.value;
		}

		public String getDescription() {
			return this.description;
		}

		public static SessionType valueOf(int value) {
			for (SessionType sessionType : SessionType.values()) {
				if (value == sessionType.value) {
					return sessionType;
				}
			}
			return null;
		}
	}

	public Integer getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(Integer executionStatus) {
		this.executionStatus = executionStatus;
	}

	@ManyToOne
	@JoinColumn(name = "TRANSACTION_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	public LocalTransaction getSuperTransaction() {
		return superTransaction;
	}

	public void setSuperTransaction(LocalTransaction superTransaction) {
		this.superTransaction = superTransaction;
	}

	@OneToMany(mappedBy = "localTransaction")
	@Cascade(value = { CascadeType.ALL })
	@OrderBy(value = "id")
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<Cms2acParam> getCms2acParams() {
		return cms2acParams;
	}

	public void setCms2acParams(List<Cms2acParam> cms2acParams) {
		this.cms2acParams = cms2acParams;
	}

	@OneToMany(mappedBy = "localTransaction")
	@Cascade(value = { CascadeType.ALL })
	@OrderBy(value = "id")
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<Personalizations> getPersonalizations() {
		return personalizations;
	}

	public void setPersonalizations(List<Personalizations> personalizations) {
		this.personalizations = personalizations;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_LOCAL_TRANSACTION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "TASK_ID", referencedColumnName = "ID")
	@OrderBy(value = "id")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getCardNo() {
		return cardNo;
	}

	public String getOriginalCardNo() {
		return originalCardNo;
	}

	public void setOriginalCardNo(String originalCardNo) {
		this.originalCardNo = originalCardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public Integer getCurrentLoadFileIndex() {
		return currentLoadFileIndex;
	}

	public void setCurrentLoadFileIndex(Integer currentLoadFileIndex) {
		this.currentLoadFileIndex = currentLoadFileIndex;
	}

	public Long getCurrentAppletIndex() {
		return currentAppletIndex;
	}

	public void setCurrentAppletIndex(Long currentAppletIndex) {
		this.currentAppletIndex = currentAppletIndex;
	}

	public Integer getCommType() {
		return commType;
	}

	public void setCommType(Integer commType) {
		this.commType = commType;
	}

	public String getLocalSessionId() {
		return localSessionId;
	}

	public void setLocalSessionId(String localSessionId) {
		this.localSessionId = localSessionId;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Integer getExecuteNo() {
		return executeNo;
	}

	public void setExecuteNo(Integer executeNo) {
		this.executeNo = executeNo;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}

	public Calendar getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Calendar beginTime) {
		this.beginTime = beginTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public Integer getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(Integer sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public Integer getSessionType() {
		return sessionType;
	}

	public void setSessionType(Integer sessionType) {
		this.sessionType = sessionType;
	}

	public void increaseExecuteNo() {
		if (this.executeNo == null) {
			this.executeNo = 0;
		}
		this.executeNo++;
	}

	@Transient
	public Cms2acParam getLastCms2acParam() {
		if (this.cms2acParams.isEmpty()) {
			return null;
		} else {
			return this.cms2acParams.get(this.cms2acParams.size() - 1);
		}
	}

	public void increaseCurrentAppletIndex() {
		this.currentAppletIndex++;
	}

	public void increaseCurrentLoadFileIndex() {
		this.currentLoadFileIndex++;
	}

	public void setResult(PlatformMessage message) {
		this.result = message.getCode();
	}

	@OneToMany
	@JoinColumn(name = "TRANSACTION_ID")
	@Cascade(CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<LocalTransaction> getSubTransactions() {
		return subTransactions;
	}

	public void setSubTransactions(List<LocalTransaction> subTransactions) {
		this.subTransactions = subTransactions;
	}

	/**
	 * 添加一个Cms2acParam，建立双向关联
	 * 
	 * @param cms2acParam
	 */
	public void addCms2acParam(Cms2acParam cms2acParam) {
		this.cms2acParams.add(cms2acParam);
		cms2acParam.setLocalTransaction(this);
	}

	/**
	 * 获取当前流程中的最后一个子流程
	 * 
	 * @return 当前流程中的最后一个子流程<br/>
	 *         null-如果当前流程没有子流程
	 */
	@Transient
	public LocalTransaction getLastSubTransaction() {
		LocalTransaction lastSubTransaction = null;

		if (0 != this.subTransactions.size()) {
			lastSubTransaction = subTransactions.get(subTransactions.size() - 1);
		}

		return lastSubTransaction;
	}

	/**
	 * 为当前流程添加子流程
	 * 
	 * @param subTransaction
	 *            子流程
	 */
	public void addSubTransaction(LocalTransaction subTransaction) {
		this.subTransactions.add(subTransaction);
		subTransaction.setSuperTransaction(this);
	}

	/**
	 * 获取需要执行的子流程
	 * 
	 * @return 需要执行的子流程<br/>
	 *         null-如果没有需要执行的子流程
	 */
	@Transient
	public LocalTransaction getSubTransactionToExcute() {
		return subTransactions.get(currentSubTransactionIndex);
	}

	/**
	 * 当前流程是否执行完成？
	 * 
	 * @return true-当前流程已经执行完成<br/>
	 *         false-当前流程还未执行完成
	 */
	@Transient
	public boolean isComplete() {
		return STATUS_EXECUTION_EXEUTED == this.executionStatus.intValue();

	}

	/**
	 * 当前流程是否有需要执行的子流程？
	 * 
	 * @return true-有需要执行的子流程<br/>
	 *         false-没有需要执行的子流程
	 */
	public boolean hasSubTransactionToExcute() {
		return currentSubTransactionIndex < subTransactions.size();
	}

	/**
	 * 将流程及其子流程的执行状态设为“已执行”
	 */
	public void setExecutionStatusAsCompeleted() {
		executionStatus = ExecutionStatus.COMPLETED.status;
		endTime = Calendar.getInstance();
		for (LocalTransaction subTransaction : subTransactions) {
			subTransaction.setExecutionStatusAsCompeleted();
		}
	}

	public void setSessionType(SessionType sessionType) {
		this.sessionType = sessionType.getValue();
	}

	/**
	 * 获取当前流程所属的主流程
	 * 
	 * @return 当前流程所属的主流程
	 */
	@Transient
	public LocalTransaction getMainTransaction() {
		if (null != this.superTransaction) {// 如果当前流程有父流程，返回父流程所属主流程
			return this.superTransaction.getMainTransaction();
		} else {// 否则返回当前流程
			return this;
		}
	}

	public String getOriginalAppVersion() {
		return originalAppVersion;
	}

	public void setOriginalAppVersion(String originalAppVersion) {
		this.originalAppVersion = originalAppVersion;
	}

	public Integer getCurrentSubTransactionIndex() {
		return currentSubTransactionIndex;
	}

	public void setCurrentSubTransactionIndex(Integer currentSubTransactionIndex) {
		this.currentSubTransactionIndex = currentSubTransactionIndex;
	}

	public void increaseCurrentSubTransactionIndex() {
		currentSubTransactionIndex++;
	}

	public Integer getMaxOrder() {
		return maxOrder;
	}

	public void setMaxOrder(Integer maxOrder) {
		this.maxOrder = maxOrder;
	}

	public String getProviderSessionId() {
		return providerSessionId;
	}

	public void setProviderSessionId(String providerSessionId) {
		this.providerSessionId = providerSessionId;
	}

	/**
	 * 当前流程是否是主流程
	 * 
	 * @return true-是主流程<br/>
	 *         false-不是主流程
	 */
	@Transient
	public boolean isMainTransaction() {
		// 主流程必然属于某个任务，因此主流程的task不为空
		return null != task;
	}

	public Boolean getHasContinusOpt() {
		return hasContinusOpt;
	}

	public void setHasContinusOpt(Boolean hasContinusOpt) {
		this.hasContinusOpt = hasContinusOpt;
	}

	@Transient
	public CardPOR getCardPOR() {
		return cardPOR;
	}

	public void setCardPOR(CardPOR cardPOR) {
		this.cardPOR = cardPOR;
	}

	@Type(type = EnumUserType.NAME, parameters = @Parameter(name = EnumType.ENUM, value = PersonalType.NAME))
	public PersonalType getPersonalType() {
		return personalType;
	}

	public void setPersonalType(PersonalType personalType) {
		this.personalType = personalType;
	}

	public Integer getCurrentPersonlizationIndex() {
		return currentPersonlizationIndex;
	}

	public void setCurrentPersonlizationIndex(Integer currentPersonlizationIndex) {
		this.currentPersonlizationIndex = currentPersonlizationIndex;
	}

	/**
	 * 添加一个个人化指令列表，建立双向关联
	 * 
	 * @param personalizations
	 *            个人化指令列表
	 */
	public void addPersonalizations(Personalizations personalizations) {
		this.personalizations.add(personalizations);
		personalizations.setLocalTransaction(this);
	}

	/**
	 * 当前流程是否有待下发的个人化指令列表？
	 * 
	 * @return true-有个人化指令列表<br/>
	 *         false-没有个人化指令列表
	 */
	public boolean hasPersonalizationsToExecute() {
		if (currentPersonlizationIndex > personalizations.size()) {
			throw new IllegalArgumentException("currentPersonlizationIndex great than personalizations.size()");
		}
		return currentPersonlizationIndex < personalizations.size();
	}

	/**
	 * 获取当前待下发的的个人化指令列表
	 * 
	 * @return 当前待下发的的个人化指令列表<br/>
	 *         null-如果没有待下发的的个人化指令列表
	 */
	@Transient
	public Personalizations getCurrentPersonalizations() {
		if (hasPersonalizationsToExecute()) {
			return personalizations.get(currentPersonlizationIndex);
		} else {
			return null;
		}
	}

	public void increaseCurrentPersonlizationIndex() {
		currentPersonlizationIndex++;
	}
}