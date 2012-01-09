package com.justinmobile.tsm.history.dao.hibernate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.history.dao.SubscribeHistoryDao;
import com.justinmobile.tsm.history.domain.SubscribeHistory;

@Repository("subscribeHistoryDao")
public class SubscribeHistoryDaoHibernate extends EntityDaoHibernate<SubscribeHistory, Long> implements SubscribeHistoryDao {

	@Override
	public Page<SubscribeHistory> recentlyDownLoad(Page<SubscribeHistory> page, Long aid, boolean isRecently) {
		Map<String, Object> values = new HashMap<String, Object>();
		String hsql = "select g from " + SubscribeHistory.class.getName() + " as g join g.applicationVersion.application a";
		if (aid != 0) {
			hsql += " where a.id=:aid";
			values.put("aid", aid);
			if (isRecently) {
				Calendar ca = Calendar.getInstance();
				ca.add(Calendar.DAY_OF_MONTH, -14);
				hsql += " and g.subscribeDate>:subscribeDate";
				values.put("subscribeDate", ca);
				page.setPageSize(1000); // 查出两个星期内的多条记录
			}
		}
		hsql += " order by g.subscribeDate desc";
		return findPage(page, hsql, values);
	}

	@Override
	public List<SubscribeHistory> getByCustomerCardAndApplicationVersionOrderBySubscribeDateDesc(CustomerCardInfo customerCard,
			ApplicationVersion applicationVersion) {
		String hql = "from " + SubscribeHistory.class.getName()
				+ " as g where g.applicationVersion=:applicationVersion and g.customerCardInfo=:customerCard order by subscribeDate desc";

		Map<String, Object> values = new HashMap<String, Object>();
		values.put("applicationVersion", applicationVersion);
		values.put("customerCard", customerCard);

		return find(hql, values);
	}
	
	@Override
	public Page<SubscribeHistory> findPageByMultiQueryParams(Page<SubscribeHistory> page, Map<String, Object> queryParams) {
		
		String hql = "from SubscribeHistory a where 1 = ? ";
		
		if(queryParams != null && !queryParams.isEmpty()) {
			Set<String> keys = queryParams.keySet();
			for(Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
				String key = iter.next();
				if(key.equalsIgnoreCase("customerCardInfo_customer_nickName") && queryParams.containsKey(key)) {
					hql += " and a.customerCardInfo.customer.nickName like '%" + queryParams.get(key) + "%' " ;
				}
				
				if(key.equalsIgnoreCase("customerCardInfo_mobileNo") && queryParams.containsKey(key)) {
					hql += " and a.customerCardInfo.mobileNo = '" + queryParams.get(key) + "' " ;
				}
				
				if(key.equalsIgnoreCase("applicationVersion_application_name") && queryParams.containsKey(key)) {
					hql += " and a.applicationVersion.application.name like '%" + queryParams.get(key) + "%' " ;
				}
				
				if(key.equalsIgnoreCase("applicationVersion_application_sp") && queryParams.containsKey(key)) {
					hql += " and a.applicationVersion.application.sp.id = " + queryParams.get(key);
				}
				
				if(key.equalsIgnoreCase("province") && queryParams.containsKey(key)) {
					hql += " and a.applicationVersion.application.sp.locationNo = '" + queryParams.get(key) + "'";
				}
				
				if(key.equalsIgnoreCase("subscribeDate") && queryParams.containsKey(key)) {
					try {
						String subscribeDate = (String)queryParams.get(key);
						String parsePatterns = "yyyyMMddHHmmss";
						hql += " and a.subscribeDate >= ? and a.subscribeDate <= ?";
						Calendar calendar = CalendarUtils.parseCalendar(subscribeDate, "yyyyMM");
						
						Calendar start = CalendarUtils.parseCalendar(subscribeDate + "01000000", parsePatterns);
						Calendar end = CalendarUtils.parseCalendar(subscribeDate + calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + "235959", parsePatterns);
						page = findPage(page, hql, 1, start, end);
						return page;
					} catch (Exception e) {
						//page = findPage(page, hql, 1);
					}
					
				}
			}
		}
		
		page = findPage(page, hql, 1);
		
		return page;
	}
 
	@Override
	public Long getCountByAppAndDate(Application app,
			String start, String end) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(distinct customer_card_info_id) from subscribe_history  sb,application app,");
		sql.append("application_version appVer where sb.application_version_id=appVer.id and appVer.application_id=app.id");
		sql.append(" and app.id=").append(app.getId());
		sql.append(" and sb.subscribe_date >= TO_DATE('").append(start).append("','yyyymmdd')");
		sql.append(" and sb.subscribe_date <= TO_DATE('").append(end).append("','yyyymmdd')");
		Session s = null;
		BigDecimal result = null;
		try{
			s = super.sessionFactory.openSession();
			result =  (BigDecimal)s.createSQLQuery(sql.toString()).uniqueResult(); 
		}
		 catch (Exception e) { 
			 e.printStackTrace();
		 }
		 finally { 
			 try { 
			 s.close(); 
			 } catch (PlatformException he) { 
				 he.printStackTrace();
			 }
		} 
		 return result.longValue(); 
		
	}
	@Override
	public List<String> getCardNoByAppAndDate(Application app,
			String start, String end) {
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct sb.customerCardInfo.card.cardNo from ").append(SubscribeHistory.class.getName())
		.append(" as sb where sb.applicationVersion.application.id=").append(app.getId());
		if(!StringUtils.isBlank(start)){
			hql.append(" and sb.subscribeDate >= TO_DATE('").append(start).append("','yyyymmdd')");
		}
		if(!StringUtils.isBlank(end)){
			hql.append(" and sb.subscribeDate <= TO_DATE('").append(end).append("','yyyymmdd')");
		}
		return find(hql.toString());
		
	}
	
	public static void main(String[] arg) throws Exception {
		String subscribeDate = "201102";
		Date date = new SimpleDateFormat("yyyyMM").parse(subscribeDate);
		System.out.println(date);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		System.out.println(c.getActualMinimum(Calendar.DAY_OF_MONTH));
	}
	/**
	 * 查询出只订购过该应用某一版本，未订购过该应用的其他版本
	 * 
	 */
	@Override
	public Long getCountByOnlyAppVerAndDate(ApplicationVersion appVer,
			String start, String end) {
		    //暂时还没想出简单的sql
			StringBuilder sql = new StringBuilder();
			sql.append("select count(customer_card_info_id) from subscribe_history sb where ");
			sql.append(" application_version_id=").append(appVer.getId());
			sql.append(" and sb.subscribe_date >= TO_DATE('").append(start).append("','yyyymmdd')");
			sql.append(" and sb.subscribe_date <= TO_DATE('").append(end).append("','yyyymmdd')");
			sql.append(" and sb.customer_card_info_id in (select customer_card_info_id  from subscribe_history sb,application app,application_version appVer ");
		    sql.append(" where sb.application_version_id=appVer.id and appVer.application_id=app.id and app.id  = ").append(appVer.getApplication().getId());
		    sql.append(" and sb.subscribe_date >= TO_DATE('").append(start).append("','yyyymmdd')");
			sql.append(" and sb.subscribe_date <= TO_DATE('").append(end).append("','yyyymmdd')");
		    sql.append(" group by customer_card_info_id having count(application_version_id)=1)");
			Session s = null;
			BigDecimal result = null;
			try{
				s = super.sessionFactory.openSession();
				result =  (BigDecimal)s.createSQLQuery(sql.toString()).uniqueResult(); 
			}
			 catch (Exception e) { 
				 e.printStackTrace();
			 }
			 finally { 
				 try { 
				 s.close(); 
				 } catch (PlatformException he) { 
					 he.printStackTrace();
				 }
			} 
			 return result.longValue(); 
			
		}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * 查询用户订购过该应用的多次相同或者不同版本
	 */
	public List<BigDecimal> getCustomerCardInfoByMultiAppVerAndDate(Application app,
			String start, String end) {
		StringBuilder sql = new StringBuilder();
		sql.append("select customer_card_info_id from subscribe_history  sb,application app,application_version appVer");
		sql.append(" where sb.application_version_id=appVer.id and appVer.application_id=app.id and app.id=").append(app.getId());
		sql.append(" and sb.subscribe_date >= TO_DATE('").append(start).append("','yyyymmdd')");
		sql.append(" and sb.subscribe_date <= TO_DATE('").append(end).append("','yyyymmdd')");
	    sql.append("group by customer_card_info_id having count(application_version_id)>1 ");
		Session s = null;
		List<BigDecimal> result = new ArrayList<BigDecimal>();
		try{
			s = super.sessionFactory.openSession();
			result =  s.createSQLQuery(sql.toString()).list();
		}
		 catch (Exception e) { 
			 e.printStackTrace();
		 }
		 finally { 
			 try { 
			 s.close(); 
			 } catch (PlatformException he) { 
				 he.printStackTrace();
			 }
		} 
		 return result; 
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public Long getAppVerIdByCustomerCardInfoAndDate(Application app,Long customerCardInfoId,
			String start, String end) {
		StringBuilder sql = new StringBuilder();
		sql.append("select application_version_id from subscribe_history sb,application app,application_version appVer");
		sql.append(" where appVer.application_id = app.id and appVer.id = sb.application_version_id and app.id =").append(app.getId());
		sql.append(" and sb.subscribe_date >= TO_DATE('").append(start).append("','yyyymmdd')");
		sql.append(" and sb.subscribe_date <= TO_DATE('").append(end).append("','yyyymmdd')");
		sql.append(" and customer_card_info_id=").append(customerCardInfoId);
		sql.append(" order by sb.subscribe_date desc");
		Session s = null;
		List<BigDecimal> result = new ArrayList<BigDecimal>();
		try{
			s = super.sessionFactory.openSession();
			result =  s.createSQLQuery(sql.toString()).list(); 
		}
		 catch (Exception e) { 
			 e.printStackTrace();
		 }
		 finally { 
			 try { 
			 s.close(); 
			 } catch (PlatformException he) { 
				 he.printStackTrace();
			 }
		} 
		 return result.get(0).longValue(); 
	}

	@Override
	public Page<SubscribeHistory> listHistoryForCustomer(Page<SubscribeHistory> page,Map<String, Object> paramMap) {
		String hql = "from " + SubscribeHistory.class.getName() + " as sh where sh.customerCardInfo.customer = ?";
		if(StringUtils.isNotBlank((String) paramMap.get("phoneName"))){
			hql += " and sh.customerCardInfo.name like '%" + (String) paramMap.get("phoneName") + "%'";
		}
		if(StringUtils.isNotBlank((String) paramMap.get("appName"))){
			hql += " and sh.applicationVersion.application.name like '%" + (String) paramMap.get("appName") + "%'";
		}
		hql += " order by sh.subscribeDate desc";
		return this.findPage(page, hql,paramMap.get("customer") );
	}
	
	
}
