package com.justinmobile.tsm.customer.dao;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysUserUtils;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.utils.ApplicationUtils;
import com.justinmobile.tsm.application.utils.ApplicationVersionUtils;
import com.justinmobile.tsm.card.dao.CardApplicationDao;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.utils.CustomerUtils;

@TransactionConfiguration
public class CustomerCardInfoDaoTest extends BaseAbstractTest {

	@Autowired
	private CustomerCardInfoDao target;

	@Autowired
	private CustomerCardInfoDao cciDao;

	@Autowired
	private CardInfoDao ciDao;

	@Autowired
	private CustomerDao custDao;

	@Autowired
	private ApplicationDao appDao;

	@Autowired
	private ApplicationVersionDao appVerDao;

	@Autowired
	private CardApplicationDao cardAppDao;

	public void testCRUD() {
		// Create
		CustomerCardInfo customerCardInfo = new CustomerCardInfo();
		setSimpleProperties(customerCardInfo);
		customerCardInfo.setImei("imeiimei");
		Assert.assertNull(customerCardInfo.getId());
		cciDao.saveOrUpdate(customerCardInfo);
		Long id = customerCardInfo.getId();
		Assert.assertNotNull(id);
		// Upate
		CustomerCardInfo cci = cciDao.load(id);
		Assert.assertEquals("imeiimei", cci.getImei());
		cci.setImei("imeimodify");
		cciDao.saveOrUpdate(cci);
		CustomerCardInfo cciModify = cciDao.load(id);
		Assert.assertEquals("imeimodify", cciModify.getImei());
		// remove
		cciDao.remove(id);
		try {
			cciDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testGetByCardThatStatusNotCanclledOrNotReplacedNormal() {
		CardInfo card = new CardInfo();
		ciDao.saveOrUpdate(card);

		Customer customer = new Customer();
		customer.setSysUser(SysUserUtils.createDefult());
		custDao.saveOrUpdate(customer);

		{
			CardInfo cardOther = new CardInfo();
			ciDao.saveOrUpdate(cardOther);

			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(cardOther);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_CANCEL);
			cciDao.saveOrUpdate(customerCard);
		}

		CustomerCardInfo customerCard = new CustomerCardInfo();
		customerCard.setCard(card);
		customerCard.setCustomer(customer);
		customerCard.setStatus(CustomerCardInfo.STATUS_NORMAL);
		cciDao.saveOrUpdate(customerCard);

		CustomerCardInfo result = null;
		try {
			System.out.println("开始调用目标方法");
			result = target.getByCardThatStatusNotCanclledOrNotReplaced(card);
			System.out.println("完成调用目标方法");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals(customerCard, result);
	}

	@Test
	public void testGetByCardThatStatusNotCanclledOrNotReplacedLost() {
		CardInfo card = new CardInfo();
		ciDao.saveOrUpdate(card);

		Customer customer = new Customer();
		customer.setSysUser(SysUserUtils.createDefult());
		custDao.saveOrUpdate(customer);

		{
			CardInfo cardOther = new CardInfo();
			ciDao.saveOrUpdate(cardOther);

			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(cardOther);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_CANCEL);
			cciDao.saveOrUpdate(customerCard);
		}

		// {
		CustomerCardInfo customerCard = new CustomerCardInfo();
		customerCard.setCard(card);
		customerCard.setCustomer(customer);
		customerCard.setStatus(CustomerCardInfo.STATUS_LOST);
		cciDao.saveOrUpdate(customerCard);
		// }
		//
		// {
		// CustomerCardInfo customerCard = new CustomerCardInfo();
		// customerCard.setCard(card);
		// customerCard.setStatus(CustomerCardInfo.STATUS_NOT_USE);
		// cciDao.saveOrUpdate(customerCard);
		// expected.add(customerCard);
		// }
		//
		// {
		// CustomerCardInfo customerCard = new CustomerCardInfo();
		// customerCard.setCard(card);
		// customerCard.setStatus(CustomerCardInfo.STATUS_REPLACING);
		// cciDao.saveOrUpdate(customerCard);
		// }

		CustomerCardInfo result = null;
		try {
			System.out.println("开始调用目标方法");
			result = target.getByCardThatStatusNotCanclledOrNotReplaced(card);
			System.out.println("完成调用目标方法");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals(customerCard, result);
	}

	@Test
	public void testGetByCardThatStatusNotCanclledOrNotReplacedNotUse() {
		CardInfo card = new CardInfo();
		ciDao.saveOrUpdate(card);

		Customer customer = new Customer();
		customer.setSysUser(SysUserUtils.createDefult());
		custDao.saveOrUpdate(customer);

		{
			CardInfo cardOther = new CardInfo();
			ciDao.saveOrUpdate(cardOther);

			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(cardOther);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_CANCEL);
			cciDao.saveOrUpdate(customerCard);
		}

		CustomerCardInfo customerCard = new CustomerCardInfo();
		customerCard.setCard(card);
		customerCard.setCustomer(customer);
		customerCard.setStatus(CustomerCardInfo.STATUS_NOT_USE);
		cciDao.saveOrUpdate(customerCard);

		CustomerCardInfo result = null;
		try {
			System.out.println("开始调用目标方法");
			result = target.getByCardThatStatusNotCanclledOrNotReplaced(card);
			System.out.println("完成调用目标方法");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals(customerCard, result);
	}

	@Test
	public void testGetByCardThatStatusNotCanclledOrNotReplacedReplacing() {
		CardInfo card = new CardInfo();
		ciDao.saveOrUpdate(card);

		Customer customer = new Customer();
		customer.setSysUser(SysUserUtils.createDefult());
		custDao.saveOrUpdate(customer);

		{
			CardInfo cardOther = new CardInfo();
			ciDao.saveOrUpdate(cardOther);

			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(cardOther);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			cciDao.saveOrUpdate(customerCard);
			cciDao.getAll();
		}

		{
			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			customerCard.setStatus(CustomerCardInfo.STATUS_CANCEL);
			cciDao.saveOrUpdate(customerCard);
		}

		CustomerCardInfo customerCard = new CustomerCardInfo();
		customerCard.setCard(card);
		customerCard.setCustomer(customer);
		customerCard.setStatus(CustomerCardInfo.STATUS_REPLACING);
		cciDao.saveOrUpdate(customerCard);

		CustomerCardInfo result = null;
		try {
			System.out.println("开始调用目标方法");
			result = target.getByCardThatStatusNotCanclledOrNotReplaced(card);
			System.out.println("完成调用目标方法");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals(customerCard, result);
	}

	@Test
	public void testGetByCustomerAndApplicationThatCardApplicationMigrateableTrueOrderByBindingDateDesc() {
		Customer customer = CustomerUtils.createDefult();
		custDao.saveOrUpdate(customer);

		Application application = ApplicationUtils.createDefult();
		appDao.saveOrUpdate(application);

		{// 干扰数据-应用不同
			CardInfo card = new CardInfo();
			ciDao.saveOrUpdate(card);

			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			cciDao.saveOrUpdate(customerCard);

			Application applicationOther = ApplicationUtils.createDefult();
			appDao.saveOrUpdate(applicationOther);

			ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
			applicationVersion.assignApplication(applicationOther);
			appVerDao.saveOrUpdate(applicationVersion);

			CardApplication cardApplication = new CardApplication();
			cardApplication.setCardInfo(card);
			cardApplication.setApplicationVersion(applicationVersion);
			cardApplication.setMigratable(Boolean.TRUE);
			cardAppDao.saveOrUpdate(cardApplication);
		}

		{// 干扰数据-不同的用户
			CardInfo card = new CardInfo();
			ciDao.saveOrUpdate(card);

			Customer customerOther = CustomerUtils.createDefult();
			custDao.saveOrUpdate(customerOther);

			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customerOther);
			cciDao.saveOrUpdate(customerCard);

			ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
			applicationVersion.assignApplication(application);
			appVerDao.saveOrUpdate(applicationVersion);

			CardApplication cardApplication = new CardApplication();
			cardApplication.setCardInfo(card);
			cardApplication.setApplicationVersion(applicationVersion);
			cardApplication.setMigratable(Boolean.TRUE);
			cardAppDao.saveOrUpdate(cardApplication);
		}

		{// 干扰数据-未迁出
			CardInfo card = new CardInfo();
			ciDao.saveOrUpdate(card);

			CustomerCardInfo customerCard = new CustomerCardInfo();
			customerCard.setCard(card);
			customerCard.setCustomer(customer);
			cciDao.saveOrUpdate(customerCard);

			ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
			applicationVersion.assignApplication(application);
			appVerDao.saveOrUpdate(applicationVersion);

			CardApplication cardApplication = new CardApplication();
			cardApplication.setCardInfo(card);
			cardApplication.setApplicationVersion(applicationVersion);
			cardAppDao.saveOrUpdate(cardApplication);
		}

		CustomerCardInfo customerCard1 = new CustomerCardInfo();
		{
			CardInfo card = new CardInfo();
			ciDao.saveOrUpdate(card);

			customerCard1.setCard(card);
			customerCard1.setCustomer(customer);
			customerCard1.setBindingDate(Calendar.getInstance());
			cciDao.saveOrUpdate(customerCard1);

			ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
			applicationVersion.assignApplication(application);
			appVerDao.saveOrUpdate(applicationVersion);

			CardApplication cardApplication = new CardApplication();
			cardApplication.setCardInfo(card);
			cardApplication.setApplicationVersion(applicationVersion);
			cardApplication.setMigratable(Boolean.TRUE);
			cardAppDao.saveOrUpdate(cardApplication);
		}

		CustomerCardInfo customerCard2 = new CustomerCardInfo();
		{
			CardInfo card = new CardInfo();
			ciDao.saveOrUpdate(card);

			customerCard2.setCard(card);
			customerCard2.setCustomer(customer);
			customerCard2.setBindingDate(Calendar.getInstance());
			cciDao.saveOrUpdate(customerCard2);

			ApplicationVersion applicationVersion = ApplicationVersionUtils.createDefult();
			applicationVersion.assignApplication(application);
			appVerDao.saveOrUpdate(applicationVersion);

			CardApplication cardApplication = new CardApplication();
			cardApplication.setCardInfo(card);
			cardApplication.setApplicationVersion(applicationVersion);
			cardApplication.setMigratable(Boolean.TRUE);
			cardAppDao.saveOrUpdate(cardApplication);
		}

		List<CustomerCardInfo> result = null;
		try {
			System.out.println("开始调用目标方法");
			result = target.getByCustomerAndApplicationThatCardApplicationMigrateableTrueOrderByBindingDateDesc(application, customer);
			System.out.println("完成调用目标方法");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("抛出异常");
		}

		Assert.assertEquals("结果数目正确", 2, result.size());
		Assert.assertEquals("第1个结果", customerCard2, result.get(0));
		Assert.assertEquals("第2个结果", customerCard1, result.get(1));

	}
}
