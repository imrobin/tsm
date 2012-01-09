package com.justinmobile.tsm.application.manager;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysUserUtils;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.dao.LoadModuleDao;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.utils.AppletUtils;
import com.justinmobile.tsm.application.utils.LoadFileUtils;
import com.justinmobile.tsm.application.utils.LoadFileVerUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.application.utils.LoadModuleUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.utils.SpBaseInfoUtils;

@TransactionConfiguration
public class LoadModuleManagerTest extends BaseAbstractTest {

	@Autowired
	LoadModuleManager target;

	@Autowired
	private LoadFileVersionDao lfVerDao;

	@Autowired
	private SpBaseInfoDao spDao;

	@Autowired
	private LoadModuleDao lmDao;

	@Test
	public void testCreateNewLoadModule() {
		logger.info("testCreateNewLoadModule");

		LoadFileVersion loadFileVersion = LoadFileVerUtils.createDefualt();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		LoadFile lf = LoadFileUtils.createDefult();
		loadFileVersion.setLoadFile(lf);
		lf.setSp(sp);
		lfVerDao.saveOrUpdate(loadFileVersion);
		lfVerDao.getAll();
		LoadModule loadModule = LoadModuleUtils.createDefualt("11");
		try {
			target.createNewLoadModule(loadModule, loadFileVersion.getId(), sp.getSysUser().getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("未期待异常");
		}
	}

	@Test
	public void testCreateNewLoadModuleSpDiscard() {
		logger.info("testCreateNewLoadModuleSpDiscard");

		LoadFileVersion loadFileVersion = LoadFileVerUtils.createDefualt();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		LoadFile lf = LoadFileUtils.createDefult();
		loadFileVersion.setLoadFile(lf);
		lf.setSp(sp);
		lfVerDao.saveOrUpdate(loadFileVersion);
		lfVerDao.getAll();

		SysUser user = SysUserUtils.createDefult();
		user.setUserName(RandomStringUtils.random(5, true, false) + user.getUserName());
		user.setEmail(RandomStringUtils.random(5, true, false) + user.getEmail());
		user.setMobile(RandomStringUtils.random(5, true, false) + user.getMobile());
		sp.setSysUser(user);
		sp.setStatus(SpBaseInfo.STATUS_AVALIABLE);
		spDao.saveOrUpdate(sp);
		spDao.getAll();
		
		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();
		spDao.saveOrUpdate(spOther);
		spDao.getAll();

		LoadModule loadModule = LoadModuleUtils.createDefualt("11");
		try {
			target.createNewLoadModule(loadModule, loadFileVersion.getId(), spOther.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("未期待异常");
		}
	}

	@Test
	public void testCreateNewLoadModuleAidReduplicate() {
		logger.info("testCreateNewLoadModuleAidReduplicate");
		LoadFileVersion loadFileVersion = LoadFileVerUtils.createDefualt();
		SpBaseInfo sp = SpBaseInfoUtils.createDefult();
		LoadFile lf = LoadFileUtils.createDefult();
		loadFileVersion.setLoadFile(lf);
		lf.setSp(sp);
		lfVerDao.saveOrUpdate(loadFileVersion);
		lfVerDao.getAll();

		LoadModule loadModule1 = LoadModuleUtils.createDefualt("11");
		loadModule1.setLoadFileVersion(loadFileVersion);
		lmDao.saveOrUpdate(loadModule1);

		LoadModule loadModule2 = LoadModuleUtils.createDefualt("11");
		try {
			target.createNewLoadModule(loadModule2, loadFileVersion.getId(), sp.getSysUser().getUserName());
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals(PlatformErrorCode.LOAD_MODULE_AID_REDUPLICATE, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("未期待异常");
		}
	}

	@Test
	public void testRemoveLoadModule() {
		LoadModule lm = LoadModuleUtils.createDefualt();
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lm.assignLoadFileVersion(lfVer);

		LoadFile lf = lfVer.getLoadFile();

		SpBaseInfo sp = SpBaseInfoUtils.createRandom();
		lf.setSp(sp);

		System.out.println("保存数据开始");
		lmDao.saveOrUpdate(lm);
		lmDao.getAll();
		System.out.println("保存数据完成");

		Assert.assertEquals("删除前模块数", 1, lm.getLoadFileVersion().getLoadModules().size());
		try {
			System.out.println("调用目标方法开始");
			target.removeLoadModule(lm.getId(), sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}
		Assert.assertEquals("删除后模块数", 0, lm.getLoadFileVersion().getLoadModules().size());
	}

	@Test
	public void testRemoveLoadModuleSpDiscard() {
		LoadModule lm = LoadModuleUtils.createDefualt();
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lm.assignLoadFileVersion(lfVer);

		LoadFile lf = lfVer.getLoadFile();

		SpBaseInfo sp = SpBaseInfoUtils.createRandom();
		lf.setSp(sp);

		SpBaseInfo spOther = SpBaseInfoUtils.createRandom();

		System.out.println("保存数据开始");
		spDao.saveOrUpdate(spOther);
		spDao.getAll();
		lmDao.saveOrUpdate(lm);
		lmDao.getAll();
		System.out.println("保存数据完成");

		try {
			System.out.println("调用目标方法开始");
			target.removeLoadModule(lm.getId(), spOther.getSysUser().getUserName());
			System.out.println("调用目标方法完成");
			Assert.fail("未抛出异常");
		} catch (PlatformException e) {
			Assert.assertEquals(PlatformErrorCode.LOAD_FILE_SP_DISCARD, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("未期待异常");
		}
	}

	@Test
	public void testRemoveLoadModuleAppletDefined() {
		LoadModule lm = LoadModuleUtils.createDefualt();
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lm.assignLoadFileVersion(lfVer);

		lm.addApplet(AppletUtils.createDefult());

		LoadFile lf = lfVer.getLoadFile();

		SpBaseInfo sp = SpBaseInfoUtils.createRandom();
		lf.setSp(sp);

		System.out.println("保存数据开始");
		lmDao.saveOrUpdate(lm);
		lmDao.getAll();
		System.out.println("保存数据完成");

		try {
			System.out.println("调用目标方法开始");
			target.removeLoadModule(lm.getId(), sp.getSysUser().getUserName());
			target.getAll();
			System.out.println("调用目标方法完成");
		} catch (PlatformException e) {
			Assert.assertEquals(PlatformErrorCode.LOAD_MODULE_DEFINED_APPLET, e.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("未期待异常");
		}
	}
}
