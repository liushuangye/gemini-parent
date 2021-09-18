package com.gemini.toolkit.basedata.dto;

import java.io.Serializable;

import lombok.Data;


public class CondtionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2094127622035130167L;
	/**
	 * 查询key
	 */
	private String key;
	/**
	 * 查询value
	 */
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "CondtionDto{" +
				"key='" + key + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
