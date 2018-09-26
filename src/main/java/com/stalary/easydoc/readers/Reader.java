/**
 * @(#)Reader.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.stalary.easydoc.data.*;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ReaderImpl
 *
 * @author lirongqian
 * @since 2018/09/25
 */
@Component
@Slf4j
public class Reader {

    /** 获取当前路径 **/
    private final String curPath = System.getProperty("user.dir");

    /**
     * 单文件读取
     *
     * @param file 单文件路径
     * @return 返回view，前端进行渲染
     */
    @SuppressWarnings("unchecked")
    private View singleReader(File file) {
        try {
            View view = new View();
            Controller controller = new Controller();
            Model model = new Model();
            FileReader fileReader = new FileReader(file);
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
                Map<String, String> paramMap = new HashMap<>();
                Map<String, String> fieldMap = new HashMap<>();
                Map<Integer, String> returnMap = new HashMap<>();
                Map<String, String> bodyMap = new HashMap<>();
                String temp = matcher.group().replace("/*", "<doc>").replace("*/", "</doc>").replace("*", "");
                if (!temp.contains(Constant.CONTROLLER) && !temp.contains(Constant.METHOD) && !temp.contains(Constant.MODEL)) {
                    continue;
                }
                Document document = DocumentHelper.parseText(temp);
                Element root = document.getRootElement();
                // 遍历xml，存储各属性
                for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
                    Element element = it.next();
                    switch (element.getName()) {
                        case Constant.FIELDS:
                            for (Iterator<Element> i = element.elementIterator(); i.hasNext(); ) {
                                Element cur = i.next();
                                fieldMap.put(cur.getName(), cur.getText());
                            }
                            break;
                        case Constant.PARAMS:
                            for (Iterator<Element> i = element.elementIterator(); i.hasNext(); ) {
                                Element cur = i.next();
                                paramMap.put(cur.getName(), cur.getText());
                            }
                            break;
                        case Constant.RETURN:
                            for (Iterator<Element> i = element.elementIterator(); i.hasNext(); ) {
                                Element cur = i.next();
                                returnMap.put(Integer.valueOf(cur.getName().substring(4)), cur.getText());
                            }
                            break;
                        case Constant.BODY:
                            for (Iterator<Element> i = element.elementIterator(); i.hasNext(); ) {
                                Element cur = i.next();
                                bodyMap.put(cur.getName(), cur.getText());
                            }
                            break;
                        default:
                            map.put(element.getName(), element.getText());
                            break;
                    }
                }
                // 填充controller，method，model
                if (map.containsKey(Constant.CONTROLLER)) {
                    readController(controller, map, view);
                } else if (map.containsKey(Constant.METHOD)) {
                    readMethod(controller, map, paramMap, returnMap, bodyMap);
                } else if (map.containsKey(Constant.MODEL)) {
                    readModel(model, map, fieldMap, view);
                }
            }
            return view;
        } catch (Exception e) {
            log.warn("singleReader error!", e);
        }
        return null;
    }

    private void readController(Controller controller, Map<String, String> map, View view) {
        controller = controller.toBuilder()
                .author(map.getOrDefault(Constant.AUTHOR, ""))
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .name(map.getOrDefault(Constant.CONTROLLER, ""))
                .build();
        view.getControllerList().add(controller);
    }

    private void readMethod(Controller controller, Map<String, String> map, Map<String, String> paramMap, Map<Integer, String> returnMap, Map<String, String> bodyMap) {
        // 其次遍历存储method
        Method method = new Method().toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .name(map.getOrDefault(Constant.METHOD, ""))
                .path(map.getOrDefault(Constant.PATH, ""))
                .body(bodyMap)
                .paramMap(paramMap)
                .returnMap(returnMap)
                .build();
        controller.getMethodList().add(method);
    }

    private void readModel(Model model, Map<String, String> map, Map<String, String> fieldMap, View view) {
        model = model.toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .fieldMap(fieldMap)
                .name(map.getOrDefault(Constant.MODEL, ""))
                .build();
        view.getModelList().add(model);
    }


    /** view缓存 **/
    private View viewCache = null;

    /**
     * 多文件读取
     *
     * @param folder 文件夹路径
     * @return 返回view，前端进行渲染
     */
    public View multiReader(String folder) {
        if (viewCache != null) {
            return viewCache;
        }
        View view = new View();
        StopWatch sw = new StopWatch("test");
        File file = new File(curPath + folder);
        sw.start("multi");
        if (file.exists()) {
            if (file.isFile()) {
                view = singleReader(file);
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File single : files) {
                        view.addView(singleReader(single));
                    }
                }
            }
        }
        sw.stop();
        System.out.println(sw.prettyPrint());
        // 缓存
        viewCache = view;
        return view;
    }
}