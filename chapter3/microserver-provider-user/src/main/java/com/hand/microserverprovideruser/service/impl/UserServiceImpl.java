package com.hand.microserverprovideruser.service.impl;

import com.hand.microserverprovideruser.domain.User;
import com.hand.microserverprovideruser.mapper.UserMapper;
import com.hand.microserverprovideruser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/3/28
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectById(Long id) {
        return userMapper.selectById(id);
    }
}
