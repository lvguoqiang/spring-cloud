# 改造服务消费者-电影

测试获取用户微服务提供的自定义元数据

## 获取服务提供者 ip 和 port

```
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
```

## 获取服务提供者 instance

```
@Override
public List<ServiceInstance> getInstance(String serverId) {
    return discoveryClient.getInstances(serverId);
}
```

## 获取自定义元数据

```
@Override
public List<String> getMyMetaData(String serverId) {

    List<String> metadatas = new ArrayList<>();
    this.getInstance(serverId).forEach(instance -> {
        metadatas.add(instance.getMetadata().get("my-metadata"));
    });
    return metadatas;
}
```