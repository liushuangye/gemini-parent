package com.gemini.toolkit.sysparam.service;

import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.sysparam.entity.TStaModuleEntity;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 树code 服务类
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
public interface TStaModuleService extends IService<TStaModuleEntity> {

	/**
	 * 获取树
	 * @param sysName
	 * @return
	 */
	public R getTreeCode(String sysName);
	
	/**
	 * 获取busId
	 * @param sysName
	 * @return
	 */
	public String getTreeCodeString(String sysName);
	
}
