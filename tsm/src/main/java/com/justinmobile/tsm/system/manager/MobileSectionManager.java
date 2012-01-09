package com.justinmobile.tsm.system.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.system.domain.MobileSection;

@Transactional
public interface MobileSectionManager extends EntityManager<MobileSection>{
	
	public String getProvinceByMobile(String mobileNo) throws PlatformException;
	
	public List<String> importExcelFile(String filePath) throws PlatformException;

	/**
	* 得到指定省代码的手机号前缀
	* @param      {引入参数名}   {引入参数说明}
	* @return      {返回参数名}   {返回参数说明}
	* @exception   {说明在某情况下,将发生什么异常}
	*/
	public List<String> getOwnerParaGraphByProvinceName(String provinceName) throws PlatformException;
	
	public MobileSection getByParagraph(String paragraph) throws PlatformException;

	public void remove(String[] msId);

}