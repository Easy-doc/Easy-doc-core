/*
 * @(#)ReflectReader.java, 2018-09-28.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.core;

import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.Model;
import com.stalary.easydoc.data.Param;
import com.stalary.easydoc.data.View;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.*;
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

    /**
     * isController 判断是否为controller
     *
     * @param name controller名称
     * @return true|false
     **/
    static boolean isController(String name) {
        Class clazz = path2Class(name);
        return AnnotatedElementUtils.hasAnnotation(clazz, Controller.class);
    }

    /**
     * getControllerPath 获取controller中的路径
     *
     * @param name controller名称
     * @return 路径
     **/
    static String getControllerPath(String name) {
        Class clazz = path2Class(name);
        RequestMapping annotation = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
        if (annotation != null) {
            return annotation.value()[0];
        }
        return "";
    }

    /**
     * getMethod 获取method
     *
     * @param controllerName controller名称
     * @param methodName     方法名称
     * @return 方法
     **/
    private static Method getMethod(String controllerName, String methodName) {
        Class clazz = path2Class(controllerName);
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NullPointerException("getMethod method " + methodName + " is null");
    }

    /**
     * getMethodMapping 获取RequestMapping
     *
     * @param controllerName controller名称
     * @param methodName     方法名称
     * @return RequestMapping
     **/
    static RequestMapping getMethodMapping(String controllerName, String methodName) {
        return AnnotatedElementUtils.findMergedAnnotation(getMethod(controllerName, methodName), RequestMapping.class);
    }

    /**
     * path2Class 将包路径转化为class
     *
     * @param name 类名
     * @return 类
     **/
    private static Class path2Class(String name) {
        try {
            return Class.forName(Constant.PATH_MAP.get(name));
        } catch (Exception e) {
            throw new NullPointerException("path2Class " + name + " error, " + "path=" + Constant.PATH_MAP.get(name));
        }
    }

    /**
     * getBodyParam 获取被@RequestBody注解的自定义参数
     *
     * @param controllerName controller名称
     * @param methodName     方法名称
     * @return 参数
     **/
    private static Parameter getBodyParam(String controllerName, String methodName) {
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
     * getBody 获取Model
     *
     * @param controllerName controller名称
     * @param methodName     方法名称
     * @param view           前端渲染对象
     * @return Model对象
     **/
    static Model getBody(String controllerName, String methodName, View view) {
        try {
            Parameter parameter = getBodyParam(controllerName, methodName);
            if (parameter != null) {
                String name = parameter.getType().getName();
                final String finalName = name.substring(name.lastIndexOf(Constant.PACKAGE_SPLIT) + 1);
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
            log.warn("easy-doc getBody error!", e);
        }
        return null;
    }

    /**
     * isDeprecated 判断是否已经被弃用
     *
     * @param className  类名
     * @param methodName 方法名
     * @return true|false
     **/
    static boolean isDeprecated(String className, String methodName) {
        Class clazz = path2Class(className);
        if (StringUtils.isEmpty(className)) {
            return false;
        }
        // 只传入类名代表判断类
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

    /**
     * getParams 获取method中的param
     *
     * @param controllerName controller名称
     * @param methodName     方法名
     * @return 参数map
     **/
    static Map<String, Param> getParams(String controllerName, String methodName) {
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

    /**
     * getField 获取model中的field
     *
     * @param modelName model名称
     * @return 字段map
     **/
    static Map<String, String> getField(String modelName) {
        Class clazz = path2Class(modelName);
        Map<String, String> result = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            result.put(field.getName(), field.getType().getName());
        }
        return result;
    }

    /**
     * getSuper 获取父类名称
     *
     * @param name 类名
     * @return 父类名
     **/
    static String getSuper(String name) {
        Class clazz = path2Class(name);
        Class superClazz = clazz.getSuperclass();
        String simpleName = superClazz.getSimpleName();
        if (Constant.OBJECT.equals(simpleName)) {
            return null;
        }
        return simpleName;
    }

    /**
     * @param name 类名
     * @return 嵌套类名
     * @method getNest 获取嵌套类名称
     **/
    static Set<Pair<String, String>> getNest(String name) {
        Class clazz = path2Class(name);
        Class[] declaredClasses = clazz.getDeclaredClasses();
        Set<Pair<String, String>> ret = new HashSet<>();
        for (Class nestedClass : declaredClasses) {
            Field[] fields = nestedClass.getDeclaredFields();
            addField(fields, ret);
        }
        Field[] fields = clazz.getDeclaredFields();
        addField(fields, ret);
        return ret;
    }

    private static void addField(Field[] fields, Set<Pair<String, String>> ret) {
        for (Field field : fields) {
            field.setAccessible(true);
            // 不存在classloader的为自定义类
            if (field.getType().getClassLoader() != null) {
                String name = field.getType().getName();
                // nested
                if (name.contains("$")) {
                    int i = name.lastIndexOf(".");
                    String substring = name.substring(i + 1);
                    name = substring.replace("$", ".");
                } else {
                    name = field.getType().getSimpleName();
                }
                ret.add(Pair.of(name, field.getName()));
            } else {
                if (field.getType() == java.util.List.class || field.getType() == java.util.Map.class) {
                    Type type = field.getGenericType();
                    if (type instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) type;
                        if (pt.getActualTypeArguments()[0] instanceof Class) {
                            Class<?> genericClass = (Class<?>) pt.getActualTypeArguments()[0];
                            if (genericClass.getClassLoader() != null) {
                                ret.add(Pair.of(genericClass.getSimpleName(), field.getName()));
                            }
                        }
                    }
                } else {
                    ret.add(Pair.of(field.getType().getSimpleName(), field.getName()));
                }
            }
        }
    }

}