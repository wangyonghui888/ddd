package com.panda.sport.rcs.predict.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitMapper;
import com.panda.sport.rcs.mapper.RcsUserConfigMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mapper.limit.RcsLabelLimitConfigMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastMapper;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastPlayMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecast;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastPlay;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecast;
import com.panda.sport.rcs.pojo.vo.predict.pending.RcsPredictPendingForecastPlay;
import com.panda.sport.rcs.predict.common.ForecastPlayIds;
import com.panda.sport.rcs.predict.service.ForecastPendingService;
import com.panda.sport.rcs.predict.service.ForecastService;
import com.panda.sport.rcs.predict.service.pending.RcsPredictPendingForecastPlayService;
import com.panda.sport.rcs.predict.service.pending.RcsPredictPendingForecastService;
import com.panda.sport.rcs.predict.vo.ForecastScopeVo;
import com.panda.sport.rcs.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 预测共用service
 **/
@Service("predictCommonService")
@Slf4j
public class PredictCommonServiceImpl {

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    RcsPredictForecastPlayMapper rcsPredictForecastPlayMapper;

    @Autowired
    private RcsPredictForecastMapper rcsPredictForecastMapper;

    @Autowired
    private RcsPredictPendingForecastPlayService rcsPredictPendingForecastPlayService;

    @Autowired
    private RcsPredictPendingForecastService rcsPredictPendingForecastService;

    @Autowired
    TUserMapper userMapper;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    RcsUserConfigMapper rcsUserConfigMapper;
 
    @Autowired
    RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;
    @Autowired
    RcsLabelLimitConfigMapper labelLimitConfigMapper;

    /**
     * 足球  根据玩法 获取处理对应计算forecast的service
     */
    public ForecastService getFootPlayForecastService(Integer playId) {
        ForecastService service = null;
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;

        if (Arrays.asList(letPoint).contains(playId)) {
            return SpringContextUtils.getBean("footPlayLetPointForecastService");
        }
        
        //独赢，独赢需要走让球的forecast ，bug-15954
        Integer aloneWin[] = ForecastPlayIds.aloneWin;

        if (Arrays.asList(aloneWin).contains(playId)) {
            return SpringContextUtils.getBean("footPlayAloneWinForecastService");
        }
        
        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            return SpringContextUtils.getBean("footPlayBigSmallForecastService");
        }
        return null;
    }

    /**
     * 足球  根据玩法 获取处理对应计算forecast的service
     */
    public ForecastPendingService getFootPlayPendingForecastService(Integer playId) {
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;

        if (Arrays.asList(letPoint).contains(playId)) {
            return SpringContextUtils.getBean("footPlayLetPointPendingForecastService");
        }

        Integer aloneWin[] = ForecastPlayIds.aloneWin;

        if (Arrays.asList(aloneWin).contains(playId)) {
            return SpringContextUtils.getBean("footPlayAloneWinPendingForecastService");
        }

        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            return SpringContextUtils.getBean("footPlayBigSmallPendingForecastService");
        }
        return null;
    }

    /**
     * 足球  根据玩法 获取处理对应计算forecast的service
     */
    public ForecastService getFootPlaceNumForecastService(Integer playId) {
        ForecastService service = null;
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;
        if (Arrays.asList(letPoint).contains(playId)) {
            return SpringContextUtils.getBean("footPlaceNumLetPointForecastService");
        }
        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            return SpringContextUtils.getBean("footPlaceNumBigSmallForecastService");
        }
        return null;
    }


    /**
     * 足球  根据玩法 获取处理对应计算forecast的service
     */
    public ForecastService getFootGrainedForecastService(Integer playId) {
        ForecastService service = null;
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;
        if (Arrays.asList(letPoint).contains(playId)) {
            return SpringContextUtils.getBean("footGrainedLetPointForecastService");
        }
        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            return SpringContextUtils.getBean("footGrainedBigSmallForecastService");
        }
        return null;
    }

    /**
     * 足球  根据玩法 获取处理对应计算forecast的service
     */
    public ForecastPendingService getFootGrainedPendingForecastService(Integer playId) {
        //让球
        Integer letPoint[] = ForecastPlayIds.letPoint;
        if (Arrays.asList(letPoint).contains(playId)) {
            return SpringContextUtils.getBean("footGrainedLetPointPendingForecastService");
        }
        //大小
        Integer bigSmall[] = ForecastPlayIds.bigSmall;
        if (Arrays.asList(bigSmall).contains(playId)) {
            return SpringContextUtils.getBean("footGrainedBigSmallPendingForecastService");
        }
        return null;
    }


    /**
     * rcs_predict_forecast_play 表保存数据
     *
     * @param list
     */
    public void updateRcsPredictForecastPlay(List<RcsPredictForecastPlay> list) {
        RcsPredictForecastPlay bean = list.get(0);
        //异步存库
        HashMap<String, String> mqMap = new HashMap<>();
        mqMap.put("time", "" + System.currentTimeMillis());
        String hashKey = String.format("mq_data_rcs_predict_forecast_play:%s_%s_%s_%s", bean.getMatchId(), bean.getPlayId(), bean.getMatchType(), bean.getPlaceNum());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        producerSendMessageUtils.sendMsg("mq_data_rcs_predict_forecast_play", "", uuid, JSONObject.toJSONString(list), mqMap, hashKey);
        log.info("rcs_predict_forecast_play表mq入库完成:订单号:{}", uuid);
    }

    public void updateRcsPredictPendingForecastPlay(List<RcsPredictPendingForecastPlay> list) {
        //入库新表
        if (!CollectionUtils.isEmpty(list)) {
            RcsPredictPendingForecastPlay rcsPredictPendingForecastPlay = list.get(0);
            rcsPredictPendingForecastPlayService.remove(new LambdaQueryWrapper<RcsPredictPendingForecastPlay>()
                    .eq(RcsPredictPendingForecastPlay::getPlayId, rcsPredictPendingForecastPlay.getPlayId())
                    .eq(RcsPredictPendingForecastPlay::getMatchId, rcsPredictPendingForecastPlay.getMatchId())
                    .eq(RcsPredictPendingForecastPlay::getMatchType, rcsPredictPendingForecastPlay.getMatchType())
                    .eq(RcsPredictPendingForecastPlay::getPlaceNum, rcsPredictPendingForecastPlay.getPlaceNum()));
            rcsPredictPendingForecastPlayService.saveBatch(list);
        }
    }

    /**
     * rcs_predict_forecast 表保存数据
     *
     * @param list
     */
    public void updateRcsPredictForecast(List<RcsPredictForecast> list) {
        RcsPredictForecast bean = list.get(0);
        //异步存库
        HashMap<String, String> mqMap = new HashMap<>();
        mqMap.put("time", "" + System.currentTimeMillis());
        String hashKey = "rcs.risk.predict.forecast.match_id.%s.match_type.%s.play_id.%s.market_id.%s.odds_item.%s.bet_score.%s";
        hashKey = String.format(hashKey, bean.getMatchId(), bean.getMatchType(), bean.getPlayId(), bean.getMarketId(),bean.getOddsItem(), StringUtils.isEmpty(bean.getBetScore()) ? "0:0" : bean.getBetScore());

        producerSendMessageUtils.sendMsg("mq_data_rcs_predict_forecast", "", "", JSONObject.toJSONString(list), mqMap, hashKey);
        log.info("rcs_predict_forecast表mq入库完成:订单号:{}", hashKey);
    }

    public void updateRcsPredictPendingForecast(List<RcsPredictPendingForecast> list) {
        //存库
        if (!CollectionUtils.isEmpty(list)) {
            RcsPredictPendingForecast rcsPredictPendingForecast = list.get(0);
            rcsPredictPendingForecastService.remove(new LambdaQueryWrapper<RcsPredictPendingForecast>()
                    .eq(RcsPredictPendingForecast::getPlayId, rcsPredictPendingForecast.getPlayId())
                    .eq(RcsPredictPendingForecast::getMatchId, rcsPredictPendingForecast.getMatchId())
                    .eq(RcsPredictPendingForecast::getMatchType, rcsPredictPendingForecast.getMatchType())
                    .eq(RcsPredictPendingForecast::getMarketId, rcsPredictPendingForecast.getMarketId())
                    .eq(RcsPredictPendingForecast::getOddsItem, rcsPredictPendingForecast.getOddsItem())
                    .eq(RcsPredictPendingForecast::getBetScore, rcsPredictPendingForecast.getBetScore()));
            rcsPredictPendingForecastService.saveBatch(list);
        }
    }


    /**
     * 获取让分比分区间
     */
    public ForecastScopeVo getLetPointForecastScopeVo(Integer playId) {
        ForecastScopeVo forecastScopeVo = new ForecastScopeVo();
        Integer min = -12;
        Integer max = 12;
        //全场让分
        if (Arrays.asList(new Integer[]{4, 128, 306,334}).contains(playId)) {
            min = -8;
            max = 8;
        }
        //半场让分
        if (Arrays.asList(new Integer[]{19, 130, 308}).contains(playId)) {
            min = -5;
            max = 5;
        }
        forecastScopeVo.setMin(min);
        forecastScopeVo.setMax(max);
        return forecastScopeVo;
    }

    /**
     * 获取大小比分区间
     */
    public ForecastScopeVo getBigSmallForecastScopeVo(Integer playId) {
        ForecastScopeVo forecastScopeVo = new ForecastScopeVo();
        Integer min = 0;
        Integer max = 30;
        //全场大小
        if (Arrays.asList(new Integer[]{2, 127, 307,335}).contains(playId)) {
            max = 16;
        }
        //半场大小
        if (Arrays.asList(new Integer[]{18, 309, 332}).contains(playId)) {
            max = 8;
        }
        forecastScopeVo.setMin(min);
        forecastScopeVo.setMax(max);
        return forecastScopeVo;
    }
}