package com.justinmobile.tsm.customer.manager;



import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.endpoint.sms.SmsEndpoint;



/**  
 * Filename:    CustomerTestCase.java  
 * Description:   
 * Copyright:   Copyright (c)2009  
 * Company:     justinmobile
 * @author:     haojinghua  
 * @version:    1.0  
 * Create at:   2011-4-25 上午11:19:04  
 *
 */

public class CustomerManagerTest extends BaseAbstractTest{
	@Autowired
	private SysUserManager sysUserManager;
	@Autowired
	private CustomerManager customerManager;
	@Autowired
	private SmsEndpoint smsEndpoint;
	
	//@Test
	public void testgetUserByEmail()throws Exception{
		SysUser sysUser = sysUserManager.getUserByNameOrMobileOrEmail("ccnaccnp@126.com");
		assertNotNull(sysUser);
	}
	//@Test
	public void testSaveCustomer() throws Exception{
		SysUser u = new SysUser();
		
		u.setUserName("280166679@qq.com");
		
		Customer c = new Customer();
		
	   // c.setZip("100000");
	    c.setSex(1);
	    //c.setAddress("四川成都");
	    //c.setBirthday(Calendar.getInstance());
	    //c.setLocation("四川成都");
	    c.setRegDate(Calendar.getInstance());
	    c.setActive(1);
	    c.setActiveEmailCode("1122334455667788");
	    c.setSysUser(sysUserManager.getUserByName("280166679@qq.com"));
	    customerManager.addCustomer(c);
	    
	}
	/*
	 * 修改密码
	 */
	//@Test
	public void testChangePwd()throws Exception{
         sysUserManager.modifyPassword("fire", "111111", "222222", "222222");	    
		
	}
	//@Test
    public void testPaswdReset()throws Exception{
		SysUser u = sysUserManager.getUserByNameOrMobileOrEmail("fire");
		u.setPassword("333333");
		sysUserManager.saveOrUpdate(u);	
    }
    
    /*
     *更换头像
     */
    
	//@Test
    public void testChangeIcon()throws Exception{
		SysUser u = new SysUser();
		u.setId(259L);
    	customerManager.uploadIcon("/home/20110426/1.jpg",u);
    }
    //@Test 
    public void testRandomNumber()throws Exception{
    	System.out.println(RandomStringUtils.random(6));
    }
	/*
	  修改详细资料
	 */
	//@Test
	public void testModifyCustomer()throws Exception{
		SysUser u = sysUserManager.getUserByName("280166679");
		Customer c = customerManager.getCustomerByUserName(u.getUserName());
		c.setActive(0);
		c.setActiveEmailCode("0000000000000000");
		c.setAddress("四川成都11");
		c.setBirthday(Calendar.getInstance());
		c.setIconUrl("/home/20110426/2.jpg");
		c.setLocation("四川成都22");
		c.setZip("111111");
		c.setNickName("郝静华2");
		c.setSex(0);
		c.setRegDate(Calendar.getInstance());
		customerManager.addCustomer(c);
		
	}
	//@Test
	public void testSms2()throws Exception{
		smsEndpoint.sendMessage("13880777683", "钟表&");
	}

}



