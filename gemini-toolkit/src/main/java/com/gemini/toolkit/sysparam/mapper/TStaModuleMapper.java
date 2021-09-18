package com.gemini.toolkit.sysparam.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gemini.toolkit.sysparam.entity.TStaModuleEntity;

/**
 * <p>
 * 树code Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
public interface TStaModuleMapper extends BaseMapper<TStaModuleEntity> {
	
	public List<TStaModuleEntity> getTreeCode(@Param("sysName") String sysName);
	
}
