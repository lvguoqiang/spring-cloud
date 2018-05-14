package com.hand.microserverconsumemovie.web;

import com.hand.microserverconsumemovie.feign.UserFeignClient;
import com.hand.microserverconsumemovie.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/4/18
 */
@RestController
public class MovieController {

    @Autowired
    UserFeignClient userFeignClient;

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable Long id) {
        return userFeignClient.queryById(id);
    }
}
