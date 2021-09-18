package com.gemini.toolkit.login.service.impl;

import com.gemini.toolkit.login.entity.TUserEntity;
import com.gemini.toolkit.login.mapper.TUserMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gemini.toolkit.login.service.TUserService;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUserEntity> implements TUserService {

}
