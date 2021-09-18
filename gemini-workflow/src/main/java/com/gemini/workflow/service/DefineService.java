package com.gemini.workflow.service;

import com.gemini.workflow.DTO.ModelImportDTO;
import org.activiti.bpmn.model.BpmnModel;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DefineService {
    //列表分页查询
    public Map<String, Object> listModel(Map<String,Object> queryparam) throws Exception;

    /** 获取流程模型 */
    public BpmnModel getBpmnModelById(String modelId) throws IOException;
    /** 导入流程模型 */
    public void importBpmnModel(ModelImportDTO modelImportDTO) throws Exception;
    /** 删除流程模型 */
    public void deleteModel(String modelId) throws Exception;
//
//    /** 获取所有流程模型 */
//    public List<BpmnModel> getAllBpmnModel(String modelId) throws IOException;
//    /** 批量流程模型 */
//    public void importAllBpmnModel(String modelId) throws IOException;
}
