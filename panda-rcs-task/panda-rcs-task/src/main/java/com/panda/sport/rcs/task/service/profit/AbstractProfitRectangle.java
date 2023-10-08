package com.panda.sport.rcs.task.service.profit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import com.panda.sport.rcs.task.job.operation.ProfitRectangleVo;
import com.panda.sport.rcs.task.wrapper.order.ITOrderDetailService;
import com.panda.sport.rcs.task.wrapper.statistics.RcsProfitMarketService;
import com.panda.sport.rcs.task.wrapper.statistics.RcsProfitRectangleService;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.vo.statistics.MarketBalanceVo;
import com.panda.sport.rcs.vo.statistics.MarketProfitVo;
import com.panda.sport.rcs.vo.statistics.ProfitDetailBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.order.impl
 * @Description :  期望值矩阵处理
 * @Date: 2019-12-13 11:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public abstract class AbstractProfitRectangle {
    @Autowired
    protected RcsProfitRectangleService rcsProfitRectangleService;

    @Autowired
    protected RcsProfitMarketService rcsProfitMarketService;


    /**
     * 最小值
     */
    protected Double MIN_MATRIX_VALUE = 0.0 ;
    /**
     * 最大值
     */
    protected  Double MAX_MATRIX_VALUE = 0.0;
    /**
     * redis操作
     */
    @Autowired
    protected RedisClient redisClient;

    /**
     * map类
     */
    protected ConcurrentHashMap<Double, RcsProfitRectangle> map = new ConcurrentHashMap<Double, RcsProfitRectangle>();

    /**
     * @Description   校验规则
     * @Param []
     * @Author  toney
     * @Date  9:49 2019/12/19
     * @return java.lang.Boolean
     **/
    public abstract Boolean checkParams(ProfitRectangleVo rectangleVo);
    /**
     * @Description   初始化矩阵起始参数
     * @Param [playId]
     * @Author  toney
     * @Date  18:35 2019/12/13
     * @return void
     **/
    public abstract void initRectangleParam(Integer playId);

    @Autowired
    private ITOrderDetailService orderDetailService;

    /**
     * 逻辑处理
     * @param rectangleVo
     */
    public abstract void logicHandle(ProfitRectangleVo rectangleVo);

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;


    /**
     * @return void
     * @Description 处理
     * @Param [orderBean]
     * @Author toney
     * @Date 15:10 2019/12/11
     **/
    public void handleData(ProfitRectangleVo rectangleVo) {
        calcData(rectangleVo);
    }



    /**
     * 初始化矩阵
     * @param rectangleVo
     * @return
     */
    protected ConcurrentHashMap<Double, RcsProfitRectangle> initRcsProfitRectangle(ProfitRectangleVo rectangleVo){
        for (Double i = MIN_MATRIX_VALUE; i <= MAX_MATRIX_VALUE; i++) {
            RcsProfitRectangle rcsProfitRectangle = new RcsProfitRectangle();
            rcsProfitRectangle.setMatchId(rectangleVo.getMatchId());
            rcsProfitRectangle.setPlayId(rectangleVo.getPlayId());
            rcsProfitRectangle.setScore(i.intValue());
            rcsProfitRectangle.setProfitValue(BigDecimal.ZERO);
            rcsProfitRectangle.setCreateTime(new Date());
            rcsProfitRectangle.setUpdateTime(new Date());
            rcsProfitRectangle.setMatchType(rectangleVo.getMatchType());
            map.put(i, rcsProfitRectangle);
        }
        return map;
    }



    /**
     * 计数
     * @param rectangleVo
     */
    public void calcData(ProfitRectangleVo rectangleVo) {
        initRectangleParam(rectangleVo.getPlayId());
        initRcsProfitRectangle(rectangleVo);
        logicHandle(rectangleVo);


        ArrayList<RcsProfitRectangle> rcsProfitRectangles = new ArrayList<>();
        Iterator<Map.Entry<Double, RcsProfitRectangle>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Double, RcsProfitRectangle> next = iterator.next();
            RcsProfitRectangle value = next.getValue();
            rcsProfitRectangles.add(value);
        }
        Integer row = rcsProfitRectangleService.batchInsert(rcsProfitRectangles);
        //String cacheKey = String.format(RedisKeys.RCS_MATCH_PROFIT_DETAIL,orderItem.getMatchId());
        //写入缓存
        //redisClient.hSet(cacheKey,orderItem.getPlayId().toString(),JsonFormatUtils.toJson(rcsProfitRectangles));


        syncData(rectangleVo.getMatchId(), rectangleVo.getPlayId(), rectangleVo.getMatchType());


        log.info("期望详情", JsonFormatUtils.toJson(map.values()));
    }



    /**
     * @Description   实时推送数据
     * @Param [orderItem]
     * @Author  myname
     * @Date  10:33 2020/1/13
     * @return void
     **/
    private void syncData(Long matchId, Integer playId,Integer matchType){
        try {
            ProfitDetailBean bean = new ProfitDetailBean();
            bean.setMatchId(matchId);
            bean.setPlayId(playId);

            bean.setBalancesList(getBalanceByMatchIdAndPlayId(matchId,playId.longValue(),matchType));
            bean.setRcsProfitRectangleList(getProfitByMatchIdAndPlayId(matchId,playId,matchType));
            producerSendMessageUtils.sendMessage(MqConstants.WS_ODDS_CHANGED_TOPIC, MqConstants.WS_SCROLL_BALL_LIVE_BET_DETAIL_BY_PLAYID_2_4_18_19_CHANGED_TAG, "", bean);
        }catch (Exception ex){
            log.error("推送期望详情失败:",ex.getMessage(),ex);
        }
    }



    /**
     * @Description   获取平衡值
     * @Param [matchId, playId]
     * @Author  myname
     * @Date  13:20 2020/1/13
     * @return java.util.List<com.panda.sport.rcs.vo.statistics.MarketBalanceVo>
     **/
    private List<MarketBalanceVo> getBalanceByMatchIdAndPlayId(Long matchId,Long playId,Integer matchType){
        List<OrderDetailStatReportVo> list = orderDetailService.getMarketStatByMatchIdAndPlayId(matchId, playId,matchType);
        List<MarketBalanceVo> oddList = Lists.newArrayList();
        Map<Long, MarketBalanceVo> markets = Maps.newHashMap();
        for (OrderDetailStatReportVo rvo : list) {
            MarketBalanceVo vo = markets.get(rvo.getMarketId());
            if (vo == null) {
                vo = new MarketBalanceVo();
                vo.setMarketId(rvo.getMarketId());
                vo.setMarketValue(rvo.getMarketValue());
                markets.put(rvo.getMarketId(), vo);
            }
            if ("home".equals(rvo.getOddsType())) {
                vo.setHomeAmount(rvo.getBetAmount() / 100);
            } else {
                vo.setAwayAmount(rvo.getBetAmount() / 100 * -1);
            }
            vo.setBalanceValue(vo.getHomeAmount() + vo.getAwayAmount());
        }
        for (Map.Entry<Long, MarketBalanceVo> entry : markets.entrySet()) {
            oddList.add(entry.getValue());
        }
        Collections.sort(oddList, new Comparator<MarketBalanceVo>() {
            @Override
            public int compare(MarketBalanceVo o1, MarketBalanceVo o2) {
                return (int) ((Float.parseFloat(o1.getMarketValue()) - Float.parseFloat(o2.getMarketValue())) * 100);
            }
        });
        return oddList;
    }
    /**
     * @Description   获取期望详情数据
     * @Param [rcsProfitRectangle]
     * @Author  myname
     * @Date  11:23 2020/1/13
     * @return java.util.List<com.panda.sport.rcs.vo.statistics.MarketProfitVo>
     **/
    private List<MarketProfitVo> getProfitByMatchIdAndPlayId(Long matchId,Integer playId,Integer match_type){
        QueryWrapper<RcsProfitRectangle> wrapper = new QueryWrapper<>();
        wrapper.eq("match_id", matchId);
        wrapper.eq("play_id", playId);
        wrapper.eq("match_type", match_type);
        wrapper.lambda().orderByDesc(RcsProfitRectangle::getScore);
        List<RcsProfitRectangle> rcsProfitRectangleList = rcsProfitRectangleService.list(wrapper);
        List<MarketProfitVo> list = BeanCopyUtils.copyPropertiesList(rcsProfitRectangleList, MarketProfitVo.class);
        return list;
    }
}
