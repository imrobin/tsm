package com.justinmobile.security.domain;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
@Table(name = "SYS_ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysRole extends AbstractEntity {

	private static final long serialVersionUID = 7540057081748938422L;
	
	private Long id;

	/** 角色名 */
	private String roleName;

	/** 描述 */
	private String description;
	
	/** 登录成功转向的页面  */
	private String loginSuccessForward;

	private Set<SysAuthority> sysAuthorities = Sets.newHashSet();

	private Set<SysUser> sysUsers = Sets.newHashSet();
	
	public enum SpecialRoleType {
		CUSTOMER,
		CUSTOMER_NOT_ACTIVE,
		SERVICE_PROVIDER,
		OPERATOR_CUSTOMER_SERVICE,
		OPERATOR_AUDITOR,
		SUPER_OPERATOR
	}

	@ManyToMany
	@JoinTable(name = "SYS_ROLE_AUTH", joinColumns = { @JoinColumn(name = "ROLE_ID") }, inverseJoinColumns = { @JoinColumn(name = "AUTH_ID") })
	@Cascade(value = { CascadeType.MERGE, CascadeType.PERSIST })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysAuthority> getSysAuthorities() {
		return sysAuthorities;
	}

	public void setSysAuthorities(Set<SysAuthority> sysAuthorities) {
		this.sysAuthorities = sysAuthorities;
	}

	@OneToMany(mappedBy = "sysRole", targetEntity = SysUser.class)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysUser> getSysUsers() {
		return sysUsers;
	}

	public void setSysUsers(Set<SysUser> sysUsers) {
		this.sysUsers = sysUsers;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_ROLE") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLoginSuccessForward() {
		return loginSuccessForward;
	}

	public void setLoginSuccessForward(String loginSuccessForward) {
		this.loginSuccessForward = loginSuccessForward;
	}
}
