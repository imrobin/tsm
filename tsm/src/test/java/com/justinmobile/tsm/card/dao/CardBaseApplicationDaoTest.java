package com.justinmobile.tsm.card.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.utils.CardBaseInfoUtils;

@TransactionConfiguration
public class CardBaseApplicationDaoTest extends BaseAbstractTest {

	@Autowired
	CardBaseApplicationDao target;

	@Autowired
	CardBaseApplicationDao cbaDao;

	@Autowired
	CardBaseInfoDao cbDao;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Autowired
	ApplicationDao appDao;

	@Test
	public void testGetByCardBaseAndApplicationAsVersionNoDesc() {
		CardBaseInfo cardBase = CardBaseInfoUtils.createDefult();
		cbDao.saveOrUpdate(cardBase);

		ApplicationVersion appVer1 = ApplicationVersionUtils.createDefult();
		ApplicationVersion appVer2 = ApplicationVersionUtils.createDefult();
		ApplicationVersion appVer3 = ApplicationVersionUtils.createDefult();

		Application app = ApplicationUtils.createDefult();
		appDao.saveOrUpdate(app);

		appVer1.setApplication(app);
		appVer1.setVersionNo("1.0.5");
		appVerDao.saveOrUpdate(appVer1);

		appVer2.setApplication(app);
		appVer2.setVersionNo("1.0.21");
		appVerDao.saveOrUpdate(appVer2);

		appVer3.setApplication(app);
		appVer3.setVersionNo("2.0.3");
		appVerDao.saveOrUpdate(appVer3);

		CardBaseApplication cba1 = new CardBaseApplication();
		cba1.setApplicationVersion(appVer1);
		cba1.setCardBase(cardBase);
		cbaDao.saveOrUpdate(cba1);

		CardBaseApplication cba2 = new CardBaseApplication();
		cba2.setApplicationVersion(appVer2);
		cba2.setCardBase(cardBase);
		cbaDao.saveOrUpdate(cba2);

		CardBaseApplication cba3 = new CardBaseApplication();
		cba3.setApplicationVersion(appVer3);
		cba3.setCardBase(cardBase);
		cbaDao.saveOrUpdate(cba3);

		{
			Application appOther = ApplicationUtils.createDefult();
			appDao.saveOrUpdate(appOther);
			ApplicationVersion appVerOther = ApplicationVersionUtils.createDefult();
			appVerOther.setApplication(appOther);
			appVerDao.saveOrUpdate(appVerOther);

			CardBaseApplication cbaOther = new CardBaseApplication();
			cbaOther.setApplicationVersion(appVerOther);
			cbaOther.setCardBase(cardBase);
			cbaDao.saveOrUpdate(cbaOther);
		}

		{
			CardBaseInfo cardBaseOther = CardBaseInfoUtils.createDefult();
			cbDao.saveOrUpdate(cardBaseOther);

			CardBaseApplication cbaOther = new CardBaseApplication();
			cbaOther.setApplicationVersion(appVer1);
			cbaOther.setCardBase(cardBaseOther);
			cbaDao.saveOrUpdate(cbaOther);
		}

		{
			CardBaseInfo cardBaseOther = CardBaseInfoUtils.createDefult();
			cbDao.saveOrUpdate(cardBaseOther);

			Application appOther = ApplicationUtils.createDefult();
			appDao.saveOrUpdate(appOther);
			ApplicationVersion appVerOther = ApplicationVersionUtils.createDefult();
			appVerOther.setApplication(appOther);
			appVerDao.saveOrUpdate(appVerOther);

			CardBaseApplication cbaOther = new CardBaseApplication();
			cbaOther.setApplicationVersion(appVerOther);
			cbaOther.setCardBase(cardBaseOther);
			cbaDao.saveOrUpdate(cbaOther);
		}

		cbDao.getAll();
		appDao.getAll();
		appVerDao.getAll();
		cbaDao.getAll();

		List<CardBaseApplication> result = null;
		try {
			result = target.getByCardBaseAndApplicationThatStatusPublishedAsVersionNoDesc(cardBase, app);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		System.out.println("appVer1.id: "+ appVer1.getId());
		System.out.println("appVer2.id: "+ appVer2.getId());
		System.out.println("appVer3.id: "+ appVer3.getId());

		for (CardBaseApplication cba : result) {
			System.out.println(cba.getApplicationVersion().getId());
		}
		Assert.assertEquals("结果数", 3, result.size());
		Assert.assertEquals("结果1", appVer3, result.get(0).getApplicationVersion());
		Assert.assertEquals("结果2", appVer2, result.get(1).getApplicationVersion());
		Assert.assertEquals("结果3", appVer1, result.get(2).getApplicationVersion());
	}
}
