package com.gemini.workflow.service;

import com.gemini.workflow.DTO.ProcessDTO;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;

public interface ProcessService{

    public ProcessInstance startWorkflow(ProcessDTO processDTO) throws Exception;

    public List searchProcessInstances(String processDefinitionKey) throws Exception;

    public ProcessInstance searchInstanceById(String processId) throws Exception;

    public void deleteInstanceById(String processId) throws Exception ;
    /** 将会删除一个流程定义下的所有实例，请谨慎使用 */
    public void deleteInstanceByKey(String processDefinitionKey) throws Exception;
}
