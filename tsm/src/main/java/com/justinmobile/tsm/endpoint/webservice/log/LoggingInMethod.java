package com.justinmobile.tsm.endpoint.webservice.log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebResult;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.utils.encode.JsonBinder;
import com.justinmobile.tsm.application.domain.Application;
import com.justinmobile.tsm.application.manager.ApplicationManager;
import com.justinmobile.tsm.customer.domain.CustomerCardInfo;
import com.justinmobile.tsm.customer.manager.CustomerCardInfoManager;
import com.justinmobile.tsm.endpoint.webservice.dto.CardPOR;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.log.LogMethod.Type;
import com.justinmobile.tsm.endpoint.webservice.log.domain.MethodLog;
import com.justinmobile.tsm.endpoint.webservice.log.manager.MethodLogManager;

@Aspect
@Service("loggingInMethod")
public class LoggingInMethod {
	
	@Autowired
	private MethodLogManager methodLogManager;
	
	@Autowired
	private ApplicationManager applicationManager;
	
	@Autowired
	private CustomerCardInfoManager customerCardInfoManager;
	
	/**
	 * 环绕式切入，执行前记录传入的参数，执行后记录返回的结果
	 * @param jp
	 * @return
	 * @throws Throwable
	 */
	@Around("@annotation(com.justinmobile.tsm.endpoint.webservice.log.LogMethod)&&@annotation(logMethod)")
	public Object handleMessage(ProceedingJoinPoint jp, LogMethod logMethod) throws Throwable {
		MethodLog log = new MethodLog();
		JsonBinder jsonBinder = JsonBinder.buildNormalBinder();
		Object result = null;
		Method method = null;
		try {
			Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
			//得到目标类接口
			Class<?> targetClass = jp.getSignature().getDeclaringType();
			//接口名称
			String targetName = targetClass.getSimpleName();
			//得到方法名称
			String methodName = jp.getSignature().getName();
			//得到传入参数
			Object[] args = jp.getArgs();
			
			//处理apdu响应
			buildApduReq(logMethod, log, args);
			
			log.setTargetName(targetName);
			log.setMethodName(methodName);
			
			method = buildParam(paramMap, targetClass, methodName, args);
			//将传入的参数转成JsonString
			log.setParams(jsonBinder.toJson(paramMap));
			
			log.setStartTime(Calendar.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
			//不影响到主流程
		}
		//执行被切入的方法
		result = jp.proceed();
		try {
			log.setEndTime(Calendar.getInstance());
			
			//处理apdu下发
			buildApduRes(logMethod, log, result);
			
			//得到返回结果的声明名称
			WebResult webResult = method.getAnnotation(WebResult.class);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put(webResult.name(), result);
			//将结果转成JsonString
			log.setResult(jsonBinder.toJson(resultMap));
			methodLogManager.saveOrUpdate(log);
		} catch (Exception e) {
			e.printStackTrace();
			//不影响到主流程
		}
		return result;
	}

	private void buildApduRes(LogMethod logMethod, MethodLog log, Object result) {
		if (Type.APDU.equals(logMethod.value()) && (result instanceof ResExecAPDU)) {
			ResExecAPDU res = (ResExecAPDU) result;
			String appAid = res.getCurrentAppAid();
			if (StringUtils.isNotBlank(appAid)) {
				Application app = applicationManager.getByAid(appAid);
				log.setApplication(app);
			}
			log.setSessionId(res.getSessionID());
			log.setSeqNum(res.getSeqNum());
			log.setApduName(res.getApduName());
		}
	}

	private void buildApduReq(LogMethod logMethod, MethodLog log, Object[] args) {
		if (Type.APDU.equals(logMethod.value()) && (args[0] instanceof ReqExecAPDU)) {
			ReqExecAPDU req = (ReqExecAPDU) args[0];
			String cardNo = req.getCardNo();
			if (StringUtils.isNotBlank(cardNo)) {
				try {
					CustomerCardInfo customerCardInfo = customerCardInfoManager.getByCardNo(cardNo);
					log.setCustomerCardInfo(customerCardInfo);
				} catch (Exception e) {
					//卡片未找到，不建立关联关系
				}
			}
			CardPOR cardPOR = req.getCardPOR();
			if (cardPOR != null && StringUtils.isNotBlank(cardPOR.getLastAPDUSW())) {
				MethodLog preLog = methodLogManager.getLog(req.getSessionID(), req.getSeqNum());
				if (preLog != null) {
					preLog.setCardResult(cardPOR.getLastAPDUSW());
					methodLogManager.saveOrUpdate(preLog);
				}
			}
		}
	}

	private Method buildParam(Map<String, Object> paramMap, Class<?> targetClass, String methodName, Object[] args)
			throws NoSuchMethodException {
		Class<?>[] parameterTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			Class<? extends Object> argClass = args[i].getClass();
			parameterTypes[i] = argClass;
		}
		//得到接口声明的方法
		Method method = targetClass.getDeclaredMethod(methodName, parameterTypes);
		//得到方法参数声明的Annotation
		Annotation[][] annotations = method.getParameterAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			for (Annotation annotation : annotations[i]) {
				if (annotation instanceof WebParam) {
					//根据WebParam的name得到参数名称
					WebParam webParam = (WebParam) annotation;
					paramMap.put(webParam.name(), args[i]);
				}
			}
		}
		return method;
	}

}
