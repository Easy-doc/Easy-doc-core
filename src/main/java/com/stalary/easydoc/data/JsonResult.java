/**
 * @(#)JSONResult.java, 2018-09-26.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import com.alibaba.fastjson.JSONObject;

/**
 * JsonResult
 *
 * @author lirongqian
 * @since 2018/09/26
 */
public class JsonResult {

    public static JSONObject ok() {
        return ok(null);
    }

    public static <T> JSONObject ok(T data) {
        return new JSONObject()
                .fluentPut("success", true)
                .fluentPut("code", 0)
                .fluentPut("data", data);
    }
}