/**  
 * Filename:    SysUserRetrievePassword.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-4-27 下午04:01:51  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-27     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.security.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;


@Entity
@Table(name = "SYS_USER_RETRIEVE_PASSWORD")
public class SysUserRetrievePassword extends AbstractEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2629576806814592466L;

	/** 主键 */
	private Long id;

	/** 邮箱 */
	private String email;

	/** 密码加密随机数 */
	private String salt;

	/**  验证值 */
	private String checkSign;  
	
	/** 过期时间     */
	private Date overdueTime;
	
	/** 重置密码状态  */
	private Integer status;
	
	public static final Integer STATUS_ALREADY_RESET = 1;//已重置
	
	public static final Integer STATUS_NOT_RESET = 0;//未重置
	
	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_SYS_USER_RETRIEVE_PASSWORD") })
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getCheckSign() {
		return checkSign;
	}

	public void setCheckSign(String checkSign) {
		this.checkSign = checkSign;
	}

	public Date getOverdueTime() {
		return overdueTime;
	}

	public void setOverdueTime(Date overdueTime) {
		this.overdueTime = overdueTime;
	}
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}



