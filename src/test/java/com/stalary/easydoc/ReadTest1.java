package com.stalary.easydoc;

/**
 * <controller>ReadTest1</controller>
 * <author>lirongqian</author>
 * <description>测试类1</description>
 */
public class ReadTest1 {

    /**
     * <method>test1</method>
     * <path>/test1</path>
     * <params>
     *     <name>名称</name>
     * </params>
     * <description>测试方法</description>
     */
    public void hello(String name) {
        System.out.println("hello " + name);
    }
}