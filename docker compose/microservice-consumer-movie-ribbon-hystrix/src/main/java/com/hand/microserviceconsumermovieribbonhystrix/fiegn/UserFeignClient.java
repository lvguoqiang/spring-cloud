package com.hand.microserviceconsumermovieribbonhystrix.fiegn;

import com.hand.microserviceconsumermovieribbonhystrix.models.User;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/5/16
 */
@FeignClient(name = "microservice-provider-user", fallbackFactory = FeignClientFallbackFactory.class)
public interface UserFeignClient {

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    User queryById(@PathVariable(value = "id") Long id);
}

@Component
class FeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    private static Logger logger = LoggerFactory.getLogger(FeignClientFallbackFactory.class);
    @Override
    public UserFeignClient create(Throwable throwable) {
        return ((item) -> {
            logger.info("fallback; reason was:" + throwable);
            return User.builder().id(-1L).name("默认用户").build();
        });
    }

}
