package com.justinmobile.tsm.endpoint.sms;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.endpoint.manager.PushSmsManager;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;

public class PushSmsManagerTest extends BaseAbstractTest{
	
	@Autowired
	private PushSmsManager psManager;
	
	@Autowired
	private OracleSequenceDao oracleSequenceDao;  
	
	//@Test
	public void testPushSms(){
		
		psManager.sendPushSms("12000004000000100006", "D1560000000000000000000000000003", "1.0.0", Operation.CREATE_SD);
	}
	@Test
	public void serial(){
		 String serial = oracleSequenceDao.getNextSerialNo("PUSHSERIAL", 12);
		 System.out.println(serial);
	}
	

}
