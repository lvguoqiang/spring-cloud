package com.hand.microserverconsumemovie.feign;

import com.hand.microserverconsumemovie.models.User;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/5/14
 */
@FeignClient(name = "microserver-provider-user", fallbackFactory = FeignClientFallbackFactory.class)
public interface UserFeignClient {

    /**
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User queryById(@PathVariable("id") Long id);
}

@Component
class FeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    private static Logger logger = LoggerFactory.getLogger(FeignClientFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable throwable) {

        return new UserFeignClient() {
            @Override
            public User queryById(Long id) {
                logger.info("fallback; reason was:" + throwable);
                return User.builder()
                        .id(-1L)
                        .name("默认用户")
                        .build();
            }
        };
    }
}
