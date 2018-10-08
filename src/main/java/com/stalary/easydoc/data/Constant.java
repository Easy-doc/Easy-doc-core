/**
 * @(#)Constant.java, 2018-09-26.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Constant
 *
 * @author lirongqian
 * @since 2018/09/26
 */
public class Constant {

    public static final String CONTROLLER = "controller";

    public static final String METHOD = "method";

    public static final String FIELD = "field";

    public static final String PARAM = "param";

    public static final String RETURN = "return";

    public static final String MODEL = "model";

    public static final String MODEL_TAG = "@model";

    public static final String AUTHOR = "author";

    public static final String DESCRIPTION = "description";

    public static final String PATH = "path";

    public static final String TYPE = "type";

    public static final String XML = "xml";

    public static final String DOC = "doc";

    public static final String SPLIT = ":";

    public static final String HTTP = "http://";

    public static final String OUTPUT = "output";

    /** 获取当前路径 **/
    public static final String CUR_PATH = System.getProperty("user.dir");

    /** 类名和路径的映射 **/
    public static final Map<String, String> pathMap = new HashMap<>();
}