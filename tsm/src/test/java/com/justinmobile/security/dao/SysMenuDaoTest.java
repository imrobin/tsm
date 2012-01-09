package com.justinmobile.security.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysMenu;

public class SysMenuDaoTest extends BaseAbstractTest {
	
	@Autowired
	private SysMenuDao sysMenuDao;
	
	@BeforeTransaction
	public void setup() {
		executeSql("insert into sys_menu (id, menu_name, order_no, parent_id, url) " +
								 "values (999999999, 'test_menu_name', 0, null, 'test_url')");
	}
	
	@AfterTransaction
	public void teardown() {
		executeSql("delete from sys_menu where id = 999999999");
	}
	
	@Test
	public void testCRUD() {
		SysMenu user = new SysMenu();
		setSimpleProperties(user);
		user.setMenuName("test_MenuName");

		Assert.assertNull(user.getId());
		sysMenuDao.saveOrUpdate(user);
		Long id = user.getId();
		Assert.assertNotNull(id);
		
		SysMenu menuTestSave = sysMenuDao.load(id);
		Assert.assertEquals("test_MenuName", menuTestSave.getMenuName());
		
		menuTestSave.setMenuName("test_modify_MenuName");
		sysMenuDao.saveOrUpdate(menuTestSave);
		
		SysMenu menuTestUpdate = sysMenuDao.load(id);
		Assert.assertEquals("test_modify_MenuName", menuTestUpdate.getMenuName());
		
		sysMenuDao.remove(id);
		try {
			sysMenuDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
}