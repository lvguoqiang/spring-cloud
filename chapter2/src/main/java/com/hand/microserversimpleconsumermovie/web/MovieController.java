package com.hand.microserversimpleconsumermovie.web;

import com.hand.microserversimpleconsumermovie.models.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable Long id) {
        return restTemplate.getForObject("http://localhost:8035/user/" + id, User.class);
    }
}
