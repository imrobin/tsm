package com.justinmobile.tsm.endpoint.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.type.EnumType;

import com.justinmobile.core.dao.support.EnumUserType;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

@Entity
@Table(name = "PUSH_SMS")
public class PushSms extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1586093204112137950L;

	/** 主键 */
	private Long id;

	/** Push短信序列号 */
	private String serial;

	/** 手机号 */
	private String mobileNo;

	/** 卡号 */
	private String cardNo;

	/** 应用AID */
	private String aid;

	/** 应用版本 */
	private String version;

	/** */
	private Operation operation;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_PUSH_SMS") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Type(type = EnumUserType.NAME, parameters = @Parameter(name = EnumType.ENUM, value = Operation.NAME))
	public Operation getOperation() {
		return operation;
	}

}
