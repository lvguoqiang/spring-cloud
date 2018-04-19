package com.hand.microserverconsumemovie.service.impl;

import com.hand.microserverconsumemovie.service.MovieService;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guoqiang.lv@hand-china.com
 * @version 1.0
 * @date 2018/4/19
 */
@Service
public class MovieServiceImpl implements MovieService {

    @Value("${user.url}")
    private String url;

    @Autowired
    DiscoveryClient discoveryClient;

    @Override
    public String getHostAndIp(String serverId) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serverId);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            return url;
        }
        EurekaDiscoveryClient.EurekaServiceInstance eurekaServiceInstance = (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstances.get(0);
        InstanceInfo instanceInfo = eurekaServiceInstance.getInstanceInfo();
        return String.valueOf((instanceInfo.getIPAddr().concat(":")
                .concat(String.valueOf(instanceInfo.getPort()))));
    }

    @Override
    public List<ServiceInstance> getInstance(String serverId) {
        return discoveryClient.getInstances(serverId);
    }

    @Override
    public List<String> getMyMetaData(String serverId) {

        List<String> metadatas = new ArrayList<>();
        this.getInstance(serverId).forEach(instance -> {
            metadatas.add(instance.getMetadata().get("my-metadata"));
        });
        return metadatas;
    }
}
