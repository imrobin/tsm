package com.justinmobile.tsm.application.manager;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.manager.CardApplicationManager;

@TransactionConfiguration
public class ApplicationClientInfoManagerTest extends BaseAbstractTest {

	@Autowired
	private ApplicationClientInfoManager target;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private ApplicationVersionDao appVerDao;

	@Test
	public void testUploadApplicationClient() {
		ApplicationVersion appVer = ApplicationVersionUtils.createDefult();
		appVerDao.saveOrUpdate(appVer);
		appVerDao.getAll();

		String filePath = "src\\test\\resources\\apdu.cap";
		String saveDir = "";
		ApplicationClientInfo appCi = new ApplicationClientInfo();

		try {
			System.out.println("调用目标方法开始");
			target.uploadApplicationClient(appCi, filePath, saveDir, appVer.getId(), "", "");
			target.getAll();
			System.out.println("调用目标方法结束");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("业务类型", ApplicationClientInfo.BUSI_TYPE_APPLICATION_CLIENT, appCi.getBusiType());
		Assert.assertTrue("url存在", StringUtils.isNotBlank(appCi.getFileUrl()));
		Assert.assertTrue("文件存在", new File(saveDir + appCi.getFileUrl()).exists());
		Assert.assertTrue("文件大小", (null != appCi.getSize()) && (0 < appCi.getSize()));
		Assert.assertEquals("关联到应用版本", 1, appCi.getApplicationVersions().size());
		Assert.assertTrue("关联到指定应用版本", appCi.getApplicationVersions().contains(appVer));
	}

	@Test
	public void testApplicationVersion() {
		CardApplication cardApp = cardApplicationManager.getByCardNoAid("081111111111111111", "001122334455A");
		ApplicationVersion appVer = cardApp.getApplicationVersion();
		System.out.println("appVer==" + appVer);
		ApplicationClientInfo aci = target.getByApplicationVersionSysTypeSysRequirementFileType(appVer, "j2me", "J2EE", "jad");
		System.out.println("aci==" + aci);
	}
}
