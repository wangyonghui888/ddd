package com.panda.sport.rcs.task.service.profit.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMarketDTO;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.merge.dto.StandardMatchMarketDTO;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.enums.DataSourceTypeEnum;
import com.panda.sport.rcs.mapper.RcsMatchMarketConfigMapper;
import com.panda.sport.rcs.mapper.RcsTradeConfigMapper;
import com.panda.sport.rcs.mapper.statistics.MatchStatisticsInfoDetailMapper;
import com.panda.sport.rcs.pojo.MatchPeriod;
import com.panda.sport.rcs.pojo.MatchStatisticsInfoDetail;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.task.service.profit.ScoreMonitoringService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketOddsService;
import com.panda.sport.rcs.task.wrapper.StandardSportMarketService;
import com.panda.sport.rcs.utils.MarketAdditionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.service.profit.impl
 * @Description :  TODO
 * @Date: 2020-03-02 21:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class ScoreMonitoringServiceImpl implements ScoreMonitoringService {
    @Autowired
    private MatchStatisticsInfoDetailMapper matchStatisticsInfoDetailMapper;
    @Autowired
    private StandardSportMarketService standardSportMarketService;
    @Autowired
    private StandardSportMarketOddsService standardSportMarketOddsService;
    @Autowired
    private RcsMatchMarketConfigMapper rcsMatchMarketConfigMapper;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ITradeMarketOddsApi iTradeMarketOddsApi;
    @Autowired
    private RcsTradeConfigMapper rcsTradeConfigMapper;
    @Override
    public void scoreMonitoring(MatchPeriod matchPeriod) {
    	if(StringUtils.isBlank(matchPeriod.getScore())) return ;
    	if(!"1".equals(String.valueOf(matchPeriod.getSportId()))) {
    		return;
    	}
    	
        // 1: 先查询以前比分
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("standard_match_id", matchPeriod.getStandardMatchId());
        columnMap.put("code", "match_score");
        columnMap.put("first_num", 0);
        List<MatchStatisticsInfoDetail> matchStatisticsInfoDetails = matchStatisticsInfoDetailMapper.selectByMap(columnMap);
        int oldScore = 0;
        if (!CollectionUtils.isEmpty(matchStatisticsInfoDetails)) {
            oldScore = matchStatisticsInfoDetails.get(0).getT1() - matchStatisticsInfoDetails.get(0).getT2();
        }
        //2：获取新比分差
        String score = matchPeriod.getScore();
        String[] split = score.split(":");
        int newScore = Integer.parseInt(split[0]) - Integer.parseInt(split[1]);
        //3：判断比分差值是否相等
        if (oldScore != newScore) {
            //改变盘口值并且向融合发送
            getCurrentMarketInfo(matchPeriod.getStandardMatchId(), newScore);
        }
    }

    public void getCurrentMarketInfo(Long matchId, Integer newScore) {
        //先根据赛事id和玩法查找所有的盘口  只查询手动
        List<StandardSportMarket> standardSportMarketList = standardSportMarketService.selectStandardSportMarketByMatchIdAndPlayIdAndPlayId(matchId);
        if (!CollectionUtils.isEmpty(standardSportMarketList)) {
            StandardMatchMarketDTO standardMatchMarketDTO = new StandardMatchMarketDTO();
            standardMatchMarketDTO.setStandardMatchInfoId(matchId);
            ArrayList<StandardMarketDTO> standardMarketDTOs = new ArrayList<>();
            for (StandardSportMarket standardSportMarket : standardSportMarketList) {
                RcsTradeConfig rcsTradeConfig = rcsTradeConfigMapper.selectRcsTradeConfig(standardSportMarket.getStandardMatchInfoId().toString(), standardSportMarket.getMarketCategoryId().toString(), standardSportMarket.getId().toString());
                if (rcsTradeConfig != null && rcsTradeConfig.getDataSource() != null && rcsTradeConfig.getDataSource().equals(DataSourceTypeEnum.MANUAL.getValue())) {
                    StandardMarketDTO bean = BeanCopyUtils.copyProperties(standardSportMarket, StandardMarketDTO.class);
                    QueryWrapper<StandardSportMarketOdds> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(StandardSportMarketOdds::getMarketId, standardSportMarket.getId());
                    List<StandardSportMarketOdds> list = standardSportMarketOddsService.list(queryWrapper);
                    if (list == null || list.size() == 0) {
                        log.warn("风控标准库-盘口id：{} 不存在投注项", standardSportMarket.getId());
                        continue;
                    }
                    List<StandardMarketOddsDTO> oddsList = new ArrayList<>();
                    //设置新的盘口
                    String add1 = MarketAdditionUtils.add2ToAdd1(Double.parseDouble(bean.getAddition2()), newScore);
                    for (StandardSportMarketOdds obj : list) {
                        StandardMarketOddsDTO dto = BeanCopyUtils.copyProperties(obj, StandardMarketOddsDTO.class);
                        dto.setThirdOddsFieldSourceId(String.valueOf(obj.getId()));
                        dto.setOddsFieldsTemplateId(obj.getOddsFieldsTempletId());
                        oddsList.add(dto);
                        //投注项的值也会变
                        if (obj.getOrderOdds() == 2) {
                            dto.setNameExpressionValue(new BigDecimal(add1).multiply(new BigDecimal("-1")).toPlainString());
                        } else {
                            dto.setNameExpressionValue(add1);
                        }
                    }
                    bean.setMarketOddsList(oddsList);
                    bean.setThirdMarketSourceId(String.valueOf(standardSportMarket.getId()));
                    bean.setAddition1(add1);
                    //还要修改配置
//                    if (Double.parseDouble(add1) > 0) {
//                        rcsMatchMarketConfigMapper.updateRcsMatchMarketConfigToOddsValue(standardSportMarket.getId(), "0", add1);
//                    } else {
//                        rcsMatchMarketConfigMapper.updateRcsMatchMarketConfigToOddsValue(standardSportMarket.getId(), new BigDecimal(add1).multiply(new BigDecimal("-1")).toPlainString(), "0");
//                    }
                    standardMarketDTOs.add(bean);
                }
            }
            standardMatchMarketDTO.setMarketList(standardMarketDTOs);
            Request<StandardMatchMarketDTO> request = new Request<>();
            request.setData(standardMatchMarketDTO);
            request.setLinkId(UUID.randomUUID().toString().replace("-", "") + "_trade");
            request.setDataSourceTime(System.currentTimeMillis());
            Response<String> response = iTradeMarketOddsApi.putTradeMarketOdds(request);
            log.info("比分修改盘口值：{} , response:{}", JSON.toJSONString(request),JSONObject.toJSONString(response));
        }
    }
}
