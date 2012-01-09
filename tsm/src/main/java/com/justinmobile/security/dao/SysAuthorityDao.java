package com.justinmobile.security.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.security.domain.SysAuthority;

public interface SysAuthorityDao extends EntityDao<SysAuthority, Long> {
	
	List<SysAuthority> getAllEnableAuthorities();
	
	List<SysAuthority> getAllAuthoritiesByUserName(String userName);
	
	List<SysAuthority> getAllNotAuthoritiesByUserName(String userName);
	
	List<SysAuthority> getSysAuthoritiesByResource(String resString);
	
	SysAuthority getAuthorityByName(String authName);

}
