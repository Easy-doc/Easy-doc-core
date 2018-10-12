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
 * @author lirongqian
 */
import lombok.Data;

@Data
public class User {

    private String name;

    private int age;
}