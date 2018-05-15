package com.hand.microserviceprovideruser.service;

import com.hand.microserviceprovideruser.domain.User;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/3/28
 */
public interface UserService {

    /**
     * 根据主键查询
     * @param id 主键
     * @return 对应的实体类
     */
    User selectById(Long id);
}
