package com.gemini.toolkit.login.controller;

import java.security.NoSuchAlgorithmException;

import com.gemini.toolkit.login.entity.TStaffEntity;
import com.gemini.toolkit.login.entity.TUserEntity;
import com.gemini.toolkit.login.entity.TUserRoleEntity;
import com.gemini.toolkit.login.form.LoginForm;
import com.gemini.toolkit.login.form.LoginResponseForm;
import com.gemini.toolkit.login.form.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gemini.toolkit.common.exception.PgInputCheckException;
import com.gemini.toolkit.common.utils.JwtUtil;
import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.config.PasswordUtil;
import com.gemini.toolkit.login.service.TStaffService;
import com.gemini.toolkit.login.service.TUserRoleService;
import com.gemini.toolkit.login.service.TUserService;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
@RestController
@RequestMapping("/login")
public class TUserController {

	/** JWT 过期时间 */
	@Value("${jwt.config.expiration_time:#{1800000}}")
	private long expirationTime;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	TUserService userService;
	
	@Autowired
	TStaffService staffService;
	
	@Autowired
	TUserRoleService tUserRoleService;
	

	@PostMapping("/check")
	public R checkLogin(@Validated @RequestBody LoginForm loginForm) {
		try {
			String username = loginForm.getUsername();
			String password = loginForm.getPassword();
			String passwordHash = "";
			passwordHash = PasswordUtil.getSecurePassword(password);
			TUserEntity user = userService.getOne(new LambdaQueryWrapper<TUserEntity>()
					.eq(TUserEntity::getUserId, username)
					.eq(TUserEntity::getDeleteFlg,"0"));

			if (user == null) {
				throw new PgInputCheckException("login.userAndPassword.error",
						messageSource.getMessage("login.userAndPassword.error", null, LocaleContextHolder.getLocale()));
			}
			if(!StringUtils.equals(passwordHash, user.getPasswd())) {
				throw new PgInputCheckException("login.userAndPassword.error",
						messageSource.getMessage("login.userAndPassword.error", null, LocaleContextHolder.getLocale()));
			}
			
			// 判断用户是否是有admin权限
			int count = tUserRoleService.count(new LambdaQueryWrapper<TUserRoleEntity>()
					.eq(TUserRoleEntity::getUserId, username)
					.eq(TUserRoleEntity::getRoleId, "admin")
					.eq(TUserRoleEntity::getDeleteFlg,"0"));
			
			if(count <= 0) {
				throw new PgInputCheckException("login.adminRole.error",
						messageSource.getMessage("login.adminRole.error", null, LocaleContextHolder.getLocale()));
			}
			
			// 查询用户名
			TStaffEntity staff = staffService.getOne(new LambdaQueryWrapper<TStaffEntity>()
					.eq(TStaffEntity::getStaffId, user.getStaffId())
					.eq(TStaffEntity::getDeleteFlg,"0"));

			// 生成token
			UserInfo userInfo = new UserInfo();
			userInfo.setUserCode(username);
			userInfo.setStaffName(staff.getStaffName());
			userInfo.setStaffId(staff.getStaffId());
			
			String userInfoStr = JSON.toJSONString(userInfo);
			String jwtToken = JwtUtil.generateToken(userInfoStr, expirationTime);
			// response
			LoginResponseForm response = new LoginResponseForm();
			response.setUserName(username);
			response.setStaffName(staff.getStaffName());
			response.setToken(jwtToken);
			response.setLanguage("zh-CN");
			return R.ok().put("data", response);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new PgInputCheckException("login.calculationError",
					messageSource.getMessage("login.calculationError", null, LocaleContextHolder.getLocale()));
		}

		
	}
	
    @PostMapping("/logout")
    public R logout() {
    	//TODO logout
        return R.ok();
    }
}
