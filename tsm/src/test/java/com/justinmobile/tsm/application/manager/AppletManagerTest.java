package com.justinmobile.tsm.application.manager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.AppletDao;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.dao.LoadModuleDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.utils.AppletUtils;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadModuleUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration
public class AppletManagerTest extends BaseAbstractTest {

	@Autowired
	AppletManager target;

	@Autowired
	ApplicationVersionDao applicationVersionDao;

	@Autowired
	LoadModuleDao loadModuleDao;

	@Autowired
	SpBaseInfoDao spDao;

	@Autowired
	AppletDao appletDao;

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCreateNewApplet() {
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		Application application = ApplicationUtils.createDefult("11");

		applicationVersion.setApplication(application);
		application.setSp(sp);

		LoadModule loadModule = LoadModuleUtils.createDefualt("");

		applicationVersionDao.saveOrUpdate(applicationVersion);
		applicationVersionDao.getAll();
		loadModuleDao.saveOrUpdate(loadModule);

		Applet applet = AppletUtils.createDefult();
		try {
			target.createNewApplet(applet, applicationVersion.getId(), loadModule.getId(), sp.getSysUser().getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("应用版本", applicationVersion, applet.getApplicationVersion());
		Assert.assertEquals("模块", loadModule, applet.getLoadModule());
	}

	@Test
	public void testCreateNewAppletSpDiscard() {
		SpBaseInfo ownerSp = SpBaseInfoUtils.createDefult();
		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		Application application = ApplicationUtils.createDefult("11");

		applicationVersion.setApplication(application);
		application.setSp(ownerSp);

		applicationVersionDao.saveOrUpdate(applicationVersion);
		applicationVersionDao.getAll();

		LoadModule loadModule = LoadModuleUtils.createDefualt("11");
		loadModuleDao.saveOrUpdate(loadModule);
		loadModuleDao.getAll();

		SpBaseInfo requestSp = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(requestSp);
		spDao.getAll();

		Applet applet = AppletUtils.createDefult();
		try {
			target.createNewApplet(applet, applicationVersion.getId(), loadModule.getId(), requestSp.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_DEFINE_APPLET_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testSetInstallOrder() {
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		Application application = ApplicationUtils.createDefult();
		application.setSp(sp);

		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		applicationVersion.setApplication(application);

		int orignalOrder = 1028;
		Applet applet = AppletUtils.createDefult();
		applet.setApplicationVersion(applicationVersion);
		applet.setOrderNo(orignalOrder);
		appletDao.saveOrUpdate(applet);
		appletDao.getAll();

		Integer expertOrder = 1984;
		Assert.assertNotSame("测试前安装持续与预期不同", expertOrder, applet.getOrderNo());

		try {
			target.setInstallOrder(applet.getId(), expertOrder, sp.getSysUser().getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("测试后安装持续与预期相同", expertOrder, applet.getOrderNo());
	}

	@Test
	public void testSetInstallOrderSpDiscard() {
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		Application application = ApplicationUtils.createDefult();
		application.setSp(sp);

		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		applicationVersion.setApplication(application);

		int orignalOrder = 1028;
		Applet applet = AppletUtils.createDefult();
		applet.setApplicationVersion(applicationVersion);
		applet.setOrderNo(orignalOrder);
		appletDao.saveOrUpdate(applet);
		appletDao.getAll();

		SpBaseInfo requestSp = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(requestSp);
		spDao.getAll();

		Integer expertOrder = 1984;
		try {
			target.setInstallOrder(applet.getId(), expertOrder, requestSp.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_SET_INSTALL_ORDER_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testRemoveApplet() {
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		Application application = ApplicationUtils.createDefult();
		application.setSp(sp);

		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		applicationVersion.setApplication(application);

		Applet applet = AppletUtils.createDefult();
		applet.assignApplicationVersion(applicationVersion);

		LoadModule lm = LoadModuleUtils.createDefualt();
		applet.assignLoadModule(lm);

		appletDao.saveOrUpdate(applet);
		appletDao.getAll();

		Assert.assertEquals("应用版本删除前", 1, applicationVersion.getApplets().size());
		Assert.assertEquals("模块删除前", 1, lm.getApplets().size());
		try {
			System.out.println("调用前");
			target.removeApplet(applet.getId(), sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用后");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		Assert.assertEquals("应用版本删除后", 0, applicationVersion.getApplets().size());
		Assert.assertEquals("模块删除后", 0, lm.getApplets().size());
	}

	@Test
	public void testRemoveAppletSpDiscard() {
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		Application application = ApplicationUtils.createDefult();
		application.setSp(sp);

		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		applicationVersion.setApplication(application);

		Applet applet = AppletUtils.createDefult();
		applet.assignApplicationVersion(applicationVersion);

		LoadModule lm = LoadModuleUtils.createDefualt();
		applet.assignLoadModule(lm);

		appletDao.saveOrUpdate(applet);
		appletDao.getAll();

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();
		try {
			System.out.println("调用前");
			target.removeApplet(applet.getId(), spOther.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用后");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			System.out.println("抛出异常");
			Assert.assertEquals("验证异常码", PlatformErrorCode.APPLICAION_REMOVE_APPLET_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
}
