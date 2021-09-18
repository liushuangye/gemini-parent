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
@TableName("t_tk_toolkit_oplog_detail")
public class TToolkitOplogDetailEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid;

    private String requestBody;

    private String requestHeaders;

    private String exceptionStacktrace;

    private String tSysOplogid;

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

    public String gettSysOplogid() {
        return tSysOplogid;
    }

    public void setTSysOplogid(String tSysOplogid) {
        this.tSysOplogid = tSysOplogid;
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
        return "TToolkitOplogDetailEntity{" +
                "uuid='" + uuid + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", requestHeaders='" + requestHeaders + '\'' +
                ", exceptionStacktrace='" + exceptionStacktrace + '\'' +
                ", tSysOplogid='" + tSysOplogid + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", updateUserId='" + updateUserId + '\'' +
                ", createDateTime=" + createDateTime +
                ", updateDateTime=" + updateDateTime +
                ", deleteFlg='" + deleteFlg + '\'' +
                '}';
    }
}
