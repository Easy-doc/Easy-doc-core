package com.stalary.easydoc;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;

/**
 * <controller>ReadTest2</controller>
 * <author>lirongqian</author>
 * <description>测试类2</description>
 */
public class ReadTest2 {

    /**
     * <method>test2</method>
     * <description>测试方法</description>
     * <path>/test2</path>
     * <return>
     *     <code0>成功</code0>
     * </return>
     */
    public JSONObject test() {
        System.out.println("test");
        return JsonResult.ok();
    }
}