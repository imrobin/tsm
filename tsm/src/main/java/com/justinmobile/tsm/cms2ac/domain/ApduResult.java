package com.justinmobile.tsm.cms2ac.domain;

import static com.justinmobile.core.utils.ByteUtils.leftSubArray;
import static com.justinmobile.core.utils.ByteUtils.rightSubArray;
import static com.justinmobile.core.utils.ByteUtils.toHexString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.utils.ConvertUtils;

@Entity
@Table(name = "APDU_RESULT")
public class ApduResult extends AbstractEntity {

	private static final long serialVersionUID = 930584925L;

	/************************************************/

	private Long id;

	private Integer batchNo;

	private Integer index;

	private String rawHex;

	private String securityHex;

	private String icv;

	private String rMac;

	private boolean firstPair;

	/************************************************/

	private byte[] data;

	private byte sw1;

	private byte sw2;

	private boolean havingData;

	private Cms2acParam cms2acParam;

	public ApduResult() {

	}

	public ApduResult(byte[] resultBytes, int batchNo, int index) {
		parseStateWords(resultBytes);

		this.rawHex = toHexString(resultBytes);
		this.batchNo = batchNo;
		this.index = index;

		parseStateWords(ConvertUtils.hexString2ByteArray(this.rawHex));
	}

	public void parseStateWords(byte[] resultBytes) {
		if (resultBytes.length < 2) {
			throw new IllegalArgumentException();
		}
		this.havingData = resultBytes.length > 2;
		if (havingData) {
			this.data = leftSubArray(resultBytes, resultBytes.length - 2);
		}
		byte[] swBytes = rightSubArray(resultBytes, resultBytes.length - 2);
		this.sw1 = swBytes[0];
		this.sw2 = swBytes[1];
	}

	public ApduResult(byte[] resultBytes, int batchNo, int index, byte[] securedResult) {
		// this(resultBytes, batchNo, index);
		this.securityHex = toHexString(securedResult);
		this.batchNo = batchNo;
		this.index = index;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CM2AC_PARAM_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public Cms2acParam getCms2acParam() {
		return cms2acParam;
	}

	public void setCms2acParam(Cms2acParam cms2acParam) {
		this.cms2acParam = cms2acParam;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APDU_RESULT") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(Integer batchNo) {
		this.batchNo = batchNo;
	}

	@Column(name = "APDU_INDEX")
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getRawHex() {
		return rawHex;
	}

	public void setRawHex(String rawHex) {
		this.rawHex = rawHex;
	}

	public String getSecurityHex() {
		return securityHex;
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

	public String getRMac() {
		return rMac;
	}

	public void setRMac(String mac) {
		rMac = mac;
	}

	@Transient
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Transient
	public byte getSw1() {
		return sw1;
	}

	public void setSw1(byte sw1) {
		this.sw1 = sw1;
	}

	@Transient
	public byte getSw2() {
		return sw2;
	}

	public void setSw2(byte sw2) {
		this.sw2 = sw2;
	}

	@Transient
	public boolean isHavingData() {
		return havingData;
	}

	public void setHavingData(boolean havingData) {
		this.havingData = havingData;
	}
}