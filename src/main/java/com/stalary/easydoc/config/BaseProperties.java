/*
 * @(#)EasyDocProperties.java, 2020-01-19.
 * <p>
 * Copyright 2020 Stalary.
 */
package com.stalary.easydoc.config;

import lombok.Data;

/**
 * BaseProperties
 *
 * @author lirongqian
 * @since 2020/01/19
 */
@Data
public class BaseProperties {

    /** 项目名称 **/
    private String name;

    /** 项目介绍 **/
    private String description;

    /** 联系人 **/
    private String contact;
}