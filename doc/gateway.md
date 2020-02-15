# 网关配置
```yaml
com:
  stalary:
    easydoc:
      description: 网关项目 # 网关描述
      name: gateway # 网关名称
      contact: lirq # 网关负责人
      gateway: true # 网关模式文档
      gateway-config: # 网关配置
        service-list[0]: # 服务配置
          name: service1 # 子服务名称
          url: http://service1 # 子服务地址
          auth: true # 是否有权限验证,默认为false
        service-list[1]:
          name: service2
          url: http://service2
```