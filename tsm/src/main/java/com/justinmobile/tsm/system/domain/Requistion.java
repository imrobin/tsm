package com.justinmobile.tsm.system.domain;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;

@Entity
@Table(name = "Requistion")
public class Requistion extends AbstractEntity {

	private static final long serialVersionUID = 4024470864617341782L;

	public static final Integer RESULT_PASS = 1;
	public static final Integer RESULT_REJECT = 0;
	public static final String RESULT_PASS_CH = "通过";
	public static final String RESULT_REJECT_CH = "未通过";

	/** 应用发布申请 */
	public static final Integer TYPE_APP_PUBLISH = 11;
	/** 应用归档申请 */
	public static final Integer TYPE_APP_ARCHIVE = 12;
	/** 应用信息修改申请 */
	public static final Integer TYPE_APP_MODIFY = 13;
	/** 应用上传申请 */
	public static final Integer TYPE_APP_UPLOAD = 14;
	/** 安全域发布申请 */
	public static final Integer TYPE_SD_PUBLISH = 21;
	/** 安全域归档申请 */
	public static final Integer TYPE_SD_ARCHIVE = 22;
	/** 安全域修改申请 */
	public static final Integer TYPE_SD_MODIFY = 23;
	/** 应用提供商注册申请 */
	public static final Integer TYPE_SP_REGISTER = 31;
	/** 应用提供商修改申请 */
	public static final Integer TYPE_SP_MODIFY = 32;

	/** 1-未审核 */
	public static final Integer STATUS_INIT = 1;
	/** 2-已撤销 */
	public static final Integer STATUS_UNDO = 2;
	/** 3-审核通过 */
	public static final Integer STATUS_PASS = 3;
	/** 4-审核未通过 */
	public static final Integer STATUS_REJECT = 4;

	public static final String OPINION_DEFAULT_AGREE = "同意";
	public static final String OPINION_DEFAULT_REJECT = "拒绝";

	public static final String REASON_DEFAULT_SD_APPLY = "安全域发布申请";
	public static final String REASON_DEFAULT_APP_UPlOAD_APPLY = "应用上传申请";
	public static final String REASON_DEFAULT_SD_APPLY_ARCHIVE = "安全域归档申请";
	public static final String REASON_DEFAULT_SD_APPLY_MODIFY = "安全域修改申请";
	public static final String REASON_DEFAULT_SP_APPLY = "SP注册申请";
	public static final String REASON_DEFAULT_SP_APPLY_MODIFY = "SP修改申请";
	public static final String REASON_DEFAULT_APP_APPLY = "应用发布申请";

	private Long id;

	/**
	 * 申请的类型 11 --应用发布申请<br/>
	 * 12 --应用归档申请<br/>
	 * 13 --应用信息修改申请<br/>
	 * 21 --安全域发布申请<br/>
	 * 22 --安全域归档申请<br/>
	 * 31 --SP注册申请<br/>
	 */
	@ResourcesFormat(key = "requistion.type")
	private Integer type;

	/**
	 * 原值
	 */
	private Long originalId;

	//private SecurityDomainApply securityDomainApply;
	
	/**
	 * 新值
	 */
	private Long newId;

	/**
	 * 请求审核理由
	 */
	private String reason;

	/**
	 * 审核提交时间
	 */
	@DateFormat
	private Calendar submitDate;

	/**
	 * 审核意见
	 */
	private String opinion;

	/**
	 * 审核时间
	 */
	@DateFormat
	private Calendar reviewDate;

	/**
	 * 申请人第一次查看的时间
	 */
	@DateFormat
	private Calendar applicantReview;
	
	/**
	 * 状态<br/>
	 * 1-未审核<br/>
	 * 2-已撤销<br/>
	 * 3-审核通过<br/>
	 * 4-审核未通过<br/>
	 */
	@ResourcesFormat(key = "requistion.status")
	private Integer status;

	/**
	 * 审核结果<br/>
	 * 0-不通过<br/>
	 * 1-通过
	 */
	@ResourcesFormat(key = "requistion.result")
	private Integer result;

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_REQUEST_DATA") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public Calendar getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Calendar submitDate) {
		this.submitDate = submitDate;
	}

	public Calendar getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Calendar reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getOriginalId() {
		return originalId;
	}

	public void setOriginalId(Long originalId) {
		this.originalId = originalId;
	}

	public Long getNewId() {
		return newId;
	}

	public void setNewId(Long newId) {
		this.newId = newId;
	}

	public Calendar getApplicantReview() {
		return applicantReview;
	}

	public void setApplicantReview(Calendar applicantReview) {
		this.applicantReview = applicantReview;
	}

}
