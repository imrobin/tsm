package com.justinmobile.tsm.card.dao.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.dao.CardApplicationDao;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Repository("cardApplicationDao")
public class CardApplicationDaoHibernate extends EntityDaoHibernate<CardApplication, Long> implements CardApplicationDao {

	/*
	 * (non Javadoc) <p>Title: listRevertApps</p> <p>Description: </p>
	 * 
	 * @param cci
	 * 
	 * @return
	 * 
	 * @see com.justinmobile.tsm.card.dao.CardApplicationDao#listRevertApps(com.
	 * justinmobile.tsm.customer.domain.CustomerCardInfo)
	 */
	@Override
	public List<CardApplication> listRevertApps(CustomerCardInfo cci) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.cardInfo = ?";
		return find(hql, cci.getCard());
	}

	@Override
	public CardApplication getByCardNoAid(String cardNo, String aid) {
		String hql = " from " + CardApplication.class.getName()
				+ " as ca where ca.cardInfo.cardNo = ? and ca.applicationVersion.application.aid = ?";
		return findUniqueEntity(hql, cardNo, aid);
	}

	@Override
	public List<CardApplication> getCardAppBySd(long sdId) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.applicationVersion.application.sd.id = ?";
		List<CardApplication> list = find(hql, sdId);
		return list;
	}

	@Override
	public List<CardApplication> getByCardAndApplicationSd(CardInfo card, SecurityDomain sd) {
		String hql = "from " + CardApplication.class.getName()
				+ " as ca where ca.applicationVersion.application.sd = ? and ca.cardInfo = ?";
		return find(hql, sd, card);
	}

	@Override
	public CardApplication getByCardAndApplicationAndUndownload(CardInfo card, ApplicationVersion applicationVersion) {
		String hql = "from " + CardApplication.class.getName()
				+ " as ca where ca.applicationVersion = ? and ca.cardInfo = ? and ca.status = ?";
		return findUniqueEntity(hql, applicationVersion, card, CardApplication.STATUS_UNDOWNLOAD);
	}

	@Override
	public CardApplication getByCardAndAppver(CardInfo card, ApplicationVersion applicationVersion) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.applicationVersion = ? and ca.cardInfo = ?";
		return findUniqueEntity(hql, applicationVersion, card);
	}

	@Override
	public List<CardApplication> findAvailableList(CardInfo card) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.cardInfo = ? and ca.status = ?";
		return this.find(hql, card, CardApplication.STATUS_AVAILABLE);
	}

	@Override
	public List<CardApplication> findDownloadList(CardInfo card) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.cardInfo = ? and ca.status = ? and ca.recovering = ?";
		return this.find(hql, card, CardApplication.STATUS_UNDOWNLOAD, true);
	}

	@Override
	public Long getAppVerSize(ApplicationVersion appVer) {
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct (usedNonVolatileSpace+usedVolatileSpace) as appSize from ").append(CardApplication.class.getName())
				.append(" as ca where ca.applicationVersion.id=").append(appVer.getId())
				.append(" order by (usedNonVolatileSpace+usedVolatileSpace) desc");
		List<Long> result = find(hql.toString());
		if (result.size() > 0) {
			return result.get(0);
		} else {
			return 0L;
		}
	}

	@Override
	public Page<CardApplication> findPageByCustomer(Page<CardApplication> page, Map<String, Object> queryParams) {
		StringBuilder hql = new StringBuilder();
		hql.append("select ca from ").append(CardApplication.class.getName()).append(" ca,");
		hql.append(CustomerCardInfo.class.getName()).append(" cci where ca.cardInfo=cci.card");
		if (queryParams != null && !queryParams.isEmpty()) {
			Set<String> keys = queryParams.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				String key = iter.next();
				if (key.equalsIgnoreCase("mobileNo") && queryParams.containsKey(key)) {
					hql.append(" and cci.mobileNo = '").append(queryParams.get(key)).append("'");
				}
			}
		}
		page = findPage(page, hql.toString());

		return page;
	}

	public List<Map<String, Object>> findByCustomer(Page<Map<String, Object>> page, String mobileNo) {
		StringBuilder hql = new StringBuilder();
		hql.append("select new map(cci.mobileNo as cci_mobileNo,cci.name as cci_name,");
		hql.append("case when cci.status = 1 then '正常' when cci.status = 2 then '挂失' ");
		hql.append("when cci.status = 3 then '未激活' when cci.status = 4 then '注销' ");
		hql.append("when cci.status = 5 then '替换中' when cci.status = 6 then '已替换' else '未知' end as cci_status,");
		hql.append("ca.applicationVersion.application.name as ca_applicationVersion_application_name,ca.applicationVersion.versionNo as ca_applicationVersion_versionNo,");
		hql.append("case when ca.status = 1 then '未下载' when ca.status = 2 then '下载中' ");
		hql.append("when ca.status = 3 then '删除中' when ca.status = 4 then '已下载' ");
		hql.append("when ca.status = 5 then '已锁定' when ca.status = 6 then '已安装' ");
		hql.append("when ca.status = 7 then '已个人化' when ca.status = 8 then '可用' else '未知' end as ca_status,");
		hql.append("ca.usedNonVolatileSpace as ca_usedNonVolatileSpace,ca.usedVolatileSpace as ca_usedVolatileSpace,");
		hql.append("case when ca.migratable = 1 then '是' when ca.migratable = 0 then '否' else '未知' end as ca_migratable) from ");
		hql.append(CardApplication.class.getName()).append(" ca,");
		hql.append(CustomerCardInfo.class.getName()).append(" cci where ca.cardInfo=cci.card");
		hql.append(" and cci.status<>").append(CustomerCardInfo.STATUS_CANCEL);
		hql.append(" and cci.mobileNo='").append(mobileNo).append("'");
		if (page.getOrderBy() != null) {
			String orderBy = StringUtils.replace(page.getOrderBy(), "_", ".");
			hql.append(" order by ").append(orderBy).append(" ").append(page.getOrder());
		}
		return find(hql.toString());
	}

	@Override
	public List<CardApplication> getCaListNotDelAndNotMigratable(CardInfo card) {
		String hql = "from " + CardApplication.class.getName()
				+ " as ca where ca.cardInfo = ? and ca.migratable = ? and ca.status <> ? and ca.status <> ? and ca.status <> ?";
		return find(hql, card, Boolean.FALSE, CardApplication.STATUS_UNDOWNLOAD, CardApplication.STATUS_DOWNLOADED,
				CardApplication.STATUS_INSTALLED);
	}

	@Override
	public List<CardApplication> getCaListMigratable(CardInfo card) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.cardInfo = ? and ca.migratable = ?";
		return find(hql, card, Boolean.TRUE);
	}

	@Override
	public CardApplication getAvailbleOrLockedByCardNoAid(String cardNo, String aid) {
		String hql = " from "
				+ CardApplication.class.getName()
				+ " as ca where ca.cardInfo.cardNo = ? and ca.applicationVersion.application.aid = ? and (ca.status=? or ca.status=? or ca.status=?)";
		return findUniqueEntity(hql, cardNo, aid, CardApplication.STATUS_AVAILABLE, CardApplication.STATUS_LOCKED,
				CardApplication.STATUS_PERSONALIZED);
	}

	@Override
	public List<CardApplication> getByLastFeeTime(Date end) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("end", end);
		hql.append("select ca from ").append(CardApplication.class.getName()).append(" ca,");
		hql.append(CustomerCardInfo.class.getName()).append(" cci");
		hql.append(" where cci.card=ca.cardInfo and ( cci.status<>").append(CustomerCardInfo.STATUS_CANCEL);
		hql.append(" or cci.status<>").append(CustomerCardInfo.STATUS_REPLACING).append(" )");
		hql.append(" and ca.lastFeeTime<:end");
		return find(hql.toString(), values);
	}

	@Override
	public List<CardApplication> getByCardAndApplication(CardInfo cardInfo, Application app) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.cardInfo = ? and ca.applicationVersion.application = ?";
		return find(hql.toString(), cardInfo, app);
	}

	@Override
	public List<CardApplication> getByCardAndStatus(CardInfo card, int status) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.cardInfo = ? and ca.status = ?";
		return find(hql, card, status);
	}

	@Override
	public List<CardApplication> getForLostListByCardInfo(CardInfo card) {
		String hql = "from " + CardApplication.class.getName() + " as ca where ca.cardInfo = ? and (ca.status = ? or ca.status = ? )";
		return find(hql, card, CardApplication.STATUS_PERSONALIZED,CardApplication.STATUS_AVAILABLE);
	}

}