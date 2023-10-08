package com.panda.sport.rcs.data.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.merge.bo.I18nItemBO;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.data.mq.RcsConsumer;
import com.panda.sport.rcs.data.mqSerializaBean.EsportLanguageInternation;
import com.panda.sport.rcs.data.mqSerializaBean.EsportMarketTypeBO;
import com.panda.sport.rcs.data.service.IStandardSportMarketCategoryService;
import com.panda.sport.rcs.data.service.RcsLanguageInternationService;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
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
 * @ClassName DjMarketCategoryConsumer
 * @Description TODO
 * @Author Administrator
 * @Date 2021/10/14 15:16
 * @Version 1.0
 **/
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "esport_business_topic_odd_type",
        consumerGroup = "RCS_DATA_ESPORT_BUSINESS_TOPIC_ODD_TYPE_GROUP",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class DjMarketCategoryConsumer extends RcsConsumer<Request<EsportMarketTypeBO>> {

    @Autowired
    private IStandardSportMarketCategoryService iStandardSportMarketCategoryService;

    @Autowired
    private RcsLanguageInternationService iRcsLanguageInternationService;

    @Override
    protected String getTopic() {
        return "esport_business_topic_odd_type";
    }

    @Override
    public Boolean handleMs(Request<EsportMarketTypeBO> standardMatchStatusMessageRequest) {
        try {
            EsportMarketTypeBO esportMarketTypeBO = standardMatchStatusMessageRequest.getData();
            log.info("::{}::接收到的电竞的玩法数据","RDEBTOTG_"+standardMatchStatusMessageRequest.getLinkId());
            //重组数据
            StandardSportMarketCategory categoryBean = new StandardSportMarketCategory();
            categoryBean.setId(esportMarketTypeBO.getStandardMarketId().longValue());
            categoryBean.setSportId(esportMarketTypeBO.getSportId());
            categoryBean.setFieldsNum(esportMarketTypeBO.getFieldsNum());
            categoryBean.setMultiMarket(esportMarketTypeBO.getMultiMarket());
            categoryBean.setTemplatePc(esportMarketTypeBO.getTemplatePcClient());
            categoryBean.setTemplateH5(esportMarketTypeBO.getTemplateH5Client());
            categoryBean.setModifyTime(esportMarketTypeBO.getModifyTime());
            categoryBean.setCreateTime(System.currentTimeMillis());
            categoryBean.setNameCode(esportMarketTypeBO.getNameCode());
            Map<String, Object> info = new HashMap<String, Object>();
            info.put("category_id", categoryBean.getId());
            info.put("sport_id", esportMarketTypeBO.getSportId());
            info.put("scope_id", esportMarketTypeBO.getCategory());
            info.put("order_no", esportMarketTypeBO.getOrderNo());
            info.put("status", esportMarketTypeBO.getStatus());
            info.put("name_code", esportMarketTypeBO.getNameCode());
            ArrayList<Map<String, Object>> categoryRefList = new ArrayList<>();
            categoryRefList.add(info);
            //操作数据
            try{
                iStandardSportMarketCategoryService.insert(categoryBean);
            } catch (Exception e){
                log.error("::{}::玩法数据入库重复不影响后续流程","RDEBTOTG_"+standardMatchStatusMessageRequest.getLinkId());
            }
            /**
             * 1 人工  2 系统
             * 人工不允许数据商下发的数据覆盖
             */
            Map<String, List<EsportLanguageInternation>> collect = esportMarketTypeBO.getEsportLanguageInternations().stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getFlag())));
            List<RcsLanguageInternation> rcsLanguageInternations1 = transferI18nItemBO2(collect.get("1"));
            List<RcsLanguageInternation> rcsLanguageInternations2 = transferI18nItemBO2(collect.get("2"));
            iStandardSportMarketCategoryService.batchInsertOrUpdateCategoryRef(categoryRefList);
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
                        log.info("::{}::esport_business_topic_odd_type 数据商下发数据 nameCode已经存在 不存","RDEBTOTG"+nameCode);
                    }
                }
            }
        } catch (Exception e) {
            log.error("::{}::Mq-StandardMatchStatus存入MQ消息队列错误:{},{}" ,"RDEBTOTG_"+standardMatchStatusMessageRequest.getLinkId(), e.getMessage() ,e);
            return false;
        }
        return true;
    }

    /**
     * 转换 I18nItemBO
     * @param i18nItemBOS
     * @return
     */
    private List<I18nItemBO> transferI18nItemBO(List<EsportLanguageInternation> i18nItemBOS) {
        List<I18nItemBO> i18nItemBOSs = new ArrayList<I18nItemBO>();
        for (EsportLanguageInternation i18nItemBO : i18nItemBOS) {
            if ("zs".equals(i18nItemBO.getLanguageType())||"en".equals(i18nItemBO.getLanguageType())){
                I18nItemBO i18nItemBO1 = new I18nItemBO();
                i18nItemBO1.setLanguageType(i18nItemBO.getLanguageType());
                i18nItemBO1.setModifyTime(i18nItemBO.getModifyTime());
                i18nItemBO1.setNameCode(i18nItemBO.getNameCode());
                i18nItemBO1.setRemark(i18nItemBO.getRemark());
                i18nItemBO1.setText(i18nItemBO.getText());
                i18nItemBOSs.add(i18nItemBO1);
            }
        }
        return i18nItemBOSs;
    }


    /**
     * 转换 I18nItemBO
     * @param i18nItemBOS
     * @return
     */
    private List<RcsLanguageInternation> transferI18nItemBO2(List<EsportLanguageInternation> i18nItemBOS) {
        if(CollectionUtils.isEmpty(i18nItemBOS)){return null;}
        Map<String, List<EsportLanguageInternation>> collect = i18nItemBOS.stream().collect(Collectors.groupingBy(e->String.valueOf(e.getNameCode())));
        HashMap<String, String> objectObjectHashMap1 = new HashMap<>();
        for (String nameCode : collect.keySet()) {
            HashMap<String, String> objectObjectHashMap = new HashMap<>();
            List<EsportLanguageInternation> esportLanguageInternations = collect.get(nameCode);
            for (EsportLanguageInternation esportLanguageInternation : esportLanguageInternations) {
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
