package com.justinmobile.tsm.application.manager;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.AppletDao;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.ApplicationLoadFileDao;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.utils.AppletUtils;
import com.justinmobile.tsm.application.utils.ApplicationLoadFileUtils;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;

@TransactionConfiguration
public class ApplicationVersionManagerTest extends BaseAbstractTest {

	@Autowired
	ApplicationVersionManager target;

	@Autowired
	ApplicationDao appDao;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Autowired
	LoadFileVersionDao lfVerDao;

	@Autowired
	AppletDao appletDao;

	@Autowired
	ApplicationLoadFileDao alfDao;

	@Test
	public void testCompleteCreateApplicationVersion() {
		Application app = ApplicationUtils.createDefult();
		appDao.saveOrUpdate(app);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appVer.assignApplication(app);

		LoadFileVersion lfVer1 = LoadFileVersionUtils.createDefult();
		LoadFileVersion lfVer2 = LoadFileVersionUtils.createDefult();
		LoadFileVersion lfVer3 = LoadFileVersionUtils.createDefult();
		{
			lfVer1.setLoadParams("EF0CC6025DC9C702000AC8020800");
			lfVer1.setHash("lfv_e_1");
			LoadFile lf = LoadFileUtils.createDefult();
			lf.addLoadFileVersion(lfVer1);
			ApplicationLoadFile alf = ApplicationLoadFileUtils.createDefult();
			alf.assignApplicationVersionAndLoadFileVersion(appVer, lfVer1);
			alf.setDownloadOrder(1);
			alf.setDeleteOrder(2);
			lfVerDao.saveOrUpdate(lfVer1);
		}
		{
			lfVer2.setLoadParams("EF0CC6025DC9C702000AC8020800");
			lfVer2.setHash("lfv_e_2");
			LoadFile lf = LoadFileUtils.createDefult();
			lf.addLoadFileVersion(lfVer2);
			ApplicationLoadFile alf = ApplicationLoadFileUtils.createDefult();
			alf.assignApplicationVersionAndLoadFileVersion(appVer, lfVer2);
			alf.setDownloadOrder(3);
			alf.setDeleteOrder(2);
			lfVerDao.saveOrUpdate(lfVer2);
		}
		{
			lfVer3.setLoadParams("EF0CC6025DC9C702000AC8020800");
			lfVer3.setHash("lfv_e_3");
			LoadFile lf = LoadFileUtils.createDefult();
			lf.addLoadFileVersion(lfVer3);
			ApplicationLoadFile alf = ApplicationLoadFileUtils.createDefult();
			alf.assignApplicationVersionAndLoadFileVersion(appVer, lfVer3);
			alf.setDownloadOrder(3);
			alf.setDeleteOrder(3);
			lfVerDao.saveOrUpdate(lfVer3);
		}

		Applet applet1 = AppletUtils.createDefult();
		Applet applet2 = AppletUtils.createDefult();
		Applet applet3 = AppletUtils.createDefult();

		applet1.setAid(app.getAid());
		applet1.setInstallParams("C900EF08C8020100C7020010");
		applet1.setOrderNo(3);
		applet1.assignApplicationVersion(appVer);
		appletDao.saveOrUpdate(applet1);

		applet2.setInstallParams("C900EF08C8020100C7020010");
		applet2.setOrderNo(4);
		applet2.assignApplicationVersion(appVer);
		appletDao.saveOrUpdate(applet2);

		applet3.setInstallParams("C900EF08C8020100C7020010");
		applet3.setOrderNo(1);
		applet3.assignApplicationVersion(appVer);
		appletDao.saveOrUpdate(applet3);

		appVerDao.saveOrUpdate(appVer);
		appVerDao.getAll();
		appletDao.getAll();
		lfVerDao.getAll();
		alfDao.getAll();
		System.out.println("appVer.id: " + appVer.getId());

		System.out.println("lfv.id: " + lfVer1.getId() + ", " + lfVer1.getHash());
		System.out.println("lfv.id: " + lfVer2.getId() + ", " + lfVer2.getHash());
		System.out.println("lfv.id: " + lfVer3.getId() + ", " + lfVer3.getHash());

		System.out.println("加载文件：");
		for (ApplicationLoadFile alf : appVer.getApplicationLoadFiles()) {
			System.out.println("lfv.id: " + alf.getLoadFileVersion().getId() + ", " + alf.getLoadFileVersion().getHash());
		}

		System.out.println("实例：");
		for (Applet applet : appVer.getApplets()) {
			System.out.println("applet.id: " + applet.getId());
		}

		try {
			System.out.println("调用目标方法开始");
			target.completeCreateApplicationVersion(appVer.getId());
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("状态", ApplicationVersion.STATUS_UPLOADED, appVer.getStatus());

		{
			List<ApplicationLoadFile> actualDownloadOrder = alfDao.getByApplicationVersionAsDownloadOrder(appVer);
			System.out.println("下载顺序：");
			for (ApplicationLoadFile alf : actualDownloadOrder) {
				System.out.println("lfv.id: " + alf.getLoadFileVersion().getId() + ", " + alf.getLoadFileVersion().getHash() + ", alf.id: "
						+ alf.getId());
			}
			Assert.assertEquals("下载顺序数目", 3, actualDownloadOrder.size());

			ApplicationLoadFile applicationLoadFile6 = actualDownloadOrder.get(0);
			ApplicationLoadFile applicationLoadFile7 = actualDownloadOrder.get(1);
			ApplicationLoadFile applicationLoadFile8 = actualDownloadOrder.get(2);

			// 自有加载文件的顺序
			Assert.assertEquals("下载顺序1", lfVer1, applicationLoadFile6.getLoadFileVersion());
			Assert.assertEquals("下载顺序值1", 1, (int) applicationLoadFile6.getDownloadOrder());

			Assert.assertEquals("下载顺序2", lfVer2, applicationLoadFile7.getLoadFileVersion());
			Assert.assertEquals("下载顺序值2", 2, (int) applicationLoadFile7.getDownloadOrder());

			Assert.assertEquals("下载顺序3", lfVer3, applicationLoadFile8.getLoadFileVersion());
			Assert.assertEquals("下载顺序值3", 3, (int) applicationLoadFile8.getDownloadOrder());
		}
		{
			List<ApplicationLoadFile> actualDeleteOrder = alfDao.getByApplicationVersionAsDeleteOrder(appVer);
			Assert.assertEquals("删除顺序数目", 3, actualDeleteOrder.size());
			ApplicationLoadFile alf1 = actualDeleteOrder.get(0);
			Assert.assertEquals("删除顺序1", lfVer2, alf1.getLoadFileVersion());
			Assert.assertEquals("删除顺序值1", 1, (int) alf1.getDeleteOrder());
			ApplicationLoadFile alf2 = actualDeleteOrder.get(1);
			Assert.assertEquals("删除顺序2", lfVer1, alf2.getLoadFileVersion());
			Assert.assertEquals("删除顺序值2", 2, (int) alf2.getDeleteOrder());
			ApplicationLoadFile alf3 = actualDeleteOrder.get(2);
			Assert.assertEquals("删除顺序3", lfVer3, alf3.getLoadFileVersion());
			Assert.assertEquals("删除顺序值3", 3, (int) alf3.getDeleteOrder());
		}

		Assert.assertEquals("不可变空间", 78939, (long) appVer.getNonVolatileSpace());
		Assert.assertEquals("可变空间", 78, (int) appVer.getVolatileSpace());
	}
}
