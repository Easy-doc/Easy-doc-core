/**
 * @(#)TestResponse.java, 2018-10-02.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TestResponse
 *
 * @author lirongqian
 * @since 2018/10/02
 */
@Data
@AllArgsConstructor
public class TestResponse {

    /** 最慢时间 **/
    private int slowTime;

    /** 最快时间 **/
    private int fastTime;

    /** 总耗时 **/
    private int totalTime;

    /** 平均时间 **/
    private int avgTime;

    private int qps;
}