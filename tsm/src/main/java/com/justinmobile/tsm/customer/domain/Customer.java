package com.justinmobile.tsm.customer.domain;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.DateFormat;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;

@Entity
@Table(name = "CUSTOMER")
public class Customer extends AbstractEntity {

	private static final long serialVersionUID = 670819326L;
	
	public static final Integer ACTIVE_YES = 1;
	
	public static final Integer ACTIVE_NO = 0;

	/** 主键 */
	private Long id;

	/** 用户邮编 */
	private String zip;

	/** 用户地址 */
	private String address;

	/** 真实姓名 */
	private String nickName;

	/** 性别 */
	@ResourcesFormat(key="customer.sex")
	private Integer sex;

	/** 生日 */
	@DateFormat(format="yyyy-MM-dd")
	private Calendar birthday;

	/** 用户所在地区 */
	private String location;

	/** 注册日期 */
	@DateFormat(format="yyyy-MM-dd")
	private Calendar regDate;

	/** 激活码 */
	private String activeEmailCode;

	/** 激活码 */
	private String activeSmsCode;

	/** 是否激活 */
	@ResourcesFormat(key="customer.active")
	private Integer active;

	/** 用户头像地址 */
	private String iconUrl;
	
	private byte[] pcIcon;

	/** 关联表 */
	private SysUser sysUser;

	private List<CustomerCardInfo> customerCardInfos;
	
	private List<DesiredOperation> desiredOperations;

	public byte[] getPcIcon() {
		return pcIcon;
	}
	
	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public List<DesiredOperation> getDesiredOperations() {
		return desiredOperations;
	}
	
	
	public void setDesiredOperations(List<DesiredOperation> desiredOperations) {
		this.desiredOperations = desiredOperations;
	}

	public void setPcIcon(byte[] pcIcon) {
		this.pcIcon = pcIcon;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SysUser getSysUser() {
		return sysUser;
	}

	@OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
	public List<CustomerCardInfo> getCustomerCardInfos() {
		return customerCardInfos;
	}

	public void setCustomerCardInfos(List<CustomerCardInfo> customerCardInfos) {
		this.customerCardInfos = customerCardInfos;
	}

	public void setSysUser(SysUser sysUser) {
		this.sysUser = sysUser;
	}

	@Id
	@GeneratedValue(generator = "pkGenerator")
	@GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "sysUser"))
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Calendar getBirthday() {
		return birthday;
	}

	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Calendar getRegDate() {
		return regDate;
	}

	public void setRegDate(Calendar regDate) {
		this.regDate = regDate;
	}

	public String getActiveEmailCode() {
		return activeEmailCode;
	}

	public void setActiveEmailCode(String activeEmailCode) {
		this.activeEmailCode = activeEmailCode;
	}

	public String getActiveSmsCode() {
		return activeSmsCode;
	}

	public void setActiveSmsCode(String activeSmsCode) {
		this.activeSmsCode = activeSmsCode;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}