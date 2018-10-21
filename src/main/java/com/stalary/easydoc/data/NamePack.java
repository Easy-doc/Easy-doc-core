/**
 * @(#)NamePack.java, 2018-10-06.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @model NamePack
 * @description 类和包的映射
 * @field name 类名
 * @field packPath 包路径
 * @author lirongqian
 * @since 2018/10/06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePack {

    /** 类名 **/
    private String name;

    /** 包路径 **/
    private String packPath;
}