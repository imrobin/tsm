package com.justinmobile.tsm.application.domain;

import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.TlvObject;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Entity
@Table(name = "SECURITY_DOMAIN")
public class SecurityDomain extends AbstractEntity {

	private static final long serialVersionUID = -2134141884L;

	public static final String LOCK = "y";
	public static final String UNLOCK = "n";

	/** 1:主安全域 */
	public static final int MODEL_ISD = 1;

	/** 2:公共第三方安全域 */
	public static final int MODEL_COMMON = 2;

	/** 3:DAP模式 */
	public static final int MODEL_DAP = 3;

	/** 4:Token模式 */
	public static final int MODEL_TOKEN = 4;

	/** 在输入应用所属安全域模式时可以指定具体安全域的模式集合 */
	public static final Set<Integer> MODEL_APPLICATION_SELECTABLE = new HashSet<Integer>();
	{
		MODEL_APPLICATION_SELECTABLE.add(MODEL_COMMON);
		MODEL_APPLICATION_SELECTABLE.add(MODEL_DAP);
		MODEL_APPLICATION_SELECTABLE.add(MODEL_TOKEN);
	}

	/** 1：初始化 */
	public static final int STATUS_INIT = 1;

	/** 2：已发布 */
	public static final int STATUS_PUBLISHED = 2;

	/** 3：已归档 */
	public static final int STATUS_ARCHIVED = 3;

	/** 安全等级0 */
	public static final int SECURITY_LEVEL_0 = 0;

	/** 安全等级1 */
	public static final int SECURITY_LEVEL_1 = 1;

	/** 安全等级3 */
	public static final int SECURITY_LEVEL_3 = 3;

	/*************** 空间管理规则 ******************/
	/** 1:签约空间模式 */
	public static final int FIXED_SPACE = 1;

	/** 2:应用大小管理模式 */
	public static final int UNFIXABLE_SPACE = 2;

	/***************** 删除规则 *******************/
	/** 0:自动删除 */
	public static final int AUTO_DELETE = 0;

	/** 1:调用指令删除 */
	public static final int MANUAL_DELETE = 1;

	/** 2:不能删除 */
	public static final int CANNOT_DELETE = 2;

	/** 主键 */
	private Long id;

	/** 安全域AID */
	private String aid;

	/** 安全域名称 */
	private String sdName;

	/**
	 * 删除规则<br/>
	 * 0-自动删除<br/>
	 * 1-调用指令删除
	 */
	@ResourcesFormat(key = "sd.deleteRule")
	private Integer deleteRule;

	/**
	 * 安全域类型<br/>
	 * 1-主安全域<br/>
	 * 2-辅助安全域
	 */
	@Deprecated
	private Integer type;

	/**
	 * 空间模式<br/>
	 * 1-签约空间模式<br/>
	 * 2-应用大小管理模式
	 */
	@ResourcesFormat(key = "sd.spaceRule")
	private Integer spaceRule;

	/**
	 * 安全域模式<br/>
	 * 1-主安全域<br/>
	 * 2-公共第三方安全域<br/>
	 * 3-DAP模式<br/>
	 * 4-Token模式
	 */
	@ResourcesFormat(key = "sd.type")
	private Integer model;

	/**
	 * 状态<br/>
	 * 1-初始化(等待发布审核)<br/>
	 * 2-已发布<br/>
	 * 3-已归档(已删除)
	 */
	@ResourcesFormat(key = "sd.status")
	private Integer status;

	/** 权限 */
	private Integer privilege;

	/** 权限:对应的中文含义 */
	private String privilegeZh;

	/** 安装参数 */
	private String installParams;

	/** 安全域自身的不可变空间 */
	private Long noneVolatileSpace;

	/** 安全域自身的可变空间 */
	private Integer volatileSpace;

	/** 当前密钥版本 */
	private Integer currentKeyVersion;

	/** 安全等级 */
	private Integer scp02SecurityLevel;

	/** 密钥生效日期 */
	private Calendar beginDate;

	/** 更新密钥周期 */
	private String keyUpdateCycle;

	/** 加密机密钥组 */
	private Long hsmkeyBlock;

	/** 加密机密钥类型 */
	private Long hsmkeyType;

	/** 所属SP */
	private SpBaseInfo sp;

	/** 对应的加载文件 */
	private LoadModule loadModule;

	private List<KeyProfile> keyProfiles = new ArrayList<KeyProfile>();

	/** 是否有申请在处理中，y/n */
	private String hasLock = "n";

	/** 业务平台地址 */
	private String businessPlatformUrl;

	/** 业务平台服务名 */
	private String serviceName;

	// 以下字段不建议使用
	/** TAR */
	@Deprecated
	private String tar;

	@Deprecated
	private Long mainSecurityDomainid;

	/** 审核不通过原因 */
	@Deprecated
	private String comments;

	/** 安全域通道参数 */
	@Deprecated
	private Long scp80i;

	@ManyToOne
	@JoinColumn(name = "LOAD_MODULE_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public LoadModule getLoadModule() {
		return loadModule;
	}

	public void setLoadModule(LoadModule loadModule) {
		this.loadModule = loadModule;
	}

	@OneToMany(mappedBy = "securityDomain")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<KeyProfile> getKeyProfiles() {
		return keyProfiles;
	}

	public void setKeyProfiles(List<KeyProfile> keyProfiles) {
		this.keyProfiles = keyProfiles;
	}

	@ManyToOne
	@JoinColumn(name = "SP_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SpBaseInfo getSp() {
		return sp;
	}

	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SECURITY_DOMAIN") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		if (aid != null)
			aid = aid.toUpperCase();
		this.aid = aid;
	}

	public Integer getDeleteRule() {
		return deleteRule;
	}

	public void setDeleteRule(Integer deleteRule) {
		this.deleteRule = deleteRule;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSpaceRule() {
		return spaceRule;
	}

	public void setSpaceRule(Integer spaceRule) {
		this.spaceRule = spaceRule;
	}

	public Integer getModel() {
		return model;
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Integer privilege) {
		this.privilege = privilege;
		this.privilegeZh = Privilege.parse(privilege).translateToZH();
	}

	@Transient
	public String getPrivilegeZh() {
		return privilegeZh;
	}

	public String getInstallParams() {
		return installParams;
	}

	public void setInstallParams(String installParams) {
		if (installParams != null)
			installParams = installParams.toUpperCase();
		this.installParams = installParams;
		if (this.installParams != null && !StringUtils.isBlank(this.installParams) && this.installParams.startsWith("C9")) {
			TlvObject C9 = TlvObject.parse(this.installParams);
			byte[] TAG49 = null;
			try {
				C9 = TlvObject.parse(C9.getByTag("c9"));
				TAG49 = C9.getByTag("49");
			} catch (Exception e) {
			}
			this.spaceRule = ArrayUtils.isNotEmpty(TAG49) ? FIXED_SPACE : UNFIXABLE_SPACE;
		}
	}

	public Long getNoneVolatileSpace() {
		return noneVolatileSpace;
	}

	public void setNoneVolatileSpace(Long noneVolatileSpace) {
		this.noneVolatileSpace = noneVolatileSpace;
	}

	public Integer getVolatileSpace() {
		return volatileSpace;
	}

	public void setVolatileSpace(Integer volatileSpace) {
		this.volatileSpace = volatileSpace;
	}

	public Integer getCurrentKeyVersion() {
		return currentKeyVersion;
	}

	public void setCurrentKeyVersion(Integer currentKeyVersion) {
		this.currentKeyVersion = currentKeyVersion;
	}

	@Column(name = "SCP02_SECURITY_LEVEL")
	public Integer getScp02SecurityLevel() {
		return scp02SecurityLevel;
	}

	public void setScp02SecurityLevel(Integer scp02SecurityLevel) {
		this.scp02SecurityLevel = scp02SecurityLevel;
	}

	public Calendar getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Calendar beginDate) {
		this.beginDate = beginDate;
	}

	public String getKeyUpdateCycle() {
		return keyUpdateCycle;
	}

	public void setKeyUpdateCycle(String keyUpdateCycle) {
		this.keyUpdateCycle = keyUpdateCycle;
	}

	public Long getHsmkeyBlock() {
		return hsmkeyBlock;
	}

	public void setHsmkeyBlock(Long hsmkeyBlock) {
		this.hsmkeyBlock = hsmkeyBlock;
	}

	public Long getHsmkeyType() {
		return hsmkeyType;
	}

	public void setHsmkeyType(Long hsmkeyType) {
		this.hsmkeyType = hsmkeyType;
	}

	public String getSdName() {
		return sdName;
	}

	public void setSdName(String sdName) {
		this.sdName = sdName;
	}

	public String getTar() {
		return tar;
	}

	public void setTar(String tar) {
		this.tar = tar;
	}

	public Long getMainSecurityDomainid() {
		return mainSecurityDomainid;
	}

	public void setMainSecurityDomainid(Long mainSecurityDomainid) {
		this.mainSecurityDomainid = mainSecurityDomainid;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Long getScp80i() {
		return scp80i;
	}

	public void setScp80i(Long scp80i) {
		this.scp80i = scp80i;
	}

	@Transient
	public List<KeyProfile> getScp02KeyProfiles() {
		List<KeyProfile> scp02KeyProfiles = new ArrayList<KeyProfile>();
		for (KeyProfile keyProfile : getKeyProfiles()) {
			// if (Constants.CMS2AC_SCP_02.equals(keyProfile.getScp())) {
			scp02KeyProfiles.add(keyProfile);
			// }
		}
		return scp02KeyProfiles;
	}

	@Transient
	public boolean isIsd() {
		return this.model == MODEL_ISD;
	}

	/**
	 * @deprecated
	 */
	@Transient
	public KeyProfile getScp02EncKey() {
		return getEncKey();
	}

	/**
	 * @deprecated
	 */
	@Transient
	public KeyProfile getScp02MacKey() {
		return getMacKey();
	}

	/**
	 * @deprecated
	 */
	@Transient
	public KeyProfile getScp02DekKey() {
		return getDekKey();
	}

	/**
	 * @deprecated
	 */
	@Transient
	public KeyProfile getScp02EncUpdateKey() {
		return getScp02KeysByTypeStatus(KeyProfile.SCP02_ENC_TYPE, KeyProfile.STATUS_UPDATE).get(0);
	}

	/**
	 * @deprecated
	 */
	@Transient
	public KeyProfile getScp02MacUpdateKey() {
		return getScp02KeysByTypeStatus(KeyProfile.SCP02_MAC_TYPE, KeyProfile.STATUS_UPDATE).get(0);
	}

	/**
	 * @deprecated
	 */
	@Transient
	public KeyProfile getScp02DekUpdateKey() {
		return getScp02KeysByTypeStatus(KeyProfile.SCP02_DEK_TYPE, KeyProfile.STATUS_UPDATE).get(0);
	}

	/**
	 * @deprecated
	 */
	@Transient
	private List<KeyProfile> getScp02KeysByTypeStatus(int keyType, int status) {
		List<KeyProfile> keys = new ArrayList<KeyProfile>();
		List<KeyProfile> scp02KeyProfiles = getScp02KeyProfiles();
		for (KeyProfile keyProfile : scp02KeyProfiles) {
			if (keyType == keyProfile.getType() && status == keyProfile.getStatus()) {
				keys.add(keyProfile);
			}
		}
		return keys;
	}

	/**
	 * @deprecated
	 */
	@Transient
	public List<KeyProfile> getScp02UpdateKeys() {
		List<KeyProfile> keys = new ArrayList<KeyProfile>();
		keys.add(getScp02EncUpdateKey());
		keys.add(getScp02MacUpdateKey());
		keys.add(getScp02DekUpdateKey());
		return keys;
	}

	@Transient
	public long getManagedNoneVolatileSpace() {// 安全域管理的不可变空间
		SecurityDomainInstallParams params = SecurityDomainInstallParams.parse(this.installParams);
		return params.getManagedNoneVolatileSpace();
	}

	@Transient
	public int getManagedVolatileSpace() {// 安全域管理的的可变空间
		SecurityDomainInstallParams params = SecurityDomainInstallParams.parse(this.installParams);
		return params.getManagedVolatileSpace();
	}

	public String getHasLock() {
		if (hasLock == null)
			hasLock = UNLOCK;
		return hasLock;
	}

	public void setHasLock(String hasLock) {
		this.hasLock = hasLock;
	}

	@Transient
	private KeyProfile getKeyByIndex(int keyIndex) {
		for (KeyProfile key : this.keyProfiles) {
			if (keyIndex == key.getIndex().intValue()) {
				return key;
			}
		}
		throw new PlatformException(PlatformErrorCode.UNKNOWN_KEY, keyIndex);
	}

	@Transient
	public KeyProfile getEncKey() {
		return getKeyByIndex(KeyProfile.INDEX_ENC);
	}

	@Transient
	public KeyProfile getMacKey() {
		return getKeyByIndex(KeyProfile.INDEX_MAC);
	}

	@Transient
	public KeyProfile getDekKey() {
		return getKeyByIndex(KeyProfile.INDEX_DEK);
	}

	/**
	 * 安全域是否为固定空间
	 * 
	 * @return true 为固定空间<br/>
	 *         false 不为固定空间
	 */
	@Transient
	public boolean isSpaceFixed() {
		return !isSpaceExtendable();
	}

	/**
	 * 当前安全域是否为应用大小安全域
	 * 
	 * @return true-是应用大小安全域<br/>
	 *         false-不是应用大小安全域
	 */
	@Transient
	public boolean isSpaceExtendable() {
		// 主安全域的空间管理规则等于应用大小安全域
		// 辅助安全域由安装参数决定
		return MODEL_ISD == model || !SecurityDomainInstallParams.parse(installParams).isSpaceFixed();
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

	/**
	 * 获取安全域管理的总空间
	 * 
	 * @return 安全域管理的总空间
	 * @throws PlatformErrorCode.SD_SPACE_NOT_FIXED
	 *             安全域不是签约空间安全域
	 */
	@Transient
	public Space getTotalManagedSpace() {
		SecurityDomainInstallParams installParam = SecurityDomainInstallParams.parse(installParams);
		if (installParam.isSpaceFixed()) {
			return installParam.getManagedSpace();
		} else {
			throw new PlatformException(PlatformErrorCode.SD_SPACE_NOT_FIXED);
		}
	}

	/**
	 * 获取安全域占用的总空间<br/>
	 * 如果安全域是签约空间安全域，占用的总空间=自身空间+管理的总空间<br/>
	 * 如果安全域是应用大小安全域，占用的总空间=自身空间
	 * 
	 * @return 安全域占用的总空间
	 */
	@Transient
	public Space getTotalSpace() {
		Space space = new Space();

		space.setNvm(noneVolatileSpace);
		space.setRam(volatileSpace);

		SecurityDomainInstallParams installParam = SecurityDomainInstallParams.parse(installParams);
		if (installParam.isSpaceFixed()) {
			space = space.plus(installParam.getManagedSpace());
		}

		return space;
	}
}