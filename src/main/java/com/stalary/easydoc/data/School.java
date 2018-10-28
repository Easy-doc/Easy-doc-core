/**
 * @(#)School.java, 2018-10-28.
 * <p>
 * Copyright 2018 Youdao, Inc. All rights reserved.
 * YOUDAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.stalary.easydoc.data;

import com.stalary.easydoc.test.User;

import java.util.List;

/**
 * @model School
 * @description 学校
 * @field name 名称
 * @field students 学生
 * @author lirongqian
 * @since 2018/10/28
 */
public class School {

    private String name;

    private List<User> students;
}