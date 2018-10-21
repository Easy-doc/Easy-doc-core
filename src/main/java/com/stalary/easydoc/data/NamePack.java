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
 *
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