package com.justinmobile.security.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysRole;

public class SysRoleDaoTest extends BaseAbstractTest {

	@Autowired
	private SysRoleDao sysRoleDao;
	
	@BeforeTransaction
	public void setup() {
		executeSql("insert into sys_role (id, description, login_success_forward, role_name) " +
								 "values (999999999, 'test_description', 'test_login_success_forward', 'test_role_role_name')");
	}
	
	@AfterTransaction
	public void teardown() {
		executeSql("delete from sys_role where id = 999999999");
	}
	
	@Test
	public void testCRUD() {
		SysRole role = new SysRole();
		setSimpleProperties(role);
		role.setRoleName("test_RoleName");

		Assert.assertNull(role.getId());
		sysRoleDao.saveOrUpdate(role);
		Long id = role.getId();
		Assert.assertNotNull(id);
		
		SysRole roleTestSave = sysRoleDao.load(id);
		Assert.assertEquals("test_RoleName", roleTestSave.getRoleName());
		
		roleTestSave.setRoleName("test_modify_name");
		sysRoleDao.saveOrUpdate(roleTestSave);
		
		SysRole roleTestUpdate = sysRoleDao.load(id);
		Assert.assertEquals("test_modify_name", roleTestUpdate.getRoleName());
		
		sysRoleDao.remove(id);
		try {
			sysRoleDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
	public void testGetRoleByName() {
		SysRole role = sysRoleDao.getRoleByName("test_role_role_name");
		Assert.assertEquals("test_login_success_forward", role.getLoginSuccessForward());
	}

}
