# 改造服务提供者-用户

将用户微服务改造成提供自定义元数据的项目

## 定义元数据

只需要将元数据定义到 `eureka.instance.metadata-map` 下即可

```
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    metadata-map:
      my-metadata:  我自己定义的元数据
```