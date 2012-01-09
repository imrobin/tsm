package com.justinmobile.tsm.card.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.card.domain.CardClient;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.utils.CardInfoUtils;

@TransactionConfiguration
public class CardClientDaoTest extends BaseAbstractTest {

	@Autowired
	CardClientDao target;

	@Autowired
	CardClientDao ccDao;

	@Test
	public void testGetByCardAndApplication() {
		// 开始准备正确数据
		Application application = ApplicationUtils.createDefult();
		CardInfo card = CardInfoUtils.createDefult();

		ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
		application.addVersion(applicationVersion);

		ApplicationClientInfo applicationClinet = new ApplicationClientInfo();
		applicationClinet.addApplicationVersion(applicationVersion);

		CardClient cardClient = new CardClient();
		cardClient.setClient(applicationClinet);
		cardClient.setCard(card);
		ccDao.saveOrUpdate(cardClient);

		// 异常数据-卡不正确
		{
			CardInfo cardOther = CardInfoUtils.createDefult();
			CardClient cardClientOther = new CardClient();
			cardClientOther.setClient(applicationClinet);
			cardClientOther.setCard(cardOther);
			ccDao.saveOrUpdate(cardClient);
		}

		// 异常数据-应用不正确
		{
			Application applicationOther = ApplicationUtils.createDefult();

			ApplicationVersion applicationVersionOther = ApplicationVersionUtils.createDefult();
			applicationOther.addVersion(applicationVersionOther);

			ApplicationClientInfo applicationClinetOther = new ApplicationClientInfo();
			applicationClinet.addApplicationVersion(applicationVersionOther);

			CardClient cardClientOther = new CardClient();
			cardClientOther.setClient(applicationClinetOther);
			cardClientOther.setCard(card);
			ccDao.saveOrUpdate(cardClientOther);
		}

		// 异常数据-卡和应用都不正确
		{
			Application applicationOther = ApplicationUtils.createDefult();
			CardInfo cardOther = CardInfoUtils.createDefult();

			ApplicationVersion applicationVersionOther = ApplicationVersionUtils.createDefult();
			applicationOther.addVersion(applicationVersionOther);

			ApplicationClientInfo applicationClinetOther = new ApplicationClientInfo();
			applicationClinet.addApplicationVersion(applicationVersionOther);

			CardClient cardClientOther = new CardClient();
			cardClientOther.setClient(applicationClinetOther);
			cardClientOther.setCard(cardOther);
			ccDao.saveOrUpdate(cardClientOther);
		}

		// 开始测试
		List<CardClient> result = null;
		try {
			result = target.getByCardAndApplication(card, application);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("检查结果", cardClient, result);
	}
}
