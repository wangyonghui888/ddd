package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import lombok.Data;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.vo
 * @Description :  TODO
 * @Date: 2020-09-10 16:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsQuotaBusinessLimitVo {
    /**
     * 数据
     */
    public List<RcsQuotaBusinessLimit> list;
    /**
     * 一共多少页
     */
    public Long pages;
    /**
     * 当前是第几页
     */
    public Integer current;
    /**
     * 每一页大小
     */
    public Integer size;
    /**
     * 总页数
     */
    public Integer total;

}
