/**
 * @(#)ResourceController.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ResourceController
 *
 * @author lirongqian
 * @since 2018/09/25
 */
@RestController
@RequestMapping("/easy-doc")
public class ResourceController {

    @GetMapping("/resource")
    public ResponseEntity<JSONObject> getResource() throws Exception {
        return new ResponseEntity<>(ReaderImpl.read(), HttpStatus.OK);
    }
}