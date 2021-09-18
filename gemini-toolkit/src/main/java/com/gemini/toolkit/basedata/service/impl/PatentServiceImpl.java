package com.gemini.toolkit.basedata.service.impl;

import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.login.form.UserInfo;
import com.gemini.toolkit.basedata.service.PatentService;
import com.gemini.toolkit.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class PatentServiceImpl extends AbsDataImport implements PatentService {
    @Override
    public Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook) {
        return null;
    }

    @Override
    public void afterImportData(HSSFWorkbook wookbook, String templateType, String dbType) {

        // 在此处根据不同的模板做特定处理
        // 获取登录者id
        UserInfo user = GetUserInfo.getUserInfo();

        // 获取要特殊处理得sheet名
        // 组织人员
        String sheetName = CommonConsts.PATENTSHEET;
        HSSFSheet sheet = wookbook.getSheet(sheetName);
        Map<String, String> paperValueMap = new HashMap<String, String>();

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
            table.setTableName("t_workflow_bill");
            List<List<SaveOrUpdateDto>> updateDataList = new ArrayList<>();
            List<List<SaveOrUpdateDto>> insertDataList = new ArrayList<>();
            List<List<CondtionDto>> condtionListForUd = new ArrayList<>();
            Map<String, String> cellValueMap = new HashMap<String, String>();
            Map<String, String> seqMap = new HashMap<String, String>();
            String seq = null;
            table.setCreateUserId(user.getStaffId());
            table.setUpdateUserId(user.getStaffId());
            // 遍历所有行
            for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
                List<SaveOrUpdateDto> saveOrUpdateList = new ArrayList<>();
                String staffName = null;
                String mobilePhone = null;
                //获取区分:(0:无更新，1:更新，2:增加)
                String updateFlg = ExcelUtil.getcell(sheet.getRow(i).getCell(0));
                if (CommonConsts.INSERT.equals(updateFlg)) {
                    //循环所有列
                    List<SaveOrUpdateDto> insertDtoList = new ArrayList<>();
                    SaveOrUpdateDto saveOrUpdateDto1 = new SaveOrUpdateDto();
                    saveOrUpdateDto1.setKey("bill_type");
                    saveOrUpdateDto1.setValue("5E");
                    insertDtoList.add(saveOrUpdateDto1);
                    SaveOrUpdateDto saveOrUpdateDto3 = new SaveOrUpdateDto();
                    saveOrUpdateDto3.setKey("preparation10");
                    saveOrUpdateDto3.setValue("00");
                    insertDtoList.add(saveOrUpdateDto3);
                    List<String> userList = new ArrayList<>();
                    for (int m = 2; m < lastCellNum - 1; m++) {
                        String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));
                        if ("ACHIEVE_NUMBER".equalsIgnoreCase(filed)) {
                            SaveOrUpdateDto saveOrUpdateDto4 = new SaveOrUpdateDto();
                            saveOrUpdateDto4.setKey("bill_no");
                            saveOrUpdateDto4.setValue(ExcelUtil.getcell(sheet.getRow(i).getCell(m)));
                            insertDtoList.add(saveOrUpdateDto4);
                            SaveOrUpdateDto saveOrUpdateDto5 = new SaveOrUpdateDto();
//                            String thesisId = customizeMapper.getThesisId(ExcelUtil.getcell(sheet.getRow(i).getCell(m)));
//                            saveOrUpdateDto5.setKey("relation_data_id");
//                            saveOrUpdateDto5.setValue(thesisId);
//                            insertDtoList.add(saveOrUpdateDto5);
                        }


                        //判断是否为用户姓名和手机号
                        if (CheckParamUtil.isEmpty(ExcelUtil.getcell(sheet.getRow(5).getCell(m)))) {
                            userList.add(ExcelUtil.getcell(sheet.getRow(i).getCell(m)));
                        }
                    }
                    SaveOrUpdateDto saveOrUpdateDto6 = new SaveOrUpdateDto();
                    saveOrUpdateDto6.setKey("submit_date");
                    String format = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                    saveOrUpdateDto6.setValue(format);
                    insertDtoList.add(saveOrUpdateDto6);
//                    String staffId = customizeMapper.getStaffId(userList.get(0), userList.get(1));
                    SaveOrUpdateDto saveOrUpdateDto2 = new SaveOrUpdateDto();
                    saveOrUpdateDto2.setKey("modifier");
//                    saveOrUpdateDto2.setValue(staffId);
                    saveOrUpdateDto2.setValue(user.getStaffId());
                    insertDtoList.add(saveOrUpdateDto2);
                    // 设置插入值
                    insertDataList.add(insertDtoList);
                }
            }
            // TODO 新增数据
            List<String> insertSql = SqlBuilderHelper.batchInsertSqlBuilder(table, insertDataList, dbType);
            customizeMapper.bacthInsert(insertSql);
        }
    }

    @Override
    public void afterImportData(HSSFWorkbook wookbook, String templateType) {

    }
}
