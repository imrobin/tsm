package com.justinmobile.tsm.sp.manager.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.sp.dao.SpBaseInfoApplyDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;
import com.justinmobile.tsm.sp.manager.SpBaseInfoApplyManager;
import com.justinmobile.tsm.system.domain.Requistion;

@Service("spBaseInfoApplyManager")
public class SpBaseInfoApplyManagerImpl extends EntityManagerImpl<SpBaseInfoApply, SpBaseInfoApplyDao> implements SpBaseInfoApplyManager {

	@Autowired
	private SpBaseInfoApplyDao spBaseInfoApplyDao;
	
	@Override
	public Page<SpBaseInfoApply> findPage(Page<SpBaseInfoApply> page, String orderBy, Map<String, Object> params) throws PlatformException {
		String hql = "from SpBaseInfoApply a where exists (from Requistion b where a.id = b.id and b.status = ?) ";
		hql = "select a from SpBaseInfoApply a ,Requistion b where a.id = b.id and b.status = ?";
		
		if(!params.isEmpty()) {
			if(params.get("name") != null && !StringUtils.isBlank(params.get("name").toString())) {
				hql += " and a.name like '%"+params.get("name")+"%' ";
			}
			
			if(params.get("province") != null && !StringUtils.isBlank(params.get("province").toString())) {
				hql += " and a.locationNo = '"+params.get("province")+"' ";
			}
		}
		
		if(!StringUtils.isBlank(orderBy)) {
			String[] items = orderBy.split("_");
			if(items.length == 3) {
				orderBy = " order by a." + orderBy.replaceFirst("_", ".").replace("_", " ");
			} else if(items.length == 2) {
				orderBy = " order by a." + orderBy.replace("_", " ");
			}
			hql += orderBy;
		}
		return spBaseInfoApplyDao.findPage(page, hql, Requistion.STATUS_INIT);
	}
	
	@Override
	public SpBaseInfoApply get(Long id) throws PlatformException {
		String hql = "from SpBaseInfoApply a where a.id = ?";
		return spBaseInfoApplyDao.findUniqueEntity(hql, id);
	}
	
	public static void main(String[] arg) throws Exception {
		String orderBy = "requistion_desc";
		String[] items = orderBy.split("_");
		if(items.length == 3) {
			orderBy = " order by a." + orderBy.replaceFirst("_", ".").replace("_", " ");
		} else if(items.length == 2) {
			orderBy = " order by a." + orderBy.replace("_", " ");
		}
		System.out.println(orderBy);
	}
}
