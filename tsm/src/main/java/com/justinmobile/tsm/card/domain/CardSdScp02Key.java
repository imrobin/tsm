package com.justinmobile.tsm.card.domain;

import java.util.Calendar;

import javax.persistence.Column;
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
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

@Entity
@Table(name = "CARD_SD_SCP02_KEY")
public class CardSdScp02Key extends AbstractEntity {

	private static final long serialVersionUID = 378749342L;

	public static final int VALID_STATUS = 1;

	public static final int INVALID_STATUS = 0;

	private Long id;

	private CardSecurityDomain cardSecurityDomain;

	private KeyProfile keyProfile;
	/** 1 有效，0 无效 */
	private Integer status;
	/** 密钥更新时间 */
	private Calendar generateTime;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_SD_SCP02_KEY") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CARD_SD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public CardSecurityDomain getCardSecurityDomain() {
		return cardSecurityDomain;
	}

	public void setCardSecurityDomain(CardSecurityDomain cardSecurityDomain) {
		this.cardSecurityDomain = cardSecurityDomain;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KEY_PROFILE_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public KeyProfile getKeyProfile() {
		return keyProfile;
	}

	public void setKeyProfile(KeyProfile keyProfile) {
		this.keyProfile = keyProfile;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "GENERATETIME")
	public Calendar getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(Calendar generateTime) {
		this.generateTime = generateTime;
	}

}