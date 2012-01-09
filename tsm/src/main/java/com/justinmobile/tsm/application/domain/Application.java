package com.justinmobile.tsm.application.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.type.EnumType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.justinmobile.core.dao.support.EnumPersistentable;
import com.justinmobile.core.dao.support.EnumUserType;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.HexUtils;
import com.justinmobile.tsm.cms2ac.domain.ApplicationKeyProfile;
import com.justinmobile.tsm.cms2ac.security.scp02.SecureAlgorithm;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Entity
@Table(name = "APPLICATION")
public class Application extends AbstractEntity {

	private static final long serialVersionUID = -1072845520L;

	public static final String LOCATION_TOTAL_NETWORK = "全网";

	/** 删除规则:0-不能删除 */
	public static final int DELETE_RULE_CAN_NOT = 0;

	/** 删除规则:1-全部删除 */
	public static final int DELETE_RULE_DELETE_ALL = 1;

	/** 删除规则:2-只删除数据 */
	public static final int DELETE_RULE_DELETE_DATA_ONLY = 2;

	/** 个人化类型：1-指令透传 */
	public static final int TYPE_PERSONALIZE_PASSTHROUGH = 1;

	/** 个人化类型：2-应用访问安全域 */
	public static final int TYPE_PERSONALIZE_APP_TO_SD = 2;

	/** 个人化类型：3-安全域访问应用 */
	public static final int TYPE_PERSONALIZE_SD_TO_APP = 3;

	/** 状态：0-初始化 */
	public static final int STATUS_INIT = 0;

	/** 状态：1-已发布(至少有一个版本处于已发布状态) */
	public static final int STATUS_PUBLISHED = 1;

	/** 状态：2-不可用<br/> */
	public static final int STATUS_DISABLE = 2;

	/** 状态：3-已归档(所有版本都处于已归档状态) */
	public static final int STATUS_ARCHIVED = 3;

	/** 状态：4-已审核(至少有一个版本处于已审核或其后继状态) */
	public static final int STATUS_AUDITED = 4;

	/** 状态：5-待审核(第一个版本处于待审核状态) */
	public static final int STATUS_TO_BE_AUDITED = 5;

	/** 预置收费条件：1-注册后收费 */
	public static final int PRESET_CHARGE_CONDITION_REGISTED = 1;

	/** 预置收费条件：2-订购后收费 */
	public static final int PRESET_CHARGE_CONDITION_SUBSCRIBED = 2;

	/** 主键 */
	private Long id;

	/** 应用名 */
	private String name;

	/** 应用AID */
	private String aid;

	/** 所属SP */
	private SpBaseInfo sp;

	/**
	 * 所属安全域模式 <br/>
	 * 
	 * @see SecurityDomain#MODEL_ISD
	 * @see SecurityDomain#MODEL_COMMON
	 * @see SecurityDomain#MODEL_TOKEN
	 * @see SecurityDomain#MODEL_DAP
	 */
	@ResourcesFormat(key = "sd.type")
	private Integer sdModel;

	/** 所属安全域 */
	private SecurityDomain sd;

	/**
	 * 应用类型 <br/>
	 * 现在对应的是子节点
	 */
	private ApplicationType childType;

	/**
	 * 删除规则 <br/>
	 * 0-不能删除 (只退订应用，不删除)<br/>
	 * 1-删除整个应用程序 <br/>
	 * 2-只删除个人化数据
	 */
	@ResourcesFormat(key = "app.deleteRule")
	private Integer deleteRule;

	/** 应用描述 */
	private String description;

	/** 发布时间 */
	@DateFormat
	private Calendar publishDate;

	/** 归档时间 */
	private Calendar archivedDate;

	/**
	 * 状态<br/>
	 * 1-已发布(至少有一个版本处于已发布状态)<br/>
	 * 2 -不可用<br/>
	 * 3-已归档(所有版本都处于已归档状态)<br/>
	 * 4-已审核(至少有一个版本处于已审核或其后继状态)<br/>
	 * 5-待审核(第一个版本处于待审核状态)
	 */
	@ResourcesFormat(key = "app.status")
	private Integer status = STATUS_INIT;

	/** 下载次数 */
	private Integer downloadCount = 0;

	/** 当前最新版本 */
	private String lastestVersion;

	/** 版本信息 */
	private List<ApplicationVersion> versions = Lists.newArrayList();

	/** 评分统计信息 */
	private GradeStatistics statistics;

	/**
	 * 应用的技术方案<br>
	 * 1-Cms2Ac<br>
	 * 2-Mifare
	 */
	@ResourcesFormat(key = "app.form")
	private Integer form;

	/** 个人化类型 */
	@ResourcesFormat(key = "app.personalType")
	private Integer personalType;

	/** 应用所在地 */
	private String location;

	/** 业务平台地址 */
	private String businessPlatformUrl;

	/** 业务平台服务名 */
	private String serviceName;

	/** pc版图标 */
	private byte[] pcIcon;

	/** 手机版图标 */
	private byte[] moblieIcon;

	/** 应用星级 */
	private Integer starNumber = 0;

	private Set<ApplicationComment> comments = Sets.newHashSet();

	private Set<ApplicationImage> applicationImages = Sets.newHashSet();

	private Set<ApplicationStyle> applicationStyles = Sets.newHashSet();

	private Boolean available = Boolean.TRUE;

	@ResourcesFormat(key = "app.needSubscribe")
	private Boolean needSubscribe = Boolean.FALSE;

	@ResourcesFormat(key = "app.presetChargeCondition")
	private Integer presetChargeCondition;

	private List<ApplicationKeyProfile> applicationKeyProfiles = new ArrayList<ApplicationKeyProfile>();

	/** 个人化指令的安全域算法 */
	@ResourcesFormat(key = "secure.algorithm")
	private SecureAlgorithm persoCmdTransferSecureAlgorithm;

	/** 个人化指令的安全域算法 */
	@ResourcesFormat(key = "secure.algorithm")
	private SecureAlgorithm persoCmdSensitiveDataSecureAlgorithm;

	public enum PersonalType implements EnumPersistentable {
		/** 不需要个人化 */
		NOT_NECESSARY(0, null),
		/** 指令透窗 */
		PASSTHROUGH(1, "personalizeAppPassthonghProcessor"),
		/** 应用访问安全域 */
		APP_TO_SD(2, "personalizeAppAppToSdProcessor"),
		/** 安全域访问应用 */
		SD_TO_APP(3, "personalizeAppSdToAppProcessor");

		public static final String NAME = "com.justinmobile.tsm.application.domain.Application$PersonalType";

		private int value;

		private String beanName;

		public int getValue() {
			return this.value;
		}

		PersonalType(int value, String beanName) {
			this.value = value;
			this.beanName = beanName;
		}

		public static PersonalType valueOf(int value) {
			PersonalType[] personalTypes = PersonalType.values();
			PersonalType type = null;

			for (PersonalType personalType : personalTypes) {
				if (value == personalType.getValue()) {
					type = personalType;
				}
			}

			return type;
		}

		public String getBeanName() {
			return this.beanName;
		}
	}

	@ManyToOne
	@JoinColumn(name = "SP_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SpBaseInfo getSp() {
		return sp;
	}

	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}

	@ManyToOne
	@JoinColumn(name = "TYPE")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public ApplicationType getChildType() {
		return childType;
	}

	public void setChildType(ApplicationType childType) {
		this.childType = childType;
	}

	@ManyToOne
	@JoinColumn(name = "SD_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SecurityDomain getSd() {
		return sd;
	}

	public void setSd(SecurityDomain sd) {
		this.sd = sd;
	}

	@OneToOne(mappedBy = "application")
	@Cascade({ CascadeType.ALL })
	@LazyToOne(LazyToOneOption.PROXY)
	public GradeStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(GradeStatistics statistics) {
		this.statistics = statistics;
	}

	@OneToMany(mappedBy = "application")
	@OrderBy("versionNo DESC")
	@Cascade({ CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.EXTRA)
	public List<ApplicationVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<ApplicationVersion> versions) {
		this.versions = versions;
	}

	@OneToMany(mappedBy = "application")
	@OrderBy("commentTime DESC")
	@Cascade({ CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<ApplicationComment> getComments() {
		return comments;
	}

	public void setComments(Set<ApplicationComment> comments) {
		this.comments = comments;
	}

	public void setApplicationImages(Set<ApplicationImage> applicationImages) {
		this.applicationImages = applicationImages;
	}

	@OneToMany(mappedBy = "application", orphanRemoval = true)
	@Cascade({ CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<ApplicationImage> getApplicationImages() {
		return applicationImages;
	}

	public void setApplicationStyle(Set<ApplicationStyle> applicationStyles) {
		this.applicationStyles = applicationStyles;
	}

	@OneToMany(mappedBy = "application", orphanRemoval = true)
	@Cascade({ CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<ApplicationStyle> getApplicationStyle() {
		return applicationStyles;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getStarNumber() {
		return starNumber;
	}

	public void setStarNumber(Integer starNumber) {
		this.starNumber = starNumber;
	}

	@Column(name = "APP_NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "APP_AID")
	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid.toUpperCase();
	}

	public Integer getDeleteRule() {
		return deleteRule;
	}

	public void setDeleteRule(Integer deleteRule) {
		this.deleteRule = deleteRule;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Calendar publishDate) {
		this.publishDate = publishDate;
	}

	public Calendar getArchivedDate() {
		return archivedDate;
	}

	public void setArchivedDate(Calendar archivedDate) {
		this.archivedDate = archivedDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getLastestVersion() {
		return lastestVersion;
	}

	public void setLastestVersion(String lastestVersion) {
		this.lastestVersion = lastestVersion;
	}

	public String getBusinessPlatformUrl() {
		return businessPlatformUrl;
	}

	public void setBusinessPlatformUrl(String businessPlatformUrl) {
		this.businessPlatformUrl = businessPlatformUrl;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	public byte[] getPcIcon() {
		return pcIcon;
	}

	public void setPcIcon(byte[] pcIcon) {
		this.pcIcon = pcIcon;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	public byte[] getMoblieIcon() {
		return moblieIcon;
	}

	public void setMoblieIcon(byte[] moblieIcon) {
		this.moblieIcon = moblieIcon;
	}

	public Integer getSdModel() {
		return sdModel;
	}

	public void setSdModel(Integer sdModel) {
		this.sdModel = sdModel;
	}

	public Integer getForm() {
		return form;
	}

	public void setForm(Integer form) {
		this.form = form;
	}

	public Integer getPersonalType() {
		return personalType;
	}

	public void setPersonalType(Integer personalType) {
		this.personalType = personalType;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	@Transient
	public boolean isTotalNetWork() {
		return LOCATION_TOTAL_NETWORK.equals(this.getLocation());
	}

	/**
	 * 为应用创建一个新版本，建立双向关联
	 * 
	 * @param versionNo
	 *            版本号
	 */
	public void createNewVersion(String versionNo) {
		if (StringUtils.isBlank(versionNo)) {
			throw new PlatformException(PlatformErrorCode.APPLICATION_VERSION_NO_BLANK);
		}

		ApplicationVersion version = new ApplicationVersion();
		version.setVersionNo(versionNo);
		version.setStatus(ApplicationVersion.STATUS_INIT);

		versions.add(version);
		version.setApplication(this);
	}

	@Transient
	public Space getLastestSpace() {
		Space space = new Space();
		List<ApplicationVersion> versions = this.getVersions();
		if (CollectionUtils.isNotEmpty(versions)) {
			for (ApplicationVersion version : versions) {
				if (version.getVersionNo().equals(this.getLastestVersion())) {
					Long nonVolatileSpace = version.getNonVolatileSpace();
					Integer volatileSpace = version.getVolatileSpace();
					if (nonVolatileSpace == null) {
						nonVolatileSpace = 0l;
					}
					if (volatileSpace == null) {
						volatileSpace = 0;
					}
					space.setNvm(nonVolatileSpace);
					space.setRam(volatileSpace);
					break;
				}
			}
		}
		return space;
	}

	/**
	 * 创建一个新的统计对象
	 */
	public void creatNewStatistics() {
		this.statistics = new GradeStatistics();
		statistics.setApplication(this);
	}

	/**
	 * 验证aid是否符合规范要求
	 * 
	 * @throws PlatformErrorCode.APPLICATION_AID_NOT_EXIST
	 *             AID不存在
	 * @throws PlatformErrorCode.APPLICATION_AID_SHORTER
	 *             AID长度小于规范要求
	 * @throws PlatformErrorCode.APPLICATION_AID_LONGER
	 *             AID长度大于规范要求
	 * @throws PlatformErrorCode.SP_RID_DISCARD
	 *             RID不匹配
	 */
	public void validateAid() {
		if (null == this.aid) {
			throw new PlatformException(PlatformErrorCode.APPLICATION_AID_NOT_EXIST);
		}

		HexUtils.validate(this.aid);

		if (10 > this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.APPLICATION_AID_SHORTER);
		}

		if (32 < this.aid.length()) {
			throw new PlatformException(PlatformErrorCode.APPLICATION_AID_LONGER);
		}

		String rid = this.aid.substring(0, 10);
		if (!rid.equals(this.sp.getRid())) {
			throw new PlatformException(PlatformErrorCode.SP_RID_DISCARD);
		}
	}

	/**
	 * 添加一个版本，建立双向关联
	 * 
	 * @param applicationVersion
	 *            应用版本
	 */
	public void addVersion(ApplicationVersion applicationVersion) {
		this.versions.add(applicationVersion);
		applicationVersion.setApplication(this);
	}

	public void increaseDownloadcount() {
		downloadCount++;
	}

	public Boolean getNeedSubscribe() {
		return needSubscribe;
	}

	public void setNeedSubscribe(Boolean needSubscribe) {
		this.needSubscribe = needSubscribe;
	}

	public boolean needSubscribe() {
		return null != needSubscribe && needSubscribe;
	}

	@Transient
	public ApplicationVersion getLastestAppVersion() {
		List<ApplicationVersion> versions = this.getVersions();
		if (CollectionUtils.isNotEmpty(versions)) {
			for (ApplicationVersion applicationVersion : versions) {
				if (this.getLastestVersion() != null && this.getLastestVersion().equals(applicationVersion.getVersionNo())) {
					return applicationVersion;
				}
			}
		}
		return null;
	}

	public void removeVersion(ApplicationVersion applicationVersion) {
		this.versions.remove(applicationVersion);
	}

	public Integer getPresetChargeCondition() {
		return presetChargeCondition;
	}

	public void setPresetChargeCondition(Integer presetChargeCondition) {
		this.presetChargeCondition = presetChargeCondition;
	}

	public void setApplicationKeyProfiles(List<ApplicationKeyProfile> applicationKeyProfiles) {
		this.applicationKeyProfiles = applicationKeyProfiles;
	}

	@OneToMany(mappedBy = "application")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<ApplicationKeyProfile> getApplicationKeyProfiles() {
		return applicationKeyProfiles;
	}

	@Column(name = "PERSO_CMD_TRANSFER_SA")
	@Type(type = EnumUserType.NAME, parameters = @Parameter(name = EnumType.ENUM, value = SecureAlgorithm.NAME))
	public SecureAlgorithm getPersoCmdTransferSecureAlgorithm() {
		return persoCmdTransferSecureAlgorithm;
	}

	public void setPersoCmdTransferSecureAlgorithm(SecureAlgorithm persoCmdTransferSecureAlgorithm) {
		this.persoCmdTransferSecureAlgorithm = persoCmdTransferSecureAlgorithm;
	}

	@Column(name = "PERSO_CMD_SENSITIVE_DATA_SA")
	@Type(type = EnumUserType.NAME, parameters = @Parameter(name = EnumType.ENUM, value = SecureAlgorithm.NAME))
	public SecureAlgorithm getPersoCmdSensitiveDataSecureAlgorithm() {
		return persoCmdSensitiveDataSecureAlgorithm;
	}

	public void setPersoCmdSensitiveDataSecureAlgorithm(SecureAlgorithm persoCmdSensitiveDataSecureAlgorithm) {
		this.persoCmdSensitiveDataSecureAlgorithm = persoCmdSensitiveDataSecureAlgorithm;
	}

	@Transient
	private ApplicationKeyProfile getApplicationKeyProfileByType(int keyType) {
		for (ApplicationKeyProfile applicationKeyProfile : applicationKeyProfiles) {
			if (keyType == applicationKeyProfile.getKeyType().intValue()) {
				return applicationKeyProfile;
			}
		}
		throw new PlatformException(PlatformErrorCode.UNKNOWN_KEY, keyType);
	}

	@Transient
	public ApplicationKeyProfile getKek() {
		return getApplicationKeyProfileByType(ApplicationKeyProfile.TYPE_KEK);
	}

	@Transient
	public ApplicationKeyProfile getTk() {
		return getApplicationKeyProfileByType(ApplicationKeyProfile.TYPE_TK);
	}

	public void addApplicationKeyProfile(ApplicationKeyProfile applicationKeyProfile) {
		this.applicationKeyProfiles.add(applicationKeyProfile);
		applicationKeyProfile.setApplication(this);
	}
}