package com.gemini.toolkit.common.utils;

public class Constants {

	
	public static final String BACKUPLIST = "backupList";
	
	public static final String ROLLBACKLIST = "rollbackList";
	
	public static final String SELECT_REGX= "(select)(.+)(from)(.+)";
	
	public static final String UPDATE_REGX = "(update)(.+)(set)(.+)(where)(.+)";
	
	public static final String DELETE_REGX = "(delete from)(.+)(where)(.+)";
	
	public static final String INSERT_REGX= "(insert into)(.+)(values)(.+)";
	
	/**
	 *  执行状态 未执行
	 */
	public static final String EXEC_STATUS_0= "0";
	
	/**
	 * 执行状态 已执行
	 */
	public static final String EXEC_STATUS_1= "1";
	
	/**
	 * 执行状态 回滚
	 */
	public static final String EXEC_STATUS_2= "2";
	
	
	
	/**
	 *  操作code  1:运维确认
	 */
	public static final String ACTION_CODE_1= "1";
	
	/**
	 * 操作code  2:执行运维sql
	 */
	public static final String ACTION_CODE_2= "3";
	
	/**
	 * 操作code  3:回滚
	 */
	public static final String ACTION_CODE_3= "3";
	
}
