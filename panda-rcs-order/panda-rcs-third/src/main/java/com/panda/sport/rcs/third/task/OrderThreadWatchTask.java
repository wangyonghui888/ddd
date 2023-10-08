package com.panda.sport.rcs.third.task;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.third.config.GtsInitConfig;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.gts.*;
import com.panda.sport.rcs.third.monitor.HttpPoolStatus;
import com.panda.sport.rcs.third.monitor.Layout;
import com.panda.sport.rcs.third.monitor.MonitorController;
import com.panda.sport.rcs.third.monitor.OrderThread;
import com.panda.sport.rcs.third.util.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.panda.sport.rcs.third.common.Constants.GTS_INPLAY;
import static com.panda.sport.rcs.third.common.Constants.GTS_PREMATCH;

@Component
@Slf4j
public class OrderThreadWatchTask {

    @Resource(name = "cancelPoolExecutor")
    private ThreadPoolExecutor cancelPoolExecutor;
    @Resource(name = "confirmPoolExecutor")
    private ThreadPoolExecutor confirmPoolExecutor;

    @Resource(name = "betPoolExecutor")
    private ThreadPoolExecutor betPoolExecutor;
    @Resource
    MonitorController monitorController;

    @Resource
    private GtsInitConfig gtsConfig;



    private final String orderStr = "{\"deviceType\":\"2\",\"merchantCode\":\"278109\",\"ip\":\"172.21.165.70\",\"list\":[{\"currentScore\":\"1:0\",\"itemBean\":{\"matchType\":2,\"matchInfo\":\"阿斯顿维拉 v 利物浦\",\"handleAfterOddsValue1\":1.88,\"dataSourceCode\":\"RC\",\"marketType\":\"EU\",\"turnamentLevel\":0,\"otherScore\":\"1:0\",\"recType\":0,\"modifyUser\":\"系统\",\"playOptionsId\":341662465954566148,\"modifyTime\":1688701929843,\"tournamentId\":1682748461414224369,\"handleStatus\":0,\"oddsValue\":188000.0,\"validateResult\":1,\"riskChannel\":2,\"subPlayId\":\"6\",\"orderNo\":\"5066105789532027\",\"betNo\":\"506610578953527\",\"playName\":\"双重机会12345\",\"dateExpect\":\"2023-07-06\",\"matchProcessId\":6,\"maxWinAmount\":44000.0,\"oddFinally\":\"1.88\",\"betAmount\":50000,\"placeNum\":1,\"playOptionsName\":\"平局 或 利物浦\",\"sportName\":\"足球\",\"paidAmount\":94000.00,\"originOdds\":188000.0,\"handledBetAmout\":500,\"orderStatus\":0,\"matchLength\":66,\"platform\":\"RC\",\"marketId\":341662465950371841,\"playId\":6,\"uid\":506561686127700009,\"paidAmount1\":940.00,\"matchId\":341662464171986946,\"tradeType\":0,\"scoreBenchmark\":\"1:0\",\"betAmount1\":500,\"matchName\":\"PM红猫英格兰超级联赛\",\"playOptions\":\"X2\",\"sportId\":1,\"createTime\":1688701929843,\"handleAfterOddsValue\":1.88,\"betTime\":1688701929843,\"createUser\":\"系统\"},\"orderMoney\":50000,\"isScroll\":\"1\",\"orderId\":\"5066105789532027\",\"dataSourceCode\":\"RC\",\"marketId\":\"341662465950371841\",\"recType\":0,\"playId\":\"6\",\"userTagLevel\":230,\"seriesType\":1,\"odds\":\"1.88\",\"tournamentId\":1682748461414224369,\"currentMaxPaid\":44000,\"validateResult\":1,\"matchId\":\"341662464171986946\",\"busId\":\"2\",\"riskChannel\":\"23\",\"subPlayId\":\"6\",\"handicap\":\"0\",\"userId\":\"506561686127700009\",\"dateExpect\":\"2023-07-06\",\"itemId\":\"506610578953527\",\"selectId\":\"341662465954566148\",\"sportId\":\"1\",\"playType\":\"3\",\"isChampion\":0,\"tournamentLevel\":0}],\"secondaryLabelIdsList\":[],\"paTotalAmount\":50000,\"linkId\":\"605e4a598072403c8e114a145ad86749 , 5066105789532027 , 506561686127700009\",\"orderGroup\":\"common\",\"third\":\"RTS\",\"seriesType\":1,\"acceptOdds\":2,\"currency\":\"CNY\"}";

    /**
     * 测试 每秒投1000单
     */
    //@Scheduled(fixedDelay = 1000)
    public void watchOrderThreadPool1() {
        ThirdOrderExt ext = null;
        try {
            ext = JSONObject.parseObject(orderStr, ThirdOrderExt.class);
        } catch (Exception e) {
            log.error("解析错误", e);
        }
        if (ext == null) {
            return;
        }
        for (int i = 0; i < 500; i++) {
            ThirdOrderExt finalExt = ext;
            //模拟不断投注 并数据商接口延迟2s
            betPoolExecutor.execute(() -> {
                gtsAssessmentBet(finalExt);
            });

            //模拟不断取消 并数据商接口延迟2s
            cancelPoolExecutor.execute(() -> {
                gtsAssessmentBet(finalExt);
            });
            //模拟不断确认 并数据商接口延迟2s
            confirmPoolExecutor.execute(() -> {
                gtsAssessmentBet(finalExt);
            });
        }
    }

    private void gtsAssessmentBet(ThirdOrderExt ext) {
        GtsBetAssessmentRequestVo assessmentRequestVo = new GtsBetAssessmentRequestVo();
        assessmentRequestVo.setBetId("123456789110");
        assessmentRequestVo.setCurrencyCode("CNY");
        assessmentRequestVo.setPlayerId("1234566789520");
        assessmentRequestVo.setTotalStake(ext.getPaTotalAmount().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
        List<GtsBetAssessmentLegsRequestVo> legsVoList = new ArrayList<>();
        ext.getList().forEach(bean -> {
            GtsBetAssessmentLegsRequestVo legs = new GtsBetAssessmentLegsRequestVo();
            legs.setGameState(bean.getIsScroll().equals("1") ? GTS_INPLAY : GTS_PREMATCH);
            legs.setPrice(new BigDecimal(bean.getOdds()));
            legs.setSelectionId("2451283");
            legsVoList.add(legs);
        });
        assessmentRequestVo.setSystemBetType("getSystemBetType");
        assessmentRequestVo.setLegs(legsVoList);
        if (ext.getOrderNo() != null) {
            String acceptOdds = 1 == ext.getAcceptOdds() ? "AcceptHigher" : 2 == ext.getAcceptOdds() ? "AcceptAny" : "AcceptNone";
            assessmentRequestVo.setPriceChangeRule(acceptOdds);
        }
        //log.info("::{}::{}-请求GTS Bet-Assessment-Api参数:{}", logIndex, logAction, JSONObject.toJSONString(assessmentRequestVo));
        try {
            Map<String, String> headMap = new HashMap<>();
            headMap.put("x-api-key", "fdsafddddddewqrewfdsfewrwtregfdwyt4r5ygdryt5uyhfghtrytrghrey7trghfdstgewrtgdfgbdsfg");
            headMap.put("Authorization", "Bearer fdsafddddddewqrewfdsfewrwtregfdwyt4r5ygdryt5uyhfghtrytrghrey7trghfdstgewrtgdfgbdsfg");
            long start = System.currentTimeMillis();
            String url = "https://onyxcrown-risk-assessment-api.uat.riskmanagement.geniussports.com/api/v1/bookmakers/8467/assessment";
            String url2 = "http://172.21.165.120:10630/test/third";
            String url3 = "http://172.21.165.120:10630/test/third2";
            //AsyncHttpUtil.postJson(gtsConfig.getBetAssessmentUrl(), JSONObject.toJSONString(assessmentRequestVo),true, headMap, gtsCallBack);
            //log.info("::去BG获取token请求开始::");
            HttpUtil.post(url2, JSONObject.toJSONString(assessmentRequestVo), headMap);
            HttpUtil.post(url3, JSONObject.toJSONString(assessmentRequestVo), headMap);
            /*AsyncHttpUtil.postJson(url, JSONObject.toJSONString(assessmentRequestVo),true, headMap,new FutureCallback<HttpResponse>(){
                public void completed(final HttpResponse response) {
                    log.info("当前响应状态：->" + response.getStatusLine());
                    try {
                        log.info("当前请求状态：" + AsyncHttpClient.poolManager.getTotalStats() + ", response=" + EntityUtils.toString(response.getEntity()));
                    } catch (IOException e) {
                        log.error("===================", e);
                    }
                }

                public void failed(final Exception ex) {
                    log.error("===================" + ex);
                }

                public void cancelled() {
                    log.info("=================== cancelled");
                }
            });*/


            //组装请求参数
            Map<String, String> map = new HashMap<>();
            map.put("client_id", gtsConfig.getBetAssessClientId());
            map.put("client_secret", gtsConfig.getBetAssessClientCecret());
            map.put("grant_type", gtsConfig.getGrantType());
            //log.info("::去BG获取token请求::{}", JSONObject.toJSON(map));
            // AsyncHttpUtil.postMap(gtsConfig.getBetAssessmentUrl(), map, true, gtsCallBack);
            /*AsyncHttpUtil.postJson(url2, JSONObject.toJSONString(assessmentRequestVo), false, headMap, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    log.info("当前响应状态2：->" + response.getStatusLine());
                    try {
                        log.info("当前请求状态2：" + AsyncHttpClient.poolManager.getTotalStats() + ", response=" + EntityUtils.toString(response.getEntity()));
                    } catch (IOException e) {
                        log.error("===================", e);
                    }
                }
                public void failed(final Exception ex) {
                    log.error("===================" + ex);
                }
                public void cancelled() {
                    log.info("=================== cancelled");
                }
            });*/
            long end = System.currentTimeMillis();
            //log.info("::请求GTS Bet-Assessment-Api,耗时：{}", end - start);
        } catch (Exception e) {
            //log.error("测试失败", e);

        }
    }

    /**
     * 巡检请求池状态
     */
    //@Scheduled(fixedDelay = 6000 * 10)
    public void watchOrderThreadPool() {
        log.info("-------------------------服务定时检查开始---------------------------------");
        //log.info("线程-投注池-任务投递数:{}",  redisClient.get("third:bet:num"));
        log.info(Stream.of(Layout.of("投注池", watchOrderThreadPool(betPoolExecutor))).map(Layout::toString).collect(Collectors.joining(StrUtil.LF)));
        //log.info(Stream.of(Layout.of("确认池", watchOrderThreadPool(confirmPoolExecutor))).map(Layout::toString).collect(Collectors.joining(StrUtil.LF)));
        //log.info(Stream.of(Layout.of("取消池", watchOrderThreadPool(cancelPoolExecutor))).map(Layout::toString).collect(Collectors.joining(StrUtil.LF)));
        log.info(Stream.of(Layout.of("HTTP POOL", watchHttpPool())).map(Layout::toString).collect(Collectors.joining(StrUtil.LF)));
        //log.info(monitorController.print());
        log.info("-------------------------服务定时检查结束---------------------------------");
    }

    /**
     * 监控线程池
     *
     */
    private OrderThread watchOrderThreadPool(ThreadPoolExecutor pool) {
        return OrderThread.builder().corePoolSize(pool.getCorePoolSize())
                .maximumPoolSize(pool.getMaximumPoolSize())
                .poolSize(pool.getPoolSize())
                .queueSize(pool.getQueue().size())
                .taskCount(pool.getTaskCount())
                .activeCount(pool.getActiveCount())
                .completedTaskCount(pool.getCompletedTaskCount())
                .build();
    }

    /**
     * 监控http pool
     *
     */
    private HttpPoolStatus watchHttpPool() {
        PoolingHttpClientConnectionManager httpClientConnectionManager = HttpLoad.getHttpClientConnectionManager();
        PoolStats totalStats = httpClientConnectionManager.getTotalStats();
        return HttpPoolStatus.builder().maxTotal(totalStats.getMax())
                .available(totalStats.getAvailable())
                .leased(totalStats.getLeased())
                .pending(totalStats.getPending())
                .defaultMaxPerRoute(httpClientConnectionManager.getDefaultMaxPerRoute())
                .build();
    }


    /**
     * 监控服务内存，cpu
     */
    /*@Scheduled(cron = "0 0/10 * * * ?")
    public void patrol() {
        // 发现预警
        monitorController.warn();
    }*/


}
