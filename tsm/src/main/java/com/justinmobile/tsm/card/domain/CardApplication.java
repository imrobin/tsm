package com.justinmobile.tsm.card.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
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
import com.justinmobile.tsm.application.domain.Space;

/**
 * 卡上应用信息
 * 
 * @author JazGung
 * 
 */
@Entity
@Table(name = "CARD_APPLICATION")
public class CardApplication extends AbstractEntity {

	private static final long serialVersionUID = 289497088L;

	/** 1-未下载 */
	public static final Integer STATUS_UNDOWNLOAD = 1;

	/** 2-下载中(至少下载成功1个加载文件) */
	public static final Integer STATUS_DOWNING = 2;

	/** 3-删除中(至少删除成功1个APPLET) */
	public static final Integer STATUS_DELETEING = 3;

	/** 4-已下载(所有加载文件下载完成) */
	public static final Integer STATUS_DOWNLOADED = 4;

	/** 5-已锁定 */
	public static final Integer STATUS_LOCKED = 5;

	/** 6-已安装(所有实例安装完成) */
	public static final Integer STATUS_INSTALLED = 6;

	/** 7-已个人化(个人化数据下发成功) */
	public static final Integer STATUS_PERSONALIZED = 7;

	/** 8-可用(业务平台订购关系建立成功，应用可用) */
	public static final Integer STATUS_AVAILABLE = 8;

	/**
	 * 9-已挂失.作为挂失后通知业务平台后的状态.
	 */
	public static final Integer STATUS_LOSTED = 9;

	/** 已迁出 */
	public static final Integer MIGRATABLE_TRUE = 1;

	/** 未迁出 */
	public static final Integer MIGRATABLE_FALSE = 0;

	/** 可下载状态集合 */
	public static final Set<Integer> STATUS_DOWNLOADABLE = new HashSet<Integer>();
	static {
		STATUS_DOWNLOADABLE.add(STATUS_UNDOWNLOAD);
		STATUS_DOWNLOADABLE.add(STATUS_DOWNING);
		STATUS_DOWNLOADABLE.add(STATUS_DOWNLOADED);
		STATUS_DOWNLOADABLE.add(STATUS_INSTALLED);
		STATUS_DOWNLOADABLE.add(STATUS_PERSONALIZED);
	}

	/** 可锁定状态集合 */
	public static final Set<Integer> STATUS_LOCKABLE = new HashSet<Integer>();
	static {
		STATUS_LOCKABLE.add(STATUS_AVAILABLE);
	}

	/**
	 * 可解锁锁状态集合
	 */
	public static final Set<Integer> STATUS_UNLOCKABLE = new HashSet<Integer>();
	static {
		STATUS_UNLOCKABLE.add(STATUS_LOCKED);
	}

	/** 可恢复状态集合 */
	public static final Set<Integer> STATUS_REVERTABLE = new HashSet<Integer>();
	static {
		STATUS_REVERTABLE.add(STATUS_AVAILABLE);
	}

	/** 可显示状态集合 */
	public static final Set<Integer> STATUS_SHOWABLE = new HashSet<Integer>();
	static {
		STATUS_SHOWABLE.add(STATUS_DOWNING);
		STATUS_SHOWABLE.add(STATUS_DELETEING);
		STATUS_SHOWABLE.add(STATUS_DOWNLOADED);
		STATUS_SHOWABLE.add(STATUS_INSTALLED);
		STATUS_SHOWABLE.add(STATUS_PERSONALIZED);
		STATUS_SHOWABLE.add(STATUS_AVAILABLE);
		STATUS_SHOWABLE.add(STATUS_LOCKED);
		STATUS_SHOWABLE.add(STATUS_LOSTED);
	}

	/** 可使用集合 */
	public static final Set<Integer> STATUS_USEABLE = new HashSet<Integer>();
	static {
		STATUS_USEABLE.add(STATUS_PERSONALIZED);
		STATUS_USEABLE.add(STATUS_AVAILABLE);
		STATUS_USEABLE.add(STATUS_LOCKED);
	}

	public static final Set<Integer> STATUS_CAL = new HashSet<Integer>();
	static {
		STATUS_CAL.add(STATUS_DOWNING);
		STATUS_CAL.add(STATUS_DELETEING);
		STATUS_CAL.add(STATUS_DOWNLOADED);
		STATUS_CAL.add(STATUS_INSTALLED);
		STATUS_CAL.add(STATUS_PERSONALIZED);
		STATUS_CAL.add(STATUS_AVAILABLE);
		STATUS_CAL.add(STATUS_LOCKED);
	}

	/** 可删除状态集合 */
	public static final Set<Integer> STATUS_DELETEABLE = new HashSet<Integer>();
	static {
		STATUS_DELETEABLE.add(STATUS_UNDOWNLOAD);
		STATUS_DELETEABLE.add(STATUS_DOWNING);
		STATUS_DELETEABLE.add(STATUS_PERSONALIZED);
		STATUS_DELETEABLE.add(STATUS_AVAILABLE);
		STATUS_DELETEABLE.add(STATUS_DELETEING);
		STATUS_DELETEABLE.add(STATUS_LOCKED);
	}

	/** 可删除状态集合 - 删除文件 */
	public static final Set<Integer> STATUS_FILE_DELETEABLE = new HashSet<Integer>();
	static {
		STATUS_FILE_DELETEABLE.add(STATUS_DOWNING);
		STATUS_FILE_DELETEABLE.add(STATUS_PERSONALIZED);
		STATUS_FILE_DELETEABLE.add(STATUS_AVAILABLE);
		STATUS_FILE_DELETEABLE.add(STATUS_DELETEING);
		STATUS_FILE_DELETEABLE.add(STATUS_INSTALLED);
		STATUS_FILE_DELETEABLE.add(STATUS_LOCKED);
		STATUS_FILE_DELETEABLE.add(STATUS_DOWNLOADED);
	}

	/** 可删除状态集合 - 实例文件 */
	public static final Set<Integer> STATUS_APPLET_DELETEABLE = new HashSet<Integer>();
	static {
		STATUS_APPLET_DELETEABLE.add(STATUS_DOWNING);
		STATUS_APPLET_DELETEABLE.add(STATUS_PERSONALIZED);
		STATUS_APPLET_DELETEABLE.add(STATUS_AVAILABLE);
		STATUS_APPLET_DELETEABLE.add(STATUS_DELETEING);
		STATUS_APPLET_DELETEABLE.add(STATUS_LOCKED);
		STATUS_APPLET_DELETEABLE.add(STATUS_INSTALLED);
	}

	/** 可个人化状态集合 */
	public static final Set<Integer> STATUS_PESONABLE = new HashSet<Integer>();
	static {
		STATUS_PESONABLE.add(STATUS_INSTALLED);
		STATUS_PESONABLE.add(STATUS_PERSONALIZED);
		STATUS_PESONABLE.add(STATUS_AVAILABLE);
	}

	/** 可升级的状态集合 */
	public static final Set<Integer> STATUS_UPDATABLE = new HashSet<Integer>();
	static {
		STATUS_UPDATABLE.add(STATUS_DOWNING);
		STATUS_UPDATABLE.add(STATUS_DELETEING);
		STATUS_UPDATABLE.add(STATUS_DOWNLOADED);
		STATUS_UPDATABLE.add(STATUS_INSTALLED);
		STATUS_UPDATABLE.add(STATUS_PERSONALIZED);
		STATUS_UPDATABLE.add(STATUS_AVAILABLE);
	}

	/** 可读取个人化数据的状态集合 */
	public static final Set<Integer> STATUS_PERSO_DATA_READABLE = new HashSet<Integer>();
	static {
		STATUS_PERSO_DATA_READABLE.add(STATUS_PERSONALIZED);
		STATUS_PERSO_DATA_READABLE.add(STATUS_AVAILABLE);
	}

	/** 可读取个人化数据的状态集合 */
	public static final Set<Integer> STATUS_PERSO_DATA_DELETABLE = new HashSet<Integer>();
	static {
		STATUS_PERSO_DATA_DELETABLE.add(STATUS_PERSONALIZED);
		STATUS_PERSO_DATA_DELETABLE.add(STATUS_AVAILABLE);
	}

	private Long id;

	private ApplicationVersion applicationVersion;

	private CardInfo cardInfo;

	/**
	 * 状态<br/>
	 * 1-未下载<br/>
	 * 2-下载中(至少下载成功1个加载文件)<br/>
	 * 3-删除中(至少删除成功1个APPLET)<br/>
	 * 4-已下载(业务平台订购关系建立成功)<br/>
	 * 5-已锁定<br/>
	 * 6-已安装(所有实例安装完成)<br/>
	 * 7-已个人化(个人化数据下发成功) <br/>
	 * 8-可用(业务平台订购关系建立成功，应用可用)<br/>
	 */
	@ResourcesFormat(key = "cardApplication.status")
	private Integer status;

	/** 客户端是否下载 */
	@ResourcesFormat(key = "cardApplication.clientDownload")
	private Boolean clientDownload;

	/** 是否可恢复 */
	private Boolean recoverable;

	/**
	 * 是否正在恢复中
	 */
	private Boolean recovering;

	/** 在卡片中占用不可变空间大小 */
	private Long usedNonVolatileSpace;

	/** 在卡片中占用可变空间大小 */
	private Integer usedVolatileSpace;

	/** 卡上应用的订购时间 */
	private Date lastFeeTime;

	private Integer originalStatus;

	/**
	 * 应用是否可以迁移<br/>
	 * true-可以迁移<br/>
	 * false-不能迁移
	 */
	@ResourcesFormat(key = "cardApplication.migratable")
	private Boolean migratable = Boolean.FALSE;

	@ManyToOne
	@JoinColumn(name = "APPLICATION_VERSION_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public ApplicationVersion getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(ApplicationVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	@ManyToOne
	@JoinColumn(name = "CARD_INFO_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public CardInfo getCardInfo() {
		return cardInfo;
	}

	public void setCardInfo(CardInfo cardInfo) {
		this.cardInfo = cardInfo;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_APPLICATION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getClientDownload() {
		return clientDownload;
	}

	public void setClientDownload(Boolean clientDownload) {
		this.clientDownload = clientDownload;
	}

	/** @deprecated */
	public Boolean getRecoverable() {
		return recoverable;
	}

	/** @deprecated */
	public void setRecoverable(Boolean recoverable) {
		this.recoverable = recoverable;
	}

	@Column(name = "used_nonvolatilespace")
	public Long getUsedNonVolatileSpace() {
		return usedNonVolatileSpace;
	}

	public void setUsedNonVolatileSpace(Long usedNonVolatileSpace) {
		this.usedNonVolatileSpace = usedNonVolatileSpace;
	}

	@Column(name = "used_volatilespace")
	public Integer getUsedVolatileSpace() {
		return usedVolatileSpace;
	}

	public void setUsedVolatileSpace(Integer usedVolatileSpace) {
		this.usedVolatileSpace = usedVolatileSpace;
	}

	/** @deprecated */
	public Boolean getRecovering() {
		return recovering;
	}

	/** @deprecated */
	public void setRecovering(Boolean recovering) {
		this.recovering = recovering;
	}

	/**
	 * 设置空间信息
	 * 
	 * @param space
	 *            空间信息
	 */
	public void setSpaceInfo(Space space) {
		usedNonVolatileSpace = space.getNvm();
		usedVolatileSpace = space.getRam();
	}

	/**
	 * 获取空间信息
	 * 
	 * @param space
	 *            空间信息
	 */
	@Transient
	public Space getSpaceInfo() {
		Space spaceInfo = new Space();
		spaceInfo.setNvm(usedNonVolatileSpace);
		spaceInfo.setRam(usedVolatileSpace);
		return spaceInfo;
	}

	public Boolean getMigratable() {
		return migratable;
	}

	public void setMigratable(Boolean migratable) {
		this.migratable = migratable;
	}

	public Date getLastFeeTime() {
		return lastFeeTime;
	}

	public void setLastFeeTime(Date lastFeeTime) {
		this.lastFeeTime = lastFeeTime;
	}

	public Integer getOriginalStatus() {
		return originalStatus;
	}

	public void setOriginalStatus(Integer originalStatus) {
		this.originalStatus = originalStatus;
	}

}