package com.panda.sport.rcs.virtual.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import com.panda.sport.data.rcs.dto.virtual.*;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.pojo.statistics.RcsTotalValueNearlyOneTime;
import com.panda.sport.rcs.pojo.virtual.RcsVirtualUser;
import com.panda.sport.rcs.service.*;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.panda.sport.rcs.virtual.cache.RcsCacheContant;
import com.panda.sport.rcs.virtual.constants.Constants;
import com.panda.sport.rcs.virtual.third.client.ApiException;
import com.panda.sport.rcs.virtual.third.client.CONtext;
import com.panda.sport.rcs.virtual.third.client.api.EntityApi;
import com.panda.sport.rcs.virtual.third.client.api.TicketApi;
import com.panda.sport.rcs.virtual.third.client.api.WalletApi;
import com.panda.sport.rcs.virtual.third.client.model.*;
import com.panda.sport.rcs.virtual.utils.CopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 虚拟赛事逻辑实现类
 * @Author lithan
 * @Date 2020-12-22 14:38:26
 **/
@Slf4j
@Service
public class VirtualServiceImpl {

    @Autowired
    IRcsTournamentTemplateService rcsTournamentTemplateService;
    @Autowired
    IRcsVirtualUserService virtualUserService;
    @Autowired
    IRcsVirtualOrderExtService virtualOrderService;
    @Autowired
    IRcsOrderVirtualService orderVirtualService;
    @Autowired
    IRcsOrderVirtualDetailService orderVirtualDetailService;
    @Autowired
    VirtualDataServiceImpl dataService;
    @Autowired
    RedisClient redisClient;

    @Autowired
    IRcsOperateMerchantsSetService merchantsSetService;


    @Value("${rcs.virtual.currency:RMB}")
    private String currency;


    @Value("${rcs.virtual.authId}")
    private Integer authId;

    @Value("${rcs.virtual.environment:0}")
    private String environment;


    private EntityApi entityApi = new EntityApi();

    private WalletApi walletApi = new WalletApi();

//    private EventBlockApi eventApi = null;

    private TicketApi ticketApi = new TicketApi();


    /**
     * 获取投注限额
     *
     * @param reqVo
     * @return
     */
    public List<BetAmountLimitResVo> getBetAmountLimit(BetAmountLimitReqVo reqVo) {
        String linkId = reqVo.getTenantId()+""+reqVo.getUserId();
        log.info("::{}::获取投注限额开始:{}", linkId,JSONObject.toJSONString(reqVo));
        List<BetAmountLimitResVo> list = new ArrayList<>();
        try {
            RcsVirtualUser userEntity = getUserInfo(reqVo.getUserId(), reqVo.getTenantId());
            //获取最大最小值
            CalculationContext context = entityApi.getCalculationContextById(userEntity.getCalculationId());
            //List<SettingValue> settingValues  = entityApi.getContext(Arrays.asList(userEntity.getId().intValue()), "TicketContext");
            List<TicketCurrencySetting> ticketList = context.getTicketContext().getCurrencySetting().stream().filter(bean -> currency.equals(bean.getKey())).collect(Collectors.toList());

            LimitsCurrencySetting limitsCurrencySetting = ticketList.get(0).getLimits();
            if (reqVo.getSeriesType() == 1) {
                LimitSettings settings = limitsCurrencySetting.getSingle();
                BetAmountLimitResVo resVo = CopyUtils.clone(settings, BetAmountLimitResVo.class);
                resVo.setSeriesType(reqVo.getSeriesType());
                list.add(resVo);
                //调用VR虚拟商户可投额度查询接口，和PM派彩可用额度比较，谁小用谁 gulang
                return list;
            } else {
                int type = SeriesTypeUtils.getSeriesType(reqVo.getSeriesType());
                BetAmountLimitResVo resVo = null;
                for (int i = 2; i <= type; i++) {
                    Method method = limitsCurrencySetting.getClass().getMethod("getCombi" + type, null);
                    LimitsBonusSetting settings = (LimitsBonusSetting) method.invoke(limitsCurrencySetting, null);
                    resVo = CopyUtils.clone(settings, BetAmountLimitResVo.class);
                    resVo.setSeriesType(i * 1000 + 1);
                    calc(resVo, type, i);
                    list.add(resVo);
                }
                if (type > 2) {
                    BetAmountLimitResVo lastResVo = CopyUtils.clone(resVo, BetAmountLimitResVo.class);
                    Integer count = SeriesTypeUtils.getCount(reqVo.getSeriesType(), type);
                    lastResVo.setMinStake(lastResVo.getMinStake() * count);
                    lastResVo.setMaxStake(lastResVo.getMaxStake() * count);
                    lastResVo.setSeriesType(reqVo.getSeriesType());
                    list.add(lastResVo);
                }
                return list;
            }
        } catch (Exception e) {
            log.error("::{}::获取投注限额异常:{}",linkId, e.getMessage(), e);
            throw new RcsServiceException("获取投注限额异常:" + e.getMessage());
        }
    }

    /**
     * 获取第三方最大限额
     */

//    private Long getThirdMaxBet(List<ExtendBean> extendBeanList, OrderBean orderBean, String userId, String thirdDataSource) {
//        Request<TicketDto> request = new Request<>();
//        TicketDto ticketDto = parameterConversion(extendBeanList, orderBean);
//        request.setGlobalId(MDC.get("X-B3-TraceId"));
//        //设定限额响应的UID
//        ticketDto.setId(request.getGlobalId());
//        request.setData((ticketDto));
//        Long maxCount = 0L;
//        //1.获取third单注限额
//        ResponseReoffer reoffer = new ResponseReoffer();
//        try {
//            Response<TicketStateVo> response = oddinApiService.getMaxBetAmount(request);
//            System.out.println(response.getData());
//            TicketStateVo ticketStateVo = response.getData();
//            if (MapUtils.isNotEmpty(ticketStateVo.getBet_info())) {
//                for (Map.Entry<String, TicketResponseBetInfo> entry : ticketStateVo.getBet_info().entrySet()) {
//                    reoffer.setStake(entry.getValue().getReoffer().getStake());
//                    float floatValue = reoffer.getStake();
//                    maxCount = (long) floatValue;
//                }
//                return maxCount;
//            }
//        } catch (Exception e) {
//            logger.error("::{}::额度查询-获取{}单注最大限额异常:{}", e.getMessage(), thirdDataSource, e.getStackTrace());
//        }
//        return 2000L;
//
//    }

    /**
     *
     * 串关金额计算
     * @param resVo
     * @param count 一共多少选项   大
     * @param group 串其中几个     小
     */
    private void calc(BetAmountLimitResVo resVo, Integer count, Integer group) {
        int num = combination(count, group);
        resVo.setMinStake(resVo.getMinStake() * num);
        resVo.setMaxStake(resVo.getMaxStake() * num);
    }
    /**
     * 获取投注限额
     *
     * @param reqVo
     * @return
     */
    public BetResVo bet(BetReqVo reqVo) {
        BetResVo resVo = null;
        String linkId = reqVo.getOrderNo();
        log.info("::{}::投注开始:{}", linkId,JSONObject.toJSONString(reqVo));
        //校验参数
        checkParam(reqVo);
        //由于隔离环境用的是生产下发的数据 只用生产环境  这里做个兼容配置 可由nacos控制  商户rcs_operate_merchants_set表的virtual_parent_id必须为2912
        if (StringUtils.isNotBlank(environment) && environment.equals("1")) {
            String maxStr = redisClient.get("rcs:virtual:test:maxAmount");
            if (StringUtils.isBlank(maxStr)) {
                maxStr = "1000";
            }
            long maxAmount = Long.valueOf(maxStr);
            if (reqVo.getTotalStake() > maxAmount) {
                throw new RcsServiceException("摩托车测试属于特殊情况,金额请不要超过10块");
            }
            String tenantIdStr = redisClient.get("rcs:virtual:test:maxStrStr");
            if (StringUtils.isBlank(tenantIdStr)) {
                tenantIdStr = "2";
            }
            if (reqVo.getTenantId().compareTo(Long.valueOf(tenantIdStr)) != 0) {
                throw new RcsServiceException("非法商户");
            }
        }
        try {
            //获取/初始化  第三方用户相关信息
            RcsVirtualUser userEntity = getUserInfo(reqVo.getUserId(), reqVo.getTenantId());
            //构建ticket
            SellTicketExternal ticket = buildTicket(reqVo, userEntity);
            /**保存第三方投注记录*/
            dataService.saveThirdOrder(reqVo, userEntity.getVirtualUserId(), JSONObject.toJSONString(ticket));
            //投注
            log.info("::{}::投注参数:{}", linkId,JSONObject.toJSONString(ticket));
            TicketTransaction ticketRes = ticketApi.ticketCreate(ticket);
            log.info("::{}::投注结果:{}", linkId,JSONObject.toJSONString(ticketRes));

            //更新成功状态
            dataService.updateThirdOrder(reqVo.getOrderNo(), ticketRes);
            resVo = new BetResVo(reqVo.getOrderNo(), 1);
        } catch (ApiException e) {
            log.error("::{}::投注异常:{}",linkId, e.getResponseBody(), e);
            JSONObject jsonObject = JSONObject.parseObject(e.getResponseBody());
            String message = e.getMessage();
            if (jsonObject != null) {
                message = jsonObject.getString("message");
            }
            dataService.updateThirdOrder(Lists.newArrayList(reqVo.getOrderNo()), message, 2, null);
            throw new RcsServiceException(message);
        } catch (Exception e) {
            log.error("::{}::投注异常:{}",linkId,e.getMessage(), e);
            dataService.updateThirdOrder(Lists.newArrayList(reqVo.getOrderNo()), "投注异常:" + e.getMessage(), 2, null);
            throw new RcsServiceException("投注异常:" + e.getMessage());
        }
        return resVo;
    }

    /**
     * 构建投注参数
     *
     * @param reqVo
     * @param userEntity
     * @return
     */
    private SellTicketExternal buildTicket(BetReqVo reqVo, RcsVirtualUser userEntity) throws Exception {
        //构建下注对象 参数处理
        SellTicketExternal ticket = new SellTicketExternal(userEntity.getCalculationId(), userEntity.getVirtualExtId(), userEntity.getVirtualParentId());
        TicketDetail ticketDetail = new TicketDetail();
        long singleStake = 0L;
        long totalStake = reqVo.getTotalStake();
        totalStake = dataService.getExchangeAmount(totalStake, reqVo.getTenantId());
        //最大赔付金额参数
        BigDecimal maxWinning = new BigDecimal(totalStake).divide(new BigDecimal("100.0"));
        //组装每个投注项
        List<TicketEvent> eventList = new ArrayList<>();
        List<BetItemReqVo> list = reqVo.getOrderItemList();
        for (BetItemReqVo item : list) {
            long stake = item.getStake();
            stake = dataService.getExchangeAmount(stake, reqVo.getTenantId());
            TicketEvent event = new TicketEvent();
            event.setPlaylistId(item.getPlayListId().intValue());
            event.setEventId(item.getEventId());
            TicketBet ticketBet = new TicketBet(item.getMarketId(), item.getOddId(), item.getOddValue(), Double.valueOf(stake) / 100.0);
            event.addBetsItem(ticketBet);
            eventList.add(event);
            maxWinning = maxWinning.multiply(new BigDecimal(item.getOddValue()));
            singleStake = stake;
        }
        ticketDetail.setEvents(eventList);

        //设置串关方式***********
        List<SystemBet> systemBetList = new ArrayList<>();

        //获取M串N中的M
        Integer type = 1;
        Integer count = 1;
        if (reqVo.getSeriesType() != 1) {
            type = SeriesTypeUtils.getSeriesType(reqVo.getSeriesType());
            count = SeriesTypeUtils.getCount(reqVo.getSeriesType(), type);
        }
        if (count == 1) {
            SystemBet systemBet = new SystemBet();
            systemBet.setSystemCount(eventList.size());
            //举例: 3串1中的3
            systemBet.setGrouping(type);
            //排列组合一共多少种
            int combinationNum = combination(eventList.size(), type);
            systemBet.setStake(combinationNum * singleStake / 100.0);

            WinningData winningData = new WinningData();
            winningData.setLimitMaxPayout(maxWinning.doubleValue());
            winningData.setMinBonus(0D);
            winningData.setMaxBonus(0D);
            winningData.setMinWinning(maxWinning.doubleValue());
            winningData.setMaxWinning(maxWinning.doubleValue());
            systemBet.setWinningData(winningData);

            systemBetList.add(systemBet);
        } else {
            for (int i = 2; i <= type; i++) {
                SystemBet systemBet = new SystemBet();
                systemBet.setSystemCount(eventList.size());
                systemBet.setGrouping(i);
                //排列组合一共多少种
                int combinationNum = combination(eventList.size(), i);
                systemBet.setStake(singleStake * combinationNum / 100.0);

                WinningData winningData = new WinningData();
                winningData.setLimitMaxPayout(maxWinning.doubleValue());
                winningData.setMinBonus(0D);
                winningData.setMaxBonus(0D);
                winningData.setMinWinning(maxWinning.doubleValue());
                winningData.setMaxWinning(maxWinning.doubleValue());
                systemBet.setWinningData(winningData);

                systemBetList.add(systemBet);
            }
        }

        ticketDetail.setSystemBets(systemBetList);
        ticket.setDetails(ticketDetail);
        return ticket;
    }

    /**
     * 初始化用户 钱包等
     *
     * @param userId
     */
    private RcsVirtualUser getUserInfo(Long userId, Long tenantId) {

        String extId = userId.toString();
        try {
            //缓存没查到则从数据库查询
            RcsVirtualUser dbUser = RcsCacheContant.VIRTUAL_CACHE.get(userId, id -> {
                LambdaQueryWrapper<RcsVirtualUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RcsVirtualUser::getUserId, userId);
                return virtualUserService.getOne(wrapper);
            });

            if (dbUser != null) {
                String calculationIdStr = redisClient.get("rcs:virtual:user:calculationId:" + extId);
                Integer calculationId = 0;
                if (calculationIdStr != null) {
                     calculationId = Integer.valueOf(calculationIdStr);
                }else{
                    calculationId = getUserCalculationId(dbUser.getVirtualUserId(), extId);
                }
                dbUser.setCalculationId(calculationId);
                return dbUser;
            }

            //如果没有创建  则调用接口
            Entity entityUser = getThirdUser(userId, extId, tenantId);
            Wallet wallet = createWallet(entityUser.getId(), entityUser.getExtId());
            Integer calculationId = getUserCalculationId(entityUser.getId(), extId);
            //入库
            RcsVirtualUser rcsVirtualUser = new RcsVirtualUser();
            rcsVirtualUser.setCalculationId(calculationId);
            rcsVirtualUser.setCreateTime(System.currentTimeMillis());
            rcsVirtualUser.setPandaStatus(1);
            rcsVirtualUser.setRemark(JSONObject.toJSONString(entityUser));
            rcsVirtualUser.setUserId(userId);
            rcsVirtualUser.setVirtualExtId(entityUser.getExtId());
            rcsVirtualUser.setVirtualParentId(entityUser.getParentId());
            rcsVirtualUser.setVirtualUserId(entityUser.getId());
            rcsVirtualUser.setVirtualUserName(entityUser.getName());
            rcsVirtualUser.setWalletId(wallet.getId());
            rcsVirtualUser.setVirtualUserStatus(entityUser.getStatus().getValue());
            virtualUserService.save(rcsVirtualUser);
            log.info("首次处理用户信息:" + JSONObject.toJSONString(rcsVirtualUser));
            return rcsVirtualUser;
        } catch (Exception e) {
            log.info("用户信息处理失败:,{},{}", e.getMessage(), e);
            throw new RcsServiceException("用户信息处理失败:" + e.getMessage());
        }

    }

    /**
     * 获取用户信息
     *
     * @param userId panda系统的用户ID
     * @return
     */
    public Entity getThirdUser(Long userId, String extId, Long tenantId) {
        Entity entity = null;
        try {
            int entityParentId = getEntityParentId(tenantId);
            entity = entityApi.entityAdd(entityParentId, userId.toString(), "ENABLED", extId, "", true, Arrays.asList("External"));
            log.info("获取用户信息成功:{}", entity);
        } catch (ApiException e) {
            log.info("投注异常:{},{}", e.getResponseBody(), e);
            JSONObject jsonObject = JSONObject.parseObject(e.getResponseBody());
            String message = e.getMessage();
            if (jsonObject != null) {
                message = jsonObject.getString("message");
            }
            throw new RcsServiceException(message);
        } catch (Exception e) {
            log.info("获取用户信息失败,{},{}", e.getMessage(), e);
            throw new RcsServiceException("获取用户信息失败:"+e.getMessage());
        }
        return entity;
    }

    /**
     * 获取用户的calculationId
     *
     * @return
     */
    public Integer getUserCalculationId(Integer entityId, String extId) {
        Integer calculationId = 0;
        try {
            calculationId = entityApi.getCalculationIdByEntityId(entityId, extId);
            redisClient.setExpiry("rcs:virtual:user:calculationId:" + extId, calculationId.toString(), 1 * 60L);
            log.info("获取用户calculationId成功:{}:{}",extId, calculationId);
        }catch (ApiException e) {
            JSONObject jsonObject = JSONObject.parseObject(e.getResponseBody());
            String message = e.getMessage();
            if (jsonObject != null) {
                message = jsonObject.getString("message");
            }
            log.info("获取用户calculationId异常:{},{}", e.getResponseBody(), e);
            throw new RcsServiceException("获取用户calculationId失败" + message);
        } catch (Exception e) {
            log.info("获取用户calculationId失败,{},{}", e.getMessage(), e);
            throw new RcsServiceException("获取用户calculationId失败");
        }
        return calculationId;
    }

    /**
     * 创建钱包
     *
     * @return
     */
    public Wallet createWallet(Integer entityId, String extId) {
        try {
            //查看是否创建钱包
            List<Wallet> walletList = walletApi.walletFindAllByEntityId(entityId, 1, 0, "DESC", null);
            //没有就创建钱包
            if (ObjectUtils.isEmpty(walletList)) {
                //设置币种
                List<Integer> entitiesId = Arrays.asList(new Integer[]{entityId});
                LocalizationContext contexts = new LocalizationContext().defaultCurrency(currency);
                CONtext coNtext = new CONtext(contexts);
                entityApi.setContext(entitiesId, coNtext);
                //创建钱包
                Wallet wallet = walletApi.walletCreate(entityId, currency, extId, null, null, null, null, null, null, null);
                log.info("用户{}首次创建钱包", entityId);
                return wallet;
            }
            return walletList.get(0);
        } catch (ApiException e) {
            log.info("创建钱包失败:{},{}", e.getResponseBody(), e);
            JSONObject jsonObject = JSONObject.parseObject(e.getResponseBody());
            String message = e.getMessage();
            if (jsonObject != null) {
                message = jsonObject.getString("message");
            }
            throw new RcsServiceException(message);
        }catch (Exception e) {
            log.info("创建钱包失败,{},{}", e.getMessage(), e);
            throw new RcsServiceException("创建钱包失败");
        }
    }

    /**
     * 参数校验
     *
     * @param reqVo
     */
    private void checkParam(BetReqVo reqVo) {
        if (reqVo.getOrderNo() == null) {
            throw new RcsServiceException("orderNo参数异常");
        }
        if (reqVo.getTotalStake() == null) {
            throw new RcsServiceException("totalStake参数异常");
        }
        if (reqVo.getUserId() == null) {
            throw new RcsServiceException("userId参数异常");
        }
        for (BetItemReqVo item : reqVo.getOrderItemList()) {
            if (item.getBetNo() == null) {
                throw new RcsServiceException("betNo参数异常");
            }
            if (item.getOddValue() == null) {
                throw new RcsServiceException("oddValue参数异常");
            }
            if (item.getEventId() == null) {
                throw new RcsServiceException("eventId参数异常");
            }
            if (item.getMarketId() == null) {
                throw new RcsServiceException("marketId参数异常");
            }
            if (item.getOddId() == null) {
                throw new RcsServiceException("oddId参数异常");
            }
            if (item.getPlayListId() == null) {
                throw new RcsServiceException("playListId参数异常");
            }
            if (item.getStake() == null) {
                throw new RcsServiceException("stake参数异常");
            }
            if (item.getSportId() == null) {
                throw new RcsServiceException("sportId参数异常");
            }
        }
    }

    /**
     * 取消订单
     *
     * @param ticketIds
     * @return
     */
    public Map<String, Object> ticketCancel(List<Long> ticketIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("message", "取消失败");
        try {
            List<ServerTicket> response = ticketApi.ticketCancel(ticketIds);
            log.info("取消订单返回结果:{}", JSONObject.toJSONString(response));
            if (response.get(0).getStatus().getValue().equals("CANCELLED")) {
                map.put("code", 1);
                map.put("message", "取消成功");
            }
        } catch (ApiException e) {
            log.info("取消订单失败,{},{}", e.getMessage(), e);
        }
        return map;
    }

    private Integer getEntityParentId(Long busId) throws Exception {
        //缓存没查到则从数据库查询
        RcsOperateMerchantsSet merchants = RcsCacheContant.RCS_OPERATE_MERCHANTS_SET_CACHE.get(busId, id -> {
            LambdaQueryWrapper<RcsOperateMerchantsSet> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsOperateMerchantsSet::getMerchantsId, busId.toString());
            return merchantsSetService.getOne(wrapper);
        });
        if (merchants == null) {
            throw new RcsServiceException("商户配置错误");
        }
        if (merchants.getVirtualParentId() != null) {
            return merchants.getVirtualParentId();
        } else {
            try {
                log.info("商户首次创建代理ID:{}", merchants.getMerchantsId());
                String entityUserName = "merchant_" + busId;
                Entity entity = entityApi.entityAdd(authId, entityUserName, "ENABLED", entityUserName, "", true, Arrays.asList("External"));
                log.info("商户首次创建代理成功:{}:{}", busId, entity);
                LambdaUpdateWrapper<RcsOperateMerchantsSet> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(RcsOperateMerchantsSet::getMerchantsId, busId);
                updateWrapper.set(RcsOperateMerchantsSet::getVirtualParentId, entity.getId());
                merchantsSetService.update(updateWrapper);
                log.info("商户首次创建代理记录到数据库:{}:{}", busId, entity);
                RcsCacheContant.RCS_OPERATE_MERCHANTS_SET_CACHE.invalidate(busId);
            } catch (Exception e) {
                log.info("商户创建代理配置错误:{}:{}:{}", busId, e.getMessage(), e);
                throw new RcsServiceException("商户创建代理配置错误");
            }
            return getEntityParentId(busId);
        }
    }

    /**
     * 排列组合计算
     *
     * @param n 大的数
     * @param k 小的数
     * @return
     */
    private static int combination(int n, int k) {
        int a = 1, b = 1;
        if (k > n / 2) {
            k = n - k;
        }
        for (int i = 1; i <= k; i++) {
            a *= (n + 1 - i);
            b *= i;
        }
        return a / b;
    }
}

