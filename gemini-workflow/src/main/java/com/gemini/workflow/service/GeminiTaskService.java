package com.gemini.workflow.service;

import com.gemini.workflow.DTO.TaskDTO;

import java.util.List;

/**
 * 任务接口
 * 为了避免与activiti重名，添加Gemini前缀
 */
public interface GeminiTaskService {
    public List findTask(String userId,String processDefinitionKey) throws Exception;

    public void completeTask(TaskDTO taskDTO) throws Exception;

    public void claimTask(String taskId, String userId)  throws Exception;

    public void turnTask(String taskId, String userId) throws Exception ;
}
