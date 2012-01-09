package com.justinmobile.tsm.customer.dao;

import java.util.List;
import java.util.Map;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.customer.domain.MobileType;

public interface MobileTypeDao extends EntityDao<MobileType, Long> {
	/**
	 * 获取所有的手机品牌
	 * 
	 * @return 返回所有的手机品牌列表
	 */
	public List<String> getMobileBrand();
	/**
	 * 根据手机品牌获取该品牌的所有手机型号
	 * @param  brand
	 * @return 返回该品牌的所有手机型号
	 */
	public List<String> getTypeByBrand(String brand);
	/**
	 * 根据用户输入的内容给出包含此关键字的提示，类似百度或者Google的搜索Suggest
	 * @param  keyword
	 * @return 返回相关的手机型号
	 */
	public List<String> getSuggestByKeyword(String keyword);
	/**
	 * 根据品牌查询所有该品牌的手机
	 * @param  brand
	 * @return 返回该品牌的所有手机型号
	 */
	public Page<MobileType> getMobileByBrand(final Page<MobileType> page,String brand);
	/**
	 * 根据关键字查询所有该品牌的手机
	 * @param  keyword
	 * @return 返回与该关键字有关的手机类型
	 */
	public Page<MobileType> getMobileByKeyword(final Page<MobileType> page,String keyword);
	/**
	 * 根据手机品牌和手机型号查询该手机详情
	 * @param  brand
	 * @param  type
	 * @return 返回该品牌和该型号的手机
	 */
	public Page<MobileType> getMobileByBrandAndType(final Page<MobileType> page,String brand,String type);
	/**
	 * 返回所有的手机信息
	 * @return 返回所有的手机型号信息
	 */
	public Page<MobileType> getAllMobile(final Page<MobileType> page);
	public List<MobileType> getTypeAndValueByBrand(String brand);
	public Page<MobileType> getMobileByKeywordForIndex(Page<MobileType> page,
			Map<String,Object> values);
 }