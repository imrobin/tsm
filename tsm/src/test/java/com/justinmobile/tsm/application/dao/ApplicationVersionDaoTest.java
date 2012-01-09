package com.justinmobile.tsm.application.dao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;

@TransactionConfiguration
public class ApplicationVersionDaoTest extends BaseAbstractTest {

	@Autowired
	ApplicationVersionDao target;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Autowired
	ApplicationDao appDao;

	@Test
	public void testGetAidAndVersionNo() {
		String versionNo = "1.0.0";
		{
			Application app = ApplicationUtils.createDefult();
			ApplicationVersion appVer1 = ApplicationVersionUtils.createDefult();
			appDao.saveOrUpdate(app);

			appVer1.setVersionNo(versionNo);
			appVer1.assignApplication(app);
			appVerDao.saveOrUpdate(appVer1);
		}

		Application app = ApplicationUtils.createDefult();
		String aid = app.getAid();
		appDao.saveOrUpdate(app);

		ApplicationVersion appVer1 = ApplicationVersionUtils.createDefult();

		appVer1.setVersionNo(versionNo);
		appVer1.assignApplication(app);
		appVerDao.saveOrUpdate(appVer1);

		ApplicationVersion appVer2 = ApplicationVersionUtils.createDefult();
		appVer2.setVersionNo("1.0.1");
		appVer2.assignApplication(app);
		appVerDao.saveOrUpdate(appVer2);

		ApplicationVersion appVer = null;
		try {
			appVer = target.getAidAndVersionNo(aid, versionNo);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果正确", appVer1, appVer);

	}

}
