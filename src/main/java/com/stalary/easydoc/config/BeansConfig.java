/**
 * @(#)BeansConfig.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.config;

import com.stalary.easydoc.readers.Reader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BeansConfig
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@Configuration
@EnableConfigurationProperties(EasyDocProperties.class)
public class BeansConfig {

    @Bean
    public Reader reader(EasyDocProperties easyDocProperties) {
        return new Reader(easyDocProperties);
    }
}