/**
 * @(#)EasyDocProperties.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * EasyDocProperties
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@Data
@ConfigurationProperties(prefix = "com.stalary.easydoc")
public class EasyDocProperties {

    /** 需要扫描的文件路径 **/
    private String path;

    /** 项目名称 **/
    private String name;

    /** 项目介绍 **/
    private String description;

    /** 联系人 **/
    private String contact;

    /** easy-doc开关，默认开启 **/
    private boolean open = true;

    /** 是否使用源文件解析，默认使用 **/
    private boolean source = true;
}