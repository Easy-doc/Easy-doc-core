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
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    ExecutorService exec;
    @GetMapping("/getTest")
    public JSONObject getTest(
            @RequestParam String url,
            @RequestParam(required = false, defaultValue = "1") int n,
            @RequestParam(required = false, defaultValue = "1") int c,
            @RequestParam(required = false, defaultValue = "") String C) throws InterruptedException {
        // 替换为ab
        CountDownLatch cd = new CountDownLatch(n);
        long totalTimeStart = System.currentTimeMillis();
        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger max = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger sum = new AtomicInteger(0);
        exec = Executors.newFixedThreadPool(c);
        for (int i = 0; i < n; i++) {
            exec.execute(() -> {
                try {
                    long start = System.currentTimeMillis();
                    Request.Get(transRequest(url))
                            .addHeader("cookie", C)
                            .execute();
                    int temp = (int) (System.currentTimeMillis() - start);
                    // 计算请求耗时最长和最大
                    min.set(Math.min(temp, min.get()));
                    max.set(Math.max(temp, max.get()));
                    sum.addAndGet(temp);
                } catch (Exception e) {
                    log.warn("getTest error", e);
                } finally {
                    cd.countDown();
                }
            });
        }
        cd.await();
        // 计算总耗时
        int totalTime = (int) (System.currentTimeMillis() - totalTimeStart);
        int avgTime = sum.get() / n;
        int qps = 1000 * n / totalTime;
        exec.shutdownNow();
        return JsonResult.ok(new TestResponse(max.get(), min.get(), totalTime, avgTime, qps));
    }


    public String transRequest(String url) {
        return Constant.HTTP + Utils.getHostIp() + Constant.SPLIT + ipConfiguration.getPort() + url;
    }
}