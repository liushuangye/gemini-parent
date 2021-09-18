package com.gemini.workflow.controller;

import com.gemini.workflow.DTO.TaskDTO;
import com.gemini.workflow.exception.ProcessInstanceNotFoundException;
import com.gemini.workflow.service.GeminiTaskService;
import com.gemini.workflow.utils.RestMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
public class TaskController extends BaseController {
    @Autowired
    GeminiTaskService geminiTaskService;

    @GetMapping(path = "findTask")
    @ApiOperation(value = "查询当前用户的任务", notes = "包含个人任务和候选任务，processDefinitionKey可选，传空则查询改用户所有任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "待办人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "processDefinitionKey", value = "流程定义Key", dataType = "String", paramType = "query"),
    })
    public RestMsg findTask(@RequestParam("userId") String userId,@RequestParam("processDefinitionKey") String processDefinitionKey) {
        List resultList = new ArrayList<>();
        RestMsg restMsg = new RestMsg();
        try {
            resultList = geminiTaskService.findTask(userId,processDefinitionKey);
            restMsg = RestMsg.success("查询成功", resultList);
        } catch (Exception e) {
            restMsg = RestMsg.fail("查询失败:" + e.getMessage(),null);
            return restMsg;
        }
        return restMsg;
    }


    @PostMapping(path = "completeTask")
    @ApiOperation(value = "完成任务", notes = "完成任务，任务进入下一个节点")
    @ApiImplicitParam(name = "taskDTO", value = "任务处理对象", required = true, dataType = "TaskDTO")
    public RestMsg completeTask(@RequestBody @Valid TaskDTO taskDTO) {
        RestMsg restMsg = new RestMsg();
        try {
            geminiTaskService.completeTask(taskDTO);
            restMsg = RestMsg.success("完成任务成功", "");
        }catch (ProcessInstanceNotFoundException e) {
            restMsg = RestMsg.response(ProcessInstanceNotFoundException.code,ProcessInstanceNotFoundException.msg,null);
        }catch (Exception e) {
            restMsg = RestMsg.fail("完成任务失败:" + e.getMessage(),null);
        }
        return restMsg;
    }

    @PostMapping(path = "claimTask")
    @ApiOperation(value = "任务拾取", notes = "任务拾取，从候选人变为负责人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "待办任务Id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "String", paramType = "query")
    })
    public RestMsg claimTask( @RequestParam("taskId") String taskId , @RequestParam("userId") String userId) {
        RestMsg restMsg = new RestMsg();
        try {
            geminiTaskService.claimTask(taskId,userId);
            restMsg = RestMsg.success("拾取任务成功", "");
        } catch (Exception e) {
            restMsg = RestMsg.fail("拾取任务失败:" + e.getMessage(),null);
        }
        return restMsg;
    }

    @PostMapping(path = "turnTask")
    @ApiOperation(value = "任务转办", notes = "任务转办，把任务交给别人处理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "String", paramType = "query"),
    })
    public RestMsg turnTask(@RequestParam("taskId") String taskId, @RequestParam("userId") String userId) {
        RestMsg restMsg = new RestMsg();
        try {
            geminiTaskService.turnTask(taskId, userId);
            restMsg = RestMsg.success("转交任务成功", "");
        } catch (Exception e) {
            restMsg = RestMsg.fail("转交任务失败:" + e.getMessage(),null);
//            log.error("任务转办,异常:{}", e);
        }
        return restMsg;
    }
}