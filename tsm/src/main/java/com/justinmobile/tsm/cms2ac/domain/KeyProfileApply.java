/**
 * 
 */
package com.justinmobile.tsm.cms2ac.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
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
import com.justinmobile.tsm.application.domain.SecurityDomainApply;

/**
 * @author eastseven
 *
 */
@Entity
@Table(name = "KEY_PROFILE_APPLY")
public class KeyProfileApply extends AbstractEntity {

	private static final long serialVersionUID = -2429675312035299927L;

	/** 主键 */
	private Long id;

	private SecurityDomainApply securityDomainApply;
	/** 密钥版本 */
	private Integer version;
	/**
	 * 密钥索引 <br/>
	 * 0x01=加密密钥索引<br/> 
	 * 0x02=MAC密钥索引<br/> 
	 * 0x03=密钥加密密钥索引<br/> 
	 * 0x04=DAP认证密钥索引<br/>
	 * 0x05=TOKEN认证密钥索引<br/>
	 */
	private Integer index;
	/**
	 * 密钥类型 14:读卡器通道安全协议 加密密钥 12:读卡器通道安全协议 MAC密钥 13:读卡器通道安全协议 密钥加密密钥
	 * 10:读卡器通道安全协议 DAP认证密钥 11:读卡器通道安全协议 TOKEN认证密钥 14:空中通道安全协议 加密密钥 12:空中通道安全协议
	 * MAC密钥
	 */
	private Integer type;
	/** 密钥校验值 */
	private String value;

	private Calendar effectieDate;
	/** 状态0有效1无效 */
	private Integer status;

	private List<HsmkeyConfig> hsmKeyConfigs = new ArrayList<HsmkeyConfig>();

	public KeyProfileApply() {}
	
	public KeyProfileApply(SecurityDomainApply securityDomainApply, Integer index, String value) {
		this.securityDomainApply = securityDomainApply;
		this.index = index;
		this.value = value;
	}

	@ManyToOne
	@JoinColumn(name = "SD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SecurityDomainApply getSecurityDomainApply() {
		return securityDomainApply;
	}

	public void setSecurityDomainApply(SecurityDomainApply securityDomainApply) {
		this.securityDomainApply = securityDomainApply;
	}

	@ManyToMany
	@JoinTable(name = "KEY_PROFILE_HSMKEY_APPLY", joinColumns = @JoinColumn(name = "KEY_PROFILE_ID"), inverseJoinColumns = @JoinColumn(name = "HSMKEY_CONFIG_ID"))
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<HsmkeyConfig> getHsmKeyConfigs() {
		return hsmKeyConfigs;
	}

	public void setHsmKeyConfigs(List<HsmkeyConfig> hsmKeyConfigs) {
		this.hsmKeyConfigs = hsmKeyConfigs;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_KEY_PROFILE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Calendar getEffectieDate() {
		return effectieDate;
	}

	public void setEffectieDate(Calendar effectieDate) {
		this.effectieDate = effectieDate;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "KEY_ID")
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
