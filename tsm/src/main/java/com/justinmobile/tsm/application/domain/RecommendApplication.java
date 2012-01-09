package com.justinmobile.tsm.application.domain;

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
@Table(name = "RECOMMEND_APPLICATION")
public class RecommendApplication extends AbstractEntity{

	private static final long serialVersionUID = -68664556L;


	private Long id;

	private Long orderNo;
	
	/** 所属应用 */
	private Application application;
	
	public void setApplication(Application application) {
		this.application = application;
	}
	
	@OneToOne
	@JoinColumn(name = "APPLICATION_ID",referencedColumnName = "ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public Application getApplication() {
		return application;
	}
	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_RECOMMEND_APPLICATION") })
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