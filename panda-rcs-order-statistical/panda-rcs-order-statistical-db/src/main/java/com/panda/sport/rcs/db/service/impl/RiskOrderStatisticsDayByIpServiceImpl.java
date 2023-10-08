package com.panda.sport.rcs.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.api.response.ListByVisitInfoResVo;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsDayByIp;
import com.panda.sport.rcs.db.mapper.OrderStaticsForIpMapper;
import com.panda.sport.rcs.db.mapper.RiskOrderStatisticsDayByIpMapper;
import com.panda.sport.rcs.db.service.IOrderStaticsForIpService;
import com.panda.sport.rcs.db.service.IRiskOrderStatisticsDayByIpService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 根据IP分组当日内的总投注额与总输赢额 服务实现类
 * </p>
 *
 * @author author
 * @since 2021-02-03
 */
@Service
public class RiskOrderStatisticsDayByIpServiceImpl extends ServiceImpl<RiskOrderStatisticsDayByIpMapper, RiskOrderStatisticsDayByIp> implements IRiskOrderStatisticsDayByIpService {
    @Autowired
    private RiskOrderStatisticsDayByIpMapper mapper;

    @Autowired
    private IOrderStaticsForIpService ipService;

    private Logger log = LoggerFactory.getLogger(RiskOrderStatisticsDayByIpServiceImpl.class);

    @Override
    public List<RiskOrderStatisticsByIp> queryProductAmountTotal(Long startTime, Long endTime) {
        return mapper.queryProductAmountTotal(startTime, endTime);
    }

    @Override
    public List<RiskOrderStatisticsByIp> queryProfitAmount(Long startTime, Long endTime) {
        return mapper.queryProfitAmount(startTime, endTime);
    }

    @Override
    public List<ListByVisitInfoResVo> queryVisitByIp(List<String> ips) {
        return mapper.queryVisitByIp(ips);
    }

    @Override
    public void updateByIpAndStaticTime(RiskOrderStatisticsDayByIp riskOrderStatisticsDayByIp) {
        mapper.updateByIpAndStaticTime(riskOrderStatisticsDayByIp);
    }

    @Override
    public void staticsOrderForIp(Long startTime, Long endTime, String s, int type) {
        log.info("统计开始时间为：{}，统计结束时间为：{}", startTime, endTime);
        //累积投注
        List<RiskOrderStatisticsByIp> productAmountTotalList = this.queryProductAmountTotal(startTime, endTime);
        //累积输赢
        List<RiskOrderStatisticsByIp> profitAmountList = this.queryProfitAmount(startTime, endTime);
        productAmountTotalList.addAll(profitAmountList);

        //判断是否查询出数据
        if (!ObjectUtils.isEmpty(productAmountTotalList)) {
            Map<String, RiskOrderStatisticsByIp> map = new HashMap<>();
            for (RiskOrderStatisticsByIp vo : productAmountTotalList) {
                //根据ip拼装对象，为"累积投注"和"累积输赢"赋值
                if (map.get(vo.getIp()) != null) {
                    if (vo.getBetAmount() == null) {
                        vo.setBetAmount(map.get(vo.getIp()).getBetAmount());
                    }
                    if (vo.getProfitAmount() == null) {
                        vo.setBetAmount(map.get(vo.getIp()).getProfitAmount());
                    }
//                    if (vo.getSevenDaysBetAmount() == null) {
//                        vo.setSevenDaysBetAmount(vo.getBetAmount());
//                    }
//                    if (vo.getSevenDaysProfitAmount() == null) {
//                        vo.setSevenDaysProfitAmount(vo.getProfitAmount());
//                    }
                    if (vo.getFinalBetTime() < map.get(vo.getIp()).getFinalBetTime()) {
                        vo.setFinalBetTime(map.get(vo.getIp()).getFinalBetTime());
                    }
                }
                //存入map去重
                map.put(vo.getIp(), vo);
            }

            //插入总表的集合
            List<RiskOrderStatisticsByIp> allList = map.values().stream().collect(Collectors.toList());

            //根据插入总表的集合中的IP查询相对应的IP基本信息并进行赋值
            List<String> strings = allList.stream().map(RiskOrderStatisticsByIp::getIp).collect(Collectors.toList());
            List<ListByVisitInfoResVo> visitInfoResVos = this.queryVisitByIp(strings);
            for (ListByVisitInfoResVo visitInfoResVo : visitInfoResVos) {
                for (RiskOrderStatisticsByIp byIp : allList) {
                    if (byIp.getIp() == null || visitInfoResVo.getIp() == null) {
                        log.info("staticsOrderForIpip为null跳过");
                        continue;
                    }
                    if(byIp.getIp().equals(visitInfoResVo.getIp())){
                        if(visitInfoResVo.getIp()!=null){
                            byIp.setArea(visitInfoResVo.getArea());
                        }
                        if(visitInfoResVo.getTagId()!=null){
                            byIp.setTagId(visitInfoResVo.getTagId());
                        }
                    }
                }
            }

            //构造每日统计表集合
            List<RiskOrderStatisticsDayByIp> dayByIpList = new ArrayList<>();
            for (RiskOrderStatisticsByIp byIp : allList) {
                RiskOrderStatisticsDayByIp dayByIp = CopyUtils.clone(byIp, RiskOrderStatisticsDayByIp.class);
                dayByIp.setStaticTime(LocalDateTimeUtil.milliToDate(byIp.getFinalBetTime()));
                dayByIpList.add(dayByIp);
            }
            log.info("--------------对每日表数据进行更新--------------");
            //对每日表数据进行更新
            List<RiskOrderStatisticsDayByIp> dayByIpSaveList = new ArrayList<>();
            for (RiskOrderStatisticsDayByIp dayByIp : dayByIpList) {
                LambdaQueryWrapper<RiskOrderStatisticsDayByIp> dayByIpLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dayByIpLambdaQueryWrapper.eq(RiskOrderStatisticsDayByIp::getIp, dayByIp.getIp());
                dayByIpLambdaQueryWrapper.eq(RiskOrderStatisticsDayByIp::getStaticTime, dayByIp.getStaticTime());
                RiskOrderStatisticsDayByIp day = this.getOne(dayByIpLambdaQueryWrapper);
                if(ObjectUtils.isEmpty(day)){
                    //新增
                    //this.save(dayByIp);
                    dayByIpSaveList.add(dayByIp);
                }else{
                    if(StringUtils.isNotEmpty(s)){
                        //如果为补数据则修改变更数据
                        if(dayByIp.getBetAmount()==null){
                            dayByIp.setBetAmount(BigDecimal.ZERO);
                        }else{
                            dayByIp.setBetAmount(dayByIp.getBetAmount().subtract(day.getBetAmount()));
                        }

                        if(dayByIp.getProfitAmount()==null){
                            dayByIp.setProfitAmount(BigDecimal.ZERO);
                        }else{
                            dayByIp.setProfitAmount(dayByIp.getProfitAmount().subtract(day.getProfitAmount()));
                        }
                        //记录一个值 用于修改总表数据
                    }
                    //修改
                    this.updateByIpAndStaticTime(dayByIp);
                }
            }
            this.saveBatch(dayByIpSaveList, 1000);
            log.info("每日表数据共变更{}条，其中新增数据{}条，更新数据{}条", dayByIpList.size(), dayByIpSaveList.size(), dayByIpList.size() - dayByIpSaveList.size());
            log.info("--------------每日表数据更新完成--------------");

            //对总表数据进行更新
            List<RiskOrderStatisticsByIp> byIpSaveList = new ArrayList<>();
            log.info("--------------对总表数据进行更新--------------");
            for (RiskOrderStatisticsByIp byIp : allList) {
                LambdaQueryWrapper<RiskOrderStatisticsByIp> statisticsByIpLambdaQueryWrapper = new LambdaQueryWrapper<>();
                statisticsByIpLambdaQueryWrapper.eq(RiskOrderStatisticsByIp::getIp, byIp.getIp());
                RiskOrderStatisticsByIp ip = ipService.getOne(statisticsByIpLambdaQueryWrapper);
                if(ObjectUtils.isEmpty(ip)){
                    //新增
                    if(byIp.getBetAmount() == null || byIp.getProfitAmount() == null || byIp.getBetAmount() == BigDecimal.ZERO){
                        byIp.setProfitProbability(BigDecimal.ZERO);
                    }else {
                        byIp.setProfitProbability(byIp.getProfitAmount().multiply(new BigDecimal(100)).divide(byIp.getBetAmount(),2,BigDecimal.ROUND_HALF_UP));
                    }
                    //ipService.save(byIp);
                    byIpSaveList.add(byIp);
                }else{
                    //将变更后的值循环修改至总表type
                    Map<String, RiskOrderStatisticsDayByIp> collect = dayByIpList.stream().collect(Collectors.toMap(e -> e.getIp(), e -> e));
                    RiskOrderStatisticsDayByIp dayByIp1 = collect.get(byIp.getIp());
                    if(!ObjectUtils.isEmpty(dayByIp1)){
                        byIp.setBetAmount(dayByIp1.getBetAmount());
                        byIp.setProfitAmount(dayByIp1.getProfitAmount());
                        //byIp.setSevenDaysBetAmount(dayByIp.getBetAmount());
                        //byIp.setSevenDaysProfitAmount(dayByIp.getProfitAmount());
                        //如果是补数历史数据，则不修改最后投注时间
                        if(type==2){
                            byIp.setFinalBetTime(ip.getFinalBetTime()>byIp.getFinalBetTime()?ip.getFinalBetTime():byIp.getFinalBetTime());
                        }
                    }

//                    for (RiskOrderStatisticsDayByIp dayByIp : dayByIpList) {
//                        if(byIp.getIp().equals(dayByIp.getIp())){
//                            byIp.setBetAmount(dayByIp.getBetAmount());
//                            byIp.setProfitAmount(dayByIp.getProfitAmount());
//                            //byIp.setSevenDaysBetAmount(dayByIp.getBetAmount());
//                            //byIp.setSevenDaysProfitAmount(dayByIp.getProfitAmount());
//                            //如果是补数历史数据，则不修改最后投注时间
//                            if(type==2){
//                                byIp.setFinalBetTime(null);
//                            }
//                        }
//                    }
                    //修改
                    ipService.updateByIp(byIp);
                }
            }
            ipService.saveBatch(byIpSaveList, 1000);
            log.info("总表数据共变更{}条，其中新增数据{}条，更新数据{}条", allList.size(), byIpSaveList.size(), allList.size() - byIpSaveList.size());
            log.info("--------------总表数据更新完成--------------");
        }
    }


}
