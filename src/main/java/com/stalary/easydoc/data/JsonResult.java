/**
 * @(#)JSONResult.java, 2018-09-26.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import java.util.HashMap;
import java.util.Map;

/**
 * JsonResult
 *
 * @author lirongqian
 * @since 2018/09/26
 */
public class JsonResult {

    public static Map<String, Object> ok() {
        return ok(null);
    }

    public static <T> Map<String, Object> ok(T data) {
        Map<String, Object> ret = new HashMap<>();
        ret.put("success", true);
        ret.put("code", 0);
        ret.put("data", data);
        return ret;
    }

    public static Map<String, Object> failed() {
        return failed(null);
    }

    public static Map<String, Object> failed(String msg) {
        Map<String, Object> ret = new HashMap<>();
        ret.put("success", false);
        ret.put("code", -1);
        ret.put("msg", msg);
        return ret;
    }
}