package com.panda.sport.rcs.wrapper.credit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSingleMatchLimit;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网单场赛事限额
 * @Author : Paca
 * @Date : 2021-04-30 19:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsCreditSingleMatchLimitService extends IService<RcsCreditSingleMatchLimit> {

    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(List<RcsCreditSingleMatchLimit> list);

    /**
     * 查询单场限额配置
     * <li>merchantId = 0   && creditId = -1    查询信用代理默认限额</li>
     * <li>merchantId = 0   && creditId = 0     查询信用代理最大限额</li>
     * <li>merchantId = mid && creditId = cid   查询信用代理限额</li>
     *
     * @param merchantId
     * @param creditId
     * @return
     */
    List<RcsCreditSingleMatchLimit> querySingleMatchLimit(Long merchantId, String creditId);

    /**
     * 查询单场限额配置
     * <li>merchantId = 0   && creditId = -1    查询信用代理默认限额</li>
     * <li>merchantId = 0   && creditId = 0     查询信用代理最大限额</li>
     * <li>merchantId = mid && creditId = cid   查询信用代理限额</li>
     *
     * @param merchantId
     * @param creditId
     * @param sportId
     * @return
     */
    List<RcsCreditSingleMatchLimit> querySingleMatchLimit(Long merchantId, String creditId, Integer sportId);
}
