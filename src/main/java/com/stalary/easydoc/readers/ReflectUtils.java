/**
 * @(#)ReflectReader.java, 2018-09-28.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.Model;
import com.stalary.easydoc.data.Param;
import com.stalary.easydoc.data.View;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
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

    public ReflectUtils() {
    }

    /**
     * 判断是否为controller
     *
     * @param name
     * @return
     */
    public boolean isController(String name) {
        Class clazz = path2Class(name);
        if (clazz == null) {
            throw new NullPointerException("class is null");
        }
        return AnnotatedElementUtils.hasAnnotation(clazz, Controller.class);
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

    public Method getMethod(String controllerName, String methodName) {
        Class clazz = path2Class(controllerName);
        if (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
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
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(getMethod(controllerName, methodName), RequestMapping.class);
        if (mapping != null) {
            if (mapping.value().length != 0) {
                return mapping.value()[0];
            }
        }
        return "";
    }

    public String getMethodType(String controllerName, String methodName) {
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(getMethod(controllerName, methodName), RequestMapping.class);
        if (mapping != null) {
            if (mapping.method().length != 0) {
                return mapping.method()[0].name();
            }
        }
        return "";
    }

    /**
     * 将包路径转化为class
     *
     * @param name
     * @return
     */
    private Class path2Class(String name) {
        try {
            return Class.forName(Constant.PATH_MAP.get(name));
        } catch (Exception e) {
            log.warn("path2Class error!", e);
        }
        return null;
    }

    /**
     * 获取被@RequestBody注解的参数
     *
     * @param controllerName controllerName
     * @param methodName     methodName
     * @return Parameter
     */
    private Parameter getBodyParam(String controllerName, String methodName) {
        Method method = getMethod(controllerName, methodName);
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (AnnotatedElementUtils.hasAnnotation(parameter, RequestBody.class)) {
                String name = parameter.getType().getName();
                if (!name.startsWith("java")) {
                    return parameter;
                }
            }
        }
        return null;
    }


    /**
     * 获取被@RequestBody注解的Model
     *
     * @param controllerName
     * @param methodName
     * @param view
     * @return
     */
    public Model getBody(String controllerName, String methodName, View view) {
        try {
            Parameter parameter = getBodyParam(controllerName, methodName);
            if (parameter != null) {
                String name = parameter.getType().getName();
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
        } catch (Exception e) {
            log.warn("getBody error!", e);
        }
        return null;
    }

    /**
     * 判断是否已经被弃用
     *
     * @param className
     * @param methodName
     * @return
     */
    public boolean isDeprecated(String className, String methodName) {
        Class clazz = path2Class(className);
        if (clazz == null) {
            throw new NullPointerException("class is null");
        }
        if (StringUtils.isEmpty(className)) {
            return false;
        }
        // 只传入controller代表判断controller
        if (StringUtils.isEmpty(methodName)) {
            Deprecated annotation = AnnotationUtils.findAnnotation(clazz, Deprecated.class);
            return annotation != null;
        } else {
            for (Method method : clazz.getDeclaredMethods()) {
                if (methodName.equals(method.getName())) {
                    Deprecated annotation = AnnotationUtils.findAnnotation(method, Deprecated.class);
                    return annotation != null;
                }
            }
        }
        return false;
    }

    public Map<String, Param> getParams(String controllerName, String methodName) {
        Method method = getMethod(controllerName, methodName);
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        Map<String, Param> result = new HashMap<>();
        List<String> keyList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(discoverer.getParameterNames(method))));
        List<String> typeList = new ArrayList<>();
        List<Boolean> requiredList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        for (Class clazz : method.getParameterTypes()) {
            typeList.add(clazz.getName());

        }
        for (Parameter parameter : method.getParameters()) {
            RequestParam annotation = AnnotationUtils.findAnnotation(parameter, RequestParam.class);
            if (annotation == null) {
                requiredList.add(true);
                valueList.add("");
            } else {
                requiredList.add(annotation.required());
                if (ValueConstants.DEFAULT_NONE.equals(annotation.defaultValue())) {
                    valueList.add("");
                } else {
                    valueList.add(annotation.defaultValue());
                }
            }
        }
        for (int i = 0; i < keyList.size(); i++) {
            result.put(keyList.get(i), new Param(typeList.get(i), requiredList.get(i), valueList.get(i)));
        }
        return result;
    }

    public Map<String, String> getField(String modelName) {
        Class clazz = path2Class(modelName);
        if (clazz == null) {
            throw new NullPointerException("get Field class is null");
        }
        Map<String, String> result = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            result.put(field.getName(), field.getType().getName());
        }
        return result;
    }

}