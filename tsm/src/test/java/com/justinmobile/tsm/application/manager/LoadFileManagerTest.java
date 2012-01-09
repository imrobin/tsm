package com.justinmobile.tsm.application.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.dao.LoadFileDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration
public class LoadFileManagerTest extends BaseAbstractTest {

	@Autowired
	LoadFileManager target;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Autowired
	LoadFileDao lfDao;

	@Autowired
	SpBaseInfoDao spDao;

	@Test
	public void testCreateNewLoadFileForApplicationVersion() {
		// 准备测试用的cap
		String filePath = "src\\test\\resources\\apdu.cap";
		File file = new File(filePath);
		System.out.println("cap: " + file.getAbsolutePath());
		System.out.println(file.exists());

		String tempDir = "temp" + File.separator + (int) (Math.random() * Integer.MAX_VALUE);
		File tempDirFile = new File(tempDir);
		tempDirFile.mkdirs();
		System.out.println("临时目录：" + tempDirFile.getAbsolutePath());

		// 开始准备对象
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		Application app = ApplicationUtils.createDefult();
		appVer.setApplication(app);
		app.setSdModel(SecurityDomain.MODEL_COMMON);
		app.setSp(sp);
		appVerDao.saveOrUpdate(appVer);

		spDao.saveOrUpdate(sp);

		LoadFile loadFile = LoadFileUtils.createDefult();
		loadFile.setSdModel(SecurityDomain.MODEL_COMMON);

		LoadFileVersion version = LoadFileVersionUtils.createDefult();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);

		try {
			target.createNewLoadFileForApplicationVersion(loadFile, version, params, sp.getSysUser().getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("未期待的异常");
		}

		Assert.assertEquals("加载文件不共享", LoadFile.FLAG_EXCLUSIVE, (int) loadFile.getShareFlag());
		Assert.assertEquals("版本数目", 1, loadFile.getVersions().size());
		LoadFileVersion lfVer = loadFile.getVersions().get(0);
		Assert.assertNotNull("cap文件存在", lfVer.getCapFileHex());
		Assert.assertEquals("cap文件长度正确", (Integer) (lfVer.getCapFileHex().length() / 2), lfVer.getFileSize());

		Assert.assertEquals("关联的应用", 1, lfVer.getApplicationLoadFiles().size());
		ApplicationLoadFile appLoadFile = lfVer.getApplicationLoadFiles().toArray(new ApplicationLoadFile[] {})[0];
		Assert.assertEquals("有且仅有一个与新上传的LoadFile和指定的ApplicationVersion关联的ApplicationLoadFile", appVer, appLoadFile.getApplicationVersion());

		try {
			FileUtils.deleteDirectory(tempDirFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateNewSharedLoadFile() {
		// 准备测试用的cap
		String filePath = "src\\test\\resources\\apdu.cap";
		File file = new File(filePath);
		System.out.println("cap: " + file.getAbsolutePath());
		System.out.println(file.exists());

		String tempDir = "temp" + File.separator + (int) (Math.random() * Integer.MAX_VALUE);
		File tempDirFile = new File(tempDir);
		tempDirFile.mkdirs();
		System.out.println("临时目录：" + tempDirFile.getAbsolutePath());

		// 开始准备对象
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		Application app = ApplicationUtils.createDefult();
		appVer.setApplication(app);
		app.setSp(sp);
		appVerDao.saveOrUpdate(appVer);
		appVerDao.getAll();

		spDao.saveOrUpdate(sp);
		spDao.getAll();

		LoadFile loadFile = LoadFileUtils.createDefult();
		loadFile.setSdModel(SecurityDomain.MODEL_COMMON);

		LoadFileVersion version = LoadFileVersionUtils.createDefult();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);

		try {
			target.createNewSharedLoadFile(loadFile, version, params, sp.getSysUser().getUserName());
			target.getAll();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("未期待的异常");
		}

		Assert.assertEquals("加载文件共享", LoadFile.FLAG_SHARED, (int) loadFile.getShareFlag());
		Assert.assertEquals("版本数目", 1, loadFile.getVersions().size());
		LoadFileVersion lfVer = loadFile.getVersions().get(0);
		Assert.assertNotNull("cap文件存在", lfVer.getCapFileHex());
		Assert.assertEquals("cap文件长度正确", (Integer) (lfVer.getCapFileHex().length() / 2), lfVer.getFileSize());

		Assert.assertEquals("关联的应用", 0, lfVer.getApplicationLoadFiles().size());

		try {
			FileUtils.deleteDirectory(tempDirFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
