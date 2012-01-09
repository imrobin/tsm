package com.justinmobile.tsm.sp.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

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
import com.justinmobile.tsm.system.domain.Requistion;

@Entity
@Table(name = "SP_BASE_INFO_APPLY")
public class SpBaseInfoApply extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public static final String APPLY_TYPE_REGISTER = "SP注册申请";

	public static final String APPLY_TYPE_MODIFY = "SP修改申请";

	/** 申请类型：安全域申请，安全域修改 */
	private String applyType;

	/** 申请时间 */
	@DateFormat
	private Calendar applyDate;

	/** 处理结果 */
	private String applyResult;

	private Requistion requistion;

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

	/** 附件名称 */
	private String attachmentName;

	/** 附件 */
	private byte[] attachment;

	/** 是否本地SP */
	private Integer localFlag;

	/** 提供应用类型 */
	private Integer provideType;

	/** 管理员对该应用提供商的注解 */
	private String comments;

	private String email;

	@ResourcesFormat(key = "customercard.inBlack")
	private Integer inBlack = SpBaseInfo.NOT_INBLACK;

	/** 是否有LOGO */
	private String hasLogo;

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

	private String rid;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@Transient
	public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	@Transient
	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

}
