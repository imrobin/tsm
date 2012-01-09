package com.justinmobile.tsm.application.domain;

import java.util.Calendar;

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
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Entity
@Table(name = "APPLICATION_IDENTIFIER")
public class ApplicationIdentifier extends AbstractEntity {

	private static final long serialVersionUID = -3634063410127839866L;

	public static final int TYPE_SD = 1;
	public static final int TYPE_APP = 2;

	public static final int STATUS_NORMAL = 1;
	public static final int STATUS_VOID = 0;

	public static final int USED_YES = 1;
	public static final int USED_NO = 0;

	public static final String SEQ_SD  = "SEQ_APPLICATION_IDENTIFIER_SD";
	public static final String SEQ_APP = "SEQ_APPLICATION_IDENTIFIER_APP";
	
	private Long id;

	/** AID类型：1－SD，2－APP */
	@ResourcesFormat(key = "aid.type")
	private Integer type;

	/** 十六进制字符串 */
	private String aid;

	/** 应用提供商 */
	private SpBaseInfo sp;

	/** 归属 */
	private Integer belongto;

	/** 应用类型 */
	private Integer appType;

	/** 行业 */
	private Integer industry;

	/** 分配时间 */
	@DateFormat
	private Calendar assignmentTime = Calendar.getInstance();

	/** 状态：1－正常，0－作废 */
	@ResourcesFormat(key = "aid.status")
	private Integer status = STATUS_NORMAL;

	/** 使用：1－是，0－否 */
	@ResourcesFormat(key = "aid.used")
	private Integer used = USED_NO;

	/** 生成AID的数量 */
	private int size = 1;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_IDENTIFIER") })
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

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
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

	public Integer getBelongto() {
		return belongto;
	}

	public void setBelongto(Integer belongto) {
		this.belongto = belongto;
	}

	public Integer getIndustry() {
		return industry;
	}

	public void setIndustry(Integer industry) {
		this.industry = industry;
	}

	public Calendar getAssignmentTime() {
		return assignmentTime;
	}

	public void setAssignmentTime(Calendar assignmentTime) {
		this.assignmentTime = assignmentTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	@Transient
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
