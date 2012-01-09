package com.justinmobile.tsm.fee.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;

@Entity
@Table(name = "FEE_STAT")
public class FeeStat extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4658271797039536370L;
	/** 主键 */
	private Long id;
	/** 商户ID */
	private Long spId;
	/** 商户名称 */
	private String spName;
	/** 会话ID */
	private String sessionId;
	/** 计费模式 */
	private Integer feeType;
	/** 空间计费模式 */
	public static final int TYPE_SPACE = 2;
	/** 功能计费模式 */
	public static final int TYPE_FUNCTION = 1;

	public static final String APP = "app";

	public static final String SD = "sd";
	/** 卡号 */
	private String cardNo;
	/** 手机号 */
	private String mobileNo;
	/** 应用或者安全域AID */
	private String aid;
	/** 应用或安全域名称 */
	private String appName;
	/** 版本号 */
	private String version;
	/** 操作名称 */
	private String operateName;
	/** 操作时间 */
	@DateFormat
	private Date operateTime;
	/** 计费价格 */
	private Double price;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_FEE_STAT") })
	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSpId() {
		return spId;
	}

	public void setSpId(long spId) {
		this.spId = spId;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public String getCardNo() {
		return cardNo;
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

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
