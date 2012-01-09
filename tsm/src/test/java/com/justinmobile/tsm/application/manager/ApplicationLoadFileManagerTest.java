package com.justinmobile.tsm.application.manager;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.ApplicationLoadFileDao;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.utils.ApplicationLoadFileUtils;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration
public class ApplicationLoadFileManagerTest extends BaseAbstractTest {

	@Autowired
	private ApplicationLoadFileManager target;

	@Autowired
	private ApplicationLoadFileDao appLfDao;

	@Autowired
	private SpBaseInfoDao spDao;

	@Autowired
	private ApplicationVersionDao appVerDao;

	@Autowired
	private LoadFileVersionDao lfVerDao;

	@Test
	public void testSetDownloadOrder() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		Integer order = 19841028;

		try {
			System.out.println("调用目标方法开始");
			target.setDownloadOrder(appLf.getLoadFileVersion().getId(), appLf.getApplicationVersion().getId(), order, sp.getSysUser()
					.getUserName());
			target.getAll();
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals("下载顺序", order, appLf.getDownloadOrder());
	}

	@Test
	public void testSetDownloadOrderSpDiscard() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();

		Integer order = 19841028;

		try {
			target.setDownloadOrder(appLf.getLoadFileVersion().getId(), appLf.getApplicationVersion().getId(), order, spOther.getSysUser()
					.getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_SET_DOWNLOAD_ORDER_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未知异常");
		}
	}

	@Test
	public void testSetDownloadOrderSpDiscardDiffApplicationVersion() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		ApplicationVersion appVerOther = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVerOther);
		appVerDao.getAll();

		Integer order = 19841028;

		try {
			target.setDownloadOrder(appLf.getLoadFileVersion().getId(), appVerOther.getId(), order, sp.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_LOAD_FILE_UNEXIST, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未知异常");
		}
	}

	@Test
	public void testSetDownloadOrderSpDiscardDiffLoadFileVersion() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
		lfVerDao.saveOrUpdate(lfVerOther);
		lfVerDao.getAll();

		Integer order = 19841028;

		try {
			target.setDownloadOrder(lfVerOther.getId(), appLf.getApplicationVersion().getId(), order, sp.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_LOAD_FILE_UNEXIST, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未知异常");
		}
	}

	@Test
	public void testSetDeleteOrder() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		Integer order = 19841028;

		try {
			target.setDeleteOrder(appLf.getLoadFileVersion().getId(), appLf.getApplicationVersion().getId(), order, sp.getSysUser()
					.getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

		Assert.assertEquals("删除顺序", order, appLf.getDeleteOrder());
	}

	@Test
	public void testSetDeleteOrderSpDiscard() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();

		Integer order = 19841028;

		try {
			target.setDeleteOrder(appLf.getLoadFileVersion().getId(), appLf.getApplicationVersion().getId(), order, spOther.getSysUser()
					.getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_SET_DOWNLOAD_ORDER_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未知异常");
		}
	}

	@Test
	public void testSetDeleteOrderSpDiscardDiffApplicationVersion() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		ApplicationVersion appVerOther = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVerOther);
		appVerDao.getAll();

		Integer order = 19841028;

		try {
			target.setDeleteOrder(appLf.getLoadFileVersion().getId(), appVerOther.getId(), order, sp.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_LOAD_FILE_UNEXIST, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未知异常");
		}
	}

	@Test
	public void testSetDeleteOrderSpDiscardDiffLoadFileVersion() {
		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		appLf.setLoadFileVersion(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appLf.setApplicationVersion(appVer);
		Application app = ApplicationUtils.createDefult("11");
		appVer.setApplication(app);
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
		lfVerDao.saveOrUpdate(lfVerOther);
		lfVerDao.getAll();

		Integer order = 19841028;

		try {
			target.setDeleteOrder(lfVerOther.getId(), appLf.getApplicationVersion().getId(), order, sp.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("错误码", PlatformErrorCode.APPLICAION_LOAD_FILE_UNEXIST, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未知异常");
		}
	}

	@Test
	public void testRemoveImportBetweenLoadFileVersionAndApplicationVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();

		Application app = ApplicationUtils.createDefult();
		appVer.setApplication(app);

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();
		appLf.assignApplicationVersionAndLoadFileVersion(appVer, lfVer);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();
		try {
			System.out.println("开始");
			target.removeImportBetweenLoadFileVersionAndApplicationVersion(lfVer.getId(), appVer.getId(), sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("数目", 0, lfVer.getApplicationLoadFiles().size());
	}

	@Test
	public void testRemoveImportBetweenLoadFileVersionAndApplicationVersionSpDiscard() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();

		Application app = ApplicationUtils.createDefult();
		appVer.setApplication(app);

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		ApplicationLoadFile appLf = ApplicationLoadFileUtils.createDefult();
		appLf.assignApplicationVersionAndLoadFileVersion(appVer, lfVer);

		appLfDao.saveOrUpdate(appLf);
		appLfDao.getAll();

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();

		try {
			target.removeImportBetweenLoadFileVersionAndApplicationVersion(lfVer.getId(), appVer.getId(), spOther.getSysUser()
					.getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("验证错误码", PlatformErrorCode.APPLICAION_REMOVE_IMPORT_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testBuildImportBetweenLoadFileVersionAndApplicationVersion() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();

		Application app = ApplicationUtils.createDefult();
		appVer.setApplication(app);

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		lfVerDao.saveOrUpdate(lfVer);
		lfVerDao.getAll();
		appVerDao.saveOrUpdate(appVer);
		appVerDao.getAll();

		Assert.assertEquals("应用版本引入关系数目", 0, appVer.getApplicationLoadFiles().size());
		Assert.assertEquals("加载文件版本引入关系数目", 0, lfVer.getApplicationLoadFiles().size());

		try {
			target.buildImportBetweenLoadFileVersionAndApplicationVersion(lfVer.getId(), appVer.getId(), sp.getSysUser().getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("应用版本引入关系数目", 1, appVer.getApplicationLoadFiles().size());
		Assert.assertEquals("加载文件版本引入关系数目", 1, lfVer.getApplicationLoadFiles().size());

		for (ApplicationLoadFile appLfAppVer : appVer.getApplicationLoadFiles()) {
			for (ApplicationLoadFile appLfLfVer : lfVer.getApplicationLoadFiles()) {
				Assert.assertEquals("引入关系是同一对象", appLfAppVer, appLfLfVer);
			}
		}
	}

	@Test
	public void testBuildImportBetweenLoadFileVersionAndApplicationVersionSpDiscard() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();

		Application app = ApplicationUtils.createDefult();
		appVer.setApplication(app);

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		app.setSp(sp);

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		lfVerDao.saveOrUpdate(lfVer);
		lfVerDao.getAll();
		appVerDao.saveOrUpdate(appVer);
		appVerDao.getAll();

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();

		try {
			target.buildImportBetweenLoadFileVersionAndApplicationVersion(lfVer.getId(), appVer.getId(), spOther.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals("验证错误码", PlatformErrorCode.APPLICAION_BUILD_IMPORT_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
}
