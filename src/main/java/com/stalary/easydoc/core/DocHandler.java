/**
 * @(#)DocHandler.java, 2018-11-13.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.core;

import com.stalary.easydoc.data.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        docRender.render(controller, map, paramList, fieldList, responseList, throwsMap, view, model);
    }

    /**
     * 填充response中的data
     */
    // todo: 时间复杂度太高了。。待优化
    public void addData(View view) {
        List<Model> modelList = view.getModelList();
        view.getControllerList().forEach(controller -> controller.getMethodList().forEach(method -> method.getResponseList().forEach(response -> response.getFieldList().forEach(field -> modelList.forEach(model -> {
            if (field.getName().equals(model.getName())) {
                field.setData(model);
            }
        })))));
    }

    public void addSuperModel(View view) {
        Map<String, Model> modelMap = view.getModelList().stream().collect(Collectors.toMap(Model::getName, e -> e));
        view.getModelList().forEach(model -> {
            String superName = reflectUtils.getSuper(model.getName());
            if (StringUtils.isNotEmpty(superName)) {
                Model superModel = modelMap.get(superName);
                if (superModel != null) {
                    model.getFieldList().addAll(superModel.getFieldList());
                }
            }
        });
    }

}