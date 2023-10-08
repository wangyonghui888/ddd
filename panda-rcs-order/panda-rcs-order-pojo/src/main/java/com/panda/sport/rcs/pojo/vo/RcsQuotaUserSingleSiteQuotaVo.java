package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.vo
 * @Description :  TODO
 * @Date: 2020-09-06 11:13
 * @ModificationHistory Who    When    What
 * 用户单场限额页面
 */
@Data
public class RcsQuotaUserSingleSiteQuotaVo {
    /**
     * 用户单场限额
     */
    private List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaList;
    /**
     * 用户单场限额基础值
     */
    private Long userSingleSiteQuotaBase;

    /**
     * 操作人IP
     */
    private String ip;

}
