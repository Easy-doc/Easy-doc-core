package com.stalary.easydoc.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * IpConfiguration
 *
 * @author stalary
 */
@Component
public class IpConfiguration {

    private int port;

    @Autowired
    Environment environment;

    @PostConstruct
    public void init() {
        String property = environment.getProperty("server.port");
        if (StringUtils.isEmpty(property)) {
            // 默认设置为8080
            this.port = 8080;
        } else {
            this.port = Integer.parseInt(property);
        }
    }

    public int getPort() {
        return this.port;
    }
}
