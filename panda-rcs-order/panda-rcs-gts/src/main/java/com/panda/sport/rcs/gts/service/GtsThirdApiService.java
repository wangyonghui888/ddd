package com.panda.sport.rcs.gts.service;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.gts.vo.GtsBetResultVo;
import com.panda.sport.rcs.gts.vo.GtsExtendBean;

import java.util.List;

/**
 * gtsapi交互
 * @author  lithan
 * @date 2022-12-30 15:41:46
 */
public interface GtsThirdApiService {

    /**
     * 获取额度
     * @param extendBeanList
     */
    public Long getLimit(List<ExtendBean> extendBeanList, Integer seriesType);

    /**
     * 下注 评估
     *
     * @param extendBeanList
     * @param  seriesType 单关串关
     */
    public GtsBetResultVo gtsAssessmentBet(List<GtsExtendBean> extendBeanList, Long totalMoney, Integer seriesType);

    /**
     * 下注 结果确认
     *
     * @param extendBeanList
     * @param seriesType     单关串关
     */
    public String gtsReceiveBet(List<GtsExtendBean> extendBeanList, Long totalMoney, Integer seriesType, String status);

}
