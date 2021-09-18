package com.gemini.toolkit.basedata.dto;

import java.io.Serializable;

import lombok.Data;


public class CheckErrorDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7182113308779037648L;

	/**
	 *check失败行数
	 */
	private Integer errorRow;
	
	/**
	 * check失败信息
	 */
	private String errorMessage;

	public Integer getErrorRow() {
		return errorRow;
	}

	public void setErrorRow(Integer errorRow) {
		this.errorRow = errorRow;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "CheckErrorDto{" +
				"errorRow=" + errorRow +
				", errorMessage='" + errorMessage + '\'' +
				'}';
	}
}
