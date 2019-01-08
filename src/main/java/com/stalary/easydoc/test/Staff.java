/**
 * @(#)Staff.java, 2018-11-13.
 *
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.test;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class Staff extends User {

    private String grade;

    private Integer salary;
}