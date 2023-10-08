package com.panda.sport.rcs.gts.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.gts.config.GtsInitConfig;
import com.panda.sport.rcs.gts.service.GtsCommonService;
import com.panda.sport.rcs.gts.service.GtsThirdApiService;
import com.panda.sport.rcs.gts.util.CopyUtils;
import com.panda.sport.rcs.gts.util.HttpUtil;
import com.panda.sport.rcs.gts.util.SystemThreadLocal;
import com.panda.sport.rcs.gts.vo.*;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketMapper;
import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.mapper.StandardSportTournamentMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.panda.sport.rcs.gts.common.Constants.*;

/*
 *gTS相关接口
 **/
@Service(connections = 5, retries = 0, timeout = 3000)
@Slf4j
@org.springframework.stereotype.Service
public class GtsApiServiceImpl implements GtsThirdApiService {

    @Resource
    GtsInitConfig gtsConfig;
    @Resource
    RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;
    @Autowired
    GtsCommonService gtsCommonService;


    /**
     * 获取额度
     * 由于gts无查询限额接口  通过投注接口 设置一个巨大的金额投注  会返回一个最大可投额度
     * {
     *     "isBetAllowed": false,
     *     "maxAllowedStake": 300000.0,
     *     "betDelay": null
     * }
     * @param extendBeanList
     */
    @Override
    public Long getLimit(List<ExtendBean> extendBeanList, Integer seriesType) {
        Long defaultLimt = 100000000L;
        try {
//            if(1==1){
//                return defaultLimt;
//            }
//            //单关 跟进投注项-赔率 缓存查找
//            if (extendBeanList.size() == 1) {
//                ExtendBean extendBean = extendBeanList.get(0);
//                String key = String.format(GTS_SELECTION_MAXLIMIT, extendBean.getSelectId(), extendBean.getOdds());
//                String cache = RcsLocalCacheUtils.getValue(key, redisClient::get, 10 * 1000L);
//                log.info("::{}::请求限额从缓存获取:{}", cache, key);
//                if (StringUtils.isNotEmpty(cache) && new BigDecimal(cache).compareTo(BigDecimal.ZERO) > 0) {
//                    return new BigDecimal(cache).longValue();
//                }
//            }
//            log.info("::{}::请求限额返回默认", defaultLimt);
            //return defaultLimt;

            //列表扩展对象
            List<GtsExtendBean> gtsExtendBeanList = CopyUtils.clone(extendBeanList, GtsExtendBean.class);
            String orderNo = UUID.randomUUID().toString().replace("-", "");
            SystemThreadLocal.set("ordreNo", orderNo);
            gtsExtendBeanList.forEach(bean -> {
                bean.setOrderId(orderNo);
            });
            //填充基础信息 查询第三方原始数据
            gtsCommonService.convertAllParam(gtsExtendBeanList);

            GtsBetResultVo gtsAssessmentBet = gtsAssessmentBet(gtsExtendBeanList, 1L, seriesType);
            log.info("获取限额返回数据：{}", gtsAssessmentBet.getMessage());
            JSONObject json = JSONObject.parseObject(gtsAssessmentBet.getMessage());
            String maxAllowedStake = json.getString("maxAllowedStake");
            return new BigDecimal(maxAllowedStake).longValue();
        } catch (Exception e) {
            log.error("获取限额异常：{}：{}", e.getMessage(), e);
        }
        return defaultLimt;
    }
    
    /**
     * 下注 评估
     * totalMoney 投注总金额
     *
     * @param extendBeanList
     */
    @Override
    public GtsBetResultVo gtsAssessmentBet(List<GtsExtendBean> extendBeanList, Long totalMoney, Integer seriesType) {
        ExtendBean extendBean = extendBeanList.get(0);
        //组装请求gtsvo
        GtsBetAssessmentRequestVo assessmentRequestVo = new GtsBetAssessmentRequestVo();
        assessmentRequestVo.setBetId(extendBean.getOrderId());
        assessmentRequestVo.setCurrencyCode("CNY");
        assessmentRequestVo.setPlayerId(extendBean.getUserId());
        assessmentRequestVo.setTotalStake(new BigDecimal(totalMoney).divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP));
        List<GtsBetAssessmentLegsVo> legsVoList = new ArrayList<>();
        //投注项依次处理
        extendBeanList.forEach(bean -> {
            GtsBetAssessmentLegsVo legs = new GtsBetAssessmentLegsVo();
            legs.setGameState(bean.getIsScroll().equals("1") ? GTS_INPLAY : GTS_PREMATCH);
            legs.setPrice(new BigDecimal(bean.getOdds()));
            //第三方原始数据 从上游获得 三方投注项Id：BG:9883898:145519887:0:437813013 数据源+赛事id+原始盘口id+序号+原始投注项Id
            legs.setSelectionId(bean.getBetgeniusContent().getSelectionId());
            legsVoList.add(legs);
        });
        //串关方式设置
        String getSystemBetType = getSystemBetType(extendBeanList, seriesType);
        if(StringUtils.isNotEmpty(getSystemBetType)){
            assessmentRequestVo.setSystemBetType(getSystemBetType);
        }
        assessmentRequestVo.setLegs(legsVoList);
        log.info("::{}::请求gts Bet Assessment Api参数:{}", SystemThreadLocal.get().get("orderNo"), JSONObject.toJSONString(assessmentRequestVo));
        /**
         * 获取token
         */
        GtsAuthorizationVo authorizationVo = getToken(gtsConfig.getBetAssessClientId(), gtsConfig.getBetAssessClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 1);
        //第三方请求下注
        GtsBetAssessmentResVo assessmentResuestVo = gtsBetAssessment(assessmentRequestVo, authorizationVo);
        //如果超过最大额度  缓存该投注项 该赔率的最大可投  用户查询限额使用
//        if (!assessmentResuestVo.getIsBetAllowed() && extendBeanList.size() == 1 && (new BigDecimal(assessmentResuestVo.getMaxAllowedStake()).compareTo(BigDecimal.ZERO) > 0)) {
//            String key = String.format(GTS_SELECTION_MAXLIMIT, extendBean.getSelectId(), extendBean.getOdds());
//            redisClient.setExpiry(key, assessmentResuestVo.getMaxAllowedStake(), 30L);
//            log.info("::{}::限额设置缓存:{}", assessmentResuestVo.getMaxAllowedStake(), key);
//        }
        //组装内部返回结果
        GtsBetResultVo resultVo = new GtsBetResultVo();
        resultVo.setDelayTime( getDelayTime(assessmentResuestVo.getBetDelay()));
        resultVo.setMessage(JSONObject.toJSONString(assessmentResuestVo));
        resultVo.setStatus(assessmentResuestVo.getIsBetAllowed() ? "ACCEPTED" : "REJECTED");
        resultVo.setThridValue(JSONObject.toJSONString(assessmentResuestVo));
        resultVo.setTickeId(extendBean.getOrderId());
        return resultVo;
    }

    /**
     * 下注 结果确认
     * totalMoney 投注总金额
     *
     * @param extendBeanList
     */
    @Override
    public String gtsReceiveBet(List<GtsExtendBean> extendBeanList, Long totalMoney, Integer seriesType,String status) {
        ExtendBean extendBean = extendBeanList.get(0);
        //组装请求gtsvo
        GtsBetReceiverRequestVo receiverRequestVo = new GtsBetReceiverRequestVo();
        receiverRequestVo.setId(extendBean.getOrderId());
        receiverRequestVo.setBetPlacedTimestampUTC(getUtcTime());
        receiverRequestVo.setBetUpdatedTimestampUTC(getUtcTime());
        receiverRequestVo.setBookmakerName("onyxcrown");
        receiverRequestVo.setCurrencyCode("CNY");
        receiverRequestVo.setPlayerId(extendBean.getUserId());
        receiverRequestVo.setPriority(1);
        receiverRequestVo.setStatus(status.equals(ACCEPTED) ? "Open" : "Cancelled");
        receiverRequestVo.setTotalStake(totalMoney/100);
        receiverRequestVo.setPayout(new BigDecimal(totalMoney / 100).setScale(0));
        //串关方式设置
        String getSystemBetType = getSystemBetType(extendBeanList, seriesType);
        if(StringUtils.isNotEmpty(getSystemBetType)){
            receiverRequestVo.setSystemBetType(getSystemBetType);
        }
        //投注项依次处理 legs
        List<GtsBetReceiveLegsVo> legsVoList = new ArrayList<>();
        extendBeanList.forEach(bean -> {
            GtsBetReceiveLegsVo legs = new GtsBetReceiveLegsVo();
            legs.setGameState(bean.getIsScroll().equals("1") ? GTS_INPLAY : GTS_PREMATCH);
            legs.setPrice(bean.getOrderMoney());
            legs.setStatus(status.equals(ACCEPTED) ? "Open" : "Cancelled");
            legs.setBookmakerContent(bean.getBookmakerContent());
            legs.setBetgeniusContent(bean.getBetgeniusContent());
            legsVoList.add(legs);
        });
        receiverRequestVo.setLegs(legsVoList);
        log.info("::{}::请求gts Bet Receive Api参数:{}", SystemThreadLocal.get().get("orderNo"), JSONObject.toJSONString(receiverRequestVo));
        /**
         * 获取token
         */
        GtsAuthorizationVo authorizationVo = getToken(gtsConfig.getBetReceiverClientId(), gtsConfig.getBetReceiverClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 2);
        //第三方请求下注
        GtsBetReceiverResVo receiverResVo = gtsBetReceiver(receiverRequestVo,authorizationVo);
        log.info("::{}::请求gts Bet Receive 结束:{}", SystemThreadLocal.get().get("orderNo"), JSONObject.toJSONString(receiverResVo));
        return "";
    }

    /**
     * Bet Assessment api 投注请求
     * @param vo
     * @authorizationVo token信息
     * @return
     */
    private GtsBetAssessmentResVo gtsBetAssessment(GtsBetAssessmentRequestVo vo,GtsAuthorizationVo authorizationVo) {
        try {
            Map<String, String> headMap = new HashMap<>();
            headMap.put("x-api-key", gtsConfig.getBetAssessmentApiKey());
            headMap.put("Authorization", "Bearer " + authorizationVo.getAccessToken());
            String data = HttpUtil.post(gtsConfig.getBetAssessmentUrl(), JSONObject.toJSONString(vo),headMap);
            System.out.println(JSONObject.toJSON(vo));
            log.info("::{}::请求gts Bet Assessment Api返回:{}", SystemThreadLocal.get().get("orderNo"), data);
            GtsBetAssessmentResVo gtsBetAssessmentResuestVo = JSONObject.parseObject(data, GtsBetAssessmentResVo.class);
            return gtsBetAssessmentResuestVo;
        } catch (Exception e) {
            log.error("::{}::Bet Assessment api 请求异常{}:{}", SystemThreadLocal.get().get("orderNo"), e.getMessage(), e);
            throw new RcsServiceException("Bet Assessment api 请求异常"+e.getMessage());
        }
    }

    /**
     * Bet receiver api 投注 确认 请求
     * @param vo
     * @authorizationVo token信息
     * @return
     */
    private GtsBetReceiverResVo gtsBetReceiver(GtsBetReceiverRequestVo vo,GtsAuthorizationVo authorizationVo) {
        GtsBetReceiverResVo gtsBetAssessmentResuestVo = new GtsBetReceiverResVo();
        try {
            Map<String, String> headMap = new HashMap<>();
            headMap.put("x-api-key", gtsConfig.getBetReceiverApiKey());
            headMap.put("Authorization", "Bearer " + authorizationVo.getAccessToken());
            String data = HttpUtil.post(gtsConfig.getBetReceiverApiUrl(), JSONObject.toJSONString(vo),headMap);
            log.info("::{}::请求gts Bet Receiver Api返回:{}", SystemThreadLocal.get().get("orderNo"), data);
            if (data != null && data.equals("")) {
                gtsBetAssessmentResuestVo.setCode(200);
                gtsBetAssessmentResuestVo.setData("成功");
            }else {
                log.info("::{}::Bet receiver api 请求失败:{}", SystemThreadLocal.get().get("orderNo"), data);
                gtsBetAssessmentResuestVo.setCode(-1);
                gtsBetAssessmentResuestVo.setData("失败：" + data);
            }
        } catch (Exception e) {
            log.error("::{}::Bet Assessment api 请求异常{}:{}", SystemThreadLocal.get().get("orderNo"), e.getMessage(), e);
            gtsBetAssessmentResuestVo.setCode(-1);
            gtsBetAssessmentResuestVo.setData("失败：" + e.getMessage());
        }
        return gtsBetAssessmentResuestVo;
    }

    /**
     * 获取 token
     * @param clientId 平台id
     * @param clientSecret 平台秘钥
     * @param grantType
     * @param url 请求地址
     * @param type 1 Bet Assessment  API           2 Bet Feed Receiver API
     * @return
     */
    private synchronized GtsAuthorizationVo getToken(String clientId, String clientSecret, String grantType,String url,Integer type) {
        //先从缓存获取  缓存不存在则从第三方接口获取
        String tokenCache = RcsLocalCacheUtils.getValue(String.format(GTS_TOKEN, type), redisClient::get, 10 * 60L);
        if(StringUtils.isNotEmpty(tokenCache)){
            GtsAuthorizationVo vo = JSONObject.parseObject(tokenCache, GtsAuthorizationVo.class);
            log.info("::{}::本次从缓存中获取token", SystemThreadLocal.get().get("orderNo"));
            return vo;
        }
        //组装请求参数
        Map<String,String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grant_type", grantType);
        log.info("::获取token请求::{}", JSONObject.toJSON(map));
        try {
            String data = HttpUtil.post(url, map, true);
            log.error("::获取token请求返回::{}", data);
            GtsAuthorizationVo vo = JSONObject.parseObject(data, GtsAuthorizationVo.class);
            vo.setType(type);
            //拿到token存缓存 Expiress是接口返回的token失效时间 提前20分钟过期 避免临界点问题
            redisClient.setExpiry(String.format(GTS_TOKEN, type), JSONObject.toJSONString(vo), vo.getExpiresIn() - 20 * 60L);
            RcsLocalCacheUtils.getValue(String.format(GTS_TOKEN, type), redisClient::get, 10 * 60L);
            //更新所有机器缓存
            sendMessage.sendMessage(GTS_TOKEN_TOPIC, vo);
            return vo;
        } catch (Exception e) {
            log.error("::获取token请求异常::{}:{}", e.getMessage(), e);
            throw new RcsServiceException("获取token请求异常");
        }
    }

    /**
     * 获取延时  格式:00:00:05
     * @return
     */
    public int getDelayTime(String betDelay){
        if (StringUtils.isNotEmpty(betDelay)) {
            String arr[] = betDelay.split(":");
            int hour = Integer.valueOf(arr[0]);
            int minute = Integer.valueOf(arr[1]);
            int second = Integer.valueOf(arr[2]);
            int all = hour * 60 * 60 + minute * 60 + second;
            return all;
        }
        return 0;
    }

    /**
     * 获取第三方串关字符串 以下为第三方定义
     * "SystemBetTypes": {
     * "BXMUL-2L": [ 2 ],
     * "BXMUL-3L": [ 3 ],
     * "BXMUL-4L": [ 4 ],
     * "BXMUL-5L": [ 5 ],
     * "BXMUL-6L": [ 6 ],
     * "BXMUL-7L": [ 7 ],
     * "BXMUL-8L": [ 8 ],
     * "BXMUL-9L": [ 9 ],
     * "BXMUL-10L": [ 10 ],
     * "BXMUL-11L": [ 11 ],
     * "BXMUL-12L": [ 12 ],
     * "BXMUL-13L": [ 13 ],
     * "BXMUL-14L": [ 14 ],
     * "PATENT": [ 1, 2, 3 ],
     * "LUCKY15": [ 1, 2, 3, 4 ],
     * "LUCKY31": [ 1, 2, 3, 4, 5 ],
     * "LUCKY63": [ 1, 2, 3, 4, 5, 6 ],
     * "TRIXIE": [ 2, 3 ],
     * "YANKEE": [ 2, 3, 4 ],
     * "SUPYANKEE": [ 2, 3, 4, 5 ],
     * "HEINZ": [ 2, 3, 4, 5, 6 ],
     * "SUPHEINZ": [ 2, 3, 4, 5, 6, 7 ],
     * "GOLIATH": [ 2, 3, 4, 5, 6, 7, 8 ]
     * }
     *
     * @param extendBeanList
     * @param seriesType
     * @return
     */
    private String getSystemBetType(List<GtsExtendBean> extendBeanList, int seriesType) {
        //单关无需此参数
        if (seriesType == 1) {
            return "";
        }
        //获取M串N中的M
        Integer type = SeriesTypeUtils.getSeriesType(seriesType);
        Integer count = SeriesTypeUtils.getCount(seriesType, type);
        //m串1
        if (count == 1) {
            String systemBetType = String.format("BXMUL-%sL", extendBeanList.size());
            return systemBetType;
        } else {
            //m串n
            if (extendBeanList.size() == 3) {
                return "TRIXIE";
            }
            if (extendBeanList.size() == 4) {
                return "YANKEE";
            }
            if (extendBeanList.size() == 5) {
                return "SUPYANKEE";
            }
            if (extendBeanList.size() == 6) {
                return "HEINZ";
            }
            if (extendBeanList.size() == 7) {
                return "SUPHEINZ";
            }
            if (extendBeanList.size() == 8) {
                return "GOLIATH";
            }
            if (extendBeanList.size() > 8) {
                log.info("::{}::gts多串投注 最多支持8串", SystemThreadLocal.get().get("orderNo"));
                throw new RcsServiceException("gts多串投注 最多支持8串");
            }
        }
        throw new RcsServiceException("获取第三方串关参数异常");
    }

    /**
     * 获取接口所需时间参数格式
     * @return
     */
    public static String getUtcTime() {
        LocalDateTime localDateTime = new Date(System.currentTimeMillis()).toInstant().atOffset(ZoneOffset.of("+0")).toLocalDateTime();
        return localDateTime.toString()+"Z";
    }
}

