package com.gemini.toolkit.devops.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gemini.toolkit.devops.dto.DevopsFileUploadDto;
import com.gemini.toolkit.devops.dto.DevopsToolsDetailDto;
import com.gemini.toolkit.devops.dto.DevopsToolsPageDto;
import com.gemini.toolkit.devops.entity.TDevopsToolsEntity;

/**
 * <p>
 * 运维工具列表 服务类
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */
public interface TDevopsToolsService extends IService<TDevopsToolsEntity> {

	
	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @param templateName
	 * @return
	 */
	public Page<DevopsToolsPageDto> page(Integer pageNum, Integer pageSize, String templateName);
	
	
	public boolean saveData(DevopsFileUploadDto uploadFile);
	
	public boolean updateData(DevopsFileUploadDto uploadFile);

	
//	public  Map<String,Object>  execStep(DevopsToolsExecStepDto setp);
	
	public DevopsToolsDetailDto getDetailById(Integer id);
	
	public boolean deleteToolkitById(Long id);
	
	public List<Map<String,String>> getAllDevops();
}
