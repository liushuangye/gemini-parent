package com.gemini.toolkit.log.service.impl;

import com.gemini.toolkit.log.service.OpLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gemini.toolkit.log.entity.TToolkitOplogDetailEntity;
import com.gemini.toolkit.log.entity.TToolkitOplogEntity;
import com.gemini.toolkit.log.mapper.TToolkitOplogDetailMapper;
import com.gemini.toolkit.log.mapper.TToolkitOplogMapper;

/**
 * @author Bhh
 */

@Service
public class OpLogServiceImpl implements OpLogService {
    @Autowired
    private TToolkitOplogMapper tSysOplogMapper;
    @Autowired
    private TToolkitOplogDetailMapper tSysOplogDetailMapper;


    @Override
    @Transactional(rollbackFor=Exception.class)
    public int insertOpLogInfo(TToolkitOplogEntity oplogEntityIn, TToolkitOplogDetailEntity oplogDetailEntityIn) throws Exception {
        int ret  = tSysOplogMapper.insert(oplogEntityIn);
        tSysOplogDetailMapper.insert(oplogDetailEntityIn);
        return ret;
    }
}
