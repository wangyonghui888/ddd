package com.panda.sport.rcs.wrapper.credit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网玩法限额
 * @Author : Paca
 * @Date : 2021-04-30 19:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsCreditSinglePlayLimitService extends IService<RcsCreditSinglePlayLimit> {

    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(List<RcsCreditSinglePlayLimit> list);

    /**
     * 查询玩法限额
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
    List<RcsCreditSinglePlayLimit> querySinglePlayLimit(Long merchantId, String creditId, Long userId);

    /**
     * 查询玩法限额
     * <li>merchantId = 0   && creditId = -1  && userId = 0   查询信用代理默认限额</li>
     * <li>merchantId = 0   && creditId = 0   && userId = 0   查询信用代理最大限额</li>
     * <li>merchantId = mid && creditId = cid && userId = 0   查询信用代理限额</li>
     *
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
    List<RcsCreditSinglePlayLimit> querySinglePlayLimit(Long merchantId, String creditId, Long userId, Integer sportId, Integer playClassify, String betStage);
}
