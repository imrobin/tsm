package com.justinmobile.tsm.cms2ac.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Entity
@Table(name = "CMS2AC_PARAM")
public class Cms2acParam extends AbstractEntity {

	private static final long serialVersionUID = 320340899L;

	private Long id;

	private LocalTransaction localTransaction;

	private String scp;

	private Integer scp80i;

	private Integer scp02i;

	private Integer scp02SecurityLevel;

	/** 当前命令索引 */
	private Integer commandIndex;

	/** 当前命令批次 */
	private Integer commandBatchNo = 0;

	/** 当前响应索引 */
	private Integer responseIndex = 0;

	/** 当前响应批次 */
	private Integer responseBatchNo;

	private String returnedApduResult;

	/** 当前安全域 */
	private SecurityDomain currentSecurityDomain;

	/** APDU命令 */
	private List<ApduCommand> apduCommands = new ArrayList<ApduCommand>();

	/** APDU响应 */
	private List<ApduResult> apduResults = new ArrayList<ApduResult>();

	/** MAC密钥信息 */
	private KeyProfile kid;

	/** ENC密钥信息 */
	private KeyProfile kic;

	/** DEK密钥信息 */
	private KeyProfile dek;

	/** TOKEN底数信息 */
	private KeyProfile mod;

	/** TOKEN公钥指数信息 */
	private KeyProfile publicExponent;

	/** TOKEN私钥底数信息 */
	private KeyProfile privateExponent;

	/** SCP02安全通道主机随机数 */
	private String hostRandom;

	/** SCP02安全通道卡片随机数 */
	private String cardRandom;

	/** SCP02安全通道卡片序列计数器 */
	private Integer scp02Counter;

	@OneToMany(mappedBy = "cms2acParam")
	@Cascade(value = CascadeType.ALL)
	@OrderBy(value = "id")
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<ApduCommand> getApduCommands() {
		return apduCommands;
	}

	@OneToMany(mappedBy = "cms2acParam")
	@Cascade(value = CascadeType.ALL)
	@OrderBy(value = "id")
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<ApduResult> getApduResults() {
		return apduResults;
	}

	@ManyToOne
	@JoinColumn(name = "TRANSACTION_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public LocalTransaction getLocalTransaction() {
		return localTransaction;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = @Parameter(name = "sequence", value = "SEQ_CM2AC_PARAM"))
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SecurityDomain getCurrentSecurityDomain() {
		return currentSecurityDomain;
	}

	public void setCurrentSecurityDomain(SecurityDomain currentSecurityDomain) {
		this.currentSecurityDomain = currentSecurityDomain;
	}

	public void setLocalTransaction(LocalTransaction localTransaction) {
		this.localTransaction = localTransaction;
	}

	public String getScp() {
		return scp;
	}

	public Integer getScp80i() {
		return scp80i;
	}

	public Integer getScp02i() {
		return scp02i;
	}

	public Integer getCommandIndex() {
		return commandIndex;
	}

	public Integer getCommandBatchNo() {
		return commandBatchNo;
	}

	public Integer getResponseIndex() {
		return responseIndex;
	}

	public Integer getResponseBatchNo() {
		return responseBatchNo;
	}

	public String getReturnedApduResult() {
		return returnedApduResult;
	}

	public void setScp(String scp) {
		this.scp = scp;
	}

	public void setScp80i(Integer scp80i) {
		this.scp80i = scp80i;
	}

	public void setScp02i(Integer scp02i) {
		this.scp02i = scp02i;
	}

	public void setCommandIndex(Integer commandIndex) {
		this.commandIndex = commandIndex;
	}

	public void setCommandBatchNo(Integer commandBatchNo) {
		this.commandBatchNo = commandBatchNo;
	}

	public void setResponseIndex(Integer responseIndex) {
		this.responseIndex = responseIndex;
	}

	public void setResponseBatchNo(Integer responseBatchNo) {
		this.responseBatchNo = responseBatchNo;
	}

	public void setReturnedApduResult(String returnedApduResult) {
		this.returnedApduResult = returnedApduResult;
	}

	public void setApduCommands(List<ApduCommand> apduCommands) {
		this.apduCommands = apduCommands;
	}

	public void setApduResults(List<ApduResult> apduResults) {
		this.apduResults = apduResults;
	}

	@Column(name = "SCP02_SECURITYL_EVEL")
	public Integer getScp02SecurityLevel() {
		return scp02SecurityLevel;
	}

	public void setScp02SecurityLevel(Integer scp02SecurityLevel) {
		this.scp02SecurityLevel = scp02SecurityLevel;
	}

	@Transient
	public ApduCommand getLastApduCommand() {
		if (CollectionUtils.isEmpty(this.apduCommands)) {
			return null;
		} else {
			return this.apduCommands.get(this.apduCommands.size() - 1);
		}
	}

	@Transient
	public ApduResult getLastApduResult() {
		if (CollectionUtils.isEmpty(this.apduResults)) {
			return null;
		} else {
			return this.apduResults.get(this.apduResults.size() - 1);
		}
	}

	@ManyToOne
	@JoinColumn(name = "KID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public KeyProfile getKid() {
		return kid;
	}

	@ManyToOne
	@JoinColumn(name = "KIC")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public KeyProfile getKic() {
		return kic;
	}

	@ManyToOne
	@JoinColumn(name = "DEK")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public KeyProfile getDek() {
		return dek;
	}

	public void setKid(KeyProfile kid) {
		this.kid = kid;
	}

	public void setKic(KeyProfile kic) {
		this.kic = kic;
	}

	public void setDek(KeyProfile dek) {
		this.dek = dek;
	}

	public String getHostRandom() {
		return hostRandom;
	}

	public void setHostRandom(String hostRandom) {
		this.hostRandom = hostRandom;
	}

	public String getCardRandom() {
		return cardRandom;
	}

	public void setCardRandom(String cardRandom) {
		this.cardRandom = cardRandom;
	}

	@Column(name = "SCP02_COUNTER")
	public Integer getScp02Counter() {
		return scp02Counter;
	}

	public void setScp02Counter(Integer scp02Counter) {
		this.scp02Counter = scp02Counter;
	}

	public static Cms2acParam getInstance(LocalTransaction localTransaction) {
		Cms2acParam intance = new Cms2acParam();
		intance.setLocalTransaction(localTransaction);
		return intance;
	}

	public void increaseBatchNo() {
		this.commandBatchNo++;
	}

	public boolean hasNoSecurity() {
		return getCurrentSecurityDomain() == null;
	}

	@ManyToOne
	@JoinColumn(name = "MOD_KEY_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public KeyProfile getMod() {
		return mod;
	}

	public void setMod(KeyProfile tokenMod) {
		this.mod = tokenMod;
	}

	@ManyToOne
	@JoinColumn(name = "PUBLIC_EXPONENT_KEY_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public KeyProfile getPublicExponent() {
		return publicExponent;
	}

	public void setPublicExponent(KeyProfile tokenPublicExponent) {
		this.publicExponent = tokenPublicExponent;
	}

	@ManyToOne
	@JoinColumn(name = "PRIVATE_EXPONENT_KEY_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public KeyProfile getPrivateExponent() {
		return privateExponent;
	}

	public void setPrivateExponent(KeyProfile tokenPrivateExponent) {
		this.privateExponent = tokenPrivateExponent;
	}

}