package com.gemini.toolkit.universal.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gemini.toolkit.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;


@TableName("t_form_model")
@KeySequence("t_form_model_seq")
public class Model extends BaseEntity implements Serializable {

    @TableField("model_name")
    private String modelName;

    @TableField("type")
    private String type;

    @TableField("enable")
    private String enable;

    @TableField("before_init")
    private String beforeInit;

    @TableField("init")
    private String init;

    @TableField("after_init")
    private String afterInit;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getBeforeInit() {
        return beforeInit;
    }

    public void setBeforeInit(String beforeInit) {
        this.beforeInit = beforeInit;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public String getAfterInit() {
        return afterInit;
    }

    public void setAfterInit(String afterInit) {
        this.afterInit = afterInit;
    }

    @Override
    public String toString() {
        return "Model{" +
                "modelName='" + modelName + '\'' +
                ", type='" + type + '\'' +
                ", enable='" + enable + '\'' +
                ", beforeInit='" + beforeInit + '\'' +
                ", init='" + init + '\'' +
                ", afterInit='" + afterInit + '\'' +
                '}';
    }
}
