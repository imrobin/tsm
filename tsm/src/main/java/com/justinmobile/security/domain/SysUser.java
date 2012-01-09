package com.justinmobile.security.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

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
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;

@Entity
@Table(name = "SYS_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "userName", "mobile", "email" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SysUser extends AbstractEntity {

	private static final long serialVersionUID = -7556275242283581365L;

	/** 主键 */
	private Long id;

	/** 登录名 */
	private String userName;

	/** 密码 */
	private String password;

	/** 真实姓名 */
	private String realName;

	/** 手机 */
	private String mobile;

	/** 电子邮件 */
	private String email;

	/**
	 * 状态<br>
	 * 0-无效<br>
	 * 1-有效
	 */
	@ResourcesFormat(key = "user.status")
	private Integer status;

	/** 最后一次登录时间 */
	@DateFormat
	private Calendar latestLogin;

	/** 密码加密随机数 */
	private String salt;

	/** 安全问题 */
	private String safeQuestion;

	/** 安全问题答案 */
	private String safeAnswer;
	
	/** 省 */
	private String province;

	private Set<SysAuthority> sysAuthorities = Sets.newHashSet();

	private SysRole sysRole;

	public enum USER_STATUS {
		ENABLED(1), DISABLED(0);

		private Integer val;

		USER_STATUS(Integer inVal) {
			this.val = inVal;
		}

		public Integer getValue() {
			return this.val;
		}
	}

	@ManyToOne
	@JoinTable(name = "SYS_USER_ROLE", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SysRole getSysRole() {
		return sysRole;
	}

	public void setSysRole(SysRole sysRole) {
		this.sysRole = sysRole;
	}

	@ManyToMany
	@JoinTable(name = "SYS_USER_AUTH", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "AUTH_ID") })
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
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_USER") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Transient
	public boolean isEnable() {
		if (this.status == 1) {
			return true;
		} else {
			return false;
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Calendar getLatestLogin() {
		return latestLogin;
	}

	public void setLatestLogin(Calendar latestLogin) {
		this.latestLogin = latestLogin;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSafeQuestion() {
		return safeQuestion;
	}

	public void setSafeQuestion(String safeQuestion) {
		this.safeQuestion = safeQuestion;
	}

	public String getSafeAnswer() {
		return safeAnswer;
	}

	public void setSafeAnswer(String safeAnswer) {
		this.safeAnswer = safeAnswer;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

}
