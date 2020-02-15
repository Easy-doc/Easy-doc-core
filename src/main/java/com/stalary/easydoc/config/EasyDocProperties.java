/*
 * @(#)EasyDocProperties.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyDocProperties
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(prefix = "com.stalary.easydoc")
public class EasyDocProperties extends BaseProperties {

    /** 需要扫描的文件路径 **/
    private String path;

    /** 不包含的文件名称 **/
    private List<String> excludeFile = new ArrayList<>();

    /** 包含的文件名称(默认指定路径下都包含) **/
    private List<String> includeFile = new ArrayList<>();

    /** easy-doc开关，默认开启 **/
    private boolean open = true;

    /** 是否使用源文件解析，默认使用 **/
    private boolean source = true;

    /** 是否开启权限验证，默认关闭 **/
    private boolean auth = false;

    /** 权限配置 **/
    private Auth authConfig;

    /** 是否为网关文档 **/
    private boolean gateway = false;

    /** 网关文档配置 **/
    private Gateway gatewayConfig;

    @Data
    public static class Auth {

        /** 账号,默认为admin **/
        private String account = "admin";

        /** 密码,默认为admin **/
        private String password = "admin";
    }

    @Data
    public static class Gateway {

        /** 网关包含的服务列表 **/
        private List<Service> serviceList;
    }

    @Data
    public static class Service {

        /** 服务名称 **/
        private String name;

        /** 服务地址 **/
        private String url;

        /** 是否存在文档,默认存在 **/
        private boolean doc = true;

        /** 服务是否开启了权限认证 **/
        private boolean auth = false;
    }

}