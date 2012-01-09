package com.justinmobile.tsm.application.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationStyle;
import com.justinmobile.tsm.application.manager.ApplicationStyleManager;

public class ApplicationStyleDaoTest extends BaseAbstractTest {
	
	@Autowired
	private ApplicationStyleDao dao;
	
	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private ApplicationStyleManager manager;
	
	@Test
	public void testSave() {
		List<Application> list = this.applicationDao.getAll();
		Assert.assertNotNull(list);
		Assert.assertFalse(list.isEmpty());
		Application app = list.get(0);
		
		ApplicationStyle style = new ApplicationStyle();
		style.setApplication(app);
		style.setStyleUrl("test");
		
		this.dao.saveOrUpdate(style);
		
		Assert.assertNotNull(style.getId());
		
	}
	
	@Test
	public void testSaveForManager() {
		List<Application> list = this.applicationDao.getAll();
		Assert.assertNotNull(list);
		Assert.assertFalse(list.isEmpty());
		Application app = list.get(0);
		
		ApplicationStyle style = new ApplicationStyle();
		style.setApplication(app);
		style.setStyleUrl("test");
		
		this.manager.saveOrUpdate(style);
		
		Assert.assertNotNull(style.getId());
	}
}
