/**
 * @(#)ResourceController.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc;

import com.alibaba.fastjson.JSONObject;
import com.stalary.easydoc.data.JsonResult;
import com.stalary.easydoc.readers.ReaderImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ReaderImpl reader;

    @GetMapping("/resource")
    public JSONObject getResource() {
        return JsonResult.ok(reader.multiReader("/src/test/java/com/stalary/easydoc"));
    }
}