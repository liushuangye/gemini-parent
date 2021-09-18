package com.gemini.workflow.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@ApiModel(description = "流程实例-数据转换对象")
public class ProcessDTO {

    @ApiModelProperty(value = "流程定义的KEY", name = "processDefinitionKey", required = true,  example = "APPLY")
    @NotBlank(message = "processDefinitionKey不允许为空")
    private String processDefinitionKey;

    @ApiModelProperty(value = "业务单据key:约定为主表uuid", name = "businessKey", required = true,  example = "6965b08d-2775-4293-95c5-048cc4427b8a")
    @NotBlank(message = "businessKey不允许为空")
    private String businessKey;

    @ApiModelProperty(value = "用户Id", name = "userId",required = true,  example = "CTMS")
    @NotBlank(message = "userId不允许为空")
    private String userId;

    @ApiModelProperty(value = "流程变量-Map", name = "variables" )
    private Map variables;

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map getVariables() {
        return variables;
    }

    public void setVariables(Map variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "ProcessDTO{" +
                "processDefinitionKey='" + processDefinitionKey + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", userId='" + userId + '\'' +
                ", variables=" + variables +
                '}';
    }
}
