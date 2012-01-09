package com.justinmobile.tsm.sp.dao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.dao.SysUserDao;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

public class SpBaseInfoDaoTest extends BaseAbstractTest {

	@Autowired
	private SysUserDao sysUserDao;
	
	@Autowired
	private SpBaseInfoDao spBaseInfoDao;
	
	@Test
	public void testPropertyUnique() throws Exception {
		SysUser sysUser = new SysUser();
		setSimpleProperties(sysUser);
		sysUserDao.saveOrUpdate(sysUser);
		Assert.assertNotNull(sysUser.getId());
		
		SpBaseInfo sp = new SpBaseInfo();
		setSimpleProperties(sp);
		sp.setName("test_name");
		sp.setSysUser(sysUser);
		
		Assert.assertNull(sp.getId());
		spBaseInfoDao.saveOrUpdate(sp);
		Long id = sp.getId();
		Assert.assertNotNull(id);
		
		boolean result = spBaseInfoDao.isPropertyUnique("name", "test_name", null);
		Assert.assertFalse(result);

		result = spBaseInfoDao.isPropertyUnique("name", "test_name_new", null);
		Assert.assertFalse(!result);
		
		SpBaseInfo sp4Remove = spBaseInfoDao.load(id);
		Assert.assertNotNull(sp4Remove);
		
		spBaseInfoDao.remove(id);
		sysUserDao.remove(id);
	}
	
	@Test
	public void testCRUD() {
		SysUser sysUser = new SysUser();
		setSimpleProperties(sysUser);
		sysUserDao.saveOrUpdate(sysUser);
		Assert.assertNotNull(sysUser.getId());
		
		SpBaseInfo sp = new SpBaseInfo();
		setSimpleProperties(sp);
		sp.setName("test_name");
		sp.setSysUser(sysUser);
		
		Assert.assertNull(sp.getId());
		spBaseInfoDao.saveOrUpdate(sp);
		Long id = sp.getId();
		Assert.assertNotNull(id);
		
		SpBaseInfo spTestSave = spBaseInfoDao.load(id);
		Assert.assertEquals("test_name", spTestSave.getName());
		
		spTestSave.setName("test_modify_name");
		spBaseInfoDao.saveOrUpdate(sp);
		
		SpBaseInfo spTestUpdate = spBaseInfoDao.load(id);
		Assert.assertEquals("test_modify_name", spTestUpdate.getName());
		
		spBaseInfoDao.remove(id);
		try {
			spBaseInfoDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
		sysUserDao.remove(id);
	}
}
