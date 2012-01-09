package com.justinmobile.tsm.system.dao;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.system.domain.SystemParams;

public class SystemParamsDaoTest extends BaseAbstractTest {
	
	@Autowired
	private SystemParamsDao paramsDao;

	@Before
	public void setUp() throws Exception {
		executeSql("insert into system_params (id, description, key, type, value) values (999999999, 'test_description', 'test_key', 'test_type', 'test_value')");
	}

	@After
	public void tearDown() throws Exception {
		executeSql("delete from system_params where id = 999999999");
	}
	
	@Test
	public void testCRUD() {
		SystemParams param = new SystemParams();
		setSimpleProperties(param);
		param.setKey("test_userName");

		Assert.assertNull(param.getId());
		paramsDao.saveOrUpdate(param);
		Long id = param.getId();
		Assert.assertNotNull(id);
		
		SystemParams paramTestSave = paramsDao.load(id);
		Assert.assertEquals("test_userName", paramTestSave.getKey());
		
		paramTestSave.setKey("test_modify_name");
		paramsDao.saveOrUpdate(paramTestSave);
		
		SystemParams paramTestUpdate = paramsDao.load(id);
		Assert.assertEquals("test_modify_name", paramTestUpdate.getKey());
		
		paramsDao.remove(id);
		try {
			paramsDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

}
