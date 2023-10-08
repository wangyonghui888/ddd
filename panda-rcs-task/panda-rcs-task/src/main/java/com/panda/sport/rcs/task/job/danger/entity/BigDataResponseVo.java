package com.panda.sport.rcs.task.job.danger.entity;

import lombok.Data;

import java.util.List;

/**
 * 调用大数据接口返回数据基础
 */
@Data
public class BigDataResponseVo<T> {

    /**
     * 返回码， 200为成功
     */
    private Integer code;

    private String msg;

    /**
     * 总条数
     */
    private Integer totalCount;

    /**
     * 当前页码
     */
    private Integer currentPage;

    private List<T> data;

    private String errorString;
}
