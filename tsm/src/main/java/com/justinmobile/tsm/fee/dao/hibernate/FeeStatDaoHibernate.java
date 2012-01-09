package com.justinmobile.tsm.fee.dao.hibernate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.DateUtils;
import com.justinmobile.tsm.fee.dao.FeeStatDao;
import com.justinmobile.tsm.fee.domain.FeeStat;

@Repository("feeStatDao")
public class FeeStatDaoHibernate extends EntityDaoHibernate<FeeStat, Long>
		implements FeeStatDao {

	@Override
	public boolean hasBilled(String aid, String cardNo, String mobileNo) {
		boolean result = false;
		StringBuilder hql = new StringBuilder();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String now = format.format(Calendar.getInstance().getTime());

		String start = now.substring(0, 6) + "01";
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String end = now.substring(0, 6) + DateUtils.maxDay(year, month);
		hql.append("select fs from ").append(FeeStat.class.getName())
				.append(" as fs where fs.aid='").append(aid).append("'")
				.append(" and fs.cardNo='").append(cardNo).append("'")
				.append(" and fs.mobileNo='").append(mobileNo).append("'")
				.append(" and fs.feeType=").append(FeeStat.TYPE_SPACE)
				.append(" and fs.operateTime>=TO_DATE('").append(start)
				.append("','yyyymmdd')")
				.append(" and fs.operateTime<=TO_DATE('").append(end)
				.append("','yyyymmdd')");
		if (null != findUnique(hql.toString(), null)) {
			result = true;
		}
		return result;
	}

	@Override
	public long getCountFunctionBilled(Long spId, String start, String end) {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(distinct fs.card_no) from fee_stat fs")
				.append(" where fs.sp_id=").append(spId)
				.append(" and fs.fee_type=").append(FeeStat.TYPE_FUNCTION)
				.append(" and fs.operate_time>=TO_DATE('").append(start)
				.append("','yyyymmdd')")
				.append(" and fs.operate_time<=TO_DATE('").append(end)
				.append("','yyyymmdd')");
		Session s = null;
		BigDecimal result = null;
		try {
			s = super.sessionFactory.openSession();
			result = (BigDecimal) s.createSQLQuery(sql.toString())
					.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
	public List<FeeStat> getFunctionBilled(Long spId, String start, String end) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct fs.card_no as cardNo,fs.mobile_no as mobileNo,");
		sql.append("fs.sp_id as spId,fs.sp_name as spName");
		sql.append(" from fee_stat fs");
		sql.append(" where fs.sp_id=").append(spId).append(" and fs.fee_type=")
				.append(FeeStat.TYPE_FUNCTION)
				.append(" and fs.operate_time>=TO_DATE('").append(start)
				.append("','yyyymmdd')")
				.append(" and fs.operate_time<=TO_DATE('").append(end)
				.append("','yyyymmdd')");
		System.out.println("sql=="+sql.toString());
		Session s = null;
		List<Object[]> result = null;
		List<FeeStat> fsList = new ArrayList<FeeStat>();
		FeeStat fs = null;
		try {
			s = super.sessionFactory.openSession();
			result = s.createSQLQuery(sql.toString()).list();
			System.out.println("result.size()=="+result.size());
			for (Object[] o : result) {
				fs = new FeeStat();
				fs.setCardNo((String) o[0]);
				fs.setMobileNo((String) o[1]);
				fs.setSpId(((BigDecimal) o[2]).longValue());
				fs.setSpName((String) o[3]);
				fsList.add(fs);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (PlatformException he) {
				he.printStackTrace();
			}
		}
		return fsList;
	}
	@Override
	public List<FeeStat> getFeeStat(Long spId, Date start, Date end,
			Integer type) {

		StringBuilder hql = new StringBuilder();
		// 功能计费规则为按次
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("spId", spId);
		values.put("type", type);
		values.put("start", start);
		values.put("end", end);
		hql.append("select fs from ").append(FeeStat.class.getName())
				.append(" as fs where fs.spId=:spId")
				.append(" and fs.feeType=:type")
				.append(" and fs.operateTime>=:start")
				.append(" and fs.operateTime<=:end");
		return find(hql.toString(), values);
	}

}
