package com.gemini.toolkit.basedata.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gemini.toolkit.basedata.entity.TBasedataImportHisEntity;

/**
 * <p>
 * 基础数据导入履历 Mapper 接口
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
public interface TBasedataImportHisMapper extends BaseMapper<TBasedataImportHisEntity> {

	public Page<TBasedataImportHisEntity> getTBasedataImportHis(IPage<TBasedataImportHisEntity> page, @Param("templateType") String templateType);

}
