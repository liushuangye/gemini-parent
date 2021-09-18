package com.gemini.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.DeployService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.repository.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class DeployServiceImpl extends BaseService implements DeployService {

    @Override
    public Map<String, Object> listRelease(Map<String,Object> queryparam) {
        String processDefinitionKey = (String)queryparam.get("processDefinitionKey");
        Integer pageNum = (Integer) queryparam.get("pageNum");
        if(pageNum == null) {
            pageNum = 0;
        }
        // 每页显示条数
        Integer pageSize = (Integer) queryparam.get("pageSize");

        if(pageSize == null) {
            pageSize = 30;
        }
        //接口返回结果
        JSONObject result = new JSONObject();
        JSONArray modelArray = new JSONArray();

        //查询结果
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        if(StringUtils.isNotEmpty(processDefinitionKey))processDefinitionQuery = processDefinitionQuery.processDefinitionKeyLike("%"+processDefinitionKey+"%");
        long total = processDefinitionQuery.count();
        //key升序、version降序
        List<ProcessDefinition> list = processDefinitionQuery
                                        .orderByProcessDefinitionKey().asc()
                                        .orderByProcessDefinitionVersion().desc()
                                        .listPage((pageNum - 1) * pageSize, pageSize);


        Map<String,ProcessDefinition> map = new HashMap<String,ProcessDefinition>();

        if(list != null && list.size() >0){
            for(ProcessDefinition pd:list){
                map.put(pd.getId(), pd);
            }
        }
        List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());
        for (ProcessDefinition processDefinition : pdList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",processDefinition.getId());
            jsonObject.put("key",processDefinition.getKey());
            jsonObject.put("name",processDefinition.getName());
            jsonObject.put("version",processDefinition.getVersion());
            jsonObject.put("resourceName",processDefinition.getResourceName());
            jsonObject.put("deploymentId",processDefinition.getDeploymentId());
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
            jsonObject.put("deploymentTime",DateUtil.format(deployment.getDeploymentTime(),"yyyy-MM-dd HH:mm:ss"));//发布时间

            modelArray.add(jsonObject);
        }
        result.put("records",modelArray);
        result.put("total",total);
        return result;
    }

    @Override
    public void deploy(String modelId) throws Exception{
        byte[] sourceBytes = repositoryService.getModelEditorSource(modelId);
        JsonNode editorNode = new ObjectMapper().readTree(sourceBytes);
        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
        BpmnModel bpmnModel = bpmnJsonConverter.convertToBpmnModel(editorNode);
        String processKey = "";//设计器中填写的流程的id字段
        processKey = bpmnModel.getProcesses().get(0).getId();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name("手动部署")
                .key(processKey)
                .enableDuplicateFiltering()
                .addBpmnModel(processKey.concat(".bpmn20.xml"), bpmnModel);

        deploymentBuilder.deploy();//发布
    }

    @Override
    public void deleteProcess(String deploymentId,boolean cascade) throws Exception {
        if(cascade){
            /**级联删除：不管流程是否启动，都能可以删除,一锅端,需要谨慎使用*/
            repositoryService.deleteDeployment(deploymentId, true);
        }else{
            /**不带级联的删除：只能删除没有启动的流程，如果流程启动，就会抛出异常*/
            repositoryService.deleteDeployment(deploymentId);
        }
    }
}
