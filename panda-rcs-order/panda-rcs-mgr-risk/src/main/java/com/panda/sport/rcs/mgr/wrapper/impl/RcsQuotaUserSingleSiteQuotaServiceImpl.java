package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsQuotaUserSingleSiteQuotaMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.constant.LimitRedisKeys;
import com.panda.sport.rcs.mgr.enums.BusinessLimitLogTypeEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaUserSingleSiteQuotaEnum;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserSingleSiteQuotaService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota;
import com.panda.sport.rcs.pojo.vo.RcsQuotaUserSingleSiteQuotaVo;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-06 11:23
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsQuotaUserSingleSiteQuotaServiceImpl extends ServiceImpl<RcsQuotaUserSingleSiteQuotaMapper, RcsQuotaUserSingleSiteQuota> implements RcsQuotaUserSingleSiteQuotaService {
    @Autowired
    private RcsQuotaUserSingleSiteQuotaMapper rcsQuotaUserSingleSiteQuotaMapper;
    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Autowired
    private RcsQuotaMerchantSingleFieldLimitService rcsQuotaMerchantSingleFieldLimitService;

    @Autowired
    JedisCluster jedisCluster;


    @Override
    @Transactional
    public HttpResponse<List<RcsQuotaUserSingleSiteQuota>> getList(Integer sportId) {
        List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaList = rcsQuotaUserSingleSiteQuotaMapper.selectRcsQuotaUserSingleSiteQuota(sportId);
        if (CollectionUtils.isEmpty(rcsQuotaUserSingleSiteQuotaList)) {
            rcsQuotaUserSingleSiteQuotaList = initRcsQuotaUserSingleSiteQuota(sportId);
            if (CollectionUtils.isEmpty(rcsQuotaUserSingleSiteQuotaList)) {
                log.error("RcsQuotaUserSingleSiteQuota数据初始化失败");
                return HttpResponse.error(-1, "数据初始化失败");
            }
        }
        RcsQuotaUserSingleSiteQuota remove = rcsQuotaUserSingleSiteQuotaList.remove(0);
        rcsQuotaUserSingleSiteQuotaList.add(remove);
        return HttpResponse.success(rcsQuotaUserSingleSiteQuotaList);
    }


    /**
     * @return void
     * @Description 初始化用户单场限额数据
     * @Param [sportId]
     * @Author kimi
     * @Date 2020/9/6
     **/
    private List<RcsQuotaUserSingleSiteQuota> initRcsQuotaUserSingleSiteQuota(Integer sportId) {
        //先初始化用户单场限额数据
        List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaList = new ArrayList<>();
        for (RcsQuotaUserSingleSiteQuotaEnum rcsQuotaUserSingleSiteQuotaEnum : RcsQuotaUserSingleSiteQuotaEnum.values()) {
            RcsQuotaUserSingleSiteQuota rcsQuotaUserSingleSiteQuota = new RcsQuotaUserSingleSiteQuota();
            rcsQuotaUserSingleSiteQuota.setSportId(sportId);
            rcsQuotaUserSingleSiteQuota.setTemplateLevel(rcsQuotaUserSingleSiteQuotaEnum.getLevel());
            rcsQuotaUserSingleSiteQuota.setUserSingleSiteQuotaBase(Constants.QUOTA_BASE);
            rcsQuotaUserSingleSiteQuota.setEarlyUserSingleSiteQuotaProportion(new BigDecimal(rcsQuotaUserSingleSiteQuotaEnum.getEarlyTradingRatio()));
            rcsQuotaUserSingleSiteQuota.setEarlyUserSingleSiteQuota(rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion().multiply(new BigDecimal(Constants.QUOTA_BASE)));
            rcsQuotaUserSingleSiteQuota.setLiveUserSingleSiteQuotaProportion(new BigDecimal(rcsQuotaUserSingleSiteQuotaEnum.getRollingRatio()));
            rcsQuotaUserSingleSiteQuota.setLiveUserSingleSiteQuota(new BigDecimal(Constants.QUOTA_BASE).multiply(rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion()));
            rcsQuotaUserSingleSiteQuota.setStatus(1);
            rcsQuotaUserSingleSiteQuotaList.add(rcsQuotaUserSingleSiteQuota);
        }
        saveBatch(rcsQuotaUserSingleSiteQuotaList);
        return rcsQuotaUserSingleSiteQuotaList;
    }

    @Override
    @Transactional
    public int singleSiteQuotaUpdate(RcsQuotaUserSingleSiteQuotaVo rcsQuotaUserSingleSiteQuotaVo) {
        List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaList = rcsQuotaUserSingleSiteQuotaVo.getRcsQuotaUserSingleSiteQuotaList();
        Map<String, Object> columnMap = new HashMap<>();
        Integer sportId = rcsQuotaUserSingleSiteQuotaList.get(0).getSportId();
        columnMap.put("sport_id", sportId);
        Map<Long, RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaHashMap = new HashMap<>();
        for (RcsQuotaUserSingleSiteQuota rcsQuotaUserSingleSiteQuota : rcsQuotaUserSingleSiteQuotaList) {
            rcsQuotaUserSingleSiteQuotaHashMap.put(rcsQuotaUserSingleSiteQuota.getId(), rcsQuotaUserSingleSiteQuota);
        }
        Long userSingleSiteQuotaBase = rcsQuotaUserSingleSiteQuotaVo.getUserSingleSiteQuotaBase();
        //获取到原数据
        List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaListData = rcsQuotaUserSingleSiteQuotaMapper.selectByMap(columnMap);
        for (RcsQuotaUserSingleSiteQuota RcsQuotaUserSingleSiteQuotaData : rcsQuotaUserSingleSiteQuotaListData) {
            RcsQuotaUserSingleSiteQuotaData.setUserSingleSiteQuotaBase(userSingleSiteQuotaBase);
            //与参数匹配
            if (rcsQuotaUserSingleSiteQuotaHashMap.containsKey(RcsQuotaUserSingleSiteQuotaData.getId())) {
                RcsQuotaUserSingleSiteQuota rcsQuotaUserSingleSiteQuota = rcsQuotaUserSingleSiteQuotaHashMap.get(RcsQuotaUserSingleSiteQuotaData.getId());
                if (rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion() != null) {
                    RcsQuotaUserSingleSiteQuotaData.setEarlyUserSingleSiteQuotaProportion(rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion());
                }
                if (rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion() != null) {
                    RcsQuotaUserSingleSiteQuotaData.setLiveUserSingleSiteQuotaProportion(rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion());
                }
            }
            RcsQuotaUserSingleSiteQuotaData.setEarlyUserSingleSiteQuota((new BigDecimal(userSingleSiteQuotaBase)).multiply(RcsQuotaUserSingleSiteQuotaData.getEarlyUserSingleSiteQuotaProportion()));
            RcsQuotaUserSingleSiteQuotaData.setLiveUserSingleSiteQuota((new BigDecimal(userSingleSiteQuotaBase)).multiply(RcsQuotaUserSingleSiteQuotaData.getLiveUserSingleSiteQuotaProportion()));

            //添加操作日志
            RcsQuotaUserSingleSiteQuotaData.setIp(rcsQuotaUserSingleSiteQuotaVo.getIp());
        }
        //记录修改参数日志
        addUserSingleSiteQuotaLog(rcsQuotaUserSingleSiteQuotaListData, sportId);
        updateBatchById(rcsQuotaUserSingleSiteQuotaListData);
        //用户单场限额缓存刷新

        //更新所有联赛等级的数据 因为后台是基于基础金额全改的 (21条数据)
        String earlyLimitKey;
        String liveLimitKey;
        List<String> keys = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        for (RcsQuotaUserSingleSiteQuota RcsQuotaUserSingleSiteQuotaData : rcsQuotaUserSingleSiteQuotaListData) {
            //重置用户单场缓存数据
            String sport_Id = RcsQuotaUserSingleSiteQuotaData.getSportId().toString();
            Integer templateLevel = RcsQuotaUserSingleSiteQuotaData.getTemplateLevel();
            earlyLimitKey = LimitRedisKeys.getCommonSingleLimitKey(sport_Id, templateLevel, LimitDataTypeEnum.USER_SINGLE_LIMIT, "0");
            liveLimitKey = LimitRedisKeys.getCommonSingleLimitKey(sport_Id, templateLevel, LimitDataTypeEnum.USER_SINGLE_LIMIT, "1");
            jedisCluster.setex(earlyLimitKey, 30 * 24 * 60 * 60, RcsQuotaUserSingleSiteQuotaData.getEarlyUserSingleSiteQuota().multiply(new BigDecimal("100")).toPlainString());
            jedisCluster.setex(liveLimitKey, 30 * 24 * 60 * 60, RcsQuotaUserSingleSiteQuotaData.getLiveUserSingleSiteQuota().multiply(new BigDecimal("100")).toPlainString());
            //把刷新的key统计去刷新对应的本地缓存
            JSONObject earlyMap = new JSONObject();
            earlyMap.put("key", earlyLimitKey);
            earlyMap.put("type", "user_single_limit");
            jsonArray.add(earlyMap);
            JSONObject liveMap = new JSONObject();
            liveMap.put("key", liveLimitKey);
            liveMap.put("type", "user_single_limit");
            jsonArray.add(liveMap);
        }
        //将修改的信息同步一个mq广播 清除本地缓存
        sendMessage.sendMessage("rcs_local_cache_clear_sdk,,user_single_limit", JSON.toJSONString(jsonArray));
        log.info("清理用户单场通用限额缓存完成，sportId:{}，keys:{}，", sportId, jsonArray.toJSONString());
        return sportId;
    }

    /**
     * 记录日志
     *
     * @param newList
     * @param sportId
     */
    private void addUserSingleSiteQuotaLog(List<RcsQuotaUserSingleSiteQuota> newList, long sportId) {
        String operateType = BusinessLimitLogTypeEnum.getValue(4);
        String sportName;
        if (sportId == -1) {
            sportName = "其他";
        } else {
            sportName = SportIdEnum.getNameById(sportId);
        }

        for (int i = 0; i < newList.size(); i++) {
            RcsQuotaUserSingleSiteQuota newData = newList.get(i);
            RcsQuotaUserSingleSiteQuota oldData = getById(newData.getId());
            if (oldData != null) {
                String paramName = "";
                String levelName = "";
                if (oldData.getTemplateLevel() == -1) {
                    levelName = "未评级";
                } else {
                    levelName = oldData.getTemplateLevel() + "级联赛";
                }
                ////默认值，记录一次日志
                if (i == 0 && newData.getUserSingleSiteQuotaBase().longValue() != oldData.getUserSingleSiteQuotaBase().longValue()) {
                    paramName = operateType + "-" + sportName + "-默认值";
                    String afterVal = newData.getUserSingleSiteQuotaBase() + "";
                    String beforeVal = oldData.getUserSingleSiteQuotaBase() + "";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
                //早盘赔付限额
                if (newData.getEarlyUserSingleSiteQuotaProportion().compareTo(oldData.getEarlyUserSingleSiteQuotaProportion()) != 0) {
                    paramName = operateType + "-早盘-" + sportName + "-" + levelName;
                    String afterVal = newData.getEarlyUserSingleSiteQuota().longValue() + "";
                    String beforeVal = oldData.getEarlyUserSingleSiteQuota().longValue() + "";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
                //滚球赔付限额
                if (newData.getLiveUserSingleSiteQuotaProportion().compareTo(oldData.getLiveUserSingleSiteQuotaProportion()) != 0) {
                    paramName = operateType + "-滚球-" + sportName + "-" + levelName;
                    String afterVal = newData.getLiveUserSingleSiteQuota().longValue() + "";
                    String beforeVal = oldData.getLiveUserSingleSiteQuota().longValue() + "";
                    //rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    rcsQuotaMerchantSingleFieldLimitService.insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
            }
        }
    }
}
