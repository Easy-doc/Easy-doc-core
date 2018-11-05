/**
 * @(#)DocReader.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public DocReader(EasyDocProperties properties) {
        super(properties);
    }

    @Override
    public void singleReader(String str) {
        String[] split = str.split(Constant.MATCH_SPLIT);
        String name = split[0];
        Controller controller = new Controller();
        Model model = new Model();
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                handle(controller, model, split[i], name);
            }
        }
    }

    @Override
    public void singleReader(File file) {
        try {
            // 获取文件名称
            String fileName = file.getName();
            String name = fileName.substring(0, fileName.indexOf("."));
            Controller controller = new Controller();
            Model model = new Model();
            String str = readFile(file);
            // 匹配出注释代码块
            String regex = "\\/\\*(\\s|.)*?\\*\\/";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                // 1. 去除所有单行注释
                // 2. 匹配块级注释
                // 3. 合并多个空格
                String temp = matcher
                        .group()
                        .replaceAll("\\/\\*\\*", "")
                        .replaceAll("\\*\\/", "")
                        .replaceAll("\\*", "")
                        .replaceAll(" +", " ");
                handle(controller, model, temp, name);
            }
        } catch (Exception e) {
            log.warn("singleReader error!", e);
        }
    }

    private void handle(Controller controller, Model model, String temp, String name) {
        Map<String, String> map = new HashMap<>();
        List<Param> paramList = new ArrayList<>();
        List<Param> fieldList = new ArrayList<>();
        List<Response> responseList = new ArrayList<>();
        Map<String, String> throwsMap = new HashMap<>();
        String[] split = temp.split(" ");
        String cur = "";
        if (split.length <= 1) {
            return;
        }
        if (name.equals(split[1]) && reflectUtils.isController(name)) {
            map.put(Constant.CONTROLLER, split[1]);
        }
        if (Constant.MODEL_TAG.equals(split[1]) && name.equals(split[2])) {
            map.put(Constant.MODEL, split[2]);
        }
        for (int i = 1; i < split.length; i++) {
            String t = split[i];
            // 匹配到Controller
            if (t.contains("@")) {
                cur = t.substring(1);
            } else {
                if (StringUtils.isNotEmpty(cur)) {
                    switch (cur) {
                        case Constant.PARAM:
                            if (i + 1 < split.length) {
                                paramList.add(new Param(t, split[i + 1]));
                                i = i + 1;
                            }
                            break;
                        case Constant.RETURN:
                            if (i + 1 < split.length) {
                                if (StringUtils.isNumeric(t) || StringUtils.isNumeric(t.substring(1))) {
                                    responseList.add(new Response(Integer.valueOf(t), split[i + 1]));
                                } else {
                                    int size = responseList.size();
                                    if (size == 0) {
                                        Map<String, String> result = new HashMap<>();
                                        result.put(t, split[i + 1]);
                                        responseList.add(new Response(0, "成功", result));
                                    } else {
                                        responseList.get(responseList.size() - 1).getFieldList().add(new Field(t, split[i + 1]));
                                    }
                                }
                                i = i + 1;
                            }
                            break;
                        case Constant.FIELD:
                            if (i + 1 < split.length) {
                                fieldList.add(new Param(t, split[i + 1]));
                                i = i + 1;
                            }
                            break;
                        case Constant.METHOD:
                            if (i + 1 < split.length) {
                                map.put(t, split[i + 1]);
                                i = i + 1;
                            }
                            map.put(Constant.METHOD, t);
                            break;
                        case Constant.THROWS:
                            if (i + 1 < split.length) {
                                throwsMap.put(t, split[i + 1]);
                                i = i + 1;
                            }
                            break;
                        default:
                            map.put(cur, t);
                            break;
                    }
                }
            }
        }
        render(controller, map, paramList, fieldList, responseList, throwsMap, view, model);
    }

}