package com.gemini.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemini.workflow.DTO.TaskDTO;
import com.gemini.workflow.exception.ProcessInstanceNotFoundException;
import com.gemini.workflow.extension.CustomUserTaskJsonConverter;
import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.GeminiTaskService;
import com.gemini.workflow.utils.WorkflowUtils;
import com.github.wnameless.json.flattener.JsonFlattener;
import de.odysseus.el.util.SimpleContext;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.util.JsonConverterUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.SpringExpressionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.el.ExpressionFactoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.PropertyPlaceholderHelper;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class GeminiTaskServiceImpl extends BaseService implements GeminiTaskService {

    @Override
    public List findTask(String userId, String processDefinitionKey) throws Exception {
        List<Map<String, String>> resultList = new ArrayList<>();
        //指定个人任务查询
//        List<Task> taskList = taskService.createTaskQuery().taskAssignee(assignee).list();//仅查询 个人 任务
        TaskQuery taskQuery = taskService.createTaskQuery();
        taskQuery = taskQuery.taskCandidateOrAssigned(userId);//按用户筛选
        if (StringUtils.isNotEmpty(processDefinitionKey))
            taskQuery = taskQuery.processDefinitionKey(processDefinitionKey);//按流程定义Key筛选

        taskQuery.includeProcessVariables();//将任务变量一并查出来
        List<Task> taskList = taskQuery.list();//查询 个人 和 候选 任务

        if (CollectionUtil.isNotEmpty(taskList)) {

            for (Task task : taskList) {
                Map<String, String> resultMap = new HashMap<>();

                /**
                 * 处理UserTask扩展属性
                 * 所有处理不考虑子流程
                 * activiti的表达式没调用到，这里采用spring工具类替换占位符
                 */
                //获取当前任务节点的定义，
                Process mainProcess = repositoryService.getBpmnModel(task.getProcessDefinitionId()).getMainProcess();
                FlowElement flowElement = mainProcess.getFlowElement(task.getTaskDefinitionKey());//任务节点定义
                UserTask userTask = (UserTask) flowElement;
                String handleUrl = userTask.getExtensionElements().get(CustomUserTaskJsonConverter.HANDLE_URL).get(0).getElementText();//处理链接

                Map<String, Object> processVariables = task.getProcessVariables();
                WorkflowUtils.replacePlaceHolder(processVariables,handleUrl);//替换url中的占位符


                /* 任务ID */
                resultMap.put("tiid", task.getId());
                /* 流程实例ID */
                resultMap.put("piid", task.getProcessInstanceId());
                /* 任务节点 */
                resultMap.put("node", task.getTaskDefinitionKey());
                /* 任务节点名称 */
                resultMap.put("nodeDisplayName", task.getName());
                /* 任务的办理人 */
                resultMap.put("taskAssignee", task.getAssignee());

                /* 任务节点的创建人姓名（即上节点的提交人） */
                String preUserName = (String) runtimeService.getVariable(task.getExecutionId(), "preUserName");
                resultMap.put("createEmpidNm", preUserName);
                /* 任务节点的创建时间 */
                String preOptTime = (String) runtimeService.getVariable(task.getExecutionId(), "preOptTime");
                resultMap.put("createTimeString", preOptTime);
                /* 任务节点的处理链接  约定将审核url放到 自定义属性的handleUrl中 */
                resultMap.put("handlerUrl", handleUrl);

                /* 任务的办理人 */
                resultMap.put("taskAssignee", task.getAssignee());
                /* 执行对象ID */
                resultMap.put("executionId", task.getExecutionId());
                /* 流程定义ID */
                resultMap.put("processDefinitionId", task.getProcessDefinitionId());
                /* 流程定义Key */
                resultMap.put("processDefinitionKey", task.getProcessDefinitionId().substring(0, task.getProcessDefinitionId().indexOf(":")));
                /* businessKey：业务数据uuid */
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
                String businessKey = processInstance.getBusinessKey();
                resultMap.put("businessKey", businessKey);

                resultList.add(resultMap);
            }
        }

        return resultList;

    }

    @Override
    public void completeTask(TaskDTO taskDTO) throws Exception {
        securityUtil.logInAs(taskDTO.getUserId());

        String businessKey = taskDTO.getBusinessKey();
        String taskId = taskDTO.getTaskId();
        String userId = taskDTO.getUserId();
        Map variables = taskDTO.getVariables();
        Map variablesLocal = taskDTO.getVariablesLocal();


        //通过businessKey找不到说明没流程，直接抛异常
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
        if (processInstance == null) throw new ProcessInstanceNotFoundException(ProcessInstanceNotFoundException.msg);

        //有taskId先根据taskId查找
        Task task = taskService.createTaskQuery().taskId(taskDTO.getTaskId()).taskCandidateOrAssigned(userId).singleResult();
        if (task == null) {
            //根据businessKey和userId找到对应的任务
            task = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).taskCandidateOrAssigned(userId).singleResult();
            if (task == null) throw new Exception("没有对应的流程或待办任务");
            taskId = task.getId();
        }


        taskService.setVariables(taskId, variables);
        taskService.setVariablesLocal(taskId, variablesLocal);
        //assignee不为空,直接完成任务
        if (StringUtils.isNotEmpty(task.getAssignee())) {
            if (!StringUtils.equals(task.getAssignee(), userId)) throw new Exception("不是当前任务的待办人");
            taskService.complete(taskId);
            //taskService.complete(taskId,任务变量,临时变量);
        }
        //assignee为空,先拾取，再完成
        else {
            claimTask(taskId, userId);
            taskService.complete(taskId);
        }

//            taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(taskId).build());
//            taskService.complete(taskId, variables);
    }

    @Override
    public void claimTask(String taskId, String userId) throws Exception {
        securityUtil.logInAs(userId);
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (StringUtils.isNotEmpty(task.getAssignee()))
            throw new Exception("任务已经被拾取，待办人为：[" + task.getAssignee() + "]");

        taskService.claim(taskId, userId);
    }

    @Override
    public void turnTask(String taskId, String userId) throws Exception {
        securityUtil.logInAs(userId);
        taskService.setAssignee(taskId, userId);
    }
}
