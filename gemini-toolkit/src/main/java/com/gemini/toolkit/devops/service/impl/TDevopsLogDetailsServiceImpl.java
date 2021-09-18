package com.gemini.toolkit.devops.service.impl;

import java.util.List;

import com.gemini.toolkit.common.exception.PgApplicationException;
import com.gemini.toolkit.devops.dto.DevopsLogDetailsDto;
import com.gemini.toolkit.devops.dto.DevopsLogStepDto;
import com.gemini.toolkit.devops.entity.TDevopsLogDetailsEntity;
import com.gemini.toolkit.devops.entity.TDevopsTaskEntity;
import com.gemini.toolkit.devops.mapper.TDevopsLogDetailsMapper;
import com.gemini.toolkit.devops.mapper.TDevopsTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.devops.service.TDevopsLogDetailsService;

/**
 * <p>
 * 运维工具执行记录详细 服务实现类
 * </p>
 *
 * @author BHH
 * @since 2021-05-26
 */
@Service
public class TDevopsLogDetailsServiceImpl extends ServiceImpl<TDevopsLogDetailsMapper, TDevopsLogDetailsEntity> implements TDevopsLogDetailsService {

	@Autowired
    TDevopsTaskMapper taskMapper;
	@Autowired
	TDevopsLogDetailsMapper taskLogDetailsMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	public DevopsLogDetailsDto getLogDetails(Long id) {
		
		// 根据id查询任务
		TDevopsTaskEntity task = taskMapper.selectOne(new LambdaQueryWrapper<TDevopsTaskEntity>()
				.eq(TDevopsTaskEntity::getId, id)
				.eq(TDevopsTaskEntity::getDeleteFlg, "0"));
		
		if(task == null) {
			throw new PgApplicationException("devops.task.notFound",
					messageSource.getMessage("devops.task.notFound", null, LocaleContextHolder.getLocale()));
		}
		
		String uuid = task.getUuid();
		
		List<DevopsLogStepDto> list  = taskLogDetailsMapper.getDetails(uuid);
		
	
		DevopsLogDetailsDto  res = new DevopsLogDetailsDto();
		res.setDevopsToolName(task.getDevopsName());
		res.setDevopsDesc(task.getDeptdesc());
		
//		for (DevopsLogStepDto step : list) {
//			
//			if(StringUtils.isNotEmpty(step.getBackUpData())) {
//				
//				List<DevopsToolsBackupData>  backDataList = JSON.parseArray(step.getBackUpData(), DevopsToolsBackupData.class);
//				
//				step.setBackUpData(JSON.toJSONString(backDataList));
//			}
//			
//		}
		if (list != null && list.size() > 0) {

			res.setStepList(list);
		}
		
		return res;
	}
	
	
}
