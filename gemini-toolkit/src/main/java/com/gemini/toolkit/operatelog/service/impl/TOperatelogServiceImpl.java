package com.gemini.toolkit.operatelog.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.gemini.toolkit.log.entity.TToolkitOplogDetailEntity;
import com.gemini.toolkit.log.entity.TToolkitOplogEntity;
import com.gemini.toolkit.operatelog.mapper.TOperatelogDetailMapper;
import com.gemini.toolkit.operatelog.mapper.TOperatelogMapper;
import com.gemini.toolkit.operatelog.service.TOperatelogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.operatelog.dto.TOplogDetailDto;
import com.gemini.toolkit.operatelog.dto.TOplogDto;

@Service
public class TOperatelogServiceImpl extends ServiceImpl<TOperatelogMapper, TToolkitOplogEntity> implements TOperatelogService {

	@Autowired
    private TOperatelogDetailMapper tOperatelogDetailMapper;
	
    @Autowired
    private TOperatelogMapper tOperatelogMapper;
	    
	@Override
	public IPage<TOplogDto> getOpLog(TOplogDto oplogDtoIn, IPage page) {
		QueryWrapper ew = new QueryWrapper();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isEmpty(oplogDtoIn.getRequestTimeFrom())) {
//            String requestTimeFrom = oplogDtoIn.getRequestTimeFrom().replaceAll("-","");
            ew.ge("request_time",LocalDateTime.parse(oplogDtoIn.getRequestTimeFrom(),df));
        }
        if (!StringUtils.isEmpty(oplogDtoIn.getRequestTimeTo())) {
//            String requestTimeTo = oplogDtoIn.getRequestTimeTo().replaceAll("-","");
            ew.le("request_time",LocalDateTime.parse(oplogDtoIn.getRequestTimeTo(),df));
        }
        if (!StringUtils.isEmpty(oplogDtoIn.getUserName())) {
            ew.like("user_name","%"+oplogDtoIn.getUserName()+"%");
        }
        if (!StringUtils.isEmpty(oplogDtoIn.getModuleName())) {
            ew.like("module_name","%"+oplogDtoIn.getModuleName()+"%");
        }
        ew.orderByDesc("request_time");
        IPage<TToolkitOplogEntity> pageData = tOperatelogMapper.selectPage(page, ew);
        List<TToolkitOplogEntity> oplogList = pageData.getRecords();
        List<TOplogDto> oplogDtoOutList = new ArrayList<>();
        QueryWrapper ewPermission = new QueryWrapper();
        ewPermission.in("type", new String[]{"0", "1"});
        for (TToolkitOplogEntity entity : oplogList){
        	TOplogDto oplogDtoOut = new TOplogDto();
        	oplogDtoOut.setUuid(entity.getUuid());
            oplogDtoOut.setUserCode(entity.getUserCode());
            oplogDtoOut.setUserName(entity.getUserName());
            oplogDtoOut.setIpAddress(entity.getIpAddress());
            oplogDtoOut.setRequestUri(entity.getRequestUri());
            oplogDtoOut.setModuleName(entity.getModuleName());
            oplogDtoOut.setOpName(entity.getOpName());
            oplogDtoOut.setClassName(entity.getClassName());
            oplogDtoOut.setMethodName(entity.getMethodName());
            oplogDtoOut.setResultCode(entity.getResultCode());
            oplogDtoOut.setRequestTime(entity.getRequestTime());
            oplogDtoOut.setExecTime(entity.getExecTime());
            oplogDtoOut.setRemarks(entity.getRemarks());
            oplogDtoOut.setResultMsg(entity.getResultMsg());
            TOplogDetailDto oplogDetailDtoIn = new TOplogDetailDto();
            QueryWrapper ewDetail = new QueryWrapper();
            ewDetail.eq("t_sys_oplogid",entity.getUuid());
            oplogDetailDtoIn.setTSysOplogid(String.valueOf(entity.getUuid()));
            List<TToolkitOplogDetailEntity> oplogDetail = tOperatelogDetailMapper.selectList(ewDetail);
            if (oplogDetail != null && oplogDetail.size() > 0) {
                oplogDtoOut.setRequestBody(oplogDetail.get(0).getRequestBody());
                oplogDtoOut.setRequestHeaders(oplogDetail.get(0).getRequestHeaders());
                oplogDtoOut.setExceptionStacktrace(oplogDetail.get(0).getExceptionStacktrace());
            }
            oplogDtoOutList.add(oplogDtoOut);
        }
        IPage<TOplogDto> pageDataFull = new Page<>();
        pageDataFull.setCurrent(pageData.getCurrent());
        pageDataFull.setPages(pageData.getPages());
        pageDataFull.setSize(pageData.getSize());
        pageDataFull.setTotal(pageData.getTotal());
        pageDataFull.setRecords(oplogDtoOutList);
        return pageDataFull;
	}

}
