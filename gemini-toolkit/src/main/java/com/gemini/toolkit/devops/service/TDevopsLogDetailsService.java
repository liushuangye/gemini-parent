package com.gemini.toolkit.devops.service;

import com.gemini.toolkit.devops.dto.DevopsLogDetailsDto;
import com.gemini.toolkit.devops.entity.TDevopsLogDetailsEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 运维工具执行记录详细 服务类
 * </p>
 *
 * @author BHH
 * @since 2021-05-26
 */
public interface TDevopsLogDetailsService extends IService<TDevopsLogDetailsEntity> {

	
	public DevopsLogDetailsDto getLogDetails(Long id);
}
