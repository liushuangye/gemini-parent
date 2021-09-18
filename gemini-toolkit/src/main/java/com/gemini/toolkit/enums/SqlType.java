package com.gemini.toolkit.enums;

/**
 * sqltype 枚举类
 * @author jintg
 *
 */
public enum SqlType {

	SELECT(1, "select"), UPDATE(2, "update"), DELETE(3, "delete"), INSERT(4, "insert");

	private final String value;

	private final int id;

	private SqlType(int id, String value) {

		this.id = id;
		this.value = value;

	}

	public String getValue() {

		return this.value;
	}

	public int getId() {

		return this.id;
	}
}
