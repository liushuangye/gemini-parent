package com.gemini.toolkit.devops.mapper;

import java.util.List;
import java.util.Map;

import com.gemini.toolkit.devops.dto.DevopsToolsPageDto;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gemini.toolkit.devops.entity.TDevopsToolsEntity;

/**
 * <p>
 * 运维工具列表 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */
public interface TDevopsToolsMapper extends BaseMapper<TDevopsToolsEntity> {

	/**
	 * 分页查询
	 * @param page
	 * @param templateName
	 * @return
	 */
	public Page<DevopsToolsPageDto>  getInfoPage(IPage page, @Param("templateName") String templateName);
	
	
	public boolean updateByUpdateKey(TDevopsToolsEntity entity);

	public List<Map<String,String>> getAllDevops();
}
