package com.gemini.toolkit.devops.controller;


import java.util.Map;

import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.devops.dto.DevopsTaskDto;
import com.gemini.toolkit.devops.dto.DevopsTaskPageDto;
import com.gemini.toolkit.devops.dto.DevopsToolsExecStepDto;
import com.gemini.toolkit.log.OpLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gemini.toolkit.devops.service.TDevopsTaskService;

/**
 * <p>
 * 运维任务表 前端控制器
 * </p>
 *
 * @author BHH
 * @since 2021-05-19
 */
@RestController
@RequestMapping("/devops/task")
public class TDevopsTaskController {

	@Autowired
	TDevopsTaskService service;
	
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
		// 开始时间
		String startDate = (String)queryparam.get("startDate");
		// 结束时间
		String endDate = (String)queryparam.get("endDate");
		
		Page<DevopsTaskPageDto> page = service.page(pageNum,pageSize,templateName,startDate,endDate);
		
		return R.ok().put("data", page);
	}
	
	/**
	 * 创建任务
	 * @return
	 */
	@RequestMapping(value = "createTask",method = RequestMethod.POST)
	@OpLog(moduleName = "运维任务",opName = "创建任务")
	public R creatTask(@RequestBody DevopsTaskDto task) {
		
		boolean flg = service.createTask(task);
		
		 if(flg) {
			 return R.ok();
		 }
		return R.error("新建任务失败");
	}
	
	
	
	/**
	 * 执行任务step
	 * @return
	 */
	@RequestMapping(value = "execStep",method = RequestMethod.POST)
	@OpLog(moduleName = "运维任务",opName = "执行step")
	public R execStep(@RequestBody DevopsToolsExecStepDto dto) {
		
		Map<String, Object> result = service.execStep(dto);

		return R.ok().put("data", result);
	}
	
	
	@RequestMapping(value = "execRollback",method = RequestMethod.POST)
	@OpLog(moduleName = "运维任务",opName = "回滚step")
	public R execRollback(@RequestBody DevopsToolsExecStepDto dto) {
		
		
		 boolean flg = service.execStepRollback(dto);
		 if(flg) {
			 return R.ok();
		 }
		return R.error("回滚失败");
		
	}
	
	@RequestMapping(value = "getTaskById",method = RequestMethod.GET)
	public R getTaskById(@RequestParam("id") Long id) {
		
		Map<String, Object> result = service.getTaskById(id);
		
		return R.ok().put("data", result);
	}
	
	@RequestMapping(value = "deleteTask",method = RequestMethod.DELETE)
	@OpLog(moduleName = "运维任务",opName = "删除任务")
	public R deleteTask(@RequestParam("id") Long id) {
		
		boolean flg = service.deleteTaskById(id);
		
		 if(flg) {
			 return R.ok();
		 }
		return R.error("删除失败");
	}
}
