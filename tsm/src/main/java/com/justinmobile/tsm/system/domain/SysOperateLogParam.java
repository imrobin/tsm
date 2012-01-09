package com.justinmobile.tsm.system.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "SYS_OPERATE_LOG_PARAM")
public class SysOperateLogParam extends AbstractEntity {

	private static final long serialVersionUID = 371546881482531273L;

	private Long Id;

	/**
	 * 参数名称
	 */
	private String key;

	/**
	 * 参数值
	 */
	private String value;

	/**
	 * 所属日志
	 */
	private SysOperateLog log;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOG_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public SysOperateLog getLog() {
		return log;
	}

	public void setLog(SysOperateLog log) {
		this.log = log;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_OPERATE_LOG_PARAM") })
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
