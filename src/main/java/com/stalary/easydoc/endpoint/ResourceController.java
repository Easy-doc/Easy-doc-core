/**
 * @(#)ResourceController.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.endpoint;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.JsonResult;
import com.stalary.easydoc.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ResourceController
 * 资源获取controller
 * @author lirongqian
 * @since 2018/09/25
 */
@RestController
@RequestMapping(value = "/easy-doc")
@Slf4j
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", origins = "*")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    private EasyDocProperties props;

    public ResourceController(EasyDocProperties props) {
        this.props = props;
    }

    @GetMapping("/resource")
    public JSONObject getResource(
            @RequestParam(required = false, defaultValue = "") String account,
            @RequestParam(required = false, defaultValue = "") String password) {
        // 开启权限校验时校验账号密码
        if (props.isOpen() && props.isAuth()) {
            if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
                return JsonResult.failed("未登录");
            } else if (!props.getAuthConfig().getAccount().equals(account) || !props.getAuthConfig().getPassword().equals(password)) {
                return JsonResult.failed("账号密码错误");
            }
        }
        return JsonResult.ok(resourceService.read());
    }

}