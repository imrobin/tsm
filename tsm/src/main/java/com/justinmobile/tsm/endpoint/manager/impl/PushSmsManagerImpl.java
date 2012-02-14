package com.justinmobile.tsm.endpoint.manager.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationClientInfoDao;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.dao.CustomerCardInfoDao;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.endpoint.dao.PushSmsDao;
import com.justinmobile.tsm.endpoint.domain.PushSms;
import com.justinmobile.tsm.endpoint.manager.PushSmsManager;
import com.justinmobile.tsm.endpoint.sms.SmsEndpoint;
import com.justinmobile.tsm.endpoint.sms.message.MessageFormat;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@Service("pushSmsManager")
public class PushSmsManagerImpl extends EntityManagerImpl<PushSms, PushSmsDao> implements PushSmsManager {

	@Autowired
	PushSmsDao pushSmsDao;
	
	@Autowired
	CustomerCardInfoDao cciDao;
	
	@Autowired
	CardInfoDao cardInfoDao;
	
	@Autowired
	ApplicationClientInfoDao aciDao;
	
	@Autowired
	ApplicationVersionDao appVerDao;
	
	@Autowired
	private SmsEndpoint smsEndpoint;

	@Override
	public PushSms getByPushSerial(String pushSerial) {
		try {
			return pushSmsDao.getByPushSerial(pushSerial);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void sendPushSms(String cardNo, String aid,String version,
			Operation operation) {
		 try{
			 CardInfo card = cardInfoDao.getByCardNo(cardNo);
			 CustomerCardInfo cci = cciDao.getByCard(card);
			 ApplicationVersion appVer = appVerDao.getAidAndVersionNo(aid, version);
			 PushSms ps = new PushSms();
			 ps.setAid(aid);
			 ps.setCardNo(cardNo);
			 ps.setMobileNo(cci.getMobileNo());
			 ps.setOperation(operation);
			 String srcPort = SystemConfigUtils.getPushSrcPort();
			 String destPort = SystemConfigUtils.getPushDestPort();
			 String serial = RandomStringUtils.randomNumeric(12);
			 //根据应用版本获取 clientId
			 String osVersion = cci.getMobileType().getOriginalOsKey();
			 ApplicationClientInfo aci = aciDao.getByApplicationVersionTypeVersionFileType(appVer, ApplicationClientInfo.SYS_TYPE_Android, osVersion, ApplicationClientInfo.FILE_TYPE_APK);
			 String clientId = String.valueOf(aci.getId());
			 ps.setSerial(serial);
			 pushSmsDao.saveOrUpdate(ps);
			 smsEndpoint.pushMessage(cci.getMobileNo(), MessageFormat.MSG_FORMAT_TYPE_GBK.getValue(), destPort, srcPort, clientId, cci.getCard().getCardNo(), serial);
			
		 }catch (PlatformException e) {
				throw e;
			} catch (HibernateException e) {
				throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
			} catch (Exception e) {
				throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
			}
		
	}

}
