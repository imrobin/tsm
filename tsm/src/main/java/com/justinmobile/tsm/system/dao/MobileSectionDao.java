package com.justinmobile.tsm.system.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.system.domain.MobileSection;

public interface MobileSectionDao extends EntityDao<MobileSection, Long>{
	
	void ImportData(List<MobileSection> list) ;
	
	void removeAll(String[] ids);
}