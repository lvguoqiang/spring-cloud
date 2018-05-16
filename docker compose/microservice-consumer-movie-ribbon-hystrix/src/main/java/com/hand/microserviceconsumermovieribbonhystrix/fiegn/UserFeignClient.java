package com.hand.microserviceconsumermovieribbonhystrix.fiegn;

import com.hand.microserviceconsumermovieribbonhystrix.models.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/5/16
 */
@FeignClient(name = "microservice-provider-user")
public interface UserFeignClient {

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    User queryById(@PathVariable(value = "id") Long id);
}

