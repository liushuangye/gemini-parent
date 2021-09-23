package com.gemini.workflow.service;

import com.gemini.workflow.config.SecurityUtil;
import org.activiti.api.process.runtime.ProcessAdminRuntime;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskAdminRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseService {
    @Autowired
    protected SecurityUtil securityUtil;
    @Autowired
    public TaskService taskService;
    @Autowired
    public RuntimeService runtimeService;
    @Autowired
    public HistoryService historyService;
    @Autowired
    public RepositoryService repositoryService;
//    @Autowired
//    public ProcessEngineConfiguration processEngineConfiguration;


    /**
     * ProcessRuntime类内部最终调用repositoryService和runtimeService相关API。
     * 需要ACTIVITI_USER权限
     */
    @Autowired
    public ProcessRuntime processRuntime;

    /**
     * ProcessRuntime类内部最终调用repositoryService和runtimeService相关API。
     * 需要ACTIVITI_ADMIN权限
     */
    @Autowired
    public ProcessAdminRuntime processAdminRuntime;

    /**
     * 类内部调用taskService
     * 需要ACTIVITI_USER权限
     */
    @Autowired
    public TaskRuntime taskRuntime;

    /**
     * 类内部调用taskService
     * 需要ACTIVITI_ADMIN权限
     */
    @Autowired
    public TaskAdminRuntime taskAdminRuntime;
}
