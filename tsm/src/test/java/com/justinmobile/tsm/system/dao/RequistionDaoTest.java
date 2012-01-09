package com.justinmobile.tsm.system.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.system.domain.Requistion;

@TransactionConfiguration
public class RequistionDaoTest extends BaseAbstractTest {

	@Autowired
	private RequistionDao target;

	@Autowired
	private RequistionDao reqDao;

	@Test
	public void testGetCountByTypeAndStatusAndOrignaId() {
		Long experctOriginalId = (long) 1984;
		Integer expertType = Requistion.TYPE_APP_PUBLISH;
		Integer expertStatus = Requistion.STATUS_PASS;

		{
			// 干扰数据，其他类型
			Integer actualType = Requistion.TYPE_APP_ARCHIVE;
			Assert.assertNotSame("干扰数据的类型", expertType, actualType);
			Requistion requistion = new Requistion();
			requistion.setOriginalId(experctOriginalId);
			requistion.setType(actualType);
			requistion.setStatus(expertStatus);
			reqDao.saveOrUpdate(requistion);
		}

		{
			// 干扰数据，其他OriginalId
			Long actualOriginalId = (long) 1028;
			Assert.assertNotSame("干扰数据的OriginalId", experctOriginalId, actualOriginalId);
			Requistion requistion = new Requistion();
			requistion.setOriginalId(actualOriginalId);
			requistion.setType(expertType);
			requistion.setStatus(expertStatus);
			reqDao.saveOrUpdate(requistion);
		}

		{
			// 干扰数据，其他状态
			Integer actualStatus = Requistion.STATUS_INIT;
			Assert.assertNotSame("干扰数据的状态", expertStatus, actualStatus);
			Requistion requistion = new Requistion();
			requistion.setOriginalId(experctOriginalId);
			requistion.setType(expertType);
			requistion.setStatus(actualStatus);
			reqDao.saveOrUpdate(requistion);
		}

		Requistion requistion = new Requistion();
		requistion.setOriginalId(experctOriginalId);
		requistion.setType(expertType);
		requistion.setStatus(expertStatus);
		reqDao.saveOrUpdate(requistion);

		reqDao.getAll();

		int count = 0;
		try {
			System.out.println("调用目标方法开始");
			count = target.getCountByTypeAndStatusAndOrignaId(expertType, expertStatus, experctOriginalId);
			System.out.println("调用目标方法完成");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果", 1, count);
	}
}
