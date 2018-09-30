package com.stalary.easydoc.test;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <controller>ReadTest2</controller>
 * <author>lirongqian</author>
 * <description>测试类2</description>
 */
@RestController
@RequestMapping("/readTest2")
public class ReadTest2 {

    /**
     * <method>test</method>
     * <description>测试方法</description>
     * <return>
     *     <code0>成功</code0>
     * </return>
     */
    public JSONObject test() {
        System.out.println("test");
        return JsonResult.ok();
    }
}