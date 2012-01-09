package com.justinmobile.log.domain;

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

@Entity
@Table(name = "SYS_OPERATE_LOG_PARAM")
public class OperateLogParam extends AbstractEntity {

	private static final long serialVersionUID = -3695672852497275807L;

	/** 主键 */
	private Long id;

	/** 参数名 */
	private String key;

	/** 参数值 */
	private String value;

	/** 日志 */
	private OperateLog operateLog;

	@ManyToOne()
	@JoinColumn(name = "LOG_ID")
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	@LazyToOne(LazyToOneOption.PROXY)
	public OperateLog getOperateLog() {
		return operateLog;
	}

	public void setOperateLog(OperateLog operateLog) {
		this.operateLog = operateLog;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_OPERATE_LOG_PARAM") })
	public Long getId() {
		return id;
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

	public void setId(Long id) {
		this.id = id;
	}

}