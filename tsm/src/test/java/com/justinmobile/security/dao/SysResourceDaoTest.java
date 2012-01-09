package com.justinmobile.security.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysResource;

public class SysResourceDaoTest extends BaseAbstractTest {

	@Autowired
	private SysResourceDao sysResourceDao;
	
	@BeforeTransaction
	public void setup() {
		executeSql("insert into sys_resource (id, res_name, filter_string) values (999999999, 'test_res_name', 'test_filter_string')");
	}
	
	@AfterTransaction
	public void teardown() {
		executeSql("delete from sys_resource where id = 999999999");
	}
	
	@Test
	public void testCRUD() {
		SysResource user = new SysResource();
		setSimpleProperties(user);
		user.setFilterString("test_FilterName");

		Assert.assertNull(user.getId());
		sysResourceDao.saveOrUpdate(user);
		Long id = user.getId();
		Assert.assertNotNull(id);
		
		SysResource userTestSave = sysResourceDao.load(id);
		Assert.assertEquals("test_FilterName", userTestSave.getFilterString());
		
		userTestSave.setFilterString("test_modify_Filtername");
		sysResourceDao.saveOrUpdate(userTestSave);
		
		SysResource userTestUpdate = sysResourceDao.load(id);
		Assert.assertEquals("test_modify_Filtername", userTestUpdate.getFilterString());
		
		sysResourceDao.remove(id);
		try {
			sysResourceDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testGetResourceByFilterString() throws Exception {
		SysResource resource = sysResourceDao.getResourceByFilterString("test_filter_string");
		Assert.assertEquals("test_res_name", resource.getResName());
	}

}
