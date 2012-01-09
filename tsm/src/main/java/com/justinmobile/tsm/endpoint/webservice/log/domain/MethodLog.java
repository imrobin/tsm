package com.justinmobile.tsm.endpoint.webservice.log.domain;

import java.util.Calendar;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Entity
@Table(name = "METHOD_LOG")
public class MethodLog extends AbstractEntity {
	
	private static final long serialVersionUID = 8913178385687050096L;

	private Long id;
	
	/** 目标接口名 */
	private String targetName;
	
	/** 目标方法名 */
	private String methodName;
	
	/** 会话ID  */
	private String sessionId;
	
	/** 序号 */
	private String seqNum;
	
	/** 指令名称 */
	private String apduName;
	
	/** 应用 */
	private Application application;

	/** 用户 */
	private CustomerCardInfo customerCardInfo;
	
	/** 卡片响应结果 */
	private String cardResult;
	
	/** 执行开始时间 */
	@DateFormat
	private Calendar startTime;
	
	/** 执行结束时间 */
	@DateFormat
	private Calendar endTime;
	
	/** 传入参数 */
	private String params;
	
	/** 返回结果 */
	private String result;

	@ManyToOne
	@JoinColumn(name = "APP_ID")
	@Cascade({CascadeType.PERSIST, CascadeType.MERGE})
	@LazyToOne(LazyToOneOption.PROXY)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_CARD_ID")
	@Cascade({CascadeType.PERSIST, CascadeType.MERGE})
	@LazyToOne(LazyToOneOption.PROXY)
	public CustomerCardInfo getCustomerCardInfo() {
		return customerCardInfo;
	}

	public void setCustomerCardInfo(CustomerCardInfo customerCardInfo) {
		this.customerCardInfo = customerCardInfo;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_METHOD_LOG") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}

	public String getApduName() {
		return apduName;
	}

	public void setApduName(String apduName) {
		this.apduName = apduName;
	}

	public String getCardResult() {
		return cardResult;
	}

	public void setCardResult(String cardResult) {
		this.cardResult = cardResult;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	@Lob
	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	@Lob
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public <T> T convertParams(String rootName, Class<T> clazz) {
		return convert(this.getParams(), rootName, clazz);
	}
	
	public <T> T convertResult(String rootName, Class<T> clazz) {
		return convert(this.getResult(), rootName, clazz);
	}

	@SuppressWarnings("unchecked")
	private <T> T convert(String src, String rootName, Class<T> clazz) {
		if (StringUtils.isBlank(src)) {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JsonBinder jsonBinder = JsonBinder.buildNormalBinder();
		String entityJson = null;
		if (StringUtils.isBlank(rootName)) {
			entityJson = src;
		} else {
			Map<String, Object> map = jsonBinder.fromJson(src, Map.class);
			Map<String, Object> enityMap = (Map<String, Object>) map.get(rootName);
			entityJson = jsonBinder.toJson(enityMap);
		}
		return jsonBinder.fromJson(entityJson, clazz);
	}

}
