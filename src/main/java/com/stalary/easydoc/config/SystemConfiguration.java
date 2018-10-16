package com.stalary.easydoc.config;


import org.springframework.stereotype.Component;

@Component
public class SystemConfiguration {

    private int systemType;

    public SystemConfiguration() {
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
           this.systemType = 1;
        }else{
            this.systemType = 0;
        }

    }

    public int getSystemType() {
        return this.systemType;
    }
}
