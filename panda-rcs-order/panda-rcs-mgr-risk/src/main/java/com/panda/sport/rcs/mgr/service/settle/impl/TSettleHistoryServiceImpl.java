package com.panda.sport.rcs.mgr.service.settle.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.OrderDetailPO;
import com.panda.sport.data.rcs.dto.SettleItem;
import com.panda.sport.rcs.mapper.settle.TSettleDetailMapper;
import com.panda.sport.rcs.mapper.settle.TSettleHistoryDetailMapper;
import com.panda.sport.rcs.mapper.settle.TSettleHistoryMapper;
import com.panda.sport.rcs.mapper.settle.TSettleMapper;
import com.panda.sport.rcs.mgr.service.settle.ITSettleHistoryService;
import com.panda.sport.rcs.pojo.settle.TSettleHistory;
import com.panda.sport.rcs.pojo.settle.TSettleHistoryDetail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.service.settle.impl
 * @Description :  结算历史
 * @Date: 2020-12-03 上午 10:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class TSettleHistoryServiceImpl  extends ServiceImpl<TSettleHistoryMapper, TSettleHistory> implements ITSettleHistoryService {
    @Autowired
    private TSettleHistoryMapper settleHistoryMapper;

    @Autowired
    private TSettleHistoryDetailMapper settleHistoryDetailMapper;

    /**
     * 保存
     *
     * @param msg
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(SettleItem msg) {
        TSettleHistory settleHistory = new TSettleHistory();
        BeanUtils.copyProperties(msg, settleHistory);
        settleHistory.setOddFinally(Double.parseDouble(msg.getOddFinally()));
        settleHistory.setOddsValue(msg.getOddsValue());
        settleHistory.setMerchantId(msg.getMerchantId());
        settleHistory.setCreateTime(System.currentTimeMillis());
        settleHistory.setProfitAmount(msg.getSettleAmount() - msg.getBetAmount());
        settleHistoryMapper.insert(settleHistory);

        List<TSettleHistoryDetail> settleHistoryDetailList = new ArrayList<>();
        for(OrderDetailPO orderDetail:msg.getOrderDetailRisk()){
            TSettleHistoryDetail settleHistoryDetail = new TSettleHistoryDetail();
            BeanUtils.copyProperties(orderDetail,settleHistoryDetail);
            settleHistoryDetail.setCreateTime(System.currentTimeMillis());
            settleHistoryDetailList.add(settleHistoryDetail);
        }

        settleHistoryDetailMapper.batchSave(settleHistoryDetailList);
    }
}
