package com.justinmobile.tsm.application.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.utils.LoadFileVerUtils;
import com.justinmobile.tsm.application.utils.LoadModuleUtils;

@TransactionConfiguration
public class LoadModuleDaoTest extends BaseAbstractTest {

	LoadModule lm;

	@Autowired
	LoadModuleDao target;

	@Autowired
	LoadModuleDao lmDao;

	@Autowired
	LoadFileVersionDao lfVerDao;

	@Before
	public void setUp() {
		LoadFileVersion lfVer = LoadFileVerUtils.createDefualt();
		lfVerDao.saveOrUpdate(lfVer);
		lfVerDao.getAll();

		lm = LoadModuleUtils.createDefualt("11");
		lm.setLoadFileVersion(lfVer);
		lmDao.saveOrUpdate(lm);
		lmDao.getAll();
	}

	@Test
	public void testIsAidExistExist() {
		try {
			boolean result = target.isAidExist(lm.getLoadFileVersion(), lm.getAid());
			Assert.assertTrue(result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未期望异常");
		}
	}

	@Test
	public void testIsAidExistSameAidInDiffVersion() {
		LoadFileVersion lfVer = LoadFileVerUtils.createDefualt();
		lfVerDao.saveOrUpdate(lfVer);
		lfVerDao.getAll();

		try {
			boolean result = target.isAidExist(lfVer, lm.getAid());
			Assert.assertFalse(result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未期望异常");
		}
	}

	@Test
	public void testIsAidExistDiffAidInSameVersion() {
		String aid = lm.getAid() + "11";
		executeSql("delete from load_module where load_module_aid = " + aid);

		try {
			boolean result = target.isAidExist(lm.getLoadFileVersion(), aid);
			Assert.assertFalse(result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出未期望异常");
		}
	}

}
