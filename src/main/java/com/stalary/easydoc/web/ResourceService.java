/**
 * @(#)ResourceService.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.web;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.data.Constant;
import com.stalary.easydoc.data.View;
import com.stalary.easydoc.readers.DocReader;
import com.stalary.easydoc.readers.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ResourceService
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@Service
public class ResourceService {

    private EasyDocProperties properties;

    @Autowired
    DocReader docReader;

    @Autowired
    XmlReader xmlReader;

    public ResourceService(EasyDocProperties properties) {
        this.properties = properties;
    }

    public View read() {
        if (properties.isOpen()) {
            if (Constant.XML.equals(properties.getType())) {
                return xmlReader.multiReader();
            } else {
                return docReader.multiReader();
            }
        }
        return new View();
    }
}