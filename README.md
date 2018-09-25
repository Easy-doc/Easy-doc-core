# Easy-doc
简单易用的java接口文档生成

# 注释书写规则
注释名 | 解释
--- | ---
@controller: | 类名
@method: | 方法名   
@description: | 类/方法描述
@author: | 类作者
@return: | 方法返回值，不同code的可以写多个，返回对象使用json格式或者指定对象路径(包名+类名)
@throws: | 抛出的异常
@param: | 参数名
@required: | 必填的参数名
@body: | post方法传递的body 
@example: | 传递参数的例子
@cookie: | 设置此注释会从header中读取cookie用于接口测试
@token: | 设置此注释会从header中读取token用于接口测试
