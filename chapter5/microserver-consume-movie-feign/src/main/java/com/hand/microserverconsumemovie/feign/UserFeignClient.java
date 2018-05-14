package com.hand.microserverconsumemovie.feign;

import com.hand.microserverconsumemovie.models.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/5/14
 */
@FeignClient(name = "microserver-provider-user")
public interface UserFeignClient {

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public User queryById(@PathVariable("id") Long id);
}
