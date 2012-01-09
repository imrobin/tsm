package com.justinmobile.tsm.fee.dao;

import java.util.Date;
import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.tsm.fee.domain.FeeStat;

public interface FeeStatDao extends EntityDao<FeeStat, Long> {
	public boolean hasBilled(String aid, String cardNo, String mobileNo);

	public long getCountFunctionBilled(Long spId, String start, String end);

	public List<FeeStat> getFeeStat(Long spId, Date start, Date end,
			Integer type);

	public List<FeeStat> getFunctionBilled(Long spId, String start, String end);

}
