package com.stalary.easydoc.readers;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Stalary
 * @description
 * @date 2018/9/27
 */
@Slf4j
public abstract class BaseReader {

    private View viewCache;

    View view;

    @Autowired
    ReflectUtils reflectUtils;

    private EasyDocProperties properties;

    public BaseReader(EasyDocProperties properties) {
        this.properties = properties;
    }

    /**
     * 批量读取文件
     */
    // todo:暴露出main方法调用的接口，缓存view
    public View multiReader() {
        if (viewCache != null) {
            return viewCache;
        }
        view = new View(properties);
        StopWatch sw = new StopWatch("easy-doc");
        String fileName = Constant.CUR_PATH + "/src/main/java/" + properties.getPath().replaceAll("\\.", "/");
        File file = new File(fileName);
        sw.start("analysis");
        List<File> fileList = new ArrayList<>();
        getFile(file, fileList);
        pathMapper(fileList);
        for (File aFileList : fileList) {
            singleReader(aFileList);
        }
        sw.stop();
        System.out.println(sw.prettyPrint());
        // 缓存
        viewCache = view;
        return view;
    }

    public View multiReader(String str) {
        if (viewCache != null) {
            return viewCache;
        }

        return view;
    }

    /**
     * 将所有文件构造出映射关系
     */
    private void pathMapper(List<File> fileList) {
        fileList.forEach(file -> {
            NamePack namePack = path2Pack(file.getPath());
            Constant.PATH_MAP.put(namePack.getName(), namePack.getPackPath());
        });
    }

    /**
     * 将文件路径转化为类名:包路径的映射
     */
    private NamePack path2Pack(String path) {
        String temp = path.replaceAll("/", ".");
        String packPath = temp.substring(temp.indexOf(properties.getPath()));
        packPath = packPath.substring(0, packPath.lastIndexOf("."));
        return new NamePack(packPath.substring(packPath.lastIndexOf(".") + 1), packPath);
    }

    /**
     * 单文件读取匹配，子类实现
     */
    abstract void singleReader(File file);

    abstract void singleReader(String str);

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
    String readFile(File file) {
        try {
            // 此处设置编码，解决乱码问题
            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(fileReader);
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

    /**
     * 渲染controller，method，model，部分字段通过反射进行读取
     */
    void render(Controller controller, Map<String, String> map,
                List<Param> paramList, List<Param> fieldList,
                List<Response> responseList, Map<String, String> throwsMap,
                View view, Model model) {
        // 填充controller，method，model
        if (map.size() > 0) {
            if (map.containsKey(Constant.CONTROLLER)) {
                map.put(Constant.PATH, reflectUtils.getControllerPath(map.get(Constant.CONTROLLER)));
                map.put(Constant.DEPRECATED, String.valueOf(reflectUtils.isDeprecated(map.get(Constant.CONTROLLER), "")));
                renderController(controller, map, view);
            } else if (map.containsKey(Constant.METHOD)) {
                RequestMapping mapping = reflectUtils.getMethodMapping(controller.getName(), map.get(Constant.METHOD));
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
                map.put(Constant.DEPRECATED, String.valueOf(reflectUtils.isDeprecated(controller.getName(), map.get(Constant.METHOD))));
                renderMethod(controller, map, paramList, responseList, throwsMap, reflectUtils.getBody(controller.getName(), map.get(Constant.METHOD), view));
            } else if (map.containsKey(Constant.MODEL)) {
                map.put(Constant.DEPRECATED, String.valueOf(reflectUtils.isDeprecated(map.get(Constant.MODEL), "")));
                renderModel(model, map, fieldList, view);
            }
        }
    }

    /**
     * 渲染controller，因为需要继续被使用，所以不能使用Builder
     */
    private void renderController(Controller controller, Map<String, String> map, View view) {
        controller.setAuthor(map.getOrDefault(Constant.AUTHOR, ""));
        controller.setDescription(map.getOrDefault(Constant.DESCRIPTION, ""));
        controller.setName(map.getOrDefault(Constant.CONTROLLER, ""));
        controller.setPath(map.getOrDefault(Constant.PATH, ""));
        controller.setDeprecated(Boolean.valueOf(map.getOrDefault(Constant.DEPRECATED, "")));
        view.getControllerList().add(controller);
    }

    /**
     * 渲染method，需要对param进行渲染
     */
    private void renderMethod(Controller controller, Map<String, String> map,
                              List<Param> paramList, List<Response> responseList,
                              Map<String, String> throwsMap, Model body) {
        renderParamList(controller.getName(), map.getOrDefault(Constant.METHOD, ""), paramList);
        renderResponseList(responseList);
        Method method = new Method().toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .path(map.getOrDefault(Constant.PATH, ""))
                .type(map.getOrDefault(Constant.TYPE, ""))
                .body(body)
                .paramList(paramList)
                .responseList(responseList)
                .throwsMap(throwsMap)
                .deprecated(Boolean.valueOf(map.getOrDefault(Constant.DEPRECATED, "")))
                .build();
        controller.getMethodList().add(method);
    }

    private void renderResponseList(List<Response> responseList) {
        responseList.forEach(response -> {
            for (Field field : response.getFieldList()) {
                if (Constant.PATH_MAP.containsKey(field.getName())) {
                    try {
                        field.setData(reflectUtils.path2Class(field.getName()).newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 渲染model，需要渲染field，并且存入body
     */
    private void renderModel(Model model, Map<String, String> map, List<Param> fieldList, View view) {
        // todo：对象的嵌套待解决
        renderModelField(map.getOrDefault(Constant.MODEL, ""), fieldList);
        model = model.toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .name(map.getOrDefault(Constant.MODEL, ""))
                .author(map.getOrDefault(Constant.AUTHOR, ""))
                .deprecated(Boolean.valueOf(map.getOrDefault(Constant.DEPRECATED, "")))
                .fieldList(fieldList)
                .build();
        view.getModelList().add(model);
        // 渲染body
        List<Controller> controllerList = view.getControllerList();
        if (controllerList.size() > 0) {
            for (Controller controller : controllerList) {
                List<Method> methodList = controller.getMethodList();
                for (Method method : methodList) {
                    if (method.getBody() != null) {
                        // 当body还未解析时，存入model
                        if (model.getName().equals(method.getBody().getName())) {
                            if (StringUtils.isEmpty(method.getBody().getDescription())) {
                                method.setBody(model);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 渲染param
     */
    private void renderParamList(String controller, String method, List<Param> paramList) {
        Map<String, Param> params = reflectUtils.getParams(controller, method);
        paramList.forEach(param -> {
            Param temp = params.get(param.getName());
            param.setType(trans2JS(temp.getType()));
            param.setRequired(temp.isRequired());
            param.setDefaultValue(temp.getDefaultValue());
        });

    }

    /**
     * 将java类型转化为js类型
     */
    private String trans2JS(String type) {
        if (StringUtils.isEmpty(type)) {
            return "";
        }
        switch (type) {
            case "java.lang.String":
                return "String";
            case "java.lang.Double":
            case "java.lang.Float":
            case "double":
            case "float":
                return "Double";
            case "java.lang.Integer":
            case "int":
                return "Integer";
            case "java.lang.Boolean":
            case "boolean":
                return "Boolean";
            case "javax.servlet.http.HttpServletRequest":
            case "javax.servlet.http.HttpServletResponse":
                return "String";
            case "java.util.Map":
            case "java.util.List":
            case "java.lang.Object":
                return "Object";
            default:
                return "Object";
        }
    }

    /**
     * 渲染field
     */
    private void renderModelField(String modelName, List<Param> fieldList) {
        Map<String, String> fieldMap = reflectUtils.getField(modelName);
        fieldList.forEach(field -> field.setType(trans2JS(fieldMap.getOrDefault(field.getName(), ""))));
    }
}
