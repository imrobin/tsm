package com.justinmobile.tsm.transaction.manager.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.transaction.dao.LocalTransactionDao;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;

@Service("localTransactionManager")
public class LocalTransactionManagerImpl extends EntityManagerImpl<LocalTransaction, LocalTransactionDao> implements
		LocalTransactionManager {

	@Autowired
	private LocalTransactionDao localTransactionDao;

	@Override
	public void changeStatus(String ids, String targetStatus) throws PlatformException {
		ids = ids.replaceAll("/", "");
		localTransactionDao.changeStatus(ids, targetStatus);
	}

	@Override
	public Page<LocalTransaction> findTransactionByUser(Page<LocalTransaction> page, Map<String, String> paramMap) throws PlatformException {
		try {
			return localTransactionDao.findTransactionByUser(page, paramMap);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<LocalTransaction> findPage(Page<LocalTransaction> page, String mobileNo,long id) throws PlatformException {
		try {
			//orderBy = orderBy.replace("appName", "name").replace("_", " ");
			String hql = "select lt from " + LocalTransaction.class.getName() + " as lt where lt.task is not null and lt.superTransaction.id is null";
			if (mobileNo!=null && !mobileNo.equals("")){
				hql += " and lt.mobileNo like '%"+mobileNo+"%'";
			}
			if (id != -1){
				hql += " and lt.id="+id;
			}
			if (page.getOrderBy() == null){
				hql += " order by lt.beginTime desc";
			}else{
				hql += " order by lt."+page.getOrderBy()+" "+page.getOrder();
			}
					
			return localTransactionDao.findPage(page, hql);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public LocalTransaction getBySessionId(String sessionId) throws PlatformException {
		try {
			return localTransactionDao.findUniqueByProperty("localSessionId", sessionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<LocalTransaction> getRunningTransByCardNo(String cardNo) throws PlatformException {
		try {
			return localTransactionDao.getRunningTransByCardNo(cardNo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean checkCardOptFinish(String sessionId) {
		try {
			LocalTransaction session = getBySessionId(sessionId);
			if(null != session){
				List<LocalTransaction> sessions = session.getTask().getLocalTransactions();
				for(LocalTransaction lt : sessions){
					if(!lt.isComplete()){
						return false;
					}else{
						if(!lt.getResult().equals(PlatformErrorCode.SUCCESS.getErrorCode())){
							return false;
						}
					}
				}
			}
			return true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<LocalTransaction> findCreateSdLocalTransactionForMobile(Page<LocalTransaction> page, Map<String, Object> paramMap) {
		try {
			return localTransactionDao.findCreateSdLocalTransactionForMobile(page,paramMap);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}