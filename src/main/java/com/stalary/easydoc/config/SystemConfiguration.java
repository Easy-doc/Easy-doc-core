package com.stalary.easydoc.config;


import com.stalary.easydoc.data.Constant;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 获取当前系统
 * @author zhangxiaochuan
 */
@Component
public class SystemConfiguration {

    @Getter
    private boolean windows = false;

    public SystemConfiguration() {
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith(Constant.WINDOWS)){
           this.windows = true;
        }
    }
}
