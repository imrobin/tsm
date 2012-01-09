package com.justinmobile.tsm.cms2ac.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.application.domain.Application;

@Entity
@Table(name = "APPLICATION_KEY_PROFILE")
public class ApplicationKeyProfile extends AbstractEntity {

	private static final long serialVersionUID = 845261184132746349L;

	/**
	 * 1-传输密钥
	 */
	public static final int TYPE_TK = 1;

	/**
	 * 2-敏感数据加密密钥
	 */
	public static final int TYPE_KEK = 2;

	/** 主键 */
	private Long id;

	/** 所属应用 */
	private Application application;

	/** 密钥版本 */
	private Integer keyVersion;

	/** 密钥索引 */
	private Integer keyIndex;

	/** 密钥类型 */
	private Integer keyType;

	/** 密钥标识 */
	private Integer keyId;

	/** 密钥校验值 */
	private String keyValue;

	private List<HsmkeyConfig> hsmKeyConfigs = new ArrayList<HsmkeyConfig>();

	@ManyToOne
	@JoinColumn(name = "APPLICATION_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_KEY_PROFILE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getKeyVersion() {
		return keyVersion;
	}

	public void setKeyVersion(Integer keyVersion) {
		this.keyVersion = keyVersion;
	}

	public Integer getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(Integer keyIndex) {
		this.keyIndex = keyIndex;
	}

	public Integer getKeyType() {
		return keyType;
	}

	public void setKeyType(Integer keyType) {
		this.keyType = keyType;
	}

	public Integer getKeyId() {
		return keyId;
	}

	public void setKeyId(Integer keyId) {
		this.keyId = keyId;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public void setHsmKeyConfigs(List<HsmkeyConfig> hsmKeyConfigs) {
		this.hsmKeyConfigs = hsmKeyConfigs;
	}

	@ManyToMany
	@JoinTable(name = "APPLICATION_KEY_PROFILE_HSMKEY", joinColumns = @JoinColumn(name = "KEY_PROFILE_ID"), inverseJoinColumns = @JoinColumn(name = "HSMKEY_CONFIG_ID"))
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<HsmkeyConfig> getHsmKeyConfigs() {
		return hsmKeyConfigs;
	}

	public void addHsmkeyConfig(HsmkeyConfig hsmkeyConfig) {
		this.hsmKeyConfigs.add(hsmkeyConfig);
		hsmkeyConfig.getApplicationKeyProfiles().add(this);
	}

}
