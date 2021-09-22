package com.gemini.toolkit.basedata.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gemini.toolkit.common.utils.R;


/**
 * <p>
 * 基础数据导入履历 服务类
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */
public interface BasedataService {
	
	/**
	 * 导入模板
	 * 
	 * @param request
	 * @param staffId
	 * @return
	 */
	public R importXLS(MultipartHttpServletRequest request);

	/**
	 * 获取组织人员数据导出
	 * 
	 * @param String
	 * @return
	 */
	public void outPutXLS(HttpServletResponse response, String templateType);
	
	/**
	 * 文件下载
	 * @param response
	 * @param id
	 */
	public void outPutFile(HttpServletResponse response, Long id);
}
