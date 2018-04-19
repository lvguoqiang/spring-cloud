package com.hand.microserverconsumemovie;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ribbon配置类
 *
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/4/19
 */
@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        // 负载均衡规则为随机
        return new RandomRule();
    }
}
