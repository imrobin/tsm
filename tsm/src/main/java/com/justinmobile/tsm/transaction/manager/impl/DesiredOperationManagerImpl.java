package com.justinmobile.tsm.transaction.manager.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.transaction.dao.DesiredOperationDao;
import com.justinmobile.tsm.transaction.dao.LocalTransactionDao;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.manager.DesiredOperationManager;

@Service("desiredOperationManager")
public class DesiredOperationManagerImpl extends EntityManagerImpl<DesiredOperation, DesiredOperationDao> implements
		DesiredOperationManager {

	@Autowired
	private DesiredOperationDao desiredOperationDao;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private LocalTransactionDao localTransactionDao;

	@Override
	public Page<DesiredOperation> findPageByParam(Page<DesiredOperation> page, Map<String, String> paramMap) {
		try {
			List<CustomerCardInfo> customerCardInfos = customerCardInfoManager.getCustomerCardByCustomerName(
					paramMap.get("currentUserName"), null);
			StringBuffer cardInfos = new StringBuffer("-1,");
			for (int i = 0; i < customerCardInfos.size(); i++) {
				cardInfos.append(customerCardInfos.get(i).getId() + ",");
			}
			paramMap.put("customerCardId", cardInfos.toString().substring(0, cardInfos.toString().length() - 1));
			return desiredOperationDao.findPageByParam(page, paramMap);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public DesiredOperation createDO(String aid, String opttype, String userName, String ccid, String cardNo) {
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			CustomerCardInfo cci = null;
			if (LocalTransaction.Operation.DOWNLOAD_APP.getType() == Integer.valueOf(opttype).intValue()) {
				DesiredOperation desiredOperation = desiredOperationDao.getDObyUserAidOptStatus(ccid, aid,
						LocalTransaction.Operation.valueOf(Integer.valueOf(opttype)), DesiredOperation.NOT_EXCUTED);
				if (null != desiredOperation) {
					throw new PlatformException(PlatformErrorCode.DO_IS_EXIST);
				}
			} else {
				if (StringUtils.isNotBlank(cardNo)) {
					cci = customerCardInfoManager.getByCardNo(cardNo);
				} else {
					if (StringUtils.isNotBlank(ccid)) {
						cci = customerCardInfoManager.load(Long.valueOf(ccid));
					} else {
						throw new PlatformException(PlatformErrorCode.CCI_IS_NOT_EXIST);
					}
				}
				DesiredOperation desiredOperation = desiredOperationDao.getDObyCCIandAidOptStatus(cci, aid,
						LocalTransaction.Operation.valueOf(Integer.valueOf(opttype)), DesiredOperation.NOT_EXCUTED);
				if (null != desiredOperation) {
					throw new PlatformException(PlatformErrorCode.DO_IS_EXIST);
				}
			}
			DesiredOperation desiredOperation = new DesiredOperation();
			desiredOperation.setAid(aid);
			desiredOperation.setCustomer(customer);
			if (StringUtils.isNotBlank(ccid)) {
				desiredOperation.setCustomerCardId(Long.valueOf(ccid));
			}
			if (null != cci) {
				desiredOperation.setCustomerCardId(cci.getId());
			}
			desiredOperation.setProcedureName(LocalTransaction.Operation.valueOf(Integer.valueOf(opttype)));
			desiredOperation.setIsExcuted(DesiredOperation.NOT_EXCUTED);
			desiredOperation.setIsPrompt(DesiredOperation.NOT_PROMPTED);
			desiredOperation.setPreProcess(DesiredOperation.PREPROCESS_FALSE);
			desiredOperationDao.saveOrUpdate(desiredOperation);
			return desiredOperation;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<DesiredOperation> findPageByCustomerParam(Page<DesiredOperation> page, String currentUserName, String executionStatus) {
		try {
			Customer customer = customerManager.getCustomerByUserName(currentUserName);
			if (customer == null) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			return desiredOperationDao.findPageByCustomerParam(page, executionStatus, customer);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void change(Long doId, String sessionId, int flag, String result, String cardNo) {
		try {
			DesiredOperation doInfo = desiredOperationDao.load(doId);
			LocalTransaction lt = localTransactionDao.findUniqueByProperty("localSessionId", sessionId);
			CustomerCardInfo cci = customerCardInfoManager.getByCardNoCancelAndReplaced(cardNo);
			doInfo.setTaskId(lt.getTask().getId());
			doInfo.setSessionId(sessionId);
			doInfo.setIsExcuted(flag);
			doInfo.setCustomerCardId(cci.getId());
			doInfo.setIsPrompt(DesiredOperation.PROMPTED);
			doInfo.setResult(result);
			desiredOperationDao.saveOrUpdate(doInfo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<DesiredOperation> getFocedOperationByCustomerCardThatNotExcute(CustomerCardInfo customerCard) {
		try {
			return desiredOperationDao.getFocedOperationByCustomerCardThatNotExcute(customerCard);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public DesiredOperation getByAidAndProcedureNameAndCustomerCardThatNotExcuted(String aid, String procedureName,
			CustomerCardInfo customerCard) {
		try {
			return desiredOperationDao.getByAidAndProcedureNameAndCustomerCardThatNotExcuted(aid, procedureName, customerCard);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public DesiredOperation getBySessionId(String sessionId) {
		try {
			return desiredOperationDao.findUniqueByProperty("sessionId", sessionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void setCustomerCardInfo(String doIds, CustomerCardInfo cci) {
		try {
			String[] ids = doIds.split(",");
			for (String doId : ids) {
				DesiredOperation doitem = desiredOperationDao.load(Long.parseLong(doId));
				if (null != doitem) {
					doitem.setCustomer(cci.getCustomer());
					doitem.setCustomerCardId(cci.getId());
					desiredOperationDao.saveOrUpdate(doitem);
				}
			}

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public DesiredOperation getDoIdByAidAndOpt(String aid, String opt, String cardNo) {
		try {
			CustomerCardInfo cci = customerCardInfoManager.getByCardNoCancelAndReplaced(cardNo);
			return desiredOperationDao.getDObyCCIandAidOptStatus(cci, aid, opt, DesiredOperation.NOT_EXCUTED);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public DesiredOperation getByCustomerCardIdAndAidAndOperationAndStatuts(CustomerCardInfo customerCard, String aid, Operation operation,
			int status) {
		try {
			return desiredOperationDao.getDObyCCIandAidOptStatus(customerCard, aid, operation.name(), status);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}