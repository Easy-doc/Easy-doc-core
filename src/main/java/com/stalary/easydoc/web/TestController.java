/**
 * @(#)TestController.java, 2018-10-20.
 * <p>
 * Copyright 2018 Youdao, Inc. All rights reserved.
 * YOUDAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.stalary.easydoc.web;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @author lirongqian
 * @since 2018/10/20
 */
@RestController
public class TestController {

    /**
     * @method test 测试方法
     * @return JSONObject json
     */
    @GetMapping("/test")
    public JSONObject test() {
        return JsonResult.ok();
    }
}