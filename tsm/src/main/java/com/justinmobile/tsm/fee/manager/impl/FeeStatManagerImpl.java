package com.justinmobile.tsm.fee.manager.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.util.Calendar;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.ApplicationDao;
import com.justinmobile.tsm.application.dao.SecurityDomainDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardApplicationDao;
import com.justinmobile.tsm.card.dao.CardBaseApplicationDao;
import com.justinmobile.tsm.card.dao.CardBaseInfoDao;
import com.justinmobile.tsm.card.dao.CardBaseSecurityDomainDao;
import com.justinmobile.tsm.card.dao.CardSecurityDomainDao;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardBaseApplication;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.domain.CardBaseSecurityDomain;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.customer.dao.CustomerCardInfoDao;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.fee.dao.FeeRuleFunctionDao;
import com.justinmobile.tsm.fee.dao.FeeRuleSpaceDao;
import com.justinmobile.tsm.fee.dao.FeeStatDao;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;
import com.justinmobile.tsm.fee.domain.FeeRuleSpace;
import com.justinmobile.tsm.fee.domain.FeeStat;
import com.justinmobile.tsm.fee.manager.FeeStatManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.Operation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.SessionType;

@Service("feeStatManager")
@Transactional
public class FeeStatManagerImpl extends EntityManagerImpl<FeeStat, FeeStatDao> implements FeeStatManager {
	@Autowired
	private FeeStatDao fsDao;
	@Autowired
	private ApplicationDao appDao;
	@Autowired
	private FeeRuleSpaceDao frsDao;
	@Autowired
	private FeeRuleFunctionDao frfDao;
	@Autowired
	private CardApplicationDao caDao;
	@Autowired
	private SecurityDomainDao sdDao;
	@Autowired
	private CardSecurityDomainDao csdDao;
	@Autowired
	private CardBaseInfoDao cbiDao;
	@Autowired
	private CardBaseApplicationDao cbaDao;
	@Autowired
	private CardBaseSecurityDomainDao cbsdDao;
	@Autowired
	private CustomerCardInfoDao cciDao;
	private Logger log = LoggerFactory.getLogger(FeeStatManagerImpl.class);
	// 数据显示格式
	DecimalFormat format = new DecimalFormat("0.00");

	@Override
	/**
	 * 生成功能计费数据记录
	 */
	public void genStatRecord(LocalTransaction lt) {
		String procedure = lt.getProcedureName();
		// 只处理主流程的，非主流程的不处理
		if (lt.isMainTransaction()) {
			if (procedure.equalsIgnoreCase("DOWNLOAD_APP")) {
				downloadAppRecord(lt);
				// 下载应用
			} else if (procedure.equalsIgnoreCase("DELETE_APP")) {
				deleteAppRecord(lt);
				// 删除应用
			} else if (procedure.equalsIgnoreCase("CREATE_SD")) {
				createSdRecord(lt);
				// 创建SD
			} else if (procedure.equalsIgnoreCase("DELETE_SD")) {
				deleteSdRecord(lt);
				// 删除SD
			} else if (procedure.equalsIgnoreCase("UNLOCK_APP")) {
				genFunFeeRecord(lt, FeeStat.APP);
				// 解锁应用
			} else if (procedure.equalsIgnoreCase("LOCK_APP")) {
				genFunFeeRecord(lt, FeeStat.APP);
				// 锁定应用
			} else if (procedure.equalsIgnoreCase("PERSONALIZE_APP")) {
				genFunFeeRecord(lt, FeeStat.APP);
				// 个人化应用
			} else if (procedure.equalsIgnoreCase("UPDATE_KEY")) {
				genFunFeeRecord(lt, FeeStat.SD);
				// 更新密钥功能
			}
		}
	}

	/**
	 * 下载App的逻辑处理
	 */
	private void downloadAppRecord(LocalTransaction lt) {
		try {
			Application app = appDao.findUniqueByProperty("aid", lt.getAid());
			CardApplication ca = caDao.getByCardNoAid(lt.getCardNo(), lt.getAid());
			CardSecurityDomain csd = csdDao.getByCardNoAid(lt.getCardNo(), app.getSd().getAid());
			FeeStat fs = new FeeStat();
			BeanUtils.copyProperties(lt, fs);
			fs.setSessionId(lt.getLocalSessionId());
			// 判断该应用是否需要订购和该应用的安全域模式是固定空间模式,如果安全域未收费，则硬挨收取安全域的空间费用
			if (app.getSd().isSpaceFixed()) {
				if (!fsDao.hasBilled(app.getSd().getAid(), lt.getCardNo(), lt.getMobileNo())) {
					setBasicProperty(fs, app.getSd().getSp(), FeeStat.TYPE_SPACE);
					fs.setAid(app.getSd().getAid());
					fs.setAppName(app.getSd().getSdName());
					// fs.setOperateName(SessionType.valueOf(lt.getSessionType()).getDescription());
					fs.setPrice(getPriceBySd(app.getSd()));
					saveOrUpdate(fs);
				}
				// 更新安全域计费时间
				csd.setLastFeeTime(Calendar.getInstance().getTime());
				csdDao.saveOrUpdate(csd);
			}
			// 判断该应用是否需要订购和该应用的安全域模式是应用大小模式,如果安全域未收费，则应该收取安全域的空间费用
			if (!app.getNeedSubscribe() && !app.getSd().isSpaceFixed()) {

				if (!fsDao.hasBilled(lt.getAid(), lt.getCardNo(), lt.getMobileNo())) {
					// 计费前先检查本月内该应用是否已经计费,如果未计费则进行计费
					setBasicProperty(fs, app.getSp(), FeeStat.TYPE_SPACE);
					fs.setAppName(app.getName());
					fs.setVersion(lt.getAppVersion());
					// fs.setOperateName(SessionType.valueOf(lt.getSessionType()).getDescription());
					fs.setPrice(getPriceByApp(ca.getApplicationVersion()));
					saveOrUpdate(fs);
				}
				ca.setLastFeeTime(Calendar.getInstance().getTime());
				caDao.saveOrUpdate(ca);
				// 更新CardApplication计费时间
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		genFunFeeRecord(lt, FeeStat.APP);
	}

	/**
	 * 删除App的逻辑处理
	 */
	private void deleteAppRecord(LocalTransaction lt) {
		try {
			CardApplication ca = caDao.getByCardNoAid(lt.getCardNo(), lt.getAid());
			ca.setLastFeeTime(null);
			caDao.saveOrUpdate(ca);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		genFunFeeRecord(lt, FeeStat.APP);
	}

	/**
	 * 创建SD的逻辑处理
	 */
	private void createSdRecord(LocalTransaction lt) {
		String aid = lt.getAid();
		try {
			SecurityDomain sd = sdDao.getByAid(aid);
			// 判断安全域的模式如果为签约空间模式和本计费月内还未计费则进行计费
			if (sd.isSpaceFixed()) {
				if (!hasBilled(aid, lt.getCardNo(), lt.getMobileNo())) {
					FeeStat fs = new FeeStat();
					BeanUtils.copyProperties(lt, fs);
					setBasicProperty(fs, sd.getSp(), FeeStat.TYPE_SPACE);
					fs.setSessionId(lt.getLocalSessionId());
					fs.setAppName(sd.getSdName());
					fs.setVersion(lt.getAppVersion());
					fs.setOperateName(Operation.valueOf(lt.getProcedureName()).getSessionType().getDescription());
					fs.setPrice(getPriceBySd(sd));
					saveOrUpdate(fs);
				}
				// 更新card_security_domain的last_fee_time
				CardSecurityDomain csd = csdDao.getByCardNoAid(lt.getCardNo(), lt.getAid());
				csd.setLastFeeTime(Calendar.getInstance().getTime());
				csdDao.saveOrUpdate(csd);
			}
			genFunFeeRecord(lt, FeeStat.SD);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 删除SD的逻辑处理
	 */
	private void deleteSdRecord(LocalTransaction lt) {
		try {
			CardSecurityDomain csd = csdDao.getByCardNoAid(lt.getCardNo(), lt.getAid());
			csd.setLastFeeTime(null);
			csdDao.saveOrUpdate(csd);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		genFunFeeRecord(lt, FeeStat.SD);
	}

	/**
	 * 生成功能计费记录
	 */
	private void genFunFeeRecord(LocalTransaction lt, String type) {
		try {
			SpBaseInfo sp;
			String appName;
			if (type.equalsIgnoreCase("app")) {
				Application app = appDao.findUniqueByProperty("aid", lt.getAid());
				sp = app.getSp();
				appName = app.getName();
			} else {
				SecurityDomain sd = sdDao.getByAid(lt.getAid());
				sp = sd.getSp();
				appName = sd.getSdName();
			}
			FeeStat fs = new FeeStat();
			BeanUtils.copyProperties(lt, fs);
			setBasicProperty(fs, sp, FeeStat.TYPE_FUNCTION);
			fs.setSessionId(lt.getLocalSessionId());
			fs.setAppName(appName);
			fs.setVersion(lt.getAppVersion());
			fs.setOperateName(genNameByKey(lt.getProcedureName()));
			// fs.setOperateName(SessionType.valueOf(lt.getSessionType()).getDescription());
			fs.setPrice(getFunPriceBySp(sp.getId()));
			saveOrUpdate(fs);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 生成订购应用，退订应用的功能费记录
	 */
	private void genFunFeeRecord(String aid, String version, String cardNo, String mobileNo, String funCode) {
		try {
			Application app = appDao.findUniqueByProperty("aid", aid);
			SpBaseInfo sp = app.getSp();
			String appName = app.getName();
			FeeStat fs = new FeeStat();
			fs.setAid(aid);
			fs.setCardNo(cardNo);
			fs.setMobileNo(mobileNo);
			setBasicProperty(fs, sp, FeeStat.TYPE_FUNCTION);
			fs.setAppName(appName);
			fs.setVersion(version);
			fs.setOperateName(funCode);
			fs.setPrice(getFunPriceBySp(sp.getId()));
			saveOrUpdate(fs);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 根据AID，CardNo,MobileNo判断是否已经计费
	 */
	@Override
	public boolean hasBilled(String aid, String cardNo, String mobileNo) {
		try {
			return fsDao.hasBilled(aid, cardNo, mobileNo);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 用户订购成功后的调用接口
	 */
	@Override
	public void subscribeAppStatRecord(String aid, String cardNo, String mobileNo) {
		CardApplication ca;
		try {
			Application app = appDao.findUniqueByProperty("aid", aid);
			ca = caDao.getByCardNoAid(cardNo, aid);
			CardSecurityDomain csd = csdDao.getByCardNoAid(cardNo, app.getSd().getAid());
			FeeStat fs = new FeeStat();
			fs.setAid(aid);
			fs.setCardNo(cardNo);
			fs.setMobileNo(mobileNo);
			// 判断该应用是否需要订购和该应用的安全域模式是固定空间模式,如果安全域未收费，则硬挨收取安全域的空间费用
			if (app.getSd().isSpaceFixed()) {
				if (!fsDao.hasBilled(app.getSd().getAid(), cardNo, mobileNo)) {
					setBasicProperty(fs, app.getSd().getSp(), FeeStat.TYPE_SPACE);
					fs.setAid(app.getSd().getAid());
					fs.setAppName(app.getSd().getSdName());
					fs.setOperateName(SessionType.SD_CREATE.getDescription());
					fs.setPrice(getPriceBySd(app.getSd()));
					saveOrUpdate(fs);
				}
				csd.setLastFeeTime(Calendar.getInstance().getTime());
				csdDao.saveOrUpdate(csd);
			}
			// 更新安全域计费时间
			// 判断该应用是否需要订购和该应用的安全域模式是应用大小模式,如果安全域未收费，则应该收取安全域的空间费用
			if (app.getNeedSubscribe() && !app.getSd().isSpaceFixed()) {
				if (!fsDao.hasBilled(aid, cardNo, mobileNo)) {
					// 计费前先检查本月内该应用是否已经计费,如果未计费则进行计费
					setBasicProperty(fs, app.getSp(), FeeStat.TYPE_SPACE);
					fs.setAppName(app.getName());
					fs.setVersion(ca.getApplicationVersion().getVersionNo());
					fs.setOperateName(SessionType.SERVICE_SUBSCRIBE.getDescription());
					fs.setPrice(getPriceByApp(ca.getApplicationVersion()));
					saveOrUpdate(fs);
				}
				ca.setLastFeeTime(Calendar.getInstance().getTime());
				caDao.saveOrUpdate(ca);
				// 更新CardApplication计费时间
			}

		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

		genFunFeeRecord(aid, ca.getApplicationVersion().getVersionNo(), cardNo, mobileNo, SessionType.SERVICE_SUBSCRIBE.getDescription());

	}

	/**
	 * 用户退订成功后的调用接口
	 */
	@Override
	public void unSubscribeAppStatRecord(String aid, String cardNo, String mobileNo) {
		CardApplication ca;
		try {
			ca = caDao.getByCardNoAid(cardNo, aid);
			ca.setLastFeeTime(null);
			caDao.saveOrUpdate(ca);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		genFunFeeRecord(aid, ca.getApplicationVersion().getVersionNo(), cardNo, mobileNo, SessionType.SERVICE_UNSUBSCRIBE.getDescription());
	}

	/**
	 * 根据SPID获取某一段时间内用户的订购总数
	 */
	@Override
	public Long getCounthasBilled(Long spId, String start, String end) {
		try {
			return fsDao.getCountFunctionBilled(spId, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 生成计费统计数据
	 */
	@Override
	public List<FeeStat> getFeeStat(Long spId, Date start, Date end, Integer type) {
		try {
			return fsDao.getFeeStat(spId, start, end, type);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 根据SpID和用户总数获取该SP的包月功能计费规则
	 */
	@Override
	public FeeRuleFunction getMonthFrfBySpAndSize(Long spId, Long size) {
		try {
			return frfDao.getMonthFrpBySpAndSize(spId, size);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 用户绑定终端的调用接口
	 */
	@Override
	public void genPerStatRecord(String mobileNo, String cardNo) {
		try {
			CardBaseInfo cbi = cbiDao.getCardBaseInfoByCardNo(cardNo);
			List<CardBaseApplication> listApp = cbaDao.getByCardBase(cbi);
			Application app;
			for (CardBaseApplication cba : listApp) {
				app = cba.getApplicationVersion().getApplication();
				CardApplication ca = caDao.getByCardNoAid(cardNo, app.getAid());
				// 判断预置应用是否收费, 计费前先检查本月内该应用是否已经计费,如果未计费则进行计费
				if (app.getPresetChargeCondition() == Application.PRESET_CHARGE_CONDITION_REGISTED && !app.getSd().isSpaceFixed()) {
					if (!fsDao.hasBilled(app.getAid(), cardNo, mobileNo)) {
						FeeStat fs = new FeeStat();
						setBasicProperty(fs, app.getSp(), FeeStat.TYPE_SPACE);
						fs.setAid(app.getAid());
						fs.setMobileNo(mobileNo);
						fs.setCardNo(cardNo);
						fs.setAppName(app.getName());
						fs.setVersion(cba.getApplicationVersion().getVersionNo());
						fs.setOperateName(genNameByKey("PRE_APP"));
						fs.setPrice(getPriceByApp(cba.getApplicationVersion()));
						fsDao.saveOrUpdate(fs);
					}
					// 更新计费时间
					// 通过ApplicationVersion和CardBaseInfo获取CardApplication
					ca.setLastFeeTime(Calendar.getInstance().getTime());
					caDao.saveOrUpdate(ca);
				}

			}
			List<CardBaseSecurityDomain> listSd = cbsdDao.getByCardBase(cbi);
			SecurityDomain sd;
			for (CardBaseSecurityDomain cbsd : listSd) {
				sd = cbsd.getSecurityDomain();
				if (sd.isSpaceFixed()) {
					if (!fsDao.hasBilled(sd.getAid(), cardNo, mobileNo)) {
						FeeStat fs = new FeeStat();
						setBasicProperty(fs, sd.getSp(), FeeStat.TYPE_SPACE);
						fs.setAid(sd.getAid());
						fs.setCardNo(cardNo);
						fs.setMobileNo(mobileNo);
						fs.setAppName(sd.getSdName());
						fs.setOperateName(genNameByKey("PRE_SD"));
						fs.setPrice(getPriceBySd(sd));
						fsDao.saveOrUpdate(fs);
					}
					// 更新card_security_domain的last_fee_time
					CardSecurityDomain csd = csdDao.getByCardNoAid(cardNo, sd.getAid());
					csd.setLastFeeTime(Calendar.getInstance().getTime());
					csdDao.saveOrUpdate(csd);

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
	public void genFeeStatTask() {

		try {
			Date now = Calendar.getInstance().getTime();
			List<CardApplication> listCa = caDao.getByLastFeeTime(now);
			CustomerCardInfo cci = null;
			Application app = null;
			SecurityDomain sd = null;
			FeeStat fs = null;

			for (CardApplication ca : listCa) {
				cci = cciDao.getByCard(ca.getCardInfo());
				app = ca.getApplicationVersion().getApplication();

				if (!app.getSd().isSpaceFixed() && !fsDao.hasBilled(app.getAid(), ca.getCardInfo().getCardNo(), cci.getMobileNo())) {
					// 应用大小模式进行计费

					fs = new FeeStat();
					setBasicProperty(fs, app.getSp(), FeeStat.TYPE_SPACE);
					fs.setAid(app.getAid());
					fs.setCardNo(ca.getCardInfo().getCardNo());
					if (null != cci) {
						fs.setMobileNo(cci.getMobileNo());
					}
					fs.setAppName(app.getName());
					fs.setVersion(ca.getApplicationVersion().getVersionNo());
					fs.setOperateName(genNameByKey("MONTH_APP"));
					fs.setPrice(getPriceByApp(ca.getApplicationVersion()));
					fsDao.saveOrUpdate(fs);

					ca.setLastFeeTime(Calendar.getInstance().getTime());
					caDao.saveOrUpdate(ca);

				}
			}

			List<CardSecurityDomain> listCsd = csdDao.getByLastFeeTime(now);
			log.info("listCsd.size()==" + listCsd.size());
			for (CardSecurityDomain csd : listCsd) {
				sd = csd.getSd();
				// 固定空间模式进行计费

				cci = cciDao.getByCard(csd.getCard());
				if (sd.isSpaceFixed() && !fsDao.hasBilled(sd.getAid(), cci.getCard().getCardNo(), cci.getMobileNo())) {
					fs = new FeeStat();
					setBasicProperty(fs, sd.getSp(), FeeStat.TYPE_SPACE);
					fs.setAid(csd.getSd().getAid());
					fs.setCardNo(csd.getCard().getCardNo());
					if (null != cci) {
						fs.setMobileNo(cci.getMobileNo());
					}
					fs.setAppName(sd.getSdName());
					fs.setOperateName(genNameByKey("MONTH_SD"));
					fs.setPrice(getPriceBySd(sd));
					fsDao.saveOrUpdate(fs);

					csd.setLastFeeTime(Calendar.getInstance().getTime());
					csdDao.saveOrUpdate(csd);

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
	public List<FeeStat> getFunctionBilled(Long spId, String start, String end) {
		try {
			return fsDao.getFunctionBilled(spId, start, end);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	/**
	 * 根据安全域获取该安全域的计费价格
	 */
	private double getPriceBySd(SecurityDomain sd) {
		double price = 0.00;
		// 获取安全域的计费规则
		FeeRuleSpace frs = frsDao.getFrpByAid(sd.getAid());
		// 计算安全域的单价
		if (null != frs) {
			if (frs.getPattern() == FeeRuleSpace.PATTERN_SAPCE) {
				double size = sd.getManagedNoneVolatileSpace() + sd.getManagedVolatileSpace();
				int scale = (int) Math.ceil(size / frs.getGranularity());
				price = (double) frs.getPrice() * scale / 100;
			} else if (frs.getPattern() == FeeRuleSpace.PATTERN_APP) {
				price = (double) frs.getPrice() / 100;
			}

		}
		return new Double(format.format(price));
	}

	/**
	 * 根据App和CardApplication获取该应用的计费价格
	 */
	private double getPriceByApp(ApplicationVersion appVer) {
		double price = 0.00;
		// 获取该应用的空间计费规则
		FeeRuleSpace frs = frsDao.getFrpByAid(appVer.getApplication().getAid());
		if (frs != null) {
			if (frs.getPattern() == FeeRuleSpace.PATTERN_APP) {
				price = (double) frs.getPrice() / 100;
			} else if (frs.getPattern() == FeeRuleSpace.PATTERN_SAPCE) {
				double size = appVer.getNonVolatileSpace() + appVer.getVolatileSpace();
				int scale = (int) Math.ceil(size / frs.getGranularity());
				price = (double) frs.getPrice() * scale / 100;
			}
		}
		return new Double(format.format(price));
	}

	private void setBasicProperty(FeeStat fs, SpBaseInfo sp, Integer feeType) {
		fs.setSpId(sp.getId());
		fs.setSpName(sp.getName());
		fs.setOperateTime(Calendar.getInstance().getTime());
		fs.setFeeType(feeType);
	}

	private String genNameByKey(String key) {
		Resource res = new ClassPathResource("config/funcode.properties");
		Properties prop;
		String value = "";
		try {
			prop = PropertiesLoaderUtils.loadProperties(res);
			value = prop.getProperty(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	private double getFunPriceBySp(Long spId) {
		double price = 0.00;
		FeeRuleFunction frf = frfDao.getPerFrf(spId);
		if (null != frf) {
			price = (double) frf.getPrice() / 100;
		} else {
			price = 0.00;
		}
		return new Double(format.format(price));
	}

}
