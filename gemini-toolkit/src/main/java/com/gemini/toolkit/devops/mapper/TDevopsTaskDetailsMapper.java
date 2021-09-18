package com.gemini.toolkit.devops.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gemini.toolkit.devops.entity.TDevopsTaskDetailsEntity;

/**
 * <p>
 * 运维任务详细表 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-19
 */
public interface TDevopsTaskDetailsMapper extends BaseMapper<TDevopsTaskDetailsEntity> {

	
	
	public Integer getNoRollBack(@Param("stepId") Integer stepId, @Param("uuid") String uuid);
	
}
