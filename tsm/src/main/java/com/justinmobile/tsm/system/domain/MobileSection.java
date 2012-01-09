package com.justinmobile.tsm.system.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "MOBILE_SECTION")
public class MobileSection extends AbstractEntity{

	private static final long serialVersionUID = -1502012477L;

	/** 主键 */
	private Long id;
	/** 省份 */
	private String province;
	/** 城市 */
	private String city;
	/** 区号 */
	private String district;
	/** 万号段 */
	private String paragraph;
	/** 归属SCP号码 */
	private String scpNumber;
	/** SCP ID */
	private String scpId;
	/** 归属SCP名称 */
	private String scpName;
	/** 彩信中心名称 */
	private String mmscenterName;
	/** 彩信中心ID */
	private String mmscenterId;
	/** 启用局数据号 */
	private String officeData;
	
	public void setDataByIndex(int index, String data) {
		switch (index) {
		case 0:
			this.province = data;
			break;
		case 1:
			this.city = data;
			break;
		case 2:
			this.district = data;
			break;
		case 3:
			this.paragraph = data;
			break;
		case 4:
			this.scpNumber = data;
			break;
		case 5:
			this.scpId = data;
			break;
		case 6:
			this.scpName = data;
			break;
		case 7:
			this.mmscenterName = data;
			break;
		case 8:
			this.mmscenterId = data;
			break;
		case 9:
			this.officeData = data;
			break;
		default:
			break;
		}
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_MOBILE_SECTION") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getParagraph() {
		return paragraph;
	}

	public void setParagraph(String paragraph) {
		this.paragraph = paragraph;
	}

	public String getScpNumber() {
		return scpNumber;
	}

	public void setScpNumber(String scpNumber) {
		this.scpNumber = scpNumber;
	}

	public String getScpId() {
		return scpId;
	}

	public void setScpId(String scpId) {
		this.scpId = scpId;
	}

	public String getScpName() {
		return scpName;
	}

	public void setScpName(String scpName) {
		this.scpName = scpName;
	}

	public String getMmscenterName() {
		return mmscenterName;
	}

	public void setMmscenterName(String mmscenterName) {
		this.mmscenterName = mmscenterName;
	}

	public String getMmscenterId() {
		return mmscenterId;
	}

	public void setMmscenterId(String mmscenterId) {
		this.mmscenterId = mmscenterId;
	}

	public String getOfficeData() {
		return officeData;
	}

	public void setOfficeData(String officeData) {
		this.officeData = officeData;
	}
}