package com.justinmobile.tsm.application.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;

@TransactionConfiguration
public class LoadFileVersionDaoTest extends BaseAbstractTest {

	@Autowired
	LoadFileVersionDao target;

	@Autowired
	LoadFileDao lfDao;

	@Autowired
	LoadFileVersionDao lfVerDao;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Autowired
	ApplicationDao appDao;

	@Test
	public void testGetByApplicationVersion() {
		// 准备测试数据
		ApplicationVersion appVersion = ApplicationVersionUtils.createDefult();

		LoadFileVersion loadFileVer1 = LoadFileVersionUtils.createDefult();
		LoadFileVersion loadFileVer2 = LoadFileVersionUtils.createDefult();
		LoadFileVersion loadFileVer3 = LoadFileVersionUtils.createDefult();

		Set<LoadFileVersion> experts = new HashSet<LoadFileVersion>(3);
		experts.add(loadFileVer1);
		experts.add(loadFileVer2);
		experts.add(loadFileVer3);

		for (LoadFileVersion loadFileVer : experts) {
			loadFileVer.addApplictionVersion(appVersion);
			target.saveOrUpdate(loadFileVer);
		}

		// 完成测试数据
		// 准备干扰数据
		ApplicationVersion appVersionSalt = ApplicationVersionUtils.createDefult();

		LoadFileVersion loadFileVerSalt1 = LoadFileVersionUtils.createDefult();
		LoadFileVersion loadFileVerSalt2 = LoadFileVersionUtils.createDefult();
		LoadFileVersion loadFileVerSalt3 = LoadFileVersionUtils.createDefult();

		Set<LoadFileVersion> salt = new HashSet<LoadFileVersion>(3);
		salt.add(loadFileVerSalt1);
		salt.add(loadFileVerSalt2);
		salt.add(loadFileVerSalt3);

		for (LoadFileVersion loadFileVer : experts) {
			loadFileVer.addApplictionVersion(appVersionSalt);
			target.saveOrUpdate(loadFileVer);
			System.out.println(loadFileVer.getId());
		}
		// 完成干扰数据

		target.getAll();

		List<LoadFileVersion> result = null;
		try {
			result = target.getByApplicationVersion(appVersion.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数目", experts.size(), result.size());
		for (LoadFileVersion actual : result) {
			System.out.println(actual.getId());
			boolean isContains = false;
			for (LoadFileVersion expert : experts) {
				if (expert.equals(actual)) {
					isContains = true;
				}
			}
			Assert.assertTrue("结果内容" + actual.toString(), isContains);
		}
	}

	@Test
	public void testGetCountByLoadFileAndVersionNo1() {
		String versionNo = "1.0.2";
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lfVer.setVersionNo(versionNo);

		LoadFile lf = LoadFileUtils.createDefult();
		lf.addLoadFileVersion(lfVer);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Integer result = null;
		try {
			result = target.getCountByLoadFileAndVersionNo(lf, versionNo);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals(1, (int) result);
	}

	@Test
	public void testGetCountByLoadFileAndVersionNo0() {
		String versionNo = "1.0.2";
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lfVer.setVersionNo("1.0.0");

		LoadFile lf = LoadFileUtils.createDefult();
		lf.addLoadFileVersion(lfVer);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Assert.assertNotSame("版本号不一样", versionNo, lfVer.getVersionNo());
		Integer result = null;
		try {
			result = target.getCountByLoadFileAndVersionNo(lf, versionNo);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals(0, (int) result);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetWhichImportedByApplicationVersion() {
		// 准备干扰数据
		{// 其他应用版本，公有
			ApplicationVersion appVersionSalt = ApplicationVersionUtils.createDefult();

			LoadFileVersion loadFileVerSalt1 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVerSalt2 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVerSalt3 = LoadFileVersionUtils.createDefult();

			Set<LoadFileVersion> salt = new HashSet<LoadFileVersion>(3);
			salt.add(loadFileVerSalt1);
			salt.add(loadFileVerSalt2);
			salt.add(loadFileVerSalt3);

			for (LoadFileVersion loadFileVer : salt) {
				loadFileVer.addApplictionVersion(appVersionSalt);
				LoadFile lf = LoadFileUtils.createDefult();
				lf.setShareFlag(LoadFile.FLAG_SHARED);
				target.saveOrUpdate(loadFileVer);
				System.out.println(loadFileVer.getId());
			}
		}
		{// 其他应用版本，私有
			ApplicationVersion appVersionSalt = ApplicationVersionUtils.createDefult();
			LoadFileVersion loadFileVer1 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVer2 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVer3 = LoadFileVersionUtils.createDefult();

			Set<LoadFileVersion> salt = new HashSet<LoadFileVersion>(3);
			salt.add(loadFileVer1);
			salt.add(loadFileVer2);
			salt.add(loadFileVer3);

			for (LoadFileVersion loadFileVer : salt) {
				loadFileVer.addApplictionVersion(appVersionSalt);
				LoadFile lf = LoadFileUtils.createDefult();
				lf.setShareFlag(LoadFile.FLAG_EXCLUSIVE);
				target.saveOrUpdate(loadFileVer);
			}
		}
		// 完成干扰数据

		// 准备测试数据
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVer);
		Set<LoadFileVersion> expertsShared = new HashSet<LoadFileVersion>(3);
		{
			LoadFileVersion loadFileVer1 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVer2 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVer3 = LoadFileVersionUtils.createDefult();

			expertsShared.add(loadFileVer1);
			expertsShared.add(loadFileVer2);
			expertsShared.add(loadFileVer3);

			for (LoadFileVersion loadFileVer : expertsShared) {
				loadFileVer.addApplictionVersion(appVer);
				LoadFile lf = LoadFileUtils.createDefult();
				lf.setShareFlag(LoadFile.FLAG_SHARED);
				lf.addLoadFileVersion(loadFileVer);
				target.saveOrUpdate(loadFileVer);
			}
		}

		Set<LoadFileVersion> expertsExclusive = new HashSet<LoadFileVersion>(3);
		{
			LoadFileVersion loadFileVer1 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVer2 = LoadFileVersionUtils.createDefult();
			LoadFileVersion loadFileVer3 = LoadFileVersionUtils.createDefult();

			expertsExclusive.add(loadFileVer1);
			expertsExclusive.add(loadFileVer2);
			expertsExclusive.add(loadFileVer3);

			for (LoadFileVersion loadFileVer : expertsExclusive) {
				loadFileVer.addApplictionVersion(appVer);
				LoadFile lf = LoadFileUtils.createDefult();
				lf.setShareFlag(LoadFile.FLAG_EXCLUSIVE);
				lf.addLoadFileVersion(loadFileVer);
				target.saveOrUpdate(loadFileVer);
			}
		}

		// 完成测试数据

		target.getAll();

		{// 测试查找私有文件
			List<LoadFileVersion> result = null;
			System.out.println("appVer.id: " + appVer.getId());
			try {
				result = target.getWhichImportedByApplicationVersion(appVer.getId(), LoadFile.FLAG_EXCLUSIVE);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("抛出异常");
			}

			Assert.assertEquals("结果数目", expertsExclusive.size(), result.size());
			for (LoadFileVersion lfv : expertsExclusive) {
				Assert.assertTrue("结果集合", result.contains(lfv));
			}
		}
		{// 测试查找共享文件
			List<LoadFileVersion> result = null;
			System.out.println("appVer.id: " + appVer.getId());
			try {
				result = target.getWhichImportedByApplicationVersion(appVer.getId(), LoadFile.FLAG_SHARED);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("抛出异常");
			}

			Assert.assertEquals("结果数目", expertsShared.size(), result.size());
			for (LoadFileVersion lfv : expertsShared) {
				Assert.assertTrue("结果集合", result.contains(lfv));
			}
		}
	}

	@Test
	public void testGetWhichImportedByApplicationVersionAndTypeObjectInt() {
		Application application = ApplicationUtils.createDefult();
		appDao.saveOrUpdate(application);

		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		applicationVersion.assignApplication(application);
		appVerDao.saveOrUpdate(applicationVersion);
		{// 干扰数据-加载文件被其他应用使用
			LoadFile loadFile = LoadFileUtils.createDefult();
			lfDao.saveOrUpdate(loadFile);

			LoadFileVersion loadFileVersion = LoadFileVersionUtils.createDefult();
			loadFile.addLoadFileVersion(loadFileVersion);
			lfVerDao.saveOrUpdate(loadFileVersion);

			Application applicationOther = ApplicationUtils.createDefult();
			appDao.saveOrUpdate(applicationOther);

			ApplicationVersion applicationVersionOther = ApplicationVersionUtils.createDefult();
			applicationVersionOther.assignApplication(applicationOther);
			appVerDao.saveOrUpdate(applicationVersionOther);

			loadFileVersion.addApplictionVersion(applicationVersionOther);
			lfVerDao.saveOrUpdate(loadFileVersion);

			System.out.println("干扰数据-加载文件被其他应用使用：" + loadFile.getId());
		}

		{// 干扰数据-加载文件被其他应用版本使用
			LoadFile loadFile = LoadFileUtils.createDefult();
			lfDao.saveOrUpdate(loadFile);

			LoadFileVersion loadFileVersion = LoadFileVersionUtils.createDefult();
			loadFile.addLoadFileVersion(loadFileVersion);
			lfVerDao.saveOrUpdate(loadFileVersion);

			ApplicationVersion applicationVersionOther = ApplicationVersionUtils.createDefult();
			applicationVersionOther.assignApplication(application);
			appVerDao.saveOrUpdate(applicationVersionOther);

			loadFileVersion.addApplictionVersion(applicationVersionOther);
			lfVerDao.saveOrUpdate(loadFileVersion);

			System.out.println("干扰数据-加载文件被其他应用版本使用：" + loadFile.getId());
		}

		// 正常数据
		LoadFile loadFile = LoadFileUtils.createDefult();
		lfDao.saveOrUpdate(loadFile);

		LoadFileVersion loadFileVersion = LoadFileVersionUtils.createDefult();
		loadFile.addLoadFileVersion(loadFileVersion);
		lfVerDao.saveOrUpdate(loadFileVersion);

		loadFileVersion.addApplictionVersion(applicationVersion);
		lfVerDao.saveOrUpdate(loadFileVersion);

		List<LoadFileVersion> result = null;
		try {
			result = target.getWhichImportedByApplicationVersionAndType(applicationVersion, loadFile.getType());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数正确", 1, result.size());
		System.out.println("预期：" + loadFileVersion.getId());
		System.out.println("实际：" + result.get(0).getId());
		Assert.assertTrue("结果正确", result.contains(loadFileVersion));
	}
}
