/**
 * @(#)ResourceController.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.web;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.JsonResult;
import com.stalary.easydoc.data.TestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ResourceController
 * 资源获取controller
 * @author lirongqian
 * @since 2018/09/25
 */
@RestController
@RequestMapping(value = "/easy-doc")
@Slf4j
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", origins = "*")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/resource")
    public JSONObject getResource() {
        return JsonResult.ok(resourceService.read());
    }

    @GetMapping("/list")
    public JSONObject list() {
        return JsonResult.ok(Constant.URL_LIST);
    }

    /**
     * pressureTest 压力测试
     * @param n      请求 数量 2 1 a d
     * @param c      并发 数量 b s
     * @param cookie cookie
     * @param isGet  是否为get，默认true
     * @param url    请求地址
     * @param body   参数
     * @return TestResponse 时间统计对象
     */
    @PostMapping("/pressureTest")
    public JSONObject pressureTest(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "1") int n,
            @RequestParam(required = false, defaultValue = "1") Integer c,
            @RequestParam(required = false, defaultValue = "") String cookie,
            @RequestParam(required = false, defaultValue = "true") boolean isGet,
            @RequestBody TestBody body) {
        return resourceService.abTest(n, c, cookie, url, body, isGet);
    }

}