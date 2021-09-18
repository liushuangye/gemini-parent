package com.gemini.toolkit.common.utils;

import javax.servlet.http.HttpServletRequest;

import com.gemini.toolkit.login.form.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONArray;

public class GetUserInfo {

	public static UserInfo getUserInfo() {

		UserInfo userInfo = null;
		// 获取请求对象
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();

		String token = request.getHeader(CommonConsts.AUTHORIZATION);

		if (StringUtils.isNotBlank(token)) {
			String userInfoToken = JwtUtil.getUserInfoFromToken(token);
			userInfo = new UserInfo();
			userInfo = JSONArray.parseObject(userInfoToken, UserInfo.class);
		}
		return userInfo;
	}
}
