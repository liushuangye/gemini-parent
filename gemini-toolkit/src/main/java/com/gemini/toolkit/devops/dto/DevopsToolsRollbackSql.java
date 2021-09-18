package com.gemini.toolkit.devops.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;


public class DevopsToolsRollbackSql {

	
	/**
	 * 唯一key
	 */
	private String unionKey;
	
	/**
	 * 执行的sql
	 */
	private String rollbackSql;
	
	/**
	 * 是否是自动逆向生成sql
	 */
	
	private boolean autoGen = false;

	public String getUnionKey() {
		return unionKey;
	}

	public void setUnionKey(String unionKey) {
		this.unionKey = unionKey;
	}

	public String getRollbackSql() {
		return rollbackSql;
	}

	public void setRollbackSql(String rollbackSql) {
		this.rollbackSql = rollbackSql;
	}

	public boolean isAutoGen() {
		return autoGen;
	}

	public void setAutoGen(boolean autoGen) {
		this.autoGen = autoGen;
	}

	@Override
	public String toString() {
		return "DevopsToolsRollbackSql{" +
				"unionKey='" + unionKey + '\'' +
				", rollbackSql='" + rollbackSql + '\'' +
				", autoGen=" + autoGen +
				'}';
	}
}
