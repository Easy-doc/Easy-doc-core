/**
 * @(#)User.java, 2018-09-26.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.test;

/**
 * @model User
 * @description 用户对象
 * @field name 姓名
 * @field age 年龄
 * @field userList 用户列表
 * @author lirongqian
 */

import lombok.Data;

import java.util.List;

@Data
@Deprecated
public class User {

    private String name;

    private int age;

    private List<User> userList;
}