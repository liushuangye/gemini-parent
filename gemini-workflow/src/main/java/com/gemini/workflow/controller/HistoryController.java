package com.gemini.workflow.controller;

import com.gemini.workflow.service.GeminiHistoryService;
import com.gemini.workflow.utils.RestMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjialin<br>
 * @version 1.0<br>
 * @createDate 2019/08/30 11:59 <br>
 * @Description <p> 任务相关接口 </p>
 */

@RestController
@Api(tags = "任务相关接口")
public class HistoryController extends BaseController {
    @Autowired
    GeminiHistoryService geminiHistoryService;

    @GetMapping(path = "findByProcessInstanceId")
    @ApiOperation(value = "审查履历查询", notes = "已完成的、未完成的都会查出来，未完成的endTime为空")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", value = "流程实例Id", dataType = "String", paramType = "query"),
    })
    public RestMsg findByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId) {
        List resultList = new ArrayList<>();
        RestMsg restMsg = new RestMsg();
        try {
            resultList = geminiHistoryService.findByProcessInstanceId(processInstanceId);
            restMsg = RestMsg.success("查询成功", resultList);
        } catch (Exception e) {
            restMsg = RestMsg.fail("查询失败:" + e.getMessage(),null);
            return restMsg;
        }
        return restMsg;
    }
    @GetMapping(path = "getTaskVariables")
    @ApiOperation(value = "历史任务快照查询", notes = "即历史任务的流程变量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processInstanceId", value = "流程实例Id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "executionId", value = "流程实例Id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "taskId", value = "流程实例Id", dataType = "String", paramType = "query"),
    })
    public RestMsg getTaskVariables(@RequestParam("processInstanceId") String processInstanceId,@RequestParam("executionId") String executionId,@RequestParam("taskId") String taskId) {
        List resultList = new ArrayList<>();
        RestMsg restMsg = new RestMsg();
        try {
            resultList = geminiHistoryService.getTaskVariables(processInstanceId,executionId,taskId);
            restMsg = RestMsg.success("查询成功", resultList);
        } catch (Exception e) {
            restMsg = RestMsg.fail("查询失败:" + e.getMessage(),null);
            return restMsg;
        }
        return restMsg;
    }
}