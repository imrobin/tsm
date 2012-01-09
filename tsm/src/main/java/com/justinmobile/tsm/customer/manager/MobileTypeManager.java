package com.justinmobile.tsm.customer.manager;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.customer.domain.MobileType;

public interface MobileTypeManager extends EntityManager<MobileType>{
	
	public List<String> getAllBrand();
	
	public List<String> getTypeByBrand(String brand);
	
	public Page<MobileType> getAllMobile(final Page<MobileType> page);
	
	public Page<MobileType> getMobileByBrand(final Page<MobileType> page,String brand);
	
	public Page<MobileType> getMobileByKeyword(final Page<MobileType> page,String keyword);
	
	public Page<MobileType> getMobileByBrandAndType(final Page<MobileType> page,String brand,String type);
	
	public List<String> getSuggestByKeyword(String keyword);
	
	
	public List<MobileType> getTypeAndValueByBrand(String brand);
	public Page<MobileType> getMobileByKeywordForIndex(Page<MobileType> page,
			Map<String,Object> values);
	
}