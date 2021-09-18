package com.gemini.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.GeminiHistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class GeminiHistoryServiceImpl extends BaseService implements GeminiHistoryService {

    @Override
    public List findHistory(String dataUuid)  throws Exception{
        JSONArray result = new JSONArray();

        //历史任务列表：按照任务开始时间升序
        List<HistoricTaskInstance> histTaskInstList = historyService.createHistoricTaskInstanceQuery()
//                                                        .processInstanceId(processInstanceId)
                                                        .processInstanceBusinessKey(dataUuid)
//                                                        .includeTaskLocalVariables()//连二进制都会查出来，没法整，变量用的时候再去取
                                                        .orderByHistoricTaskInstanceStartTime().asc()
                                                        .list();
        //TODO 返回ctms的格式
        for (HistoricTaskInstance hisTaskInst:histTaskInstList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",hisTaskInst.getId());
            jsonObject.put("piid",hisTaskInst.getProcessInstanceId());
            jsonObject.put("processDefinitionId",hisTaskInst.getProcessDefinitionId());
            jsonObject.put("name",hisTaskInst.getName());
            jsonObject.put("taskDefinitionKey",hisTaskInst.getTaskDefinitionKey());
            jsonObject.put("startTime", DateUtil.format(hisTaskInst.getStartTime(),"yyyy-MM-dd HH:mm:ss"));
            jsonObject.put("endTime",DateUtil.format(hisTaskInst.getEndTime(),"yyyy-MM-dd HH:mm:ss"));

            result.add(jsonObject);
        }
        return result;
    }

    @Override
    public List getHistoryTaskVariables(String processInstanceId, String executionId, String taskId) throws Exception {
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
