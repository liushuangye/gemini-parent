package com.gemini.toolkit.devops.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * <p>
 * 运维工具列表
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */

public class DevopsToolsExecStepDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long id;
    
    /**
     * 指定step的Id
     */
    private Integer stepId;
    
    /**
     * 执行参数
     */
    private Map<String,Object> params;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStepId() {
        return stepId;
    }

    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "DevopsToolsExecStepDto{" +
                "id=" + id +
                ", stepId=" + stepId +
                ", params=" + params +
                '}';
    }
}
