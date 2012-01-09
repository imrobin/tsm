package com.justinmobile.tsm.sp.web;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.ServletUtils;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.details.UserWithSalt;
import com.justinmobile.security.domain.SysRole;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.manager.SysRoleManager;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.sp.domain.RecommendSp;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;
import com.justinmobile.tsm.sp.manager.RecommendSpManager;
import com.justinmobile.tsm.sp.manager.SpBaseInfoApplyManager;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.RequistionFactory;
import com.justinmobile.tsm.system.manager.RequistionManager;

@Controller("spBaseInfoController")
@RequestMapping("/spBaseInfo/")
public class SpBaseInfoController {

	private static final Logger logger = LoggerFactory.getLogger(SpBaseInfoController.class);
	
	private static final String prefix = "SP";
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private SysRoleManager sysRoleManager;
	
	@Autowired
	private SpBaseInfoManager spBaseInfoManager;
	
	@Autowired
	private ApplicationManager applicationManager;
	
	@Autowired
	private SpBaseInfoApplyManager spBaseInfoApplyManager;
	
	@Autowired
	private RecommendSpManager recommendSpManager;
	
	@Autowired
	private RequistionManager requistionManager;
	
//	@Autowired
//	private SysUserManager userManager;
	
	private SpBaseInfo getSpBaseInfo() throws PlatformException {
		String currentUserName = SpringSecurityUtils.getCurrentUserName();
		if (StringUtils.isBlank(currentUserName)) {
			throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
		}
		SysUser user = sysUserManager.getUserByName(currentUserName);
		
		String roleName = user.getSysRole().getRoleName();
		SysRole role = sysRoleManager.getRoleByName(roleName);
		if(role == null) {
			throw new PlatformException(PlatformErrorCode.SP_NOT_EXIST);
		}
		SpBaseInfo sp = spBaseInfoManager.load(user.getId());
		sp.setHasLogo(ArrayUtils.isNotEmpty(sp.getFirmLogo()));
		return sp;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage modifyPassword(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String password = ServletRequestUtils.getStringParameter(request, "password");
			String currentUserName = SpringSecurityUtils.getCurrentUserName();
			if (StringUtils.isBlank(currentUserName)) {
				throw new PlatformException(PlatformErrorCode.USER_NOT_LOGIN);
			}
			SysUser user = sysUserManager.getUserByName(currentUserName);
			user.setPassword(password);
			sysUserManager.saveOrUpdate(user);
			message.setSuccess(Boolean.TRUE);
			message.setMessage("操作成功");
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
	public @ResponseBody JsonMessage number(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			String number = this.spBaseInfoManager.generateNumber(prefix);
			message.setMessage(number);
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
	public @ResponseBody JsonMessage remove(@RequestParam Long spId) {
		JsonMessage message = new JsonMessage();
		try {
			spBaseInfoManager.remove(spId);
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
	public @ResponseBody JsonMessage cancelApply(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			spBaseInfoManager.cancelApply(id);
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
	public @ResponseBody JsonMessage cancelApplyForAdmin(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			boolean bln = false;
			SpBaseInfo sp = spBaseInfoManager.load(id);
			
			bln = spBaseInfoManager.cancelApply(sp);
			if(bln) {
				message.setSuccess(Boolean.TRUE);
				message.setMessage("撤销成功");
			} else {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("操作失败");
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
	public @ResponseBody JsonResult getAppListWithSp(HttpServletRequest request, @RequestParam Long spId) {
		JsonResult result = new JsonResult();
		try {
			SpBaseInfo sp = spBaseInfoManager.load(spId);
			Page<Application> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			filters.add(new PropertyFilter("sp", JoinType.I, "id", MatchType.EQ, PropertyType.L, sp.getId().toString()));
			filters.add(new PropertyFilter("status", MatchType.EQ, PropertyType.I, Application.STATUS_PUBLISHED+""));
			page = applicationManager.findPage(page, filters);
			List<Application> applicationList = page.getResult();
			List<Map<String, Object>> mappedApplications = applicationResult(applicationList);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedApplications);
			result.setPage(pageMap);
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
	
	private List<Map<String, Object>> applicationResult(List<Application> applications) {
		List<Map<String, Object>> mappedApplications = new ArrayList<Map<String, Object>>(applications.size());
		for (Application application : applications) {
			Map<String, Object> mappedApplication = application.toMap(null, null);
			if (application.getPcIcon() != null) {
				mappedApplication.put("hasIcon", true);
			} else {
				mappedApplication.put("hasIcon", false);
			}
			mappedApplications.add(mappedApplication);
		}
		return mappedApplications;
	}
	
	@RequestMapping
	public @ResponseBody JsonResult getSpRequistionList(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {

			Page<Requistion> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);

			page = requistionManager.findPage(page, filters);
			
			result.setPage(page, null, null);
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
	public @ResponseBody JsonMessage getSp(@RequestParam Long spId) {
		JsonMessage message = new JsonMessage();
		
		try {
			SpBaseInfo sp = this.spBaseInfoManager.load(spId);
			
			Map<String, Object> map = sp.toMap("firmNature firmScale", "firmLogo", "sysUser.email");

			map.put("hasLogo", ArrayUtils.isNotEmpty(sp.getFirmLogo()));
			map.put("email", map.get("sysUser_email"));
			map.put("password", sp.getSysUser().getPassword());

			message.setMessage(map);
			
			if(logger.isDebugEnabled()) {
				if(!map.isEmpty()) {
					Set<String> keySet = map.keySet();
					for(String key : keySet) {
						logger.debug("\n" + key + ":" + map.get(key));
					}
				}
				
				if(sp.getApplications() != null && !sp.getApplications().isEmpty()) {
					for(Application app : sp.getApplications()) {
						System.out.println(app.getId());
					}
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
	public @ResponseBody JsonMessage getSpApply(@RequestParam Long spId) {
		JsonMessage message = new JsonMessage();
		
		try {
			SpBaseInfoApply sp = this.spBaseInfoApplyManager.get(spId);
			Map<String, Object> map = sp.toMap("firmNature firmScale", "firmLogo", null);

			map.put("hasLogo",sp.getFirmLogo() != null);

			message.setMessage(map);
			
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
	public @ResponseBody JsonMessage add(HttpServletRequest request, HttpServletResponse response) {
		return save(request, response, true);
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage edit(HttpServletRequest request, HttpServletResponse response) {
		return save(request, response, false);
	}
	
	/**
	 * SP新增或修改，管理后台调用
	 * @param request
	 * @param response
	 * @param isNew
	 * @return
	 */
	@RequestMapping
	public @ResponseBody JsonMessage save(HttpServletRequest request, HttpServletResponse response, boolean isNew) {
		JsonMessage message = new JsonMessage();
		SpBaseInfo source = new SpBaseInfo();
		SpBaseInfo target = null;
		try {
			
			String email = ServletRequestUtils.getStringParameter(request, "email");
			BindingResult result = SpringMVCUtils.bindObject(request, source);
			String province = source.getLocationNo();
			if(StringUtils.isBlank(province)) province = "全网";
			if(isNew) {
				target = new SpBaseInfo();
			} else {
				target = this.spBaseInfoManager.load(ServletRequestUtils.getLongParameter(request, "id"));
			}
			target.setHasLock(SpBaseInfo.LOCK);
			
			
			if(result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				String logoPath = ServletRequestUtils.getStringParameter(request, "logoPath");
				
				if(isNew) {
					BeanUtils.copyProperties(source, target);
					
					SysUser sysUser = createNewSysUserForSpBaseInfo(request, target, SysUser.USER_STATUS.ENABLED.getValue());
					sysUser.setPassword(SpBaseInfo.DEFAULT_PASSWORD);
					sysUser.setProvince(province);
					
					//创建SP
					target.setSysUser(sysUser);
					target.setStatus(SpBaseInfo.STATUS_AVALIABLE);
					if(!StringUtils.isBlank(logoPath)) {
						byte[] logo = getFile(request, "logoPath");
						target.setFirmLogo(logo);
					}
					target.setStatus(SpBaseInfo.STATUS_INIT);
					
					Boolean bln = spBaseInfoManager.register(target);
					message.setSuccess(bln);
				} else {
					//修改
					SpBaseInfoApply apply = new SpBaseInfoApply();
					SpringMVCUtils.bindObject(request, apply);
					if(!StringUtils.isBlank(logoPath)) {
						byte[] logo = getFile(request, "logoPath");
						apply.setFirmLogo(logo);
					}
					apply.setEmail(email);
					boolean validate = validateSpModifyForm(apply, target);
					
					if(validate) {
						message.setSuccess(Boolean.FALSE);
						message.setMessage("请不要提交未修改过的表单数据");
					} else {
						message = modify(target, apply);
						message.setMessage("操作成功");
					}
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
	
	public boolean validateSpModifyForm(SpBaseInfoApply apply, SpBaseInfo target) {
		boolean validate = true;

		validate &= apply.getEmail().equals(target.getSysUser().getEmail());
		validate &= apply.getAddress().equals(target.getAddress());
		validate &= apply.getCertificateNo().equals(target.getCertificateNo());
		validate &= apply.getContactPersonMobileNo().equals(target.getContactPersonMobileNo());
		validate &= apply.getContactPersonName().equals(target.getContactPersonName());
		validate &= apply.getFirmNature().equals(target.getFirmNature());
		validate &= apply.getFirmScale().equals(target.getFirmScale());
		validate &= apply.getLegalPersonIdNo().equals(target.getLegalPersonIdNo());
		validate &= apply.getLegalPersonIdType().equals(target.getLegalPersonIdType());
		validate &= apply.getLegalPersonName().equals(target.getLegalPersonName());
		validate &= apply.getLocationNo().equals(target.getLocationNo());
		validate &= apply.getName().equals(target.getName());
		validate &= apply.getRegistrationNo().equals(target.getRegistrationNo());
		validate &= apply.getShortName().equals(target.getShortName());
		validate &= apply.getType().equals(target.getType());
		validate &= apply.getRid().equals(target.getRid());
		
		if(ArrayUtils.isEmpty(apply.getFirmLogo())) {
			validate &= true;
		} else if(ArrayUtils.isNotEmpty(apply.getFirmLogo())) {
			if(ArrayUtils.isNotEmpty(target.getFirmLogo())) {
				validate &= apply.getFirmLogo().equals(target.getFirmLogo());
			} else {
				validate &= false;
			}
		}
		
		return validate;
	}
	
	@RequestMapping
	public @ResponseBody JsonResult indexForWebSite(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SpBaseInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Map<String, String> params = new HashMap<String, String>();
			if(SpringSecurityUtils.getCurrentUser() != null){
				String province = ((UserWithSalt)SpringSecurityUtils.getCurrentUser()).getProvince();
				logger.info("Province : "+province);
				if(!StringUtils.isBlank(province)) {
					filters.add(new PropertyFilter("locationNo", MatchType.EQ, PropertyType.S, province));
					params.put("locationNo", province);
				}
			}
			//page = spBaseInfoManager.findPage(page, filters);
			page = spBaseInfoManager.getList(page, params);
			List<SpBaseInfo> list = page.getResult();
			List<SpBaseInfo> _list = new ArrayList<SpBaseInfo>();
			for(SpBaseInfo sp : list) {
				sp.setApplicationSize();
				sp.setAvailableApplicationSize();
				sp.setHasLogo(ArrayUtils.isNotEmpty(sp.getFirmLogo()));
				_list.add(sp);
				
				logger.debug("\n" + sp.getName() + " : " + sp.getApplicationSize());
			}
			page.setResult(_list);
			result.setPage(page, "firmLogo", "sysUser.email");
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
	public @ResponseBody JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SpBaseInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Map<String, String> params = new HashMap<String, String>();
			if(SpringSecurityUtils.getCurrentUser() != null){
				String province = ((UserWithSalt)SpringSecurityUtils.getCurrentUser()).getProvince();
				logger.info("Province : "+province);
				if(!StringUtils.isBlank(province)) {
					filters.add(new PropertyFilter("locationNo", MatchType.EQ, PropertyType.S, province));
					params.put("locationNo", province);
				}
			}
			page = spBaseInfoManager.findPage(page, filters);
			//page = spBaseInfoManager.getList(page, params);
			List<SpBaseInfo> list = page.getResult();
			List<SpBaseInfo> _list = new ArrayList<SpBaseInfo>();
			for(SpBaseInfo sp : list) {
				sp.setApplicationSize();
				sp.setAvailableApplicationSize();
				sp.setHasLogo(ArrayUtils.isNotEmpty(sp.getFirmLogo()));
				_list.add(sp);
				
				logger.debug("\n" + sp.getName() + " : " + sp.getApplicationSize());
			}
			page.setResult(_list);
			result.setPage(page, "firmLogo", "sysUser.email");
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
	
	/**
	 * SP下拉框
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody JsonMessage select(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		try {
			Integer status = ServletRequestUtils.getIntParameter(request, "status");
			
			String province = ((UserWithSalt)SpringSecurityUtils.getCurrentUser()).getProvince();
			
			String type = ServletRequestUtils.getStringParameter(request, "type");
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("status", status);
			parameters.put("province", province);
			parameters.put("type", type);
			
			Map<String, Object> map = spBaseInfoManager.getSpNameAndId(parameters);
			Set<String> keys = map.keySet();
			List<SpBaseInfo> list = new ArrayList<SpBaseInfo>();
			for(String key : keys) {
				SpBaseInfo sp = new SpBaseInfo();
				sp.setId(Long.valueOf(key));
				sp.setName((String)map.get(key));
				list.add(sp);
			}
			
			result.setMessage(list);
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
	
	/*
	 * ********************************************
	 * method name   : indexAudit 
	 * description   : 待审核的SP列表
	 * @return       : JsonResult
	 * @param        : @param request
	 * @param        : @return
	 * modified      : haojinghua ,  2011-6-10
	 * @see          : 
	 * *******************************************
	 */
	@RequestMapping
	public @ResponseBody JsonResult indexAudit(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SpBaseInfoApply> page = SpringMVCUtils.getPage(request);
			String orderBy = ServletRequestUtils.getStringParameter(request, "page_orderBy");
			if(StringUtils.isBlank(orderBy)) {
				orderBy = "requistion_submitDate_desc";
			}
			
			Map<String, Object> params = new HashMap<String, Object>();

			String province = ((UserWithSalt)SpringSecurityUtils.getCurrentUser()).getProvince();
			if(!StringUtils.isBlank(province)) {
				params.put("province", province);
			}
			
			params.put("name", ServletRequestUtils.getStringParameter(request, "search_LIKES_name"));
			page = spBaseInfoApplyManager.findPage(page, orderBy, params);
			String includeCascadeField = "requistion.status requistion.submitDate";
			result.setPage(page, "firmLogo", includeCascadeField);
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
	public @ResponseBody JsonMessage getCurrentSp() {
		JsonMessage message = new JsonMessage();
		
		try {
			
			SpBaseInfo sp = getSpBaseInfo();
			Requistion requistion = null;
			if(sp.getStatus().equals(SpBaseInfo.STATUS_INIT)) {
				requistion = requistionManager.getRequistionByTypeAndId(Requistion.TYPE_SP_REGISTER, sp.getId());
			} else {
				requistion = requistionManager.getRequistionByTypeAndId(Requistion.TYPE_SP_MODIFY, sp.getId());
			}
			Map<String, Object> map = sp.toMap("status type firmNature firmScale", null, "sysUser.email");
			map.put("avalidApp", sp.getAvailableApplicationSize());
			
			if(requistion != null) map.put("requistion", requistion);
			message.setMessage(map);
			
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
	public void downloadAttachment(HttpServletRequest request, HttpServletResponse response) throws Exception {
		OutputStream out = response.getOutputStream();
		//BufferedInputStream bis = null;
		try {
			Long id = ServletRequestUtils.getLongParameter(request, "id");
			SpBaseInfo sp = spBaseInfoManager.load(id);
			byte[] file = sp.getAttachment();
			if(ArrayUtils.isEmpty(file)) {
				throw new PlatformException(PlatformErrorCode.FILE_NOT_EXIST);
			} else {
				String fileName = sp.getAttachmentName();
				ServletUtils.setFileDownloadHeader(response, fileName);
				out.write(file);
				out.flush();
			}
		} catch(PlatformException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping
	public void loadSpFirmLogo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		byte[] image = null;
		SpBaseInfo sp = null;
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		if(id == null) {
			sp = getSpBaseInfo();
		} else {
			sp = this.spBaseInfoManager.load(id);
		}
		image = sp.getFirmLogo();
		
		if(ArrayUtils.isNotEmpty(image)) {
			logger.debug(String.valueOf(image.length));
			SpringMVCUtils.writeImage(image, response);
			logger.debug(image.toString());
		}
		
	}
	@RequestMapping
	public void loadSpApplyFirmLogo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		byte[] image = null;
		SpBaseInfoApply sp = null;
		
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		if(id == null) {
			sp = new SpBaseInfoApply();
		} else {
			sp = this.spBaseInfoApplyManager.load(id);
		}
		image = sp.getFirmLogo();
		
		if(ArrayUtils.isNotEmpty(image)) {
			logger.debug(String.valueOf(image.length));
			SpringMVCUtils.writeImage(image, response);
			logger.debug(image.toString());
		}
		
	}
	
	/**
	 * 根据应用提供商信息创建对应的默认系统用户<br/>
	 * @param request
	 * @param spBaseInfo
	 * @param sysUserStatus
	 * @return
	 * @throws Exception
	 */
	private SysUser createNewSysUserForSpBaseInfo(HttpServletRequest request, SpBaseInfo spBaseInfo, int sysUserStatus) throws Exception {
		SysUser sysUser = null;
		
		sysUser = new SysUser();
		sysUser.setEmail(ServletRequestUtils.getStringParameter(request, "email"));
		sysUser.setUserName(ServletRequestUtils.getStringParameter(request, "email"));
		sysUser.setPassword(ServletRequestUtils.getStringParameter(request, "password"));
		sysUser.setStatus(sysUserStatus);
		//sysUser.setRealName(spBaseInfo.getContactPersonName());
		//sysUser.setMobile(spBaseInfo.getContactPersonMobileNo());
		
		return sysUser;
	}
	
	private byte[] getFile(HttpServletRequest request, String path) throws Exception {
		byte[] firmLogo = new byte[0];
		String logoPath = ServletRequestUtils.getStringParameter(request, path);
		if(!StringUtils.isBlank(logoPath)) {
			logoPath = request.getSession().getServletContext().getRealPath("/") + logoPath;
			firmLogo = ConvertUtils.file2ByteArray(logoPath);
		}
		return firmLogo;
	}
	
	/**
	 * 应用提供商注册
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public @ResponseBody JsonMessage spRegister(HttpServletRequest request, HttpServletResponse response) {
		JsonMessage message = new JsonMessage();
		try {
			SpBaseInfo spBaseInfo = new SpBaseInfo();
			
			BindingResult bindingResult = SpringMVCUtils.bindObject(request, spBaseInfo);
			if(bindingResult.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(bindingResult);
			} else {
				//企业Logo文件路径
				String logoPath = ServletRequestUtils.getStringParameter(request, "logoPath");
				if(!StringUtils.isBlank(logoPath)) {
					byte[] firmLogo = getFile(request, "logoPath");
					spBaseInfo.setFirmLogo(firmLogo);
					logger.debug("\nFirm LOGO byte[] is not empty : " + ArrayUtils.isNotEmpty(spBaseInfo.getFirmLogo()) + "\n");
					
				}
				
				//创建User
				SysUser sysUser = createNewSysUserForSpBaseInfo(request, spBaseInfo, SysUser.USER_STATUS.ENABLED.getValue());
				sysUser.setProvince(spBaseInfo.getLocationNo());
				spBaseInfo.setSysUser(sysUser);
				//创建SP
				Boolean bln = spBaseInfoManager.register(spBaseInfo);
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
	 * 加载应用提供商信息，供修改用
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public @ResponseBody JsonMessage spLoad() throws Exception {
		JsonMessage msg = new JsonMessage();
		
		SpBaseInfo spBaseInfo = getSpBaseInfo();
		spBaseInfo.setHasLogo(ArrayUtils.isNotEmpty(spBaseInfo.getFirmLogo()));
		Map<String, Object> map = spBaseInfo.toMap("status type firmNature firmScale", "firmLogo", "sysUser.email");
		
		if(logger.isDebugEnabled()) {
			if(!map.isEmpty()) {
				Set<String> keySet = map.keySet();
				for(String key : keySet) {
					logger.debug(key + ":" + map.get(key));
				}
			}
		}
		
		msg.setMessage(map);
		logger.debug(spBaseInfo.toString());
		
		return msg;
	}
	
	/**
	 * 修改SP
	 * @param sp
	 * @param apply
	 * @return
	 */
	JsonMessage modify(SpBaseInfo sp, SpBaseInfoApply apply) {
		JsonMessage message = new JsonMessage();
		
		if(ArrayUtils.isEmpty(apply.getFirmLogo())) {
			apply.setFirmLogo(sp.getFirmLogo());
		}
		
		if(sp.getStatus().equals(SpBaseInfo.STATUS_INIT)) {
			
			boolean bln = spBaseInfoManager.modifyApply(apply, sp);
			message.setMessage(bln);
			
		} else {
			//SP可用状态下的修改
			Long originalId = sp.getId();
			Requistion _requistion = requistionManager.getRequistionByTypeAndId(Requistion.TYPE_SP_MODIFY, originalId);
			if(_requistion != null && _requistion.getStatus().equals(Requistion.STATUS_INIT)) {
				message.setMessage("当前记录正在审核中，审核员处理完毕后，方可再修改");
				message.setSuccess(Boolean.FALSE);
				return message;
			}
			
			apply.setId(null);
			apply.setNo(sp.getNo());
			apply.setInBlack(sp.getInBlack());
			apply.setApplyDate(Calendar.getInstance());
			apply.setApplyType(SpBaseInfoApply.APPLY_TYPE_MODIFY);
			
			Requistion requistion = RequistionFactory.getRequistion(Requistion.TYPE_SP_MODIFY);
			requistion.setOriginalId(sp.getId());
			
			apply.setRequistion(requistion);
			
			boolean bln = spBaseInfoManager.modifyApply(apply);
			message.setMessage(bln);
		}
		
		
		return message;
	}
	
	/**
	 * 修改SP信息，前台网站调用
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public @ResponseBody JsonMessage spModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonMessage message = new JsonMessage();
		
		try {
			SpBaseInfo sp = getSpBaseInfo();
			
			SpBaseInfoApply apply = new SpBaseInfoApply();
			SpringMVCUtils.bindObject(request, apply);
			String email = ServletRequestUtils.getStringParameter(request, "email");
			if(!StringUtils.isBlank(email)) apply.setEmail(email);
			
			String logoPath = ServletRequestUtils.getStringParameter(request, "logoPath");
			if(!StringUtils.isBlank(logoPath)) {
				byte[] firmLogo = getFile(request, "logoPath");
				apply.setFirmLogo(firmLogo);
				logger.debug("\nFirm LOGO byte[] is not empty : " + ArrayUtils.isNotEmpty(apply.getFirmLogo()) + "\n");
				
			}
			
			boolean validate = validateSpModifyForm(apply, sp);
			
			if(validate) {
				message.setSuccess(Boolean.FALSE);
				message.setMessage("请不要提交未修改过的表单数据");
			} else {
				message = modify(sp, apply);
				if(message.getSuccess()) message.setMessage("操作成功");
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
	 * 字段验证：企业名称、企业简称、企业邮箱、工商注册编号、经营许可证编号、法人证件号码
	 * @param name 
	 * @param type：full-企业全称；short-企业简称；email-企业邮箱
	 * @return JsonMessage
	 * @throws Exception
	 */
	@RequestMapping
	public @ResponseBody JsonMessage checkName(@RequestParam("name") String name, @RequestParam("type") String type) throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = false;
		if("full".equals(type)) {
			bln = spBaseInfoManager.validateSpFullName(name);
		} else if("short".equals(type)) {
			bln = spBaseInfoManager.validateSpShortName(name);
		} else if("email".equals(type)) {
			SpBaseInfo sp = spBaseInfoManager.getSpByNameOrMobileOrEmail(name);
			if(sp == null) bln = true;
		} else {
			logger.debug("type:"+type+",name:"+name);
			bln = spBaseInfoManager.validateSpProperty(type, name);
		}
		msg.setSuccess(bln);
		logger.debug(msg.toString());
		return msg;
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage validateField(@RequestParam String fieldName, @RequestParam String newValue, @RequestParam String orgValue) throws Exception {
		JsonMessage message = new JsonMessage();
		try {
			boolean bln = spBaseInfoManager.validateSpProperty(fieldName, newValue, orgValue);
			message.setMessage(bln);
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
	public @ResponseBody JsonMessage audit(HttpServletRequest request, @RequestParam Long id, @RequestParam String status) {
		JsonMessage message = new JsonMessage();
		
		try {
			
			boolean result = "yes".equalsIgnoreCase(status) ? true : false;
			String opinion = ServletRequestUtils.getStringParameter(request, "opinion");
			
			SpBaseInfoApply apply = spBaseInfoApplyManager.get(id);
			Requistion requistion = apply.getRequistion();
			requistion.setReviewDate(Calendar.getInstance());
			
			if(result) {
				requistion.setOpinion(Requistion.OPINION_DEFAULT_AGREE);
				requistion.setStatus(Requistion.STATUS_PASS);
				requistion.setResult(Requistion.RESULT_PASS);
			} else {
				requistion.setOpinion(opinion);
				requistion.setStatus(Requistion.STATUS_REJECT);
				requistion.setResult(Requistion.RESULT_REJECT);
			}
			
			apply.setRequistion(requistion);
			
			int type = apply.getRequistion().getType();
			if(type == Requistion.TYPE_SP_REGISTER) {
				
				//upload
				//企业Logo文件路径
				String filePath = ServletRequestUtils.getStringParameter(request, "filePath");
				String fileName = ServletRequestUtils.getStringParameter(request, "fileName");
				if(!StringUtils.isBlank(filePath)) {
					byte[] attachment = getFile(request, "filePath");
					apply.setAttachment(attachment);
					apply.setAttachmentName(fileName);
					logger.debug("\nAttachment byte[] is not empty : " + ArrayUtils.isNotEmpty(apply.getAttachment()) + "\n");
					
				}
				
				//注册申请
				spBaseInfoManager.handleRegisterApply(apply, result);
			} else if(type == Requistion.TYPE_SP_MODIFY) {
				//修改申请
				spBaseInfoManager.handleModifyApply(apply, result);
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
	
	/**审核SP注册
	 * @param spId：SPID；auditResult-审核结果；reason-不通过原因
	 * @return JsonMessage
	 * @throws Exception
	 */
	@RequestMapping
	public @ResponseBody JsonMessage spAudit(@RequestParam("spId") Long spId,@RequestParam("auditResult") Integer auditResult ,@RequestParam("opinion") String opinion) throws Exception {
		JsonMessage msg = new JsonMessage();
		//auditResult审核结果，1为通过，0为不通过
	    boolean bln = true;
		Requistion re = requistionManager.getRequistionByTypeAndId(Requistion.TYPE_SP_REGISTER,spId);
		if(auditResult == 1){
		SpBaseInfo sp = spBaseInfoManager.load(spId);
		SysUser user = sysUserManager.load(spId);
		user.setStatus(1);
	    sp.setStatus(1);
	    sysUserManager.saveOrUpdate(user);
	    spBaseInfoManager.saveOrUpdate(sp);  
	    re.setReviewDate(Calendar.getInstance());
	    re.setStatus(Requistion.STATUS_PASS);
	    re.setResult(Requistion.RESULT_PASS);
		}else if(auditResult == 0){
			re.setReviewDate(Calendar.getInstance());
			re.setStatus(Requistion.STATUS_REJECT);
			re.setResult(Requistion.RESULT_REJECT);
			if(opinion.equals("")){
				re.setOpinion(Requistion.OPINION_DEFAULT_AGREE);
			}
			else{
				re.setOpinion(opinion);
			}
		}
	    requistionManager.saveOrUpdate(re);
	    msg.setSuccess(bln);
		return msg;
	}
	
	
	@RequestMapping
	public @ResponseBody JsonResult getAllForTestPage(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SpBaseInfo> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			
			page = spBaseInfoManager.findPage(page, filters);
			
			result.setPage(page, "firmLogo", "sysUser.email");
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
	
	/**
	 * 推荐供应商
	 */			
	@RequestMapping
	public @ResponseBody
	JsonResult recommendSp(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<RecommendSp> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
//			boolean local = ServletRequestUtils.getBooleanParameter(request, "local",false); // 是否本地应用
//			if (local){
//				SysUser currentUser = userManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
//				if (currentUser != null && !currentUser.getSysRole().getRoleName().equals(SpecialRoleType.SUPER_OPERATOR.toString())){
//					PropertyFilter pf = new PropertyFilter("sp", JoinType.L, "locationNo", MatchType.EQ, PropertyType.S, currentUser.getProvince());
//					filters.add(pf);
//				}
//			}
			page = recommendSpManager.findPage(page, filters);
			List<RecommendSp> recommendSpList = page.getResult();
			List<Map<String, Object>> mappedApplications = recommendSpList(recommendSpList);
			Page<Map<String, Object>> pageMap = page.getMappedPage();
			pageMap.setResult(mappedApplications);
			result.setPage(pageMap);
			StringBuffer orderNos = new StringBuffer(""); 
			for (RecommendSp ra : recommendSpList){
				orderNos.append(ra.getOrderNo()+":"+ra.getSp().getLocationNo() + ",");
			}
			String orderNoStr = "";
			if (!orderNos.toString().equals("")){
				orderNoStr = orderNos.toString().substring(0, orderNos.toString().length()-1);
			}
			result.setMessage(orderNoStr);
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
	
	private List<Map<String, Object>> recommendSpList(List<RecommendSp> recommendSpList) {
		List<Map<String, Object>> mappedSps = new ArrayList<Map<String, Object>>(recommendSpList.size());
		for (RecommendSp rs : recommendSpList) {
			Map<String, Object> mappedSp = rs.toMap(null, "sp.name sp.id sp.locationNo");
			mappedSp.put("availableApplicationSize",rs.getSp().getAvailableApplicationSize());
			mappedSp.put("hasLogo",		ArrayUtils.isNotEmpty(rs.getSp().getFirmLogo()));
			mappedSps.add(mappedSp);
		}
		return mappedSps;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage saveRecommend(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		try {
			RecommendSp rs = new RecommendSp();
			BindingResult result = SpringMVCUtils.bindObject(request, rs);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				recommendSpManager.saveOrUpdate(rs);
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
	 * 删除推荐供应商
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage removeRecommend(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			recommendSpManager.remove(id);
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
	 * 高级搜索
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult advanceSearch(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		Page<SpBaseInfo> page = SpringMVCUtils.getPage(request);
		try {
			String name = ServletRequestUtils.getStringParameter(request, "name");
			Map<String, String> paramMap = new HashMap<String, String>();
			if (!StringUtils.isEmpty(name)){
				paramMap.put("name", name.trim());
			}
			page = spBaseInfoManager.advanceSearch(page, paramMap);
			List<SpBaseInfo> list = page.getResult();
			List<SpBaseInfo> _list = new ArrayList<SpBaseInfo>();
			for(SpBaseInfo sp : list) {
				sp.setApplicationSize();
				sp.setAvailableApplicationSize();
				sp.setHasLogo(ArrayUtils.isNotEmpty(sp.getFirmLogo()));
				_list.add(sp);
				
				logger.debug("\n" + sp.getName() + " : " + sp.getApplicationSize());
			}
			page.setResult(_list);
			result.setPage(page, "firmLogo", "sysUser.email");
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
	
	/**
	 * SP推荐列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult recommendSpList(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<SpBaseInfo> page = SpringMVCUtils.getPage(request);
//			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			page.setPageSize(999);
			page = spBaseInfoManager.recommendSpList(page);
			List<SpBaseInfo> list = page.getResult();
			List<SpBaseInfo> _list = new ArrayList<SpBaseInfo>();
			for(SpBaseInfo sp : list) {
				sp.setApplicationSize();
				sp.setHasLogo(ArrayUtils.isNotEmpty(sp.getFirmLogo()));
				_list.add(sp);
				
				logger.debug("\n" + sp.getName() + " : " + sp.getApplicationSize());
			}
			page.setResult(_list);
			result.setPage(page, "firmLogo", "sysUser.email");
			JsonResult result2 = this.recommendSp(request);
			result.setMessage(result2.getMessage());
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
	public @ResponseBody JsonMessage editSpSummary(HttpServletRequest request) {
		JsonMessage message = new JsonMessage();
		
		try {
			Long spId = ServletRequestUtils.getLongParameter(request, "spId");
			String spSummary = ServletRequestUtils.getStringParameter(request, "spSummary");
			
			SpBaseInfo sp = this.spBaseInfoManager.load(spId);
			sp.setSpSummary(spSummary);
			this.spBaseInfoManager.saveOrUpdate(sp);
			
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
}