# 设计思路
- 基于反射和正则表达式实现
- 文件读取扫描类为`DocReader`
- 接口数据渲染类为`DocRender`
- 所有反射相关的操作都位于`ReflectUtils`
- 服务器部署时(配置source=false),通过 jar 包生成接口文档需要使用插件先将本地文件进行扫描
  -  [easydoc-maven-plugin](https://github.com/Easy-doc/easydoc-maven-plugin)：mvn easydoc
  -  [easydoc-gradle-plugin](https://github.com/Easy-doc/easydoc-gradle-plugin)：gradle easydoc
