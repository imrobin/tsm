package com.justinmobile.tsm.sp.web;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import com.justinmobile.core.test.BaseAbstractTest;

public class SpBaseInfoControllerTest extends BaseAbstractTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerAdapter handlerAdapter;
    
    private SpBaseInfoController controller;
    
    @BeforeTransaction
    public void setup() {
    	request = new MockHttpServletRequest();
    	response = new MockHttpServletResponse();
    	handlerAdapter = applicationContext.getBean(HandlerAdapter.class);
    	controller = applicationContext.getBean(SpBaseInfoController.class);
    	
    	executeSqlScript("file:src/test/java/com/justinmobile/tsm/sp/test_script.sql", false);
    }
    
    
    @AfterTransaction
    public void teardown() throws Exception {
    	executeSqlScript("file:src/test/java/com/justinmobile/tsm/sp/clear_script.sql", false);
    }
    
//    @Test
//    public void testSpRegister() throws Exception {
//    	final String requestURI = "/spBaseInfo/";
//    	request.setMethod("POST");
//    	request.setRequestURI(requestURI);
//    	request.addParameter("m", "spRegister");
//    	
//    	ModelAndView test = handlerAdapter.handle(request, response, controller);
//    	Assert.assertNotNull(test);
//    	
//    	logger.debug("\n\n"+test+"\n\n");
//    }
    
    @Test
    public void testCheckName() throws Exception {
    	final String name = "test_name_new";
		
    	final String requestURI = "/spBaseInfo/";
    	request.setRequestURI(requestURI);
    	request.addParameter("m", "checkName");
    	request.addParameter("name", name);
    	request.addParameter("type", "full");
    	
    	ModelAndView test = handlerAdapter.handle(request, response, controller);
    	Assert.assertNull(test);
    	logger.debug("\n\n"+test+"\n\n");
    	
    }
}
