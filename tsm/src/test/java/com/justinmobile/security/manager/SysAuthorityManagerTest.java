package com.justinmobile.security.manager;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.google.common.collect.Lists;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysAuthority;

public class SysAuthorityManagerTest extends BaseAbstractTest {
	
	@Autowired
	private SysAuthorityManager sysAuthorityManager;
	
	@BeforeTransaction
	public void setUp() throws Exception {
		executeSql("insert into sys_authority (id, auth_name, auth_type, description, status)" +
		  							  "values (999999999, 'test_auth_name', 'test_auth_type', 'test_description', 1)");
		executeSql("insert into sys_menu (id, menu_name, order_no, parent_id, url) " +
		 						 "values (999999999, 'test_menu_name', 0, null, 'test_url')");
		executeSql("insert into sys_menu (id, menu_name, order_no, parent_id, url) " +
		 						 "values (999999998, 'test_menu_name2', 1, null, 'test_url2')");
		executeSql("insert into sys_menu (id, menu_name, order_no, parent_id, url) " +
		 						 "values (999999997, 'test_menu_name3', 1, null, 'test_url3')");
		executeSql("insert into sys_auth_menu (menu_id, auth_id) values (999999999, 999999999)");
		executeSql("insert into sys_auth_menu (menu_id, auth_id) values (999999998, 999999999)");
		executeSql("insert into sys_resource (id, res_name, filter_string) values (999999999, 'test_res_name', 'test_filter_string')");
		executeSql("insert into sys_resource (id, res_name, filter_string) values (999999998, 'test_res_name2', 'test_filter_string2')");
	}

	@AfterTransaction
	public void tearDown() throws Exception {
		executeSql("delete from sys_auth_menu where auth_id = 999999999");
		executeSql("delete from sys_auth_res where auth_id = 999999999");
		executeSql("delete from sys_resource where id in (999999999, 999999998)");
		executeSql("delete from sys_authority where id = 999999999");
		executeSql("delete from sys_menu where id in (999999999, 999999998, 999999997)");
	}

	@Test
	public void testAddAndRemoveAuthority() throws Exception {
		SysAuthority sysAuthority = new SysAuthority();
		setSimpleProperties(sysAuthority);
		sysAuthority.setAuthName("test_authName");
		sysAuthorityManager.saveOrUpdate(sysAuthority);
		Assert.assertNotNull(sysAuthority.getId());
		sysAuthorityManager.removeAuthority(sysAuthority.getId());
		List<Long> ids = Lists.newArrayList(sysAuthority.getId());
		List<SysAuthority> auths = sysAuthorityManager.get(ids);
		Assert.assertEquals(0, auths.size());
	}
	
	@Test
	public void testSetMenus() throws Exception {
		String menus = "999999998 999999997";
		sysAuthorityManager.setMenus(999999999l, menus);
		SysAuthority auth = sysAuthorityManager.load(999999999l);
		Assert.assertEquals(2, auth.getSysMenus().size());
	}
	
	@Test
	public void testAddAndDelResources() throws Exception {
		String reses = "999999998 999999999";
		sysAuthorityManager.addResources(999999999l, reses);
		SysAuthority auth = sysAuthorityManager.load(999999999l);
		Assert.assertEquals(2, auth.getSysResources().size());
		sysAuthorityManager.delResources(999999999l, "999999999");
		Assert.assertEquals(1, auth.getSysResources().size());
	}
}
