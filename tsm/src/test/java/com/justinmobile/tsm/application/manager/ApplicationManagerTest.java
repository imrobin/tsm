package com.justinmobile.tsm.application.manager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.ApplicationTypeDao;
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationType;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.SecurityDomainUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration(defaultRollback = true)
public class ApplicationManagerTest extends BaseAbstractTest {

	@Autowired
	private ApplicationManager target;

	@Autowired
	private SecurityDomainDao sdDao;

	@Autowired
	private SpBaseInfoDao spDao;

	@Autowired
	private ApplicationTypeDao appTypeDao;

	@Test
	public void testCreateNewApplicationIsd() {
		Application application = ApplicationUtils.createDefult();

		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		ApplicationType type = new ApplicationType();
		appTypeDao.saveOrUpdate(type);
		appTypeDao.getAll();
		spDao.saveOrUpdate(sp);
		spDao.getAll();

		SecurityDomain sd = sdDao.getIsd();
		if (null == sd) {
			sd = SecurityDomainUtils.createDefult();
		}
		application.setSdModel(SecurityDomain.MODEL_ISD);
		sdDao.saveOrUpdate(sd);
		sdDao.getAll();

		Map<String, String> params = new HashMap<String, String>();
		Long sdId = sd.getId();
		params.put("sdId", sdId.toString());
		String versionNo = "1.0.0";
		params.put("versionNo", versionNo);
		params.put("applicationTypeId", type.getId().toString());
		File file = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "testIcon.gif");
		System.out.println(file.getAbsolutePath());
		params.put("pcIconTempFileAbsPath", file.getAbsolutePath());
		params.put("mobileIconTempFileAbsPath", file.getAbsolutePath());

		try {
			target.createNewApplication(sp.getSysUser().getUserName(), application, params);
			target.getAll();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertNotNull("pc图标不存在", application.getPcIcon());
		Assert.assertNotNull("mobile图标不存在", application.getMoblieIcon());
		// 应用所关联的安全域模式是主安全域
		Assert.assertEquals(SecurityDomain.MODEL_ISD, application.getSdModel().intValue());
		// 应用所关联的安全域是主安全域
		Assert.assertEquals(sdDao.getIsd(), application.getSd());
		// 应用有且只有1个版本
		Assert.assertEquals(1, application.getVersions().size());
		// 应用唯一版本的版本号是SP指定的版本号
		Assert.assertEquals(versionNo, application.getVersions().get(0).getVersionNo());
		// 应用评分统计已经存在
		Assert.assertNotNull(application.getStatistics());
		Assert.assertEquals("下载量初始化", 0, (int) application.getDownloadCount());
		Assert.assertEquals("状态正确", Application.STATUS_INIT, application.getStatus().intValue());
	}

	@Test
	public void testCreateNewApplicationCommom() {
		Application application = ApplicationUtils.createDefult();
		SecurityDomain sd = SecurityDomainUtils.createDefult();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		spDao.saveOrUpdate(sp);
		sdDao.saveOrUpdate(sd);

		spDao.getAll();
		sdDao.getAll();

		ApplicationType type = new ApplicationType();
		appTypeDao.saveOrUpdate(type);
		appTypeDao.getAll();

		application.setSdModel(SecurityDomain.MODEL_COMMON);

		Map<String, String> params = new HashMap<String, String>();
		Long sdId = sd.getId();
		params.put("sdId", sdId.toString());
		String versionNo = "1.0.0";
		params.put("versionNo", versionNo);
		params.put("applicationTypeId", type.getId().toString());
		File file = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "testIcon.gif");
		params.put("pcIconTempFileAbsPath", file.getAbsolutePath());
		params.put("mobileIconTempFileAbsPath", file.getAbsolutePath());

		try {
			System.out.println("调用目标方法开始");
			target.createNewApplication(sp.getSysUser().getUserName(), application, params);
			target.getAll();
			System.out.println("调用目标方法结束");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertNotNull("pc图标不存在", application.getPcIcon());
		Assert.assertNotNull("mobile图标不存在", application.getMoblieIcon());
		// 应用所关联的安全域模式是公共安全域
		Assert.assertEquals(SecurityDomain.MODEL_COMMON, application.getSdModel().intValue());
		// 应用所关联的安全域ID与指定的安全域ID相同
		Assert.assertEquals(sd.getId(), application.getSd().getId());
		// 应用有且只有1个版本
		Assert.assertEquals(1, application.getVersions().size());
		// 应用唯一版本的版本号是SP指定的版本号
		Assert.assertEquals(versionNo, application.getVersions().get(0).getVersionNo());
		// 应用评分统计已经存在
		Assert.assertNotNull(application.getStatistics());
		Assert.assertEquals("下载量初始化", 0, (int) application.getDownloadCount());
		Assert.assertEquals("状态正确", Application.STATUS_INIT, application.getStatus().intValue());
	}

	@Test
	public void testCreateNewApplicationDap() {
		Application application = ApplicationUtils.createDefult();
		SecurityDomain sd = SecurityDomainUtils.createDefult();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		spDao.saveOrUpdate(sp);
		sdDao.saveOrUpdate(sd);

		spDao.getAll();
		sdDao.getAll();

		ApplicationType type = new ApplicationType();
		appTypeDao.saveOrUpdate(type);
		appTypeDao.getAll();

		application.setSdModel(SecurityDomain.MODEL_DAP);

		Map<String, String> params = new HashMap<String, String>();
		Long sdId = sd.getId();
		params.put("sdId", sdId.toString());
		String versionNo = "1.0.0";
		params.put("versionNo", versionNo);
		params.put("applicationTypeId", type.getId().toString());
		File file = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "testIcon.gif");
		params.put("pcIconTempFileAbsPath", file.getAbsolutePath());
		params.put("mobileIconTempFileAbsPath", file.getAbsolutePath());

		try {
			target.createNewApplication(sp.getSysUser().getUserName(), application, params);
			target.getAll();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertNotNull("pc图标不存在", application.getPcIcon());
		Assert.assertNotNull("mobile图标不存在", application.getMoblieIcon());
		// 应用所关联的安全域模式是DAP安全域
		Assert.assertEquals(SecurityDomain.MODEL_DAP, application.getSdModel().intValue());
		// 应用所关联的安全域ID与指定的安全域ID相同
		Assert.assertEquals(sd.getId(), application.getSd().getId());
		// 应用有且只有1个版本
		Assert.assertEquals(1, application.getVersions().size());
		// 应用唯一版本的版本号是SP指定的版本号
		Assert.assertEquals(versionNo, application.getVersions().get(0).getVersionNo());
		// 应用评分统计已经存在
		Assert.assertNotNull(application.getStatistics());
		Assert.assertEquals("下载量初始化", 0, (int) application.getDownloadCount());
		Assert.assertEquals("状态正确", Application.STATUS_INIT, application.getStatus().intValue());
	}

	@Test
	public void testCreateNewApplicationUrlError() {
		Application application = ApplicationUtils.createDefult();
		SecurityDomain sd = SecurityDomainUtils.createDefult();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		spDao.saveOrUpdate(sp);
		sdDao.saveOrUpdate(sd);

		spDao.getAll();
		sdDao.getAll();

		ApplicationType type = new ApplicationType();
		appTypeDao.saveOrUpdate(type);
		appTypeDao.getAll();

		application.setSdModel(SecurityDomain.MODEL_DAP);
		
		application.setBusinessPlatformUrl("");

		Map<String, String> params = new HashMap<String, String>();
		Long sdId = sd.getId();
		params.put("sdId", sdId.toString());
		String versionNo = "1.0.0";
		params.put("versionNo", versionNo);
		params.put("applicationTypeId", type.getId().toString());
		File file = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "testIcon.gif");
		params.put("pcIconTempFileAbsPath", file.getAbsolutePath());
		params.put("mobileIconTempFileAbsPath", file.getAbsolutePath());

		try {
			target.createNewApplication(sp.getSysUser().getUserName(), application, params);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.APPLICAION_URL_SERVICE_ERROR, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
	
	@Test
	public void testCreateNewApplicationServiceNameError() {
		Application application = ApplicationUtils.createDefult();
		SecurityDomain sd = SecurityDomainUtils.createDefult();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();

		spDao.saveOrUpdate(sp);
		sdDao.saveOrUpdate(sd);

		spDao.getAll();
		sdDao.getAll();

		ApplicationType type = new ApplicationType();
		appTypeDao.saveOrUpdate(type);
		appTypeDao.getAll();

		application.setSdModel(SecurityDomain.MODEL_DAP);
		
		application.setServiceName("");

		Map<String, String> params = new HashMap<String, String>();
		Long sdId = sd.getId();
		params.put("sdId", sdId.toString());
		String versionNo = "1.0.0";
		params.put("versionNo", versionNo);
		params.put("applicationTypeId", type.getId().toString());
		File file = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "testIcon.gif");
		params.put("pcIconTempFileAbsPath", file.getAbsolutePath());
		params.put("mobileIconTempFileAbsPath", file.getAbsolutePath());

		try {
			target.createNewApplication(sp.getSysUser().getUserName(), application, params);
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			e.printStackTrace();
			Assert.assertEquals(PlatformErrorCode.APPLICAION_URL_SERVICE_ERROR, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
	}
}
