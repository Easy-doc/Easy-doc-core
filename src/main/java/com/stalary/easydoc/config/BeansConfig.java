/**
 * @(#)BeansConfig.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.config;

import com.stalary.easydoc.core.DocHandler;
import com.stalary.easydoc.core.DocReader;
import com.stalary.easydoc.core.DocRender;
import com.stalary.easydoc.core.ReflectUtils;
import com.stalary.easydoc.endpoint.ResourceController;
import com.stalary.easydoc.service.ResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnMissingBean
    public DocReader docReader(EasyDocProperties properties) {
        return new DocReader(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DocHandler docHandler() {
        return new DocHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public DocRender docRender() {
        return new DocRender();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceService resourceService(EasyDocProperties properties) {
        return new ResourceService(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReflectUtils reflectUtils() {
        return new ReflectUtils();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceController resourceController(EasyDocProperties properties) {
        return new ResourceController(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SystemConfiguration systemConfiguration() {
        return new SystemConfiguration();
    }
}
