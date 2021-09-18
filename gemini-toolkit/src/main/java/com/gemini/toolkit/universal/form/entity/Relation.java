package com.gemini.toolkit.universal.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gemini.toolkit.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;


@TableName("t_form_rel")
@KeySequence("t_form_rel_seq")
public class Relation extends BaseEntity implements Serializable {

    @TableField("from_model")
    private String fromModel;

    @TableField("to_model")
    private String toModel;

    @TableField("rel_from")
    private String relFrom;

    @TableField("rel_to")
    private String relTo;

    @TableField("type")
    private String type;

    @TableField("rule")
    private String rule;

    @TableField("list_name")
    private String listName;

    public String getFromModel() {
        return fromModel;
    }

    public void setFromModel(String fromModel) {
        this.fromModel = fromModel;
    }

    public String getToModel() {
        return toModel;
    }

    public void setToModel(String toModel) {
        this.toModel = toModel;
    }

    public String getRelFrom() {
        return relFrom;
    }

    public void setRelFrom(String relFrom) {
        this.relFrom = relFrom;
    }

    public String getRelTo() {
        return relTo;
    }

    public void setRelTo(String relTo) {
        this.relTo = relTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "fromModel='" + fromModel + '\'' +
                ", toModel='" + toModel + '\'' +
                ", relFrom='" + relFrom + '\'' +
                ", relTo='" + relTo + '\'' +
                ", type='" + type + '\'' +
                ", rule='" + rule + '\'' +
                ", listName='" + listName + '\'' +
                '}';
    }
}
