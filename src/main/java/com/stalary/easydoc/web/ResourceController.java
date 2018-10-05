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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ResourceController
 * @description 资源获取controller
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

    @GetMapping(value = "/resource")
    public JSONObject getResource() {
        return JsonResult.ok(resourceService.read());
    }

    /**
     * @method get get方法测试
     * @param url 测试的url，包括参数
     * @return data 返回值
     * @throws IOException 执行http请求时的异常
     */
    @GetMapping("/get")
    public JSONObject get(
            @RequestParam String url) throws IOException {
        Response response = Request.Get(transRequest(url))
                .addHeader("token", tokenCache)
                .addHeader("cookie", cookieCache)
                .execute();
        return JsonResult.ok(JSONObject.parseObject(response.returnContent().asString()).get("data"));
    }

    /**
     * @method post post方法测试
     * @param url 测试的url，包括参数
     * @param params body中参数
     * @return data 返回值
     * @throws IOException 执行http请求时的异常
     */
    @PostMapping("/post")
    public JSONObject post(
            @RequestParam String url,
            @RequestBody Map<String, String> params) throws IOException {
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

    /**
     * @method addAuth 添加auth(cookie|token)
     * @param params cookie|token(可以两者都传)
     * @return JSONObject
     */
    @PostMapping("/addAuth")
    public JSONObject addAuth(
            @RequestBody Map<String, String> params) {
        tokenCache = params.get("token");
        cookieCache = params.get("cookie");
        return JsonResult.ok();
    }

    /**
     * @method token 测试post方法
     * @param request HttpServletRequest
     * @return User 用户对象
     */
    @PostMapping("/token")
    public JSONObject token(
            HttpServletRequest request,
            @RequestBody User user) {
        String token = request.getHeader("token");
        System.out.println(request.getHeader("cookie"));
        return JsonResult.ok(user);
    }

    /**
     * @method postTest post请求压力测试
     * @param n      请求数量
     * @param c      并发数量
     * @param cookie    cookie
     * @param url    请求地址
     * @param params 参数
     * @return TestResponse 时间统计对象
     */
    @PostMapping("/postTest")
    public JSONObject postTest(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "1") int n,
            @RequestParam(required = false, defaultValue = "1") int c,
            @RequestParam(required = false, defaultValue = "") String cookie,
            @RequestBody Map<String, String> params) {
        return abTest(n, c, cookie, url, params);
    }

    /**
     * @method getTest get请求压力测试
     * @param url   请求地址
     * @param n     请求数量
     * @param c     并发数量
     * @param cookie    cookie
     * @return TestResponse 时间统计对象
     */
    @GetMapping("/getTest")
    public JSONObject getTest(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "1") int n,
            @RequestParam(required = false, defaultValue = "1") int c,
            @RequestParam(required = false, defaultValue = "") String cookie) {
        return abTest(n, c, cookie, url, null);
    }


    private String transRequest(String url) {
        return Constant.HTTP + Utils.getHostIp() + Constant.SPLIT + ipConfiguration.getPort() + url;
    }

    private JSONObject abTest(int n, int c, String cookie, String url, Map<String, String> params) {
        File file = new File("");
        try {
            StringBuilder cmdBuilder = new StringBuilder();
            cmdBuilder.append("ab -n ").append(n).append(" -c ").append(c);
            if (StringUtils.isNotEmpty(cookie)) {
                cmdBuilder.append(" -C ").append(cookie);
            }
            if (params != null) {
                String fileName = Constant.CUR_PATH + "/postFile:" + UUID.randomUUID().toString().substring(0, 5) + ".txt";
                file = new File(fileName);
                FileOutputStream fileOutput = new FileOutputStream(file, false);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutput, Charset.forName("UTF-8"));
                writer.write(JSONObject.toJSONString(params));
                writer.flush();
                cmdBuilder.append(" -p ").append(fileName);
                cmdBuilder.append(" -T ").append("application/json");
            }
            cmdBuilder.append(" ").append(transRequest(url));
            log.info("ab test: " + cmdBuilder);
            Process exec = Runtime.getRuntime().exec(cmdBuilder.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
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
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
        return JsonResult.ok();
    }
}