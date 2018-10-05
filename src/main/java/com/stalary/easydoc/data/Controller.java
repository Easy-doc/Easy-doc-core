/**
 * @(#)Controller.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller
 * 接口
 * @author lirongqian
 * @since 2018/09/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Controller {

    /** 接口名称 **/
    private String name;

    /** 作者 **/
    private String author;

    /** 接口介绍 **/
    private String description;

    /** 接口路径 **/
    private String path;

    /** 接口中的方法 **/
    private List<Method> methodList = new ArrayList<>();

    /** 输入对象 **/
    private Model input;

    /** 输出对象 **/
    private Model output;
}