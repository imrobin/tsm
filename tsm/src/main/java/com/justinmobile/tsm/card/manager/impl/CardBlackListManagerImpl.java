package com.justinmobile.tsm.card.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.card.dao.CardBlackListDao;
import com.justinmobile.tsm.card.domain.CardBlackList;
import com.justinmobile.tsm.card.manager.CardBlackListManager;
import com.justinmobile.tsm.customer.dao.CustomerCardInfoDao;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Service("cardBlackListManager")
public class CardBlackListManagerImpl extends EntityManagerImpl<CardBlackList, CardBlackListDao> implements CardBlackListManager {

	@Autowired
	private CardBlackListDao cardBlackListDao;
	@Autowired
	private CustomerCardInfoDao customerCardInfoDao;
	
	/* (non Javadoc)
	* <p>Title: add</p>
	* <p>Description: </p>
	* @param blackList
	* @param customerCardId
	* @see com.justinmobile.tsm.card.manager.CardBlackListManager#add(com.justinmobile.tsm.card.domain.CardBlackList, java.lang.Long)
	*/
	@Override
	public void addBlackList(CardBlackList blackList) {
		try {
			cardBlackListDao.saveOrUpdate(blackList);
			CustomerCardInfo cci = blackList.getCustomerCardInfo();
			if (null != cci){
				cci.setInBlack(CustomerCardInfo.INBLACK);
				customerCardInfoDao.saveOrUpdate(cci);
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
	* <p>Title: remove</p>
	* <p>Description: </p>
	* @param blackListId
	* @param reason
	* @see com.justinmobile.tsm.card.manager.CardBlackListManager#remove(long, java.lang.String)
	*/
	@Override
	public void removeBlackList(CardBlackList blackList) {
		try {
			cardBlackListDao.saveOrUpdate(blackList);
			CustomerCardInfo   cci = blackList.getCustomerCardInfo();
			if (null != cci){
				cci.setInBlack(CustomerCardInfo.NOT_INBLACK);
				customerCardInfoDao.saveOrUpdate(cci);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
	}}