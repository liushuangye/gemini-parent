/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package com.gemini.toolkit.universal.calculate.groovy.cache;


import lombok.Data;


public class BeanName {

    /**
     * 类名
     */
    private String interfaceId;

    /**
     * 脚本内容
     */
    private String beanName;

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

    @Override
    public String toString() {
        return "BeanName{" +
                "interfaceId='" + interfaceId + '\'' +
                ", beanName='" + beanName + '\'' +
                '}';
    }
}
