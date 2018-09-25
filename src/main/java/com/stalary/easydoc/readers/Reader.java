package com.stalary.easydoc.readers;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.View;

/**
 * @author Stalary
 * @description
 * @date 2018/9/25
 */
public interface Reader {

    /**
     * 单文件读取
     * @param path 单文件路径
     * @return 返回读取出来的对象
     */
    View singleReader(String path);

    /**
     * 多文件读取
     * @param folder 文件夹路径
     * @return 返回读取出来的对象
     */
    View multiReader(String folder);

}
