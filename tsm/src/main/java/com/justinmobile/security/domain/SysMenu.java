package com.justinmobile.security.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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

/**
 * @author peak
 */
@Entity
@Table(name = "sys_menu")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysMenu extends AbstractEntity {

	private static final long serialVersionUID = 1147606384496817951L;

	public static final int LEVEL_ONE = 1;

	public static final int LEVEL_TWO = 2;

	public static final int LEVEL_THREE = 3;

	/** 主键 */
	private Long id;

	/** 名称 */
	private String menuName;

	/** url */
	private String url;

	/** 显示顺序 */
	private Integer orderNo;

	/** 菜单级别 */
	@ResourcesFormat(key = "menu.level")
	private Integer menuLevel;

	/** 父MENU */
	private SysMenu parent;

	/** 包含子MENU */
	private Set<SysMenu> childMenus = Sets.newHashSet();

	/** 对应权限 */
	private Set<SysAuthority> sysAuthorities = Sets.newHashSet();

	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SysMenu getParent() {
		return parent;
	}

	public void setParent(SysMenu parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent")
	@Cascade(value = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysMenu> getChildMenus() {
		return childMenus;
	}

	public void setChildMenus(Set<SysMenu> childMenus) {
		this.childMenus = childMenus;
	}

	@ManyToMany(mappedBy = "sysMenus", targetEntity = SysAuthority.class)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyCollection(LazyCollectionOption.TRUE)
	public Set<SysAuthority> getSysAuthorities() {
		return sysAuthorities;
	}

	public void setSysAuthorities(Set<SysAuthority> sysAuthorities) {
		this.sysAuthorities = sysAuthorities;
	}

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_MENU") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(columnDefinition = "NUMBER default 0")
	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(Integer menuLevel) {
		this.menuLevel = menuLevel;
	}

}
