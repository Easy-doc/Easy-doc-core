# 服务配置
```yaml
com:
  stalary:
    easydoc:
      name: easydoc demo # 项目名称
      contact: stalary@613.com # 项目联系人
      description: easydoc测试项目 # 项目描述
      path: com.stalary.easydoc # 解析的包路径(包括data和controller的包)
      open: true # 是否开启文档功能
      source: true # 是否读取源码,false则为读取resources中的easydoc.txt
      auth: true # 是否开启登录验证，默认为false
      auth-config:
        - account: stalary # 账号
        - password: 123456 # 密码
      include-file: # 指定路径下包含的文件，如果source设置为false，需要从插件中配置
        - a
        - b
        - c
      exclude-file: # 指定路径下排除的文件，如果source设置为false，需要从插件中配置
        - a
        - b
```