package com.gemini.toolkit.operatelog.controller;

import javax.annotation.Resource;

import com.gemini.toolkit.operatelog.dto.TOplogDto;
import com.gemini.toolkit.operatelog.service.TOperatelogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 操作日志
 * @author wangjh
 * 
 */
@RestController
@RequestMapping("/operatelog")
public class TOperatelogController {

	@Resource
    TOperatelogService service;

    @RequestMapping(value = "/getOpLogInfo", method = RequestMethod.GET)
    public IPage<TOplogDto> getOpLogInfo(String requestTimeFrom, String requestTimeTo, String userName, String moduleName, Integer current, Integer size) {
    	TOplogDto oplogDtoIn = new TOplogDto();
        oplogDtoIn.setRequestTimeFrom(requestTimeFrom);
        oplogDtoIn.setRequestTimeTo(requestTimeTo);
        oplogDtoIn.setUserName(userName);
        oplogDtoIn.setModuleName(moduleName);
        IPage page = new Page<>(current, size);
        return service.getOpLog(oplogDtoIn,page);
    }
}
