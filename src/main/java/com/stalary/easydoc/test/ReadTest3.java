package com.stalary.easydoc.test;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <controller>ReadTest3</controller>
 * <author>lirongqian</author>
 * <description>测试类</description>
 */
@RestController
@RequestMapping("/readTest3")
public class ReadTest3 {

    /**
     * <method>test3</method>
     * <description>测试方法</description>
     * <path>/test3</path>
     * <param>
     *     <name>姓名</name>
     * </param>
     * <return>
     *     <code0>hello name</code0>
     * </return>
     */
    public JSONObject test(String name) {
        return JsonResult.ok("hello " + name);
    }
}