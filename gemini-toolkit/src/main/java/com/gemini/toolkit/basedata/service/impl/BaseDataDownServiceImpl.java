package com.gemini.toolkit.basedata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.RefKVDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.basedata.mapper.CustomizeMapper;
import com.gemini.toolkit.common.utils.CheckParamUtil;
import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.common.utils.ExcelUtil;
import com.gemini.toolkit.common.utils.SqlBuilderHelper;
import com.gemini.toolkit.basedata.service.BaseDataDownService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BaseDataDownServiceImpl implements BaseDataDownService {

	@Autowired
    CustomizeMapper customizeMapper;

	/**
	 * @param sheetNameList
	 * @param workBook
	 * @return
	 */
	public List<String> sheetCheck(HSSFWorkbook workBook) {
		List<String> sheetNameList = new ArrayList<String>();
		// 遍历所有要导出数据的sheet
		for (int k = 0; k < workBook.getNumberOfSheets() - 3; k++) {
			// 获取要出力的sheet
			HSSFSheet sheet = workBook.getSheetAt(k + 1);
			String tableType = ExcelUtil.getcell(sheet.getRow(1).getCell(2));
			if (!CheckParamUtil.isEmpty(tableType)) {
				sheetNameList.add(sheet.getSheetName());
			}
		}
		return sheetNameList;
	}

	public void downData(List<String> sheetNameList, HSSFWorkbook workBook) {

		for (String sheetName : sheetNameList) {

			if (CommonConsts.USER_SHEETNAME.equals(sheetName) || CommonConsts.SPECIALTY_SHEETNAME.equals(sheetName)
					|| CommonConsts.USERROLE_SHEETNAME.equals(sheetName)
					|| CommonConsts.ORGANIZERS_STAFF_SHEETNAME.equals(sheetName)) {
				continue;
			}
			HSSFSheet sheet = workBook.getSheet(sheetName);
			TableInfoDto table = new TableInfoDto();

			// 获取抽出条件
			String tableType = ExcelUtil.getcell(sheet.getRow(1).getCell(2));
			table.setQueryCondtions(tableType);

			// 获取要导出的表名称
			String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
			table.setTableName(tableName);

			// 获取排序条件
			String sort = ExcelUtil.getcell(sheet.getRow(3).getCell(2));
			table.setSort(sort);

			// 获取引用sheet行数据
			Row refSheetRow = sheet.getRow(6);

			// 获取需要遍历的最后一列列数
			int lastCellNum = 0;
			for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
				String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
				if (CommonConsts.LASTCELL.equals(cell)) {
					lastCellNum = i;
				}
			}

			// 获取所有引用sheet信息
			List<String> fileds = new ArrayList<>();
			List<String> aliasNames = new ArrayList<>();
			List<RefKVDto> refs = new ArrayList<>();
			for (int i = 2; i < lastCellNum; i++) {
				if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(i)))) {
					// 表字段名
					fileds.add(ExcelUtil.getcell(sheet.getRow(5).getCell(i)));
					// 表字段中文名
					aliasNames.add(ExcelUtil.getcell(sheet.getRow(15).getCell(i)));

					RefKVDto dto = new RefKVDto();
					if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(refSheetRow.getCell(i)))) {
						// 引用的sheet名称
						dto.setRefSheetName(ExcelUtil.getcell(refSheetRow.getCell(i)));
						// 引用的join关系
						String joinsString = ExcelUtil.getcell(sheet.getRow(7).getCell(i));
						if (!CheckParamUtil.isEmpty(joinsString)) {
							JSONObject dataJson = JSONObject.parseObject(joinsString);
							// join表名称
							dto.setTableName(dataJson.getString("table"));
							// join表key
							dto.setJoinKey(dataJson.getString("key"));
							// 查询项目
							dto.setRefValue(dataJson.getString("value"));
							// join
							dto.setJoin(dataJson.getString("joinType"));
							JSONArray jsonArray = JSONArray.parseArray(dataJson.getString("queryCondtions"));
							List<CondtionDto> condtions = new ArrayList<CondtionDto>();
							for (int m = 0; m < jsonArray.size(); m++) {
								JSONObject object = jsonArray.getJSONObject(m);
								for (String key : object.keySet()) {
									CondtionDto condtionDto = new CondtionDto();
									condtionDto.setKey(key);
									condtionDto.setValue(object.getString(key));
									condtions.add(condtionDto);
									dto.setCondtions(condtions);
								}
							}
							if (CommonConsts.T_SPONS.equals(ExcelUtil.UpperString(dto.getTableName()))) {
								dto.setFlg(true);
							}
						}
					}
					refs.add(dto);
				}
			}
			if (CommonConsts.ORGANIZERS_SHEETNAME.equals(sheetName)) {
				// 查询上级科室名称
				RefKVDto staffDto = new RefKVDto();
				staffDto.setRefSheetName("上级科室");
				staffDto.setTableName(CommonConsts.T_ORGANIZE);
				staffDto.setJoinKey(CommonConsts.ORGANIZE_ID);
				staffDto.setRefValue(CommonConsts.ORGANIZE_NAME);
				staffDto.setJoin("left join");
				// 查询出的项目不拼接显示
				staffDto.setFlg(true);
				refs.add(staffDto);
				fileds.add(CommonConsts.ORGANIZE_PRNT_ID);
				aliasNames.add(CommonConsts.ORGANIZE_PRNT_NAME);
			}
			table.setFileds(fileds);
			table.setAliasNames(aliasNames);
			table.setRefKVs(refs);
			String querysql = SqlBuilderHelper.querySqlBuilder(table);

			// 写入数据
			writeData(querysql, sheet, lastCellNum, workBook);
		}
	}

	/**
	 *
	 */
	public void downafterData(List<String> sheetNameList, HSSFWorkbook workBook) {

		for (String sheetName : sheetNameList) {

			if (!CommonConsts.USER_SHEETNAME.equals(sheetName) && !CommonConsts.SPECIALTY_SHEETNAME.equals(sheetName)
					&& !CommonConsts.USERROLE_SHEETNAME.equals(sheetName)
					&& !CommonConsts.ORGANIZERS_STAFF_SHEETNAME.equals(sheetName)) {
				continue;
			}
			HSSFSheet sheet = workBook.getSheet(sheetName);
			TableInfoDto table = new TableInfoDto();

			// 获取抽出条件
			String tableType = ExcelUtil.getcell(sheet.getRow(1).getCell(2));
			table.setQueryCondtions(tableType);

			// 获取要导出的表名称
			String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
			table.setTableName(tableName);

			// 获取排序条件
			String sort = ExcelUtil.getcell(sheet.getRow(3).getCell(2));
			table.setSort(sort);

			// 获取引用sheet行数据
			Row refSheetRow = sheet.getRow(6);

			// 获取需要遍历的最后一列列数
			int lastCellNum = 0;
			for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
				String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
				if (CommonConsts.LASTCELL.equals(cell)) {
					lastCellNum = i;
				}
			}

			// 获取所有引用sheet信息
			List<String> fileds = new ArrayList<>();
			List<String> aliasNames = new ArrayList<>();
			List<RefKVDto> refs = new ArrayList<>();
			for (int i = 2; i < lastCellNum; i++) {
				if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(i)))) {
					// 表字段名
					fileds.add(ExcelUtil.getcell(sheet.getRow(5).getCell(i)));
					// 表字段中文名
					aliasNames.add(ExcelUtil.getcell(sheet.getRow(15).getCell(i)));
				}
				RefKVDto dto = new RefKVDto();
				if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(refSheetRow.getCell(i)))) {
					// 引用的sheet名称
					dto.setRefSheetName(ExcelUtil.getcell(refSheetRow.getCell(i)));
					// 引用的join关系
					String joinsString = ExcelUtil.getcell(sheet.getRow(7).getCell(i));
					if (!CheckParamUtil.isEmpty(joinsString)) {
						JSONObject dataJson = JSONObject.parseObject(joinsString);
						// join表名称
						dto.setTableName(dataJson.getString("table"));
						// join表key
						dto.setJoinKey(dataJson.getString("key"));
						// 查询项目
						dto.setRefValue(dataJson.getString("value"));
						// join
						dto.setJoin(dataJson.getString("joinType"));
						JSONArray jsonArray = JSONArray.parseArray(dataJson.getString("queryCondtions"));
						List<CondtionDto> condtions = new ArrayList<CondtionDto>();
						for (int m = 0; m < jsonArray.size(); m++) {
							JSONObject object = jsonArray.getJSONObject(m);
							for (String key : object.keySet()) {
								CondtionDto condtionDto = new CondtionDto();
								condtionDto.setKey(key);
								condtionDto.setValue(object.getString(key));
								condtions.add(condtionDto);
								dto.setCondtions(condtions);
							}
						}
					}
				}
				if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(i)))) {
					refs.add(dto);
				}
			}

			if (CommonConsts.USERROLE_SHEETNAME.equals(sheetName)) {
				// 用户权限sheet，需要关联user表，查询出staffid
				RefKVDto staffDto = new RefKVDto();
				staffDto.setRefSheetName(CommonConsts.USER_SHEETNAME);
				staffDto.setTableName(CommonConsts.T_USER);
				// join表key
				staffDto.setJoinKey(CommonConsts.USER_ID);
				// 查询项目
				staffDto.setRefValue(CommonConsts.STAFF_ID);
				// join
				staffDto.setJoin("left join");
				refs.add(staffDto);

				fileds.add(CommonConsts.USER_ID);
				aliasNames.add(CommonConsts.USER_ID);
			}

			if (CommonConsts.USER_SHEETNAME.equals(sheetName)) {
				RefKVDto dto = new RefKVDto();
				// 用户sheet，查询用户ID
				fileds.add(CommonConsts.USER_ID);
				aliasNames.add(CommonConsts.RULEUSERID);
				refs.add(dto);
				// 用户sheet，查询工号
				RefKVDto staffDto = new RefKVDto();
				staffDto.setRefSheetName("人员");
				staffDto.setTableName(CommonConsts.T_STAFF);
				// join表key
				staffDto.setJoinKey(CommonConsts.STAFF_ID);
				// 查询项目
				staffDto.setRefValue("preparation1");
				// join
				staffDto.setJoin("left join");
				// 查询出的项目不拼接显示
				staffDto.setFlg(true);
				refs.add(staffDto);

				fileds.add(CommonConsts.STAFF_ID);
				aliasNames.add(CommonConsts.RULEJOB);
			}

			if (CommonConsts.USER_SHEETNAME.equals(sheetName) || CommonConsts.SPECIALTY_SHEETNAME.equals(sheetName)
					|| CommonConsts.USERROLE_SHEETNAME.equals(sheetName)
					|| CommonConsts.ORGANIZERS_STAFF_SHEETNAME.equals(sheetName)) {
				// 用户权限sheet，需要关联staff表，查询出人员姓名和人员手机号
				// 查询人员姓名
				RefKVDto staffDto = new RefKVDto();
				staffDto.setRefSheetName("人员");
				staffDto.setTableName(CommonConsts.T_STAFF);
				// join表key
				staffDto.setJoinKey(CommonConsts.STAFF_ID);
				// 查询项目
				staffDto.setRefValue(CommonConsts.STAFF_NAME);
				// join
				staffDto.setJoin("left join");
				// 查询出的项目不拼接显示
				staffDto.setFlg(true);
				if (CommonConsts.USERROLE_SHEETNAME.equals(sheetName)) {
					// 用户权限表关联user表，通过user表关联staff表
					staffDto.setJoinTableName(CommonConsts.T_USER);
				}
				refs.add(staffDto);

				// 设置查询项及显示名
				if (CommonConsts.SPECIALTY_SHEETNAME.equals(sheetName)) {
					fileds.add(CommonConsts.SPECIALTY_LEADER_ID);
					aliasNames.add(CommonConsts.SPECIALTY_LEADER);
				} else {
					fileds.add(CommonConsts.STAFF_ID);
					aliasNames.add(CommonConsts.STAFFNAME);
				}

				// 设置手机号
				staffDto = new RefKVDto();
				staffDto.setRefSheetName("人员");
				staffDto.setTableName(CommonConsts.T_STAFF);
				// join表key
				staffDto.setJoinKey(CommonConsts.STAFF_ID);
				// 查询项目
				staffDto.setRefValue(CommonConsts.MOBILE_PHONE);
				// join
				staffDto.setJoin("left join");
				// 查询出的项目不拼接显示
				staffDto.setFlg(true);
				if (CommonConsts.USERROLE_SHEETNAME.equals(sheetName)) {
					// 用户权限表关联user表，通过user表关联staff表
					staffDto.setJoinTableName(CommonConsts.T_USER);
				}
				refs.add(staffDto);
				if (CommonConsts.SPECIALTY_SHEETNAME.equals(sheetName)) {
					fileds.add(CommonConsts.SPECIALTY_LEADER_ID);
					aliasNames.add(CommonConsts.SPECIALTY_LEADER_TEL);
				} else {
					fileds.add(CommonConsts.STAFF_ID);
					aliasNames.add(CommonConsts.RULETEL);
				}
			}

			table.setFileds(fileds);
			table.setAliasNames(aliasNames);
			table.setRefKVs(refs);
			String querysql = SqlBuilderHelper.queryStffSqlBuilder(table);

			// 写入数据
			writeData(querysql, sheet, lastCellNum, workBook);
		}
	}

	/**
	 * @param querysql
	 * @param sheet
	 * @param lastCellNum
	 * @param workBook
	 */
	public void writeData(String querysql, HSSFSheet sheet, int lastCellNum, HSSFWorkbook workBook) {
		// 查询db
		List<Map<String, Object>> dataList = customizeMapper.getResults(querysql);
		// 导出数据
		for (int i = 0; i < dataList.size(); i++) {
			// 创建表格行
			ExcelUtil.copyRows(16, 16, i + 16, sheet);
			HSSFRow newRow = sheet.getRow(i + 16);
			// 第一列设置为0:无更新
			newRow.getCell(0).setCellValue(CommonConsts.UNDO);
			// 设置区分列的下拉框
			String[] distinguish = new String[] { CommonConsts.UNDO, CommonConsts.UPDATE, CommonConsts.INSERT };
			DVConstraint dVConstraint = DVConstraint.createExplicitListConstraint(distinguish);
			CellRangeAddressList rangeAddress = new CellRangeAddressList(i + 16, i + 16, 0, 0);
			DataValidation dataValidation = new HSSFDataValidation(rangeAddress, dVConstraint);
			sheet.addValidationData(dataValidation);

			// 设置No
			newRow.getCell(1).setCellValue(i + 1);
			// 获取要输出的一行数据
			Map<String, Object> staff = dataList.get(i);

			for (int m = 2; m < lastCellNum; m++) {
				Cell cell = newRow.getCell(m);
				// 获取单元格内容
				String cellValue = null;
				// 获取字段名
				String filedName = ExcelUtil.getcell(sheet.getRow(15).getCell(m));
				if (!CheckParamUtil.isEmpty(staff.get(filedName))) {
					cellValue = staff.get(filedName).toString();
				}
				// 设置单元格的值
				if (!":".equals(cellValue)) {
					cell.setCellValue(cellValue);
				}
				String refSheetName = ExcelUtil.getcell(sheet.getRow(6).getCell(m));
				// 当有引用sheet时，设置下拉框的值
				if (!CheckParamUtil.isEmpty(refSheetName)) {
					DVConstraint formula = DVConstraint.createFormulaListConstraint(refSheetName);
					CellRangeAddressList rangeAddressList = new CellRangeAddressList(i + 16, i + 16, m, m);
					DataValidation provinceDataValidation = new HSSFDataValidation(rangeAddressList, formula);
					sheet.addValidationData(provinceDataValidation);
				}

			}
		}
		for (int m = 0; m < 1000; m++) {
			// 设置最后一行公式
			if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(0).getCell(0)))) {
				int rowNum = m + 17;
				// 设置最后一行的公式
				Cell lastCell = null;
				HSSFRow row = sheet.getRow(m + 16);
				if (!CheckParamUtil.isEmpty(row)) {
					lastCell = row.createCell(lastCellNum);
				} else {
					row = sheet.createRow(m + 16);
					lastCell = row.createCell(lastCellNum);
				}

				String lastCellValue = null;
				if (CommonConsts.SPONS_SHEETNAME.equals(sheet.getSheetName())) {
					lastCellValue = "IF(AND(C" + rowNum + "<>\"\"),C" + rowNum + ",\"\")";
				} else {
					lastCellValue = "IF(AND(C" + rowNum + "<>\"\"),C" + rowNum + "&\":\"&D" + rowNum + ",\"\")";
				}
				lastCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
				lastCell.setCellFormula(lastCellValue);
			}
		}

		// 判断当前sheet是否有名称管理器
		if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(0).getCell(0)))) {
			// 获取与当前sheet名相同得名称管理器
			Name name = workBook.getName(sheet.getSheetName());
			// 写入当前查出得所有数据
			String range = ExcelUtil.getRange(lastCellNum, 17, dataList.size() + 16, 1);
			String formula = sheet.getSheetName() + "!" + range;
			name.setRefersToFormula(formula);
		}
	}
}
