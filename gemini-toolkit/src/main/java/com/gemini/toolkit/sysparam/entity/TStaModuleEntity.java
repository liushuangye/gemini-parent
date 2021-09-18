package com.gemini.toolkit.sysparam.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 树code
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_sta_module")
@KeySequence("t_sta_module_seq")
public class TStaModuleEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 系统区分
     */
    private String sysName;

    /**
     * 业务编码
     */
    private String busId;

    /**
     * 业务名称
     */
    private String busName;

    /**
     * 业务缩写
     */
    private String busNameShort;

    /**
     * 排序码
     */
    private String displayOrder;

    /**
     * 启用状态
     */
    private String enableFlg;

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusNameShort() {
        return busNameShort;
    }

    public void setBusNameShort(String busNameShort) {
        this.busNameShort = busNameShort;
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(String enableFlg) {
        this.enableFlg = enableFlg;
    }

    @Override
    public String toString() {
        return "TStaModuleEntity{" +
                "sysName='" + sysName + '\'' +
                ", busId='" + busId + '\'' +
                ", busName='" + busName + '\'' +
                ", busNameShort='" + busNameShort + '\'' +
                ", displayOrder='" + displayOrder + '\'' +
                ", enableFlg='" + enableFlg + '\'' +
                '}';
    }
}
