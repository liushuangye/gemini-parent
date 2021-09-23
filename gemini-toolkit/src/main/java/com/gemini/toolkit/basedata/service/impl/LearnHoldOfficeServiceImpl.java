package com.gemini.toolkit.basedata.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.basedata.entity.TBasedataImportHisEntity;
import com.gemini.toolkit.basedata.service.CompanyStaffService;
import com.gemini.toolkit.basedata.service.LearnHoldOfficeService;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.login.form.UserInfo;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LearnHoldOfficeServiceImpl extends AbsDataImport implements LearnHoldOfficeService {
	private static final Logger log = LoggerFactory.getLogger(LearnHoldOfficeServiceImpl.class);

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
				for (Map.Entry<String, Map<Integer, List<CheckErrorDto>>> entry : checkErrors.entrySet()) {
					errorlist.append(entry.getKey());
					for (Map.Entry<Integer, List<CheckErrorDto>> item : entry.getValue().entrySet()) {
						errorlist.append("check通过").append(item.getKey()).append("条，");
						errorlist.append("check未通过").append(item.getValue().size()).append("条。");
					}
				}
			} else if (templateCheckError.size() > 0) {
				// 获取模板check失败信息
				for (Map.Entry<String, Map<Integer, List<CheckErrorDto>>> entry : templateCheckError.entrySet()) {
					errorlist.append(entry.getValue().get(0).get(0).getErrorMessage());
				}
			}
			return R.ok().put("isSuccess", "1").put("data", errorlist).put("uuid", uuid);
		} else {
			return R.ok().put("isSuccess", "0").put("uuid", uuid);
		}
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

				for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
					//获取区分:(0:无更新，1:更新，2:增加)
					String updateFlg = ExcelUtil.getcell(sheet.getRow(i).getCell(0));
					if (CommonConsts.INSERT.equals(updateFlg)) {
						List<SaveOrUpdateDto> insertList = new ArrayList<>();
						SaveOrUpdateDto saveOrUpdate = new SaveOrUpdateDto();
						String staffName = null;
						String mobilePhone = null;
						//循环所有列
						for (int m = 2; m <= lastCellNum - 1; m++) {
							SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
							String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));
							String value = ExcelUtil.getcell(sheet.getRow(i).getCell(m));
							if ("STAFF_NAME".equals(filed)) {
								staffName = value;
							} else if ("MOBILE_PHONE".equals(filed)) {
								mobilePhone = value;
							} else {
								saveOrUpdateDto.setKey(filed);
								saveOrUpdateDto.setValue(value);
								insertList.add(saveOrUpdateDto);
							}
						}
						String staffId = customizeMapper.getStaffId(staffName, mobilePhone);
						saveOrUpdate.setKey("STAFF_ID");
						saveOrUpdate.setValue(staffId);
						insertList.add(saveOrUpdate);
						insertDataList.add(insertList);
					}
				}


				// TODO 更新 或者 新增数据
				if (insertDataList.size() > 0) {
					List<String> insertSql = SqlBuilderHelper.batchInsertSqlBuilder(table, insertDataList, dbType);
					customizeMapper.bacthInsert(insertSql);
				}
				if (updateDataList.size() > 0) {
					List<String> updateSql = SqlBuilderHelper.batchUpdateSqlBuilder(table, updateDataList,
							condtionListForUd, dbType);
					customizeMapper.bacthUpdate(updateSql);

				}

			}
		}
	}

	@Override
	public Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook) {
		return null;
	}

	@Override
	public void afterImportData(HSSFWorkbook wookbook, String templateType, String dbType) {

	}
}



