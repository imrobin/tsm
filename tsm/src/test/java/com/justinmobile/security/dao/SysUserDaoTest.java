package com.justinmobile.security.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysUser;

public class SysUserDaoTest extends BaseAbstractTest {
	
	@Autowired
	private SysUserDao sysUserDao;
	
	@BeforeTransaction
	public void setup() {
		executeSql("insert into sys_user (id, user_name, email, latest_login, mobile, password, real_name, safe_answer, safe_question, salt, status) " +
								 "values (999999999, 'new_user_name', 'test_email', null, 'test_mobile', 'test_password', 'test_real_name', 'test_safe_answer', 'test_safe_question', null, 1)");
	}
	
	@AfterTransaction
	public void teardown() {
		executeSql("delete from sys_user where id = 999999999");
	}
	
	@Test
	public void testCRUD() {
		SysUser user = new SysUser();
		setSimpleProperties(user);
		user.setUserName("test_userName");

		Assert.assertNull(user.getId());
		sysUserDao.saveOrUpdate(user);
		Long id = user.getId();
		Assert.assertNotNull(id);
		
		SysUser userTestSave = sysUserDao.load(id);
		Assert.assertEquals("test_userName", userTestSave.getUserName());
		
		userTestSave.setUserName("test_modify_name");
		sysUserDao.saveOrUpdate(userTestSave);
		
		SysUser userTestUpdate = sysUserDao.load(id);
		Assert.assertEquals("test_modify_name", userTestUpdate.getUserName());
		
		sysUserDao.remove(id);
		try {
			sysUserDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testGetUserByNameOrMobileOrEmail() throws Exception {
		SysUser user = sysUserDao.getUserByNameOrMobileOrEmail("new_user_name");
		SysUser user2 = sysUserDao.getUserByNameOrMobileOrEmail("test_email");
		SysUser user3 = sysUserDao.getUserByNameOrMobileOrEmail("test_mobile");
		Assert.assertEquals(999999999l, user.getId().longValue());
		Assert.assertEquals(999999999l, user2.getId().longValue());
		Assert.assertEquals(999999999l, user3.getId().longValue());
	}
	
	@Test
	public void testGetUserByUserName() throws Exception {
		SysUser user = sysUserDao.getUserByUserName("new_user_name");
		Assert.assertEquals(999999999l, user.getId().longValue());
	}
	
	@Test
	public void testGetUserByEmail() throws Exception {
		SysUser user = sysUserDao.getUserByEmail("test_email");
		Assert.assertEquals(999999999l, user.getId().longValue());
	}
	
	@Test
	public void testGetUserByMobile() throws Exception {
		SysUser user = sysUserDao.getUserByMobile("test_mobile");
		Assert.assertEquals(999999999l, user.getId().longValue());
	}

}
