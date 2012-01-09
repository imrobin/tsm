package com.justinmobile.tsm.card.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.AppletDao;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.utils.AppletUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.application.utils.LoadModuleUtils;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.utils.CardInfoUtils;

@TransactionConfiguration
public class CardAppletDaoTest extends BaseAbstractTest {

	@Autowired
	CardAppletDao target;

	@Autowired
	CardAppletDao caDao;

	@Autowired
	AppletDao appletDao;

	@Autowired
	LoadFileVersionDao lfVerDao;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Autowired
	CardInfoDao ciDao;

	@Test
	public void testGetByCardNoAndApplicationVersionThatCreateLoadFileVersion() {
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lfVerDao.saveOrUpdate(lfVer);

		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVer);

		CardInfo card = CardInfoUtils.createDefult();
		ciDao.saveOrUpdate(card);

		{// 干扰数据——不在卡上
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			applet.assignLoadModule(lm);
			lm.assignLoadFileVersion(lfVer);
			applet.assignApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);
		}

		{// 干扰数据——在卡上，但不属于同一加载文件
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			applet.assignLoadModule(lm);
			lm.assignLoadFileVersion(LoadFileVersionUtils.createDefult());
			applet.assignApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);

			CardApplet ca = new CardApplet();
			ca.setCard(card);
			ca.setApplet(applet);
			caDao.saveOrUpdate(ca);
		}

		{// 干扰数据——在卡上，但不属于同一应用
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			applet.assignLoadModule(lm);
			lm.assignLoadFileVersion(lfVer);
			applet.assignApplicationVersion(ApplicationVersionUtils.createDefult());
			appletDao.saveOrUpdate(applet);

			CardApplet ca = new CardApplet();
			ca.setCard(card);
			ca.setApplet(applet);
			caDao.saveOrUpdate(ca);
		}

		{// 干扰数据——不属于同一卡片
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			applet.assignLoadModule(lm);
			lm.assignLoadFileVersion(lfVer);
			applet.assignApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);

			CardInfo co = CardInfoUtils.createDefult();
			ciDao.saveOrUpdate(co);

			CardApplet ca = new CardApplet();
			ca.setCard(co);
			ca.setApplet(applet);
			caDao.saveOrUpdate(ca);
		}

		Set<CardApplet> caSet = new HashSet<CardApplet>();
		{
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			applet.assignLoadModule(lm);
			lm.assignLoadFileVersion(lfVer);
			applet.assignApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);

			CardApplet ca = new CardApplet();
			ca.setCard(card);
			ca.setApplet(applet);
			caDao.saveOrUpdate(ca);
			caSet.add(ca);
		}
		{
			Applet applet = AppletUtils.createDefult();
			LoadModule lm = LoadModuleUtils.createDefualt();
			applet.assignLoadModule(lm);
			lm.assignLoadFileVersion(lfVer);
			applet.assignApplicationVersion(appVer);
			appletDao.saveOrUpdate(applet);

			CardApplet ca = new CardApplet();
			ca.setCard(card);
			ca.setApplet(applet);
			caDao.saveOrUpdate(ca);
			caSet.add(ca);
		}

		lfVerDao.getAll();
		appVerDao.getAll();
		ciDao.getAll();
		appletDao.getAll();
		caDao.getAll();

		List<CardApplet> result = null;
		try {
			System.out.println("调用目标方法开始");
			result = target.getByCardNoAndApplicationVersionThatCreateLoadFileVersion(card, appVer, lfVer);
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数", caSet.size(), result.size());
		Assert.assertTrue("结果集合", result.containsAll(caSet));
	}
}
