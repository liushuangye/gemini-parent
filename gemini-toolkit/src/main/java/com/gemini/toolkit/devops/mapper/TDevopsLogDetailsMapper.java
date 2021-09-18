package com.gemini.toolkit.devops.mapper;

import com.gemini.toolkit.devops.dto.DevopsLogStepDto;
import com.gemini.toolkit.devops.entity.TDevopsLogDetailsEntity;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 运维工具执行记录详细 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-26
 */
public interface TDevopsLogDetailsMapper extends BaseMapper<TDevopsLogDetailsEntity> {

	/**
	 * 查询执行日志详细
	 * @return
	 */
	public List<DevopsLogStepDto> getDetails(@Param("uuid") String uuid);
	
}
