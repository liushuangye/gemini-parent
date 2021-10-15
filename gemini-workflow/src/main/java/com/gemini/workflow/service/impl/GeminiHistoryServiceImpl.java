package com.gemini.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gemini.workflow.entity.User;
import com.gemini.workflow.extension.CustomUserTaskJsonConverter;
import com.gemini.workflow.mapper.UserMapper;
import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.GeminiHistoryService;
import com.gemini.workflow.utils.WorkflowUtils;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

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
            /** 添加办理人/待办人 */
            jsonObject.put("assignee",hisTaskInst.getAssignee());
            User assignee = userMapper.loadUserByUserId(hisTaskInst.getAssignee());
            if(StringUtils.isNotEmpty(hisTaskInst.getAssignee())){
                jsonObject.put("assigneeName",assignee.getStaffName());
            }else{
                String userNames = getTaskAssigneeOrCandidate(hisTaskInst.getId());
                jsonObject.put("assigneeName",userNames);
            }

            jsonObject.put("taskDefinitionKey",hisTaskInst.getTaskDefinitionKey());
            jsonObject.put("startTime", DateUtil.format(hisTaskInst.getStartTime(),"yyyy/MM/dd"));
            jsonObject.put("endTime",DateUtil.format(hisTaskInst.getEndTime(),"yyyy/MM/dd"));

            //获取当前任务节点的定义
            Process mainProcess = repositoryService.getBpmnModel(hisTaskInst.getProcessDefinitionId()).getMainProcess();
            FlowElement flowElement = mainProcess.getFlowElement(hisTaskInst.getTaskDefinitionKey());//任务节点定义
            UserTask userTask = (UserTask) flowElement;
            String showUrl = userTask.getExtensionElements().get(CustomUserTaskJsonConverter.SHOW_URL).get(0).getElementText();//查看链接

            // 从任务变量获取信息
            Map<String, Object> taskLocalVariables = hisTaskInst.getTaskLocalVariables();
            String startUserId = taskLocalVariables.get("startUserId").toString();//申请人
            String businessDataStr = taskLocalVariables.get("businessData").toString();
            JSONObject businessData = JSONObject.parseObject(businessDataStr);
            JSONObject actWorkflowBill = businessData.getJSONObject("actWorkflowBill");
            String reviewResult = actWorkflowBill.getString("reviewResultName");
            String reviewResultName = actWorkflowBill.getString("reviewResultName");
            if(hisTaskInst.getEndTime()!=null && StringUtils.isEmpty(reviewResultName))  reviewResultName = "提交";//如果已经审核结束了，但是没有审核结果，则显示提交。
            if(hisTaskInst.getEndTime()==null)reviewResultName = null;//审批结果是从businessData拿的，没结束的任务也有businessData，如果还没审批结束则清除结果
            String reviewComment = actWorkflowBill.getString("reviewComment");
            String modifyComment = actWorkflowBill.getString("modifyComment");
            String handleComment = "";//办理意见
            //如果本节点的办理人是申请人 且 修改意见非空，则办理意见为修改意见
            if(assignee != null && StringUtils.equals(assignee.getUserId(),startUserId) && StringUtils.isNotEmpty(modifyComment)){
                handleComment = modifyComment;
            }
            //如果本节点的办理人不是申请人 且 审核意见非空，则办理意见为审核意见
            if(assignee != null &&  !StringUtils.equals(assignee.getUserId(),startUserId) && StringUtils.isNotEmpty(reviewComment)){
                handleComment = reviewComment;
            }
            if(StringUtils.isNotEmpty(handleComment)){
                //办理意见展示换行
                handleComment = handleComment.replace(" ", "&nbsp;&nbsp;");
                handleComment = handleComment.replace("\r\n", "<br/>");
            }
            if(StringUtils.isNotEmpty(showUrl)){
                //替换掉showUrl中的占位符
                showUrl = WorkflowUtils.replacePlaceHolder(taskLocalVariables,showUrl);
                //复原showUrl中的特殊符号
                showUrl = WorkflowUtils.replaceUrlXmlChar(showUrl);
            }

            jsonObject.put("businessKey",businessData.get("uuid"));//业务数据的uuid作为businessKey
            jsonObject.put("showUrl",showUrl);
            jsonObject.put("businessDataStr",businessDataStr);
            jsonObject.put("reviewResult",reviewResult);
            jsonObject.put("reviewResultName",reviewResultName);
            jsonObject.put("handleComment",handleComment);
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

    @Override
    public String getHistoryBusinessData(String taskId) throws Exception {
        HistoricTaskInstance hisTaskInst = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .includeTaskLocalVariables()//将任务变量一并查出来
                .singleResult();
        Map<String, Object> taskLocalVariables = hisTaskInst.getTaskLocalVariables();
        String businessDataStr = taskLocalVariables.get("businessData").toString();
        return businessDataStr;
    }

    /**
     * 获取任务的 待办人(如果没有则返回候选人、候选组，逗号隔开)
     * @param taskId
     * @return
     */
    private String getTaskAssigneeOrCandidate(String taskId) {
        Set userIds = new HashSet();
        Set groupIds = new HashSet();
        String userNameStr = "";
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(taskId);
        if (identityLinkList != null && identityLinkList.size() > 0) {
            for (IdentityLink identityLink :identityLinkList) {
                String groupId = identityLink.getGroupId();
                String userId = identityLink.getUserId();

                if(org.apache.commons.lang3.StringUtils.isNotEmpty(userId)) userIds.add(userId);
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(groupId)) groupIds.add(groupId);

                List taskAssigneeOrCandidate = userMapper.getCandidateUserNames(userIds, groupIds);
                userNameStr = org.apache.commons.lang3.StringUtils.join(taskAssigneeOrCandidate,",");
            }


        }
        return userNameStr;
    }
}
