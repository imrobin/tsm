package com.justinmobile.tsm.customer.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
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
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.tsm.customer.domain.MobileType;
import com.justinmobile.tsm.customer.manager.MobileTypeManager;
import com.justinmobile.tsm.system.domain.SystemParams;
import com.justinmobile.tsm.system.manager.SystemParamsManager;

@Controller("mobileTypeController")
@RequestMapping("/mobile/")
public class MobileTypeController {
	@Autowired
	private MobileTypeManager mobileTypeManager;

	@Autowired
	private SystemParamsManager systemParamsManager;

	private static final Logger logger = LoggerFactory
			.getLogger(MobileTypeController.class);

	/**
	 * @Title: getMobileBrand
	 * @Description: 获取手机品牌
	 * @param request
	 * @param response
	 */

	@RequestMapping
	public @ResponseBody
	JsonMessage getMobileBrand(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = true;
		List<String> brand = null;
		try {
			brand = mobileTypeManager.getAllBrand();
		} catch (Exception ex) {
			ex.printStackTrace();
			bln = false;
		}
		msg.setMessage(brand);
		msg.setSuccess(bln);
		return msg;
	}

	/**
	 * @Title: getParamByType
	 * @Description: 根据手机类型获取参数
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getParamsByType(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String type = ServletRequestUtils.getStringParameter(request, "type");
		JsonMessage message = new JsonMessage();
		try {
			List<SystemParams> sp = systemParamsManager.getParamsByType(type);
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			if (CollectionUtils.isNotEmpty(sp)) {
				for (SystemParams s : sp) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("key", s.getKey());
					map.put("value", s.getValue());
					result.add(map);
				}
			}
			message.setMessage(result);
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
	 * @Title: getAllBrand
	 * @Description: 获取所有的手机品牌
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getAllBrand() {
		JsonMessage message = new JsonMessage();
		try {
			List<String> types = mobileTypeManager.getAllBrand();
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			if (CollectionUtils.isNotEmpty(types)) {
				for (String type : types) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("key", type);
					map.put("value", type);
					result.add(map);
				}
			}
			message.setMessage(result);
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
	 * @Title: index
	 * @Description: 显示所有的手机型号列表
	 * @param request
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<MobileType> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils
					.getParameters(request);
			page = mobileTypeManager.findPage(page, filters);
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

	/**
	 * @Title: getMobileByKeywordForIndex
	 * @Description: 根据关键字获取手机类型
	 * @param requese
	 */
	@RequestMapping
	public @ResponseBody
	JsonResult getMobileByKeywordForIndex(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Map<String, Object> values = new HashMap<String, Object>();
			Page<MobileType> page = SpringMVCUtils.getPage(request);
			String brandChs = ServletRequestUtils.getStringParameter(request,
					"search_LIKES_brandChs", "");
			String type = ServletRequestUtils.getStringParameter(request,
					"search_LIKES_type", "");
			values.put("brandChs", brandChs);
			values.put("type", type);
			page = mobileTypeManager.getMobileByKeywordForIndex(page, values);
			result.setPage(page, null, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setMessage(ex.getMessage());
			result.setSuccess(Boolean.FALSE);
		}
		return result;
	}

	/**
	 * @Title: getTypeByBrand
	 * @Description: 根据手机品牌获取手机类型
	 * @param request
	 * @param response
	 */

	@RequestMapping
	public @ResponseBody
	JsonMessage getTypeByBrand(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = true;
		String brand = ServletRequestUtils.getStringParameter(request, "brand");
		List<String> types = null;
		try {
			types = mobileTypeManager.getTypeByBrand(brand);
		} catch (Exception ex) {
			ex.printStackTrace();
			bln = false;
		}
		msg.setMessage(types);
		msg.setSuccess(bln);
		return msg;
	}

	/**
	 * @Title: getMobileByBrand
	 * @Description: 查出该品牌的手机
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	Object getMobileByBrand(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Page<MobileType> page = SpringMVCUtils.getPage(request);
		String brand = ServletRequestUtils.getStringParameter(request, "brand");
		//brand = new String(brand.getBytes("iso-8859-1"), "utf-8");
		
		try {
			page = mobileTypeManager.getMobileByBrand(page, brand);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*String context = request.getSession().getServletContext()
				.getContextPath();*/
		JsonMessage msg = genJsonMessage(page);
		return msg.getMessage();
	}

	/**
	 * @Title: getMobileByKeyword
	 * @Description: 根据用户输入的关键字查询包含此关键字的手机
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	Object getMobileByKeyword(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String keyword = new String(ServletRequestUtils.getStringParameter(
				request, "keyword")).trim()
				.toUpperCase();
		Page<MobileType> page = SpringMVCUtils.getPage(request);
		try {
			page = mobileTypeManager.getMobileByKeyword(page, keyword);

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		/*String context = request.getSession().getServletContext()
				.getContextPath();*/
		JsonMessage msg = genJsonMessage(page);
		return msg.getMessage();
	}

	/**
	 * @Title: getAllMobile
	 * @Description: 查询出所有的手机信息
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	Object getAllMobile(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Page<MobileType> page = SpringMVCUtils.getPage(request);
		try {
			page = mobileTypeManager.getAllMobile(page);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*String context = request.getSession().getServletContext()
				.getContextPath();*/
		JsonMessage msg = genJsonMessage(page);
		return msg.getMessage();
	}

	/**
	 * @Title: getMobileByBrandType
	 * @Description: 根据品牌和型号查询出该手机信息
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	Object getMobileByBrandAndType(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Page<MobileType> page = SpringMVCUtils.getPage(request);
		String brand = ServletRequestUtils.getStringParameter(request, "brand");
		//brand = new String(brand.getBytes("iso-8859-1"), "utf-8");
		String type = ServletRequestUtils.getStringParameter(request, "type");
		try {
			page = mobileTypeManager.getMobileByBrandAndType(page, brand, type);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		JsonMessage msg = genJsonMessage(page);
		return msg.getMessage();
	}

	/**
	 * @Title: checkMobileTypeByBrandAndType
	 * @Description: 根据品牌和型号查询该手机是否存在
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage checkMobileTypeByBrandAndType(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage message = new JsonMessage();

		Page<MobileType> page = SpringMVCUtils.getPage(request);
		String brand = ServletRequestUtils.getStringParameter(request, "brand")
				.trim();
		String type = ServletRequestUtils.getStringParameter(request, "type")
				.trim();
		try {
			page = mobileTypeManager.getMobileByBrandAndType(page, brand, type);
			if (page.getTotalCount() != 0) {
				message.setSuccess(false);
				message.setMessage(brand + "下该手机型号" + type + "已经存在");
			} else {
				message.setSuccess(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return message;
	}

	/**
	 * @Title: getMobilePic
	 * @Description: 获取手机图片
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public void getMobilePic(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long id = ServletRequestUtils.getLongParameter(request, "id");
		MobileType mt = mobileTypeManager.load(id);
		String context = request.getSession().getServletContext()
				.getRealPath("/");
		File iconSrc = new File(context + "/images/defTerim.jpg");
		byte[] b = mt.getIcon();
		if (b != null) {
			SpringMVCUtils.writeImage(mt.getIcon(), response);
		} else {
			SpringMVCUtils.writeImage(FileUtils.readFileToByteArray(iconSrc),
					response);
		}
	}

	/**
	 * @Title: ajaxSuggest
	 * @Description: 类似百度google的提示
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	Object ajaxSuggest(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String keyword = ServletRequestUtils.getStringParameter(request,
				"value");
		List<String> suggest = null;
		try {
			keyword = new String(keyword.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			suggest = mobileTypeManager.getSuggestByKeyword(keyword);
		} catch (Exception ex) {
			//
		}
		JsonMessage msg = new JsonMessage();
		msg.setMessage(suggest);
		System.out.println(msg.getMessage());
		return msg.getMessage();
	}

	/**
	 * @Title: add
	 * @Description: 增加手机类型
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage add(HttpServletRequest request) {
		return save(request, true);
	}

	/**
	 * @Title: edit
	 * @Description: 修改手机类型
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage edit(HttpServletRequest request) {
		return save(request, false);
	}

	/**
	 * @Title: save
	 * @Description: 保存手机类型
	 * @param request
	 * @param response
	 */
	private JsonMessage save(HttpServletRequest request, boolean isNew) {
		JsonMessage message = new JsonMessage();
		MobileType mb = null;
		try {
			if (isNew) {
				mb = new MobileType();
			} else {
				mb = mobileTypeManager.load(ServletRequestUtils
						.getLongParameter(request, "id"));
			}
			BindingResult result = SpringMVCUtils.bindObject(request, mb);
			String mobileIconAbs = ServletRequestUtils.getStringParameter(
					request, "logoPath");
			logger.info(mobileIconAbs);
			if (!StringUtils.isBlank(mobileIconAbs)) {
				File f = new File(request.getSession().getServletContext()
						.getRealPath("/")
						+ mobileIconAbs);
				byte[] b = ConvertUtils.file2ByteArray(f);
				mb.setIcon(b);
			}
			if (result.hasErrors()) {
				message = SpringMVCUtils.buildErrorMessage(result);
			} else {
				mobileTypeManager.saveOrUpdate(mb);
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
	 * @Title: remove
	 * @Description: 删除手机类型
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage remove(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			mobileTypeManager.remove(id);
		} catch (PlatformException e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			message.setSuccess(Boolean.FALSE);
			message.setMessage("该型号已被终端使用，无法删除");
		}
		return message;
	}

	/**
	 * @Title: getMobileType
	 * @Description: 获取手机类型
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getMobileType(@RequestParam Long id) {
		JsonMessage message = new JsonMessage();
		try {
			message.setMessage(mobileTypeManager.load(id).toMap(null, null));
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
	 * @Title: genJsonMessage
	 * @Description: 生成JsonMessage
	 * @param page
	 * @param context
	 */
	private JsonMessage genJsonMessage(Page<MobileType> page) {
		JsonMessage msg = new JsonMessage();
		Map<String, Object> message = new HashMap<String, Object>(2);
		message.put("totalCount", page.getTotalCount());
		/* StringBuffer items = new StringBuffer();
		for (MobileType mt : page.getResult()) {
			String iconSrc = "";
			if (mt.getIcon() == null || mt.getIcon().equals("")) {
				iconSrc = context + "/images/defTerim.jpg";
			} else {
				iconSrc = context + "/html/mobile/?m=getMobilePic&id="
						+ mt.getId();
			}
			items.append("<dd><img style='width: 90px;margin-left :5px;height:90px;' src='"
					+ iconSrc
					+ "'/>"
					+ "<p style='width: 100px; word-wrap: break-word; overflow: hidden;'>"
					+ mt.getBrandChs() + " " + mt.getType() + "</p></dd>");
		}*/
		message.put("result", page.getResult());
		msg.setMessage(message);
		return msg;
	}

	/**
	 * @Title: getTypeAndValueByBrand
	 * @Description: 根据品牌查出所有的手机型号及其ID
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getTypeAndValueByBrand(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage msg = new JsonMessage();
		String brand = ServletRequestUtils.getStringParameter(request, "brand");
		try {
			List<MobileType> types = mobileTypeManager
					.getTypeAndValueByBrand(brand);
			List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
			for (MobileType type : types) {
				resultMap.add(type.toMap("icon", null));
			}
			msg.setMessage(resultMap);
		} catch (Exception ex) {
			ex.printStackTrace();
			msg.setMessage(ex.getMessage());
			msg.setSuccess(Boolean.FALSE);
			;
		}
		return msg;
	}

	/**
	 * @Title: getIdByBrandType
	 * @Description: 根据品牌和型号查询出该手机ID
	 * @param request
	 * @param response
	 */

	@RequestMapping
	public @ResponseBody
	JsonMessage getIdByBrandAndType(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage msg = new JsonMessage();
		Page<MobileType> page = SpringMVCUtils.getPage(request);
		String brand = ServletRequestUtils.getStringParameter(request, "brand")
				.trim();
		//brand = new String(brand.getBytes("iso-8859-1"), "utf-8");
		String type = ServletRequestUtils.getStringParameter(request, "type")
				.trim();
		try {
			page = mobileTypeManager.getMobileByBrandAndType(page, brand, type);
			msg.setMessage(page.getResult().get(0).getId());
		} catch (Exception ex) {
			ex.printStackTrace();
			msg.setMessage(ex.getMessage());
			msg.setSuccess(Boolean.FALSE);
			;
		}
		return msg;
	}

}
