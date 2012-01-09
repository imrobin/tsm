package com.justinmobile.tsm.cms2ac.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "PARAM_DOMAINKEY")
public class ParamDomainKey extends AbstractEntity {
	
	private static final long serialVersionUID = -1253998007911668924L;

	private Long id;
	
	private String KeyID;
	
	private String KeyType;
	
	private String KeyValue;
	
	private String KeyCheckLength;
	
	private String KeyCheck;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_PARAM_DOMAINKEY") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKeyID() {
		return KeyID;
	}

	public void setKeyID(String keyID) {
		KeyID = keyID;
	}

	public String getKeyType() {
		return KeyType;
	}

	public void setKeyType(String keyType) {
		KeyType = keyType;
	}

	public String getKeyValue() {
		return KeyValue;
	}

	public void setKeyValue(String keyValue) {
		KeyValue = keyValue;
	}

	public String getKeyCheckLength() {
		return KeyCheckLength;
	}

	public void setKeyCheckLength(String keyCheckLength) {
		KeyCheckLength = keyCheckLength;
	}

	public String getKeyCheck() {
		return KeyCheck;
	}

	public void setKeyCheck(String keyCheck) {
		KeyCheck = keyCheck;
	}

}
