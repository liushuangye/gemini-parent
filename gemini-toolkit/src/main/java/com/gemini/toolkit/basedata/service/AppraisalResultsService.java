package com.gemini.toolkit.basedata.service;

import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.common.utils.R;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;
import java.util.Map;

public interface AppraisalResultsService {
    public R importExecl(HSSFWorkbook wookbook, String templateType, String tempalteName, String templateTypeName,
                         String modelFlg, String fileName, String dbType);

    /**
     * execl check
     * @param wookbook
     * @param errorSheetMap 错误信息（模板check的返回值）
     * @return 返回每个sheet的错误信息list
     */
    public Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook);

    /**
     * @param wookbook
     * @param templateName 模板名称
     */
    public void afterImportData(HSSFWorkbook wookbook, String templateType);
}
