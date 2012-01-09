package com.justinmobile.tsm.application.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.core.utils.TlvObject;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.system.domain.Requistion;

@Entity
@Table(name = "SECURITY_DOMAIN_APPLY")
public class SecurityDomainApply extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public static final String APPLY_TYPE_NEW = "申请安全域";
	
	public static final String APPLY_TYPE_ARCHIVE = "归档安全域";
	
	public static final String APPLY_TYPE_MODIFY = "修改安全域";
	
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
	private Integer spaceRule;

	/**
	 * 安全域模式<br/>
	 * 1-主安全域<br/>
	 * 2-公共第三方安全域<br/>
	 * 3-DAP模式<br/>
	 * 4-Token模式
	 */
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

	/** 正式生效的安全域ID */
	private Long sdId;

	/** 申请类型：安全域申请，安全域修改 */
	private String applyType;

	/** 申请时间 */
	@DateFormat
	private Calendar applyDate;

	/** 处理结果 */
	private String applyResult;
	
	private Requistion requistion;
	
	/** 密钥标识ENC */
	private String keyProfileENC;
	private String hsmkeyConfigENC;
	
	/** 密钥标识MAC */
	private String keyProfileMAC;
	private String hsmkeyConfigMAC;
	
	/** 密钥标识DEK */
	private String keyProfileDEK;
	private String hsmkeyConfigDEK;
	
	/** 业务平台地址 */
	private String businessPlatformUrl;

	/** 业务平台服务名 */
	private String serviceName;
	
	@OneToOne
	@PrimaryKeyJoinColumn
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Requistion getRequistion() {
		return requistion;
	}
	
	public void setRequistion(Requistion requistion) {
		this.requistion = requistion;
	}

	@Id
	@GeneratedValue(generator = "pkGenerator")
	@GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "requistion"))
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

	public String getSdName() {
		return sdName;
	}

	public void setSdName(String sdName) {
		this.sdName = sdName;
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
		if (installParams != null) installParams = installParams.toUpperCase();
		this.installParams = installParams;
		if(this.installParams != null && !StringUtils.isBlank(this.installParams) && this.installParams.startsWith("C9")) {
			TlvObject C9 = TlvObject.parse(this.installParams);
			byte[] TAG49 = null;
			try {
				C9 = TlvObject.parse(C9.getByTag("c9"));
				TAG49 = C9.getByTag("49");
			} catch (Exception e) {}
			this.spaceRule = ArrayUtils.isNotEmpty(TAG49) ? SecurityDomain.FIXED_SPACE : SecurityDomain.UNFIXABLE_SPACE;
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

	@ManyToOne
	@JoinColumn(name = "SP_ID")
	@Cascade({ CascadeType.MERGE, CascadeType.PERSIST })
	@LazyToOne(LazyToOneOption.PROXY)
	public SpBaseInfo getSp() {
		return sp;
	}

	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}

	@ManyToOne
	@JoinColumn(name = "LOAD_MODULE_ID")
	@Cascade({ CascadeType.MERGE, CascadeType.PERSIST })
	@LazyToOne(LazyToOneOption.PROXY)
	public LoadModule getLoadModule() {
		return loadModule;
	}

	public void setLoadModule(LoadModule loadModule) {
		this.loadModule = loadModule;
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

	public Long getSdId() {
		return sdId;
	}

	public void setSdId(Long sdId) {
		this.sdId = sdId;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public Calendar getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Calendar applyDate) {
		this.applyDate = applyDate;
	}

	public String getApplyResult() {
		return applyResult;
	}

	public void setApplyResult(String applyResult) {
		this.applyResult = applyResult;
	}

	@Column(name = "key_profile_enc")
	public String getKeyProfileENC() {
		return keyProfileENC;
	}

	public void setKeyProfileENC(String keyProfileENC) {
		this.keyProfileENC = keyProfileENC;
	}

	@Column(name = "key_profile_mac")
	public String getKeyProfileMAC() {
		return keyProfileMAC;
	}

	public void setKeyProfileMAC(String keyProfileMAC) {
		this.keyProfileMAC = keyProfileMAC;
	}

	@Column(name = "key_profile_dek")
	public String getKeyProfileDEK() {
		return keyProfileDEK;
	}

	public void setKeyProfileDEK(String keyProfileDEK) {
		this.keyProfileDEK = keyProfileDEK;
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

	@Transient
	public String getHsmkeyConfigENC() {
		return hsmkeyConfigENC;
	}

	public void setHsmkeyConfigENC(String hsmkeyConfigENC) {
		this.hsmkeyConfigENC = hsmkeyConfigENC;
	}

	@Transient
	public String getHsmkeyConfigMAC() {
		return hsmkeyConfigMAC;
	}

	public void setHsmkeyConfigMAC(String hsmkeyConfigMAC) {
		this.hsmkeyConfigMAC = hsmkeyConfigMAC;
	}

	@Transient
	public String getHsmkeyConfigDEK() {
		return hsmkeyConfigDEK;
	}

	public void setHsmkeyConfigDEK(String hsmkeyConfigDEK) {
		this.hsmkeyConfigDEK = hsmkeyConfigDEK;
	}

}
