package com.justinmobile.tsm.card.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import java.util.Calendar;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Entity
@Table(name = "CARD_BLACK_LIST")
public class CardBlackList extends AbstractEntity{
	
	
	/** 后台管理员添加黑名单 */
	public static final int TYPE_ADD = 1;//添加黑名单	
	/** 后台管理员移除黑名单 */
	public static final int TYPE_REMOVE = 2;//移除黑名单
	
	/** 用户自己挂失添加黑名单 */
	public static final int TYPE_CUSTOMER_ADD = 3;
	
	/** 用户解挂移除黑名单 */
	public static final int TYPE_CUSTOMER_REMOVE = 4;

	private static final long serialVersionUID = -671489043L;


	private Long id;

	private CustomerCardInfo customerCardInfo;

	private String reason = "无特殊说明";

	private int type;

	private Calendar OperateDate;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_CARD_BLACK_LIST") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_CARD_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public CustomerCardInfo getCustomerCardInfo() {
		return customerCardInfo;
	}

	
	public void setCustomerCardInfo(CustomerCardInfo customerCardInfo) {
		this.customerCardInfo = customerCardInfo;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	
	public int getType() {
		return type;
	}

	
	public void setType(int type) {
		this.type = type;
	}

	
	public Calendar getOperateDate() {
		return OperateDate;
	}

	
	public void setOperateDate(Calendar operateDate) {
		OperateDate = operateDate;
	}

	
}