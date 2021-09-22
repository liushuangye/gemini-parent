package com.gemini.toolkit.basedata.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gemini.toolkit.basedata.dto.CheckErrorDto;

public interface DataImportService {


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
	
	
}
