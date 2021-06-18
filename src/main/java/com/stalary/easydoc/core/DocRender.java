/**
 * @(#)DocRender.java, 2018-11-13.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.core;

import com.stalary.easydoc.data.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * DocRender
 *
 * @author lirongqian
 * @since 2018/11/13
 */
@Component
@Slf4j
public class DocRender {

    /**
     * render 渲染controller，method，model，部分字段通过反射进行读取
     *
     * @param controller   controller渲染对象
     * @param map          辅助操作map
     * @param paramList    参数列表
     * @param fieldList    model字段列表
     * @param responseList 返回列表
     * @param throwsMap    异常map
     * @param view         前端渲染对象
     * @param model        model渲染对象
     **/
    public void render(Controller controller, Map<String, String> map,
                       List<Param> paramList, List<Param> fieldList,
                       List<Response> responseList, Map<String, String> throwsMap,
                       View view, Model model) {
        // 填充controller，method，model
        if (map.size() > 0) {
            if (map.containsKey(Constant.CONTROLLER)) {
                map.put(Constant.PATH, ReflectUtils.getControllerPath(map.get(Constant.CONTROLLER)));
                map.put(Constant.DEPRECATED, String.valueOf(ReflectUtils.isDeprecated(map.get(Constant.CONTROLLER),
                        "")));
                renderController(controller, map, view);
            } else if (map.containsKey(Constant.METHOD)) {
                RequestMapping mapping = ReflectUtils.getMethodMapping(controller.getName(), map.get(Constant.METHOD));
                if (mapping != null) {
                    if (mapping.value().length != 0) {
                        map.put(Constant.PATH, mapping.value()[0]);
                    }
                    if (mapping.method().length != 0) {
                        RequestMethod[] method = mapping.method();
                        StringBuilder sb = new StringBuilder();
                        for (RequestMethod requestMethod : method) {
                            sb.append(requestMethod.toString()).append(",");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        map.put(Constant.TYPE, sb.toString());
                    }
                }
                map.put(Constant.DESCRIPTION, map.get(map.get(Constant.METHOD)));
                map.put(Constant.DEPRECATED, String.valueOf(ReflectUtils.isDeprecated(controller.getName(),
                        map.get(Constant.METHOD))));
                renderMethod(controller, map, paramList, responseList, throwsMap,
                        ReflectUtils.getBody(controller.getName(), map.get(Constant.METHOD), view));
            } else if (map.containsKey(Constant.MODEL)) {
                map.put(Constant.DEPRECATED, String.valueOf(ReflectUtils.isDeprecated(map.get(Constant.MODEL), "")));
                renderModel(model, map, fieldList, view);
            }
        }
    }

    /**
     * renderController
     *
     * @param controller controller渲染对象
     * @param map        辅助map
     * @param view       前端渲染对象
     **/
    private void renderController(Controller controller, Map<String, String> map, View view) {
        controller.setAuthor(map.getOrDefault(Constant.AUTHOR, ""));
        controller.setDescription(map.getOrDefault(Constant.DESCRIPTION, ""));
        controller.setName(map.getOrDefault(Constant.CONTROLLER, ""));
        controller.setPath(map.getOrDefault(Constant.PATH, ""));
        controller.setDeprecated(Boolean.parseBoolean(map.getOrDefault(Constant.DEPRECATED, "")));
        view.getControllerList().add(controller);
    }

    /**
     * renderMethod
     *
     * @param controller   controller渲染对象
     * @param map          辅助map
     * @param paramList    参数列表
     * @param responseList 返回列表
     * @param throwsMap    异常map
     * @param body         model渲染对象
     **/
    private void renderMethod(Controller controller, Map<String, String> map,
                              List<Param> paramList, List<Response> responseList,
                              Map<String, String> throwsMap, Model body) {
        renderParamList(controller.getName(), map.getOrDefault(Constant.METHOD, ""), paramList);
        // data渲染要放在最后
        Method method = new Method().toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .path(map.getOrDefault(Constant.PATH, ""))
                .type(map.getOrDefault(Constant.TYPE, ""))
                .body(body)
                .paramList(paramList)
                .responseList(responseList)
                .throwsMap(throwsMap)
                .deprecated(Boolean.parseBoolean(map.getOrDefault(Constant.DEPRECATED, "")))
                .build();
        controller.getMethodList().add(method);
    }

    /**
     * renderModel 渲染model
     *
     * @param model     model渲染对象
     * @param map       辅助map
     * @param fieldList 参数列表
     * @param view      前端渲染对象
     **/
    private void renderModel(Model model, Map<String, String> map, List<Param> fieldList, View view) {
        renderModelField(map.getOrDefault(Constant.MODEL, ""), fieldList);
        model = model.toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .name(map.getOrDefault(Constant.MODEL, ""))
                .author(map.getOrDefault(Constant.AUTHOR, ""))
                .deprecated(Boolean.parseBoolean(map.getOrDefault(Constant.DEPRECATED, "")))
                .fieldList(fieldList)
                .build();
        view.getModelList().add(model);
        // 渲染body
        List<Controller> controllerList = view.getControllerList();
        if (!controllerList.isEmpty()) {
            for (Controller controller : controllerList) {
                List<Method> methodList = controller.getMethodList();
                for (Method method : methodList) {
                    // 当body还未解析时，存入model
                    if (method.getBody() != null && model.getName().equals(method.getBody().getName()) && StringUtils.isEmpty(method.getBody().getDescription())) {
                        method.setBody(model);
                    }
                }
            }
        }
    }

    /**
     * renderParamList 渲染参数列表
     *
     * @param controller controller渲染对象
     * @param method     方法名称
     * @param paramList  参数列表
     **/
    private void renderParamList(String controller, String method, List<Param> paramList) {
        Map<String, Param> params = ReflectUtils.getParams(controller, method);
        paramList.forEach(param -> {
            Param temp = params.get(param.getName());
            param.setType(trans2Js(temp.getType()));
            param.setRequired(temp.isRequired());
            param.setDefaultValue(temp.getDefaultValue());
        });

    }

    /**
     * trans2Js 将java类型转化为js类型
     *
     * @param type java类型
     * @return js类型
     **/
    private String trans2Js(String type) {
        if (StringUtils.isEmpty(type)) {
            return "";
        }
        switch (type) {
            case "java.lang.String":
            case "javax.servlet.http.HttpServletRequest":
            case "javax.servlet.http.HttpServletResponse":
                return "String";
            case "java.lang.Double":
            case "java.lang.Float":
            case "double":
            case "float":
                return "Double";
            case "java.lang.Integer":
            case "int":
            case "java.lang.Long":
            case "long":
                return "Integer";
            case "java.lang.Boolean":
            case "boolean":
                return "Boolean";
            case "java.util.List":
                return "List";
            case "java.sql.Timestamp":
                return "Timestamp";
            case "java.util.Date":
                return "Date";
            case "org.springframework.web.multipart.MultipartFile":
                return "File";
            default:
                return "Object";
        }
    }

    /**
     * renderModelField 渲染field
     *
     * @param modelName model名称
     * @param fieldList model中字段列表
     **/
    private void renderModelField(String modelName, List<Param> fieldList) {
        Map<String, String> fieldMap = ReflectUtils.getField(modelName);
        fieldList.forEach(field -> field.setType(trans2Js(fieldMap.getOrDefault(field.getName(), ""))));
    }

}