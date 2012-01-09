package com.justinmobile.tsm.application.manager;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManager;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;
import com.justinmobile.tsm.system.domain.Requistion;

@Transactional
public interface SecurityDomainManager extends EntityManager<SecurityDomain> {

	/**
	 * 获取主安全域
	 * 
	 * @return 主安全域
	 */
	SecurityDomain getIsd();

	/**
	 * 检查安全域AID是否已使用
	 * 
	 * @param aid
	 * @return true-安全域AID可用，false-安全域AID不可用
	 */
	boolean validateSecurityDomainAid(String aid, String originalAid);

	/**
	 * 申请安全域
	 * 
	 * @param sd
	 * @param requistion
	 */
	@Deprecated
	void applySecurityDomain(SecurityDomain sd, Requistion requistion);

	boolean apply(SecurityDomain sd);

	/**
	 * 安全域发布申请，只生成申请数据，正式表待审核通过再生成
	 * 
	 * @param apply
	 * @return
	 * @throws PlatformException
	 */
	boolean applySecurityDomain(SecurityDomainApply apply) throws PlatformException;

	/**
	 * 审核安全域发布申请
	 * 
	 * @param apply
	 * @param result
	 * @return
	 * @throws PlatformException
	 */
	boolean handlePublishedApply(SecurityDomainApply apply, boolean result, Map<String, Object> params) throws PlatformException;

	/**
	 * 审核安全域归档申请
	 * 
	 * @param apply
	 * @param result
	 * @return
	 * @throws PlatformException
	 */
	boolean handleArchivedApply(SecurityDomainApply apply, boolean result) throws PlatformException;

	/**
	 * 审核安全域修改申请
	 * 
	 * @param apply
	 * @param result
	 * @return
	 * @throws PlatformException
	 */
	boolean handleModifyApply(SecurityDomainApply apply, boolean result) throws PlatformException;

	/**
	 * 撤销安全域申请
	 * 
	 * @param id
	 * @throws PlatformException
	 */
	void cancelApply(Long id) throws PlatformException;

	/**
	 * 删除安全域申请
	 * 
	 * @param id
	 * @throws PlatformException
	 */
	void deleteApply(Long id) throws PlatformException;

	/**
	 * 安全域归档申请
	 * 
	 * @param id
	 * @throws PlatformException
	 */
	void archiveApply(Long id, String reason) throws PlatformException;

	/**
	 * 验证安全域关联的应用的状态，若关联应用有未归档状态，则该安全域不能归档
	 * @param sdId
	 * @return true 可以归档， false 不可以归档
	 * @throws PlatformException
	 */
	boolean validateApplicationOfSercurityDomainStatus(Long sdId) throws PlatformException;
	/**
	 * 修改安全域申请
	 * 
	 * @param form
	 * @return
	 * @throws PlatformException
	 */
	boolean modifyApply(SecurityDomain form, String reason) throws PlatformException;

	/**
	 * 修改未发布的申请
	 * 
	 * @param apply
	 * @return
	 * @throws PlatformException
	 */
	boolean modifyApply(SecurityDomainApply apply) throws PlatformException;

	SecurityDomain getByAid(String sdAid);

	Page<Requistion> findPageForSD(Page<Requistion> page) throws PlatformException;

	/**
	 * 
	 * @param sdId
	 * @return
	 * @throws PlatformException
	 */
	Requistion getRequistionForSecurityDomain(Long sdId) throws PlatformException;

	Page<SecurityDomain> findUnLinkPage(Page<SecurityDomain> page, String cardBaseId);

	Page<SecurityDomain> findPageBy(Page<SecurityDomain> page, Map<String, Object> queryParams) throws PlatformException;
	
	Page<Map<String, Object>> findPage(Page<Map<String, Object>> page, Map<String, Object> queryParams) throws PlatformException;
	
	/**
	 * AID在安全域是否唯一？
	 * 
	 * @param aid
	 * @return true-唯一：指在数据库中，没有记录<br/>
	 *         false-不唯一：指在数据库中已经存在
	 */
	boolean isAidUnique(String aid);

	List<SecurityDomain> getByName(String sdName);

	List<SecurityDomain> getByLikeName(String sdName);
	
	void updateHsmkeyConfigBySecurityDomain(SecurityDomain sd, Map<String, String> map) throws PlatformException;
	
	void updateHsmkeyConfigBySecurityDomain(SecurityDomainApply apply, String encIds, String macIds, String dekIds) throws PlatformException;
}