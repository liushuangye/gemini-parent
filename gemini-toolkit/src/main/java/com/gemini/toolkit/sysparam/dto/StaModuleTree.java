package com.gemini.toolkit.sysparam.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * 树
 * </p>
 *
 * @author BHH
 * @since 2021-05-09
 */

public class StaModuleTree implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
     * 页面上显示的内容
     */
    private String label;
    
    /**
	 * 主键ID
	 */
	private String busId;
	
	/**
     * 直属下级
     */
    private List<StaModuleTree> children;
    
    /**
     * 添加子节点
     * @param child
     */
    public void addChild(StaModuleTree child) {
        if (child == null) {
            return;
        }
        if (this.getChildren() == null) {
            this.setChildren(new ArrayList<StaModuleTree>());
        }
        this.children.add(child);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public List<StaModuleTree> getChildren() {
        return children;
    }

    public void setChildren(List<StaModuleTree> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "StaModuleTree{" +
                "label='" + label + '\'' +
                ", busId='" + busId + '\'' +
                ", children=" + children +
                '}';
    }
}
