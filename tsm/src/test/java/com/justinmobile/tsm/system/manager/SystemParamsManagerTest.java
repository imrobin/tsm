package com.justinmobile.tsm.system.manager;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.system.domain.SystemParams;

public class SystemParamsManagerTest extends BaseAbstractTest {
	
	@Autowired
	private SystemParamsManager paramsManager;

	@Before
	public void setUp() throws Exception {
		executeSql("insert into system_params (id, description, key, type, value) values (999999999, 'test_description', 'test_key', 'test_type', 'test_value')");
	}

	@After
	public void tearDown() throws Exception {
		executeSql("delete from system_params where id = 999999999");
	}

	@Test
	public void testGetParamsByType() {
		List<SystemParams> params = paramsManager.getParamsByType("test_type");
		Assert.assertEquals(1, params.size());
	}

}
