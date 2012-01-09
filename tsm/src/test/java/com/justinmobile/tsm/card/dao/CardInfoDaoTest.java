package com.justinmobile.tsm.card.dao;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.card.domain.CardInfo;

@TransactionConfiguration
public class CardInfoDaoTest extends BaseAbstractTest {

	@Autowired
	CardInfoDao target;

	@Autowired
	CardInfoDao ciDao;

	@Test
	public void testGetByCardNo() {
		String cardNo = RandomStringUtils.randomNumeric(10);
		{
			CardInfo ci = new CardInfo();
			ci.setCardNo(cardNo + "AA");
			ciDao.saveOrUpdate(ci);
		}

		CardInfo ci = new CardInfo();
		ci.setCardNo(cardNo);
		ciDao.saveOrUpdate(ci);

		ciDao.getAll();

		CardInfo actual = null;
		try {
			actual = target.getByCardNo(cardNo);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果", ci, actual);
	}

}
