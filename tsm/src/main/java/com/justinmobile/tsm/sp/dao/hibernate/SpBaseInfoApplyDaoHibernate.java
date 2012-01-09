package com.justinmobile.tsm.sp.dao.hibernate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.justinmobile.core.dao.EntityDaoHibernate;
import com.justinmobile.tsm.sp.dao.SpBaseInfoApplyDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;
import com.justinmobile.tsm.system.domain.Requistion;

@Repository("spBaseInfoApplyDao")
public class SpBaseInfoApplyDaoHibernate extends EntityDaoHibernate<SpBaseInfoApply, Long> implements SpBaseInfoApplyDao {

	@Override
	public void delete(Long id) {
		batchExecute("delete from SpBaseInfoApply a where a.id = ?", id);
	}

	@Override
	public boolean isPropertyUniqueForAvaliable(final String propertyName, final Object newValue, final Object oldValue) {
		boolean bln = true;
		
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		
		String hql = "from SpBaseInfoApply a where a.requistion.status <> ? and a." + propertyName  + " = ?";
		List<SpBaseInfoApply> list = find(hql, Requistion.STATUS_REJECT, newValue);
		
		if(list != null && !list.isEmpty()) bln = false;
		
		return bln;
	}
}
