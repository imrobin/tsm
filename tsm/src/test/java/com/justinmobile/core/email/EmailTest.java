/**  
 * Filename:    EmailTest.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-4-28 下午05:05:59  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-28     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.core.email;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.justinmobile.security.domain.SysUser;

public class EmailTest{
	private VelocityMailSupport velocityMailSupport;

	@Test
	public void testSend() throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"spring/applicationContext-*.xml");
		velocityMailSupport = (VelocityMailSupport) ctx
				.getBean("findPasswordMailSupport");
		SysUser user = new SysUser();
		user.setEmail("280166679@qq.com");
		user.setUserName("郝静华");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", user);
		System.out.println(velocityMailSupport.getMailSender());
		System.out.println(velocityMailSupport.getTemplateName()
				+ velocityMailSupport.getSubject());
		velocityMailSupport
				.sendMime(user.getUserName(), user.getEmail(), model);
		System.out.println("test ok");
	}

}




