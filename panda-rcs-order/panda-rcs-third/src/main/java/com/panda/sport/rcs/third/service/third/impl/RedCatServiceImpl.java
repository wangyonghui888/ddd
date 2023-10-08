package com.panda.sport.rcs.third.service.third.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Stopwatch;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.bts.ThirdBetParamDto;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderTypeEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.service.RcsSwitchService;
import com.panda.sport.rcs.third.common.ThirdReceivedConstants;
import com.panda.sport.rcs.third.entity.common.pojo.RcsRtsOrderExt;
import com.panda.sport.rcs.third.entity.gts.GtsAuthorizationVo;
import com.panda.sport.rcs.third.entity.redCat.RedCatBetCancelResponseData;
import com.panda.sport.rcs.third.entity.redCat.RedCatBetPlaceResponseData;
import com.panda.sport.rcs.third.enums.*;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.third.config.RedcatApiConfig;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.entity.common.ThirdResultVo;
import com.panda.sport.rcs.third.factory.ThirdStrategyFactory;
import com.panda.sport.rcs.third.mapper.RcsRtsOrderExtMapper;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.third.service.reject.IOrderAcceptService;
import com.panda.sport.rcs.third.service.third.ThirdOrderBaseService;
import com.panda.sport.rcs.third.service.third.ThirdOrderService;
import com.panda.sport.rcs.third.util.encrypt.ZipStringUtils;
import com.panda.sport.rcs.third.util.http.HttpResponseCodeUtil;
import com.panda.sport.rcs.utils.RcsLocalCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.panda.sport.rcs.third.common.Constants.*;
import static com.panda.sport.rcs.third.common.NumberConstant.RTS_DEFAULT_DISCOUNT;
import static com.panda.sport.rcs.third.common.ThirdUrl.*;
import static com.panda.sport.rcs.third.enums.RcsThirdExceptionEnum.*;

/**
 * @author Beulah
 * @date 2023/5/9 20:30
 * @description todo
 */
@Slf4j
@Service
public class RedCatServiceImpl extends ThirdOrderBaseService implements ThirdOrderService, InitializingBean {


    /**
     * 拉单日志头
     */
    private static final String PLACE_BET_NAME = "RED_CAT_BET_PLACED";
    /**
     * 获取token日志头
     */
    private static final String GET_TOKEN = "RED_CAT_TOKEN";

    private static final String BET_CANCELED = "RED_CAT_BET_CANCEL";
    /**
     * 获取最大限额
     */
    private static final Long MAX_LIMIT_DEFAULT = 2000L;

    /**
     * redis token 过期时间
     */
    private static final Long TOKEN_REDIS_EXPIRED = 23 * 3600L;

    /**
     * 本地缓存过期时间
     */
    private static final Long TOKEN_LOCAL_EXPIRED = 23 * 3600 * 1000L;

    /**
     * redis 注单缓存
     */
    private static final Long ORDER_REDIS_EXPIRED = 300L;

    /**
     * 接口返回状态成功key
     */
    private static final String SUCCESS_KEY = "success";

    /**
     * 请求接口使用的币种
     */
    private static final String CURRENCY = "CNY";
    /**
     * 注单本地缓存
     */
    private static final Long ORDER_LOCAL_EXPIRED = 30 * 1000L;

    @Autowired
    RedisClient redisClient;
    @Autowired
    RedisClient<GtsAuthorizationVo> redisClientObj;

    @Resource
    RedcatApiConfig config;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @Autowired
    IOrderHandlerService handlerService;
    @Autowired
    IOrderAcceptService orderAcceptService;
    @Resource
    RcsRtsOrderExtMapper rcsRtsOrderExtMapper;
    @Resource
    RcsSwitchService rcsSwitchService;


    @Override
    public Long getMaxBetAmount(ThirdBetParamDto dto) {
        return MAX_LIMIT_DEFAULT;
    }

    /**
     * 系统投注(投注时出现token失效情况则再次尝试获取token，发送投注)
     *
     * @param ext
     * @return
     */
    @Override
    public ThirdResultVo placeBet(ThirdOrderExt ext) {
        ThirdResultVo vo = new ThirdResultVo();
        vo.setThirdOrderStatus(ThirdOrderStatusEnum.PENDING.getType());
        if (!ext.getSeriesType().equals(SeriesKindEnum.SINGLE.getType())) {
            //非单关,目前只支持单关，直接拒单
            log.error("::{}::{}::【{}】>>当前注单不属于单关,不处理,seriesType:【{}】", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, ext.getSeriesType());
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            return vo;
        }

        //获取token
        String token = "";
        try {
            token = getToken(ext.getLinkId(), ext.getOrderNo(), false);
        } catch (Exception ex) {
            log.error("::{}::{}::【{}】=>redCat获取token失败", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, ex);
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            vo.setReasonMsg(ex.getMessage());
            return vo;
        }
        //redis存入临时缓存，处理数据商推送注单过来的数据
        String message = ZipStringUtils.gzip(JSONObject.toJSONString(ext));
        String redisKey = String.format(RED_CAT_BET_PLACED_ORDER_NO, ext.getOrderNo());
        redisClient.setExpiry(redisKey, message, ORDER_REDIS_EXPIRED);
        String thirdOddsFiledSourceId = "";
        //单关注单详情
        ExtendBean detail = ext.getList().get(0);
        try {
            //通过缓存获取对应盘口
            //首先从本地缓存获取
            String key = String.format(REDIS_RED_CAT_SELECTION_ID_KEY, detail.getSelectId());
            thirdOddsFiledSourceId = RcsLocalCacheUtils.getValueInfo(key);
            if (StringUtils.isEmpty(thirdOddsFiledSourceId)) {
                //从redis中获取缓存
                thirdOddsFiledSourceId = redisClient.get(key);
                if (StringUtils.isEmpty(thirdOddsFiledSourceId)) {
                    log.error("::{}::{}::【{}】>>对应的投注项配置不存在,selectedId:【{}】,投注内容:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, detail.getSelectId(), JSONObject.toJSONString(ext));
                    vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
                    vo.setReasonMsg("投注项对应配置不存在，投注项id:" + thirdOddsFiledSourceId);
                    return vo;
                } else {
                    log.info("::{}::{}::【{}】>>从redis获取投注项成功,selectedId:【{}】", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, detail.getSelectId());
                }
            } else {
                log.info("::{}::{}::【{}】>>从本地缓存中获取投注项成功,selectedId:【{}】", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, detail.getSelectId());
            }
        } catch (Exception ex) {
            //获取盘口失败，返回拒单
            log.error("::{}::{}::【{}】>>获取盘口失败,selectedId:【{}】", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, detail.getSelectId(), ex);
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            vo.setReasonMsg(ex.getMessage());
            return vo;
        }
        //第三方投注结果处理
        Stopwatch betWatch = Stopwatch.createStarted();
        vo = handlerPlaceBetResult(ext, detail, thirdOddsFiledSourceId, token);
        log.info("::{}::{}::【{}】>>发起投注耗时:【{}】", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, betWatch.elapsed(TimeUnit.MILLISECONDS));
        return vo;
    }


    @Override
    public Boolean orderConfirm(ThirdOrderExt ext) {
        if (ext.getOrderStatus().equals(OrderStatusEnum.REJECTED.getCode())) {
            //如果拒单则发起取消动作
            log.info("::{}::{}=>后置检查拒单，发起取消注单请求", ext.getLinkId(), ext.getOrderNo());
            orderCancel(ext);
        }
        return true;
    }


    @Override
    public Object convertThirdParam(ThirdOrderExt ext) {
        return null;
    }

    @Override
    public String updateThirdOrderStatus(OrderBean orderBean, String reason) {
        Stopwatch stopwatch=Stopwatch.createStarted();
        //0：待处理  1：已接单  2：拒单
        LambdaQueryWrapper<RcsRtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsRtsOrderExt::getOrderNo, orderBean.getOrderNo());
        RcsRtsOrderExt ext = rcsRtsOrderExtMapper.selectOne(wrapper);
        log.info("::{}::查询第三方更新订单状态完成，耗时:{}",orderBean.getOrderNo(),stopwatch.elapsed(TimeUnit.MILLISECONDS));
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
        stopwatch.reset().start();
        rcsRtsOrderExtMapper.updateById(ext);
        log.info("::{}::业务主动取消注单,更新第三方{}订单表完成,耗时:{}", ext.getOrderNo(), third,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return ext.getThirdNo();
    }

    @Override
    public boolean orderIsCanceled(String orderNo) {
        Stopwatch stopwatch=Stopwatch.createStarted();
        LambdaQueryWrapper<RcsRtsOrderExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsRtsOrderExt::getOrderNo, orderNo);
        RcsRtsOrderExt rcsCtsOrderExt = rcsRtsOrderExtMapper.selectOne(wrapper);
        log.info("::{}::查询订单是否取消状态,耗时:{}",orderNo,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        if (rcsCtsOrderExt == null) {
            return false;
        }
        return rcsCtsOrderExt.getCancelStatus() == 1;
    }

    @Override
    public void saveOrder(ThirdOrderExt thirdOrderExt) {
        String orderNo = thirdOrderExt.getList().get(0).getOrderId();
        String third = thirdOrderExt.getThird();
        try {
            save(thirdOrderExt, orderNo, third);

        } catch (Exception e) {
            log.error("::{}::{}::投注-{}订单入库处理异常:", thirdOrderExt.getLinkId(), orderNo, third, e);
        }
        log.info("::{}::{}::投注-{}订单入库处理完成", thirdOrderExt.getLinkId(), orderNo, third);
    }

    /**
     * C01 特有的 第三方投注保存方法(为了兼容版本，特殊处理)
     *
     * @param thirdOrderExt
     * @return 是否有主键冲突
     */
    public boolean saveOrderByC01(ThirdOrderExt thirdOrderExt) {
        String orderNo = thirdOrderExt.getList().get(0).getOrderId();
        String third = thirdOrderExt.getThird();
        try {
            save(thirdOrderExt, orderNo, third);
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            if (throwable instanceof SQLIntegrityConstraintViolationException) {
                //进入主键冲突
                return false;
            }
            log.error("::{}::{}::投注-{}订单入库处理异常:", thirdOrderExt.getLinkId(), orderNo, third, e);
        }
        log.info("::{}::{}::投注-{}订单入库处理完成", thirdOrderExt.getLinkId(), orderNo, third);
        return true;
    }

    /**
     * 保存方法
     *
     * @param thirdOrderExt 注单信息
     * @param orderNo       注单号
     * @param third         第三方标识
     */
    private void save(ThirdOrderExt thirdOrderExt, String orderNo, String third) {
        Stopwatch stopwatch=Stopwatch.createStarted();
        RcsRtsOrderExt ext = new RcsRtsOrderExt();
        ext.setOrderNo(orderNo);
        ext.setStatus("INIT");
        if (thirdOrderExt.getPaTotalAmount() != null) {
            //分转为元
            BigDecimal paTotalAmount = thirdOrderExt.getPaTotalAmount().divide(new BigDecimal("100"), 2, RoundingMode.FLOOR);
            ext.setPaAmount(paTotalAmount.toPlainString());
        }
        BigDecimal thirdAmount = discountAmount(thirdOrderExt);
        ext.setResult(thirdOrderExt.getThirdResJson());
        ext.setCreTime(new Date());
        ext.setThirdName(third);
        ext.setThirdNo(thirdOrderExt.getThirdOrderNo());
        ext.setRemark("订单保存");
        ext.setRtsAmount(thirdAmount.toPlainString());
        rcsRtsOrderExtMapper.insert(ext);
        log.info("::{}::订单保存耗时:{}",orderNo,stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }


    @Override
    public void updateOrder(ThirdOrderExt orderExt) {
        Stopwatch finalWatch=Stopwatch.createStarted();
        Stopwatch stopwatch= Stopwatch.createStarted();
        String orderNo = null;
        try {
            orderNo = orderExt.getList().get(0).getOrderId();
            LambdaQueryWrapper<RcsRtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsRtsOrderExt::getOrderNo, orderNo);
            //防止首次入库失败
            RcsRtsOrderExt ext = rcsRtsOrderExtMapper.selectOne(wrapper);
            log.info("::{}::{}::投注-{} 查询第三方库完成,耗时:{}", orderExt.getLinkId(), orderNo, orderExt.getThird(),stopwatch.elapsed(TimeUnit.MILLISECONDS));
            if (ext == null) {
                boolean result = saveOrderByC01(orderExt);
                if (!result) {
                    //没有主键冲突，插入成功，直接返回
                    log.info("::{}::{}::投注-{} 没有主键冲突真接返回结果", orderExt.getLinkId(), orderNo, orderExt.getThird());
                    return;
                }
                log.info("::{}::{}::投注-{} 主键冲突，继续更新", orderExt.getLinkId(), orderNo, orderExt.getThird());
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
            stopwatch.reset().start();
            rcsRtsOrderExtMapper.updateById(ext);
            log.info("::{}::{}::投注-{} 更新第三方库完成,耗时:{}",orderExt.getLinkId(),orderNo,orderExt.getThird(),stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            log.error("::{}::{}::投注-{}订单更新处理异常:", orderExt.getLinkId(), orderNo, orderExt.getThird(), e);
        }
        log.info("::{}::{}::投注-{}订单更新处理完成,总计耗时:{}", orderExt.getLinkId(), orderNo, orderExt.getThird(),finalWatch.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * 取消注单
     *
     * @param ext
     */
    @Override
    public void orderCancel(ThirdOrderExt ext) {
        String token = "";
        //获取token操作
        try {
            token = getToken(ext.getLinkId(), ext.getOrderNo(), false);
        } catch (Exception ex) {
            log.error("::{}::{}::【{}】=>获取token异常", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, ex);
            return;
        }
        //第一次请求数据商操作
        String postResult = "";
        //错误原因
        String errorReason = "";
        try {
            postResult = postOrderCancel(ext, token);
        } catch (RcsServiceException exception) {
            if (exception.getCode().equals(HTTP_UN_AUTHORIZATION.getCode())) {
                try {
                    //token 校验失败,再次尝试获取token
                    log.info("::{}::{}=>redCat 第二次尝试获取token", ext.getLinkId(), ext.getOrderNo());
                    token = getToken(ext.getLinkId(), ext.getOrderNo(), true);
                    log.info("::{}::{}=>redCat 第二次尝试取消", ext.getLinkId(), ext.getOrderNo());
                    //发起二次取消请求
                    postResult = postOrderCancel(ext, token);
                } catch (RcsServiceException rcsServiceException) {
                    //如果碰到二次尝试投注失败,不处理,只记录日志
                    log.error("::{}::{}::【{}】=> 第二次尝试取消失败", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, rcsServiceException.getCode());
                    errorReason = rcsServiceException.getMessage();
                    return;
                } catch (Exception e) {
                    errorReason = e.getMessage();
                    log.error("::{}::{}::【{}】=> 第二次尝试取消失败", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, e);
                    return;
                }
            }

        } catch (Exception ex) {
            //其他异常,只记录日志，不处理
            errorReason = ex.getMessage();
            updateOrderCancelFailedReason(ext, ex.getMessage());
            log.error("::{}::{}::【{}】=> 取消失败,发生其他异常", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, ex);
            return;
        }
        if (StringUtils.isNotBlank(errorReason)) {
            //发生错误
            updateOrderCancelFailedReason(ext, errorReason);
            return;
        }
        //解析数据商返回结果
        errorReason = setOrderCancel(ext, postResult);
        if (StringUtils.isNotBlank(errorReason)) {
            updateOrderCancelFailedReason(ext, errorReason);
        } else {
            updateOrderCancelFailedReason(ext, "取消成功");
        }
    }

    /**
     * 取消失败
     *
     * @param ext    注单五一特惠
     * @param reason 错误原因
     */
    @Override
    public void updateOrderCancelFailedReason(ThirdOrderExt ext, String reason) {
        try {
            Stopwatch stopwatch=Stopwatch.createStarted();
            LambdaQueryWrapper<RcsRtsOrderExt> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsRtsOrderExt::getOrderNo, ext.getOrderNo());
            RcsRtsOrderExt orderExt = rcsRtsOrderExtMapper.selectOne(wrapper);
            if (orderExt == null) {
                throw new RcsServiceException("订单信息未找到");
            }
            orderExt.setRemark("业务取消但数据商接口错误，错误原因:" + reason);
            rcsRtsOrderExtMapper.updateById(orderExt);
            log.info("::{}::{}::第三方数据商取消失败原因更新完成,耗时:{}", ext.getLinkId(), ext.getOrderNo(),stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception ex) {
            log.error("::{}::{}::更新数据库发生异常", ext.getLinkId(), ext.getOrderNo());
        }

    }

    @Override
    public BigDecimal discountAmount(ThirdOrderExt ext) {
        String val = null;
        try {
            String busDiscountKey = String.format(RTS_AMOUNT_RATE, ext.getBusId());
            val = redisClient.get(busDiscountKey);
            log.info("::{}::{}::{}投注获取到商户:{}对应折扣率为:{}", ext.getLinkId(), ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            if (StringUtils.isBlank(val)) {
                String busAllDiscountKey = RTS_AMOUNT_RATE_ALL;
                val = redisClient.get(busAllDiscountKey);
                log.info("::{}::{}::{}投注获取到商户:{}通用折扣率为:{}", ext.getLinkId(), ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            }
            if (StringUtils.isBlank(val)) {
                val = RTS_DEFAULT_DISCOUNT;
                log.info("::{}::{}::{}投注获取到商户:{}默认折扣率为:{}", ext.getLinkId(), ext.getOrderNo(), ext.getThird(), ext.getBusId(), val);
            }
        } catch (Exception e) {
            val = RTS_DEFAULT_DISCOUNT;
            log.info("::{}::{}::{}投注获取商户:{}折扣率异常:使用默认折扣率:{}", ext.getLinkId(), ext.getOrderNo(), ext.getThird(), ext.getBusId(), val, e);
        }
        return ext.getPaTotalAmount().divide(new BigDecimal("100"), 2, RoundingMode.FLOOR).multiply(new BigDecimal(val));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ThirdStrategyFactory.register(OrderTypeEnum.REDCAT.getPlatFrom(), this);
    }

    /**
     * 第三方接注结果处理
     *
     * @param ext                    一些注单参数
     * @param detail                 注单明细
     * @param thirdOddsFiledSourceId 第三方赔率投注项配置
     * @param token                  token
     * @return
     */
    public ThirdResultVo handlerPlaceBetResult(ThirdOrderExt ext, ExtendBean detail, String thirdOddsFiledSourceId, String token) {
        ThirdResultVo vo = new ThirdResultVo();
        vo.setThirdOrderStatus(ThirdOrderStatusEnum.PENDING.getType());
        String responseResult = "";
        try {
            //第一次请求数据商投注接口
            responseResult = placeBetPost(ext, detail, thirdOddsFiledSourceId, token);
        } catch (RcsServiceException ex) {
            if (ex.getCode().equals(HTTP_UN_AUTHORIZATION.getCode())) {
                //token 校验失败,有可能失效,再次尝试获取token
                try {
                    token = getToken(ext.getLinkId(), ext.getOrderNo(), true);
                    log.info("::{}::{}::【{}】=>redCat 第二次尝试获取token成功", ext.getLinkId(), ext.getOrderNo(), GET_TOKEN);
                } catch (RcsServiceException exception) {
                    //网络超时，直接拒单
                    vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
                    vo.setReasonMsg(exception.getMessage());
                    log.error("::{}::{}::【{}】=>获取token网络异常,拒单处理,失败原因:{}", ext.getLinkId(), ext.getOrderNo(), GET_TOKEN, ex.getMessage(), exception);
                }
                try {
                    //第二请求数据商
                    responseResult = placeBetPost(ext, detail, thirdOddsFiledSourceId, token);
                    log.info("::{}::{}::【{}】=>redCat 第二次尝试投注成功", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME);

                } catch (RcsServiceException exception) {
                    //非网络读取超时
                    if (!exception.getCode().equals(HTTP_READ_TIME_OUT.getCode())) {
                        //如果碰到二次尝试投注失败,则直接拒单
                        vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
                        vo.setReasonMsg(exception.getMessage());
                        log.error("::{}::{}::【{}】=>第二次尝试投注失败,拒单处理,失败原因:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, ex.getMessage(), exception);
                    }
                    return vo;
                } catch (Exception e) {
                    //其他异常，直接拒单
                    vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
                    vo.setReasonMsg(e.getMessage());
                    log.error("::{}::{}::【{}】=>第二次尝试投注失败,拒单处理,失败原因:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, ex.getMessage(), e);
                    return vo;
                }
            }
        } catch (Exception ex) {
            //其他异常，直接拒单
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            vo.setReasonMsg(ex.getMessage());
            log.error("::{}::{}::【{}】=>第二次尝试投注失败,拒单处理,失败原因:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, ex.getMessage(), ex);
            return vo;
        }
        //处理数据商返回的结果
        try {
            if (StringUtils.isEmpty(responseResult)) {
                log.error("::{}::{}::【{}】>>请求数据商未返回任务数据,result:【{}】", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, JSONObject.toJSONString(responseResult));
                //没有返回任何信息，等待超时
                vo.setThirdOrderStatus(ThirdOrderStatusEnum.PENDING.getType());
                return vo;
            }
            //处理业务
            vo = getPlaceBetResult(ext, responseResult);
        } catch (Exception ex) {
            //内部异常，直接拒单
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            log.error("::{}::{}::【{}】=>解析投注状态失败,拒单处理,失败原因:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, ex.getMessage(), ex);
            return vo;
        }
        return vo;
    }

    /**
     * 获取token
     *
     * @param isExpires 过期状态
     * @return
     */
    public String getToken(String linkId, String orderNo, boolean isExpires) {
        String tokenKey = String.format(RC_TOKEN, TokenTypeEnum.BetAssessmentAPI.getCode());
        if (!isExpires) {
            //先从缓存获取  缓存不存在则从第三方接口获取
            try {
                GtsAuthorizationVo vo = (GtsAuthorizationVo) RcsLocalCacheUtils.timedCache.get(tokenKey);
                if (Objects.nonNull(vo) && StringUtils.isNotEmpty(vo.getAccessToken())) {
                    //本地有缓存
                    log.info("::{}::{}::【{}】=>从本地缓存从获取token成功,value:【{}】", linkId, orderNo, GET_TOKEN, vo.getAccessToken());
                    return vo.getAccessToken();
                }
            } catch (Exception ex) {

            }
            try {
                //从redis 获取缓存
                GtsAuthorizationVo vo = redisClientObj.getObj(tokenKey, GtsAuthorizationVo.class);
                //过期时间
                Long expireIn = vo.getExpiresIn();
                //刷新时间
                Long refreshTime = vo.getRefreshTime();
                //当前时间
                Long now = System.currentTimeMillis();
                if (now - refreshTime < expireIn) {
                    //新的刷新时间为当时
                    vo.setRefreshTime(now);
                    //获取剩余时间= 剩余过期时间-（当前时间-刷新时间)
                    long remainTime = expireIn - (now - refreshTime);
                    vo.setExpiresIn(remainTime);
                    RcsLocalCacheUtils.timedCache.put(tokenKey, vo, remainTime);
                }
                log.info("::{}::{}::【{}】=>从redis缓存从获取token成功,value:【{}】", linkId, orderNo, GET_TOKEN, vo.getAccessToken());
                return vo.getAccessToken();
            } catch (Exception ex) {
            }

        }
        String url = String.format("%s%s?username=%s&password=%s", config.getUrl(), RED_CAT_TOKEN_URL, URLEncoder.encode(config.getUsername()), URLEncoder.encode(config.getPwd()));
        log.info("::{}::{}::【{}】=>获取token请求::{}", linkId, orderNo, GET_TOKEN, url);
        String data = "";
        try {
            data = HttpResponseCodeUtil.get(url, null);
        } catch (RcsServiceException ex) {
            //错误码
            log.error("::{}::{}::【{}】=>获取token请求数据商异常", linkId, orderNo, GET_TOKEN, ex);
            throw new RcsServiceException(ex.getCode(), ex.getErrorMassage());
        } catch (Exception ex) {
            log.error("::{}::{}::【{}】=>获取token请求系统内部异常", linkId, orderNo, GET_TOKEN, ex);
            throw new RcsServiceException("redCat获取token请求异常,异常内容" + ex.getMessage());
        }
        try {
            log.error("::{}::【{}】=>获取token请求返回::{}", linkId, GET_TOKEN, data);
            //更新所有机器缓存
            GtsAuthorizationVo authorizationVo = new GtsAuthorizationVo();
            authorizationVo.setType(TokenTypeEnum.BetAssessmentAPI.getCode());
            authorizationVo.setAccessToken(data);
            authorizationVo.setThird(ThirdTokenTypeEnum.RC.getCode());
            authorizationVo.setExpiresIn(TOKEN_LOCAL_EXPIRED);
            authorizationVo.setTokenype(BEARER);
            authorizationVo.setRefreshTime(System.currentTimeMillis());
            //拿到token存缓存 Expiress是接口返回的token失效时间 提前1小时过期 避免临界点问题
            redisClient.setExpiry(tokenKey, JSONObject.toJSONString(authorizationVo), TOKEN_REDIS_EXPIRED);
            RcsLocalCacheUtils.timedCache.put(tokenKey, authorizationVo, TOKEN_LOCAL_EXPIRED);
            return data;
        } catch (Exception e) {
            log.error("::{}::{}::【{}】=>设置redCat异常", linkId, orderNo, GET_TOKEN, e);
            throw new RcsServiceException(SYS_INTERNAL_ERROR.getCode(), SYS_INTERNAL_ERROR.getMessage());
        }

    }

    /**
     * 投注结果返回
     *
     * @param ext 传入参数
     * @return 返回投注结果
     */
    private ThirdResultVo getPlaceBetResult(ThirdOrderExt ext, String result) throws Exception {
        ThirdResultVo vo = new ThirdResultVo();
        vo.setThirdOrderStatus(ThirdOrderStatusEnum.PENDING.getType());
        //开始解析数据
        vo.setThirdRes(result);
        //对数据进行解析
        JSONObject responseData = null;
        try {
            responseData = JSONObject.parseObject(result);
        } catch (Exception ex) {
            log.error("::{}::{}::【{}】>>解析失败,原数据:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, result);
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            vo.setReasonMsg("红猫数据商返回数据解析不正确");
            return vo;
        }
        if (Objects.isNull(responseData)) {
            log.error("::{}::{}::【{}】>>解析以后是空值,原数据:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, result);
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            return vo;
        }
        //处理数据
        String success = responseData.getString(SUCCESS_KEY);
        if (!success.equals(ThirdReceivedConstants.RedCatMessage.SUCCESS)) {
            //失败
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            vo.setErrorCode(responseData.getString("systemStatus"));
            vo.setReasonMsg("数据商系统异常:" + responseData.getString("error"));
            return vo;
        }
        //请求成功
        RedCatBetPlaceResponseData resultData = responseData.getObject("data", RedCatBetPlaceResponseData.class);
        vo.setErrorCode(resultData.getMessage());
        //处理数据，并返回结果
        try {
            String status = resultData.getBetStatus();
            switch (status) {
                //已经处理
                case ThirdReceivedConstants.RedCatMessage.ACCEPTED:
                    vo.setThirdOrderStatus(ThirdOrderStatusEnum.SUCCESS.getType());
                    vo.setThirdNo(String.valueOf(resultData.getRedcatBetId()));
                    ext.setThirdOrderNo(String.valueOf(resultData.getRedcatBetId()));
                    break;
                //正在处理当中
                case ThirdReceivedConstants.RedCatMessage.PENDING:
                    vo.setThirdNo(String.valueOf(resultData.getRedcatBetId()));
                    ext.setThirdOrderNo(String.valueOf(resultData.getRedcatBetId()));
                    break;
                default:
                    //其余状态全部按拒单处理
                    vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
                    vo.setReasonMsg(resultData.getMessage());
                    break;
            }
            log.info("::{}::{}::【{}】>>解析以后的订单状态:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, vo.getThirdOrderStatus());
        } catch (Exception ex) {
            //产生异常
            log.error("::{}::{}::【{}】>>解析以后是空值,原数据:{}", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, result, ex);
            vo.setThirdOrderStatus(ThirdOrderStatusEnum.REJECTED.getType());
            vo.setReasonMsg("内部异常");
            return vo;
        }
        return vo;
    }

    /**
     * 投注请求
     *
     * @return
     */
    private String placeBetPost(ThirdOrderExt ext, ExtendBean detail, String thirdOddsFiledSourceId, String token) {
        //组装数据
        Map<String, Object> params = new TreeMap<>();
        params.put("betId", Long.valueOf(ext.getOrderNo()));
        params.put("amount", discountAmount(ext));
        params.put("oddsValue", new BigDecimal(detail.getOdds()));
        params.put("acceptOdds", ext.getAcceptOdds());
        params.put("selectionId", Long.valueOf(thirdOddsFiledSourceId));
        //临时测试用，此赛事关盘太快，为了方便手动测试用
        params.put("currency", CURRENCY);
        String url = config.getUrl() + RED_CAT_BET_PLACED_URL;
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Authorization", "Bearer " + token);
        log.info("::{}::{}::【{}】>>请求数据商开始", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME);
        Stopwatch sendWatch = Stopwatch.createStarted();
        String result = HttpResponseCodeUtil.post(url, params, true, headMap);
        /*   AsyncHttpUtil.postJsonByMap(url, params, true, headMap, catCallBack);*/
        log.info("::{}::【{}】>>请求数据商响应结果,params:【{}】,返回结果:【{}】,url:【{}】,耗时:【{}】", ext.getOrderNo(), PLACE_BET_NAME, JSONObject.toJSONString(params), result, url, sendWatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
        //return null;
    }

    /**
     * 返回取消请求数据商结果
     *
     * @param ext
     * @param token
     * @return
     * @throws Exception
     */
    private String postOrderCancel(ThirdOrderExt ext, String token) throws Exception {
        Stopwatch stopwatch=Stopwatch.createStarted();
        //组装参数
        String url = null;
        Map<String, Object> params = new TreeMap<>();
        params.put("betId", ext.getOrderNo());
        url = config.getUrl() + RED_CAT_BET_CANCEL_URL;
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Authorization", "Bearer " + token);
        String result = HttpResponseCodeUtil.post(url, params, true, headMap);
        log.info("::{}::{}::【{}】=>请求参数:{},返回:{},耗时:{}", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, JSONObject.toJSONString(params), result,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    /**
     * 处理取消操作
     *
     * @param ext    注单明细
     * @param result 错误原因，正常取消返回空
     * @return
     */
    private String setOrderCancel(ThirdOrderExt ext, String result) {
        //错误原因定义
        String errorReason = "";
        //组装参数
        try {
            if (StringUtils.isNotBlank(result)) {
                JSONObject responseData = JSONObject.parseObject(result);
                if (responseData.getString(SUCCESS_KEY).equals(ThirdReceivedConstants.RedCatMessage.SUCCESS)) {
                    //请求成功
                    RedCatBetCancelResponseData resultData = responseData.getObject("data", RedCatBetCancelResponseData.class);
                    if (resultData.getBetStatus().equals(ThirdReceivedConstants.RedCatMessage.CANCELLED)) {
                        //取消成功
                        log.info("::{}::{}::【{}】=>成功", ext.getLinkId(), resultData.getBetId(), BET_CANCELED);
                    } else {
                        //取消失败
                        errorReason = resultData.getMessage();
                        log.error("::{}::{}:: 【{}】=>失败，失败原因:", ext.getLinkId(), resultData.getBetId(), BET_CANCELED, resultData.getMessage());
                    }
                } else {
                    String error = responseData.getString("error");
                    //取消失败(系统问题)
                    errorReason = error;
                    log.error("::{}::{}::【{}】=>失败,失败原因:{}", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, error);
                }

            } else {
                errorReason = "网络异常，未返回数据";
                log.warn("::{}::{}::【{}】=>未返回响应数据,params={}", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, JSONObject.toJSONString(result));
            }
        } catch (Exception e) {
            errorReason = e.getMessage();
            log.error("::{}::{}::【{}】=>异常,params={}", ext.getLinkId(), ext.getOrderNo(), BET_CANCELED, JSONObject.toJSONString(result), e);
        }
        return errorReason;

    }


    /**
     * 投注请求(测试用)
     *
     * @return
     * @return
     * @throws Exception
     */
    public String placeBetPostTest(ThirdOrderExt ext, ExtendBean detail, String token) throws Exception {
        //组装数据
        Map<String, Object> params = new TreeMap<>();
        params.put("betId", Long.valueOf(ext.getOrderNo()));
        params.put("amount", ext.getPaTotalAmount());
        params.put("oddsValue", new BigDecimal(detail.getOdds()));
        params.put("acceptOdds", ext.getAcceptOdds());
        params.put("selectionId", detail.getSelectId());
        //临时测试用，此赛事关盘太快，为了方便手动测试用
        params.put("currency", CURRENCY);
        String url = config.getUrl() + RED_CAT_BET_PLACED_URL;
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Authorization", "Bearer " + token);
        Stopwatch sendWatch = Stopwatch.createStarted();
        String result = HttpResponseCodeUtil.post(url, params, true, headMap);
        log.info("::{}::{}::【{}】>>请求数据商响应结果,params:【{}】,返回结果:【{}】,url:【{}】,耗时:【{}】", ext.getLinkId(), ext.getOrderNo(), PLACE_BET_NAME, JSONObject.toJSONString(params), result, url, sendWatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }
}
