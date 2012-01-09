package com.justinmobile.tsm.system.domain;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "SYS_OPERATE_LOG")
public class SysOperateLog extends AbstractEntity {

	private static final long serialVersionUID = -6623543091876086949L;

	private Long id;

	/**
	 * 操作名
	 */
	private String operateName;

	/**
	 * 用户名
	 */
	private String loginName;

	/**
	 * 操作描述（包括记录错误信息）
	 */
	private String description;

	/**
	 * 操作结果(是否成功)
	 */
	private Boolean success;

	/**
	 * 操作时间
	 */
	private Calendar time;

	private Set<SysOperateLogParam> params = new HashSet<SysOperateLogParam>(0);

	@OneToMany(mappedBy = "log")
	@Cascade(value = CascadeType.ALL)
	public Set<SysOperateLogParam> getParams() {
		return params;
	}

	public void setParams(Set<SysOperateLogParam> params) {
		this.params = params;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_OPERATE_LOG") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}
}
