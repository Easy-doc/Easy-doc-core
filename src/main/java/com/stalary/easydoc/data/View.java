/*
 * @(#)View.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import com.stalary.easydoc.config.EasyDocProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * View
 * @author lirongqian
 * @since 2018/09/25
 */
@Data
public class View {

    /** 项目名称 **/
    private String name;

    /** 项目介绍 **/
    private String description;

    /** 联系人 **/
    private String contact;

    /** 所有接口 **/
    private List<Controller> controllerList;

    /** 所有返回对象 **/
    private List<Model> modelList;

    /** 是否为网关文档 **/
    private boolean gateway;

    /** 子服务列表(网关) **/
    private List<EasyDocProperties.Service> serviceList;

    /** 总方法数量 **/
    private int totalMethod;

    /** 过时方法数量 **/
    private int deprecatedMethod;

    /** 总对象数量 **/
    private int totalModel;

    /** 过时对象数量 **/
    private int deprecatedModel;

    /** 总服务数量(网关) **/
    private int totalService;

    /** 存在文档的服务数量(网关) **/
    private int docService;

    public View(EasyDocProperties properties) {
        this.controllerList = new ArrayList<>();
        this.modelList = new ArrayList<>();
        this.name = properties.getName();
        this.description = properties.getDescription();
        this.contact = properties.getContact();
        this.gateway = properties.isGateway();
        if (properties.isGateway()) {
            this.serviceList = properties.getGatewayConfig().getServiceList();
            this.totalService = properties.getGatewayConfig().getServiceList().size();
            this.docService = (int) properties.getGatewayConfig().getServiceList().stream().filter(EasyDocProperties.Service::isDoc).count();
        }
    }

    public View() {
        this.controllerList = new ArrayList<>();
        this.modelList = new ArrayList<>();
    }
}