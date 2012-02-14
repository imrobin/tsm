package com.justinmobile.tsm.endpoint.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.HexUtils;
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
	private PushSmsDao pushSmsDao;
	
	@Autowired
	private CustomerCardInfoDao cciDao;
	
	@Autowired
	private CardInfoDao cardInfoDao;
	
	@Autowired
	private ApplicationClientInfoDao aciDao;
	
	@Autowired
	private ApplicationVersionDao appVerDao;
	
	@Autowired
	private OracleSequenceDao oracleSequenceDao;
	
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
			 ps.setVersion(version);
			 ps.setMobileNo(cci.getMobileNo());
			 ps.setOperate(operation);
			 String srcPort = SystemConfigUtils.getPushSrcPort();
			 String destPort = SystemConfigUtils.getPushDestPort();
			 String serial = oracleSequenceDao.getNextSerialNo("PUSHSERIAL", 12);
			 //根据应用版本获取 clientId
			 String osVersion = cci.getMobileType().getOriginalOsKey();
			 ApplicationClientInfo aci = aciDao.getByApplicationVersionTypeVersionFileType(appVer, ApplicationClientInfo.SYS_TYPE_Android, osVersion, ApplicationClientInfo.FILE_TYPE_APK);
			 String clientId = "";
			 if(aci!=null){
			   clientId = String.valueOf(aci.getId());
			   ConvertUtils.long2HexString(aci.getId(), 8);
			 }
			 ps.setSerial(serial);
			 pushSmsDao.saveOrUpdate(ps);
			 smsEndpoint.pushMessage(cci.getMobileNo(), MessageFormat.MSG_FORMAT_TYPE_GBK.getValue(), destPort, srcPort, clientId, cci.getCard().getCardNo(), serial);
			 System.out.println("success");
		 }catch (PlatformException e) {
				throw e;
			} catch (HibernateException e) {
				throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
			} catch (Exception e) {
				throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
			}
		
	}

}
