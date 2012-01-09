package com.justinmobile.tsm.sp.domain;

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

@Entity
@Table(name = "RECOMMEND_SP")
public class RecommendSp extends AbstractEntity{

	private static final long serialVersionUID = -68664556L;


	private Long id;

	private Long orderNo;
	
	/** 所属应用 */
	private SpBaseInfo sp;
	
	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}
	
	@OneToOne
	@JoinColumn(name = "SP_ID",referencedColumnName = "ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public SpBaseInfo getSp() {
		return sp;
	}
	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_RECOMMEND_SP") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}


}