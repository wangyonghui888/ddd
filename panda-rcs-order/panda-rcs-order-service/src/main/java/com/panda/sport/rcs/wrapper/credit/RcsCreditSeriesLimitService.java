package com.panda.sport.rcs.wrapper.credit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSeriesLimit;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网串关限额
 * @Author : Paca
 * @Date : 2021-04-30 19:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsCreditSeriesLimitService extends IService<RcsCreditSeriesLimit> {

    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(List<RcsCreditSeriesLimit> list);

    /**
     * 查询串关限额
     * <li>merchantId = 0   && creditId = -1  && userId = 0   查询信用代理默认限额</li>
     * <li>merchantId = 0   && creditId = 0   && userId = 0   查询信用代理最大限额</li>
     * <li>merchantId = mid && creditId = cid && userId = 0   查询信用代理限额</li>
     *
     * <li>merchantId = 0   && creditId = -1  && userId = -1  查询用户通用默认限额</li>
     * <li>merchantId = 0   && creditId = 0   && userId = -1  查询用户通用最大限额</li>
     * <li>merchantId = mid && creditId = cid && userId = -1  查询用户通用限额</li>
     * <li>merchantId = mid && creditId = cid && userId = uid 查询用户限额</li>
     *
     * @param merchantId 商户ID
     * @param creditId   信用代理ID
     * @param userId     用户ID
     * @return
     */
    List<RcsCreditSeriesLimit> querySeriesLimit(Long merchantId, String creditId, Long userId);

    Long getMerchantIdByCreditId(String creidtId);
}
