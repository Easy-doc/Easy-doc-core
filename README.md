# Easy-doc
简单易用的java接口文档生成，目前仍在开发中，仅提供测试版本

# 引入依赖方法
使用maven引入
```xml
<repositories>
    <repository>
	<id>oss</id>
	<name>oss</name>
	<url>https://oss.sonatype.org/content/groups/public</url>
    </repository>
    <repository>
        <id>aliyun-repository</id>
        <name>aliyun repository</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.stalary</groupId>
    <artifactId>easy-doc</artifactId>
    <version>1.1-SNAPSHOT</version>
</dependency>
```

# 初始化配置
```yml
com:
  stalary:
    easydoc:
      name: swagger demo # 项目名称
      contact: stalary@613.com # 项目联系人
      description: swagger测试项目 # 项目描述
      type: doc # 解析方式(doc|xml)
      path: com.stalary.swagger # 解析的包路径(包括data和controller的包)
```

# 注释书写规则
> 使用javadoc和xml两种模式，可以在application.yml中进行选择

> demo都在test包中
## xml格式
### controller
注释名 | 解释
--- | ---
\<controller>\</controller> | controller名称
\<author>\</author> | controller作者
\<description>\</description> | controller的描述
\<path>\</path> | controller路径
### method
注释名 | 解释
--- | ---
\<method>\</method> | 方法名称
\<path>\</path> | 请求路径
\<type>\</type> | 请求类型(get,post等)
\<description>\</description> | 方法的描述
\<body>\</body> | post方法的请求体
\<param>\</param> | 请求参数
\<return>\</return> | 接口返回描述(如<code0>成功</code0>)
### model
注释名 | 解释
--- | ---
\<model>\</model> | model名称
\<field>\</field> | model的属性
\<description>\</description> | model的描述


## javadoc格式(待完成)
注释名 | 解释
--- | ---
@controller: | 类名
@method: | 方法名   
@description: | 类/方法描述
@author: | 类作者
@return: | 方法返回值，不同code的可以写多个，返回对象使用json格式或者指定对象路径(包名+类名)
@throws: | 抛出的异常
@param: | 参数名
@example: | 传递参数的例子
@cookie: | 设置此注释会从header中读取cookie用于接口测试
@token: | 设置此注释会从header中读取token用于接口测试
