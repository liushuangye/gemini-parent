package com.gemini.toolkit.login.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 登录信息
 * @author zhangliang2019
 *
 */

public class LoginForm {
	/**
	 * 用户名
	 */
	@NotBlank( message="{login.user.notEmpty}")
	@Size(max=20,message="{login.user.lengthMaxError}")
	private String username;

	/**
	 * 密码
	 */
	@NotBlank( message="{login.password.error}")
//	@Size(min=6,message="{login.password.error}")
//	@Size(max=20,message="{login.password.lengthMaxError}")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginForm{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
