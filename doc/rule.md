
注释名 | 解释
--- | ---
@method | 方法名   
@description | 类/实体描述
@author | 类作者
@throws | 抛出的异常
@return | 方法返回值
@param | 参数名
@model | 对象标识
@field | 对象的参数

## controller
/**
 * TestController // 接口类名称
 * @description 测试接口 // 接口类描述
 * @author lirongqian // 作者名称
 */
 
 ## method
 /**
 * @method getMethod 获取数据接口 // 方法名和解释
 * @param testId 测试id // 字段名和解释
 * @return TestModel 测试返回对象 // 返回值和返回值名称(默认为code=0)
 * @return -1 失败 // 返回的code值和对应的解释
 **/
 
 ## model
 /**
 * @model TestModel // 对象名
 * @description 测试对象 // 对象描述
 * @field testId 测试id // 对象字段和描述
 **/
 

使用 Idea 可以设置 Live template，具体设置如下
> Preferences -> Editor -> Live Templates -> 创建 Template group -> 创建 LiveTemplate -> 设置自己想要的快捷键，在下方 define 勾选 java

method模版设置：
```
**
 * @method $name$
$param$
 * @return
 **/
```
然后点击 Edit Variables 设置上述参数, name 为 methodName()

param为groovy脚本：
> groovyScript("if(\"${_1}\".length() == 2) {return '';} else {def result=''; def params=\"${_1}\".replaceAll('[\\\\[|\\\\]|\\\\s]', '').split(',').toList();for(i = 0; i < params.size(); i++) {if(i<(params.size()-1)){result+=' * @param ' + params[i] + ' ' + '\\n'}else{result+=' * @param ' + params[i] + ' '}}; return result;}", methodParameters());

model模版设置:
```
**
 * @model $name$
 * @description
 **/
```

类注释从File and Code Templates设置
```
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
#parse("Class Header.java")
public class ${NAME} {
}
```