package com.gemini.toolkit.basedata.dto;

import java.io.Serializable;
import java.util.List;


import lombok.Data;


public class TableInfoDto implements Serializable {
	
	private static final long serialVersionUID = 1073377785971345398L;
	
	/**
	 * sheet名
	 */
	private String sheetName;
	
	/**
	 * 查询条件
	 */
	private List<CondtionDto> condtions;
	
	/**
	 * 直接查询条件
	 */
	private String queryCondtions;
	
	/**
	 * 类型 : main:主表 ,code: code表
	 */
	private String tableType;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 表字段名
	 */
	private List<String> fileds;
	
	/**
	 * 表字段中文名
	 */
	private List<String> aliasNames;
	
	/**
	 * 引用信息
	 */
	private List<RefKVDto> refKVs;

	/**
	 * 排序
	 */
	private String sort;

	/**
	 * 表字段更新值
	 */
	private List<SaveOrUpdateDto> saveOrUpdate;

	/**
	 *登录者
	 */
	private String createUserId;

	/**
	 *更新者
	 */
	private String updateUserId;

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<CondtionDto> getCondtions() {
		return condtions;
	}

	public void setCondtions(List<CondtionDto> condtions) {
		this.condtions = condtions;
	}

	public String getQueryCondtions() {
		return queryCondtions;
	}

	public void setQueryCondtions(String queryCondtions) {
		this.queryCondtions = queryCondtions;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getFileds() {
		return fileds;
	}

	public void setFileds(List<String> fileds) {
		this.fileds = fileds;
	}

	public List<String> getAliasNames() {
		return aliasNames;
	}

	public void setAliasNames(List<String> aliasNames) {
		this.aliasNames = aliasNames;
	}

	public List<RefKVDto> getRefKVs() {
		return refKVs;
	}

	public void setRefKVs(List<RefKVDto> refKVs) {
		this.refKVs = refKVs;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<SaveOrUpdateDto> getSaveOrUpdate() {
		return saveOrUpdate;
	}

	public void setSaveOrUpdate(List<SaveOrUpdateDto> saveOrUpdate) {
		this.saveOrUpdate = saveOrUpdate;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	@Override
	public String toString() {
		return "TableInfoDto{" +
				"sheetName='" + sheetName + '\'' +
				", condtions=" + condtions +
				", queryCondtions='" + queryCondtions + '\'' +
				", tableType='" + tableType + '\'' +
				", tableName='" + tableName + '\'' +
				", fileds=" + fileds +
				", aliasNames=" + aliasNames +
				", refKVs=" + refKVs +
				", sort='" + sort + '\'' +
				", saveOrUpdate=" + saveOrUpdate +
				", createUserId='" + createUserId + '\'' +
				", updateUserId='" + updateUserId + '\'' +
				'}';
	}
}
