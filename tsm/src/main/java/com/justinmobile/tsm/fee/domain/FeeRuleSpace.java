package com.justinmobile.tsm.fee.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DecimalNumberFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Entity
@Table(name = "FEE_RULE_SPACE")
public class FeeRuleSpace extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7496823590163430436L;
	/** 主键 */
	private Long id;
	/** 所属SP */
	private SpBaseInfo sp;
	/** AID */
	private String aid;
	/**应用或安全域名称 */
	private String appName;
	/** 价格模式 */
	@ResourcesFormat(key = "feeRuleSpace.pattern")
	private Integer pattern;
	/** 直接定价模式 */
	public static final int PATTERN_APP = 1;
	/** 空间大小定价模式 */
	public static final int PATTERN_SAPCE = 2;
	@ResourcesFormat(key = "feeRuleSpace.type")
	private Integer type;
	/**应用*/
	public static final int TYPE_APP = 1;
	/**安全域*/
	public static final int TYPE_SD = 2;
	/** 计费单位粒度 */
	//@ResourcesFormat(key = "feeRuleSpace.granularity")
	private Integer granularity;
	/** 空间单价 */
	private Integer price;
	/** 显示单价(单位元) */
	@DecimalNumberFormat
	private double uiPrice;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_FEE_RULE_SPACE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@JoinColumn(name = "SP_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SpBaseInfo getSp() {
		return sp;
	}

	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}

	public Integer getPrice() {
		return price;
	}

	public Integer getPattern() {
		return pattern;
	}

	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}

	public void setPrice(Integer price) {
		this.price = price;
		this.uiPrice = (double) (price) / 100;
	}

	@Transient
	public double getUiPrice() {
		return uiPrice;
	}

	public Integer getGranularity() {
		return granularity;
	}

	public void setGranularity(Integer granularity) {
		this.granularity = granularity;
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

	public Integer getType() {
		return type;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
    
}
