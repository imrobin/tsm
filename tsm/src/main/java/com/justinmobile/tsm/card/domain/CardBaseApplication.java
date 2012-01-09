package com.justinmobile.tsm.card.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.application.domain.ApplicationVersion;

@Entity
@Table(name = "CARD_BASE_APPLICATION")
public class CardBaseApplication extends AbstractEntity {

	private static final long serialVersionUID = 85486703L;

	/** 空卡模式 */
	public static final int MODE_EMPTY = 1;
	/** 实例创建模式 */
	public static final int MODE_CREATE = 2;
	/** 个人化模式 */
	public static final int MODE_PERSONAL = 3;

	/** 主键 */
	private Long id;

	/** 卡批次 */
	private CardBaseInfo cardBase;

	/** 应用版本信息 */
	private ApplicationVersion applicationVersion;

	/**
	 * 是否预置 private Boolean preset;
	 */

	/** 预制类型 */
	private Integer presetMode;

	/**
	 * 用来显示
	 */
	@ResourcesFormat(key = "common.ispreset")
	private Integer intPrest;

	public Integer getPresetMode() {
		return presetMode;
	}

	public void setPresetMode(Integer presetMode) {
		this.presetMode = presetMode;
	}

	/*
	 * public void setPreset(Boolean preset) { this.preset = preset; }
	 */

	public void setIntPrest(Integer intPrest) {
		this.intPrest = intPrest;
	}

	@Transient
	public Integer getIntPrest() {
		return intPrest;
	}
  
	@ManyToOne
	@JoinColumn(name = "CARD_BASE_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public CardBaseInfo getCardBase() {
		return cardBase;
	}

	public void setCardBase(CardBaseInfo cardBase) {
		this.cardBase = cardBase;
	}

	@ManyToOne
	@JoinColumn(name = "APPLICATION_VERSION_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public ApplicationVersion getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(ApplicationVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_BASE_APPLICATION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public Boolean getPreset() {
		if (null == this.presetMode || MODE_EMPTY == this.presetMode.intValue()) {
			return false;
		} else {
			return true;
		}
	}

	@Transient
	public boolean isPreset() {
		return getPreset();
	}
}