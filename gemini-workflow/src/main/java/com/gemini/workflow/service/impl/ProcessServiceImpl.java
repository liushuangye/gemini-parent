package com.gemini.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.gemini.workflow.DTO.ProcessDTO;
import com.gemini.workflow.entity.User;
import com.gemini.workflow.mapper.UserMapper;
import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.ProcessService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessServiceImpl extends BaseService implements ProcessService {
    @Resource
    UserMapper userMapper;

    @Override
    public ProcessInstance startWorkflow(ProcessDTO processDTO)  throws Exception{
        String processDefinitionKey = processDTO.getProcessDefinitionKey();
        String businessKey = processDTO.getBusinessKey();//约定使用业务表的uuid作为businessKey，完成业务表和流程实例的关联
        Map variables = processDTO.getVariables();
        User startUser = userMapper.loadUserByUserId(processDTO.getUserId());
        String startUserName = startUser.getStaffName();
        String startTime = DateUtil.format(DateUtil.date(),"yyyy/MM/dd HH:mm:ss");//任务处理时间

        variables.put("startUserId", processDTO.getUserId());// 启动流程的用户
        variables.put("startUserName", startUserName);// 启动流程的用户姓名
        variables.put("startOptTime", startTime);// 启动流程的时间
        variables.put("preUserId", processDTO.getUserId());// 上次操作的用户
        variables.put("preUserName", startUserName);// 上次操作的用户姓名
        variables.put("preOptTime", startTime);// 上次操作的时间

        ProcessInstance instance = null;
        instance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);

        return instance;
    }

    @Override
    public List searchProcessInstances(String processDefinitionKey)  throws Exception{
        List<ProcessInstance> runningList = new ArrayList<>();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        runningList = processInstanceQuery.processDefinitionKey(processDefinitionKey).list();

        if (CollectionUtil.isNotEmpty(runningList)) {
            List<Map<String, String>> resultList = new ArrayList<>();
            runningList.forEach(s -> {
                Map<String, String> resultMap = new HashMap<>();
                // 流程实例ID
                resultMap.put("processId", s.getId());
                // 流程定义ID
                resultMap.put("processDefinitionKey", s.getProcessDefinitionId());
                resultList.add(resultMap);
            });
        }
        return runningList;
    }

    @Override
    public ProcessInstance searchInstanceById(String processId)  throws Exception{
        ProcessInstance pi = null;
        pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        return pi;
    }

    @Override
    public void deleteInstanceById(String processId)  throws Exception{
        runtimeService.deleteProcessInstance(processId, "删除" + processId);
    }

    @Override
    public void deleteInstanceByKey(String processDefinitionKey) throws Exception{
        List<ProcessInstance> runningList = new ArrayList<>();
            ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
            runningList = processInstanceQuery.processDefinitionKey(processDefinitionKey).list();

        if (CollectionUtil.isNotEmpty(runningList)) {
            List<Map<String, String>> resultList = new ArrayList<>();
            runningList.forEach(s -> runtimeService.deleteProcessInstance(s.getId(), "删除"));
        }
    }
}
