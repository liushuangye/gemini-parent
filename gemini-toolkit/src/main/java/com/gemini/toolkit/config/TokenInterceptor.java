
package com.gemini.toolkit.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.Hutool;
import cn.hutool.core.net.URLDecoder;
import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.common.utils.JwtUtil;
import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.login.entity.TUserEntity;
import com.gemini.toolkit.login.form.UserInfo;
import com.gemini.toolkit.login.service.TUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.nio.charset.Charset;

@Configuration
@Component
public class TokenInterceptor implements HandlerInterceptor {

    /** JWT 过期时间 */
    @Value("${jwt.config.expiration_time:#{1800000}}")
    private long expirationTime;

    @Autowired
    private MessageSource messageSource;

    @Autowired
	private TUserService userInfoService;
    
    private static final String USER_ID = "user_id";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        String requestURI = request.getRequestURI();
        String token = request.getHeader(CommonConsts.AUTHORIZATION);
        //CTMS系统使用CTMS_INTERFACE_TOKEN,跳过验证
        if("CTMS_INTERFACE_TOKEN".equals(token)) return true;

        if(StringUtils.isEmpty(token)){
            Cookie[] cookieArr = request.getCookies();
            if(cookieArr != null && cookieArr.length>0 ) {
                for(Cookie cookie : cookieArr) {
                    if("Authorization".equals(cookie.getName())){
                       token =  cookie.getValue();
                       token = URLDecoder.decode(token, Charset.defaultCharset());//utf-8

                    }
                }
            }
        }
        if (StringUtils.isBlank(token)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            R error = R.error(401, messageSource.getMessage("token.empty", null, LocaleContextHolder.getLocale()));
            response.setHeader(CommonConsts.AUTHORIZATION, "");
            response.getWriter().append(JSON.toJSONString(error));
            return false;
        }
        // 验证token是否有效
        boolean verify = JwtUtil.verify(token);
        if (!verify) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            R error =
                R.error(401, messageSource.getMessage("token.timeout", null, LocaleContextHolder.getLocale()));
            response.setHeader(CommonConsts.AUTHORIZATION, "");
            response.getWriter().append(JSON.toJSONString(error));
            return false;
        }
        String userInfoToken = JwtUtil.getUserInfoFromToken(token);
        if (!StringUtils.isBlank(userInfoToken)) {
            UserInfo userInfo = JSONArray.parseObject(userInfoToken, UserInfo.class);
            // 当前登录用户不存在，跳转到login
            if (userInfoService
                .getMap(new QueryWrapper<TUserEntity>().eq("user_id", userInfo.getUserCode())) == null) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                R error =
                    R.error(401, messageSource.getMessage("token.timeout", null, LocaleContextHolder.getLocale()));
                response.setHeader(CommonConsts.AUTHORIZATION, "");
                response.getWriter().append(JSON.toJSONString(error));
                return false;
            } else {
                String generateToken = JwtUtil.generateToken(userInfoToken, expirationTime);
                response.setHeader(CommonConsts.AUTHORIZATION, generateToken);
            }
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            R error = R.error(401, messageSource.getMessage("token.empty", null, LocaleContextHolder.getLocale()));
            response.setHeader(CommonConsts.AUTHORIZATION, "");
            response.getWriter().append(JSON.toJSONString(error));
            return false;
        }

        return true;
    }

}
