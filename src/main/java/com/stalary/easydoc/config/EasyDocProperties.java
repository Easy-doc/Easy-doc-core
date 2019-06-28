/**
 * @(#)EasyDocProperties.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

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

    /** 不包含的文件名称 **/
    private List<String> excludeFile = new ArrayList<>();

    /** 包含的文件名称(默认指定路径下都包含) **/
    private List<String> includeFile = new ArrayList<>();

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

    /** 是否开启权限验证，默认关闭 **/
    private boolean auth = false;

    /** 权限配置 **/
    private Auth authConfig;

    @Data
    public static class Auth {

        /** 账号,默认为admin **/
        private String account = "admin";

        /** 密码,默认为admin **/
        private String password = "admin";
    }
}