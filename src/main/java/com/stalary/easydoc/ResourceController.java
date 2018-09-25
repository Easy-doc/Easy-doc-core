/**
 * @(#)ResourceController.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc;

import com.stalary.easydoc.data.View;
import com.stalary.easydoc.readers.ReaderImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    ReaderImpl reader;

    @GetMapping("/resource")
    public ResponseEntity<View> getResource() throws Exception {
        return new ResponseEntity<>(reader.read(), HttpStatus.OK);
    }
}