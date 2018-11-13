/**
 * @(#)DocReader.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.config.SystemConfiguration;
import com.stalary.easydoc.data.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
public class DocReader {

    private View viewCache;

    public View view;

    @Autowired
    ReflectUtils reflectUtils;

    @Autowired
    private DocHandler docHandler;

    @Autowired
    private SystemConfiguration systemConfiguration;

    private EasyDocProperties properties;

    public DocReader(EasyDocProperties properties) {
        this.properties = properties;
    }

    private void getFile(File file, List<File> fileList) {
        if (file.exists()) {
            if (file.isFile()) {
                fileList.add(file);
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File single : files) {
                        getFile(single, fileList);
                    }
                }
            }
        }
    }

    /**
     * 读取文件
     */
    private String readFile(File file) {
        // 此处设置编码，解决乱码问题
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(new FileInputStream(file),
                             Charset.forName("UTF-8")))) {
            StringBuilder sb = new StringBuilder();
            String s = reader.readLine();
            while (s != null) {
                sb.append(s);
                s = reader.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("readFile error!", e);
        }
        return "";
    }

    private void commonReader() {
        docHandler.addSuperModel(view);
        docHandler.addData(view);
        // 缓存
        viewCache = view;
    }

    /**
     * 批量读取文件
     */
    public View multiReader() {
        if (viewCache != null) {
            return viewCache;
        }
        view = new View(properties);
        String fileName = Constant.CUR_PATH + Constant.JAVA + properties.getPath().replaceAll("\\.", "/");
        File file = new File(fileName);
        List<File> fileList = new ArrayList<>();
        getFile(file, fileList);
        pathMapper(fileList);
        for (File aFileList : fileList) {
            singleReader(aFileList);
        }
        commonReader();
        return view;
    }

    /**
     * 读取通过插件生成的文件
     **/
    public View multiReader(String str) {
        if (viewCache != null) {
            return viewCache;
        }
        view = new View(properties);
        String[] pathSplit = str.split(Constant.PATH_SPLIT);
        Constant.PATH_MAP.putAll(JSONObject.parseObject(pathSplit[0], new TypeReference<Map<String, String>>() {
        }));
        String[] fileSplit = pathSplit[1].split(Constant.FILE_SPLIT);
        for (String temp : fileSplit) {
            singleReader(temp);
        }
        commonReader();
        return view;
    }

    public void singleReader(String str) {
        String[] split = str.split(Constant.MATCH_SPLIT);
        String name = split[0];
        Controller controller = new Controller();
        Model model = new Model();
        if (split.length > 1) {
            for (int i = 1; i < split.length; i++) {
                docHandler.handle(controller, model, split[i], name, view);
            }
        }
    }

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
                docHandler.handle(controller, model, temp, name, view);
            }
        } catch (Exception e) {
            log.warn("singleReader error!", e);
        }
    }

    /**
     * 将所有文件构造出映射关系
     */
    public void pathMapper(List<File> fileList) {
        fileList.forEach(file -> {
            NamePack namePack = path2Pack(file.getPath());
            Constant.PATH_MAP.put(namePack.getName(), namePack.getPackPath());
        });
    }

    /**
     * 将文件路径转化为类名:包路径的映射
     */
    private NamePack path2Pack(String path) {
        String temp;
        if (systemConfiguration.isWindows()) {
            temp = path.replaceAll("\\\\", ".");
        } else {
            temp = path.replaceAll("/", ".");
        }
        String packPath = temp.substring(temp.indexOf(properties.getPath()));
        packPath = packPath.substring(0, packPath.lastIndexOf("."));
        return new NamePack(packPath.substring(packPath.lastIndexOf(".") + 1), packPath);
    }

}