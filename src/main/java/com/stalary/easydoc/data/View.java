/**
 * @(#)View.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * View
 * 展示对象
 * @author lirongqian
 * @since 2018/09/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class View {

    /** 所有接口 **/
    private List<Controller> controllerList = new ArrayList<>();

    /** 所有返回对象 **/
    private List<Model> modelList = new ArrayList<>();

    /**
     * 添加view
     * @param view
     */
    public void addView(View view) {
        this.getControllerList().addAll(this.getModelList().size(), view.getControllerList());
        this.getModelList().addAll(view.getModelList());
    }
}