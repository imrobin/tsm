package com.justinmobile.tsm.fee.manager;

import java.util.Date;
import java.util.List;

import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.fee.domain.FeeRuleFunction;
import com.justinmobile.tsm.fee.domain.FeeStat;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;

public interface FeeStatManager extends EntityManager<FeeStat> {
	public void genStatRecord(LocalTransaction lt);

	public boolean hasBilled(String aid, String cardNo, String mobileNo);

	/**
	 * 获取功能计费的总人数
	 */
	public Long getCounthasBilled(Long spId, String start, String end);

	public List<FeeStat> getFunctionBilled(Long spId, String start, String end);

	public void subscribeAppStatRecord(String aid, String cardNo,
			String mobileNo);
	public void unSubscribeAppStatRecord(String aid, String cardNo,
			String mobileNo);
	public List<FeeStat> getFeeStat(Long spId, Date start, Date end,
			Integer type);

	public FeeRuleFunction getMonthFrfBySpAndSize(Long spId, Long size);

	public void genPerStatRecord(String mobileNo, String cardNo);

	public void genFeeStatTask();

}
