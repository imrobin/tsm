package com.justinmobile.tsm.application.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.application.dao.SecurityDomainApplyDao;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;
import com.justinmobile.tsm.system.domain.Requistion;

@Repository("securityDomainApplyDao")
public class SecurityDomainApplyDaoHibernate extends EntityDaoHibernate<SecurityDomainApply, Long> implements SecurityDomainApplyDao {

	@Override
	public boolean deleteSecurityDomainApplyByFormalId(Long sdId) {
		boolean bln = false;
		
		batchExecute("delete from SecurityDomainApply a where a.sdId = ?", sdId);
		
		bln = true;
		return bln;
	}
	
	@Override
	public boolean isPropertyUniqueForAidByStatus(Object newValue, Object orgValue) {
		boolean bln = true;
		
		if (newValue == null || newValue.equals(orgValue)) {
			return true;
		}
		
		String hql = "from SecurityDomainApply a where a.requistion.status <> ? and a.aid = ?";
		List<SecurityDomainApply> list = find(hql, Requistion.STATUS_REJECT, newValue);
		if(list != null && !list.isEmpty()) {
			bln = false;
		}
		
		return bln;
	}
}
