package com.gemini.workflow.service.component;

import com.gemini.workflow.entity.User;
import com.gemini.workflow.mapper.UserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AssigneeService {
    @Resource
    UserMapper userMapper;

    //staffId转userId
    public String getUserIdByStaffId(String staffId) throws Exception{
        String userId = "";
        if(StringUtils.isNotEmpty(staffId)){
            User user = userMapper.loadUserByStaffId(staffId);
            if (user != null){
                userId = user.getUserId();
            }
        }
        return userId;
    }
}
