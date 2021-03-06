package com.gemini.workflow.service;

import java.util.List;
import java.util.Map;

public interface DeployService {
    public Map<String, Object> listRelease(Map<String,Object> queryparam);
    public void deploy(String modelId) throws Exception;
    public void deleteProcess(String deploymentId, boolean cascade) throws Exception;//cascading=false时只能删除未发布的流程，为true时会删除已发布的流程及所有实例，需要谨慎使用。
}
