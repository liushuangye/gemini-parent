package com.gemini.toolkit.devops.controller;


import com.gemini.toolkit.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gemini.toolkit.devops.service.TDevopsLogDetailsService;

/**
 * <p>
 * 运维工具执行记录详细 前端控制器
 * </p>
 *
 * @author BHH
 * @since 2021-05-26
 */
@RestController
@RequestMapping("/devops/log")
public class TDevopsLogDetailsController {
	@Autowired
	TDevopsLogDetailsService service;
	
	
	@RequestMapping(value = "details", method = RequestMethod.GET)
	public R getDetails(@RequestParam("id") long id) {
		
		return R.ok().put("data", service.getLogDetails(id));
		
	}
}
