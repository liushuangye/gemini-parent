package com.gemini.toolkit.basedata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.basedata.entity.TBasedataImportHisEntity;
import com.gemini.toolkit.basedata.entity.TBasedataTempDownloadHisEntity;
import com.gemini.toolkit.basedata.mapper.CustomizeMapper;
import com.gemini.toolkit.basedata.mapper.TBasedataTempDownloadHisMapper;
import com.gemini.toolkit.basedata.service.TBasedataImportHisService;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.config.PasswordUtil;
import com.gemini.toolkit.devops.service.impl.TDevopsTaskServiceImpl;
import com.gemini.toolkit.login.form.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 基础数据导入抽象类</br>
 * 各模板继承此抽象类，并实现beforeCheck 和
 * 
 * @author jintg
 *
 */
public abstract class AbsDataImport {
	private static final Logger log = LoggerFactory.getLogger(AbsDataImport.class);

	@Autowired
    TBasedataImportHisService tBasedataImportHisService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private TBasedataTempDownloadHisMapper tBasedataTempDownloadHisMapper;

	@Autowired
    CustomizeMapper customizeMapper;

	/**
	 * 基础数据导入前处理前处理
	 * 
	 * @param wookbook
	 * @param errorSheetMap
	 * @return
	 */
	public abstract Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook);

	/**
	 * 基础数据导入后处理
	 * 
	 * @param wookbook
	 * @param templateType
	 */
	public abstract void afterImportData(HSSFWorkbook wookbook, String templateType, String dbType);

	/**
	 * execl check
	 * 
	 * @param wookbook
	 * @param errorMap 错误信息（templateCheck处理的返回值）
	 * @return 返回模板check的错误信息list
	 */

	public Map<String, Map<Integer, List<CheckErrorDto>>> templateCheck(HSSFWorkbook wookbook, String templateType,
                                                                        String tempalteName, String templateTypeName) {
		log.info(".=========> templateCheck");

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

	public String staticVersionCheck(HSSFWorkbook wookbook, String templateType, String tempalteName) {
		log.info(".=========> staticVersionCheck");
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

	public Map<String, Map<Integer, List<CheckErrorDto>>> check(HSSFWorkbook wookbook,
                                                                Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap) {
		log.info(".=========> 共通check");

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
				String userIdType = null;

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
							log.info("check："+tableName+"，第"+(i+1)+"行，"+"第"+(m+1)+"列");
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
								if (CommonConsts.USERROLE_VALUE
										.equals(ExcelUtil.getcell(sheet.getRow(15).getCell(m)))) {
									userIdType = filedsValue;
								}
							}
						}

						String userId = null;
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

	public void importData(HSSFWorkbook wookbook, String dbType) {

		log.info(".=========> 共通数据导入");

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
//					String organizeId = null;
//					String organizePrntId = null;
//					String organizeName = null;
//					String organizeType = null;

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
								if (!CommonConsts.ID.equals(filed) && !CheckParamUtil.isEmpty(filed)) {
									// 设置插入或更新的值
									saveOrUpdateDto.setKey(filed);
									saveOrUpdateDto.setValue(filedsValue);
									saveOrUpdateList.add(saveOrUpdateDto);
									// 填写了GCP信息的，t_staff.gcp_flg标记为1
									if (CommonConsts.GCP_LEVEL.equals(filed) && !CheckParamUtil.isEmpty(filedsValue)) {
										gcpFlg = true;
									}
//									if (CommonConsts.T_ORGANIZE.equals(ExcelUtil.UpperString(tableName))) {
//										if (CommonConsts.ORGANIZE_ID.equals(filed)) {
//											organizeId = filedsValue;
//										} else if (CommonConsts.ORGANIZE_PRNT_ID.equals(filed)) {
//											// 获取父组织id
//											organizePrntId = filedsValue;
//										} else if (CommonConsts.ORGANIZE_NAME.equals(filed)) {
//											// 获取组织名称
//											organizeName = filedsValue;
//										} else if (CommonConsts.ORGANIZE_TYPE.equals(filed)) {
//											// 获取组织类型
//											organizeType = filedsValue;
//										}
//									}
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
							if (CommonConsts.T_STAFF.equals(ExcelUtil.UpperString(tableName))) {
								// 填写了GCP信息的，t_staff.gcp_flg标记为1
								if (gcpFlg) {
									SaveOrUpdateDto staffDto = new SaveOrUpdateDto();
									staffDto.setKey(CommonConsts.GCP_FLG);
									staffDto.setValue(CommonConsts.GCP_FLG_1);
									saveOrUpdateList.add(staffDto);
								}
							}
//							if (CommonConsts.T_ORGANIZE.equals(ExcelUtil.UpperString(tableName))
//									&& !CheckParamUtil.isEmpty(organizeType)) {
//								saveOrUpdateList = getPrntOrgList(organizePrntId, organizeType, organizeId,
//										organizeName, saveOrUpdateList);
//							}
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

//							if (CommonConsts.T_ORGANIZE.equals(ExcelUtil.UpperString(tableName))
//									&& !CheckParamUtil.isEmpty(organizeType)) {
//								saveOrUpdateList = getPrntOrgList(organizePrntId, organizeType, organizeId,
//										organizeName, saveOrUpdateList);
//							}
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

	/**
	 * @param organizePrntId
	 * @param organizeType
	 * @param organizeId
	 * @param organizeName
	 * @param saveOrUpdateDtoList
	 * @return
	 */
	public List<SaveOrUpdateDto> getPrntOrgList(String organizePrntId, String organizeType, String organizeId,
                                                String organizeName, List<SaveOrUpdateDto> saveOrUpdateDtoList) {
		// 追加备用字段4-6
		SaveOrUpdateDto preparation4Dto = new SaveOrUpdateDto();
		SaveOrUpdateDto preparation5Dto = new SaveOrUpdateDto();
		preparation4Dto.setKey(CommonConsts.PREPARATION4_FILED);
		preparation5Dto.setKey(CommonConsts.PREPARATION5_FILED);

		// 判断增加组织是否存在父组织
		if (!CheckParamUtil.isEmpty(organizePrntId)) {
			// 备用字段4获取逻辑：递归获取新增组织的所有父组织的id，并拼接成*.*.*格式
			String prntOrgId = getPrntOrgId(organizeId, organizeType, organizePrntId);
			preparation4Dto.setValue(prntOrgId);
			// 备用字段5获取逻辑：递归获取新增组织的所有父组织名称，并拼接成*·*·*格式
			String prntOrgName = getPrntOrgName(organizeName, organizeType, organizePrntId);
			preparation5Dto.setValue(prntOrgName);
		} else {
			// 父组织为空，组织编号是自己的组织编号
			preparation4Dto.setValue(organizeId);
			preparation5Dto.setValue(organizeName);
		}
		saveOrUpdateDtoList.add(preparation4Dto);
		saveOrUpdateDtoList.add(preparation5Dto);
		return saveOrUpdateDtoList;
	}

	/**
	 * @param organizeId
	 * @param organizeType
	 * @param organizePrntId
	 * @return
	 */
	public String getPrntOrgId(String organizeId, String organizeType, String organizePrntId) {
		String orgId = "";
		// 根据添加组织的父组织id获取父组织
		Map<String, Object> organizeDataMap = customizeMapper.getOrganizeResults(organizePrntId);

		// 判断增加组织存在父组织并且父组织不是根节点(专业)
		if ("021".equals(organizeType)) {
			if (!CheckParamUtil.isEmpty(organizeDataMap)
					&& ((String) organizeDataMap.get(CommonConsts.ORGANIZE_TYPE)).length() == 3) {
				// 拼接组织编号
				orgId = organizeDataMap.get(CommonConsts.ORGANIZE_ID) + "." + organizeId;
				// 获取添加组织的父组织的父组织id
				String organizePrntId2 = (String) organizeDataMap.get(CommonConsts.ORGANIZE_PRNT_ID);
				// 父组织的父组织不为空，递归调用
				if (!CheckParamUtil.isEmpty(organizePrntId2)) {
					// 继续查询是否存在父组织并拼接组织编号
					orgId = getPrntOrgId(orgId, organizeType, organizePrntId2);
				}
			} else {// 如果增加组织不存在父组织或者父组织为根节点（专业），停止递归
				orgId = organizeId;
			}
		} else if ("01".equals(organizeType)) {
			if (!CheckParamUtil.isEmpty(organizeDataMap)
					&& !"0".equals(organizeDataMap.get(CommonConsts.ORGANIZE_TYPE))) {
				// 拼接组织编号
				orgId = organizeDataMap.get(CommonConsts.ORGANIZE_ID) + "." + organizeId;
				// 获取添加组织的父组织的父组织id
				String organizePrntId2 = (String) organizeDataMap.get(CommonConsts.ORGANIZE_PRNT_ID);
				// 父组织的父组织不为空，递归调用
				if (!CheckParamUtil.isEmpty(organizePrntId2)) {
					// 继续查询是否存在父组织并拼接组织编号
					orgId = getPrntOrgId(orgId, organizeType, organizePrntId2);
				}
			} else {// 如果增加组织不存在父组织或者父组织为根节点（专业），停止递归
				orgId = organizeId;
			}
		} else {
			orgId = organizeId;
		}
		return orgId;
	}

	/**
	 * @param organizeName
	 * @param organizeType
	 * @param organizePrntId
	 * @return
	 */
	public String getPrntOrgName(String organizeName, String organizeType, String organizePrntId) {
		String orgName = "";
		// 根据添加组织的父组织名称获取父组织
		Map<String, Object> organizeDataMap = customizeMapper.getOrganizeResults(organizePrntId);
		// 判断增加组织存在父组织并且父组织不是根节点(专业)
		if ("021".equals(organizeType)) {
			if (!CheckParamUtil.isEmpty(organizeDataMap)
					&& ((String) organizeDataMap.get(CommonConsts.ORGANIZE_TYPE)).length() == 3) {
				// 拼接组织名称
				orgName = organizeDataMap.get(CommonConsts.ORGANIZE_NAME) + "." + organizeName;
				// 获取添加组织的父组织的父组织名称
				String organizePrntId2 = (String) organizeDataMap.get(CommonConsts.ORGANIZE_PRNT_ID);
				// 父组织的父组织不为空，递归调用
				if (!CheckParamUtil.isEmpty(organizePrntId2)) {
					// 继续查询是否存在父组织并拼接组织名称
					orgName = getPrntOrgName(orgName, organizeType, organizePrntId2);
				}
			} else {// 如果增加组织不存在父组织或者父组织为根节点（专业），停止递归
				orgName = organizeName;
			}
		} else if ("01".equals(organizeType)) {
			if (!CheckParamUtil.isEmpty(organizeDataMap)
					&& !"0".equals(organizeDataMap.get(CommonConsts.ORGANIZE_TYPE))) {
				// 拼接组织名称
				orgName = organizeDataMap.get(CommonConsts.ORGANIZE_NAME) + "." + organizeName;
				// 获取添加组织的父组织的父组织名称
				String organizePrntId2 = (String) organizeDataMap.get(CommonConsts.ORGANIZE_PRNT_ID);
				// 父组织的父组织不为空，递归调用
				if (!CheckParamUtil.isEmpty(organizePrntId2)) {
					// 继续查询是否存在父组织并拼接组织名称
					orgName = getPrntOrgName(orgName, organizeType, organizePrntId2);
				}
			} else {// 如果增加组织不存在父组织或者父组织为根节点（专业），停止递归
				orgName = organizeName;
			}
		} else {
			orgName = organizeName;
		}
		return orgName;
	}

	/**
	 * @param wookbook
	 * @param errorSheetMap
	 */
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
	 * 基础数据导入
	 *
	 * @param wookbook
	 * @param errorSheetMap
	 * @param templateType
	 * @param tempalteName
	 * @param templateTypeName
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public R importExecl(HSSFWorkbook wookbook, String templateType, String tempalteName, String templateTypeName,
                         String modelFlg, String fileName, String dbType) {

		log.info(".=========> 基础数据导入 start");

		Map<String, Map<Integer, List<CheckErrorDto>>> checkErrors = new HashMap<String, Map<Integer, List<CheckErrorDto>>>();

		// 模板check
		Map<String, Map<Integer, List<CheckErrorDto>>> templateCheckError = templateCheck(wookbook, templateType,
				tempalteName, templateTypeName);

		boolean fail = true;
		String uuid = null;

		if (templateCheckError.size() > 0) {
			if (templateCheckError.containsKey("check")) {
				// 标记错误信息
				markError(wookbook, templateCheckError);
			}
		} else {

			if ("0".equals(modelFlg)) {
				String check = staticVersionCheck(wookbook, templateType, tempalteName);
				if (!CheckParamUtil.isEmpty(check)) {
					return R.ok().put("isSuccess", "2").put("data", check);
				}
			}

			// 前处理
			Map<String, Map<Integer, List<CheckErrorDto>>> errorMap = beforeCheck(wookbook);

			// check处理，map中存放sheetName，check成功条数，check异常信息
			checkErrors = check(wookbook, errorMap);

			// 如果有异常则标记到execl中,并将execl保存到数据库中
			if (checkErrors.size() > 0) {
				// 标记错误信息
				markError(wookbook, checkErrors);

			} else {
				fail = false;
				// 没有异常,全量数据导入
				importData(wookbook, dbType);

				// 后处理
				afterImportData(wookbook, templateType, dbType);

			}
		}
		if (!templateCheckError.containsKey("templateTypeError")) {
			// 获取byte数组
			byte[] res = ExcelUtil.workbook2ByteArray(wookbook);
			// 获取登录者id
			UserInfo user = GetUserInfo.getUserInfo();
			// 保存上传履历
			TBasedataImportHisEntity tBasedataImportHisEntity = new TBasedataImportHisEntity();
			tBasedataImportHisEntity.setTemplateType(templateType);
			tBasedataImportHisEntity.setTempalteName(fileName);
			tBasedataImportHisEntity.setFileContent(res);
			if (fail) {
				tBasedataImportHisEntity.setState(CommonConsts.FAILURE);
			} else {
				tBasedataImportHisEntity.setState(CommonConsts.SUCCESS);
			}

			tBasedataImportHisEntity.setCreateUserId(user.getStaffId());
			Date nowtime = new Date();

			tBasedataImportHisEntity.setCreateDateTime(nowtime);
			tBasedataImportHisEntity.setUpdateUserId(user.getStaffId());
			tBasedataImportHisEntity.setUpdateDateTime(nowtime);
			tBasedataImportHisEntity.setUpdateKey(0);
			tBasedataImportHisEntity.setDeleteFlg("0");
			uuid = UUID.randomUUID().toString();
			tBasedataImportHisEntity.setUuid(uuid);

			tBasedataImportHisService.save(tBasedataImportHisEntity);
		}

		// check失败
		if (fail) {
			StringBuilder errorlist = new StringBuilder();
			if (checkErrors.size() > 0) {

				// 获取数据check失败信息
				for (Entry<String, Map<Integer, List<CheckErrorDto>>> entry : checkErrors.entrySet()) {
					errorlist.append(entry.getKey());
					for (Entry<Integer, List<CheckErrorDto>> item : entry.getValue().entrySet()) {
						errorlist.append("check通过").append(item.getKey()).append("条，");
						errorlist.append("check未通过").append(item.getValue().size()).append("条。");
					}
				}
			} else if (templateCheckError.size() > 0) {
				// 获取模板check失败信息
				for (Entry<String, Map<Integer, List<CheckErrorDto>>> entry : templateCheckError.entrySet()) {
					errorlist.append(entry.getValue().get(0).get(0).getErrorMessage());
				}
			}
			return R.ok().put("isSuccess", "1").put("data", errorlist).put("uuid", uuid);
		} else {
			return R.ok().put("isSuccess", "0").put("uuid", uuid);
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

}
