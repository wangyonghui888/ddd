package com.panda.sport.rcs.mts.sportradar.service.impl;

import com.panda.sport.data.rcs.api.PIMtsApiService;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mts.sportradar.vo.PinnacleMarketUpdated;
import com.panda.sport.rcs.mts.sportradar.vo.PinnacleMarketUpdatedSelection;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Objects;
import java.util.Optional;

/*
 *MTS相关接口
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class PIMtsServiceImpl implements PIMtsApiService {
  @Autowired private StandardMatchInfoMapper standardMatchInfoMapper;
  @Autowired private StandardSportMarketMapper standardSportMarketMapper;
  @Autowired private StandardSportMarketOddsMapper standardSportMarketOddsMapper;
  @Autowired private MongoTemplate mongoTemplate;

  /** 賽事 ID 轉成資料商的賽事 ID */
  @Override
  public Response<Long> getThirdMatchSourceId(Long matchId) {
    log.info("賽事ID {} 進入 getThirdMatchSourceId", matchId);
    StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(matchId);

    Optional.ofNullable(standardMatchInfo)
        .orElseThrow(() -> new NullPointerException("賽事ID " + matchId + "找不到 PI 轉換資料"));
    return Response.success(standardMatchInfo.getThirdMatchSourceId());
  }

  /** 盤口 ID 轉成資料商的盤口 ID */
  @Override
  public Response<Long> getThirdMarketSourceId(Long marketId) {
    log.info("盤口ID {} 進入 getThirdMarketSourceId", marketId);
    StandardSportMarket standardSportMarket = standardSportMarketMapper.selectById(marketId);

    Optional.ofNullable(standardSportMarket)
        .orElseThrow(() -> new NullPointerException("盤口ID " + marketId + "找不到 PI 轉換資料"));
    return Response.success(standardSportMarket.getThirdMarketSourceId());
  }

  @Override
  public Response<String> getThirdPlayOptionSourceId(Long playOptionId) {
    log.info("投注項ID {} 進入 getThirdPlayOptionSourceId", playOptionId);
    StandardSportMarketOdds standardSportMarketOdds =
        standardSportMarketOddsMapper.selectById(playOptionId);

    Optional.ofNullable(standardSportMarketOdds)
        .orElseThrow(() -> new NullPointerException("投注項ID " + playOptionId + "找不到 PI 轉換資料"));
    return Response.success(standardSportMarketOdds.getThirdOddsFieldSourceId());
  }

  @Override
  public Response<String> getMaxBetAmount(Long marketId, Long playOptionId) {
    log.info("賽事ID {} 投注項ID {} 進入 getMaxBetAmount", marketId, playOptionId);
    Long thirdMarketId = getThirdMarketSourceId(marketId).getData();
    String thirdPlayOptionId = getThirdPlayOptionSourceId(playOptionId).getData();

    PinnacleMarketUpdated pmu =
        mongoTemplate.findOne(
            Query.query(Criteria.where("_id").is(thirdMarketId)), PinnacleMarketUpdated.class);

    String maxStake = "0";
    if (Objects.nonNull(pmu)) {
      for (PinnacleMarketUpdatedSelection pmus : pmu.getSelections()) {
        if (StringUtils.equals(pmus.getId(), thirdPlayOptionId)) {
          maxStake = pmus.getMaxStake();
          break;
        }
      }
    }
    return Response.success(Long.parseLong(maxStake) * 10_0000);
  }
}
