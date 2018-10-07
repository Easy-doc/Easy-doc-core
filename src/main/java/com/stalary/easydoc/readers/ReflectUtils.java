/**
 * @(#)ReflectReader.java, 2018-09-28.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.stalary.easydoc.annotation.ModelData;
import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.Model;
import com.stalary.easydoc.data.View;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ReflectReader
 *
 * @author lirongqian
 * @since 2018/09/28
 */
@Slf4j
@Component
public class ReflectUtils {

    private EasyDocProperties properties;

    public ReflectUtils(EasyDocProperties properties) {
        this.properties = properties;
    }

    /**
     * 判断是否为controller
     *
     * @param name
     * @return
     */
    public boolean isController(String name) {
        Class clazz = path2Class(name);
        if (clazz != null) {
            return AnnotatedElementUtils.hasAnnotation(clazz, Controller.class);
        }
        return false;
    }

    /**
     * 判断是否为model
     *
     * @param name
     * @return
     */
    public boolean isModel(String name) {
        Class clazz = path2Class(name);
        if (clazz != null) {
            return AnnotatedElementUtils.hasAnnotation(clazz, ModelData.class);
        }
        return false;
    }

    /**
     * 获取controller中的路径
     *
     * @param name
     * @return
     */
    public String getControllerPath(String name) {
        Class clazz = path2Class(name);
        RequestMapping annotation = AnnotationUtils.findAnnotation(Objects.requireNonNull(clazz), RequestMapping.class);
        if (annotation != null) {
            return annotation.value()[0];
        }
        return "";
    }

    public RequestMapping getMethod(String controllerName, String methodName) {
        Class clazz = path2Class(controllerName);
        if (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    // 获取名称或者别名为RequestMapping的注解
                    return AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                }
            }
        }
        return null;
    }

    /**
     * 获取method中的路径
     *
     * @param controllerName
     * @param methodName
     * @return
     */
    public String getMethodPath(String controllerName, String methodName) {
        RequestMapping mapping = getMethod(controllerName, methodName);
        if (mapping != null) {
            if (mapping.value().length != 0) {
                return mapping.value()[0];
            }
        }
        return "";
    }

    public String getMethodType(String controllerName, String methodName) {
        RequestMapping mapping = getMethod(controllerName, methodName);
        if (mapping != null) {
            if (mapping.method().length != 0) {
                return mapping.method()[0].name();
            }
        }
        return "";
    }

    /**
     * 获取需要生成文档的接口
     *
     * @param name
     * @return
     */
    public List<String> getMethod(String name) {
        Class clazz = path2Class(name);
        if (clazz != null) {
            List<String> result = new ArrayList<>();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    result.add(method.getName());
                }
            }
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 将包路径转化为class
     *
     * @param name
     * @return
     */
    private Class path2Class(String name) {
        try {
            return Class.forName(Constant.pathMap.get(name));
        } catch (Exception e) {
            log.warn("path2Class error!", e);
        }
        return null;
    }

    public Model getBody(String controllerName, String methodName, View view) {
        try {
            Class clazz = path2Class(controllerName);
            if (clazz != null) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (methodName.equals(method.getName())) {
                        Parameter[] parameters = method.getParameters();
                        for (Parameter parameter : parameters) {
                            if (parameter.isAnnotationPresent(RequestBody.class)) {
                                String name = parameter.getType().getName();
                                if (!name.startsWith("java")) {
                                    final String finalName = name.substring(name.lastIndexOf(".") + 1);
                                    AtomicReference<Model> finalModel = new AtomicReference<>();
                                    view.getModelList().forEach(model -> {
                                        if (model.getName().equals(finalName)) {
                                            finalModel.set(model);
                                        }
                                    });
                                    // 还未解析出model时，暂时存储name
                                    Model model = finalModel.get();
                                    if (model != null) {
                                        return model;
                                    }
                                    return new Model().toBuilder().name(finalName).build();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("getBody error!", e);
        }
        return null;
    }
}