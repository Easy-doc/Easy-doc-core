/**
 * @(#)ResourceController.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.web;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.config.IpConfiguration;
import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.JsonResult;
import com.stalary.easydoc.test.User;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ResourceController
 *
 * @author lirongqian
 * @since 2018/09/25
 */
@RestController
@RequestMapping("/easy-doc")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private IpConfiguration ipConfiguration;

    private String tokenCache;

    private String cookieCache;

    @GetMapping("/resource")
    public JSONObject getResource() {
        return JsonResult.ok(resourceService.read());
    }

    @GetMapping("/get")
    public JSONObject get(
            @RequestParam String url) throws Exception {
        Response response = Request.Get(transRequest(url))
                .addHeader("token", tokenCache)
                .addHeader("cookie", cookieCache)
                .execute();
        return JsonResult.ok(JSONObject.parseObject(response.returnContent().asString()).get("data"));
    }

    @PostMapping("/post")
    public JSONObject post(
            @RequestParam String url,
            @RequestBody Map<String, String> params) throws Exception {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        Response response = Request.Post(transRequest(url))
                .addHeader("token", tokenCache)
                .addHeader("cookie", cookieCache)
                .bodyForm(nameValuePairs)
                .execute();
        return JsonResult.ok(JSONObject.parseObject(response.returnContent().asString()).get("data"));
    }

    @PostMapping("/addAuth")
    public JSONObject addAuth(
            @RequestBody Map<String, String> params) {
        tokenCache = params.get("token");
        cookieCache = params.get("cookie");
        return JsonResult.ok();
    }

    @PostMapping("/test")
    public JSONObject test(
            HttpServletRequest request,
            User user) {
        String token = request.getHeader("token");
        System.out.println(token);
        return JsonResult.ok(user);
    }

    public String transRequest(String url) {
        return Constant.HTTP + Utils.getHostIp() + Constant.SPLIT + ipConfiguration.getPort() + url;
    }
}