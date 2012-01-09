package com.justinmobile.tsm.application.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;

@TransactionConfiguration
public class ApplicationClientInfoDaoTest extends BaseAbstractTest {

	@Autowired
	ApplicationClientInfoDao target;

	@Autowired
	ApplicationClientInfoDao aciDao;

	@Autowired
	ApplicationVersionDao appVerDao;

	@Test
	public void testGetByApplicationVersion() {
		ApplicationVersion appVers = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVers);
		appVerDao.getAll();

		ApplicationClientInfo aci1 = new ApplicationClientInfo();
		aci1.getApplicationVersions().add(appVers);
		aciDao.saveOrUpdate(aci1);

		ApplicationClientInfo aci2 = new ApplicationClientInfo();
		aciDao.saveOrUpdate(aci2);

		aciDao.getAll();

		Page<ApplicationClientInfo> page = null;
		try {
			System.out.println("调用目标方法开始");
			page = new Page<ApplicationClientInfo>(20);
			page = target.getByApplicationVersion(page, appVers.getId());
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数目", 1, page.getResult().size());
		Assert.assertEquals("结果", aci1, page.getResult().get(0));
	}
}
