package com.gemini.workflow.service;

import com.gemini.workflow.DTO.ProcessDTO;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProcessService{
    /** 启动流程 */
    public ProcessInstance startWorkflow(ProcessDTO processDTO) throws Exception;
    /** 启动流程-批量 */
    public List<ProcessInstance> startWorkflowMultiple(List<ProcessDTO> processDTOList) throws Exception;

    public List searchProcessInstances(String processDefinitionKey) throws Exception;

    public ProcessInstance searchInstanceById(String processId) throws Exception;

    public void deleteInstanceById(String processId) throws Exception ;
    /** 将会删除一个流程定义下的所有实例，请谨慎使用 */
    public void deleteInstanceByKey(String processDefinitionKey) throws Exception;
    /** 根据一组businessKey+userId获取相应的流程实例 */
    public List<Map> searchProInstByBusinessKeys(List<String> businessKeys,String userId) throws Exception;
}
