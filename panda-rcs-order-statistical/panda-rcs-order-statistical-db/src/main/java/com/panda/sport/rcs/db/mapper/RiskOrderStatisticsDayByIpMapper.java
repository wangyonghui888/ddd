package com.panda.sport.rcs.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.common.vo.api.response.ListByVisitInfoResVo;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsDayByIp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 根据IP分组当日内的总投注额与总输赢额 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2021-02-03
 */
public interface RiskOrderStatisticsDayByIpMapper extends BaseMapper<RiskOrderStatisticsDayByIp> {
    
    /**
     * @Description 根据时间查询投注数据
     * @Param RiskOrderStatisticsDayByIp
     * @Author Kir
     * @Date  2021-01-29
     * @return List<ListByGroupAndUserNumResVo>
     **/
    List<RiskOrderStatisticsByIp> queryProductAmountTotal(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * @Description 根据时间查询结算数据
     * @Param RiskOrderStatisticsDayByIp
     * @Author Kir
     * @Date  2021-01-29
     * @return List<ListByGroupAndUserNumResVo>
     **/
    List<RiskOrderStatisticsByIp> queryProfitAmount(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * @Description 根据IP查询IP对应的基本信息
     * @Param ips
     * @Author Kir
     * @Date  2021-01-29
     * @return List<ListByVisitInfoResVo>
     **/
    List<ListByVisitInfoResVo> queryVisitByIp(@Param("ips") List<String> ips);

    /**
     * 根据IP和统计时间修改数据
     * @param riskOrderStatisticsDayByIp
     */
    void updateByIpAndStaticTime(@Param("riskOrderStatisticsDayByIp") RiskOrderStatisticsDayByIp riskOrderStatisticsDayByIp);
}
