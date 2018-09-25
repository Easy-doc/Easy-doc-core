/**
 * @(#)Reader.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.stalary.easydoc.data.Controller;
import com.stalary.easydoc.data.Method;
import com.stalary.easydoc.data.View;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ReaderImpl
 *
 * @author lirongqian
 * @since 2018/09/25
 */
@Component
public class ReaderImpl {

    @SuppressWarnings("unchecked")
    public View read() throws Exception {
        View view = new View();
        Controller controller = new Controller();
        // 获取当前路径
        String curPath = System.getProperty("user.dir");
        FileReader fileReader = new FileReader(curPath + "/src/test/java/com/stalary/easydoc/ReadTest.java");
        BufferedReader reader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String s = reader.readLine();
        while (s != null) {
            sb.append(s);
            s = reader.readLine();
        }
        // 匹配出注释代码块
        String regex = "\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sb);
        while (matcher.find()) {
            Map<String, String> map = new HashMap<>();
            String temp = matcher.group().replace("/*", "<doc>").replace("*/", "</doc>").replace("*", "");
            Document document = DocumentHelper.parseText(temp);
            Element root = document.getRootElement();
            // 遍历xml
            for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
                Element element = it.next();
                map.put(element.getName(), element.getText());
            }
            // 首先先存储controller
            if (map.containsKey("controller")) {
                controller = controller.toBuilder()
                        .author(map.getOrDefault("author", ""))
                        .description(map.getOrDefault("description", ""))
                        .name(map.getOrDefault("name", ""))
                        .build();
                view.getControllerList().add(controller);
            } else if (map.containsKey("method")) {
                // 其次遍历存储method
                Method method = new Method().toBuilder()
                        .description(map.getOrDefault("description", ""))
                        .name(map.getOrDefault("name", ""))
                        .path(map.getOrDefault("path", ""))
                        .body(JSONObject.parseObject(map.getOrDefault("body", null)))
                        .paramMap(JSONObject.parseObject(map.getOrDefault("paramMap", null), new TypeReference<Map<String, String>>() {
                        }))
                        .returnMap(JSONObject.parseObject(map.getOrDefault("paramMap", null), new TypeReference<Map<Integer, String>>() {
                        }))
                        .build();
                controller.getMethodList().add(method);
            }
        }
        return view;
    }
}