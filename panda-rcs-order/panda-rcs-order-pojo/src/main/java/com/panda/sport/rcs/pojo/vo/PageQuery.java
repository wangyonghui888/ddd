package com.panda.sport.rcs.pojo.vo;

import lombok.Data;

/**
 * 分页查询条件基类
 */
@Data
public class PageQuery {

    /**
     * 当前页
     */
    private Integer currentPage = 1;
    /**
     * 每页显示条数
     */
    private Integer pageSize = 10;
}
