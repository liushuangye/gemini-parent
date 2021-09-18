package com.gemini.toolkit.basedata.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 模板类型
 * @author BHH
 * @since 2021-05-1
 */

public class TemplateTypeDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
     * 值
     */
    private String value;
    
    /**
     * 标签
     */
    private String label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "TemplateTypeDto{" +
                "value='" + value + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
