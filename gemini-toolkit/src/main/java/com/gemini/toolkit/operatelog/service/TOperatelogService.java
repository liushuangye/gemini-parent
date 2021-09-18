package com.gemini.toolkit.operatelog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gemini.toolkit.log.entity.TToolkitOplogEntity;
import com.gemini.toolkit.operatelog.dto.TOplogDto;

public interface TOperatelogService extends IService<TToolkitOplogEntity>{

	IPage<TOplogDto> getOpLog(TOplogDto oplogDtoIn, IPage page);

}
