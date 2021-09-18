package com.gemini.toolkit.log.service;

import com.gemini.toolkit.log.entity.TToolkitOplogDetailEntity;
import com.gemini.toolkit.log.entity.TToolkitOplogEntity;

/**
 * @author zcj
 */

public interface OpLogService {
    /**
     * service
     * @param oplogEntityIn
     * @param oplogDetailEntityIn
     * @return
     * @throws Exception
     */
    int insertOpLogInfo(TToolkitOplogEntity oplogEntityIn, TToolkitOplogDetailEntity oplogDetailEntityIn) throws Exception;
}
