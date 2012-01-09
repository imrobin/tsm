package com.justinmobile.tsm.card.domain;

import javax.persistence.Entity;
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
import com.justinmobile.tsm.application.domain.SecurityDomain;

@Entity
@Table(name = "CARD_BASE_SECURITY_DOMAIN")
public class CardBaseSecurityDomain extends AbstractEntity {

	private static final long serialVersionUID = -6520331761286781400L;

	public static final int PRESET = 1;

	public static final int UNPRESET = 0;

	public static final int PRESET_MODE_CARETE = CardSecurityDomain.STATUS_CREATED;

	public static final int PRESET_MODE_PERSONALIZED = CardSecurityDomain.STATUS_PERSO;
	
	private Long id;

	/** 是否是预置 */
	@ResourcesFormat(key = "common.ispreset")
	private Integer preset;

	private CardBaseInfo cardBaseInfo;

	private SecurityDomain securityDomain;

	private Integer presetKeyVersion;

	@ResourcesFormat(key = "cardBaseSecurityDomain.mode")
	private Integer presetMode;

	@ManyToOne
	@JoinColumn(name = "SD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public SecurityDomain getSecurityDomain() {
		return securityDomain;
	}

	public void setSecurityDomain(SecurityDomain securityDomain) {
		this.securityDomain = securityDomain;
	}

	@ManyToOne
	@JoinColumn(name = "CARD_BASE_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public CardBaseInfo getCardBaseInfo() {
		return cardBaseInfo;
	}

	public void setCardBaseInfo(CardBaseInfo cardBaseInfo) {
		this.cardBaseInfo = cardBaseInfo;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_BASE_SECURITY_DOMAIN") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPreset() {
		return preset;
	}

	public void setPreset(Integer preset) {
		this.preset = preset;
	}

	public Integer getPresetMode() {
		return presetMode;
	}

	public void setPresetMode(Integer presetMode) {
		this.presetMode = presetMode;
	}

	public Integer getPresetKeyVersion() {
		return presetKeyVersion;
	}

	public void setPresetKeyVersion(Integer presetKeyVersion) {
		this.presetKeyVersion = presetKeyVersion;
	}

}
