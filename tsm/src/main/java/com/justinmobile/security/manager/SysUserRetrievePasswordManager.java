/**  
 * Filename:    SysUserRetrievePasswordManager.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-4-27 下午04:19:14  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-27     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.security.manager;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.security.domain.SysUserRetrievePassword;


@Transactional
public interface SysUserRetrievePasswordManager extends EntityManager<SysUserRetrievePassword>{
	public void addUserRetrivePassword(SysUserRetrievePassword userRP) throws PlatformException;
	public SysUserRetrievePassword getUserRPBySignEmail(String checkSign,String email)throws PlatformException;
}

