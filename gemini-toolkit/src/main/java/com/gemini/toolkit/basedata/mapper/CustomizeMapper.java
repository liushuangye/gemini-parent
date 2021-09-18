package com.gemini.toolkit.basedata.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 基础数据导入履历 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
public interface CustomizeMapper {

	public List<Map<String, Object>> getResults(@Param("sqlText") String sqlText);
	
	public String getMaxStaffId();
	
	public boolean bacthUpdate(@Param("sqlList") List<String> sqlList);
	
	public boolean bacthUpdateOracle(@Param("sql") String sql);

	public boolean bacthInsert(@Param("sqlList") List<String> sqlList);
	
	public boolean bacthInsertOracle(@Param("sql") String sql);

	public String getUserId(@Param("mobilePhone") String mobilePhone, @Param("staffName") String staffName);
	
	public String getStaffId(@Param("staffName") String staffName, @Param("mobilePhone") String mobilePhone);
	
	public String getRuleValue();
	
	public boolean updateRuleValue(@Param("currValue") Integer currValue, @Param("userId") String userId
            , @Param("dateTime") Date date);
	
	public String getSpons(@Param("sponsName") String sponsName);

	public Map<String, Object> getOrganizeResults(@Param("organizeId") String organizeId);

	public List<String> getStaffIds(String author);
}
