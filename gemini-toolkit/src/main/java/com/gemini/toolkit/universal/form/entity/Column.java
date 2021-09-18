package com.gemini.toolkit.universal.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gemini.toolkit.base.BaseEntity;
import com.gemini.toolkit.universal.form.enums.ColumnSrcEnum;
import lombok.Data;

import java.io.Serializable;


@TableName("t_form_column")
@KeySequence("t_form_column_seq")
public class Column extends BaseEntity implements Serializable {

    @TableField("model_id")
    private String modelId;

    @TableField("column_name")
    private String columnName;

    @TableField("column_label")
    private String columnLabel;

    @TableField("before_init")
    private String beforeInit;

    /** String、Integer 等 */
    @TableField("column_type")
    private String columnType;

    @TableField("column_src")
    private ColumnSrcEnum columnSrc;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public void setColumnLabel(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    public String getBeforeInit() {
        return beforeInit;
    }

    public void setBeforeInit(String beforeInit) {
        this.beforeInit = beforeInit;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public ColumnSrcEnum getColumnSrc() {
        return columnSrc;
    }

    public void setColumnSrc(ColumnSrcEnum columnSrc) {
        this.columnSrc = columnSrc;
    }

    @Override
    public String toString() {
        return "Column{" +
                "modelId='" + modelId + '\'' +
                ", columnName='" + columnName + '\'' +
                ", columnLabel='" + columnLabel + '\'' +
                ", beforeInit='" + beforeInit + '\'' +
                ", columnType='" + columnType + '\'' +
                ", columnSrc=" + columnSrc +
                '}';
    }
}
