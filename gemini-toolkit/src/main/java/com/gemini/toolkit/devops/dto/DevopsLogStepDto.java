package com.gemini.toolkit.devops.dto;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import lombok.Data;


public class DevopsLogStepDto {

	/**
	 * Step
	 */
	private String stepName;
	
	/**
	 * 描述
	 */
	private String stepDesc;
	
	
	/**
	 * 执行参数
	 */
	private String execParamsStr;
	
	/**
	 * 执行的参数
	 */
//	private Map<String, Object> execParam;

	/**
	 * 执行SQL
	 */
	private String execSqlStr;
	
	/**
	 * 执行的sql
	 */
//	private List<String> execSql;
	
	/**
	 * 查询结果
	 */
	private String execDataStr;
	
	/**
	 * 查询结果
	 */
//	private Map<String, List<Map<String, Object>>> execData;
	
	/**
	 * 备份的sql
	 */
//	private String backUpSqlStr;
	
	/**
	 * 备份数据
	 */
	private String backUpDataStr;
	
	/**
	 * 备份的数据
	 */
//	private List<DevopsToolsBackupData> backUpData;
	
	/**
	 * 回滚sql Str
	 */
	private String rollBackSqlStr;
	
	/**
	 * 回滚的sql
	 */
//	private List<DevopsToolsRollbackSql> rollBackSql;
	/**
	 * 执行动作
	 */
	private String status;
	
	/**
	 * 执行人
	 */
	private String userName;
	
	/**
	 * 执行时间
	 */
	private String createDateTime;
	
	
//	@SuppressWarnings("unchecked")
//	public Map<String,Object> getExecParam() {
//		
//		return JSON.parseObject(this.execParamsStr, Map.class);
//		
//	}


	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getStepDesc() {
		return stepDesc;
	}

	public void setStepDesc(String stepDesc) {
		this.stepDesc = stepDesc;
	}

	public String getExecParamsStr() {
		return execParamsStr;
	}

	public void setExecParamsStr(String execParamsStr) {
		this.execParamsStr = execParamsStr;
	}

	public String getExecSqlStr() {
		return execSqlStr;
	}

	public void setExecSqlStr(String execSqlStr) {
		this.execSqlStr = execSqlStr;
	}

	public String getExecDataStr() {
		return execDataStr;
	}

	public void setExecDataStr(String execDataStr) {
		this.execDataStr = execDataStr;
	}

	public String getBackUpDataStr() {
		return backUpDataStr;
	}

	public void setBackUpDataStr(String backUpDataStr) {
		this.backUpDataStr = backUpDataStr;
	}

	public String getRollBackSqlStr() {
		return rollBackSqlStr;
	}

	public void setRollBackSqlStr(String rollBackSqlStr) {
		this.rollBackSqlStr = rollBackSqlStr;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}

	@Override
	public String toString() {
		return "DevopsLogStepDto{" +
				"stepName='" + stepName + '\'' +
				", stepDesc='" + stepDesc + '\'' +
				", execParamsStr='" + execParamsStr + '\'' +
				", execSqlStr='" + execSqlStr + '\'' +
				", execDataStr='" + execDataStr + '\'' +
				", backUpDataStr='" + backUpDataStr + '\'' +
				", rollBackSqlStr='" + rollBackSqlStr + '\'' +
				", status='" + status + '\'' +
				", userName='" + userName + '\'' +
				", createDateTime='" + createDateTime + '\'' +
				'}';
	}
}
