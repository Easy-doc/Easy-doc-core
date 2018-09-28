package com.stalary.easydoc.test;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;

/**
 * <controller>ReadTest1</controller>
 * <path>/test</path>
 * <author>lirongqian</author>
 * <description>测试类1</description>
 */
public class ReadTest1 {

    /**
     * <method>test1</method>
     * <path>/test1</path>
     * <param>
     *     <name>名称</name>
     * </param>
     * <description>测试方法</description>
     */
    public void hello(String name) {
        System.out.println("hello " + name);
    }

    /**
     * <method>user</method>
     * <description>测试对象方法</description>
     * <path>/user</path>
     * <body>
     *     <name>用户名</name>
     *     <age>年龄</age>
     * </body>
     * <return>
     *     <code0>user对象</code0>
     * </return>
     */
    public JSONObject user(User user) {
        return JsonResult.ok(user);
    }
}