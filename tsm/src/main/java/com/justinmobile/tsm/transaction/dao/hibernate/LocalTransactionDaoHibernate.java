package com.justinmobile.tsm.transaction.dao.hibernate;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Repository;
import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.web.KeyLongValue;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.transaction.dao.LocalTransactionDao;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.domain.LocalTransaction.ExecutionStatus;

@Repository("localTransactionDao")
public class LocalTransactionDaoHibernate extends
		EntityDaoHibernate<LocalTransaction, Long> implements
		LocalTransactionDao {
	static final String RESULT_STR = "0000";

	@Override
	public void changeStatus(String ids, String targetStatus) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("status", Integer.parseInt(targetStatus));
		values.put("beginTime", Calendar.getInstance());
		String hql = "update " + DesiredOperation.class.getName()
				+ " g set g.isExcuted = :status where g.id in (" + ids + ")";
		this.batchExecute(hql, values);
	}

	@Override
	public Page<LocalTransaction> findTransactionByUser(
			Page<LocalTransaction> page, Map<String, String> paramMap) {
		Map<String, Object> values = new HashMap<String, Object>();
		String result = paramMap.get("result");
		String hsql = "select g from " + LocalTransaction.class.getName()
				+ " as g" + " where 1=1 ";
		if (!StringUtils.isEmpty(paramMap.get("executionStatus"))) {
			values.put("executionStatus",
					Integer.parseInt(paramMap.get("executionStatus")));
			hsql += " and g.executionStatus = :executionStatus";
		}
		if (!StringUtils.isEmpty(result)) {
			values.put("result", RESULT_STR);
			if (result.equals("success")) {
				hsql += " and g.result = :result";
			} else {
				hsql += " and (g.result != :result OR g.result is null)";
			}
		}
		if (!StringUtils.isEmpty(paramMap.get("cardNos"))) {
			hsql += " and g.cardNo in (" + paramMap.get("cardNos") + ")";
		}
		return findPage(page, hsql, values);
	}

	@Override
	public List<LocalTransaction> getRunningTransByCardNo(String cardNo) {
		String hql = "from "
				+ LocalTransaction.class.getName()
				+ " as lt where lt.executionStatus = :executionStatus and lt.cardNo = :cardNo";

		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("executionStatus", ExecutionStatus.RUNNING.getStatus());
		values.put("cardNo", cardNo);

		return find(hql, values);
	}

	@Override
	public List<KeyLongValue> getTransByAidAndVersion(String aid, String version,
			String start, String end) {
		StringBuilder hql = new StringBuilder();
		hql.append("select new com.justinmobile.core.utils.web.KeyLongValue(lt.procedureName,count(lt.procedureName)) from ")
				.append(LocalTransaction.class.getName())
				.append(" lt where lt.aid='").append(aid).append("'")
				.append(" and lt.result='").append(PlatformMessage.SUCCESS.getCode()).append("'")
				.append(" and lt.executionStatus=")
				.append(LocalTransaction.STATUS_EXECUTION_EXEUTED);
		if (!StringUtils.isEmpty(version)) {
			hql.append(" and lt.appVersion='").append(version).append("'");
		}
		if (!StringUtils.isEmpty(start)) {
			hql.append(" and endTime>= TO_DATE('").append(start)
					.append("','yyyymmdd')");
		}
		if (!StringUtils.isEmpty(end)) {
			hql.append(" and endTime<= TO_DATE('").append(end)
					.append("','yyyymmdd')");
		}
		hql.append(" group by lt.procedureName");
		return find(hql.toString());
	}

	@Override
	public List<String> getCreateSD(String aid, String start, String end) {
		StringBuilder hql = new StringBuilder();
		hql.append("select distinct lt.cardNo from ").append(LocalTransaction.class.getName())
				.append(" lt where lt.aid='").append(aid).append("'")
				.append(" and lt.superTransaction is null ")
                .append(" and lt.result='").append(PlatformMessage.SUCCESS.getCode()).append("'")
				.append(" and lt.executionStatus=")
				.append(LocalTransaction.STATUS_EXECUTION_EXEUTED);
		if (!StringUtils.isEmpty(start)) {
			hql.append(" and endTime>= TO_DATE('").append(start)
					.append("','yyyymmdd')");
		}
		if (!StringUtils.isEmpty(end)) {
			hql.append(" and endTime<= TO_DATE('").append(end)
					.append("','yyyymmdd')");
		}
		return find(hql.toString());
	}

	@Override
	public Page<LocalTransaction> findCreateSdLocalTransactionForMobile(Page<LocalTransaction> page, Map<String, Object> paramMap) {
		String hql = "from " + LocalTransaction.class.getName() + " as lt where lt.mobileNo = ? and lt.procedureName = ? and lt.result = ?";
		if(null != paramMap.get("queryPhone")){
			hql += " and lt.cardNo in (";
			@SuppressWarnings("unchecked")
			List<CustomerCardInfo> cciList = (List<CustomerCardInfo>) paramMap.get("cciList");
			for(int i = 0;i < cciList.size();i++){
				if(i == cciList.size()-1){
					hql +=  "'" +cciList.get(i).getCard().getCardNo() + "')";
				}else{
					hql +=  "'" +cciList.get(i).getCard().getCardNo() + "',";
				}
			}
		}
		if(null != paramMap.get("querySd")){
			hql += " and lt.aid in (";
			@SuppressWarnings("unchecked")
			List<SecurityDomain> sdList = (List<SecurityDomain>) paramMap.get("sdList");
			for(int i = 0;i < sdList.size();i++){
				if(i == sdList.size()-1){
					hql += "'" + sdList.get(i).getAid() + "')";
				}else{
					hql += "'" + sdList.get(i).getAid() + "',";
				}
			}
		}
		hql += " order by lt.endTime desc";
		Customer customer = (Customer) paramMap.get("customer");
		System.out.println(customer.getSysUser());
		System.out.println(customer.getSysUser().getMobile());
		return findPage(page,hql,customer.getSysUser().getMobile(),LocalTransaction.Operation.CREATE_SD.name(),PlatformMessage.SUCCESS.getCode());
	}
}