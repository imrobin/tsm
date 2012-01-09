package com.justinmobile.tsm.application.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.application.domain.ApplicationType;
import com.justinmobile.tsm.application.domain.ApplicationType.AppTypeLevel;
import com.justinmobile.tsm.application.manager.ApplicationTypeManager;

@Controller("pplicationTypeController")
@RequestMapping("/applicationType/")
public class ApplicationTypeController {

	@Autowired
	private ApplicationTypeManager applicationTypeManger;

	@RequestMapping
	public @ResponseBody JsonResult getByCriteria(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			List<PropertyFilter> filters = SpringMVCUtils.getParameters(request);
			Page<ApplicationType> page = SpringMVCUtils.getPage(request);

			page = applicationTypeManger.findPage(page, filters);
			result.setPage(page, null, "parentType.id parentType.name");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	@RequestMapping
	public @ResponseBody JsonResult getAllTopLevel(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<ApplicationType> appTypePage = new Page<ApplicationType>();
			List<ApplicationType> appTypeList = applicationTypeManger.getAllTopLevel();
			appTypePage.setResult(appTypeList);
			appTypePage.setTotalCount(appTypeList.size());
			result.setPage(appTypePage, null, "parentType.id parentType.name");
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}

	/**
	 * 获取子节点
	 */
	@RequestMapping
	public @ResponseBody JsonResult getChild(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Long parentId = ServletRequestUtils.getLongParameter(request, "parentId");
			Page<ApplicationType> page = SpringMVCUtils.getPage(request);
			page = applicationTypeManger.getChild(page, parentId);
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
	public @ResponseBody JsonMessage getTypeById(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			ApplicationType type = applicationTypeManger.load(id);

			StringBuffer sb = new StringBuffer(type.getName());

			ApplicationType parentType = type.getParentType();
			while (null != parentType) {
				StringBuffer tempSb = new StringBuffer(parentType.getName());
				tempSb.append(" >> ").append(sb);

				sb = tempSb;
				parentType = parentType.getParentType();
			}
			message.setMessage(sb.toString());
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
	public @ResponseBody JsonMessage getType(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			ApplicationType type = applicationTypeManger.load(id);
			Map<String, Object> map = type.toMap(null, "parentType.id");
			if(null == type.getTypeLogo()){
				map.put("hasLogo", false);
			}else{
				map.put("hasLogo", true);
			}
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
	public @ResponseBody JsonMessage add(HttpServletRequest request) {
		return save(request, true);
	}

	@RequestMapping
	public @ResponseBody JsonMessage update(HttpServletRequest request) {
		return save(request, false);
	}

	private JsonMessage save(HttpServletRequest request, boolean isNew) {
		JsonMessage message = new JsonMessage();
		ApplicationType type = null;
		try {
			if (isNew) {
				type = new ApplicationType();
				type.setShowIndex(ApplicationType.NO_SHOW);
			} else {
				type = applicationTypeManger.load(ServletRequestUtils.getLongParameter(request, "id"));
				if (AppTypeLevel.ONE_LEVEL.getType() == type.getTypeLevel()) {
					Integer paramType = ServletRequestUtils.getIntParameter(request, "typeLevel");
					if (AppTypeLevel.TWO_LEVEL.getType() == paramType) {
						if(type.getShowIndex() != null && type.getShowIndex().intValue() == ApplicationType.SHOW.intValue()) {
							throw new PlatformException(PlatformErrorCode.APPLICAION_TYPE_IS_SHOW_INDEX);
						}
						if (CollectionUtils.isNotEmpty(type.getApplicationTypes())) {
							throw new PlatformException(PlatformErrorCode.APPLICAION_TYPE_BY_CHILD_USE);
						}
					}
				}
			}
			BindingResult result = SpringMVCUtils.bindObject(request, type);
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				String logoPath = ServletRequestUtils.getStringParameter(request, "logoPath");
				Long parentId = ServletRequestUtils.getLongParameter(request, "parentType_id");
				if (parentId != null && type.getTypeLevel() != 1) {
					ApplicationType parent = applicationTypeManger.load(parentId);
					type.setParentType(parent);
				} else {
					type.setParentType(null);
				}
				if(!StringUtils.isBlank(logoPath)) {
					byte[] logo = getFile(request, "logoPath");
					type.setTypeLogo(logo);
				}
				applicationTypeManger.checkName(type);
				applicationTypeManger.saveOrUpdate(type);
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
	
	private byte[] getFile(HttpServletRequest request, String path) throws Exception {
		byte[] firmLogo = new byte[0];
		String logoPath = ServletRequestUtils.getStringParameter(request, path);
		if(!StringUtils.isBlank(logoPath)) {
			logoPath = request.getSession().getServletContext().getRealPath("/") + logoPath;
			firmLogo = ConvertUtils.file2ByteArray(logoPath);
		}
		return firmLogo;
	}
	
	@RequestMapping
	public void loadTypeLogo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		byte[] image = null;
		ApplicationType appType = null;
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		appType = this.applicationTypeManger.load(id);
		image = appType.getTypeLogo();
		if(ArrayUtils.isNotEmpty(image)) {
			SpringMVCUtils.writeImage(image, response);
		}
	}
	
	@RequestMapping
	public @ResponseBody JsonMessage remove(@RequestParam Long typeId) {
		JsonMessage message = new JsonMessage();
		try {
			ApplicationType appType = applicationTypeManger.load(typeId);
			if (CollectionUtils.isNotEmpty(appType.getApplications())) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_TYPE_BY_USE);
			}
			Set<ApplicationType> childTypes = appType.getApplicationTypes();
			if (CollectionUtils.isNotEmpty(childTypes)) {
				for (ApplicationType childType : childTypes) {
					if (CollectionUtils.isNotEmpty(childType.getApplications())) {
						throw new PlatformException(PlatformErrorCode.APPLICAION_TYPE_BY_USE);
					}
				}
			}
			if(appType.getShowIndex() !=null && appType.getShowIndex().intValue() == ApplicationType.SHOW.intValue()) {
				throw new PlatformException(PlatformErrorCode.APPLICAION_TYPE_IS_SHOW_INDEX);
			}
			applicationTypeManger.remove(appType);
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
	public @ResponseBody JsonMessage setShowIndex(HttpServletRequest requset) {
		JsonMessage message = new JsonMessage();
		try {
			String ids  = requset.getParameter("ids");
			String[] idArray = ids.split(",");
			if(idArray.length != 0) {
				applicationTypeManger.setShowIndex(idArray);
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
}
