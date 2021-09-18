package com.gemini.workflow.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@ApiModel(description = "模型导入-数据转换对象")
public class ModelImportDTO {
    @ApiModelProperty(value = "名称", name = "modelName",  example = "会签模型")
    private String modelName;

    @ApiModelProperty(value = "描述", name = "processDefinitionKey",  example = "会签流程")
    private String description;

    @ApiModelProperty(value = "文件", name = "processDefinitionId")
    private MultipartFile file;

    @ApiModelProperty(value = "文件名称", name = "fileName", example = "xxx.bpmn20.xml")
    private String fileName;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
