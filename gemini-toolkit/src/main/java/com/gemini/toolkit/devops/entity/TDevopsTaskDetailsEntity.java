package com.gemini.toolkit.devops.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 运维任务详细表
 * </p>
 *
 * @author BHH
 * @since 2021-05-19
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_tk_devops_task_details")
@KeySequence("t_tk_devops_task_details_seq")
public class TDevopsTaskDetailsEntity  extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运维任务ID
     */
    private String devopsTaskId;

    /**
     * 运维STEPID
     */
    private Integer devopsStepId;

    /**
     * 运维STEP
     */
    private String devopsStep;

    /**
     * 功能描述
     */
    private String deptdesc;

    /**
     * 功能类型
     */
    private String devopsType;

    /**
     * 执行sql
     */
    private String devopsExecSql;

    /**
     * 查询结果
     */
    private String devopsExecData;

    /**
     * 修改前数据
     */
    private String devopsBackUpData;
    
    /**
     * 回滚sql
     */
    private String devopsRollbackSql;
    
    /**
     * 执行参数
     */
    private String execParams;

    /**
     * 执行状态
     */
    private String status;

    public String getDevopsTaskId() {
        return devopsTaskId;
    }

    public void setDevopsTaskId(String devopsTaskId) {
        this.devopsTaskId = devopsTaskId;
    }

    public Integer getDevopsStepId() {
        return devopsStepId;
    }

    public void setDevopsStepId(Integer devopsStepId) {
        this.devopsStepId = devopsStepId;
    }

    public String getDevopsStep() {
        return devopsStep;
    }

    public void setDevopsStep(String devopsStep) {
        this.devopsStep = devopsStep;
    }

    public String getDeptdesc() {
        return deptdesc;
    }

    public void setDeptdesc(String deptdesc) {
        this.deptdesc = deptdesc;
    }

    public String getDevopsType() {
        return devopsType;
    }

    public void setDevopsType(String devopsType) {
        this.devopsType = devopsType;
    }

    public String getDevopsExecSql() {
        return devopsExecSql;
    }

    public void setDevopsExecSql(String devopsExecSql) {
        this.devopsExecSql = devopsExecSql;
    }

    public String getDevopsExecData() {
        return devopsExecData;
    }

    public void setDevopsExecData(String devopsExecData) {
        this.devopsExecData = devopsExecData;
    }

    public String getDevopsBackUpData() {
        return devopsBackUpData;
    }

    public void setDevopsBackUpData(String devopsBackUpData) {
        this.devopsBackUpData = devopsBackUpData;
    }

    public String getDevopsRollbackSql() {
        return devopsRollbackSql;
    }

    public void setDevopsRollbackSql(String devopsRollbackSql) {
        this.devopsRollbackSql = devopsRollbackSql;
    }

    public String getExecParams() {
        return execParams;
    }

    public void setExecParams(String execParams) {
        this.execParams = execParams;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TDevopsTaskDetailsEntity{" +
                "devopsTaskId='" + devopsTaskId + '\'' +
                ", devopsStepId=" + devopsStepId +
                ", devopsStep='" + devopsStep + '\'' +
                ", deptdesc='" + deptdesc + '\'' +
                ", devopsType='" + devopsType + '\'' +
                ", devopsExecSql='" + devopsExecSql + '\'' +
                ", devopsExecData='" + devopsExecData + '\'' +
                ", devopsBackUpData='" + devopsBackUpData + '\'' +
                ", devopsRollbackSql='" + devopsRollbackSql + '\'' +
                ", execParams='" + execParams + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
