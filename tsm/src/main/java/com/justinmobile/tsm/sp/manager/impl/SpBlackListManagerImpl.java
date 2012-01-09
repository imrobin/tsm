package com.justinmobile.tsm.sp.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.dao.SysUserDao;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.dao.SpBlackListDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.domain.SpBlackList;
import com.justinmobile.tsm.sp.manager.SpBlackListManager;

@Service("spBlackListManager")
public class SpBlackListManagerImpl extends EntityManagerImpl<SpBlackList, SpBlackListDao> implements SpBlackListManager {

	@Autowired
	private SpBlackListDao spBlackListDao;
	@Autowired
	private SpBaseInfoDao spBaseInfoDao;
	@Autowired
	private SysUserDao sysUserDao;

	/* (non Javadoc)
	* <p>Title: addBlackList</p>
	* <p>Description: </p>
	* @param blackList
	* @see com.justinmobile.tsm.sp.manager.SpBlackListManager#addBlackList(com.justinmobile.tsm.sp.domain.SpBlackList)
	*/
	@Override
	public void addBlackList(SpBlackList blackList) {
		try {
			spBlackListDao.saveOrUpdate(blackList);
			 SpBaseInfo sp = blackList.getSp();
			if (null != sp){
				sp.setInBlack(CustomerCardInfo.INBLACK);
				spBaseInfoDao.saveOrUpdate(sp);
				SysUser user = sp.getSysUser();
				user.setStatus(0);//无效
				sysUserDao.saveOrUpdate(user);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
	}

	/* (non Javadoc)
	* <p>Title: removeBlackList</p>
	* <p>Description: </p>
	* @param blackList
	* @see com.justinmobile.tsm.sp.manager.SpBlackListManager#removeBlackList(com.justinmobile.tsm.sp.domain.SpBlackList)
	*/
	@Override
	public void removeBlackList(SpBlackList blackList) {
		try {
			spBlackListDao.saveOrUpdate(blackList);
			SpBaseInfo sp = blackList.getSp();
			if (null != sp){
				sp.setInBlack(CustomerCardInfo.NOT_INBLACK);
				spBaseInfoDao.saveOrUpdate(sp);
				SysUser user = sp.getSysUser();
				user.setStatus(1);//有效
				sysUserDao.saveOrUpdate(user);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}}