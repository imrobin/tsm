package com.justinmobile.tsm.transaction.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.customer.domain.Customer;

/**
 * @author WLQ
 * 
 */
@Entity
@Table(name = "DESIRED_OPERATION")
public class DesiredOperation extends AbstractEntity {

	private static final long serialVersionUID = 457336845L;

	/** 执行状态：0-未执行 */
	public static final int NOT_EXCUTED = 0;
	/** 执行状态：1-已执行 */
	public static final int EXCUTED = 1;
	/** 执行状态：2-已完成 */
	public static final int FINISH_EXCUTED = 2;
	/** 执行状态：3-未完成 */
	public static final int NOT_FINISH_EXCUTED = 3;

	/** 提示状态：0-未提示 */
	public static final int NOT_PROMPTED = 0;
	/** 提示状态：1-已提示 */
	public static final int PROMPTED = 1;

	/** 执行方式：0-提示执行 */
	public static final int PREPROCESS_FALSE = 0;
	/** 执行方式：1-强制执行 */
	public static final int PREPROCESS_TURE = 1;

	/** 主键 */
	private Long id;

	/** 操作名称 */
	@ResourcesFormat(key = "desiredoperation.procedureName")
	private String procedureName;

	/** 可以得到用户信息和卡片信息 */
	private Long customerCardId;

	/** 操作的应用aid */
	private String aid;

	/** task主键 */
	private Long taskId;

	/** 会话ID */
	private String sessionId;

	/** 0-未执行 1-执行中 2-已完成 3-未完成 */
	private Integer isExcuted = NOT_EXCUTED;

	/** 0-未提醒 1-已提醒 */
	private Integer isPrompt;

	/** 结果信息 */
	private String result;

	/**
	 * 预处理:1后台自动强行执行0:用户正常处理
	 */
	private Integer preProcess;

	private Customer customer;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_DESIRED_OPERATION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Long getCustomerCardId() {
		return customerCardId;
	}

	public void setCustomerCardId(Long customerCardId) {
		this.customerCardId = customerCardId;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getIsExcuted() {
		return isExcuted;
	}

	public void setIsExcuted(Integer isExcuted) {
		this.isExcuted = isExcuted;
	}

	public Integer getIsPrompt() {
		return isPrompt;
	}

	public void setIsPrompt(Integer isPrompt) {
		this.isPrompt = isPrompt;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getPreProcess() {
		return preProcess;
	}

	public void setPreProcess(Integer preProcess) {
		this.preProcess = preProcess;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}