package com.hand.microserverconsumemovie.web;

import com.hand.microserverconsumemovie.models.User;
import com.hand.microserverconsumemovie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/4/18
 */
@RestController
public class MovieController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MovieService movieService;

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable Long id) {

        String host = movieService.getHostAndIp("microserver-provider-user");
        String url = "http://".concat(host).concat("/user/");

        return restTemplate.getForObject(url + id, User.class);
    }

    @GetMapping("/user/instance-info")
    public List<ServiceInstance> showInstanceInfo() {

        return movieService.getInstance("microserver-provider-user");
    }

    @GetMapping("/user/my-metadata")
    public List<String> getMyMetaData() {
        return movieService.getMyMetaData("microserver-provider-user");
    }
}
