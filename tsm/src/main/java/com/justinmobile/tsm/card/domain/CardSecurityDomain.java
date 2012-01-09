package com.justinmobile.tsm.card.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;

@Entity
@Table(name = "CARD_SECURITY_DOMAIN")
public class CardSecurityDomain extends AbstractEntity {

	private static final long serialVersionUID = 268513140L;

	/** 未创建 */
	public static final int STATUS_UNCREATE = 1;

	/** 已创建 */
	public static final int STATUS_CREATED = 2;

	/** 密钥已更新 */
	public static final int STATUS_KEY_UPDATED = 3;

	/** 状态已个人化 */
	public static final int STATUS_PERSO = 4;

	public static final int STATUS_LOCK = 5;

	/** 可创建状态 */
	public static final Set<Integer> STATUS_CREATEABLE = new HashSet<Integer>();
	static {
		STATUS_CREATEABLE.add(STATUS_UNCREATE);
		STATUS_CREATEABLE.add(STATUS_CREATED);
		STATUS_CREATEABLE.add(STATUS_KEY_UPDATED);
	}

	/** 可删除状态 */
	public static final Set<Integer> STATUS_DELETEABLE = new HashSet<Integer>();
	static {
		STATUS_DELETEABLE.add(STATUS_CREATED);
		STATUS_DELETEABLE.add(STATUS_KEY_UPDATED);
		STATUS_DELETEABLE.add(STATUS_PERSO);
		STATUS_DELETEABLE.add(STATUS_LOCK);
	}

	/** 可更新密钥状态 */
	public static final Set<Integer> STATUS_UPDATABLE = new HashSet<Integer>();
	static {
		STATUS_UPDATABLE.add(STATUS_CREATED);
		STATUS_UPDATABLE.add(STATUS_KEY_UPDATED);
		STATUS_UPDATABLE.add(STATUS_PERSO);
	}

	/** 可锁定状态 */
	public static final Set<Integer> STATUS_LOCKABLE = new HashSet<Integer>();
	static {
		STATUS_LOCKABLE.add(STATUS_PERSO);
	}

	/** 可解锁状态 */
	public static final Set<Integer> STATUS_UNLOCKABLE = new HashSet<Integer>();
	static {
		STATUS_UNLOCKABLE.add(STATUS_LOCK);
	}

	private Long id;

	/** 卡片信息ID */
	private CardInfo card;

	/** 应用ID */
	private SecurityDomain sd;

	private Long freeNonVolatileSpace;

	private Integer freeVolatileSpace;
	/** 状态1:未创建、2:已创建、3:密钥已更新 4:已个人化 */
	private Integer status = STATUS_UNCREATE;

	private Integer scp02Counter;

	private Integer scp02SecurityLevel;

	private Integer orginalStatus;

	private Integer currentKeyVersion;

	/** 最后计费时间 */
	private Date lastFeeTime;

	/** 未知可变空间 */
	private Integer unknownVolatileSpace = 0;

	/** 未知不可变空间 */
	private Long unknownNoneVolatileSpace = 0L;

	private List<CardSdScp02Key> cardSdScp02Keys = new ArrayList<CardSdScp02Key>();

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_SECURITY_DOMAIN") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "CARD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public CardInfo getCard() {
		return card;
	}

	public void setCard(CardInfo card) {
		this.card = card;
	}

	@ManyToOne
	@JoinColumn(name = "SECURITY_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SecurityDomain getSd() {
		return sd;
	}

	public void setSd(SecurityDomain sd) {
		this.sd = sd;
	}

	@OneToMany(mappedBy = "cardSecurityDomain")
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<CardSdScp02Key> getCardSdScp02Keys() {
		return cardSdScp02Keys;
	}

	public void setCardSdScp02Keys(List<CardSdScp02Key> cardSdScp02Keys) {
		this.cardSdScp02Keys = cardSdScp02Keys;
	}

	public Long getFreeNonVolatileSpace() {
		return freeNonVolatileSpace;
	}

	public void setFreeNonVolatileSpace(Long freeNonVolatileSpace) {
		this.freeNonVolatileSpace = freeNonVolatileSpace;
	}

	public Integer getFreeVolatileSpace() {
		return freeVolatileSpace;
	}

	public void setFreeVolatileSpace(Integer freeVolatileSpace) {
		this.freeVolatileSpace = freeVolatileSpace;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOrginalStatus() {
		return orginalStatus;
	}

	public void setOrginalStatus(Integer orginalStatus) {
		this.orginalStatus = orginalStatus;
	}

	@Column(name = "SCP02_COUNTER")
	public Integer getScp02Counter() {
		return scp02Counter;
	}

	public void setScp02Counter(Integer scp02Counter) {
		this.scp02Counter = scp02Counter;
	}

	@Column(name = "SCP02_SECURITY_LEVEL")
	public Integer getScp02SecurityLevel() {
		return scp02SecurityLevel;
	}

	public void setScp02SecurityLevel(Integer scp02SecurityLevel) {
		this.scp02SecurityLevel = scp02SecurityLevel;
	}

	public Integer getCurrentKeyVersion() {
		return currentKeyVersion;
	}

	public void setCurrentKeyVersion(Integer currentKeyVersion) {
		this.currentKeyVersion = currentKeyVersion;
	}

	public Integer getUnknownVolatileSpace() {
		return unknownVolatileSpace;
	}

	public void setUnknownVolatileSpace(Integer unknownVolatileSpace) {
		this.unknownVolatileSpace = unknownVolatileSpace;
	}

	public Long getUnknownNoneVolatileSpace() {
		return unknownNoneVolatileSpace;
	}

	public void setUnknownNoneVolatileSpace(Long unknownNoneVolatileSpace) {
		this.unknownNoneVolatileSpace = unknownNoneVolatileSpace;
	}

	@Transient
	public KeyProfile getScp02EncKey() {
		return getScp02KeysByType(KeyProfile.SCP02_ENC_TYPE);
	}

	@Transient
	public KeyProfile getScp02MacKey() {
		return getScp02KeysByType(KeyProfile.SCP02_MAC_TYPE);
	}

	@Transient
	public KeyProfile getScp02DekKey() {
		return getScp02KeysByType(KeyProfile.SCP02_DEK_TYPE);
	}

	@Transient
	public KeyProfile getTokenMod() {
		return getScp02KeysByType(KeyProfile.TOKEN_MOD_TYPE);
	}

	@Transient
	public KeyProfile getTokenPublicExponext() {
		return getScp02KeysByType(KeyProfile.TOKEN_PUBLIC_EXPONENT_TYPE);
	}

	@Transient
	public KeyProfile getTokenPrivateExponext() {
		return getScp02KeysByType(KeyProfile.TOKEN_PRIVATE_EXPONENT_TYPE);
	}

	/**
	 * @deprecated
	 */
	@Transient
	private KeyProfile getScp02KeysByType(int keyType) {
		for (CardSdScp02Key cardSdScp02Key : cardSdScp02Keys) {
			if (CardSdScp02Key.VALID_STATUS == cardSdScp02Key.getStatus()) {
				KeyProfile scp02Key = cardSdScp02Key.getKeyProfile();
				if (keyType == scp02Key.getType()) {
					return scp02Key;
				}
			}
		}
		throw new IllegalStateException("error state of CardSecurityDomain");
	}

	/**
	 * @deprecated
	 */
	@Transient
	public List<CardSdScp02Key> getValidCardSdScp02Keys() {
		List<CardSdScp02Key> validCardSdKeys = new ArrayList<CardSdScp02Key>();
		for (CardSdScp02Key cardSdScp02Key : cardSdScp02Keys) {
			if (cardSdScp02Key.getStatus() == CardSdScp02Key.VALID_STATUS) {
				validCardSdKeys.add(cardSdScp02Key);
			}
		}
		return validCardSdKeys;
	}

	/**
	 * @deprecated
	 */
	@Transient
	public List<CardSdScp02Key> getInvalidCardSdScp02Keys() {
		List<CardSdScp02Key> validCardSdKeys = new ArrayList<CardSdScp02Key>();
		for (CardSdScp02Key cardSdScp02Key : cardSdScp02Keys) {
			if (cardSdScp02Key.getStatus() == CardSdScp02Key.INVALID_STATUS) {
				validCardSdKeys.add(cardSdScp02Key);
			}
		}
		return validCardSdKeys;
	}

	public Date getLastFeeTime() {
		return lastFeeTime;
	}

	public void setLastFeeTime(Date lastFeeTime) {
		this.lastFeeTime = lastFeeTime;
	}

	/**
	 * 获取卡上安全域的可用空间
	 * 
	 * @return 卡上安全域的可用空间
	 */
	@Transient
	public Space getAviliableSpace() {
		if (sd.isSpaceExtendable()) {// 如果安全域是应用大小安全域，剩余空间由卡剩余空间决定
			return card.getAvailableSpace();
		} else {// 如果安全域是签约空间安全域，剩余空间由安全域决定
			Space space = new Space();
			space.setNvm(freeNonVolatileSpace);
			space.setRam(freeVolatileSpace);
			return space;
		}
	}

	/**
	 * 设置卡上安全域可用空间
	 * 
	 * @param space
	 *            空间信息
	 */
	public void setAviliableSpace(Space space) {
		if (sd.isSpaceExtendable()) {// 如果安全域是应用大小安全域，剩余空间由卡剩余空间决定
			card.setAvailableSpace(space);
		} else {// 如果安全域是签约空间安全域，剩余空间由安全域决定
			this.freeNonVolatileSpace = space.getNvm();
			this.freeVolatileSpace = space.getRam();
		}
	}

	/**
	 * 获取未知空间
	 * 
	 * @return 未知空间
	 */
	@Transient
	public Space getUnknownSpace() {
		if (sd.isSpaceExtendable()) {// 如果安全域是应用大小安全域，未知空间由卡未知空间决定
			return card.getUnknownSpace();
		} else {
			Space space = new Space();

			if (null != unknownVolatileSpace && null != unknownNoneVolatileSpace) {
				space.setNvm(unknownNoneVolatileSpace);
				space.setRam(unknownVolatileSpace);
			}

			return space;
		}
	}

	/**
	 * 设置未知空间
	 */
	public void setUnknownSpace(Space space) {
		if (sd.isSpaceExtendable()) {// 如果安全域是应用大小安全域，未知空间由卡未知空间决定
			card.setUnknownSpace(space);
		} else {
			this.unknownNoneVolatileSpace = space.getNvm();
			this.unknownVolatileSpace = space.getRam();
		}
	}

}