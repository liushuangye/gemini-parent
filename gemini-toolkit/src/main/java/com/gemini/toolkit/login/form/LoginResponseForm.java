package com.gemini.toolkit.login.form;

import lombok.Data;

/**
 * 登录信息
 * @author Bhh
 *
 */

public class LoginResponseForm {
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 部门id
	 */
	private String staffName;
	/**
	 * token
	 */
	private String token;

	
	private String language;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "LoginResponseForm{" +
				"userName='" + userName + '\'' +
				", staffName='" + staffName + '\'' +
				", token='" + token + '\'' +
				", language='" + language + '\'' +
				'}';
	}
}
