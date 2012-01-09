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
@Table(name = "FEE_RULE_FUNCTION")
public class FeeRuleFunction extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 292040159135892729L;
	/** 主键 */
	private Long id;
	/** 应用提供商 */
	private SpBaseInfo sp;

	/** 计费类型按次或者包月 */
	@ResourcesFormat(key = "feeRuleFunction.pattern")
	private Integer pattern;

	private Integer granularity;

	/** 按次计费 */
	public static final int PATTERN_PER = 1;
	/** 包月计费 */
	public static final int PATTERN_MONTH = 2;

	private Integer price;
	/** 显示价格 */
	@DecimalNumberFormat
	private double uiPrice;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_FEE_RULE_FUNCTION") })
	public Long getId() {
		return id;
	}

	public void setId(long id) {
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

	public Integer getGranularity() {
		return granularity;
	}

	public void setGranularity(Integer granularity) {
		this.granularity = granularity;
	}

	@Transient
	public double getUiPrice() {
		return uiPrice;
	}

}
