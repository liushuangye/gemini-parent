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
import com.gemini.toolkit.basedata.service.BaseDataImportService;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.config.PasswordUtil;
import com.gemini.toolkit.login.form.UserInfo;
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
		// ???????????????????????????????????????
		TBasedataTempDownloadHisEntity tBasedataTempDownloadHisEntity = tBasedataTempDownloadHisMapper
				.selectOne(new LambdaQueryWrapper<TBasedataTempDownloadHisEntity>()
						.eq(TBasedataTempDownloadHisEntity::getTemplateType, templateType)
						.eq(TBasedataTempDownloadHisEntity::getTempalteName, tempalteName)
						.eq(TBasedataTempDownloadHisEntity::getDeleteFlg, "0"));

		// ????????????????????????????????????
		String staticVersion = ExcelUtil.getcell(checkSheet.getRow(5).getCell(4));
		// ????????????????????????????????????
		String dynamicVersion = ExcelUtil.getcell(checkSheet.getRow(6).getCell(4));
		// ???????????????????????????????????????????????????
		String oldDynamicVersion = null;
		try {
			oldDynamicVersion = PasswordUtil.getSecurePassword(tBasedataTempDownloadHisEntity.getDynamicVersion());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ???????????????
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
		// check??????????????????????????????
		if (!checkTemplateType.equals(templateTypeName)) {
			String errorMessages = messageSource.getMessage("basedata.common.templateTypeError", null,
					LocaleContextHolder.getLocale());
			CheckErrorDto checkErrorDto = new CheckErrorDto();
			checkErrorDto.setErrorMessage(errorMessages);
			checkErrorDto.setErrorRow(7);
			checkErrorDtoList.add(checkErrorDto);
			checkResultMap.put(0, checkErrorDtoList);
			// ??????sheet??????check???????????????????????????
			errorSheetMap.put("templateTypeError", checkResultMap);
			return errorSheetMap;
		}

		String checkStatus = ExcelUtil.getcell(checkSheet.getRow(7).getCell(4));
		// TODO 1?????????check??????????????? vabcheck?????????????????????ok?????????????????????
		if (!CommonConsts.CHECKSTATUSOK.equals(ExcelUtil.UpperString(checkStatus))) {
			String errorMessages = messageSource.getMessage("basedata.common.templateCheckError", null,
					LocaleContextHolder.getLocale());
			CheckErrorDto checkErrorDto = new CheckErrorDto();
			checkErrorDto.setErrorMessage(errorMessages);
			checkErrorDto.setErrorRow(7);
			checkErrorDtoList.add(checkErrorDto);
			checkResultMap.put(0, checkErrorDtoList);
			// ??????sheet??????check???????????????????????????
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

		// 3. ????????????sheet,????????????check
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// ??????????????????sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);
			Map<String, String> userValueMap = new HashMap<String, String>();

			checkErrorDtoList = new ArrayList<CheckErrorDto>();
			errorsMessageMap = new HashMap<>();
			int n = 0;
			int before = 0;
			// ????????????sheet?????????checkError
			if (errorSheetMap != null && errorSheetMap.containsKey(sheet.getSheetName())) {
				for (Entry<Integer, List<CheckErrorDto>> entry : errorSheetMap.get(sheet.getSheetName()).entrySet()) {
					// ???????????????checkError??????
					for (CheckErrorDto beforErrorDto : entry.getValue()) {
						errorsMessageMap.put(beforErrorDto.getErrorRow(), beforErrorDto.getErrorMessage());
					}
					before = entry.getKey();
				}
			}
			checkResultMap = new HashMap<Integer, List<CheckErrorDto>>();

			// ??????????????????
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				// ???????????????????????????
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));

				// ??????checkError???????????????
				CellStyle style = wookbook.createCellStyle();
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);

				// ???????????????????????????????????????
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELL.equals(cell)) {
						lastCellNum = i;
					}
				}
				// ???????????????
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					// ??????errorlist
					StringBuilder errorlist = new StringBuilder();

					// ?????????????????????????????????
					Row row = sheet.getRow(i);
					// ??????????????????errorMessage
					ExcelUtil.clearCell(row.getCell(lastCellNum + 2));

					// ????????????:(0:????????????1:?????????2:??????)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {
						for (int m = 2; m < lastCellNum; m++) {
							// ??????????????????????????????
							if (!CheckParamUtil.isEmpty(row.getCell(m).getCellStyle())) {
								if (row.getCell(m).getCellStyle().getFillForegroundColor() == IndexedColors.RED
										.getIndex()) {
									row.getCell(m).getCellStyle()
											.setFillForegroundColor(IndexedColors.WHITE.getIndex());
									row.getCell(m).getCellStyle().setFillPattern(CellStyle.SOLID_FOREGROUND);
								}
							}

							// ?????????????????????
							String filedsValue = ExcelUtil.getcell(row.getCell(m));
							// ????????????????????????
							String[] tmp = { (String) ExcelUtil.getcell(sheet.getRow(15).getCell(m)) };

							if (CheckParamUtil.isEmpty(filedsValue)) {
								// ???????????????????????????,??????????????????
								String required = ExcelUtil.getcell(sheet.getRow(8).getCell(m));
								// ???????????????????????????
								if (!CheckParamUtil.isEmpty(required)) {
									String errorMessages = messageSource.getMessage("basedata.common.notEmpty", tmp,
											LocaleContextHolder.getLocale());
									errorlist.append(errorMessages);
									row.getCell(m).setCellStyle(style);
								}
							} else {
								// ????????????????????????sheet
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
									if (!CheckParamUtil.isEmpty(filedsValue)) {
										String[] Value = filedsValue.split("\\:");
										filedsValue = Value[0];
									}
								}

								// ??????????????????
								String check = ExcelUtil.getcell(sheet.getRow(10).getCell(m));
								// ??????????????????????????????
								if (!CheckParamUtil.isEmpty(check)) {
									if (!CheckParamUtil.valueCheck(check, filedsValue)) {
										String errorMessages = messageSource.getMessage(
												"basedata.common.valueCheckError", tmp,
												LocaleContextHolder.getLocale());
										errorlist.append(errorMessages);
										row.getCell(m).setCellStyle(style);
									}
								}

								// ????????????????????????
								String lengthCheck = ExcelUtil.getcell(sheet.getRow(11).getCell(m));
								// ????????????????????????
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
								// ???????????????????????????map
								userValueMap.put(ExcelUtil.getcell(sheet.getRow(15).getCell(m)), filedsValue);
							}
						}

						String userId = null;
						String userIdType = ExcelUtil.getcell(row.getCell(lastCellNum - 1));
						if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
							// ?????????
							if (CheckParamUtil.isEmpty(userIdType)) {
								if (CommonConsts.INSERT.equals(updateFlg)) {
									// ??????????????????????????????id?????????????????????
									String errorMessages = messageSource.getMessage("basedata.common.userIdRoleError",
											null, LocaleContextHolder.getLocale());
									errorlist.append(errorMessages);
								}
							} else {
								// ????????????ID????????????,????????????userId
								if (CommonConsts.RULETEL.equals(userIdType)
										|| CommonConsts.RULEJOB.equals(userIdType)) {
									userId = userValueMap.get(userIdType);
								} else {
									userId = userValueMap.get(CommonConsts.RULEUSERID);
								}
								if (CheckParamUtil.isEmpty(userId)) {
									// ????????????id????????????????????????????????????
									String errorMessages = messageSource.getMessage("basedata.common.userIdError", null,
											LocaleContextHolder.getLocale());
									errorlist.append(errorMessages);
								}
							}
						}

						// ????????????????????????list???
						if (StringUtils.isNotEmpty(errorlist.toString())) {
							// ?????????????????????????????????????????????
							String beforeError = "";
							if (errorsMessageMap.containsKey(i)) {
								beforeError = errorsMessageMap.get(i);
								// ????????????????????????????????????
								errorsMessageMap.put(i, errorlist.insert(0, beforeError).toString());
							} else {
								// ?????????????????????beforecheck
								before--;
								errorsMessageMap.put(i, errorlist.toString());
							}
						} else {
							// ??????check???????????????
							n++;
						}
					}
				}
			}

			if (errorsMessageMap.size() > 0) {
				// ????????????check????????????
				for (Entry<Integer, String> entry : errorsMessageMap.entrySet()) {
					CheckErrorDto checkErrorDto = new CheckErrorDto();
					checkErrorDto.setErrorRow(entry.getKey());
					checkErrorDto.setErrorMessage(entry.getValue());
					checkErrorDtoList.add(checkErrorDto);
				}
				// ??????sheet??????check???????????????????????????
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

		// ???????????????id
		UserInfo user = GetUserInfo.getUserInfo();
		// ??????????????????????????????sheet
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// ??????????????????sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);

			// ??????????????????
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				// ???????????????????????????
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
				// ???????????????????????????????????? ????????????
				if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))
						|| CommonConsts.T_SPONS_CONTACT.equals(ExcelUtil.UpperString(tableName))) {
					continue;
				}

				// ???????????????????????????????????????
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELLFLG.equals(cell)) {
						lastCellNum = i;
					}
				}

				// ??????tableinfo
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);
				List<List<SaveOrUpdateDto>> updateDataList = new ArrayList<>();
				List<List<SaveOrUpdateDto>> insertDataList = new ArrayList<>();
				List<List<CondtionDto>> condtionListForUd = new ArrayList<>();
				Map<String, String> cellValueMap = new HashMap<String, String>();
				Map<String, String> seqMap = new HashMap<String, String>();
				String seq = null;
				// ???????????????
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					List<SaveOrUpdateDto> saveOrUpdateList = new ArrayList<>();
					List<CondtionDto> condtionList = new ArrayList<>();
					boolean gcpFlg = false;

					// ?????????????????????????????????
					Row row = sheet.getRow(i);

					// ????????????:(0:????????????1:?????????2:??????)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {

						for (int m = 2; m < lastCellNum; m++) {
							SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
							CondtionDto condtionDto = new CondtionDto();
							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));

							// ?????????????????????
							String filedsValue = ExcelUtil.getcell(row.getCell(m));
							if (!CheckParamUtil.isEmpty(filedsValue)) {
								// ????????????????????????sheet
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
									if (!CheckParamUtil.isEmpty(filedsValue)) {
										String[] Value = filedsValue.split("\\:");
										filedsValue = Value[0];
									}
								}
							}
							cellValueMap.put(filed, filedsValue);

							// ?????????????????????????????????key
							// ??????????????????????????????pk
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {
								// ?????????????????????
								condtionDto.setKey(filed);
								condtionDto.setValue(filedsValue);
								condtionList.add(condtionDto);
							}
							// ?????????????????????
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(13).getCell(m)))) {
								if (!CommonConsts.UPDATE.equals(updateFlg)
										&& CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// ???????????????????????????????????????????????????1???
									seq = getSeq(tableName, sheet.getRow(13).getCell(m), cellValueMap, seqMap);
									saveOrUpdateDto.setValue(seq);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateList.add(saveOrUpdateDto);

									// ??????seqMap
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String groupBy = dataJson.getString(CommonConsts.GROUPBY);
									// ??????????????????seq??????map????????????????????????seq???+1
									if (!CheckParamUtil.isEmpty(groupBy)) {
										// ??????????????????????????????organize_id?????????organize_id??????key????????????
										seqMap.put(cellValueMap.get(groupBy), seq);
									} else {
										seqMap.put(CommonConsts.TARGETKEY, seq);
									}
								}
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// ????????????????????????format??????
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String format = dataJson.getString(CommonConsts.FORMAT);
									String dateFormat = JSONObject.parseObject(format)
											.getString(CommonConsts.DATEFORAMT);
									// ???????????????format?????????
									filedsValue = ExcelUtil.getDateCell(row.getCell(m), dateFormat);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
								}
							} else {
								if (!CommonConsts.ID.equals(filed)) {
									// ???????????????????????????
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
									// ?????????GCP????????????t_staff.gcp_flg?????????1
									if (CommonConsts.GCP_LEVEL.equals(filed) && !CheckParamUtil.isEmpty(filedsValue)) {
										gcpFlg = true;
									}
								}
							}
						}

						// ??????????????????
						String insertCondtions = ExcelUtil.getcell(sheet.getRow(2).getCell(2));
						if (CommonConsts.UPDATE.equals(updateFlg)) {
							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// ??????????????????????????????where??????
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
							// ???????????????
							table.setUpdateUserId(user.getStaffId());
							condtionListForUd.add(condtionList);
							updateDataList.add(saveOrUpdateList);
						} else if (CommonConsts.INSERT.equals(updateFlg)) {
							// ???????????????????????????
							table.setCreateUserId(user.getStaffId());
							table.setUpdateUserId(user.getStaffId());
							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// ???????????????????????????????????????
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
								// ?????????????????????????????????????????????????????????????????????
								SaveOrUpdateDto staffDto = new SaveOrUpdateDto();
								staffDto.setKey(CommonConsts.PREPARATION4_FILED);
								staffDto.setValue(CommonConsts.STAFF_BELONG_TYPE_1);
								saveOrUpdateList.add(staffDto);
								// ?????????GCP????????????t_staff.gcp_flg?????????1
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

				// TODO ?????? ?????? ????????????
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
						// ????????????????????????????????????????????????????????????
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
		// ???????????????????????????????????????check

		Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap = new HashMap<String, Map<Integer, List<CheckErrorDto>>>();

		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Integer> staffMap = new HashMap<String, Integer>();
		Map<String, Integer> userMap = new HashMap<String, Integer>();
		// ??????????????????????????????sheet,??????db????????????????????????
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// ??????pk???map
			Map<String, Integer> pkMap = new HashMap<String, Integer>();
			// ??????????????????sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);

			Map<Integer, List<CheckErrorDto>> checkResultMap = new HashMap<Integer, List<CheckErrorDto>>();
			List<CheckErrorDto> checkErrorDtoList = new ArrayList<CheckErrorDto>();

			// ??????????????????
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				int n = 0;

				// ???????????????????????????????????????
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELLFLG.equals(cell)) {
						lastCellNum = i;
					}
				}

				// ???????????????????????????
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
				// ??????tableinfo
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);

				// ???????????????
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					CheckErrorDto checkErrorDto = new CheckErrorDto();
					List<CondtionDto> condtionList = new ArrayList<>();
					List<String> fileds = new ArrayList<>();
					List<String> aliasNames = new ArrayList<>();
					String staffName = null;
					String mobilePhone = null;
					String pkValue = "";
					// ?????????????????????????????????
					Row row = sheet.getRow(i);

					// ???????????????
					String tableType = ExcelUtil.getcell(sheet.getRow(1).getCell(2));
					table.setQueryCondtions(tableType);

					// ??????????????????
					String errorMessages = null;
					// ????????????:(0:????????????1:?????????2:??????)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.INSERT.equals(updateFlg) || CommonConsts.UPDATE.equals(updateFlg)) {
						for (int m = 2; m < lastCellNum; m++) {

							// ????????????
							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));
							// ?????????
							String filedValue = ExcelUtil.getcell(row.getCell(m));
							// ???????????????sheet
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
								if (!CheckParamUtil.isEmpty(filedValue)) {
									String[] Value = filedValue.split("\\:");
									filedValue = Value[0];
								}
							}
							// ??????????????????
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

								// ????????????staffId???????????????key
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
										// ???????????????????????????????????????pk
										// ???????????????
										CondtionDto condtionCode = new CondtionDto();
										condtionCode.setKey(filed);
										condtionCode.setValue(filedValue);
										condtionList.add(condtionCode);
										fileds.add(filed);
										// ??????????????????
										aliasNames.add(tmp);
										pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
									}
								} else if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))
										&& CommonConsts.ROLE_ID.equals(filed)) {
									if (CommonConsts.INSERT.equals(updateFlg)) {
										// ????????????????????????????????????????????????????????????????????????
										CondtionDto condtionCode = new CondtionDto();
										condtionCode.setKey(filed);
										condtionCode.setValue(filedValue);
										condtionList.add(condtionCode);
									}
									pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
								} else if (CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
										&& CommonConsts.ORGANIZE_ID.equals(filed)) {
									if (CommonConsts.INSERT.equals(updateFlg)) {
										// ???????????????????????????????????????????????????????????????id???????????????
										CondtionDto condtionCode = new CondtionDto();
										condtionCode.setKey(filed);
										condtionCode.setValue(filedValue);
										condtionList.add(condtionCode);
									}
									pkValue = pkValue + ExcelUtil.getcell(row.getCell(m));
								}
							} else {
								// ??????pk???,??????pk
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {

									// ???????????????
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
									// ???????????????????????????????????????????????????map
									staffMap.put(staffName + mobilePhone, i);
								}
							}

							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))) {
								String staffId = null;
								if (!CheckParamUtil.isEmpty(staffName) || !CheckParamUtil.isEmpty(mobilePhone)) {
									// ????????????staffId
									staffId = customizeMapper.getStaffId(staffName, mobilePhone);

									if (CheckParamUtil.isEmpty(staffId)) {
										// ?????????????????????????????????
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
									// ???????????????
									CondtionDto staffIdCode = new CondtionDto();
									if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
											|| CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))) {
										staffIdCode.setKey(CommonConsts.STAFF_ID);
										// ??????????????????
										fileds.add(CommonConsts.STAFF_ID);
										aliasNames.add(CommonConsts.STAFF_ID);
										staffIdCode.setValue(staffId);
										condtionList.add(staffIdCode);
									}

									if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))
											&& CommonConsts.INSERT.equals(updateFlg)) {
										// ??????????????????????????????????????????????????????map
										userMap.put(staffName + mobilePhone, i);
									}
								}
							}

							if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
								// ????????????userId
								String userId = customizeMapper.getUserId(mobilePhone, staffName);

								if (CheckParamUtil.isEmpty(userId)) {
									if (!userMap.containsKey(staffName + mobilePhone)) {
										// ?????????????????????????????????
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
								// ???????????????
								CondtionDto userIdCode = new CondtionDto();
								userIdCode.setKey(CommonConsts.USER_ID);
								userIdCode.setValue(userId);
								condtionList.add(userIdCode);
								// ??????????????????
								fileds.add(CommonConsts.USER_ID);
								aliasNames.add(CommonConsts.USER_ID);
							}

							// ????????????pk?????????db
							table.setFileds(fileds);
							table.setAliasNames(aliasNames);
							table.setCondtions(condtionList);
							String querysql = SqlBuilderHelper.querySqlBuilder(table);
							// ??????pk??????DB
							dataList = customizeMapper.getResults(querysql);

							if (CommonConsts.INSERT.equals(updateFlg)) {
								if (dataList.size() > 0 || pkMap.containsKey(pkValue)) {
									// db?????????????????????,??????excel???????????????pk???????????????
									checkErrorDto.setErrorRow(i);
									errorMessages = messageSource.getMessage("basedata.common.dataRepeatError", null,
											LocaleContextHolder.getLocale());
									checkErrorDto.setErrorMessage(errorMessages);
									checkErrorDtoList.add(checkErrorDto);
								} else {
									// ??????????????????
									n++;
									// ?????????pk??????map???
									pkMap.put(pkValue, i);
								}
							} else if (CommonConsts.UPDATE.equals(updateFlg)) {
								if (dataList.size() == 0 && !pkMap.containsKey(pkValue)) {
									// db?????????????????????,??????excel??????????????????pk???????????????
									checkErrorDto.setErrorRow(i);
									errorMessages = messageSource.getMessage("basedata.common.dataNotExistError", null,
											LocaleContextHolder.getLocale());
									checkErrorDto.setErrorMessage(errorMessages);
									checkErrorDtoList.add(checkErrorDto);
								} else {
									// db?????????????????????
									n++;
									// ?????????pk??????map???
									pkMap.put(pkValue, i);
								}
							}
						}
					}
				}
				if (checkErrorDtoList.size() > 0) {
					// ????????????????????????
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
		// ?????????????????????????????????????????????
		// ???????????????id
		UserInfo user = GetUserInfo.getUserInfo();

		String[] sheetName = null;
		// ????????????????????????sheet???
		if (CommonConsts.ORGANIZERS_STAFF.equals(templateType)) {
			// ????????????
			sheetName = CommonConsts.ORGANIZERS_STAFFSHEET.split("\\,");
		} else if (CommonConsts.COMPANY_STAFF.equals(templateType)) {
			// ????????????
			sheetName = CommonConsts.COMPANY_STAFFSHEET.split("\\,");
		} else if (CommonConsts.ETHICS_STAFF.equals(templateType)) {
			// ????????????
			sheetName = CommonConsts.ETHICS_STAFFSHEET.split("\\,");
		} else if (CommonConsts.PROFESSIONAL_INFO.equals(templateType)) {
			// ????????????
			sheetName = CommonConsts.PROFESSIONAL_INFOSHEET.split("\\,");
		}
		for (int k = 0; k < sheetName.length; k++) {
			// ??????????????????sheet
			HSSFSheet sheet = wookbook.getSheet(sheetName[k]);
			Map<String, String> userValueMap = new HashMap<String, String>();

			// ??????????????????
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {
				// ???????????????????????????
				String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));

				// ???????????????????????????????????????
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELLFLG.equals(cell)) {
						lastCellNum = i;
					}
				}

				// ??????tableinfo
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);
				List<List<SaveOrUpdateDto>> updateDataList = new ArrayList<>();
				List<List<SaveOrUpdateDto>> insertDataList = new ArrayList<>();
				List<List<CondtionDto>> condtionListForUd = new ArrayList<>();
				Map<String, String> cellValueMap = new HashMap<String, String>();
				Map<String, String> seqMap = new HashMap<String, String>();
				String seq = null;
				// ???????????????
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					List<SaveOrUpdateDto> saveOrUpdateList = new ArrayList<>();
					String staffName = null;
					String mobilePhone = null;

					// ?????????????????????????????????
					Row row = sheet.getRow(i);

					// ???????????????where??????
					List<CondtionDto> condtionDtoList = new ArrayList<>();
					// ????????????:(0:????????????1:?????????2:??????)
					String updateFlg = ExcelUtil.getcell(row.getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {
						for (int m = 2; m < lastCellNum; m++) {
							// ???????????????
							SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));

							// ?????????????????????
							String filedsValue = ExcelUtil.getcell(row.getCell(m));
							if (!CheckParamUtil.isEmpty(filedsValue)) {
								// ????????????????????????sheet
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
									if (!CheckParamUtil.isEmpty(filedsValue)) {
										String[] Value = filedsValue.split("\\:");
										filedsValue = Value[0];
									}
								}
							}

							// ????????????staffId???????????????key
							String tmp = ExcelUtil.getcell(sheet.getRow(15).getCell(m));
							// ????????????????????????
							if (CommonConsts.STAFFNAME.equals(tmp) || CommonConsts.SPECIALTY_LEADER.equals(tmp)) {
								staffName = ExcelUtil.getcell(row.getCell(m));
							} else if (CommonConsts.RULETEL.equals(tmp)
									|| CommonConsts.SPECIALTY_LEADER_TEL.equals(tmp)) {
								mobilePhone = ExcelUtil.getcell(row.getCell(m));
							}

							// ???????????????????????????
							cellValueMap.put(filed, filedsValue);
							// ?????????????????????
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(13).getCell(m)))) {
								if (!CommonConsts.UPDATE.equals(updateFlg)
										&& CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// ???????????????????????????????????????????????????1???
									seq = getSeq(tableName, sheet.getRow(13).getCell(m), cellValueMap, seqMap);
									saveOrUpdateDto.setValue(seq);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateList.add(saveOrUpdateDto);

									// ??????seqMap
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String groupBy = dataJson.getString(CommonConsts.GROUPBY);
									// ??????????????????seq??????map????????????????????????seq???+1
									if (!CheckParamUtil.isEmpty(groupBy)) {
										// ??????????????????????????????organize_id?????????organize_id??????key????????????
										seqMap.put(cellValueMap.get(groupBy), seq);
									} else {
										seqMap.put(CommonConsts.TARGETKEY, seq);
									}
								}
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// ????????????????????????format??????
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String format = dataJson.getString(CommonConsts.FORMAT);
									String dateFormat = JSONObject.parseObject(format)
											.getString(CommonConsts.DATEFORAMT);
									// ???????????????format?????????
									filedsValue = ExcelUtil.getDateCell(row.getCell(m), dateFormat);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
								}
							} else {
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(m)))) {
									if (!CommonConsts.ID.equals(filed)) {
										// ???????????????????????????
										saveOrUpdateDto.setKey(ExcelUtil.getcell(sheet.getRow(5).getCell(m)));
										saveOrUpdateDto.setValue(filedsValue);
										saveOrUpdateList.add(saveOrUpdateDto);
									}
								}

							}

							// ??????????????????????????????pk
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(m)))) {
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))
										|| CommonConsts.ID.equals(filed)) {
									// ?????????????????????
									CondtionDto condtion = new CondtionDto();
									condtion.setKey(filed);
									condtion.setValue(filedsValue);
									condtionDtoList.add(condtion);
								}
							}
							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// ???????????????????????????map
								userValueMap.put(ExcelUtil.getcell(sheet.getRow(15).getCell(m)), filedsValue);
							}
						}

						String userId = null;
						String userIdType = ExcelUtil.getcell(row.getCell(lastCellNum - 1));
						if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
							// ????????????ID????????????,????????????userId
							if (CommonConsts.RULETEL.equals(userIdType) || CommonConsts.RULEJOB.equals(userIdType)) {
								userId = userValueMap.get(userIdType);
							} else {
								userId = userValueMap.get(CommonConsts.RULEUSERID);
							}
						}

						// ??????staffId
						String staffId = null;
						if (!CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
							staffId = customizeMapper.getStaffId(staffName, mobilePhone);
						}

						CondtionDto condtionDto = new CondtionDto();
						// ??????????????????
						SaveOrUpdateDto saveStaff = new SaveOrUpdateDto();

						// ??????????????????
						String insertCondtions = ExcelUtil.getcell(sheet.getRow(2).getCell(2));
						if (CommonConsts.UPDATE.equals(updateFlg)) {
							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// ??????????????????????????????where??????
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
							// ??????????????????
							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// ????????????????????????userId
								if (!CheckParamUtil.isEmpty(userId)) {
									saveStaff.setKey(CommonConsts.USER_ID);
									saveStaff.setValue(userId);
									saveOrUpdateList.add(saveStaff);
								}
							}
							if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
								// ?????????????????????????????????

								condtionDto.setKey(CommonConsts.USER_ID);
								// ????????????USERID?????????db??????USERID
								String oldUserId = customizeMapper.getUserId(mobilePhone, staffName);
								condtionDto.setValue(oldUserId);
								condtionDtoList.add(condtionDto);
								condtionListForUd.add(condtionDtoList);
							}
							if (CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// ??????????????????where?????????staffId

								condtionDto.setKey(CommonConsts.STAFF_ID);
								condtionDto.setValue(staffId);
								condtionDtoList.add(condtionDto);
								condtionListForUd.add(condtionDtoList);
							}
							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))) {
								if (!CheckParamUtil.isEmpty(staffId)) {
									// ????????????????????????,???????????????Id
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
							// ???????????????
							updateDataList.add(saveOrUpdateList);
						} else if (CommonConsts.INSERT.equals(updateFlg)) {
							table.setCreateUserId(user.getStaffId());
							table.setUpdateUserId(user.getStaffId());
							if (CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// ??????????????????userId
								saveStaff.setKey(CommonConsts.USER_ID);
								saveStaff.setValue(userId);
								saveOrUpdateList.add(saveStaff);
								// ??????????????????
								SaveOrUpdateDto savePassWd = new SaveOrUpdateDto();
								savePassWd.setKey(CommonConsts.PASSWD);
								savePassWd.setValue(ExcelUtil.MD5(userId));
								saveOrUpdateList.add(savePassWd);

							}
							if (CommonConsts.T_USER_ROLE.equals(ExcelUtil.UpperString(tableName))) {
								saveStaff.setKey(CommonConsts.USER_ID);
								// ??????db??????USERID
								String oldUserId = customizeMapper.getUserId(mobilePhone, staffName);
								saveStaff.setValue(oldUserId);
								saveOrUpdateList.add(saveStaff);
							}
							if (CommonConsts.T_ORGANIZE_STAFF.equals(ExcelUtil.UpperString(tableName))
									|| CommonConsts.T_USER.equals(ExcelUtil.UpperString(tableName))) {
								// ??????staffId
								saveStaff = new SaveOrUpdateDto();
								saveStaff.setKey(CommonConsts.STAFF_ID);
								saveStaff.setValue(staffId);
								saveOrUpdateList.add(saveStaff);
							}
							if (CommonConsts.T_SPECIALTY.equals(ExcelUtil.UpperString(tableName))) {
								if (!CheckParamUtil.isEmpty(staffName)) {
									// ????????????????????????,???????????????Id
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
								// ???????????????????????????????????????
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
							// ???????????????
							insertDataList.add(saveOrUpdateList);
						}
					}
				}
				// TODO ?????? ?????? ????????????
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
			// ?????????check?????????sheet
			if (entry.getValue().size() > 0) {
				HSSFSheet sheet = wookbook.getSheet(entry.getKey());
				// ????????????sheet??????errorlist??????
				int errorListNum = 0;
				if ("check".equals(entry.getKey())) {
					errorListNum = sheet.getRow(7).getLastCellNum();
				} else {
					errorListNum = sheet.getRow(0).getLastCellNum() - 1;
				}

				// ??????errorList??????
				for (Entry<Integer, List<CheckErrorDto>> item : entry.getValue().entrySet()) {
					for (CheckErrorDto error : item.getValue()) {
						// ?????????check???????????????
						Row row = sheet.getRow(error.getErrorRow());
						// ??????errorList
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
		// ???????????????????????????
		JSONObject dataJson = JSONObject.parseObject(ExcelUtil.getcell(cell));
		String format = dataJson.getString(CommonConsts.FORMAT);
		String function = dataJson.getString(CommonConsts.FUNCTION);
		String tagetKey = dataJson.getString(CommonConsts.TARGETKEY);
		String supplyValue = JSONObject.parseObject(format).getString(CommonConsts.SUPPLYVALUE);
		Integer length = Integer.valueOf(JSONObject.parseObject(format).getString(CommonConsts.LENGTH));
		String groupby = dataJson.getString(CommonConsts.GROUPBY);

		// ?????????????????????seq
		if (!CheckParamUtil.isEmpty(groupby)) {
			seq = seqMap.get(cellValueMap.get(groupby));
		} else {
			seq = seqMap.get(CommonConsts.TARGETKEY);
		}

		if (CheckParamUtil.isEmpty(seq)) {
			if (CommonConsts.T_SPONS.equals(tableName)) {
				// ??????????????????????????????????????????????????????????????????????????????????????????spons_id
				seq = customizeMapper.getRuleValue();
			} else {

				// ????????????sql
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);
				CondtionDto condtionCode = new CondtionDto();
				List<CondtionDto> condtionList = new ArrayList<>();
				if (!CheckParamUtil.isEmpty(groupby)) {
					// ??????where???????????????
					condtionCode.setKey(groupby);
					condtionCode.setValue(cellValueMap.get(groupby));
					condtionList.add(condtionCode);
					table.setCondtions(condtionList);
				}

				// ???????????????
				List<String> fileds = new ArrayList<>();
				fileds.add(function + "(" + tagetKey + ")");
				table.setFileds(fileds);

				// ????????????????????????
				List<String> aliasNames = new ArrayList<>();
				aliasNames.add(tagetKey);
				table.setAliasNames(aliasNames);

				// ??????DB
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

		// ???????????????id
		UserInfo user = GetUserInfo.getUserInfo();
		// ??????????????????????????????sheet
		for (int k = 0; k < wookbook.getNumberOfSheets() - 3; k++) {
			// ??????????????????sheet
			HSSFSheet sheet = wookbook.getSheetAt(k + 1);
			// ???????????????????????????
			String tableName = ExcelUtil.getcell(sheet.getRow(0).getCell(2));
			// ??????????????????????????????
			if (!CommonConsts.T_SPONS_CONTACT.equals(ExcelUtil.UpperString(tableName))) {
				continue;
			}

			// ??????????????????
			String importFlg = ExcelUtil.getcell(sheet.getRow(4).getCell(2));
			if (CheckParamUtil.isEmpty(importFlg) || CommonConsts.IMPORT.equals(importFlg)) {

				// ???????????????????????????????????????
				int lastCellNum = 0;
				for (int i = 2; i < sheet.getRow(0).getLastCellNum() - 1; i++) {
					String cell = ExcelUtil.getcell(sheet.getRow(0).getCell(i));
					if (CommonConsts.LASTCELLFLG.equals(cell)) {
						lastCellNum = i;
					}
				}

				// ??????tableinfo
				TableInfoDto table = new TableInfoDto();
				table.setTableName(tableName);
				List<List<SaveOrUpdateDto>> updateDataList = new ArrayList<>();
				List<List<SaveOrUpdateDto>> insertDataList = new ArrayList<>();
				List<List<CondtionDto>> condtionListForUd = new ArrayList<>();
				Map<String, String> cellValueMap = new HashMap<String, String>();
				Map<String, String> seqMap = new HashMap<String, String>();
				String seq = null;

				// ???????????????
				TableInfoDto staffTable = new TableInfoDto();
				staffTable.setTableName(CommonConsts.T_STAFF);
				List<List<SaveOrUpdateDto>> insertStaffDataList = new ArrayList<>();
				List<List<SaveOrUpdateDto>> updateStaffDataList = new ArrayList<>();
				List<List<CondtionDto>> staffListForUd = new ArrayList<>();
				// ???????????????
				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					List<SaveOrUpdateDto> saveOrUpdateList = new ArrayList<>();
					List<CondtionDto> condtionList = new ArrayList<>();
					// ?????????????????????
					List<SaveOrUpdateDto> staffSaveOrUpdateList = new ArrayList<>();
					List<CondtionDto> staffCondtionList = new ArrayList<>();
					String staffName = null;
					String mobilePhone = null;

					// ????????????:(0:????????????1:?????????2:??????)
					String updateFlg = ExcelUtil.getcell(sheet.getRow(i).getCell(0));
					if (CommonConsts.UPDATE.equals(updateFlg) || CommonConsts.INSERT.equals(updateFlg)) {

						for (int m = 2; m < lastCellNum; m++) {
							SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
							CondtionDto condtionDto = new CondtionDto();
							// ???????????????????????????
							SaveOrUpdateDto staffSaveOrUpdateDto = new SaveOrUpdateDto();
							// ???????????????pk
							CondtionDto staffCondtionDto = new CondtionDto();

							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));
							// ?????????????????????
							String filedsValue = ExcelUtil.getcell(sheet.getRow(i).getCell(m));
							if (!CheckParamUtil.isEmpty(filedsValue)) {
								// ????????????????????????sheet
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(6).getCell(m)))) {
									// ????????????ID
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

							// ?????????????????????????????????key
							// ??????????????????????????????pk
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(9).getCell(m)))) {
								// ?????????????????????
								condtionDto.setKey(filed);
								condtionDto.setValue(filedsValue);
								condtionList.add(condtionDto);

								// ????????????????????????pk
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

							// ?????????????????????
							if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(13).getCell(m)))) {
								if (!CommonConsts.UPDATE.equals(updateFlg)
										&& CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// ???????????????????????????????????????????????????1???
									seq = getSeq(tableName, sheet.getRow(13).getCell(m), cellValueMap, seqMap);
									saveOrUpdateDto.setValue(seq);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateList.add(saveOrUpdateDto);

									// ??????seqMap
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String groupBy = dataJson.getString(CommonConsts.GROUPBY);
									// ??????????????????seq??????map????????????????????????seq???+1
									seqMap.put(cellValueMap.get(groupBy), seq);
								}
								if (!CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(10).getCell(m)))) {
									// ????????????????????????format??????
									JSONObject dataJson = JSONObject
											.parseObject(ExcelUtil.getcell(sheet.getRow(13).getCell(m)));
									String format = dataJson.getString(CommonConsts.FORMAT);
									String dateFormat = JSONObject.parseObject(format)
											.getString(CommonConsts.DATEFORAMT);
									// ???????????????format?????????
									filedsValue = ExcelUtil.getDateCell(sheet.getRow(i).getCell(m), dateFormat);
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
								}
							} else {
								// ???????????????????????????
								saveOrUpdateDto.setKey(filed);
								saveOrUpdateDto.setValue(filedsValue);
								saveOrUpdateList.add(saveOrUpdateDto);

								// ??????????????????????????????
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
								// ??????????????????????????????where??????
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
							// ???????????????
							table.setUpdateUserId(user.getStaffId());
							condtionListForUd.add(condtionList);
							updateDataList.add(saveOrUpdateList);
							// ???????????????????????????
							staffTable.setUpdateUserId(user.getStaffId());
							staffListForUd.add(staffCondtionList);
							updateStaffDataList.add(staffSaveOrUpdateList);
						} else if (CommonConsts.INSERT.equals(updateFlg)) {
							// ???????????????????????????
							table.setCreateUserId(user.getStaffId());
							table.setUpdateUserId(user.getStaffId());
							if (!CheckParamUtil.isEmpty(insertCondtions)) {
								// ?????????????????????
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

							// ???????????????staffId?????????
							String staffId = customizeMapper.getStaffId(staffName, mobilePhone);
							if (!CheckParamUtil.isEmpty(staffId)) {
								// ??????????????????????????????
								seq = staffId;
							} else {
								seq = getSponsContactStaffId(seqMap);
								seqMap.put(CommonConsts.TARGETKEY, seq);
							}

							// ??????????????????????????????????????????1???staffId
							SaveOrUpdateDto sponsContact = new SaveOrUpdateDto();
							sponsContact.setKey(CommonConsts.PREPARATION1_FILED);
							sponsContact.setValue(seq);
							saveOrUpdateList.add(sponsContact);
							insertDataList.add(saveOrUpdateList);

							// ????????????????????????????????????????????????
							SaveOrUpdateDto staffDto = new SaveOrUpdateDto();
							staffDto.setKey(CommonConsts.PREPARATION4_FILED);
							staffDto.setValue(CommonConsts.STAFF_BELONG_TYPE_2);
							staffSaveOrUpdateList.add(staffDto);
							if (!CheckParamUtil.isEmpty(staffId)) {
								// ??????????????????????????????
								staffListForUd.add(staffCondtionList);
								updateStaffDataList.add(staffSaveOrUpdateList);
							} else {
								// ???????????????staffId
								staffDto = new SaveOrUpdateDto();
								staffDto.setKey(CommonConsts.STAFF_ID);
								staffDto.setValue(seq);
								staffSaveOrUpdateList.add(staffDto);

								// ???????????????????????????
								insertStaffDataList.add(staffSaveOrUpdateList);
								staffTable.setCreateUserId(user.getStaffId());
							}
							staffTable.setUpdateUserId(user.getStaffId());
						}
					}
				}

				// TODO ?????? ?????? ????????????
				if (insertStaffDataList.size() > 0) {
					// ????????????????????????
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
					// ??????????????????????????????
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
					// ????????????????????????
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
					// ??????????????????????????????
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

		// ?????????????????????seq
		seq = seqMap.get(CommonConsts.TARGETKEY);
		if (CheckParamUtil.isEmpty(seq)) {
			// ????????????sql
			TableInfoDto table = new TableInfoDto();
			table.setTableName(CommonConsts.T_STAFF);

			// ???????????????
			List<String> fileds = new ArrayList<>();
			fileds.add(CommonConsts.MAX + "(" + CommonConsts.STAFF_ID + ")");
			table.setFileds(fileds);

			// ????????????????????????
			List<String> aliasNames = new ArrayList<>();
			aliasNames.add(CommonConsts.STAFF_ID);
			table.setAliasNames(aliasNames);

			// ??????DB
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
