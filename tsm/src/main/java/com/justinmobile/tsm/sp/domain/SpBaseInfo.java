package com.justinmobile.tsm.sp.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

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
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.application.domain.Application;

@Entity
@Table(name = "SP_BASE_INFO")
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SpBaseInfo extends AbstractEntity {

	private static final long serialVersionUID = 104413085L;

	public static final String DEFAULT_PASSWORD = "000000";

	public static final int UNREGISTERED = 0;

	public static final int NORMAL = 1;

	public static final int REGFAILED = 2;

	public static final int LOGOUT = 3;

	public static final int SP_TYPE_MOBILE_SELF = 0;

	public static final int SP_TYPE_MOBILE_OTHER = 1;

	public static final int SP_TYPE_OTHERS = 2;

	public static final int LOCAL_FLAG_LOCAL = 1;

	public static final int LOCAL_FLAG_GLOBAL = 0;

	public final static int INBLACK = 1;

	public final static int NOT_INBLACK = 0;

	/** 0-初始(等待审核) */
	public static final int STATUS_INIT = 0;

	/** 1-可用(审核通过) */
	public static final int STATUS_AVALIABLE = 1;

	public static final int LOCK = 0;

	public static final int UNLOCK = 1;

	/** 主键 */
	private Long id;

	/** 应用提供商编号 */
	private String no;

	/** 应用提供商名称 */
	private String name;

	/** 应用提供商简称 */
	private String shortName;

	/** 所在地 */
	private String locationNo;

	/**
	 * 应用提供商类型<br/>
	 * 1-全网移动<br/>
	 * 2-本地移动<br/>
	 * 3-全网SP<br/>
	 * 4-本地SP<br/>
	 */
	public static final int TYPE_GLOBAL_YD = 1;
	public static final int TYPE_LOCAL_YD = 2;
	public static final int TYPE_GLOBAL_SP = 3;
	public static final int TYPE_LOCAL_SP = 4;
	@ResourcesFormat(key = "sp.type")
	private Integer type;

	/** 地址 */
	private String address;

	/** 工商局注册编号 */
	private String registrationNo;

	/** 经营许可证编号 */
	private String certificateNo;

	/** 法人姓名 */
	private String legalPersonName;

	/** 法人证件类型 */
	private String legalPersonIdType;

	/** 法人证件号 */
	private String legalPersonIdNo;

	/** 联系人姓名 */
	private String contactPersonName;

	/** 联系人手机号 */
	private String contactPersonMobileNo;

	/**
	 * 企业性质<br/>
	 * 1-国有<br/>
	 * 2-合作<br/>
	 * 3-合资<br/>
	 * 4-独资<br/>
	 * 5-集体<br/>
	 * 6-私营<br/>
	 * 7-个体工商户<br/>
	 * 8-报关<br/>
	 * 9-其他<br/>
	 * */
	@ResourcesFormat(key = "sp.firmNature")
	private Integer firmNature;

	/**
	 * 企业规模enterprise scale<br/>
	 * 1-小型(100人以下)small<br/>
	 * 2-中型(100-500人)midium<br/>
	 * 3-大型(500人以上)large<br/>
	 * 
	 * */
	@ResourcesFormat(key = "sp.firmScale")
	private Integer firmScale;

	/** 企业LOGO */
	private byte[] firmLogo;

	/** 是否本地SP */
	private Integer localFlag;

	/** 提供应用类型 */
	private Integer provideType;

	/** 管理员对该应用提供商的注解 */
	private String comments;

	private SysUser sysUser;

	@ResourcesFormat(key = "customercard.inBlack")
	private Integer inBlack = NOT_INBLACK;

	/** 是否有LOGO */
	private String hasLogo;

	private int hasLock;

	private String rid;

	/** 附件名称 */
	private String attachmentName;

	/** 附件 */
	private byte[] attachment;

	private String spSummary;

	public int getHasLock() {
		return hasLock;
	}

	public void setHasLock(int hasLock) {
		this.hasLock = hasLock;
	}

	@Column(name = "INBLACK")
	public Integer getInBlack() {
		return inBlack;
	}

	public void setInBlack(Integer inBlack) {
		this.inBlack = inBlack;
	}

	/**
	 * 状态<br/>
	 * 0-初始<br/>
	 * 1-审核通过<br/>
	 */
	@ResourcesFormat(key = "sp.status")
	private Integer status;

	private Set<SpBlackList> blackListItems = new HashSet<SpBlackList>(0);

	private Set<Application> applications = new HashSet<Application>();

	private int applicationSize;

	@SuppressWarnings("unused")
	private int availableApplicationSize;

	public void setAvailableApplicationSize() {
		this.availableApplicationSize = this.getAvailableApplicationSize();
	}

	@Transient
	public int getApplicationSize() {
		return applicationSize;
	}

	public void setApplicationSize() {
		this.applicationSize = getApplications().size();
	}

	@Transient
	public int getAvailableApplicationSize() {
		Set<Application> applications = getApplications();
		int count = 0;
		for (Iterator<Application> it = applications.iterator(); it.hasNext();) {
			Application application = it.next();
			if (application.getStatus() != null
					&& application.getStatus().equals(
							Application.STATUS_PUBLISHED)) {
				count++;
			}
		}
		return count;
	}

	@OneToMany(mappedBy = "sp")
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<Application> getApplications() {
		return applications;
	}

	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}

	@OneToMany(mappedBy = "sp")
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SpBlackList> getBlackListItems() {
		return blackListItems;
	}

	public void setBlackListItems(Set<SpBlackList> blackListItems) {
		this.blackListItems = blackListItems;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SysUser getSysUser() {
		return sysUser;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	@Id
	@GeneratedValue(generator = "pkGenerator")
	@GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "sysUser"))
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocationNo() {
		return locationNo;
	}

	public void setLocationNo(String locationNo) {
		this.locationNo = locationNo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public String getCertificateNo() {
		return certificateNo;
	}

	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}

	public String getLegalPersonName() {
		return legalPersonName;
	}

	public void setLegalPersonName(String legalPersonName) {
		this.legalPersonName = legalPersonName;
	}

	public String getLegalPersonIdType() {
		return legalPersonIdType;
	}

	public void setLegalPersonIdType(String legalPersonIdType) {
		this.legalPersonIdType = legalPersonIdType;
	}

	public String getLegalPersonIdNo() {
		return legalPersonIdNo;
	}

	public void setLegalPersonIdNo(String legalPersonIdNo) {
		this.legalPersonIdNo = legalPersonIdNo;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

	public String getContactPersonMobileNo() {
		return contactPersonMobileNo;
	}

	public void setContactPersonMobileNo(String contactPersonMobileNo) {
		this.contactPersonMobileNo = contactPersonMobileNo;
	}

	public Integer getFirmNature() {
		return firmNature;
	}

	public void setFirmNature(Integer firmNature) {
		this.firmNature = firmNature;
	}

	public Integer getFirmScale() {
		return firmScale;
	}

	public void setFirmScale(Integer firmScale) {
		this.firmScale = firmScale;
	}

	public Integer getLocalFlag() {
		return localFlag;
	}

	public void setLocalFlag(Integer lacalFlag) {
		this.localFlag = lacalFlag;
	}

	public Integer getProvideType() {
		return provideType;
	}

	public void setProvideType(Integer provideType) {
		this.provideType = provideType;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Lob
	public byte[] getFirmLogo() {
		return firmLogo;
	}

	public void setFirmLogo(byte[] firmLogo) {
		this.firmLogo = firmLogo;
	}

	@Transient
	public String getHasLogo() {
		return hasLogo;
	}

	public void setHasLogo(boolean hasLogo) {
		this.hasLogo = hasLogo ? "有" : "无";
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		if (!StringUtils.isBlank(rid)) {
			rid = rid.toUpperCase();
		}
		this.rid = rid;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	@Lob
	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public String getSpSummary() {
		return spSummary;
	}

	public void setSpSummary(String spSummary) {
		this.spSummary = spSummary;
	}

	public enum FIRM_NATUE {
		STATE_OWNED_BUSINESS(1), COOPERATIVE_ENTERPRISE(2), JOINT_VENTURE(3), INDIVIDUAL_PROPRIETOSHIP(
				4), COLLECTIVE_ENTERPRISE(5), PRIVATE_BUSINESS(6), INDIVIDUALLY_OWNED_BUSINESS(
				7), CUSTOMER_CLEARANCE(8), OTHERS(9);

		private Integer val;

		FIRM_NATUE(Integer inVal) {
			this.val = inVal;
		}

		public Integer getValue() {
			return this.val;
		}
	}

	public enum FIRM_SCALE {
		SMALL(1), MIDIUM(2), LARGE(3);

		private Integer val;

		FIRM_SCALE(Integer val) {
			this.val = val;
		}

		public Integer getValue() {
			return this.val;
		}
	}
}