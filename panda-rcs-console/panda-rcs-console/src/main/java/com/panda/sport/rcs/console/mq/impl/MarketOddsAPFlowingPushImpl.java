package com.panda.sport.rcs.console.mq.impl;


import com.panda.sport.rcs.console.dao.StandardSportMarketFlowingMapper;
import com.panda.sport.rcs.console.dao.StandardSportMarketOddsFlowingMapper;
import com.panda.sport.rcs.console.pojo.Request;
import com.panda.sport.rcs.console.pojo.StandardMarketMessage;
import com.panda.sport.rcs.console.pojo.StandardMarketOddsMessage;
import com.panda.sport.rcs.console.pojo.StandardMatchMarketMessage;
import com.panda.sport.rcs.console.pojo.StandardSportMarketFlowing;
import com.panda.sport.rcs.console.pojo.StandardSportMarketOddsFlowing;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.MacthStatusEnum;
import com.panda.sport.rcs.enums.MarketStatusEnum;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.utils.NameExpressionValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MarketOddsAPFlowingPushImpl extends ConsumerAdapter<Request<StandardMatchMarketMessage>> {

    @Autowired
    StandardSportMarketFlowingMapper standardSportMarketFlowingMapper;

    @Autowired
    StandardSportMarketOddsFlowingMapper standardSportMarketOddsFlowingMapper;

/*
    public MarketOddsAPFlowingPushImpl() {
        super("STANDARD_MARKET_ODDS_RISK", "");
    }
*/

    /**
     * @Description: 实时盘口赔率变化通知
     * @Date: 2019/12/12
     **/
    @Override
    public Boolean handleMs(Request<StandardMatchMarketMessage> msg, Map<String, String> paramsMap) {
      /*  log.info("flowing-datasync-接收盘口赔率{}", JsonFormatUtils.toJson(msg));
        try {
            StandardMatchMarketMessage data = msg.getData();
            String linkId = msg.getLinkId();
            long currentTime = System.currentTimeMillis();
            if (data == null || data.getStandardMatchInfoId() == null) return true;
            //如果封盘或者是关盘
            if (data.getStatus().intValue() == MacthStatusEnum.CLOSE.getStatus() || data.getStatus().intValue() == MacthStatusEnum.SEAL.getStatus() || data.getStatus().intValue() == MacthStatusEnum.NOT_OPEN.getStatus()) {
                Integer status = MarketStatusEnum.CLOSE.getState();
                String myRemark = "";
                if (data.getStatus() == MacthStatusEnum.CLOSE.getStatus()) {
                    status = MarketStatusEnum.CLOSE.getState();
                    myRemark = "关盘";
                } else if (data.getStatus() == MacthStatusEnum.SEAL.getStatus()) {
                    status = MarketStatusEnum.SEAL.getState();
                    myRemark = "封盘";
                }else if (data.getStatus() == MacthStatusEnum.NOT_OPEN.getStatus()) {
                    status = MarketStatusEnum.LOCK.getState();
                    myRemark = "锁盘";
                }
                // 修改所有盘口状态
                StandardSportMarketFlowing standardSportMarketFlowing = new StandardSportMarketFlowing();
                standardSportMarketFlowing.setStatus(status);
                standardSportMarketFlowing.setOId(0l);
                standardSportMarketFlowing.setModifyTime(data.getModifyTime());
                standardSportMarketFlowing.setLinkId(linkId);
                standardSportMarketFlowing.setStandardMatchInfoId(data.getStandardMatchInfoId());
                standardSportMarketFlowing.setMyRemark(myRemark);
                standardSportMarketFlowingMapper.insertSelective(standardSportMarketFlowing);
            }

            //1.更新赛事比赛开盘标识
            List<StandardMarketMessage> marketList = data.getMarketList();
            if (CollectionUtils.isEmpty(marketList)) return true;
            List<StandardSportMarketFlowing> standardSportMarketFlowings = new ArrayList<>();
            List<StandardSportMarketOddsFlowing> listStandardSportMarketOddFlowings = new ArrayList<>();
            for (StandardMarketMessage marketDTO : marketList) {
                //2.盘口数据转换
                StandardSportMarketFlowing standardSportMarketFlowing = new StandardSportMarketFlowing();
                BeanUtils.copyProperties(marketDTO, standardSportMarketFlowing);
                standardSportMarketFlowing.setOId(marketDTO.getId());
                standardSportMarketFlowing.setLinkId(linkId);
                standardSportMarketFlowing.setI18nNames(JsonFormatUtils.toJson(marketDTO.getI18nNames()));
                standardSportMarketFlowing.setCreateTime(currentTime);
                standardSportMarketFlowing.setStandardMatchInfoId(data.getStandardMatchInfoId());
                standardSportMarketFlowing.setVersionId(String.valueOf(msg.getDataSourceTime()));
                standardSportMarketFlowing.setDataType(1);
                standardSportMarketFlowings.add(standardSportMarketFlowing);
                // 3.获取当前盘口下的投注项数据
                List<StandardMarketOddsMessage> marketOddsDTOList = marketDTO.getMarketOddsList();
                if (!CollectionUtils.isEmpty(marketOddsDTOList)) {
                    for (StandardMarketOddsMessage standardSportMarketOddsDTO : marketOddsDTOList) {
                        // 4.投注项数据转换
                        StandardSportMarketOddsFlowing standardSportMarketOddsFlowing = new StandardSportMarketOddsFlowing();
                        BeanUtils.copyProperties(standardSportMarketOddsDTO, standardSportMarketOddsFlowing);
                        if (standardSportMarketOddsFlowing.getId() != null) {
                            standardSportMarketOddsFlowing.setOId(standardSportMarketOddsDTO.getId());
                            standardSportMarketOddsFlowing.setLinkId(linkId);
                            standardSportMarketOddsFlowing.setPlaceNumId(marketDTO.getPlaceNumId());
                            standardSportMarketOddsFlowing.setOddsFieldsTempletId(standardSportMarketOddsDTO.getOddsFieldsTemplateId());
                            standardSportMarketOddsFlowing.setI18nNames(JsonFormatUtils.toJson(standardSportMarketOddsDTO.getI18nNames()));
                            standardSportMarketOddsFlowing.setOddsValue(standardSportMarketOddsDTO.getPaOddsValue());
                            standardSportMarketOddsFlowing.setCreateTime(currentTime);
                            standardSportMarketOddsFlowing.setOddsValue(standardSportMarketOddsDTO.getPaOddsValue());
                            standardSportMarketOddsFlowing.setDataType(1);
                            String nameExpressionValue = NameExpressionValueUtils.getNameExpressionValue(marketDTO.getMarketCategoryId().intValue(), standardSportMarketOddsDTO.getOddsType(), marketDTO.getAddition1());
                            standardSportMarketOddsFlowing.setNameExpressionValue(nameExpressionValue);
                            listStandardSportMarketOddFlowings.add(standardSportMarketOddsFlowing);
                        }
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(standardSportMarketFlowings))standardSportMarketFlowingMapper.batchInsert(standardSportMarketFlowings);
            if(CollectionUtils.isNotEmpty(listStandardSportMarketOddFlowings))standardSportMarketOddsFlowingMapper.batchInsert(listStandardSportMarketOddFlowings);

        } catch (Exception e) {
            log.error("flowing-datasync-接收盘口赔率错误" +JsonFormatUtils.toJson(msg)+ e.getMessage(), e);
        }*/
        return true;
    }


}
