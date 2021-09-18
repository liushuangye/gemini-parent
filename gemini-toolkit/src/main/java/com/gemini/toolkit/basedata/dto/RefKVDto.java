package com.gemini.toolkit.basedata.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.Data;


/**
 * @author bhh
 * 
 * join表信息 </br>
 * 
 * eg:tableName = t_code </br>
 *     joinKey = code_id </br>
 *     refValue = code_name </br>
 *     condtion = {key:code_type,value:GENDER_CD} </br>
 * 
 * select t_code.code_name from xxx join t_code t_code on xxx.xxx = t_code.code_id and t_code.code_type = 'GENDER_CD'
 *    
 */

public class RefKVDto implements Serializable {

	
	private static final long serialVersionUID = 243654816536293098L;

	/**
	 * 引用的sheet
	 */
	private String refSheetName;
	/**
	 * join 表名
	 */
	private String tableName;
	
	/**
	 * join key
	 */
	private String joinKey;
	
	/**
	 * 查询项目
	 */
	private String refValue;
	
	/**
	 * 过滤条件
	 */
	private List<CondtionDto> condtions;

	/**
	 * 判断是否有引用
	 * @return
	 */
	public boolean isJoin() {
		if(StringUtils.isNotBlank(this.refSheetName)) {
			return true;
		}
		return false;
	}
	
	/**
	 * join关系 
	 */
	private String join;
	
	/**
	 * 不join主表时联合查询表名
	 */
	private String joinTableName;
	
	/**
	 * 不join主表时联合查询表名
	 */
	private boolean flg;

	public String getRefSheetName() {
		return refSheetName;
	}

	public void setRefSheetName(String refSheetName) {
		this.refSheetName = refSheetName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getJoinKey() {
		return joinKey;
	}

	public void setJoinKey(String joinKey) {
		this.joinKey = joinKey;
	}

	public String getRefValue() {
		return refValue;
	}

	public void setRefValue(String refValue) {
		this.refValue = refValue;
	}

	public List<CondtionDto> getCondtions() {
		return condtions;
	}

	public void setCondtions(List<CondtionDto> condtions) {
		this.condtions = condtions;
	}

	public String getJoin() {
		return join;
	}

	public void setJoin(String join) {
		this.join = join;
	}

	public String getJoinTableName() {
		return joinTableName;
	}

	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}

	public boolean isFlg() {
		return flg;
	}

	public void setFlg(boolean flg) {
		this.flg = flg;
	}

	@Override
	public String toString() {
		return "RefKVDto{" +
				"refSheetName='" + refSheetName + '\'' +
				", tableName='" + tableName + '\'' +
				", joinKey='" + joinKey + '\'' +
				", refValue='" + refValue + '\'' +
				", condtions=" + condtions +
				", join='" + join + '\'' +
				", joinTableName='" + joinTableName + '\'' +
				", flg=" + flg +
				'}';
	}
}
