package com.gemini.toolkit.basedata.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.dto.CondtionDto;
import com.gemini.toolkit.basedata.dto.SaveOrUpdateDto;
import com.gemini.toolkit.basedata.dto.TableInfoDto;
import com.gemini.toolkit.basedata.service.AcademicPapersService;
import com.gemini.toolkit.common.exception.MyException;
import com.gemini.toolkit.common.utils.*;
import com.gemini.toolkit.login.form.UserInfo;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class AcademicPapersServiceImpl extends AbsDataImport implements AcademicPapersService {
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
        String sheetName = CommonConsts.ACADEMIC_PAPERSSHEET;
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
            List<List<SaveOrUpdateDto>> insertAchList = new ArrayList<>();
            List<List<SaveOrUpdateDto>> insertBillList = new ArrayList<>();
            List<List<SaveOrUpdateDto>> insertUnitList = new ArrayList<>();
            // 构建tableinfo
            TableInfoDto billTable = new TableInfoDto();
            billTable.setTableName("t_workflow_bill");
            billTable.setCreateUserId(user.getStaffId());
            billTable.setUpdateUserId(user.getStaffId());
            TableInfoDto achTable = new TableInfoDto();
            achTable.setTableName("t_ach_author");
            achTable.setCreateUserId(user.getStaffId());
            achTable.setUpdateUserId(user.getStaffId());
            TableInfoDto unitTable = new TableInfoDto();
            unitTable.setTableName("t_ach_unit");
            unitTable.setCreateUserId(user.getStaffId());
            unitTable.setUpdateUserId(user.getStaffId());
            // 遍历所有行
            for (int i = 16; i < sheet.getLastRowNum() + 1; i++) {
                //获取区分:(0:无更新，1:更新，2:增加)
                String updateFlg = ExcelUtil.getcell(sheet.getRow(i).getCell(0));
                if (CommonConsts.INSERT.equals(updateFlg)) {
                    List<SaveOrUpdateDto> insertWorkflowList = new ArrayList<>();
                    List<SaveOrUpdateDto> insertAuthorList = new ArrayList<>();
                    billUtil(user, insertWorkflowList,insertAuthorList);
                    List<String> userList = new ArrayList<>();
                    //循环所有列
                    for (int m = 2; m < lastCellNum - 1; m++) {
                        String filed = ExcelUtil.getcell(sheet.getRow(5).getCell(m));
                        String value = ExcelUtil.getcell(sheet.getRow(i).getCell(m));
                        if ("ACHIEVE_NUMBER".equalsIgnoreCase(filed)) {


                            /*List<Map<String, Object>> trials = customizeMapper.getResults("select trial_id from " + tableName + " where achieve_number='"
                                    + value + "'");
                            if (CheckParamUtil.isEmpty(trials.get(0).get("trial_id"))) {
                                customizeMapper.bacthUpdateOracle("update" + tableName + "set trial_id = '' where achieve_number='" + value + "'");
                            }*/
                            customizeMapper.bacthUpdateOracle("update " + tableName + " set trial_id = '' where achieve_number='" + value + "'");
                            SaveOrUpdateDto saveOrUpdateDto = new SaveOrUpdateDto();
                            saveOrUpdateDto.setKey("bill_no");
                            saveOrUpdateDto.setValue(value);
                            insertWorkflowList.add(saveOrUpdateDto);

                            SaveOrUpdateDto author = new SaveOrUpdateDto();
                            author.setValue(value);
                            author.setKey("ACHIEVE_NUMBER");
                            insertAuthorList.add(author);

                            SaveOrUpdateDto saveOrUpdateDto5 = new SaveOrUpdateDto();
                            List<Map<String, Object>> results = customizeMapper.getResults("select * from " + tableName + " where achieve_number='"
                                    + value+"'");
                            for (Map<String, Object> result : results) {
                                Set<String> keys = result.keySet();
                                if (result.containsKey("id")) {
                                    Long id = (Long) result.get("id");
                                    saveOrUpdateDto5.setValue(Long.toString(id));
                                }
                            }
                            saveOrUpdateDto5.setKey("relation_data_id");
                            insertWorkflowList.add(saveOrUpdateDto5);
                        }

                        //判断是否为用户姓名和手机号
                        if (CheckParamUtil.isEmpty(filed)) {
                            userList.add(value);
                        }


                        if ("SIGN_UNIT".equalsIgnoreCase(filed)) {
                            if (!CheckParamUtil.isEmpty(value)) {
                                String[] authors = value.split("，");
                                for (int j = 0; j < authors.length; j++) {
                                    List<SaveOrUpdateDto> unitList = new ArrayList<>();
                                    unitList.addAll(insertAuthorList);
                                    SaveOrUpdateDto unit1 = new SaveOrUpdateDto();
                                    unit1.setValue(j+1+"");
                                    unit1.setKey("order_by");
                                    unitList.add(unit1);
                                    SaveOrUpdateDto unit = new SaveOrUpdateDto();
                                    if (value.contains("附属")) {
                                        unit.setValue("10");
                                    } else {
                                        unit.setValue("20");
                                        SaveOrUpdateDto unit2 = new SaveOrUpdateDto();
                                        unit2.setValue(authors[j]);
                                        unit2.setKey("unit_name");
                                        unitList.add(unit2);
                                    }
                                    unit.setKey("this_unit_flag");
                                    unitList.add(unit);
                                    insertUnitList.add(unitList);
                                }
                            }
                        }


                        String ach = ExcelUtil.getcell(sheet.getRow(15).getCell(m));
                        if (ach.contains("作者")) {
                            if (!CheckParamUtil.isEmpty(value)) {
                                String[] authors = value.split("，");
                                for (int j = 0; j < authors.length; j++) {
                                    List<SaveOrUpdateDto> authorList = new ArrayList<>();
                                    authorList.addAll(insertAuthorList);
                                    SaveOrUpdateDto author1 = new SaveOrUpdateDto();
                                    author1.setValue(j+1+"");
                                    author1.setKey("order_by");
                                    authorList.add(author1);
                                    SaveOrUpdateDto author = new SaveOrUpdateDto();
                                    author.setValue(filed.replace("_",""));
                                    author.setKey("author_field_name");
                                    authorList.add(author);
                                    List<String> staffIds = customizeMapper.getStaffIds(authors[j]);
                                    if (staffIds != null && staffIds.size() == 1) {
                                        SaveOrUpdateDto author2 = new SaveOrUpdateDto();
                                        author2.setValue(staffIds.get(0));
                                        author2.setKey("staff_id");
                                        authorList.add(author2);
                                        insertAchList.add(authorList);
                                    } else {
                                        throw new MyException("姓名不确定");
                                    }

                                }
                            }
                        }

                    }
                    // 设置插入值
                    insertBillList.add(insertWorkflowList);

                }
            }

//            if () {
//            }

            // TODO 新增t_workflow_bill数据
            List<String> insertBillSql = SqlBuilderHelper.batchInsertSqlBuilder(billTable, insertBillList, dbType);
            customizeMapper.bacthInsert(insertBillSql);

            // TODO 新增t_ach_author数据
            List<String> insertAchSql = SqlBuilderHelper.batchInsertSqlBuilder(achTable, insertAchList, dbType);
            customizeMapper.bacthInsert(insertAchSql);

            // TODO 新增t_ach_unit数据
            List<String> insertUnitSql = SqlBuilderHelper.batchInsertSqlBuilder(unitTable, insertUnitList, dbType);
            customizeMapper.bacthInsert(insertUnitSql);

        }
    }

    private void billUtil(UserInfo user, List<SaveOrUpdateDto> insertWorkflowList,
                          List<SaveOrUpdateDto> insertAuthorList) {
        SaveOrUpdateDto saveOrUpdateDto1 = new SaveOrUpdateDto();
        saveOrUpdateDto1.setKey("bill_type");
        saveOrUpdateDto1.setValue("5A");
        insertWorkflowList.add(saveOrUpdateDto1);
        SaveOrUpdateDto saveOrUpdateDto3 = new SaveOrUpdateDto();
        saveOrUpdateDto3.setKey("preparation10");
        saveOrUpdateDto3.setValue("30");
        insertWorkflowList.add(saveOrUpdateDto3);

        SaveOrUpdateDto saveOrUpdateDto4 = new SaveOrUpdateDto();
        saveOrUpdateDto4.setKey("review_result1");
        saveOrUpdateDto4.setValue("06");
        insertWorkflowList.add(saveOrUpdateDto4);
        SaveOrUpdateDto saveOrUpdateDto6 = new SaveOrUpdateDto();
        saveOrUpdateDto6.setKey("submit_date");
        String format = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        saveOrUpdateDto6.setValue(format);
        insertWorkflowList.add(saveOrUpdateDto6);
        SaveOrUpdateDto saveOrUpdateDto2 = new SaveOrUpdateDto();
        saveOrUpdateDto2.setKey("modifier");
        saveOrUpdateDto2.setValue(user.getStaffId());
        insertWorkflowList.add(saveOrUpdateDto2);

        SaveOrUpdateDto author = new SaveOrUpdateDto();
        author.setKey("ach_type");
        author.setValue("5A");
        insertAuthorList.add(author);
    }

    @Override
    public void afterImportData(HSSFWorkbook wookbook, String templateType) {

    }


}
