package com.panda.sport.rcs.data.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMatchMarketMessageDTO;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.utils.CommonUtil;
import com.panda.sport.rcs.enums.MacthStatusEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author V
 * @since 2019-09-26
 */
@Slf4j
@Service
public class StandardMatchInfoServiceImpl extends ServiceImpl<StandardMatchInfoMapper  , StandardMatchInfo> implements IStandardMatchInfoService {

    private static final String BASKETBALL_TIME_CORRECTION = "basketballTimeCorrection";
    private static final String SNOOKER_TIME_CORRECTION = "snookerTimeCorrection";
    private static final String TENNIS_TIME_CORRECTION = "tennisTimeCorrection";
    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;
    private static final String MATCH_TEMP_INFO = "matchTempInfo";
    private static final String MARK_EVENT = "markEvent";
    private static final String WORD_PERIOD = "period";
    private static final String WORD_SECOND_NUM = "secondNum";
    public static List<Integer> TETC = Arrays.asList(3,4,5,6,7,8,9);
    //网球局暂停
    public static List<String> TENNIS_SET_PUASE = Arrays.asList("800","900","1000","1100","1200","301","302","303","304","305","306","445","100","999");
    public static List<String> BASEBALL_PUASE = Arrays.asList("421","422","423","424","425","426","427","428","429","430","431","432","433","434","435","435","436","437","438","43910","43810","43911","43811","43912","43812","43913","43813","43914","43814","43915","43815","43916","43816","43917","43817","43918","43818","43919","43819","43920","100","999");
    public static List<Integer> passPeriod = Arrays.asList(
            0,32,34,61,81,90,100,999,
            800,900,1000,1100,1200,
            301,302,303,304,305,306,445,
            421,422,423,424,425,426,427,428,429,430,431,432,433,434,435,435,436,437,438,
            43910,43810,43911,43811,43912,43812,43913,43813,43914,43814,43915,43815,43916,43816,43917,43817,43918,43818,43919,43819,43920);
    public static List<String> PINGPONG_SET_PUASE = Arrays.asList("301","302","303","304","305","306","100","999");
    public static List<String> VOLLEYBALL_SET_PUASE = Arrays.asList("301","302","303","304","305","306","100","999");


    @Autowired
    protected RedisClient redisClient;

    private static final List<String> eventList = Arrays.asList(new String[]{"goal","canceled_goal","match_status","coverage_status","time_start","timeout","timeout_over","uof","match_stop_suspension", "match_stop_suspension_over","tennis_score_change","ace","table_tennis_score_change","volleyball_score_change","ball_possession","snooker_score_change","time_startstop"});

    //-------------------------本地缓存------------------------------start
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    private Cache<String, String> matchTimeCache = Caffeine.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();
    //-------------------------本地缓存------------------------------end


    @Override
    public Long getLastCrtTime() {
        return standardMatchInfoMapper.getLastCrtTime();
    }

    @Override
    public int updateBatch(List<StandardMatchInfo> list) {
        return standardMatchInfoMapper.updateBatch(list);
    }

    @Override
    public int batchInsertOrUpdate(List<StandardMatchInfo> standardMatchInfos) {
    	if(CollectionUtils.isEmpty(standardMatchInfos)) {return 0;}
        return standardMatchInfoMapper.batchInsertOrUpdate(standardMatchInfos);
    }

    @Override
    public List<StandardMatchInfo> listByListIds(ArrayList<Long> matchInfoDataLongs) {
        return standardMatchInfoMapper.selectBatchIds(matchInfoDataLongs);
    }

    @Override
    public int updateOperateMatchStatus(StandardMatchMarketMessageDTO marketDTO) {
        try {
            if(marketDTO==null) {return 0;}
            if(marketDTO.getMarketList() == null || marketDTO.getMarketList().size() <= 0 ) {
                //将盘口状态转为赛事操盘状态
//                marketDTO.setStatus(marketDTO.getStatus());
                marketDTO.setStatus(MacthStatusEnum.getState(marketDTO.getStatus()));
                return standardMatchInfoMapper.updateOperateMatchStatus(marketDTO.getStandardMatchInfoId(),marketDTO.getStatus());
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return 0;
    }
    private void update(MatchEventInfoMessage data, String linkId){
        UpdateWrapper<StandardMatchInfo> standardMatchInfoUpdateWrapper = new UpdateWrapper<>();
        standardMatchInfoUpdateWrapper.lambda().eq(StandardMatchInfo::getId, data.getStandardMatchId());
        StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
        standardMatchInfo.setUpdateTime(DateUtils.getCurrentTime());
        standardMatchInfo.setEventCode(data.getEventCode());
        if(data.getEventTime()!=null){
            standardMatchInfo.setEventTime(data.getEventTime());
            standardMatchInfo.setMatchPeriodId(data.getMatchPeriodId());
        }
        boolean isSet = false;
        if(data.getSecondsFromStart() != null && data.getSecondsFromStart().intValue() > 0 ) {
            standardMatchInfo.setSecondsMatchStart(data.getSecondsFromStart().intValue());
            isSet = true;
        }
        //篮球特殊处理
        if(1==data.getSportId()) {
            if(("goal".equals(data.getEventCode()) && null!=data.getCanceled() &&1==data.getCanceled())){
                standardMatchInfo.setEventCode("canceled_goal");
            }
        } else if(2==data.getSportId()) {
            //如果缓存为timeout
            String eventFlag = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, BASKETBALL_TIME_CORRECTION, data.getStandardMatchId()));
            if(CommonUtil.isNotBlankAndNull(eventFlag)&&"timeout".equals(eventFlag)){
                log.info("::{}::篮球赛事timout-flag-1:{}",linkId+"_"+data.getStandardMatchId(),data.getStandardMatchId());
                isSet = false;
            }
            //如果篮球事件为 time_start  extraInfo 为 0 的话 为暂停 事件统一成 timeout,并缓存timeout
            if(("time_start".equals(data.getEventCode()) && "0".equals(data.getExtraInfo()))||"timeout".equals(data.getEventCode())){
                //time_start extraInfo 为 0 转成 timeout
                standardMatchInfo.setEventCode("timeout");
                redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, BASKETBALL_TIME_CORRECTION, data.getStandardMatchId()),"timeout", 600L);
                isSet = true;
            }
            //如果篮球事件为 time_start  extraInfo 为 1 的话 为开始  去掉缓存timeout
            if(("time_start".equals(data.getEventCode()) && "1".equals(data.getExtraInfo()))||"timeout_over".equals(data.getEventCode())){
                standardMatchInfo.setEventCode("timeout_over");
                redisClient.delete(String.format(RCS_DATA_KEY_CACHE_KEY, BASKETBALL_TIME_CORRECTION, data.getStandardMatchId()));
                isSet = true;
            }
        }
        //网球特殊处理
        if(5==data.getSportId()) {
            //如果缓存为timeout
            String eventFlag = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, TENNIS_TIME_CORRECTION, data.getStandardMatchId()));
            if(StringUtils.isNotBlank(eventFlag)&&"match_stop_suspension".equals(eventFlag)){
                log.info("::{}::网球赛事timout-flag-1:{}",linkId+"_"+data.getStandardMatchId(),data.getStandardMatchId());
                isSet = false;
            }
            //如果篮球事件为 time_start  extraInfo 为 0 的话 为暂停 事件统一成 timeout,并缓存timeout
            if("match_stop_suspension".equals(data.getEventCode())){
                //time_start extraInfo 为 0 转成 timeout
                standardMatchInfo.setEventCode("match_stop_suspension");
                redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, TENNIS_TIME_CORRECTION, data.getStandardMatchId()),"match_stop_suspension", 600L);
                isSet = true;
            }
            //如果篮球事件为 time_start  extraInfo 为 1 的话 为开始  去掉缓存timeout
            if("match_stop_suspension_over".equals(data.getEventCode()) ){
                standardMatchInfo.setEventCode("match_stop_suspension_over");
                redisClient.delete(String.format(RCS_DATA_KEY_CACHE_KEY, TENNIS_TIME_CORRECTION, data.getStandardMatchId()));
                isSet = true;
            }
        }
        if("match_status".equals(data.getEventCode()) && StringUtils.isNotBlank(data.getExtraInfo())) {
            standardMatchInfo.setMatchPeriodId(data.getMatchPeriodId());
            isSet = true;
        }
        if(SportIdEnum.SNOOKER.getId().intValue()==data.getSportId().intValue()) {
            //如果缓存为timeout
            String eventFlag = redisClient.get(String.format(RCS_DATA_KEY_CACHE_KEY, SNOOKER_TIME_CORRECTION, data.getStandardMatchId()));
            if(StringUtils.isNotBlank(eventFlag)&&"time_startstop_0".equals(eventFlag)){
                log.info("::{}::斯洛克赛事timout-flag-1:{}",linkId+"_"+data.getStandardMatchId(),data.getStandardMatchId());
                isSet = false;
            }
            //如果篮球事件为 time_start  extraInfo 为 0 的话 为暂停 事件统一成 timeout,并缓存timeout
            if(("time_startstop".equals(data.getEventCode()) && "0".equals(data.getExtraInfo()))){
                //time_start extraInfo 为 0 转成 timeout
                standardMatchInfo.setEventCode("time_startstop_0");
                redisClient.setExpiry(String.format(RCS_DATA_KEY_CACHE_KEY, SNOOKER_TIME_CORRECTION, data.getStandardMatchId()),"time_startstop_0", 600L);
                isSet = true;
            }
            //如果篮球事件为 time_start  extraInfo 为 1 的话 为开始  去掉缓存timeout
            if(("time_startstop".equals(data.getEventCode()) && "1".equals(data.getExtraInfo()))){
                standardMatchInfo.setEventCode("time_startstop_1");
                redisClient.delete(String.format(RCS_DATA_KEY_CACHE_KEY, SNOOKER_TIME_CORRECTION, data.getStandardMatchId()));
                isSet = true;
            }
            standardMatchInfo.setSetNum(data.getFirstNum());
            isSet = true;
        }
        //冰球特殊处理
        if(SportIdEnum.ICE_HOCKEY.getId().intValue()==data.getSportId().intValue()){
            if( null != data.getPeriodRemainingSeconds()) {
                standardMatchInfo.setSecondsMatchStart(data.getPeriodRemainingSeconds().intValue());
                standardMatchInfo.setMatchPeriodId(data.getMatchPeriodId());
                isSet = true;
            }
        }
        if(999==data.getMatchPeriodId().intValue()){
            standardMatchInfo.setEndTime(System.currentTimeMillis());
            isSet = true;
        }
        if(isSet) {standardMatchInfoMapper.update(standardMatchInfo, standardMatchInfoUpdateWrapper);}
     }

    /**
     *
     * @param data
     * @param channel  1:事件 2：统计uof
     * @param linkId
     * @return
     */
    @Override
    public int updateMatchEventParam(MatchEventInfoMessage data, int channel, String linkId) {
        try {
            if(null==data){return 0;}
            if (CommonUtil.isBlankOrNull(matchTimeCache.getIfPresent(String.valueOf(data.getStandardMatchId()))) && !eventList.contains(data.getEventCode())&&!passPeriod.contains(data.getMatchPeriodId())) {
                matchTimeCache.put(String.valueOf(data.getStandardMatchId()),"1");
                update(data,linkId);
            }

            String key1 = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, data.getStandardMatchId());
            //网球局暂停
            boolean periodJuge = false;
            periodJuge = TETC.contains(data.getSportId().intValue())&&null!=data.getMatchPeriodId()&&passPeriod.contains(data.getMatchPeriodId().intValue());
            if (eventList.contains(data.getEventCode())||periodJuge||channel == 1) {
                update(data,linkId);
                redisClient.hSet(key1,"period",String.valueOf(data.getMatchPeriodId()));
                redisClient.hSet(key1,"lastEventInTime",String.valueOf(System.currentTimeMillis()));
                if(data.getMatchPeriodId().intValue()==445){
                    redisClient.hSet(key1,"time445",String.valueOf(System.currentTimeMillis()));
                }
            }
            //网球等判断
            if(TETC.contains(data.getSportId().intValue())){
                Integer setNum=0;
                if(SportIdEnum.SNOOKER.getId().intValue()==data.getSportId().intValue()){
                    setNum= data.getFirstNum();
                }else{
                    setNum= data.getSecondNum();
                }
                if(null!=setNum){
                    String rs = redisClient.hGet(key1, WORD_SECOND_NUM);
                    if(StringUtils.isNotBlank(rs)&&(!rs.equals("null"))){
                        if(!rs.equals(setNum)){
                            redisClient.hSet(key1,WORD_SECOND_NUM,String.valueOf(setNum));
                            log.info("::{}::Tennis,matchId{},period{},secondNum{}",linkId+"_"+data.getStandardMatchId(),data.getStandardMatchId(),data.getMatchPeriodId(),setNum);
                        }
                    }else{
                        redisClient.hSet(key1,WORD_SECOND_NUM,String.valueOf(setNum));
                        log.info("::{}::Tennis,matchId{},period{},secondNum{}",linkId+"_"+data.getStandardMatchId(),data.getStandardMatchId(),data.getMatchPeriodId(),setNum);
                    }
                }
                redisClient.expireKey(key1,2*24*60*60);
            }
        } catch (Exception e) {
            log.error("::{}::赛事更新事件内容参数错误{},{},{}",linkId, JsonFormatUtils.toJson(data) ,e.getMessage(),e);
        }
        return 0;
    }

    @Override
    public int updateMatchStatus(StandardMatchInfo data, String linkid) {
        try {
            UpdateWrapper<StandardMatchInfo> standardMatchInfoUpdateWrapper = new UpdateWrapper<>();
            standardMatchInfoUpdateWrapper.lambda().eq(StandardMatchInfo::getId,data.getId());
            if(data.getMatchStatus().intValue()==0){
                StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectById(data.getId());
                if(null==standardMatchInfo){return 0;}
                if(standardMatchInfo.getOddsLive()==1 && standardMatchInfo.getMatchStatus()==1 ){
                    return 0;
                }
            }
            return standardMatchInfoMapper.update(data,standardMatchInfoUpdateWrapper);
        } catch (Exception e) {
            log.error("::{}::更新事状态错误{},{},{}",linkid,JsonFormatUtils.toJson(data),e.getMessage(),e);
        }
        return 0;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateMatchMauManualRevStatus(List<Long> matchIds) {

        // 3=结束 4=关闭 和所有非滚球状态
        List<Integer> matchStatusParam = CollUtil.newCopyOnWriteArrayList(RcsConstant.LIVE_MATCH_STATUS);
        matchStatusParam.add(3);
        matchStatusParam.add(4);
        //1.先将所有早盘赛事更新为不支持手动rev
        LambdaUpdateWrapper<StandardMatchInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.notIn(StandardMatchInfo::getMatchStatus, matchStatusParam);
        wrapper.eq(StandardMatchInfo::getSportId, SportIdEnum.FOOTBALL.getId());
        StandardMatchInfo notManualRevParam = new StandardMatchInfo();
        notManualRevParam.setManualRev(0);
        this.update(notManualRevParam, wrapper);

        //2.将指定赛事更新为支持rev
        List<List<Long>> splitList = CollUtil.split(matchIds, 500);
        StandardMatchInfo manualRevParam = new StandardMatchInfo();
        manualRevParam.setManualRev(1);
        for(List<Long> list : splitList){
            LambdaUpdateWrapper<StandardMatchInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(StandardMatchInfo::getId, list);
            updateWrapper.eq(StandardMatchInfo::getSportId, SportIdEnum.FOOTBALL.getId());
            this.update(manualRevParam, updateWrapper);
        }

    }

    public static void main(String[] args) {
        String s = DateUtils.transferLongToDateStrings(1603519728287L);
        System.out.println(s);
    }
}
