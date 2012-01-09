 

package com.justinmobile.tsm.fee.manager;

import java.util.List;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.core.utils.web.KeyLongValue;
import com.justinmobile.core.utils.web.KeyValue;



import com.justinmobile.tsm.fee.dao.FeeRuleFunctionDao;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;

import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;



public class FeeRuleFunctionManagerTest extends BaseAbstractTest{
	@Autowired
	private FeeRuleFunctionManager frfpManager;
	@Autowired
	private SpBaseInfoManager spManager;
	@Autowired
	private FeeRuleFunctionDao frfDao;
	/*@Autowired
	private ApplicationVersionManager appVerManager;*/
	//private static String destFileName = "FeeFunction_output.xls";
	//@Test
	public void testAdd()throws Exception{
		SpBaseInfo sp = spManager.load(new Long(1943));
		//ApplicationVersion appVer = appVerManager.load(new Long(853));
		FeeRuleFunction frfp = new FeeRuleFunction();
	    frfp.setSp(sp);
		frfpManager.saveOrUpdate(frfp);
	}
	//@Test
	public void testGetSpHasApp()throws Exception{
		List<KeyValue> list = frfpManager.getSpNameHasApp();
		System.out.println(list.size());
	}
	//@Test
	public void testGetAppVerBySp()throws Exception{
		List<KeyValue> list = frfpManager.getAppNameBySp(new Long(1721));
		System.out.println(list.size());
	}
	//@Test
	public void getTransByAidAndVersion() throws Exception{
		String aid = "D1560001010001600000000100000002";
		List<KeyLongValue> list = frfpManager.getTransByAidAndVersion(aid, null, null, null);
		System.out.println(list.get(0).getKey());
	}
	@Test
	public void testGetMonth() throws Exception{
		frfDao.getMonthFrpBySpAndSize(651L, 2L);
		
	}

}



