/**
 * @(#)Field.java, 2018-10-12.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.Data;

/**
 * @model Field
 * @description 字段
 * @field name 名称
 * @field description 描述
 * @field data 数据
 * @author lirongqian
 * @since 2018/10/12
 */
@Data
public class Field {

    private String name;

    private String description;

    private Object data;

    public Field(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Field() {
    }
}