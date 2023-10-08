package com.panda.sport.rcs.mts.sportradar.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mts.sportradar.constants.Constants;
import com.panda.sport.rcs.utils.SeriesTypeUtils;
import com.panda.sport.rcs.utils.SpringContextUtils;
import com.sportradar.mts.sdk.api.Bet;
import com.sportradar.mts.sdk.api.Selection;
import com.sportradar.mts.sdk.api.Sender;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.TicketAck;
import com.sportradar.mts.sdk.api.TicketCancel;
import com.sportradar.mts.sdk.api.TicketCancelAck;
import com.sportradar.mts.sdk.api.TicketCancelResponse;
import com.sportradar.mts.sdk.api.TicketCashout;
import com.sportradar.mts.sdk.api.TicketNonSrSettle;
import com.sportradar.mts.sdk.api.TicketResponse;
import com.sportradar.mts.sdk.api.builders.BetBuilder;
import com.sportradar.mts.sdk.api.builders.BuilderFactory;
import com.sportradar.mts.sdk.api.builders.TicketBuilder;
import com.sportradar.mts.sdk.api.enums.OddsChangeType;
import com.sportradar.mts.sdk.api.enums.SenderChannel;
import com.sportradar.mts.sdk.api.enums.StakeType;
import com.sportradar.mts.sdk.api.enums.TicketAcceptance;
import com.sportradar.mts.sdk.api.enums.TicketAckStatus;
import com.sportradar.mts.sdk.api.enums.TicketCancelAckStatus;
import com.sportradar.mts.sdk.api.enums.TicketCancellationReason;
import com.sportradar.mts.sdk.api.impl.builders.BetBuilderImpl;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TicketBuilderHelper {

    private BuilderFactory builderFactory;
    private static String CURRENY = "CNY";

    public static String MTS_AMOUNT_RATE = "rcs:mts:amount_rate:tenantId:%s";
    public static String MTS_AMOUNT_RATE_ALL = "rcs:mts:amount_rate";

    public TicketBuilderHelper(BuilderFactory builderFactory) {
        Preconditions.checkNotNull(builderFactory);
        this.builderFactory = builderFactory;
    }

    public TicketBuilderHelper() {

    }

    public Ticket getTicket() {
    	//hcp=-0.75
        String specifiers = "";
        Map<String, Object> sportEventStatus = new HashMap<>(1);

        return builderFactory.createTicketBuilder()
                .setTicketId(UUID.randomUUID().toString().replace("-", ""))
                .setOddsChange(OddsChangeType.NONE)
                .setSender(builderFactory.createSenderBuilder()
                        .setBookmakerId(Constants.getConfig().getBookmakerId())
                        .setLimitId(Constants.getConfig().getLimitId())
                        .setSenderChannel(SenderChannel.INTERNET)
                        .setCurrency(CURRENY)
                        .setEndCustomer("1.2.3.4", new Random().nextInt(10000) + "", "EN", "device1", 12092L)
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId(System.currentTimeMillis() + "")
                        .addSelectedSystem(1)
                        .setStake(10000, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId("21984779")
                                .setIdUof(3, "sr:sport:1", 1, "1", specifiers, sportEventStatus)
                                .setOdds(15700)
                                .setBanker(false)
                                .build())
                        .build()
                )
                .build();
    }

    public Ticket getTicket(String matchId,int marketType,String sprotId, int playId ,String marketId, String marketValue,int odds,int money) {
        //hcp=-0.75
        String specifiers = "";
        Map<String, Object> sportEventStatus = new HashMap<>(1);

        return builderFactory.createTicketBuilder()
                .setTicketId("test666_"+UUID.randomUUID().toString().replace("-", ""))
                .setOddsChange(OddsChangeType.NONE)
                .setSender(builderFactory.createSenderBuilder()
                        .setBookmakerId(Constants.getConfig().getBookmakerId())
                        .setLimitId(Constants.getConfig().getLimitId())
                        .setSenderChannel(SenderChannel.INTERNET)
                        .setCurrency(CURRENY)
                        .setEndCustomer("1.2.3.4", new Random().nextInt(10000) + "", "EN", "device1", 12092L)
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId(System.currentTimeMillis() + "")
                        .addSelectedSystem(1)
                        .setStake(money, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId(matchId)
                                .setIdUof(marketType, sprotId, playId, marketId, marketValue, sportEventStatus)
                                .setOdds(odds)
                                .setBanker(false)
                                .build())
                        .build()
                )
                .build();
    }

    /**
     * 获取最大最小值，生成ticket
     */
    public Ticket getMaxAmountTicket(ExtendBean order) {
        Integer marketType = convertMarketType(order);
        String specifiers = convertSpecifiers(order);
        String[] thirdTemplateId = order.getThirdTemplateSourceId().split("###");
        thirdTemplateId[1] = convertThridId(thirdTemplateId[1]);

        Map<String, Object> sportEventStatus = new HashMap<>(1);
        String mtsId = order.getItemBean().getOrderNo();
        if (StringUtils.isEmpty(mtsId)) {
            mtsId = UUID.randomUUID().toString();
        }

        return builderFactory.createTicketBuilder()
                .setTicketId(mtsId)
                .setOddsChange(OddsChangeType.ANY)
                .setSender(builderFactory.createSenderBuilder()
                        .setBookmakerId(Constants.getConfig().getBookmakerId())
                        .setLimitId(Constants.getConfig().getLimitId())
                        .setSenderChannel(SenderChannel.INTERNET)
                        .setCurrency(CURRENY)
                        .setEndCustomer("127.0.0.1", order.getUserId(), "EN", "device1", 12092L)
                        .build())
                .addBet(builderFactory.createBetBuilder()
                        .setBetId(mtsId)
                        .addSelectedSystem(1)
                        //查询MaxStake时 目前系统没有传金额参数  接口需要>0的金额参数 目前固定参数1000000L
                        .setStake(10000L, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId(convertEventId(order))
                                .setIdUof(marketType, "sr:sport:" + order.getSportId(), Integer.parseInt(thirdTemplateId[1]), thirdTemplateId[2], specifiers, sportEventStatus)
                                .setOdds(order.getItemBean().getOddsValue().intValue() / 10)
                                .setBanker(false)
                                .build())
                        .build()
                )
                .build();
    }

    /**
     * 判断是否是MTS测试用户
     * true 是
     * flase 否
     * @return
     */
    private Boolean isTestUser(String userId) {
    	RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);

    	String cacheVal = redisClient.get("rcs:mts:test_user");
    	if(StringUtils.isBlank(cacheVal)) return false;

    	if(Arrays.asList(cacheVal.split(",")).contains(userId)) return true;

    	return false;
    }


    /**
     * @param oddsChangeType
     * @return
     * @Description 生成MTS 单关订单ticket
     * @Param
     * @Author max
     * @Date 16:47 2019/12/16
     **/
    public Ticket getTicket(ExtendBean order , Sender sender, OddsChangeType oddsChangeType) {
        Integer marketType = convertMarketType(order);
        String specifiers = convertSpecifiers(order);
        String[] thirdTemplateId = order.getThirdTemplateSourceId().split("###");
        thirdTemplateId[1] = convertThridId(thirdTemplateId[1]);

        Map<String, Object> sportEventStatus = new HashMap<>(1);
        String mtsId = order.getItemBean().getOrderNo();
        if (StringUtils.isEmpty(mtsId)) {
            mtsId = UUID.randomUUID().toString();
        }

        Long mtsAmount = getExchangeAmount(order.getItemBean().getBetAmount() * 100,order.getBusId());
        order.setMtsAmount(mtsAmount);

        return builderFactory.createTicketBuilder()
                .setTicketId(mtsId)
                .setOddsChange(oddsChangeType)
                .setSender(sender)
                .setTestSource(isTestUser(order.getUserId()))
                .addBet(builderFactory.createBetBuilder()
                        .setBetId(mtsId)
                        .addSelectedSystem(1)
                        .setStake(mtsAmount, StakeType.TOTAL)
                        .addSelection(builderFactory.createSelectionBuilder()
                                .setEventId(convertEventId(order))
                                .setIdUof(marketType, "sr:sport:" + order.getSportId(), Integer.parseInt(thirdTemplateId[1]), thirdTemplateId[2], specifiers, sportEventStatus)
                                .setOdds(order.getItemBean().getOriginOdds().intValue() / 10)
                                .setBanker(false)
                                .build())
                        .build()
                )
                .build();
    }
    
    /**
     * "设备类型 1:H5，2：PC,3:Android,4:IOS,5:其他设备"
     * @param deviceType
     * @return
     */
    private SenderChannel getSenderChannel(String deviceType) {
    	if("1".equals(deviceType) || "4".equals(deviceType) || "3".equals(deviceType)) {
    		return SenderChannel.MOBILE;
    	}else if("2".equals(deviceType)) {
    		return SenderChannel.INTERNET;
    	}

    	return SenderChannel.INTERNET;
    }


    public com.sportradar.mts.sdk.api.Sender buildSender(String deviceType , String ip,String userId , String device,Long confidence) {
    	if(ip == null ) ip = "127.0.0.1";
    	if(device == null ) device = "device-null";
    	return builderFactory.createSenderBuilder()
        .setBookmakerId(Constants.getConfig().getBookmakerId())
        .setLimitId(Constants.getConfig().getLimitId())
        .setSenderChannel(getSenderChannel(deviceType))
        .setCurrency(CURRENY)
        .setEndCustomer(ip, userId, "EN", device, confidence)
        .build();
    }

    /**
     * @param oddsChangeType
     * @return
     * @Description 串关 MTS订单效验
     * @Param [order]
     * @Author max
     * @Date 11:02 2019/12/21
     **/
    public Ticket getSeriesTicket(List<ExtendBean> list,String seriesType,String totalMoney , Sender sender, OddsChangeType oddsChangeType) {
        String orderId =list.get(0).getItemBean().getOrderNo();
        log.info("{}MTS串关订单效验参数:{}",orderId, JSONObject.toJSONString(list));
        Map<String, Object> sportEventStatus = new HashMap<>(1);
        BetBuilderImpl betBuilder = new BetBuilderImpl();
        betBuilder.setBetId(list.get(0).getItemBean().getOrderNo());

        //获取M串N中的M
        Integer type = SeriesTypeUtils.getSeriesType(Integer.parseInt(seriesType));
        Integer count = SeriesTypeUtils.getCount(Integer.parseInt(seriesType), type);
        if(count == 1) {
            betBuilder.addSelectedSystem(type);
        }else {
            for(int i = 2 ; i <= type ; i ++) {
                betBuilder.addSelectedSystem(i);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            ExtendBean order = list.get(i);
            Integer marketType = convertMarketType(order);
            String specifiers = convertSpecifiers(order);
            String[] thirdTemplateId = order.getThirdTemplateSourceId().split("###");
            thirdTemplateId[1] = convertThridId(thirdTemplateId[1]);

            Selection selection = builderFactory.createSelectionBuilder()
                    .setEventId(convertEventId(order))
                    .setIdUof(marketType, "sr:sport:" + order.getSportId(), Integer.parseInt(thirdTemplateId[1]), thirdTemplateId[2], specifiers, sportEventStatus)
                    .setOdds(order.getItemBean().getOriginOdds().intValue() / 10)
                    .setBanker(false)
                    .build();

            betBuilder.addSelection(selection);
        }
        //金额都是CNY 先转成元 业务倍数是100 ， mts倍数是1万倍，最终是100倍
        Long mtsAmount = getExchangeAmount(Double.valueOf(totalMoney).longValue() * 100, list.get(0).getBusId());
        betBuilder.setStake(mtsAmount, StakeType.TOTAL);
        Bet bet = betBuilder.build();

        Ticket ticket = builderFactory.createTicketBuilder()
                .setTicketId(list.get(0).getItemBean().getOrderNo())
                .setOddsChange(oddsChangeType)
                .setSender(sender)
                .setTestSource(isTestUser(list.get(0).getUserId()))
                .addBet(bet)
                .build();

//        saveMtsOrder(list.get(0).getItemBean().getOrderNo(), String.valueOf(totalMoney), String.valueOf(mtsAmount));
        list.get(0).setMtsAmount(mtsAmount);

        return ticket;
    }

    /**
     * 串关查询限额
     *
     * @param list
     * @param n    N串M 中的N
     * @param flag false表示计算 n串1    true表示组装 n串m
     *             mtsOddsType 赔率接受类型
     * @return
     */
    public Ticket getSeriesTickets(List<ExtendBean> list, int n, boolean flag) {
        List<Selection> selectionsList = new ArrayList<Selection>();
        //构造selections
        for (int i = 0; i < list.size(); i++) {
            ExtendBean order = list.get(i);
            Integer marketType = convertMarketType(order);
            String specifiers = convertSpecifiers(order);
            String[] thirdTemplateId = order.getThirdTemplateSourceId().split("###");
            thirdTemplateId[1] = convertThridId(thirdTemplateId[1]);
            Selection selection = builderFactory.createSelectionBuilder()
                    .setEventId(convertEventId(order))
                    .setIdUof(marketType, "sr:sport:" + order.getSportId(), Integer.parseInt(thirdTemplateId[1]), thirdTemplateId[2], specifiers, new HashMap<>(list.size()))
                    .setOdds(order.getItemBean().getOddsValue().intValue() / 10)
                    .setBanker(false)
                    .build();
            selectionsList.add(selection);
        }

        //n串m  把多个n串1的bet添加到betList
        BetBuilder betBuilder = builderFactory.createBetBuilder();
        betBuilder.setBetId(UUID.randomUUID().toString().replace("-", ""));
        betBuilder.setStake(getExchangeAmount(10000L, list.get(0).getBusId()), StakeType.TOTAL);
        for (Selection selection : selectionsList) {
            betBuilder.addSelection(selection);
        }
        //如果是N串m
        if (flag) {
            for (int i = 2; i <= n; i++) {
                betBuilder.addSelectedSystem(i);
            }
        } else {//n串1
            betBuilder.addSelectedSystem(n);
        }
        Bet bet = betBuilder.build();
        //构造ticket
        TicketBuilder ticketBuilder = builderFactory.createTicketBuilder();
        ticketBuilder.setTicketId(UUID.randomUUID().toString().replace("-", ""));
        ticketBuilder.setOddsChange(OddsChangeType.ANY);
        ticketBuilder.setSender(builderFactory.createSenderBuilder()
                .setBookmakerId(Constants.getConfig().getBookmakerId())
                .setLimitId(Constants.getConfig().getLimitId())
                .setSenderChannel(SenderChannel.INTERNET)
                .setCurrency(CURRENY)
                .setEndCustomer("127.0.0.1", list.get(0).getUserId(), "EN", "device1", 12092L)
                .build());
        ticketBuilder.addBet(bet);
        Ticket ticket = ticketBuilder.build();
        return ticket;
    }

    private String convertSpecifiers(ExtendBean order) {
        String specifiers = "";
        Map<String, String> specifiersMap = JsonFormatUtils.fromJsonMap(order.getSpecifiers(), String.class, String.class);
        if (specifiersMap != null && specifiersMap.size() > 0) {
            for (Map.Entry<String, String> entry : specifiersMap.entrySet()) {
                specifiers += entry.getKey() + "=" + entry.getValue() + "|";
            }
            if (specifiers.endsWith("|")) {
                specifiers = specifiers.substring(0, specifiers.length() - 1);
            }
        }
        return specifiers;
    }

    private int convertMarketType(ExtendBean order) {
        Integer marketType = 3;
        if (order.getIsScroll().equals("1")) {
            marketType = 1;
        }
        return marketType;
    }

    private String convertEventId(ExtendBean order) {
        //如果是冠军玩法 表字段已经带有前缀 sr:season:
        if (order.getIsChampion() != null && order.getIsChampion() == 1) {
            return order.getThirdMatchSourceId();
        }
        return "sr:match:" + order.getThirdMatchSourceId();
    }


    /**
     * 按照比例给MTS金额 ， 默认是1
     **/
    private Long getExchangeAmount(Long betAmount,String tenantId) {
    	try {
    		RedisClient redisClient = SpringContextUtils.getBeanByClass(RedisClient.class);
    		String val = redisClient.get(String.format(MTS_AMOUNT_RATE, tenantId));
            log.info("商户比例信息:{}比例{}", tenantId, val);
        	if(StringUtils.isBlank(val)){
                val = redisClient.get(MTS_AMOUNT_RATE_ALL);
            }
        	if(StringUtils.isBlank(val)){
                val = "1";
            }
        	
        	return new BigDecimal(String.valueOf(betAmount)).multiply(new BigDecimal(val)).longValue();
    	}catch (Exception e) {
    		log.error(e.getMessage(),e);
    	}
    	
        return betAmount.longValue();
    }

    public TicketAck getTicketAck(TicketResponse ticketResponse) {
        return builderFactory.createTicketAckBuilder()
                .setTicketId(ticketResponse.getTicketId())
                .setBookmakerId(Constants.getConfig().getBookmakerId())
                .setAckStatus(ticketResponse.getStatus() == TicketAcceptance.ACCEPTED ? TicketAckStatus.ACCEPTED : TicketAckStatus.REJECTED)
                .setSourceCode(ticketResponse.getReason().getCode())
                .build();
    }

    public TicketCancelAck getTicketCancelAck(TicketCancelResponse ticketCancelResponse) {
        return builderFactory.createTicketCancelAckBuilder()
                .setTicketId(ticketCancelResponse.getTicketId())
                .setBookmakerId(Constants.getConfig().getBookmakerId())
                .setAckStatus(ticketCancelResponse.getReason().getCode() == 1024 ? TicketCancelAckStatus.CANCELLED : TicketCancelAckStatus.NOT_CANCELLED)
                .setSourceCode(ticketCancelResponse.getReason().getCode())
                .build();
    }

    public TicketCancel getTicketCancel(String ticketId) {
        return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.getConfig().getBookmakerId(), TicketCancellationReason.BookmakerBackofficeTriggered);
    }

    /*101 普通取消
    102 超时取消 mts没回调
    103 后台取消
    104 技术问题取消
    105 后台异常取消
    106 现金返还促销*/
    public TicketCancel getTicketCancel(String ticketId, String code) {
        if ("101".equals(code)) {
            return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.getConfig().getBookmakerId(), TicketCancellationReason.CustomerTriggeredPrematch);
        } else if ("103".equals(code)) {
            return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.getConfig().getBookmakerId(), TicketCancellationReason.BookmakerBackofficeTriggered);
        } else if ("104".equals(code)) {
            return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.getConfig().getBookmakerId(), TicketCancellationReason.BookmakerTechnicalIssue);
        }else if ("105".equals(code)) {
            return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.getConfig().getBookmakerId(), TicketCancellationReason.ExceptionalBookmakerTriggered);
        }else if ("102".equals(code)) {
            return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.getConfig().getBookmakerId(), TicketCancellationReason.TimeoutTriggered);
        }
        return builderFactory.createTicketCancelBuilder().build(ticketId, Constants.getConfig().getBookmakerId(), TicketCancellationReason.TimeoutTriggered);
    }

    public TicketCashout getTicketCashout(String ticketId) {
        return builderFactory.createTicketCashoutBuilder().setBookmakerId(Constants.getConfig().getBookmakerId()).setTicketId(ticketId).setCashoutStake(12345).build();
    }

    public TicketNonSrSettle getTicketNonSrSettle(String ticketId) {
        return builderFactory.createTicketNonSrSettleBuilder().setBookmakerId(Constants.getConfig().getBookmakerId()).setTicketId(ticketId).setNonSRSettleStake(12345).build();
    }

    /**
     * 接入那边手工修改的篮球玩法id  301_4   301是原始id   下划线后面表示第几节  是自己定义的   这里需要还原成第三方原始id
     *
     * @return
     */
    private String convertThridId(String thirdSourceId) {
        if (thirdSourceId.contains("_")) {
            return thirdSourceId.substring(0, thirdSourceId.indexOf("_"));
        }
        return thirdSourceId;
    }


    /**
     * 从redis获取全局赔率变化接受类型
     *
     * @return
     */
//    private OddsChangeType getOddsChangeType() {
//        RedisClient redisClient = (RedisClient) SpringContextUtils.getBeanByClass(RedisClient.class);
//        String oddsType = redisClient.get("mts.odds.change.type");
//        if (StringUtils.isEmpty(oddsType) || oddsType.equals("any")) {
//            return OddsChangeType.ANY;
//        }
//        if (oddsType.equals("higher")) {
//            return OddsChangeType.HIGHER;
//        }
//        if (oddsType.equals("none")) {
//            return OddsChangeType.NONE;
//        }
//        return OddsChangeType.ANY;
//    }


}
