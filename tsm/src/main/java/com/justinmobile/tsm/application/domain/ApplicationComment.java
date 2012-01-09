package com.justinmobile.tsm.application.domain;

import java.util.Calendar;

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
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.tsm.customer.domain.Customer;

@Entity
@Table(name = "APPLICATION_COMMENT")
public class ApplicationComment extends AbstractEntity {

	private static final long serialVersionUID = -1679915457L;

	private Long id;

	private Application application;

	@DateFormat
	private Calendar commentTime;

	private Integer grade;
	
	private Integer oldGrade; // 记录修改评论前的星

	private String content;

	private Customer customer;

	@Transient
	public Integer getOldGrade() {
		return oldGrade;
	}

	public void setOldGrade(Integer oldGrade) {
		this.oldGrade = oldGrade;
	}

	@ManyToOne
	@JoinColumn(name = "APPLICATION_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_COMMENT") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(Calendar commentTime) {
		this.commentTime = commentTime;
	}


	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}