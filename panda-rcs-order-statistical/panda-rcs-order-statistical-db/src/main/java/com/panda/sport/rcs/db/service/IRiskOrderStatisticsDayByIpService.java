package com.panda.sport.rcs.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.common.vo.api.response.ListByVisitInfoResVo;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsDayByIp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 根据IP分组当日内的总投注额与总输赢额 服务类
 * </p>
 *
 * @author author
 * @since 2021-02-03
 */
public interface IRiskOrderStatisticsDayByIpService extends IService<RiskOrderStatisticsDayByIp> {

    /**
     * @Description 根据时间查询投注数据
     * @Param RiskOrderStatisticsDayByIp
     * @Author Kir
     * @Date  2021-01-29
     * @return List<ListByGroupAndUserNumResVo>
     **/
    List<RiskOrderStatisticsByIp> queryProductAmountTotal(Long startTime, Long endTime);

    /**
     * @Description 根据时间查询结算数据
     * @Param RiskOrderStatisticsDayByIp
     * @Author Kir
     * @Date  2021-01-29
     * @return List<ListByGroupAndUserNumResVo>
     **/
    List<RiskOrderStatisticsByIp> queryProfitAmount(Long startTime, Long endTime);

    /**
     * @Description 根据IP查询IP对应的基本信息
     * @Param ips
     * @Author Kir
     * @Date  2021-01-29
     * @return List<ListByVisitInfoResVo>
     **/
    List<ListByVisitInfoResVo> queryVisitByIp(List<String> ips);

    /**
     * @Description 以开始时间与结束时间为条件根据IP统计订单数据
     * @Param startStamp,endTime,s,type
     * @Author Kir
     * @Date  2021-01-29
     **/
    void staticsOrderForIp(Long startTime, Long endTime, String s, int type);

    /**
     * 根据IP和统计时间修改数据
     * @param riskOrderStatisticsDayByIp
     */
    void updateByIpAndStaticTime(RiskOrderStatisticsDayByIp riskOrderStatisticsDayByIp);
}
