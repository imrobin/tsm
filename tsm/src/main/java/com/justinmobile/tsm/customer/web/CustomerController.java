package com.justinmobile.tsm.customer.web;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.dao.support.PropertyFilter;
import com.justinmobile.core.email.VelocityMailSupport;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.message.PlatformMessage;
import com.justinmobile.core.utils.spring.SpringContextHolder;
import com.justinmobile.core.utils.web.JsonMessage;
import com.justinmobile.core.utils.web.JsonResult;
import com.justinmobile.core.utils.web.SpringMVCUtils;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysUserRetrievePassword;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.manager.SysUserRetrievePasswordManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.customer.domain.Customer;
import com.justinmobile.tsm.customer.manager.CustomerManager;
import com.justinmobile.tsm.endpoint.sms.SmsEndpoint;

@Controller("customerController")
@RequestMapping("/customer/")
public class CustomerController {
	private Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private SysUserManager sysUserManager;

	@Autowired
	private SysUserRetrievePasswordManager sysUserRetrievePasswordManager;

	@Autowired
	private SmsEndpoint smsEndpoint;

	/**
	 * @Title: customerReg
	 * @Description: 用户注册
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage customerReg(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage message = new JsonMessage();
		try {
			String mobileNo = request.getParameter("userMobile");
			if (StringUtils.isBlank(mobileNo)) {
				throw new PlatformException(PlatformErrorCode.PARAM_ERROR);
			}
			SysUser sysUser = buildSysUser(request, mobileNo);
			Customer c = buildCustomer(request);
			String activeCode = RandomStringUtils.random(4, false, true);
			String password = sysUser.getPassword();
			c.setActiveEmailCode(activeCode + "@" + password);
			c.setActiveSmsCode(activeCode + "@" + password);
			sysUserManager
					.addUser(sysUser, SpecialRoleType.CUSTOMER_NOT_ACTIVE);
			c.setSysUser(sysUserManager.getUserByName(sysUser.getUserName()));
			Calendar regDate = Calendar.getInstance();
			c.setRegDate(regDate);
			customerManager.addCustomer(c);
			String dns = ServletRequestUtils.getStringParameter(request, "dns")
					+ request.getSession().getServletContext().getContextPath();
			// 发送邮件
			sendActiveEmail(sysUser, password, activeCode, dns);
			// 发送激活短信
			smsEndpoint.sendMessage(mobileNo,
					PlatformMessage.SMS_ACTIVE_MESSAGE
							.getDefaultMessage(activeCode));
			message.setSuccess(Boolean.TRUE);
			Map<String, String> map = new HashMap<String, String>();
			map.put("email", sysUser.getEmail());
			map.put("mobile", sysUser.getMobile());
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
	public @ResponseBody
	JsonMessage sendActiveSms(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		try {
			String mobile = request.getParameter("mobile");
			SysUser user = sysUserManager.getUserByNameOrMobileOrEmail(mobile);
			Customer customer = customerManager.getCustomerByUserName(user
					.getUserName());
			String activeCode = buildActiveCode(customer);
			smsEndpoint.sendMessage(customer.getSysUser().getMobile(),
					PlatformMessage.SMS_ACTIVE_MESSAGE
							.getDefaultMessage(activeCode));
			result.setMessage(customer.getSysUser().getEmail());
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

	private String buildActiveCode(Customer customer) {
		String active = customer.getActiveSmsCode();
		String password = StringUtils.substringAfterLast(active, "@");
		String activeCode = RandomStringUtils.random(4, false, true);
		customer.setActiveSmsCode(activeCode + "@" + password);
		customer.setRegDate(Calendar.getInstance());
		customerManager.saveOrUpdate(customer);
		return activeCode;
	}

	private Customer buildCustomer(HttpServletRequest request)
			throws ServletRequestBindingException {
		Customer c = new Customer();
		// 设置用户详情
		Integer sex = ServletRequestUtils.getIntParameter(request, "sex");
		int year = ServletRequestUtils.getIntParameter(request, "year");
		int month = ServletRequestUtils.getIntParameter(request, "month");
		int day = ServletRequestUtils.getIntParameter(request, "day");
		Calendar birthDay = Calendar.getInstance();
		if (year != -1 && month != -1 && day != -1) {
			birthDay.set(year, month - 1, day, 0, 0, 0);
			c.setBirthday(birthDay);
		}
		String location = ServletRequestUtils.getStringParameter(request,
				"location");
		if (location != null) {
			c.setLocation(location);
		}
		if (sex != null) {
			c.setSex(sex);
		}
		c.setActive(Customer.ACTIVE_NO);
		return c;
	}

	private SysUser buildSysUser(HttpServletRequest request, String mobileNo)
			throws Exception {
		SysUser sysUser = new SysUser();
		SpringMVCUtils.bindObject(request, sysUser);
		sysUser.setUserName(mobileNo);
		sysUser.setStatus(SysUser.USER_STATUS.ENABLED.getValue());
		sysUser.setMobile(mobileNo);
		return sysUser;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage smsActive(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage message = new JsonMessage();
		try {
			String activeCode = request.getParameter("activeCode");
			String mobile = request.getParameter("mobile");
			SysUser user = sysUserManager.getUserByNameOrMobileOrEmail(mobile);
			Customer customer = customerManager.getCustomerByUserName(user
					.getUserName());
			String realActiveCode = StringUtils.substringBefore(
					customer.getActiveSmsCode(), "@");
			if (realActiveCode.equals(activeCode)) {// 验证码输入正确，跳转页面
				String password = StringUtils.substringAfter(
						customer.getActiveSmsCode(), "@");
				String contextPath = request.getSession().getServletContext()
						.getContextPath();
				message.setMessage(contextPath
						+ "/html/customer/?m=regVerify&activeCode="
						+ activeCode + "@" + password + "&uid=" + user.getId()
						+ "&active=mobile");
			} else {// 验证码输入错误，返回错误信息
				message.setSuccess(Boolean.FALSE);
				message.setMessage(PlatformErrorCode.CUSTOMER_ACTIVE_CODE_INVALID
						.getDefaultMessage());
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
	 * @Title: modifyCustomer
	 * @Description: 修改用户空间主页
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage modifyCustomer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = true;
		Customer c = new Customer();
		try {
			SpringMVCUtils.bindObject(request, c);
			String userName = SpringSecurityUtils.getCurrentUserName();
			Customer customer = customerManager.getCustomerByUserName(userName);
			customer.setAddress(c.getAddress());
			customer.setNickName(c.getNickName());
			customer.setZip(c.getZip());
			customer.setLocation(c.getLocation());
			customer.setSex(c.getSex());
			int year = ServletRequestUtils.getIntParameter(request, "year");
			int month = ServletRequestUtils.getIntParameter(request, "month");
			int day = ServletRequestUtils.getIntParameter(request, "day");
			Calendar birthDay = Calendar.getInstance();
			if (year != -1 && month != -1 && day != -1) {
				birthDay.set(year, month - 1, day, 0, 0, 0);
				customer.setBirthday(birthDay);
			}
			SysUser user = sysUserManager.getUserByName(userName);
			customer.setSysUser(user);
			customerManager.addCustomer(customer);
		} catch (PlatformException e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		}
		msg.setSuccess(bln);
		return msg;
	}

	/**
	 * @Title: regVerify
	 * @Description: 验证激活码
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public ModelAndView regVerify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView();
		try {
			String uid = ServletRequestUtils.getStringParameter(request, "uid");
			String activeCode = ServletRequestUtils.getStringParameter(request,
					"activeCode");
			String isLogin = ServletRequestUtils.getStringParameter(request,
					"isLogin");
			String active = ServletRequestUtils.getStringParameter(request,
					"active");
			// 判断用户是否登录后进行的提交
			if (null != isLogin && isLogin.equals("y")) {
				throw new PlatformException(
						PlatformErrorCode.CUSTOMER_ALREADY_LOGIN);
			}
			// 用户名和Email在数据库中是一致的
			List<Customer> customers = customerManager.get(Lists
					.newArrayList(Long.valueOf(uid)));
			if (CollectionUtils.isEmpty(customers)) {// 未找到用户
				result.addObject("message",
						PlatformErrorCode.CUSTOMER_NOT_REG.getDefaultMessage());
			} else {
				Customer c = customers.get(0);
				boolean activeTrue = false;
				if ("mobile".equals(active)) {// 激活方式
					activeTrue = c.getActiveSmsCode().equals(activeCode);
				} else if ("email".equals(active)) {
					activeTrue = c.getActiveEmailCode().equals(activeCode);
				}
				if (c.getActive().intValue() == Customer.ACTIVE_YES) {// 已激活用户
					result.addObject("message",
							PlatformErrorCode.CUSTOMER_ALREADY_ACTIVE
									.getDefaultMessage());
				} else {// 验证码不正确，或者超时
					if (!activeTrue
							|| (Calendar.getInstance().getTimeInMillis() - c
									.getRegDate().getTimeInMillis()) > 24 * 60 * 60 * 1000) {
						result.addObject("message",
								PlatformErrorCode.CUSTOMER_ACTIVE_INVALID
										.getDefaultMessage());
					} else {
						sysUserManager.updateUser(c.getSysUser(),
								SpecialRoleType.CUSTOMER);
						c.setActive(Customer.ACTIVE_YES);// 已激活
						customerManager.saveOrUpdate(c);
						result.addObject("message",
								PlatformErrorCode.CUSTOMER_ACTIVE_SUCCESS
										.getDefaultMessage());
					}
				}
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			result.addObject("message", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.addObject("message", e.getMessage());
		}
		result.setViewName("/home/customer/activeResult.jsp");
		return result;
	}
	
	/**
	 * @Title: findPwdRequest
	 * @Description: 找回密码请求
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage findPwdRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = true;
		String message = "";
		String validateCode = RandomStringUtils.random(6, false, true);
		logger.info("validateCode=="+validateCode);
		try {
			String type = ServletRequestUtils.getStringParameter(request,
					"type");
			if (type.equalsIgnoreCase("mobile")) {
				String mobile = ServletRequestUtils.getStringParameter(request,
						"mobile");
				SysUser user = sysUserManager.getUserByNameOrMobileOrEmail(mobile);
				if(null == user){
					bln = false;
					message = PlatformErrorCode.CUSTOMER_NOT_REG.getDefaultMessage();
				}else{
					buildRetrievePasword(mobile,validateCode);
					smsEndpoint.sendMessage(mobile,PlatformMessage.SMS_VALIDATE_CODE.getDefaultMessage(validateCode));
					message = mobile;
				}
				msg.setSuccess(bln);
				msg.setMessage(message);
			} else if (type.equalsIgnoreCase("email")) {
				String email = ServletRequestUtils.getStringParameter(request,
						"email");
				String dns = ServletRequestUtils.getStringParameter(request,
						"dns")
						+ request.getSession().getServletContext().getContextPath();
				SysUser user = sysUserManager.getUserByNameOrMobileOrEmail(email);
				if (null == user) {
					bln = false;
					message = PlatformErrorCode.CUSTOMER_NOT_REG.getDefaultMessage();
				} else {
					SysUserRetrievePassword userRP = buildRetrievePasword(email,validateCode);
					sendFindPwdEmail(userRP, dns);
					message = email;
				}
				msg.setSuccess(bln);
				msg.setMessage(message);
			}
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
	
	/**
	 * @Title: findPwd
	 * @Description: 用户点击邮件找回密码链接
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public ModelAndView findPwdByEmail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView();
		Date now = Calendar.getInstance().getTime();
		try{
			String email = ServletRequestUtils.getStringParameter(request, "email");
			String checkSign = ServletRequestUtils.getStringParameter(request,
					"checkSign");
			SysUserRetrievePassword userRP = sysUserRetrievePasswordManager
					.getUserRPBySignEmail(checkSign, email);
			if (null != userRP
					&& userRP.getStatus().intValue() == SysUserRetrievePassword.STATUS_NOT_RESET
					&& userRP.getOverdueTime().getTime() - now.getTime() > 0) {
				result.addObject("userRP", userRP);
				result.setViewName("/home/customer/resetPass.jsp");
			} else {
				result.setViewName("/home/customer/resetPassError.jsp");
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			result.setViewName("/home/customer/resetPassError.jsp");
		} catch (Exception e) {
			e.printStackTrace();
			result.setViewName("/home/customer/resetPassError.jsp");
		}
		return result;
	}
	/**
	 * @Title: findPwd
	 * @Description: 用户填写手机验证码后进行的处理
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage findPwdByMobile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = true;
		String message = "";
		Date now = Calendar.getInstance().getTime();
		try {
			String mobile = ServletRequestUtils.getStringParameter(request,
					"mobile");
			String validateCode = ServletRequestUtils.getStringParameter(
					request, "validateCode");
			String checkSign = DigestUtils.md5Hex(new String(mobile + "$"
					+ validateCode).getBytes());
			SysUserRetrievePassword userRP = sysUserRetrievePasswordManager
					.getUserRPBySignEmail(checkSign, mobile);
			if (userRP == null) {
				throw new PlatformException(
						PlatformErrorCode.CUSTOMER_FINDPASS_CODE_INVALID);
			} else {
				if (userRP.getOverdueTime().getTime() - now.getTime() > 0
						&& userRP.getStatus().intValue() == SysUserRetrievePassword.STATUS_NOT_RESET) {
                      message = "email="+mobile+"&checkSign="+checkSign;
				} else {
                   throw new PlatformException(PlatformErrorCode.CUSTOMER_FINDPASS_CODE_OVERTIME);
				}
		}}
		catch (PlatformException e) {
			e.printStackTrace();
			bln = false;
			message = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			bln = false;
			message = e.getMessage();
		}
		msg.setSuccess(bln);
		msg.setMessage(message);
		return msg;
	}
	/**
	 * @Title: center
	 * @Description: 个人空间显示
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage center(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JsonMessage msg = new JsonMessage();
		String userName = SpringSecurityUtils.getCurrentUserName();
		Map<String, Object> message = new HashMap<String, Object>(4);
		try {
			Customer customer = customerManager.getCustomerByUserName(userName);
			message.put("location",
					StringUtils.trimToEmpty(customer.getLocation()));
			message.put("zip", StringUtils.trimToEmpty(customer.getZip()));
			message.put("address",
					StringUtils.trimToEmpty(customer.getAddress()));
			message.put("nickName",
					StringUtils.trimToEmpty(customer.getNickName()));
			if (customer.getSex() == null) {
				message.put("sex", -1);
			} else {
				message.put("sex", customer.getSex());
			}
			if (customer.getBirthday() == null) {
				message.put("year", "");
				message.put("month", "");
				message.put("day", "");
			} else {
				message.put("year", customer.getBirthday().get(Calendar.YEAR));
				message.put("month",
						customer.getBirthday().get(Calendar.MONTH) + 1);
				message.put("day",
						customer.getBirthday().get(Calendar.DAY_OF_MONTH));
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			msg.setSuccess(Boolean.FALSE);
			msg.setMessage(e.getMessage());
		}
		msg.setSuccess(true);
		msg.setMessage(message);
		return msg;
	}

	/**
	 * @Title: resetPwd
	 * @Description: 重置密码
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage resetPwd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JsonMessage message = new JsonMessage();
		boolean bln = true;
		try {
			String password = ServletRequestUtils.getStringParameter(request,
					"password");
			String rePassword = ServletRequestUtils.getStringParameter(request,
					"rePassword");
			String email = ServletRequestUtils.getStringParameter(request,
					"email");
			String checkSign = ServletRequestUtils.getStringParameter(
					request, "checkSign");
			SysUser user = sysUserManager.getUserByNameOrMobileOrEmail(email);
			if (null!=checkSign) {
				SysUserRetrievePassword userRP = sysUserRetrievePasswordManager
						.getUserRPBySignEmail(checkSign, email);
				Date now = Calendar.getInstance().getTime();
				if (null != userRP
						&& userRP.getStatus().intValue() == SysUserRetrievePassword.STATUS_NOT_RESET
						&& userRP.getOverdueTime().getTime() - now.getTime() > 0) {
					userRP.setStatus(SysUserRetrievePassword.STATUS_ALREADY_RESET);
					sysUserRetrievePasswordManager.saveOrUpdate(userRP);
					sysUserManager.retrievePassword(user.getId(), password, rePassword);
				}else{
					throw new PlatformException(
							PlatformErrorCode.CUSTOMER_PASSWORD_RESET_INVALID);
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
		message.setSuccess(bln);
		return message;

	}
	

	/**
	 * @Title: modifyPwd
	 * @Description: 修改密码
	 * @param oldPassword
	 * @param newPassword
	 * @param reNewPassword
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage modifyPwd(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("password") String newPassword,
			@RequestParam("rePassword") String reNewPassword) throws Exception {
		JsonMessage msg = new JsonMessage();
		String userName = SpringSecurityUtils.getCurrentUserName();
		try {
			sysUserManager.modifyPassword(userName, oldPassword, newPassword,
					reNewPassword);
			// 短信通知
			SysUser user = sysUserManager.getUserByName(userName);
			smsEndpoint.sendMessage(user.getMobile(),
					PlatformMessage.SMS_CHANGE_PWD
							.getDefaultMessage(newPassword));
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

	/**
	 * @Title: checkEmail
	 * @Description: 检查邮箱是否注册
	 * @param email
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage checkEmail(@RequestParam("email") String email)
			throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = false;
		if (null != sysUserManager.getUserByNameOrMobileOrEmail(email)) {
			bln = true;
		}
		msg.setSuccess(bln);
		logger.debug(msg.toString());
		return msg;
	}

	@RequestMapping
	public @ResponseBody
	JsonMessage checkMobile(@RequestParam("mobile") String mobile)
			throws Exception {
		JsonMessage msg = new JsonMessage();
		boolean bln = false;
		if (null != sysUserManager.getUserByName(mobile)
				|| null != sysUserManager.getUserByMobile(mobile)) {
			bln = true;
		}
		msg.setSuccess(bln);
		logger.debug(msg.toString());
		return msg;
	}

	private void sendFindPwdEmail(SysUserRetrievePassword userRP, String dns) {
		VelocityMailSupport velocityMailSupport = (VelocityMailSupport) SpringContextHolder
				.getBean("findPasswordMailSupport");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("userRP", userRP);
		model.put("dns", dns);
		velocityMailSupport.sendMime(userRP.getEmail(), userRP.getEmail(),
				model);
	}

	private void sendActiveEmail(SysUser user, String orginalPassword,
			String activeCode, String dns) {
		VelocityMailSupport velocityMailSupport = (VelocityMailSupport) SpringContextHolder
				.getBean("activationMailSupport");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", user);
		model.put("activeCode", activeCode + "@" + orginalPassword);
		model.put("dns", dns);
		velocityMailSupport.sendMime(user.getEmail(), user.getEmail(), model);
	}
	private SysUserRetrievePassword buildRetrievePasword(String userName,String validateCode) {
		String checkSign = DigestUtils.md5Hex(new String(userName
				+ "$" + validateCode).getBytes());
		SysUserRetrievePassword userRP = new SysUserRetrievePassword();
		userRP.setEmail(userName);
		userRP.setSalt(validateCode);
		userRP.setCheckSign(checkSign);
		userRP.setOverdueTime(new Date(Calendar.getInstance()
				.getTime().getTime() + 30 * 60 * 1000));
		userRP.setStatus(SysUserRetrievePassword.STATUS_NOT_RESET);
		sysUserRetrievePasswordManager.saveOrUpdate(userRP);
		return userRP;
	}

	/**
	 * @Title: sendActiveEmail
	 * @Description: 发送激活邮件
	 * @param request
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage sendActiveEmail(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		try {
			String email = request.getParameter("email");
			SysUser user = sysUserManager.getUserByNameOrMobileOrEmail(email);
			Customer customer = customerManager.getCustomerByUserName(user
					.getUserName());
			String active = customer.getActiveEmailCode();
			String password = StringUtils.substringAfterLast(active, "@");
			String activeCode = RandomStringUtils.random(4, false, true);
			customer.setActiveEmailCode(activeCode + "@" + password);
			customer.setRegDate(Calendar.getInstance());
			customerManager.saveOrUpdate(customer);
			String dns = ServletRequestUtils.getStringParameter(request, "dns")
					+ request.getSession().getServletContext().getContextPath();
			sendActiveEmail(customer.getSysUser(), password, activeCode, dns);
			result.setMessage(customer.getSysUser().getEmail());
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
	 * @Title: loadUserIcon
	 * @Description: 加载用户图片
	 * @param request
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage loadUserIcon(HttpServletRequest request) {
		JsonMessage result = new JsonMessage();
		try {
			Long customerId = ServletRequestUtils.getLongParameter(request,
					"customerId", 0);
			Customer customer = customerManager
					.getCustomerByUserName(SpringSecurityUtils
							.getCurrentUserName());
			if (customerId == 0) {
				customerId = customer.getId();
			}
			if (customer.getPcIcon() != null) {
				result.setMessage("/html/customer/?m=getCustomerPcImg&customerId="
						+ customerId);
			} else {
				result.setMessage("/images/defuser.jpg");
			}
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
	 * @Title: 获取用户信息
	 * @param request
	 * @param response
	 */
	@RequestMapping
	public @ResponseBody
	JsonMessage getCustomer(HttpServletRequest request,
			@RequestParam("cId") Long cId) throws Exception {
		JsonMessage msg = new JsonMessage();
		try {
			Customer customer = customerManager.load(cId);
			msg.setMessage(customer.toMap(null, null));
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
	/**
	 * @Title: getAppPcImg
	 * @Description: 获取用户的图片
	 * @param response
	 * @param appId
	 */
	@RequestMapping
	public void getCustomerPcImg(HttpServletResponse response,
			@RequestParam Long customerId) {
		try {
			byte[] image = customerManager.getPcImgById(customerId);
			if (image != null) {
				SpringMVCUtils.writeImage(image, response);
			}
		} catch (PlatformException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@RequestMapping()
	public @ResponseBody
	JsonResult index(HttpServletRequest request) {
		JsonResult result = new JsonResult();
		try {
			Page<Customer> page = SpringMVCUtils.getPage(request);
			List<PropertyFilter> filters = SpringMVCUtils
					.getParameters(request);
			String mobileNo = request
					.getParameter("custom_search_LIKES_mobileNo");
			if (StringUtils.isBlank(mobileNo)) {
				page = customerManager.findPage(page, filters);
				result.setPage(page, null,
						"sysUser.realName sysUser.userName sysUser.mobile sysUser.email sysUser.status");
			} else {
				page = customerManager.findPageByMobileNo(page, mobileNo);
				result.setPage(page, null,
						"sysUser.realName sysUser.userName sysUser.mobile sysUser.email sysUser.status");
			}
		} catch (PlatformException pe) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(pe.getMessage());
		} catch (Exception e) {
			result.setSuccess(Boolean.FALSE);
			result.setMessage(e.getMessage());
		}
		return result;
	}
}
