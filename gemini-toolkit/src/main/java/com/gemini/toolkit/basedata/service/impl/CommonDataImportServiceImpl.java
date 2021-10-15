package com.gemini.toolkit.basedata.service.impl;

import com.gemini.toolkit.basedata.dto.CheckErrorDto;
import com.gemini.toolkit.basedata.service.CommonDataImportService;
import com.gemini.toolkit.common.utils.R;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonDataImportServiceImpl extends AbsDataImport implements CommonDataImportService {

    @Override
    public Map<String, Map<Integer, List<CheckErrorDto>>> beforeCheck(HSSFWorkbook wookbook) {
        return null;
    }

    @Override
    public void afterImportData(HSSFWorkbook wookbook, String templateType, String dbType) {

    }

    @Override
    public void afterImportData(HSSFWorkbook wookbook, String templateType) {

    }
}
