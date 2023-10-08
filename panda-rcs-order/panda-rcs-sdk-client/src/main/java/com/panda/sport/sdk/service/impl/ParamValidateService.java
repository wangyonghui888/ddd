package com.panda.sport.sdk.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.dto.*;
import com.panda.sport.data.rcs.dto.oddin.TicketResultDto;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.exception.LogicException;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.vo.CategoryVo;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.RcsBusinessPlayPaidConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.service.impl
 * @Description :  订单参数效验
 * @Date: 2019-12-10 21:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class ParamValidateService {
    private static final Logger log = LoggerFactory.getLogger(ParamValidateService.class);

    @Inject
    PropertiesUtil propertiesUtil;
    @Inject
    RcsPaidConfigServiceImp rcsPaidConfigService;
    @Inject
    private CategoryService categoryService;
    @Inject
    RcsPaidConfigServiceImp configService;

    //虚拟赛事种类
    private List<Integer> VIRSTUAL_SPORT = Arrays.asList( 1001,1004,1002,1007,1008,1009,10010,1011,1012);
    /**
     * @return void
     * @Description 查询未登录最大最小限额参数验证
     * @Param [requestParam]
     * @Author max
     * @Date 13:47 2019/12/11
     **/
    public void checkInitMaxBetArguments(Request<OrderBean> requestParam) {
        if (requestParam == null) {
            throw new NullPointerException("requestParam不能为空");
        }
        if (requestParam.getData() == null) {
            throw new NullPointerException("requestParam.data不能为空");
        }

        if (requestParam.getData().getTenantId() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "商户ID不能为空！");
        }
        if (requestParam.getData().getSeriesType() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "串关类型不能为空！");
        }
        if (requestParam.getData().getItems().get(0).getPlayId() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "玩法ID不能为空！");
        }
        if (requestParam.getData().getItems().get(0).getMatchType() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "盘口MatchType不能为空！");
        }
        if (requestParam.getData().getItems().get(0).getSportId() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "sportId不能为空！");
        }
        if (requestParam.getData().getItems().get(0).getOddsValue() == null || requestParam.getData().getItems().get(0).getOddsValue() == 0) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赔率OddsValue不能为空！");
        }
    }

    /**
     * @return void
     * @Description 查询前选项最大可投注金额验证
     * @Param [requestParam]
     * @Author max
     * @Date 15:02 2019/12/11
     **/
    public void checkMaxBetArguments(Request<OrderBean> requestParam) {
        if (null == requestParam || null == requestParam.getData() || null == requestParam.getData().getItems()) {
            throw new RcsServiceException("参数错误");
        }
        if (requestParam.getData().getSeriesType() == 1 && requestParam.getData().getItems().size() > 1) {
            throw new RcsServiceException("单关协议格式异常，不支持多个投注项");
        }

        if (requestParam.getData().getTenantId() == null) {
            throw new RcsServiceException("商户ID不能为空！");
        }
        if (requestParam.getData().getUid() == null) {
            throw new RcsServiceException("用户ID不能为空！");
        }
        if (requestParam.getData().getUsername() == null || requestParam.getData().getUsername().indexOf("_") == -1) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "用户名参数错误！");
        }
        for (OrderItem orderItem : requestParam.getData().getItems()) {
            if (orderItem.getUid() == null) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "用户ID不能为空！");
            }
            if (orderItem.getMarketId() == null || orderItem.getMarketId() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "盘口id不能为空！");
            }
            if (orderItem.getOddsValue() == null || orderItem.getOddsValue() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赔率OddsValue不能为空！");
            }
            if(SdkConstants.VIRSTUAL_SPORT.contains(orderItem.getSportId())){//如果是虚拟赛事则不需要判断更多
                continue;
            }
            if (orderItem.getPlaceNum() == null) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "盘口位置不能为空！");
            }
            checkBaseNotNullArguments(orderItem);
        }

    }

    /**
     * @return void
     * @Description 订单保存参数效验
     * @Param [requestParam]
     * @Author max
     * @Date 21:21 2019/12/10
     **/
    public void checkSaveArguments(Request<OrderBean> requestParam) {
        OrderBean orderBean = requestParam.getData();
        if (requestParam.getData().getSeriesType() == 1 && requestParam.getData().getItems().size() > 1) {
            throw new RcsServiceException("单关协议格式异常，不支持多个投注项");
        }
        if (StringUtils.isEmpty(orderBean.getOrderNo())) {
            throw new RcsServiceException("订单编号不能为空！");
        }
        if (orderBean.getTenantId() == null) {
            throw new RcsServiceException("商户ID不能为空！");
        }
        if (orderBean.getUid() == null) {
            throw new RcsServiceException("用户ID不能为空！");
        }
        for (OrderItem orderItem : orderBean.getItems()) {
            if (StringUtils.isEmpty(orderItem.getBetNo())) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "注单编号不能为空！");
            }
            if (StringUtils.isEmpty(orderItem.getOrderNo())) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "订单编号不能为空！");
            }
            if (orderItem.getBetAmount() == null || orderItem.getBetAmount() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "BetAmount不能为空！");
            }
            if (orderItem.getMaxWinAmount() == null || orderItem.getMaxWinAmount() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "最高可赢不能为空！");
            }
            if (orderItem.getPlaceNum() == null && !SdkConstants.VIRSTUAL_SPORT.contains(orderItem.getSportId())) {//如果是虚拟赛事则不需要判断盘口位置
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "盘口位置不能为空！");
            }
            if (orderItem.getOriginOdds() == null) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "originOdds字段不能为空！");
            }
            if (orderItem.getOddsValue() == null || orderItem.getOddsValue() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赔率OddsValue不能为空！");
            }
        }
    }


    /**
     * @return void
     * @Description 订单保存参数效验
     * @Param [requestParam]
     * @Author max
     * @Date 21:21 2019/12/10
     **/
    public void checkPreSettleArguments(Request<PreOrderRequest> requestParam) {
        PreOrderRequest orderBean = requestParam.getData();
        if (requestParam.getData().getSeriesType() == 1 && requestParam.getData().getDetailList().size() > 1) {
            throw new RcsServiceException("单关协议格式异常，不支持多个投注项");
        }
        if (StringUtils.isEmpty(orderBean.getOrderNo())) {
            throw new RcsServiceException("订单编号不能为空！");
        }
//        if (orderBean.getProbability()==null) {
//            throw new RcsServiceException("概率不能为空！");
//        }
        if (StringUtils.isEmpty(orderBean.getUserId())){
            throw new RcsServiceException("用户ID不能为空！");
        }
        for (PreOrderDetailRequest orderItem : orderBean.getDetailList()) {
            if (StringUtils.isEmpty(orderItem.getBetNo())) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "注单编号不能为空！");
            }
            if (StringUtils.isEmpty(orderItem.getOrderNo())) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "订单编号不能为空！");
            }
            if (orderItem.getSportId() == null || orderItem.getSportId() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赛种ID不能为空！");
            }
            if (orderItem.getMatchId() == null || orderItem.getMatchId() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赛事ID不能为空！");
            }
            if (orderItem.getPlayId() == null || orderItem.getPlayId() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "玩法ID不能为空！");
            }
            if (orderItem.getMatchType() == null || orderItem.getMatchType() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赛事类型不能为空！");
            }
            if (orderItem.getMarketId() == null || orderItem.getMarketId() == 0) {
                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "盘口ID不能为空！");
            }
//            if (StringUtils.isEmpty(orderItem.getMarketValue())) {
//                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "盘口marketValue不能为空！");
//            }
//            if (orderItem.getOddsValue() == null || orderItem.getOddsValue() == 0) {
//                throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赔率OddsValue不能为空！");
//            }
        }
    }


    /**
     * @return void
     * @Description 验证orderItem
     * @Param [bean]
     * @Author max
     * @Date 10:04 2019/12/11
     **/
    private void checkBaseNotNullArguments(OrderItem bean) {
        if (bean.getSportId() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "运动类型ID不能为空！");
        }
        if (bean.getPlayId() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "玩法ID不能为空！");
        }
        if (bean.getMatchId() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "比赛ID不能为空！");
        }
        if (bean.getMatchType() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "盘口MatchType不能为空！");
        }
        if (bean.getOddsValue() == null || bean.getOddsValue() == 0) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赔率OddsValue不能为空！");
        }
        if (bean.getPlayOptionsId() == null || bean.getPlayOptionsId() == 0) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "投注项ID不能为空！");
        }
        //冠军玩法不传该字段
        if (bean.getMatchProcessId() == null && bean.getMatchType() != 3) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "注单所属赛事阶段ID不能为空！");
        }
        if (bean.getTurnamentLevel() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "联赛等级不能为空！");
        }
        if (bean.getTournamentId() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "联赛id不能为空！");
        }
        if (bean.getDataSourceCode() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "数据源不能为空！");
        }
        if (bean.getDateExpect() == null) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "赛事所属时间期号不能为空！");
        }
        if (StringUtils.isEmpty(bean.getPlatform())) {
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "操盘平台不能为空！");
        }
    }

    /**
     * @return com.panda.sport.data.rcs.dto.ExtendBean
     * @Description 根据orderItem 获取扩展 orderBean
     * @Param [bean, item]
     * @Author max
     * @Date 11:15 2019/12/11
     **/
    public ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("2") ? "1" : "0");
        //冠军盘标识
        extend.setIsChampion(item.getMatchType().intValue() == 3 ? 1 : 0);
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(String.valueOf(item.getPlayId()));
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));//投注项ID
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());
        //阶段  冠军玩法走mts/虚拟赛事 可以不设置此字段
        if (item.getMatchType() != 3 && !SdkConstants.VIRSTUAL_SPORT.contains(item.getSportId())) {
            extend.setPlayType(rcsPaidConfigService.getPlayProcess(String.valueOf(item.getSportId()), String.valueOf(item.getPlayId())));
        }
        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        } else {
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setTournamentLevel(item.getTurnamentLevel());
        extend.setTournamentId(item.getTournamentId());
        extend.setDateExpect(item.getDateExpect());
        extend.setDataSourceCode(item.getDataSourceCode());

        extend.setItemBean(item);

        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap("0");
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore("0:0");
        }
        extend.setSubPlayId(item.getSubPlayId());
        extend.setUserTagLevel(bean.getUserTagLevel());
        return extend;
    }

    /**
     * @return void
     * @Description 设置矩阵Bean对象
     * @Param [bean, matrixForecastVo]
     * @Author max
     * @Date 15:13 2019/12/11
     **/
    public void putBeanVal(ExtendBean bean, MatrixForecastVo matrixForecastVo) {
        CategoryVo vo = categoryService.queryCategoryVo(NumberUtils.toInt(bean.getPlayId(), 0));
        if (vo != null) {
            matrixForecastVo.setCtype(MatrixConstant.MatrixCategoryType.values()[vo.getCtype()]);
            matrixForecastVo.setIsNeedBenchmark(vo.getBenchmark());
            bean.setRecType(matrixForecastVo.getCtype().ordinal());
            bean.setRelationScore(matrixForecastVo.getIsNeedBenchmark() == 1);
            if (bean.getRelationScore()) {
                bean.setHandicap(bean.getHandicap() + "-" + bean.getCurrentScore());
            }
        }
        if (bean.getHandicap() == null) {
            bean.setHandicap("0");
        }
        bean.setRecVal(JSONObject.toJSONString(matrixForecastVo.getMatrixArray()));
    }

    /**
     * 将矩阵计算结果数据设置到对应的OrderItem里面
     */
    public void setResultToItemBean(ExtendBean bean, OrderItem item) {
        item.setRecType(bean.getRecType());
        item.setValidateResult(bean.getValidateResult());
        if (bean.getRecType() != null) {
            if (bean.getRecType() == 0) {
                item.setRecVal(bean.getRecVal());
            } else if (bean.getRecType() == 1) {
                item.setRecVal(bean.getCurrentMaxPaid() + "");
            }
        }
        if (bean.getRelationScore() != null) {
            if (bean.getRelationScore()) {
                item.setIsRelationScore(1);
            } else {
                item.setIsRelationScore(0);
            }
        }
    }

    /**
     * @return void
     * @Description 处理盈利金额
     * @Param []
     * @Author max
     * @Date 16:24 2019/11/5
     **/
    public void setProfitAmount(SettleItem settleItem, OrderDetailsDTO orderDetailsDTO, ExtendBean extendBean) {
        //走水或拒单
        if (settleItem.getOutCome() == 2 || settleItem.getOutCome() == 9) {
            extendBean.setProfit(0L);
            return;
        }
        //输 赢本金
        if (settleItem.getOutCome() == 3) {
            extendBean.setProfit(orderDetailsDTO.getBetAmount() * -1);
            return;
        }
        //4-赢,赢一半
        if (settleItem.getOutCome() == 4 || settleItem.getOutCome() == 5) {
            extendBean.setProfit((settleItem.getSettleAmount() - orderDetailsDTO.getBetAmount()));
            return;
        }
        // 6-输一半
        if (settleItem.getOutCome() == 6) {
            extendBean.setProfit(settleItem.getSettleAmount() * -1);
            return;
        }

        if (settleItem.getSettleAmount() == null) {
            settleItem.setSettleAmount(0L);
        }
        extendBean.setProfit((settleItem.getSettleAmount() - orderDetailsDTO.getBetAmount()));
    }

    public long getProfitAmount(SettleItem settleItem) {
        //走水或拒单
        if (settleItem.getOutCome() == 2 || settleItem.getOutCome() == 9) {
            return 0L;
        }
        //输 赢本金
        if (settleItem.getOutCome() == 3) {
            return settleItem.getBetAmount() * -1;
        }
        if (settleItem.getSettleAmount() == null) {
            settleItem.setSettleAmount(0L);
        }
        //4-赢,赢一半
        if (settleItem.getOutCome() == 4 || settleItem.getOutCome() == 5) {
            return settleItem.getSettleAmount() - settleItem.getBetAmount();
        }
        // 6-输一半
        if (settleItem.getOutCome() == 6) {
            return settleItem.getSettleAmount() * -1;
        }
        return settleItem.getSettleAmount() - settleItem.getBetAmount();
    }


    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig>
     * @Description 查询玩法配置列表
     * tenantId 商户id
     * @Param [requestParam, item, type]
     * @Author max
     * @Date 14:46 2019/12/11
     **/
    public List<RcsBusinessPlayPaidConfig> getConfigList(String tenantId, OrderItem item, Integer playId) {
        List<RcsBusinessPlayPaidConfig> list = new ArrayList<RcsBusinessPlayPaidConfig>();
        //体育类型
        String sportId = item.getSportId().toString();
        //赛事类型
        String matchType = item.getMatchType() == 2 ? "1" : "0";
        //所属时间段
        String theirTime = rcsPaidConfigService.getPlayProcess(String.valueOf(item.getSportId()), String.valueOf(item.getPlayId()));
        RcsBusinessPlayPaidConfig playConfig = configService.getPlayPaidConfig(tenantId, sportId, matchType, theirTime, playId.toString());
        list.add(playConfig);
        return list;
    }


    /**
     * @return com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo
     * @Description 获取玩法配置configVo
     * @Param seriesType 1单关 2串关
     * @Author max
     * @Date 14:51 2019/12/11
     **/
    public RcsBusinessPlayPaidConfigVo getConfigVo(int seriesType, OrderItem item, RcsBusinessPlayPaidConfig config) {
        RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
        vo.setPlayId((Long.valueOf(item.getPlayId())));
        if (seriesType == 1) {
            vo.setMinBet(propertiesUtil.getInt("sdk.single.min.amount", 0).longValue());
        } else {
            vo.setMinBet(propertiesUtil.getInt("sdk.multi.min.amount", 10).longValue());
        }
        BigDecimal oddsValue = new BigDecimal(item.getHandleAfterOddsValue().toString()).subtract(new BigDecimal("1"));
        if (oddsValue.compareTo(BigDecimal.ZERO) == 0) {
            vo.setOrderMaxPay(config.getOrderMaxPay());
        } else {
            //最大赔付除以赔率 得到最大限额
            vo.setOrderMaxPay(new BigDecimal(config.getOrderMaxPay()).divide(oddsValue, 2, RoundingMode.HALF_UP).longValue());
        }
        return vo;
    }


    /**
     * @return void
     * @Description 查询前选项最大可投注金额验证
     * @Param [requestParam]
     * @Author max
     * @Date 15:02 2019/12/11
     **/
    public void checkSettleArguments(OrderDetailsDTO orderDetailsDTO) {
        if (null == orderDetailsDTO) {
            throw new RuntimeException("参数错误");
        }
       /* if(orderDetailsDTO.getPlayType()==null){//赛事阶段  1：全场  2：上半场  3：下半场
            throw new RuntimeException("playType不能为空");
        }*/
        if (orderDetailsDTO.getTournamentLevel() == null) {
            throw new RuntimeException("tournamentLevel不能为空");
        }
        if (orderDetailsDTO.getMatchId() == null) {
            throw new RuntimeException("mathId不能为空");
        }
        if (orderDetailsDTO.getBeginTime() == null) {
            throw new RuntimeException("赛事开始时间不能为空");
        }
        if (orderDetailsDTO.getMatchType() == null) {
            throw new RuntimeException("赛事类型不能为空");
        }
        if (orderDetailsDTO.getSportId() == null) {
            throw new RuntimeException("sportID不能为空");
        }
        if (orderDetailsDTO.getUid() == null) {
            throw new RuntimeException("用户不能为空");
        }
        if (orderDetailsDTO.getMarketType() == null) {
            throw new RuntimeException("盘口类型不能为空");
        }
        if (orderDetailsDTO.getPlayOptions() == null) {
            throw new RuntimeException("投注项playOptions不能为空");
        }
    }

    /**
     * 体育拉单接口
     * @param dto
     */
    public void validatePullSingleArguments(TicketResultDto dto) {
        if (StringUtils.isBlank(dto.getId())) {
            log.error("拉单，id,订单号不能为空");
            throw new com.panda.sport.rcs.exeception.RcsServiceException("id 订单编号不能为空");
        }
        if(dto.getSourceId()==null) {
            log.error("注单orderNo:{} -dateSourceId 数据来源id不能为空", dto.getId());
            throw new com.panda.sport.rcs.exeception.RcsServiceException("dateSourceId 数据来源id不能为空");
        }
    }
}
