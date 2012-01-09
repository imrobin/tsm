package com.justinmobile.security.domain;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Parameter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import com.google.common.collect.Sets;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;

@Entity
@Table(name = "SYS_AUTHORITY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysAuthority extends AbstractEntity {

	private static final long serialVersionUID = -1696933371379708151L;

	private Long id;

	/** 权限名 */
	private String authName;

	/** 权限描述 */
	private String description;

	/** 状态 */
	@ResourcesFormat(key = "auth.status")
	private Integer status;

	private Set<SysUser> sysUsers = Sets.newHashSet();

	private Set<SysRole> sysRoles = Sets.newHashSet();

	private Set<SysResource> sysResources = Sets.newHashSet();

	private Set<SysMenu> sysMenus = Sets.newHashSet();

	public enum AUTH_STATUS {
		ENABLED(1), DISABLED(0);

		private Integer val;

		AUTH_STATUS(Integer inVal) {
			this.val = inVal;
		}

		public Integer getValue() {
			return this.val;
		}
	}

	public void setSysMenus(Set<SysMenu> sysMenus) {
		this.sysMenus = sysMenus;
	}

	@ManyToMany
	@JoinTable(name = "SYS_AUTH_MENU", joinColumns = { @JoinColumn(name = "AUTH_ID") }, inverseJoinColumns = { @JoinColumn(name = "MENU_ID") })
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysMenu> getSysMenus() {
		return sysMenus;
	}

	@ManyToMany(mappedBy = "sysAuthorities", targetEntity = SysRole.class)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysRole> getSysRoles() {
		return sysRoles;
	}

	public void setSysRoles(Set<SysRole> sysRoles) {
		this.sysRoles = sysRoles;
	}

	@ManyToMany(mappedBy = "sysAuthorities", targetEntity = SysUser.class)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysUser> getSysUsers() {
		return sysUsers;
	}

	public void setSysUsers(Set<SysUser> sysUsers) {
		this.sysUsers = sysUsers;
	}

	@ManyToMany
	@JoinTable(name = "SYS_AUTH_RES", joinColumns = { @JoinColumn(name = "AUTH_ID") }, inverseJoinColumns = { @JoinColumn(name = "RES_ID") })
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysResource> getSysResources() {
		return sysResources;
	}

	public void setSysResources(Set<SysResource> sysResources) {
		this.sysResources = sysResources;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_AUTHORITY") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthName() {
		return authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static Set<GrantedAuthority> toGrantedAuthority(Collection<SysAuthority> sysAuthorities) {
		Set<GrantedAuthority> auths = Sets.newHashSet();
		if (CollectionUtils.isNotEmpty(sysAuthorities)) {
			for (SysAuthority sysAuthority : sysAuthorities) {
				auths.add(new GrantedAuthorityImpl(sysAuthority.getAuthName()));
			}
		}
		return auths;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
