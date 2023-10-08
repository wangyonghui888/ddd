package com.panda.sport.rcs.third.service.third.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.bts.ThirdBetParamDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.service.RcsSwitchService;
import com.panda.sport.rcs.third.config.GtsInitConfig;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.ThirdResultVo;
import com.panda.sport.rcs.third.entity.common.pojo.RcsGtsOrderExt;
import com.panda.sport.rcs.third.entity.gts.*;
import com.panda.sport.rcs.third.enums.GtsSportsEnum;
import com.panda.sport.rcs.third.enums.OrderStatusEnum;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.mapper.RcsGtsOrderExtMapper;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.third.service.reject.IOrderAcceptService;
import com.panda.sport.rcs.third.service.third.ThirdOrderBaseService;
import com.panda.sport.rcs.third.service.third.ThirdOrderService;
import com.panda.sport.rcs.third.util.CopyUtils;
import com.panda.sport.rcs.third.util.DateUtil;
import com.panda.sport.rcs.third.util.SystemThreadLocal;
import com.panda.sport.rcs.third.util.encrypt.ZipStringUtils;
import com.panda.sport.rcs.third.util.http.AsyncHttpUtil;
import com.panda.sport.rcs.third.util.http.HttpUtil;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.panda.sport.rcs.constants.RedisKey.REDIS_MATCH_MARKET_ODDS_NEW;
import static com.panda.sport.rcs.third.common.Constants.*;
import static com.panda.sport.rcs.third.common.NumberConstant.GTS_DEFAULT_DISCOUNT;

/**
 * @author Beulah
 * @date 2023/4/3 12:41
 * @description GTS业务逻辑处理
 */
@Service
@Slf4j
public class GTSServiceImpl extends ThirdOrderBaseService implements ThirdOrderService, InitializingBean {


    @Resource
    GtsInitConfig gtsConfig;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardSportTournamentMapper standardSportTournamentMapper;
    @Autowired
    StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    StandardSportMarketOddsMapper standardSportMarketOddsMapper;
    @Resource
    RcsStandardOutrightMatchInfoMapper rcsStandardOutrightMatchInfoMapper;
    @Autowired
    RcsLanguageInternationMapper languageInternationMapper;
    @Autowired
    RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @Autowired
    TOrderDetailMapper orderDetailMapper;
    @Autowired
    TOrderMapper orderMapper;

    @Resource
    IOrderHandlerService iOrderHandlerService;

    @Resource
    RcsGtsOrderExtMapper rcsGtsOrderExtMapper;

    @Resource
    JedisCluster jedisCluster;

    @Resource
    private GtsCallBack gtsCallBack;
    @Resource
    IOrderAcceptService orderAcceptService;
    @Resource
    RcsSwitchService rcsSwitchService;


    @Override
    public void afterPropertiesSet() throws Exception {
        ThirdStrategyFactory.register(OrderTypeEnum.GTS.getPlatFrom(), this);
    }

    @Override
    public Long getMaxBetAmount(ThirdBetParamDto dto) {
        List<ExtendBean> extendBeanList = dto.getExtendBeanList();
        String userId = extendBeanList.get(0).getUserId();
        ExtendBean extendBean = extendBeanList.get(0);
        Integer seriesType = extendBean.getSeriesType();
        Long defaultLimit = 100000000L;
        try {
            ThirdOrderExt ext = new ThirdOrderExt();
            ext.setList(extendBeanList);
            ext.setSeriesType(seriesType);
            ext.setPaTotalAmount(new BigDecimal("1"));
            ThirdResultVo gtsAssessmentBet = gtsAssessmentBet(ext);
            log.info("::{}::限额-请求GTS返回数据={}", userId, JSONObject.toJSONString(gtsAssessmentBet.getThirdRes()));
            JSONObject json = JSONObject.parseObject(gtsAssessmentBet.getThirdRes());
            String maxAllowedStake = json.getString("maxAllowedStake");
            return new BigDecimal(maxAllowedStake).longValue();
        } catch (Exception e) {
            log.error("::{}::限额-请求GTS异常", userId, e);
        }
        return defaultLimit;

    }

    @Override
    public ThirdResultVo placeBet(ThirdOrderExt ext) {
        MDC.put(LINKID, ext.getLinkId());
        try {
            if (ext.getSeriesType() == 1) {
                return gtsAssessmentBet(ext);
            }
            //串关过滤GTS的赛事 风控处理
            List<ExtendBean> gtsList = new ArrayList<>();
            List<ExtendBean> paList = new ArrayList<>();
            ext.getList().forEach(e -> {
                if ("GTS".equalsIgnoreCase(e.getItemBean().getPlatform())) {
                    gtsList.add(e);
                } else {
                    paList.add(e);
                }
            });
            log.info("::{}::投注-请求GTS={},PA={}", ext.getOrderNo(), JSONObject.toJSONString(gtsList), JSONObject.toJSONString(paList));
            //走gts接拒
            ext.setList(gtsList);
            ThirdResultVo gtsAssessmentBet = gtsAssessmentBet(ext);
            log.info("::{}::投注-请求GTS返回数据={}", ext.getOrderNo(), JSONObject.toJSONString(gtsAssessmentBet.getThirdRes()));
            if (gtsAssessmentBet.getThirdOrderStatus() == 1) {
                //gts接拒通过，剩下继续走pa接拒
                if (!CollectionUtils.isEmpty(paList)) {
                    ext.setList(paList);
                    log.info("::{}::投注-串关PA赛事走内部接拒={}", ext.getOrderNo(), JSONObject.toJSONString(paList));
                    iOrderHandlerService.orderByPa(ext);
                }
            } else {
                log.warn("::{}::投注-串关GTS操盘赛事拒单", ext.getOrderNo());
            }
            return gtsAssessmentBet;
        } catch (Exception e) {
            log.error("::{}::投注-请求GTS异常", ext.getOrderNo(), e);
        }
        MDC.remove(LINKID);
        return null;
    }

    @Override
    public Boolean orderConfirm(ThirdOrderExt ext) {
        gtsReceiveBet(ext);
        return true;
    }


    @Override
    public List<GtsExtendBean> convertThirdParam(ThirdOrderExt ext) {
        List<GtsExtendBean> gtsExtendBeanList = CopyUtils.clone(ext.getList(), GtsExtendBean.class);
        if (ext.getOrderNo() == null) {
            String orderNo = UUID.randomUUID().toString().replace("-", "");
            ext.setOrderNo(orderNo);
        }
        gtsExtendBeanList.forEach(bean -> {
            //针对限额请求创建一个注单号
            if (bean.getOrderId() == null) {
                bean.setOrderId(ext.getOrderNo());
            }
        });
        for (GtsExtendBean bean : gtsExtendBeanList) {
            Map<String, String> map = getThirdData(bean.getMatchId(), bean.getMarketId(), bean.getItemBean().getPlayOptionsId().toString(), bean.getIsChampion());
            GtsBetGeniusContentVo gtsBetGeniusContentVo = JSONObject.parseObject(JSONObject.toJSONString(map), GtsBetGeniusContentVo.class);
            BookmakerContentContentVo bookmakerContentContentVo = JSONObject.parseObject(JSONObject.toJSONString(map), BookmakerContentContentVo.class);
            bean.setBetgeniusContent(gtsBetGeniusContentVo);
            bean.setBookmakerContent(bookmakerContentContentVo);
            log.info("::{}::获取GTS注单[{}]原始数据, 结果{}", ext.getOrderNo(), bean.getItemBean().getBetNo(), JSONObject.toJSON(map));
        }
        return gtsExtendBeanList;

    }


    public List<GtsExtendBean> convertThirdParam(ThirdOrderExt ext, GtsBetReceiverCache cache) {
        List<GtsExtendBean> gtsExtendBeanList = CopyUtils.clone(ext.getList(), GtsExtendBean.class);
        if (ext.getOrderNo() == null) {
            String orderNo = UUID.randomUUID().toString().replace("-", "");
            ext.setOrderNo(orderNo);
        }
        gtsExtendBeanList.forEach(bean -> {
            //针对限额请求创建一个注单号
            if (bean.getOrderId() == null) {
                bean.setOrderId(ext.getOrderNo());
            }
        });
        for (GtsExtendBean bean : gtsExtendBeanList) {
            Map<String, String> map = getThirdData(bean.getMatchId(), bean.getMarketId(), bean.getItemBean().getPlayOptionsId().toString(), bean.getIsChampion());
            GtsBetGeniusContentVo gtsBetGeniusContentVo = JSONObject.parseObject(JSONObject.toJSONString(map), GtsBetGeniusContentVo.class);
            BookmakerContentContentVo bookmakerContentContentVo = JSONObject.parseObject(JSONObject.toJSONString(map), BookmakerContentContentVo.class);
            bean.setBetgeniusContent(gtsBetGeniusContentVo);
            bean.setBookmakerContent(bookmakerContentContentVo);
            log.info("::{}::获取GTS注单[{}]原始数据, 结果{}", ext.getOrderNo(), bean.getItemBean().getBetNo(), JSONObject.toJSON(map));
            //映射关系
            cache.getPlayOptionMap().put(bean.getItemBean().getPlayOptionsId().toString(), gtsBetGeniusContentVo.getSelectionId());
        }
        return gtsExtendBeanList;
    }

    @Override
    public void orderCancel(ThirdOrderExt ext) {
        MDC.put(LINKID, ext.getOrderNo());
        //填充基础信息 查询第三方原始数据
        List<GtsExtendBean> extendBeanList = convertThirdParam(ext);
        ExtendBean extendBean = extendBeanList.get(0);
        //组装请求gtsvo
        GtsBetReceiverRequestVo receiverRequestVo = new GtsBetReceiverRequestVo();
        receiverRequestVo.setId(extendBean.getOrderId());
        receiverRequestVo.setBetPlacedTimestampUTC(DateUtil.getUtcTime());
        receiverRequestVo.setBetUpdatedTimestampUTC(DateUtil.getUtcTime());
        receiverRequestVo.setBookmakerName("onyxcrown");
        receiverRequestVo.setCurrencyCode("CNY");
        receiverRequestVo.setPlayerId(extendBean.getUserId());
        receiverRequestVo.setPriority(1);
        receiverRequestVo.setStatus(ext.getOrderStatus().equals(1) ? "Open" : "Cancelled");
        receiverRequestVo.setTotalStake(getGtsAmount(ext));
        receiverRequestVo.setPayout(getGtsAmount(ext));
        //receiverRequestVo.setPayout(gtsAmount.multiply(new BigDecimal(extendBean.getOdds()).subtract(new BigDecimal("1"))).divide(new BigDecimal("1"), 2, RoundingMode.FLOOR));
        //串关方式设置
        String getSystemBetType = getSystemBetType(ext.getList(), ext.getSeriesType());
        if (StringUtils.isNotEmpty(getSystemBetType)) {
            receiverRequestVo.setSystemBetType(getSystemBetType);
        }
        //投注项依次处理 legs
        List<GtsBetReceiveLegsVo> legsVoList = new ArrayList<>();
        extendBeanList.forEach(bean -> {
            GtsBetReceiveLegsVo legs = new GtsBetReceiveLegsVo();
            legs.setGameState(bean.getIsScroll().equals("1") ? GTS_INPLAY : GTS_PREMATCH);
            legs.setPrice(new BigDecimal(bean.getOdds()));
            legs.setStatus(ext.getOrderStatus().equals(1) ? "Open" : "Cancelled");
            legs.setBookmakerContent(bean.getBookmakerContent());
            legs.setBetgeniusContent(bean.getBetgeniusContent());
            legsVoList.add(legs);
        });
        receiverRequestVo.setLegs(legsVoList);
        //获取token
        GtsAuthorizationVo authorizationVo = getToken(gtsConfig.getBetReceiverClientId(), gtsConfig.getBetReceiverClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 2, ext.getOrderNo());
        //第三方请求下注
        GtsBetReceiverResVo gtsBetReceiverResVo = new GtsBetReceiverResVo();
        String betReceiverApiUrl = gtsConfig.getBetReceiverApiUrl();
        log.info("::{}::[取消]请求GTS url={} 参数:{}", ext.getOrderNo(), betReceiverApiUrl, JSONObject.toJSONString(receiverRequestVo));
        try {
            Map<String, String> headMap = new HashMap<>();
            headMap.put("x-api-key", gtsConfig.getBetReceiverApiKey());
            headMap.put("Authorization", "Bearer " + authorizationVo.getAccessToken());
            String data = HttpUtil.post(gtsConfig.getBetReceiverApiUrl(), JSONObject.toJSONString(receiverRequestVo), headMap);
            log.info("::{}::[取消]请求GTS url={} 返回:{}", ext.getOrderNo(), betReceiverApiUrl, data);
            if (data != null && data.equals("")) {
                gtsBetReceiverResVo.setCode(200);
                gtsBetReceiverResVo.setData("成功");
                updateOrderCancelFailedReason(ext, "取消成功");
            } else {
                log.info("::{}::[取消]请求GTS url={} 失败响应:{}", ext.getOrderNo(), betReceiverApiUrl, data);
                gtsBetReceiverResVo.setCode(-1);
                gtsBetReceiverResVo.setData("失败：" + data);
                updateOrderCancelFailedReason(ext, "取消失败:" + JSONObject.toJSONString(data));
            }
        } catch (Exception e) {
            if (e instanceof RcsServiceException) {
                //投注请求异常 记录 -
                if (((RcsServiceException) e).getCode() == 5031) {
                    String retryStatus = redisClient.get(String.format(GTS_RETRY_STATUS_CANCEL, ext.getOrderNo()));
                    if (StringUtils.isBlank(retryStatus) || (StringUtils.isNotBlank(retryStatus)) && Integer.parseInt(retryStatus) < gtsConfig.getRetryTime()) {
                        //手动重连
                        log.warn("::{}::请求GTS Bet-Assessment-Api 连接拒绝,丢入重试队列", ext.getOrderNo());
                        ext.setRetryType(2);
                        sendMessage.sendMessage("rcs_risk_third_order_retry", ext.getThird() + "_ORDER_RETRY", ext.getOrderNo(), ext);
                    }
                }
            }
            log.error("::{}::[取消]请求GTS url={} 出现异常", ext.getOrderNo(), betReceiverApiUrl, e);
            gtsBetReceiverResVo.setCode(-1);
            gtsBetReceiverResVo.setData("失败：" + e.getMessage());
            updateOrderCancelFailedReason(ext, "取消失败:" + e.getMessage());
        }
        log.info("::{}::[取消]请求GTS url={} 结束:{}", ext.getOrderNo(), betReceiverApiUrl, JSONObject.toJSONString(gtsBetReceiverResVo));
        MDC.remove(LINKID);
    }

    @Override
    public void updateOrderCancelFailedReason(ThirdOrderExt ext, String reason) {
        try {
            LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsGtsOrderExt::getOrderNo, ext.getOrderNo());
            RcsGtsOrderExt orderExt = rcsGtsOrderExtMapper.selectOne(wrapper);
            if (orderExt == null) {
                throw new RcsServiceException("订单:" + ext.getOrderNo() + "未找到");
            }
            orderExt.setRemark(reason);
            orderExt.setCancelStatus(2);
            rcsGtsOrderExtMapper.updateById(orderExt);
            log.info("::{}::注单发往{}取消,更新完成", ext.getOrderNo(), ext.getThird());
        } catch (Exception ex) {
            log.error("::{}::注单发往{}取消,更新数据库发生异常", ext.getOrderNo(), ext.getThird());
        }
    }


    /**
     * 取消注单 修改第三方注单状态
     */
    @Override
    public String updateThirdOrderStatus(OrderBean orderBean, String reason) {
        //0：待处理  1：已接单  2：拒单
        LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsGtsOrderExt::getOrderNo, orderBean.getOrderNo());
        RcsGtsOrderExt ext = rcsGtsOrderExtMapper.selectOne(wrapper);
        if (ext == null) {
            throw new RcsServiceException("订单信息未找到");
        }
        String third = ext.getThirdName();
        Integer cancel = ext.getCancelStatus();
        //幂等校验
        if (cancel == 1) {
            throw new RcsServiceException("订单已取消,不做重复取消处理");
        }
        ext.setStatus(ext.getStatus() + ",REJECTED");
        ext.setCancelStatus(1);
        ext.setCancelId(102);
        ext.setRemark(ext.getRemark() + "," + reason);
        rcsGtsOrderExtMapper.updateById(ext);
        log.info("::{}::业务主动取消注单,更新第三方{}订单表完成", ext.getOrderNo(), third);
        return ext.getThirdNo();
    }

    /**
     * 获取第三方赛事原始数据
     */
    private Map<String, String> getThirdData(String matchId, String paMarketId, String playOptionsId, Integer isChampion) {

        StopWatch sw = new StopWatch();
        sw.start();
        //联赛信息 用于获取 第三方联赛id 第三方联赛名称
        StandardSportTournament standardSportTournament = queryStandardTournament(matchId, isChampion);
        //第三方联赛id  命名根据第三方参数 命名
        String competitionId = standardSportTournament.getThirdTournamentSourceId().replace("bg:simple_tournament:", "");
        //第三方联赛名称 通过国际化获取
        String text = queryRcsLanguageInternation(matchId, standardSportTournament.getNameCode());
        JSONObject textJson = JSONObject.parseObject(text);
        String competitionName = textJson.getString("en");
        //投注项表可以获取信息 例子: BG:9883898:145519887:0:437813013	，规则：数据源+赛事id+原始盘口id+序号+原始投注项Id
        StandardSportMarketOdds standardSportMarketOdds = standardSportMarketOddsMapper.selectById(NumberUtils.toLong(playOptionsId, 0));
        String thirdOddsFieldSourceId = standardSportMarketOdds.getThirdOddsFieldSourceId();
        String arr[] = thirdOddsFieldSourceId.split(":");
        //第三方赛事id
        String fixtureId = arr[1];
        //第三方盘口id
        String marketId = arr[2];
        //第三方投注项目id
        String selectionId = arr[4];

        /**所有name参数 用于第三方后台显示的 已和对方沟通  他们后续会自己处理  我们目前传id即可**/
        //第三方赛事名称
        String fixtureName = fixtureId;
        //第三盘口名称
        String marketName = marketId;
        //第三方投注项目名称
        String selectionName = selectionId;
        Map<String, String> map = new HashMap<>();
        map.put("sportId", GtsSportsEnum.getByPaSportId(standardSportTournament.getSportId().intValue()).getGtsSportId().toString());
        map.put("sportName", GtsSportsEnum.getByPaSportId(standardSportTournament.getSportId().intValue()).getGtsSprotName());
        map.put("competitionId", competitionId);
        map.put("competitionName", competitionName);
        map.put("fixtureId", fixtureId);
        map.put("fixtureName", fixtureName);
        map.put("marketId", marketId);
        map.put("marketName", marketName);
        map.put("selectionId", selectionId);
        map.put("selectionName", selectionName);
        sw.stop();
        log.info("::{}::组装GTS三方数据耗时:{}", matchId, sw.getTotalTimeMillis());
        return map;
    }


    /**
     * 获取 token
     *
     * @param clientId     平台id
     * @param clientSecret 平台秘钥
     * @param grantType    gts参数 固定client_credentials
     * @param url          请求地址
     * @param type         1 Bet Assessment  API           2 Bet Feed Receiver API
     * @param orderNo      注单号，用来跟踪日志
     * @return token
     */
    private GtsAuthorizationVo getToken(String clientId, String clientSecret, String grantType, String url, Integer type, String orderNo) {
        //先从缓存获取  缓存不存在则从第三方接口获取
        String tokenKey = String.format(GTS_TOKEN, type);
//        try {
//            Object localCache = RcsLocalCacheUtils.timedCache.get(tokenKey);
//            if (Objects.nonNull(localCache)) {
//                log.info("::{}：：缓存key::{}::从本地缓存获取到的数据::{}", orderNo, tokenKey, localCache);
//                return JSONObject.parseObject((String) localCache, GtsAuthorizationVo.class);
//            }
//        } catch (Exception e) {
//            log.error("::{}::从本地获取token请求异常::", orderNo, e);
//            throw new RcsServiceException("从本地获取token请求异常");
//        }
        String redisCache = redisClient.get(tokenKey);
        if (StringUtils.isNotEmpty(redisCache)) {
            try {
                log.info("::{}::redis获取到GTS token::{}", orderNo, redisCache);
                GtsAuthorizationVo authorizationVo = JSONObject.parseObject(redisCache, GtsAuthorizationVo.class);
                //在有效期内 用于判断临界点
            /*long timed = System.currentTimeMillis() - authorizationVo.getRefreshTime();
            long expire = (authorizationVo.getExpiresIn() - 10 * 60L) * 1000L;
            if (timed < expire) {
                String key = String.format(GTS_TOKEN, authorizationVo.getType());
                RcsLocalCacheUtils.timedCache.put(key, authorizationVo, expire - timed - 1000);
            }*/
                String key = String.format(GTS_TOKEN, authorizationVo.getType());
                //本地缓存设置2s
                RcsLocalCacheUtils.timedCache.put(key, authorizationVo, 2000);
                return authorizationVo;
            } catch (Exception e) {
                log.error("::{}::从redis获取token请求异常::", orderNo, e);
                throw new RcsServiceException("从redis获取token请求异常");
            }
        }
        //组装请求参数
        Map<String, String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grant_type", grantType);
        log.info("::{}::去BG获取token请求::{}", orderNo, JSONObject.toJSON(map));
        try {
            /*boolean isRefresh = jedisCluster.setnx("gts:token:refresh", "1") == 1;
            if (isRefresh) {
                jedisCluster.expire("gts:token:refresh", 3);
            } else {
                Thread.sleep(1000);
                localCache = RcsLocalCacheUtils.timedCache.get(tokenKey);
                if (Objects.nonNull(localCache)) {
                    return (GtsAuthorizationVo) localCache;
                }
                return null;
            }*/
            String data = HttpUtil.post(url, map, true);
            log.info("::{}::去BG获取token请求返回::{}", orderNo, data);
            GtsAuthorizationVo vo = JSONObject.parseObject(data, GtsAuthorizationVo.class);
            vo.setType(type);
            vo.setRefreshTime(System.currentTimeMillis());
            //刷新最新的token 提前20分钟过期
            redisClient.setExpiry(String.format(GTS_TOKEN, type), JSONObject.toJSONString(vo), vo.getExpiresIn() - 20 * 60L);
//            RcsLocalCacheUtils.timedCache.put(String.format(GTS_TOKEN, type), JSONObject.toJSONString(vo), 2000);
            /*sendMessage.sendMessage(GTS_TOKEN_TOPIC, vo);
            jedisCluster.del("gts:token:refresh");*/
            return vo;
        } catch (Exception e) {
            //jedisCluster.del("gts:token:refresh");
            log.error("::{}::获取token请求异常::", orderNo, e);
            throw new RcsServiceException("获取token请求异常");
        }
    }


    /**
     * 获取延时  格式:00:00:05
     *
     * @return 延时秒数
     */
    private int getDelayTime(String betDelay) {
        if (StringUtils.isNotEmpty(betDelay)) {
            String[] arr = betDelay.split(":");
            int hour = Integer.parseInt(arr[0]);
            int minute = Integer.parseInt(arr[1]);
            int second = Integer.parseInt(arr[2]);
            return hour * 60 * 60 + minute * 60 + second;
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
     * @param extendBeanList 订单列表
     * @param seriesType     串关类型
     * @return 三方的串关类型
     */
    private String getSystemBetType(List<ExtendBean> extendBeanList, int seriesType) {
        //单关无需此参数
        if (seriesType == 1) {
            return "";
        }
        //获取M串N中的M
        Integer type = SeriesTypeUtils.getSeriesType(seriesType);
        Integer count = SeriesTypeUtils.getCount(seriesType, type);
        //m串1
        if (count == 1) {
            return String.format("BXMUL-%sL", extendBeanList.size());
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
                //log.info("::{}::gts多串投注 最多支持8串", SystemThreadLocal.get().get("orderNo"));
                throw new RcsServiceException("gts多串投注 最多支持8串");
            }
        }
        throw new RcsServiceException("获取第三方串关参数异常");
    }

    private ThirdResultVo gtsAssessmentBet(ThirdOrderExt ext) {
        String logIndex = ext.getOrderNo() == null ? ext.getList().get(0).getUserId() : ext.getOrderNo();
        String logAction = ext.getOrderNo() == null ? "限额" : "投注";
        BigDecimal gtsAmount = ext.getPaTotalAmount();
        //组装参数请求GTS
        GtsBetAssessmentRequestVo assessmentRequestVo = new GtsBetAssessmentRequestVo();
        if (ext.getOrderNo() != null) {
            //投注
            gtsAmount = getGtsAmount(ext);
            //1：自动接收更好的赔率 2：自动接受任何赔率变动 3：不自动接受赔率变动
            String acceptOdds = (1 == ext.getAcceptOdds()) ? "AcceptHigher" : (2 == ext.getAcceptOdds()) ? "AcceptAny" : "AcceptNone";
            assessmentRequestVo.setPriceChangeRule(acceptOdds);
        }
        //填充基础信息 查询第三方原始数据
        List<GtsExtendBean> extendBeanList = convertThirdParam(ext);
        ExtendBean extendBean = extendBeanList.get(0);
        assessmentRequestVo.setBetId(extendBean.getOrderId());
        assessmentRequestVo.setCurrencyCode("CNY");
        assessmentRequestVo.setPlayerId(extendBean.getUserId());
        assessmentRequestVo.setTotalStake(gtsAmount);
        List<GtsBetAssessmentLegsRequestVo> legsVoList = new ArrayList<>();
        //投注项依次处理
        extendBeanList.forEach(bean -> {
            GtsBetAssessmentLegsRequestVo legs = new GtsBetAssessmentLegsRequestVo();
            legs.setGameState(bean.getIsScroll().equals("1") ? GTS_INPLAY : GTS_PREMATCH);
            legs.setPrice(new BigDecimal(bean.getOdds()));
            //第三方原始数据 从上游获得 三方投注项Id：BG:9883898:145519887:0:437813013 数据源+赛事id+原始盘口id+序号+原始投注项Id
            legs.setSelectionId(bean.getBetgeniusContent().getSelectionId());
            legsVoList.add(legs);
        });
        //串关方式设置
        String getSystemBetType = getSystemBetType(ext.getList(), ext.getSeriesType());
        if (StringUtils.isNotEmpty(getSystemBetType)) {
            assessmentRequestVo.setSystemBetType(getSystemBetType);
        }
        assessmentRequestVo.setLegs(legsVoList);

        log.info("::{}::{}-请求GTS Bet-Assessment-Api参数:{}", logIndex, logAction, JSONObject.toJSONString(assessmentRequestVo));
        //获取token
        GtsAuthorizationVo authorizationVo = getToken(gtsConfig.getBetAssessClientId(), gtsConfig.getBetAssessClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 1, logIndex);
        //第三方请求下注
        GtsBetAssessmentResVo assessmentResVo = null;
        String betAssessmentUrl = gtsConfig.getBetAssessmentUrl();
        try {
            Map<String, String> headMap = new HashMap<>();
            headMap.put("x-api-key", gtsConfig.getBetAssessmentApiKey());
            headMap.put("Authorization", "Bearer " + authorizationVo.getAccessToken());
            StopWatch sw = new StopWatch();
            sw.start("GTS请求:" + gtsConfig.getBetAssessmentUrl());
            //redis存入临时缓存，处理数据商推送注单过来的数据
            /*String message = ZipStringUtils.gzip(JSONObject.toJSONString(ext));
            String redisKey = String.format(GTS_BET_PLACED_ORDER_NO, ext.getOrderNo());
            redisClient.setExpiry(redisKey, message, ORDER_REDIS_EXPIRED);*/
            //AsyncHttpUtil.postJson(gtsConfig.getBetAssessmentUrl(), JSONObject.toJSONString(assessmentRequestVo), true, headMap, gtsCallBack);
            //注单id线程中
            String orderNo = ext.getOrderNo();
            MDC.put("orderNo", orderNo);
            /*if ("1".equals(gtsConfig.getRefused())) {
                throw new RcsServiceException(5031, "模拟拒连");
            }*/
            String data = HttpUtil.post(gtsConfig.getBetAssessmentUrl(), JSONObject.toJSONString(assessmentRequestVo), headMap);
            sw.stop();
            log.info("::{}::{}-请求GTS url={} 返回:{},耗时:{}", logIndex, logAction, betAssessmentUrl, data, sw.getTotalTimeMillis());
            if (StringUtils.isNotBlank(data)) {
                redisClient.delete(String.format(ORDER_REQUEST_FAILED, orderNo));
            }
            assessmentResVo = JSONObject.parseObject(data, GtsBetAssessmentResVo.class);
        } catch (Exception e) {
            if (e instanceof RcsServiceException) {
                //投注请求异常 记录 -
                if (((RcsServiceException) e).getCode() == 5031 && "投注".equals(logAction)) {
                    String retryStatus = redisClient.get(String.format(GTS_RETRY_STATUS_BET, ext.getOrderNo()));
                    if (StringUtils.isBlank(retryStatus) || (StringUtils.isNotBlank(retryStatus)) && Integer.parseInt(retryStatus) < gtsConfig.getRetryTime()) {
                        //手动重连
                        log.warn("::{}::投注请求未发送成功,取消不用通知数据商", ext.getOrderNo());
                        redisClient.setExpiry(String.format(ORDER_REQUEST_FAILED, ext.getOrderNo()), "1", 20L);
                        log.warn("::{}::请求GTS Bet-Assessment-Api 连接拒绝,丢入重试队列", logIndex);
                        ext.setRetryType(1);
                        sendMessage.sendMessage("rcs_risk_third_order_retry", ext.getThird() + "_ORDER_RETRY", ext.getOrderNo(), ext);
                    }
                }
            }

            throw new RcsServiceException(logAction + "请求GTS Bet-Assessment-Api异常:" + e.getMessage());
        }
        if (assessmentResVo == null) {
            log.error("::{}::{}-请求GTS url={} 返回null", logIndex, logAction, betAssessmentUrl);
            throw new RcsServiceException(logAction + "请求GTS Bet-Assessment-Api返回null");
        }

        ThirdResultVo resultVo = new ThirdResultVo();
        int delayTime = getDelayTime(assessmentResVo.getBetDelay());
        resultVo.setDelay(delayTime);
        resultVo.setThirdRes(JSONObject.toJSONString(assessmentResVo));
        resultVo.setThirdNo(assessmentResVo.getBetId());
        if (assessmentResVo.getIsBetAllowed()) {
            resultVo.setThirdOrderStatus(OrderStatusEnum.ACCEPTED.getCode());
        } else {
            if (delayTime > 0) {
                //有延迟时间，等待
                resultVo.setThirdOrderStatus(OrderStatusEnum.WAITING.getCode());
                resultVo.setIsBetAllowed(assessmentResVo.getIsBetAllowed());
            } else {
                resultVo.setThirdOrderStatus(OrderStatusEnum.REJECTED.getCode());
            }
        }
        String msg = assessmentResVo.getRejectReason() != null ? assessmentResVo.getRejectReason().getReasonCode() : null;
        if (msg == null) {
            List<GtsBetAssessmentLegsVo> legs = assessmentResVo.getLegs();
            msg = CollectionUtils.isEmpty(legs) ? null : legs.get(0).getRejectReason() == null ? null : legs.get(0).getRejectReason().getReasonCode();
        }
        resultVo.setReasonMsg(msg);
        resultVo.setErrorCode(assessmentResVo.getRejectReason() != null ? assessmentResVo.getRejectReason().getReasonCode() : null);
        return resultVo;
    }


    /**
     * 下注 结果确认
     * totalMoney 投注总金额
     */
    public void gtsReceiveBet(ThirdOrderExt ext) {
        MDC.put(LINKID, ext.getLinkId());
        String orderNo = ext.getOrderNo();
        log.info("::{}::订单确认-接拒状态通知GTS,开始", orderNo);
        GtsBetReceiverCache cache = new GtsBetReceiverCache();
        List<GtsExtendBean> gtsExtendBeans = convertThirdParam(ext, cache);
        ExtendBean extendBean = gtsExtendBeans.get(0);
        //组装请求gtsvo
        GtsBetReceiverRequestVo receiverRequestVo = new GtsBetReceiverRequestVo();
        receiverRequestVo.setId(extendBean.getOrderId());
        receiverRequestVo.setBetPlacedTimestampUTC(getUtcTime());
        receiverRequestVo.setBetUpdatedTimestampUTC(getUtcTime());
        receiverRequestVo.setBookmakerName("onyxcrown");
        receiverRequestVo.setCurrencyCode("CNY");
        receiverRequestVo.setPlayerId(extendBean.getUserId());
        receiverRequestVo.setPriority(1);
        receiverRequestVo.setStatus(ext.getOrderStatus() == 1 ? "Open" : "Cancelled");
        BigDecimal gtsAmount = getGtsAmount(ext);
        try {
            //BigDecimal gtsAmount = ext.getPaTotalAmount().multiply(discountAmount(ext)).divide(new BigDecimal("100"), 2, RoundingMode.FLOOR);
            receiverRequestVo.setTotalStake(gtsAmount);
            //receiverRequestVo.setPayout(gtsAmount.multiply(new BigDecimal(extendBean.getOdds()).subtract(new BigDecimal("1"))).divide(new BigDecimal("1"), 2, RoundingMode.FLOOR));
            if (ext.getOrderStatus() == 2) {
                //取消
                receiverRequestVo.setPayout(gtsAmount);
            }
            //串关方式设置
            String getSystemBetType = getSystemBetType(ext.getList(), ext.getSeriesType());
            if (StringUtils.isNotEmpty(getSystemBetType)) {
                receiverRequestVo.setSystemBetType(getSystemBetType);
            }
            //投注项依次处理 legs
            List<GtsBetReceiveLegsVo> legsVoList = new ArrayList<>();
            gtsExtendBeans.forEach(bean -> {
                GtsBetReceiveLegsVo legs = new GtsBetReceiveLegsVo();
                legs.setGameState(bean.getIsScroll().equals("1") ? GTS_INPLAY : GTS_PREMATCH);
                legs.setPrice(new BigDecimal(bean.getOdds()));
                legs.setStatus(ext.getOrderStatus() == 1 ? "Open" : "Cancelled");
                legs.setBookmakerContent(bean.getBookmakerContent());
                legs.setBetgeniusContent(bean.getBetgeniusContent());
                legsVoList.add(legs);
            });
            receiverRequestVo.setLegs(legsVoList);
        } catch (Exception e) {
            log.error("::{}::订单确认异常", orderNo, e);
        }
        log.info("::{}::订单确认-请求Receive Api参数:{}", orderNo, JSONObject.toJSONString(receiverRequestVo));
        GtsAuthorizationVo authorizationVo = getToken(gtsConfig.getBetReceiverClientId(), gtsConfig.getBetReceiverClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 2, orderNo);
        //第三方请求下注
        cacheReceiveBet(receiverRequestVo, ext, gtsAmount, cache);
        GtsBetReceiverResVo gtsBetAssessmentResultVo = new GtsBetReceiverResVo();
        try {
            Map<String, String> headMap = new HashMap<>();
            headMap.put("x-api-key", gtsConfig.getBetReceiverApiKey());
            headMap.put("Authorization", "Bearer " + authorizationVo.getAccessToken());
            String data = HttpUtil.post(gtsConfig.getBetReceiverApiUrl(), JSONObject.toJSONString(receiverRequestVo), headMap);
            log.info("::{}::订单确认-请求Receiver Api返回:{}", orderNo, data);
            if (data != null && data.equals("")) {
                gtsBetAssessmentResultVo.setCode(200);
                gtsBetAssessmentResultVo.setData("成功");
            } else {
                log.info("::{}::订单确认-请求receiver api失败:{}", orderNo, data);
                gtsBetAssessmentResultVo.setCode(-1);
                gtsBetAssessmentResultVo.setData("失败：" + data);
            }
        } catch (Exception e) {
            if (e instanceof RcsServiceException) {
                //投注请求异常 记录 -
                if (((RcsServiceException) e).getCode() == 5031) {
                    String retryStatus = redisClient.get(String.format(GTS_RETRY_STATUS_RECEIVE, ext.getOrderNo()));
                    if (StringUtils.isBlank(retryStatus) || (StringUtils.isNotBlank(retryStatus)) && Integer.parseInt(retryStatus) < gtsConfig.getRetryTime()) {
                        //手动重连
                        log.warn("::{}::订单确认-请求receiver api 连接拒绝,丢入重试队列", orderNo);
                        ext.setRetryType(3);
                        sendMessage.sendMessage("rcs_risk_third_order_retry", ext.getThird() + "_ORDER_RETRY", ext.getOrderNo(), ext);
                    }
                }
            }
            log.error("::{}::订单确认-请求receiver api异常:", orderNo, e);
            gtsBetAssessmentResultVo.setCode(-1);
            gtsBetAssessmentResultVo.setData("失败：" + e.getMessage());
        }
        log.info("::{}::订单确认-接拒状态通知GTS,结束:{}", orderNo, JSONObject.toJSONString(gtsBetAssessmentResultVo));
        MDC.remove(LINKID);
    }

    public void gtsSettleReceive(GtsBetReceiverRequestVo gtsBetReceiverRequestVo) {
        String orderNo = gtsBetReceiverRequestVo.getId();
        log.info("::{}::订单结算-请求Receive Api参数:{}", orderNo, JSONObject.toJSONString(gtsBetReceiverRequestVo));
        GtsAuthorizationVo authorizationVo = getToken(gtsConfig.getBetReceiverClientId(), gtsConfig.getBetReceiverClientCecret()
                , gtsConfig.getGrantType(), gtsConfig.getTokenUrl(), 2, orderNo);
        log.info("::{}::获取authorizationVo成功", orderNo);
        GtsBetReceiverResVo gtsBetAssessmentResultVo = new GtsBetReceiverResVo();
        try {
            Map<String, String> headMap = new HashMap<>();
            headMap.put("x-api-key", gtsConfig.getBetReceiverApiKey());
            headMap.put("Authorization", "Bearer " + authorizationVo.getAccessToken());
            log.info("::{}::订单结算-开始请求Receive Api入参参数:{}", orderNo, JSONObject.toJSONString(gtsBetReceiverRequestVo));
            String data = HttpUtil.post(gtsConfig.getBetReceiverApiUrl(), JSONObject.toJSONString(gtsBetReceiverRequestVo), headMap);
            log.info("::{}::订单结算-请求Receiver Api返回:{}", orderNo, data);
            if (data != null && data.equals("")) {
                gtsBetAssessmentResultVo.setCode(200);
                gtsBetAssessmentResultVo.setData("成功");
            } else {
                log.info("::{}::订单结算-请求receiver api失败:{}", orderNo, data);
                gtsBetAssessmentResultVo.setCode(-1);
                gtsBetAssessmentResultVo.setData("失败：" + data);
            }
            //结算通知 删除状态
           /* log.info("::{}::订单结算-删除结算数据", orderNo);
            String receiveBetKey = String.format(GTS_SETTLE_INFO, orderNo);
            redisClient.delete(receiveBetKey);*/
        } catch (Exception e) {
            log.error("::{}::订单结算-请求receiver api异常:", orderNo, e);
            gtsBetAssessmentResultVo.setCode(-1);
            gtsBetAssessmentResultVo.setData("失败：" + e.getMessage());
        }
        log.info("::{}::订单结算-接拒状态通知GTS,结束:{}", orderNo, JSONObject.toJSONString(gtsBetAssessmentResultVo));
    }


    private void cacheReceiveBet(GtsBetReceiverRequestVo receiverRequestVo, ThirdOrderExt ext, BigDecimal gtsAmount, GtsBetReceiverCache cache) {
        String receiveBetKey = String.format(GTS_SETTLE_INFO, receiverRequestVo.getId());
        int orderStatus = ext.getOrderStatus();
        try {
            switch (orderStatus) {
                case 1:
                    //投注成功 记录当时的确认状态
                    boolean isScroll = orderAcceptService.orderIsScroll(ext.getList());
                    long expiry = 7 * 24 * 60 * 60; //早盘7天
                    if (isScroll) {
                        //滚球120分钟
                        expiry = 6 * 60 * 60;
                    }
                    BeanUtils.copyProperties(receiverRequestVo, cache);
                    log.info("::{}::订单确认-缓存结算数据:{}", ext.getOrderNo(), JSONObject.toJSONString(cache));
                    cache.setDisAmount(gtsAmount);
                    redisClient.setExpiry(receiveBetKey, cache, expiry);
                    break;
                case 2:
                    //投注取消 不处理
                    break;
                case 3:
                    break;
                default:
                    //todo
            }
        } catch (Exception e) {
            log.error("::{}::订单结算数据处理异常:", receiverRequestVo.getId(), e);
        }
    }

    /**
     * 获取接口所需时间参数格式
     *
     * @return
     */
    public static String getUtcTime() {
        LocalDateTime localDateTime = new Date(System.currentTimeMillis()).toInstant().atOffset(ZoneOffset.of("+0")).toLocalDateTime();
        return localDateTime.toString() + "Z";
    }


    /**
     * 订单是否被取消
     *
     * @param orderNo 订单号
     * @return 是否取消
     */
    @Override
    public boolean orderIsCanceled(String orderNo) {
        LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsGtsOrderExt::getOrderNo, orderNo);
        RcsGtsOrderExt rcsGtsOrderExt = rcsGtsOrderExtMapper.selectOne(wrapper);
        if (rcsGtsOrderExt == null) {
            return false;
        }
        return rcsGtsOrderExt.getCancelStatus() == 1;
    }


    @Override
    public void saveOrder(ThirdOrderExt thirdOrderExt) {
        String orderNo = thirdOrderExt.getList().get(0).getOrderId();
        String third = thirdOrderExt.getThird();
        try {
            RcsGtsOrderExt ext = new RcsGtsOrderExt();
            ext.setOrderNo(orderNo);
            ext.setStatus("INIT");
            if (thirdOrderExt.getPaTotalAmount() != null) {
                //分转为元
                BigDecimal paTotalAmount = thirdOrderExt.getPaTotalAmount().divide(new BigDecimal("100"), 2, RoundingMode.FLOOR);
                ext.setPaAmount(paTotalAmount.toPlainString());
                BigDecimal rate = discountAmount(thirdOrderExt);
                ext.setGtsAmount(paTotalAmount.multiply(rate).divide(new BigDecimal("1"), 2, RoundingMode.FLOOR).toPlainString());
                //放入缓存60s，防止在投注过程中折扣变动
                String GTS_BET_AMOUNT_KEY = String.format(GTS_BET_AMOUNT, orderNo);
                redisClient.setExpiry(GTS_BET_AMOUNT_KEY, ext.getGtsAmount(), 60L);
            }
            ext.setResult(thirdOrderExt.getThirdResJson());
            ext.setCreTime(new Date());
            ext.setThirdName(third);
            ext.setThirdNo(thirdOrderExt.getThirdOrderNo());
            ext.setRemark("订单保存");
            rcsGtsOrderExtMapper.insert(ext);
        } catch (Exception e) {
            log.error("::{}::投注-{}订单入库处理异常:", orderNo, third, e);
        }
        log.info("::{}::投注-{}订单入库处理完成", orderNo, third);
    }

    @Override
    public void updateOrder(ThirdOrderExt orderExt) {
        String orderNo = null;
        try {
            orderNo = orderExt.getList().get(0).getOrderId();
            LambdaQueryWrapper<RcsGtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsGtsOrderExt::getOrderNo, orderNo);
            //防止首次入库失败
            RcsGtsOrderExt ext = rcsGtsOrderExtMapper.selectOne(wrapper);
            if (ext == null) {
                saveOrder(orderExt);
                return;
            }
            ext.setThirdNo(orderExt.getThirdOrderNo());
            String orderStatus = orderExt.getThirdOrderStatus() == 1 ? ACCEPTED : REJECTED;
            if (ext.getStatus() == null) {
                ext.setStatus(orderStatus);
            } else {
                ext.setStatus(ext.getStatus() + "," + orderStatus);
            }
            String thirdResJson = orderExt.getThirdResJson();
            if (ext.getResult() == null) {
                ext.setResult(thirdResJson);
            } else {
                ext.setResult(ext.getResult() + ", " + thirdResJson);
            }
            ext.setUpdateTime(new Date());
            ext.setRemark(ext.getRemark() + ", 更新");
            rcsGtsOrderExtMapper.updateById(ext);
        } catch (Exception e) {
            log.error("::{}::投注-{}订单更新处理异常:", orderNo, orderExt.getThird(), e);
        }
        log.info("::{}::投注-{}订单更新处理完成", orderNo, orderExt.getThird());
    }

    /**
     * 商户折扣
     */
    @Override
    public BigDecimal discountAmount(ThirdOrderExt ext) {
        String val = null;
        String switchStatus = rcsSwitchService.getMissOrderSwitchStatus();
        if (StringUtils.isNotEmpty(switchStatus) && switchStatus.equals(YesNoEnum.Y.getValue().toString())) {
            val = GTS_DEFAULT_DISCOUNT;
            log.info("::{}商户折扣功能关闭，采用默认比例:{}", ext.getBusId(), val);
            return new BigDecimal(val);
        }
        try {
            String busDiscountKey = String.format(GTS_AMOUNT_RATE, ext.getBusId());
            val = redisClient.get(busDiscountKey);
            log.info("::{}::{}投注获取到商户:{}对应折扣率为:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            if (StringUtils.isBlank(val)) {
                String busAllDiscountKey = GTS_AMOUNT_RATE_ALL;
                val = redisClient.get(busAllDiscountKey);
                log.info("::{}::{}投注获取到商户:{}通用折扣率为:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            }
            if (StringUtils.isBlank(val)) {
                val = GTS_DEFAULT_DISCOUNT;
                log.info("::{}::{}投注获取到商户:{}默认折扣率为:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            }
        } catch (Exception e) {
            val = GTS_DEFAULT_DISCOUNT;
            log.info("::{}::{}投注获取商户:{}折扣率异常:使用默认折扣率:{}", ext.getOrderNo(), ext.getThird(), ext.getBusId(), val, e);
        }
        return new BigDecimal(val);

    }


    /**
     * 获取联赛id
     *
     * @param matchId
     * @param isChampion
     * @return
     */
    private StandardSportTournament queryStandardTournament(String matchId, Integer isChampion) {
        String tournamentKey = String.format(GTS_MATCH_TOURNAMENTCODE, matchId);
        Object tournament = RcsLocalCacheUtils.timedCache.get(tournamentKey);
        if (Objects.nonNull(tournament)) {
            return (StandardSportTournament) tournament;
        }
        //获取联赛id
        Long standardTournamentId = 0L;
        //冠军赛事从rcs_standard_outright_match_info表查询 第三方赛事id
        if (isChampion != null && isChampion == 1) {
            String key = String.format(GTS_OUTRIGHT_MATCH_TOURNAMENTID, matchId);
            Object o = RcsLocalCacheUtils.timedCache.get(key);
            if (Objects.isNull(o)) {
                RcsStandardOutrightMatchInfo matchInfo = rcsStandardOutrightMatchInfoMapper.selectById(matchId);
                if (matchInfo == null) {
                    throw new RcsServiceException("冠军赛事数据不存在");
                }
                standardTournamentId = matchInfo.getStandardTournamentId();
                RcsLocalCacheUtils.timedCache.put(key, standardTournamentId, GTS_MATCH_TIMOEOUT);
            } else {
                standardTournamentId = (Long) o;
            }
        } else {
            String key = String.format(GTS_MATCH_TOURNAMENTID, matchId);
            Object o = RcsLocalCacheUtils.timedCache.get(key);
            if (Objects.isNull(o)) {
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(matchId);
                if (standardMatchInfo == null) {
                    throw new RcsServiceException("赛事数据不存在");
                }
                standardTournamentId = standardMatchInfo.getStandardTournamentId();
                RcsLocalCacheUtils.timedCache.put(key, standardTournamentId, GTS_MATCH_TIMOEOUT);
            } else {
                standardTournamentId = (Long) o;
            }
        }
        //联赛信息 用于获取 第三方联赛id 第三方联赛名称
        StandardSportTournament standardSportTournament = standardSportTournamentMapper.selectById(standardTournamentId);
        if (standardSportTournament == null) {
            throw new RcsServiceException("联赛信息不存在");
        }
        RcsLocalCacheUtils.timedCache.put(tournamentKey, standardSportTournament, GTS_MATCH_TIMOEOUT);
        return standardSportTournament;
    }

    /**
     * 获取国际化数据
     *
     * @return
     */
    private String queryRcsLanguageInternation(String matchId, Long tournamentNameCode) {
        String languageInternationKey = String.format(GTS_MATCH_LANGUAGEINTERNATION, matchId);
        Object text = RcsLocalCacheUtils.timedCache.get(languageInternationKey);
        if (Objects.nonNull(text)) {
            return (String) text;
        }
        LambdaQueryWrapper<RcsLanguageInternation> languageInternationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        languageInternationLambdaQueryWrapper.eq(RcsLanguageInternation::getNameCode, tournamentNameCode);
        RcsLanguageInternation rcsLanguageInternation = languageInternationMapper.selectOne(languageInternationLambdaQueryWrapper);
        if (rcsLanguageInternation == null) {
            throw new RcsServiceException("获取联赛名称异常");
        }
        RcsLocalCacheUtils.timedCache.put(languageInternationKey, rcsLanguageInternation.getText(), GTS_MATCH_TIMOEOUT);
        return rcsLanguageInternation.getText();
    }

    //获取到当时入库的折扣金额
    private BigDecimal getGtsAmount(ThirdOrderExt ext) {
        try {
            String GTS_BET_AMOUNT_KEY = String.format(GTS_BET_AMOUNT, ext.getOrderNo());
            String gstAmountStr = redisClient.get(GTS_BET_AMOUNT_KEY);
            BigDecimal gtsAmount;
            if (gstAmountStr != null) {
                gtsAmount = new BigDecimal(gstAmountStr);
            } else {
                BigDecimal rate = discountAmount(ext);
                gtsAmount = ext.getPaTotalAmount().multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.FLOOR);
            }
            return gtsAmount;
        } catch (Exception e) {
            log.error("::{}::获取gtsAmount异常", ext.getOrderNo(), e);
        }
        return new BigDecimal("0");
    }


}
