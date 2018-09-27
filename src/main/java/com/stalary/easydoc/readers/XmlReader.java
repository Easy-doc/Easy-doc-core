/**
 * @(#)Reader.java, 2018-09-25.
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
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BaseReader
 *
 * @author lirongqian
 * @since 2018/09/25
 */
@Component
@Slf4j
public class XmlReader extends BaseReader {

    private EasyDocProperties properties;

    public XmlReader(EasyDocProperties properties) {
        this.properties = properties;
    }

    /**
     * 单文件读取
     *
     * @param file 单文件路径
     * @return 返回view，前端进行渲染
     */
    @Override
    @SuppressWarnings("unchecked")
    public View singleReader(File file) {
        try {
            View view = new View();
            Controller controller = new Controller();
            Model model = new Model();
            String str = readFile(file);
            // 匹配出注释代码块
            String regex = "(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                Map<String, String> map = new HashMap<>();
                Map<String, String> paramMap = new HashMap<>();
                Map<String, String> fieldMap = new HashMap<>();
                Map<Integer, String> returnMap = new HashMap<>();
                Map<String, String> bodyMap = new HashMap<>();
                // 1. 去除所有单行注释
                // 2. 匹配块级注释
                // 3. 文档化块级注释
                String temp = matcher
                        .group()
                        .replaceAll("\\/\\/[^\n]*", "")
                        .replace("/*", "<doc>")
                        .replace("*/", "</doc>")
                        .replace("*", "");
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
                    renderController(controller, map, view);
                } else if (map.containsKey(Constant.METHOD)) {
                    renderMethod(controller, map, paramMap, returnMap, bodyMap);
                } else if (map.containsKey(Constant.MODEL)) {
                    renderModel(model, map, fieldMap, view);
                }
            }
            return view;
        } catch (Exception e) {
            log.warn("singleReader error!", e);
        }
        return null;
    }

    /**
     * 多文件读取
     *
     * @return 返回view，前端进行渲染
     */
    @Override
    public View multiReader() {
        if (viewCache != null) {
            return viewCache;
        }
        View view = new View(properties);
        StopWatch sw = new StopWatch("xml");
        File file = new File(CUR_PATH + properties.getPath());
        sw.start("multi");
        List<File> fileList = new ArrayList<>();
        getFile(file, fileList);
        for (File aFileList : fileList) {
            view.addView(singleReader(aFileList));
        }
        sw.stop();
        System.out.println(sw.prettyPrint());
        // 缓存
        viewCache = view;
        return view;
    }

}