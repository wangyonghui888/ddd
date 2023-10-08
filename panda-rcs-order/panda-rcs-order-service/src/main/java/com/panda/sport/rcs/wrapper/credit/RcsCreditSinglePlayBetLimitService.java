package com.panda.sport.rcs.wrapper.credit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.credit.RcsCreditSinglePlayBetLimit;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网玩法单注限额
 * @Author : Paca
 * @Date : 2021-07-17 18:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsCreditSinglePlayBetLimitService extends IService<RcsCreditSinglePlayBetLimit> {

    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(List<RcsCreditSinglePlayBetLimit> list);

    /**
     * 查询玩法限额
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
    List<RcsCreditSinglePlayBetLimit> querySinglePlayBetLimit(Long merchantId, String creditId, Long userId);

    /**
     * 查询玩法限额
     * <li>merchantId = 0   && creditId = -1  && userId = -1  查询用户通用默认限额</li>
     * <li>merchantId = 0   && creditId = 0   && userId = -1  查询用户通用最大限额</li>
     * <li>merchantId = mid && creditId = cid && userId = -1  查询用户通用限额</li>
     * <li>merchantId = mid && creditId = cid && userId = uid 查询用户限额</li>
     *
     * @param merchantId   商户ID
     * @param creditId     信用代理ID
     * @param userId       用户ID
     * @param sportId
     * @param playClassify
     * @param betStage
     * @return
     */
    List<RcsCreditSinglePlayBetLimit> querySinglePlayBetLimit(Long merchantId, String creditId, Long userId, Integer sportId, Integer playClassify, String betStage);
}
