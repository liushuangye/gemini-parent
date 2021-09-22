package com.gemini.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.workflow.entity.User;
import com.gemini.workflow.mapper.UserMapper;
import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.GeminiHistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class GeminiHistoryServiceImpl extends BaseService implements GeminiHistoryService {
    @Resource
    UserMapper userMapper;

    @Override
    public List findHistory(String businessKey)  throws Exception{
        JSONArray result = new JSONArray();

        //历史任务列表：按照任务开始时间升序
        List<HistoricTaskInstance> histTaskInstList = historyService.createHistoricTaskInstanceQuery()
//                                                        .processInstanceId(processInstanceId)
                                                        .processInstanceBusinessKey(businessKey)
                                                        .includeTaskLocalVariables()//将任务变量一并查出来
                                                        .orderByHistoricTaskInstanceStartTime().asc()
                                                        .list();
        //TODO 返回ctms的格式
        for (HistoricTaskInstance hisTaskInst:histTaskInstList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",hisTaskInst.getId());
            jsonObject.put("piid",hisTaskInst.getProcessInstanceId());
            jsonObject.put("processDefinitionId",hisTaskInst.getProcessDefinitionId());
            jsonObject.put("name",hisTaskInst.getName());
            jsonObject.put("assignee",hisTaskInst.getAssignee());
            User assignee = userMapper.loadUserByUserId(hisTaskInst.getAssignee());
            jsonObject.put("assigneeName",assignee.getStaffName());//办理人
            jsonObject.put("taskDefinitionKey",hisTaskInst.getTaskDefinitionKey());
            jsonObject.put("startTime", DateUtil.format(hisTaskInst.getStartTime(),"yyyy/MM/dd"));
            jsonObject.put("endTime",DateUtil.format(hisTaskInst.getEndTime(),"yyyy/MM/dd"));
            // 从任务变量获取信息
            Map<String, Object> taskLocalVariables = hisTaskInst.getTaskLocalVariables();
            String businessDataStr = taskLocalVariables.get("businessData").toString();
            JSONObject businessData = JSONObject.parseObject(businessDataStr);
            JSONObject actWorkflowBill = businessData.getJSONObject("actWorkflowBill");
            String reviewResult = actWorkflowBill.getString("reviewResultName");
            String reviewResultName = actWorkflowBill.getString("reviewResultName");
            String reviewComment = actWorkflowBill.getString("reviewComment");
            if(StringUtils.isNotEmpty(reviewComment)){
                //审核意见展示换行
                reviewComment = reviewComment.replace(" ", "&nbsp;&nbsp;");
                reviewComment = reviewComment.replace("\r\n", "<br/>");
            }
            jsonObject.put("reviewResult",reviewResult);
            jsonObject.put("reviewResultName",reviewResultName);
            jsonObject.put("reviewComment",reviewComment);
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
