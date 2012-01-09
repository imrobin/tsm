package com.justinmobile.tsm.sp.manager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysUserUtils;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration(defaultRollback = true)
public class SpBaseInfoManagerTest extends BaseAbstractTest {

	private SpBaseInfo sp = null;

	@Autowired
	private SpBaseInfoManager spBaseInfoManager;
    
	@Test
	public void getAllUnAuditSp(){
		Page<SpBaseInfo> page = new Page<SpBaseInfo>();
		spBaseInfoManager.getUnAuditSp(page,"");
		System.out.println(page.getTotalCount());
	}
	/*@Before
	public void before() {
		sp = SpBaseInfoUtils.createDefult();
		spBaseInfoManager.saveOrUpdate(sp);
		executeSqlScript("file:src/test/java/com/justinmobile/tsm/sp/test_script.sql", false);
	}

	@After
	public void after() {
		sp = null;
		executeSqlScript("file:src/test/java/com/justinmobile/tsm/sp/clear_script.sql", false);
	}

	@Test
	public void testGetSpByNameOrMobileOrEmail() {
		SpBaseInfo actualUseName = spBaseInfoManager.getSpByNameOrMobileOrEmail(sp.getSysUser().getUserName());
		SpBaseInfo actualEmail = spBaseInfoManager.getSpByNameOrMobileOrEmail(sp.getSysUser().getEmail());
		SpBaseInfo actualMobile = spBaseInfoManager.getSpByNameOrMobileOrEmail(sp.getSysUser().getMobile());

		Assert.assertEquals(sp, actualUseName);
		Assert.assertEquals(sp, actualEmail);
		Assert.assertEquals(sp, actualMobile);
	}

	@Test
	public void testGetSpByNameOrMobileOrEmailNull() {
		SpBaseInfo actualUseName = spBaseInfoManager.getSpByNameOrMobileOrEmail("");
		SpBaseInfo actualEmail = spBaseInfoManager.getSpByNameOrMobileOrEmail("");
		SpBaseInfo actualMobile = spBaseInfoManager.getSpByNameOrMobileOrEmail("");

		Assert.assertNull(actualUseName);
		Assert.assertNull(actualEmail);
		Assert.assertNull(actualMobile);
	}

	@Test
	public void testGetSpByNameOrMobileOrEmailSysUserUnavaliable() {
		sp.getSysUser().setStatus(SysUser.USER_STATUS.DISABLED.getValue());

		SpBaseInfo actualUseName = spBaseInfoManager.getSpByNameOrMobileOrEmail(sp.getSysUser().getUserName());
		SpBaseInfo actualEmail = spBaseInfoManager.getSpByNameOrMobileOrEmail(sp.getSysUser().getUserName());
		SpBaseInfo actualMobile = spBaseInfoManager.getSpByNameOrMobileOrEmail(sp.getSysUser().getUserName());

		Assert.assertNull(actualUseName);
		Assert.assertNull(actualEmail);
		Assert.assertNull(actualMobile);
	}
	
	@Test
	public void testValidateSpFullName() throws Exception {
		String name = "FULL_NAME_001";
		
		SpBaseInfo spBaseInfo = SpBaseInfoUtils.getNewInstance();
		spBaseInfo.setSysUser(SysUserUtils.createDefult());
		spBaseInfo.setName(name);
		
		spBaseInfoManager.saveOrUpdate(spBaseInfo);
		
		Assert.assertFalse(spBaseInfoManager.validateSpFullName(name));
		Assert.assertTrue(spBaseInfoManager.validateSpFullName(name + name));
	}
	
	@Test
	public void testValidateSpShortName() throws Exception {
		String name = "SHORT_NAME_001";
		
		SpBaseInfo spBaseInfo = SpBaseInfoUtils.getNewInstance();
		spBaseInfo.setSysUser(SysUserUtils.createDefult());
		spBaseInfo.setShortName(name);
		
		spBaseInfoManager.saveOrUpdate(spBaseInfo);
		
		Assert.assertFalse(spBaseInfoManager.validateSpShortName(name));
		Assert.assertTrue(spBaseInfoManager.validateSpShortName(name + name));
	}*/
}
