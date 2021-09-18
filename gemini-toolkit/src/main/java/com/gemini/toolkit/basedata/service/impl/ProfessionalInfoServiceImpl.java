package com.gemini.toolkit.basedata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.login.form.UserInfo;
import com.gemini.toolkit.basedata.service.ProfessionalInfoService;
import com.gemini.toolkit.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProfessionalInfoServiceImpl extends AbsDataImport implements ProfessionalInfoService {

	@Autowired
	private MessageSource messageSource;

	@Override
	public Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook) {
		log.info(".=========> 专业信息  前处理");
		// 在此处根据不同模板做不同的check

		Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap = new HashMap<String, Map<Integer, List<CheckErrorDto>>>();

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		// 遍历所有要导出数据的sheet,判断db中是否有重复数据
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// 存放pk的map
			Map<String, Integer> pkMap = new HashMap<String, Integer>();
			// 获取要出力的sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);

			Map<Integer, List<CheckErrorDto>> checkResultMap = new HashMap<Integer, List<CheckErrorDto>>();
			List<CheckErrorDto> checkErrorDtoList = new ArrayList<CheckErrorDto>();

			// 获取是否导入
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				int n = 0;

				// 获取需要遍历的最后一列列数
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELLFLG.equals(cell)) {
						lastCellNum = i;
					}
				}

				// 获取要导入的表名称
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
				// 构建tableinfo
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);

				// 遍历所有行
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					CheckErrorDto checkErrorDto = new CheckErrorDto();
					List<CondtionDto> condtionList = new ArrayList<>();
					List<String> fileds = new ArrayList<>();
					List<String> aliasNames = new ArrayList<>();
					String staffName = null;
					String mobilePhone = null;
					String pkValue = "";
					// 获取插入或更新的数据行
					Row row = sheet.getRow(i);

					// 获取抽出项
					String tableType = ExcelUtil.getcell(sheet.getRow(1).getCell(2));
					table.setQueryCondtions(tableType);

					// 设置错误信息
					String errorMessages = null;
					// 获取区分:(0:无更新，1:更新，2:增加)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.INSERT.equals(updateFlg) || CommonConsts.UPDATE.equals(updateFlg)) {
						for (int m = 2; m < lastCellNum; m++) {

							// 表字段名
							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));
							// 字段值
							String filedValue = ExcelUtil.getcell(row.getCell(m));
							// 如果有引用sheet
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
								if (!CheckParamUtil.isEmpty(filedValue)) {
									String[] Value = filedValue.split("\\:");
									filedValue = Value[0];
								}
							}
							// 表字段中文名
							String aliasName = ExcelUtil.getcell(sheet.getRow(15).getCell(m));

							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))) {

								// 获取确定staffId的唯一性主key
								String tmp = ExcelUtil.getcell(sheet.getRow(15).getCell(m));
								if (CommonConsts.SPECIALTY_LEADER.equals(tmp)) {
									staffName = ExcelUtil.getcell(row.getCell(m));
									if (!CheckParamUtil.isEmpty(staffName)) {
										pkValue = pkValue + staffName;
									}
								} else if (CommonConsts.SPECIALTY_LEADER_TEL.equals(tmp)) {
									mobilePhone = ExcelUtil.getcell(row.getCell(m));
									if (!CheckParamUtil.isEmpty(mobilePhone)) {
										pkValue = pkValue + mobilePhone;
									}
								}

								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {
									if (!CommonConsts.SPECIALTY_LEADER.equals(tmp)
											&& !CommonConsts.SPECIALTY_LEADER_TEL.equals(tmp)) {
										// 获取除了手机号和姓名以外的pk
										// 设置查询项
										CondtionDto condtionCode = new CondtionDto();
										condtionCode.setKey(filed);
										condtionCode.setValue(filedValue);
										condtionList.add(condtionCode);
										fileds.add(filed);
										// 表字段中文名
										aliasNames.add(tmp);
										pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
									}
								}
							} else {
								// 当有pk时,获取pk
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {

									// 设置查询项
									CondtionDto condtionCode = new CondtionDto();
									condtionCode.setKey(filed);
									condtionCode.setValue(filedValue);
									condtionList.add(condtionCode);
									fileds.add(filed);
									aliasNames.add(aliasName);
									pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
								}
							}
						}
						if (CheckParamUtil.isEmpty(errorMessages)) {

							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))) {
								if (!CheckParamUtil.isEmpty(staffName) || !CheckParamUtil.isEmpty(mobilePhone)) {
									// 先查询出staffId
									String staffId = customizeMapper.getStaffId(staffName, mobilePhone);

									if (CheckParamUtil.isEmpty(staffId)) {
										errorMessages = messageSource.getMessage("basedata.common.dataStaffError", null,
												LocaleContextHolder.getLocale());
										checkErrorDto.setErrorRow(i);
										checkErrorDto.setErrorMessage(errorMessages);
										checkErrorDtoList.add(checkErrorDto);
										continue;
									}
								}
							}

							// 导入表有pk去检索db
							table.setFileds(fileds);
							table.setAliasNames(aliasNames);
							table.setCondtions(condtionList);
							String querysql = SqlBuilderHelper.querySqlBuilder(table);
							// 根据pk查询DB
							dataList = customizeMapper.getResults(querysql);

							if (CommonConsts.INSERT.equals(updateFlg)) {
								if (dataList.size() > 0 || pkMap.containsKey(pkValue)) {
									// db中该数据已存在,或者excel中已有相同pk的数据插入
									checkErrorDto.setErrorRow(i);
									errorMessages = messageSource.getMessage("basedata.common.dataRepeatError", null,
											LocaleContextHolder.getLocale());
									checkErrorDto.setErrorMessage(errorMessages);
									checkErrorDtoList.add(checkErrorDto);
								} else {
									// 没有重复数据
									n++;
									// 将所有pk放入map中
									pkMap.put(pkValue, i);
								}
							} else if (CommonConsts.UPDATE.equals(updateFlg)) {
								if (dataList.size() == 0 && !pkMap.containsKey(pkValue)) {
									// db中该数据不存在,并且excel中没有有相同pk的数据插入
									checkErrorDto.setErrorRow(i);
									errorMessages = messageSource.getMessage("basedata.common.dataNotExistError", null,
											LocaleContextHolder.getLocale());
									checkErrorDto.setErrorMessage(errorMessages);
									checkErrorDtoList.add(checkErrorDto);
								} else {
									// db中没有重复数据
									n++;
									// 将所有pk放入map中
									pkMap.put(pkValue, i);
								}
							}
						}
					}
				}
				if (checkErrorDtoList.size() > 0) {
					// 获取所有异常信息
					checkResultMap.put(n, checkErrorDtoList);
					errorSheetMap.put(sheet.getSheetName(), checkResultMap);
				}
			}
		}
		return errorSheetMap;
	}

	@Override
	public void afterImportData(HSSFWorkbook wookbook, String templateType, String dbType) {
		log.info(".=========> 专业信息  后处理");
		// 在此处根据不同的模板做特定处理
		// 获取登录者id
		UserInfo user = GetUserInfo.getUserInfo();

		String[] sheetName = null;
		// 获取要特殊处理得sheet名
		// 专业信息
		sheetName = CommonConsts.PROFESSIONAL_INFOSHEET.split("\\,");
		for (int k = 0; k < sheetName.length; k++) {
			// 获取要出力的sheet
			HSSFSheet sheet = wookbook.getSheet(sheetName[k]);

			// 获取是否导入
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				// 获取要导入的表名称
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));

				// 获取需要遍历的最后一列列数
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELLFLG.equals(cell)) {
						lastCellNum = i;
					}
				}

				// 构建tableinfo
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);
				List<List<SaveOrUpdateDto>> updateDataList = new ArrayList<>();
				List<List<SaveOrUpdateDto>> insertDataList = new ArrayList<>();
				List<List<CondtionDto>> condtionListForUd = new ArrayList<>();
				Map<String, String> cellValueMap = new HashMap<String, String>();
				// 遍历所有行
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					List<SaveOrUpdateDto> saveOrUpdateList = new ArrayList<>();
					String staffName = null;
					String mobilePhone = null;

					// 获取插入或更新的数据行
					Row row = sheet.getRow(i);

					// 获取更新的where条件
					List<CondtionDto> condtionDtoList = new ArrayList<>();
					// 获取区分:(0:无更新，1:更新，2:增加)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {
						for (int m = 2; m < lastCellNum; m++) {
							// 循环所有列
							SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));

							// 获取单元格的值
							String filedsValue = ExcelUtil.getcell(row.getCell(m));
							if (!CheckParamUtil.isEmpty(filedsValue)) {
								// 如果单元格有引用sheet
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
									if (!CheckParamUtil.isEmpty(filedsValue)) {
										String[] Value = filedsValue.split("\\:");
										filedsValue = Value[0];
									}
								}
							}

							// 获取确定staffId的唯一性主key
							String tmp = ExcelUtil.getcell(sheet.getRow(15).getCell(m));
							// 设置姓名，手机号
							if (CommonConsts.SPECIALTY_LEADER.equals(tmp)) {
								staffName = ExcelUtil.getcell(row.getCell(m));
							} else if (CommonConsts.SPECIALTY_LEADER_TEL.equals(tmp)) {
								mobilePhone = ExcelUtil.getcell(row.getCell(m));
							}

							// 存放所有字段名和值
							cellValueMap.put(filed, filedsValue);

							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(m)))) {
								if (!CommonConsts.ID.equals(filed)) {
									// 设置插入或更新的值
									saveOrUpdateDto.setKey(ExcelUtil.getcell(sheet.getRow(5).getCell(m)));
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
								}
							}

							// 更新的数据才需要使用pk
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(m)))) {
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))
										|| CommonConsts.ID.equals(filed)) {
									// 设置更新查询项
									CondtionDto condtion = new CondtionDto();
									condtion.setKey(filed);
									condtion.setValue(filedsValue);
									condtionDtoList.add(condtion);
								}
							}
						}

						// 获取staffId
						String staffId = null;
						if (!CheckParamUtil.isEmpty(staffName) && !CheckParamUtil.isEmpty(mobilePhone)) {
							staffId = customizeMapper.getStaffId(staffName, mobilePhone);
						}

						CondtionDto condtionDto = new CondtionDto();
						// 获取更新的值
						SaveOrUpdateDto saveStaff = new SaveOrUpdateDto();

						// 取默认插入值
						String insertCondtions = ExcelUtil.getcell(sheet.getRow(2).getCell(2));
						if (CommonConsts.UPDATE.equals(updateFlg)) {
							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// 获取默认插入值，设置where条件
								JSONObject dataJson = JSONObject.parseObject(insertCondtions);
								JSONArray jsonArray = JSONArray.parseArray(dataJson.getString("insertCondtions"));
								for (int m = 0; m < jsonArray.size(); m++) {
									JSONObject object = jsonArray.getJSONObject(m);
									for (String key : object.keySet()) {
										condtionDto = new CondtionDto();
										condtionDto.setKey(key);
										condtionDto.setValue(object.getString(key));
										condtionDtoList.add(condtionDto);
									}
								}
							}

							table.setUpdateUserId(user.getStaffId());
							// 设置更新信息
							// 设置专业负责人名,专业负责人Id
							saveStaff = new SaveOrUpdateDto();
							saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_NAME);
							saveStaff.setValue(staffName);
							saveOrUpdateList.add(saveStaff);
							saveStaff = new SaveOrUpdateDto();
							saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_ID);
							saveStaff.setValue(staffId);
							saveOrUpdateList.add(saveStaff);
							condtionListForUd.add(condtionDtoList);
							// 设置更新项
							updateDataList.add(saveOrUpdateList);
						} else if (CommonConsts.INSERT.equals(updateFlg)) {
							table.setCreateUserId(user.getStaffId());
							table.setUpdateUserId(user.getStaffId());
							// 设置专业负责人名,专业负责人Id
							saveStaff = new SaveOrUpdateDto();
							saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_NAME);
							saveStaff.setValue(staffName);
							saveOrUpdateList.add(saveStaff);
							saveStaff = new SaveOrUpdateDto();
							saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_ID);
							saveStaff.setValue(staffId);
							saveOrUpdateList.add(saveStaff);

							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// 获取默认插入值，设置插入项
								JSONObject dataJson = JSONObject.parseObject(insertCondtions);
								JSONArray jsonArray = JSONArray.parseArray(dataJson.getString("insertCondtions"));
								for (int m = 0; m < jsonArray.size(); m++) {
									JSONObject object = jsonArray.getJSONObject(m);
									for (String key : object.keySet()) {
										SaveOrUpdateDto insertDto = new SaveOrUpdateDto();
										insertDto.setKey(key);
										insertDto.setValue(object.getString(key));
										saveOrUpdateList.add(insertDto);
									}
								}
							}
							// 设置插入值
							insertDataList.add(saveOrUpdateList);
						}
					}
				}
				// TODO 更新 或者 新增数据
				if (insertDataList.size() > 0) {
					List<String> insertSql = SqlBuilderHelper.batchInsertSqlBuilder(table, insertDataList, dbType);
					if ("postgresql".equals(dbType)) {
						customizeMapper.bacthInsert(insertSql);
					} else {
						for (String sqlString : insertSql) {
							customizeMapper.bacthInsertOracle(sqlString);
						}
					}
				}
				if (updateDataList.size() > 0) {
					List<String> updateSql = SqlBuilderHelper.batchUpdateSqlBuilder(table, updateDataList,
							condtionListForUd, dbType);
					if ("postgresql".equals(dbType)) {
						customizeMapper.bacthUpdate(updateSql);
					} else {
						for (String sqlString : updateSql) {
							customizeMapper.bacthUpdateOracle(sqlString);
						}
					}
				}
			}
		}
	}

}
