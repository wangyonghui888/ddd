package com.panda.sport.rcs.mgr.mq.impl;

import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.mq.impl
 * @Description :  初始化各维度基础配置，发送MQ消息到SDK
 * @Date: 2020-01-11 20:58
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class


BasicConfigProvider {
    public static String msgConfTag = "rcs_sdk_config_";

    @Autowired
    ProducerSendMessageUtils sendMessage;



    /**
     * 初始化发送基础配置消息到MQ
     **/
    public void sendBusConfig( Map<String, Object> cacheConfigMap){
//        List<RcsBusinessDayPaidConfig> dayPaidConfigList = (List<RcsBusinessDayPaidConfig>)cacheConfigMap.get(RcsBusinessDayPaidConfig.class.getName());
//        List<RcsBusinessMatchPaidConfig> matchPaidConfigList = (List<RcsBusinessMatchPaidConfig>)cacheConfigMap.get(RcsBusinessMatchPaidConfig.class.getName());
//        List<RcsBusinessPlayPaidConfig> playPaidConfigList = (List<RcsBusinessPlayPaidConfig>)cacheConfigMap.get(RcsBusinessPlayPaidConfig.class.getName());
//        List<RcsBusinessUserPaidConfig> userPaidConfigList = (List<RcsBusinessUserPaidConfig>)cacheConfigMap.get(RcsBusinessUserPaidConfig.class.getName());
//        List<RcsBusinessSingleBetConfig> singleBetConfigList = (List<RcsBusinessSingleBetConfig>)cacheConfigMap.get(RcsBusinessSingleBetConfig.class.getName());
//        List<RcsBusinessConPlayConfig> conPlayConfigList = (List<RcsBusinessConPlayConfig>)cacheConfigMap.get(RcsBusinessConPlayConfig.class.getName());


//        Map<String,List<RcsBusinessMatchPaidConfig>> matchPaidConfigs = new HashMap<>();
//        Map<String,List<RcsBusinessPlayPaidConfig>> playPaidConfigs = new HashMap<>();
//        Map<String,List<RcsBusinessUserPaidConfig>> userPaidConfigs = new HashMap<>();
//        Map<String,List<RcsBusinessSingleBetConfig>> singleBetConfigs = new HashMap<>();
//        Map<String,List<RcsBusinessConPlayConfig>> conPlayConfigs =new HashMap<>();
//        for(RcsBusinessDayPaidConfig dayPaidConfig:dayPaidConfigList) {
//            if(dayPaidConfig!=null) {
//                sendMessage.sendMessage(msgConfTag + "," + BUS_DAY_CONFIG_KEY + "," + dayPaidConfig.getBusinessId(), JSONObject.toJSON(dayPaidConfig));
//            }
//        }
//
//        for(RcsBusinessMatchPaidConfig config:matchPaidConfigList) {
//            List<RcsBusinessMatchPaidConfig> list = matchPaidConfigs.get(config.getBusinessId().toString());
//            if(list ==null) {
//                list = new ArrayList<>();
//            }
//            list.add(config);
//            matchPaidConfigs.put(config.getBusinessId().toString(),list);
//        }
//
//        for(RcsBusinessPlayPaidConfig config:playPaidConfigList) {
//            List<RcsBusinessPlayPaidConfig> list = playPaidConfigs.get(config.getBusinessId().toString());
//            if (list == null) {
//                list = new ArrayList<>();
//                playPaidConfigs.put(config.getBusinessId().toString(),list);
//            }
//            list.add(config);
//        }
//
//        for(RcsBusinessUserPaidConfig config:userPaidConfigList) {
//            List<RcsBusinessUserPaidConfig> list = userPaidConfigs.get(config.getBusinessId().toString());
//            if(list ==null){
//                list = new ArrayList<>();
//                userPaidConfigs.put(config.getBusinessId().toString(),list);
//            }
//            list.add(config);
//        }
//
//        for(RcsBusinessSingleBetConfig config:singleBetConfigList) {
//           List<RcsBusinessSingleBetConfig> list= singleBetConfigs.get(config.getBusinessId().toString());
//           if(list == null){
//                list = new ArrayList<>();
//               singleBetConfigs.put(config.getBusinessId().toString(),list);
//            }
//            list.add(config);
//
//        }
//
//        for(RcsBusinessConPlayConfig config:conPlayConfigList) {
//            List<RcsBusinessConPlayConfig> list = conPlayConfigs.get(config.getBusinessId().toString());
//            if(list == null){
//                list = new ArrayList<>();
//            }
//            list.add(config);
//            conPlayConfigs.put(config.getBusinessId().toString(),list);
//        }
//
//
//
//
//
//        for(String key: matchPaidConfigs.keySet()) {
//            if(matchPaidConfigs.get(key)!=null && matchPaidConfigs.get(key).size() >0) {
//                sendMessage.sendMessage(msgConfTag + "," + BUS_MATCH_CONFIG_KEY + "," + key, JSONObject.toJSON(matchPaidConfigs.get(key)));
//            }
//        }
//        for(String key: playPaidConfigs.keySet()) {
//            if(playPaidConfigs.get(key)!=null && playPaidConfigs.get(key).size() >0) {
//                sendMessage.sendMessage(msgConfTag + "," + BUS_PLAY_CONFIG_KEY + "," + key, JSONObject.toJSON(playPaidConfigs.get(key)));
//            }
//        }
//        for(String key: userPaidConfigs.keySet()) {
//            if(userPaidConfigs.get(key)!=null && userPaidConfigs.get(key).size() >0) {
//                sendMessage.sendMessage(msgConfTag + "," + BUS_USER_CONFIG_KEY + "," + key, JSONObject.toJSON(userPaidConfigs.get(key)));
//            }
//        }
//        for(String key: singleBetConfigs.keySet()) {
//            if(singleBetConfigs.get(key)!=null && singleBetConfigs.get(key).size() >0) {
//                sendMessage.sendMessage(msgConfTag + "," + BUS_SINGLE_BET_CONFIG_KEY + "," + key, JSONObject.toJSON(singleBetConfigs.get(key)));
//            }
//        }
//        for(String key: conPlayConfigs.keySet()) {
//            if(conPlayConfigs.get(key)!=null && conPlayConfigs.get(key).size() >0) {
//                sendMessage.sendMessage(msgConfTag + "," + BUS_CON_PLAY_CONFIG_KEY + "," + key, JSONObject.toJSON(conPlayConfigs.get(key)));
//            }
//        }
//
//        /*sendMessage.sendMessage(msgConfTag + "," + BUS_DAY_CONFIG_KEY, cacheConfigMap.get(RcsBusinessDayPaidConfig.class.getName()));
//        sendMessage.sendMessage(msgConfTag+","+BUS_MATCH_CONFIG_KEY,cacheConfigMap.get(RcsBusinessMatchPaidConfig.class.getName()));
//        sendMessage.sendMessage(msgConfTag+","+BUS_PLAY_CONFIG_KEY,cacheConfigMap.get(RcsBusinessPlayPaidConfig.class.getName()));
//        sendMessage.sendMessage(msgConfTag+","+BUS_USER_CONFIG_KEY,cacheConfigMap.get(RcsBusinessUserPaidConfig.class.getName()));
//        sendMessage.sendMessage(msgConfTag+","+BUS_SINGLE_BET_CONFIG_KEY,cacheConfigMap.get(RcsBusinessSingleBetConfig.class.getName()));
//        sendMessage.sendMessage(msgConfTag+","+BUS_CON_PLAY_CONFIG_KEY,cacheConfigMap.get(RcsBusinessConPlayConfig.class.getName()));
//        sendMessage.sendMessage(msgConfTag+","+BUS_ORDER_ACCEPT_CONFIG_KEY,cacheConfigMap.get(RcsMatchOrderAcceptConfig.class.getName()));
//*/
//        sendMessage.sendMessage(msgConfTag+","+BUS_ORDER_ACCEPT_CONFIG_KEY,cacheConfigMap.get(RcsMatchOrderAcceptConfig.class.getName()));
        sendMessage.sendMessage(msgConfTag+","+"AllPlay",cacheConfigMap.get("AllPlay"));

        log.info("risk 发送MQ基础数据成功!");
    }

}
