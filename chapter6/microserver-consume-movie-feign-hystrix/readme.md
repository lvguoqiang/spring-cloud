# Feign 使用 Hystrix

之前的代码是使用注解 @HystrixCommand 的 fallbackMethod 属性实现回退. 然而 Feign 是以接口的形式工作的, 它没有方法体, 前文讲解的方式显然不适合 Feign.

