/**
 * @(#)Param.java, 2018-10-10.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.Data;

/**
 * Param
 * @author lirongqian
 * @since 2018/10/10
 */
@Data
public class Param {

    /** 名称 **/
    private String name;

    /** 描述 **/
    private String description;

    /** 类型 **/
    private String type;

    /** 是否必传，默认为true **/
    private boolean required = true;

    /** 默认值 **/
    private String defaultValue;

    public Param(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Param(String type, boolean required, String defaultValue) {
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public Param() {
    }
}