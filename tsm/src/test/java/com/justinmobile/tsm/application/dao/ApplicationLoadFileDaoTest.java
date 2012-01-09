package com.justinmobile.tsm.application.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.utils.ApplicationLoadFileUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;

@TransactionConfiguration
public class ApplicationLoadFileDaoTest extends BaseAbstractTest {

	@Autowired
	private ApplicationLoadFileDao target;

	@Autowired
	private ApplicationLoadFileDao appLfDao;

	@Autowired
	private LoadFileVersionDao lfVerDao;

	@Autowired
	private ApplicationVersionDao appVerDao;

	@Test
	public void testRemove() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVer);
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lfVerDao.saveOrUpdate(lfVer);

		ApplicationLoadFile alf = ApplicationLoadFileUtils.createDefult();
		alf.assignApplicationVersionAndLoadFileVersion(appVer, lfVer);
		appLfDao.saveOrUpdate(alf);


		System.out.println("保存测试数据开始");
		appLfDao.getAll();
		appLfDao.getAll();
		System.out.println("保存测试数据完成");

		try {
			System.out.println("调用目标方法开始");
			appLfDao.remove(alf);
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals(0, lfVer.getApplicationLoadFiles().size());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetByApplicationVersionAndLoadFileVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		lfVer.addApplictionVersion(appVer);
		lfVerDao.saveOrUpdate(lfVer);
		lfVerDao.getAll();

		ApplicationLoadFile result = null;
		try {
			result = target.getByApplicationVersionAndLoadFileVersion(appVer.getId(), lfVer.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("加载文件版本", lfVer, result.getLoadFileVersion());
		Assert.assertEquals("应用版本", appVer, result.getApplicationVersion());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetByApplicationVersionAndLoadFileVersionDiffApplicationVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		lfVer.addApplictionVersion(appVer);
		lfVerDao.saveOrUpdate(lfVer);
		lfVerDao.getAll();

		ApplicationVersion appVerOther = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVerOther);
		appVerDao.getAll();

		ApplicationLoadFile result = null;
		try {
			result = target.getByApplicationVersionAndLoadFileVersion(appVerOther.getId(), lfVer.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertNull(result);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetByApplicationVersionAndLoadFileVersionDiffLoadFileVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		lfVer.addApplictionVersion(appVer);
		lfVerDao.saveOrUpdate(lfVer);
		lfVerDao.getAll();

		LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
		lfVerDao.saveOrUpdate(lfVerOther);
		lfVerDao.getAll();

		Assert.assertNotSame("加载文件版本不同", lfVerOther, lfVer);
		ApplicationLoadFile result = null;
		try {
			result = target.getByApplicationVersionAndLoadFileVersion(appVer.getId(), lfVerOther.getId());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertNull(result);
	}

	@Test
	public void testGetExclusiveByDownloadOrder() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		ApplicationLoadFile appLf1 = ApplicationLoadFileUtils.createDefult();
		appLf1.setDownloadOrder(1);

		ApplicationLoadFile appLf2 = ApplicationLoadFileUtils.createDefult();
		appLf2.setDownloadOrder(2);
		ApplicationLoadFile appLf3 = ApplicationLoadFileUtils.createDefult();
		appLf3.setDownloadOrder(2);
		Set<ApplicationLoadFile> appLfSet = new HashSet<ApplicationLoadFile>(3);
		appLfSet.add(appLf1);
		appLfSet.add(appLf2);
		appLfSet.add(appLf3);
		for (ApplicationLoadFile appLf : appLfSet) {
			appLf.assignApplicationVersionAndLoadFileVersion(appVer, LoadFileVersionUtils.createDefult());
			appLf.getLoadFileVersion().getLoadFile().setShareFlag(LoadFile.FLAG_EXCLUSIVE);
			appLfDao.saveOrUpdate(appLf);
		}

		ApplicationLoadFile appLfOtherAppVer = ApplicationLoadFileUtils.createDefult();
		appLfOtherAppVer.assignApplicationVersionAndLoadFileVersion(ApplicationVersionUtils.createDefult(),
				LoadFileVersionUtils.createDefult());
		appLfOtherAppVer.getLoadFileVersion().getLoadFile().setShareFlag(LoadFile.FLAG_EXCLUSIVE);
		appLfDao.saveOrUpdate(appLfOtherAppVer);

		ApplicationLoadFile appLfLfShared = ApplicationLoadFileUtils.createDefult();
		appLfLfShared.assignApplicationVersionAndLoadFileVersion(ApplicationVersionUtils.createDefult(),
				LoadFileVersionUtils.createDefult());
		appLfLfShared.getLoadFileVersion().getLoadFile().setShareFlag(LoadFile.FLAG_SHARED);
		appLfDao.saveOrUpdate(appLfLfShared);

		appLfDao.getAll();

		List<ApplicationLoadFile> result = null;
		try {
			System.out.println("调用目标方法");
			result = target.getExclusiveByDownloadOrder(appVer.getId());
			System.out.println("完成目标方法");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数量", appLfSet.size(), result.size());
		Assert.assertEquals("加载文件1", appLf1, result.get(0));
		Assert.assertEquals("加载文件2与加载文件3下载次序相同", result.get(2).getDownloadOrder(), result.get(1).getDownloadOrder());
		Assert.assertTrue("加载文件2的ID小于加载文件3的ID", result.get(1).getId() < result.get(2).getId());
	}

	@Test
	public void testGetExclusiveByDeleteOrder() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();

		ApplicationLoadFile appLf1 = ApplicationLoadFileUtils.createDefult();
		appLf1.setDeleteOrder(1);

		ApplicationLoadFile appLf2 = ApplicationLoadFileUtils.createDefult();
		appLf2.setDeleteOrder(2);

		ApplicationLoadFile appLf3 = ApplicationLoadFileUtils.createDefult();
		appLf3.setDeleteOrder(2);

		Set<ApplicationLoadFile> appLfSet = new HashSet<ApplicationLoadFile>(3);
		appLfSet.add(appLf1);
		appLfSet.add(appLf2);
		appLfSet.add(appLf3);
		for (ApplicationLoadFile appLf : appLfSet) {
			appLf.assignApplicationVersionAndLoadFileVersion(appVer, LoadFileVersionUtils.createDefult());
			appLf.getLoadFileVersion().getLoadFile().setShareFlag(LoadFile.FLAG_EXCLUSIVE);
			appLfDao.saveOrUpdate(appLf);
		}

		ApplicationLoadFile appLfOtherAppVer = ApplicationLoadFileUtils.createDefult();
		appLfOtherAppVer.assignApplicationVersionAndLoadFileVersion(ApplicationVersionUtils.createDefult(),
				LoadFileVersionUtils.createDefult());
		appLfOtherAppVer.getLoadFileVersion().getLoadFile().setShareFlag(LoadFile.FLAG_EXCLUSIVE);
		appLfDao.saveOrUpdate(appLfOtherAppVer);

		ApplicationLoadFile appLfLfShared = ApplicationLoadFileUtils.createDefult();
		appLfLfShared.assignApplicationVersionAndLoadFileVersion(ApplicationVersionUtils.createDefult(),
				LoadFileVersionUtils.createDefult());
		appLfLfShared.getLoadFileVersion().getLoadFile().setShareFlag(LoadFile.FLAG_SHARED);
		appLfDao.saveOrUpdate(appLfLfShared);

		appLfDao.getAll();

		List<ApplicationLoadFile> result = null;
		try {
			System.out.println("调用目标方法");
			result = target.getExclusiveByDeleteOrder(appVer.getId());
			System.out.println("完成目标方法");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数量", appLfSet.size(), result.size());
		ApplicationLoadFile alf1 = result.get(0);
		Assert.assertEquals("加载文件1", appLf1, alf1);
		Assert.assertEquals("加载文件2与加载文件3下载次序相同", result.get(2).getDeleteOrder(), result.get(1).getDeleteOrder());
		Assert.assertTrue("加载文件2的ID大于于加载文件3的ID", result.get(1).getId() > result.get(2).getId());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetByLoadFileVersionAndApplicationVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		// 正常数据，指定的ApplicationVersion，指定的LoadFileVersion
		ApplicationLoadFile alf1 = ApplicationLoadFileUtils.createDefult();
		alf1.assignApplicationVersionAndLoadFileVersion(appVer, lfVer);

		// 干扰数据，指定的ApplicationVersion，其他的LoadFileVersion
		ApplicationLoadFile alf2 = ApplicationLoadFileUtils.createDefult();
		alf2.assignApplicationVersionAndLoadFileVersion(appVer, LoadFileVersionUtils.createDefult());

		// 干扰数据，其他的ApplicationVersion，指定的LoadFileVersion
		ApplicationLoadFile alf3 = ApplicationLoadFileUtils.createDefult();
		alf3.assignApplicationVersionAndLoadFileVersion(ApplicationVersionUtils.createDefult(), lfVer);

		// 干扰数据，其他的ApplicationVersion，其他的LoadFileVersion
		ApplicationLoadFile alf4 = ApplicationLoadFileUtils.createDefult();
		alf4.assignApplicationVersionAndLoadFileVersion(ApplicationVersionUtils.createDefult(), LoadFileVersionUtils.createDefult());

		// 保存测试数据
		appLfDao.saveOrUpdate(alf1);
		appLfDao.saveOrUpdate(alf2);
		appLfDao.saveOrUpdate(alf3);
		appLfDao.saveOrUpdate(alf4);
		appLfDao.getAll();

		ApplicationLoadFile result = null;
		try {
			System.out.println("调用目标方法开始");
			result = target.getByApplicationVersionAndLoadFileVersion(appVer.getId(), lfVer.getId());
			System.out.println("调用目标方法结束");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals("结果对比", alf1, result);
	}
}
