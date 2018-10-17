package com.stalary.easydoc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * IpConfiguration
 * @author stalary
 */
@Component
public class IpConfiguration {

    private int port;

    @Autowired
    Environment environment;

    public void onApplicationEvent() {
        this.port  = Integer.parseInt(Objects.requireNonNull(environment.getProperty("local.server.port")));
    }

    public int getPort() {
        return this.port;
    }
}
