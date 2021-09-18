package com.gemini.toolkit.basedata.mapper;

import com.gemini.toolkit.basedata.entity.TBasedataTempDownloadHisEntity;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 基础数据模板下载履历 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
public interface TBasedataTempDownloadHisMapper extends BaseMapper<TBasedataTempDownloadHisEntity> {
	
	public String getDynamicVersion(@Param("templateType") String templateType, @Param("tempalteName") String tempalteName);

}
