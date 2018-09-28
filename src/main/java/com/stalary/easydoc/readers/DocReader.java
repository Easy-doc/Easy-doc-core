/**
 * @(#)DocReader.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.Controller;
import com.stalary.easydoc.data.Model;
import com.stalary.easydoc.data.View;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DocReader
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@Slf4j
@Component
public class DocReader extends BaseReader {

    private EasyDocProperties properties;

    public DocReader(EasyDocProperties properties) {
        super(properties);
        this.properties = properties;
    }

    @Autowired
    ReflectReader reflectReader;

    @Override
    public View singleReader(File file) {
        try {
            boolean isController = false;
            boolean isModel = false;
            // 获取文件名称
            String fileName = file.getName();
            String name = fileName.substring(0, fileName.indexOf("."));
            reflectReader.isController(name);
            View view = new View();
            Controller controller = new Controller();
            Model model = new Model();
            String str = readFile(file);
            // 包括以下两个注解的为Controller
            if (str.contains("@Controller") || str.contains("@RestController")) {
                isController = true;
            }
            if (str.contains("@Model")) {
                isModel = true;
            }
            // 匹配出注释代码块
            String regex = "(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                Map<String, String> map = new HashMap<>();
                Map<String, String> paramMap = new HashMap<>();
                Map<String, String> fieldMap = new HashMap<>();
                Map<String, String> returnMap = new HashMap<>();
                Map<String, String> bodyMap = new HashMap<>();
                // 1. 去除所有单行注释
                // 2. 匹配块级注释
                // 3. 合并多个空格
                String temp = matcher
                        .group()
                        .replaceAll("\\/\\/[^\n]*", "")
                        .replaceAll("\\/\\*\\*", "")
                        .replaceAll("\\*\\/", "")
                        .replaceAll("\\*", "")
                        .replaceAll(" +", " ");
                String[] split = temp.split(" ");
                String cur = "";
                if (split.length <= 1) {
                    return new View();
                }
                if (name.equals(split[1]) && isController) {
                    map.put(Constant.CONTROLLER, split[1]);
                }
                if (name.equals(split[1]) && isModel) {
                    map.put(Constant.MODEL, split[1]);
                }
                for (int i = 1; i < split.length; i++) {
                    String t = split[i];
                    // 匹配到Controller
                    if (t.contains("@")) {
                        cur = t.substring(1);
                    } else {
                        if (StringUtils.isNotEmpty(cur)) {
                            if (Constant.PARAM.equals(cur)) {
                                if (i + 1 < split.length) {
                                    paramMap.put(t, split[i + 1]);
                                    i = i + 1;
                                }
                            } else if (Constant.RETURN.equals(cur)) {
                                if (i + 1 < split.length) {
                                    returnMap.put(t, split[i + 1]);
                                    i = i + 1;
                                }
                            } else if (Constant.FIELD.equals(cur)) {
                                if (i + 1 < split.length) {
                                    fieldMap.put(t, split[i + 1]);
                                    i = i + 1;
                                }
                            } else {
                                map.put(cur, t);
                            }
                        }
                    }
                }
                render(controller, map, paramMap, fieldMap, returnMap, bodyMap, view, model);
            }
            return view;
        } catch (Exception e) {
            log.warn("singleReader error!", e);
        }
        return new View();
    }

}