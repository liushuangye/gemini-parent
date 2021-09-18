package com.gemini.workflow.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gemini.workflow.DTO.ModelImportDTO;
import com.gemini.workflow.service.BaseService;
import com.gemini.workflow.service.DefineService;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.repository.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefineServiceImpl extends BaseService implements DefineService {
    @Override
    public Map<String, Object> listModel(Map<String,Object> queryparam) throws Exception{
        String modelName = (String)queryparam.get("modelName");
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
        ModelQuery modelQuery = repositoryService.createModelQuery();
        if(StringUtils.isNotEmpty(modelName))modelQuery = modelQuery.modelNameLike("%"+modelName+"%");
        long total = modelQuery.count();
        List<Model> list = modelQuery.listPage((pageNum - 1) * pageSize, pageSize);


        Map<String,Model> map = new HashMap<String,Model>();

        if(list != null && list.size() >0){
            for(Model model:list){
                map.put(model.getId(), model);
            }
        }
        List<Model> modelList = new ArrayList<Model>(map.values());
        for (Model model : modelList) {
            JSONObject metaJson = JSONObject.parseObject(model.getMetaInfo());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",model.getId());
            jsonObject.put("key",model.getKey());
            jsonObject.put("name",model.getName());
            jsonObject.put("version",model.getVersion());
            jsonObject.put("createTime", DateUtil.format(model.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            jsonObject.put("lastUpdateTime", DateUtil.format(model.getLastUpdateTime(),"yyyy-MM-dd HH:mm:ss"));
            jsonObject.put("metaInfo",model.getMetaInfo());
            jsonObject.put("description",metaJson.getString("description"));
            jsonObject.put("deploymentId",model.getDeploymentId());

            modelArray.add(jsonObject);
        }
        result.put("records",modelArray);
        result.put("total",total);
        return result;
    }

    public BpmnModel getBpmnModelById(String modelId) throws IOException {
        Model modelData = repositoryService.getModel(modelId);
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
        BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        return bpmnModel;
    }

    @Override
    public void importBpmnModel(ModelImportDTO modelImportDTO) throws Exception {
        MultipartFile multipartFile = modelImportDTO.getFile();
        String fileName = modelImportDTO.getFileName();
        String modelName = modelImportDTO.getModelName();
        String description = modelImportDTO.getDescription();
        try(InputStream inputStream = multipartFile.getInputStream()) {
            XMLInputFactory xif = XMLInputFactory.newInstance();
            InputStreamReader in = new InputStreamReader(inputStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            // 转为bpmnModel
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);
            ObjectNode objectNode = new BpmnJsonConverter().convertToJson(bpmnModel);

            //metaInfo
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode modelObjectNode = objectMapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, modelName);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);

            Model model = repositoryService.newModel();
            model.setName(modelName);
            model.setMetaInfo(modelObjectNode.toString());
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), objectNode.toString().getBytes("utf-8"));

            model = repositoryService.getModel(model.getId());

            // 此处保存上传文件的原始文件名，但必须是以.bpmn20.xml结尾，否则无法产生流程数据且不报错。
//            if (!srcFileName.endsWith(".bpmn20.xml"))
//                srcFileName += ".bpmn20.xml";
//            Deployment deployment = repositoryService.createDeployment()
//                    .name(String.valueOf(userID))           // deployment名称，此处用于保存导入用户ID
//                    .addBpmnModel(fileName, bpmnModel)
//                    .enableDuplicateFiltering()
//                    .deploy();


//            model.setDeploymentId(deployment.getId());
            repositoryService.saveModel(model);
        }

    }

    @Override
    public void deleteModel(String modelId) throws Exception {
        repositoryService.deleteModel(modelId);
    }
}
