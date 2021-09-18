package com.gemini.toolkit.log;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gemini.toolkit.log.service.OpLogService;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.common.utils.JwtUtil;
import com.gemini.toolkit.log.entity.TToolkitOplogDetailEntity;
import com.gemini.toolkit.log.entity.TToolkitOplogEntity;
import com.gemini.toolkit.login.form.UserInfo;

/**
 * @author zcj
 */

@Aspect
@Component
public class SystemLogAspect {

	/**
	 * 注入Service用于把日志保存数据库
	 */
	@Resource
	private OpLogService logService;

	/**
	 * 本地异常日志记录对象
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

	/**
	 * Service层切点
	 */
	@Pointcut("@annotation(OpLog)")
	public void serviceAspect() {
	}

	/**
	 * 正常通知 用于拦截service层记录正常日志
	 */
	@Around("serviceAspect()")
	public Object doAfter(ProceedingJoinPoint point) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		MyRequestWrapper requestWrapper = new MyRequestWrapper(request);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		Object result = null;
		TToolkitOplogEntity log = new TToolkitOplogEntity();
		log.setResultMsg("成功");
		log.setResultCode(response.getStatus());
		String[] params = SystemLogAspect.getServiceMthodDescription(point);
		log.setUuid(UUID.randomUUID().toString());

		String token = request.getHeader(CommonConsts.AUTHORIZATION);
		if (StringUtils.isNotBlank(token)) {
			String userInfoStr = JwtUtil.getUserInfoFromToken(token);
			UserInfo userInfo = JSONArray.parseObject(userInfoStr, UserInfo.class);
			if (userInfo != null) {
				log.setUserCode(userInfo.getStaffId());
				log.setUserName(userInfo.getStaffName());
				log.setCreateUserId(userInfo.getStaffId());
				log.setUpdateUserId(userInfo.getStaffId());
			}
		}
		String requestIp = getRequestIp(request);
		log.setIpAddress(requestIp);
		log.setClassName(point.getTarget().getClass().getSimpleName());
		log.setMethodName(point.getSignature().getName());
		log.setRequestUri(requestWrapper.getRequestURI());
		log.setModuleName(params[1]);
		log.setOpName(params[2]);
		log.setRemarks(params[0]);
		Date nowtime = new Date();
		log.setRequestTime(nowtime);
		Long start = System.currentTimeMillis();
		result = point.proceed();
		Long end = System.currentTimeMillis();
		Long time = end - start;
		log.setExecTime(time);
		log.setCreateDateTime(nowtime);
		log.setUpdateDateTime(nowtime);
		log.setDeleteFlg("0");
		// 获取header和body
		String header = "", body = "";
		Enumeration<?> enum1 = requestWrapper.getHeaderNames();
		while (enum1.hasMoreElements()) {
			String key = (String) enum1.nextElement();
			String value = requestWrapper.getHeader(key);
			header += key + ":" + value + ";";
		}
		Object[] args = point.getArgs();
		Object[] arguments = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse
					|| args[i] instanceof MultipartFile) {
				// ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is
				// illegal to call this method if the current request is not in asynchronous
				// mode (i.e. isAsyncStarted() returns false)
				// ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException:
				// getOutputStream() has already been called for this response
				continue;
			}
			arguments[i] = args[i];
		}
		// 获取requestBody的参数
		body = JSONObject.toJSONString(arguments);
		TToolkitOplogDetailEntity logDetail = new TToolkitOplogDetailEntity();
		logDetail.setExceptionStacktrace(null);
		logDetail.setUuid(UUID.randomUUID().toString());

		logDetail.setRequestBody(body);
		logDetail.setRequestHeaders(header);
		logDetail.setTSysOplogid(log.getUuid());
		logDetail.setCreateDateTime(nowtime);
		logDetail.setUpdateDateTime(nowtime);
		logDetail.setDeleteFlg("0");
		if (StringUtils.isNotBlank(token)) {
			String userInfoStr = JwtUtil.getUserInfoFromToken(token);
			UserInfo userInfo = JSONArray.parseObject(userInfoStr, UserInfo.class);
			if (userInfo != null) {
				logDetail.setUpdateUserId(userInfo.getStaffId());
				logDetail.setCreateUserId(userInfo.getStaffId());
			}
		}

		// 保存数据库
		logService.insertOpLogInfo(log, logDetail);
		return result;
	}

	/**
	 * 异常通知 用于拦截service层记录异常日志
	 *
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(pointcut = "serviceAspect()", throwing = "e")
	public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		try {
			MyRequestWrapper requestWrapper = new MyRequestWrapper(request);
			TToolkitOplogEntity log = new TToolkitOplogEntity();
			log.setResultMsg("失败");
			log.setResultCode(response.getStatus());
			String[] params = SystemLogAspect.getServiceMthodDescription(joinPoint);
			log.setUuid(UUID.randomUUID().toString());
			String token = request.getHeader(CommonConsts.AUTHORIZATION);
			if (StringUtils.isNotBlank(token)) {
				String userInfoStr = JwtUtil.getUserInfoFromToken(token);
				UserInfo userInfo = JSONArray.parseObject(userInfoStr, UserInfo.class);
				if (userInfo != null) {
					log.setUserCode(userInfo.getStaffId());
					log.setUserName(userInfo.getStaffName());
					log.setCreateUserId(userInfo.getStaffId());
					log.setUpdateUserId(userInfo.getStaffId());
				}
			}
			String requestIp = getRequestIp(request);
			log.setIpAddress(requestIp);
			log.setClassName(joinPoint.getTarget().getClass().getSimpleName());
			log.setMethodName(joinPoint.getSignature().getName());
			log.setRequestUri(requestWrapper.getRequestURI());
			log.setModuleName(params[1]);
			log.setOpName(params[2]);
			log.setRemarks(params[0]);
			Date nowtime = new Date();
			log.setRequestTime(nowtime);
			log.setExecTime(0L);
			log.setDeleteFlg("0");
			log.setCreateDateTime(nowtime);
			log.setUpdateDateTime(nowtime);
			// 获取header和body
			String header = "", body = "";
			Enumeration<?> enum1 = requestWrapper.getHeaderNames();
			while (enum1.hasMoreElements()) {
				String key = (String) enum1.nextElement();
				String value = requestWrapper.getHeader(key);
				header += key + ":" + value + ";";
			}
			Object[] args = joinPoint.getArgs();
			Object[] arguments = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse
						|| args[i] instanceof MultipartFile) {
					// ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is
					// illegal to call this method if the current request is not in asynchronous
					// mode (i.e. isAsyncStarted() returns false)
					// ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException:
					// getOutputStream() has already been called for this response
					continue;
				}
				arguments[i] = args[i];
			}
			// 获取requestBody的参数
			body = JSONObject.toJSONString(arguments);

			TToolkitOplogDetailEntity logDetail = new TToolkitOplogDetailEntity();
			logDetail.setExceptionStacktrace(e.getMessage());
			logDetail.setUuid(UUID.randomUUID().toString());

			logDetail.setRequestBody(body);
			logDetail.setRequestHeaders(header);
			logDetail.setTSysOplogid(log.getUuid());
			logDetail.setCreateDateTime(nowtime);
			logDetail.setUpdateDateTime(nowtime);
			logDetail.setDeleteFlg("0");
			if (StringUtils.isNotBlank(token)) {
				String userInfoStr = JwtUtil.getUserInfoFromToken(token);
				UserInfo userInfo = JSONArray.parseObject(userInfoStr, UserInfo.class);
				if (userInfo != null) {
					logDetail.setUpdateUserId(userInfo.getStaffId());
					logDetail.setCreateUserId(userInfo.getStaffId());
				}
			}
			// 保存数据库
			logService.insertOpLogInfo(log, logDetail);
		} catch (Exception ex) {
			// 记录本地异常日志
			logger.error("==异常通知异常==");
			logger.error("异常信息:{}", ex.getMessage());
		}
	}

	/**
	 * 获取注解中对方法的描述信息 用于service层注解
	 *
	 * @param joinPoint
	 *            切点
	 * @return 方法描述
	 * @throws Exception
	 */
	public static String[] getServiceMthodDescription(JoinPoint joinPoint) throws Exception {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		OpLog controllerLog = method.getAnnotation(OpLog.class);
		String[] params = new String[3];
		params[0] = controllerLog.remarks();
		params[1] = controllerLog.moduleName();
		params[2] = controllerLog.opName();
		return params;
	}
	private String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
		String unknown = "unknown";
		if (ip != null && ip.length() > 0 && !unknown.equalsIgnoreCase(ip)) {
        	int idx = ip.indexOf(",");
        	if (idx == -1) {
        		return ip;
        	}
        	else {
        		return ip.substring(0, idx);
        	}
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && ip.length() > 0 && !unknown.equalsIgnoreCase(ip)) {
        	return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !unknown.equalsIgnoreCase(ip)) {
        	return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && ip.length() > 0 && !unknown.equalsIgnoreCase(ip)) {
        	return ip;
        }

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip != null && ip.length() > 0 && !unknown.equalsIgnoreCase(ip)) {
        	return ip;
        }

        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip != null && ip.length() > 0 && !unknown.equalsIgnoreCase(ip)) {
        	return ip;
        }

        return request.getRemoteAddr();
	}
}
