package com.justinmobile.tsm.application.manager.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.tsm.application.dao.ApplicationIdentifierDao;
import com.justinmobile.tsm.application.domain.ApplicationIdentifier;
import com.justinmobile.tsm.application.manager.ApplicationIdentifierManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;

@Service("aidManager")
public class ApplicationIdentifierManagerImpl extends EntityManagerImpl<ApplicationIdentifier, ApplicationIdentifierDao> implements ApplicationIdentifierManager {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationIdentifierManagerImpl.class);
	
	@Autowired
	private OracleSequenceDao seqDao;
	
	@Autowired
	private SpBaseInfoManager spManager;
	
	@Autowired
	private ApplicationIdentifierDao aidDao;
	
	@Override
	public void saveAid(ApplicationIdentifier aid) throws PlatformException {
		try {
			SpBaseInfo sp = spManager.load(aid.getSp().getId());
			final Calendar assignmentTime = Calendar.getInstance();
			final int type = aid.getType();
			String rid = sp.getRid();
			
			String D1 = "";
			String D2D3 = "";
			String D4D5 = "";
			String D6D14 = "";
			String D15D22 = "00000000";
			
			switch (type) {
			case ApplicationIdentifier.TYPE_SD :
				D1 = "0";
				D2D3 = aid.getBelongto() == 0 ? "00" : ConvertUtils.int2HexString(aid.getBelongto(), 2);
				D4D5 = "00";
				break;
			case ApplicationIdentifier.TYPE_APP : 
				D1 = "8";
				D2D3 = aid.getAppType() == 0 ? "00" : ConvertUtils.int2HexString(aid.getAppType(), 2);
				D4D5 = aid.getIndustry() == 0 ? "00" : ConvertUtils.int2HexString(aid.getIndustry(), 2);
				break;
			default:
				break;
			}
			logger.debug("D1~D5:" + D1 + D2D3 + D4D5);
			
			List<ApplicationIdentifier> list = new ArrayList<ApplicationIdentifier>(aid.getSize());
			for(int index = 0; index < aid.getSize(); index++) {
				ApplicationIdentifier _aid = (ApplicationIdentifier)BeanUtils.cloneBean(aid);
				_aid.setAssignmentTime(assignmentTime);
				_aid.setSp(sp);

				switch (type) {
				case ApplicationIdentifier.TYPE_SD :
					String seqSd = seqDao.getSequence(ApplicationIdentifier.SEQ_SD);
					//杯具：规范文档中的流水号是9位，int2HexString只能转偶数位的，只有转成8位再加0，补成9位
					D6D14 = "0" + ConvertUtils.int2HexString(Integer.parseInt(seqSd), 8);
					
					break;
				case ApplicationIdentifier.TYPE_APP : 
					String seqApp = seqDao.getSequence(ApplicationIdentifier.SEQ_APP);
					D6D14 = "0" + ConvertUtils.int2HexString(Integer.parseInt(seqApp), 8);
					break;
				default:
					break;
				}
				
				String AID = rid + D1 + D2D3 + D4D5 + D6D14 + D15D22;
				_aid.setAid(AID);
				list.add(_aid);

				logger.debug(AID);
			}
			
			aidDao.saveApplicationIdentifiers(list);
			
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	
}
