/**  
 * Filename:    MobileTypeDaoTest.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-5-24 下午02:49:25  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-24     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.tsm.customer.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.customer.domain.MobileType;




public class MobileTypeDaoTest  extends BaseAbstractTest{
	@Autowired
	private MobileTypeDao mobileTypeDao;
	@BeforeTransaction
	public void setup(){
		executeSql("insert into MOBILE_TYPE (ID,BRAND_CHS,BRAND_ENG,TYPE,ORIGINAL_OS_KEY,J2ME_KEY) values (999999999,'联想','lenovo','P668',0,0)");
		
	}
	
	@AfterTransaction
	public void teardown(){
		executeSql("delete from MOBILE_TYPE where id = 999999");
	}
	@Test
	public void testCRUD(){
		MobileType param = new MobileType();
		setSimpleProperties(param);
		param.setBrandEng("nokia");

		Assert.assertNull(param.getId());
		mobileTypeDao.saveOrUpdate(param);
		Long id = param.getId();
		Assert.assertNotNull(id);
		
		MobileType paramTestSave = mobileTypeDao.load(id);
		Assert.assertEquals("test_brandChs", paramTestSave.getBrandChs());
		
		paramTestSave.setBrandChs("test_modify_brand");
		mobileTypeDao.saveOrUpdate(paramTestSave);
		
		MobileType paramTestUpdate = mobileTypeDao.load(id);
		Assert.assertEquals("test_modify_brand", paramTestUpdate.getBrandChs());
		
		mobileTypeDao.remove(id);
		try {
			mobileTypeDao.load(id);
			Assert.fail("not here");
		} catch (Exception e) {
			Assert.assertTrue(true);
		}
	}
	

}



