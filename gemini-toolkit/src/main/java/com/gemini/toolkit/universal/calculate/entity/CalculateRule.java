package com.gemini.toolkit.universal.calculate.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


@TableName("calculate_rule")
@KeySequence("calculate_rule_seq")
public class CalculateRule implements Serializable {

    /**
     * 主鍵ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("interface_id")
    private String interfaceId;

    @TableField("bean_name")
    private String beanName;

    @TableField("calculate_rule")
    private String calculateRule;

    @TableField("calculate_type")
    private String calculateType;

    @TableField("extend_info")
    private String extendInfo;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField("modified_time")
    private Date modifiedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getCalculateRule() {
        return calculateRule;
    }

    public void setCalculateRule(String calculateRule) {
        this.calculateRule = calculateRule;
    }

    public String getCalculateType() {
        return calculateType;
    }

    public void setCalculateType(String calculateType) {
        this.calculateType = calculateType;
    }

    public String getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(String extendInfo) {
        this.extendInfo = extendInfo;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "CalculateRule{" +
                "id=" + id +
                ", interfaceId='" + interfaceId + '\'' +
                ", beanName='" + beanName + '\'' +
                ", calculateRule='" + calculateRule + '\'' +
                ", calculateType='" + calculateType + '\'' +
                ", extendInfo='" + extendInfo + '\'' +
                ", createdTime=" + createdTime +
                ", modifiedTime=" + modifiedTime +
                '}';
    }
}
