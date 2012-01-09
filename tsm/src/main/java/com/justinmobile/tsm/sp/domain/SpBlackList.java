package com.justinmobile.tsm.sp.domain;

import java.util.Calendar;

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

import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "SP_BLACK_LIST")
public class SpBlackList extends AbstractEntity {
	
	public static final int TYPE_ADD = 1;//添加黑名单	
	public static final int TYPE_REMOVE = 2;//移除黑名单
	
	private static final long serialVersionUID = -316723392L;

	private Long id;

	private SpBaseInfo sp;

	private String reason;

	private int type;

	private Calendar OperateDate;


	@ManyToOne
	@JoinColumn(name = "SP_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public SpBaseInfo getSp() {
		return sp;
	}

	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SP_BLACK_LIST") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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