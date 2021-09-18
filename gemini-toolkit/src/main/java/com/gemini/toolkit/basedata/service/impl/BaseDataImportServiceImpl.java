package com.gemini.toolkit.basedata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.basedata.entity.TBasedataTempDownloadHisEntity;
import com.gemini.toolkit.basedata.mapper.CustomizeMapper;
import com.gemini.toolkit.basedata.mapper.TBasedataTempDownloadHisMapper;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.config.PasswordUtil;
import com.gemini.toolkit.login.form.UserInfo;
import com.gemini.toolkit.basedata.service.BaseDataImportService;
import com.gemini.toolkit.common.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

@Service
public class BaseDataImportServiceImpl implements BaseDataImportService {

	@Autowired
    CustomizeMapper customizeMapper;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private TBasedataTempDownloadHisMapper tBasedataTempDownloadHisMapper;
	
	@Value("${db.type}")
    String dbType;

	@Override
	public String staticVersionCheck(HSSFWorkbook wookbook, String templateType, String tempalteName) {
		String errorMessage = null;
		HSSFSheet checkSheet = wookbook.getSheetAt(0);
		// 查询下载履历中最新一条数据
		TBasedataTempDownloadHisEntity tBasedataTempDownloadHisEntity = tBasedataTempDownloadHisMapper
				.selectOne(new LambdaQueryWrapper<TBasedataTempDownloadHisEntity>()
						.eq(TBasedataTempDownloadHisEntity::getTemplateType, templateType)
						.eq(TBasedataTempDownloadHisEntity::getTempalteName, tempalteName)
						.eq(TBasedataTempDownloadHisEntity::getDeleteFlg, "0"));

		// 获取导入文件的静态版本号
		String staticVersion = ExcelUtil.getcell(checkSheet.getRow(5).getCell(4));
		// 获取导入文件的动态版本号
		String dynamicVersion = ExcelUtil.getcell(checkSheet.getRow(6).getCell(4));
		// 查询下载履历中最新一条数据的版本号
		String oldDynamicVersion = null;
		try {
			oldDynamicVersion = PasswordUtil.getSecurePassword(tBasedataTempDownloadHisEntity.getDynamicVersion());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 比较版本号
		if (!tBasedataTempDownloadHisEntity.getStaticVersion().equals(staticVersion)
				|| !dynamicVersion.equals(oldDynamicVersion)) {
			errorMessage = messageSource.getMessage("basedata.common.versionCheckError", null,
					LocaleContextHolder.getLocale());
		}

		return errorMessage;
	}

	@Override
	public Map<String, Map<Integer, List<CheckErrorDto>>> templateCheck(HSSFWorkbook wookbook, String templateType,
                                                                        String tempalteName, String templateTypeName) {

		Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap = new HashMap<String, Map<Integer, List<CheckErrorDto>>>();

		Map<Integer, List<CheckErrorDto>> checkResultMap = new HashMap<Integer, List<CheckErrorDto>>();
		List<CheckErrorDto> checkErrorDtoList = new ArrayList<CheckErrorDto>();

		HSSFSheet checkSheet = wookbook.getSheetAt(0);
		String checkTemplateType = ExcelUtil.getcell(checkSheet.getRow(4).getCell(4));
		// check导入模板类型是否正确
		if (!checkTemplateType.equals(templateTypeName)) {
			String errorMessages = messageSource.getMessage("basedata.common.templateTypeError", null,
					LocaleContextHolder.getLocale());
			CheckErrorDto checkErrorDto = new CheckErrorDto();
			checkErrorDto.setErrorMessage(errorMessages);
			checkErrorDto.setErrorRow(7);
			checkErrorDtoList.add(checkErrorDto);
			checkResultMap.put(0, checkErrorDtoList);
			// 记录sheet名和check成功件数，失败件数
			errorSheetMap.put("templateTypeError", checkResultMap);
			return errorSheetMap;
		}

		String checkStatus = ExcelUtil.getcell(checkSheet.getRow(7).getCell(4));
		// TODO 1。首先check第一页中的 vabcheck状态，如果不是ok，直接终止处理
		if (!CommonConsts.CHECKSTATUSOK.equals(ExcelUtil.UpperString(checkStatus))) {
			String errorMessages = messageSource.getMessage("basedata.common.templateCheckError", null,
					LocaleContextHolder.getLocale());
			CheckErrorDto checkErrorDto = new CheckErrorDto();
			checkErrorDto.setErrorMessage(errorMessages);
			checkErrorDto.setErrorRow(7);
			checkErrorDtoList.add(checkErrorDto);
			checkResultMap.put(0, checkErrorDtoList);
			// 记录sheet名和check成功件数，失败件数
			errorSheetMap.put("check", checkResultMap);
			return errorSheetMap;

		}
		return errorSheetMap;

	}

	@Override
	public Map<String, Map<Integer, List<CheckErrorDto>>> check(HSSFWorkbook wookbook,
                                                                Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap) {

		Map<Integer, String> errorsMessageMap = new HashMap<>();
		Map<Integer, List<CheckErrorDto>> checkResultMap = new HashMap<Integer, List<CheckErrorDto>>();
		List<CheckErrorDto> checkErrorDtoList = new ArrayList<CheckErrorDto>();

		if (errorSheetMap == null) {
			errorSheetMap = new HashMap<>();
		}

		// 3. 遍历所有sheet,进行业务check
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// 获取要出力的sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);
			Map<String, String> userValueMap = new HashMap<String, String>();

			checkErrorDtoList = new ArrayList<CheckErrorDto>();
			errorsMessageMap = new HashMap<>();
			int n = 0;
			int before = 0;
			// 如果当前sheet已经有checkError
			if (errorSheetMap != null && errorSheetMap.containsKey(sheet.getSheetName())) {
				for (Entry<Integer, List<CheckErrorDto>> entry : errorSheetMap.get(sheet.getSheetName()).entrySet()) {
					// 获取已有的checkError信息
					for (CheckErrorDto beforErrorDto : entry.getValue()) {
						errorsMessageMap.put(beforErrorDto.getErrorRow(), beforErrorDto.getErrorMessage());
					}
					before = entry.getKey();
				}
			}
			checkResultMap = new HashMap<Integer, List<CheckErrorDto>>();

			// 获取是否导入
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				// 获取要导入的表名称
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));

				// 设置checkError单元格格式
				CellStyle style = wookbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);

				// 获取需要遍历的最后一列列数
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELL.equals(cell)) {
						lastCellNum = i;
					}
				}
				// 遍历所有行
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					// 设置errorlist
					StringBuilder errorlist = new StringBuilder();

					// 获取插入或更新的数据行
					Row row = sheet.getRow(i);
					// 清空当前行的errorMessage
					ExcelUtil.clearCell(row.getCell(lastCellNum + 2));

					// 获取区分:(0:无更新，1:更新，2:增加)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {
						for (int m = 2; m < lastCellNum; m++) {
							// 清空当前单元格的颜色
							if (!CheckParamUtil.isEmpty(row.getCell(m).getCellStyle())) {
								if (row.getCell(m).getCellStyle().getFillForegroundColor() == IndexedColors.RED
										.getIndex()) {
									row.getCell(m).getCellStyle()
											.setFillForegroundColor(IndexedColors.WHITE.getIndex());
									row.getCell(m).getCellStyle().setFillPattern(CellStyle.SOLID_FOREGROUND);
								}
							}

							// 获取单元格的值
							String filedsValue = ExcelUtil.getcell(row.getCell(m));
							// 获取该列的中文名
							String[] tmp = { (String) ExcelUtil.getcell(sheet.getRow(15).getCell(m)) };

							if (CheckParamUtil.isEmpty(filedsValue)) {
								// 如果单元格的值为空,获取校验规则
								String required = ExcelUtil.getcell(sheet.getRow(8).getCell(m));
								// 判断是否为必须输入
								if (!CheckParamUtil.isEmpty(required)) {
									String errorMessages = messageSource.getMessage("basedata.common.notEmpty", tmp,
											LocaleContextHolder.getLocale());
									errorlist.append(errorMessages);
									row.getCell(m).setCellStyle(style);
								}
							} else {
								// 如果单元格有引用sheet
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
									if (!CheckParamUtil.isEmpty(filedsValue)) {
										String[] Value = filedsValue.split("\\:");
										filedsValue = Value[0];
									}
								}

								// 获取校验规则
								String check = ExcelUtil.getcell(sheet.getRow(10).getCell(m));
								// 判断是否满足校验规则
								if (!CheckParamUtil.isEmpty(check)) {
									if (!CheckParamUtil.valueCheck(check, filedsValue)) {
										String errorMessages = messageSource.getMessage(
												"basedata.common.valueCheckError", tmp,
												LocaleContextHolder.getLocale());
										errorlist.append(errorMessages);
										row.getCell(m).setCellStyle(style);
									}
								}

								// 获取长度校验规则
								String lengthCheck = ExcelUtil.getcell(sheet.getRow(11).getCell(m));
								// 判断是否长度超出
								if (!CheckParamUtil.isEmpty(lengthCheck)) {
									if (!CheckParamUtil.checkLength(lengthCheck, filedsValue)) {
										String errorMessages = messageSource.getMessage(
												"basedata.common.lengthMaxError", tmp, LocaleContextHolder.getLocale());
										errorlist.append(errorMessages);
										row.getCell(m).setCellStyle(style);
									}

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
							// 用户表
							if (CheckParamUtil.isEmpty(userIdType)) {
								if (CommonConsts.INSERT.equals(updateFlg)) {
									// 插入的时候，如果用户id指定规则未输入
									String errorMessages = messageSource.getMessage("basedata.common.userIdRoleError",
											null, LocaleContextHolder.getLocale());
									errorlist.append(errorMessages);
								}
							} else {
								// 获取用户ID指定规则,匹配对应userId
								if (CommonConsts.RULETEL.equals(userIdType)
										|| CommonConsts.RULEJOB.equals(userIdType)) {
									userId = userValueMap.get(userIdType);
								} else {
									userId = userValueMap.get(CommonConsts.RULEUSERID);
								}
								if (CheckParamUtil.isEmpty(userId)) {
									// 如果用户id指定规则对应的项目未输入
									String errorMessages = messageSource.getMessage("basedata.common.userIdError", null,
											LocaleContextHolder.getLocale());
									errorlist.append(errorMessages);
								}
							}
						}

						// 如果有错误添加到list中
						if (StringUtils.isNotEmpty(errorlist.toString())) {
							// 判断当前行在前处理时是否有异常
							String beforeError = "";
							if (errorsMessageMap.containsKey(i)) {
								beforeError = errorsMessageMap.get(i);
								// 记录错误的行号和错误信息
								errorsMessageMap.put(i, errorlist.insert(0, beforeError).toString());
							} else {
								// 当前数据通过了beforecheck
								before--;
								errorsMessageMap.put(i, errorlist.toString());
							}
						} else {
							// 记录check正常的条数
							n++;
						}
					}
				}
			}

			if (errorsMessageMap.size() > 0) {
				// 获取所有check异常信息
				for (Entry<Integer, String> entry : errorsMessageMap.entrySet()) {
					CheckErrorDto checkErrorDto = new CheckErrorDto();
					checkErrorDto.setErrorRow(entry.getKey());
					checkErrorDto.setErrorMessage(entry.getValue());
					checkErrorDtoList.add(checkErrorDto);
				}
				// 记录sheet名和check成功件数，失败件数
				if (before >= 0) {
					checkResultMap.put(before, checkErrorDtoList);
				} else {
					checkResultMap.put(n, checkErrorDtoList);
				}

				errorSheetMap.put(sheet.getSheetName(), checkResultMap);
			}
		}

		return errorSheetMap;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void importData(HSSFWorkbook wookbook) {

		// 获取登录者id
		UserInfo user = GetUserInfo.getUserInfo();
		// 遍历所有要导出数据的sheet
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// 获取要出力的sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);

			// 获取是否导入
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				// 获取要导入的表名称
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
				// 如果是用户表和用户权限表 直接跳过
				if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_SPONS_CONTACT.equals(ExcelUtil.UpperString(tableName))) {
					continue;
				}

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
					List<CondtionDto> condtionList = new ArrayList<>();
					boolean gcpFlg = false;

					// 获取插入或更新的数据行
					Row row = sheet.getRow(i);

					// 获取区分:(0:无更新，1:更新，2:增加)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {

						for (int m = 2; m < lastCellNum; m++) {
							SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
							CondtionDto condtionDto = new CondtionDto();
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
							cellValueMap.put(filed, filedsValue);

							// 获取确定数据的唯一性主key
							// 更新的数据才需要使用pk
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {
								// 设置更新查询项
								condtionDto.setKey(filed);
								condtionDto.setValue(filedsValue);
								condtionList.add(condtionDto);
							}
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
								if (!CommonConsts.ID.equals(filed)) {
									// 设置插入或更新的值
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
									// 填写了GCP信息的，t_staff.gcp_flg标记为1
									if (CommonConsts.GCP_LEVEL.equals(filed) && !CheckParamUtil.isEmpty(filedsValue)) {
										gcpFlg = true;
									}
								}
							}
						}

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
										CondtionDto condtionDto = new CondtionDto();
										condtionDto.setKey(key);
										condtionDto.setValue(object.getString(key));
										condtionList.add(condtionDto);
									}
								}
							}
							// 设置更新者
							table.setUpdateUserId(user.getStaffId());
							condtionListForUd.add(condtionList);
							updateDataList.add(saveOrUpdateList);
						} else if (CommonConsts.INSERT.equals(updateFlg)) {
							// 设置登陆者，更新者
							table.setCreateUserId(user.getStaffId());
							table.setUpdateUserId(user.getStaffId());
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

							if (CommonConsts.T_STAFF.equals(ExcelUtil.UpperString(tableName))) {
								// 默认设置组织人员及伦理人员表插入信息为内部人员
								SaveOrUpdateDto staffDto = new SaveOrUpdateDto();
								staffDto.setKey(CommonConsts.PREPARATION4_FILED);
								staffDto.setValue(CommonConsts.STAFF_BELONG_TYPE_1);
								saveOrUpdateList.add(staffDto);
								// 填写了GCP信息的，t_staff.gcp_flg标记为1
								if (gcpFlg) {
									staffDto = new SaveOrUpdateDto();
									staffDto.setKey(CommonConsts.GCP_FLG);
									staffDto.setValue(CommonConsts.GCP_FLG_1);
									saveOrUpdateList.add(staffDto);
								}
							}
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
					if (CommonConsts.T_SPONS.equals(tableName)) {
						// 申办方插入数据时，更新单据编码当前取值表
						customizeMapper.updateRuleValue(Integer.valueOf(seq), user.getStaffId(), new Date());
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

	@Override
	public Map<String, Map<Integer, List<CheckErrorDto>>> before(HSSFWorkbook wookbook, String templateName) {
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

							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {

								// 获取确定staffId的唯一性主key
								String tmp = ExcelUtil.getcell(sheet.getRow(15).getCell(m));
								if (CommonConsts.STAFFNAME.equals(tmp) || CommonConsts.SPECIALTY_LEADER.equals(tmp)) {
									staffName = ExcelUtil.getcell(row.getCell(m));
									if (!CheckParamUtil.isEmpty(staffName)) {
										pkValue = pkValue + staffName;
									}
								} else if (CommonConsts.RULETEL.equals(tmp)
										|| CommonConsts.SPECIALTY_LEADER_TEL.equals(tmp)) {
									mobilePhone = ExcelUtil.getcell(row.getCell(m));
									if (!CheckParamUtil.isEmpty(mobilePhone)) {
										pkValue = pkValue + mobilePhone;
									}
								}

								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {
									if (!CommonConsts.STAFFNAME.equals(tmp)
											&& !CommonConsts.SPECIALTY_LEADER.equals(tmp)
											&& !CommonConsts.RULETEL.equals(tmp)
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

									if (CommonConsts.T_STAFF.equals(ExcelUtil.UpperString(tableName))
											|| CommonConsts.T_SPONS_CONTACT.equals(ExcelUtil.UpperString(tableName))) {
										if (CommonConsts.STAFF_NAME.equals(filed)
												|| CommonConsts.CONTACT_NAME.equals(filed)) {
											staffName = ExcelUtil.getcell(row.getCell(m));
										} else if (CommonConsts.MOBILE_PHONE.equals(filed)
												|| CommonConsts.MOBILEPHONE.equals(filed)) {
											mobilePhone = ExcelUtil.getcell(row.getCell(m));
										}
									}
									pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
								}
							}
						}
						if (CheckParamUtil.isEmpty(errorMessages)) {

							if (CommonConsts.T_STAFF.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_SPONS_CONTACT.equals(ExcelUtil.UpperString(tableName))) {
								if (CommonConsts.INSERT.equals(updateFlg)) {
									// 将人员表插入的人员名称和手机号存入map
									staffMap.put(staffName + mobilePhone, i);
								}
							}

							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
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
									if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
											|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))) {
										staffIdCode.setKey(CommonConsts.STAFF_ID);
										// 设置查询字段
										fileds.add(CommonConsts.STAFF_ID);
										aliasNames.add(CommonConsts.STAFF_ID);
										staffIdCode.setValue(staffId);
										condtionList.add(staffIdCode);
									}

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
	@Transactional(rollbackFor = Exception.class)
	public void after(HSSFWorkbook wookbook, String templateType) {
		// 在此处根据不同的模板做特定处理
		// 获取登录者id
		UserInfo user = GetUserInfo.getUserInfo();

		String[] sheetName = null;
		// 获取要特殊处理得sheet名
		if (CommonConsts.ORGANIZERS_STAFF.equals(templateType)) {
			// 组织人员
			sheetName = CommonConsts.ORGANIZERS_STAFFSHEET.split("\\,");
		} else if (CommonConsts.COMPANY_STAFF.equals(templateType)) {
			// 公司人员
			sheetName = CommonConsts.COMPANY_STAFFSHEET.split("\\,");
		} else if (CommonConsts.ETHICS_STAFF.equals(templateType)) {
			// 伦理人员
			sheetName = CommonConsts.ETHICS_STAFFSHEET.split("\\,");
		} else if (CommonConsts.PROFESSIONAL_INFO.equals(templateType)) {
			// 专业信息
			sheetName = CommonConsts.PROFESSIONAL_INFOSHEET.split("\\,");
		}
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
							if (CommonConsts.STAFFNAME.equals(tmp) || CommonConsts.SPECIALTY_LEADER.equals(tmp)) {
								staffName = ExcelUtil.getcell(row.getCell(m));
							} else if (CommonConsts.RULETEL.equals(tmp)
									|| CommonConsts.SPECIALTY_LEADER_TEL.equals(tmp)) {
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
							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))) {
								if (!CheckParamUtil.isEmpty(staffId)) {
									// 设置专业负责人名,专业负责人Id
									saveStaff = new SaveOrUpdateDto();
									saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_NAME);
									saveStaff.setValue(staffName);
									saveOrUpdateList.add(saveStaff);
									saveStaff = new SaveOrUpdateDto();
									saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_ID);
									saveStaff.setValue(staffId);
									saveOrUpdateList.add(saveStaff);
								}
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
							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))) {
								if (!CheckParamUtil.isEmpty(staffName)) {
									// 设置专业负责人名,专业负责人Id
									saveStaff = new SaveOrUpdateDto();
									saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_NAME);
									saveStaff.setValue(staffName);
									saveOrUpdateList.add(saveStaff);
									saveStaff = new SaveOrUpdateDto();
									saveStaff.setKey(CommonConsts.SPECIALTY_LEADER_ID);
									saveStaff.setValue(staffId);
									saveOrUpdateList.add(saveStaff);
								}
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

	@Override
	public void markError(HSSFWorkbook wookbook, Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap) {
		// TODO Auto-generated method stub

		for (Entry<String, Map<Integer, List<CheckErrorDto>>> entry : errorSheetMap.entrySet()) {
			// 获取有check异常的sheet
			if (entry.getValue().size() > 0) {
				HSSFSheet sheet = wookbook.getSheet(entry.getKey());
				// 获取当前sheet中的errorlist列数
				int errorListNum = 0;
				if ("check".equals(entry.getKey())) {
					errorListNum = sheet.getRow(7).getLastCellNum();
				} else {
					errorListNum = sheet.getRow(0).getLastCellNum() - 1;
				}

				// 获取errorList信息
				for (Entry<Integer, List<CheckErrorDto>> item : entry.getValue().entrySet()) {
					for (CheckErrorDto error : item.getValue()) {
						// 获取有check异常的行数
						Row row = sheet.getRow(error.getErrorRow());
						// 设置errorList
						row.createCell(errorListNum).setCellValue(error.getErrorMessage());
					}
				}
			}
		}
	}

	/**
	 * @param tableName
	 * @param cell
	 * @param filed
	 * @param cellValueMap
	 * @param seqMap
	 * @return
	 */
	public String getSeq(String tableName, Cell cell, Map<String, String> cellValueMap, Map<String, String> seqMap) {
		String seq = null;
		// 获取自增字段的条件
		JSONObject dataJson = JSONObject.parseObject(ExcelUtil.getcell(cell));
		String format = dataJson.getString(CommonConsts.FORMAT);
		String function = dataJson.getString(CommonConsts.FUNCTION);
		String tagetKey = dataJson.getString(CommonConsts.TARGETKEY);
		String supplyValue = JSONObject.parseObject(format).getString(CommonConsts.SUPPLYVALUE);
		Integer length = Integer.valueOf(JSONObject.parseObject(format).getString(CommonConsts.LENGTH));
		String groupby = dataJson.getString(CommonConsts.GROUPBY);

		// 如果已经查询过seq
		if (!CheckParamUtil.isEmpty(groupby)) {
			seq = seqMap.get(cellValueMap.get(groupby));
		} else {
			seq = seqMap.get(CommonConsts.TARGETKEY);
		}

		if (CheckParamUtil.isEmpty(seq)) {
			if (CommonConsts.T_SPONS.equals(tableName)) {
				// 申办方表通过单据编码当前取值表和单据编码规则表，查询寻出当前spons_id
				seq = customizeMapper.getRuleValue();
			} else {

				// 获取查询sql
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);
				CondtionDto condtionCode = new CondtionDto();
				List<CondtionDto> condtionList = new ArrayList<>();
				if (!CheckParamUtil.isEmpty(groupby)) {
					// 设置where查询项的值
					condtionCode.setKey(groupby);
					condtionCode.setValue(cellValueMap.get(groupby));
					condtionList.add(condtionCode);
					table.setCondtions(condtionList);
				}

				// 设置查询项
				List<String> fileds = new ArrayList<>();
				fileds.add(function + "(" + tagetKey + ")");
				table.setFileds(fileds);

				// 设置查询出的名字
				List<String> aliasNames = new ArrayList<>();
				aliasNames.add(tagetKey);
				table.setAliasNames(aliasNames);

				// 查询DB
				String querysql = SqlBuilderHelper.queryMaxSeqSqlBuilder(table);
				List<Map<String, Object>> dataList = customizeMapper.getResults(querysql);

				if (dataList.get(0) != null) {
					seq = dataList.get(0).get(tagetKey).toString();
				} else {
					seq = "0";
				}
			}
		}

		seq = String.valueOf(Integer.valueOf(seq) + 1);
		seq = FunctionUtility.supplyFront(seq, length, supplyValue);
		return seq;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void importSponsContactData(HSSFWorkbook wookbook) {

		// 获取登录者id
		UserInfo user = GetUserInfo.getUserInfo();
		// 遍历所有要导出数据的sheet
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// 获取要出力的sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);
			// 获取要导入的表名称
			String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
			// 仅处理申办方联系人表
			if (!CommonConsts.T_SPONS_CONTACT.equals(ExcelUtil.UpperString(tableName))) {
				continue;
			}

			// 获取是否导入
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {

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

				// 创建人员表
				TableInfoDto staffTable = new TableInfoDto();
				staffTable.setTableName(CommonConsts.T_STAFF);
				List<List<SaveOrUpdateDto>> insertStaffDataList = new ArrayList<>();
				List<List<SaveOrUpdateDto>> updateStaffDataList = new ArrayList<>();
				List<List<CondtionDto>> staffListForUd = new ArrayList<>();
				// 遍历所有行
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					List<SaveOrUpdateDto> saveOrUpdateList = new ArrayList<>();
					List<CondtionDto> condtionList = new ArrayList<>();
					// 人员表插入信息
					List<SaveOrUpdateDto> staffSaveOrUpdateList = new ArrayList<>();
					List<CondtionDto> staffCondtionList = new ArrayList<>();
					String staffName = null;
					String mobilePhone = null;

					// 获取区分:(0:无更新，1:更新，2:增加)
					String updateFlg = ExcelUtil.getcell(sheet.getRow(i).getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {

						for (int m = 2; m < lastCellNum; m++) {
							SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
							CondtionDto condtionDto = new CondtionDto();
							// 人员插入或更新信息
							SaveOrUpdateDto staffSaveOrUpdateDto = new SaveOrUpdateDto();
							// 人员表更新pk
							CondtionDto staffCondtionDto = new CondtionDto();

							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));
							// 获取单元格的值
							String filedsValue = ExcelUtil.getcell(sheet.getRow(i).getCell(m));
							if (!CheckParamUtil.isEmpty(filedsValue)) {
								// 如果单元格有引用sheet
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
									// 取出公司ID
									if (CommonConsts.SPONS_ID.equals(filed)) {
										filedsValue = customizeMapper.getSpons(filedsValue);
									} else {
										if (!CheckParamUtil.isEmpty(filedsValue)) {
											String[] Value = filedsValue.split("\\:");
											filedsValue = Value[0];
										}
									}
								}
							}
							cellValueMap.put(filed, filedsValue);

							// 获取确定数据的唯一性主key
							// 更新的数据才需要使用pk
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {
								// 设置更新查询项
								condtionDto.setKey(filed);
								condtionDto.setValue(filedsValue);
								condtionList.add(condtionDto);

								// 设置更新人员表的pk
								if (CommonConsts.CONTACT_NAME.equals(filed)) {
									staffCondtionDto.setKey(CommonConsts.STAFF_NAME);
									staffCondtionDto.setValue(filedsValue);
									staffCondtionList.add(staffCondtionDto);
								}
								if (CommonConsts.MOBILEPHONE.equals(filed)) {
									staffCondtionDto.setKey(CommonConsts.MOBILE_PHONE);
									staffCondtionDto.setValue(filedsValue);
									staffCondtionList.add(staffCondtionDto);
								}
							}

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
									seqMap.put(cellValueMap.get(groupBy), seq);
								}
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// 时间格式字段获取format格式
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String format = dataJson.getString(CommonConsts.FORMAT);
									String dateFormat = JSONObject.parseObject(format)
											.getString(CommonConsts.DATEFORAMT);
									// 获取单元格format后的值
									filedsValue = ExcelUtil.getDateCell(sheet.getRow(i).getCell(m), dateFormat);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
								}
							} else {
								// 设置插入或更新的值
								saveOrUpdateDto.setKey(filed);
								saveOrUpdateDto.setValue(filedsValue);
								saveOrUpdateList.add(saveOrUpdateDto);

								// 设置插入人员表的信息
								if (CommonConsts.CONTACT_NAME.equals(filed)) {
									staffSaveOrUpdateDto.setKey(CommonConsts.STAFF_NAME);
									staffSaveOrUpdateDto.setValue(filedsValue);
									staffSaveOrUpdateList.add(staffSaveOrUpdateDto);
									staffName = filedsValue;
								} else if (CommonConsts.SEX_TYPE.equals(filed)) {
									staffSaveOrUpdateDto.setKey(CommonConsts.SEX_TYPE);
									staffSaveOrUpdateDto.setValue(filedsValue);
									staffSaveOrUpdateList.add(staffSaveOrUpdateDto);
								} else if (CommonConsts.MOBILEPHONE.equals(filed)) {
									staffSaveOrUpdateDto.setKey(CommonConsts.MOBILE_PHONE);
									staffSaveOrUpdateDto.setValue(filedsValue);
									staffSaveOrUpdateList.add(staffSaveOrUpdateDto);
									mobilePhone = filedsValue;
								} else if (CommonConsts.TELEPHONE.equals(filed)) {
									staffSaveOrUpdateDto.setKey(CommonConsts.TELEPHONE);
									staffSaveOrUpdateDto.setValue(filedsValue);
									staffSaveOrUpdateList.add(staffSaveOrUpdateDto);
								} else if (CommonConsts.E_MAIL.equals(filed)) {
									staffSaveOrUpdateDto.setKey(CommonConsts.EMAIL);
									staffSaveOrUpdateDto.setValue(filedsValue);
									staffSaveOrUpdateList.add(staffSaveOrUpdateDto);
								} else if (CommonConsts.LETTER_ADDRESS.equals(filed)) {
									staffSaveOrUpdateDto.setKey(CommonConsts.PREPARATION6_FILED);
									staffSaveOrUpdateDto.setValue(filedsValue);
									staffSaveOrUpdateList.add(staffSaveOrUpdateDto);
								}
							}

						}
						String insertCondtions = ExcelUtil.getcell(sheet.getRow(2).getCell(2));
						if (CommonConsts.UPDATE.equals(updateFlg)) {
							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// 获取默认插入值，设置where条件
								JSONObject dataJson = JSONObject.parseObject(insertCondtions);
								JSONArray jsonArray = JSONArray.parseArray(dataJson.getString("insertCondtions"));
								for (int m = 0; m < jsonArray.size(); m++) {
									JSONObject object = jsonArray.getJSONObject(m);
									for (String key : object.keySet()) {
										CondtionDto condtionDto = new CondtionDto();
										condtionDto.setKey(key);
										condtionDto.setValue(object.getString(key));
										condtionList.add(condtionDto);
									}
								}
							}
							// 设置更新者
							table.setUpdateUserId(user.getStaffId());
							condtionListForUd.add(condtionList);
							updateDataList.add(saveOrUpdateList);
							// 设置人员表更新信息
							staffTable.setUpdateUserId(user.getStaffId());
							staffListForUd.add(staffCondtionList);
							updateStaffDataList.add(staffSaveOrUpdateList);
						} else if (CommonConsts.INSERT.equals(updateFlg)) {
							// 设置登陆者，更新者
							table.setCreateUserId(user.getStaffId());
							table.setUpdateUserId(user.getStaffId());
							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// 获取默认插入值
								JSONObject dataJson = JSONObject.parseObject(insertCondtions);
								JSONArray jsonArray = JSONArray.parseArray(dataJson.getString("insertCondtions"));
								for (int m = 0; m < jsonArray.size(); m++) {
									JSONObject object = jsonArray.getJSONObject(m);
									for (String key : object.keySet()) {
										SaveOrUpdateDto condtionDto = new SaveOrUpdateDto();
										condtionDto.setKey(key);
										condtionDto.setValue(object.getString(key));
										saveOrUpdateList.add(condtionDto);
									}
								}
							}

							// 获取人员表staffId最大值
							String staffId = customizeMapper.getStaffId(staffName, mobilePhone);
							if (!CheckParamUtil.isEmpty(staffId)) {
								// 当人员表该人员存在时
								seq = staffId;
							} else {
								seq = getSponsContactStaffId(seqMap);
								seqMap.put(CommonConsts.TARGETKEY, seq);
							}

							// 申办方联系人表，设置备用字段1为staffId
							SaveOrUpdateDto sponsContact = new SaveOrUpdateDto();
							sponsContact.setKey(CommonConsts.PREPARATION1_FILED);
							sponsContact.setValue(seq);
							saveOrUpdateList.add(sponsContact);
							insertDataList.add(saveOrUpdateList);

							// 默认设置人员表插入信息为外部人员
							SaveOrUpdateDto staffDto = new SaveOrUpdateDto();
							staffDto.setKey(CommonConsts.PREPARATION4_FILED);
							staffDto.setValue(CommonConsts.STAFF_BELONG_TYPE_2);
							staffSaveOrUpdateList.add(staffDto);
							if (!CheckParamUtil.isEmpty(staffId)) {
								// 当人员表该人员存在时
								staffListForUd.add(staffCondtionList);
								updateStaffDataList.add(staffSaveOrUpdateList);
							} else {
								// 设置人员表staffId
								staffDto = new SaveOrUpdateDto();
								staffDto.setKey(CommonConsts.STAFF_ID);
								staffDto.setValue(seq);
								staffSaveOrUpdateList.add(staffDto);

								// 设置人员表插入信息
								insertStaffDataList.add(staffSaveOrUpdateList);
								staffTable.setCreateUserId(user.getStaffId());
							}
							staffTable.setUpdateUserId(user.getStaffId());
						}
					}
				}

				// TODO 更新 或者 新增数据
				if (insertStaffDataList.size() > 0) {
					// 先插入人员表信息
					List<String> insertStaffSql = SqlBuilderHelper.batchInsertSqlBuilder(staffTable,
							insertStaffDataList, dbType);
					if ("postgresql".equals(dbType)) {
						customizeMapper.bacthInsert(insertStaffSql);
					} else {
						for (String sqlString : insertStaffSql) {
							customizeMapper.bacthInsertOracle(sqlString);
						}
					}
				}
				if (insertDataList.size() > 0) {
					// 插入申办方联系人信息
					List<String> insertSql = SqlBuilderHelper.batchInsertSqlBuilder(table, insertDataList, dbType);
					if ("postgresql".equals(dbType)) {
						customizeMapper.bacthInsert(insertSql);
					} else {
						for (String sqlString : insertSql) {
							customizeMapper.bacthInsertOracle(sqlString);
						}
					}
				}
				if (updateStaffDataList.size() > 0) {
					// 先更新人员表信息
					List<String> updateStaffSql = SqlBuilderHelper.batchUpdateSqlBuilder(staffTable,
							updateStaffDataList, staffListForUd, dbType);
					if ("postgresql".equals(dbType)) {
						customizeMapper.bacthUpdate(updateStaffSql);
					} else {
						for (String sqlString : updateStaffSql) {
							customizeMapper.bacthUpdateOracle(sqlString);
						}
					}
				}
				if (updateDataList.size() > 0) {
					// 更新申办方联系人信息
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

	/**
	 * @param seqMap
	 * @return
	 */
	public String getSponsContactStaffId(Map<String, String> seqMap) {
		String seq = null;

		// 如果已经查询过seq
		seq = seqMap.get(CommonConsts.TARGETKEY);
		if (CheckParamUtil.isEmpty(seq)) {
			// 获取查询sql
			TableInfoDto table = new TableInfoDto();
			table.setTableName(CommonConsts.T_STAFF);

			// 设置查询项
			List<String> fileds = new ArrayList<>();
			fileds.add(CommonConsts.MAX + "(" + CommonConsts.STAFF_ID + ")");
			table.setFileds(fileds);

			// 设置查询出的名字
			List<String> aliasNames = new ArrayList<>();
			aliasNames.add(CommonConsts.STAFF_ID);
			table.setAliasNames(aliasNames);

			// 查询DB
			String querysql = SqlBuilderHelper.queryMaxSeqSqlBuilder(table);
			List<Map<String, Object>> dataList = customizeMapper.getResults(querysql);

			if (dataList.get(0) != null) {
				seq = dataList.get(0).get(CommonConsts.STAFF_ID).toString();
			} else {
				seq = "0";
			}
		}

		seq = String.valueOf(Integer.valueOf(seq) + 1);
		seq = FunctionUtility.supplyFront(seq, 8, "0");
		return seq;
	}
}
