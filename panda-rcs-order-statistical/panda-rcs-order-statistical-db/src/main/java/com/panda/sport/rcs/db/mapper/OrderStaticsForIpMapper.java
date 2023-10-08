package com.panda.sport.rcs.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.vo.api.request.IpListReqVo;
import com.panda.sport.rcs.common.vo.api.response.IpListResVo;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 根据IP分组统计用户订单 Mapper 接口
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
public interface OrderStaticsForIpMapper extends BaseMapper<RiskOrderStatisticsByIp> {

    /**
     * 查询总条数
     * @param staticBeginTime
     * @param staticEndTime
     * @param finalBeginTime
     * @param finalEndTime
     * @return
     */
    Integer queryAmountByFinalBetTimeCount(@Param("staticBeginTime") Long staticBeginTime, @Param("staticEndTime") Long staticEndTime, @Param("finalBeginTime") Long finalBeginTime, @Param("finalEndTime") Long finalEndTime);

    /**
     * @Description 每小时扫描总表8天内所有投注过得IP，根据IP将7天内每日数据表存在的数据总金额累加到总表
     * @Author Kir
     * @Date  2021-01-29
     * @return List<RiskOrderStatisticsByIp>
     **/
    List<RiskOrderStatisticsByIp> queryAmountByFinalBetTime(@Param("staticBeginTime") Long staticBeginTime, @Param("staticEndTime") Long staticEndTime, @Param("finalBeginTime") Long finalBeginTime, @Param("finalEndTime") Long finalEndTime, @Param("num") Integer num);

    /**
     * 根据IP修改数据
     * @param riskOrderStatisticsByIp
     */
    void updateByIp(@Param("riskOrderStatisticsByIp") RiskOrderStatisticsByIp riskOrderStatisticsByIp);

    /**
     * 根据条件查询统计总表数据集合
     * @param vo
     * @return IPage<IpListResVo>
     */
    IPage<IpListResVo> queryIpList(Page<IpListResVo> page, @Param("vo") IpListReqVo vo, @Param("ips") List<String> ips);
}
