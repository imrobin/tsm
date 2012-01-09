package com.justinmobile.log.domain;

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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;

@Entity
@Table(name="SYS_OPERATE_LOG")
public class OperateLog extends AbstractEntity {

	private static final long serialVersionUID = -2055615106694571679L;

	/** 主键 */
	private Long id;

	/** 操作时间 */
	@DateFormat
	private Calendar time;

	/** 操作名称 */
	private String operateName;
	
	/** 描述 */
	private String description;
	
	/** 标志0：成功1：失败 */
	@ResourcesFormat(key="log.success")
	private Integer success;

	/** 日志参数 */
	private Set<OperateLogParam> logParams = new HashSet<OperateLogParam>();

	/** 操作员登录名 */
	private String loginName;

	@OneToMany(mappedBy = "operateLog", orphanRemoval = true)
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<OperateLogParam> getLogParams() {
		return logParams;
	}

	public void setLogParams(Set<OperateLogParam> logParams) {
		this.logParams = logParams;
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

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public Integer getSuccess() {
		return success;
	}

	public void setSuccess(Integer success) {
		this.success = success;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}
