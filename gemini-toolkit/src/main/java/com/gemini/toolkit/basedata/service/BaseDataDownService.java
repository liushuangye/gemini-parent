package com.gemini.toolkit.basedata.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface BaseDataDownService {

	/**
	 * @param sheetNameList
	 * @param workBook
	 * @return 需要处理得sheetName
	 */
	public List<String> sheetCheck(HSSFWorkbook workBook);
	
	/**
	 * @param sheetNameList
	 * @param workBook
	 */
	public void downData(List<String> sheetNameList, HSSFWorkbook workBook);
	
	/**
	 * @param sheetNameList
	 * @param workBook
	 */
	public void downafterData(List<String> sheetNameList, HSSFWorkbook workBook);
}
