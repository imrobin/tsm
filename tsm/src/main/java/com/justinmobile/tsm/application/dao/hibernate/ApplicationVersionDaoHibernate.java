package com.justinmobile.tsm.application.dao.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.application.dao.ApplicationVersionDao;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.system.domain.Requistion;

@Repository("applicationVersionDao")
public class ApplicationVersionDaoHibernate extends
		EntityDaoHibernate<ApplicationVersion, Long> implements
		ApplicationVersionDao {

	@Override
	public ApplicationVersion getAidAndVersionNo(String aid, String versionNo) {
		String hql = "from "
				+ ApplicationVersion.class.getName()
				+ " as av where av.application.aid=:aid and av.versionNo=:versionNo";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("aid", aid);
		values.put("versionNo", versionNo);

		return findUnique(hql, values);
	}

	@Override
	public long hasArchiveRequest(Long appVerId) {
		String hql = "select r from "
				+ ApplicationVersion.class.getName()
				+ " as av,"
				+ Requistion.class.getName()
				+ " as r where av.id=:appVerId and av.id=r.originalId and r.type=:type and r.status=:status";
		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("appVerId", appVerId);
		values.put("type", Requistion.TYPE_APP_ARCHIVE);
		values.put("status", Requistion.STATUS_INIT);
		Requistion r = findUnique(hql, values);
		return r == null ? 0 : r.getId();
	}

	@Override
	public List<KeyValue> getAppVerBySp(Long spId) {

		StringBuilder hql = new StringBuilder();
		hql.append(
				"select app.aid||'-'||appVer.versionNo||'-'||app.name as key,concat(app.name,appVer.versionNo) as value from ")
				.append(Application.class.getName())
				.append(" app, ")
				.append(ApplicationVersion.class.getName())
				.append(" appVer where app.id=appVer.application.id and app.sp.id=")
				.append(spId).append(" and appVer.status=").append(ApplicationVersion.STATUS_PULISHED);
		return find(hql.toString());
	}

	@Override
	public Page<ApplicationVersion> findPageByMultParams(Page<ApplicationVersion> page, Map<String, Object> queryParams) {
		String hql = "from ApplicationVersion a where 1 = ? ";
		if(!queryParams.isEmpty()) {
			if(queryParams.containsKey("status")) {
				String status = (String)queryParams.get("status");
				hql += " and a.status = " + status;
			}
			
			if(queryParams.containsKey("sp")) {
				Long spId = (Long)queryParams.get("sp");
				hql += " and a.application.sp.id = " + spId;
			}
		}
		page = this.findPage(page, hql, 1);
		
		return page;
	}
	
	int getUndownloadUserAmountByApplicationVersion(String sql) {
		this.logger.debug("\n\n"+sql+"\n\n");
		Session session = this.getSession();
		@SuppressWarnings("deprecation")
		Query query = session.createSQLQuery(sql).addScalar("amount", Hibernate.INTEGER);
		@SuppressWarnings("rawtypes")
		List list = query.list();
		return (Integer)list.get(0);
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	public int getUndownloadUserAmountByApplicationVersionWithCardInfo(ApplicationVersion applicationVersion) {
		long appVerId = applicationVersion.getId();
		long appId = applicationVersion.getApplication().getId();
		
		String sql = "";
			   sql += " select a.id card_id";
			   sql += " from card_info a where not exists ";
		       sql += " (select 1 from card_application b where a.id = b.card_info_id and b.application_version_id = "+appVerId+" and b.status in ("+CardApplication.STATUS_LOCKED+","+CardApplication.STATUS_PERSONALIZED+","+CardApplication.STATUS_AVAILABLE+")) ";
		       Session session = this.getSession();
				List list = session.createSQLQuery(sql).addScalar("card_id", Hibernate.LONG).list();
				if(list.isEmpty()) return 0;
				List<Long> cardIdList = new ArrayList<Long>();
				for(int index = 0 ; index < list.size(); index++) {
					Long cardId = (Long)list.get(index);
					String check = "SELECT COUNT(1) amount FROM card_application a WHERE EXISTS (SELECT 1 FROM application_version b WHERE a.application_version_id = b.id AND b.application_id = "+appId+" ) AND a.card_info_id = "+cardId+" AND a.application_version_id != "+appVerId+" AND a.status IN ("+CardApplication.STATUS_LOCKED+","+CardApplication.STATUS_AVAILABLE+","+CardApplication.STATUS_PERSONALIZED+")";
					List l = session.createSQLQuery(check).addScalar("amount", Hibernate.LONG).list();
					long size = (Long)l.get(0);
					if(size > 0) continue;
					cardIdList.add(cardId);
				}
				
				if(cardIdList.isEmpty()) return 0;
				String cardIds = "";
				for(Long id : cardIdList) {
					cardIds += "," + id;
				}
				cardIds = cardIds.replaceFirst(",", "");
				
				String query = " SELECT COUNT(1) amount ";
				query += " FROM card_info a, ";
				query += " application_version b ";
				query += " WHERE a.id IN("+cardIds+") ";
				query += " AND b.id = " + appVerId;
				query += " AND a.available_nonevolatile_space >= b.non_volatile_space ";
				query += " AND a.available_volatile_space     >= b.volatile_space ";
		       
	    this.logger.debug("\n\n"+query+"\n\n");
		return getUndownloadUserAmountByApplicationVersion(query);
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	public int getUndownloadUserAmountByApplicationVersionWithCardSecurityDomain(ApplicationVersion applicationVersion) {
		long appVerId = applicationVersion.getId();
		long appId = applicationVersion.getApplication().getId();
		String sql = " SELECT a.card_id ";
			  sql += " FROM card_security_domain a";
			  sql += " WHERE NOT EXISTS";
			  sql += " (SELECT 1";
			  sql += " FROM card_application b";
			  sql += " WHERE a.card_id              = b.card_info_id";
			  sql += " AND b.application_version_id = " + appVerId;
			  sql += " AND b.status                IN ("+CardApplication.STATUS_AVAILABLE+","+CardApplication.STATUS_LOCKED+","+CardApplication.STATUS_PERSONALIZED+","+CardApplication.STATUS_AVAILABLE+")";
			  sql += " )";
			  sql += " AND EXISTS";
			  sql += " (SELECT 1 FROM application c WHERE a.security_id = c.sd_id AND c.id = " + appId;
			  sql += " )";
		Session session = this.getSession();
		List list = session.createSQLQuery(sql).addScalar("card_id", Hibernate.LONG).list();
		if(list.isEmpty()) return 0;
		List<Long> cardIdList = new ArrayList<Long>();
		for(int index = 0 ; index < list.size(); index++) {
			Long cardId = (Long)list.get(index);
			String check = "SELECT COUNT(1) amount FROM card_application a WHERE EXISTS (SELECT 1 FROM application_version b WHERE a.application_version_id = b.id AND b.application_id = "+appId+" ) AND a.card_info_id = "+cardId+" AND a.application_version_id != "+appVerId+" AND a.status IN ("+CardApplication.STATUS_LOCKED+","+CardApplication.STATUS_AVAILABLE+","+CardApplication.STATUS_PERSONALIZED+")";
			List l = session.createSQLQuery(check).addScalar("amount", Hibernate.LONG).list();
			long size = (Long)l.get(0);
			if(size > 0) continue;
			cardIdList.add(cardId);
		}
		
		if(cardIdList.isEmpty()) return 0;
		String cardIds = "";
		for(Long id : cardIdList) {
			cardIds += "," + id;
		}
		cardIds = cardIds.replaceFirst(",", "");
		
		String query = " SELECT COUNT(1) amount ";
				query += " FROM ";
				query += " (SELECT a.card_id, ";
				query += " a.free_non_volatile_space ram, ";
				query += " a.free_volatile_space rom ";
				query += " FROM card_security_domain a ";
				query += " WHERE a.card_id IN ("+cardIds+") ";
				query += " AND EXISTS ";
				query += " (SELECT 1 FROM application c WHERE a.security_id = c.sd_id AND c.id = "+appId+") ";
				query += " ) a, ";
				query += " application_version b ";
				query += " WHERE b.id = " + appVerId;
				query += " AND a.ram >= b.non_volatile_space ";
				query += " AND a.rom >= b.volatile_space ";
		
		this.logger.debug("\n\n"+query+"\n\n");
		return getUndownloadUserAmountByApplicationVersion(query);
	}

	@Override
	public Page<ApplicationVersion> getDownTestFileAppver(Page<ApplicationVersion> page ,String appName) {
		String hql ="from " + ApplicationVersion.class.getName() + " as av where av.status in (2,3,4)";
		if(StringUtils.isNotBlank(appName)){
			hql += " and av.application.name like '%" + appName + "%'";
		}
		return findPage(page,hql);
	}
}