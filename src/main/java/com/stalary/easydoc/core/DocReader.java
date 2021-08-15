/*
 * @(#)DocReader.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.core;


import com.fasterxml.jackson.core.type.TypeReference;
import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.config.SystemConfiguration;
import com.stalary.easydoc.data.*;
import com.stalary.easydoc.util.JsonUtils;
import com.stalary.easydoc.util.RegularExpressionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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
    private DocHandler docHandler;

    @Autowired
    private SystemConfiguration systemConfiguration;

    private EasyDocProperties properties;

    public DocReader(EasyDocProperties properties) {
        this.properties = properties;
    }

    /**
     * getFile 获取文件
     *
     * @param file     传入文件
     * @param fileList 生成的文件列表
     **/
    private void getFile(File file, List<File> fileList) {
        if (file.exists()) {
            if (file.isFile()) {
                // 获取去掉后缀的文件名
                String name = file.getName().split("\\.")[0];
                // 排除掉不需要的文件
                if (!properties.getExcludeFile().contains(name)) {
                    if (!properties.getIncludeFile().isEmpty()) {
                        if (properties.getIncludeFile().contains(name)) {
                            fileList.add(file);
                        }
                    } else {
                        fileList.add(file);
                    }
                }
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
     * readFile 读取单个文件
     *
     * @param file 文件
     * @return 内容
     **/
    private String readFile(File file) {
        // 此处设置编码，解决乱码问题
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(new FileInputStream(file),
                             StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String s = reader.readLine();
            while (s != null) {
                sb.append(s);
                s = reader.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("easy-doc readFile error!", e);
        }
        return "";
    }

    /**
     * commonReader 公共读入方法
     **/
    private void commonReader() {
        docHandler.addSuperAndNestModel(view);
        docHandler.addUrl(view);
        // 缓存
        viewCache = view;
    }

    /**
     * multiReader 多文件读入方法
     *
     * @return 前端渲染对象
     **/
    public View multiReader() {
        if (viewCache != null) {
            return viewCache;
        }
        view = new View(properties);
        if (properties.isGateway()) {
            return view;
        }
        String fileName = Constant.CUR_PATH + Constant.JAVA + properties.getPath().replaceAll("\\.", "/");
        File file = new File(fileName);
        List<File> fileList = new ArrayList<>();
        getFile(file, fileList);
        pathMapper(fileList);
        for (File aFileList : fileList) {
            singleReader(aFileList);
        }
        // 最后添加父类和接口映射
        commonReader();
        // 按照名词对接口和model排序
        sortByName();
        // 计算数量
        calCount();
        return view;
    }

    /**
     * calCount 计算接口和对象数量
     **/
    private void calCount() {
        int totalMethod = (int) view.getControllerList()
                .stream()
                .mapToLong(c -> c.getMethodList().size())
                .sum();
        int deprecatedMethod = (int) view.getControllerList()
                .stream()
                .mapToLong(c -> c.getMethodList().stream().filter(Method::isDeprecated).count())
                .sum();
        int totalModel = view.getModelList().size();
        int deprecatedModel = (int) view.getModelList().stream().filter(Model::isDeprecated).count();
        view.setTotalMethod(totalMethod);
        view.setDeprecatedMethod(deprecatedMethod);
        view.setTotalModel(totalModel);
        view.setDeprecatedModel(deprecatedModel);
    }

    /**
     * sortByName 根据名称排序
     *
     * @param view 前端渲染对象
     **/
    private void sortByName() {
        view.getControllerList().sort(Comparator.comparing(Controller::getName));
        view.getModelList().sort(Comparator.comparing(Model::getName));
    }

    /**
     * multiReader 多匹配后字符串渲染方法
     *
     * @param str 匹配后的字符串
     * @return 前端渲染对象
     **/
    public View multiReader(String str) {
        if (viewCache != null) {
            return viewCache;
        }
        view = new View(properties);
        if (properties.isGateway()) {
            return view;
        }
        String[] pathSplit = str.split(Constant.PATH_SPLIT);
        Constant.PATH_MAP.putAll(JsonUtils.parse(pathSplit[0], new TypeReference<Map<String, String>>() {
        }));
        String[] fileSplit = pathSplit[1].split(Constant.FILE_SPLIT);
        for (String temp : fileSplit) {
            try {
                singleReader(temp);
            } catch (Exception e) {
                log.warn("easy-doc singleReader error! fileName={}, error_msg={}, error_stack={}", temp, e.getLocalizedMessage(), e.getStackTrace());
            }
        }
        // 最后添加父类和接口映射
        commonReader();
        // 按照名词对接口和model排序
        sortByName();
        // 计算数量
        calCount();
        return view;
    }

    /**
     * singleReader 单匹配后字符串渲染方法
     *
     * @param str 传入匹配后的字符串
     **/
    private void singleReader(String str) {
        String[] split = str.split(Constant.MATCH_SPLIT);
        if (split.length > 0) {
            String name = split[0];
            Controller controller = new Controller();
            Model model = new Model();
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    docHandler.handle(controller, model, split[i], name, view);
                }
            }
        }
    }

    /**
     * singleReader 单文件读入方法
     *
     * @param file 文件
     **/
    private void singleReader(File file) {
        // 获取文件名称
        String fileName = file.getName();
        try {
            String name = fileName.substring(0, fileName.indexOf(Constant.PACKAGE_SPLIT));
            Controller controller = new Controller();
            Model model = new Model();
            String str = readFile(file);
            // 匹配出注释代码块
            String regex = "\\/\\*(\\s|.)*?\\*\\/";
            Matcher matcher = RegularExpressionUtils.createMatcherWithTimeout(str, regex, 200);
            while (matcher.find()) {
                // 1. 去除所有单行注释
                // 2. 匹配块级注释
                // 3. 合并多个空格
                String temp = matcher
                        .group()
                        .replaceAll("\\/\\*\\*", Constant.EMPTY_STR)
                        .replaceAll("\\*\\/", Constant.EMPTY_STR)
                        .replaceAll("\\*", Constant.EMPTY_STR)
                        .replaceAll(" +", Constant.BLANK);
                docHandler.handle(controller, model, temp, name, view);
            }
        } catch (Exception e) {
            log.warn("easy-doc singleReader error! fileName={}, error_msg={}, error_stack={}", fileName, e.getLocalizedMessage(), e.getStackTrace());
        }
    }

    /**
     * pathMapper 路径映射生成方法
     *
     * @param fileList 文件列表
     **/
    private void pathMapper(List<File> fileList) {
        fileList.forEach(file -> {
            NamePack namePack = path2Pack(file.getPath());
            Constant.PATH_MAP.put(namePack.getName(), namePack.getPackPath());
        });
    }

    /**
     * path2Pack 将文件路径转化为类名:包路径的映射
     *
     * @param path 路径
     * @return 类名:包路径的映射
     **/
    private NamePack path2Pack(String path) {
        String temp;
        if (systemConfiguration.isWindows()) {
            temp = path.replace("\\", ".");
        } else {
            temp = path.replace("/", ".");
        }
        String packPath = temp.substring(temp.indexOf(properties.getPath()));
        packPath = packPath.substring(0, packPath.lastIndexOf(Constant.PACKAGE_SPLIT));
        return new NamePack(packPath.substring(packPath.lastIndexOf(Constant.PACKAGE_SPLIT) + 1), packPath);
    }

}