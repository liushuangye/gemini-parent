package com.gemini.toolkit.common.utils;

import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.RefKVDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 生成sql帮助类
 * 
 * @author jintg
 *
 */
public class SqlBuilderHelper {

	public static String querySqlBuilder(TableInfoDto tableInfo) {

		StringBuilder selectSql = new StringBuilder();
		List<String> joinSqls = new ArrayList<String>();
		selectSql.append("select ");
		// 循环遍历每个filed
		for (int i = 0; i < tableInfo.getFileds().size(); i++) {
			String filed = tableInfo.getFileds().get(i);
			String aliasName = tableInfo.getAliasNames().get(i);
			// 判断当前字段是否关联查询
			if (!CheckParamUtil.isEmpty(tableInfo.getRefKVs())) {
				RefKVDto ref = tableInfo.getRefKVs().get(i);
				if (ref.isJoin()) {
					String aliasTableName = ref.getTableName() + i;
					String joinSql = joinSqlBuilder(ref, tableInfo.getTableName(), filed, aliasTableName);
					joinSqls.add(joinSql);
					// 拼接显示joinkey和查询项
					if (!ref.isFlg()) {
						selectSql.append(aliasTableName).append(".").append(ref.getJoinKey()).append("|| ':' ||");
					}
					selectSql.append(aliasTableName).append(".").append(ref.getRefValue());
					selectSql.append(" \"").append(aliasName).append("\"");

				} else {
					selectSql.append(tableInfo.getTableName()).append(".");
					selectSql.append(filed).append(" \"").append(aliasName).append("\"");
				}
			} else {
				selectSql.append(tableInfo.getTableName()).append(".");
				selectSql.append(filed).append(" \"").append(aliasName).append("\"");
			}
			selectSql.append(",");
		}
		// 删除最后一个 逗号
		selectSql.deleteCharAt(selectSql.length() - 1);
		// 拼接from
		selectSql.append(" from ").append(tableInfo.getTableName()).append(" ").append(tableInfo.getTableName());
		// 拼接join
		if (joinSqls != null && joinSqls.size() > 0) {
			for (String joinsql : joinSqls) {
				selectSql.append(joinsql);
			}
		}

		// 拼接where条件
		selectSql.append(" where ");
		selectSql.append(tableInfo.getTableName()).append(".");
		selectSql.append("delete_flg = '0' ");
		if (!CheckParamUtil.isEmpty(tableInfo.getQueryCondtions())) {
			selectSql.append(" and ").append(tableInfo.getQueryCondtions());
		}
		selectSql.append(andSqlBuilder(tableInfo.getCondtions(), tableInfo.getTableName()));
		if (tableInfo.getSort() != null) {
			selectSql.append(" order by ");
			String[] sort = tableInfo.getSort().split(",");
			for (int i = 0; i < sort.length; i++) {
				selectSql.append(tableInfo.getTableName()).append(".");
				selectSql.append(sort[i]).append(",");
				;
			}
			// 删除最后一个 逗号
			selectSql.deleteCharAt(selectSql.length() - 1);
		}

		return selectSql.toString();
	}

	public static String joinSqlBuilder(RefKVDto ref, String mainTableName, String filed, String aliasTableName) {

		StringBuilder joinSql = new StringBuilder();
		// eg join t_code t_code on t_staff.sex_type = t_code.code_id and
		// t_code.code_type = 'GENDER_CD'
		joinSql.append(" ").append(ref.getJoin()).append(" ").append(ref.getTableName()).append(" ")
				.append(aliasTableName).append(" on ").append(mainTableName).append(".").append(filed).append(" = ")
				.append(aliasTableName).append(".").append(ref.getJoinKey());
		joinSql.append(" and ");
		joinSql.append(aliasTableName).append(".");
		joinSql.append("delete_flg = '0' ");
		joinSql.append(andSqlBuilder(ref.getCondtions(), aliasTableName));

		return joinSql.toString();
	}

	public static String andSqlBuilder(List<CondtionDto> condtions, String aliasTableName) {
		StringBuilder andSql = new StringBuilder();
		if (condtions != null && condtions.size() > 0) {
			for (CondtionDto condtion : condtions) {
				andSql.append(" and ");
				andSql.append(aliasTableName).append(".");
				andSql.append(condtion.getKey()).append("= '").append(condtion.getValue()).append("'");
			}
		}

		return andSql.toString();
	}

	public static String insertSqlBuilder(TableInfoDto tableInfo, String dbType) {
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("insert into ");
		insertSql.append(tableInfo.getTableName()).append("(");
		// 添加插入字段
		for (int i = 0; i < tableInfo.getSaveOrUpdate().size(); i++) {
			String filed = tableInfo.getSaveOrUpdate().get(i).getKey();
			String value = tableInfo.getSaveOrUpdate().get(i).getValue();
			// 插入字段
			insertSql.append(filed).append(",");
			if (!CheckParamUtil.isEmpty(value)) {
				// 插入字段值
				valuesSql.append("'").append(value).append("',");
			} else {
				valuesSql.append("NULL ,");
			}
		}
		// 追加固定字段
		insertSql.append(
				"update_key,delete_flg,create_date_time,create_user_id,update_date_time,update_user_id,id,uuid)");
		insertSql.append(" VALUES ( ");

		insertSql.append(valuesSql);
		// 添加固定字段插入值
		insertSql.append("0,'0',");
		Date nowtime = new Date();
		String keySequence = "";
		if (dbType.equals("oracle")) {
			keySequence = tableInfo.getTableName() + "_seq.nextval";
			// 时间转换格式
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(nowtime);
			insertSql.append(" to_date('").append(date).append("', 'yyyy-mm-dd hh24:mi:ss') ");
			insertSql.append(",'").append(tableInfo.getCreateUserId()).append("',");
			insertSql.append(" to_date('").append(date).append("', 'yyyy-mm-dd hh24:mi:ss') ");
			insertSql.append(",'").append(tableInfo.getUpdateUserId()).append("',");
		} else {
			keySequence = "nextval('" + tableInfo.getTableName() + "_seq')";
			insertSql.append("'").append(nowtime).append("','").append(tableInfo.getCreateUserId()).append("','");
			insertSql.append(nowtime).append("','").append(tableInfo.getUpdateUserId()).append("',");
		}
		insertSql.append(keySequence).append(",SYS_GUID()");
		insertSql.append(")");
		return insertSql.toString();
	}

	public static String updateSqlBuilder(TableInfoDto tableInfo, String dbType) {
		StringBuilder updateSql = new StringBuilder();
		updateSql.append("update ");
		updateSql.append(tableInfo.getTableName()).append(" set ");
		for (int i = 0; i < tableInfo.getSaveOrUpdate().size(); i++) {
			String filed = tableInfo.getSaveOrUpdate().get(i).getKey();
			String filedValue = tableInfo.getSaveOrUpdate().get(i).getValue();
			updateSql.append(filed).append(" = ");
			if (!CheckParamUtil.isEmpty(filedValue)) {
				updateSql.append("'").append(filedValue).append("',");
			} else {
				updateSql.append("NULL ,");
			}
		}
		updateSql.append(" update_key = update_key + 1 ,");
		if (dbType.equals("oracle")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new Date());
			updateSql.append(" update_date_time = ").append(" to_date('").append(date)
					.append("', 'yyyy-mm-dd hh24:mi:ss') ");
		} else {
			updateSql.append(" update_date_time = '").append(new Date()).append("'");
		}
		updateSql.append(", update_user_id = '").append(tableInfo.getUpdateUserId());
		// 拼接where条件
		updateSql.append("' where ");
		for (int i = 0; i < tableInfo.getCondtions().size(); i++) {
			String updateKey = tableInfo.getCondtions().get(i).getKey();
			String updateValue = tableInfo.getCondtions().get(i).getValue();
			updateSql.append(updateKey).append(" = '");
			updateSql.append(updateValue).append("' AND ");
		}
		// 删除最后一个 AND
		updateSql.delete(updateSql.length() - 4, updateSql.length() - 1);
		return updateSql.toString();
	}

	/**
	 * 生成批量插入sql
	 * 
	 * @param tableInfo
	 * @param dataList  插入数据
	 * @return sqlList
	 */
	public static List<String> batchInsertSqlBuilder(TableInfoDto tableInfo, List<List<SaveOrUpdateDto>> dataList,
                                                     String dbType) {

		List<String> sqlList = new ArrayList<>();
		for (int k = 0; k < dataList.size(); k++) {

			StringBuilder insertSql = new StringBuilder();
			StringBuilder valuesSql = new StringBuilder();
			insertSql.append("insert into ");
			insertSql.append(tableInfo.getTableName()).append("(");

			for (int i = 0; i < dataList.get(k).size(); i++) {
				String filed = dataList.get(k).get(i).getKey();
				String value = dataList.get(k).get(i).getValue();
				// 插入字段
				insertSql.append(filed).append(",");
				if (!CheckParamUtil.isEmpty(value)) {
					// 插入字段值
					valuesSql.append("'").append(value).append("',");
				} else {
					valuesSql.append("NULL ,");
				}
			}
			// 追加固定字段
			insertSql.append(
					"update_key,delete_flg,create_date_time,create_user_id,update_date_time,update_user_id,id,uuid)");
			insertSql.append(" VALUES ( ");
			insertSql.append(valuesSql);
			// 添加固定字段插入值
			insertSql.append("0,'0',");
			Date nowtime = new Date();
			String keySequence = "";
			if (dbType.equals("oracle")) {
				keySequence = tableInfo.getTableName() + "_seq.nextval";
				// 时间转换格式
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdf.format(nowtime);
				insertSql.append(" to_date('").append(date).append("', 'yyyy-mm-dd hh24:mi:ss') ");
				insertSql.append(",'").append(tableInfo.getCreateUserId()).append("',");
				insertSql.append(" to_date('").append(date).append("', 'yyyy-mm-dd hh24:mi:ss') ");
				insertSql.append(",'").append(tableInfo.getUpdateUserId()).append("',");
			} else {
				keySequence = "nextval('" + tableInfo.getTableName() + "_seq')";
				insertSql.append("'").append(nowtime).append("','").append(tableInfo.getCreateUserId()).append("','");
				insertSql.append(nowtime).append("','").append(tableInfo.getUpdateUserId()).append("',");
			}
			insertSql.append(keySequence).append(",SYS_GUID()");
			insertSql.append(")");
			sqlList.add(insertSql.toString());
		}
		return sqlList;
	}

	/**
	 * 生成批量更新sql
	 * 
	 * @param tableInfo
	 * @param dataList     更新数据
	 * @param condtionList 更新条件
	 * @return sqllist
	 */
	public static List<String> batchUpdateSqlBuilder(TableInfoDto tableInfo, List<List<SaveOrUpdateDto>> dataList,
                                                     List<List<CondtionDto>> condtionList, String dbType) {
		List<String> sqlList = new ArrayList<>();
		// 添加插入字段
		for (int k = 0; k < dataList.size(); k++) {
			List<SaveOrUpdateDto> list = dataList.get(k);
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update ");
			updateSql.append(tableInfo.getTableName()).append(" set ");
			for (int i = 0; i < list.size(); i++) {
				String filed = list.get(i).getKey();
				String filedValue = list.get(i).getValue();
				updateSql.append(filed).append(" = ");
				if (!CheckParamUtil.isEmpty(filedValue)) {
					updateSql.append("'").append(filedValue).append("',");
				} else {
					updateSql.append("NULL ,");
				}
			}
			updateSql.append(" update_key = update_key + 1 ,");
			if (dbType.equals("oracle")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date = sdf.format(new Date());
				updateSql.append(" update_date_time = ").append(" to_date('").append(date)
						.append("', 'yyyy-mm-dd hh24:mi:ss') ");
			} else {
				updateSql.append(" update_date_time = '").append(new Date()).append("'");
			}
			updateSql.append(", update_user_id = '").append(tableInfo.getUpdateUserId());
			// 拼接where条件
			updateSql.append("' where ");
			List<CondtionDto> condtion = condtionList.get(k);
			for (int i = 0; i < condtion.size(); i++) {
				String updateKey = condtion.get(i).getKey();
				String updateValue = condtion.get(i).getValue();
				updateSql.append(updateKey).append(" = '");
				updateSql.append(updateValue).append("' AND ");
			}
			// 删除最后一个 AND
			updateSql.delete(updateSql.length() - 4, updateSql.length() - 1);
			sqlList.add(updateSql.toString());
		}
		return sqlList;
	}

	/**
	 * @param tableInfo
	 * @return
	 */
	public static String queryMaxSeqSqlBuilder(TableInfoDto tableInfo) {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select ");
		// 循环遍历每个filed
		for (int i = 0; i < tableInfo.getFileds().size(); i++) {
			String filed = tableInfo.getFileds().get(i);
			String aliasName = tableInfo.getAliasNames().get(i);
			selectSql.append(filed).append(" \"").append(aliasName).append("\"");
			selectSql.append(",");
		}
		// 删除最后一个 逗号
		selectSql.deleteCharAt(selectSql.length() - 1);
		// 拼接from
		selectSql.append(" from ").append(tableInfo.getTableName()).append(" ").append(tableInfo.getTableName());

		// 拼接where条件
		if (tableInfo.getCondtions() != null && tableInfo.getCondtions().size() > 0) {
			selectSql.append(" where ");
			for (CondtionDto condtion : tableInfo.getCondtions()) {
				selectSql.append(tableInfo.getTableName()).append(".");
				selectSql.append(condtion.getKey()).append("= '").append(condtion.getValue()).append("'");
			}
		}

		return selectSql.toString();
	}

	public static String queryStffSqlBuilder(TableInfoDto tableInfo) {

		StringBuilder selectSql = new StringBuilder();
		List<String> joinSqls = new ArrayList<String>();
		selectSql.append("select ");

		Map<String, String> tableNameMap = new HashMap<String, String>();
		// 循环遍历每个filed
		for (int i = 0; i < tableInfo.getFileds().size(); i++) {
			String filed = tableInfo.getFileds().get(i);
			String aliasName = tableInfo.getAliasNames().get(i);
			// 判断当前字段是否关联查询
			if (!CheckParamUtil.isEmpty(tableInfo.getRefKVs())) {
				RefKVDto ref = tableInfo.getRefKVs().get(i);
				if (ref.isJoin()) {
					String aliasTableName = ref.getTableName() + i;
					String joinSql = null;
					if (!CheckParamUtil.isEmpty(ref.getJoinTableName())) {
						// 如果关联表不为主表
						if (CheckParamUtil.isEmpty(tableNameMap.containsKey(ref.getJoinTableName()))) {
							joinSql = joinSqlBuilder(ref, ref.getJoinTableName(), filed, aliasTableName);
						} else {
							// 如果关联表已经关联主表，表明发生变化
							joinSql = joinSqlBuilder(ref, tableNameMap.get(ref.getJoinTableName()), filed,
									aliasTableName);
						}
					} else {
						joinSql = joinSqlBuilder(ref, tableInfo.getTableName(), filed, aliasTableName);
					}
					joinSqls.add(joinSql);
					// 拼接显示joinkey和查询项
					if (!ref.isFlg()) {
						selectSql.append(aliasTableName).append(".").append(ref.getJoinKey()).append("|| ':' ||");
					}
					selectSql.append(aliasTableName).append(".").append(ref.getRefValue());
					selectSql.append(" \"").append(aliasName).append("\"");
					tableNameMap.put(ref.getTableName(), aliasTableName);
				} else {
					selectSql.append(tableInfo.getTableName()).append(".");
					selectSql.append(filed).append(" \"").append(aliasName).append("\"");
				}
			} else {
				selectSql.append(tableInfo.getTableName()).append(".");
				selectSql.append(filed).append(" \"").append(aliasName).append("\"");
			}
			selectSql.append(",");
		}
		// 删除最后一个 逗号
		selectSql.deleteCharAt(selectSql.length() - 1);
		// 拼接from
		selectSql.append(" from ").append(tableInfo.getTableName()).append(" ").append(tableInfo.getTableName());
		// 拼接join
		if (joinSqls != null && joinSqls.size() > 0) {
			for (String joinsql : joinSqls) {
				selectSql.append(joinsql);
			}
		}

		// 拼接where条件
		selectSql.append(" where ");
		selectSql.append(tableInfo.getTableName()).append(".");
		selectSql.append("delete_flg = '0' ");
		if (!CheckParamUtil.isEmpty(tableInfo.getQueryCondtions())) {
			selectSql.append(" and ").append(tableInfo.getQueryCondtions());
		}
		selectSql.append(andSqlBuilder(tableInfo.getCondtions(), tableInfo.getTableName()));
		if (tableInfo.getSort() != null) {
			selectSql.append(" order by ");
			String[] sort = tableInfo.getSort().split(",");
			for (int i = 0; i < sort.length; i++) {
				selectSql.append(tableInfo.getTableName()).append(".");
				selectSql.append(sort[i]).append(",");
				;
			}
			// 删除最后一个 逗号
			selectSql.deleteCharAt(selectSql.length() - 1);
		}

		return selectSql.toString();
	}
}
