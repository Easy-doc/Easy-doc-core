/**
 * @(#)User.java, 2018-09-26.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.test;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private String name;

    private int age;

    private List<User> userList;
}