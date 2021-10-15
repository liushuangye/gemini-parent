package com.gemini.workflow.mapper;


import com.gemini.workflow.entity.Role;
import com.gemini.workflow.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程Mapper
 */
@Mapper
public interface WorkflowMapper {
    /** 根据一组businessKey+userId获取相应的流程实例 */
    public List<Map> searchProInstByBusinessKeys(@Param("businessKeys") List<String> businessKeys,@Param("userId") String userId);
}
