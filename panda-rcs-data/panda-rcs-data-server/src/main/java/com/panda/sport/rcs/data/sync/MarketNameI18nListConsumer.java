package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.merge.bo.I18nItemBO;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.service.RcsLanguageInternationService;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.pojo.dto.MarketOddsNameI18nList;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MARKET_NAME_I18N_LIST",
        consumerGroup = "RCS_DATA_MARKET_NAME_I18N_LIST_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class MarketNameI18nListConsumer extends RcsConsumer<Request<List<MarketOddsNameI18nList>>> {

    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;

    @Autowired
    private RcsLanguageInternationService iRcsLanguageInternationService;

    @Override
    protected String getTopic() {
        return "MARKET_NAME_I18N_LIST";
    }


    @Override
    public Boolean handleMs(Request<List<MarketOddsNameI18nList>> rRequests){
        try {
            List<MarketOddsNameI18nList> list = rRequests.getData();
            ArrayList<I18nItemBO> i18nItemBOs1 = new ArrayList<>();
            ArrayList<I18nItemBO> i18nItemBOs2 = new ArrayList<>();
            for (MarketOddsNameI18nList marketOddsNameI18nList : list) {
                I18nItemBO i18nItemBO = new I18nItemBO();
                i18nItemBO.setNameCode(Long.valueOf(marketOddsNameI18nList.getNameCode()));
                i18nItemBO.setText(marketOddsNameI18nList.getText());
                i18nItemBO.setLanguageType(marketOddsNameI18nList.getLanguageType());
                i18nItemBO.setRemark(marketOddsNameI18nList.getRemark());
                i18nItemBO.setModifyTime(marketOddsNameI18nList.getModifyTime());
                if(marketOddsNameI18nList.getFlag().intValue()==1){
                    i18nItemBOs1.add(i18nItemBO);
                }else if(marketOddsNameI18nList.getFlag().intValue()==2){
                    i18nItemBOs2.add(i18nItemBO);
                }
            }
            /**
             * 1 人工  2 系统
             * 人工不允许数据商下发的数据覆盖
             */
            List<RcsLanguageInternation> rcsLanguageInternations1 = transferI18nItemBO2(i18nItemBOs1);
            List<RcsLanguageInternation> rcsLanguageInternations2 = transferI18nItemBO2(i18nItemBOs2);
            iRcsLanguageInternationService.batchInsertOrUpdateMerge(rcsLanguageInternations1);
            if(!CollectionUtils.isEmpty(rcsLanguageInternations2)){
                Map<String, List<RcsLanguageInternation>> collect1 = rcsLanguageInternations2.stream().collect(Collectors.groupingBy(RcsLanguageInternation::getNameCode));
                //一般只有一个nameCode
                for (String nameCode : collect1.keySet()) {
                    QueryWrapper<RcsLanguageInternation> objectQueryWrapper = new QueryWrapper<>();
                    objectQueryWrapper.lambda().eq(RcsLanguageInternation::getNameCode,Long.valueOf(nameCode));
                    List<RcsLanguageInternation> list2 = iRcsLanguageInternationService.list(objectQueryWrapper);
                    //来自数据商数据如果库里为空就插入
                    if(CollectionUtils.isEmpty(list2)){
                        iRcsLanguageInternationService.batchInsert(rcsLanguageInternations2);
                    }else {
                        log.info("::{}::MARKET_NAME_I18N_LIST 数据商下发数据 nameCode已经存在 不存","RDMILG"+nameCode);
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::{}{}{}","RDMNILG_"+rRequests.getLinkId(),JsonFormatUtils.toJson(rRequests),e.getMessage() , e);
        }
        return true;
    }


    /**
     * 转换 I18nItemBO
     * @param i18nItemBOS
     * @return
     */
    private List<RcsLanguageInternation> transferI18nItemBO2(List<I18nItemBO> i18nItemBOS) {
        Map<String, List<I18nItemBO>> collect = i18nItemBOS.stream().collect(Collectors.groupingBy(e->String.valueOf(e.getNameCode())));
        HashMap<String, String> objectObjectHashMap1 = new HashMap<>();
        for (String nameCode : collect.keySet()) {
            HashMap<String, String> objectObjectHashMap = new HashMap<>();
            List<I18nItemBO> esportLanguageInternations = collect.get(nameCode);
            for (I18nItemBO esportLanguageInternation : esportLanguageInternations) {
                objectObjectHashMap.put(esportLanguageInternation.getLanguageType(), esportLanguageInternation.getText());
            }
            objectObjectHashMap1.put(nameCode, JSONObject.toJSONString(objectObjectHashMap));
        }
        List<RcsLanguageInternation> list = new ArrayList<>();
        for (String nameCode : collect.keySet()) {
            RcsLanguageInternation rcsLanguageInternation = new RcsLanguageInternation();
            rcsLanguageInternation.setNameCode(nameCode);
            rcsLanguageInternation.setText(objectObjectHashMap1.get(nameCode));
            list.add(rcsLanguageInternation);
        }
        return list;
    }
}
