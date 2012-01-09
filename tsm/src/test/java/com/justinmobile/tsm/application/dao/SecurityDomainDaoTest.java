package com.justinmobile.tsm.application.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.LoadModuleDao;
import com.justinmobile.tsm.application.dao.hibernate.SecurityDomainDaoHibernate;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

public class SecurityDomainDaoTest extends BaseAbstractTest {

	@Autowired
	private SecurityDomainDaoHibernate sdDao;

	@Autowired
	private LoadModuleDao loadModuleDao;
	
	@Autowired
	private SpBaseInfoDao spBaseInfoDao;
	
	@BeforeTransaction
	public void setup() throws Exception {
		executeSql("insert into load_file(id,load_file_aid) values(-1, 'test_load_file_aid')");
		executeSql("insert into load_file_version(id,load_file_id) values(-1,-1)");
		executeSql("insert into load_module(id,load_moduel_aid,load_file_version_id) values(-1,'test_load_moduel_aid',-1)");
		
		executeSql("insert into sys_user(id,user_name,password,status) values(-1,'test_user_name','test_password',1)");
		executeSql("insert into sp_base_info(id,no,name) values(-1,'test_no','test_name')");
	}
	
	@AfterTransaction
	public void teardown() throws Exception {
		executeSql("delete from load_module where id = -1");
		executeSql("delete from load_file_version where id = -1");
		executeSql("delete from load_file where id = -1");
		
		executeSql("delete from sp_base_info where id = -1");
		executeSql("delete from sys_user where id = -1");
	}
	
	@Test
	public void testCRUD() throws Exception {
		final Long id = -1L;
		SpBaseInfo sp = spBaseInfoDao.load(id);
		Assert.assertNotNull(sp);
		
		LoadModule loadModule = loadModuleDao.load(id);
		Assert.assertNotNull(loadModule);
		
		SecurityDomain sd = new SecurityDomain();
		setSimpleProperties(sd);
		sd.setAid("test_aid");
		sd.setInstallParams("test_install_params");
		sd.setSp(sp);
		sd.setLoadModule(loadModule);
		
		sdDao.saveOrUpdate(sd);
		Long sdId = sd.getId();
		Assert.assertNotNull(sdId);
		
		SecurityDomain sdTestSave = sdDao.load(sdId);
		Assert.assertEquals("test_aid", sdTestSave.getAid());
		
		sdTestSave.setInstallParams("test_modify_install_params");
		sdDao.saveOrUpdate(sdTestSave);
		
		SecurityDomain sdTestUpdate = sdDao.load(sdId);
		Assert.assertEquals("test_modify_install_params", sdTestUpdate.getInstallParams());
		
		sdDao.remove(sdId);
		try {
			sdDao.load(sdId);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void testGetIsd() {
		SecurityDomain expected = new SecurityDomain();
		expected.setAid("001122334455667788899");
		expected.setStatus(SecurityDomain.STATUS_PUBLISHED);
		expected.setModel(SecurityDomain.MODEL_ISD);

		sdDao.saveOrUpdate(expected);

		SecurityDomain actual = sdDao.getIsd();

		sdDao.remove(expected);

		System.out.println(expected);
		System.out.println(actual);
		Assert.assertEquals(expected, actual);
	}

}
