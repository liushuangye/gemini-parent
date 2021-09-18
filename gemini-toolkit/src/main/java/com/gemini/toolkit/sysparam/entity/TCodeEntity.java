package com.gemini.toolkit.sysparam.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统配置
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_code")
@KeySequence("t_code_seq")
public class TCodeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 数据种类
     */
    private String codeType;

    /**
     * 数据编号
     */
    private String codeId;

    /**
     * 数据名称
     */
    private String codeName;

    /**
     * 数据简称
     */
    private String codeRnm;

    /**
     * 值１
     */
    private String codeValue1;

    /**
     * 值2
     */
    private String codeValue2;

    /**
     * 值3
     */
    private String codeValue3;

    /**
     * 值4
     */
    private String codeValue4;

    /**
     * 值5
     */
    private String codeValue5;

    /**
     * 字典项目备注
     */
    private String codeRemark;
    

    /**
     * 排序码
     */
    private Long displayOrder;
    
    /**
     * 业务编码
     */
    private String busId;

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeRnm() {
        return codeRnm;
    }

    public void setCodeRnm(String codeRnm) {
        this.codeRnm = codeRnm;
    }

    public String getCodeValue1() {
        return codeValue1;
    }

    public void setCodeValue1(String codeValue1) {
        this.codeValue1 = codeValue1;
    }

    public String getCodeValue2() {
        return codeValue2;
    }

    public void setCodeValue2(String codeValue2) {
        this.codeValue2 = codeValue2;
    }

    public String getCodeValue3() {
        return codeValue3;
    }

    public void setCodeValue3(String codeValue3) {
        this.codeValue3 = codeValue3;
    }

    public String getCodeValue4() {
        return codeValue4;
    }

    public void setCodeValue4(String codeValue4) {
        this.codeValue4 = codeValue4;
    }

    public String getCodeValue5() {
        return codeValue5;
    }

    public void setCodeValue5(String codeValue5) {
        this.codeValue5 = codeValue5;
    }

    public String getCodeRemark() {
        return codeRemark;
    }

    public void setCodeRemark(String codeRemark) {
        this.codeRemark = codeRemark;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    @Override
    public String toString() {
        return "TCodeEntity{" +
                "codeType='" + codeType + '\'' +
                ", codeId='" + codeId + '\'' +
                ", codeName='" + codeName + '\'' +
                ", codeRnm='" + codeRnm + '\'' +
                ", codeValue1='" + codeValue1 + '\'' +
                ", codeValue2='" + codeValue2 + '\'' +
                ", codeValue3='" + codeValue3 + '\'' +
                ", codeValue4='" + codeValue4 + '\'' +
                ", codeValue5='" + codeValue5 + '\'' +
                ", codeRemark='" + codeRemark + '\'' +
                ", displayOrder=" + displayOrder +
                ", busId='" + busId + '\'' +
                '}';
    }
}
