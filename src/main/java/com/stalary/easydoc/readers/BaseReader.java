package com.stalary.easydoc.readers;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

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

    /** view缓存 **/
    private View viewCache = null;

    @Autowired
    ReflectUtils reflectUtils;

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
        String fileName = Constant.CUR_PATH + "/src/main/java/" + properties.getPath().replaceAll("\\.", "/");
        File file = new File(fileName);
        sw.start("analysis");
        List<File> fileList = new ArrayList<>();
        getFile(file, fileList);
        pathMapper(fileList);
        System.out.println(Constant.pathMap);
        for (File aFileList : fileList) {
            view.addView(singleReader(aFileList));
        }
        sw.stop();
        System.out.println(sw.prettyPrint());
        // 缓存
        viewCache = view;
        return view;
    }

    /**
     * 将所有文件构造出映射关系
     * @param fileList
     */
    private void pathMapper(List<File> fileList) {
        fileList.forEach(file -> {
            NamePack namePack = path2Pack(file.getPath());
            Constant.pathMap.put(namePack.getName(), namePack.getPackPath());
        });
    }

    /**
     * 将文件路径转化为类名:包路径的映射
     * @param path
     * @return
     */
    private NamePack path2Pack(String path) {
        String temp = path.replaceAll("/", ".");
        String packPath = temp.substring(temp.indexOf(properties.getPath()));
        packPath = packPath.substring(0, packPath.lastIndexOf("."));
        return new NamePack(packPath.substring(packPath.lastIndexOf(".") + 1), packPath);
    }

    abstract View singleReader(File file);

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

    private void renderController(Controller controller, Map<String, String> map, View view) {
        controller.setAuthor(map.getOrDefault(Constant.AUTHOR, ""));
        controller.setDescription(map.getOrDefault(Constant.DESCRIPTION, ""));
        controller.setName(map.getOrDefault(Constant.CONTROLLER, ""));
        controller.setPath(map.getOrDefault(Constant.PATH, ""));
        view.getControllerList().add(controller);
    }

    private void renderMethod(Controller controller, Map<String, String> map, Map<String, String> paramMap, Map<String, String> returnMap, Map<String, String> bodyMap, Model input) {
        // 其次遍历存储method
        Method method = new Method().toBuilder()
                .description(map.getOrDefault(Constant.DESCRIPTION, ""))
                .name(map.getOrDefault(Constant.METHOD, ""))
                .path(map.getOrDefault(Constant.PATH, ""))
                .type(map.getOrDefault(Constant.TYPE, ""))
                .body(bodyMap)
                .input(input)
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

    /**
     * 渲染controller，method，model，部分字段通过反射进行读取
     * @param controller controller
     * @param map 所有数据
     * @param paramMap 参数数据
     * @param fieldMap 属性数据
     * @param returnMap 返回参数数据
     * @param bodyMap post传递的body数据
     * @param view 前端展示对象
     * @param model model对象
     */
    void render(Controller controller, Map<String, String> map,
                Map<String, String> paramMap, Map<String, String> fieldMap,
                Map<String, String> returnMap, Map<String, String> bodyMap,
                View view, Model model) {
        // 填充controller，method，model
        if (map.containsKey(Constant.CONTROLLER)) {
            map.put(Constant.PATH, reflectUtils.getControllerPath(map.get(Constant.CONTROLLER)));
            renderController(controller, map, view);
        } else if (map.containsKey(Constant.METHOD)) {
            map.put(Constant.PATH, reflectUtils.getMethodPath(controller.getName(), map.get(Constant.METHOD)));
            map.put(Constant.TYPE, reflectUtils.getMethodType(controller.getName(), map.get(Constant.METHOD)));
            map.put(Constant.DESCRIPTION, map.get(map.get(Constant.METHOD)));
            // todo:model为后解析。。
            renderMethod(controller, map, paramMap, returnMap, bodyMap, reflectUtils.getBody(controller.getName(), map.get(Constant.METHOD), view));
        } else if (map.containsKey(Constant.MODEL)) {
            renderModel(model, map, fieldMap, view);
        }
    }
}
