package com.justinmobile.tsm.history.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

@Transactional
public interface CustomerHistoryManager {

	List<Map<String, Object>> getCustomerCreateSDHistory(Page<LocalTransaction> page, Map<String, Object> paramMap);
	
}