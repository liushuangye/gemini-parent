package com.gemini.workflow.controller;

import com.alibaba.fastjson.JSONArray;
import com.gemini.workflow.DTO.ProcessDTO;
import com.gemini.workflow.service.ProcessService;
import com.gemini.workflow.utils.RestMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@Api(tags = "流程实例管理")
public class ProcessController extends BaseController {

    @Autowired
    private ProcessService processService;

    @PostMapping(path = "startWorkflow")
    @ApiOperation(value = "根据流程key启动流程", notes = "根据key启动流程，每一个流程有对应的一个key这个是某一个流程内固定的写在bpmn内的，需要给出userId")
    @ApiImplicitParam(name = "processDTO", value = "流程处理对象", required = true, dataType = "ProcessDTO")
    public RestMsg startWorkflow(@RequestBody @Valid ProcessDTO processDTO) {
        ProcessInstance instance = null;
        RestMsg restMsg = new RestMsg();
        try {
            instance = processService.startWorkflow(processDTO);
        } catch (Exception e) {
            e.printStackTrace();
            restMsg = RestMsg.fail("启动失败", e.getMessage());
        }
        if (instance != null) {
            Map<String, String> result = new HashMap<>();
            // 流程实例ID
            result.put("processId", instance.getId());
            // 流程定义ID
            result.put("processDefinitionKey", instance.getProcessDefinitionId());
            restMsg = RestMsg.success("启动成功", result);
        }
        return restMsg;
    }


    @GetMapping(path = "searchByKey")
    @ApiOperation(value = "根据流程key查询流程实例", notes = "查询流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程key", dataType = "String", paramType = "query"),
    })
    public RestMsg searchProcessInstance(@RequestParam("processDefinitionKey") String processDefinitionKey) {
        RestMsg restMsg = new RestMsg();

        try {
            List<ProcessInstance> resultList = processService.searchProcessInstances(processDefinitionKey);
            restMsg = RestMsg.success("查询成功", resultList);
        } catch (Exception e) {
            e.printStackTrace();
            restMsg = RestMsg.fail("查询失败:" + e.getMessage(),null);
        }
        return restMsg;
    }


    @PostMapping(path = "searchProInstByBusinessKeys")
    @ApiOperation(value = "根据businessKeys查询流程实例", notes = "返回proc_def_id_，business_key_，proc_inst_id_的列表")
    @ApiImplicitParam(name = "businessKeys", value = "单据uuid的set", required = true)
    public RestMsg searchProInstByBusinessKeys(@RequestBody Map map) {
        String userId = (String) map.getOrDefault("userId", "");
        List<String> businessKeysList = (List<String>) map.getOrDefault("businessKeys", new ArrayList());

        List<Map> maps = new ArrayList<>();
        RestMsg restMsg = new RestMsg();
        try {
            maps = processService.searchProInstByBusinessKeys(businessKeysList,userId);
        } catch (Exception e) {
            e.printStackTrace();
            restMsg = RestMsg.fail("查询失败", e.getMessage());
        }
        Map<String, String> result = new HashMap<>();
        restMsg = RestMsg.success("查询成功", maps);
        return restMsg;
    }

    @GetMapping(path = "searchById")
    @ApiOperation(value = "根据流程ID查询流程实例", notes = "查询流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "流程实例ID", dataType = "String", paramType = "query"),
    })
    public RestMsg searchByID(@RequestParam("processId") String processId) {
        RestMsg restMsg = new RestMsg();
        try {
            ProcessInstance pi = null;
            pi = processService.searchInstanceById(processId);
            if (pi != null) {
                Map<String, String> resultMap = new HashMap<>(2);
                // 流程实例ID
                resultMap.put("processID", pi.getId());
                // 流程定义ID
                resultMap.put("processDefinitionKey", pi.getProcessDefinitionId());
                restMsg = RestMsg.success("查询成功", resultMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            restMsg = RestMsg.fail("查询失败:" + e.getMessage(),null);
        }
        return restMsg;
    }


    @DeleteMapping(path = "deleteProcessInstanceByID")
    @ApiOperation(value = "根据流程实例ID删除流程实例", notes = "根据流程实例ID删除流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "流程实例ID", dataType = "String", paramType = "query"),
    })
    public RestMsg deleteProcessInstanceByID(@RequestParam("processId") String processId) {
        RestMsg restMsg = new RestMsg();
        try {
            processService.deleteInstanceById(processId);
            restMsg = RestMsg.success("删除成功", "");
        } catch (Exception e) {
            e.printStackTrace();
            restMsg = RestMsg.fail("删除失败:" + e.getMessage(),null);
        }
        return restMsg;
    }


    @DeleteMapping(path = "deleteProcessInstanceByKey")
    @ApiOperation(value = "根据流程实例key删除流程实例", notes = "根据流程实例key删除流程实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程实例Key", dataType = "String", paramType = "query"),
    })
    public RestMsg deleteProcessInstanceByKey(@RequestParam("processDefinitionKey") String processDefinitionKey) {
        RestMsg restMsg = new RestMsg();
        try {
            processService.deleteInstanceByKey(processDefinitionKey);
            restMsg = RestMsg.success("删除失败", "");
        } catch (Exception e) {
            e.printStackTrace();
            restMsg = RestMsg.fail("删除失败" +  e.getMessage(),null);
        }

        return restMsg;
    }

}