package com.gemini.toolkit.sysparam.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gemini.toolkit.common.utils.CommonConsts;
import com.gemini.toolkit.common.utils.JwtUtil;
import com.gemini.toolkit.common.utils.R;
import com.gemini.toolkit.log.OpLog;
import com.gemini.toolkit.login.form.UserInfo;
import com.gemini.toolkit.sysparam.entity.TCodeEntity;
import com.gemini.toolkit.sysparam.form.CodeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.gemini.toolkit.sysparam.service.TCodeService;
import com.gemini.toolkit.sysparam.service.TStaModuleService;

/**
 * <p>
 * 系统配置 前端控制器
 * </p>
 *
 * @author BHH
 * @since 2021-05-08
 */
@RestController
@RequestMapping("/sysparam")
public class SysparamController {

	@Autowired
	private TStaModuleService tStaModuleService;

	@Autowired
	private TCodeService tCodeService;
	
	@Value("${sys.name}")
    private String sysName;

	@PostMapping("/getTree")
//	@OpLog(moduleName = "系统参数配置", opName = "获取sta树")
	public R getTree() {
		return tStaModuleService.getTreeCode(sysName);
	}

	@PostMapping("/getTCode")
//	@OpLog(moduleName = "系统参数配置", opName = "获取系统参数配置")
	public R getTCode(@Validated @RequestBody CodeForm codeForm) {
		return tCodeService.getTCode(codeForm, sysName);
	}

	@PostMapping("/save")
	@OpLog(moduleName = "系统参数配置", opName = "更新系统参数配置")
	public R save(@RequestBody List<TCodeEntity> tCodeEntityList, HttpServletRequest request) {
		String token = request.getHeader(CommonConsts.AUTHORIZATION);
		String userInfoToken = JwtUtil.getUserInfoFromToken(token);
		UserInfo userInfo = JSONArray.parseObject(userInfoToken, UserInfo.class);
		return tCodeService.save(tCodeEntityList, userInfo.getStaffId());
	}

	@PostMapping("/delete")
	@OpLog(moduleName = "系统参数配置", opName = "删除系统参数配置")
	public R delete(@RequestBody TCodeEntity tCodeEntity, HttpServletRequest request) {
		String token = request.getHeader(CommonConsts.AUTHORIZATION);
		String userInfoToken = JwtUtil.getUserInfoFromToken(token);
		UserInfo userInfo = JSONArray.parseObject(userInfoToken, UserInfo.class);
		return tCodeService.delete(tCodeEntity, userInfo.getStaffId());
	}

	@PostMapping("/outPutXLS")
	@OpLog(moduleName = "系统参数配置", opName = "文件导出")
	public void tCodeOutPutExcel(HttpServletResponse response, @Validated @RequestBody CodeForm codeForm) {
		tCodeService.tCodeOutPutExcel(response, codeForm.getBusId(), sysName);
	}

	@RequestMapping(value = "/importXLS", method = RequestMethod.POST)
	@OpLog(moduleName = "系统参数配置", opName = "文件导入")
	public R importXLS(MultipartHttpServletRequest request) {
		String token = request.getHeader(CommonConsts.AUTHORIZATION);
		String userInfoToken = JwtUtil.getUserInfoFromToken(token);
		UserInfo userInfo = JSONArray.parseObject(userInfoToken, UserInfo.class);
		return tCodeService.importXLS(request, userInfo.getStaffId());
	}

}
