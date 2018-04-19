package com.hand.microserverconsumemovie;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义 Ribbon 配置测试
 *
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/4/19
 */
@Configuration
@RibbonClient(name = "microserver-provider-user", configuration = RibbonConfiguration.class)
public class TestRibbon {
}
