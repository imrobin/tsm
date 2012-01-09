package com.justinmobile.tsm.application.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.google.common.collect.Sets;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;


@Entity
@Table(name = "APPLICATION_TYPE")
public class ApplicationType extends AbstractEntity {

	private static final long serialVersionUID = 2050726026L;
	
	public static final Integer SHOW = 1;
	public static final Integer NO_SHOW = 0;

	private Long id;
	
	private Integer classify;

	private String name;

	private ApplicationType parentType;
	
	private Integer showIndex;
	
	private byte[] typeLogo;
	
	private Set<ApplicationType> applicationTypes = Sets.newHashSet();

	@ResourcesFormat(key = "app.type.level")
	private Integer typeLevel;
	
	private Set<Application> applications = Sets.newHashSet();
	
	public enum AppTypeLevel {
		ONE_LEVEL(1), TWO_LEVEL(2);
		
		private int type;

		AppTypeLevel(int ordinal) {
			this.type = ordinal;
		}
		
		public int getType() {
			return this.type;
		}
	}

	@ManyToOne
	@JoinColumn(name = "PARENT_TYPE_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public ApplicationType getParentType() {
		return parentType;
	}

	public void setParentType(ApplicationType parentType) {
		this.parentType = parentType;
	}

	@OneToMany(mappedBy = "parentType")
	@Cascade(value = { CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<ApplicationType> getApplicationTypes() {
		return applicationTypes;
	}

	public void setApplicationTypes(Set<ApplicationType> applicationTypes) {
		this.applicationTypes = applicationTypes;
	}

	@OneToMany(mappedBy = "childType")
	@Cascade(value = { CascadeType.ALL })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<Application> getApplications() {
		return applications;
	}

	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_TYPE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTypeLevel() {
		return typeLevel;
	}

	public void setTypeLevel(Integer typeLevel) {
		this.typeLevel = typeLevel;
	}

	public Integer getClassify() {
		return classify;
	}

	public void setClassify(Integer classify) {
		this.classify = classify;
	}
	
	public Integer getShowIndex() {
		return showIndex;
	}

	public void setShowIndex(Integer showIndex) {
		this.showIndex = showIndex;
	}

	@Lob
	public byte[] getTypeLogo() {
		return typeLogo;
	}

	public void setTypeLogo(byte[] typeLogo) {
		this.typeLogo = typeLogo;
	}
	
	
	
}