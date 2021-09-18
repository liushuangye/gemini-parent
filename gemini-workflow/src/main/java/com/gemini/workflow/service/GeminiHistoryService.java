package com.gemini.workflow.service;

import java.util.List;

/**
 * 历史记录接口
 * 为了避免与activiti重名，添加Gemini前缀
 */
public interface GeminiHistoryService {
    //通过流程实例Id 查询审查履历
    public List findByProcessInstanceId(String processInstanceId)  throws Exception;

    //获取某个任务节点的数据快照（流程变量）
    public List getTaskVariables(String processInstanceId, String executionId, String taskId)  throws Exception;
}
