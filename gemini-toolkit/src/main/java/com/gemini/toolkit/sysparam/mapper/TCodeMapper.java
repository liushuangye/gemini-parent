package com.gemini.toolkit.sysparam.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gemini.toolkit.sysparam.entity.TCodeEntity;

/**
 * <p>
 * 系统配置 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
public interface TCodeMapper extends BaseMapper<TCodeEntity> {
	
	public  Page<TCodeEntity> getTCode(IPage<TCodeEntity> page, @Param("busId") String busId);
	
	public List<TCodeEntity> getTCode(@Param("busId") String busId);
	
	public Page<TCodeEntity> getTCodeAll(IPage<TCodeEntity> page, @Param("busId") String busId);
	
	public List<TCodeEntity> getTCodeAll(@Param("busId") String busId);
	
}
