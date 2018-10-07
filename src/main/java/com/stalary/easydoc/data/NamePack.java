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
 * NamePack
 *
 * @author lirongqian
 * @since 2018/10/06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NamePack {

    private String name;

    private String packPath;
}