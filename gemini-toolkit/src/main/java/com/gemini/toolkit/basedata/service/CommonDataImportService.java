package com.gemini.toolkit.basedata.service;

import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.common.utils.R;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;
import java.util.Map;

/**
 * 数据导入接口-通用，无特殊处理
 */
public interface CommonDataImportService {
    public R importExecl(HSSFWorkbook wookbook, String templateType, String tempalteName, String templateTypeName,
                         String modelFlg, String fileName, String dbType);

    /**
     * 前置校验
     */
    public Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook);

    /**
     * 后置处理
     */
    public void afterImportData(HSSFWorkbook wookbook, String templateType);
}
