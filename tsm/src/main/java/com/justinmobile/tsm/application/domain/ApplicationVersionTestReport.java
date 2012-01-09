package com.justinmobile.tsm.application.domain;

import java.util.Calendar;

import javax.persistence.Entity;
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
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.card.domain.CardBaseInfo;

@Entity
@Table(name = "APP_VER_TEST_REPORT")
public class ApplicationVersionTestReport extends AbstractEntity {
	
	/**  */
	private static final long serialVersionUID = -8541969888527746573L;
	
	public static final Integer RESULT_PASS = 1;
	public static final Integer RESULT_UNPASS = 0;
	
	
	private Long id;
	/** 测试日期 */
	@DateFormat(format="yyyy-MM-dd")
	private Calendar testDate;
	/** 测试手机号 */
	private String mobileNo;
	/** SEID */
	private String seId;
	/** NFC终端型号 */
	private String modelType;
	/** SE芯片类型 */
	private String seType;
	/** CMS2AC版本 */ 
	private String cms2acVer;
	/**  测试结果*/
	@ResourcesFormat(key="appTestReport.result")
	private Integer result;
	/** 卡批次**/
	private CardBaseInfo cardBaseInfo;
	/** 结果说明*/
	private String resultComment;
	/** 作者*/
	private String author;
	/**  所属的应用版本*/
	private ApplicationVersion appVer;
	
	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence",parameters = {@Parameter(name = "sequence", value = "SEQ_APP_VER_TEST_REPORT")})
	public Long getId() {
		return id;
	}

	public Calendar getTestDate() {
		return testDate;
	}

	public void setTestDate(Calendar testDate) {
		this.testDate = testDate;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getSeId() {
		return seId;
	}

	public void setSeId(String seId) {
		this.seId = seId;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getSeType() {
		return seType;
	}

	public void setSeType(String seType) {
		this.seType = seType;
	}

	public String getCms2acVer() {
		return cms2acVer;
	}

	public void setCms2acVer(String cms2acVer) {
		this.cms2acVer = cms2acVer;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	@ManyToOne
	@JoinColumn(name = "CARD_BASE_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public CardBaseInfo getCardBaseInfo() {
		return cardBaseInfo;
	}

	public void setCardBaseInfo(CardBaseInfo cardBaseInfo) {
		this.cardBaseInfo = cardBaseInfo;
	}

	public String getResultComment() {
		return resultComment;
	}

	public void setResultComment(String resultComment) {
		this.resultComment = resultComment;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@ManyToOne
	@JoinColumn(name = "APP_VER_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public ApplicationVersion getAppVer() {
		return appVer;
	}
	public void setAppVer(ApplicationVersion appVer) {
		this.appVer = appVer;
	}
}
