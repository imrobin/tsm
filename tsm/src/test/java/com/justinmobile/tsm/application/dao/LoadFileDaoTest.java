package com.justinmobile.tsm.application.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration
public class LoadFileDaoTest extends BaseAbstractTest {

	@Autowired
	private LoadFileDao target;

	@Autowired
	private LoadFileDao lfDao;

	@Autowired
	private LoadFileVersionDao lfVerDao;

	@Autowired
	private ApplicationVersionDao appVerDao;

	@Autowired
	private SpBaseInfoDao spDao;

	@Autowired
	private ApplicationDao appDao;

	@Test
	public void testGetLoadFilesWhichExclusivAndBelongSpAndUnassociateWithApplicationVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		appVerDao.saveOrUpdate(appVer);
		appVerDao.getAll();

		spDao.saveOrUpdate(sp);
		spDao.getAll();

		{
			// 第1组干扰数据：已被指定的应用版本引用
			System.out.println("第1组干扰数据");
			LoadFile lf1 = LoadFileUtils.createDefult();
			LoadFile lf2 = LoadFileUtils.createDefult();
			LoadFile lf3 = LoadFileUtils.createDefult();
			Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
			lfSet.add(lf1);
			lfSet.add(lf2);
			lfSet.add(lf3);
			for (LoadFile lf : lfSet) {
				lf.setShareFlag(LoadFile.FLAG_EXCLUSIVE);
				LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer);
				lfVer.addApplictionVersion(appVer);
				lfDao.saveOrUpdate(lf);
				System.out.println("lf.id: " + lf.getId());
			}
			lfDao.getAll();
		}

		{
			// 第2组干扰数据：加载文件为公有的
			System.out.println("第2组干扰数据");
			LoadFile lf1 = LoadFileUtils.createDefult();
			LoadFile lf2 = LoadFileUtils.createDefult();
			LoadFile lf3 = LoadFileUtils.createDefult();
			Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
			lfSet.add(lf1);
			lfSet.add(lf2);
			lfSet.add(lf3);
			for (LoadFile lf : lfSet) {
				lf.setShareFlag(LoadFile.FLAG_SHARED);
				LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer);
				lfDao.saveOrUpdate(lf);
				System.out.println("lf.id: " + lf.getId());
			}
			lfDao.getAll();
		}

		{
			// 第3组干扰数据：加载文件不属于该SP
			System.out.println(" 第3组干扰数据");
			LoadFile lf1 = LoadFileUtils.createDefult();
			LoadFile lf2 = LoadFileUtils.createDefult();
			LoadFile lf3 = LoadFileUtils.createDefult();
			Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
			lfSet.add(lf1);
			lfSet.add(lf2);
			lfSet.add(lf3);
			SpBaseInfo otherSp = SpBaseInfoUtils.createRandom();
			for (LoadFile lf : lfSet) {
				lf.setShareFlag(LoadFile.FLAG_EXCLUSIVE);
				lf.setSp(otherSp);
				LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer);
				lfDao.saveOrUpdate(lf);
				System.out.println("lf.id: " + lf.getId());
			}
			lfDao.getAll();
		}

		{
			// 第4组干扰数据：加载文件其他版本被指定应用版本使用
			System.out.println(" 第4组干扰数据");
			LoadFile lf1 = LoadFileUtils.createDefult();
			LoadFile lf2 = LoadFileUtils.createDefult();
			LoadFile lf3 = LoadFileUtils.createDefult();
			Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
			lfSet.add(lf1);
			lfSet.add(lf2);
			lfSet.add(lf3);
			for (LoadFile lf : lfSet) {
				lf.setShareFlag(LoadFile.FLAG_EXCLUSIVE);
				lf.setSp(sp);
				LoadFileVersion lfVer1 = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer1);
				lfVer1.addApplictionVersion(appVer);
				LoadFileVersion lfVer2 = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer2);
				lfDao.saveOrUpdate(lf);
				System.out.println("lf.id: " + lf.getId());
			}
			lfDao.getAll();
		}

		// 正常数据：加载文件属于指定sp、非共享、未被指定应用版本引用
		System.out.println("正常数据");
		LoadFile lf1 = LoadFileUtils.createDefult();
		LoadFile lf2 = LoadFileUtils.createDefult();
		LoadFile lf3 = LoadFileUtils.createDefult();
		Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
		lfSet.add(lf1);
		lfSet.add(lf2);
		lfSet.add(lf3);
		for (LoadFile lf : lfSet) {
			lf.setShareFlag(LoadFile.FLAG_EXCLUSIVE);
			lf.setSp(sp);
			LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
			lf.addLoadFileVersion(lfVer);
			lfDao.saveOrUpdate(lf);
			System.out.println("lf.id: " + lf.getId());
		}
		lfDao.getAll();
		{
			System.out.println("被其他应用版本引用");
			LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
			lf1.addLoadFileVersion(lfVer);
			lfVer.addApplictionVersion(ApplicationVersionUtils.createDefult());
			lfDao.saveOrUpdate(lf1);
			System.out.println("lf1.id: " + lf1.getId());
		}
		lfDao.getAll();

		List<LoadFile> reslut = null;
		try {
			System.out.println("调用目标方法开始");
			reslut = target.getLoadFilesWhichExclusivAndBelongSpAndUnassociateWithApplicationVersion(sp, appVer.getId());
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		for (LoadFile lf : reslut) {
			System.out.println("result.id: " + lf.getId());
		}

		Assert.assertEquals("结果数目", lfSet.size(), reslut.size());
		Assert.assertTrue("结果中包含所有预期的实体", reslut.containsAll(lfSet));
	}

	@Test
	public void testSharedLoadFilesWhichUnassociateWithApplicationVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		appVerDao.saveOrUpdate(appVer);
		appVerDao.getAll();

		spDao.saveOrUpdate(sp);
		spDao.getAll();

		{
			// 第2组干扰数据：加载文件为私有的
			System.out.println("第2组干扰数据");
			LoadFile lf1 = LoadFileUtils.createDefult();
			LoadFile lf2 = LoadFileUtils.createDefult();
			LoadFile lf3 = LoadFileUtils.createDefult();
			Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
			lfSet.add(lf1);
			lfSet.add(lf2);
			lfSet.add(lf3);
			for (LoadFile lf : lfSet) {
				lf.setShareFlag(LoadFile.FLAG_EXCLUSIVE);
				LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer);
				lfDao.saveOrUpdate(lf);
				System.out.println("lf.id: " + lf.getId());
			}
			lfDao.getAll();
		}

		{
			// 第4组干扰数据：加载文件有多个版本且其中一个已被指定的应用版本引用
			System.out.println(" 第4组干扰数据");
			LoadFile lf1 = LoadFileUtils.createDefult();
			LoadFile lf2 = LoadFileUtils.createDefult();
			LoadFile lf3 = LoadFileUtils.createDefult();
			Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
			lfSet.add(lf1);
			lfSet.add(lf2);
			lfSet.add(lf3);
			for (LoadFile lf : lfSet) {
				lf.setShareFlag(LoadFile.FLAG_SHARED);
				lf.setSp(sp);
				LoadFileVersion lfVer1 = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer1);
				lfVer1.addApplictionVersion(appVer);
				LoadFileVersion lfVer2 = LoadFileVersionUtils.createDefult();
				lf.addLoadFileVersion(lfVer2);
				lfDao.saveOrUpdate(lf);
				System.out.println("lf.id: " + lf.getId());
				for (LoadFileVersion lfVer : lf.getVersions()) {
					System.out.println("lfVer.id: " + lfVer.getId());
					for (ApplicationLoadFile alf : lfVer.getApplicationLoadFiles()) {
						System.out.println("appVer.id: " + alf.getApplicationVersion().getId());
					}
				}
			}
			lfDao.getAll();
		}

		// 正常数据：共享、未被指定应用版本引用
		System.out.println("正常数据");
		LoadFile lf1 = LoadFileUtils.createDefult();
		LoadFile lf2 = LoadFileUtils.createDefult();
		LoadFile lf3 = LoadFileUtils.createDefult();
		Set<LoadFile> lfSet = new HashSet<LoadFile>(3);
		lfSet.add(lf1);
		lfSet.add(lf2);
		lfSet.add(lf3);
		for (LoadFile lf : lfSet) {
			lf.setShareFlag(LoadFile.FLAG_SHARED);
			lf.setSp(sp);
			LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
			lf.addLoadFileVersion(lfVer);
			lfDao.saveOrUpdate(lf);
			System.out.println("lf.id: " + lf.getId());
		}
		lfDao.getAll();
		{
			System.out.println("被其他应用版本引用");
			LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
			lf1.addLoadFileVersion(lfVer);
			lfVer.addApplictionVersion(ApplicationVersionUtils.createDefult());
			lfDao.saveOrUpdate(lf1);
			System.out.println("lf1.id: " + lf1.getId());
		}
		lfDao.getAll();

		List<LoadFile> reslut = null;
		try {
			System.out.println("调用目标方法开始");
			reslut = target.getSharedLoadFilesWhichUnassociateWithApplicationVersion(appVer.getId());
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		System.out.println("appVer.id: " + appVer.getId());
		for (LoadFile lf : reslut) {
			System.out.println("lf.id: " + lf.getId());
			for (LoadFileVersion lfVer : lf.getVersions()) {
				System.out.println("lfVer.id: " + lfVer.getId());
				for (ApplicationLoadFile alf : lfVer.getApplicationLoadFiles()) {
					System.out.println("appVer.id: " + alf.getApplicationVersion().getId());
				}
			}
		}

		Assert.assertEquals("结果数目", lfSet.size(), reslut.size());
		Assert.assertTrue("结果中包含所有预期的实体", reslut.containsAll(lfSet));
	}

	@Test
	public void testGetThatIsSharedAndIsnotSelfAndIsnotDependent() {
		// 干扰数据——私有的
		LoadFileVersion lfVer1 = LoadFileVersionUtils.createDefult();
		lfVer1.getLoadFile().setShareFlag(LoadFile.FLAG_EXCLUSIVE);
		lfVerDao.saveOrUpdate(lfVer1);
		System.out.println("lfVer1.id: " + lfVer1.getId());

		// 干扰数据——某一版本已依赖
		LoadFileVersion lfVer2 = LoadFileVersionUtils.createDefult();
		lfVer2.getLoadFile().setShareFlag(LoadFile.FLAG_SHARED);
		lfVer2.getLoadFile().addLoadFileVersion(LoadFileVersionUtils.createDefult());
		lfVerDao.saveOrUpdate(lfVer2);
		System.out.println("lfVer2.id: " + lfVer2.getId());

		// 目标数据
		LoadFileVersion lfVerE = LoadFileVersionUtils.createDefult();
		lfVerE.getLoadFile().setShareFlag(LoadFile.FLAG_SHARED);
		lfVerE.addDenepency(lfVer2);
		lfVerDao.saveOrUpdate(lfVerE);
		lfVerE.getLoadFile().addLoadFileVersion(LoadFileVersionUtils.createDefult());
		System.out.println("lfVerE.id: " + lfVerE.getId());

		// 正常数据
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lfVer.getLoadFile().setShareFlag(LoadFile.FLAG_SHARED);
		lfVerDao.saveOrUpdate(lfVer);
		System.out.println("lfVerS.id: " + lfVer.getId());

		lfVerDao.getAll();
		List<LoadFile> result = null;
		try {
			System.out.println("调用目标方法开始");
			result = target.getThatIsSharedAndIsnotSelfAndIsnotDependent(lfVerE);
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		for (LoadFile lf : result) {
			System.out.println("result.id: " + lf.getId());
		}

		Assert.assertTrue("结果不包括自身", !result.contains(lfVerE.getLoadFile()));
		Assert.assertTrue("结果不包括干扰数据1", !result.contains(lfVer1.getLoadFile()));
		Assert.assertTrue("结果不包括干扰数据2", !result.contains(lfVer2.getLoadFile()));
		Assert.assertTrue("结果包括正常数据", result.contains(lfVer.getLoadFile()));
	}

	@Test
	public void testGetUnusedByApplicationVersionAndType() {
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

		{// 干扰数据-加载文件被指定应用版本使用
			LoadFile loadFile = LoadFileUtils.createDefult();
			lfDao.saveOrUpdate(loadFile);

			LoadFileVersion loadFileVersion = LoadFileVersionUtils.createDefult();
			loadFile.addLoadFileVersion(loadFileVersion);
			loadFileVersion.addApplictionVersion(applicationVersion);

			lfVerDao.saveOrUpdate(loadFileVersion);

			System.out.println("干扰数据-加载文件被指定应用版本使用：" + loadFile.getId());
		}

		{// 干扰数据-加载文件有多个版本，其中一个被指定应用版本使用

			LoadFile loadFile = LoadFileUtils.createDefult();
			lfDao.saveOrUpdate(loadFile);

			LoadFileVersion loadFileVersion = LoadFileVersionUtils.createDefult();
			loadFile.addLoadFileVersion(loadFileVersion);
			loadFileVersion.addApplictionVersion(applicationVersion);
			lfVerDao.saveOrUpdate(loadFileVersion);

			ApplicationVersion applicationVersionOther = ApplicationVersionUtils.createDefult();
			applicationVersionOther.assignApplication(application);
			appVerDao.saveOrUpdate(applicationVersionOther);

			LoadFileVersion loadFileVersionOther = LoadFileVersionUtils.createDefult();
			loadFile.addLoadFileVersion(loadFileVersionOther);
			loadFileVersion.addApplictionVersion(applicationVersionOther);
			lfVerDao.saveOrUpdate(loadFileVersionOther);

			System.out.println("干扰数据-加载文件有多个版本，其中一个被指定应用版本使用：" + loadFile.getId());
		}

		// 正常数据
		ApplicationVersion applicationVersionOther = ApplicationVersionUtils.createDefult();
		applicationVersionOther.assignApplication(application);
		appVerDao.saveOrUpdate(applicationVersionOther);

		LoadFile loadFile = LoadFileUtils.createDefult();
		lfDao.saveOrUpdate(loadFile);

		LoadFileVersion loadFileVersion = LoadFileVersionUtils.createDefult();
		loadFile.addLoadFileVersion(loadFileVersion);
		loadFileVersion.addApplictionVersion(applicationVersionOther);

		lfVerDao.saveOrUpdate(loadFileVersion);

		List<LoadFile> result = null;
		try {
			result = target.getUnusedByApplicationVersionAndType(applicationVersion, loadFile.getType());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数正确", 1, result.size());
		System.out.println("预期：" + loadFile.getId());
		System.out.println("实际：" + result.get(0).getId());
		Assert.assertTrue("结果正确", result.contains(loadFile));

	}
}
