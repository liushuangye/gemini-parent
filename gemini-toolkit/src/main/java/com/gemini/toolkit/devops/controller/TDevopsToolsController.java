package com.gemini.toolkit.devops.controller;


import java.util.List;
import java.util.Map;

import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.devops.dto.DevopsFileUploadDto;
import com.gemini.toolkit.devops.dto.DevopsToolsDetailDto;
import com.gemini.toolkit.devops.dto.DevopsToolsPageDto;
import com.gemini.toolkit.log.OpLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gemini.toolkit.devops.service.TDevopsToolsService;

/**
 * <p>
 * 运维工具列表 前端控制器
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */
@RestController
@RequestMapping("/devops")
public class TDevopsToolsController {
	
	@Autowired
	TDevopsToolsService service;
	
	/**
	 * 分页查询工具集
	 * @param queryparam
	 * @return
	 */
	@RequestMapping(value = "getByPage",method = RequestMethod.POST)
	public R getByPage(@RequestBody  Map<String,Object> queryparam) {
		
		Integer pageNum = (Integer) queryparam.get("pageNum");
		if(pageNum == null) {
			pageNum = 0;
		}
		// 每页显示条数
		Integer pageSize = (Integer) queryparam.get("pageSize");
		
		if(pageSize == null) {
			pageSize = 30;
		}
		// 模板名称
		String templateName =  (String)queryparam.get("templateName");
		
		Page<DevopsToolsPageDto> page = service.page(pageNum,pageSize,templateName);
		
		return R.ok().put("data", page);
		
	}
	
	/**
	 * 查询指定id的运维工具
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "getById",method = RequestMethod.GET)
	public R getById(@RequestParam("id")  Integer id) {
		
		DevopsToolsDetailDto result = service.getDetailById(id);
		
		return R.ok().put("data", result);
		
	}
	
	/**
	 * 上传运维工具
	 * @param uploadFile
	 * @return
	 */
	@RequestMapping(value = "importTemplate",method = RequestMethod.POST)
	@OpLog(moduleName = "运维工具集",opName = "导入工具")
	public R importTemplate( DevopsFileUploadDto uploadFile) {
		
		boolean result = service.saveData(uploadFile);
		
		if(result) {
			return R.ok();
		}
		
		return  R.error("上传失败,请刷新后重试");
		
	}
	
	
	@RequestMapping(value = "updateTemplate",method = RequestMethod.POST)
	@OpLog(moduleName = "运维工具集",opName = "更新工具")
	public R updateTemplate( DevopsFileUploadDto uploadFile) {
		
		boolean result = service.updateData(uploadFile);
		
		if(result) {
			return R.ok();
		}
		
		return  R.error("更新失败,请刷新后重试");
		
	}
	
	@RequestMapping(value = "getAllDevops",method = RequestMethod.GET)
	public R getAllDevops() {
		
		List<Map<String, String>> result = service.getAllDevops();
		
		
		return  R.ok().put("data", result);
	}
	@RequestMapping(value = "deleteToolkitById",method = RequestMethod.DELETE)
	@OpLog(moduleName = "运维工具集",opName = "删除工具")
	public R deleteToolkitById( @RequestParam("id") Long id) {
		
		boolean result = service.deleteToolkitById(id);
		if(result) {
			return R.ok();
		}
		
		return  R.error("删除失败,请刷新后重试");
		
	}
		
	
}
