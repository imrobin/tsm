package com.justinmobile.core.utils.web;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.encode.JsonBinder;

public class SpringMVCUtils {

	private static final String DEFAULT_PAGE_ORDER = "orderBy";
	private static final String DEFAULT_PAGE_SIZE = "pageSize";
	private static final String DEFAULT_CURRENT_PAGE_NO = "pageNo";
	private static final String DEFAULT_PAGE_START = "page_";
	private static final String DEFAULT_SEARCH_START = "search_";
	protected static final String[] DATEPATTERN = new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy年MM月dd日 HH时mm分ss秒",
			"yyyy年MM月dd日", "yyyyMMdd", "yyyyMMddHHmmss" };

	public static void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = null;
		String[] patterns = null;
		patterns = DATEPATTERN;

		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Calendar.class, new CustomCalendarEditor(true, patterns));
		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
		binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	public static BindingResult bindObject(HttpServletRequest request, Object javaBean) throws Exception {
		Assert.notNull(javaBean);

		// 自动将需要实例化的对象实例化
		Field[] fields = javaBean.getClass().getDeclaredFields();
		for (Field field : fields) {
			String buf = StringUtils.substringAfter(String.valueOf(field.getType().getSuperclass()), "class ");
			if (AbstractEntity.class.getName().equals(buf)) {
				Method m = javaBean.getClass().getDeclaredMethod("set" + StringUtils.capitalize(field.getName()), field.getType());
				m.invoke(javaBean, field.getType().newInstance());
			}
		}
		// 创建Binder
		ServletRequestDataBinder binder = new ServletRequestDataBinder(javaBean);
		// 不知道为什么Spring不会自动调用，只好在绑定之前再调用一次
		initBinder(binder);
		// 绑定
		binder.bind(request);
		return binder.getBindingResult();
	}

	public static JsonMessage buildErrorMessage(final BindingResult result) {
		JsonMessage message = new JsonMessage();
		List<ObjectError> errors = result.getAllErrors();
		String errorMessage = JsonBinder.buildNonNullBinder().toJson(errors);
		message.setSuccess(Boolean.FALSE);
		message.setMessage(errorMessage);
		return message;
	}

	public static List<PropertyFilter> getParameters(final HttpServletRequest request) {
		return getParameters(request, DEFAULT_SEARCH_START);
	}

	/**
	 * 从HttpRequest中创建PropertyFilter列表
	 * PropertyFilter命名规则为Filter属性前缀_比较类型属性类型_属性名.
	 * 
	 * eg. filter_EQS_name filter_LIKES_name_OR_email
	 */
	public static List<PropertyFilter> getParameters(final HttpServletRequest request, final String filterPrefix) {
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		Map<String, String> filterMap = ServletUtils.getParametersStartingWith(request, filterPrefix);
		if (MapUtils.isNotEmpty(filterMap)) {
			for (Map.Entry<String, String> entry : filterMap.entrySet()) {
				// 传进的值为空的不考虑
				if (StringUtils.isNotBlank(entry.getValue())) {
					String value = entry.getValue();
					//如果是通过GET提交上来的数据，过滤器是不会执行的，遇到中文就只好强行转成我们需要的UTF-8
					if (request.getMethod().equals(HttpMethod.GET.name())) {
						try {
							value = new String(value.getBytes("iso-8859-1"),"utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
//					if (StringUtils.indexOf(value, "%") != -1) {
//						value = StringUtils.replace(value, "%", "/%");
//					}
//					if (StringUtils.indexOf(value, "_") != -1) {
//						value = StringUtils.replace(value, "_", "/_");
//					}
//					if (StringUtils.indexOf(value, "?") != -1) {
//						value = StringUtils.replace(value, "?", "/?");
//					}
//					if (StringUtils.indexOf(value, "#") != -1) {
//						value = StringUtils.replace(value, "#", "/#");
//					}
					PropertyFilter filter = new PropertyFilter(entry.getKey(), value);
					filters.add(filter);
				}
			}
		}
		return filters;
	}

	/**
	 * 页面设置分页数列子：page_pageNo=1
	 * 页面设置分页大小列子：page_pageSize=10
	 * 页面设置排序列子：page_orderBy=salesVolume_desc,viewTimes_desc
	 *
	 */
	public static <T> Page<T> getPage(final HttpServletRequest request) {
		Map<String, String> pageMap = ServletUtils.getParametersStartingWith(request, DEFAULT_PAGE_START);
		Page<T> page = new Page<T>();
		if (MapUtils.isNotEmpty(pageMap)) {
			if (StringUtils.isNotBlank(pageMap.get(DEFAULT_PAGE_SIZE))) {
				int pageSize = Integer.parseInt(pageMap.get(DEFAULT_PAGE_SIZE));
				page.setPageSize(pageSize);
			}
			if (StringUtils.isNotBlank(pageMap.get(DEFAULT_CURRENT_PAGE_NO))) {
				int pageNo = Integer.parseInt(pageMap.get(DEFAULT_CURRENT_PAGE_NO));
				page.setPageNo(pageNo);
			}
			if (StringUtils.isNotBlank(pageMap.get(DEFAULT_PAGE_ORDER))) {
				String orderBy = pageMap.get(DEFAULT_PAGE_ORDER);
				if (orderBy.indexOf(",") != -1) {
					String[] orders = StringUtils.split(orderBy, ",");
					StringBuilder orderByName = new StringBuilder();
					StringBuilder orderByType = new StringBuilder();
					for (String str : orders) {
						String[] orderStr = StringUtils.split(str, "_");
						orderByName.append(orderStr[0]).append(",");
						orderByType.append(orderStr[1]).append(",");
					}
					if (orderByName.lastIndexOf(",") == orderByName.length() - 1) {
						orderByName.deleteCharAt(orderByName.length() - 1);
					}
					if (orderByType.lastIndexOf(",") == orderByType.length() - 1) {
						orderByType.deleteCharAt(orderByType.length() - 1);
					}
					page.setOrderBy(orderByName.toString());
					page.setOrder(orderByType.toString());
				} else {
					page.setOrderBy(StringUtils.substringBeforeLast(orderBy, "_"));
					page.setOrder(StringUtils.substringAfterLast(orderBy, "_"));
				}
			}
		}
		return page;
	}
	
	public static void writeImage(byte[] image, HttpServletResponse response) {
		try {
			response.reset();
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/jpeg");
			ServletOutputStream responseOutputStream = response.getOutputStream();
			responseOutputStream.write(image);
			responseOutputStream.flush();
			responseOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.PARAM_ERROR);
		}
	}
	
	//前面版本比后面大返回true
	public static boolean compareVersion(String versionNo, String versionNo2) {
		String[] ver1 = versionNo.split("\\."); 
		String[] ver2 = versionNo2.split("\\."); 
		
		if (Integer.parseInt(ver1[0]) > Integer.parseInt(ver2[0])){
			return true;
		}else if (Integer.parseInt(ver1[0]) == Integer.parseInt(ver2[0]) && Integer.parseInt(ver1[1]) > Integer.parseInt(ver2[1])){
			return true;
		}else if (Integer.parseInt(ver1[0]) == Integer.parseInt(ver2[0]) && Integer.parseInt(ver1[1]) 
				== Integer.parseInt(ver2[1]) && Integer.parseInt(ver1[2]) > Integer.parseInt(ver2[2])){
			return true;
		}

		return false;
	}
}
