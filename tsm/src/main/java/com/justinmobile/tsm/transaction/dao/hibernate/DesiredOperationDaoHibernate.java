package com.justinmobile.tsm.transaction.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.transaction.dao.DesiredOperationDao;
import com.justinmobile.tsm.transaction.domain.DesiredOperation;

@Repository("desiredOperationDao")
public class DesiredOperationDaoHibernate extends EntityDaoHibernate<DesiredOperation, Long> implements DesiredOperationDao {

	@Override
	public Page<DesiredOperation> findPageByParam(Page<DesiredOperation> page, Map<String, String> paramMap) {
		Map<String, Object> values = new HashMap<String, Object>();
		StringBuffer hsql = new StringBuffer("select d from " + DesiredOperation.class.getName()
				+ " as d where d.isExcuted=:isExcuted and d.customerCardId in (" + paramMap.get("customerCardId") + ") order by d.id desc");
		values.put("isExcuted", Integer.parseInt(paramMap.get("executionStatus")));
		return findPage(page, hsql.toString(), values);
	}

	@Override
	public DesiredOperation getDObyUserAidOptStatus(String ccid, String aid, String opttype, int status) {
		String hql = "from "
				+ DesiredOperation.class.getName()
				+ " as dersireOpt where dersireOpt.customerCardId = ?  and dersireOpt.aid = ? and dersireOpt.procedureName = ? and dersireOpt.isExcuted = ?";
		return findUniqueEntity(hql, Long.valueOf(ccid), aid, opttype, status);
	}

	@Override
	public Page<DesiredOperation> findPageByCustomerParam(Page<DesiredOperation> page, String executionStatus, Customer customer) {
		String hql = "";
		if(Integer.valueOf(executionStatus) == 0){
			 hql = "from " + DesiredOperation.class.getName()
			+ " as dersireOpt where dersireOpt.customer = ?  and dersireOpt.isExcuted = ? and dersireOpt.preProcess = 0 order by id desc";
		}else{
			 hql = "from " + DesiredOperation.class.getName()
			+ " as dersireOpt where dersireOpt.customer = ?  and dersireOpt.isExcuted = ?";
		}
		return findPage(page, hql, customer, Integer.valueOf(executionStatus));
	}

	@Override
	public DesiredOperation getDObyCCIandAidOptStatus(CustomerCardInfo cci, String aid, String opttype, int status) {
		String hql = "from "
				+ DesiredOperation.class.getName()
				+ " as dersireOpt where dersireOpt.customerCardId = ?  and dersireOpt.aid = ? and dersireOpt.procedureName = ? and dersireOpt.isExcuted = ?";
		return findUniqueEntity(hql, cci.getId(), aid, opttype, status);
	}

	@Override
	public List<DesiredOperation> getFocedOperationByCustomerCardThatNotExcute(CustomerCardInfo customerCard) {
		String hql = "from " + DesiredOperation.class.getName()
				+ " as dp where dp.customerCardId = :customerCardId  and dp.isExcuted = :isExcuted and dp.preProcess = :preProcess";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("customerCardId", customerCard.getId());
		values.put("isExcuted", DesiredOperation.NOT_EXCUTED);
		values.put("preProcess", DesiredOperation.PREPROCESS_TURE);

		return find(hql, values);
	}

	@Override
	public DesiredOperation getByAidAndProcedureNameAndCustomerCardThatNotExcuted(String aid, String procedureName,
			CustomerCardInfo customerCard) {
		String hql = "from "
				+ DesiredOperation.class.getName()
				+ " as dp where dp.aid = :aid and dp.customerCardId = :customerCardId  and dp.isExcuted = :isExcuted and dp.procedureName = :procedureName";

		Map<String, Object> values = new HashMap<String, Object>(4);
		values.put("aid", aid);
		values.put("procedureName", procedureName);
		values.put("customerCardId", customerCard.getId());
		values.put("isExcuted", DesiredOperation.NOT_EXCUTED);

		return findUnique(hql, values);
	}
}