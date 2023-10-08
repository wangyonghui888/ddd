package com.panda.rcs.order.mq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.RandomUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.rcs.order.cache.DangerDataCache;
import com.panda.rcs.order.client.ClientManageService;
import com.panda.rcs.order.entity.constant.BaseConstant;
import com.panda.rcs.order.entity.enums.SubscriptionEnums;
import com.panda.rcs.order.entity.vo.DangerVo;
import com.panda.rcs.order.entity.vo.OrderBeanVo;
import com.panda.rcs.order.entity.vo.OrderBetResponseVO;
import com.panda.rcs.order.entity.vo.OrderItemVo;
import com.panda.rcs.order.entity.vo.PlayInfoVo;
import com.panda.rcs.order.entity.vo.SettleOrder;
import com.panda.rcs.order.utils.BaseUtils;
import com.panda.rcs.order.utils.ClientResponseUtils;
import com.panda.rcs.order.utils.PlayOptionNameUtil;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.cache.local.WebsocketConstants;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.constants.SeriesEnum;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.factory.BeanFactory;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mapper.RcsRectanglePlayMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mongo.I18nBean;
import com.panda.sport.rcs.pojo.RcsMarketCategorySet;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.utils.TwoLevelCacheUtil;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.wrapper.MarketCategorySetService;
import com.panda.sport.rcs.wrapper.StandardSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Description: 实时注单 - 及时注单
 * Topic=WS_CHANNEL_TOPIC WS_ORDER_BET_RECORD_TAG
 * Group=RCS_PUSH_WS_ORDER_BET_RECORD_TAG_GROUP
 * 对应指令 -> 30006
 */
@Slf4j
@Component
@TraceCrossThread
@RocketMQMessageListener(
        topic = "WS_CHANNEL_TOPIC",
        selectorExpression = "WS_ORDER_BET_RECORD_TAG",
        consumerGroup = "RCS_PUSH_WS_ORDER_BET_RECORD_TAG_GROUP",
        messageModel = MessageModel.BROADCASTING,
        consumeMode = ConsumeMode.CONCURRENTLY,
        selectorType = SelectorType.TAG)
public class OrderBetRecordNotifyConsumer implements RocketMQListener<OrderBeanVo>, RocketMQPushConsumerLifecycleListener {

    private static final String AWAY = "away";
    private static final String TEAM_NAME = "teamName:";
    private static final String USER_BEAN = "userBean";
    private static final String MATCH_IP_MAP = "matchIpMap";

    private final static String DANGER_IP_KEY = "rcs:danger:ip:";
    private final static String DANGER_FP_KEY = "rcs:danger:fp:";
    private final static String DANGER_USER_KEY = "rcs:danger:player:group:";

    private static String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    private static Map<String, TUser> userMapCache = new HashMap<String, TUser>(4096);

    private static Map<String, PlayInfoVo> playInfoMap = new ConcurrentHashMap<String, PlayInfoVo>(4096);

    private static final SubscriptionEnums subscriptionEnums = SubscriptionEnums.TIMELY_ORDER;

    @Autowired
    MarketCategorySetService marketCategorySetService;

    @Autowired
    ITOrderDetailService orderDetailService;

    @Autowired
    RcsRectanglePlayMapper rcsRectanglePlayMapper;

    @Autowired
    private TUserMapper userMapper;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TwoLevelCacheUtil twoLevelCacheUtil;

    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    @Autowired
    private StandardSportMarketCategoryService categoryService;

    @Autowired
    private ClientManageService clientManageService;

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.setConsumeThreadMin(160);
        defaultMQPushConsumer.setConsumeThreadMax(320);
    }

    @Override
    public void onMessage(OrderBeanVo msg) {
        if (msg == null) {
            return;
        }
        log.info("::{}::实时注单推送", msg.getOrderNo());
        try {
            TUser user = userMapCache.get(String.valueOf(msg.getUid()));
            if (user == null) {
                user = userMapper.selectByUserId(msg.getUid());
                if (userMapCache.size() >= 3000) {
                    userMapCache.clear();
                }
                userMapCache.put(String.valueOf(msg.getUid()), user);
            }
            HashMap<Object, Object> firstDataMap = new HashMap<>();
            firstDataMap.put(USER_BEAN, user);
            OrderBeanVo orderBean = BeanCopyUtils.copyProperties(msg, OrderBeanVo.class);
            //兼容 提前结算的注单
            if (orderBean.getItemsVo() == null) {
                List<OrderItemVo> itemVos = new ArrayList<>();
                orderBean.getItems().forEach(e -> itemVos.add(BeanCopyUtils.copyProperties(e, OrderItemVo.class)));
                orderBean.setItemsVo(itemVos);
            }
            //赔率转换
            for (Integer i = 0; i < orderBean.getItemsVo().size(); i++) {
                //因上游偶尔发送数据导致位数可能是小数，则不需处理
                if (orderBean.getItemsVo().get(i).getOddsValue() > 1000) {
                    orderBean.getItemsVo().get(i).setOddsValue(new BigDecimal(orderBean.getItemsVo().get(i).getOddsValue()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE)).setScale(2, RoundingMode.DOWN).doubleValue());
                } else {
                    orderBean.getItemsVo().get(i).setOddsValue(orderBean.getItemsVo().get(i).getOddsValue());
                }
            }
            user = (TUser) firstDataMap.get(USER_BEAN);
            Map<String, List<OrderBetResponseVO>> orderLanguageMap = orderBeanMaps(orderBean, user);
            List<OrderBetResponseVO> zsbetResponseVO = orderLanguageMap.get("zs");
            List<OrderBetResponseVO> enbetResponseVO = orderLanguageMap.get("en");
            String msgid = UUID.randomUUID().toString();
            log.info("::{}::实时注单-推送前端消费数据->{}", msg.getOrderNo(), JSONObject.toJSON(zsbetResponseVO));

            Object sendZsMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), zsbetResponseVO, 1, orderBean.getOrderNo(), msgid, null);
            Object sendEnMessage = ClientResponseUtils.createResponseContext(subscriptionEnums.getKey(), enbetResponseVO, 1, orderBean.getOrderNo(), msgid, null);

            //bug：44669
            String orderStatus = zsbetResponseVO.get(0).getOrderStatus().toString();
            String lockWsOrder = "rcs:lock:invalid:order:" + zsbetResponseVO.get(0).getOrderNo();
            log.info("::实时注单-无效订单缓存KEY->{}", lockWsOrder);
            if (orderStatus.equals("-1")) {
                //-1推了后，其他状态都不推送,30s内不用做任何操作(-1不能是第一次推送)
                redisClient.setExpiry(lockWsOrder, "1", 30000L);
            } else {
                String redisStatus = redisClient.get(lockWsOrder);
                if (StringUtils.isNotEmpty(redisStatus) && redisStatus.equals("1")) {
                    return;
                }
            }
            clientManageService.sendMessage(subscriptionEnums, zsbetResponseVO, sendEnMessage, sendZsMessage);
            log.info("::{}::实时注单-推送前端消费数据结束", msg.getOrderNo());
        } catch (Exception e) {
            log.error("::{}::及时注单消费数据->{}，异常信息：", msg.getOrderNo(), msg, e);
        }
    }

    public Map<String, List<OrderBetResponseVO>> orderBeanMaps(OrderBeanVo bean, TUser user) {
        Map<String, List<OrderBetResponseVO>> map = new HashMap<>();
        map.put("zs", new ArrayList<>());
        map.put("en", new ArrayList<>());
        //计算注数
        Integer betCount = getBetCount(bean);
        Double oddsCount = getOddsCount(bean);
        bean.getItemsVo().parallelStream().forEach(e -> {
            Map<String, OrderBetResponseVO> assemblyOrderBetResponseVO = new HashMap<>();
            OrderBetResponseVO getbet = bet(e, bean, user, betCount, oddsCount);
            map.forEach((k, v) -> {
                assemblyOrderBetResponseVO.put(k, BeanCopyUtils.copyProperties(getbet, OrderBetResponseVO.class));
            });
            queryMatchInfo(e, assemblyOrderBetResponseVO);
            map.forEach((k, v) -> {
                v.add(assemblyOrderBetResponseVO.get(k));
            });
        });

        return map;
    }

    private Integer getBetCount(OrderBeanVo bean) {
        Integer num;
        Integer matchCount = bean.getItemsVo().size();
        //单注
        if (bean.getSeriesType() == 1) {
            return 1;
        }
        //串关多注
        else if (bean.getSeriesType().toString().endsWith("001")) {
            num = Integer.valueOf(bean.getSeriesType().toString().replace("001", ""));
            return BaseUtils.combination(matchCount, num);
        } else {
            return SeriesEnum.getSeriesEnumBySeriesJoin(bean.getSeriesType()).getSeriesMax();
        }

    }

    private static ThreadLocal<List<List<Integer>>> oddsGroups = new ThreadLocal<>();

    //总赔率
    private Double getOddsCount(OrderBeanVo bean) {
        //单注
        if (bean.getSeriesType() == 1) {
            return bean.getItems().get(0).getOddsValue();
        }
        oddsGroups.set(Lists.newArrayList());
        Integer matchsize = bean.getItemsVo().size();
        Integer[] matchgourp = new Integer[matchsize];
        for (Integer i = 0; i < matchsize; i++) {
            matchgourp[i] = i + 1;
        }

        Integer num = bean.getSeriesType().toString().indexOf("000") == -1 ? Integer.valueOf(bean.getSeriesType().toString().substring(0, 1)) : 10;
        //多串1
        if (bean.getSeriesType().toString().endsWith("001")) {
            computeOdds(matchgourp, 0, new Integer[num], num, num, matchsize);
        }
        //M串N
        else {
            for (int i = 2; i <= num; i++) {
                computeOdds(matchgourp, 0, new Integer[i], i, i, matchsize);
            }
        }
        BigDecimal oddssum = new BigDecimal(0);
        //循环根据组合进行计算赔率
        for (List<Integer> branchlist : oddsGroups.get()) {
            BigDecimal oddscount = new BigDecimal(1);
            boolean hasNotMultyply = true;
            for (Integer i : branchlist) {
                if (hasNotMultyply) {
                    oddscount = new BigDecimal(bean.getItemsVo().get(i).getOddsValue().toString());
                    hasNotMultyply = false;
                    continue;
                }
                oddscount = oddscount.multiply(new BigDecimal(bean.getItemsVo().get(i).getOddsValue().toString()));
            }
            oddscount = oddscount.setScale(2, BigDecimal.ROUND_DOWN);
            oddssum = oddssum.add(oddscount);
        }
        oddsGroups.remove();
        return oddssum.doubleValue();
    }

    //所有组合
    private void computeOdds(Integer arr[], Integer start, Integer result[], int count, int NUM, int arr_len) {
        for (int i = start; i < arr_len + 1 - count; i++) {
            result[count - 1] = i;
            if (count - 1 == 0) {
                int j;
                List<Integer> list = Lists.newArrayList();
                for (j = NUM - 1; j >= 0; j--) {
                    list.add(arr[result[j]] - 1);
                }
                oddsGroups.get().add(list);
            } else //根据组合数进行递归
            {
                computeOdds(arr, i + 1, result, count - 1, NUM, arr_len);
            }
        }
    }


    private OrderBetResponseVO bet(OrderItemVo e, OrderBeanVo bean, TUser user, Integer betCount, Double oddsCount) {
        OrderBetResponseVO bet = new OrderBetResponseVO();
        SettleOrder settleOrder = e.getSettleOrder();
        if (e.getSettleOrder() != null) {
            bet.setSettleOrder(settleOrder);
        }
        bet.setOddsCount(oddsCount);
        bet.setBetCount(betCount);
        bet.setUid(bean.getUid().toString());
        bet.setIsUpdateOdds(bean.getIsUpdateOdds());
        bet.setOrderNo(e.getOrderNo());
        bet.setBetNo(e.getBetNo());
        bet.setPlayName(e.getPlayName());
        bet.setPlayId(e.getPlayId());
        String playCacheKey = BaseUtils.toStringForParams(Integer.toString(e.getPlayId()), BaseConstant.SEPARATE_UNDERSCORE, Integer.toString(e.getSportId()));
        PlayInfoVo playInfoVo = playInfoMap.get(playCacheKey);
        if (playInfoVo != null) {
            bet.setPlaySetId(playInfoVo.getId());
        } else {
            RcsMarketCategorySet category = marketCategorySetService.findMarketCategoryListByPlayId(e.getPlayId(), e.getSportId().longValue());
            if (category != null) {
                playInfoVo = new PlayInfoVo();
                playInfoVo.setId(category.getId());
                playInfoVo.setName(category.getName());
                playInfoMap.put(playCacheKey, playInfoVo);
                bet.setPlaySetId(category.getId());
            }
        }

        bet.setSportId(e.getSportId());
        bet.setMatchInfo(e.getMatchInfo());
        bet.setTenantId(bean.getTenantId());
        bet.setTournamentId(e.getTournamentId().intValue());
        bet.setMatchId(Long.toString(e.getMatchId()));
        bet.setOddsValue(e.getOddsValue());
        bet.setBetAmount(new BigDecimal(bean.getOrderAmountTotal()).divide(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)).setScale(0, BigDecimal.ROUND_DOWN).toString());
        bet.setProductAmountTotal(new BigDecimal(bean.getProductAmountTotal()).divide(new BigDecimal(BaseConstants.MULTIPLE_BET_AMOUNT_VALUE)).setScale(0, BigDecimal.ROUND_DOWN).toString());
        bet.setCurrencyCode(bean.getCurrencyCode());
        bet.setDeviceType(bean.getDeviceType());
        bet.setBetTime(e.getBetTime());
        bet.setIpArea(bean.getIpArea());
        bet.setIp(bean.getIp());
        bet.setMarketValue(e.getMarketValue());
        bet.setSeriesType(bean.getSeriesType());
        bet.setMaxWinAmount(e.getMaxWinAmount());
        bet.setTradeType(e.getTradeType());
        bet.setOrderStatus(bean.getOrderStatus());
        bet.setVipLevel(bean.getVipLevel());
        if (user != null) {
            bet.setUserFlag(bean.getUserFlag());
            //bet.setLevelId(user.getUserLevel());
            bet.setUsername(user.getUsername());
        } else {
            bet.setUsername("");
            bet.setLevelId(0);
            bet.setUserFlag("正常用户");
        }
        bet.setLevelId(bean.getUserTagLevel());
        bet.setScoreBenchmark(e.getScoreBenchmark());
        bet.setMatchType(e.getMatchType());
        bet.setInfoStatus(bean.getInfoStatus());
        bet.setReason(bean.getReason());
        bet.setModifyTime(e.getModifyTime());
        bet.setSecondaryTag(bean.getSecondaryTag());
        bet.setSecondaryLabelIdsList(bean.getSecondaryLabelIdsList());
        bet.setOtherScore(e.getOtherScore());
        bet.setPauseTime(e.getPauseTime());

        if (StringUtils.isNotEmpty(bean.getIp())) {
            String dangerIpKey = BaseUtils.toStringForParams(DANGER_IP_KEY, bean.getIp());
            String dangerIp = DangerDataCache.getDangerIp(bean.getIp());
            if (StringUtils.isNotEmpty(dangerIp)) {
                bet.setDangerIpMark(1);
            } else {
                dangerIp = redisClient.get(dangerIpKey);
                if (StringUtils.isNotEmpty(dangerIp)) {
                    DangerDataCache.dangerIpMap.put(bean.getIp(), new DangerVo(bean.getIp(), Long.toString(System.currentTimeMillis()), System.currentTimeMillis()));
                    bet.setDangerIpMark(1);
                }
            }
        }

        if (StringUtils.isNotEmpty(bean.getFpId())) {
            String dangerFp = DangerDataCache.getDangerFp(bean.getFpId());
            if (StringUtils.isNotEmpty(dangerFp)) {
                bet.setFpLevel(Integer.parseInt(dangerFp));
            } else {
                dangerFp = redisClient.get(BaseUtils.toStringForParams(DANGER_FP_KEY, bean.getFpId()));
                if (StringUtils.isNotEmpty(dangerFp)) {
                    DangerDataCache.dangerFpMap.put(bean.getFpId(), new DangerVo(bean.getFpId(), dangerFp, System.currentTimeMillis()));
                    bet.setFpLevel(Integer.parseInt(dangerFp));
                }
            }
        }

        String dangerUser = DangerDataCache.getDangerUserGroupMap(Long.toString(bean.getUid()));
        if (StringUtils.isNotEmpty(dangerUser)) {
            bet.setPlayerGroupLevel(Integer.parseInt(dangerUser));
        } else {
            dangerUser = redisClient.get(BaseUtils.toStringForParams(DANGER_USER_KEY, Long.toString(bean.getUid())));
            if (StringUtils.isNotEmpty(dangerUser)) {
                DangerDataCache.dangerUserGroupMap.put(Long.toString(bean.getUid()), new DangerVo(Long.toString(bean.getUid()), dangerUser, System.currentTimeMillis()));
                bet.setPlayerGroupLevel(Integer.parseInt(dangerUser));
            }
        }

        bet.setUserTagLevel(bean.getUserTagLevel());
        bet.setIsPendingOrder(bean.getIsPendingOrder());
        return bet;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Description //获取球队和联赛国际化信息
     * @Param [matchId]
     * @Author Sean
     * @Date 20:18 2020/7/31
     **/
    public Map<String, Object> getMatchMarketTeamVos(Long matchId) {
        Map<String, Object> nameMap = Maps.newHashMap();
        List<Map<String, String>> teams = rcsLanguageInternationMapper.queryTeamNameByMatchId(matchId);
        Map<String, String> tournamentName = rcsLanguageInternationMapper.queryTournamentNameByMatchId(matchId);
        Map<String, String> homeNames = Maps.newHashMap();
        Map<String, String> awayNames = Maps.newHashMap();
        Map<String, String> tournamentNames = Maps.newHashMap();
        Long matchStartTime = System.currentTimeMillis();
        String matchManageId = "0";
        String tournamentId = "0";
        try {
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(teams)) {
                for (Map<String, String> map : teams) {
                    if (BaseConstants.ODD_TYPE_HOME.equalsIgnoreCase(map.get("matchPosition"))) {
                        homeNames = JSONObject.parseObject(map.get("text"), Map.class);
                    } else if (AWAY.equalsIgnoreCase(map.get("matchPosition"))) {
                        awayNames = JSONObject.parseObject(map.get("text"), Map.class);
                    }
                }
                matchStartTime = com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotEmpty(teams.get(0).get("beginTime")) ? Long.parseLong(teams.get(0).get("beginTime")) : System.currentTimeMillis();
                matchManageId = teams.get(0).get("matchManageId");
            }
            if (!ObjectUtils.isEmpty(tournamentName)) {
                tournamentNames = JSONObject.parseObject(tournamentName.get("text"), Map.class);
                tournamentId = tournamentName.get("tournamentId");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        nameMap.put("beginTime", matchStartTime);
        nameMap.put("homeName", homeNames);
        nameMap.put("awayName", awayNames);
        nameMap.put("matchManageId", matchManageId);
        nameMap.put("tournamentNames", tournamentNames);
        nameMap.put("tournamentId", tournamentId);
        return nameMap;
    }

    public Map<String, Object> getMatchInfoCache(OrderItem item) {
        Map<String, Object> match = Maps.newHashMap();
        String key = TEAM_NAME + item.getMatchId();
        String valueJson = twoLevelCacheUtil.get(key, key1 -> {
            Map<String, Object> match1 = Maps.newHashMap();
            match1 = getMatchMarketTeamVos(item.getMatchId());
            if (null != match1) {
                return JsonFormatUtils.toJson(match1);
            } else {
                return "";
            }
        });
        match = JSONObject.parseObject(valueJson);
        return match;
    }

    private void queryMatchInfo(OrderItem e, Map<String, OrderBetResponseVO> bet) {
        log.info("::{}::订单信息->{}", e.getOrderNo(), JSONObject.toJSON(e));
        if (e.getMatchType() != null && e.getMatchType() == 3) {
            //冠军赛事
            List<I18nItemVo> championMatchName = categoryService.championMatchNameAllLanguage(e.getMatchId());
            championMatchName.forEach(v -> {
                if (bet.containsKey(v.getLanguageType())) {
                    bet.get(v.getLanguageType()).setMatchInfo(v.getText());
                }
            });
            List<I18nItemVo> tournmentName = categoryService.getTournmentName(e.getTournamentId());
            tournmentName.forEach(v -> {
                if (bet.containsKey(v.getLanguageType())) {
                    bet.get(v.getLanguageType()).setTournamentName(v.getText());
                }
            });
            if (!bet.containsKey("zs")) {
                List<I18nItemVo> playNames = categoryService.queryChampionPlayName(e.getMarketId());
                playNames.forEach(v -> {
                    if (bet.containsKey(v.getLanguageType())) {
                        bet.get(v.getLanguageType()).setPlayName(v.getText());
                    }
                });
            }
        } else {
            Map<String, Object> map = getMatchInfoCache(e);
            if (!ObjectUtils.isEmpty(map.get("tournamentNames"))) {
                Map<String, String> m = (Map<String, String>) map.get("tournamentNames");
                bet.forEach((k, v) -> {
                    bet.get(k).setTournamentName(m.containsKey(k) ? m.get(k) : "");
                });
            } else {
                bet.forEach((k, v) -> {
                    bet.get(k).setTournamentName("");
                });
            }
            bet.forEach((k, v) -> {
                bet.get(k).setMatchStartTime(Long.valueOf(ObjectUtils.isEmpty(map.get("beginTime")) ? "0" : map.get("beginTime").toString()));
            });
            if (map.get("matchManageId") != null) {
                bet.forEach((k, v) -> {
                    bet.get(k).setMatchManageId(String.valueOf(map.get("matchManageId")));
                });
            } else {
                bet.forEach((k, v) -> {
                    bet.get(k).setMatchManageId("");
                });
            }
            JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(map));
            JSONObject awayJson = json.getJSONObject("awayName");
            JSONObject homeJson = json.getJSONObject("homeName");
            bet.forEach((k, v) -> {
                bet.get(k).setAwayTeam(awayJson.containsKey(k) ? awayJson.getString(k) : "");
                bet.get(k).setHomeTeam(homeJson.containsKey(k) ? homeJson.getString(k) : "");

            });
            if (bet.containsKey("en")) {
                bet.get("en").setMatchInfo(homeJson.getString("en") + " v " + awayJson.getString("en"));
                String playName = categoryService.getPlayName(e.getSportId(), e.getPlayId(), "en");
                StandardSportMarket market = null;
                if (PlayOptionNameUtil.XPLAY.contains(e.getPlayId())) {
                    market = categoryService.queryCacheMarket(e.getMarketId());
                }
                log.info("::{}::获取到en赛事信息->{}", e.getOrderNo(), JSONObject.toJSON(market));
                playName = PlayOptionNameUtil.assemblyPlayName(homeJson.getString("en"), awayJson.getString("en"), playName, e, market);
                bet.get("en").setPlayName(playName);
            }
        }
        bet.forEach((k, v) -> {
            bet.get(k).setPlayOptionsName(getPlayOptionsName(e, k));
            bet.get(k).setOptionMarket(e.getPlayOptionsName());
            if (e.getPlayName() != null && (e.getPlayName().contains("让") || e.getPlayName().contains("Handicap"))) {
                String marketValue = e.getMarketValue();
                if ("1".equals(e.getPlayOptions()) && marketValue.contains("-")) {
                    bet.get(k).setOptionMarket(marketValue);
                }
                if ("2".equals(e.getPlayOptions()) && !marketValue.contains("-")) {
                    bet.get(k).setOptionMarket("-" + marketValue);
                }
            }
        });
        bet.forEach((k, v) -> {
            if (v.getIsUpdateOdds() != null && v.getIsUpdateOdds()) {
                Long waitTime = (v.getModifyTime() - v.getBetTime()) / 1000;
                v.setWaitTime(waitTime.intValue());
            }
        });
    }

    public String getPlayOptionsName(OrderItem item, String languageType) {
        Integer playId = item.getPlayId();
        String mapKey = item.getMarketId() + "-" + item.getPlayOptions() + "-" + languageType;
        // 冠军玩法
        if (item.getMatchType() == 3) {
            if ("zs".equals(languageType)) {
                return item.getPlayOptionsName();
            } else {
                return categoryService.queryChampionOptionValue(item.getPlayOptionsId(), languageType);
            }
        }

        Map<String, Object> map = getMatchInfoCache(item);

        if (MapUtils.isEmpty(map)) {
            WebsocketConstants.MARKETOPTIONNAME_CACHE.invalidate(mapKey);
        }

        String optionValue = "";
        I18nBean i18nBean = null;
        //球员玩法 足球  35L, 36L, 148L, 150L, 151L, 152L 篮球 220L, 221L,271L,272L
        log.info("::{}::实时注单-投注项名称处理->{}", item.getOrderNo(), JSONObject.toJSON(item));
        if (Arrays.asList(35, 36, 148, 150, 151, 152, 363, 364, 365, 366).contains(playId)) {
            String oddsType = item.getPlayOptions();
            if (OddsTypeEnum.OTHER.equalsIgnoreCase(oddsType)) {
                i18nBean = BeanFactory.getOtherI18n();
            } else if (OddsTypeEnum.NONE.equalsIgnoreCase(oddsType)) {
                i18nBean = BeanFactory.getNoneI18n();
            } else if (OddsTypeEnum.OWN_GOAL.equalsIgnoreCase(oddsType)) {
                i18nBean = BeanFactory.getOwnGoalI18n();
            } else {
                optionValue = WebsocketConstants.MARKETOPTIONNAME_CACHE.get(mapKey, key ->
                        orderDetailService.queryPlayerOptionValue(item.getPlayOptionsId(), languageType));
                if (StringUtils.isBlank(optionValue)) {
                    optionValue = item.getPlayOptionsName();
                }
            }
            if (i18nBean != null) {
                optionValue = "zs".equals(languageType) ? i18nBean.getZs() : i18nBean.getEn();
            }
        } else if (Arrays.asList(220, 221, 271, 272).contains(playId)) {
            if ("zs".equals(languageType)) {
                return item.getPlayOptionsName();
            }
            optionValue = WebsocketConstants.MARKETOPTIONNAME_CACHE.get(mapKey, key -> {
                String player = orderDetailService.queryMarketPlayer(item.getMarketId(), languageType);
                String oddsName = orderDetailService.queryOptionValue(item, languageType);
                return player + "-" + oddsName;
            });
        } else if (Arrays.asList(340, 359, 383).contains(playId)) {
            StandardSportMarketOdds odds = categoryService.queryCacheMarketOdds(item.getPlayOptionsId());
            if (odds.getOddsType().contains(OddsTypeEnum.DRAW0)) {
                return languageType.equals("zs") ? "无进球" : "None";
            }
            if (odds.getOddsType().contains(OddsTypeEnum.DRAW1)) {
                return languageType.equals("zs") ? "平局且进球" : "Draw";
            }
        } else if (Arrays.asList(337, 339).contains(playId)) {
            if ("zs".equals(languageType)) {
                return item.getPlayOptionsName();
            }
            StandardSportMarketOdds odds = categoryService.queryCacheMarketOdds(item.getPlayOptionsId());
            return categoryService.specialOddsName(playId, languageType, odds);
        } else {
            optionValue = WebsocketConstants.MARKETOPTIONNAME_CACHE.get(mapKey, key -> orderDetailService.queryOptionValue(item, languageType));
        }
        if (Arrays.asList(367).contains(playId) && "zs".equals(languageType)) {
            if (optionValue != null && optionValue.contains("Others")) {
                optionValue = optionValue.replace("Others", "其他");
            }
        }
        if (Arrays.asList(371, 372).contains(playId)) {
            optionValue = item.getPlayOptionsName();
        }
        Pattern p = Pattern.compile(regEx);
        return PlayOptionNameUtil.assemblyMarketValue(map, item, optionValue, languageType);
    }
}
