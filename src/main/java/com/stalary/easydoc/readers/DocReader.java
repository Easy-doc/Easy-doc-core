/**
 * @(#)DocReader.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.Controller;
import com.stalary.easydoc.data.Model;
import com.stalary.easydoc.data.View;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

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

    private EasyDocProperties properties;

    public DocReader(EasyDocProperties properties) {
        this.properties = properties;
    }

    @Override
    public View multiReader() {
        if (viewCache != null) {
            return viewCache;
        }
        View view = new View(properties);
        StopWatch sw = new StopWatch("doc");
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

    @Override
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
                String temp = matcher
                        .group()
                        .replaceAll("\\/\\/[^\n]*", "");
                System.out.println(temp);
            }
        } catch (Exception e) {
            log.warn("singleReader error!", e);
        }
        return null;
    }
}