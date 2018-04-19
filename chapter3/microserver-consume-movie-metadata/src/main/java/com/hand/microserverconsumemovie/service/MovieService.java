package com.hand.microserverconsumemovie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/4/19
 */
public interface MovieService {

    /**
     * 获取服务提供者的ip和端口
     *
     * @param serverId
     * @return
     */
    String getHostAndIp(String serverId);

    /**
     * 查询服务提供者信息
     * @param serverId
     * @return
     */
    List<ServiceInstance> getInstance(String serverId);

    /**
     * 获取自定义元数据
     * @param serverId
     * @return
     */
    List<String> getMyMetaData(String serverId);
}
