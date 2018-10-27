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
import com.stalary.easydoc.test.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * ResourceController
 *
 * @author lirongqian
 * @description 资源获取controller
 * @since 2018/09/25
 */
@RestController
@RequestMapping(value = "/easy-doc")
@Slf4j
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", origins = "*")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    private String tokenCache;

    private String cookieCache;

    @GetMapping("/resource")
    public JSONObject getResource() {
        return JsonResult.ok(resourceService.read());
    }

    @GetMapping("/list")
    public JSONObject list() {
        return JsonResult.ok(Constant.URL_LIST);
    }

    /**
     * @param name 名称
     * @param age  年龄
     * @return 0 成功
     * @return 字符串 名字+年龄
     * @return -1 失败
     * @method testParam 测试get含参方法
     */
    // todo 解析多个return
    // todo 对象嵌套
    @GetMapping("/testParam")
    public JSONObject testParam(
            @RequestParam(required = false, defaultValue = "stalary") String name,
            @RequestParam(required = false, defaultValue = "22") int age) {
        return JsonResult.ok("name: " + name + " age: " + age);
    }

    @PostMapping("/addCookie")
    public JSONObject addCookie(
            @RequestBody Map<String, String> params,
            HttpServletResponse response) {
        params.forEach((k, v) -> {
            Cookie cookie = new Cookie(k, v);
            response.addCookie(cookie);
        });
        return JsonResult.ok();
    }

    /**
     * @param params cookie|token(可以两者都传)
     * @return 0 JSONObject
     * @method addAuth 添加auth(cookie|token)
     */
    @PostMapping("/addAuth")
    public JSONObject addAuth(
            @RequestBody Map<String, String> params) {
        tokenCache = params.get("token");
        cookieCache = params.get("cookie");
        return JsonResult.ok();
    }

    /**
     * @param request HttpServletRequest
     * @param user    用户对象
     * @return User 用户对象
     * @method token 测试post方法
     */
    @PostMapping("/token")
    public JSONObject token(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody User user) {
        String cookie = request.getHeader("Set-Cookie");
        String token = request.getHeader("Authorization");
        Map<String, String> map = new HashMap<>();
        map.put("cookie", cookie);
        map.put("token", token);
        response.setHeader("Cookie", cookie);
        return JsonResult.ok(map);
    }

    /**
     * @param n      请求数量
     * @param c      并发数量
     * @param cookie cookie
     * @param isGet  是否为get，默认true
     * @param url    请求地址
     * @param body   参数
     * @return TestResponse 时间统计对象
     * @method pressureTest 压力测试
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