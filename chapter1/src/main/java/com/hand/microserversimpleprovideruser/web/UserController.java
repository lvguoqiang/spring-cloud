package com.hand.microserversimpleprovideruser.web;

import com.hand.microserversimpleprovideruser.domain.User;
import com.hand.microserversimpleprovideruser.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/3/28
 */
@RestController
@Api(description = "服务提供者api")
public class UserController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "获取模板基本信息", notes = "根据id，获取模板基本信息,包含文本信息")
    @GetMapping("/user/{id}")
    public User findById(@PathVariable Long id){
        return userService.selectById(id);
    }
}
