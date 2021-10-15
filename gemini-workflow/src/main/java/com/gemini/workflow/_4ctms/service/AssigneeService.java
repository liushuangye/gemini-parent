package com.gemini.workflow._4ctms.service;

import com.gemini.workflow.entity.User;
import com.gemini.workflow.mapper.UserMapper;
import org.apache.commons.lang.StringUtils;
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
    //id转userId
    public String getUserIdById(Long id) throws Exception{
        String userId = "";
        if(id != null){
            User user = userMapper.loadUserById(id);
            if (user != null){
                userId = user.getUserId();
            }
        }
        return userId;
    }
    //staff.id转userId
    public String getUserIdByStaffLongId(Long id) throws Exception{
        String userId = "";
        if(id != null){
            User user = userMapper.loadUserByStaffLongId(id);
            if (user != null){
                userId = user.getUserId();
            }
        }
        return userId;
    }

}
