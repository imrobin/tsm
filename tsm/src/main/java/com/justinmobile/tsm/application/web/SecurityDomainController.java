package com.justinmobile.tsm.application.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.dao.support.PropertyFilter.JoinType;
import com.justinmobile.core.dao.support.PropertyFilter.MatchType;
import com.justinmobile.core.dao.support.PropertyFilter.PropertyType;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.TlvObject;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.details.UserWithSalt;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.LoadModule;
import com.justinmobile.tsm.application.domain.Privilege;
import com.justinmobile.tsm.application.domain.SecurityDomain;
import com.justinmobile.tsm.application.domain.SecurityDomainApply;
import com.justinmobile.tsm.application.domain.SecurityDomainInstallParams;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.application.manager.SecurityDomainApplyManager;
import com.justinmobile.tsm.application.manager.SecurityDomainManager;
import com.justinmobile.tsm.cms2ac.dao.KeyProfileApplyDao;
import com.justinmobile.tsm.cms2ac.domain.HsmkeyConfig;
import com.justinmobile.tsm.cms2ac.domain.KeyProfile;
import com.justinmobile.tsm.cms2ac.domain.KeyProfileApply;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.RequistionManager;

@Controller("securityDomainController")
@RequestMapping("/securityDomain/")
public class SecurityDomainController {

	private static final Logger logger = LoggerFactory.getLogger(SecurityDomainController.class);

	@Autowired
	private SysUserManager sysUserManager;

	@Autowired
	private SecurityDomainManager securityDomainManager;

	@Autowired
	private SecurityDomainApplyManager securityDomainApplyManager;

	@Autowired
	private SpBaseInfoManager spBaseInfoManager;

	@Autowired
	private RequistionManager requistionManager;
	
	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;
	
	@Autowired
	private ApplicationManager applicationManager;
	
//	@Autowired
//	private HsmkeyConfigManager hsmkeyConfigManager;
	
	@Autowired
	private KeyProfileApplyDao keyProfileApplyDao;

	@RequestMapping
	public @ResponseBody
	JsonMessage cancelSdApply(@RequestParam Long sdApplyId) {
		JsonMessage message = new JsonMessage();

		try {
			securityDomainManager.cancelApply(sdApplyId);
			message.setSuccess(Boolean.TRUE);
			message.setMessage("撤销成功");
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage deleteSdApply(@RequestParam Long sdId) {
		JsonMessage message = new JsonMessage();

		try {
			securityDomainManager.deleteApply(sdId);
			message.setSuccess(Boolean.TRUE);
			message.setMessage("撤销成功");
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage archiveSdApplyForAdmin(@RequestParam Long sdId, @RequestParam String reason) {
		JsonMessage message = new JsonMessage();

		try {
			SecurityDomain sd = securityDomainManager.load(sdId);
			if (sd.getStatus().equals(SecurityDomain.STATUS_PUBLISHED)) {
				message = archiveSdApply(sdId, reason);
			} else {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("只有发布状态的安全域才能进行归档操作");
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage archiveSdApply(@RequestParam Long sdId, @RequestParam String reason) {
		JsonMessage message = new JsonMessage();

		try {
			// 已发布状态SD，归档操作要检查其关联的应用状态，应用有一个未归档状态，SD都不能归档
			// TODO 加判断，验证SD的APP是否有未归档的
			boolean bln = securityDomainManager.validateApplicationOfSercurityDomainStatus(sdId);
			if (bln) {
				securityDomainManager.archiveApply(sdId, reason);
				message.setSuccess(Boolean.TRUE);
				message.setMessage("归档申请提交成功，后台审核中。。。");
			} else {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("当前安全域还有未归档的应用，请将该安全域关联的所有应用全部归档后，再归档该安全域");
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage audit(HttpServletRequest request, @RequestParam Long id,@RequestParam String status) {
		JsonMessage message = new JsonMessage();
		try {
			boolean result = "yes".equalsIgnoreCase(status) ? true : false;
			String opinion = ServletRequestUtils.getStringParameter(request, "opinion");

			if(id == null) {
				id = ServletRequestUtils.getLongParameter(request, "applyId");
			}
			SecurityDomainApply apply = securityDomainApplyManager.load(id);
			Requistion requistion = apply.getRequistion();
			requistion.setReviewDate(Calendar.getInstance());

			if (result) {
				requistion.setOpinion(Requistion.OPINION_DEFAULT_AGREE);
				requistion.setStatus(Requistion.STATUS_PASS);
				requistion.setResult(Requistion.RESULT_PASS);
			} else {
				requistion.setOpinion(opinion);
				requistion.setStatus(Requistion.STATUS_REJECT);
				requistion.setResult(Requistion.RESULT_REJECT);
			}

			apply.setRequistion(requistion);

			// 判断申请类别，分类处理
			int type = apply.getRequistion().getType();
			if (type == Requistion.TYPE_SD_PUBLISH) {
				// 申请发布处理
				String hsmkeyConfigDEK = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigDEK");
				String hsmkeyConfigENC = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigENC");
				String hsmkeyConfigMAC = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigMAC");
				
				if(StringUtils.isNotBlank(hsmkeyConfigDEK)) apply.setHsmkeyConfigDEK(hsmkeyConfigDEK);
				if(StringUtils.isNotBlank(hsmkeyConfigENC)) apply.setHsmkeyConfigENC(hsmkeyConfigENC);
				if(StringUtils.isNotBlank(hsmkeyConfigMAC)) apply.setHsmkeyConfigMAC(hsmkeyConfigMAC);
				
				String keyProfileDEK = ServletRequestUtils.getStringParameter(request, "keyProfileDEK");
				String keyProfileENC = ServletRequestUtils.getStringParameter(request, "keyProfileENC");
				String keyProfileMAC = ServletRequestUtils.getStringParameter(request, "keyProfileMAC");
				
				if(StringUtils.isNotBlank(keyProfileDEK)) apply.setKeyProfileDEK(keyProfileDEK);
				if(StringUtils.isNotBlank(keyProfileENC)) apply.setKeyProfileENC(keyProfileENC);
				if(StringUtils.isNotBlank(keyProfileMAC)) apply.setKeyProfileMAC(keyProfileMAC);
				
				Map<String, Object> keyIndexs = new HashMap<String, Object>();
				
				securityDomainManager.handlePublishedApply(apply, result, keyIndexs);
			} else if (type == Requistion.TYPE_SD_ARCHIVE) {
				// 申请归档处理
				boolean sucess = securityDomainManager.handleArchivedApply(apply, result);
				if (!sucess) {
					message.setSuccess(Boolean.FALSE);
					message.setMessage("该安全域有预置关联关系，且该卡批次已经发卡");
					return message;
				}
			} else if (type == Requistion.TYPE_SD_MODIFY) {
				// 申请修改处理
				securityDomainManager.handleModifyApply(apply, result);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage remove(@RequestParam Long sdApplyId) {
		JsonMessage message = new JsonMessage();
		try {
			if (sdApplyId < 0) {
				Long sdId = Math.abs(sdApplyId);
				this.keyProfileApplyDao.removeAll(sdId);
				securityDomainApplyManager.remove(sdId);
				requistionManager.remove(sdId);
			} else {
				message.setMessage("所选的安全域已经发布或已经归档，不能删除");
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage getSd(@RequestParam Long sdId) {
		JsonMessage message = new JsonMessage();

		try {
			if (sdId < 0) {
				SecurityDomainApply apply = securityDomainApplyManager.load(Math.abs(sdId));

				if (apply != null) {
					Privilege privilege = Privilege.parse(apply.getPrivilege());

					String excludeField = "sp loadModule";
					String includeCascadeField = "sp.id";
					Map<String, Object> map = apply.toMap("status", excludeField, includeCascadeField);
					map.put("spSelect", apply.getSp().getId());
					map.put("dap", privilege.isDap());
					map.put("dapForce", privilege.isDapForce());
					map.put("token", privilege.isToken());
					map.put("lockCard", privilege.isLockCard());
					map.put("abandonCard", privilege.isAbandonCard());
					map.put("defaultSelect", privilege.isDefaultSelect());
					map.put("cvm", privilege.isCvm());
					map.put("spRule", apply.getSpaceRule());
					map.put("applyId", apply.getId());
					
					//key profile
					if(apply.getRequistion() != null && apply.getRequistion().getOriginalId() != null) {
						SecurityDomain sd = securityDomainManager.load(apply.getRequistion().getOriginalId());
						List<KeyProfile> list = sd.getKeyProfiles();
						if(list != null && !list.isEmpty()) {
							for(KeyProfile keyProfile : list) {
								Integer index = keyProfile.getIndex();
								String value = keyProfile.getValue();
								String key = "";
								if(index.equals(KeyProfile.INDEX_ENC)) {
									key = "keyProfileENC";
								} else if(index.equals(KeyProfile.INDEX_MAC)) {
									key = "keyProfileMAC";
								} else if(index.equals(KeyProfile.INDEX_DEK)) {
									key = "keyProfileDEK";
								}
								map.put(key, value);
								
							}
						}
					} else {
						map.put("keyProfileENC", apply.getKeyProfileENC());
						map.put("keyProfileMAC", apply.getKeyProfileMAC());
						map.put("keyProfileDEK", apply.getKeyProfileDEK());
					}
					
					
					message.setMessage(map);
				}
			} else {
				SecurityDomain sd = securityDomainManager.load(sdId);
				if (sd != null) {
					Privilege privilege = Privilege.parse(sd.getPrivilege());

					String excludeField = "sp loadModule";
					String includeCascadeField = "sp.id";
					Map<String, Object> map = sd.toMap("status", excludeField, includeCascadeField);
					map.put("hasLock", sd.getHasLock());
					map.put("spSelect", sd.getSp().getId());
					map.put("dap", privilege.isDap());
					map.put("dapForce", privilege.isDapForce());
					map.put("token", privilege.isToken());
					map.put("lockCard", privilege.isLockCard());
					map.put("abandonCard", privilege.isAbandonCard());
					map.put("defaultSelect", privilege.isDefaultSelect());
					map.put("cvm", privilege.isCvm());
					map.put("spRule", sd.getSpaceRule());
					map.put("deleteRule", sd.getDeleteRule());
					
					List<KeyProfile> list = sd.getKeyProfiles();
					if(list != null && !list.isEmpty()) {
						for(KeyProfile keyProfile : list) {
							Integer index = keyProfile.getIndex();
							String value = keyProfile.getValue();
							String key = "";
							if(index.equals(KeyProfile.INDEX_ENC)) {
								key = "keyProfileENC";
							} else if(index.equals(KeyProfile.INDEX_MAC)) {
								key = "keyProfileMAC";
							} else if(index.equals(KeyProfile.INDEX_DEK)) {
								key = "keyProfileDEK";
							}
							map.put(key, value);
							
							List<HsmkeyConfig> configs = keyProfile.getHsmKeyConfigs();
							logger.debug("List<HsmkeyConfig> configs is empty : " + configs.isEmpty());
							if(configs != null && !configs.isEmpty()) {
								HsmkeyConfig hsmkey = configs.get(0);
								map.put(key+"index", hsmkey.getIndex());
								map.put(key+"version", hsmkey.getVersion());
								logger.debug(hsmkey.toString());
							}
						}
					}
					
					//取当前最近的Requistion id
					Requistion requistion = securityDomainManager.getRequistionForSecurityDomain(sdId);
					if(requistion != null) {
						map.put("sdApplyId", requistion.getId());
					}
					
					message.setMessage(map);

				}
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	/**
	 * 后台管理界面，审核安全域列表显示用
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult list(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			Integer status = ServletRequestUtils.getIntParameter(request, "search_EQI_status");
			String name = ServletRequestUtils.getStringParameter(request, "search_ALIAS_spL_LIKES_name");
			//List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Map<String, Object> params = new HashMap<String, Object>();
			String province = ((UserWithSalt)SpringSecurityUtils.getCurrentUser()).getProvince();
			if(!StringUtils.isBlank(province)) {
				params.put("province", province);
			}
			if(!StringUtils.isBlank(name)) {
				params.put("name", name);
			}
			params.put("status", new Integer[] {status});
			
			if (status.equals(SecurityDomain.STATUS_INIT)) {
				// SecurityDomainApply
				Page<SecurityDomainApply> page = SpringMVCUtils.getPage(request);
				params.put("sdStatus", SecurityDomain.STATUS_INIT);
				page = securityDomainApplyManager.findPage(page, null, params);
				Page<Map<String, Object>> _page = SpringMVCUtils.getPage(request);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (SecurityDomainApply apply : page.getResult()) {
					Map<String, Object> map = apply.toMap(null, "sp.name");
					map.put("id", -apply.getId());
					list.add(map);
				}
				_page.setResult(list);
				_page.setTotalCount(page.getTotalCount());
				result.setPage(_page);
			} else {
				// SecurityDomain
				Page<SecurityDomain> page = SpringMVCUtils.getPage(request);
				page = securityDomainManager.findPageBy(page, params);//.findPage(page, filters);
				String includeCascadeField = "sp.name";
				result.setPage(page, null, includeCascadeField);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		}

		return result;
	}

	/**
	 * 后台管理界面，审核安全域列表显示用
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody JsonResult listAudit(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			Page<SecurityDomainApply> page = SpringMVCUtils.getPage(request);
			String orderBy = ServletRequestUtils.getStringParameter(request, "page_orderBy");
			if (StringUtils.isBlank(orderBy)) {
				orderBy = "applyDate_desc";
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", ServletRequestUtils.getStringParameter(request, "search_LIKES_name"));
			String province = ((UserWithSalt)SpringSecurityUtils.getCurrentUser()).getProvince();
			if(!StringUtils.isBlank(province)) {
				params.put("province", province);
			}
			params.put("requistionStatus", Requistion.STATUS_INIT);
			page = securityDomainApplyManager.findPage(page, orderBy, params);
			String includeCascadeField = "requistion.reason requistion.status requistion.submitDate sp.name";
			result.setPage(page, null, includeCascadeField);

			logger.debug(result.getResult().toString());

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody JsonResult listSelf(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			Page<SecurityDomainApply> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(currentUserName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			SysUser user = sysUserManager.getUserByName(currentUserName);

			filters.add(new PropertyFilter("sp", JoinType.I, "id", MatchType.EQ, PropertyType.L, user.getId().toString()));
			page = securityDomainApplyManager.findPage(page, filters);
			String includeCascadeField = "requistion.opinion requistion.reviewDate requistion.result requistion.status requistion.type";
			result.setPage(page, null, includeCascadeField);

			logger.debug(result.getResult().toString());

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody JsonResult listSelfByStatus(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			Page<SecurityDomain> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			
			if (StringUtils.isBlank(currentUserName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			
			SysUser user = sysUserManager.getUserByName(currentUserName);

			filters.add(new PropertyFilter("sp", JoinType.I, "id", MatchType.EQ, PropertyType.L, user.getId().toString()));
			page = securityDomainManager.findPage(page, filters);
			result.setPage(page, null, null);

			logger.debug(result.getResult().toString());

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody JsonResult listBySpAndStatus(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			Page<SecurityDomain> page = SpringMVCUtils.getPage(request);
			
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			
			if (StringUtils.isBlank(currentUserName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			
			SysUser user = sysUserManager.getUserByName(currentUserName);

			Integer[] status = new Integer[] {SecurityDomain.STATUS_PUBLISHED};
			
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("spId", user.getId());
			queryParams.put("status", status);
			page = securityDomainManager.findPageBy(page, queryParams);
			
			result.setPage(page, null, null);

			logger.debug(result.getResult().toString());

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		}

		return result;
	}
	
	/**
	 * 后台查询安全域对应的订购用户清单<br/>
	 *  /admin/sd/js/sd.js function openSubscribeTable  
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody JsonResult listSubscribe(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			Page<CustomerCardInfo> page = SpringMVCUtils.getPage(request);
			long id = ServletRequestUtils.getLongParameter(request, "id");
			SecurityDomain securityDomain = securityDomainManager.load(id);
			page = customerCardInfoManager.getCustomerCardInfoPageBySd(page, securityDomain);
			result.setPage(page, null, "customer.nickName card.cardNo");

			logger.debug(result.getResult().toString());

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		}

		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonResult getApplyList(HttpServletRequest request) {
		JsonResult result = new JsonResult();

		try {
			Page<SecurityDomainApply> page = SpringMVCUtils.getPage(request);
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(currentUserName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			SysUser user = sysUserManager.getUserByName(currentUserName);

			page = securityDomainApplyManager.findPage(page, user.getId());
			String includeCascadeField = "requistion.opinion requistion.result requistion.reviewDate requistion.submitDate";
			result.setPage(page, null, includeCascadeField);

			logger.debug(result.getResult().toString());

		} catch (PlatformException e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(Boolean.FALSE);
		}

		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage add(HttpServletRequest request, HttpServletResponse response) {
		JsonMessage message = new JsonMessage();
		try {
			SecurityDomainApply sd = new SecurityDomainApply();
			BindingResult bindingResult = SpringMVCUtils.bindObject(request, sd);
			
			Boolean token = ServletRequestUtils.getBooleanParameter(request, "token");
			checkInstallParams(sd, null, token);
			Privilege privilege = new Privilege();
			privilege.setSd(true);
			SpringMVCUtils.bindObject(request, privilege);

			checkPrivilege(privilege, sd);
			
			LoadModule loadModule = securityDomainManager.getIsd().getLoadModule();
			SpBaseInfo sp = spBaseInfoManager.load(ServletRequestUtils.getLongParameter(request, "spId"));

			String rid = sp.getRid().toUpperCase();
			String aid = sd.getAid().toUpperCase();
			if(!aid.startsWith(rid)) throw new PlatformException(PlatformErrorCode.SP_RID_DISCARD);
			
			if (bindingResult.hasErrors()) {
				message.setMessage(bindingResult.getGlobalError());
				message.setSuccess(Boolean.FALSE);
			} else {
				sd.setSp(sp);
				sd.setLoadModule(loadModule);
				sd.setPrivilege(privilege.biuld());
				sd.setModel(privilege.getModel());
				sd.setStatus(SecurityDomain.STATUS_INIT);
				boolean bln = securityDomainManager.applySecurityDomain(sd);
				message.setSuccess(bln);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	/**
	 * 申请安全域<br/>
	 * 申请数据全部添加到SecurityDomainApply对象中，待审核通过后再生成SecurityDomain<br/>
	 * /home/sp/applySd.jsp<br/>
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage apply(HttpServletRequest request, HttpServletResponse response) {
		JsonMessage message = new JsonMessage();
		try {
			//权限
			Privilege privilege = new Privilege();
			privilege.setSd(true);
			SpringMVCUtils.bindObject(request, privilege);

			logger.debug("\n\nsecurity domain privilege value is " + privilege);

			LoadModule loadModule = securityDomainManager.getIsd().getLoadModule();
			logger.debug(loadModule.toString());

			//验证当前用户是否登录
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(currentUserName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			SysUser user = sysUserManager.getUserByName(currentUserName);
			SpBaseInfo sp = spBaseInfoManager.load(user.getId());
			logger.debug(sp.toString());
			
			SecurityDomainApply apply = new SecurityDomainApply();
			BindingResult bindingResult = SpringMVCUtils.bindObject(request, apply);
			Boolean token = ServletRequestUtils.getBooleanParameter(request, "token");
			checkInstallParams(apply, null, token);

			if (bindingResult.hasErrors()) {
				message.setMessage(bindingResult.getGlobalError());
			} else {
				
				//检查权限：代理模式，密钥必填；委托模式，URL必填
				checkPrivilege(privilege, apply);
				
				apply.setSp(sp);
				apply.setLoadModule(loadModule);
				apply.setPrivilege(privilege.biuld());
				apply.setModel(privilege.getModel());
				apply.setStatus(SecurityDomain.STATUS_INIT);

				boolean bln = securityDomainManager.applySecurityDomain(apply);

				message.setSuccess(bln);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	private void checkPrivilege(Privilege privilege, SecurityDomainApply apply) throws Exception {
		if(privilege.isToken()) {
			//委托
			if(StringUtils.isBlank(apply.getBusinessPlatformUrl())) {
				throw new Exception("业务平台URL未填写");
			}
			if(StringUtils.isBlank(apply.getServiceName())) {
				throw new Exception("业务平台服务名未填写");
			}
		} else {
			//代理:啥子都没选
			if(apply.getCurrentKeyVersion() == null) {
				throw new Exception("安全域密钥版本号未填写");
			}
			if(StringUtils.isBlank(apply.getKeyProfileDEK())) {
				throw new Exception("DEK密钥未填写");
			}
			if(StringUtils.isBlank(apply.getKeyProfileENC())) {
				throw new Exception("ENC密钥未填写");
			}
			if(StringUtils.isBlank(apply.getKeyProfileMAC())) {
				throw new Exception("MAC密钥未填写");
			}
		}
	}

	@RequestMapping
	public @ResponseBody JsonMessage sdLoad(@RequestParam("sdid") Long sdid, @RequestParam Integer status) throws Exception {
		JsonMessage msg = new JsonMessage();

		try {
			String excludeField = "sp loadModule";
			String includeCascadeField = null;

			if (status.equals(SecurityDomain.STATUS_INIT)) {
				SecurityDomainApply apply = securityDomainApplyManager.load(sdid);
				Privilege privilege = Privilege.parse(apply.getPrivilege());
				Map<String, Object> map = apply.toMap(excludeField, includeCascadeField);
				map.put("dap", privilege.isDap());
				map.put("dapForce", privilege.isDapForce());
				map.put("token", privilege.isToken());
				map.put("lockCard", privilege.isLockCard());
				map.put("abandonCard", privilege.isAbandonCard());
				map.put("defaultSelect", privilege.isDefaultSelect());
				map.put("cvm", privilege.isCvm());
				
				//key profile
				
				if(apply.getRequistion() != null && apply.getRequistion().getOriginalId() != null) {
					
					SecurityDomain sd = securityDomainManager.load(apply.getRequistion().getOriginalId());
					List<KeyProfile> list = sd.getKeyProfiles();
					if(list != null && !list.isEmpty()) {
						for(KeyProfile keyProfile : list) {
							Integer index = keyProfile.getIndex();
							String value = keyProfile.getValue();
							String key = "";
							if(index.equals(KeyProfile.INDEX_ENC)) {
								key = "keyProfileENC";
							} else if(index.equals(KeyProfile.INDEX_MAC)) {
								key = "keyProfileMAC";
							} else if(index.equals(KeyProfile.INDEX_DEK)) {
								key = "keyProfileDEK";
							}
							map.put(key, value);
						}
					}
				} else {
					map.put("keyProfileENC", apply.getKeyProfileENC());
					map.put("keyProfileMAC", apply.getKeyProfileMAC());
					map.put("keyProfileDEK", apply.getKeyProfileDEK());
				}
				
				msg.setMessage(map);
			} else {
				SecurityDomain sd = securityDomainManager.load(sdid);
				Privilege privilege = Privilege.parse(sd.getPrivilege());
				Map<String, Object> map = sd.toMap(excludeField, includeCascadeField);
				map.put("dap", privilege.isDap());
				map.put("dapForce", privilege.isDapForce());
				map.put("token", privilege.isToken());
				map.put("lockCard", privilege.isLockCard());
				map.put("abandonCard", privilege.isAbandonCard());
				map.put("defaultSelect", privilege.isDefaultSelect());
				map.put("cvm", privilege.isCvm());
				map.put("deleteRule", sd.getDeleteRule() == null ? 0 : sd.getDeleteRule());
				
				List<KeyProfile> list = sd.getKeyProfiles();
				if(list != null && !list.isEmpty()) {
					for(KeyProfile keyProfile : list) {
						Integer index = keyProfile.getIndex();
						String value = keyProfile.getValue();
						String key = "";
						if(index.equals(KeyProfile.INDEX_ENC)) {
							key = "keyProfileENC";
						} else if(index.equals(KeyProfile.INDEX_MAC)) {
							key = "keyProfileMAC";
						} else if(index.equals(KeyProfile.INDEX_DEK)) {
							key = "keyProfileDEK";
						}
						map.put(key, value);
						
						List<HsmkeyConfig> hsmkeyConfigs = keyProfile.getHsmKeyConfigs();
						if(hsmkeyConfigs != null && !hsmkeyConfigs.isEmpty()) {
							String _value = "";
							for(HsmkeyConfig e : hsmkeyConfigs) {
								_value += "," + e.getId();
							}
							_value = _value.replaceFirst(",", "");
							switch (keyProfile.getIndex()) {
							case KeyProfile.INDEX_DEK :
								map.put("hsmkeyConfigDEK", _value);
								break;
							case KeyProfile.INDEX_ENC : 
								map.put("hsmkeyConfigENC", _value);
								break;
							case KeyProfile.INDEX_MAC : 
								map.put("hsmkeyConfigMAC", _value);
								break;
							default:
								break;
							}
						}
					}
				}
				
				msg.setMessage(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		}

		return msg;
	}

	@RequestMapping
	public @ResponseBody JsonMessage sdApplyLoad(@RequestParam("sdid") Long sdid) throws Exception {
		JsonMessage msg = new JsonMessage();

		try {

			SecurityDomainApply sd = securityDomainApplyManager.load(sdid);
			Requistion requistion = sd.getRequistion();
			
			// parser security domain install parameter
			Privilege privilege = Privilege.parse(sd.getPrivilege());
			logger.debug(privilege.toString());

			String excludeField = "loadModule";
			String includeCascadeField = "sp.id";
			Map<String, Object> map = sd.toMap(excludeField, includeCascadeField);
			map.put("dap", privilege.isDap());
			map.put("dapForce", privilege.isDapForce());
			map.put("token", privilege.isToken());
			map.put("lockCard", privilege.isLockCard());
			map.put("abandonCard", privilege.isAbandonCard());
			map.put("defaultSelect", privilege.isDefaultSelect());
			map.put("cvm", privilege.isCvm());
			Map<String, Object> sdInstallParams = parseInstallParams(sd.getInstallParams());
			if (!sdInstallParams.isEmpty()) {
				map.put("ip", sdInstallParams);
			}
			if(requistion != null && requistion.getType().equals(Requistion.TYPE_SD_PUBLISH)) {
				map.put("isPublishApply", true);
			} else {
				map.put("isPublishApply", false);
			}
			
			//KeyProfileApply--HsmkeyConfig
			List<KeyProfileApply> list = this.keyProfileApplyDao.findByProperty("securityDomainApply", sd);
			if(list != null && !list.isEmpty()) {
				List<HsmkeyConfig> hsmkeyConfigs = null;
				for(KeyProfileApply keyProfile : list) {
					hsmkeyConfigs = keyProfile.getHsmKeyConfigs();
					if(hsmkeyConfigs != null && !hsmkeyConfigs.isEmpty()) {
						String value = "";
						for(HsmkeyConfig e : hsmkeyConfigs) {
							value += "," + e.getId();
						}
						value = value.replaceFirst(",", "");
						switch (keyProfile.getIndex()) {
						case KeyProfile.INDEX_DEK :
							map.put("hsmkeyConfigDEK", value);
							break;
						case KeyProfile.INDEX_ENC : 
							map.put("hsmkeyConfigENC", value);
							break;
						case KeyProfile.INDEX_MAC : 
							map.put("hsmkeyConfigMAC", value);
							break;
						default:
							break;
						}
					}
				}
			}
			
			msg.setMessage(map);

		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		}

		return msg;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage sdModify(HttpServletRequest request, HttpServletResponse response) {
		JsonMessage message = new JsonMessage();
		try {
			String reason = ServletRequestUtils.getStringParameter(request, "reason");
			Long sdId = ServletRequestUtils.getLongParameter(request, "sdId");

			if (sdId < 0) {
				//待审核SD所有内容都可以改
				sdId = Math.abs(sdId);
				request.setAttribute("id", sdId);
				message = sdApplyModify(request, response);

			} else {
				// 后台管理-修改安全域：修改“已发布”的Token安全域，
				// 不应该提示“公共第三方安全域或DAP安全域，必须接受迁移”
				// 和“公共第三方安全域或DAP安全域，必须接受从主安全域发起的应用删除”。

				SecurityDomain sd = new SecurityDomain();
				SpringMVCUtils.bindObject(request, sd);
				sd.setId(sdId);
				SecurityDomain originalSd = this.securityDomainManager.load(sdId);
				Boolean token = ServletRequestUtils.getBooleanParameter(request, "token");
				if (token == null) token = Privilege.parse(originalSd.getPrivilege()).isToken();
				checkInstallParams(null, sd, token);
				
				sd.setPrivilege(originalSd.getPrivilege());
				
				// 发布状态，只能修改安全域名称和安装参数，且要审核通过后才能生效
				SecurityDomain original = securityDomainManager.load(sd.getId());
				boolean isEqualWithName        = sd.getSdName().equals( original.getSdName());
				boolean isEqualWithInstall     = sd.getInstallParams().equals(original.getInstallParams());
				boolean isScp02SecurityLevel   = sd.getScp02SecurityLevel().equals(original.getScp02SecurityLevel());
				
				boolean isEqualWithUrl         = false;
				boolean isEqualWithServiceName = false;
				
				if(token) {
					if(StringUtils.isBlank(sd.getBusinessPlatformUrl())) {
						throw new Exception("业务平台URL未填写");
					}
					if(StringUtils.isBlank(sd.getServiceName())) {
						throw new Exception("业务平台服务名未填写");
					}
					isEqualWithUrl         = sd.getBusinessPlatformUrl().equals(original.getBusinessPlatformUrl());
					isEqualWithServiceName = sd.getServiceName().equals(original.getServiceName());
				}
				
				// 防止用户啥子都不修改就提交修改申请的情况出现
				if (isEqualWithName && isEqualWithInstall && isScp02SecurityLevel && isEqualWithUrl && isEqualWithServiceName) {
					message.setMessage("请勿提交无效的申请");
					message.setSuccess(Boolean.FALSE);
				} else {
					boolean bln = securityDomainManager.modifyApply(sd, reason);
					if (bln)
						message.setMessage("修改申请提交成功，后台审核中。。。");
				}
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody JsonMessage sdModifyKeyVersion(HttpServletRequest request, HttpServletResponse response) {
		JsonMessage message = new JsonMessage();
		try {
			
			SecurityDomain sd = securityDomainManager.load(ServletRequestUtils.getLongParameter(request, "sdId"));
			int currentKeyVersion = ServletRequestUtils.getIntParameter(request, "currentKeyVersion");
			sd.setCurrentKeyVersion(currentKeyVersion);
			securityDomainManager.saveOrUpdate(sd);
			
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage sdModifyKeyProfile(HttpServletRequest request, HttpServletResponse response) {
		JsonMessage message = new JsonMessage();
		try {
			
			String dekIds = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigDEK");
			String encIds = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigENC");
			String macIds = ServletRequestUtils.getStringParameter(request, "hsmkeyConfigMAC");
			
			String keyProfileDEK = ServletRequestUtils.getStringParameter(request, "keyProfileDEK");
			String keyProfileENC = ServletRequestUtils.getStringParameter(request, "keyProfileENC");
			String keyProfileMAC = ServletRequestUtils.getStringParameter(request, "keyProfileMAC");
			
			Long sdId = ServletRequestUtils.getLongParameter(request, "sdid");
			if(sdId < 0) {
				//SecurityDomainApply
				long id = Math.abs(sdId);
				SecurityDomainApply apply = this.securityDomainApplyManager.load(id);
				apply.setKeyProfileDEK(keyProfileDEK);
				apply.setKeyProfileENC(keyProfileENC);
				apply.setKeyProfileMAC(keyProfileMAC);
				this.securityDomainManager.updateHsmkeyConfigBySecurityDomain(apply, encIds, macIds, dekIds);
			} else {
				//SecurityDomain
				SecurityDomain sd = this.securityDomainManager.load(sdId);
				Map<String, String> map = new HashMap<String, String>();
				map.put("dekIds", dekIds);
				map.put("encIds", encIds);
				map.put("macIds", macIds);
				map.put("keyProfileDEK", keyProfileDEK);
				map.put("keyProfileENC", keyProfileENC);
				map.put("keyProfileMAC", keyProfileMAC);
				this.securityDomainManager.updateHsmkeyConfigBySecurityDomain(sd, map);
			}
			
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage sdApplyModify(HttpServletRequest request, HttpServletResponse response) {
		JsonMessage message = new JsonMessage();
		try {

			SecurityDomainApply apply = new SecurityDomainApply();
			SpringMVCUtils.bindObject(request, apply);
			apply.setId(ServletRequestUtils.getLongParameter(request, "applyId"));
			// RID & AID 验证
			Long spId = ServletRequestUtils.getLongParameter(request, "spId");
			SpBaseInfo sp = spBaseInfoManager.load(spId);
			String rid = sp.getRid();
			String aid = apply.getAid();
			logger.debug("\n"+rid+":"+aid+"\n");
			if(!aid.startsWith(rid)) {
				throw new PlatformException(PlatformErrorCode.SP_RID_DISCARD);
			}
			
			apply.setSp(sp);
			
			// AID 验证
			SecurityDomainApply original = securityDomainApplyManager.load(apply.getId());
			Requistion requistion = original.getRequistion();
			if (requistion.getStatus().equals(Requistion.STATUS_REJECT) || requistion.getStatus().equals(Requistion.STATUS_INIT)) {
				boolean aidExsits = securityDomainManager.validateSecurityDomainAid(apply.getAid(), original.getAid());
				if (!aidExsits) {
					throw new PlatformException(PlatformErrorCode.SD_AID_EXISTED);
				}
			}
			
			Privilege privilege = new Privilege();
			privilege.setSd(true);
			SpringMVCUtils.bindObject(request, privilege);
			//验证权限跟相关字段的匹配关系
			checkPrivilege(privilege, apply);
			
			// URL 验证
			if(privilege.isToken()) {
				applicationManager.validateBuissinessUrl(apply.getBusinessPlatformUrl(), apply.getServiceName());
			}
			
			Boolean token = ServletRequestUtils.getBooleanParameter(request, "token");
			checkInstallParams(apply, null, token);
			apply.setPrivilege(privilege.biuld());
			apply.setModel(privilege.getModel());

			securityDomainManager.modifyApply(apply);

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	/**
	 * 检查安全域AID是否可用
	 * 
	 * @param aid
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage checkAid(@RequestParam("aid") String aid,
			@RequestParam("originalAid") String originalAid) {
		JsonMessage msg = new JsonMessage();
		boolean bln = false;

		try {
			bln = securityDomainManager.validateSecurityDomainAid(
					aid.toUpperCase(), originalAid.toUpperCase());

		} catch (Exception e) {
			e.printStackTrace();
		}
		msg.setSuccess(bln);
		return msg;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage exportConstant() {
		JsonMessage message = new JsonMessage();

		try {
			Map<String, Integer> constant = new HashMap<String, Integer>();

			constant.put("STATUS_INIT", SecurityDomain.STATUS_INIT);
			constant.put("STATUS_PUBLISHED", SecurityDomain.STATUS_PUBLISHED);
			constant.put("STATUS_ARCHIVED", SecurityDomain.STATUS_ARCHIVED);

			constant.put("MODEL_ISD", SecurityDomain.MODEL_ISD);
			constant.put("MODEL_COMMON", SecurityDomain.MODEL_COMMON);
			constant.put("MODEL_DAP", SecurityDomain.MODEL_DAP);
			constant.put("MODEL_TOKEN", SecurityDomain.MODEL_TOKEN);

			message.setMessage(constant);
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils
					.getParameters(request);
			Page<SecurityDomain> page = SpringMVCUtils.getPage(request);

			page = securityDomainManager.findPage(page, filters);
			result.setPage(page, null, "sp.name");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getApplyType(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();

		try {

			Requistion requistion = securityDomainManager
					.getRequistionForSecurityDomain(id);
			if (requistion != null) {

				if (requistion.getType().equals(Requistion.TYPE_SD_MODIFY)) {
					message.setMessage("修改");
				} else if (requistion.getType().equals(
						Requistion.TYPE_SD_ARCHIVE)) {
					message.setMessage("归档");
				}

			} else {
				message.setMessage("没有记录");
				message.setSuccess(Boolean.FALSE);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage getApply(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();

		try {

			Requistion requistion = securityDomainManager.getRequistionForSecurityDomain(id);
			if (requistion != null) {
				message.setMessage(requistion.toMap(null, null));
			} else {
				message.setMessage("没有记录");
				message.setSuccess(Boolean.FALSE);
			}

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	//
	@RequestMapping
	public @ResponseBody
	JsonMessage signApply(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();

		try {

			Requistion requistion = requistionManager.load(id);
			requistion.setApplicantReview(Calendar.getInstance());
			requistionManager.saveOrUpdate(requistion);

		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}

		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage createInstallParams(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		SecurityDomainInstallParams sdip = new SecurityDomainInstallParams();
		try {
			SpringMVCUtils.bindObject(request, sdip);
			int deleteSelf = ServletRequestUtils.getIntParameter(request, "deleteSelf");
			int transfer = ServletRequestUtils.getIntParameter(request, "transfer");
			int deleteApp = ServletRequestUtils.getIntParameter(request, "deleteApp");
			int installApp = ServletRequestUtils.getIntParameter(request, "installApp");
			int downloadApp = ServletRequestUtils.getIntParameter(request, "downloadApp");
			int lockedApp = ServletRequestUtils.getIntParameter(request, "lockedApp");
			sdip.setBaseProp(transfer + 2 * deleteSelf + 4 * deleteApp + 8 * installApp + 16 * downloadApp + 32 * lockedApp);
			logger.debug("\nSDIP : " + sdip.getBaseProp());
			String scp = ServletRequestUtils.getStringParameter(request, "scp");
			if (scp.equals("-1")) {
				sdip.setSecurityChannel("");
				sdip.setSecurityChannelOption("");
			} else {
				String[] str = scp.split(",");
				String securityChannel = str[0];
				String securityChannelOption = str[1];
				sdip.setSecurityChannel(securityChannel);
				sdip.setSecurityChannelOption(securityChannelOption);
			}
			String installParams = sdip.build();
			message.setMessage(installParams);
			message.setSuccess(true);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		}
		return message;
	}

	public Map<String, Object> parseInstallParams(final String installParams) {
		logger.debug("\nSD InstallParams : "+installParams+"\n");
		Map<String, Object> message = new HashMap<String, Object>(9);
		TlvObject c9 = TlvObject.parse(installParams);
		c9 = TlvObject.parse(c9.getByTag("c9"));
		int tag_45 = ConvertUtils.byteArray2Int(c9.getByTag("45"));
		String baseProp = ByteUtils.intToBinaryString(tag_45, 8);
		
		//八位二进制 XXXX XXXX
		message.put("transfer", baseProp.substring(7));      //右起第一位
		message.put("deleteSelf", baseProp.substring(6, 7)); //右起第二位
		message.put("deleteApp", baseProp.substring(5, 6));  //右起第三位
		message.put("installApp", baseProp.substring(4, 5)); //右起第四位
		message.put("downloadApp", baseProp.substring(3, 4));//右起第五位
		message.put("lockedApp", baseProp.substring(2, 3));  //右起第六位
		logger.debug("\nSD Base Property : "+baseProp+"\n"+message);

		if (c9.getByTag("46").length == 0) {
			message.put("keyVersion", "");
		} else {
			int tag_46 = ConvertUtils.byteArray2Int(c9.getByTag("46"));
			message.put("keyVersion", tag_46);
		}
		if (c9.getByTag("47").length == 0) {
			message.put("scp", "-1");
		} else {
			String tag_47 = ConvertUtils.byteArray2HexString(c9.getByTag("47"));
			message.put("scp",
					tag_47.substring(0, 2) + "," + tag_47.substring(2, 4));
		}
		if (c9.getByTag("48").length == 0) {
			message.put("maxFailCount", "");
		} else {
			int tag_48 = ConvertUtils.byteArray2Int(c9.getByTag("48"));
			message.put("maxFailCount", tag_48);
		}
		if (c9.getByTag("49").length == 0) {
			message.put("managedVolatileSpace", "");
			message.put("managedNoneVolatileSpace", "");
		} else {
			String tag_49 = ConvertUtils.byteArray2HexString(c9.getByTag("49"));
			message.put("managedVolatileSpace",
					ConvertUtils.hexString2Int(tag_49.substring(0, 4)));
			message.put("managedNoneVolatileSpace",
					ConvertUtils.hexString2Int(tag_49.substring(4, 12)));
		}
		if (c9.getByTag("4a").length == 0) {
			message.put("maxKeyNumber", "");
		} else {
			int tag_4a = ConvertUtils.byteArray2Int(c9.getByTag("4a"));
			message.put("maxKeyNumber", tag_4a);
		}
		return message;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage parseInstallParams(HttpServletRequest request) {
		JsonMessage msg = new JsonMessage();
		Map<String, Object> message = new HashMap<String, Object>(9);
		try {
			String installParams = ServletRequestUtils.getRequiredStringParameter(request, "installParams");
			message = parseInstallParams(installParams);

			msg.setMessage(message);
			msg.setSuccess(true);
		} catch (PlatformException e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		}
		return msg;
	}

	@RequestMapping()
	public @ResponseBody
	JsonResult findUnLinkPage(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SecurityDomain> page = SpringMVCUtils.getPage(request);
			String cardBaseId = request.getParameter("cardBaseId");
			page = securityDomainManager.findUnLinkPage(page, cardBaseId);
			result.setPage(page, null, null);
		} catch (PlatformException pe) {
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	@RequestMapping()
	public @ResponseBody
	JsonMessage getSdStatus(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		try {
			String sdId = request.getParameter("sdId");
			SecurityDomain sd = securityDomainManager.load(Long.valueOf(sdId));
			Map<String, Object> map = sd.toMap(null, null);
			result.setMessage(map);
		} catch (PlatformException pe) {
			pe.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	private void checkInstallParams(SecurityDomainApply sdApply, SecurityDomain sd, Boolean token) throws PlatformException {
		String installParams = "";
		Integer deleteRule = null;
		if (sdApply == null) {
			installParams = sd.getInstallParams();
			deleteRule = sd.getDeleteRule();
		} else if (sd == null) {
			installParams = sdApply.getInstallParams();
			deleteRule = sdApply.getDeleteRule();
		}
		TlvObject c9;
		try {
			c9 = TlvObject.parse(installParams);
			c9 = TlvObject.parse(c9.getByTag("c9"));
		} catch (Exception ex) {
			throw new PlatformException(PlatformErrorCode.SD_INSTALLPARAMS_PARSE_ERROR);
		}
		int tag_45 = ConvertUtils.byteArray2Int(c9.getByTag("45"));
		String transfer = ByteUtils.intToBinaryString(tag_45, 8).substring(7);
		String deleteApp = ByteUtils.intToBinaryString(tag_45, 8).substring(5, 6);
		String deleteSelf = ByteUtils.intToBinaryString(tag_45, 8).substring(6, 7);
		if (deleteSelf.equals(String.valueOf(SecurityDomainInstallParams.ACCPET_DELETESELF))) {
			if (deleteRule == 2) {
				//throw new PlatformException(PlatformErrorCode.SD_INSTALLPARAMS_DELETERULE_ERROR);
			}
		} else if (deleteSelf.equals(String.valueOf(SecurityDomainInstallParams.NOT_ACCEPT_DELETSELF))) {
			if (deleteRule != 2) {
				throw new PlatformException(PlatformErrorCode.SD_INSTALLPARAMS_DELETERULE_ERROR);
			}
		}
		if (token == null && transfer.equals(String.valueOf(SecurityDomainInstallParams.NOT_ACCEPT_TRANSFER))) {
			throw new PlatformException(PlatformErrorCode.SD_INSTALLPARAMS_TRANSFER_ERROR);
		}
		if (token == null && deleteApp.equals(String.valueOf(SecurityDomainInstallParams.NOT_ACCEPT_DELETEAPP))) {
			throw new PlatformException(PlatformErrorCode.SD_INSTALLPARAMS_DELETEAPP_ERROR);
		}
	}
	
}
