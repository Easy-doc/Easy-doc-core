/**
 * @(#)ReflectReader.java, 2018-09-28.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.readers;

import com.stalary.easydoc.config.EasyDocProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ReflectReader
 *
 * @author lirongqian
 * @since 2018/09/28
 */
@Slf4j
@Component
public class ReflectReader {

    private EasyDocProperties properties;

    public ReflectReader(EasyDocProperties properties) {
        this.properties = properties;
    }

    public boolean isController(String name) {
        try {
            System.out.println(name);
            return true;
        } catch (Exception e) {
            log.warn("reflect error!", e);
        }
        return false;
    }
}