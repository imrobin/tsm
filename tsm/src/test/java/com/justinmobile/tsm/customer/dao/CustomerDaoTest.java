/**  
 * Filename:    CustomerDaoTest.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-4-27 上午10:42:18  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-27     jinghua.hao             1.0        1.0 Version  
 */

package com.justinmobile.tsm.customer.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.customer.domain.Customer;

/**
 * Filename: CustomerTestCase.java Description: Copyright: Copyright (c)2009
 * Company: justinmobile
 * 
 * @author: haojinghua
 * @version: 1.0 Create at: 2011-4-25 上午11:19:04
 * 
 */

public class CustomerDaoTest extends BaseAbstractTest {
	@Autowired
	private CustomerDao customerDao;

	@BeforeTransaction
	public void setUp() throws Exception {
		executeSql("insert into sys_user (id, user_name, email, latest_login, mobile, password, real_name, safe_answer, safe_question, salt, status) "
				+ "values (999999999, 'new_user_name', 'test_email', null, 'test_mobile', 'test_password', 'test_real_name', 'test_safe_answer', 'test_safe_question', null, 1)");
		executeSql("insert into customer(ID,ZIP,ADDRESS,NICK_NAME,SEX,BIRTHDAY,LOCATION,REG_DATE,ACTIVE_CODE,ACTIVE,ICON_URL) values(999999999,'000000','四川成都','郝静华',1,TO_DATE('2011-04-27','YYYY-MM-DD'),'四川成都',TO_DATE('2011-04-27','YYYY-MM-DD'),'1122334455667788',1,'/home/20110427/1.jpg')");

	}

	@AfterTransaction
	public void tearDown() throws Exception {
		executeSql("delete from sys_user where id = 999999999");
		executeSql("delete from customer where ID=999999999");
	}

	@Test
	public void testCRUD() {
		Customer c = new Customer();
		setSimpleProperties(c);
		c.setNickName("haojinghua");
		c.setId(999999998L);
		System.out.println(c);
		Assert.assertNotNull(c.getId());
		customerDao.saveOrUpdate(c);

		Long id = c.getId();
		Assert.assertNotNull(id);

		Customer cTestSave = customerDao.load(id);
		Assert.assertEquals("haojinghua", cTestSave.getNickName());

		cTestSave.setNickName("test_modify_name");
		customerDao.saveOrUpdate(cTestSave);

		Customer cTestUpdate = customerDao.load(id);
		Assert.assertEquals("test_modify_name", cTestUpdate.getNickName());

		customerDao.remove(id);
		try {
			customerDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
}
