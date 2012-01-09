package com.justinmobile.tsm.card.manager.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardApplicationDao;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.DesiredOperationManager;

@Service("cardApplicationManager")
public class CardApplicationManagerImpl extends EntityManagerImpl<CardApplication, CardApplicationDao> implements CardApplicationManager {

	@Autowired
	private CardApplicationDao cardApplicationDao;

	@Autowired
	private DesiredOperationManager desiredOperationManager;

	@Autowired
	private CustomerCardInfoManager customerCardManager;

	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;

	@Override
	public CardApplication getByCardNoAid(String cardNo, String aid) {
		try {
			return cardApplicationDao.getByCardNoAid(cardNo, aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplication> getCardAppBySd(long sdId) {
		try {
			return cardApplicationDao.getCardAppBySd(sdId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplication> getByCardAndApplicationSd(CardInfo card, SecurityDomain sd) {
		try {
			return cardApplicationDao.getByCardAndApplicationSd(card, sd);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardApplication getByCardAndAppver(CardInfo card, ApplicationVersion applicationVersion) {
		try {
			return cardApplicationDao.getByCardAndAppver(card, applicationVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplication> getCardAppByCard(CardInfo card) {
		try {
			return cardApplicationDao.findByProperty("cardInfo", card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void emigrate(Application application, CardInfo card) {
		try {
			CardApplication cardApplication = cardApplicationDao.getByCardNoAid(card.getCardNo(), application.getAid());

			if (null == cardApplication) {// 如果无卡上应用的记录，说明应用未订购，抛出异常
				throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
			}

			CardBaseApplication cardBaseApplication = cardBaseApplicationManager.getByCardBaseAndApplicationVersion(card.getCardBaseInfo(),
					cardApplication.getApplicationVersion());

			// if (CardBaseApplication.MODE_EMPTY ==
			// cardBaseApplication.getPresetMode().intValue()) {// 如果发行模式是“空卡”
			// if ((Application.DELETE_RULE_DELETE_ALL ==
			// application.getDeleteRule().intValue())
			// && (CardApplication.STATUS_UNDOWNLOAD.intValue() ==
			// cardApplication.getStatus().intValue())) {//
			// 如果应用的删除规则是“全部删除”且卡上应用状态是“未下载”，说明应用未订购，抛出异常
			// throw new
			// PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
			// } else if ((Application.DELETE_RULE_DELETE_DATA_ONLY ==
			// application.getDeleteRule().intValue())
			// && (CardApplication.STATUS_DOWNLOADED.intValue() ==
			// cardApplication.getStatus().intValue())) {//
			// 如果应用的删除规则是“只删除数据”且卡上应用状态是“已下载”，说明应用未订购，抛出异常
			// throw new
			// PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
			// }
			// } else if ((CardBaseApplication.MODE_CREATE ==
			// cardBaseApplication.getPresetMode().intValue())//
			// 如果发行模式是“实例创建”且卡上应用状态是“已下载”，说明应用未订购，抛出异常
			// && (CardApplication.STATUS_DOWNLOADED.intValue() ==
			// cardApplication.getStatus().intValue())) {
			// throw new
			// PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
			// } else if ((CardBaseApplication.MODE_PERSONAL ==
			// cardBaseApplication.getPresetMode().intValue())//
			// 如果发行模式是“个人化”且卡上应用状态是“已安装”，说明应用未订购，抛出异常
			// && (CardApplication.STATUS_INSTALLED.intValue() ==
			// cardApplication.getStatus().intValue())) {
			// throw new
			// PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
			// }
			//
			// if (CardApplication.STATUS_AVAILABLE.intValue() !=
			// cardApplication.getStatus().intValue()) {//
			// 在其他情况下，如果卡上应用状态不是“可用”，抛出异常
			// throw new
			// PlatformException(PlatformErrorCode.CARD_APPLICATION_ERROR_STATUS);
			// }

			if (cardApplication.getStatus() == CardApplication.STATUS_UNDOWNLOAD.intValue()) {
				if (CardBaseApplication.MODE_EMPTY == cardBaseApplication.getPresetMode().intValue()) {
					throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
				} else if (CardBaseApplication.MODE_EMPTY != cardBaseApplication.getPresetMode().intValue()) {
					throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_ERROR_STATUS);
				}
			} else if (cardApplication.getStatus() == CardApplication.STATUS_DOWNLOADED.intValue()) {
				if (CardBaseApplication.MODE_EMPTY == cardBaseApplication.getPresetMode().intValue()) {
					if (Application.DELETE_RULE_DELETE_DATA_ONLY == application.getDeleteRule().intValue()) {
						throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
					} else {
						throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_ERROR_STATUS);
					}
				} else if (CardBaseApplication.MODE_CREATE == cardBaseApplication.getPresetMode().intValue()) {
					throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_ERROR_STATUS);
				}
			} else if (cardApplication.getStatus() == CardApplication.STATUS_INSTALLED.intValue()) {
				if (CardBaseApplication.MODE_EMPTY == cardBaseApplication.getPresetMode().intValue()) {
					throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_ERROR_STATUS);
				} else if (CardBaseApplication.MODE_CREATE == cardBaseApplication.getPresetMode().intValue()) {
					throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_ERROR_STATUS);
				} else {
					throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_NOT_DOWNLOADED);
				}
			} else if (cardApplication.getStatus() == CardApplication.STATUS_DOWNING.intValue()
					|| cardApplication.getStatus() == CardApplication.STATUS_DELETEING.intValue()
					|| cardApplication.getStatus() == CardApplication.STATUS_LOCKED.intValue()) {
				throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_ERROR_STATUS);
			}

			if (null != cardApplication.getMigratable() && cardApplication.getMigratable()) {
				throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_EMIRATED);
			}

			// 检查被迁出终端的绑定关系
			CustomerCardInfo customerCard = customerCardManager.getByCardThatStatusNormalOrLost(card);
			if (null == customerCard || !CustomerCardInfo.STATUS_EMIGRATABLE.contains(customerCard.getStatus())) {// 如果绑定关系不存在，或绑定关系状态不在“可迁出”的状态集合，抛出异常
				throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
			} else if (CustomerCardInfo.STATUS_NORMAL == customerCard.getStatus().intValue()
					&& CustomerCardInfo.INBLACK == customerCard.getInBlack().intValue()) {// 如果绑定关系状态为“正常”但在黑名单，抛出异常
				throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
			}

			cardApplication.setMigratable(Boolean.TRUE);
			cardApplicationDao.saveOrUpdate(cardApplication);

			// 添加预定的强制删除操作
			DesiredOperation currentOperation = desiredOperationManager.getByCustomerCardIdAndAidAndOperationAndStatuts(customerCard,
					application.getAid(), Operation.EMIGRATE_APP, DesiredOperation.NOT_EXCUTED);
			if (null != currentOperation) {
				throw new PlatformException(PlatformErrorCode.CARD_APPLICATION_EMIRATED);
			}

			DesiredOperation desiredOperation = new DesiredOperation();
			desiredOperation.setAid(application.getAid());
			desiredOperation.setCustomerCardId(customerCard.getId());
			desiredOperation.setPreProcess(DesiredOperation.PREPROCESS_TURE);
			desiredOperation.setProcedureName(Operation.EMIGRATE_APP.name());

			desiredOperationManager.saveOrUpdate(desiredOperation);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public Page<CardApplication> findPageByCustomer(Page<CardApplication> page, Map<String, Object> queryParams) throws PlatformException {

		try {
			page = cardApplicationDao.findPageByCustomer(page, queryParams);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return page;
	}

	@Override
	public List<CardApplication> getCaListNotDelAndNotMigratable(CardInfo card) {
		try {
			return cardApplicationDao.getCaListNotDelAndNotMigratable(card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplication> getCaListMigratable(CardInfo card) {
		try {
			return cardApplicationDao.getCaListMigratable(card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CardApplication getAvailbleOrLockedByCardNoAid(String cardNo, String aid) {
		try {
			return cardApplicationDao.getAvailbleOrLockedByCardNoAid(cardNo, aid);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Map<String, Object>> findByCustomer(Page<Map<String, Object>> page, String mobileNo) {

		try {
			return cardApplicationDao.findByCustomer(page, mobileNo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isPreset(CardInfo card, Application application) {
		try {
			return null != cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(card.getCardBaseInfo(), application);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplication> getByCardAndStatus(CardInfo card, int status) {
		try {
			return cardApplicationDao.getByCardAndStatus(card, status);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplication> getForLostListByCardInfo(CardInfo card) {
		try {
			return cardApplicationDao.getForLostListByCardInfo(card);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}