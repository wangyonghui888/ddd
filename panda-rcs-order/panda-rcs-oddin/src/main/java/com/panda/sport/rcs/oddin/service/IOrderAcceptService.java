package com.panda.sport.rcs.oddin.service;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.oddin.entity.common.pojo.ErrorMessagePrompt;

import java.util.List;
import java.util.Map;

/**
 * @author Beulah
 * @date 2023/3/22 0:38
 * @description 订单实时接拒业务
 * 包含下列接拒通用功能：
 * 1.检查接单之前，是否注单被取消
 * 2.判断赛事是否滚球
 * 3.检查滚球秒接场景
 * 4.赛事状态检查
 * 5.赛事阶段判断
 * 6.赛事是否即将开赛
 * 7.赛事模板延时配置检查
 * 8.盘口状态检查
 * 9.实时接拒检查
 * 10.赔率变动
 * 11.当前是否安全事件判断
 */
public interface IOrderAcceptService {


    /**
     * 检查接单之前，是否注单被取消
     *
     * @param orderNo 注单号
     * @return 是否被取消
     */
    boolean orderIsCanceled(String orderNo);


    /**
     * 判断赛事是否滚球
     *
     * @param list 订单
     * @return 是否滚球订单
     */
    boolean orderIsScroll(List<String> list);


    /**
     * 检查滚球秒接场景
     *
     * @param list  订单
     * @param third 第三方标识
     * @return 是否秒接
     */
    boolean checkScrollSpeedAccept(List<ExtendBean> list, String third);


    /**
     * 赛事状态检查
     *
     * @param matchId            赛事id
     * @param orderNo            注单id
     * @param marketId           盘口id
     * @param errorMessagePrompt 错误提示
     * @return 赛事状态变更拒单
     */
    boolean checkMatchStatus(String matchId, String orderNo, ErrorMessagePrompt errorMessagePrompt, String third);


    /**
     * 赛事阶段判断
     *
     * @param orderItem 注单
     * @param third     三方标识
     * @return 是否秒接
     */
    boolean isSpeedAcceptByMatchPeriod(ExtendBean orderItem, String third);


    /**
     * 赛事是否即将开赛
     *
     * @param matchId 赛事id
     * @return 是否即将开赛
     */
    boolean matchIsReadyStart(String matchId, String orderNo);


    /**
     * 赛事模板延时配置检查
     *
     * @param list 注单
     * @return 是否0秒
     */
    boolean matchDelayConfig(List<ExtendBean> list);


    /**
     * 盘口状态检查
     *
     * @param extendBean       订单详情
     * @param errorMessagePrompt 错误提示
     * @return 盘口是否变动拒单
     */
    boolean checkMarketStatus(ExtendBean extendBean, ErrorMessagePrompt errorMessagePrompt, String third,Integer orderStatus);


    /**
     * 检查赛事与盘口状态
     *
     * @param orderStatus        注单状态
     * @param orderNo            订单号
     * @param errorMessagePrompt 错误提示
     * @return 赛事与盘口变更拒单
     */
    boolean matchAndMarketCheck(String orderStatus, String orderNo, ErrorMessagePrompt errorMessagePrompt, String third);


    /**
     * 实时接拒检查
     *
     * @param tOrderDetailList   注单列表
     * @param errorMessagePrompt 错误提示
     * @return 是否通过
     */
    boolean dealWithData(List<ExtendBean> tOrderDetailList, ErrorMessagePrompt errorMessagePrompt, String third, Integer OrderStatus);


    /**
     * 获取赔率接拒变动范围
     *
     * @param list 注单列表
     * @return 变动范围
     */
    Map<String, String> queryOddsRange(List<OrderItem> list, String third);


    /**
     * 当前是否安全事件
     *
     * @param list 订单
     * @return 是否安全事件
     */
    boolean isSafe(List<ExtendBean> list);

    /**
     * 获取盘口赔率变动范围
     *
     * @param tournamentId 联赛id
     * @param matchId      赛事id
     * @param playId       玩法id
     * @return 变动范围值
     */
    String getOddsScope(String orderNo, Long tournamentId, String matchId, String playId, Integer matchType, String third);


    /**
     * 赛事,盘口状态检查
     *
     * @param matchId            赛事id
     * @param orderNo            注单id
     * @param marketId           盘口id
     * @param errorMessagePrompt 错误提示
     * @return 赛事状态变更拒单
     */
    boolean checkMatchAndMarketStatus(String matchId,String marketId, String orderNo, ErrorMessagePrompt errorMessagePrompt, String third);
}
