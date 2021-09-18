package com.gemini.toolkit.devops.dto;

import java.io.Serializable;

import com.gemini.toolkit.devops.xml.model.Devops;

import lombok.Data;

/**
 * <p>
 * 运维工具列表
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */

public class DevopsToolsDetailDto implements Serializable {

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
     * xml解析后结果
     */
    private Devops devops;


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

    public Devops getDevops() {
        return devops;
    }

    public void setDevops(Devops devops) {
        this.devops = devops;
    }

    @Override
    public String toString() {
        return "DevopsToolsDetailDto{" +
                "devopsName='" + devopsName + '\'' +
                ", deptdesc='" + deptdesc + '\'' +
                ", id=" + id +
                ", uuid='" + uuid + '\'' +
                ", devops=" + devops +
                '}';
    }
}
