package com.gemini.toolkit.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.gemini.toolkit.enums.SqlType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gemini.toolkit.devops.xml.model.Param;
import com.gemini.toolkit.devops.xml.model.Sql;
import com.gemini.toolkit.devops.xml.model.Step;

/**
 * 运维工具集 工具类
 * @author jintg
 *
 */
@Component
public class DevopsUtils {

	
	@Autowired
	private SqlParse sqlParse;
	
	@Value("${db.type}")
	String dbType;
	/**
	 * 生成sqllist
	 * 
	 * @param sqlList
	 * @return
	 */
	public   List<String> getExecSqls(List<Sql> sqlList) {
		// 生成执行sql
		List<String> execSqls = new ArrayList<String>();
		// 按order排序
		sqlList.sort(Comparator.comparing(Sql::getOrder));

		// 解析xml的sql
		for (Sql sql : sqlList) {
			// 除去 当前环境数据库类型不同的sql
			if (!sql.getDbtype().isEmpty() && !StringUtils.equals(dbType, sql.getDbtype())) {
				continue;
			}
			// xml中定义的sql脚本，用冒号分隔
			String[] sqlSplit = sql.getContent().trim().split(";");
			for (int i = 0; i < sqlSplit.length; i++) {
				if (StringUtils.isNotEmpty(sqlSplit[i])) {
					execSqls.add(sqlSplit[i]);
				}
			}
		}

		return execSqls;
	}
	
	/**
	 * 根据xml定义重新整理输入参数格式
	 * 
	 * @param inputParams
	 *            页面输入的参数
	 * @param xmlParam
	 *            xml中定义的参数
	 * @return
	 */
	public  Map<String, Object> parseParam(Map<String, Object> inputParams, List<Param> paramList) {

		// List<Param> paramList = xmlParam.getParam();
		Map<String, Param> paramMap = new HashMap<>();

		Map<String, Object> res = new HashMap<>();

		// 为了方便嵌套循环，把paramlist转成map
		paramList.stream().forEach(p -> {
			paramMap.put(p.getKey(), p);
		});

		for (Map.Entry<String, Object> entry : inputParams.entrySet()) {
			// 判断xmlparam中是否存在
			if (paramMap.containsKey(entry.getKey())) {
				Param param = paramMap.get(entry.getKey());
				// 判断是否是in的参数,将传入的value按逗号分隔添加单引号
				if (param.isMulti()) {
					String inValue = (String) entry.getValue();
					String[] splitValue = inValue.split(",");
					// 数组转list
					List<String> inValueList = Arrays.asList(splitValue);
					String addQutes = "";
					// 判断一下参数的类型
					String type = param.getType();
					// 如果参数的类型时数字,不需要加单引号
					if (StringUtils.equals("Integer", type)) {

						addQutes = inValueList.stream().map(s -> s.trim()).collect(Collectors.joining(","));
					} else {
						addQutes = inValueList.stream().map(s -> "'" + s.trim() + "'").collect(Collectors.joining(","));
					}

					// 重新更新
					res.put(entry.getKey(), addQutes);
				} else {
					res.put(entry.getKey(), entry.getValue().toString().trim());
				}

			} else {
				// TODO 报个错
			}

		}
		return res;
	}
	/**
	 * 逆向生成sql
	 * 
	 * @param sqlList
	 * @return
	 */
	public  Map<String, Map<String, String>> createBackupAndRollbackSql(Step step, Map<String, Object> inputParams,
			List<String> sqlList) {

		// back and rollback
		Map<String, Map<String, String>> res = new HashMap<>();
		Map<String, String> backupMap = new LinkedHashMap<>();
		Map<String, String> rollbackMap = new LinkedHashMap<>();
		List<Sql> sqls = null;
		// 自定义备份和回滚sqld时
		if (step.getBackupSqls() != null && step.getBackupSqls().getSql().size() > 0 && step.getRollbackSqls() != null
				&& step.getRollbackSqls().getSql().size() > 0) {
			Map<String, String> backupList = new LinkedHashMap<>();
			sqls = step.getBackupSqls().getSql();
			// 排序
			sqls.sort(Comparator.comparing(Sql::getOrder));
			sqls.stream()
					// 除去 当前环境数据库类型不同的sql
					.filter(s -> s.getDbtype().isEmpty() || StringUtils.equals(dbType, s.getDbtype())).forEach(s -> {
						// sql替换模板
						backupList.put(s.getName(), sqlParse.getParseSql(s.getContent().trim().split(";")[0], inputParams));
					});
			backupMap = backupList;

			// rollback
			Map<String, String> rollbackList = new LinkedHashMap<>();
			rollbackMap = new LinkedHashMap<>();
			sqls = step.getRollbackSqls().getSql();
			// 排序
			sqls.sort(Comparator.comparing(Sql::getOrder));
			sqls.stream()
					// 除去 当前环境数据库类型不同的sql
					.filter(s -> s.getDbtype().isEmpty() || StringUtils.equals(dbType, s.getDbtype())).forEach(s -> {
						rollbackList.put(s.getName(), sqlParse.getParseSql(s.getContent().trim().split(";")[0], inputParams));
					});
			rollbackMap = rollbackList;

		} else {

			Pattern pattern = null;
			Matcher matcher = null;

			for (String sql : sqlList) {

				String sqlType = getSqlType(sql);
				String backup = "";
				String rollback = "";
				// 更新
				if (StringUtils.equals(SqlType.UPDATE.getValue(), sqlType)) {

					pattern = Pattern.compile(Constants.UPDATE_REGX, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(sql);
					if (matcher.matches()) {
						// 逆向生成 select 和 update
						backup = "select * from " + matcher.group(2) + " " + matcher.group(5) + " " + matcher.group(6);
						// 回滚sql，根据备份数据的主键，逐条回滚
						rollback = "update " + matcher.group(2) + " set ${@sets@} where id = ${@id@}" ;

					}
				} else if (StringUtils.equals(SqlType.DELETE.getValue(), sqlType)) {
					pattern = Pattern.compile(Constants.DELETE_REGX, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(sql);
					if (matcher.matches()) {
						// 逆向生成 select 和 insert
						backup = "select * from " + matcher.group(2) + " where " + matcher.group(4);
						rollback = "insert into  " + matcher.group(2) + "(${@keys@}) values (${@values@})";
					}

				} else if (StringUtils.equals(SqlType.INSERT.getValue(), sqlType)) {
					pattern = Pattern.compile(Constants.INSERT_REGX, Pattern.CASE_INSENSITIVE);
					matcher = pattern.matcher(sql);
					if (matcher.matches()) {
						// 逆向生成 delete
						// 按uuid删除 由于id是根据序列号自增,所以暂定使用uuid当作删除条件
						rollback = "delete from  " + matcher.group(2) + " where uuid = '${uuid}'";
					}
				}

				// rollback和backup的key使用rollbacksql生成的md5值
				String md5Key = CreateMd5.getMd5(rollback);
				backupMap.put(md5Key, backup);
				rollbackMap.put(md5Key, rollback);
				if (StringUtils.equals(SqlType.INSERT.getValue(), sqlType)) {
					// insert的操作不需要backup
					backupMap.put(md5Key, "nobackup");

				}

			}
		}
		res.put(Constants.BACKUPLIST, backupMap);
		res.put(Constants.ROLLBACKLIST, rollbackMap);

		return res;

	}

	/**
	 * 获取sql类型
	 * 
	 * @param sql
	 * @return
	 */
	public  String getSqlType(String sql) {

		if (sql.trim().toLowerCase().startsWith(SqlType.SELECT.getValue())) {
			return SqlType.SELECT.getValue();
		} else if (sql.trim().toLowerCase().startsWith(SqlType.UPDATE.getValue())) {
			return SqlType.UPDATE.getValue();
		} else if (sql.trim().toLowerCase().startsWith(SqlType.DELETE.getValue())) {
			return SqlType.DELETE.getValue();
		} else if (sql.trim().toLowerCase().startsWith(SqlType.INSERT.getValue())) {
			return SqlType.INSERT.getValue();

		}
		return "";
	}



}
