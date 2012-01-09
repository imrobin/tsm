package com.justinmobile.security.manager;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.google.common.collect.Lists;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysAuthority;
import com.justinmobile.security.domain.SysRole;

public class SysRoleManagerTest extends BaseAbstractTest {
	
	@Autowired
	private SysRoleManager sysRoleManager;

	@BeforeTransaction
	public void setUp() throws Exception {
		executeSql("insert into sys_role (id, description, login_success_forward, role_name) " +
		 						 "values (999999999, 'test_description', 'test_login_success_forward', 'test_role_role_name')");
		executeSql("insert into sys_authority (id, auth_name, auth_type, description, status)" +
		  "values (999999999, 'test_auth_name', 'test_auth_type', 'test_description', 1)");
		executeSql("insert into sys_authority (id, auth_name, auth_type, description, status)" +
		  "values (999999998, 'test_auth_name2', 'test_auth_type2', 'test_description2', 1)");
		executeSql("insert into sys_authority (id, auth_name, auth_type, description, status)" +
		  "values (999999997, 'test_auth_name3', 'test_auth_type3', 'test_description3', 1)");
	}

	@AfterTransaction
	public void tearDown() throws Exception {
		executeSql("delete from sys_role_auth where role_id = 999999999");
		executeSql("delete from sys_authority where id in (999999999, 999999998, 999999997)");
		executeSql("delete from sys_role where id = 999999999");
	}

	@Test
	public void testAddRoleAndRemoveRole() {
		SysRole role = new SysRole();
		setSimpleProperties(role);
		role.setRoleName("test_role_name");
		
		Assert.assertNull(role.getId());
		sysRoleManager.saveOrUpdate(role);
		Assert.assertNotNull(role.getId());
		sysRoleManager.removeRole(role.getId());
		List<Long> ids = Lists.newArrayList(role.getId());
		List<SysRole> roles = sysRoleManager.get(ids);
		Assert.assertEquals(0, roles.size());
	}

	@Test
	public void testAddAuthsAndDelAuths() {
		String auths = "999999999 999999998 999999997";
		sysRoleManager.addAuths(999999999l, auths);
		SysRole role = sysRoleManager.load(999999999l);
		Set<SysAuthority> authorities = role.getSysAuthorities();
		Assert.assertEquals(3, authorities.size());
		String delAuths = "999999999 999999998";
		sysRoleManager.delAuths(999999999l, delAuths);
		SysRole delRole = sysRoleManager.load(999999999l);
		Assert.assertEquals(1, delRole.getSysAuthorities().size());
	}

}
