package com.justinmobile.tsm.customer.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.card.domain.CardApplication;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.domain.CardSecurityDomain;
import com.justinmobile.tsm.customer.dao.CustomerCardInfoDao;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;

@Repository("customerCardInfoDao")
public class CustomerCardInfoDaoHibernate extends EntityDaoHibernate<CustomerCardInfo, Long> implements CustomerCardInfoDao {

	@Override
	public List<CustomerCardInfo> getListByCardInfo(CardInfo cardInfo) {
		return this.findByProperty("card", cardInfo);
	}

	@Override
	public List<CustomerCardInfo> getListByMobileNo(String mobileNo) {
		return this.findByProperty("mobileNo", mobileNo);
	}

	@Override
	public CustomerCardInfo getByCardThatStatusNotCanclledOrNotReplaced(CardInfo card) {
		String hql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.card = :card and cci.status != :cancelled and cci.status != :replaced";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("card", card);
		values.put("cancelled", CustomerCardInfo.STATUS_CANCEL);
		values.put("replaced", CustomerCardInfo.STATUS_END_REPLACE);

		return findUnique(hql, values);
	}

	@Override
	public CustomerCardInfo findByCciName(Customer customer, String cardName) {
		String hql = "from " + CustomerCardInfo.class.getName() + " as cci where cci.name = :name and cci.customer = :customer";
		Map<String, Object> values = new HashMap<String, Object>(2);
		values.put("name", cardName);
		values.put("customer", customer);
		return findUnique(hql, values);
	}

	@Override
	public List<CustomerCardInfo> getCustomerCardByCustomer(Customer customer, Integer status) {
		String hql = "from " + CustomerCardInfo.class.getName() + " as cci where cci.customer = ? ";
		if (status != null) {
			hql += " and cci.status = " + status;
		}
		hql += " order by cci.status";
		return this.find(hql, customer);
	}

	@Override
	public List<CustomerCardInfo> getHasRequiremnt(Long id, Long caId, Long appVerId) {
		String hql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.id=? and (cci.mobileType.originalOsKey in (select aci.sysRequirment from "
				+ ApplicationClientInfo.class.getName() + " as aci inner join aci.applicationVersions av where  av.id=? )"
				+ " or cci.mobileType.j2meKey in (select aci.sysRequirment from " + ApplicationClientInfo.class.getName()
				+ " as aci inner join aci.applicationVersions av where  av.id=?))";
		hql += " order by cci.status";
		return this.find(hql, id, caId, appVerId);
	}

	@Override
	public List<CustomerCardInfo> getCustomerCardInfoByIdAsNormomAndLost(Customer customer) {
		String hql = "from " + CustomerCardInfo.class.getName() + " as cci where cci.customer = ? and (cci.status = ? or cci.status = ?)";
		return this.find(hql, customer, CustomerCardInfo.STATUS_NORMAL, CustomerCardInfo.STATUS_LOST);
	}

	@Override
	public List<CustomerCardInfo> findCanRevert(Customer customer) {
		String hql = "from " + CustomerCardInfo.class.getName()
				+ " cci where cci.customer = ? and (cci.status = ? or cci.status = ? or cci.status = ?)";
		return this.find(hql, customer, CustomerCardInfo.STATUS_NORMAL, CustomerCardInfo.STATUS_LOST, CustomerCardInfo.STATUS_REPLACING);
	}

	@Override
	public CustomerCardInfo findCustomerCardInfo(CardInfo cardInfo, int status, Long actived, int inblack) {
		String ccHql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.card = ? and cci.status = ? and cci.active = ? and inBlack = ?";
		return this.findUniqueEntity(ccHql, cardInfo, status, actived, inblack);
	}

	@Override
	public List<CustomerCardInfo> getByMobileNoNotCancelAndEnd(String mobileNo) {
		String ccHql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.mobileNo = ? and (cci.status <> ? and cci.status <> ?)";
		return this.find(ccHql, mobileNo, CustomerCardInfo.STATUS_CANCEL, CustomerCardInfo.STATUS_END_REPLACE);
	}

	@Override
	public CustomerCardInfo getByMobileNoRepalcing(String stringNo) {
		String ccHql = "from " + CustomerCardInfo.class.getName() + " as cci where cci.mobileNo = ? and cci.active = ? and cci.status = ?";
		return this.findUniqueEntity(ccHql, stringNo, CustomerCardInfo.ACTIVED, CustomerCardInfo.STATUS_REPLACING);
	}

	@Override
	public CustomerCardInfo findByActiveAndNotCancelEnd(CardInfo cardInfo) {
		String ccHql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.card = ? and cci.active = ? and cci.status <> ? and cci.status <> ?";
		return this.findUniqueEntity(ccHql, cardInfo, CustomerCardInfo.ACTIVED, CustomerCardInfo.STATUS_CANCEL,
				CustomerCardInfo.STATUS_END_REPLACE);
	}

	@Override
	public List<CustomerCardInfo> findCCIByNotCustomerAndMobiemo(Customer customer, String newmobileNo) {
		String hql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.customer <> ? and cci.mobileNo = ? and cci.status <> ? and cci.status <> ?";
		return this.find(hql, customer, newmobileNo, CustomerCardInfo.STATUS_CANCEL, CustomerCardInfo.STATUS_END_REPLACE);
	}

	@Override
	public Page<CustomerCardInfo> getCustomerCardInfoPageBySd(Page<CustomerCardInfo> page, SecurityDomain securityDomain) {
		String hql = "from CustomerCardInfo a where exists (select 1 from CardSecurityDomain b where a.card = b.card and b.status in (?, ?) and b.sd = ?) and a.status <> ? ";
		page = findPage(page, hql, CardSecurityDomain.STATUS_PERSO, CardSecurityDomain.STATUS_LOCK, securityDomain,
				CustomerCardInfo.STATUS_CANCEL);
		return page;
	}

	@Override
	public Page<CustomerCardInfo> getCustomerCardInfoPageByApp(Page<CustomerCardInfo> page, ApplicationVersion appVersion) {
		String hql = "from CustomerCardInfo a where exists (select 1 from CardApplication b where a.card = b.cardInfo and b.status in (?,?,?) and b.applicationVersion = ?) and a.status <> ?";
		page = findPage(page, hql, CardApplication.STATUS_LOCKED, CardApplication.STATUS_PERSONALIZED, CardApplication.STATUS_AVAILABLE,
				appVersion, CustomerCardInfo.STATUS_CANCEL);
		return page;
	}

	@Override
	public List<CustomerCardInfo> getByMobileNo(String mobileNo) {
		return this.findByProperty("mobileNo", mobileNo);
	}

	@Override
	public List<CustomerCardInfo> getByCustomerAndApplicationThatCardApplicationMigrateableTrueOrderByBindingDateDesc(
			Application application, Customer customer) {
		String hql = "select cci from "
				+ CustomerCardInfo.class.getName()
				+ " cci, "
				+ CardApplication.class.getName()
				+ " as ca where ca.cardInfo = cci.card and ca.migratable = :migratable and ca.applicationVersion.application = :application and cci.customer = :customer order by cci.bindingDate desc";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("migratable", Boolean.TRUE);
		values.put("application", application);
		values.put("customer", customer);
		return find(hql, values);
	}

	@Override
	public Page<CustomerCardInfo> getByMobileNoAllAndPage(Page<CustomerCardInfo> page, String mobileNo) {
		String hql = "from " + CustomerCardInfo.class.getName() + " as cci where  cci.mobileNo like ?";
		return findPage(page, hql, '%' + mobileNo + '%');
	}

	@Override
	public CustomerCardInfo getByCardThatStatusNormalOrLost(CardInfo card) {
		String hql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.card = :card and (cci.status = :normal or cci.status = :lost)";

		Map<String, Object> values = new HashMap<String, Object>(3);
		values.put("card", card);
		values.put("normal", CustomerCardInfo.STATUS_NORMAL);
		values.put("lost", CustomerCardInfo.STATUS_LOST);

		return findUnique(hql, values);
	}

	@Override
	public CustomerCardInfo getByCardThatStatusNormalOrLostOrNotUse(CardInfo card) {
		String hql = "from " + CustomerCardInfo.class.getName()
				+ " as cci where cci.card = :card and (cci.status = :normal or cci.status = :lost or cci.status = :notUse)";

		Map<String, Object> values = new HashMap<String, Object>(4);
		values.put("card", card);
		values.put("normal", CustomerCardInfo.STATUS_NORMAL);
		values.put("lost", CustomerCardInfo.STATUS_LOST);
		values.put("notUse", CustomerCardInfo.STATUS_NOT_USE);

		return findUnique(hql, values);
	}
    
	@Override
	public CustomerCardInfo getByCard(CardInfo card) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> values = new HashMap<String, Object>(1);
		values.put("card", card);
		hql.append("select cci from ").append(CustomerCardInfo.class.getName()).append(" as cci");
		hql.append(" where cci.card=:card and cci.status<>").append(CustomerCardInfo.STATUS_CANCEL);
		hql.append(" and cci.status<>").append(CustomerCardInfo.STATUS_REPLACING);
		return findUnique(hql.toString(),values);
	}

	@Override
	public CustomerCardInfo getCCIByCustomerAndCard(Customer customer, CardInfo card) {
		Session session = this.getSessionFactory().openSession();
		String hql = "select max(cci.bindingDate) from " +  CustomerCardInfo.class.getName() + " as cci where cci.customer = ? and cci.card = ?";
		Query query = session.createQuery(hql);
		query.setEntity(0, customer);
		query.setEntity(1, card);
		Object date = query.uniqueResult();
		session.close();
		String hql2 = "from " +  CustomerCardInfo.class.getName() + " as cci where cci.customer = ? and cci.card = ? and cci.bindingDate = ?";
		return this.findUniqueEntity(hql2, customer,card,date);
	}

	@Override
	public List<CustomerCardInfo> getCustomerCardLikeCustomerAndCCName(Customer customer, String phoneName) {
		String hql = "from " +  CustomerCardInfo.class.getName() + " as cci where cci.customer = ? and cci.name like '%" + phoneName + "%'";
		return this.find(hql, customer);
	}

	@Override
	public List<CustomerCardInfo> getCustomerCardByCustomerThatNormAndLost(Customer customer) {
		String hql = "from " + CustomerCardInfo.class.getName() + " as cci where cci.customer = ? and cci.status = ? or cci.status = ?";
		return this.find(hql, customer, CustomerCardInfo.STATUS_NORMAL, CustomerCardInfo.STATUS_LOST);
	}
}