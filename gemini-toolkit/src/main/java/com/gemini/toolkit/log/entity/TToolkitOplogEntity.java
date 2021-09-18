package com.gemini.toolkit.log.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author BHH
 * @since 2021-05-10
 */

@EqualsAndHashCode(callSuper = false)
@TableName("t_tk_toolkit_oplog")
public class TToolkitOplogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid;

    private String userCode;

    private String userName;

    private String ipAddress;

    private String className;

    private String methodName;

    private String requestUri;

    private String moduleName;

    private String opName;

    private String remarks;

    private Integer resultCode;

    private String resultMsg;

    private Date requestTime;

    private Long execTime;

    private String createUserId;

    private String updateUserId;

    private Date createDateTime;

    private Date updateDateTime;

    private String deleteFlg;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg(String deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    @Override
    public String toString() {
        return "TToolkitOplogEntity{" +
                "uuid='" + uuid + '\'' +
                ", userCode='" + userCode + '\'' +
                ", userName='" + userName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", opName='" + opName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", resultCode=" + resultCode +
                ", resultMsg='" + resultMsg + '\'' +
                ", requestTime=" + requestTime +
                ", execTime=" + execTime +
                ", createUserId='" + createUserId + '\'' +
                ", updateUserId='" + updateUserId + '\'' +
                ", createDateTime=" + createDateTime +
                ", updateDateTime=" + updateDateTime +
                ", deleteFlg='" + deleteFlg + '\'' +
                '}';
    }
}
