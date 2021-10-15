package com.gemini.workflow.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gemini.workflow.DTO.ModelImportDTO;
import com.gemini.workflow.service.DefineService;
import com.gemini.workflow.utils.RestMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "流程定义管理")
public class DefineController extends BaseController {

    @Autowired
    private DefineService defineService;
    @PostMapping("listModel")
    @ApiOperation(value = "查询流程模型", notes = "查询流程模型:按更新时间倒排")
    public RestMsg listModel(@RequestBody Map<String,Object> queryparam) {
        RestMsg restMsg = new RestMsg();
        try {
            Map<String, Object> result = defineService.listModel(queryparam);
            restMsg = RestMsg.success("查询成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            restMsg = RestMsg.fail("查询失败", e.getMessage());
        }
        return restMsg;
    }

    @GetMapping("create")
    @ApiOperation(value = "新建流程模型", notes = "创建即保存")
    public String create(HttpServletRequest request, HttpServletResponse response) {
        try {
            ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
            RepositoryService repositoryService = processEngine.getRepositoryService();
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode editorNode = objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.set("stencilset", stencilSetNode);
            Model modelData = repositoryService.newModel();
            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, "新建流程");
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            String description = "请输入流程描述信息~";
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelData.setMetaInfo(modelObjectNode.toString());
            //保存模型
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
            // 编辑流程模型时,只需要直接跳转此url并传递上modelId即可
//            response.sendRedirect(request.getContextPath() + "/static/modeler.html?modelId=" + modelData.getId());
            return "static/modeler.html?modelId=" + modelData.getId();//VUE前端会拼接http://host:port/gemini/ 并进行跳转
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("design")
    @ApiOperation(value = "设计流程模型", notes = "设计流程模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "流程模型ID", dataType = "String", paramType = "query"),
    })
    public String design(@RequestParam("modelId") String modelId,HttpServletRequest request, HttpServletResponse response) {
        try {
            // 编辑流程模型时,只需要直接跳转此url并传递上modelId即可
//            response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelId);
            return "static/modeler.html?modelId=" +modelId;//VUE前端会拼接http://host:port/gemini/ 并进行跳转
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = "exportByModelId", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "导出流程定义", notes = "导出流程定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "流程模型ID", dataType = "String", paramType = "query"),
    })
    @ResponseBody
    public void exportByModelId(@RequestParam("modelId") String modelId, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8"); //转码
        try {
            //根据modelId获取BpmnModel对象
            BpmnModel bpmnModel = defineService.getBpmnModelById(modelId);

            //IO 返回给前端
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);

            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("content-Type", "application/xml");
            response.addHeader("Content-Disposition", "attachment; filename=" + filename);
            IOUtils.copy(in, response.getOutputStream());  //这句必须放到setHeader下面，否则10K以上的xml无法导出，
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostMapping(value = "/importBpmnModel", consumes = { "multipart/form-data" })
    @ApiOperation(value = "导入流程定义", notes = "通过[xxx.bpmn20.xml]导入流程定义")
    @ResponseBody
    public RestMsg importBpmnModel(ModelImportDTO modelImportDTO) {
        RestMsg restMsg = new RestMsg();
        try{
            defineService.importBpmnModel(modelImportDTO);
            restMsg = RestMsg.success("导入成功", "");
        }catch (Exception e){
            restMsg = RestMsg.fail("导入失败:" + e.getMessage(),null);
        }
        return restMsg;
    }
    @DeleteMapping(value = "/deleteModel/{modelId}")
    @ApiOperation(value = "删除流程定义", notes = "删除流程定义")
    @ResponseBody
    public RestMsg deleteModel(@PathVariable("modelId") String modelId) {
        RestMsg restMsg = new RestMsg();
        try{
            defineService.deleteModel(modelId);
            restMsg = RestMsg.success("删除成功", "");
        }catch (Exception e){
            e.printStackTrace();
            restMsg = RestMsg.fail("删除失败:" + e.getMessage(),null);
        }
        return restMsg;
    }
}