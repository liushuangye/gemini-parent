package com.gemini.workflow._4ctms.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrialMapper {
    @Select("")
    public List<String> getTrialUserIds(String trialId);//根据qcTaskId获取质控任务、子任务的执行人userId

}
