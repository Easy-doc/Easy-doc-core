/**
 * @(#)Response.java, 2018-10-10.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Response
 *
 * @author lirongqian
 * @since 2018/10/10
 */
@Data
public class Response {

    /** 返回code **/
    private int code;

    /** 描述 **/
    private String description;

    /** 返回的参数 **/
    private List<Model> fieldList = new ArrayList<>();

    public Response(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Response(int code, String description, Map<String, String> fieldMap) {
        this.code = code;
        this.description = description;
        fieldMap.forEach((k, v) -> fieldList.add(new Model(k, v)));
    }

    public Response() {
    }
}