package com.gemini.workflow.service.impl;

import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.GeminiHistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class GeminiHistoryServiceImpl extends BaseService implements GeminiHistoryService {

    @Override
    public List findByProcessInstanceId(String processInstanceId)  throws Exception{
        //历史任务列表：按照任务开始时间升序
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                                                        .processInstanceId(processInstanceId)
                                                        .orderByHistoricTaskInstanceStartTime().asc()
                                                        .list();
        //TODO 返回ctms的格式
        return list;
    }

    @Override
    public List getTaskVariables(String processInstanceId, String executionId, String taskId) throws Exception {
        //历史任务列表：按照任务开始时间升序
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .executionId(executionId)
                .taskId(taskId)
                .list();

        //TODO 返回ctms的格式
        return list;
    }
}
