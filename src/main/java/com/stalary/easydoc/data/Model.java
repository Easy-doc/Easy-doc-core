/**
 * @(#)Model.java, 2018-09-26.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @model ModelData
 *
 * @author lirongqian
 * @since 2018/09/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Model {

    /** 名称 **/
    private String name;

    /** 作者 **/
    private String author;

    /** 描述 **/
    private String description;

    /** 参数和对应的解释 **/
    private List<Param> fieldList;

    /** 代表实体是否已经被弃置 **/
    @Builder.Default
    private boolean deprecated = false;
}