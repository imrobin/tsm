package com.justinmobile.tsm.system.domain;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "SYSTEM_PARAMS")
public class SystemParams extends AbstractEntity{

	private static final long serialVersionUID = -627110731L;

	/** 主键 */
	private Long id;
	
	/** 参数类型 */
	private String type;
	
	/** 参数名 */
	private String key;
	
	/** 参数值 */
	private String value;
	
	/** 描述 */
	private String description;
	
	public enum SystemType {
		
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYSTEM_PARAMS") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> toMap() {
		return super.toMap("id type description", null);
	}

	

}