/**
 * @(#)TestResponse.java, 2018-10-02.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc.data;

import com.stalary.easydoc.annotation.ModelData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TestResponse
 * @description 压力测试返回对象
 * @field slowTime 最慢返回时间
 * @field fastTime 最快返回时间
 * @field avgTime 平均返回时间
 * @field qps qps
 * @author lirongqian
 * @since 2018/10/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelData
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