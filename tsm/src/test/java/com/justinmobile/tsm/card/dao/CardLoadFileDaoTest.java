package com.justinmobile.tsm.card.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.utils.LoadFileVersionUtils;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.utils.CardInfoUtils;

@TransactionConfiguration
public class CardLoadFileDaoTest extends BaseAbstractTest {

	@Autowired
	CardLoadFileDao target;

	@Autowired
	CardLoadFileDao clfDao;

	@Autowired
	LoadFileVersionDao lfVerDao;

	@Autowired
	CardInfoDao ciDao;

	@Test
	public void testGetByCardAndLoadFileVersion() {
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lfVerDao.saveOrUpdate(lfVer);

		CardInfo card = CardInfoUtils.createDefult();
		ciDao.saveOrUpdate(card);

		CardLoadFile cardLf = new CardLoadFile();
		cardLf.setCard(card);
		cardLf.setLoadFileVersion(lfVer);
		clfDao.saveOrUpdate(cardLf);

		{
			LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
			lfVerDao.saveOrUpdate(lfVerOther);

			CardLoadFile cardLfOther = new CardLoadFile();
			cardLfOther.setCard(card);
			cardLfOther.setLoadFileVersion(lfVerOther);
			clfDao.saveOrUpdate(cardLfOther);
		}

		{
			CardInfo cardOther = CardInfoUtils.createDefult();
			ciDao.saveOrUpdate(cardOther);

			CardLoadFile cardLfOther = new CardLoadFile();
			cardLfOther.setCard(cardOther);
			cardLfOther.setLoadFileVersion(lfVer);
			clfDao.saveOrUpdate(cardLfOther);
		}

		{
			CardInfo cardOther = CardInfoUtils.createDefult();
			ciDao.saveOrUpdate(cardOther);
			LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
			lfVerDao.saveOrUpdate(lfVerOther);

			CardLoadFile cardLfOther = new CardLoadFile();
			cardLfOther.setCard(cardOther);
			cardLfOther.setLoadFileVersion(lfVerOther);
			clfDao.saveOrUpdate(cardLfOther);
		}

		lfVerDao.getAll();
		ciDao.getAll();
		clfDao.getAll();

		{
			CardLoadFile result = null;
			try {
				result = target.getByCardAndLoadFileVersion(card, lfVer);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("抛出异常");
			}
			Assert.assertEquals("检查结果", cardLf, result);
		}

		{
			CardInfo cardOther = CardInfoUtils.createDefult();
			ciDao.saveOrUpdate(cardOther);
			LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
			lfVerDao.saveOrUpdate(lfVerOther);
			ciDao.getAll();
			clfDao.getAll();

			CardLoadFile result = null;
			try {
				result = target.getByCardAndLoadFileVersion(cardOther, lfVerOther);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("抛出异常");
			}
			Assert.assertNull("检查结果", result);
		}
	}

	@Test
	public void testGetByAidAndCardNo() {
		LoadFileVersion lfVer = LoadFileVersionUtils.createDefult();
		lfVerDao.saveOrUpdate(lfVer);

		CardInfo card = CardInfoUtils.createDefult();
		ciDao.saveOrUpdate(card);

		CardLoadFile cardLf = new CardLoadFile();
		cardLf.setCard(card);
		cardLf.setLoadFileVersion(lfVer);
		clfDao.saveOrUpdate(cardLf);

		{
			LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
			lfVerDao.saveOrUpdate(lfVerOther);

			CardLoadFile cardLfOther = new CardLoadFile();
			cardLfOther.setCard(card);
			cardLfOther.setLoadFileVersion(lfVerOther);
			clfDao.saveOrUpdate(cardLfOther);
		}

		{
			CardInfo cardOther = CardInfoUtils.createDefult();
			ciDao.saveOrUpdate(cardOther);

			CardLoadFile cardLfOther = new CardLoadFile();
			cardLfOther.setCard(cardOther);
			cardLfOther.setLoadFileVersion(lfVer);
			clfDao.saveOrUpdate(cardLfOther);
		}

		{
			CardInfo cardOther = CardInfoUtils.createDefult();
			ciDao.saveOrUpdate(cardOther);
			LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
			lfVerDao.saveOrUpdate(lfVerOther);

			CardLoadFile cardLfOther = new CardLoadFile();
			cardLfOther.setCard(cardOther);
			cardLfOther.setLoadFileVersion(lfVerOther);
			clfDao.saveOrUpdate(cardLfOther);
		}

		lfVerDao.getAll();
		ciDao.getAll();
		clfDao.getAll();

		{
			CardLoadFile result = null;
			try {
				result = target.getByAidAndCardNo(lfVer.getLoadFile().getAid(), card.getCardNo());
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("抛出异常");
			}
			Assert.assertEquals("检查结果", cardLf, result);
		}

		{
			CardInfo cardOther = CardInfoUtils.createDefult();
			ciDao.saveOrUpdate(cardOther);
			LoadFileVersion lfVerOther = LoadFileVersionUtils.createDefult();
			lfVerDao.saveOrUpdate(lfVerOther);
			ciDao.getAll();
			clfDao.getAll();

			CardLoadFile result = null;
			try {
				result = target.getByAidAndCardNo(lfVerOther.getLoadFile().getAid(), cardOther.getCardNo());
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("抛出异常");
			}
			Assert.assertNull("检查结果", result);
		}
	}
}
