package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mapper.RcsOrderStatisticBetTimeMapper;
import com.panda.sport.rcs.mapper.RcsOrderStatisticMatchTimeMapper;
import com.panda.sport.rcs.mapper.RcsOrderStatisticSettleTimeMapper;
import com.panda.sport.rcs.mapper.TSettleMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.TSettle;
import com.panda.sport.rcs.pojo.report.*;
import com.panda.sport.rcs.task.wrapper.OrderStatisticTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.task.wrapper.impl
 * @Description :  TODO
 * @Date: 2019-12-26 20:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class OrderStatisticTimeServiceImpl implements OrderStatisticTimeService {
    @Autowired
    private RcsOrderStatisticBetTimeMapper rcsOrderStatisticBetTimeMapper;
    @Autowired
    private RcsOrderStatisticMatchTimeMapper rcsOrderStatisticMatchTimeMapper;
    @Autowired
    private RcsOrderStatisticSettleTimeMapper rcsOrderStatisticSettleTimeMapper;
    @Autowired
    private TSettleMapper tSettleMapper;
    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean orderStatisticTimeDealwith(CalcSettleItem calcSettleItem) {
        Long betNo = calcSettleItem.getBetNo();
        TSettle tSettle = tSettleMapper.selectById(betNo);
        if (tSettle == null || ((!ObjectUtils.isEmpty(tSettle.getCalcStatus())) && tSettle.getCalcStatus() != 0)) {
            log.error("mq推送了已经处理的数据，请检查");
            return true;
        }
        RcsOrderStatisticMatchTime rcsOrderStatisticMatchTime = new RcsOrderStatisticMatchTime(calcSettleItem);
        rcsOrderStatisticMatchTime.dealWithCalcSettleItem(calcSettleItem);
        RcsOrderStatisticBetTime rcsOrderStatisticBetTime = new RcsOrderStatisticBetTime(calcSettleItem);
        rcsOrderStatisticBetTime.dealWithCalcSettleItem(calcSettleItem);
        RcsOrderStatisticSettleTime rcsOrderStatisticSettleTime = new RcsOrderStatisticSettleTime(calcSettleItem);
        rcsOrderStatisticSettleTime.dealWithCalcSettleItem(calcSettleItem);
        saveAndUpdateBetReport(rcsOrderStatisticBetTime);
        saveAndUpdateMatchReport(rcsOrderStatisticMatchTime);
        saveAndUpdateSettleReport(rcsOrderStatisticSettleTime);
        updateSettleStatus(calcSettleItem);
        return Boolean.TRUE;
    }

    private void saveAndUpdateBetReport(RcsOrderStatisticBetTime list){
        rcsOrderStatisticBetTimeMapper.updateRcsOrderStatisticBet(list);
    }
    private void saveAndUpdateMatchReport(RcsOrderStatisticMatchTime list){
        rcsOrderStatisticMatchTimeMapper.updateRcsOrderStatisticMatch(list);
    }
    private void saveAndUpdateSettleReport(RcsOrderStatisticSettleTime list){
        rcsOrderStatisticSettleTimeMapper.updateRcsOrderStatisticSettle(list);
    }
    private void updateSettleStatus(CalcSettleItem list){
        tSettleMapper.updateTSettleToOrderStatic(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDataByDate(Long startTime,Long endTime) {
        MinDates dates = tSettleMapper.getMinDates(startTime);
        if(dates != null){
            String settleDate = DateUtils.transferLongToDateString(startTime);
            String betDate = dates.getMinBetDate();
            String matchDate = dates.getMinMatchDate();
            //删除该日所有汇总数据
            rcsOrderStatisticBetTimeMapper.deleteInfoByDate(betDate);
            rcsOrderStatisticMatchTimeMapper.deleteInfoByDate(matchDate);
            rcsOrderStatisticSettleTimeMapper.deleteInfoByDate(settleDate);
        }
        //修改结算表里的数据状态为0
        Integer size = 100000;
        Page<Long> pageParam = new Page<>(1, size);
        IPage<Long> result = tSettleMapper.selectIdByDate(pageParam,startTime,endTime);
        List<Long> items = result.getRecords();
        updateStatusByIds(items);
        long pages = result.getPages();
        if(pages > 1){
            for(int page = 2 ; page <= pages; page++){
                pageParam = new Page<>(page, size);
                result = tSettleMapper.selectIdByDate(pageParam,startTime,endTime);
                items = result.getRecords();
                updateStatusByIds(items);
            }
        }

    }

    private void updateStatusByIds(List<Long> items){
        if(items.size()>0){
            tSettleMapper.updateSettleStatusByIds(items);
        }
    }

}
