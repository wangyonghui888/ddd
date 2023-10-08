package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessLimitLogMapper;
import com.panda.sport.rcs.mapper.RcsQuotaMerchantSingleFieldLimitMapper;
import com.panda.sport.rcs.mapper.RcsSysUserMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.constant.LimitRedisKeys;
import com.panda.sport.rcs.mgr.enums.BusinessLimitLogTypeEnum;
import com.panda.sport.rcs.mgr.enums.DataTypeEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaMerchantSingleFieldLimitEnum;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.vo.LimitCacheClearVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaMerchantSingleFieldLimitVo;
import com.panda.sport.rcs.utils.TradeUserUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-04 17:39
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsQuotaMerchantSingleFieldLimitServiceImpl extends ServiceImpl<RcsQuotaMerchantSingleFieldLimitMapper, RcsQuotaMerchantSingleFieldLimit> implements RcsQuotaMerchantSingleFieldLimitService {
    @Autowired
    private RcsQuotaMerchantSingleFieldLimitMapper rcsQuotaMerchantSingleFieldLimitMapper;
    @Autowired
    private ProducerSendMessageUtils sendMessage;
    @Autowired
    RcsQuotaBusinessLimitLogMapper rcsQuotaBusinessLimitLogMapper;
    @Autowired
    RcsSysUserMapper rcsSysUserMapper;

    @Autowired
    JedisCluster jedisCluster;


    @Override
    @Transactional
    public HttpResponse<List<RcsQuotaMerchantSingleFieldLimit>> getList(Integer sportId) {
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList = rcsQuotaMerchantSingleFieldLimitMapper.selectRcsQuotaMerchantSingleFieldLimit(sportId);
        if (CollectionUtils.isEmpty(rcsQuotaMerchantSingleFieldLimitList)) {
            //初始化并且查询
            rcsQuotaMerchantSingleFieldLimitList = initRcsQuotaMerchantSingleFieldLimit(sportId);
            if (CollectionUtils.isEmpty(rcsQuotaMerchantSingleFieldLimitList)) {
                log.error("RcsQuotaMerchantSingleFieldLimit数据初始化失败");
                return HttpResponse.error(-1, "数据初始化失败");
            }
        }
        RcsQuotaMerchantSingleFieldLimit remove = rcsQuotaMerchantSingleFieldLimitList.remove(0);
        rcsQuotaMerchantSingleFieldLimitList.add(remove);
        return HttpResponse.success(rcsQuotaMerchantSingleFieldLimitList);
    }

    /**
     * @return void
     * @Description 初始化商户单场限额数据
     * @Param [sportId]
     * @Author kimi
     * @Date 2020/9/6
     **/
    public List<RcsQuotaMerchantSingleFieldLimit> initRcsQuotaMerchantSingleFieldLimit(Integer sportId) {
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList = new ArrayList<>();
        for (RcsQuotaMerchantSingleFieldLimitEnum rcsQuotaMerchantSingleFieldLimitEnum : RcsQuotaMerchantSingleFieldLimitEnum.values()) {
            RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimit = new RcsQuotaMerchantSingleFieldLimit();
            rcsQuotaMerchantSingleFieldLimit.setSportId(sportId);
            rcsQuotaMerchantSingleFieldLimit.setTemplateLevel(rcsQuotaMerchantSingleFieldLimitEnum.getLevel());
            rcsQuotaMerchantSingleFieldLimit.setCompensationLimitBase(Constants.QUOTA_BASE);
            rcsQuotaMerchantSingleFieldLimit.setEarlyMorningPaymentLimitRatio(new BigDecimal(rcsQuotaMerchantSingleFieldLimitEnum.getEarlyTradingRatio()));
            rcsQuotaMerchantSingleFieldLimit.setEarlyMorningPaymentLimit((long) (rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio().doubleValue() * Constants.QUOTA_BASE));
            rcsQuotaMerchantSingleFieldLimit.setLiveBallPayoutLimitRatio(new BigDecimal(rcsQuotaMerchantSingleFieldLimitEnum.getRollingRatio()));
            rcsQuotaMerchantSingleFieldLimit.setLiveBallPayoutLimit((long) (rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio().doubleValue() * Constants.QUOTA_BASE));
            rcsQuotaMerchantSingleFieldLimit.setStatus(1);
            rcsQuotaMerchantSingleFieldLimitList.add(rcsQuotaMerchantSingleFieldLimit);
        }
        saveBatch(rcsQuotaMerchantSingleFieldLimitList);
        return rcsQuotaMerchantSingleFieldLimitList;
    }

    @Override
    @Transactional
    public List<RcsQuotaMerchantSingleFieldLimit> fieldLimitUpdate(RcsQuotaMerchantSingleFieldLimitVo rcsQuotaMerchantSingleFieldLimitVo) {
        Long compensationLimitBase = rcsQuotaMerchantSingleFieldLimitVo.getCompensationLimitBase();
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList = rcsQuotaMerchantSingleFieldLimitVo.getRcsQuotaMerchantSingleFieldLimitList();
        Map<String, Object> columnMap = new HashMap<>();
        Integer sportId = rcsQuotaMerchantSingleFieldLimitList.get(0).getSportId();
        columnMap.put("sport_id", sportId);
        Map<Long, RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitMap = new HashMap<>();
        for (RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimit : rcsQuotaMerchantSingleFieldLimitList) {
            rcsQuotaMerchantSingleFieldLimitMap.put(rcsQuotaMerchantSingleFieldLimit.getId(), rcsQuotaMerchantSingleFieldLimit);
        }
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitListData = rcsQuotaMerchantSingleFieldLimitMapper.selectByMap(columnMap);
        for (RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimitdData : rcsQuotaMerchantSingleFieldLimitListData) {
            rcsQuotaMerchantSingleFieldLimitdData.setCompensationLimitBase(compensationLimitBase);
            if (rcsQuotaMerchantSingleFieldLimitMap.containsKey(rcsQuotaMerchantSingleFieldLimitdData.getId())) {
                RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimit = rcsQuotaMerchantSingleFieldLimitMap.get(rcsQuotaMerchantSingleFieldLimitdData.getId());
                if (rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio() != null) {
                    rcsQuotaMerchantSingleFieldLimitdData.setEarlyMorningPaymentLimitRatio(rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio());
                }
                if (rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio() != null) {
                    rcsQuotaMerchantSingleFieldLimitdData.setLiveBallPayoutLimitRatio(rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio());
                }
            }
            //计算出具体的限额值
            rcsQuotaMerchantSingleFieldLimitdData.setEarlyMorningPaymentLimit((long) (compensationLimitBase * rcsQuotaMerchantSingleFieldLimitdData.getEarlyMorningPaymentLimitRatio().doubleValue()));
            rcsQuotaMerchantSingleFieldLimitdData.setLiveBallPayoutLimit((long) (compensationLimitBase * rcsQuotaMerchantSingleFieldLimitdData.getLiveBallPayoutLimitRatio().doubleValue()));
            //添加操作IP
            rcsQuotaMerchantSingleFieldLimitdData.setIp(rcsQuotaMerchantSingleFieldLimitVo.getIp());
        }
        //记录修改参数日志
        addQuotaMerchantSingleFieldLimitLog(rcsQuotaMerchantSingleFieldLimitListData, sportId);
        updateBatchById(rcsQuotaMerchantSingleFieldLimitListData);

        //更新所有联赛等级的数据 因为后台是基于基础金额全改的 (21条数据)
        String earlyLimitKey;
        String liveLimitKey;
        JSONArray jsonArray = new JSONArray();
        for (RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimitdData : rcsQuotaMerchantSingleFieldLimitListData) {
            //重置用户单场缓存数据
            String sport_Id = rcsQuotaMerchantSingleFieldLimitdData.getSportId().toString();
            Integer templateLevel = rcsQuotaMerchantSingleFieldLimitdData.getTemplateLevel();
            earlyLimitKey = LimitRedisKeys.getCommonMerchantSingleLimitKey(sport_Id, templateLevel, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, "0");
            liveLimitKey = LimitRedisKeys.getCommonMerchantSingleLimitKey(sport_Id, templateLevel, LimitDataTypeEnum.MERCHANT_SINGLE_LIMIT, "1");
            jedisCluster.setex(earlyLimitKey, 30 * 24 * 60 * 60, String.valueOf(rcsQuotaMerchantSingleFieldLimitdData.getEarlyMorningPaymentLimit() * 100));
            jedisCluster.setex(liveLimitKey, 30 * 24 * 60 * 60, String.valueOf(rcsQuotaMerchantSingleFieldLimitdData.getLiveBallPayoutLimit() * 100));
            //把刷新的key统计去刷新对应的本地缓存
            JSONObject earlyMap = new JSONObject();
            earlyMap.put("key", earlyLimitKey);
            earlyMap.put("type", "merchant_single_limit");
            JSONObject liveMap = new JSONObject();
            liveMap.put("key", liveLimitKey);
            liveMap.put("type", "merchant_single_limit");
            jsonArray.add(earlyMap);
            jsonArray.add(liveMap);
        }
        //将修改的信息同步一个mq广播 清除本地缓存
        sendMessage.sendMessage("rcs_local_cache_clear_sdk,,merchant_single_limit", JSON.toJSONString(jsonArray));
        log.info("清理商户单场通用限额缓存完成，sportId:{}，keys:{}，", sportId, jsonArray.toJSONString());
        return rcsQuotaMerchantSingleFieldLimitListData;
    }

    /**
     * 记录日志
     *
     * @param newList
     * @param sportId
     */
    private void addQuotaMerchantSingleFieldLimitLog(List<RcsQuotaMerchantSingleFieldLimit> newList, long sportId) {
        String operateType = BusinessLimitLogTypeEnum.getValue(2);
        String sportName;
        if (sportId == -1) {
            sportName = "其他";
        } else {
            sportName = SportIdEnum.getNameById(sportId);
        }

        for (int i = 0; i < newList.size(); i++) {
            RcsQuotaMerchantSingleFieldLimit newData = newList.get(i);
            RcsQuotaMerchantSingleFieldLimit oldData = getById(newData.getId());
            if (oldData != null) {
                String paramName = "";
                String levelName = "";
                if (oldData.getTemplateLevel() == -1) {
                    levelName = "未评级";
                } else {
                    levelName = oldData.getTemplateLevel() + "级联赛";
                }
                //默认值，记录一次日志
                if (i == 0 && newData.getCompensationLimitBase().longValue() != oldData.getCompensationLimitBase().longValue()) {
                    paramName = operateType + "-" + sportName + "-默认值";
                    String afterVal = newData.getCompensationLimitBase() + "";
                    String beforeVal = oldData.getCompensationLimitBase() + "";
                    //insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
                //早盘赔付限额
                if (newData.getEarlyMorningPaymentLimitRatio().compareTo(oldData.getEarlyMorningPaymentLimitRatio()) != 0) {
                    paramName = operateType + "-早盘-" + sportName + "-" + levelName;
                    String afterVal = newData.getEarlyMorningPaymentLimit() + "";
                    String beforeVal = oldData.getEarlyMorningPaymentLimit() + "";
                    //insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }
                //滚球赔付限额
                if (newData.getLiveBallPayoutLimitRatio().compareTo(oldData.getLiveBallPayoutLimitRatio()) != 0) {
                    paramName = operateType + "-滚球-" + sportName + "-" + levelName;
                    String afterVal = newData.getLiveBallPayoutLimit() + "";
                    String beforeVal = oldData.getLiveBallPayoutLimit() + "";
                    //insertBusinessLimitLog(paramName, operateType, beforeVal, afterVal);
                    insertBusinessLimitLogIP(paramName, operateType, beforeVal, afterVal,newData.getIp());
                }

            }
        }
    }

    @Override
    public void insertBusinessLimitLog(String paramName, String operateType, String beforeVal, String afterVal) {
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
        RcsSysUser user = null;
        try {
            user = rcsSysUserMapper.selectById(TradeUserUtils.getUserId());
        } catch (Exception e) {
            log.error("当前用户不存在{}",e.getMessage(), e);
            throw new RuntimeException("当前用户不存在");
        }
        limitLoglog.setUserId(user.getId().toString());
        limitLoglog.setUserName(user.getUserCode());
        rcsQuotaBusinessLimitLogMapper.insert(limitLoglog);
    }

    @Override
    public void insertBusinessLimitLogIP(String paramName, String operateType, String beforeVal, String afterVal, String ip) {
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
        limitLoglog.setIp(ip);
        RcsSysUser user = null;
        try {
            user = rcsSysUserMapper.selectById(TradeUserUtils.getUserId());
        } catch (Exception e) {
            log.error("插入商户风控管理：{}",e.getMessage(), e);
            throw new RuntimeException("当前用户不存在");
        }
        limitLoglog.setUserId(user.getId().toString());
        limitLoglog.setUserName(user.getUserCode());
        rcsQuotaBusinessLimitLogMapper.insert(limitLoglog);
    }

}
