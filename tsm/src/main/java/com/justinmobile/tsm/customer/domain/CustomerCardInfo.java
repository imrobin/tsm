package com.justinmobile.tsm.customer.domain;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.card.domain.CardInfo;

@Entity
@Table(name = "CUSTOMER_CARD_INFO")
public class CustomerCardInfo extends AbstractEntity {

	private static final long serialVersionUID = -895044100L;

	/** 绑定关系状态：1-正常使用 */
	public final static int STATUS_NORMAL = 1;

	/** 绑定关系状态：2-挂失 */
	public final static int STATUS_LOST = 2;

	/** 绑定关系状态：3-未激活 */
	public final static int STATUS_NOT_USE = 3;

	/** 绑定关系状态：4-注销 */
	public final static int STATUS_CANCEL = 4;

	/** 绑定关系状态：5-替换中 */
	public final static int STATUS_REPLACING = 5;

	/** 绑定关系状态：6-已替换 */
	public final static int STATUS_END_REPLACE = 6;

	/** 可迁出的状态集合 */
	public static Set<Integer> STATUS_EMIGRATABLE = new HashSet<Integer>();
	static {
		STATUS_EMIGRATABLE.add(STATUS_NORMAL);
		STATUS_EMIGRATABLE.add(STATUS_LOST);
	}

	/**
	 * 处于黑名单中
	 */
	public final static int INBLACK = 1;
	/**
	 * 未入黑名单
	 */
	public final static int NOT_INBLACK = 0;

	/**
	 * 已激活
	 * 
	 */
	public final static Long ACTIVED = 1L;
	/**
	 * 未激活
	 */
	public final static Long NOT_ACTIVED = 0L;

	/** 主键 */
	private Long id;

	private CardInfo card;

	private Customer customer;

	private String mobileNo;

	private MobileType mobileType;

	private Long j2meKey;

	private Long currentOsKey;

	private String mobileTypeNo;

	@DateFormat
	private Calendar bindingDate;

	private String imei;

	private String activeCode;

	@ResourcesFormat(key = "customercard.active")
	private Long active;

	private String name;

	// 如果这个字段为NULL,表示还没有选择过恢复应用,如果不为空,这表示恢复过终端,且再恢复完成前必须使用这个指定的终端
	private CustomerCardInfo backCustomerCard;

	/**
	 * @Fields inBlack : 1.已列入 0.未列入
	 */
	@ResourcesFormat(key = "customercard.inBlack")
	private Integer inBlack;

	/**
	 * @Fields status : 1.正常,2.已挂失3.未使用4.已注销
	 */
	@ResourcesFormat(key = "customercard.status")
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CUSTOMER_CARD_INFO") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CARD_ID")
	public CardInfo getCard() {
		return card;
	}

	public void setCard(CardInfo card) {
		this.card = card;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID")
	public Customer getCustomer() {
		return customer;
	}

	@OneToOne()
	@JoinColumn(name = "MOBILE_TYPE_ID")
	public MobileType getMobileType() {
		return mobileType;
	}

	public void setMobileType(MobileType mobileType) {
		this.mobileType = mobileType;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public Long getJ2meKey() {
		return j2meKey;
	}

	public void setJ2meKey(Long j2meKey) {
		this.j2meKey = j2meKey;
	}

	public Long getCurrentOsKey() {
		return currentOsKey;
	}

	public void setCurrentOsKey(Long currentOsKey) {
		this.currentOsKey = currentOsKey;
	}

	public String getMobileTypeNo() {
		return mobileTypeNo;
	}

	public void setMobileTypeNo(String mobileTypeNo) {
		this.mobileTypeNo = mobileTypeNo;
	}

	public Calendar getBindingDate() {
		return bindingDate;
	}

	public void setBindingDate(Calendar bindingDate) {
		this.bindingDate = bindingDate;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getActiveCode() {
		return activeCode;
	}

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	public Long getActive() {
		return active;
	}

	public void setActive(Long active) {
		this.active = active;
	}

	@Column(name = "inblack")
	public Integer getInBlack() {
		return inBlack;
	}

	public void setInBlack(Integer inBlack) {
		this.inBlack = inBlack;
	}

	@Transient
	public boolean isInBlack() {
		if (this.inBlack == 1) {
			return true;
		} else {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBackCustomerCard(CustomerCardInfo backCustomerCard) {
		this.backCustomerCard = backCustomerCard;
	}

	@ManyToOne()
	@JoinColumn(name = "BACK_CUSTOMER_CARD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public CustomerCardInfo getBackCustomerCard() {
		return backCustomerCard;
	}

	@Transient
	public boolean isInBlackList() {
		return CustomerCardInfo.STATUS_NORMAL != status.intValue() || CustomerCardInfo.INBLACK == inBlack.intValue();
	}
}