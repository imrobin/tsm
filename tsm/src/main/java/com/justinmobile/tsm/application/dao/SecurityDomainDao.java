package com.justinmobile.tsm.application.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.utils.web.KeyValue;
import com.justinmobile.tsm.application.domain.SecurityDomain;


public interface SecurityDomainDao extends EntityDao<SecurityDomain, Long> {

	SecurityDomain getIsd();
	
	Page<Map<String, Object>> findPage(Page<Map<String, Object>> page, Map<String, Object> params);
	
	/**
	 * 查询指定状态的安全域
	 * @param page
	 * @param params
	 * @return
	 */
	Page<SecurityDomain> findPageByStatus(Page<SecurityDomain> page, Map<String, Object> params);
	/**
	 * 查询拥有安全域的SP
	 * 
	 */
	List<KeyValue> getSpNameHasSd();
	List<KeyValue> getSdNameBySp(Long spId);
	List<SecurityDomain> getSdBySp(Long spId);
	SecurityDomain getByAid(String aid);

	List<SecurityDomain> getByLikeName(String sdName);
}