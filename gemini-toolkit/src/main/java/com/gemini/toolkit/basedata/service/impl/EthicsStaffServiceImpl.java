package com.gemini.toolkit.basedata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.login.form.UserInfo;
import com.gemini.toolkit.basedata.service.EthicsStaffService;
import com.gemini.toolkit.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EthicsStaffServiceImpl extends AbsDataImport implements EthicsStaffService {
	private static final Logger log = LoggerFactory.getLogger(EthicsStaffServiceImpl.class);
	@Autowired
	private MessageSource messageSource;

	@Override
	public Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook) {
		log.info(".=========> 伦理人员  前处理");
		// 在此处根据不同模板做不同的check

		Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap = new HashMap<String, Map<Integer, List<CheckErrorDto>>>();

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Integer> staffMap = new HashMap<String, Integer>();
		Map<String, Integer> userMap = new HashMap<String, Integer>();
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

							if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))) {
								if (CommonConsts.ID.equals(filed) && CommonConsts.UPDATE.equals(updateFlg)) {
									if (CheckParamUtil.isEmpty(filedValue)) {
										errorMessages = messageSource.getMessage("basedata.common.idError", null,
												LocaleContextHolder.getLocale());
										checkErrorDto.setErrorRow(i);
										checkErrorDto.setErrorMessage(errorMessages);
										checkErrorDtoList.add(checkErrorDto);
										break;
									} else {
										CondtionDto condtionCode = new CondtionDto();
										condtionCode.setKey(filed);
										condtionCode.setValue(filedValue);
										condtionList.add(condtionCode);
									}
								}
							}

							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {

								// 获取确定staffId的唯一性主key
								String tmp = ExcelUtil.getcell(sheet.getRow(15).getCell(m));
								if (CommonConsts.STAFFNAME.equals(tmp)) {
									staffName = ExcelUtil.getcell(row.getCell(m));
									if (!CheckParamUtil.isEmpty(staffName)) {
										pkValue = pkValue + staffName;
									}
								} else if (CommonConsts.RULETEL.equals(tmp)) {
									mobilePhone = ExcelUtil.getcell(row.getCell(m));
									if (!CheckParamUtil.isEmpty(mobilePhone)) {
										pkValue = pkValue + mobilePhone;
									}
								}

								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {
									if (!CommonConsts.STAFFNAME.equals(tmp) && !CommonConsts.RULETEL.equals(tmp)) {
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
								} else if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))
										&& CommonConsts.ROLE_ID.equals(filed)) {
									if (CommonConsts.INSERT.equals(updateFlg)) {
										// 用户权限表，一个用户对应多个权限，权限也为查询项
										CondtionDto condtionCode = new CondtionDto();
										condtionCode.setKey(filed);
										condtionCode.setValue(filedValue);
										condtionList.add(condtionCode);
									}
									pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
								} else if (CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
										&& CommonConsts.ORGANIZE_ID.equals(filed)) {
									if (CommonConsts.INSERT.equals(updateFlg)) {
										// 组织人员表，一个用户可能属于多个组织，组织id也为查询项
										CondtionDto condtionCode = new CondtionDto();
										condtionCode.setKey(filed);
										condtionCode.setValue(filedValue);
										condtionList.add(condtionCode);
									}
									pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
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

									if (CommonConsts.T_STAFF.equals(ExcelUtil.UpperString(tableName))) {
										if (CommonConsts.STAFF_NAME.equals(filed)) {
											staffName = ExcelUtil.getcell(row.getCell(m));
										} else if (CommonConsts.MOBILE_PHONE.equals(filed)) {
											mobilePhone = ExcelUtil.getcell(row.getCell(m));
										}
									}
									pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
								}
							}
						}
						if (CheckParamUtil.isEmpty(errorMessages)) {

							if (CommonConsts.T_STAFF.equals(ExcelUtil.UpperString(tableName))) {
								if (CommonConsts.INSERT.equals(updateFlg)) {
									// 将人员表插入的人员名称和手机号存入map
									staffMap.put(staffName + mobilePhone, i);
								}
							}

							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))) {
								String staffId = null;
								if (!CheckParamUtil.isEmpty(staffName) || !CheckParamUtil.isEmpty(mobilePhone)) {
									// 先查询出staffId
									staffId = customizeMapper.getStaffId(staffName, mobilePhone);

									if (CheckParamUtil.isEmpty(staffId)) {
										// 判断是否为新插入的人员
										if (!staffMap.containsKey(staffName + mobilePhone)) {
											if (CommonConsts.INSERT.equals(updateFlg)) {
												errorMessages = messageSource.getMessage(
														"basedata.common.dataStaffError", null,
														LocaleContextHolder.getLocale());
											} else {
												errorMessages = messageSource.getMessage(
														"basedata.common.dataNotExistError", null,
														LocaleContextHolder.getLocale());
											}
											checkErrorDto.setErrorRow(i);
											checkErrorDto.setErrorMessage(errorMessages);
											checkErrorDtoList.add(checkErrorDto);
											continue;
										}
									}
									// 设置查询项
									CondtionDto staffIdCode = new CondtionDto();
									staffIdCode.setKey(CommonConsts.STAFF_ID);
									// 设置查询字段
									fileds.add(CommonConsts.STAFF_ID);
									aliasNames.add(CommonConsts.STAFF_ID);
									staffIdCode.setValue(staffId);
									condtionList.add(staffIdCode);

									if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
											&& CommonConsts.INSERT.equals(updateFlg)) {
										// 将用户表新插入的人员名称和手机号存入map
										userMap.put(staffName + mobilePhone, i);
									}
								}
							}

							if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
								// 先查询出userId
								String userId = customizeMapper.getUserId(mobilePhone, staffName);

								if (CheckParamUtil.isEmpty(userId)) {
									if (!userMap.containsKey(staffName + mobilePhone)) {
										// 判断是否为新插入的用户
										if (CommonConsts.INSERT.equals(updateFlg)) {
											errorMessages = messageSource.getMessage("basedata.common.dataStaffError",
													null, LocaleContextHolder.getLocale());
										} else {
											errorMessages = messageSource.getMessage(
													"basedata.common.dataNotExistError", null,
													LocaleContextHolder.getLocale());
										}
										checkErrorDto.setErrorRow(i);
										checkErrorDto.setErrorMessage(errorMessages);
										checkErrorDtoList.add(checkErrorDto);
										continue;
									}
								}
								// 设置查询项
								CondtionDto userIdCode = new CondtionDto();
								userIdCode.setKey(CommonConsts.USER_ID);
								userIdCode.setValue(userId);
								condtionList.add(userIdCode);
								// 设置查询字段
								fileds.add(CommonConsts.USER_ID);
								aliasNames.add(CommonConsts.USER_ID);
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
		log.info(".=========> 伦理人员  后处理");
		// 在此处根据不同的模板做特定处理
		// 获取登录者id
		UserInfo user = GetUserInfo.getUserInfo();

		// 获取要特殊处理得sheet名，组织人员
		String[] sheetName = CommonConsts.ETHICS_STAFFSHEET.split("\\,");
		for (int k = 0; k < sheetName.length; k++) {
			// 获取要出力的sheet
			HSSFSheet sheet = wookbook.getSheet(sheetName[k]);
			Map<String, String> userValueMap = new HashMap<String, String>();

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
				Map<String, String> seqMap = new HashMap<String, String>();
				String seq = null;
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
							if (CommonConsts.STAFFNAME.equals(tmp)) {
								staffName = ExcelUtil.getcell(row.getCell(m));
							} else if (CommonConsts.RULETEL.equals(tmp)) {
								mobilePhone = ExcelUtil.getcell(row.getCell(m));
							}

							// 存放所有字段名和值
							cellValueMap.put(filed, filedsValue);
							// 特殊处理的字段
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(13).getCell(m)))) {
								if (!CommonConsts.UPDATE.equals(updateFlg)
										&& CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// 当插入字段需要根据数据库取最大值加1时
									seq = getSeq(tableName, sheet.getRow(13).getCell(m), cellValueMap, seqMap);
									saveOrUpdateDto.setValue(seq);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateList.add(saveOrUpdateDto);

									// 设置seqMap
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String groupBy = dataJson.getString(CommonConsts.GROUPBY);
									// 将已经获取的seq放入map中，供下一行获取seq值+1
									if (!CheckParamUtil.isEmpty(groupBy)) {
										// 取值有条件，比如同一organize_id下，将organize_id设为key以便检索
										seqMap.put(cellValueMap.get(groupBy), seq);
									} else {
										seqMap.put(CommonConsts.TARGETKEY, seq);
									}
								}
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// 时间格式字段获取format格式
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String format = dataJson.getString(CommonConsts.FORMAT);
									String dateFormat = JSONObject.parseObject(format)
											.getString(CommonConsts.DATEFORAMT);
									// 获取单元格format后的值
									filedsValue = ExcelUtil.getDateCell(row.getCell(m), dateFormat);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
								}
							} else {
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(m)))) {
									if (!CommonConsts.ID.equals(filed)) {
										// 设置插入或更新的值
										saveOrUpdateDto.setKey(ExcelUtil.getcell(sheet.getRow(5).getCell(m)));
										saveOrUpdateDto.setValue(filedsValue);
										saveOrUpdateList.add(saveOrUpdateDto);
									}
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
							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// 将工号手机号等放入map
								userValueMap.put(ExcelUtil.getcell(sheet.getRow(15).getCell(m)), filedsValue);
							}
						}

						String userId = null;
						String userIdType = ExcelUtil.getcell(row.getCell(lastCellNum - 1));
						if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
							// 获取用户ID指定规则,匹配对应userId
							if (CommonConsts.RULETEL.equals(userIdType) || CommonConsts.RULEJOB.equals(userIdType)) {
								userId = userValueMap.get(userIdType);
							} else {
								userId = userValueMap.get(CommonConsts.RULEUSERID);
							}
						}

						// 获取staffId
						String staffId = null;
						if (!CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
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
							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// 更新用户表，设置userId
								if (!CheckParamUtil.isEmpty(userId)) {
									saveStaff.setKey(CommonConsts.USER_ID);
									saveStaff.setValue(userId);
									saveOrUpdateList.add(saveStaff);
								}
							}
							if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
								// 设置用户权限表登录信息

								condtionDto.setKey(CommonConsts.USER_ID);
								// 没有更新USERID，获取db中的USERID
								String oldUserId = customizeMapper.getUserId(mobilePhone, staffName);
								condtionDto.setValue(oldUserId);
								condtionDtoList.add(condtionDto);
								condtionListForUd.add(condtionDtoList);
							}
							if (CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// 设置更新语句where条件的staffId

								condtionDto.setKey(CommonConsts.STAFF_ID);
								condtionDto.setValue(staffId);
								condtionDtoList.add(condtionDto);
								condtionListForUd.add(condtionDtoList);
							}
							// 设置更新项
							updateDataList.add(saveOrUpdateList);
						} else if (CommonConsts.INSERT.equals(updateFlg)) {
							table.setCreateUserId(user.getStaffId());
							table.setUpdateUserId(user.getStaffId());
							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// 设置插入项的userId
								saveStaff.setKey(CommonConsts.USER_ID);
								saveStaff.setValue(userId);
								saveOrUpdateList.add(saveStaff);
								// 设置登录密码
								SaveOrUpdateDto savePassWd = new SaveOrUpdateDto();
								savePassWd.setKey(CommonConsts.PASSWD);
								savePassWd.setValue(ExcelUtil.MD5(userId));
								saveOrUpdateList.add(savePassWd);

							}
							if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
								saveStaff.setKey(CommonConsts.USER_ID);
								// 获取db中的USERID
								String oldUserId = customizeMapper.getUserId(mobilePhone, staffName);
								saveStaff.setValue(oldUserId);
								saveOrUpdateList.add(saveStaff);
							}
							if (CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// 设置staffId
								saveStaff = new SaveOrUpdateDto();
								saveStaff.setKey(CommonConsts.STAFF_ID);
								saveStaff.setValue(staffId);
								saveOrUpdateList.add(saveStaff);
							}

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
