/**
 * @(#)TestBody.java, 2018-10-08.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @model TestBody
 * @field params 参数
 * @field body post方法的body
 * @author lirongqian
 * @since 2018/10/08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestBody {

    private Map<String, String> params;

    private Object body;
}