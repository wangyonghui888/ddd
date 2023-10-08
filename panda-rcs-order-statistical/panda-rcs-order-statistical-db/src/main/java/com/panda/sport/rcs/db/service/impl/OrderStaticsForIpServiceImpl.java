package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.common.vo.api.request.IpListReqVo;
import com.panda.sport.rcs.common.vo.api.response.IpListResVo;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.mapper.OrderStaticsForIpMapper;
import com.panda.sport.rcs.db.service.IOrderStaticsForIpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 根据IP分组统计用户订单 Mapper 接口
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@Service
public class OrderStaticsForIpServiceImpl extends ServiceImpl<OrderStaticsForIpMapper, RiskOrderStatisticsByIp> implements IOrderStaticsForIpService {
    private Logger log = LoggerFactory.getLogger(OrderStaticsForIpServiceImpl.class);

    @Autowired
    private OrderStaticsForIpMapper mapper;

    @Override
    public Integer queryAmountByFinalBetTimeCount(Long staticBeginTime, Long staticEndTime, Long finalBeginTime, Long finalEndTime) {
        return mapper.queryAmountByFinalBetTimeCount(staticBeginTime, staticEndTime, finalBeginTime, finalEndTime);
    }

    @Override
    public List<RiskOrderStatisticsByIp> queryAmountByFinalBetTime(Long staticBeginTime, Long staticEndTime, Long finalBeginTime, Long finalEndTime, Integer count) {
        if(count>0){
            log.info("--------------进入limit分页查询--------------");
            for (int i = 0; i <= count/1000; i++){
                List<RiskOrderStatisticsByIp> list = mapper.queryAmountByFinalBetTime(staticBeginTime, staticEndTime, finalBeginTime, finalEndTime, i * 1000);
                log.info("--------------目前统计第"+(i+1)+"页,共"+(count/1000)+1+"页,"+"从第 "+i * 1000+" 条开始统计长度为1000条的数据--------------");
                log.info("--------------7日统计数据正在修改中--------------");
                for (RiskOrderStatisticsByIp byIp : list) {
                    LambdaUpdateWrapper<RiskOrderStatisticsByIp> warpper = new LambdaUpdateWrapper<>();
                    warpper.eq(RiskOrderStatisticsByIp::getIp, byIp.getIp());
                    warpper.set(RiskOrderStatisticsByIp::getSevenDaysBetAmount, byIp.getSevenDaysBetAmount());
                    warpper.set(RiskOrderStatisticsByIp::getSevenDaysProfitAmount, byIp.getSevenDaysProfitAmount());
                    this.update(warpper);
                }
                log.info("本次变更数据共{}条",list.size());
                log.info("--------------7日统计数据已修改完成--------------");
            }
        }
        return null;
    }

    @Override
    public void updateByIp(RiskOrderStatisticsByIp riskOrderStatisticsByIp) {
        mapper.updateByIp(riskOrderStatisticsByIp);
    }

    @Override
    public IPage<IpListResVo> queryIpList(Page<IpListResVo> page, IpListReqVo vo, List<String> ips) {
        return mapper.queryIpList(page, vo, ips);
    }

}
