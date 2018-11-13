/**
 * @(#)ResourceService.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.web;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.config.IpConfiguration;
import com.stalary.easydoc.data.*;
import com.stalary.easydoc.core.DocReader;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * ResourceService
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@Service
@Slf4j
public class ResourceService {

    private EasyDocProperties properties;

    @Autowired
    private DocReader docReader;

    @Autowired
    private IpConfiguration ipConfiguration;

    public ResourceService(EasyDocProperties properties) {
        this.properties = properties;
    }

    View read() {
        if (properties.isOpen()) {
            if (properties.isSource()) {
                return docReader.multiReader();
            } else {
                try (InputStream inputStream =
                             ClassUtils.class.getClassLoader().getResourceAsStream("easydoc.txt");
                     BufferedReader bf =
                             new BufferedReader(new InputStreamReader(inputStream,
                                     Charset.forName("UTF-8")))) {
                    String temp;
                    StringBuilder sb = new StringBuilder();
                    while ((temp = bf.readLine()) != null) {
                        sb.append(temp);
                    }
                    return docReader.multiReader(sb.toString());
                } catch (Exception e) {
                    log.warn("multiReader readTxt error", e);
                }
            }
        }
        return new View();
    }

    String transRequest(String url) {
        String result = Constant.HTTP + Utils.getHostIp() + Constant.SPLIT + ipConfiguration.getPort() + url;
        log.info("transRequest: " + result);
        return result;
    }

    JSONObject abTest(int n, int c, String cookie, String url,
                      TestBody body, boolean isGet) {
        File file = new File("");
        try {
            StringBuilder cmdBuilder = new StringBuilder();
            cmdBuilder.append("ab -n ").append(n).append(" -c ").append(c);
            if (StringUtils.isNotEmpty(cookie)) {
                cmdBuilder.append(" -C ").append(cookie);
            }
            if (!isGet) {
                String fileName = Constant.CUR_PATH + "/postFile:" + UUID.randomUUID().toString().substring(0, 5) + ".txt";
                file = new File(fileName);
                @Cleanup FileOutputStream fileOutput = new FileOutputStream(file, false);
                @Cleanup OutputStreamWriter writer = new OutputStreamWriter(fileOutput, Charset.forName("UTF-8"));
                writer.write(JSONObject.toJSONString(body.getBody()));
                writer.flush();
                cmdBuilder.append(" -p ").append(fileName);
                cmdBuilder.append(" -T ").append("application/json");
            }
            cmdBuilder.append(" ").append(transRequest(url));
            cmdBuilder.append("?");
            body.getParams().forEach((k, v) -> cmdBuilder.append(k).append("=").append(v).append("&"));
            // 删除末尾&
            cmdBuilder.deleteCharAt(cmdBuilder.length() - 1);
            log.info("ab test: " + cmdBuilder);
            Process exec = Runtime.getRuntime().exec(cmdBuilder.toString());
            @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
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