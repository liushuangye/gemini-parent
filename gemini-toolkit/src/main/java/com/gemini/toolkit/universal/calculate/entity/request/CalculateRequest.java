package com.gemini.toolkit.universal.calculate.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;


public class CalculateRequest implements Serializable {

    private String interfaceId;

    private Map<String, Object> extendInfo;

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public Map<String, Object> getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(Map<String, Object> extendInfo) {
        this.extendInfo = extendInfo;
    }

    @Override
    public String toString() {
        return "CalculateRequest{" +
                "interfaceId='" + interfaceId + '\'' +
                ", extendInfo=" + extendInfo +
                '}';
    }
}