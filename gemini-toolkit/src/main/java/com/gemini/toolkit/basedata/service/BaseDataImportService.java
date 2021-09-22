package com.gemini.toolkit.basedata.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gemini.toolkit.basedata.dto.CheckErrorDto;

public interface BaseDataImportService {

	/**
	 * 前处理
	 * @param wookbook
	 * @paramt emplateName 模板名称
	 * @return 返回每个sheet的错误信息list
	 */
	public Map<String, Map<Integer, List<CheckErrorDto>>> before(HSSFWorkbook wookbook, String templateName);		
		
	/**
	 * execl check
	 * @param wookbook
	 * @param errorSheetMap 错误信息（before处理的返回值）
	 * @return 返回每个sheet的错误信息list
	 */
	public Map<String, Map<Integer, List<CheckErrorDto>>> check(HSSFWorkbook wookbook,
                                                                Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap);
	
	/**
	 * 标记错误信息
	 * @param wookbook
	 * @param errorSheetMap
	 */
	public void markError(HSSFWorkbook wookbook, Map<String, Map<Integer, List<CheckErrorDto>>> errorSheetMap);
	
	/**
	 * 导入数据
	 * @param wookbook
	 * @param staffId
	 */
	public void importData(HSSFWorkbook wookbook);		
	/**
	 * 
	 * @param wookbook
	 * @param templateName 模板名称
	 */
	public void after(HSSFWorkbook wookbook, String templateType);
	
	/**
	 * execl check
	 * @param wookbook
	 * @param errorMap 错误信息（templateCheck处理的返回值）
	 * @return 返回模板check的错误信息list
	 */
	public Map<String, Map<Integer, List<CheckErrorDto>>> templateCheck(HSSFWorkbook wookbook,
                                                                        String templateType, String tempalteName, String templateTypeName);

	/**
	 * @param wookbook
	 * @param templateType
	 * @param tempalteName
	 * @return 判断静态模板是否正确
	 */
	public String staticVersionCheck(HSSFWorkbook wookbook,
                                     String templateType, String tempalteName);
	
	/**
	 * 导入数据
	 * @param 申办方联系人
	 * @param wookbook
	 */
	public void importSponsContactData(HSSFWorkbook wookbook);	
}
