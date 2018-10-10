/**
 * @(#)BeansConfig.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.config;

import com.stalary.easydoc.readers.DocReader;
import com.stalary.easydoc.readers.ReflectUtils;
import com.stalary.easydoc.readers.XmlReader;
import com.stalary.easydoc.web.ResourceController;
import com.stalary.easydoc.web.ResourceService;
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
    public XmlReader xmlReader(EasyDocProperties properties) {
        return new XmlReader(properties);
    }

    @Bean
    public DocReader docReader(EasyDocProperties properties) {
        return new DocReader(properties);
    }

    @Bean
    public ResourceService resourceService(EasyDocProperties properties) {
        return new ResourceService(properties);
    }

    @Bean
    public ReflectUtils reflectReader() {
        return new ReflectUtils();
    }

    @Bean
    public ResourceController resourceController() {
        return new ResourceController();
    }

    @Bean
    public IpConfiguration ipConfiguration() {
        return new IpConfiguration();
    }

    @Bean
    public TimeAdvice timeAdvice() {
        return new TimeAdvice();
    }
}