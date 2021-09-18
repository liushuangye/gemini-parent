package com.gemini.toolkit.sysparam.service;

import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.sysparam.entity.TCodeEntity;
import com.gemini.toolkit.sysparam.form.CodeForm;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统配置 服务类
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
public interface TCodeService extends IService<TCodeEntity> {
	/**
	 * 获取系统配置
	 * @param busId
	 * @return
	 */
	public R getTCode(CodeForm codeForm, String sysName);
	
	
	/**
	 * 保存系统配置
	 * @param tCodeEntityList
	 * @param String
	 * @return
	 */
	public R save(List<TCodeEntity> tCodeEntityList, String staffId);
	
	/**
	 * 删除系统配置
	 * @param tCodeEntity
	 * @param String
	 * @return R
	 */
	public R delete(TCodeEntity tCodeEntity, String staffId);
	
	/**
	 * 系统配置下载excel
	 * @param response
	 * @param busId
	 * @return
	 */
	public void tCodeOutPutExcel(HttpServletResponse response, String busId, String sysName);
	
	
	/**
	 * 系统配置导入
	 * @param request
	 * @return
	 */
	public R importXLS(MultipartHttpServletRequest request, String staffId);
}
