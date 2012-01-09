package com.justinmobile.tsm.card.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
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
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.application.domain.Space;

@Entity
@Table(name = "CARD_INFO")
public class CardInfo extends AbstractEntity {

	/** 卡状态：0-不可用 */
	public static final String STATUS_DISABLE = "0";

	/** 卡状态：1-可用 */
	public static final String STATUS_ENABLE = "1";

	/** 卡状态：1-新添加 */
	public static final Integer REGISTERABLE_NEW = 1;

	/** 卡状态：2-更换SIM卡 */
	public static final Integer REGISTERABLE_CHANGE_SIM = 2;

	/** 卡状态：3-准备注册 */
	public static final Integer REGISTERABLE_READY = 3;
	
	public static final Set<Integer> REGISTERABLE_REGISTER =new HashSet<Integer>();
	static{
		REGISTERABLE_REGISTER.add(REGISTERABLE_NEW);
		REGISTERABLE_REGISTER.add(REGISTERABLE_READY);
	}

	/** 卡类型：0-正常卡 */
	public static final Integer CARD_TYPE_NORMAL = 0;

	/** 卡类型：1-测试卡 */
	public static final Integer CARD_TYPE_TEST = 1;

	private static final long serialVersionUID = 56078334L;

	private Long id;

	private CardBaseInfo cardBaseInfo;

	private String cardNo;

	private String token;

	private String challengeNo;

	/** 手机号码，仅在手机钱包注册时保存上行短信的号码，完绑定后移到CustomerCardInfo */
	private String mobileNo;

	private String imei;

	private String imsi;

	/**
	 * 0-不可用 1- 可用
	 */
	@ResourcesFormat(key = "auth.status")
	private String status;

	private Integer availableVolatileSpace;

	private Long availableNonevolatileSpace;

	private Integer cardType;

	/** 未知可变空间 */
	private Integer unknownVolatileSpace = 0;

	/** 未知不可变空间 */
	private Long unknownNoneVolatileSpace = 0L;

	private Integer registerable;

	public Integer getRegisterable() {
		return registerable;
	}

	public void setRegisterable(Integer registerable) {
		this.registerable = registerable;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_INFO") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "card_base_id")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public CardBaseInfo getCardBaseInfo() {
		return cardBaseInfo;
	}

	public void setCardBaseInfo(CardBaseInfo cardBaseInfo) {
		this.cardBaseInfo = cardBaseInfo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getAvailableVolatileSpace() {
		return availableVolatileSpace;
	}

	public void setAvailableVolatileSpace(Integer availableVolatileSpace) {
		this.availableVolatileSpace = availableVolatileSpace;
	}

	public Long getAvailableNonevolatileSpace() {
		return availableNonevolatileSpace;
	}

	public void setAvailableNonevolatileSpace(Long availableNonevolatileSpace) {
		this.availableNonevolatileSpace = availableNonevolatileSpace;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getChallengeNo() {
		return challengeNo;
	}

	public void setChallengeNo(String challengeNo) {
		this.challengeNo = challengeNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public Integer getUnknownVolatileSpace() {
		return unknownVolatileSpace;
	}

	public void setUnknownVolatileSpace(Integer unknownVolatileSpace) {
		this.unknownVolatileSpace = unknownVolatileSpace;
	}

	public Long getUnknownNoneVolatileSpace() {
		return unknownNoneVolatileSpace;
	}

	public void setUnknownNoneVolatileSpace(Long unknownNonevolatileSpace) {
		this.unknownNoneVolatileSpace = unknownNonevolatileSpace;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	/**
	 * 获取卡片可用空间
	 * 
	 * @return
	 */
	@Transient
	public Space getAvailableSpace() {
		Space space = new Space();
		space.setNvm(availableNonevolatileSpace);
		space.setRam(availableVolatileSpace);
		return space;
	}

	/**
	 * 设置卡片剩余空间
	 * 
	 * @param space
	 */
	public void setAvailableSpace(Space space) {
		this.availableNonevolatileSpace = space.getNvm();
		this.availableVolatileSpace = space.getRam();
	}

	/**
	 * 当前卡是否是测试卡？
	 * 
	 * @return true-是测试卡<br/>
	 *         false-不是测试卡
	 */
	@Transient
	public boolean isTestCard() {
		return null != cardType && CARD_TYPE_TEST.intValue() == cardType.intValue();
	}

	/**
	 * 获取未知空间
	 * 
	 * @return 未知空间
	 */
	@Transient
	public Space getUnknownSpace() {
		Space space = new Space();

		if (null != unknownVolatileSpace && null != unknownNoneVolatileSpace) {
			space.setNvm(unknownNoneVolatileSpace);
			space.setRam(unknownVolatileSpace);
		}

		return space;
	}

	/**
	 * 设置未知空间
	 */
	public void setUnknownSpace(Space space) {
		this.unknownNoneVolatileSpace = space.getNvm();
		this.unknownVolatileSpace = space.getRam();
	}
}