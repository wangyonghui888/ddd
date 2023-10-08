package com.panda.sport.rcs.data.sync;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.RcsMarketChampionExtService;
import com.panda.sport.rcs.data.service.RcsStandardOutrightMatchInfoService;
import com.panda.sport.rcs.data.service.StandardSportMarketService;
import com.panda.sport.rcs.pojo.RcsMarketChampionExt;
import com.panda.sport.rcs.pojo.RcsStandardOutrightMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.dto.OutrightMarketTimeDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MODIFY_CHAMPION_MARKET_TIME",
        consumerGroup = "RCS_DATA_MODIFY_CHAMPION_MARKET_TIME_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class OutrightMarketTimeConsumer extends RcsConsumer<OutrightMarketTimeDTO> {

    @Autowired
    private RcsMarketChampionExtService rcsMarketChampionExtService;

    @Autowired
    private RcsStandardOutrightMatchInfoService standardOutrightMatchInfoService;

    @Autowired
    private StandardSportMarketService standardSportMarketService;

    @Override
    protected String getTopic() {
        return "MODIFY_CHAMPION_MARKET_TIME";
    }

    @Override
    public Boolean handleMs(OutrightMarketTimeDTO outrightMarketTimeDTO) {
        String linkId = outrightMarketTimeDTO.getStandardMatchId() + "_" + outrightMarketTimeDTO.getRelationMarketId();
        log.info("::{}::冠军盘时间更新入库开始", linkId);

        //兜底获取冠军赛事赛种ID
        RcsStandardOutrightMatchInfo outrightMatchInfo = standardOutrightMatchInfoService.selectByPrimaryKey(outrightMarketTimeDTO.getStandardMatchId());
        if (null == outrightMatchInfo) {
            //防止空指针报错
            outrightMatchInfo = new RcsStandardOutrightMatchInfo();
        }
        //冠军赛事扩展表入库
        RcsMarketChampionExt rcsMarketChampionExt = new RcsMarketChampionExt();
        rcsMarketChampionExt.setStandardMatchInfoId(outrightMarketTimeDTO.getStandardMatchId());
        rcsMarketChampionExt.setMarketId(outrightMarketTimeDTO.getRelationMarketId());
        rcsMarketChampionExt.setSportId(outrightMatchInfo.getSportId());
        rcsMarketChampionExt.setMarketCategoryId(10001L);
        //盘口表入库
        StandardSportMarket standardSportMarket = new StandardSportMarket();
        standardSportMarket.setId(outrightMarketTimeDTO.getRelationMarketId());
        standardSportMarket.setSportId(outrightMatchInfo.getSportId());
        //只更新修改的字段
        if (null != outrightMarketTimeDTO.getMarketNextCloseTime()) {
            String nextCloseTime = outrightMarketTimeDTO.getMarketNextCloseTime().toString();
            rcsMarketChampionExt.setNextSealTime(nextCloseTime);
            standardSportMarket.setAddition1(nextCloseTime);
        }
        if (null != outrightMarketTimeDTO.getMarketStartTime()) {
            String marketStartTime = outrightMarketTimeDTO.getMarketStartTime().toString();
            rcsMarketChampionExt.setMarketStartTime(marketStartTime);
            standardSportMarket.setAddition2(marketStartTime);
        }
        if (null != outrightMarketTimeDTO.getMarketEndTime()) {
            String marketEndTime = outrightMarketTimeDTO.getMarketEndTime().toString();
            rcsMarketChampionExt.setMarketEndTime(marketEndTime);
            standardSportMarket.setAddition3(marketEndTime);
        }
        try {
            rcsMarketChampionExtService.insertOrUpdateSelective(rcsMarketChampionExt);
            standardSportMarketService.updateById(standardSportMarket);
        } catch (Exception e) {
            log.error("::{}::冠军盘时间更新入库异常:{}", linkId, e);
            return false;
        }
        log.info("::{}::冠军盘时间更新入库成功", linkId);
        return true;
    }
}
