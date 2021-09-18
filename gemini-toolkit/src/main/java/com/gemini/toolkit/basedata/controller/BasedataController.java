package com.gemini.toolkit.basedata.controller;

import javax.servlet.http.HttpServletResponse;

import com.gemini.toolkit.basedata.form.ImportHisForm;
import com.gemini.toolkit.basedata.service.BasedataService;
import com.gemini.toolkit.basedata.service.OrganizeStaffService;
import com.gemini.toolkit.basedata.service.TBasedataImportHisService;
import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.log.OpLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * <p>
 * 基础数据导入履历 前端控制器
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
@RestController
@RequestMapping("/basedata")
public class BasedataController {
	
	@Autowired
    OrganizeStaffService organizeStaffService;
	
	
	@Autowired
	private TBasedataImportHisService tBasedataImportHisService;
	
	@Autowired
	private BasedataService basedataService;
	
	@PostMapping("/getTBasedataImportHis")
	public R getTBasedataImportHis(@Validated @RequestBody ImportHisForm importHisForm) {
		return tBasedataImportHisService.getTBasedataImportHis(importHisForm);
	}
	
	@GetMapping("/getTemplateType")
	public R getTemplateType() {
		return tBasedataImportHisService.getTemplateType();
	}
	
	@RequestMapping(value = "/importXLS", method = RequestMethod.POST)
	@OpLog(moduleName = "基础数据导入履历", opName = "数据导入")
	public R importXLS(MultipartHttpServletRequest request) {
		return basedataService.importXLS(request);
	}
	
	@RequestMapping(value = "/outPutXLS", method = RequestMethod.POST)
	@OpLog(moduleName = "基础数据导入履历", opName = "模板下载")
	public void outPutXLS(HttpServletResponse response, @Validated @RequestBody ImportHisForm importHisForm) {
		basedataService.outPutXLS(response, importHisForm.getTemplateType());
	}
	
	@RequestMapping(value = "/outPutFile", method = RequestMethod.GET)
	@OpLog(moduleName = "基础数据导入履历", opName = "文件下载")
	public void outPutFile(HttpServletResponse response, Long id) {
		basedataService.outPutFile(response, id);
	}
	
}
