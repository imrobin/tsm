/**  
 * Filename:    SysUserRetrievePasswordDaoHibernate.java  
 * Description:   
 * Copyright:   Copyright (c)2010  
 * Company:     justinmobile
 * @author:     jinghua  
 * @version:    1.0  
 * Create at:   2011-4-27 下午04:11:38  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2011-4-27     jinghua.hao             1.0        1.0 Version  
 */  


package com.justinmobile.security.dao.hibernate;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.security.dao.SysUserRetrievePasswordDao;
import com.justinmobile.security.domain.SysUserRetrievePassword;


@Repository("sysUserRetrievePasswordDao")
public class SysUserRetrievePasswordDaoHibernate extends EntityDaoHibernate<SysUserRetrievePassword, Long> implements SysUserRetrievePasswordDao{
	 
}



