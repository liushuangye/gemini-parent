package com.gemini.toolkit.basedata.service;

import com.gemini.toolkit.basedata.entity.TBasedataImportHisEntity;
import com.gemini.toolkit.basedata.form.ImportHisForm;
import com.gemini.toolkit.common.utils.R;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 基础数据导入履历 服务类
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
public interface TBasedataImportHisService extends IService<TBasedataImportHisEntity> {

	/**
	 * 获取基础数据导入履历
	 * 
	 * @param templateType
	 * @return
	 */
	public R getTBasedataImportHis(ImportHisForm importHisForm);
	
	/**
	 * 获取模板类型
	 * @return
	 */
	public R getTemplateType();

}
