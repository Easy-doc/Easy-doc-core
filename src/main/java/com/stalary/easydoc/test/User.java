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
 */
import lombok.Data;

/**
 * <model>User</model>
 * <description>用户对象</description>
 * <field>
 *     <name>用户名</name>
 *     <age>年龄</age>
 * </field>
 */
@Data
public class User {

    private String name;

    private Integer age;
}