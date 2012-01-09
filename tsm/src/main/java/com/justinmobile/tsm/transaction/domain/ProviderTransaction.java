package com.justinmobile.tsm.transaction.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "PROVIDER_TRANSACTION")
public class ProviderTransaction extends AbstractEntity {

	private static final long serialVersionUID = 2219950965095007259L;

	private Long id;

	private String sessionId;

	private String appAid;

	private Integer batchIndex;

	private String procedureName;

	private Integer commType;

	private Integer originalType;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_PROVIDER_TRANSACTION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getAppAid() {
		return appAid;
	}

	public void setAppAid(String appAid) {
		this.appAid = appAid;
	}

	public Integer getBatchIndex() {
		return batchIndex;
	}

	public void setBatchIndex(Integer batchIndex) {
		this.batchIndex = batchIndex;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public int increaseBatchIndex() {
		if (this.batchIndex == null) {
			this.batchIndex = 0;
		}
		this.batchIndex++;
		return this.batchIndex;
	}

	public Integer getCommType() {
		return commType;
	}

	public void setCommType(Integer commType) {
		this.commType = commType;
	}

	@Column(name = "ORIGINAL_TYPE")
	public Integer getOriginalType() {
		return originalType;
	}

	public void setOriginalType(Integer originalType) {
		this.originalType = originalType;
	}
}
