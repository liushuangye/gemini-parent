/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.gemini.toolkit.universal.calculate.groovy.cache;


import lombok.Data;


public class GroovyInfo {

    /**
     * 类名
     */
    private String className;

    /**
     * 脚本内容
     */
    private String groovyContent;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGroovyContent() {
        return groovyContent;
    }

    public void setGroovyContent(String groovyContent) {
        this.groovyContent = groovyContent;
    }

    @Override
    public String toString() {
        return "GroovyInfo{" +
                "className='" + className + '\'' +
                ", groovyContent='" + groovyContent + '\'' +
                '}';
    }
}
