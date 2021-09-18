package com.gemini.toolkit.devops.entity;

import java.io.Serializable;
import java.util.Arrays;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 运维任务表
 * </p>
 *
 * @author BHH
 * @since 2021-05-19
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_tk_devops_task")
@KeySequence("t_tk_devops_task_seq")
public class TDevopsTaskEntity extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    private String serviceName;

    /**
     * 工具ID
     */
    private Long devopsTaskId;

    /**
     * xml模板
     */
    private byte[] templateXml;

    /**
     * 工具名称
     */
    private String devopsName;

    /**
     * 功能描述
     */
    private String deptdesc;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getDevopsTaskId() {
        return devopsTaskId;
    }

    public void setDevopsTaskId(Long devopsTaskId) {
        this.devopsTaskId = devopsTaskId;
    }

    public byte[] getTemplateXml() {
        return templateXml;
    }

    public void setTemplateXml(byte[] templateXml) {
        this.templateXml = templateXml;
    }

    public String getDevopsName() {
        return devopsName;
    }

    public void setDevopsName(String devopsName) {
        this.devopsName = devopsName;
    }

    public String getDeptdesc() {
        return deptdesc;
    }

    public void setDeptdesc(String deptdesc) {
        this.deptdesc = deptdesc;
    }

    @Override
    public String toString() {
        return "TDevopsTaskEntity{" +
                "serviceName='" + serviceName + '\'' +
                ", devopsTaskId=" + devopsTaskId +
                ", templateXml=" + Arrays.toString(templateXml) +
                ", devopsName='" + devopsName + '\'' +
                ", deptdesc='" + deptdesc + '\'' +
                '}';
    }
}
