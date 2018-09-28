package com.stalary.easydoc.readers;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    /** view缓存 **/
    View viewCache = null;

    /** 获取当前路径 **/
    final String CUR_PATH = System.getProperty("user.dir");

    private EasyDocProperties properties;

    public BaseReader(EasyDocProperties properties) {
        this.properties = properties;
    }

    /**
     * 批量读取文件
     * @return View
     */
    public View multiReader() {
        if (viewCache != null) {
            return viewCache;
        }
        View view = new View(properties);
        StopWatch sw = new StopWatch("easy-doc");
        File file = new File(CUR_PATH + "/src/main/java/" + properties.getPath().replaceAll("\\.", "/"));
        System.out.println(file.getPath());
        sw.start("task");
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

    abstract View singleReader(File file);

    void getFile(File file, List<File> fileList) {
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

    String readFile(File file) {
        try {
            FileReader fileReader = new FileReader(file);
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

    private void renderController(Controller controller, Map<String, String> map, View view) {
        controller = controller.toBuilder()
                .author(map.getOrDefault(Constant.AUTHOR, ""))
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .name(map.getOrDefault(Constant.CONTROLLER, ""))
                .path(map.getOrDefault(Constant.PATH, ""))
                .build();
        view.getControllerList().add(controller);
    }

    private void renderMethod(Controller controller, Map<String, String> map, Map<String, String> paramMap, Map<String, String> returnMap, Map<String, String> bodyMap) {
        // 其次遍历存储method
        Method method = new Method().toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .name(map.getOrDefault(Constant.METHOD, ""))
                .path(map.getOrDefault(Constant.PATH, ""))
                .body(bodyMap)
                .paramMap(paramMap)
                .returnMap(returnMap)
                .build();
        controller.getMethodList().add(method);
    }

    private void renderModel(Model model, Map<String, String> map, Map<String, String> fieldMap, View view) {
        model = model.toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .fieldMap(fieldMap)
                .name(map.getOrDefault(Constant.MODEL, ""))
                .build();
        view.getModelList().add(model);
    }

    void render(Controller controller, Map<String, String> map,
                Map<String, String> paramMap, Map<String, String> fieldMap,
                Map<String, String> returnMap, Map<String, String> bodyMap,
                View view, Model model) {
        // 填充controller，method，model
        if (map.containsKey(Constant.CONTROLLER)) {
            renderController(controller, map, view);
        } else if (map.containsKey(Constant.METHOD)) {
            renderMethod(controller, map, paramMap, returnMap, bodyMap);
        } else if (map.containsKey(Constant.MODEL)) {
            renderModel(model, map, fieldMap, view);
        }
    }
}
