package com.justinmobile.tsm.customer.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "MOBILE_TYPE")
public class MobileType extends AbstractEntity{

	private static final long serialVersionUID = -127447268L;


	private Long id;

	private String brandChs;

	private String brandEng;

	private String type;

	private byte[] icon;

	private String originalOsKey;

	private String j2meKey;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_MOBILE_TYPE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBrandChs() {
		return brandChs;
	}

	public void setBrandChs(String brandChs) {
		this.brandChs = brandChs;
	}

	public String getBrandEng() {
		return brandEng;
	}

	public void setBrandEng(String brandEng) {
		this.brandEng = brandEng;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getIcon() {
		return icon;
	}

	public void setIcon(byte[] icon) {
		this.icon = icon;
	}

	public String getOriginalOsKey() {
		return originalOsKey;
	}

	public void setOriginalOsKey(String originalOsKey) {
		this.originalOsKey = originalOsKey;
	}

	public String getJ2meKey() {
		return j2meKey;
	}

	public void setJ2meKey(String j2meKey) {
		this.j2meKey = j2meKey;
	}


}