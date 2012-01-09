package com.justinmobile.tsm.card.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import com.google.common.collect.Lists;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.application.domain.Space;

/**
 * @author WLQ
 *
 */
@Entity
@Table(name = "CARD_BASE_INFO")
public class CardBaseInfo extends AbstractEntity {

	private static final long serialVersionUID = -1093717393L;

	/** ID */
	private Long id;

	/** 名称 */
	private String name;

	/** 批次编号 */
	private String batchNo;
	
	/** 批次开始范围号 */
	private String startNo;
	
	/** 批次结束范围号 */
	private String endNo;
	
	/** 卡批次的类型 1贴片、2NFC终端、3SIM卡 */
	@ResourcesFormat(key="cardBaseInfo.type")
	private Integer type;
	
	/** 卡批次的cms2ac版本 */
	private String cms2acVersion;
	
	/** 卡批次自动关联主安全域的密钥版本 **/
	private Integer cardKeyVersion;
	
	/** 芯片类型**/
	private String coreType;
	
	/** JAVA版本**/
	private String javaVersion;
	
	/** 预置内存类型**/
	private Integer presetRamType;
	
	/** 预置内存状态**/
	private String presetRamStatus;
	
	private List<CardBaseApplication> cardBaseApplications = Lists.newArrayList();

	/** 该卡的安全域关联对象 **/
	private List<CardBaseSecurityDomain> cardBaseSecurityDomain = Lists.newArrayList();
	
	private List<CardBaseLoadFile> cardBaseLoadFile = Lists.newArrayList();
	
	

	// TODO 以下信息不可用
	private String stockId;
	
	@ResourcesFormat(key="dardBaseInfo.platformType")
	private Long platformType;

	private String platformVersion;
	
	@ResourcesFormat(key="dardBaseInfo.osPlatform")
	private Long osPlatform;

	private String osVersion;
	
	@ResourcesFormat(key="cardBaseInfo.osImplementor")
	private String osImplementor;

	private Integer avlRamSize;

	private Long avlEepromSize;
	
	@Deprecated
	private Long avlFlashSize;

	private Long romSize;

	private Integer ramSize;

	private Long eepromSize;

	private Long flashSize;
	
	private Integer totalRamSize;
	
	private Long totalRomSize;

	private Long garbageCollection;
	/** 备注 */
	private String comments;
	

	public Long getTotalRomSize() {
		return totalRomSize;
	}

	public void setTotalRomSize(Long totalRomSize) {
		this.totalRomSize = totalRomSize;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getStartNo() {
		return startNo;
	}

	public void setStartNo(String startNo) {
		this.startNo = startNo;
	}

	public String getEndNo() {
		return endNo;
	}

	public void setEndNo(String endNo) {
		this.endNo = endNo;
	}


	@OneToMany(mappedBy = "cardBase")
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<CardBaseApplication> getCardBaseApplications() {
		return cardBaseApplications;
	}

	public void setCardBaseApplications(List<CardBaseApplication> cardBaseApplications) {
		this.cardBaseApplications = cardBaseApplications;
	}

	
	@OneToMany(mappedBy = "cardBaseInfo")
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<CardBaseSecurityDomain> getCardBaseSecurityDomain() {
		return cardBaseSecurityDomain;
	}

	public void setCardBaseSecurityDomain(List<CardBaseSecurityDomain> cardBaseSecurityDomain) {
		this.cardBaseSecurityDomain = cardBaseSecurityDomain;
	}
	
	@OneToMany(mappedBy = "cardBaseInfo")
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public List<CardBaseLoadFile> getCardBaseLoadFile() {
		return cardBaseLoadFile;
	}

	public void setCardBaseLoadFile(List<CardBaseLoadFile> cardBaseLoadFile) {
		this.cardBaseLoadFile = cardBaseLoadFile;
	}
	
	
	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_BASE_INFO") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public Long getPlatformType() {
		return platformType;
	}

	public void setPlatformType(Long platformType) {
		this.platformType = platformType;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public Long getOsPlatform() {
		return osPlatform;
	}

	public void setOsPlatform(Long osPlatform) {
		this.osPlatform = osPlatform;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getOsImplementor() {
		return osImplementor;
	}

	public void setOsImplementor(String osImplementor) {
		this.osImplementor = osImplementor;
	}

	public Integer getAvlRamSize() {
		return avlRamSize;
	}

	public void setAvlRamSize(Integer avlRamSize) {
		this.avlRamSize = avlRamSize;
	}

	public Long getAvlEepromSize() {
		return avlEepromSize;
	}

	public void setAvlEepromSize(Long avlEepromSize) {
		this.avlEepromSize = avlEepromSize;
	}

	public Long getAvlFlashSize() {
		return avlFlashSize;
	}

	public void setAvlFlashSize(Long avlFlashSize) {
		this.avlFlashSize = avlFlashSize;
	}

	public Long getRomSize() {
		return romSize;
	}

	public void setRomSize(Long romSize) {
		this.romSize = romSize;
	}

	public Integer getRamSize() {
		return ramSize;
	}

	public void setRamSize(Integer ramSize) {
		this.ramSize = ramSize;
	}

	public Long getEepromSize() {
		return eepromSize;
	}

	public void setEepromSize(Long eepromSize) {
		this.eepromSize = eepromSize;
	}

	public Long getFlashSize() {
		return flashSize;
	}

	public void setFlashSize(Long flashSize) {
		this.flashSize = flashSize;
	}

	public Long getGarbageCollection() {
		return garbageCollection;
	}

	public void setGarbageCollection(Long garbageCollection) {
		this.garbageCollection = garbageCollection;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCms2acVersion() {
		return cms2acVersion;
	}

	public void setCms2acVersion(String cms2acVersion) {
		this.cms2acVersion = cms2acVersion;
	}

	public Integer getTotalRamSize() {
		return totalRamSize;
	}

	public void setTotalRamSize(Integer totalRamSize) {
		this.totalRamSize = totalRamSize;
	}
	
	@Transient
	public Space getTotalSpace() {
		Space space = new Space();
		if (null != totalRomSize && null != totalRamSize) {
			space.setNvm(totalRomSize);
			space.setRam(totalRamSize);
		}
		return space;
	}

	public Integer getCardKeyVersion() {
		return cardKeyVersion;
	}

	public void setCardKeyVersion(Integer cardKeyVersion) {
		this.cardKeyVersion = cardKeyVersion;
	}

	public String getCoreType() {
		return coreType;
	}

	public void setCoreType(String coreType) {
		this.coreType = coreType;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	
	public Integer getPresetRamType() {
		return presetRamType;
	}

	public void setPresetRamType(Integer presetRamType) {
		this.presetRamType = presetRamType;
	}

	public String getPresetRamStatus() {
		return presetRamStatus;
	}

	public void setPresetRamStatus(String presetRamStatus) {
		this.presetRamStatus = presetRamStatus;
	}

	
}