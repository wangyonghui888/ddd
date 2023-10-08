package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.enums.MatchTypeEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitLogMapper;
import com.panda.sport.rcs.mapper.RcsQuotaUserSingleNoteMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.constant.LimitRedisKeys;
import com.panda.sport.rcs.mgr.enums.BusinessLimitLogTypeEnum;
import com.panda.sport.rcs.mgr.utils.PlayTypeConstants;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserSingleNoteService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote;
import com.panda.sport.rcs.pojo.dto.PlayLanguageInternation;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-12 10:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsQuotaUserSingleNoteServiceImpl extends ServiceImpl<RcsQuotaUserSingleNoteMapper, RcsQuotaUserSingleNote> implements RcsQuotaUserSingleNoteService {
    @Autowired
    private RcsQuotaUserSingleNoteMapper rcsQuotaUserSingleNoteMapper;

    @Autowired
    private ProducerSendMessageUtils sendMessage;

    @Autowired
    private RcsQuotaMerchantSingleFieldLimitService rcsQuotaMerchantSingleFieldLimitService;

    @Autowired
    private RcsLanguageInternationMapper languageInternationMapper;

    @Autowired
    private RcsQuotaBusinessLimitLogMapper rcsQuotaBusinessLimitLogMapper;
    @Autowired
    JedisCluster jedisCluster;


    @Override
    @Transactional
    public HttpResponse<List<RcsQuotaUserSingleNote>> getList(RcsQuotaUserSingleNote rcsQuotaUserSingleNote) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("sport_id", rcsQuotaUserSingleNote.getSportId());
        columnMap.put("bet_state", rcsQuotaUserSingleNote.getBetState());
        columnMap.put("status", 1);
        List<RcsQuotaUserSingleNote> rcsQuotaUserSingleNotes = rcsQuotaUserSingleNoteMapper.selectByMap(columnMap);
        if (CollectionUtils.isEmpty(rcsQuotaUserSingleNotes)) {
            rcsQuotaUserSingleNotes = initRcsQuotaUserSingleNote(rcsQuotaUserSingleNote.getSportId(), rcsQuotaUserSingleNote.getBetState());
            if (CollectionUtils.isEmpty(rcsQuotaUserSingleNotes)) {
                log.error("RcsQuotaUserSingleNote数据初始化失败");
                return HttpResponse.error(-1, "数据初始化失败");
            }
        }
        return HttpResponse.success(rcsQuotaUserSingleNotes);
    }

    /**
     * @return void
     * @Description 初始化数据
     * @Param [sportId, playType]
     * @Author kimi
     * @Date 2020/9/12
     **/
    public List<RcsQuotaUserSingleNote> initRcsQuotaUserSingleNote(Integer sportId, Integer betState) {
        //通同一个体育种类数据基础值必须一样
        Long quotaBase = Constants.QUOTA_BASE;
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("sport_id", sportId);
        List<RcsQuotaUserSingleNote> rcsQuotaUserSingleNoteList1 = rcsQuotaUserSingleNoteMapper.selectByMap(columnMap);
        if (!CollectionUtils.isEmpty(rcsQuotaUserSingleNoteList1)) {
            quotaBase = rcsQuotaUserSingleNoteList1.get(0).getQuotaBase();
        }
        List<RcsQuotaUserSingleNote> rcsQuotaUserSingleNoteList = new ArrayList<>();
        int[] ints = PlayTypeConstants.get(sportId.longValue());

        for (MatchTypeEnum matchTypeEnum : MatchTypeEnum.values()) {
            for (int x = 0; x < ints.length; x++) {
                RcsQuotaUserSingleNote rcsQuotaUserSingleNote1 = createRcsQuotaUserSingleNote(sportId, matchTypeEnum.getId(), ints[x], quotaBase);
                rcsQuotaUserSingleNoteList.add(rcsQuotaUserSingleNote1);
            }
        }
        saveBatch(rcsQuotaUserSingleNoteList);
        List<RcsQuotaUserSingleNote> rcsQuotaUserSingleNoteList2 = new ArrayList<>();
        for (RcsQuotaUserSingleNote rcsQuotaUserSingleNote : rcsQuotaUserSingleNoteList) {
            if (betState.equals(rcsQuotaUserSingleNote.getBetState())) {
                rcsQuotaUserSingleNoteList2.add(rcsQuotaUserSingleNote);
            }
        }
        return rcsQuotaUserSingleNoteList2;
    }

    /**
     * @return com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote
     * @Description
     * @Param [sportId, betState, playType, playId]
     * @Author kimi
     * @Date 2020/9/12
     **/
    public RcsQuotaUserSingleNote createRcsQuotaUserSingleNote(Integer sportId, Integer betState, Integer playId, Long quotaBase) {
        RcsQuotaUserSingleNote rcsQuotaUserSingleNote = new RcsQuotaUserSingleNote();
        rcsQuotaUserSingleNote.setSportId(sportId);
        rcsQuotaUserSingleNote.setBetState(betState);
        rcsQuotaUserSingleNote.setPlayId(playId);
        rcsQuotaUserSingleNote.setQuotaBase(quotaBase);
        rcsQuotaUserSingleNote.setSingleBetLimitRatio(Constants.SINGLE_BET_LIMIT_RATIO_DATA);
        rcsQuotaUserSingleNote.setSingleBetLimit(Constants.SINGLE_BET_LIMIT_RATIO_DATA.multiply(new BigDecimal(quotaBase)));
        rcsQuotaUserSingleNote.setCumulativeCompensationPlayingRatio(Constants.DAY_COMPENSATION_PROPORTION_DATA);
        rcsQuotaUserSingleNote.setCumulativeCompensationPlaying(Constants.DAY_COMPENSATION_PROPORTION_DATA.multiply(new BigDecimal(quotaBase)));
        rcsQuotaUserSingleNote.setStatus(1);
        return rcsQuotaUserSingleNote;
    }

    @Override
    @Transactional
    public List<RcsQuotaUserSingleNote> singleNoteUpdate(RcsQuotaUserSingleNote rcsQuotaUserSingleNote) {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("sport_id", rcsQuotaUserSingleNote.getSportId());
        List<RcsQuotaUserSingleNote> rcsQuotaUserSingleNoteList = rcsQuotaUserSingleNoteMapper.selectByMap(columnMap);
        List<Integer> playIds = new ArrayList<>();
        for (RcsQuotaUserSingleNote rcsQuotaUserSingleNote1 : rcsQuotaUserSingleNoteList) {
            rcsQuotaUserSingleNote1.setQuotaBase(rcsQuotaUserSingleNote.getQuotaBase());
            if (rcsQuotaUserSingleNote1.getId().equals(rcsQuotaUserSingleNote.getId())) {
                rcsQuotaUserSingleNote1.setSingleBetLimitRatio(rcsQuotaUserSingleNote.getSingleBetLimitRatio());
                rcsQuotaUserSingleNote1.setCumulativeCompensationPlayingRatio(rcsQuotaUserSingleNote.getCumulativeCompensationPlayingRatio());
            }
            rcsQuotaUserSingleNote1.setCumulativeCompensationPlaying(rcsQuotaUserSingleNote1.getCumulativeCompensationPlayingRatio().multiply(new BigDecimal(rcsQuotaUserSingleNote1.getQuotaBase())));
            rcsQuotaUserSingleNote1.setSingleBetLimit(rcsQuotaUserSingleNote1.getSingleBetLimitRatio().multiply(new BigDecimal(rcsQuotaUserSingleNote1.getQuotaBase())));
            playIds.add(rcsQuotaUserSingleNote1.getPlayId());
            rcsQuotaUserSingleNote1.setIp(rcsQuotaUserSingleNote.getIp());
        }
        //记录参数修改日志
        addQuotaUserSingleNoteLog(rcsQuotaUserSingleNoteList, rcsQuotaUserSingleNote.getSportId());
        updateBatchById(rcsQuotaUserSingleNoteList);

        //数据库修改成功  -  更新所有通用限额的数据 因为后台是基于基础金额全改的
        String limitKey;
        List<String> keys = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for (RcsQuotaUserSingleNote rcsQuotaUserSingleNote1 : rcsQuotaUserSingleNoteList) {
            String sport_Id = rcsQuotaUserSingleNote1.getSportId().toString();
            String playId = rcsQuotaUserSingleNote1.getPlayId().toString();
            String matchType = rcsQuotaUserSingleNote1.getBetState().toString();
            limitKey = LimitRedisKeys.getCommonSingleBetPlayLimitKey(sport_Id, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, matchType, playId);
            int expire = 30 * 24 * 60 * 60;
            BigDecimal singlePayLimit = rcsQuotaUserSingleNote1.getSinglePayLimit();
            BigDecimal playPaymentLimit = rcsQuotaUserSingleNote1.getCumulativeCompensationPlaying();
            BigDecimal singleBetLimit = rcsQuotaUserSingleNote1.getSingleBetLimit();

            //其他赛种的情况下,更新单注单关限额需要同时更新赔付限额
            if("-1".equals(sport_Id) && null == singlePayLimit && null != singleBetLimit){
                singlePayLimit = singleBetLimit;
            }

            //单注赔付
            if (singlePayLimit != null) {
                jedisCluster.setex(limitKey + ":singlePay", expire, singlePayLimit.multiply(new BigDecimal("100")).toPlainString());
                JSONObject singlePayLimitMap = new JSONObject();
                singlePayLimitMap.put("key", limitKey + ":singlePay");
                singlePayLimitMap.put("type", "user_single_bet_limit");
                jsonArray.add(singlePayLimitMap);
            }
            //玩法累计限额
            if (playPaymentLimit != null) {
                jedisCluster.setex(limitKey + ":playTotal", expire, playPaymentLimit.multiply(new BigDecimal("100")).toPlainString());
                JSONObject playPaymentLimitMap = new JSONObject();
                playPaymentLimitMap.put("key", limitKey + ":playTotal");
                playPaymentLimitMap.put("type", "user_single_bet_limit");
                jsonArray.add(playPaymentLimitMap);
            }
            //单注限额
            if (singleBetLimit != null) {
                jedisCluster.setex(limitKey + ":singleBet", expire, singleBetLimit.multiply(new BigDecimal("100")).toPlainString());
                JSONObject singleBetLimitMap = new JSONObject();
                singleBetLimitMap.put("key", limitKey + ":singleBet");
                singleBetLimitMap.put("type", "user_single_bet_limit");
                jsonArray.add(singleBetLimitMap);
            }
            //把刷新的key统计去刷新对应的本地缓存
            keys.add(limitKey);
        }
        //将修改的信息同步一个mq广播 清除本地缓存
        sendMessage.sendMessage("rcs_local_cache_clear_sdk", jsonArray.toJSONString());
        log.info("清理用户单注单关通用限额缓存完成，sportId:{}，keys:{}，", rcsQuotaUserSingleNote.getSportId(), jsonArray.toJSONString());
        return rcsQuotaUserSingleNoteList;
    }

    /**
     * 记录日志
     *
     * @param newList
     * @param sportId
     */
    public void addQuotaUserSingleNoteLog(List<RcsQuotaUserSingleNote> newList, long sportId) {
        String operateType = BusinessLimitLogTypeEnum.getValue(5);
        String sportName;
        if (sportId == -1) {
            sportName = "其他";
        } else {
            sportName = SportIdEnum.getNameById(sportId);
        }
        for (int i = 0; i < newList.size(); i++) {
            RcsQuotaUserSingleNote newData = newList.get(i);
            RcsQuotaUserSingleNote oldData = getById(newData.getId());
            if (oldData != null) {
                String paramName = "";
                String matchName = oldData.getBetState() == 0 ? "早盘" : "滚球";
                //默认值，记录一次日志
                if (i == 0 && newData.getQuotaBase().longValue() != oldData.getQuotaBase().longValue()) {
                    paramName = operateType + "-" + sportName + "-默认值";
                    String afterVal = newData.getQuotaBase() + "";
                    String beforeVal = oldData.getQuotaBase() + "";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
                //单注投注限额
                if (newData.getSingleBetLimitRatio().compareTo(oldData.getSingleBetLimitRatio()) != 0) {
                    String playName = getPlayName(sportId, oldData.getPlayId());
                    paramName = operateType + "-" + matchName + "-" + sportName + "-" + playName;
                    String afterVal = newData.getSingleBetLimit().longValue() + "";
                    String beforeVal = oldData.getSingleBetLimit().longValue() + "";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
                //玩法累计赔付比例
                if (newData.getCumulativeCompensationPlayingRatio().compareTo(oldData.getCumulativeCompensationPlayingRatio()) != 0) {
                    String playName = getPlayName(sportId, oldData.getPlayId());
                    paramName = operateType + "-" + matchName + "-" + sportName + "-" + playName;
                    String afterVal = newData.getCumulativeCompensationPlaying().longValue() + "";
                    String beforeVal = oldData.getCumulativeCompensationPlaying().longValue() + "";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
            }
        }
    }

    public String getPlayName(long sportId, long palyId) {
        if (sportId == -1 || palyId == -1) {
            return "其它所有玩法";
        }
        String playName = "";
        try {
            List<PlayLanguageInternation> byMultilingualisms = languageInternationMapper.getByMultilingualism(sportId);
            Map<String, List<PlayLanguageInternation>> sportList = byMultilingualisms.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getSportId())));
            HashMap<String, Map> sportIdMap = new HashMap<>();
            for (String key : sportList.keySet()) {
                List<PlayLanguageInternation> playLanguageInternations = sportList.get(key);
                Map<String, Map> playMap = new HashMap<>();
                for (PlayLanguageInternation playLanguageInternation : playLanguageInternations) {
                    Map<String, Map> map = JSONObject.parseObject(playLanguageInternation.getText(), Map.class);
                    playMap.put(String.valueOf(playLanguageInternation.getPlayId()), map);
                }
                sportIdMap.put(key, playMap);
            }
            Map<String, String> playMap = (HashMap) sportIdMap.get(String.valueOf(sportId)).get(String.valueOf(palyId));
            if (playMap != null) {
                playName = playMap.get("zs");
            }
        } catch (Exception e) {
            log.info("::{}::单注单关限额查询玩法异常：", sportId, e);
            playName = String.valueOf(palyId);
        }
        return playName;
    }


    @Override
    public void insertLimitLog(JSONObject data) {
        //参数名称
        String paramName = data.getString("paramName");
        //风控日志操作类型
        String operateType = BusinessLimitLogTypeEnum.getValue(9);
        //修改前参数值
        String beforeVal = data.getString("beforeVal");
        //修改后参数值
        String afterVal = data.getString("afterVal");
        //操作人id
        String userId = data.getString("userId");
        //操作人名称
        String userName = data.getString("userName");
        String ip = data.getString("ip");
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory("商户风控管理");
        limitLoglog.setObjectId("-");
        limitLoglog.setObjectName("-");
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(operateType);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(userId);
        limitLoglog.setUserName(userName);
        limitLoglog.setIp(ip);
        rcsQuotaBusinessLimitLogMapper.insert(limitLoglog);
    }
}
