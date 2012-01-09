package com.justinmobile.tsm.cms2ac.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "APDU_MESSAGE")
public class Apdu extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3202987210658250848L;

	public static final String DEFAULT_ICV = "0000000000000000";

	protected Long id;

	protected Integer batchNo;

	protected Integer index;

	protected String rawHex;

	protected String securityHex;

	protected String icv;

	protected boolean firstPair;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APDU_MESSAGE") })
	public Long getId() {
		return id;
	}

	public Integer getBatchNo() {
		return batchNo;
	}

	@Column(name = "APDU_INDEX")
	public Integer getIndex() {
		return index;
	}

	public String getRawHex() {
		return rawHex;
	}

	public String getSecurityHex() {
		return securityHex;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setBatchNo(Integer batchNo) {
		this.batchNo = batchNo;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public void setRawHex(String rawHex) {
		this.rawHex = rawHex;
	}

	public void setSecurityHex(String securityHex) {
		this.securityHex = securityHex;
	}

	public String getIcv() {
		return icv;
	}

	public void setIcv(String icv) {
		this.icv = icv;
	}

	public boolean isFirstPair() {
		return firstPair;
	}

	public void setFirstPair(boolean firstPair) {
		this.firstPair = firstPair;
	}
}
