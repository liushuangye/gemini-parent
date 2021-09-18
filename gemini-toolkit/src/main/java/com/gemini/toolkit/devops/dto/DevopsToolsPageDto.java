package com.gemini.toolkit.devops.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 运维工具列表
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */

public class DevopsToolsPageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工具名称
     */
    private String devopsName;

    /**
     * 功能描述
     */
    private String deptdesc;

    /**
     * ID
     */
    private Long id;

    /**
     * UUID
     */
    private String uuid;
    
    /**
     * 导入时间
     */
    private Date createTime;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DevopsToolsPageDto{" +
                "devopsName='" + devopsName + '\'' +
                ", deptdesc='" + deptdesc + '\'' +
                ", id=" + id +
                ", uuid='" + uuid + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
