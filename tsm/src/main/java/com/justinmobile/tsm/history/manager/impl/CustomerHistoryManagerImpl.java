package com.justinmobile.tsm.history.manager.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.card.domain.CardInfo;
import com.justinmobile.tsm.card.manager.CardInfoManager;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.history.manager.CustomerHistoryManager;
import com.justinmobile.tsm.transaction.domain.LocalTransaction;
import com.justinmobile.tsm.transaction.manager.LocalTransactionManager;

@Service("customerHistoryManager")
public class CustomerHistoryManagerImpl  implements CustomerHistoryManager {
	
	@Autowired
	private LocalTransactionManager localTransactionManager;
	
	@Autowired
	private SecurityDomainManager securityDomainManager;
	
	@Autowired
	private CardInfoManager carCardInfoManager;

	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;
	
	@Override
	public List<Map<String,Object>> getCustomerCreateSDHistory(Page<LocalTransaction> page, Map<String, Object> paramMap) {
		try {
			Customer customer = (Customer) paramMap.get("customer");
			String phoneName = (String) paramMap.get("phoneName");
			if(StringUtils.isNotBlank(phoneName)){
				List<CustomerCardInfo> cciList = customerCardInfoManager.getCustomerCardLikeCustomerAndCCName(customer,phoneName);
				if(cciList.size()>0){
					paramMap.put("queryPhone", Boolean.TRUE);
					paramMap.put("cciList", cciList);
				}else{
					return new ArrayList<Map<String,Object>>();
				}
			}
			String sdName = (String) paramMap.get("sdname");
			if(StringUtils.isNotBlank(sdName)){
				List<SecurityDomain> sdList = securityDomainManager.getByLikeName(sdName);
				if(sdList.size()>0){
					paramMap.put("querySd", Boolean.TRUE);
					paramMap.put("sdList", sdList);
				}else{
					return new ArrayList<Map<String,Object>>();
				}
			}
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			page = localTransactionManager.findCreateSdLocalTransactionForMobile(page,paramMap);
			List<LocalTransaction> ltList = page.getResult();
			for(LocalTransaction lt : ltList){
				Map<String,Object> map = new HashMap<String,Object>();
				Calendar begindate = lt.getBeginTime();
				map.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(begindate.getTime()));
				String aid = lt.getAid();
				map.put("sdName", securityDomainManager.getByAid(aid).getSdName());
				map.put("commons", "独立安装");
				if(null == lt.getTask()){
					map.put("commons", "随应用安装");
				}
				String cardNo = lt.getCardNo();
				CardInfo card = carCardInfoManager.getByCardNo(cardNo);
				CustomerCardInfo cci = customerCardInfoManager.getCCIByCustomerAndCard(customer,card);
				map.put("phoneName", cci.getName());
				mapList.add(map);
			}
			return mapList;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	
	}
	
}
