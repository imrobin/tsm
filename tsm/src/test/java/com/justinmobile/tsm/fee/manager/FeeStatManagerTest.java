
package com.justinmobile.tsm.fee.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.tsm.fee.domain.FeeStat;

import com.justinmobile.tsm.transaction.domain.LocalTransaction;



public class FeeStatManagerTest extends BaseAbstractTest{
	@Autowired
	private FeeStatManager fsManager;
	//@Test
	public void testgetStatRecord()throws Exception{
		long spId = 1721L;
		Integer type = FeeStat.TYPE_FUNCTION;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date start = format.parse("20110901");
		Date end = format.parse("20110930");
		fsManager.getFeeStat(spId,start,end,type);
	}
	//@Test
	public void testGenStatRecord()throws Exception{
		LocalTransaction lt = new LocalTransaction();
		lt.setAid("5061636B616765322E4170703100");
		lt.setAppVersion("1.1.0");
		lt.setCardNo("084906206057526806");
		lt.setProcedureName("DOWNLOAD_APP");
		lt.setMobileNo("13467502425");
		lt.setLocalSessionId("333333333");
		fsManager.genStatRecord(lt);
	}
	//@Test
	public void testTask()throws Exception{
		fsManager.genFeeStatTask();
	}
	//@Test
	public void testPerset()throws Exception{
		fsManager.genPerStatRecord("13618087882","08D561000000010006");
		//fsManager.genPerStatRecord("13013513600","084906206057526806");
		//fsManager.genPerStatRecord("13501298756","084906206057526822");
	}
	//@Test
	public void testCount()throws Exception{
		Long spId = 1721L;
		//SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String start = "20110901";
		String end = "20110930";
		Long count = fsManager.getCounthasBilled(spId, start, end);
		System.out.println(count);
	}
	//@Test
	public void testFloor()throws Exception{
		double size = 52198;
		System.out.println((int)Math.ceil(size/1024));
		System.out.println((int)Math.ceil(0.0));
		System.out.println((int)Math.ceil(0.9));
	}
	//@Test
	public void testFunctionBilled()throws Exception{
		Long spId = 1721L;
		String start = "20110901";
		String end = "20110930";
		List<FeeStat> list = fsManager.getFunctionBilled(spId, start, end);
		System.out.println(list.size());
	}
	@Test
	public void testSubscribeApp() throws Exception{
		String mobileNo="13467502425";
		String cardNo="SE0001";
		String aid = "5061636B616765332E4170703100";
		fsManager.subscribeAppStatRecord(aid, cardNo, mobileNo);
		
	}

}



