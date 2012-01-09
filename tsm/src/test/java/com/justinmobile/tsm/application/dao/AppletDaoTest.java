package com.justinmobile.tsm.application.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.utils.AppletUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.application.utils.LoadModuleUtils;

@TransactionConfiguration
public class AppletDaoTest extends BaseAbstractTest {

	@Autowired
	private AppletDao target;

	@Autowired
	private AppletDao appletDao;

	@Test
	public void testGetByInstallOrder() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		Applet applet1 = AppletUtils.createDefult();
		applet1.setOrderNo(1);
		Applet applet2 = AppletUtils.createDefult();
		applet1.setOrderNo(2);
		Applet applet3 = AppletUtils.createDefult();
		applet1.setOrderNo(2);

		Set<Applet> appletSet = new HashSet<Applet>(3);
		appletSet.add(applet1);
		appletSet.add(applet2);
		appletSet.add(applet3);
		for (Applet applet : appletSet) {
			applet.assignApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);
		}

		Applet appletOtherAppVer = AppletUtils.createDefult();
		appletOtherAppVer.setOrderNo(1);
		appletOtherAppVer.assignApplicationVersion(ApplicationVersionUtils.createDefult());
		appletDao.saveOrUpdate(appletOtherAppVer);

		appletDao.getAll();

		List<Applet> result = null;
		try {
			result = target.getByInstallOrder(appVer.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数量", appletSet.size(), result.size());
		for (Applet applet : appletSet) {
			System.out.println("applet.id:" + applet.getId());
			Assert.assertTrue("预期的实例都在检索结果中", result.contains(applet));
		}
		Assert.assertEquals("实例1顺序", applet1, result.get(0));
		Assert.assertEquals("实例2与实例3次序相同", result.get(1).getOrderNo(), result.get(2).getOrderNo());
		Assert.assertTrue("实例2ID小于实例3ID", result.get(1).getId() < result.get(2).getId());
	}

	@Test
	public void testGetByLoadFileVersionIdAndApplicationVersionId() {
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		// 干扰数据1，其他的应用版本
		{
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			lm.setLoadFileVersion(lfVer);
			applet.setLoadModule(lm);
			applet.setApplicationVersion(ApplicationVersionUtils.createDefult());
			appletDao.saveOrUpdate(applet);
		}

		// 干扰数据2，其他的加载文件版本
		{
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			lm.setLoadFileVersion(LoadFileVersionUtils.createDefult());
			applet.setLoadModule(lm);
			applet.setApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);
		}

		// 正常数据

		Applet applet1 = AppletUtils.createDefult();
		Applet applet2 = AppletUtils.createDefult();
		Applet applet3 = AppletUtils.createDefult();
		Set<Applet> applets = new HashSet<Applet>(3);
		applets.add(applet1);
		applets.add(applet2);
		applets.add(applet3);
		for (Applet applet : applets) {
			LoadModule lm = LoadModuleUtils.createDefualt();
			lm.setLoadFileVersion(lfVer);
			applet.setLoadModule(lm);
			applet.setApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);
		}

		List<Applet> result = null;
		try {
			result = target.getByLoadFileVersionAndApplicationVersion(lfVer.getId(), appVer.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数目", applets.size(), result.size());
		for (Applet applet : applets) {
			Assert.assertTrue("结果范围", result.contains(applet));
		}
	}

	@Test
	public void testGetByLoadFileVersionAndApplicationVersion() {
		LoadModule lm = LoadModuleUtils.createDefualt();

		Applet applet1 = AppletUtils.createDefult();
		applet1.assignLoadModule(lm);
		appletDao.saveOrUpdate(applet1);
		Applet applet2 = AppletUtils.createDefult();
		applet2.assignLoadModule(lm);
		appletDao.saveOrUpdate(applet2);
		Applet applet3 = AppletUtils.createDefult();
		applet3.assignLoadModule(lm);
		appletDao.saveOrUpdate(applet3);

		Applet applet4 = AppletUtils.createDefult();
		applet4.assignLoadModule(LoadModuleUtils.createDefualt());
		appletDao.saveOrUpdate(applet4);

		appletDao.getAll();
		int result = 0;
		try {
			result = target.getCountThatBelongLoadModule(lm.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertEquals("结果", 3, result);
	}
}
