package com.panda.sport.rcs.pojo.vo;

import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import lombok.Data;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo.vo
 * @Description :  TODO
 * @Date: 2020-09-05 17:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsQuotaMerchantSingleFieldLimitVo {
    /**
     * 赔付限额基础值
     */
    private Long compensationLimitBase;
    /**
     * 数据
     */
    List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList;

    /**
     * 操作人IP
     */
    private String ip;
}
