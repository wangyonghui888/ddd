package com.panda.rcs.push;

import com.alibaba.fastjson.JSON;
import com.panda.rcs.push.entity.constant.BaseConstant;
import com.panda.rcs.push.entity.enums.SubscriptionEnums;
import com.panda.rcs.push.entity.vo.ClientRequestVo;
import com.panda.rcs.push.entity.vo.ClientResponseVo;
import com.panda.rcs.push.entity.vo.SingleSubInfoVo;
import com.panda.rcs.push.utils.BaseUtils;
import com.panda.rcs.push.utils.ClientResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class PushTest {

    private static List<Integer> timelyOrderCommands = Arrays.asList(301, 306);

    //足球货量
    private static List<Integer> amountCommands = Arrays.asList(SubscriptionEnums.LIVE_ODDS_UOF_DATA.getKey(), SubscriptionEnums.FOOTBALL_FORECAST.getKey(), SubscriptionEnums.MARKET_PREDICT_FORECAST.getKey(), SubscriptionEnums.PLACE_PREDICT_FORECAST.getKey());

    //篮球货量
    private static List<Integer> notFootballAmountCommands = Arrays.asList(SubscriptionEnums.MATCH_EVENT.getKey(), SubscriptionEnums.LIVE_ODDS_UOF_DATA.getKey(), SubscriptionEnums.UPDATE_MARKET_INDEX.getKey(), SubscriptionEnums.NO_FOOTBALL_AMOUNT.getKey());

    //右侧消息
    private static List<Integer> messageCommands = Arrays.asList(SubscriptionEnums.MESSAGE_NOT_READ.getKey(), SubscriptionEnums.TRADER_MESSAGE.getKey());

    //事件流
    private static Integer eventCommands = SubscriptionEnums.THIRD_MATCH_EVENT.getKey();



    public void test1() {
        String requestJosnStr = "{\"commands\":[301,306],\"needCommands\":[30006],\"protocolVersion\":2,\"subscribe\":{\"30006\":[{\"userLevels\":[],\"passType\":\"\",\"betAmount\":null,\"tournamentIds\":[],\"matchIds\":[],\"riskPlayIds\":[],\"playIds\":[],\"inputType\":2,\"sportId\":\"1\",\"merchantIds\":[],\"languageType\":\"zs\"}]},\"uuid\":\"258cf560-814c-4dad-b6e6-4a9e524595e6\",\"acks\":[30006]}";
        ClientRequestVo clientRequest = JSON.parseObject(requestJosnStr, ClientRequestVo.class);

        for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
            if(entry.getValue() != null){
                SingleSubInfoVo singleSubInfoVo =  entry.getValue().get(0);
                if(singleSubInfoVo.getMatchId() != null){
                    System.out.println("------------------------------------1");
                }

                if(singleSubInfoVo.getMatchIds() != null && singleSubInfoVo.getMatchIds().length > 0){
                    System.out.println("------------------------------------2");
                }

                //订阅信息没有赛事Id
                if(null == singleSubInfoVo.getMatchId() && singleSubInfoVo.getMatchIds().length == 0){
                    System.out.println("------------------------------------3");
                }
            }
            break;
        }
    }

    public void testOrder(){
        String requestJosnStr = "{\"commands\":[301,306],\"needCommands\":[30006],\"protocolVersion\":2,\"subscribe\":{\"30006\":[{\"matchType\":2,\"userLevels\":[],\"passType\":\"\",\"betAmount\":1000,\"tournamentIds\":[1172425],\"matchIds\":[],\"riskPlayIds\":[],\"playIds\":[],\"inputType\":2,\"sportId\":\"1\",\"merchantIds\":[],\"languageType\":\"zs\"}]},\"uuid\":\"ad29da08-47e6-424d-a031-5500ed739f6a\",\"acks\":[30006]}";
        ClientRequestVo clientRequest = JSON.parseObject(requestJosnStr, ClientRequestVo.class);
        List<Integer> timelyOrderCommands1 = Arrays.asList(301, 306);
        Map<String, SingleSubInfoVo> otherClientGroupMap = new ConcurrentHashMap<>();
        if(clientRequest.getCommands() != null && timelyOrderCommands1.containsAll(Arrays.asList(clientRequest.getCommands())) && clientRequest.getNeedCommands() != null && Arrays.stream(clientRequest.getNeedCommands()).filter(s -> s.equals(30006)).collect(Collectors.toList()).size() > 0){
            for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
                if(entry.getKey().equals(SubscriptionEnums.TIMELY_ORDER.getKey()) && entry.getValue() != null){
                    SingleSubInfoVo singleSubInfoVo =  entry.getValue().get(0);
                    if(singleSubInfoVo.getMatchId() != null){
                        System.out.println("----------------------------1");
                    }

                    if(singleSubInfoVo.getMatchIds() != null && singleSubInfoVo.getMatchIds().length > 0){
                        System.out.println("----------------------------2");
                    }

                    //订阅信息没有赛事Id
                    if(null == singleSubInfoVo.getMatchId() && singleSubInfoVo.getMatchIds().length == 0){

                        otherClientGroupMap.put(clientRequest.getUuid(), singleSubInfoVo);
                        System.out.println("----------------------------3");
                    }
                }
            }
        }

        System.out.println(otherClientGroupMap);

        otherClientGroupMap.forEach((k, v) -> {
            if(v.getSportId() != null && Arrays.asList(v.getSportId().split(BaseConstant.COMMA)).contains(Integer.toString(1)) ){
                if(v.getBetAmount() != null && Integer.parseInt("10000") > v.getBetAmount() ){
                    System.out.println("----------------------------4");
                } else {
                    System.out.println("----------------------------5");
                }
            }
        });
    }

    public void test2() {
//        String requestJosnStr = "{\"states\":[1,2,3,4,5,6],\"commands\":[301,307],\"marketCategoryIds\":[\"4\"],\"needCommands\":[30005,30007,30012,30014],\"currentMatchIds\":[\"3024886\"],\"marketIndex\":\"1\",\"uuid\":\"4b7aed07-6d11-47ec-971d-4bdaf9e56296\"}";
//        String requestJosnStr = "{\"protocolVersion\":2,\"subscribe\":{\"30041\":[{\"matchId\":\"3024886\"}],\"30049\":[{\"matchId\":\"3024886\",\"marketCategoryIds\":[4]}]},\"uuid\":\"4b7aed07-6d11-47ec-971d-4bdaf9e56296\"}";

//        String requestJosnStr = "{\"commands\":[301,306],\"needCommands\":[30001,30003,30006,30014,30012],\"protocolVersion\":2,\"subscribe\":{\"30001\":[{\"sportId\":\"1\",\"matchId\":\"3028718\",\"languageType\":\"zs\"}],\"30003\":[{\"sportId\":\"1\",\"matchId\":\"3028718\",\"languageType\":\"zs\"}],\"30006\":[{\"matchType\":2,\"userLevels\":[],\"passType\":\"\",\"betAmount\":null,\"tournamentIds\":[1172425],\"matchIds\":[3028718],\"riskPlayIds\":[],\"playIds\":[],\"inputType\":3,\"sportId\":\"1\",\"merchantIds\":[],\"matchDate\":2}],\"30012\":[{\"sportId\":\"1\",\"matchId\":\"3028718\",\"languageType\":\"zs\"}],\"30014\":[{\"sportId\":\"1\",\"matchId\":\"3028718\",\"languageType\":\"zs\"}]},\"uuid\":\"885efd23-ad7a-412e-b561-1cd642408a82\",\"acks\":[30006]}\n";

        String requestJosnStr = "{\"msgId\":\"f23973e1-6354-4e07-a30e-c782c20612fb\",\"ack\":1,\"uuid\":\"e058a01e-09c2-4b93-83ee-a116b4f29433\"}\n" +
                "\n";


        ClientRequestVo clientRequest = JSON.parseObject(requestJosnStr, ClientRequestVo.class);
        log.info(":::Socket客户端订阅信息->{}", clientRequest);
        if(clientRequest.getCommands() != null && clientRequest.getSubscribe() == null && Arrays.asList(clientRequest.getCommands()).contains(SubscriptionEnums.CMD_HEARTBEAT_300.getKey())){
            ClientResponseVo clientResponseVo = ClientResponseUtils.createResponseContext(SubscriptionEnums.CMD_HEARTBEAT_300.getKey(), System.currentTimeMillis(), 0, clientRequest.getUuid(), null, null);
            log.info("心跳300");
            return;
        }

        if(clientRequest.getProtocolVersion() != null && clientRequest.getProtocolVersion() == 2){

            /**
             * 及时注单订阅：国际编码 + 赛事 + 玩法
             * eg：{"commands":[301,306],"needCommands":[30006],"protocolVersion":2,"subscribe":{"30006":[{"matchType":1,"userLevels":[],"passType":"","betAmount":null,"tournamentIds":[],"matchIds":[1689180,1672792,1687500,1681664,1689262,1689278,1689280,1689281,1689283,1689284,1689282,1687440,1689737,1689286,1689287,1689289,1689290,1625367,1672774,1664372,1662583,1664373,1685669,1662588,1689633,1689291,1689292,1672742,1689745,1689293,1689294,1689743,1689637,1689634,1689638,1687530,1685696,1685695,1687494,1689639,1577550,1662576,1662575,1662580,1662578,1662744],"riskPlayIds":[],"playIds":[],"inputType":2,"sportId":"1","merchantIds":[],"languageType":"zs"}]},"uuid":"b4310463-8587-48fc-8f97-7d6e39ada56a","acks":[30006]}
             */
            //if(clientRequest.getCommands() != null && timelyOrderCommands.containsAll(Arrays.asList(clientRequest.getCommands())) && clientRequest.getNeedCommands() != null && Arrays.stream(clientRequest.getNeedCommands()).filter(s -> s.equals(30006)).collect(Collectors.toList()).size() > 0){
            if(null != clientRequest.getSubscribe() && clientRequest.getSubscribe().containsKey(SubscriptionEnums.TIMELY_ORDER.getKey())){
                log.info("--->及时注单订阅，订阅客户端->{}", clientRequest);
                for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
                    if(entry.getKey().equals(SubscriptionEnums.TIMELY_ORDER.getKey()) && entry.getValue() != null){
                        SingleSubInfoVo singleSubInfoVo =  entry.getValue().get(0);
                        if(singleSubInfoVo.getMatchId() != null){
                            log.info("及时注单-单赛事订阅");
                        }

                        if(singleSubInfoVo.getMatchIds() != null && singleSubInfoVo.getMatchIds().length > 0){
                            for (String matchId : singleSubInfoVo.getMatchIds()){
                                log.info("及时注单-多赛事订阅");
                            }
                        }

                        //订阅信息没有赛事Id
                        if(null == singleSubInfoVo.getMatchId() && singleSubInfoVo.getMatchIds().length == 0){
                            log.info("及时注单-无赛事Id订阅");
                        }
                        break;
                    }
                }
                return;
            }

            if(clientRequest.getSubscribe() != null && clientRequest.getSubscribe().size() > 0){

                /**
                 * 货量推送订阅：赛事 + 玩法
                 * eg：足球：{"protocolVersion":2,"subscribe":{"30014":[{"sportId":"1","matchId":"2996140","marketCategoryIds":[4]}],"30036":[{"sportId":"1","matchId":"2996140","marketCategoryIds":[4]}],"30044":[{"sportId":"1","matchId":"2996140","marketCategoryIds":[4]}],"30045":[{"sportId":"1","matchId":"2996140","marketCategoryIds":[4]}]},"uuid":"a3cadfaa-6e4e-4ae2-8807-10a118bc4f4d"}
                 * 篮球：{"protocolVersion":2,"acks":[30042],"subscribe":{"30003":[{"sportId":"2","matchId":"1690175","marketCategoryIds":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],"states":[1,2,3,4,5,6],"playTimeStages":[3],"matchType":2}],"30014":[{"sportId":"2","matchId":"1690175","marketCategoryIds":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],"states":[1,2,3,4,5,6],"playTimeStages":[3],"matchType":2}],"30033":[{"sportId":"2","matchId":"1690175","marketCategoryIds":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],"states":[1,2,3,4,5,6],"playTimeStages":[3],"matchType":2}],"30042":[{"sportId":"2","matchId":"1690175","marketCategoryIds":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],"states":[1,2,3,4,5,6],"playTimeStages":[3],"matchType":2}]},"uuid":"524a7943-7c3c-494f-8a7a-4e207c0d24a3"}
                 */
                List<Integer> subCmd = new ArrayList<>();
                clientRequest.getSubscribe().forEach((k ,v) -> {
                    subCmd.add(k);
                });

                if(amountCommands.containsAll(subCmd) || notFootballAmountCommands.containsAll(subCmd)){
                    for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
                        if(entry.getValue() != null && entry.getValue().size() > 0){
                            entry.getValue().forEach(match -> {
                                String playIds = BaseUtils.toStringForParams(match.getMarketCategoryIds());
                                String mapKey = BaseUtils.toStringForParams(match.getMatchId().toString(), BaseConstant.SEPARATE_UNDERSCORE, playIds);
                                log.info("货量订阅-订阅信息-{}", mapKey);
                            });
                            break;
                        }
                    }
                    return;
                }

                /**
                 * 右侧消息订阅
                 * {"protocolVersion":2,"subscribe":{"30039":[{"userId":10018,"warningMessageDataList":[{"msgType":[101,102,103,104,105,106]},{"msgType":[1],"sportId":2,"matchType":1},{"msgType":[3],"sportId":2,"matchType":1}],"languageType":"zs"}],"30040":[{"userId":10018,"warningMessageDataList":[{"msgType":[101,102,103,104,105,106]},{"msgType":[1],"sportId":2,"matchType":1},{"msgType":[3],"sportId":2,"matchType":1}],"languageType":"zs"}]},"uuid":"a0849fe6-d464-407c-a41c-9a799069538c"}
                 * 此处逻辑先搁置
                 */
                if(messageCommands.containsAll(subCmd)){
                    for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
                        SingleSubInfoVo singleSubInfoVo = entry.getValue().get(0);
                        if(singleSubInfoVo != null){
                            String userMapKey = BaseUtils.toStringForParams(singleSubInfoVo.getLanguageType(), BaseConstant.SEPARATE_UNDERSCORE, singleSubInfoVo.getUserId());
                            log.info("右侧消息-订阅信息-{}", userMapKey);
                        }
                        break;
                    }
                    return;
                }

                /**
                 * 赛事事件流订阅：事件源_赛事Id
                 * eg:{"needCommands":[30001,30003,30013,30051],"protocolVersion":2,"subscribe":{"30001":[{"sportId":"1","matchId":"1667650"}],"30003":[{"sportId":"1","matchId":"1667650"}],"30013":[{"matchId":"1667650","dataSourceCode":"SR"}],"30051":[{"sportId":"1","matchId":"1667650","dataSourceCode":"SR"}]},"uuid":"6ef59f43-5af9-4037-8a8d-a5df97909a22"}
                 */
                if(clientRequest.getNeedCommands() != null && Arrays.asList(clientRequest.getNeedCommands()).contains(eventCommands)){
                    for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
                        if(entry.getKey().equals(SubscriptionEnums.THIRD_MATCH_EVENT.getKey())){
                            SingleSubInfoVo singleSubInfoVo = entry.getValue().get(0);
                            if(singleSubInfoVo != null){
                                String userMapKey = BaseUtils.toStringForParams(singleSubInfoVo.getDataSourceCode().toUpperCase(), BaseConstant.SEPARATE_UNDERSCORE, Long.toString(singleSubInfoVo.getMatchId()));
                                log.info("赛事事件流-订阅信息-{}", userMapKey);
                            }
                            break;
                        }
                    }
                    return;
                }

                /**
                 * {"states":[1,2,3,4,5,6],"commands":[301,307],"marketCategoryIds":["4"],"needCommands":[30005,30007,30012,30014],"currentMatchIds":["3024886"],"marketIndex":"1","uuid":"271bd691-f02a-4e12-a4c8-9f3003caf8b0"}
                 */

                /**
                 * 赛事订阅：赛事Id + 玩法Id
                 * eg：{"protocolVersion":2,"subscribe":{"30001":[{"sportId":1,"matchId":2992694,"marketCategoryIds":[1,4,2,17,19,18,111,113,114,119,121,122,126,128,127,129,130,333,334,335,133,240,332,310,306,307,311,308,309],"states":[1,2,3,4,5,6]},{"sportId":1,"matchId":2993924,"marketCategoryIds":[1,4,2,17,19,18,111,113,114,119,121,122,126,128,127,129,130,333,334,335,133,240,332,310,306,307,311,308,309],"states":[1,2,3,4,5,6]},{"sportId":1,"matchId":2993636,"marketCategoryIds":[1,4,2,17,19,18,111,113,114,119,121,122,126,128,127,129,130,333,334,335,133,240,332,310,306,307,311,308,309],"states":[1,2,3,4,5,6]}]},"uuid":"d97e89ec-4177-4361-96f2-c69b7d4079d0"}
                 */
                for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
                    if(entry.getValue() != null && entry.getValue().size() > 0){
                        entry.getValue().forEach(match -> {
                            String playIds;
                            if(match.getMarketCategoryIds() == null){
                                playIds = "*";
                            } else {
                                playIds = BaseUtils.toStringForParams(match.getMarketCategoryIds());
                            }
                            String mapKey = BaseUtils.toStringForParams(match.getMatchId().toString(), BaseConstant.SEPARATE_UNDERSCORE, playIds);
                            log.info("赛事维度-订阅信息-{}", mapKey);
                        });
                        break;
                    }
                }
            }
        } else if (clientRequest.getCommands() != null && clientRequest.getNeedCommands() != null && clientRequest.getCurrentMatchIds() != null && clientRequest.getMarketCategoryIds() != null){
            String playIds=BaseUtils.toStringForParams(clientRequest.getMarketCategoryIds());
            String mapKey = BaseUtils.toStringForParams(clientRequest.getCurrentMatchIds()[0].toString(), BaseConstant.SEPARATE_UNDERSCORE, playIds);
            log.info("赛事玩法-订阅信息-{}", mapKey);
        }
    }

    public void  test3() {

        List<Integer> playIds = new ArrayList<Integer>(){{

        }};

        List<Integer> tournamentIds = new ArrayList<Integer>(){{

        }};

        List<Integer> playSetIds = new ArrayList<Integer>(){{
            this.add(585);
        }};

        List<String> matchIds = new ArrayList<String>(){{
            this.add("3002029"); //足球
        }};

        String ss = "{\"commands\":[301,306],\"needCommands\":[30001,30003,30006,30014,30012],\"protocolVersion\":2,\"subscribe\":{\"30001\":[{\"sportId\":\"1\",\"matchId\":\"3029173\",\"languageType\":\"zs\"}],\"30003\":[{\"sportId\":\"1\",\"matchId\":\"3029173\",\"languageType\":\"zs\"}],\"30006\":[{\"matchType\":2,\"userLevels\":[],\"passType\":\"\",\"betAmount\":null,\"tournamentIds\":[823915],\"matchIds\":[3029173],\"riskPlayIds\":[585],\"playIds\":[],\"inputType\":3,\"sportId\":\"1\",\"merchantIds\":[],\"matchDate\":2,\"languageType\":\"zs\"}],\"30012\":[{\"sportId\":\"1\",\"matchId\":\"3029173\",\"languageType\":\"zs\"}],\"30014\":[{\"sportId\":\"1\",\"matchId\":\"3029173\",\"languageType\":\"zs\"}]},\"uuid\":\"7f842f58-3f12-4c60-aba2-6514e94f60c5\",\"acks\":[30006]}";
        ClientRequestVo clientRequest = JSON.parseObject(ss, ClientRequestVo.class);
        SingleSubInfoVo singleSubInfoVo = clientRequest.getSubscribe().get(30006).get(0);
        //国际化过滤
        if(!singleSubInfoVo.getLanguageType().equals("zs")){
            log.info("1");
        }

        //额度过滤
        if(null != singleSubInfoVo.getBetAmount() && 100 < singleSubInfoVo.getBetAmount() ){
            log.info("2");
        }

        //滚球早盘类型过滤
        if(null != singleSubInfoVo.getMatchType() && !singleSubInfoVo.getMatchType().equals(2)){
            log.info("3");
        }

        //订单类型 单/串关过滤
        if(null != singleSubInfoVo.getPassType() && !singleSubInfoVo.getPassType().equals(1)){
            log.info("4");
        }

        //用户标签过滤
        if(singleSubInfoVo.getUserLevels().length > 0 && !Arrays.asList(singleSubInfoVo.getUserLevels()).contains(1)){
            log.info("5");
        }

        //球种过滤
        if(!BaseConstant.NULL_STRING.equals(singleSubInfoVo.getSportId()) && null != singleSubInfoVo.getSportId() && !Arrays.asList(singleSubInfoVo.getSportId().split(BaseConstant.COMMA)).contains(Integer.toString(1))){
            log.info("6");
        }

        //玩法集过滤
        if(singleSubInfoVo.getRiskPlayIds() != null && singleSubInfoVo.getRiskPlayIds().length > 0 && !Arrays.asList(singleSubInfoVo.getRiskPlayIds()).containsAll(playSetIds)){
            log.info("11");
        }

        //玩法过滤
        if(singleSubInfoVo.getPlayIds().length > 0 && !Arrays.asList(singleSubInfoVo.getPlayIds()).containsAll(playIds)){
            log.info("7");
        }

        //联赛过滤
        if(singleSubInfoVo.getTournamentIds().length > 0 && !Arrays.asList(singleSubInfoVo.getTournamentIds()).containsAll(tournamentIds)){
            log.info("8");
        }

        //多赛事维度
        if(singleSubInfoVo.getMatchIds().length > 0 && !Arrays.asList(singleSubInfoVo.getMatchIds()).containsAll(matchIds)){
            log.info("9");
        }

        //单赛事
        if(null != singleSubInfoVo.getMatchId() && !matchIds.contains(Long.toString(singleSubInfoVo.getMatchId())) ){
            log.info("10");
        }
    }

    public void test5(){
        String str1 ="{\"commands\":[301,306],\"needCommands\":[30006],\"protocolVersion\":2,\"subscribe\":{\"30006\":[{\"matchType\":1,\"userLevels\":[],\"passType\":\"\",\"betAmount\":null,\"tournamentIds\":[],\"matchIds\":[1689180,1672792,1687500,1681664,1689262,1689278,1689280,1689281,1689283,1689284,1689282,1687440,1689737,1689286,1689287,1689289,1689290,1625367,1672774,1664372,1662583,1664373,1685669,1662588,1689633,1689291,1689292,1672742,1689745,1689293,1689294,1689743,1689637,1689634,1689638,1687530,1685696,1685695,1687494,1689639,1577550,1662576,1662575,1662580,1662578,1662744],\"riskPlayIds\":[],\"playIds\":[],\"inputType\":2,\"sportId\":\"1\",\"merchantIds\":[],\"languageType\":\"zs\"}]},\"uuid\":\"b4310463-8587-48fc-8f97-7d6e39ada56a\",\"acks\":[30006]}";
        String str2 = "{\"commands\":[301,306],\"needCommands\":[30001,30003,30006,30014,30012,50001],\"protocolVersion\":2,\"subscribe\":{\"30001\":[{\"sportId\":\"1\",\"matchId\":\"3044338\",\"languageType\":\"zs\"}],\"30003\":[{\"sportId\":\"1\",\"matchId\":\"3044338\",\"languageType\":\"zs\"}],\"30006\":[{\"matchType\":2,\"userLevels\":[],\"passType\":\"\",\"betAmount\":null,\"tournamentIds\":[822164],\"matchIds\":[3044338],\"riskPlayIds\":[],\"playIds\":[],\"inputType\":3,\"sportId\":\"1\",\"merchantIds\":[],\"matchDate\":2,\"languageType\":\"zs\"}],\"30012\":[{\"sportId\":\"1\",\"matchId\":\"3044338\",\"languageType\":\"zs\"}],\"30014\":[{\"sportId\":\"1\",\"matchId\":\"3044338\",\"languageType\":\"zs\"}],\"50001\":[{\"sportId\":\"1\",\"matchId\":\"3044338\",\"languageType\":\"zs\"}]},\"uuid\":\"add9b740-4170-47b3-8f56-fc74efc7ff23\",\"acks\":[30006]}";
        String str3 = "{\"protocolVersion\":2,\"subscribe\":{\"30014\":[{\"sportId\":\"1\",\"matchId\":\"2996140\",\"marketCategoryIds\":[4]}],\"30036\":[{\"sportId\":\"1\",\"matchId\":\"2996140\",\"marketCategoryIds\":[4]}],\"30044\":[{\"sportId\":\"1\",\"matchId\":\"2996140\",\"marketCategoryIds\":[4]}],\"30045\":[{\"sportId\":\"1\",\"matchId\":\"2996140\",\"marketCategoryIds\":[4]}]},\"uuid\":\"a3cadfaa-6e4e-4ae2-8807-10a118bc4f4d\"}";
        String str4 = "{\"protocolVersion\":2,\"acks\":[30042],\"subscribe\":{\"30003\":[{\"sportId\":\"2\",\"matchId\":\"1690175\",\"marketCategoryIds\":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],\"states\":[1,2,3,4,5,6],\"playTimeStages\":[3],\"matchType\":2}],\"30014\":[{\"sportId\":\"2\",\"matchId\":\"1690175\",\"marketCategoryIds\":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],\"states\":[1,2,3,4,5,6],\"playTimeStages\":[3],\"matchType\":2}],\"30033\":[{\"sportId\":\"2\",\"matchId\":\"1690175\",\"marketCategoryIds\":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],\"states\":[1,2,3,4,5,6],\"playTimeStages\":[3],\"matchType\":2}],\"30042\":[{\"sportId\":\"2\",\"matchId\":\"1690175\",\"marketCategoryIds\":[37,38,39,40,42,43,18,19,45,46,47,48,51,52,53,54,57,58,59,60,63,64,65,66,142,143,26,75],\"states\":[1,2,3,4,5,6],\"playTimeStages\":[3],\"matchType\":2}]},\"uuid\":\"524a7943-7c3c-494f-8a7a-4e207c0d24a3\"}";
        String str5 = "{\"protocolVersion\":2,\"subscribe\":{\"30039\":[{\"userId\":10018,\"warningMessageDataList\":[{\"msgType\":[101,102,103,104,105,106]},{\"msgType\":[1],\"sportId\":2,\"matchType\":1},{\"msgType\":[3],\"sportId\":2,\"matchType\":1}],\"languageType\":\"zs\"}],\"30040\":[{\"userId\":10018,\"warningMessageDataList\":[{\"msgType\":[101,102,103,104,105,106]},{\"msgType\":[1],\"sportId\":2,\"matchType\":1},{\"msgType\":[3],\"sportId\":2,\"matchType\":1}],\"languageType\":\"zs\"}]},\"uuid\":\"a0849fe6-d464-407c-a41c-9a799069538c\"}";
        String str6 = "{\"needCommands\":[30001,30003,30013,30051],\"protocolVersion\":2,\"subscribe\":{\"30001\":[{\"sportId\":\"1\",\"matchId\":\"1667650\"}],\"30003\":[{\"sportId\":\"1\",\"matchId\":\"1667650\"}],\"30013\":[{\"matchId\":\"1667650\",\"dataSourceCode\":\"SR\"}],\"30051\":[{\"sportId\":\"1\",\"matchId\":\"1667650\",\"dataSourceCode\":\"SR\"}]},\"uuid\":\"6ef59f43-5af9-4037-8a8d-a5df97909a22\"}";

        ClientRequestVo clientRequest1 = JSON.parseObject(str1, ClientRequestVo.class);
        ClientRequestVo clientRequest2 = JSON.parseObject(str2, ClientRequestVo.class);
        ClientRequestVo clientRequest3 = JSON.parseObject(str3, ClientRequestVo.class);
        ClientRequestVo clientRequest4 = JSON.parseObject(str4, ClientRequestVo.class);
        ClientRequestVo clientRequest5 = JSON.parseObject(str5, ClientRequestVo.class);
        ClientRequestVo clientRequest6 = JSON.parseObject(str6, ClientRequestVo.class);
        ClientRequestVo clientRequest7 = JSON.parseObject(str1, ClientRequestVo.class);
        ClientRequestVo clientRequest8 = JSON.parseObject(str2, ClientRequestVo.class);
        ClientRequestVo clientRequest9 = JSON.parseObject(str3, ClientRequestVo.class);
        ClientRequestVo clientRequest10 = JSON.parseObject(str4, ClientRequestVo.class);
        ClientRequestVo clientRequest11 = JSON.parseObject(str5, ClientRequestVo.class);
        ClientRequestVo clientRequest12 = JSON.parseObject(str6, ClientRequestVo.class);

        Map<String, ClientRequestVo> clientRequestVoMap = new HashMap<>();
        clientRequestVoMap.put("1", clientRequest1);
        clientRequestVoMap.put("2", clientRequest2);
        clientRequestVoMap.put("3", clientRequest3);
        clientRequestVoMap.put("4", clientRequest4);
        clientRequestVoMap.put("5", clientRequest5);
        clientRequestVoMap.put("6", clientRequest6);
        clientRequestVoMap.put("7", clientRequest7);
        clientRequestVoMap.put("8", clientRequest8);
        clientRequestVoMap.put("9", clientRequest9);
        clientRequestVoMap.put("10", clientRequest10);
        clientRequestVoMap.put("11", clientRequest11);
        clientRequestVoMap.put("12", clientRequest12);


        Map<String, ClientRequestVo> filterMap = clientRequestVoMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(SubscriptionEnums.SERVER_ANSWER_MESSAGE.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        filterMap.forEach((k,v) -> {
            System.out.println("-------------------------------------");
        });

        System.out.println(filterMap);
        if(filterMap.size() > 0){
            for (Map.Entry<String, ClientRequestVo> entry : filterMap.entrySet()){
                System.out.println("K = " + entry.getKey() + ",V = " + entry.getValue());
            }
        }

        //System.out.println(filterMap.size());
//        clientRequestVoMap.entrySet().stream().filter(m -> m.getValue().getSubscribe().containsKey(SubscriptionEnums.TIMELY_ORDER.getKey())).forEach(m -> {
//            System.out.println("K = " + m.getKey() + ",V = " + m.getValue());
//        });
    }

    public static void main(String[] args) {
        PushTest pushTest = new PushTest();
        pushTest.test5();

//        List<String> playIds = new ArrayList<>();
//        playIds.add("4");
//
//        String matchId = "21002";
//        String str = "21002_*";
//
//        String newKey = str.replace(BaseUtils.toStringForParams(matchId, BaseConstant.SEPARATE_UNDERSCORE), "");
//        List<String> _playIds = Arrays.asList(newKey.split(BaseConstant.SEPARATE_UNDERSCORE));
//
//        List<String> containsPlayIds = _playIds.stream().filter(playIds::contains).collect(Collectors.toList());
//
//        System.out.println(_playIds);
//        System.out.println(containsPlayIds);

//        String leftStr = "{\"protocolVersion\":2,\"subscribe\":{\"30039\":[{\"userId\":10018,\"warningMessageDataList\":[{\"msgType\":[101,102,103,104,105,106]},{\"msgType\":[1],\"sportId\":2,\"matchType\":1},{\"msgType\":[3],\"sportId\":2,\"matchType\":1}],\"languageType\":\"zs\"}],\"30040\":[{\"userId\":10018,\"warningMessageDataList\":[{\"msgType\":[101,102,103,104,105,106]},{\"msgType\":[1],\"sportId\":2,\"matchType\":1},{\"msgType\":[3],\"sportId\":2,\"matchType\":1}],\"languageType\":\"zs\"}]},\"uuid\":\"a0849fe6-d464-407c-a41c-9a799069538c\"}";
//        ClientRequestVo clientRequest = JSON.parseObject(leftStr, ClientRequestVo.class);
//
//        for (Map.Entry<Integer, List<SingleSubInfoVo>> entry : clientRequest.getSubscribe().entrySet()){
//            SingleSubInfoVo singleSubInfoVo = entry.getValue().get(0);
//            if(singleSubInfoVo != null){
//                String userMapKey = BaseUtils.toStringForParams(singleSubInfoVo.getLanguageType(), BaseConstant.SEPARATE_UNDERSCORE, singleSubInfoVo.getUserId());
//            }
//            break;
//        }
    }

}
