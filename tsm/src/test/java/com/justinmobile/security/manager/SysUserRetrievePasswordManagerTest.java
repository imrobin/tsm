/**  
 * Filename:    SysUserRetrievePasswordManagerTest.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-5-10 上午09:21:46  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-5-10     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.security.manager;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.justinmobile.core.test.BaseAbstractTest;
import com.justinmobile.security.domain.SysUserRetrievePassword;



public class SysUserRetrievePasswordManagerTest extends BaseAbstractTest{
	@Autowired
	private SysUserRetrievePasswordManager sysUserRetrievePasswordManager;
	@Test
	public void testGetRetrievePassword()throws Exception{
		SysUserRetrievePassword sysUserRetrievePassword = sysUserRetrievePasswordManager.getUserRPBySignEmail("a98c2e59b16b518a81c77ba66cbdecda","280166679@qq.com");
		System.out.println(sysUserRetrievePassword);
	}

}



