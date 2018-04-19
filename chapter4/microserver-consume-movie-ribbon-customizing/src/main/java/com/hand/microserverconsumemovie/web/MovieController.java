package com.hand.microserverconsumemovie.web;

import com.hand.microserverconsumemovie.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/4/18
 */
@RestController
public class MovieController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable Long id) {
        return restTemplate.getForObject("http://microserver-provider-user/user/" + id, User.class);
    }

    @GetMapping("/log-instance")
    public String logUserInstance() {
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("microserver-provider-user");
        LOGGER.info("{}:{}:{}", serviceInstance.getServiceId(), serviceInstance.getHost(), serviceInstance.getPort());
        return serviceInstance.getServiceId().concat(" : ")
                .concat(serviceInstance.getHost().concat(" : ")
                .concat(String.valueOf(serviceInstance.getPort())));
    }
}
