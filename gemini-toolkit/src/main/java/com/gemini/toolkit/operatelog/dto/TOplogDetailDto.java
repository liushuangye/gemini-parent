package com.gemini.toolkit.operatelog.dto;


import lombok.Data;


public class TOplogDetailDto{
	/**
     * 序号
     */
    private String id;

    /**
     * 请求参数
     */
    private Integer requestBody;

    /**
     * 请求头部
     */
    private String requestHeaders;

    /**
     * 异常信息
     */
    private Integer exceptionStacktrace;

    /**
     * tSysOplogid
     */
    private String tSysOplogid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Integer requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Integer getExceptionStacktrace() {
        return exceptionStacktrace;
    }

    public void setExceptionStacktrace(Integer exceptionStacktrace) {
        this.exceptionStacktrace = exceptionStacktrace;
    }

    public String gettSysOplogid() {
        return tSysOplogid;
    }

    public void setTSysOplogid(String tSysOplogid) {
        this.tSysOplogid = tSysOplogid;
    }

    @Override
    public String toString() {
        return "TOplogDetailDto{" +
                "id='" + id + '\'' +
                ", requestBody=" + requestBody +
                ", requestHeaders='" + requestHeaders + '\'' +
                ", exceptionStacktrace=" + exceptionStacktrace +
                ", tSysOplogid='" + tSysOplogid + '\'' +
                '}';
    }
}
