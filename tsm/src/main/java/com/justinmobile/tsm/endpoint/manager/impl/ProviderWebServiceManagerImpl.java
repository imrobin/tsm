package com.justinmobile.tsm.endpoint.manager.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.cms2ac.domain.ProviderProcess;
import com.justinmobile.tsm.cms2ac.domain.ProviderProcess.BusinessEvent;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.endpoint.manager.ProviderWebServiceManager;
import com.justinmobile.tsm.fee.manager.FeeStatManager;
import com.justinmobile.tsm.history.manager.SubscribeHistoryManager;

@Service("providerWebServiceManager")
public class ProviderWebServiceManagerImpl implements ProviderWebServiceManager {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ProviderWebServiceManagerImpl.class);

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private FeeStatManager feeStatManager;

	@Autowired
	private SubscribeHistoryManager subscribeHistoryManager;

	@Autowired
	private CustomerCardInfoManager customerCardManager;

	@Override
	public void businessEventNotify(ProviderProcess process) {
		try {
			BusinessEvent businessEvent = BusinessEvent.valueOf(process.getEventId());

			switch (businessEvent) {
			// case CANCEL_SERVICE:
			//
			// break;
			case UNSUBSCRIBE_BUSINESS:
				unsubcribeBusiness(process.getAppAid(), process.getSeId(), process.getMsisdn());
				break;
			case SUBSCRIBE_BUSINESS:
				subcribeBusiness(process.getAppAid(), process.getSeId(), process.getMsisdn());
				break;
			default:
				break;
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	private void unsubcribeBusiness(String aid, String cardNo, String mobileNo) {
		CustomerCardInfo checkCustomerCard = customerCardManager.getByCardNoThatNormalOrLosted(cardNo);
		if(null != checkCustomerCard) {
			CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, aid);
			if (null != cardApplication
					&& ((CardApplication.STATUS_AVAILABLE.intValue() == cardApplication.getStatus().intValue()) || (CardApplication.STATUS_LOSTED
							.intValue() == cardApplication.getStatus().intValue()))) {
				cardApplication.setStatus(CardApplication.STATUS_INSTALLED);
				feeStatManager.unSubscribeAppStatRecord(aid, cardNo, mobileNo);
				subscribeHistoryManager.unsubscribeApplication(cardApplication.getCardInfo(), cardApplication.getApplicationVersion());

				// 判断终端是否已经挂失
				CustomerCardInfo customerCard = customerCardManager.getByCardNoThatStatusLost(cardNo);
				if (null != customerCard) {// 如果绑定记录不为空，说明终端已经挂失
					List<CardApplication> cardApplications = cardApplicationManager.getByCardAndStatus(customerCard.getCard(),
							CardApplication.STATUS_LOSTED);
					if (CollectionUtils.isEmpty(cardApplications)) {// 如果卡上没有状态为“已挂失”的应用，将绑定关系重置为能够绑定的状态
						customerCard.resetStatusToBindable();
						customerCardManager.sysnLostToCancel(customerCard);
					}
				}
			} else {
				throw new PlatformException(PlatformErrorCode.CARD_APP_ERROR_STATUS);
			}
		}
	}

	private void subcribeBusiness(String aid, String cardNo, String mobileNo) {
		CardApplication cardApplication = cardApplicationManager.getByCardNoAid(cardNo, aid);
		if (null != cardApplication && CardApplication.STATUS_PERSONALIZED.intValue() == cardApplication.getStatus().intValue()) {
			cardApplication.setStatus(CardApplication.STATUS_AVAILABLE);
			feeStatManager.subscribeAppStatRecord(aid, cardNo, mobileNo);
			subscribeHistoryManager.subscribeApplication(cardApplication.getCardInfo(), cardApplication.getApplicationVersion());
		} else {
			throw new PlatformException(PlatformErrorCode.CARD_APP_ERROR_STATUS);
		}
	}
}
