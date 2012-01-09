package com.justinmobile.tsm.sp.dao.hibernate;

import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.sp.dao.SpBaseInfoApplyDao;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.RecommendSp;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;
import com.justinmobile.tsm.system.dao.RequistionDao;
import com.justinmobile.tsm.system.domain.Requistion;

@Repository("spBaseInfoDao")
public class SpBaseInfoDaoHibernate extends EntityDaoHibernate<SpBaseInfo, Long> implements SpBaseInfoDao {

	@Autowired
	private SpBaseInfoApplyDao spBaseInfoApplyDao;

	@Autowired
	private RequistionDao requistionDao;

	@Override
	public Long generateServiceProviderNumber() {
		Object id = null;
		try {
			@SuppressWarnings("deprecation")
			SQLQuery query = getSession().createSQLQuery("select seq_sp_no_generator.nextval sp_no from dual").addScalar("sp_no",
					Hibernate.LONG);
			@SuppressWarnings("rawtypes")
			List list = query.list();
			if (!list.isEmpty())
				id = list.get(0);

			logger.debug("seq_sp_no_generator.nextval is " + id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return (Long) id;
	}

	/* ********************************************
	 * method name : getUnAuditSp modified : haojinghua , 2011-6-10
	 * 
	 * @see : @see
	 * com.justinmobile.tsm.sp.dao.SpBaseInfoDao#getUnAuditSp(com.justinmobile
	 * .core.dao.support.Page) *******************************************
	 */
	@Override
	public Page<SpBaseInfo> getUnAuditSp(final Page<SpBaseInfo> page, final Object... values) {
		String hql = "select sp from " + SpBaseInfo.class.getName() + " as sp," + Requistion.class.getName()
				+ " as req where sp.id = req.originalId and req.type=31 and req.status=1";
		return findPage(page, hql);
	}

	@Override
	public Page<SpBaseInfo> advanceSearch(Page<SpBaseInfo> page, Map<String, String> paramMap) {
		StringBuffer hsql = new StringBuffer(
				"select g from "
						+ SpBaseInfo.class.getName()
						+ " as g where g.status= ? and g.inBlack = ? and exists (select 1 from Application b where g.id = b.sp.id and b.status = ? group by b.sp) ");
		if (!StringUtils.isEmpty(paramMap.get("name"))) {
			String name = paramMap.get("name").replaceAll(" ", "  ").replaceAll("%", " %").replaceAll("_", " _");
			name = "%" + name + "%";
			hsql.append(" and g.name like '" + name + "' escape ' '");
		}
		return findPage(page, hsql.toString(), SpBaseInfo.STATUS_AVALIABLE, SpBaseInfo.NOT_INBLACK, Application.STATUS_PUBLISHED);
	}

	@Override
	public Page<SpBaseInfo> recommendSpList(Page<SpBaseInfo> page, SysUser currentUser) {
		StringBuffer hsql = new StringBuffer("select a from " + SpBaseInfo.class.getName()
				+ " as a where a.status= 1 and a.inBlack <>1 and a.id not in (select rs.sp.id from " + RecommendSp.class.getName()
				+ " as rs)");
		if (currentUser != null && !currentUser.getSysRole().getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())) {
			// hsql.append(" and a.locationNo='"+currentUser.getProvince()+"'");
		}
		return findPage(page, hsql.toString());
	}

	@Override
	public boolean deleteSpForUnavaliable(Long spId) {
		boolean bln = false;

		final String hql = "from SpBaseInfoApply a where a.requistion.originalId = ?";
		List<SpBaseInfoApply> list = find(hql, spId);
		if (list != null && !list.isEmpty()) {
			for (SpBaseInfoApply apply : list) {
				Requistion requistion = apply.getRequistion();
				spBaseInfoApplyDao.remove(apply);
				requistionDao.remove(requistion);
			}
		}
		remove(spId);

		bln = true;
		return bln;
	}

	@Override
	public boolean isPropertyUniqueForAvaliable(String propertyName, Object newValue, Object oldValue) {
		boolean bln = true;

		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}

		String hql = "from SpBaseInfo a where a.status <> ? and a." + propertyName + " = ?";
		List<SpBaseInfoApply> list = find(hql, SpBaseInfo.STATUS_INIT, newValue);

		if (list != null && !list.isEmpty())
			bln = false;

		return bln;
	}

	@Override
	public List<KeyValue> getSpName() {

		StringBuilder hql = new StringBuilder();
		hql.append("select distinct sp.id as key, sp.name as value from ").append(SpBaseInfo.class.getName())
				.append(" as sp where sp.status=").append(SpBaseInfo.STATUS_AVALIABLE);
		return find(hql.toString());
	}

	@Override
	public Page<SpBaseInfo> getSpForAvailableApplication(Page<SpBaseInfo> page, Map<String, String> params) {
		String sql = "from SpBaseInfo a where a.status = ? and a.inBlack = ? and exists (select 1 from Application b where a.id = b.sp.id and b.status = ? group by b.sp)";
		if (params != null && !params.isEmpty()) {
			if (params.containsKey("locationNo")) {
				sql += "";
			}
		}
		return this.findPage(page, sql, SpBaseInfo.STATUS_AVALIABLE, SpBaseInfo.NOT_INBLACK, Application.STATUS_PUBLISHED);
	}
}