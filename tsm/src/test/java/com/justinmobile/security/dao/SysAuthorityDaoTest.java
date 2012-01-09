package com.justinmobile.security.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysAuthority;

public class SysAuthorityDaoTest extends BaseAbstractTest {

	@Autowired
	private SysAuthorityDao authorityDao;
	
	@BeforeTransaction
	public void setup() {
		executeSql("insert into sys_user (id, user_name, email, latest_login, mobile, password, real_name, safe_answer, safe_question, salt, status) " +
		 						 "values (999999999, 'new_user_name', 'test_email', null, 'test_mobile', 'test_password', 'test_real_name', 'test_safe_answer', 'test_safe_question', null, 1)");
		executeSql("insert into sys_user (id, user_name, email, latest_login, mobile, password, real_name, safe_answer, safe_question, salt, status) " +
		 						 "values (999999998, 'new_user_name2', 'test_email2', null, 'test_mobile2', 'test_password2', 'test_real_name2', 'test_safe_answer2', 'test_safe_question2', null, 1)");
		executeSql("insert into sys_authority (id, auth_name, auth_type, description, status)" +
									  "values (999999999, 'test_auth_name', 'test_auth_type', 'test_description', 1)");
		executeSql("insert into sys_authority (id, auth_name, auth_type, description, status)" +
		  							  "values (999999998, 'test_auth_name2', 'test_auth_type2', 'test_description2', 1)");
		executeSql("insert into sys_authority (id, auth_name, auth_type, description, status)" +
		  							  "values (999999997, 'test_auth_name3', 'test_auth_type3', 'test_description3', 1)");
		executeSql("insert into sys_resource (id, res_name, filter_string) values (999999999, 'test_res_name', 'test_filter_string')");
		executeSql("insert into sys_user_auth (user_id, auth_id) values (999999999, 999999999)");
		executeSql("insert into sys_user_auth (user_id, auth_id) values (999999998, 999999998)");
		executeSql("insert into sys_auth_res (res_id, auth_id) values (999999999, 999999999)");
	}
	
	@AfterTransaction
	public void teardown() {
		executeSql("delete from sys_user_auth where user_id = 999999999 and auth_id = 999999999");
		executeSql("delete from sys_user_auth where user_id = 999999998 and auth_id = 999999998");
		executeSql("delete from sys_auth_res where res_id = 999999999 and auth_id = 999999999");
		executeSql("delete from sys_authority where id in (999999999, 999999998, 999999997)");
		executeSql("delete from sys_user where id in (999999999, 999999998)");
		executeSql("delete from sys_resource where id = 999999999");
	}
	
	@Test
	public void testCRUD() {
		SysAuthority authority = new SysAuthority();
		setSimpleProperties(authority);
		authority.setAuthName("test_AuthName");

		Assert.assertNull(authority.getId());
		authorityDao.saveOrUpdate(authority);
		Long id = authority.getId();
		Assert.assertNotNull(id);
		
		SysAuthority authTestSave = authorityDao.load(id);
		Assert.assertEquals("test_AuthName", authTestSave.getAuthName());
		
		authTestSave.setAuthName("test_modify_name");
		authorityDao.saveOrUpdate(authTestSave);
		
		SysAuthority authTestUpdate = authorityDao.load(id);
		Assert.assertEquals("test_modify_name", authTestUpdate.getAuthName());
		
		authorityDao.remove(id);
		try {
			authorityDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testGetAllEnableAuthorities() throws Exception {
		List<SysAuthority> authorities = authorityDao.getAllEnableAuthorities();
		Assert.assertNotSame(0, authorities.size());
	}
	
	@Test
	public void testGetAllAuthoritiesByUserName() throws Exception {
		List<SysAuthority> authorities = authorityDao.getAllAuthoritiesByUserName("new_user_name");
		Assert.assertEquals(1, authorities.size());
	}

	@Test
	public void testGetAllNotAuthoritiesByUserName() throws Exception {
		List<SysAuthority> authorities = authorityDao.getAllNotAuthoritiesByUserName("new_user_name");
		Assert.assertTrue(authorities.size() >= 2);
	}

	@Test
	public void testGetSysAuthoritiesByResource() throws Exception {
		List<SysAuthority> authorities = authorityDao.getSysAuthoritiesByResource("test_filter_string");
		Assert.assertEquals(1, authorities.size());
	}
	
}
