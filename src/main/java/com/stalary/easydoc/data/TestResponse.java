/**
 * @(#)TestResponse.java, 2018-10-02.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TestResponse
 * @author lirongqian
 * @since 2018/10/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResponse {

    /** 最慢时间 **/
    private int slowTime;

    /** 最快时间 **/
    private int fastTime;

    /** 平均时间 **/
    private double avgTime;

    /** qps **/
    private double qps;
}