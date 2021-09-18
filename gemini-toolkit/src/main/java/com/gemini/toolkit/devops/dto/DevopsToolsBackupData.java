package com.gemini.toolkit.devops.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;


public class DevopsToolsBackupData {

	
	/**
	 * 唯一key
	 */
	private String unionKey;
	
	/**
	 * 执行的sql
	 */
	private String backupSql;
	
	/**
	 * 备份的数据
	 */
	private List<Map<String, Object>> data;
	
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

	public String getBackupSql() {
		return backupSql;
	}

	public void setBackupSql(String backupSql) {
		this.backupSql = backupSql;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public boolean isAutoGen() {
		return autoGen;
	}

	public void setAutoGen(boolean autoGen) {
		this.autoGen = autoGen;
	}

	@Override
	public String toString() {
		return "DevopsToolsBackupData{" +
				"unionKey='" + unionKey + '\'' +
				", backupSql='" + backupSql + '\'' +
				", data=" + data +
				", autoGen=" + autoGen +
				'}';
	}
}
