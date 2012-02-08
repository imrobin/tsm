package com.justinmobile.tsm.customer.manager.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.OracleSequenceDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.JoinType;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.core.utils.ResourceBundleUtils;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysRoleManager;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.tsm.application.domain.Applet;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationLoadFile;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.Space;
import com.justinmobile.tsm.application.domain.SpecialMobile;
import com.justinmobile.tsm.application.manager.ApplicationLoadFileManager;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.card.dao.CardApplicationDao;
import com.justinmobile.tsm.card.dao.CardBaseApplicationDao;
import com.justinmobile.tsm.card.dao.CardBaseInfoDao;
import com.justinmobile.tsm.card.dao.CardBlackListDao;
import com.justinmobile.tsm.card.dao.CardInfoDao;
import com.justinmobile.tsm.card.domain.CardApplet;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseLoadFile;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardBlackList;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardLoadFile;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.card.manager.CardAppletManager;
import com.justinmobile.tsm.card.manager.CardApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseApplicationManager;
import com.justinmobile.tsm.card.manager.CardBaseLoadFileManager;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.card.manager.CardLoadFileManager;
import com.justinmobile.tsm.card.manager.CardSecurityDomainManager;
import com.justinmobile.tsm.cms2ac.manager.TaskManager;
import com.justinmobile.tsm.customer.dao.CustomerCardInfoDao;
import com.justinmobile.tsm.customer.dao.MobileTypeDao;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.domain.MobileType;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.endpoint.sms.SmsEndpoint;
import com.justinmobile.tsm.history.domain.SubscribeHistory;
import com.justinmobile.tsm.history.manager.SubscribeHistoryManager;
import com.justinmobile.tsm.system.manager.MobileSectionManager;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.manager.DesiredOperationManager;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;

@Service("customerCardInfoManager")
public class CustomerCardInfoManagerImpl extends EntityManagerImpl<CustomerCardInfo, CustomerCardInfoDao> implements
		CustomerCardInfoManager {

	String TRANS_SESSION_ID = "TRANS_SESSION_ID";

	String TRANS_SEQNUM_ID = "TRANS_SEQNUM_ID";

	@Autowired
	protected OracleSequenceDao sequenceDao;

	@Autowired
	protected TaskManager taskManager;

	@Autowired
	private CustomerCardInfoDao customerCardInfoDao;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private CardApplicationDao cardApplicationDao;

	@Autowired
	private CardBaseApplicationManager cardBaseApplicationManager;

	@Autowired
	private CardBaseApplicationDao cardBaseApplicationDao;

	@Autowired
	private CardBaseInfoDao cardBaseInfoDao;

	@Autowired
	private CardBlackListDao cardBlackListDao;

	@Autowired
	private DesiredOperationManager desiredOperationManager;

	@Autowired
	private CardInfoDao cardInfoDao;

	@Autowired
	private CardInfoManager cardManager;

	@Autowired
	private MobileTypeDao mobileTypeDao;

	@Autowired
	private SmsEndpoint smsEndpoint;

	@Autowired
	private CardSecurityDomainManager cardSecurityDomainManager;

	@Autowired
	private CardLoadFileManager cardLoadFileManager;

	@Autowired
	private CardAppletManager cardAppletManager;

	@Autowired
	private CardBaseLoadFileManager cardBaseLoadFileManager;

	@Autowired
	private LocalTransactionManager localTransactionManager;

	@Autowired
	private CardApplicationManager cardApplicationManager;

	@Autowired
	private ApplicationManager applicationManager;
	
	@Autowired
	ApplicationLoadFileManager applicationLoadFileManager;

	@Autowired
	private MobileSectionManager mobileSectionManager;

	@Autowired
	private SysUserManager userManager;

	@Autowired
	private SysRoleManager roleManager;

	@Autowired
	private SubscribeHistoryManager subscribeHistoryManager;

	@Override
	public List<CustomerCardInfo> getCustomerCardByCustomerName(String userName, Integer status) throws PlatformException {
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			if (null == customer) {
				return null;
			}
			List<CustomerCardInfo> ca = customerCardInfoDao.getCustomerCardByCustomer(customer, status);
			return ca;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean hasSysRequirment(CustomerCardInfo cci, CardApplication ca) {
		try {

			List<CustomerCardInfo> _cci = customerCardInfoDao.getHasRequiremnt(cci.getId(), ca.getApplicationVersion().getId(), ca
					.getApplicationVersion().getId());
			return _cci.size() != 0;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CustomerCardInfo getCustomerCardInfoById(Long ccIdL) {
		try {
			return customerCardInfoDao.load(ccIdL);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public void changeCustomerCardStatus(Long ccId, int status) {
		try {
			CustomerCardInfo customerCardInfo = customerCardInfoDao.load(ccId);
			if (null != customerCardInfo) {
				customerCardInfo.setStatus(status);
				customerCardInfoDao.saveOrUpdate(customerCardInfo);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public void doCustomerCardInfoLost(Long ccId) {
		try {
			CustomerCardInfo cci = customerCardInfoDao.load(ccId);
			if (null != cci) {
				if(cci.getStatus().intValue() == CustomerCardInfo.STATUS_NORMAL) {
					List<CardApplication> caList = cardApplicationManager.getForLostListByCardInfo(cci.getCard());
					if(CollectionUtils.isNotEmpty(caList)){
						if(cci.isInBlack()){
							cci.setStatus(CustomerCardInfo.STATUS_LOST);
							this.saveOrUpdate(cci);
						}else{
							saveCCiInBlackForLost(cci);
							cearteBlackListWithLost(ccId);
						}
						// 保存挂失前每个应用的状态
						for (CardApplication ca : caList) {
							ca.setOriginalStatus(ca.getStatus());
							ca.setStatus(CardApplication.STATUS_LOSTED);
							cardApplicationManager.saveOrUpdate(ca);
						}
					}else{
						finashCancel(ccId);
					}
				} else{
					throw new PlatformException(PlatformErrorCode.TERM_STATUS_IS_ANOMALOUS);
				}
			} else {
				throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_EXIST);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * @param cci
	 */
	private void saveCCiInBlackForLost(CustomerCardInfo cci) {
		cci.setInBlack(CustomerCardInfo.INBLACK);
		cci.setStatus(CustomerCardInfo.STATUS_LOST);
		customerCardInfoDao.saveOrUpdate(cci);
	}

	/**
	 * @Title: addDesitredOption
	 * @Description: 添加预制任务
	 * 
	 *               private void addDesitredOptionLost(Long ccId) { try {
	 *               CustomerCardInfo customerCard =
	 *               customerCardInfoDao.load(ccId); SecurityDomain sd =
	 *               securityDomainManager.getIsd(); DesiredOperation desiredOpt
	 *               = buildeDesireOperation(customerCard, sd);
	 *               desiredOperationManager.saveOrUpdate(desiredOpt); } catch
	 *               (PlatformException pe) { throw pe; } catch
	 *               (HibernateException e) { throw new
	 *               PlatformException(PlatformErrorCode.DB_ERROR, e); } catch
	 *               (Exception e) { throw new
	 *               PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e); }
	 * 
	 *               }
	 * 
	 *               /**
	 * @param customerCard
	 * @param sd
	 * @return
	 * 
	 *         private DesiredOperation buildeDesireOperation(CustomerCardInfo
	 *         customerCard, SecurityDomain sd) { DesiredOperation desiredOpt =
	 *         new DesiredOperation(); desiredOpt.setAid(sd.getAid());
	 *         desiredOpt.setCustomer(customerCard.getCustomer());
	 *         desiredOpt.setCustomerCardId(customerCard.getId());
	 *         desiredOpt.setIsExcuted(DesiredOperation.NOT_EXCUTED);
	 *         desiredOpt.setIsPrompt(DesiredOperation.NOT_PROMPTED);
	 *         desiredOpt.setPreProcess(DesiredOperation.PREPROCESS_TURE);
	 *         desiredOpt.setProcedureName(LocalTransaction.Operation.valueOf(
	 *         LocalTransaction.Operation.LOCK_CARD.getType())); return
	 *         desiredOpt; }
	 */

	/**
	 * @Title: getBlackListWithLost
	 * @Description: 把指定的CUSTOMERCARD放入黑名单
	 * @param ccId
	 */
	private void cearteBlackListWithLost(Long ccId) {
		try {
			CustomerCardInfo cci = customerCardInfoDao.load(ccId);
			CardBlackList cardBlackList = new CardBlackList();
			cardBlackList.setCustomerCardInfo(cci);
			cardBlackList.setOperateDate(Calendar.getInstance());
			cardBlackList.setType(CardBlackList.TYPE_CUSTOMER_ADD);
			cardBlackList.setReason("终端挂失自动加入黑名单");
			cardBlackListDao.saveOrUpdate(cardBlackList);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void doCustomerCardInfoRecover(Long ccIdL) {
		try {
			changeCustomerCardStatus(ccIdL, CustomerCardInfo.STATUS_NORMAL);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Map<String, Object>> createDelAppListForCancelTerm(Long ccIdL) {
		try {
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			CustomerCardInfo cci = customerCardInfoDao.load(ccIdL);
			resultList = createDelAid(cci);
			return resultList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	private List<Map<String, Object>> createDelAid(CustomerCardInfo cci) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		CardInfo card = cci.getCard();
		if (card.getStatus().equals(CardInfo.STATUS_DISABLE)) {
			throw new PlatformException(PlatformErrorCode.CARD_IS_DISABLE);
		}
		List<CardApplication> caList = cardApplicationDao.findByProperty("cardInfo", card);
		for (CardApplication ca : caList) {
			if (CardApplication.STATUS_DELETEABLE.contains(ca.getStatus())) {
				if (ca.getApplicationVersion().getApplication().getDeleteRule().intValue() != Application.DELETE_RULE_CAN_NOT) {
					Map<String, Object> resultMap = new HashMap<String, Object>();
					resultMap.put("aid", ca.getApplicationVersion().getApplication().getAid());
					resultMap.put("opt", LocalTransaction.Operation.DELETE_APP.getType());
					resultList.add(resultMap);
				}
			} else if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
				CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
				CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
				Application app = ca.getApplicationVersion().getApplication();
				if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
						&& app.getDeleteRule().intValue() == Application.DELETE_RULE_DELETE_ALL) {
					Map<String, Object> resultMap = new HashMap<String, Object>();
					resultMap.put("aid", ca.getApplicationVersion().getApplication().getAid());
					resultMap.put("opt", LocalTransaction.Operation.DELETE_APP.getType());
					resultList.add(resultMap);
				}
			} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
				CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
				CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
				if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
						|| cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
					Map<String, Object> resultMap = new HashMap<String, Object>();
					resultMap.put("aid", ca.getApplicationVersion().getApplication().getAid());
					resultMap.put("opt", LocalTransaction.Operation.DELETE_APP.getType());
					resultList.add(resultMap);
				}
			}
		}
		return resultList;
	}

	@Override
	// 获取用户卡上所有的应用
	public List<Application> getApplistByCustomerCard(Long customerCardId) {
		List<Application> appList = new ArrayList<Application>();
		// 查找卡片上的所有应用
		CustomerCardInfo cci = customerCardInfoDao.load(customerCardId);
		CardInfo cardInfo = cci.getCard();
		List<CardApplication> cardAppList = cardApplicationDao.findByProperty("cardInfo", cardInfo);
		for (CardApplication ca : cardAppList) {
			if (CardApplication.STATUS_DELETEABLE.contains(ca.getStatus())) {
				appList.add(ca.getApplicationVersion().getApplication());
			}
		}
		return appList;
	}

	public List<Map<String, Object>> getAppMaplistByCustomerCard(Long customerCardId) {
		// 查找卡片上的所有应用
		CustomerCardInfo cci = customerCardInfoDao.load(customerCardId);
		CardInfo cardInfo = cci.getCard();
		List<CardApplication> cardAppList = cardApplicationDao.findByProperty("cardInfo", cardInfo);
		// 是否有用有更新
		List<CardBaseApplication> cardBaseApplications = cardInfo.getCardBaseInfo().getCardBaseApplications();

		// end
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (CardApplication ca : cardAppList) {
			if (CardApplication.STATUS_SHOWABLE.contains(ca.getStatus())) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("appNames", ca.getApplicationVersion().getApplication().getName());
				resultMap.put("appId", ca.getApplicationVersion().getApplication().getId());
				resultMap.put("aid", ca.getApplicationVersion().getApplication().getAid());
				resultMap.put("spId", ca.getApplicationVersion().getApplication().getSp().getId());
				resultMap.put("spName", ca.getApplicationVersion().getApplication().getSp().getName());
				resultMap.put("appVer", ca.getApplicationVersion().getVersionNo());
				if (null != ca.getUsedNonVolatileSpace() && null != ca.getUsedVolatileSpace()) {
					resultMap.put("useRam", ca.getSpaceInfo().getRam());
					resultMap.put("useRom", ca.getSpaceInfo().getNvm());
				} else {
					resultMap.put("useRam", -1);
					resultMap.put("useRom", -1);
				}
				resultMap.put("statusOrg", ca.getStatus());
				resultMap.put("cardAppId", ca.getId());
				resultMap
						.put("status", ResourceBundleUtils.getMapMessage("customerapplication.status").get(String.valueOf(ca.getStatus())));

				CardBaseApplication cbaTemp = cardBaseApplicationDao.getByCardBaseAndAppver(ca.getCardInfo().getCardBaseInfo(),
						ca.getApplicationVersion());
				resultMap.put("presetMode", cbaTemp.getPresetMode());
				resultMap.put("delRule", ca.getApplicationVersion().getApplication().getDeleteRule());
				List<ApplicationVersion> versions = ca.getApplicationVersion().getApplication().getVersions();

				// 显示判断
				if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
					CardBaseApplication cba = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(cardInfo.getCardBaseInfo(),
							ca.getApplicationVersion().getApplication());
					if (null == cba) {
						resultMap.put("showRule", false);
					} else {
						resultMap.put("showRule", true);
						resultMap.put("show", false);
					}
				} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
					CardBaseApplication cba = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(cardInfo.getCardBaseInfo(),
							ca.getApplicationVersion().getApplication());
					if (null == cba) {
						CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
						cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
								&& ca.getApplicationVersion().getApplication().getDeleteRule().intValue() == Application.DELETE_RULE_CAN_NOT) {
							resultMap.put("showRule", true);
							resultMap.put("show", false);
						} else {
							resultMap.put("showRule", false);
						}
					} else {
						resultMap.put("showRule", true);
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
							resultMap.put("show", true);
						} else {
							resultMap.put("show", false);
						}
					}
				} else {
					resultMap.put("showRule", false);
				}

				// 是否有用有更新

				resultMap.put("hasNew", false);
				// 在application.version中存在更高版本，status=3，且card_base_application有该终端对应批次的记录。就显示
				for (ApplicationVersion av : versions) {
					if (av.getStatus().intValue() == ApplicationVersion.STATUS_PULISHED
							&& SpringMVCUtils.compareVersion(av.getVersionNo(), ca.getApplicationVersion().getVersionNo())) {
						for (int i = 0; i < cardBaseApplications.size(); i++) {
							CardBaseApplication cba = (CardBaseApplication) cardBaseApplications.get(i);
							if (av.getId() == cba.getApplicationVersion().getId()) {
								Set<SpecialMobile> speicalMobiles = av.getSpeicalMobiles();
								if (speicalMobiles.size() != 0) {// 不为空表示当前版本有特定手机限制
									for (SpecialMobile sm : speicalMobiles) { // 如果在特定手机里，可以更新
										if (sm.getMobileNo().equals(cci.getMobileNo())) {
											resultMap.put("hasNew", true);
											break;
										}
									}
								} else {
									resultMap.put("hasNew", true);
									break;
								}
							}
						}
					}
				}
				// end
				resultList.add(resultMap);
			}
		}
		return resultList;
	}

	@Override
	public int getAppCountByCustomerCardInfo(CustomerCardInfo cci) {
		try {
			int count = 0;

			CardInfo card = cci.getCard();
			List<CardApplication> cardAppList = cardApplicationDao.findByProperty("cardInfoId", card);
			count += cardAppList.size();

			Long cbid = card.getCardBaseInfo().getId();
			CardBaseInfo cardBaseInfo = cardBaseInfoDao.load(cbid);
			List<CardBaseApplication> cardBaseApplicationList = cardBaseApplicationDao.findByProperty("cardBase", cardBaseInfo);
			count += cardBaseApplicationList.size();

			return count;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CustomerCardInfo> getCustomerCardCanChange(String userName) throws PlatformException {
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			if (null == customer) {
				return null;
			}
			List<CustomerCardInfo> cciList = customerCardInfoDao.getCustomerCardInfoByIdAsNormomAndLost(customer);
			return cciList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Map<String, Object> calCardSize(Long ccId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			CustomerCardInfo cci = customerCardInfoDao.load(ccId);

			Integer totalRamSpace = cci.getCard().getCardBaseInfo().getTotalRamSize();
			Long totalRomSpace = cci.getCard().getCardBaseInfo().getTotalRomSize();

			if (null == totalRamSpace || null == totalRomSpace) {
				throw new PlatformException(PlatformErrorCode.GET_CARD_SPACE_ERROR);
			}

			Integer cardAvaiableRamSpace = cci.getCard().getAvailableVolatileSpace();
			Long cardAvaiableRomSpace = cci.getCard().getAvailableNonevolatileSpace();

			if (null == cardAvaiableRamSpace || null == cardAvaiableRomSpace) {
				throw new PlatformException(PlatformErrorCode.GET_CARD_SPACE_ERROR);
			}

			Integer cardUnKnowRamSpace = cci.getCard().getUnknownVolatileSpace();
			Long cardUnKnowRomSpace = cci.getCard().getUnknownNoneVolatileSpace();

			if (null == cardUnKnowRamSpace || null == cardUnKnowRomSpace) {
				throw new PlatformException(PlatformErrorCode.GET_CARD_SPACE_ERROR);
			}

			Integer usedRamSpace = totalRamSpace - cardAvaiableRamSpace;
			Long usedRomSpace = totalRomSpace - cardAvaiableRomSpace;

			// 计算百分比
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMaximumFractionDigits(2);
			String nsPercent = "0";
			String vsPercent = "0";
			if (0 != totalRomSpace) {
				nsPercent = numberFormat.format((usedRomSpace + 0.00) / totalRomSpace * 100);
			}
			if (0 != totalRamSpace) {
				vsPercent = numberFormat.format((usedRamSpace + 0.00) / totalRamSpace * 100);
			}

			Space toalSpace = new Space();
			Space usedSpace = new Space();
			Space existSpace = new Space();
			toalSpace.setRam(totalRamSpace);
			toalSpace.setNvm(totalRomSpace);
			existSpace.setRam(cardAvaiableRamSpace);
			existSpace.setNvm(cardAvaiableRomSpace);
			usedSpace.setRam(usedRamSpace);
			usedSpace.setNvm(usedRomSpace);
			// 返回传输
			map.put("totalSpace", toalSpace);
			map.put("existSpace", existSpace);
			map.put("usedSpace", usedSpace);
			map.put("vsPercent", vsPercent + "%");
			map.put("nsPercent", nsPercent + "%");
			return map;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	/**
	 * @param cardAppList
	 * @return
	 */
	/*
	 * private Space calUsedSpace(List<CardApplication> cardAppList) { Space
	 * usedSpace = new Space();// 已用空间 for (CardApplication app : cardAppList) {
	 * if (CardApplication.STATUS_CAL.contains(app.getStatus())) { if
	 * (app.getUsedVolatileSpace() == null) {
	 * usedSpace.setRam(usedSpace.getRam() + 0); } else {
	 * usedSpace.setRam(usedSpace.getRam() + app.getUsedVolatileSpace()); } if
	 * (app.getUsedNonVolatileSpace() == null) {
	 * usedSpace.setNvm(usedSpace.getNvm() + 0); } else {
	 * usedSpace.setNvm(usedSpace.getNvm() + app.getUsedNonVolatileSpace()); }
	 * 
	 * } } return usedSpace; }
	 */

	/**
	 * @param cci
	 * @return
	 */
	/*
	 * private Space calExistSpace(CustomerCardInfo cci) { Space existSpcae =
	 * new Space();// 剩余空间 if (cci.getCard().getAvailableVolatileSpace() ==
	 * null) { existSpcae.setRam(0); } else {
	 * existSpcae.setRam(cci.getCard().getAvailableVolatileSpace()); } if
	 * (cci.getCard().getAvailableNonevolatileSpace() == null) {
	 * existSpcae.setNvm(0); } else {
	 * existSpcae.setNvm(cci.getCard().getAvailableNonevolatileSpace()); }
	 * return existSpcae; }
	 */

	@Override
	public List<Map<String, Object>> getCardApplicationsByMobileNo(String moibleNo, Page<CardApplication> page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			List<CustomerCardInfo> cciList = getByMobileNo(moibleNo);
			if (CollectionUtils.isNotEmpty(cciList)) {
				for (CustomerCardInfo cci : cciList) {
					List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
					filters.add(new PropertyFilter("cardInfo", JoinType.I, "id", MatchType.EQ, PropertyType.L, String.valueOf(cci.getCard()
							.getId())));
					Page<CardApplication> cardAppList = cardApplicationDao.findPage(page, filters);
					for (CardApplication ca : cardAppList.getResult()) {
						Map<String, Object> map = buildAppMap(ca);
						map.put("cciName", cci.getName());
						map.put("userName", cci.getCustomer().getSysUser().getUserName());
						map.put("cciId", cci.getId());
						list.add(map);
					}
				}
			}

		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return list;
	}

	/**
	 * @param ca
	 * @return
	 */
	private Map<String, Object> buildAppMap(CardApplication ca) {
		Application app = ca.getApplicationVersion().getApplication();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("appName", app.getName());
		map.put("appDesc", app.getDescription());
		map.put("appType", app.getChildType().getName());
		map.put("location", app.getLocation());
		map.put("sp", app.getSp().getName());
		map.put("spId", app.getSp().getId());
		map.put("aid", ca.getApplicationVersion().getApplication().getAid());
		map.put("appStatus", ResourceBundleUtils.getMapMessage("customerapplication.status").get(String.valueOf(ca.getStatus())));
		map.put("statusOrg", ca.getStatus());
		map.put("id", ca.getId());
		map.put("appId", app.getId());
		map.put("appver", ca.getApplicationVersion().getVersionNo());
		if (null != ca.getUsedNonVolatileSpace() && null != ca.getUsedVolatileSpace()) {
			map.put("useRam", ca.getSpaceInfo().getRam());
			map.put("useRom", ca.getSpaceInfo().getNvm());
		} else {
			map.put("useRam", -1);
			map.put("useRom", -1);
		}
		return map;
	}

	@Override
	public Long bindCardWithMobileType(Map<String, String> paramMap, boolean isChange) {
		try {
			// 获取手机型号
			MobileType type = getMobileType(paramMap);

			CustomerCardInfo customerCard = bindCard(paramMap, isChange);

			customerCard.setMobileType(type);// 设置手机型号
			customerCardInfoDao.saveOrUpdate(customerCard);

			this.adminActiveCard(customerCard.getId());

			return customerCard.getId();
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public CustomerCardInfo bindCard(Map<String, String> paramMap, boolean isChange) {
		// 获取用户
		Customer customer = checkUser(paramMap);
		// 获取终端,如果没有又在卡批次中就建立
		CardInfo cardInfo = cardManager.buildCardInfoIfNotExist(paramMap.get("cardNo"));
		// 判断终端是否能够重复绑定
		checkCardBindable(cardInfo);
		// 判断手机号是否能够重复及号段
		checkBindMobile(paramMap, customer);
		// checkBindCardName(customer, paramMap.get("mobileName"));
		CustomerCardInfo customerCard = new CustomerCardInfo();
		// 建立数据
		if (!isChange) {
			customerCard.setMobileNo(paramMap.get("mobileNo"));
		} else {
			CustomerCardInfo oldCCi = customerCardInfoDao.load(Long.valueOf(paramMap.get("oldCardId")));
			customerCard.setMobileNo(oldCCi.getMobileNo());
			oldCCi.setStatus(CustomerCardInfo.STATUS_REPLACING);
			customerCardInfoDao.saveOrUpdate(oldCCi);
		}
		MobileType type = getMobileType(paramMap);
		customerCard.setMobileType(type);// 设置手机型号
		customerCard.setActive(CustomerCardInfo.NOT_ACTIVED);// 设置未激活
		customerCard.setCard(cardInfo);// 设置SE
		customerCard.setCustomer(customer);
		customerCard.setInBlack(CustomerCardInfo.NOT_INBLACK);
		customerCard.setName(paramMap.get("mobileName"));
		customerCard.setStatus(CustomerCardInfo.STATUS_NOT_USE);

		customerCardInfoDao.saveOrUpdate(customerCard);

		return customerCard;
	}

	/**
	 * @param paramMap
	 * @return
	 */
	private MobileType getMobileType(Map<String, String> paramMap) {
		String mobileTypeId = paramMap.get("mobileTypeId");
		MobileType type = null;
		if (null != mobileTypeId) {
			type = mobileTypeDao.load(Long.valueOf(mobileTypeId));
		}
		return type;
	}

	/**
	 * @param paramMap
	 * @return
	 */
	private Customer checkUser(Map<String, String> paramMap) {
		Customer customer = customerManager.getCustomerByUserName(paramMap.get("userName"));
		if (null == customer) {
			throw new PlatformException(PlatformErrorCode.USER_NOT_EXIST);
		}
		return customer;
	}

	@SuppressWarnings("unused")
	private void checkBindCardName(Customer customer, String cardName) {
		CustomerCardInfo cci = customerCardInfoDao.findByCciName(customer, cardName);
		if (null != cci) {
			throw new PlatformException(PlatformErrorCode.CCI_NAME_EXIST);
		}
	}

	/**
	 * @param paramMap
	 * @param customer
	 */
	private void checkBindMobile(Map<String, String> paramMap, Customer customer) {
		String mobileNo = paramMap.get("mobileNo");
		boolean flag = checkMobileNoDupForOther(customer, mobileNo);
		if (!flag) {
			throw new PlatformException(PlatformErrorCode.USER_MOBILE_REDUPLICATE);
		}
	}

	@Override
	public void checkCardBindable(CardInfo cardInfo) {
		List<CustomerCardInfo> seList = customerCardInfoDao.getListByCardInfo(cardInfo);
		if (CollectionUtils.isNotEmpty(seList)) {
			for (CustomerCardInfo seCCi : seList) {
				if (seCCi.getStatus() != CustomerCardInfo.STATUS_CANCEL && seCCi.getStatus() != CustomerCardInfo.STATUS_END_REPLACE) {
					throw new PlatformException(PlatformErrorCode.CARD_IS_EXIST);
				}
			}
		}
	}

	@Override
	public void sendActive(Long customerCardId, String type) {
		try {
			// 获取用户
			CustomerCardInfo cci = customerCardInfoDao.load(customerCardId);
			String activeCode = RandomStringUtils.randomNumeric(6);
			cci.setActiveCode(activeCode);
			customerCardInfoDao.saveOrUpdate(cci);
			String msg = "";
			if (type.equals("1")) {
				msg = "尊敬的用户,您的手机绑定激活码是:";
			} else {
				msg = "尊敬的用户,您的手机验证码是:";
			}
			smsEndpoint.sendMessage(cci.getMobileNo(), msg + activeCode);
		} catch (PlatformException e) {
			throw new PlatformException(PlatformErrorCode.BIND_CARD_SMS_ERROR, e);
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean activeCard(String userName, String avtiveCode, String ccId) {
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			if (null == customer) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_EXIST);
			}
			CustomerCardInfo cci = customerCardInfoDao.load(Long.valueOf(ccId));
			String orgActiveCode = cci.getActiveCode();
			if (orgActiveCode.equals(avtiveCode.trim())) {
				List<CustomerCardInfo> mayExist = customerCardInfoDao.findByProperty("mobileNo", cci.getMobileNo());
				for (CustomerCardInfo mayCci : mayExist) {
					if (mayCci.getId() == Long.parseLong(ccId) && mayCci.getStatus() == CustomerCardInfo.STATUS_NOT_USE) {
						cci.setActive(CustomerCardInfo.ACTIVED);
						cci.setBindingDate(Calendar.getInstance());
						cci.setStatus(CustomerCardInfo.STATUS_NORMAL);
						customerCardInfoDao.saveOrUpdate(cci);
						saveCustomerCardContact(cci);// 保存预制卡片批次信息
						checkCardSpace(cci);
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	private void checkCardSpace(CustomerCardInfo cci) {
		CardInfo card = cci.getCard();
		if (card.getAvailableNonevolatileSpace() < 0 || card.getAvailableVolatileSpace() < 0) {
			throw new PlatformException(PlatformErrorCode.CARD_SPACE_ERROR);
		}
	}

	/**
	 * @param cci
	 */
	private void saveCustomerCardContact(CustomerCardInfo cci) {
		CardBaseInfo cbi = cci.getCard().getCardBaseInfo();
		createCardSd(cci, cbi);
		List<CardBaseApplication> applist = cbi.getCardBaseApplications();
		for (CardBaseApplication cba : applist) {
			if (cba.getPreset()) {
				// 预制cardApplication表
				List<CardApplication> oldCaList = cardApplicationDao.findByProperty("cardInfo", cci.getCard());
				boolean isExist = false;
				for (CardApplication oldca : oldCaList) {
					if (oldca.getApplicationVersion().getId() == cba.getApplicationVersion().getId()) {
						isExist = true;
					}
				}
				if (isExist) {
					continue;
				}
				List<CardApplication> caList = cardApplicationManager.getByCardAndApplication(cci.getCard(), cba.getApplicationVersion().getApplication());
				if (caList.size() > 0) {
					continue;
				} else {
					createCardApp(cci, cba);
				}
			}
		}
		createCardLoadFile(cci, cbi);
	}

	/**
	 * @param cci
	 * @param cbi
	 */
	private void createCardSd(CustomerCardInfo cci, CardBaseInfo cbi) {
		CardInfo card = cci.getCard();
		List<CardBaseSecurityDomain> sdlist = cbi.getCardBaseSecurityDomain();
		for (CardBaseSecurityDomain cbsd : sdlist) {
			if (cbsd.getPreset() == CardBaseSecurityDomain.PRESET && null != cbsd.getPresetMode()) {
				SecurityDomain securityDomain = cbsd.getSecurityDomain();
				CardSecurityDomain csd = cardSecurityDomainManager.getbySdAndCard(card, securityDomain);
				if (null == csd) {
					createCustomerCardAndSave(card, cbsd);
				}
			}
		}
	}

	private void createCustomerCardAndSave(CardInfo card, CardBaseSecurityDomain cbsd) {
		CardSecurityDomain csd = new CardSecurityDomain();
		csd.setCard(card);
		csd.setSd(cbsd.getSecurityDomain());
		if (cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED) {
			csd.setCurrentKeyVersion(cbsd.getPresetKeyVersion());
			csd.setStatus(CardBaseSecurityDomain.PRESET_MODE_PERSONALIZED);
		} else if (cbsd.getPresetMode().intValue() == CardBaseSecurityDomain.PRESET_MODE_CARETE) {
			csd.setStatus(CardBaseSecurityDomain.PRESET_MODE_CARETE);
		}
		if (cbsd.getSecurityDomain().getSpaceRule().intValue() == SecurityDomain.FIXED_SPACE) {
			csd.setAviliableSpace(cbsd.getSecurityDomain().getTotalManagedSpace());
		}
		Space space = new Space();
		if (!cbsd.getSecurityDomain().isIsd()) {
			space = cbsd.getSecurityDomain().getTotalSpace();
		}
		card.setAvailableSpace(card.getAvailableSpace().minus(space));
		cardManager.saveOrUpdate(card);
		cardSecurityDomainManager.saveOrUpdate(csd);
	}

	/**
	 * @param cci
	 * @param cba
	 */
	private void createCardApplet(CustomerCardInfo cci, CardBaseApplication cba) {
		Set<Applet> applets = cba.getApplicationVersion().getApplets();
		for (Applet applet : applets) {
			CardApplet calet = cardAppletManager.getByCardAndApplet(cci.getCard(), applet);
			if (null != calet) {
				continue;
			} else {
				calet = new CardApplet();
				calet.setCard(cci.getCard());
				calet.setApplet(applet);
				cardAppletManager.saveOrUpdate(calet);
			}
		}
	}

	/**
	 * @param cci
	 * @param cbi
	 */
	private void createCardLoadFile(CustomerCardInfo cci, CardBaseInfo cbi) {
		List<CardBaseLoadFile> BaseLoadFileList = cardBaseLoadFileManager.getBaseLoadFileByCardBase(cbi);
		for (CardBaseLoadFile cbf : BaseLoadFileList) {
			boolean dupFlag = false;//是否有同一应用下的重复LOADFILE	
			
			List<CardLoadFile> clfList = cardLoadFileManager.getByCard(cci.getCard());
			Set<ApplicationLoadFile> loadFiles = cbf.getLoadFileVersion().getApplicationLoadFiles();
			
			for (CardLoadFile clf : clfList) {
				Set<ApplicationLoadFile> AppverLoadFileSet = clf.getLoadFileVersion().getApplicationLoadFiles();
				for (ApplicationLoadFile alf : AppverLoadFileSet) {
					Application application = alf.getApplicationVersion().getApplication();
					for(ApplicationLoadFile presetAlf : loadFiles) {
						Application preapp = presetAlf.getApplicationVersion().getApplication();
						if(application.getId().longValue() == preapp.getId().longValue()) {
							dupFlag = true;
						}
					}
				}
			}
			
			if (dupFlag) {
				continue;
			} else {
				CardLoadFile clf = new CardLoadFile();
				clf.setCard(cci.getCard());
				clf.setLoadFileVersion(cbf.getLoadFileVersion());
				CardInfo card = cci.getCard();
				SecurityDomain sd = cbf.getLoadFileVersion().getLoadFile().getSd();
				CardSecurityDomain csd = cardSecurityDomainManager.getbySdAndCard(card, sd);
				if (null == csd) {
					throw new PlatformException(PlatformErrorCode.NOT_FOUND_PARENT_SD);
				}
				csd.setAviliableSpace(csd.getAviliableSpace().minus(cbf.getLoadFileVersion().getSpaceInfo()));
				cardManager.saveOrUpdate(card);
				cardSecurityDomainManager.saveOrUpdate(csd);
				cardLoadFileManager.saveOrUpdate(clf);
			}
		}
	}

	/**
	 * @param cci
	 * @param cba
	 * @return
	 */
	private void createCardApp(CustomerCardInfo cci, CardBaseApplication cba) {
		CardApplication ca = new CardApplication();
		ca.setApplicationVersion(cba.getApplicationVersion());
		if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
			ca.setStatus(CardApplication.STATUS_DOWNLOADED);
			createCardLoadFileForApp(cci, ca);
			ca.setUsedVolatileSpace(cba.getApplicationVersion().getLoadFileSpaceInfo().getRam());
			ca.setUsedNonVolatileSpace(cba.getApplicationVersion().getLoadFileSpaceInfo().getNvm());
		} else if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_PERSONAL) {
			ca.setStatus(CardApplication.STATUS_INSTALLED);
			createCardLoadFileForApp(cci, ca);
			createCardApplet(cci, cba);
			Integer ram = cba.getApplicationVersion().getLoadFileSpaceInfo().getRam()
					+ cba.getApplicationVersion().getAppletSpaceInfo().getRam();
			Long rom = cba.getApplicationVersion().getLoadFileSpaceInfo().getNvm()
					+ cba.getApplicationVersion().getAppletSpaceInfo().getNvm();
			ca.setUsedNonVolatileSpace(rom);
			ca.setUsedVolatileSpace(ram);
		}
		ca.setCardInfo(cci.getCard());
		CardInfo card = cci.getCard();
		SecurityDomain sd = ca.getApplicationVersion().getApplication().getSd();
		CardSecurityDomain csd = cardSecurityDomainManager.getbySdAndCard(card, sd);
		if (null == csd) {
			throw new PlatformException(PlatformErrorCode.NOT_FOUND_PARENT_SD);
		}
		csd.setAviliableSpace(csd.getAviliableSpace().minus(calApplicatioVerSpace(cba)));
		cardManager.saveOrUpdate(card);
		cardSecurityDomainManager.saveOrUpdate(csd);
		cardApplicationDao.saveOrUpdate(ca);
	}

	private void createCardLoadFileForApp(CustomerCardInfo cci, CardApplication ca) {
		CardInfo card = cci.getCard();
		Set<ApplicationLoadFile> appLoadFileList = ca.getApplicationVersion().getApplicationLoadFiles();
		for (ApplicationLoadFile alf : appLoadFileList) {
			LoadFileVersion lfv = alf.getLoadFileVersion();
			CardLoadFile clf = cardLoadFileManager.getByCardAndLoadFileVersion(card, lfv);
			if (null != clf) {
				continue;
			} else {
				clf = new CardLoadFile();
				clf.setCard(cci.getCard());
				clf.setLoadFileVersion(lfv);
				cardLoadFileManager.saveOrUpdate(clf);
			}
		}
	}

	private Space calApplicatioVerSpace(CardBaseApplication cba) {
		Space space = new Space();
		if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
			space = cba.getApplicationVersion().getLoadFileSpaceInfo();
		} else if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_PERSONAL) {
			space = cba.getApplicationVersion().getLoadFileSpaceInfo().plus(cba.getApplicationVersion().getAppletSpaceInfo());
		}
		return space;
	}

	@Override
	public Map<String, Object> changeActive(String userName, String avtiveCode, String ccId, String oldId) {
		try {
			Map<String, Object> resutlMap = new HashMap<String, Object>();
			CustomerCardInfo oldCard = customerCardInfoDao.load(Long.valueOf(oldId));
			if (null == oldCard || oldCard.getStatus() != CustomerCardInfo.STATUS_REPLACING) {
				new PlatformException(PlatformErrorCode.OLD_CARD_IS_NOT_EXIST);
			}
			if (activeCard(userName, avtiveCode, ccId)) {
				CustomerCardInfo newCard = customerCardInfoDao.load(Long.valueOf(ccId));
				List<CardApplication> appList = getRevertApp(oldCard, newCard);
				List<String> aidList = new ArrayList<String>();
				for (CardApplication ca : appList) {
					aidList.add(ca.getApplicationVersion().getApplication().getAid());
				}
				resutlMap.put("aidList", aidList);
				resutlMap.put("active", Boolean.TRUE);
				return resutlMap;
			} else {
				resutlMap.put("active", Boolean.FALSE);
				return resutlMap;
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	@Override
	public List<Application> listRevertApps(Long ccId) {
		try {
			List<Application> appList = new ArrayList<Application>();
			// 查找卡片上的所有应用
			CustomerCardInfo cci = customerCardInfoDao.load(ccId);
			List<CardApplication> cardAppList = cardApplicationDao.listRevertApps(cci);
			for (CardApplication ca : cardAppList) {
				Application app = ca.getApplicationVersion().getApplication();
				if (app.getStatus().equals(Application.STATUS_PUBLISHED)) {// 如果应用还未发布
					if (CardApplication.STATUS_REVERTABLE.contains(ca.getStatus())) {// 如果应用尚未再原卡片中下载完成
						appList.add(app);
					}
				}
			}
			return appList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CardApplication> getRevertApp(CustomerCardInfo oldCci, CustomerCardInfo cci) {
		List<CardApplication> canRvertApp = new ArrayList<CardApplication>();// 需要恢复的应用列表
		try {
			// 1.先判断时候以前有未完成的恢复列表,可以判断是否以前做过.如果做过,就继续处理
			// CustomerCardInfo backCustomerCard = cci.getBackCustomerCard();
			/*
			 * CustomerCardInfo backCustomerCard = null; if (null !=
			 * backCustomerCard) { if
			 * (oldCci.getId().equals(backCustomerCard.getId())) {//
			 * 如果此次选择的应用和上次一样 List<CardApplication> nowApplist =
			 * cardApplicationDao.findByProperty("cardInfo", cci.getCard());//
			 * 需要恢复应用的终端 for (CardApplication ca : nowApplist) { if
			 * (ca.getApplicationVersion
			 * ().getApplication().getStatus().intValue() ==
			 * Application.STATUS_PUBLISHED) {// 如果应用还未发布 if
			 * (ca.getStatus().intValue() ==
			 * CardApplication.STATUS_UNDOWNLOAD.intValue()) {// 如果应用尚未再原卡片中下载完成
			 * canRvertApp.add(ca); } } } if (canRvertApp.size() == 0) {
			 * cci.setBackCustomerCard(null);
			 * customerCardInfoDao.saveOrUpdate(cci); } } else { throw new
			 * PlatformException(PlatformErrorCode.OLD_CARD_DIFFERENT); } } else
			 * { // 如果是全新选择应用恢复
			 */List<CardApplication> oldApplist = cardApplicationDao.findByProperty("cardInfo", oldCci.getCard());// 需要恢复应用的终端
			List<CardApplication> nowApplist = cardApplicationDao.findByProperty("cardInfo", cci.getCard());// 需要恢复应用的终端
			if (null == nowApplist) {
				nowApplist = new ArrayList<CardApplication>();
			}
			List<CardBaseApplication> baseApp = cci.getCard().getCardBaseInfo().getCardBaseApplications();
			List<CardApplication> resonAppList = new ArrayList<CardApplication>();
			// 计算出符合条件的APP
			getReasonAppList(oldApplist, nowApplist, resonAppList);
			// 复制创建关系
			createCardAppvers(cci, canRvertApp, baseApp, resonAppList);
			// cci.setBackCustomerCard(oldCci);// 设置卡片有原恢复的指向
			customerCardInfoDao.saveOrUpdate(cci);
			/* } */
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return canRvertApp;
	}

	/**
	 * @param cci
	 * @param canRvertApp
	 * @param baseApp
	 * @param resonAppList
	 */
	private void createCardAppvers(CustomerCardInfo cci, List<CardApplication> canRvertApp, List<CardBaseApplication> baseApp,
			List<CardApplication> resonAppList) {
		for (CardApplication ca : resonAppList) {
			for (CardBaseApplication cba : baseApp) {
				if (cba.getPreset()) {
					if (cba.getApplicationVersion().getId() == ca.getId()) {
						continue;
					}
				}
			}
			if (ca.getApplicationVersion().getApplication().getStatus().intValue() == Application.STATUS_PUBLISHED) {// 如果应用还未发布
				CardApplication newCa = cardApplicationDao.getByCardAndApplicationAndUndownload(cci.getCard(), ca.getApplicationVersion());
				if (null != newCa) {
					newCa.setUsedNonVolatileSpace(0L);
					newCa.setUsedVolatileSpace(0);
				} else {
					newCa = new CardApplication();
					newCa.setCardInfo(cci.getCard());
					newCa.setStatus(CardApplication.STATUS_UNDOWNLOAD);
					newCa.setApplicationVersion(ca.getApplicationVersion());
					newCa.setUsedNonVolatileSpace(0L);
					newCa.setUsedVolatileSpace(0);
				}
				cardApplicationDao.saveOrUpdate(newCa);// 复制应用并设定状态
				canRvertApp.add(newCa);
			}
		}
	}

	/**
	 * @param oldApplist
	 * @param nowApplist
	 * @param resonAppList
	 */
	private void getReasonAppList(List<CardApplication> oldApplist, List<CardApplication> nowApplist, List<CardApplication> resonAppList) {
		for (CardApplication oldca : oldApplist) {
			boolean flag = true;
			if (CardApplication.STATUS_REVERTABLE.contains(oldca.getStatus())
					&& oldca.getApplicationVersion().getApplication().getStatus().intValue() == Application.STATUS_PUBLISHED) {
				for (CardApplication nowCa : nowApplist) {
					if (oldca.getApplicationVersion().getApplication().getAid()
							.equals(nowCa.getApplicationVersion().getApplication().getAid())
							&& nowCa.getStatus().intValue() != CardApplication.STATUS_UNDOWNLOAD) {
						flag = false;
						break;
					}
				}
				if (flag) {
					resonAppList.add(oldca);
				}
			}
		}
	}

	@Override
	public List<CustomerCardInfo> getCanRevertByCustomerName(String userName) {
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			if (null == customer) {
				return null;
			}
			List<CustomerCardInfo> cciList = customerCardInfoDao.findCanRevert(customer);
			return cciList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CustomerCardInfo checkAndFinishRevert(String userName, String ccid) {
		try {
			CustomerCardInfo customerCard = customerCardInfoDao.load(Long.valueOf(ccid));
			CustomerCardInfo backCci = customerCard.getBackCustomerCard();
			// 如果恢复应用是在恢复以前更换应用时未完成的终端,那么直接跳转到结束更换流程
			if (null != backCci && backCci.getStatus() == CustomerCardInfo.STATUS_REPLACING) {
				checkChangeFinish(ccid, String.valueOf(backCci.getId()));
				return null;
			}
			CardInfo card = customerCard.getCard();

			List<CardApplication> caList = cardApplicationDao.findAvailableList(card);
			for (CardApplication cardApp : caList) {
				cardApplicationDao.saveOrUpdate(cardApp);
			}

			List<CardApplication> unDownList = cardApplicationDao.findDownloadList(card);
			if (unDownList.size() > 0) {
				return null;
			}
			customerCard.setBackCustomerCard(null);
			customerCardInfoDao.saveOrUpdate(customerCard);
			return customerCard;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CustomerCardInfo getByCardNo(String cardNo) {
		try {
			CardInfo cardInfo = cardInfoDao.findUniqueByProperty("cardNo", cardNo);
			if (null == cardInfo || cardInfo.getStatus() == CardInfo.STATUS_DISABLE) {
				throw new PlatformException(PlatformErrorCode.CARD_IS_NOT_SUPPOT);
			}
			CustomerCardInfo cci = customerCardInfoDao.findCustomerCardInfo(cardInfo, CustomerCardInfo.STATUS_NORMAL,
					CustomerCardInfo.ACTIVED, CustomerCardInfo.NOT_INBLACK);
			return cci;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CustomerCardInfo> getByMobileNo(String mobileNo) {
		try {
			List<CustomerCardInfo> cciList = customerCardInfoDao.getByMobileNoNotCancelAndEnd(mobileNo);
			return cciList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void changeCustomerCardActive(Long ccId, Long active) {
		try {
			CustomerCardInfo customerCardInfo = customerCardInfoDao.load(ccId);
			if (null != customerCardInfo) {
				customerCardInfo.setActive(active);
				customerCardInfoDao.saveOrUpdate(customerCardInfo);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CustomerCardInfo getByMobileNoRepalcing(String stringNo) {
		try {
			CustomerCardInfo customerCardInfo = customerCardInfoDao.getByMobileNoRepalcing(stringNo);
			return customerCardInfo;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Map<String, Object> getCardMessageByCardNo(String cardNo) {
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			CustomerCardInfo customerCardInfo = getByCardNoAndOpt(cardNo);
			if (null != customerCardInfo) {
				resultMap.put("username", customerCardInfo.getCustomer().getSysUser().getUserName());
				resultMap.put("mobileNo", customerCardInfo.getMobileNo());
				resultMap.put("mobileType", customerCardInfo.getMobileType().getBrandChs() + " : "
						+ customerCardInfo.getMobileType().getType());
				resultMap.put("id", customerCardInfo.getId());
			} else {
				throw new PlatformException(PlatformErrorCode.CARD_IS_NOT_SUPPOT);
			}
			return resultMap;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private CustomerCardInfo getByCardNoAndOpt(String cardNo) {
		try {
			CardInfo cardInfo = cardInfoDao.findUniqueByProperty("cardNo", cardNo);
			if (null == cardInfo) {
				throw new PlatformException(PlatformErrorCode.CARD_IS_NOT_SUPPOT);
			}
			CustomerCardInfo cci = customerCardInfoDao.findByActiveAndNotCancelEnd(cardInfo);
			return cci;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void checkChangeFinish(String ccid, String oldId) {
		try {
			CustomerCardInfo oldCard = customerCardInfoDao.load(Long.valueOf(oldId));
			CustomerCardInfo customerCard = customerCardInfoDao.load(Long.valueOf(ccid));
			CardInfo card = customerCard.getCard();
			List<CardApplication> caList = cardApplicationDao.findAvailableList(card);
			for (CardApplication cardApp : caList) {
				cardApplicationDao.saveOrUpdate(cardApp);
			}
			List<CardApplication> unDownList = cardApplicationDao.findDownloadList(card);
			if (unDownList.size() > 0) {
				throw new PlatformException(PlatformErrorCode.OPT_IS_NOT_FINISH);
			}
			oldCard.setStatus(CustomerCardInfo.STATUS_END_REPLACE);
			customerCard.setBackCustomerCard(null);
			customerCardInfoDao.saveOrUpdate(customerCard);
			customerCardInfoDao.saveOrUpdate(oldCard);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void finashCancel(Long ccId) {
		try {
			CustomerCardInfo cci = customerCardInfoDao.load(ccId);

			if (cci.getActive().equals(CustomerCardInfo.NOT_ACTIVED)) {
				changeCustomerCardStatus(ccId, CustomerCardInfo.STATUS_CANCEL);
			} else if (cci.getActive().equals(CustomerCardInfo.ACTIVED)) {
				changeCustomerCardStatus(ccId, CustomerCardInfo.STATUS_CANCEL);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void cancelLost(Long ccid) {
		try {
			CustomerCardInfo cci = customerCardInfoDao.load(ccid);
			if (cci.getStatus().intValue() == CustomerCardInfo.STATUS_LOST) {
				// 1.改变CCI的状态
				cci.setStatus(CustomerCardInfo.STATUS_NORMAL);
				if(cci.isInBlack()) {
					cci.setInBlack(CustomerCardInfo.NOT_INBLACK);
					// 2.从黑名单中移除
					CardBlackList cardBlackList = new CardBlackList();
					cardBlackList.setCustomerCardInfo(cci);
					cardBlackList.setType(CardBlackList.TYPE_CUSTOMER_REMOVE);
					cardBlackList.setOperateDate(Calendar.getInstance());
					cardBlackList.setReason("终端解挂自动移除黑名单");
					cardBlackListDao.saveOrUpdate(cardBlackList);
				}
				customerCardInfoDao.saveOrUpdate(cci);
				// 3.讲CARDAPPLICATION9的变为6
				List<CardApplication> caList = cardApplicationManager.getByCardAndStatus(cci.getCard(), CardApplication.STATUS_LOSTED);
				for (CardApplication ca : caList) {
					ca.setStatus(CardApplication.STATUS_INSTALLED);
					cardApplicationManager.saveOrUpdate(ca);
					
					SubscribeHistory subscribeHistory = subscribeHistoryManager.getLastSubscribeHistoryByCustomerCardAndApplicationVersion(cci, ca.getApplicationVersion());
					if (null != subscribeHistory) {
						subscribeHistory.setUnsubscribeDate(Calendar.getInstance());
						subscribeHistoryManager.saveOrUpdate(subscribeHistory);
					}
					subscribeHistoryManager.unsubscribeApplication(ca.getCardInfo(), ca.getApplicationVersion());
				}
			} else {
				throw new PlatformException(PlatformErrorCode.TERM_STATUS_IS_ANOMALOUS);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CustomerCardInfo getByCardThatStatusNotCanclledOrNotReplaced(CardInfo card) {
		try {
			return customerCardInfoDao.getByCardThatStatusNotCanclledOrNotReplaced(card);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Map<String, Object> tipRevert(String sessionId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			LocalTransaction lt = localTransactionManager.getBySessionId(sessionId);
			List<LocalTransaction> lts = lt.getTask().getLocalTransactions();
			List<String> noexeList = new ArrayList<String>();// 未完成应用名称
			List<String> successList = new ArrayList<String>();// 执行成功应用名称
			List<String> failList = new ArrayList<String>();// 执行失败应用名称
			boolean isSuccess = true;
			for (LocalTransaction singleLt : lts) {
				if (singleLt.getExecutionStatus().intValue() == LocalTransaction.STATUS_EXECUTION_EXEUTORY) {
					noexeList.add(applicationManager.getByAid(singleLt.getAid()).getName());
					isSuccess = false;
				} else if (singleLt.getExecutionStatus().intValue() == LocalTransaction.STATUS_EXECUTION_EXEUTED) {
					if (singleLt.getResult().equals(PlatformErrorCode.SUCCESS.getErrorCode())) {
						successList.add(applicationManager.getByAid(singleLt.getAid()).getName());
					} else {
						failList.add(applicationManager.getByAid(singleLt.getAid()).getName());
						isSuccess = false;
					}
				}
			}
			resultMap.put("doSuccess", isSuccess);
			resultMap.put("noexeSize", noexeList.size());
			resultMap.put("noexeList", noexeList);
			resultMap.put("successSize", successList.size());
			resultMap.put("successList", successList);
			resultMap.put("failSize", failList.size());
			resultMap.put("failList", failList);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return resultMap;
	}

	@Override
	public CustomerCardInfo getByCardNoCancelAndReplaced(String cardNo) {
		try {
			CardInfo cardInfo = cardInfoDao.findUniqueByProperty("cardNo", cardNo);
			if (null == cardInfo) {
				throw new PlatformException(PlatformErrorCode.OPERATION_NOT_BELONG_THIS_TERMINAL);
			}
			return customerCardInfoDao.findByActiveAndNotCancelEnd(cardInfo);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public String checkSend(String newMobileNo) {
		try {
			// 获取用户
			String activeCode = RandomStringUtils.randomNumeric(6);
			String msg = "尊敬的用户,您的手机验证码是:";
			smsEndpoint.sendMessage(newMobileNo, msg + activeCode);
			return activeCode;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void changeMobileNo(String ccId, String newmobileNo) {
		try {
			CustomerCardInfo cci = this.load(Long.valueOf(ccId));
			Customer customer = cci.getCustomer();
			boolean flag = checkMobileNoDupForOther(customer, newmobileNo);
			if (flag) {
				cci.setMobileNo(newmobileNo);
				this.saveOrUpdate(cci);
			} else {
				throw new PlatformException(PlatformErrorCode.USER_MOBILE_REDUPLICATE);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	private boolean checkMobileNoDupForOther(Customer customer, String newmobileNo) {
		List<CustomerCardInfo> cciList = customerCardInfoDao.findCCIByNotCustomerAndMobiemo(customer, newmobileNo);
		if (CollectionUtils.isNotEmpty(cciList)) {
			return false;
		}
		return true;
	}

	@Override
	public Page<CustomerCardInfo> getCustomerCardInfoPageBySd1(Page<CustomerCardInfo> page, SecurityDomain securityDomain)
			throws PlatformException {
		try {
			page = customerCardInfoDao.getCustomerCardInfoPageBySd(page, securityDomain);
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
	public Page<CustomerCardInfo> getCustomerCardInfoPageBySd(Page<CustomerCardInfo> page, SecurityDomain securityDomain)
			throws PlatformException {
		try {
			page = customerCardInfoDao.getCustomerCardInfoPageBySd(page, securityDomain);
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
	public Page<CustomerCardInfo> getCustomerCardInfoPageByApp(Page<CustomerCardInfo> page, ApplicationVersion appVersion)
			throws PlatformException {
		try {
			page = customerCardInfoDao.getCustomerCardInfoPageByApp(page, appVersion);
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
	public List<Map<String, Object>> getCardAppinfoListByCci(Long ccId) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			CustomerCardInfo cci = this.load(ccId);
			CardInfo card = cci.getCard();
			List<CardApplication> caList = cardApplicationManager.getCardAppByCard(card);

			for (CardApplication ca : caList) {
				boolean showRule = false;
				boolean show = false;
				if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
					CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(card.getCardBaseInfo(),
							ca.getApplicationVersion().getApplication());
					if (null == checkCBA) {
						showRule = false;
					} else {
						showRule = true;
						show = false;
					}
				} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
					CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(card.getCardBaseInfo(),
							ca.getApplicationVersion().getApplication());
					if (null == checkCBA) {
						showRule = false;
					} else {
						showRule = true;
						if (checkCBA.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
							showRule = true;
							show = true;
						} else {
							show = false;
						}
					}
				}
				if (!showRule) {
					if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
						CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
						CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
						Application app = ca.getApplicationVersion().getApplication();
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
								&& app.getDeleteRule().intValue() == Application.DELETE_RULE_DELETE_ALL) {
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("appName", ca.getApplicationVersion().getApplication().getName());
							tempMap.put("appVer", ca.getApplicationVersion().getVersionNo());
							tempMap.put("loadFileRam", ca.getApplicationVersion().getLoadFileSpaceInfo().getRam());
							tempMap.put("loadFileRom", ca.getApplicationVersion().getLoadFileSpaceInfo().getNvm());
							tempMap.put("appletRam", ca.getApplicationVersion().getAppletSpaceInfo().getRam());
							tempMap.put("appletRom", ca.getApplicationVersion().getAppletSpaceInfo().getNvm());
							tempMap.put("status",
									ResourceBundleUtils.getMapMessage("customerapplication.status").get(String.valueOf(ca.getStatus())));
							tempMap.put("statusOriginal", ca.getStatus());
							tempMap.put("sdName", ca.getApplicationVersion().getApplication().getSd().getSdName());
							resultList.add(tempMap);
						}
					} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
						CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
						CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
								&& ca.getApplicationVersion().getApplication().getDeleteRule().intValue() == Application.DELETE_RULE_CAN_NOT) {
							continue;
						}
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
								|| cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("appName", ca.getApplicationVersion().getApplication().getName());
							tempMap.put("appVer", ca.getApplicationVersion().getVersionNo());
							tempMap.put("loadFileRam", ca.getApplicationVersion().getLoadFileSpaceInfo().getRam());
							tempMap.put("loadFileRom", ca.getApplicationVersion().getLoadFileSpaceInfo().getNvm());
							tempMap.put("appletRam", ca.getApplicationVersion().getAppletSpaceInfo().getRam());
							tempMap.put("appletRom", ca.getApplicationVersion().getAppletSpaceInfo().getNvm());
							tempMap.put("status",
									ResourceBundleUtils.getMapMessage("customerapplication.status").get(String.valueOf(ca.getStatus())));
							tempMap.put("statusOriginal", ca.getStatus());
							tempMap.put("sdName", ca.getApplicationVersion().getApplication().getSd().getSdName());
							resultList.add(tempMap);
						}
					} else {
						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("appName", ca.getApplicationVersion().getApplication().getName());
						tempMap.put("appVer", ca.getApplicationVersion().getVersionNo());
						tempMap.put("loadFileRam", ca.getApplicationVersion().getLoadFileSpaceInfo().getRam());
						tempMap.put("loadFileRom", ca.getApplicationVersion().getLoadFileSpaceInfo().getNvm());
						tempMap.put("appletRam", ca.getApplicationVersion().getAppletSpaceInfo().getRam());
						tempMap.put("appletRom", ca.getApplicationVersion().getAppletSpaceInfo().getNvm());
						tempMap.put("status",
								ResourceBundleUtils.getMapMessage("customerapplication.status").get(String.valueOf(ca.getStatus())));
						tempMap.put("statusOriginal", ca.getStatus());
						tempMap.put("sdName", ca.getApplicationVersion().getApplication().getSd().getSdName());
						resultList.add(tempMap);
					}
				} else if (showRule && show) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("appName", ca.getApplicationVersion().getApplication().getName());
					tempMap.put("appVer", ca.getApplicationVersion().getVersionNo());
					tempMap.put("loadFileRam", ca.getApplicationVersion().getLoadFileSpaceInfo().getRam());
					tempMap.put("loadFileRom", ca.getApplicationVersion().getLoadFileSpaceInfo().getNvm());
					tempMap.put("appletRam", ca.getApplicationVersion().getAppletSpaceInfo().getRam());
					tempMap.put("appletRom", ca.getApplicationVersion().getAppletSpaceInfo().getNvm());
					tempMap.put("status",
							ResourceBundleUtils.getMapMessage("customerapplication.status").get(String.valueOf(ca.getStatus())));
					tempMap.put("statusOriginal", ca.getStatus());
					tempMap.put("sdName", ca.getApplicationVersion().getApplication().getSd().getSdName());
					resultList.add(tempMap);
				}
			}
			return resultList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Map<String, Object>> getCardSDListByCci(Long ccId) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		try {
			CustomerCardInfo cci = this.load(ccId);
			CardInfo card = cci.getCard();
			List<CardSecurityDomain> csdList = cardSecurityDomainManager.getByCard(card);
			for (CardSecurityDomain csd : csdList) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("sdName", csd.getSd().getSdName());
				tempMap.put("appMod", ResourceBundleUtils.getMapMessage("sd.type").get(String.valueOf(csd.getSd().getModel())));
				tempMap.put("status", ResourceBundleUtils.getMapMessage("cardSecurityDomian.status").get(String.valueOf(csd.getStatus())));
				tempMap.put("sdKeyVersion", csd.getCurrentKeyVersion());
				tempMap.put("statusOriginal", csd.getStatus());
				formatSdSpace(tempMap, csd);
				resultList.add(tempMap);
			}
			return resultList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CustomerCardInfo> getByMobileNoNotCancelAndEnd(String mobileNo) {
		try {
			List<CustomerCardInfo> cciList = customerCardInfoDao.getByMobileNoNotCancelAndEnd(mobileNo);
			return cciList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CustomerCardInfo> getByCustomerThatEmigratedApplication(Application application, Customer customer) {
		try {
			return customerCardInfoDao.getByCustomerAndApplicationThatCardApplicationMigrateableTrueOrderByBindingDateDesc(application,
					customer);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CustomerCardInfo> getByCardThatEmigratedApplication(Application application, CardInfo card) {
		try {
			CustomerCardInfo customerCard = customerCardInfoDao.getByCardThatStatusNormalOrLostOrNotUse(card);
			return customerCardInfoDao.getByCustomerAndApplicationThatCardApplicationMigrateableTrueOrderByBindingDateDesc(application,
					customerCard.getCustomer());
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean checkCancelTermCardApp(Long ccId) {
		try {
			CustomerCardInfo cci = this.load(ccId);
			CardInfo card = cci.getCard();
			List<CardApplication> caList = cardApplicationDao.findByProperty("cardInfo", card);
			for (CardApplication ca : caList) {
				boolean showRule = false;
				boolean show = false;
				if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
					CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(card.getCardBaseInfo(),
							ca.getApplicationVersion().getApplication());
					if (null == checkCBA) {
						showRule = false;
					} else {
						showRule = true;
						show = false;
					}
				} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
					CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(card.getCardBaseInfo(),
							ca.getApplicationVersion().getApplication());
					if (null == checkCBA) {
						showRule = false;
					} else {
						showRule = true;
						if (checkCBA.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
							showRule = true;
							show = true;
						} else {
							show = false;
						}
					}
				}

				if (!showRule) {
					if (CardApplication.STATUS_DELETEABLE.contains(ca.getStatus())) {
						return false;
					} else if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
						CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
						CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
						Application app = ca.getApplicationVersion().getApplication();
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
								&& app.getDeleteRule().intValue() == Application.DELETE_RULE_DELETE_ALL) {
							return false;
						}
					} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
						CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
						CardBaseApplication cba = cardBaseApplicationDao.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
								&& ca.getApplicationVersion().getApplication().getDeleteRule().intValue() == Application.DELETE_RULE_CAN_NOT) {
							return true;
						}
						if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
								|| cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
							return false;
						}
					}
				} else if (showRule && show) {
					return false;
				}
			}
			return true;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean checkMobileNoLocation(String cardNo, Long appId, Map<String, Object> forceMap) {
		try {
			CustomerCardInfo customerCardInfo = getByCardNoAndOpt(cardNo);
			if (null != customerCardInfo) {
				String mobileNo = customerCardInfo.getMobileNo();
				String province = mobileSectionManager.getProvinceByMobile(mobileNo);
				if (null != province) {
					Application app = applicationManager.load(appId);
					if (app.getLocation().equals(province) || app.getLocation().equals("全网")) {
						return true;
					} else {
						forceMap.put("msg", "应用属于" + app.getLocation() + "，您的手机号属于" + province + "，可能无法使用该应用.是否确定继续操作？");
						return false;
					}
				} else {
					throw new PlatformException(PlatformErrorCode.NO_OPEN_NO_DOWN);
				}
			} else {
				throw new PlatformException(PlatformErrorCode.CUSTOMER_CARD_NOT_BIND);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<CustomerCardInfo> getByMobileNoAllAndPage(Page<CustomerCardInfo> page, String mobileNo) {
		try {
			return customerCardInfoDao.getByMobileNoAllAndPage(page, mobileNo);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Map<String, Object>> getCardApplicationsByMobileNoForAdmin(String moibleNo, Page<CardApplication> page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			List<CustomerCardInfo> cciList = getByMobileNo(moibleNo);
			if (CollectionUtils.isNotEmpty(cciList)) {
				for (CustomerCardInfo cci : cciList) {
					List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
					filters.add(new PropertyFilter("cardInfo", JoinType.I, "id", MatchType.EQ, PropertyType.L, String.valueOf(cci.getCard()
							.getId())));
					Page<CardApplication> cardAppList = cardApplicationDao.findPage(page, filters);
					for (CardApplication ca : cardAppList.getResult()) {

						CardInfo card = ca.getCardInfo();
						// 对是否采取默认规则进行判断
						boolean showRule = false;
						boolean show = true;
						if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								show = false;
							}
						} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								if (checkCBA.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
									showRule = true;
									show = true;
								} else {
									show = false;
								}
							}
						}

						if (!showRule) {
							if (ca.getStatus().intValue() != CardApplication.STATUS_UNDOWNLOAD) {
								if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									Application app = ca.getApplicationVersion().getApplication();
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& app.getDeleteRule().intValue() == Application.DELETE_RULE_DELETE_ALL) {
										Map<String, Object> map = buildAppMap(ca);
										map.put("cciName", cci.getName());
										map.put("userName", cci.getCustomer().getSysUser().getUserName());
										map.put("cciId", cci.getId());
										list.add(map);
									}
								} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& ca.getApplicationVersion().getApplication().getDeleteRule().intValue() == Application.DELETE_RULE_CAN_NOT) {
										continue;
									}
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											|| cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
										Map<String, Object> map = buildAppMap(ca);
										map.put("cciName", cci.getName());
										map.put("userName", cci.getCustomer().getSysUser().getUserName());
										map.put("cciId", cci.getId());
										list.add(map);
									}
								} else {
									Map<String, Object> map = buildAppMap(ca);
									map.put("cciName", cci.getName());
									map.put("userName", cci.getCustomer().getSysUser().getUserName());
									map.put("cciId", cci.getId());
									list.add(map);
								}
							}
						} else if (showRule && show) {
							Map<String, Object> map = buildAppMap(ca);
							map.put("cciName", cci.getName());
							map.put("userName", cci.getCustomer().getSysUser().getUserName());
							map.put("cciId", cci.getId());
							list.add(map);
						}
					}
				}
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return list;
	}

	@Override
	public boolean getMigratableAppList(Long ccId) {
		try {
			CustomerCardInfo cci = this.load(ccId);
			CardInfo card = cci.getCard();
			List<CardApplication> caList = cardApplicationManager.getCaListMigratable(card);
			for (CardApplication ca : caList) {
				DesiredOperation desireOperation = desiredOperationManager.getByAidAndProcedureNameAndCustomerCardThatNotExcuted(ca
						.getApplicationVersion().getApplication().getAid(), LocalTransaction.Operation.EMIGRATE_APP.name(), cci);
				if (null != desireOperation) {
					return true;
				}
			}
			return false;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void adminActiveCard(Long customerCardId) {
		try {
			CustomerCardInfo cci = customerCardInfoDao.load(Long.valueOf(customerCardId));
			List<CustomerCardInfo> mayExist = customerCardInfoDao.findByProperty("mobileNo", cci.getMobileNo());
			for (CustomerCardInfo mayCci : mayExist) {
				if (mayCci.getId() == customerCardId && mayCci.getStatus() == CustomerCardInfo.STATUS_NOT_USE) {
					cci.setActive(CustomerCardInfo.ACTIVED);
					cci.setBindingDate(Calendar.getInstance());
					cci.setStatus(CustomerCardInfo.STATUS_NORMAL);
					customerCardInfoDao.saveOrUpdate(cci);
					saveCustomerCardContact(cci);// 保存预制卡片批次信息
					checkCardSpace(cci);
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
	public CustomerCardInfo getByCardThatStatusNormalOrLost(CardInfo card) {
		try {
			return customerCardInfoDao.getByCardThatStatusNormalOrLost(card);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Map<String, Object>> getSDMaplistByCustomerCard(Long ccId) {
		try {
			CustomerCardInfo cci = customerCardInfoDao.load(ccId);
			CardInfo cardInfo = cci.getCard();
			List<CardSecurityDomain> cardSDList = cardSecurityDomainManager.getByCard(cardInfo);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (CardSecurityDomain csd : cardSDList) {
				if (csd.getStatus().intValue() != CardSecurityDomain.STATUS_UNCREATE) {
					Map<String, Object> resultMap = csd.getSd().toMap(null, null);
					resultMap.put("sdName", csd.getSd().getSdName());
					resultMap.put("statusOrg", csd.getStatus());
					resultMap.put("status",
							ResourceBundleUtils.getMapMessage("cardSecurityDomain.status").get(String.valueOf(csd.getStatus())));
					formatSdSpace(resultMap, csd);
					resultList.add(resultMap);
				}
			}
			return resultList;
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void formatSdSpace(Map<String, Object> resultMap, CardSecurityDomain csd) {
		Integer sdTotalRam = 0;
		Long sdTotalRom = 0L;
		if (csd.getSd().isIsd() || csd.getSd().getSpaceRule().intValue() == SecurityDomain.UNFIXABLE_SPACE) {
			sdTotalRam = csd.getCard().getCardBaseInfo().getTotalSpace().getRam();
			sdTotalRom = csd.getCard().getCardBaseInfo().getTotalSpace().getNvm();
		} else {
			sdTotalRam = csd.getSd().getManagedVolatileSpace();
			sdTotalRom = csd.getSd().getManagedNoneVolatileSpace();
		}
		Space sdAviliableSpace = csd.getAviliableSpace();
		Integer sdUsedRam = sdTotalRam - sdAviliableSpace.getRam();
		Long sdUsedRom = sdTotalRom - sdAviliableSpace.getNvm();
		resultMap.put("sdTotalRam", sdTotalRam);
		resultMap.put("sdTotalRom", sdTotalRom);
		resultMap.put("sdAviliableRam", sdAviliableSpace.getRam());
		resultMap.put("sdAviliableRom", sdAviliableSpace.getNvm());
		resultMap.put("sdUsedRam", sdUsedRam);
		resultMap.put("sdUsedRom", sdUsedRom);
	}

	@Override
	public CustomerCardInfo getCCIByCustomerAndCard(Customer customer, CardInfo card) {
		try {
			return this.customerCardInfoDao.getCCIByCustomerAndCard(customer, card);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<CustomerCardInfo> getCustomerCardLikeCustomerAndCCName(Customer customer, String phoneName) {
		try {
			return this.customerCardInfoDao.getCustomerCardLikeCustomerAndCCName(customer, phoneName);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<Map<String, Object>> getAllAppListByUserName(String userName) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Set<Application> appSet = new HashSet<Application>();
			Customer customer = customerManager.getCustomerByUserName(userName);
			List<CustomerCardInfo> cciList = customerCardInfoDao.getCustomerCardByCustomerThatNormAndLostAndNotUse(customer);
			if (CollectionUtils.isNotEmpty(cciList)) {
				for (CustomerCardInfo cci : cciList) {
					List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
					filters.add(new PropertyFilter("cardInfo", JoinType.I, "id", MatchType.EQ, PropertyType.L, String.valueOf(cci.getCard()
							.getId())));
					List<CardApplication> cardAppList = cardApplicationDao.find(filters);
					for (CardApplication ca : cardAppList) {
						CardInfo card = ca.getCardInfo();
						// 对是否采取默认规则进行判断
						boolean showRule = false;
						boolean show = false;
						if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								show = false;
							}
						} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								if (checkCBA.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
									showRule = true;
									show = true;
								} else {
									show = false;
								}
							}
						}

						if (!showRule) {
							if (ca.getStatus().intValue() != CardApplication.STATUS_UNDOWNLOAD) {
								if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									Application app = ca.getApplicationVersion().getApplication();
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& app.getDeleteRule().intValue() == Application.DELETE_RULE_DELETE_ALL) {
										appSet.add(app);
									}
								} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& ca.getApplicationVersion().getApplication().getDeleteRule().intValue() == Application.DELETE_RULE_CAN_NOT) {
										continue;
									}
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											|| cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
										appSet.add(ca.getApplicationVersion().getApplication());
									}
								} else {
									appSet.add(ca.getApplicationVersion().getApplication());
								}
							}
						} else if (showRule && show) {
							appSet.add(ca.getApplicationVersion().getApplication());
						}

					}
				}
			}
			for (Application app : appSet) {
				list.add(app.toMap(null, null));
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return list;
	}

	private void buildCCIInfo(CustomerCardInfo cci, Map<String, Object> map) {
		map.put("cciName", cci.getName());
		map.put("cciCardNo", cci.getCard().getCardNo());
		map.put("cciStatus", cci.getStatus());
		map.put("userName", cci.getCustomer().getSysUser().getUserName());
		map.put("cciBlack", cci.getInBlack());
		map.put("cciId", cci.getId());
	}

	@Override
	public List<Map<String, Object>> getAllCardAppListByUserName(String userName) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			List<CustomerCardInfo> cciList = customerCardInfoDao.getCustomerCardByCustomerThatNormAndLost(customer);
			if (CollectionUtils.isNotEmpty(cciList)) {
				for (CustomerCardInfo cci : cciList) {
					List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
					filters.add(new PropertyFilter("cardInfo", JoinType.I, "id", MatchType.EQ, PropertyType.L, String.valueOf(cci.getCard()
							.getId())));
					List<CardApplication> cardAppList = cardApplicationDao.find(filters);
					for (CardApplication ca : cardAppList) {
						CardInfo card = ca.getCardInfo();
						boolean showRule = false;
						boolean show = false;
						if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								show = false;
							}
						} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								if (checkCBA.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
									showRule = true;
									show = true;
								} else {
									show = false;
								}
							}
						}
						if (!showRule) {
							if (ca.getStatus().intValue() != CardApplication.STATUS_UNDOWNLOAD) {
								if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									Application app = ca.getApplicationVersion().getApplication();
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& app.getDeleteRule().intValue() == Application.DELETE_RULE_DELETE_ALL) {
										Map<String, Object> map = buildAppMap(ca);
										buildCCIInfo(cci, map);
										list.add(map);
									}
								} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& ca.getApplicationVersion().getApplication().getDeleteRule().intValue() == Application.DELETE_RULE_CAN_NOT) {
										continue;
									}
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											|| cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
										Map<String, Object> map = buildAppMap(ca);
										buildCCIInfo(cci, map);
										list.add(map);
									}
								} else {
									Map<String, Object> map = buildAppMap(ca);
									buildCCIInfo(cci, map);
									list.add(map);
								}
							}
						} else if (showRule && show) {
							Map<String, Object> map = buildAppMap(ca);
							buildCCIInfo(cci, map);
							list.add(map);
						}
					}
				}
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getCardApplicationByUserAndAppId(String userName, Long appId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			Application queryApp = applicationManager.load(appId);
			List<CustomerCardInfo> cciList = customerCardInfoDao.getCustomerCardByCustomerThatNormAndLostAndNotUse(customer);
			if (CollectionUtils.isNotEmpty(cciList)) {
				for (CustomerCardInfo cci : cciList) {
					List<CardApplication> cardAppList = cardApplicationDao.getByCardAndApplication(cci.getCard(), queryApp);
					for (CardApplication ca : cardAppList) {
						CardInfo card = ca.getCardInfo();
						boolean showRule = false;
						boolean show = false;
						if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								show = false;
							}
						} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
							CardBaseApplication checkCBA = cardBaseApplicationManager.getByCardBaseAndApplicationThatPreset(
									card.getCardBaseInfo(), ca.getApplicationVersion().getApplication());
							if (null == checkCBA) {
								showRule = false;
							} else {
								showRule = true;
								if (checkCBA.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
									showRule = true;
									show = true;
								} else {
									show = false;
								}
							}
						}

						if (!showRule) {
							if (ca.getStatus().intValue() != CardApplication.STATUS_UNDOWNLOAD) {
								if (ca.getStatus().intValue() == CardApplication.STATUS_DOWNLOADED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									Application app = ca.getApplicationVersion().getApplication();
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& app.getDeleteRule().intValue() == Application.DELETE_RULE_DELETE_ALL) {
										Map<String, Object> map = buildAppMap(ca);
										buildCCIInfo(cci, map);
										list.add(map);
									}
								} else if (ca.getStatus().intValue() == CardApplication.STATUS_INSTALLED) {
									CardBaseInfo cbi = ca.getCardInfo().getCardBaseInfo();
									CardBaseApplication cba = cardBaseApplicationDao
											.getByCardBaseAndAppver(cbi, ca.getApplicationVersion());
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											&& ca.getApplicationVersion().getApplication().getDeleteRule().intValue() == Application.DELETE_RULE_CAN_NOT) {
										continue;
									}
									if (cba.getPresetMode().intValue() == CardBaseApplication.MODE_EMPTY
											|| cba.getPresetMode().intValue() == CardBaseApplication.MODE_CREATE) {
										Map<String, Object> map = buildAppMap(ca);
										buildCCIInfo(cci, map);
										list.add(map);
									}
								} else {
									Map<String, Object> map = buildAppMap(ca);
									buildCCIInfo(cci, map);
									list.add(map);
								}
							}
						} else if (showRule && show) {
							Map<String, Object> map = buildAppMap(ca);
							buildCCIInfo(cci, map);
							list.add(map);
						}
					}
				}
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return list;
	}

	@Override
	public void bindCardAsActivedAndCreatCustomerIfNeed(String mobileNo, String cardNo) {
		try {
			Customer customer = customerManager.getByUserNameOrEmailOrMobileNo(mobileNo);
			if (null == customer) {// 如果当前手机号未注册，用户注册
				SysUser user = new SysUser();
				user.setUserName(mobileNo);
				user.setMobile(mobileNo);
				user.setPassword("000000");
				user.setStatus(SysUser.USER_STATUS.ENABLED.getValue());
				userManager.addUser(user, SysRole.SpecialRoleType.CUSTOMER);
				user.setSysRole(roleManager.getRoleByName(SysRole.SpecialRoleType.CUSTOMER.name()));

				customer = new Customer();
				customer.setSysUser(user);
				customer.setActive(Customer.ACTIVE_YES);

				customerManager.saveOrUpdate(customer);
			}

			Map<String, String> param = new HashMap<String, String>();
			param.put("userName", mobileNo);
			param.put("mobileNo", mobileNo);
			param.put("cardNo", cardNo);
			CustomerCardInfo customerCard = bindCard(param, Boolean.FALSE);
			adminActiveCard(customerCard.getId());

			CardInfo card = customerCard.getCard();
			card.setRegisterable(null);
			cardManager.saveOrUpdate(card);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CustomerCardInfo getByCardNoThatStatusLost(String cardNo) {
		try {
			return customerCardInfoDao.getByCardNoThatStatusLost(cardNo);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void sysnLostToCancel(CustomerCardInfo cci) {
		try {
			if(cci.isInBlack()) {
				cci.setInBlack(CustomerCardInfo.NOT_INBLACK);
				CardBlackList cardBlackList = new CardBlackList();
				cardBlackList.setCustomerCardInfo(cci);
				cardBlackList.setOperateDate(Calendar.getInstance());
				cardBlackList.setType(CardBlackList.TYPE_FOR_LOST_TO_CANCEL);
				cardBlackList.setReason("挂失后退订所有应用，自动注销移出黑名单");
				this.saveOrUpdate(cci);
				cardBlackListDao.saveOrUpdate(cardBlackList);
			}
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public CustomerCardInfo getByCardNoThatNormalOrLosted(String cardNo) {
		try {
			CardInfo cardInfo = cardInfoDao.findUniqueByProperty("cardNo", cardNo);
			if (null == cardInfo) {
				throw new PlatformException(PlatformErrorCode.CARD_IS_NOT_SUPPOT);
			}
			return customerCardInfoDao.findByCardNoThatNormalOrLosted(cardInfo);
		} catch (PlatformException pe) {
			throw pe;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}