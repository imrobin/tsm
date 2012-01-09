/**  
 * Filename:    SysUserRetrievePasswordManagerImpl.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-4-27 下午04:22:04  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-27     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.security.manager.impl;


import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.dao.SysUserRetrievePasswordDao;
import com.justinmobile.security.domain.SysUserRetrievePassword;
import com.justinmobile.security.manager.SysUserRetrievePasswordManager;


@Service("sysUserRetrievePasswordManager")
public class SysUserRetrievePasswordManagerImpl extends EntityManagerImpl<SysUserRetrievePassword, SysUserRetrievePasswordDao> implements SysUserRetrievePasswordManager{
	@Autowired
	private SysUserRetrievePasswordDao sysUserRetrievePasswordDao;  
	
	public void addUserRetrivePassword(SysUserRetrievePassword userRP) throws PlatformException {
		try {
			sysUserRetrievePasswordDao.saveOrUpdate(userRP);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
	public SysUserRetrievePassword getUserRPBySignEmail(String checkSign,String email)throws PlatformException{
		try{
			return sysUserRetrievePasswordDao.findUniqueEntity("from SysUserRetrievePassword where checkSign=? and email=?", checkSign,email);
		}catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}



