package com.gemini.toolkit.devops.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * 运维工具列表
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */

public class DevopsToolsDto implements Serializable {

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

    @Override
    public String toString() {
        return "DevopsToolsDto{" +
                "devopsName='" + devopsName + '\'' +
                ", deptdesc='" + deptdesc + '\'' +
                ", id=" + id +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
