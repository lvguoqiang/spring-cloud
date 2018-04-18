package com.hand.microserversimpleprovideruser.service.impl;

import com.hand.microserversimpleprovideruser.domain.User;
import com.hand.microserversimpleprovideruser.mapper.UserMapper;
import com.hand.microserversimpleprovideruser.service.UserService;
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
