package com.justinmobile.security.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;

import com.google.common.collect.Sets;
import com.justinmobile.core.domain.AbstractEntity;

@Entity
@Table(name = "SYS_RESOURCE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysResource extends AbstractEntity {

	private static final long serialVersionUID = -635789819157263051L;

	private Long id;
	/** 资源串 */
	private String filterString;
	/** 资源名称 */
	private String resName;
	
	private Set<SysAuthority> sysAuthorities = Sets.newHashSet();
	
	@ManyToMany(mappedBy = "sysResources", targetEntity = SysAuthority.class)
	@Cascade(value = {CascadeType.PERSIST, CascadeType.MERGE})
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysAuthority> getSysAuthorities() {
		return sysAuthorities;
	}

	public void setSysAuthorities(Set<SysAuthority> sysAuthorities) {
		this.sysAuthorities = sysAuthorities;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_RESOURCE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilterString() {
		return filterString;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}
	
	public static String[] propertiesToArray() {
		return new String[]{"id", "type", "filterString"};
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

}