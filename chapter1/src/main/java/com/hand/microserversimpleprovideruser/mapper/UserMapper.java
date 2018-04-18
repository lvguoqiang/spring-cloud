package com.hand.microserversimpleprovideruser.mapper;

import com.hand.microserversimpleprovideruser.domain.User;

/**
 *
 * 用户对数据库操作的mapper
 *
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/3/28
 */
public interface UserMapper {
    /**
     * 根据主键查询
     * @param id 主键
     * @return 对应的实体类
     */
    User selectById(Long id);
}
