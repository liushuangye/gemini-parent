package com.gemini.workflow._4ctms.service;

import com.gemini.workflow._4ctms.mapper.QcTaskMapper;
import com.gemini.workflow._4ctms.mapper.TrialMapper;
import com.gemini.workflow.entity.User;
import com.gemini.workflow.mapper.UserMapper;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class QcAssigneeService {
    @Resource
    QcTaskMapper qcTaskMapper;

    @Resource
    UserMapper userMapper;

    // 获取质控任务对应项目的所有研究人员
    public String getTrialUserIds(Long trialId) throws Exception{
        List<String> userIds = new ArrayList();
        if(trialId != null){
            userIds = qcTaskMapper.getTrialUserIds(trialId);

        }
        String userIdsStr = StringUtils.join(userIds, ",");
        if(StringUtils.isEmpty(userIdsStr))userIdsStr = null;
        return userIdsStr;
    }
}
