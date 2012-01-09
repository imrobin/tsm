package com.justinmobile.tsm.application.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.dao.LoadFileDao;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileUtils;
import com.justinmobile.tsm.application.utils.LoadFileVerUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration
public class LoadFileVersionManagerTest extends BaseAbstractTest {

	@Autowired
	LoadFileVersionManager target;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Autowired
	LoadFileDao lfDao;

	@Autowired
	SpBaseInfoDao spDao;

	@Autowired
	LoadFileVersionDao lfVerDao;

	@Test
	public void testCreateNewLoadFileVersionForApplicaitonVersion() {
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

		LoadFile lf = LoadFileUtils.createDefult();
		lf.setSp(sp);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);
		params.put("loadFileId", lf.getId().toString());

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		try {
			System.out.println("调用目标方法开始");
			target.createNewLoadFileVersionForApplicaitonVersion(lfVer, params, sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法结束");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertNotNull("cap文件存在", lfVer.getCapFileHex());
		Assert.assertEquals("cap文件长度正确", (Integer) (lfVer.getCapFileHex().length() / 2), lfVer.getFileSize());
		Assert.assertEquals("引用关系数", 1, lfVer.getApplicationLoadFiles().size());
		for (ApplicationLoadFile appLf : lfVer.getApplicationLoadFiles()) {
			Assert.assertEquals("被引入应用版本", appVer, appLf.getApplicationVersion());
		}
		Assert.assertEquals("所属加载文件", lf, lfVer.getLoadFile());
		Assert.assertEquals("所属加载文件版本数", 1, lfVer.getLoadFile().getVersions().size());
		for (LoadFileVersion lfVer1 : lfVer.getLoadFile().getVersions()) {
			Assert.assertEquals("加载文件关联", lfVer, lfVer1);
		}
	}

	@Test
	public void testCreateNewLoadFileVersionCapDir() {
		// 准备测试用的cap
		String filePath = "src\\test\\resources\\";
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

		LoadFile lf = LoadFileUtils.createDefult();
		lf.setSp(sp);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);
		params.put("loadFileId", lf.getId().toString());

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		try {
			System.out.println("调用目标方法开始");
			target.createNewLoadFileVersion(lfVer, params, sp.getSysUser().getUserName());
			System.out.println("调用目标方法结束");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码", PlatformErrorCode.LOAD_FILE_CAP_UNEXIST, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testCreateNewLoadFileVersionCapUnknown() {
		// 准备测试用的cap
		String filePath = "src\\test\\resources\\ooxx.txt";
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

		LoadFile lf = LoadFileUtils.createDefult();
		lf.setSp(sp);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);
		params.put("loadFileId", lf.getId().toString());

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		try {
			System.out.println("调用目标方法开始");
			target.createNewLoadFileVersion(lfVer, params, sp.getSysUser().getUserName());
			System.out.println("调用目标方法结束");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码", PlatformErrorCode.LOAD_FILE_CAP_UNEXIST, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testCreateNewLoadFileVersion() {
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

		LoadFile lf = LoadFileUtils.createDefult();
		lf.setSp(sp);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);
		params.put("loadFileId", lf.getId().toString());

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();

		try {
			System.out.println("调用目标方法开始");
			target.createNewLoadFileVersion(lfVer, params, sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法结束");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertNotNull("cap文件存在", lfVer.getCapFileHex());
		Assert.assertEquals("cap文件长度正确", (Integer) (lfVer.getCapFileHex().length() / 2), lfVer.getFileSize());
		Assert.assertEquals("引用关系数", 1, lfVer.getApplicationLoadFiles().size());
		for (ApplicationLoadFile appLf : lfVer.getApplicationLoadFiles()) {
			Assert.assertEquals("被引入应用版本", appVer, appLf.getApplicationVersion());
		}
		Assert.assertEquals("所属加载文件", lf, lfVer.getLoadFile());
		Assert.assertEquals("所属加载文件版本数", 1, lfVer.getLoadFile().getVersions().size());
		for (LoadFileVersion lfVer1 : lfVer.getLoadFile().getVersions()) {
			Assert.assertEquals("加载文件关联", lfVer, lfVer1);
		}
	}

	@Test
	public void testCreateNewLoadFileVersionSpDiscard() {
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

		LoadFile lf = LoadFileUtils.createDefult();
		lf.setSp(sp);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);
		params.put("loadFileId", lf.getId().toString());

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();

		LoadFileVersion lfVer = new LoadFileVersion();
		try {
			System.out.println("调用目标方法开始");
			target.createNewLoadFileVersion(lfVer, params, spOther.getSysUser().getUserName());
			System.out.println("调用目标方法结束");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码", PlatformErrorCode.LOAD_FILE_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testCreateNewLoadFileVersionVersionNoBlank() {
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

		LoadFile lf = LoadFileUtils.createDefult();
		lf.setSp(sp);
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);
		params.put("loadFileId", lf.getId().toString());

		LoadFileVersion lfVer = new LoadFileVersion();
		try {
			System.out.println("调用目标方法开始");
			target.createNewLoadFileVersion(lfVer, params, sp.getSysUser().getUserName());
			System.out.println("调用目标方法结束");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码", PlatformErrorCode.LOAD_FILE_VERSION_NO_BLANK, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testCreateNewLoadFileVersionVersionNoReduplicate() {
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

		LoadFile lf = LoadFileUtils.createDefult();
		lf.setSp(sp);
		lf.addLoadFileVersion(LoadFileVersionUtils.createDefult());
		lfDao.saveOrUpdate(lf);
		lfDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		params.put("applicationVersionId", appVer.getId().toString());
		params.put("tempDir", tempDir);
		params.put("tempFileAbsPath", filePath);
		params.put("loadFileId", lf.getId().toString());

		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		try {
			System.out.println("调用目标方法开始");
			target.createNewLoadFileVersion(lfVer, params, sp.getSysUser().getUserName());
			System.out.println("调用目标方法结束");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码", PlatformErrorCode.LOAD_FILE_VERSION_NO_REDUPLICATE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testCalcDependenceAsDownloadOrder() {
		LoadFileVersion lfv1 = LoadFileVersionUtils.createDefult();
		LoadFileVersion lfv2 = LoadFileVersionUtils.createDefult();
		LoadFileVersion lfv3 = LoadFileVersionUtils.createDefult();
		LoadFileVersion lfv4 = LoadFileVersionUtils.createDefult();
		LoadFileVersion lfv5 = LoadFileVersionUtils.createDefult();

		// 临时使用hash字段记录对象描述信息
		lfv1.setHash("lfv1");
		lfv2.setHash("lfv2");
		lfv3.setHash("lfv3");
		lfv4.setHash("lfv4");
		lfv5.setHash("lfv5");

		lfv2.addDenepency(lfv1);
		lfv3.addDenepency(lfv1);
		lfv4.addDenepency(lfv2);
		lfv4.addDenepency(lfv3);
		lfv5.addDenepency(lfv1);
		lfv5.addDenepency(lfv3);

		Set<LoadFileVersion> lfvs = new HashSet<LoadFileVersion>(5);
		lfvs.add(lfv4);
		lfvs.add(lfv5);

		List<LoadFileVersion> reslut = new ArrayList<LoadFileVersion>();
		try {
			System.out.println("调用测试方法开始");
			reslut = target.calcDependenceAsDownloadOrder(lfvs);
			System.out.println("调用测试方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Map<LoadFileVersion, Integer> reslutMap = new HashMap<LoadFileVersion, Integer>(5);
		for (int i = 0; i < reslut.size(); i++) {
			LoadFileVersion lfv = reslut.get(i);
			reslutMap.put(lfv, i);
			System.out.println(lfv.getHash());
		}
		Assert.assertTrue("lfv1优先于lfv2", (reslutMap.get(lfv1) < reslutMap.get(lfv2)));
		Assert.assertTrue("lfv1优先于lfv3", (reslutMap.get(lfv1) < reslutMap.get(lfv3)));
		Assert.assertTrue("lfv1优先于lfv3", (reslutMap.get(lfv1) < reslutMap.get(lfv3)));
		Assert.assertTrue("lfv2优先于lfv4", (reslutMap.get(lfv2) < reslutMap.get(lfv4)));
		Assert.assertTrue("lfv3优先于lfv4", (reslutMap.get(lfv3) < reslutMap.get(lfv4)));
		Assert.assertTrue("lfv1优先于lfv5", (reslutMap.get(lfv1) < reslutMap.get(lfv5)));
		Assert.assertTrue("lfv3优先于lfv5", (reslutMap.get(lfv3) < reslutMap.get(lfv5)));
	}

	@Test
	public void testAddDependency() {
		LoadFileVersion parent = LoadFileVersionUtils.createDefult();
		LoadFileVersion child = LoadFileVersionUtils.createDefult();

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		parent.getLoadFile().setSp(sp);
		child.getLoadFile().setSp(sp);

		lfVerDao.saveOrUpdate(parent);
		lfVerDao.saveOrUpdate(child);
		lfVerDao.getAll();

		try {
			System.out.println("调用目标方法开始");
			target.addDependence(parent, child, sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法结束");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("被依赖加载文件版本存在", 1, parent.getChildren().size());
		for (LoadFileVersion lfVer : parent.getChildren()) {
			Assert.assertEquals("被依赖加载文件版本正确", child, lfVer);
		}
		Assert.assertEquals("依赖加载文件版本存在", 1, child.getParents().size());
		for (LoadFileVersion lfVer : child.getParents()) {
			Assert.assertEquals("依赖加载文件版本正确", parent, lfVer);
		}
	}

	@Test
	public void testAddDependencySpDiscard() {
		LoadFileVersion parent = LoadFileVersionUtils.createDefult();
		LoadFileVersion child = LoadFileVersionUtils.createDefult();

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		parent.getLoadFile().setSp(sp);
		child.getLoadFile().setSp(sp);

		lfVerDao.saveOrUpdate(parent);
		lfVerDao.saveOrUpdate(child);
		lfVerDao.getAll();

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();
		try {
			System.out.println("调用目标方法开始");
			target.addDependence(parent, child, spOther.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法结束");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码正确", PlatformErrorCode.LOAD_FILE_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testRemoveDependency() {
		LoadFileVersion parent = LoadFileVersionUtils.createDefult();
		LoadFileVersion child = LoadFileVersionUtils.createDefult();

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		parent.getLoadFile().setSp(sp);
		child.getLoadFile().setSp(sp);

		lfVerDao.saveOrUpdate(parent);
		lfVerDao.saveOrUpdate(child);

		parent.addDenepency(child);
		lfVerDao.saveOrUpdate(parent);
		lfVerDao.getAll();

		try {
			System.out.println("调用目标方法开始");
			target.removeDependence(parent, child, sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法结束");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("被依赖加载文件版本不存在", 0, parent.getChildren().size());
		Assert.assertEquals("依赖加载文件版本不存在", 0, child.getParents().size());
		System.out.println("!testRemoveDependency");
	}

	@Test
	public void testRemoveDependencySpDiscard() {
		LoadFileVersion parent = LoadFileVersionUtils.createDefult();
		LoadFileVersion child = LoadFileVersionUtils.createDefult();

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		parent.getLoadFile().setSp(sp);
		child.getLoadFile().setSp(sp);

		lfVerDao.saveOrUpdate(parent);
		lfVerDao.saveOrUpdate(child);

		parent.addDenepency(child);
		lfVerDao.saveOrUpdate(parent);
		lfVerDao.getAll();

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();

		try {
			System.out.println("调用目标方法开始");
			target.removeDependence(parent, child, sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法结束");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals("错误码正确", PlatformErrorCode.LOAD_FILE_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateCircularDependence() {
		LoadFileVersion lfVer1 = LoadFileVerUtils.createDefualt();
		lfVer1.setVersionNo("1");
		LoadFileVersion lfVer2 = LoadFileVerUtils.createDefualt();
		lfVer2.setVersionNo("2");
		LoadFileVersion lfVer3 = LoadFileVerUtils.createDefualt();
		lfVer3.setVersionNo("3");
		LoadFileVersion lfVer4 = LoadFileVerUtils.createDefualt();
		lfVer4.setVersionNo("4");
		
		LoadFile loadFile = LoadFileUtils.createDefult();

		lfVer1.addDenepency(lfVer2);
		lfVer2.addDenepency(lfVer4);
		lfVer3.addDenepency(lfVer4);
		
		lfVer2.setLoadFile(loadFile);
		lfVer3.setLoadFile(loadFile);

		try {
			target.validateCircularDependence(lfVer1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		
		try {
			target.validateCircularDependence(lfVer2);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		
		try {
			target.validateCircularDependence(lfVer3);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		
		try {
			target.validateCircularDependence(lfVer4);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateCircularDependenceSameFileDiffVersionDirectly() {
		LoadFileVersion lfVer1 = LoadFileVerUtils.createDefualt();
		LoadFileVersion lfVer2 = LoadFileVerUtils.createDefualt();

		LoadFile loadFile = LoadFileUtils.createDefult();

		lfVer1.setLoadFile(loadFile);
		lfVer2.setLoadFile(loadFile);

		lfVer1.addDenepency(lfVer2);

		try {
			target.validateCircularDependence(lfVer1);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		
		try {
			target.validateCircularDependence(lfVer2);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateCircularDependenceSameFileSameVersionIndirectly() {
		LoadFileVersion lfVer1 = LoadFileVerUtils.createDefualt();

		LoadFileVersion lfVer2 = LoadFileVerUtils.createDefualt();
		lfVer2.addDenepency(lfVer1);
		lfVer1.addDenepency(lfVer2);

		try {
			target.validateCircularDependence(lfVer1);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		
		try {
			target.validateCircularDependence(lfVer2);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}

	@Test
	public void testValidateCircularDependenceSameFileDiffVersionIndirectly() {
		LoadFileVersion lfVer1 = LoadFileVerUtils.createDefualt();
		LoadFileVersion lfVer2 = LoadFileVerUtils.createDefualt();

		LoadFile loadFile = LoadFileUtils.createDefult();

		lfVer1.setLoadFile(loadFile);
		lfVer1.setVersionNo("1");
		lfVer2.setLoadFile(loadFile);
		lfVer2.setVersionNo("2");

		LoadFileVersion lfVer3 = LoadFileVerUtils.createDefualt();
		lfVer3.addDenepency(lfVer2);
		lfVer1.addDenepency(lfVer3);

		try {
			target.validateCircularDependence(lfVer1);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		
		try {
			target.validateCircularDependence(lfVer2);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		
		try {
			target.validateCircularDependence(lfVer3);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
}
