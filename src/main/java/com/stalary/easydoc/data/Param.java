/**
 * @(#)Param.java, 2018-10-10.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Param
 *
 * @author lirongqian
 * @since 2018/10/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Param {

    /** 名称 **/
    private String name;

    /** 描述 **/
    private String description;

    /** 类型 **/
    private String type;

    /** 为引用其他对象 **/
    private Object ref;
}