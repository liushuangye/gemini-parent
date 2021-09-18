package com.gemini.toolkit.operatelog.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;


public class TOplogDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 序号
     */
    private String uuid;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 操作时间
     */
    private Long execTime;

    /**
     * 操作时间From
     */
    private String requestTimeFrom;

    /**
     * 操作时间To
     */
    private String requestTimeTo;

    /**
     * 操作用户
     */
    private String userName;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 操作结果
     */
    private Integer resultCode;

    /**
     * 请求参数
     */
    private String requestBody;

    /**
     * 请求头部
     */
    private String requestHeaders;

    /**
     * 异常信息
     */
    private String exceptionStacktrace;

    /**
     * 类名
     */
    private String className;

    /**
     * uri
     */
    private String requestUri;

    /**
     * op名
     */
    private String opName;

    /**
     * remarks
     */
    private String remarks;

    /**
     * 结果信息
     */
    private String resultMsg;

    /**
     * requestTime
     */
    private Date requestTime;

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

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public String getRequestTimeFrom() {
        return requestTimeFrom;
    }

    public void setRequestTimeFrom(String requestTimeFrom) {
        this.requestTimeFrom = requestTimeFrom;
    }

    public String getRequestTimeTo() {
        return requestTimeTo;
    }

    public void setRequestTimeTo(String requestTimeTo) {
        this.requestTimeTo = requestTimeTo;
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

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getExceptionStacktrace() {
        return exceptionStacktrace;
    }

    public void setExceptionStacktrace(String exceptionStacktrace) {
        this.exceptionStacktrace = exceptionStacktrace;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
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

    @Override
    public String toString() {
        return "TOplogDto{" +
                "uuid='" + uuid + '\'' +
                ", userCode='" + userCode + '\'' +
                ", execTime=" + execTime +
                ", requestTimeFrom='" + requestTimeFrom + '\'' +
                ", requestTimeTo='" + requestTimeTo + '\'' +
                ", userName='" + userName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", methodName='" + methodName + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", resultCode=" + resultCode +
                ", requestBody='" + requestBody + '\'' +
                ", requestHeaders='" + requestHeaders + '\'' +
                ", exceptionStacktrace='" + exceptionStacktrace + '\'' +
                ", className='" + className + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", opName='" + opName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", requestTime=" + requestTime +
                '}';
    }
}
