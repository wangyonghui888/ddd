package com.panda.sport.sdk.strategy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.config.ConfigApiService;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.limit.Mts1StatusReqVo;
import com.panda.sport.data.rcs.dto.limit.RcsQuotaBusinessLimitResVo;
import com.panda.sport.rcs.enums.DataSourceEnum;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.core.JedisClusterServer;
import com.panda.sport.sdk.service.impl.LimitConfigService;
import com.panda.sport.sdk.util.GuiceContext;
import com.panda.sport.sdk.util.PropertiesUtil;
import com.panda.sport.sdk.util.RcsLocalCacheUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.panda.sport.sdk.constant.RedisKeys.THIRD_MERCHANT_STATUS;
import static net.bytebuddy.matcher.ElementMatchers.isVirtual;

public class StrategyFactory {

    private static final Logger logger = LoggerFactory.getLogger(StrategyFactory.class);

    /**
     * 获取处理方法
     *
     * @param orderBean
     * @return
     */
    public static IOrderStrategy getOrderStrategy(OrderBean orderBean) {
        JedisClusterServer jedisClusterServer = GuiceContext.getInstance(JedisClusterServer.class);
        //所属的特殊限额的种类  0 无  1标签限额  2特殊百分比限额 3特殊单注单场限额 4特殊vip限额
        String key = LimitRedisKeys.getUserSpecialLimitKey(orderBean.getUid().toString());
        String type = RcsLocalCacheUtils.getValue(key, LimitRedisKeys.USER_SPECIAL_LIMIT_TYPE_FIELD, jedisClusterServer::hget);
        String indexKey = org.springframework.util.StringUtils.isEmpty(orderBean.getOrderNo()) ? String.valueOf(orderBean.getUid()) : orderBean.getOrderNo();
        String logKey = StringUtils.isBlank(orderBean.getOrderNo()) ? "限额" : "投注";
        if(isRedCatThird(orderBean)){
            //如果是红猫接口就走风控策略
            if (StringUtils.isNotBlank(type) && type.equals("4")) {
                logger.info("::{}::当前订单选择VIP操盘,限额策略:{}", indexKey, SpecialVipStrategy.class.getName());
                for (OrderItem orderItem : orderBean.getItems()) {
                    orderItem.setRiskChannel(1);
                }
                return GuiceContext.getInstance(SpecialVipStrategy.class);
            }
            //如果是红猫接口就走风控策略
            logger.info("::{}::当前用户走风控策略,限额策略:{}", indexKey, RiskOrderV3Strategy.class.getName());
            return GuiceContext.getInstance(RiskOrderV3Strategy.class);
        }
        else if (isVirtual(orderBean) && SdkConstants.VIRSTUAL_SPORT.contains(orderBean.getSportId())) {
            //只有虚拟体育类型才进
            logger.info("::{}::{}当前订单选择第三方virtual处理", indexKey, logKey);
            return GuiceContext.getInstance(VirtualOrderStrategy.class);
        } else if (isOddin(orderBean)) {
            logger.info("::{}::{}当前订单选择Oddin处理", indexKey, logKey);
            return GuiceContext.getInstance(OddinOrderStrategy.class);
        } else if (isMtsWithThird(orderBean)) {
            for (OrderItem orderItem : orderBean.getItems()) {
                orderItem.setRiskChannel(2);
            }
            logger.info("::{}::{}当前订单选择Third处理", indexKey, logKey);
            return GuiceContext.getInstance(ThirdOrderV3Strategy.class);
        } else if (isMtsWithSR(orderBean)) {
            logger.info("::{}::当前订单选择MTS操盘,限额策略:{}", indexKey, MtsOrderV3Strategy.class.getName());
            for (OrderItem orderItem : orderBean.getItems()) {
                orderItem.setRiskChannel(2);
            }
            return GuiceContext.getInstance(MtsOrderV3Strategy.class);
        } else if (isMtsWithPI(orderBean)) {
            logger.info("::{}::当前订单选择MTS(PI)操盘,限额策略:{}", indexKey, MtsOrderWithPIStrategy.class.getName());
            orderBean.getItems().forEach(o -> o.setRiskChannel(2));
            return GuiceContext.getInstance(MtsOrderWithPIStrategy.class);
        } else if (StringUtils.isNotBlank(type) && type.equals("4")) {
            logger.info("::{}::当前订单选择VIP操盘,限额策略:{}", indexKey, SpecialVipStrategy.class.getName());
            for (OrderItem orderItem : orderBean.getItems()) {
                orderItem.setRiskChannel(1);
            }
            return GuiceContext.getInstance(SpecialVipStrategy.class);
        } else {
            logger.info("::{}::当前订单选择PA操盘,限额策略:{}", indexKey, RiskOrderV3Strategy.class.getName());
            for (OrderItem orderItem : orderBean.getItems()) {
                orderItem.setRiskChannel(1);
            }
            return GuiceContext.getInstance(RiskOrderV3Strategy.class);
        }
    }

    /**
     * 判断是否走mts接口
     */
    public static Boolean isMtsWithSR(OrderBean orderBean) {
        return isMts(orderBean, DataSourceEnum.SR);
    }

    /**
     * 判断是否走mts接口
     */
    public static Boolean isMtsWithPI(OrderBean orderBean) {
        return isMts(orderBean, DataSourceEnum.PI);
    }

    private static boolean isMts(OrderBean orderBean, DataSourceEnum dataSource) {
        if (Objects.isNull(dataSource)) return false;

        List<OrderItem> orderItemList = orderBean.getItems();
        PropertiesUtil propertiesUtil = GuiceContext.getInstance(PropertiesUtil.class);
        int mtsStatus = propertiesUtil.getInt("sdk.mts.status", 0);
        if (mtsStatus != 1) {
            return false;
        }


        OrderItem mtsOrderItem = orderBean.getItems().get(0);
        //判断mts-1开关是否开启
        JedisClusterServer jedisClusterServer = GuiceContext.getInstance(JedisClusterServer.class);
        String swithKey = "rcs:redis:mts:contact:config:matchId:%s:matchType:%s";
        int matchType = orderBean.getItems().get(0).getMatchType();
        swithKey = String.format(swithKey, orderBean.getItems().get(0).getMatchId(), matchType == 2 ? 0 : 1);
        String mtsSwitchConfig = jedisClusterServer.get(swithKey);
        logger.info("mts-1缓存key,{},订单号,{}开关:{}", swithKey, orderBean.getOrderNo(), mtsSwitchConfig);
        if (StringUtils.isNotBlank(mtsSwitchConfig)) {
            Integer mtsSwitch = JSONObject.parseObject(mtsSwitchConfig).getInteger("mtsSwitch");
            if (mtsSwitch == 1) {
                logger.info("当前订单选择mts-1操盘,{}", orderBean.getOrderNo());
                return false;
            }
        }

        // 是否都是同数据(SR、PI...)，并且有一个是MTS操盘就交给MTS操盘
        boolean isAllData = true;
        boolean isContaintMTSPlatform = true;
        boolean isAutoTradeType = true;
        for (OrderItem orderItem : orderItemList) {
            if (!dataSource.name().equals(orderItem.getDataSourceCode())) {
                isAllData = false;
            }
            if (!"MTS".equalsIgnoreCase(orderItem.getPlatform())) {
                isContaintMTSPlatform = false;
            }

            if ("1".equalsIgnoreCase(String.valueOf(orderItem.getTradeType()))) {
                isAutoTradeType = false;
            }
        }

        //是单关并且不是自动操盘，则不走MTS
        if (orderItemList.size() == 1 && !isAutoTradeType) {
            return false;
        }
        return isAllData && isContaintMTSPlatform;
    }

    //判断是否是mts-1操盘
    private static boolean ismts1(long matchId, int matchType, String orderNo) {
        try {
            String swithKey = "rcs:redis:mts:contact:config:matchId:%s:matchType:%s";
            swithKey = String.format(swithKey, matchId, matchType);
            matchType = matchType == 2 ? 0 : 1;

            String mtsSwitchConfig;
            Object cache = RcsLocalCacheUtils.timedCache.get(swithKey);
            if (cache != null) {
                mtsSwitchConfig = cache.toString();
            } else {
                Request<Mts1StatusReqVo> reqVoRequest = new Request<>();
                Mts1StatusReqVo mts1StatusReqVo = new Mts1StatusReqVo();
                mts1StatusReqVo.setMatchId(matchId);
                mts1StatusReqVo.setMatchType(matchType);
                reqVoRequest.setData(mts1StatusReqVo);
                ConfigApiService configApiService = GuiceContext.getInstance(ConfigApiService.class);
                Response<String> response = configApiService.getMts1Status(reqVoRequest);
                mtsSwitchConfig = response.getData();
                RcsLocalCacheUtils.timedCache.put(swithKey, mtsSwitchConfig, 5 * 60 * 1000);
            }

            logger.info("mts-1缓存key,{},订单号,{}开关:{}", swithKey, orderNo, mtsSwitchConfig);
            if (StringUtils.isNotBlank(mtsSwitchConfig)) {
                Integer mtsSwitch = JSONObject.parseObject(mtsSwitchConfig).getInteger("mtsSwitch");
                if (mtsSwitch == 1) {
                    logger.info("当前订单选择mts-1操盘,{}", orderNo);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.info("判断是否mts-1操盘异常,{}", orderNo);
            return true;
        }
    }
    /**
     * 红猫策略
     * @param orderBean
     * @return
     */
    public static Boolean isRedCatThird(OrderBean orderBean){
        if(StringUtils.isBlank(orderBean.getOrderNo())){
            //选择限额策略
            for (OrderItem orderItem : orderBean.getItems()) {
                //包含红猫赛事的限额 默认走风控
                if (Arrays.asList(OrderTypeEnum.REDCAT.getDataSource()).contains(orderItem.getDataSourceCode())) {
                    orderItem.setRiskChannel(1);
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 红猫策略
     * @param orderBean
     * @return
     */
    public static Boolean isOddin(OrderBean orderBean) {
        List<OrderItem> orderItemList = orderBean.getItems();
        String orderNo = orderBean.getOrderNo();
        logger.info("::{}::Oddin操盘策略判断", orderNo);
        boolean isOdd = true;
        boolean isAutoTradeType = true;
        //串关是否包含其他操盘的赛事
        int isContainsOTSPlatform = 0;
        //OTS操盘并且为自动操盘才发往第三方
        for (OrderItem orderItem : orderItemList) {
            if (!OrderTypeEnum.ODDIN.getPlatFrom().equalsIgnoreCase(orderItem.getPlatform())) {
                logger.info("::{}::非Oddin操盘", orderNo);
                isOdd = false;
            }
            //手动操盘不走第三方 0自动 1手动
            if ("1".equalsIgnoreCase(String.valueOf(orderItem.getTradeType()))) {
                logger.info("::{}::非A操盘模式:{}", orderNo, orderItem.getTradeType());
                isAutoTradeType = false;
            }
            //串关包含不同的操盘赛事 不走第三方
            if (OrderTypeEnum.ODDIN.getPlatFrom().equalsIgnoreCase(orderItem.getPlatform())) {
                isContainsOTSPlatform++;
            }
        }
        int size = orderItemList.size();
        if (size == 1) {
            if (!isAutoTradeType || isSubmitThird(orderBean)) {
                logger.info("::{}::单关非A操盘模式或者内部商户不走Oddin", orderNo);
                return false;
            }
        } else {
            if (isContainsOTSPlatform != size) {
                logger.info("::{}::串关包含其他操盘方赛事不走Oddin", orderNo);
                return false;
            }
        }
        //第三方数据源并且自动操盘
        return isOdd && isAutoTradeType;
    }


    /**
     * 是否走第三方投注
     *
     * @param orderBean 订单
     * @return 是否走第三方逻辑
     */
    public static Boolean isMtsWithThird(OrderBean orderBean) {
        List<OrderItem> orderItemList = orderBean.getItems();
        String orderNo = orderBean.getOrderNo();
        logger.info("::{}::Third操盘策略判断", orderNo);
        /*PropertiesUtil propertiesUtil = GuiceContext.getInstance(PropertiesUtil.class);
        int mtsStatus = propertiesUtil.getInt("sdk.mts.status", 0);
        if (mtsStatus != 1) {
            return false;
        }*/
        //是否第三方数据源赛事 或者是GTS操盘
        boolean isAllData = true;
        //串关是否包含其他操盘的赛事
        int isContainsGTSPlatform = 0;
        int isContainsCTSPlatform = 0;
        int isContainsREDCATPlatform = 0;
        boolean isAutoTradeType = true;
        for (OrderItem orderItem : orderItemList) {
            //是否指定的三方操盘平台
            String platform = orderItem.getPlatform();
            boolean isThirdPlatForm = OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(platform)
                    || OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(platform)
                    || OrderTypeEnum.REDCAT.getDataSource().equalsIgnoreCase(orderItem.getDataSourceCode());
            if (!isThirdPlatForm) {
                logger.info("::{}::非三方操盘:{}", orderNo, platform);
                isAllData = false;
            }
            //手动操盘不走第三方 0自动 1手动
            if ("1".equalsIgnoreCase(String.valueOf(orderItem.getTradeType()))) {
                logger.info("::{}::非A操盘模式:{}", orderNo, orderItem.getTradeType());
                isAutoTradeType = false;
            }
            //串关包含不同的操盘赛事 不走第三方
            if (OrderTypeEnum.GTS.getPlatFrom().equalsIgnoreCase(platform)) {
                ++isContainsGTSPlatform;
            }
            if (OrderTypeEnum.CTS.getPlatFrom().equalsIgnoreCase(platform)) {
                isContainsCTSPlatform++;
            }
            if (OrderTypeEnum.REDCAT.getDataSource().equalsIgnoreCase(orderItem.getDataSourceCode())) {
                isContainsREDCATPlatform++;
            }
        }
        //是单关并且不是自动操盘或者是内部商户，不走第三方
        int size = orderItemList.size();
        if (size == 1) {
            if (!isAutoTradeType || isSubmitThird(orderBean)) {
                logger.info("::{}::单关非A操盘模式或者内部商户不走第三方", orderNo);
                return false;
            }
        } else {
            boolean isContainsOther = (isContainsGTSPlatform != 0 && isContainsGTSPlatform != size)
                    || (isContainsCTSPlatform != 0 && isContainsCTSPlatform != size)
                    || (isContainsREDCATPlatform != 0 && isContainsREDCATPlatform != size);
            if (isContainsOther) {
                logger.info("::{}::串关包含其他操盘方赛事不走第三方", orderNo);
                return false;
            }
        }
        //第三方数据源并且自动操盘
        return isAllData && isAutoTradeType;
    }

    private static boolean isSubmitThird(OrderBean orderBean) {
        //特殊商户的注单都不提交数据商 只走内部接单
        boolean isSubmitThird = getMerchantList(orderBean);
        //足球 篮球默认不走内部
        Integer sportId = orderBean.getItems().get(0).getSportId();
        if (Arrays.asList(1, 2).contains(sportId)) {
            isSubmitThird = false;
            logger.info("::{}::{}默认发往第三方投注", orderBean.getOrderNo(), sportId == 1 ? "足球" : "篮球");
        }
        return isSubmitThird;
    }

    /**
     * 内部商户默认不发往第三方
     * @param orderBean
     * @return
     */
    private static Boolean getMerchantList(OrderBean orderBean) {
        JedisClusterServer jedisClusterServer = GuiceContext.getInstance(JedisClusterServer.class);
        String merchantList = jedisClusterServer.get(THIRD_MERCHANT_STATUS);
        if (StringUtils.isBlank(merchantList)) {
            merchantList = "[]";
        }
        logger.info("::{}::内部商户:{}", orderBean.getOrderNo(), merchantList);
        //获取商户限额配置
        LimitConfigService limitConfigService = GuiceContext.getInstance(LimitConfigService.class);
        RcsQuotaBusinessLimitResVo businessLimit = limitConfigService.getBusinessLimit(orderBean.getTenantId());
        return JSONArray.parseArray(merchantList).contains(businessLimit.getParentName());
    }

    /**
     * 虚拟订单是否走第三方
     * true --走第三方限额
     * false--走风控限额
     *
     * @param orderBean
     * @return
     */
    public static Boolean isVirtual(OrderBean orderBean) {
        String vrEnableKey = String.format(LimitRedisKeys.VR_ENABLE_AMOUNT_RATE, orderBean.getTenantId());
        JedisClusterServer jedisClusterServer = GuiceContext.getInstance(JedisClusterServer.class);
        String vrEnable = jedisClusterServer.get(vrEnableKey);
        String indexKey = org.springframework.util.StringUtils.isEmpty(orderBean.getOrderNo()) ? String.valueOf(orderBean.getUid()) : orderBean.getOrderNo();
        logger.info("::{}::用户开启藏单:{}", indexKey, vrEnable);
        //用户开启藏单-->只查我们自己的限额逻辑
        if ((StringUtils.isNotBlank(vrEnable) && vrEnable.equals("1")) || StringUtils.isBlank(vrEnable)) {
            return true;
        } else {
            return false;
        }
    }



}
