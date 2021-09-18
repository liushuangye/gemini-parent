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
 * 运维工具列表
 * </p>
 *
 * @author BHH
 * @since 2021-05-14
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_tk_devops_tools")
@KeySequence("t_tk_devops_tools_seq")
public class TDevopsToolsEntity extends BaseEntity implements Serializable {

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
     * xml模板
     */
    private byte[] templateXml;

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

    public byte[] getTemplateXml() {
        return templateXml;
    }

    public void setTemplateXml(byte[] templateXml) {
        this.templateXml = templateXml;
    }

    @Override
    public String toString() {
        return "TDevopsToolsEntity{" +
                "devopsName='" + devopsName + '\'' +
                ", deptdesc='" + deptdesc + '\'' +
                ", templateXml=" + Arrays.toString(templateXml) +
                '}';
    }
}
