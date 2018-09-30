/**
 * @(#)JavaDocTest.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.test;

import org.springframework.web.bind.annotation.*;

/**
 * JavaDocTest
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@RestController
@RequestMapping("/doc")
public class JavaDocTest {

    /**
     * @method test 测试方法
     * @param name 名称
     * @return hello
     */
    @GetMapping(value = "/test")
    public String test(String name) {
        return "hello " + name;
    }

    /**
     * @method user 用户测试
     * @param user
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public void user(
            @RequestBody User user) {
        System.out.println(user);
    }
}