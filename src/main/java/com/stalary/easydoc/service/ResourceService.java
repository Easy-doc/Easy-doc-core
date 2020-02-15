/**
 * @(#)ResourceService.java, 2018-09-27.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.service;

import com.stalary.easydoc.config.EasyDocProperties;
import com.stalary.easydoc.core.DocReader;
import com.stalary.easydoc.data.View;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ResourceService
 *
 * @author lirongqian
 * @since 2018/09/27
 */
@Service
@Slf4j
public class ResourceService {

    private EasyDocProperties properties;

    @Autowired
    private DocReader docReader;

    public ResourceService(EasyDocProperties properties) {
        this.properties = properties;
    }

    public View read() {
        if (properties.isOpen()) {
            if (properties.isSource()) {
                return docReader.multiReader();
            } else {
                try (InputStream inputStream =
                         ClassUtils.class.getClassLoader().getResourceAsStream("easydoc.txt");
                     BufferedReader bf =
                         new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream),
                             StandardCharsets.UTF_8))) {
                    String temp;
                    StringBuilder sb = new StringBuilder();
                    while ((temp = bf.readLine()) != null) {
                        sb.append(temp);
                    }
                    return docReader.multiReader(sb.toString());
                } catch (Exception e) {
                    log.warn("easy-doc multiReader readTxt error", e);
                }
            }
        }
        return new View();
    }

}