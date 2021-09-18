package com.gemini.toolkit.devops.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gemini.toolkit.devops.dto.DevopsTaskDto;
import com.gemini.toolkit.devops.dto.DevopsTaskPageDto;
import com.gemini.toolkit.devops.dto.DevopsToolsExecStepDto;
import com.gemini.toolkit.devops.entity.TDevopsTaskEntity;

/**
 * <p>
 * 运维任务表 服务类
 * </p>
 *
 * @author BHH
 * @since 2021-05-19
 */
public interface TDevopsTaskService extends IService<TDevopsTaskEntity> {

	
	/**
	 * 执行运维
	 * @param dto
	 * @return
	 */
	public Map<String, Object> execStep(DevopsToolsExecStepDto dto);
	
	
	/**
	 * 创建任务
	 * @param task
	 * @return
	 */
	public boolean createTask(DevopsTaskDto task);
	
	/**
	 * 修改任务
	 * @param task
	 * @return
	 */
	public boolean updateTask(DevopsTaskDto task);
	
	/**
	 * 回滚
	 * @param dto
	 * @return
	 */
	public boolean execStepRollback(DevopsToolsExecStepDto dto);
	
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @param templateName 工具模板名称
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return
	 */
	public Page<DevopsTaskPageDto> page(Integer pageNum, Integer pageSize, String templateName, String startDate, String endDate);
	
	
	/**
	 * 打开任务详情(需要查询出前次的执行记录)
	 * @param id
	 * @return 
	 */
	public Map<String, Object> getTaskById(Long id);

	/**
	 * 删除任务
	 * 创建后异质性过更新或者回滚的任务不可被删除。
	 * @param id
	 * @return
	 */
	public boolean deleteTaskById(Long id);
}
