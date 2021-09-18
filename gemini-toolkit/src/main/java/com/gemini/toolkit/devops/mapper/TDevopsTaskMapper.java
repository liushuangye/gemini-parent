package com.gemini.toolkit.devops.mapper;

import java.util.List;
import java.util.Map;

import com.gemini.toolkit.devops.dto.DevopsTaskPageDto;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gemini.toolkit.devops.entity.TDevopsTaskEntity;

/**
 * <p>
 * 运维任务表 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-19
 */
public interface TDevopsTaskMapper extends BaseMapper<TDevopsTaskEntity> {

	
	
	public List<Map<String,Object>> execQuery(@Param("sql") String sql);
	
	public Integer execUpdate(@Param("sql") String sql);
	
	
	public Page<DevopsTaskPageDto>  getInfoPage(IPage page, @Param("taskName") String taskName,
                                                @Param("startDate") String startDate, @Param("endDate") String endDate);
	
	
	public boolean execRollback(@Param("sqlList") List<String> sqlList);
}
