package com.justinmobile.tsm.application.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import com.google.common.collect.Sets;
import com.justinmobile.core.domain.AbstractEntity;


@Entity
@Table(name = "SPECIAL_MOBILE")
public class SpecialMobile extends AbstractEntity {

	private static final long serialVersionUID = -1072845520L;

	/** 主键 */
	private Long id;

	private Set<ApplicationVersion> applicationVersions = Sets.newHashSet();
	
	private String mobileNo;
	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SPECIAL_MOBILE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@ManyToMany
	@JoinTable(name = "MOBILE_APPLICATION_VERSION", joinColumns = { @JoinColumn(name = "MOBILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPLICATION_VERSION_ID") })
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<ApplicationVersion> getApplicationVersions() {
		return applicationVersions;
	}

	public void setApplicationVersions(Set<ApplicationVersion> applicationVersions) {
		this.applicationVersions = applicationVersions;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
}