package com.justinmobile.tsm.history.domain;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Entity
@Table(name = "SUBSCRIBE_HISTORY")
public class SubscribeHistory  extends AbstractEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 主键 */
	private Long id;

	/** 用户卡信息 */
	private CustomerCardInfo customerCardInfo;

	/** 应用版本 */
	private ApplicationVersion applicationVersion;

	/** 订购时间 */
	@DateFormat
	private Calendar subscribeDate;

	/** 退订时间 */
	@DateFormat
	private Calendar unsubscribeDate;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SUBSCRIBE_HISTORY") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToOne
	@JoinColumn(name = "CUSTOMER_CARD_INFO_ID", referencedColumnName = "id")
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	public CustomerCardInfo getCustomerCardInfo() {
		return customerCardInfo;
	}

	public void setCustomerCardInfo(CustomerCardInfo customerCardInfo) {
		this.customerCardInfo = customerCardInfo;
	}

	@OneToOne
	@JoinColumn(name = "APPLICATION_VERSION_ID", referencedColumnName = "id")
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	public ApplicationVersion getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(ApplicationVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public Calendar getSubscribeDate() {
		return subscribeDate;
	}

	public void setSubscribeDate(Calendar subscribeDate) {
		this.subscribeDate = subscribeDate;
	}

	public Calendar getUnsubscribeDate() {
		return unsubscribeDate;
	}

	public void setUnsubscribeDate(Calendar unsubscribeDate) {
		this.unsubscribeDate = unsubscribeDate;
	}

}
