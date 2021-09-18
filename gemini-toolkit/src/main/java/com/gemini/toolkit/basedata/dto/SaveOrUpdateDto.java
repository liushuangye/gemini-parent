package com.gemini.toolkit.basedata.dto;

import java.io.Serializable;

import lombok.Data;


public class SaveOrUpdateDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2094127622035130167L;
	/**
	 * 插入或更新key
	 */
	private String key;
	/**
	 * 插入或更新value
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
		return "SaveOrUpdateDto{" +
				"key='" + key + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
