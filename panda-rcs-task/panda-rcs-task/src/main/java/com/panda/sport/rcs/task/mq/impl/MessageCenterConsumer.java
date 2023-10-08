package com.panda.sport.rcs.task.mq.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mapper.RcsBroadCastMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBroadCast;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.dto.RcsBroadCastDTO;
import com.panda.sport.rcs.task.enums.LanguageTypeDataEnum;
import com.panda.sport.rcs.task.enums.MsgTypeEnum;
import com.panda.sport.rcs.task.utils.LanguageUtils;
import com.panda.sports.api.ISystemUserOrgAuthApi;
import com.panda.sports.api.vo.SysTraderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: xindaima
 * @description: 消息中心
 * @author: kimi
 * @create: 2021-01-16 16:31
 **/
@Component
@Slf4j
public class MessageCenterConsumer extends ConsumerAdapter<RcsBroadCastDTO> {

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    private RcsBroadCastMapper rcsBroadCastMapper;
    @Autowired
    private LanguageUtils languageUtils;
    /***
     * 操盘部有权限的id
     **/
    private   HashMap<Integer,List<Integer>> hashMap=new HashMap<>();
    /**
     * 操盘部所有人
     */
    private  List<Integer> allTradeUserIdList=new ArrayList<>();
    /**
     * 风控中心有权限的人
     */
    private  List<Integer> riskUserIdList=new ArrayList<>();
    /**
     * 结算组人源
     */
    private  List<Integer> settlementUserIds = new ArrayList<>();
    /**
     * 业务拉取的数据
     */
    private List<Map<String, Object>> authUserByOperation=new ArrayList<>();
    /***
     * 最新拉取rpc时间
     **/
    private static  long time=0;
    @Reference(check = false, lazy = true, retries = 1, timeout = 5000)
    private ISystemUserOrgAuthApi systemUserOrgAuthApi;
    public MessageCenterConsumer() {
        super("risk_msg_alarm",null);
    }

    @Override
    public Boolean handleMs(RcsBroadCastDTO msg, Map<String, String> paramsMap) throws Exception {
        try {
            log.info("收到消息：" + JSONObject.toJSONString(msg));
            RcsBroadCast rcsBroadCast = msg.getRcsBroadCast();
            if (rcsBroadCast != null) {
                //1:预警消息  3:封盘消息
                int msgType = rcsBroadCast.getMsgType();
                rcsBroadCast.setExtendsField1(String.valueOf(msg.getMatchType()));
                if ((!ObjectUtils.isEmpty(msg.getMatchType())) && 3 == msg.getMatchType()){
                    initData();
                    msg.setUserId(allTradeUserIdList);
                    log.info("rcsBroadCast={}",JSONObject.toJSONString(rcsBroadCast));
                }else if (msgType == MsgTypeEnum.WARNING.getMsgType()|| msgType == MsgTypeEnum.SEALING.getMsgType()||msgType == MsgTypeEnum.ERROR_END.getMsgType()||msgType == MsgTypeEnum.ORDER_UNSETTLE.getMsgType()) {
                    msg.setUserId(getUserIdByEarlyWarning(Integer.parseInt(rcsBroadCast.getExtendsField())));
                    if(msgType == MsgTypeEnum.ERROR_END.getMsgType()){
                        setErrorEndMsg(rcsBroadCast);
                    }if(msgType == MsgTypeEnum.ORDER_UNSETTLE.getMsgType()){
                        setOrderUnsettleMsg(rcsBroadCast);
                    }
                }
            } else {
                //2:结算消息
                rcsBroadCast = new RcsBroadCast();
                rcsBroadCast.setMsgType(100 + msg.getMsgType());
                rcsBroadCast.setMsgId(msg.getMsgId());
                rcsBroadCast.setContent(msg.getMsg());
                rcsBroadCast.setExtendsField(String.valueOf(msg.getMatchId()));
                rcsBroadCast.setExtendsField1(msg.getMsgTitle());
                rcsBroadCast.setStatus(1);
                rcsBroadCast.setIsRead(0);
                msg.setUserId(getUserIdBySettlement(msg.getMatchId()));
                msg.setRcsBroadCast(rcsBroadCast);
            }
            //插入数据库
            rcsBroadCastMapper.insert(rcsBroadCast);
            //发送mq
            producerSendMessageUtils.sendMessage("rcs_predict_odds_data_ws", msg);
            return true;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * 设置赛事异常结事消息
     * @param rcsBroadCast
     */
    private void setErrorEndMsg(RcsBroadCast rcsBroadCast) {
        try{
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(Long.valueOf(rcsBroadCast.getExtendsField()));
            HashMap<String,String> hashMap=new HashMap();
            for (LanguageTypeDataEnum languageTypeDataEnum:LanguageTypeDataEnum.values()){
                StringBuilder stringBuilder=new StringBuilder();
                String type = languageTypeDataEnum.getType();
                String teamName = languageUtils.getHomeNameAndAwayName(Long.valueOf(rcsBroadCast.getExtendsField()), type);
                stringBuilder.append(teamName);
                if(null!=standardMatchInfo){
                    stringBuilder.append("("+standardMatchInfo.getMatchManageId()+")");
                }
                stringBuilder.append(languageUtils.getErrorEndMsg(type));
                hashMap.put(type,stringBuilder.toString());
            }
            rcsBroadCast.setContent( JSONObject.toJSONString(hashMap));
            if(null!=standardMatchInfo) {
                rcsBroadCast.setExtendsField2(standardMatchInfo.getMatchManageId());
            }
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 未结算赛事消息
     * @param rcsBroadCast
     */
    private void setOrderUnsettleMsg(RcsBroadCast rcsBroadCast) {
        try{
            StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(Long.valueOf(rcsBroadCast.getExtendsField()));
            HashMap<String,String> hashMap=new HashMap();
            for (LanguageTypeDataEnum languageTypeDataEnum:LanguageTypeDataEnum.values()){
                StringBuilder stringBuilder=new StringBuilder();
                String type = languageTypeDataEnum.getType();
                String teamName = languageUtils.getHomeNameAndAwayName(Long.valueOf(rcsBroadCast.getExtendsField()), type);
                stringBuilder.append(teamName);
                if(null!=standardMatchInfo){
                    stringBuilder.append("("+standardMatchInfo.getMatchManageId()+")");
                }
                stringBuilder.append(String.format(languageUtils.getOrderUnsettleMsg(type),rcsBroadCast.getAddition1()));
                hashMap.put(type,stringBuilder.toString());
            }
            rcsBroadCast.setContent(JSONObject.toJSONString(hashMap));
            if(null!=standardMatchInfo) {
                rcsBroadCast.setExtendsField2(standardMatchInfo.getMatchManageId());
            }
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 预警的消息有权限的玩家
     * @param matchId
     * @return
     */
    private List<Integer>  getUserIdByEarlyWarning(Integer matchId){
        return standardMatchInfoMapper.getUserId(matchId);
    }

    /**
     * 结算有权限的玩家
     * @param matchId
     * @return
     */
    private Set<Integer> getUserIdBySettlement(Integer matchId){
        //初始化数据
        initData();
        Set<Integer> userId=new HashSet<>();
        //1:操盘部有权限的id
        List<Integer> list1 = hashMap.get(rcsBroadCastMapper.selectSportIdByMatchId(matchId));
        if (!CollectionUtils.isEmpty(list1)){
            userId.addAll(list1);
        }
        //2:收藏和操盘该赛事的id
        List<Integer> list = rcsBroadCastMapper.selectUserIdByCollection(matchId);
        userId.addAll(list);
        //3：去掉现在不是操盘部的人
        Iterator<Integer> iterator = userId.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            if (!allTradeUserIdList.contains(next)){
                iterator.remove();
            }
        }
        //4:加入风控中心的玩家
        if (!CollectionUtils.isEmpty(riskUserIdList)){
            userId.addAll(riskUserIdList);
        }
        //5:加入结算组的人员
        if (!CollectionUtils.isEmpty(settlementUserIds)){
            userId.addAll(settlementUserIds);
        }
        return userId;
    }

    /**
     * 初始化业务来的数据
     */
    private   void initData() {
        if (System.currentTimeMillis() < time) {
            return;
        }
        authUserByOperation= systemUserOrgAuthApi.getAuthUserByOperation();
        hashMap = getSportIds(authUserByOperation);
        riskUserIdList = systemUserOrgAuthApi.riskUser();
        List<SysTraderVO> sysTraderVOS = systemUserOrgAuthApi.traderUser();
        if (!CollectionUtils.isEmpty(sysTraderVOS)){
            allTradeUserIdList.clear();
            allTradeUserIdList.addAll(sysTraderVOS.stream().map(SysTraderVO :: getId).collect(Collectors.toSet()));
        }
        List<SysTraderVO> settlementTradeVos = systemUserOrgAuthApi.settlementUsers();
        if(!CollectionUtils.isEmpty(settlementTradeVos)){
            settlementUserIds.clear();
            settlementUserIds.addAll(settlementTradeVos.stream().map(SysTraderVO :: getId).collect(Collectors.toSet()));
        }
        time = System.currentTimeMillis() + 1000 * 60;
    }

    private static HashMap<Integer,List<Integer>>  getSportIds( List<Map<String, Object>> authUserByOperation){
        HashMap<Integer,List<Integer>> hashMap=new HashMap<>();
        if (!CollectionUtils.isEmpty(authUserByOperation)){
            for (Map<String, Object> map:authUserByOperation){
                List<Integer> ids = (List<Integer>) map.get("ids");
                String orgName = (String)map.get("orgName");
                Integer sportId = getSportId(orgName);
                List<Integer> list = hashMap.get(sportId);
                if (list==null){
                    list=new ArrayList<>();
                    list.addAll(ids);
                    hashMap.put(sportId,list);
                }
            }
        }
        return hashMap;
    }

    private static  Integer getSportId(String s){
        if (s.contains("美式足球")){
            return 6;
        }else if (s.contains("篮球")){
            return 2;
        } else if (s.contains("足球")){
            return 1;
        }else if (s.contains("棒球")){
            return 3;
        }else if (s.contains("冰球")){
            return 4;
        }else if (s.contains("网球")){
            return 5;
        }else if (s.contains("斯诺克")){
            return 7;
        }else if (s.contains("乒乓球")){
            return 8;
        }else if (s.contains("排球")){
            return 9;
        }else if (s.contains("羽毛球")){
            return 10;
        }else if (s.contains("英雄联盟")){
            return 100;
        }else if (s.contains("dota2")){
            return 101;
        }else if (s.contains("cs")){
            return 102;
        }else if (s.contains("王者荣耀")){
            return 103;
        } else if (s.contains("绝地求生")){
            return 104;
        }else if (s.contains("政治娱乐")){
            return 105;
        }
        return 0;
    }
}
