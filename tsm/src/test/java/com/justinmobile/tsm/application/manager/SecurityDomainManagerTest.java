package com.justinmobile.tsm.application.manager;

import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.util.Assert;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.application.dao.AppletDao;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.SecurityDomainApplyDao;
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;
import com.justinmobile.tsm.application.utils.SecurityDomainUtils;

@TransactionConfiguration(defaultRollback = true)
public class SecurityDomainManagerTest extends BaseAbstractTest {

	private SecurityDomain isd;

	private SecurityDomain dap;

	 @Autowired
	 private SecurityDomainManager sdManager;

	@Autowired
	private SecurityDomainDao sdDao;

	@Autowired
	private SecurityDomainApplyDao sdApplyDao;
	
	@Autowired
	private ApplicationDao appDao;
	
	@Autowired
	private AppletDao appletDap;
	
	//@Before
	public void before() {
		isd = SecurityDomainUtils.createDefult("00");
		dap = SecurityDomainUtils.createDefult("11");

		dap.setModel(SecurityDomain.MODEL_DAP);
		dap.getAid();

		sdDao.saveOrUpdate(isd);
		sdDao.saveOrUpdate(dap);
	}

	//@After
	public void after() {
		isd = null;
		dap = null;
	}

	@Test
	public void testValidateSecurityDomainAid() {
		final String propertyName = "aid";
		String newValue = "";
		String orgValue = "";
		int index = 0;
		int seed = 1;

		boolean testAidBlank = sdManager.isPropertyUnique(propertyName, newValue, orgValue);
		Assert.isTrue(testAidBlank);
		//sd
		SecurityDomain sd = null;
		List<SecurityDomain> sdList = sdDao.getAll();
		if(sdList != null && !sdList.isEmpty()) {
			seed = sdList.size();
			index = new Random().nextInt(seed);
			sd = sdList.get(index);
		}
		if(sd != null) {
			newValue = sd.getAid() + "FF";
		}
		boolean sdAidUnique = sdManager.isPropertyUnique(propertyName, newValue, orgValue);
		Assert.isTrue(sdAidUnique);
		if(sd != null) newValue = sd.getAid();
		sdAidUnique = sdManager.isPropertyUnique(propertyName, newValue, orgValue);
		Assert.isTrue(!sdAidUnique);
		//sd apply
		SecurityDomainApply sdApply = null;
		List<SecurityDomainApply> sdApplyList = sdApplyDao.getAll();
		if(sdApplyList != null && !sdApplyList.isEmpty()) {
			seed = sdApplyList.size();
			index = new Random().nextInt(seed);
			sdApply = sdApplyList.get(index);
		}
		if(sdApply != null) {
			newValue = sdApply.getAid() + "FF";
		}
		boolean sdApplyAidUnique = sdManager.isPropertyUnique(propertyName, newValue, orgValue);
		Assert.isTrue(sdApplyAidUnique);
		if(sdApply != null) newValue = sdApply.getAid();
		sdApplyAidUnique = sdManager.isPropertyUnique(propertyName, newValue, orgValue);
		Assert.isTrue(!sdApplyAidUnique);
		//application
		//applet
	}
}
