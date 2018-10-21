/**
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
 * @model View
 * @description 展示对象
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

    public View(EasyDocProperties properties) {
        this.controllerList = new ArrayList<>();
        this.modelList = new ArrayList<>();
        this.name = properties.getName();
        this.description = properties.getDescription();
        this.contact = properties.getContact();
    }

    public View() {
        this.controllerList = new ArrayList<>();
        this.modelList = new ArrayList<>();
    }
}