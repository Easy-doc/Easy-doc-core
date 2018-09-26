package com.stalary.easydoc;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;

/**
 * <controller>ReadTest4</controller>
 * <author>lirongqian</author>
 * <description>测试类</description>
 */
public class ReadTest4 {

    /**
     * <method>test4</method>
     * <description>测试方法</description>
     * <path>/test4</path>
     * <body>
     *     <name>用户名</name>
     *     <age>年龄</age>
     * </body>
     * <return>
     *     <code0>user对象</code0>
     * </return>
     */
    public JSONObject test(User user) {
        return JsonResult.ok(user);
    }
}