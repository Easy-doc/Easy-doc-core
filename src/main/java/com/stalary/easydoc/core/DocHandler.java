/**
 * @(#)DocHandler.java, 2018-11-13.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.core;

import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.Controller;
import com.stalary.easydoc.data.Field;
import com.stalary.easydoc.data.Model;
import com.stalary.easydoc.data.Param;
import com.stalary.easydoc.data.Response;
import com.stalary.easydoc.data.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DocHandler
 *
 * @author lirongqian
 * @since 2018/11/13
 */
@Component
public class DocHandler {

    @Autowired
    ReflectUtils reflectUtils;

    @Autowired
    private DocRender docRender;

    /**
     * handle 核心处理方法
     * @param controller controller渲染对象
     * @param model model渲染对象
     * @param temp 当前传入的文件
     * @param name 文件名称
     * @param view 前端渲染对象
     **/
    public void handle(Controller controller, Model model, String temp, String name, View view) {
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
        // 合并解释之间的空格
        int pre = 0;
        boolean merge = false;
        for (int i = 1; i < split.length; i++) {
            if (split[i].contains("@")) {
                int distance = i - pre;
                if (distance > 3) {
                    if (StringUtils.isNotBlank(split[pre])) {
                        for (int j = pre + 3; j < i; j++) {
                            split[pre + 2] += " " + split[j];
                            split[j] = "-";
                            merge = true;
                        }
                    }
                }
                pre = i;
            }
        }
        if (merge) {
            List<String> filterList = Arrays.stream(split).filter(d -> !d.equals("-")).collect(Collectors.toList());
            split = filterList.toArray(new String[]{});
        }
        int len = split.length;
        if (name.equals(split[1]) && reflectUtils.isController(name)) {
            map.put(Constant.CONTROLLER, split[1]);
        }
        if (Constant.MODEL_TAG.equals(split[1]) && name.equals(split[2])) {
            map.put(Constant.MODEL, split[2]);
        }
        for (int i = 1; i < len; i++) {
            String t = split[i];
            // 匹配到Controller
            if (t.contains("@")) {
                cur = t.substring(1);
            } else {
                if (StringUtils.isNotEmpty(cur)) {
                    switch (cur) {
                        case Constant.PARAM:
                            if (i + 1 < len) {
                                paramList.add(new Param(t, split[i + 1]));
                                i++;
                            }
                            break;
                        case Constant.RETURN:
                            if (i + 1 < len) {
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
                                i++;
                            }
                            break;
                        case Constant.FIELD:
                            if (i + 1 < len) {
                                fieldList.add(new Param(t, split[i + 1]));
                                i++;
                            }
                            break;
                        case Constant.METHOD:
                            if (i + 1 < len) {
                                map.put(t, split[i + 1]);
                                i++;
                            }
                            map.put(Constant.METHOD, t);
                            break;
                        case Constant.THROWS:
                            if (i + 1 < len) {
                                throwsMap.put(t, split[i + 1]);
                                i++;
                            }
                            break;
                        default:
                            map.put(cur, t);
                            break;
                    }
                }
            }
        }
        docRender.render(controller, map, paramList, fieldList, responseList, throwsMap, view, model);
    }

    /**
     * addSuperModel 添加model的父类
     * @param view 前端渲染对象
     **/
    void addSuperAndNestModel(View view) {
        Map<String, Model> modelMap = view.getModelList().stream().collect(Collectors.toMap(Model::getName, e -> e));
        // 填充父类对象
        view.getModelList().forEach(model -> {
            String superName = reflectUtils.getSuper(model.getName());
            if (StringUtils.isNotEmpty(superName)) {
                Model superModel = modelMap.get(superName);
                if (superModel != null) {
                    model.getFieldList().addAll(superModel.getFieldList());
                }
            }
        });
        // 填充嵌套对象
        view.getModelList().forEach(model -> fillNestModel(model, modelMap));
        // 填充responseList
        view.getControllerList().forEach(controller -> controller.getMethodList().forEach(method -> method.getResponseList().forEach(response -> response.getFieldList().forEach(field -> {
            field.setData(modelMap.get(field.getName()));
        }))));
    }

    /**
     * fillNestModel 填充嵌套对象
     * @param model 填充对象
     * @param modelMap 对象临时map
     **/
    private void fillNestModel(Model model, Map<String, Model> modelMap) {
        Set<Pair<String, String>> nestNameList = reflectUtils.getNest(model.getName());
        if (!nestNameList.isEmpty()) {
            for (Pair<String, String> nestPair : nestNameList) {
                Model nestModel = modelMap.get(nestPair.getKey());
                if (nestModel != null) {
                    for (Param param : model.getFieldList()) {
                        if (nestPair.getValue().equals(param.getName())) {
                            param.setNestModel(nestModel);
                        }
                    }
                    // 更新modelMap
                    modelMap.put(model.getName(), model);
                }
            }
        }
    }

    /**
     * addURL 添加接口映射
     * @param view 前端渲染对象
     **/
    void addURL(View view) {
        view.getControllerList().forEach(controller -> controller.getMethodList().forEach(method -> {
            Constant.URL_LIST.add(controller.getPath() + method.getPath());
        }));
    }

}