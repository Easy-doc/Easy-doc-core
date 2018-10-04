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
import com.stalary.easydoc.data.TestResponse;
import com.stalary.easydoc.test.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
@RequestMapping(value = "/easy-doc")
@Slf4j
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private IpConfiguration ipConfiguration;

    private String tokenCache;

    private String cookieCache;

    @GetMapping(value = "/resource", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @PostMapping("/token")
    public JSONObject token(
            HttpServletRequest request,
            User user) {
        String token = request.getHeader("token");
        System.out.println(token);
        return JsonResult.ok(user);
    }

    /**
     * post请求测试
     *
     * @param n      请求数量
     * @param c      并发数量
     * @param C      cookie
     * @param url    请求地址
     * @param params 参数
     * @return
     */
    @PostMapping("/postTest")
    public JSONObject postTest(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "1") int n,
            @RequestParam(required = false, defaultValue = "1") int c,
            @RequestParam(required = false, defaultValue = "") String C,
            @RequestBody Map<String, String> params) {

        return JsonResult.ok();
    }

    @GetMapping("/getTest")
    public JSONObject getTest(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "1") int n,
            @RequestParam(required = false, defaultValue = "1") int c,
            @RequestParam(required = false, defaultValue = "") String cookie) {
        return abTest(n, c, cookie, url);
    }


    public String transRequest(String url) {
        return Constant.HTTP + Utils.getHostIp() + Constant.SPLIT + ipConfiguration.getPort() + url;
    }

    private JSONObject abTest(int n, int c, String cookie, String url) {
        try {
            StringBuilder cmdBuilder = new StringBuilder();
            cmdBuilder.append("ab -n ").append(n).append(" -c").append(c);
            if (StringUtils.isNotEmpty(cookie)) {
                cmdBuilder.append(" -C ").append(cookie);
            }
            cmdBuilder.append(" ").append(transRequest(url));
            Process exec = Runtime.getRuntime().exec(cmdBuilder.toString());
            BufferedInputStream buffer = new BufferedInputStream(exec.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(buffer));
            TestResponse response = new TestResponse();
            String str = reader.readLine();
            while (str != null) {
                str = str.replaceAll(" +", " ");
                if (str.contains("Requests per second")) {
                    String temp = str.split(":")[1];
                    response.setQps(Double.valueOf(temp.split(" ")[1]));
                } else if (str.contains("longest request")) {
                    response.setSlowTime(Integer.valueOf(str.split(" ")[2]));
                } else if (str.contains("50%")) {
                    response.setFastTime(Integer.valueOf(str.split(" ")[2]));
                } else if (str.contains("Time per request") && str.contains("(mean)")) {
                    String temp = str.split(":")[1];
                    response.setAvgTime(Double.valueOf(temp.split(" ")[1]));
                }
                str = reader.readLine();
            }
            return JsonResult.ok(response);
        } catch (Exception e) {
            log.warn("cmd error!", e);
        }
        return JsonResult.ok();
    }
}