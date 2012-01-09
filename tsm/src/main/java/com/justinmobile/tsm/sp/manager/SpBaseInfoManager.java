package com.justinmobile.tsm.sp.manager;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;
import com.justinmobile.tsm.system.domain.Requistion;

@Transactional
public interface SpBaseInfoManager extends EntityManager<SpBaseInfo> {

	/**
	 * 根据用户名/手机号/邮箱获取sp，不考虑sp的状态
	 * 
	 * @param proof
	 *            sp的用户名或手机号或邮箱
	 * @return sp 返回对应的sp<br/>
	 *         当用户名/手机号/邮箱对应的sp不存在或者帐号状态(SysUser)状态为无效返回null
	 */
	public SpBaseInfo getSpByNameOrMobileOrEmail(String proof);

	/**
	 * 检查企业名称是否可用
	 * 
	 * @param name
	 *            企业名称
	 * @return true-当前企业名称可用；false-当前企业名称已经被占用
	 * @throws PlatformException
	 */
	public boolean validateSpFullName(String name) throws PlatformException;

	/**
	 * 检查企业检查是否可用
	 * 
	 * @param name
	 *            企业简称
	 * @return true-当前企业简称可用；false-当前企业简称已经被占用
	 * @throws PlatformException
	 */
	public boolean validateSpShortName(String name) throws PlatformException;

	public boolean validateSpProperty(String property, String value) throws PlatformException;
	
	public boolean validateSpProperty(String property, String newValue, String orgValue) throws PlatformException;
	/**
	 * 验证SP
	 * 
	 * @param sp
	 * @exception PlatformException
	 * <br/>
	 *                sp为null，PlatformErrorCode.SP_NOT_EXIST<br/>
	 *                sp状态不为”可用“时，PlatformErrorCode.SP_UNAVALIABLE
	 */
	public void validateSpAvaliable(SpBaseInfo spBaseInfo);
	
	/**
	 * 生成SP编号，格式：SP+7位流水号
	 * @return
	 */
	public String generateNumber(String prefix);
	
	/**
	 * 注册
	 * @param sp
	 * @return
	 * @throws PlatformException
	 */
	public Boolean register(SpBaseInfo spBaseInfo) throws PlatformException;

	
	/**
	 * 提交修改应用提供注册信息申请
	 * @param apply
	 * @return
	 * @throws PlatformException
	 */
	public boolean modifyApply(SpBaseInfoApply apply) throws PlatformException;
	
	public boolean modifyApply(SpBaseInfoApply apply, SpBaseInfo sp) throws PlatformException;

	/**
	 * 处理新增注册申请
	 * @param apply
	 * @return
	 * @throws PlastformException
	 */
	public boolean handleRegisterApply(SpBaseInfoApply apply, boolean result) throws PlatformException;
	
	/**
	 * 处理注册修改申请
	 * @param apply
	 * @return
	 * @throws PlatformException
	 */
	public boolean handleModifyApply(SpBaseInfoApply apply, boolean result) throws PlatformException;
	
	public SpBaseInfo getOnlyBySpno(String spNo);
	
	public Page<SpBaseInfo> getUnAuditSp(final Page<SpBaseInfo> page,final Object... values);
	
	public Page<Requistion> getRequistionPageForSp(Page<Requistion> page, Long spId);

	public Page<SpBaseInfo> advanceSearch(Page<SpBaseInfo> page, Map<String, String> paramMap);

	public Page<SpBaseInfo> getList(Page<SpBaseInfo> page, Map<String, String> params) throws PlatformException;
	
	/**
	 * 推荐提供商获取，非重复
	 * @param page
	 * @return
	 */
	Page<SpBaseInfo> recommendSpList(Page<SpBaseInfo> page);
	
	/**
	 * 撤销修改申请
	 * @param id
	 * @return 成功true；失败false；
	 * @throws PlatformException
	 */
	boolean cancelApply(Long id) throws PlatformException;
	
	boolean cancelApply(SpBaseInfo sp) throws PlatformException;
	
	Map<String, Object> getSpNameAndId(Map<String, Object> parameters) throws PlatformException;
	
	Map<String, Object> getSpNameAndId(Integer status, String province) throws PlatformException;
}