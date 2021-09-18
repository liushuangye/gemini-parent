package com.gemini.toolkit.login.form;

import java.io.Serializable;

import lombok.Data;


public class UserInfo implements Serializable {
	
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = -441118312397211665L;

	/**
	 * 用户名
	 */
	private String staffId;
	
	/**
	 * 用户名
	 */
	private String staffName;
	
	/**
	 * 用户code
	 */
	private String userCode;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	@Override
	public String toString() {
		return "UserInfo{" +
				"staffId='" + staffId + '\'' +
				", staffName='" + staffName + '\'' +
				", userCode='" + userCode + '\'' +
				'}';
	}
}
