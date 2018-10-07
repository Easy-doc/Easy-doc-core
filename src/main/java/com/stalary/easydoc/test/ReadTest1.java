package com.stalary.easydoc.test;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <controller>ReadTest1</controller>
 * <author>lirongqian</author>
 * <description>测试类1</description>
 */
@RestController
@RequestMapping("/readTest1")
public class ReadTest1 {

    /**
     * <method>hello</method>
     * <param>
     *     <name>名称</name>
     * </param>
     * <description>测试方法</description>
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public void hello(String name) {
        System.out.println("hello " + name);
    }

    /**
     * <method>user</method>
     * <description>测试对象方法</description>
     * <return>
     *     <code0>user对象</code0>
     * </return>
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public JSONObject user(
            @RequestBody User user) {
        return JsonResult.ok(user);
    }
}