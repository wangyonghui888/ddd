package com.panda.sport.rcs.mgr.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.sport.data.rcs.api.MtsApiService;
import com.panda.sport.data.rcs.api.OrderPaidApiService;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.*;
import com.panda.sport.data.rcs.dto.limit.UserLimitReferenceResVo;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.SeriesEnum;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.log.annotion.monnitor.MonitorAnnotion;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mgr.constant.RcsCacheContant;
import com.panda.sport.rcs.mgr.paid.PaidService;
import com.panda.sport.rcs.mgr.paid.matrix.MatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import com.panda.sport.rcs.mgr.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.mgr.utils.CopyUtils;
import com.panda.sport.rcs.mgr.utils.RedisUtils;
import com.panda.sport.rcs.mgr.utils.TOrderDetailExtUtils;
import com.panda.sport.rcs.mgr.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessPlayPaidConfigService;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp;
import com.panda.sport.rcs.mgr.wrapper.impl.RcsRectanglePlayServiceImpl;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.mgr.repository.TOrderDetailExtRepository;
import com.panda.sport.rcs.vo.RcsBusinessPlayPaidConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 风控对外提供接口服务
 * 查询用户最大最小限额
 * 用户下单效验
 * @Param
 * @Author max
 * @Date 11:26 2019/12/11
 * @return
 **/
@Service(connections = 5, retries = 0)
@Slf4j
@org.springframework.stereotype.Service
@Path("")
public class OrderPaidApiImpl implements OrderPaidApiService {

    @Autowired
    PaidService paidService;
    @Autowired
    MatrixForecast matrixForecast;
    @Autowired
    RcsRectanglePlayServiceImpl playService;
    @Autowired
    TOrderDetailMapper orderDetailMapper;
    @Autowired
    TOrderDetailExtMapper orderDetailExtMapper;
    @Autowired
    ITOrderDetailService orderDetailService;
    @Autowired
    RcsBusinessPlayPaidConfigService rcsBusinessPlayPaidConfigService;
    @Autowired
    RcsPaidConfigServiceImp rcsPaidConfigService;
    @Autowired
    SeriesTradeService seriesTradeService;
    @Autowired
    TOrderMapper tOrderMapper;
    @Autowired
    TOrderDetailExtRepository TOrderDetailExtRepository;
    @Autowired
    TOrderDetailExtUtils tOrderDetailExtUtils;
    @Autowired
    ParamValidate paramValidate;
    @Autowired
    TaskExecutorOrder taskExecutorOrder;
    @Reference(retries = 3, lazy = true, check = false)
    MtsApiService mtsApiService;
    @Autowired
    RedisUtils redisUtils;


    @Autowired
    private RedisClient redisClient;

    @Resource
    RcsMatchOrderAcceptConfigMapper rcsMatchOrderAcceptConfigMapper;
    @Resource
    RcsTournamentOrderAcceptConfigMapper rcsTournamentOrderAcceptConfigMapper;

    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Value("${paid.single.min}")
    private long singleMinAmount;
    @Value("${paid.mts.bet}")
    private Integer mtsBet;
    @Value("${paid.mts.seriesType}")
    private Integer mtsSeriesType;
    @Value("${paid.multi.min}")
    private long multiMinAmount;
    @Value("${rocketmq.order.save.config}")
    private String saveOrderConfig;
    @Value("${rocketmq.order.realTimeVolume.config}")
    private String realTimeVolumeConfig;

    /**
     * SDK 初始化参数加载
     *
     * @return Response
     */
    @Override
    @POST
    @Path("/loadSdkConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    public Response loadSdkConfig() {
        try {
            rcsPaidConfigService.sendCacheConfigMQ();
            Map<String, Object> result = new HashMap<>(1);
            result.put("status", 1);
            return Response.success(result);
        } catch (Exception ex) {
            return Response.error(500, ex.getMessage());
        }
    }

    /**
     * @return com.panda.sport.data.rcs.api.Response
     * @Description 查询用户未登录最大最小限额
     * @Param [requestParam]
     * @Author max
     * @Date 14:52 2019/12/11
     **/
    @Override
    @POST
    @Path("queryInitMaxBetMoneyBySelect")
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Trace
    @Deprecated
    public Response queryInitMaxBetMoneyBySelect(Request<OrderBean> requestParam) {
        Map<String, Object> result = Maps.newHashMap();
        List<RcsBusinessPlayPaidConfigVo> li = Lists.newArrayList();
        result.put("data", li);
        OrderItem item=null;
        try {
            paramValidate.checkInitMaxBetArguments(requestParam);
            //串关限额
            if (requestParam.getData().getSeriesType() != 1) {
                return seriesTradeService.queryMaxBetMoneyBySelect(requestParam);
            }
            item = requestParam.getData().getItems().get(0);
            if (item.getSportId() == 1) {
                List<RcsBusinessPlayPaidConfig> list = paramValidate.getConfigList(requestParam, item, item.getPlayId());
                if (list != null && list.size() > 0) {
                    li.add(paramValidate.getConfigVo(requestParam, item, list.get(0)));
                } else {
                    //查找其它玩法
                    list = paramValidate.getConfigList(requestParam, item, -1);
                    if (list != null && list.size() > 0) {
                        li.add(paramValidate.getConfigVo(requestParam, item, list.get(0)));
                    } else {
                        log.warn("::{}::玩法配置异常,未找到当前参数:{}配置的玩法.",item.getOrderNo(),requestParam);
                        throw new LogicException("500", "玩法配置异常,未找到配置的玩法！");
                    }
                }
            } else {
                RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
                vo.setPlayId((Long.valueOf(item.getPlayId())));
                if (requestParam.getData().getSeriesType() == 1) {
                    vo.setMinBet(singleMinAmount);
                }
                if (requestParam.getData().getSeriesType() == 2) {
                    vo.setMinBet(multiMinAmount);
                }
                vo.setOrderMaxPay(100000000L);
                li.add(vo);
            }
        } catch (LogicException e) {
            log.error("::{}::查找未登录最大最小限额异常{}",item.getOrderNo(),e.getMessage());
            return Response.error(500, e.getMsg());
        } catch (Exception e) {
            log.error("::{}::查找未登录最大最小限额异常{}",item.getOrderNo(),e.getMessage());
            return Response.error(500, "查找未登录最大最小限额异常");
        }

        return Response.success(result);
    }

    /**
     * @return com.panda.sport.data.rcs.api.Response
     * @Description 用户点击投注选项返回当前选项最大可投注金额
     * @Param [requestParam]
     * @Author max
     * @Date 14:54 2019/12/11
     **/
    @Override
    @POST
    @Path("queryMaxBetMoneyBySelect")
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Trace
    @Deprecated
    public Response queryMaxBetMoneyBySelect(Request<OrderBean> requestParam) {
        //最大限额
        Long amount;
        try {
            //各种参数校验
            paramValidate.checkMaxBetArguments(requestParam);
            //设置最小值
            RcsBusinessPlayPaidConfigVo vo = new RcsBusinessPlayPaidConfigVo();
            vo.setMinBet(singleMinAmount);
            if (requestParam.getData().getSeriesType() == 1) {
                vo.setMinBet(singleMinAmount);
            }
            if (requestParam.getData().getSeriesType() == 2) {
                vo.setMinBet(multiMinAmount);
            }
            //返回结果result对象
            Map<String, Object> result = Maps.newHashMap();
            List<RcsBusinessPlayPaidConfigVo> list = Lists.newArrayList();

            //是否走mts接口
            Boolean mtsFlag = true;

            List<ExtendBean> extendBeanList = new ArrayList<>();
            //注单列表  循环判断每个注单是否都满足走mts
            List<OrderItem> orderItemList = requestParam.getData().getItems();
            for (OrderItem orderItem : orderItemList) {
                //构建ExtendBean对象
                ExtendBean bean = paramValidate.buildExtendBean(requestParam.getData(), orderItem);
                MatrixForecastVo matrixForecastVo = new MatrixForecastVo();
                paramValidate.putBeanVal(bean, matrixForecastVo);
                //matrixForecastVo设置玩法id
                if (matrixForecastVo.getMarketCategoryId() == null && ObjectUtils.isNotEmpty(bean.getPlayId())) {
                    matrixForecastVo.setMarketCategoryId(NumberUtils.toLong(bean.getPlayId(), 0));
                }
                //是否走mts接口判断
                if (mtsFlag) {
                    mtsFlag = isMts(orderItem.getMatchId(), Integer.valueOf(orderItem.getMatchType()));
                }
                extendBeanList.add(bean);
            }
            ExtendBean firstExtendBean = extendBeanList.get(0);

            //串关是否走MTS开关
            if (requestParam.getData().getSeriesType() != 1) {
                if (mtsSeriesType == 1) {
                    mtsFlag = true;
                    log.info("::{}::串关走MTS开关开启状态!",firstExtendBean.getOrderId());
                }
            }
            log.info("::{}:: {}查询最大可下注额mtsFlag:{}",firstExtendBean.getOrderId(),requestParam.getGlobalId(), mtsFlag);
            //mts查询额度
            if (mtsFlag) {
                //串关限额  如:40011
                Integer seriesType = requestParam.getData().getSeriesType();
                if (seriesType != 1) {
                    //一共多少个赛事
                    int seriesNum = SeriesEnum.getSeriesEnumBySeriesJoin(seriesType).getSeriesNum();
                    //查询所有N串1的额度
                    for (int i = 2; i <= seriesNum; i++) {
                        Request<MtsgGetMaxStakeDTO> request = new Request<>();
                        request.setGlobalId(requestParam.getGlobalId() + i);
                        request.setData(new MtsgGetMaxStakeDTO(extendBeanList, i, false));
                        amount = mtsApiService.getMaxStake(request).getData();
                        log.info("::{}::、{}{}获取mts N串1 最大限额{}", requestParam.getGlobalId(), i, amount);
                        vo = new RcsBusinessPlayPaidConfigVo();
                        vo.setMinBet(multiMinAmount);
                        vo.setOrderMaxPay(amount);
                        vo.setType(i + "001");
                        list.add(vo);
                    }
                    if (seriesNum > 2) {
                        //增加N串M的额度
                        Request<MtsgGetMaxStakeDTO> request = new Request<>();
                        request.setGlobalId(requestParam.getGlobalId() + seriesType);
                        request.setData(new MtsgGetMaxStakeDTO(extendBeanList, seriesNum, true));
                        amount = mtsApiService.getMaxStake(request).getData();
                        log.info("::{}::{}{}获取mts N串M 最大限额{}",firstExtendBean.getOrderId(),requestParam.getGlobalId(), seriesType, amount);
                        vo = new RcsBusinessPlayPaidConfigVo();
                        vo.setMinBet(multiMinAmount);
                        vo.setOrderMaxPay(amount);
                        vo.setType(seriesType.toString());
                        list.add(vo);
                    }
                    log.info("::{}:: {}获取mts单注最大限额返回{}",firstExtendBean.getOrderId(),requestParam.getGlobalId(), JSONObject.toJSONString(list));
                } else { //单注
                    Request<ExtendBean> request = new Request<>();
                    request.setGlobalId(requestParam.getGlobalId() + seriesType);
                    firstExtendBean.getItemBean().setBetAmount(10000L);
                    request.setData(firstExtendBean);
                    amount = mtsApiService.getSingleMaxStake(request).getData();
                    log.info("::{}::{}获取mts单注最大限额{}",firstExtendBean.getOrderId(),requestParam.getGlobalId(), amount);
                    vo.setOrderMaxPay(amount);
                    list.add(vo);
                }

            } else {
                //串关限额
                if (requestParam.getData().getSeriesType() != 1) {
                    return seriesTradeService.queryMaxBetMoneyBySelect(requestParam);
                }
                //篮球不做限制，直接返回最大值
                amount = 100000000L;
                //计算最大限额
                if (firstExtendBean.getSportId().equalsIgnoreCase("1")) {
                    amount = paidService.getUserSelectsMaxBetAmount(firstExtendBean, null);
                }
                vo.setOrderMaxPay(amount);
                list.add(vo);
            }
            result.put("data", list);
            return Response.success(result);
        } catch (LogicException e) {
            log.error("::{}:: {}查询用户最大可投注金额异常{}",requestParam.getData().getOrderNo(),requestParam.getGlobalId(), e);
            return Response.error(500, e.getMsg());
        } catch (RcsServiceException e) {
            log.error("::{}::{}查询用户最大可投注金额异常{}",requestParam.getData().getOrderNo(),requestParam.getGlobalId(), e);
            return Response.error(500, e.getErrorMassage());
        } catch (Exception e) {
            log.error("::{}::{}查询用户最大可投注金额异常{}",requestParam.getData().getOrderNo(),requestParam.getGlobalId(), e);
            return Response.error(500, "查询用户最大可投注金额异常");
        }
    }

    /**
     * @return Response
     * @Description 校验当前订单是否超过最大赔付金额
     * 该方法已废弃,用户下注会进行效验 不用单独验证
     * @Param [requestParam]
     * @Author max
     * @Date 20:42 2019/12/10
     **/
    @Override
    @POST
    @Path("validateOrderMaxPaid")
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Trace
    @Deprecated
    public Response validateOrderMaxPaid(Request<OrderBean> requestParam) {
        Map<String, Object> result = new HashMap<>(1);
        result.put(requestParam.getData().getOrderNo(), true);
        return Response.success(result);
    }

    /**
     * @return Response
     * @Description 订单矩阵效验入库保存
     * @Param [requestParam] 订单参数
     * @Author max
     * @Date 20:47 2019/12/10
     **/
    @Override
    @POST
    @Path("saveOrderAndValidateMaxPaid")
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Trace
    @Deprecated
    public Response saveOrderAndValidateMaxPaid(Request<OrderBean> requestParam) {

        Map<String, Object> result = new HashMap<>(1);
        boolean mtsFlag;
        //0 失败  1成功  2 mts渠道  待处理
        int status;
        try {
            OrderBean orderMsgBean = paramValidate.checkSaveArguments(requestParam);
            //验证是否走mst渠道
            mtsFlag = isMts(requestParam.getData());

            //串关是否走MTS开关
            if (requestParam.getData().getSeriesType() != 1) {
                if (mtsSeriesType == 1) {
                    mtsFlag = true;
                    log.info("::{}::串关走MTS开关开启状态!",orderMsgBean.getOrderNo());
                }
            }

            for (OrderItem orderItem : orderMsgBean.getItems()) {
                //订单风控验证渠道  1 : 内部风控  2 : mts
                orderItem.setRiskChannel(mtsFlag ? 2 : 1);
                orderItem.setValidateResult(0);
            }

            MatrixForecastVo matrixForecastVo = MatrixForecastVo.getMatrixForecastBean(requestParam.getData().getItems().get(0));

            Boolean isSuccess = true;
            Boolean mtsResult = mtsBetTicket(orderMsgBean.getExtendBean(), requestParam, matrixForecastVo);

            //发送订单MQ消息
            taskExecutorOrder.sendOrderMessage(orderMsgBean, "queue_settle_item,saveorder");

            if (mtsResult == false) {
                //串关限额
                if (requestParam.getData().getSeriesType() != 1) {
                    return seriesTradeService.saveOrderAndValidateMaxPaid(requestParam);
                }
                if (orderMsgBean.getExtendBean().getSportId().equalsIgnoreCase("1")) {
                    matrixForecast.MatrixForecastAmount(matrixForecastVo);
                    String matrixStatus =  matrixForecastVo.queryMatrixStatus();
                    paramValidate.putBeanVal(orderMsgBean.getExtendBean(), matrixForecastVo);
                    isSuccess = paidService.saveOrderAndValidateV2(orderMsgBean.getExtendBean(), matrixStatus);
                }
                result.put(requestParam.getData().getOrderNo(), isSuccess);
                if (isSuccess) {
                    //如果是滚球
                    if (orderScroll(requestParam.getData())) {
                        status = 2;
                        orderMsgBean.getExtendBean().setValidateResult(0);
                        paramValidate.setResultToItemBean(orderMsgBean.getExtendBean(), orderMsgBean.getItems().get(0));
                        taskExecutorOrder.sendOrderMessage(orderMsgBean, "queue_settle_item,saveorder");
                    } else {
                        orderMsgBean.getExtendBean().setValidateResult(1);
                        paramValidate.setResultToItemBean(orderMsgBean.getExtendBean(), orderMsgBean.getItems().get(0));
                        //更新订单消息
                        log.info("::{}::发送订单消息到MQ ---->> orderBean : {}",orderMsgBean.getOrderNo(),orderMsgBean);
                        taskExecutorOrder.sendOrderMessage(orderMsgBean, "queue_settle_item,saveorder|realTimeVolume");
                        status = 1;
                    }
                } else {
                    //更新为失败
                    orderMsgBean.getExtendBean().setValidateResult(2);
                    taskExecutorOrder.sendOrderMessage(orderMsgBean, "queue_settle_item,saveorder");
                    result.put(requestParam.getData().getOrderNo() + "_error_msg", "校验不通过");
                    status = 0;
                }
            } else {
                status = 2;
                result.put(requestParam.getData().getOrderNo(), true);
            }
        } catch (LogicException e) {
            log.error("::{}::保存订单失败{}",requestParam.getData().getOrderNo(),e.getMessage());
            status = 0;
            result.put(requestParam.getData().getOrderNo(), false);
            result.put(requestParam.getData().getOrderNo() + "_error_msg", e.getMessage());
        } catch (RcsServiceException e) {
            log.error("::{}::保存订单失败{}", requestParam.getData().getOrderNo(),e.getMessage());
            status = 0;
            result.put(requestParam.getData().getOrderNo(), false);
            result.put(requestParam.getData().getOrderNo() + "_error_msg", e.getMessage());
        } catch (Exception e) {
            log.error("::{}::保存订单失败{}",requestParam.getData().getOrderNo(),e.getMessage(),e.getMessage());
            status = 0;
            result.put("status", status);
            result.put(requestParam.getData().getOrderNo(), false);
            result.put(requestParam.getData().getOrderNo() + "_error_msg", "保存失败");
            return Response.success(result);
        }
        //是否走mts接口  如果该字段为
        result.put("status", status);
        return Response.success(result);
    }

    /**
     * @return Response
     * @Description 提供给业务-提前结算接口
     * @Param [requestParam] 订单参数
     * @Author Eamon
     * @Date 2023年7月1日21:48:31
     **/
    @Override
    @MonitorAnnotion(code = "RPC_PRE_SETTLE")
    public Response<PreOrderRequest> preSettleOrder(Request<PreOrderRequest> requestParam) {
        Map<String, Object> resultMap = new HashMap<>();
        //默认风控
        int orderType = 1;
        resultMap.put("orderType", orderType);
        return Response.success(resultMap);
    }

    /**
     * 校验是否有滚球
     *
     * @param orderBean
     * @return
     */
    private boolean orderScroll(OrderBean orderBean) {
        //是否滚球
        boolean scrollflag = false;
        //是否有手工接单
        boolean modleFlag = false;

        for (OrderItem item : orderBean.getItems()) {
            //查询赛事配置
            LambdaQueryWrapper<RcsMatchOrderAcceptConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(RcsMatchOrderAcceptConfig::getMatchId, item.getMatchId());
            RcsMatchOrderAcceptConfig rcsMatchOrderAcceptConfig = rcsMatchOrderAcceptConfigMapper.selectOne(queryWrapper);

            //查询联赛配置1
            LambdaQueryWrapper<RcsTournamentOrderAcceptConfig> eventWrapper = new LambdaQueryWrapper<>();
            eventWrapper.eq(RcsTournamentOrderAcceptConfig::getTournamentId, item.getTournamentId());
            RcsTournamentOrderAcceptConfig rcsTournamentOrderAcceptConfig = rcsTournamentOrderAcceptConfigMapper.selectOne(eventWrapper);

            //如果没有配置1
            if (rcsMatchOrderAcceptConfig == null && rcsTournamentOrderAcceptConfig == null) {
                continue;
            }
            //中场休息
            if(rcsMatchOrderAcceptConfig !=null && rcsMatchOrderAcceptConfig.getHalfTime()==1){
                continue;
            }
            if(rcsTournamentOrderAcceptConfig  !=null &&  rcsTournamentOrderAcceptConfig.getHalfTime()==1){
                continue;
            }

            //如果是手工接单
            if(rcsMatchOrderAcceptConfig!=null && rcsMatchOrderAcceptConfig.getMode()==1  ){
                modleFlag = true;
            }
            if(rcsTournamentOrderAcceptConfig!=null && rcsTournamentOrderAcceptConfig.getMode()==1  ){
                modleFlag = true;
            }

            //出现任何滚球赛事  需走滚球接拒单流程逻辑
            if (item.getMatchType() == 2) {
                scrollflag = true;
            }
        }

        //手工接单的需要推送 用于显示
        //及时注单
        if(modleFlag){
            taskExecutorOrder.sendOrderMessage(orderBean, MqConstants.WS_ORDER_BET_RECORD_TOPIC + "," +MqConstants.WS_ORDER_BET_RECORD_TAG);
        }
        log.info("::{}::滚球判断完成scrollflag:{} modleFlag:{}",orderBean.getOrderNo(),scrollflag,modleFlag);
        return scrollflag;
    }


    /**
     * 判断是否走mts接口
     * 下单走MTS规则
     * 1.操盘平台选择是MTS
     * 2、mtsBet 开关打开
     * 3、数据源是SR
     * 4、篮球和不支持的玩法
     *
     * @return
     */
    private Boolean isMts(Long matchId, int isScroll) {
        //mtsBet为1表示开启mts接口  数据源是SR的才走mts
        if (mtsBet == 1 && paramValidate.isMtsPlatForm(matchId, isScroll)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否走mts接口
     *
     * @return
     */
    private Boolean isMts(OrderBean orderBean) {
        Boolean result = true;
        List<OrderItem> orderItemList = orderBean.getItems();
        for (OrderItem orderItem : orderItemList) {
            ExtendBean bean = paramValidate.buildExtendBean(orderBean, orderItem);
            MatrixForecastVo matrixForecastVo = new MatrixForecastVo();
            matrixForecastVo.setMarketCategoryId(NumberUtils.toLong(bean.getPlayId(), 0));
            paramValidate.putBeanVal(bean, matrixForecastVo);
            //是否走mts接口判断
            if (result) {
                result = isMts(orderItem.getMatchId(), Integer.valueOf(bean.getIsScroll()));
            } else {
                log.info("::{}::isMts注单判断不通过{}", orderBean.getOrderNo(),JSONObject.toJSONString(orderItem));
                return false;
            }
        }
        return result;
    }

    /**
     * @return java.lang.Boolean
     * @Description mts 风控下单验证
     * @Param [extendBean]
     * mtsBet mts下注开关 1开 0关
     * 篮球、足球不支持的玩法 走MTS
     * @Author max
     * @Date 16:06 2019/12/19
     **/
    private Boolean mtsBetTicket(ExtendBean extendBean, Request<OrderBean> requestParam, MatrixForecastVo matrixForecastVo) {
        Boolean mtsFlag = true;
        //串关限额
        if (requestParam.getData().getSeriesType() != 1) {
            for (OrderItem orderItem : requestParam.getData().getItems()) {
                //是否走mts接口判断
                if (mtsFlag) {
                    String matchType = orderItem.getMatchType().equals("1") ? "0" : "1";
                    mtsFlag = isMts(orderItem.getMatchId(), Integer.valueOf(matchType));
                }
            }
        } else {
            mtsFlag = isMts(Long.parseLong(extendBean.getMatchId()), Integer.valueOf(extendBean.getIsScroll()));
        }

        //串关是否走MTS开关
        if (requestParam.getData().getSeriesType() != 1) {
            if (mtsSeriesType == 1) {
                mtsFlag = true;
                log.info("::{}::串关走MTS开关开启状态!",extendBean.getOrderId());
            }
        }

        if (mtsFlag == true) {
            log.info("::{}::下单验证-----MTS",extendBean.getOrderId());
            if (Strings.isNullOrEmpty(extendBean.getThirdTemplateSourceId())) {
                throw new LogicException("610", "第三方赛事模板ID不能为空！");
            }
            if (Strings.isNullOrEmpty(extendBean.getThirdMatchSourceId())) {
                throw new LogicException("610", "第三方赛事ID不能为空！");
            }

            log.info("::{}::下单验证-----MTS",extendBean.getOrderId());
            if (Strings.isNullOrEmpty(extendBean.getThirdTemplateSourceId())) {
                throw new LogicException("610", "第三方赛事模板ID不能为空！");
            }
            if (Strings.isNullOrEmpty(extendBean.getThirdMatchSourceId())) {
                throw new LogicException("610", "第三方赛事ID不能为空！");
            }
            if (Strings.isNullOrEmpty(extendBean.getSpecifiers())) {
                throw new LogicException("610", "SR Specifiers不能为空！");
            }

            //串关限额
            List<ExtendBean> list = new ArrayList<>();
            if (extendBean.getSeriesType() != 1) {
                for (int i = 0; i < requestParam.getData().getItems().size(); i++) {
                    ExtendBean bean = paramValidate.buildExtendBean(requestParam.getData(), requestParam.getData().getItems().get(i));
                    paramValidate.putBeanVal(bean, matrixForecastVo);
                    list.add(bean);
                }
            } else {
                list.add(extendBean);
            }

            //mts校验订单  map参数
            Map<String, Object> map = new HashMap<>();
            //注单列表
            map.put("list",list);
            //串   40011
            map.put("seriesNum",requestParam.getData().getSeriesType());
            map.put("ip",requestParam.getData().getIp());
            map.put("totalMoney",requestParam.getData().getProductAmountTotal());
            //发送订单MQ消息
            taskExecutorOrder.sendOrderMessage(map, "queue_validate_mts_order,validateMtsOrder");
        } else {
            log.info("::{}::下单验证-----Panda",extendBean.getOrderId());
        }
        return mtsFlag;
    }

    /**
     * 派奖后做订单状态和返奖数据同步
     * 成功返回注单号
     */
    @Override
    @Trace
    @Deprecated
    public Response updateOrderAfterRefund(Request<SettleItem> requestParam) {
        if (ObjectUtils.isNull(requestParam) || ObjectUtils.isNull(requestParam.getData())) {
            throw new RcsServiceException("参数为空:" + JSONObject.toJSONString(requestParam));
        }
        log.info("::{}:: updateOrderAfterRefund 传入参数：{}",requestParam.getData().getOrderNo(),JSONObject.toJSONString(requestParam));
        SettleItem settleItem = requestParam.getData();
        Preconditions.checkNotNull(settleItem.getOrderNo());
        Preconditions.checkNotNull(settleItem.getOrderDetailRisk());
        if(null == settleItem.getOrderDetailRisk().get(0).getMatchId()){
            String orderKey = String.format(RcsCacheContant.REDIS_MATCH_DETAIL_EXT_INFO_KEY, settleItem.getOrderNo());
            if(!redisClient.exist(orderKey)){
                log.warn(" ::{}::无订单信息 ", settleItem.getOrderNo());
                throw new RcsServiceException("参数为空:" + JSONObject.toJSONString(requestParam));
            }
            String orderBeanValue=redisClient.get(orderKey);
            OrderBean orderBean=JSONObject.parseObject(orderBeanValue,OrderBean.class);
            List<OrderDetailPO> poList= CopyUtils.clone(orderBean.getItems(),OrderDetailPO.class);
            settleItem.setOrderDetailRisk(poList);
            log.info("::{}:: 设置订单参数 {}",requestParam.getData().getOrderNo(),settleItem.getOrderDetailRisk().get(0).getMatchId());
        }
        for (int i = 0; i < settleItem.getOrderDetailRisk().size(); i++) {
            OrderDetailPO orderDetailPO=settleItem.getOrderDetailRisk().get(i);
            if (orderDetailPO == null) {
                log.warn("::{}:: updateOrderAfterRefund 当前注单不存在,settleItem:{}!", orderDetailPO.getOrderNo(),JSONObject.toJSONString(orderDetailPO));
                continue;
            }
            Map<String, Object> map = new HashMap<>(1);
            String betNo=settleItem.getOrderDetailRisk().get(i).getBetNo();
            settleItem.setBetNo(betNo);
            settleItem.setSettleStatus(settleItem.getPayoutStatus());
            ExtendBean extendBean=new ExtendBean();
            map.put("betNo", betNo);
            Map<String, Object> matchInfo = paramValidate.getMatchInfo(String.valueOf(orderDetailPO.getMatchId()), String.valueOf(orderDetailPO.getMarketId()), String.valueOf(orderDetailPO.getPlayOptionsId()));
            extendBean.setTournamentLevel(Integer.parseInt(String.valueOf(matchInfo.get("tournamentLevel"))));
            extendBean.setDateExpect(String.valueOf(matchInfo.get("dateExpect")));
            extendBean.setPlayType(rcsPaidConfigService.getPlayProcess(String.valueOf(orderDetailPO.getSportId()), String.valueOf(orderDetailPO.getPlayId())));
            extendBean.setMarketId(orderDetailPO.getMarketId().toString());
            paramValidate.setProfitAmount(settleItem, settleItem.getOrderDetailRisk().get(i), extendBean);
            extendBean.setIsScroll(String.valueOf(orderDetailPO.getMatchType()).equals("2") ? "1" : "0");
            //"1".equals(extendBean.getRiskChannel()) &&
            if ( !"1".equals(String.valueOf(settleItem.getIsSettled()))) {
            	//发送到sdk消费
            	settleItem.setSeriesType(extendBean.getSeriesType());
                putSettleItemBean(settleItem.getOrderDetailRisk().get(i), extendBean, matchInfo);
                sendMessage.sendMessage(MqConstants.SDK_ORDER_REFUSAL_TOPIC, RcsConstant.RISK_SEND_REFUSAL, settleItem.getOrderNo(), settleItem);
                log.info("::{}::取消订单-单关通知sdk处理额度完成", settleItem.getOrderNo());
            }
            /*更新注单状态,拒单的情况不更新结算状态
            * 滚球考虑拒单情况---3拒单
            * */
            map.put("isSettlement", NumberUtils.INTEGER_ONE);
            if (settleItem.getOutCome() == 9) {
                map.put("isSettlement", 3);
                map.put("validateResult", 3);
                // 滚球 取消订单更新
                if (extendBean.getIsScroll().equalsIgnoreCase("1")) {
                  if (tOrderDetailExtUtils.isSaveToMongo()) {
                    TOrderDetailExtRepository.updateOrderDetailExtStatus(settleItem.getOrderNo(), "2");
                  } else {
                    orderDetailExtMapper.updateOrderDetailExtStatus(settleItem.getOrderNo(), "2");
                  }
                }
            }
            //风控不需要处理结算状态信息 2022-10-18 Magic
//            redisUtils.rpush("RCS:SETTLE:ORDER:DETAIL:BATCH:SAVE", JSONObject.toJSONString(map));
//            int flag = orderDetailMapper.updateOrderDetailStatus(map);
            settleItem.setIsSuccess(true);

            log.info("::{};;更新注单状态成功:{}", requestParam.getData().getOrderNo(), JSONObject.toJSONString(map));
        }
        return Response.success(requestParam.getData().getOrderNo());
    }

    /**
     * 获取用户投注限额 上限 参考值
     *
     * @param request
     * @return
     */
    @Override
    public Response<UserLimitReferenceResVo> getUserLimitReference(Request<Long> request) {
        return null;
    }

    private void putSettleItemBean(OrderDetailPO orderDetailPO, ExtendBean extendBean, Map<String, Object> matchInfo) {
    	OrderDetailPO detailRisk = orderDetailPO;
    	detailRisk.setTournamentLevel(orderDetailPO.getTournamentLevel());
    	detailRisk.setPlayType(orderDetailPO.getPlayType());
    	detailRisk.setMatchId(orderDetailPO.getMatchId());
    	detailRisk.setSportId(orderDetailPO.getSportId());
    	detailRisk.setUid(orderDetailPO.getUid());
    	if(matchInfo.containsKey("beginTime")) {
    		detailRisk.setBeginTime(Long.parseLong(String.valueOf(matchInfo.get("beginTime"))));
    	}else {
    		detailRisk.setBeginTime(System.currentTimeMillis());
    	}
    	detailRisk.setMatchType(orderDetailPO.getMatchType());
    	detailRisk.setMarketType("OU");
    	detailRisk.setPlayOptions(orderDetailPO.getPlayOptions());
    	detailRisk.setOrderNo(orderDetailPO.getOrderNo());
    	detailRisk.setBetAmount(orderDetailPO.getBetAmount());
        detailRisk.setOddsValue(new BigDecimal(orderDetailPO.getOddsValue()).multiply(new BigDecimal("100000")).doubleValue());
    	detailRisk.setPlayOptionsId(orderDetailPO.getPlayOptionsId());
    	detailRisk.setMarketValue(orderDetailPO.getMarketValue());
        detailRisk.setMaxWinAmount(Double.valueOf(detailRisk.getBetAmount() * detailRisk.getOddsValue() / 100000).longValue());
	}

	/**
     * 取消订单
     *
     * @param requestParam 请求参数
     * @return Response
     */
    @Override
    @Deprecated
    public Response rejectOrder(Request<OrderBean> requestParam) {
        return null;
    }
}

