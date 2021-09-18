package com.gemini.workflow.controller;

import com.gemini.workflow.service.DeployService;
import com.gemini.workflow.utils.RestMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "流程发布管理")
public class DeployController extends BaseController {

    @Autowired
    private DeployService deployService;

    @GetMapping(path = "listDef")
    @ApiOperation(value = "查询流程定义", notes = "查询流程定义")
//    @ApiImplicitParams({})
    public RestMsg listDef() {
        RestMsg restMsg = new RestMsg();

        try {
            List defList = deployService.listDeploy();
            restMsg = RestMsg.success("查询成功", defList);
        } catch (Exception e) {
            restMsg = RestMsg.fail("删除失败:" + e.getMessage(),null);
        }
        return restMsg;
    }

    @PostMapping(path = "deploy")
    @ApiOperation(value = "根据modelId部署流程", notes = "根据modelId部署流程，传map")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "设计的流程图模型ID", dataType = "String", paramType = "query")
    })
    public RestMsg deploy(@RequestBody  Map<String,Object> map) {
        String modelId = (String) map.get("modelId");

        RestMsg restMsg = new RestMsg();
        Deployment deployment = null;
        try {
            deployService.deploy(modelId);
            restMsg = RestMsg.success("部署成功", "");
        } catch (Exception e) {
            restMsg = RestMsg.fail("部署失败:" + e.getMessage(),null);
//            log.error("根据modelId部署流程,异常:{}", e);
        }

        if (deployment != null) {
            Map<String, String> result = new HashMap<>(2);
            result.put("deploymentId", deployment.getId());
            result.put("deploymentName", deployment.getName());
            restMsg = RestMsg.success("部署成功", result);
        }
        return restMsg;
    }


    @DeleteMapping(path = "deleteProcess")
    @ApiOperation(value = "根据部署ID删除流程", notes = "根据部署ID删除流程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deploymentId", value = "部署ID", dataType = "String", paramType = "query", example = ""),
            @ApiImplicitParam(name = "cascade", value = "是否级联删除-默认false", dataType = "boolean", paramType = "query", example = "false")
    })
    public RestMsg deleteProcess(@RequestParam("deploymentId") String deploymentId,@RequestParam("cascade") boolean cascade) {
        RestMsg restMsg = new RestMsg();
        try {
            deployService.deleteProcess(deploymentId,cascade);
            restMsg = RestMsg.success("删除成功", null);
        } catch (Exception e) {
            restMsg = RestMsg.fail("删除失败:" + e.getMessage(),null);
//            log.error("根据部署ID删除流程,异常:{}", e);
        }
        return restMsg;
    }
}