package com.panda.sport.rcs.predict.service.impl.football;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mapper.RcsMatrixInfoMapper;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.pojo.RcsMatrixInfo;
import com.panda.sport.rcs.pojo.RcsOperateMerchantsSet;
import com.panda.sport.rcs.predict.service.FootballMatrixService;
import com.panda.sport.rcs.predict.utils.RcsPredictMysqlFrequencyNacosConfig;
import com.panda.sport.rcs.predict.utils.RedisUtilsNxExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * kir
 * 足球矩阵入库
 */
@Slf4j
@Service("footballMatrixService")
public class FootballMatrixServiceImpl implements FootballMatrixService {

    @Autowired
    RcsMatrixInfoMapper matrixInfoMapper;


    @Autowired
    TOrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisUtilsNxExtend redisUtilsNxExtend;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsPredictMysqlFrequencyNacosConfig rcsPredictMysqlFrequencyNacosConfig;

    private static String RCS_FOOTBALL_MATRIX_MYSQL_FREQUENCY_INSERT_INSERT_KEY = "rcs_football_matrix_mysql_frequency_insert.match_id.%s.matrix_type.%s.play_type.%s.business_type.%s.match_type.%s.early_settlement_type.%s";

    private static String RCS_FOOTBALL_MATRIX_MYSQL_INSERT_INSERT_KEY = "rcs_football_matrix_mysql_insert.match_id.%s.matrix_type.%s.play_type.%s.business_type.%s.match_type.%s.early_settlement_type.%s";

    private static String RCS_OPERATE_MERCHANTSSET_LIMIT_TYPE = "rcs_operate_merchantsset_limit_type.tenantId.%s";
    @Override
    public void footballMatrixData(OrderItem item, Integer type, Long tenantId) {
        RcsMatrixInfo info = new RcsMatrixInfo();
        info.setMatchId(item.getMatchId());
        //货量百分比 volumePercentage
        BigDecimal volumePercentage = item.getVolumePercentage();
        if (volumePercentage == null) {
            volumePercentage = BigDecimal.valueOf(1);
        }
        log.info("订单:{}:货量百分比{}", item.getOrderNo(), item.getVolumePercentage());
        //获取商户限额类型（1标准限额模式 2信用限额模式）
        String limitType = redisClient.get(String.format(RCS_OPERATE_MERCHANTSSET_LIMIT_TYPE, tenantId));
        if (StringUtils.isEmpty(limitType)) {
            RcsOperateMerchantsSet operateMerchantsSet = matrixInfoMapper.getOperateMerchantsSet(String.valueOf(tenantId));
            redisClient.setExpiry(String.format(RCS_OPERATE_MERCHANTSSET_LIMIT_TYPE, tenantId), operateMerchantsSet.getLimitType().toString(), 24 * 60 * 60L);
            limitType = operateMerchantsSet.getLimitType().toString();
        }
        info.setBusinessType(Integer.parseInt(limitType));
        info.setMatchType(item.getMatchType());

        //根据玩法id判断矩阵类型和玩法类型
        String matrixTypeAndPlayType = getMatrixTypeAndPlayType(item);
        String[] matrixTypeAndPlayTypes = matrixTypeAndPlayType.split(":");
        String format = String.format(RCS_FOOTBALL_MATRIX_MYSQL_FREQUENCY_INSERT_INSERT_KEY, item.getMatchId(), matrixTypeAndPlayTypes[0], matrixTypeAndPlayTypes[1], info.getBusinessType(), info.getMatchType(), 0);
        log.info("订单为 {},矩阵入库时玩法id为{},相对应的矩阵类型和玩法类型是{}:{}", item.getOrderNo(), item.getPlayId(), matrixTypeAndPlayTypes[0], matrixTypeAndPlayTypes[1]);
        if (matrixTypeAndPlayType.equals("0:0")) {
            log.info("当前订单 {} 玩法id为{},不支持矩阵,跳过该步骤", item.getOrderNo(), item.getPlayId());
        } else {
            info.setMatrixType(Integer.parseInt(matrixTypeAndPlayTypes[0]));
            info.setPlayType(Integer.parseInt(matrixTypeAndPlayTypes[1]));
            //根据以下五个条件判断是第一次插入还是累加，然后将当前订单的值乘以type再做计算
            QueryWrapper<RcsMatrixInfo> queryWrapper = new QueryWrapper<>();
            String md5Hex = DigestUtil.md5Hex(format);
            queryWrapper.eq("hash_unique", md5Hex);
            RcsMatrixInfo matrixInfo = matrixInfoMapper.selectOne(queryWrapper);
            int homeSize = 13;
            int awaySize = 13;

            if (item.getRecVal() == null || item.getRecVal().equals("null") || item.getRecVal().equals("") || item.getRecType() == null || item.getRecType() != 0) {
                log.info("矩阵无效-当前订单{}的矩阵为{},订单详情为:{}:{}", item.getOrderNo(), item.getRecVal(), JSONObject.toJSONString(item), item.getRecType());
                return;
            }

            if (ObjectUtils.isEmpty(matrixInfo)) {
                BigDecimal[][] arr = new BigDecimal[homeSize][awaySize];
                try {
                    arr = JSON.parseObject(item.getRecVal(), BigDecimal[][].class);
                    for (int i = 0; i < homeSize; i++) {
                        for (int j = 0; j < awaySize; j++) {
                            //做运算的时候乘以type值（type值是1或者-1，来判断订单是否取消）
                            arr[i][j] = Optional.ofNullable(arr[i][j]).orElse(BigDecimal.ZERO).multiply(volumePercentage);
                        }
                    }
                } catch (Exception e) {
                    log.error("requestId:{},当前注单存入矩阵数据有误!：{}", LogContext.getContext().getRequestId(), e);
                    log.error("requestId:{},当前注单存入矩阵数据有误, betNo:{},detail:{}", LogContext.getContext().getRequestId(), item.getBetNo(), JSONObject.toJSONString(item));
                }
                info.setRecVal(JSONObject.toJSONString(arr));
                info.setEarlySettlementType(0);
                info.setHashUnique(md5Hex);
                try {
                    log.info("新增矩阵数据,订单为{},参数为:{}", item.getOrderNo(), JSONObject.toJSONString(info));
                    matrixInfoMapper.insert(info);
                    redisUtilsNxExtend.setNX(format, "1", rcsPredictMysqlFrequencyNacosConfig.getForecastInsertMysqlFrequency());
                    redisClient.setExpiry(String.format(RCS_FOOTBALL_MATRIX_MYSQL_INSERT_INSERT_KEY, item.getMatchId(),
                            matrixTypeAndPlayTypes[0], matrixTypeAndPlayTypes[1], info.getBusinessType(), info.getMatchType(), 0), JSONObject.toJSONString(info), 3 * 30 * 24 * 60 * 60L);
                }catch (Exception e){
                    log.error("矩阵信息新增异常{}", e);
                }
            }else {
                //如果不为空则累加,将查出来的id值设置到新的对象中去
                info.setId(matrixInfo.getId());
                String redisKey = String.format(RCS_FOOTBALL_MATRIX_MYSQL_INSERT_INSERT_KEY, item.getMatchId(),
                        matrixTypeAndPlayTypes[0], matrixTypeAndPlayTypes[1], info.getBusinessType(), info.getMatchType(), 0);
                String str = redisClient.get(redisKey);
                BigDecimal[][] result = JSON.parseObject(matrixInfo.getRecVal(), BigDecimal[][].class);
                if (!StringUtils.isEmpty(str)) {
                    RcsMatrixInfo rcsMatrixInfo = JSONObject.parseObject(str, RcsMatrixInfo.class);
                    result = JSON.parseObject(rcsMatrixInfo.getRecVal(), BigDecimal[][].class);
                    redisClient.delete(redisKey);
                }
                //获取数据库中的矩阵数据，然后与当前订单数据做累加

                String matrixStr = matrixInfo.getRecVal();
                if (!StringUtils.isEmpty(matrixStr)) {
                    BigDecimal[][] arr = new BigDecimal[homeSize][awaySize];
                    try {
                        arr = JSON.parseObject(item.getRecVal(), BigDecimal[][].class);
                        log.info("订单中的矩阵数据为:{}", JSONObject.toJSONString(item));
                        log.info("数据库中的矩阵数据为:{}", JSONObject.toJSONString(matrixInfo));
                    } catch (Exception e) {
                        log.error("requestId:{},当前注单存入矩阵数据有误!：{}", LogContext.getContext().getRequestId(), e);
                        log.error("requestId:{},当前注单存入矩阵数据有误, betNo:{},detail:{}", LogContext.getContext().getRequestId(), item.getBetNo(), JSONObject.toJSONString(item));
                    }
                    for (int i = 0; i < homeSize; i++) {
                        for (int j = 0; j < awaySize; j++) {
                            try {
                                //做运算的时候乘以type值（type值是1或者-1，来判断订单是否取消）
                                result[i][j] =  result[i][j].add( Optional.ofNullable(arr[i][j]).orElse(BigDecimal.ZERO).multiply(volumePercentage).multiply(BigDecimal.valueOf(type)) );
                            } catch (Exception e) {
                                log.error("requestId:{},矩阵 arr[{}][{}] 转换失败!", LogContext.getContext().getRequestId(), i, j);
                                log.error("requestId:{},矩阵转换失败!,ex:{}", LogContext.getContext().getRequestId(), e);
                            }
                        }
                    }
                }
                info.setRecVal(JSONObject.toJSONString(result));
                info.setEarlySettlementType(0);
                info.setHashUnique(md5Hex);
                try {
                    boolean flag = redisUtilsNxExtend.setNX(format, "1", rcsPredictMysqlFrequencyNacosConfig.getForecastInsertMysqlFrequency());
                    if (!flag) {
                        redisClient.setExpiry(String.format(RCS_FOOTBALL_MATRIX_MYSQL_INSERT_INSERT_KEY, item.getMatchId(),
                                matrixTypeAndPlayTypes[0], matrixTypeAndPlayTypes[1], info.getBusinessType(), info.getMatchType(), 0), JSONObject.toJSONString(info), 3 * 30 * 24 * 60 * 60L);
                        log.info("足球矩阵 频率限制 本次执行跳过！订单号：{}，key {}", item.getOrderNo(), format);
                        return;
                    } else {
                        log.info("修改原有矩阵数据,订单为{}", item.getOrderNo());
                        matrixInfoMapper.updateById(info);
                    }
                }catch (Exception e){
                    log.error("矩阵信息累加异常{}", e.getMessage());
                }
            }
        }
    }

    /**
     * 根据玩法id判断矩阵类型和玩法类型
     * @param item
     */
    @Override
    public String getMatrixTypeAndPlayType(OrderItem item){
        //获取玩法id
        Integer playId = item.getPlayId();

        /**
         * 全场比分矩阵
         */
        //波胆
        List<Integer> bd = Arrays.asList(7);
        //双重机会
        List<Integer> scjh = Arrays.asList(6);
        //其他次要玩法
        List<Integer> qtcywf = Arrays.asList(3,5,8,9,10,11,12,13,14,15,16,27,28,36,68,77,78,79,80,81,82,83,84,85,86,91,92,93,94,95,96,101,102,103,104,107,108,109,110,135,136,137,141,144,148,149,150,151,152,222,223,363,364,365,366);
        //全场比分主要玩法
        List<Integer> zywf = Arrays.asList(1,2,4);

        if(bd.contains(playId)){
            //波胆
            return "1:1";
        }
        if(scjh.contains(playId)){
            //双重机会
            return "1:2";
        }
        if(qtcywf.contains(playId)){
            //其他次要玩法
            return "1:3";
        }
        if(zywf.contains(playId)){
            //主要玩法
            return "1:4";
        }

        /**
         * 上半场比分矩阵
         */
        //上半场比分
        List<Integer> sbcbf = Arrays.asList(20, 341);
        //上半场双重机会
        List<Integer> sbcscjh = Arrays.asList(70);
        //其他次要玩法
        List<Integer> sbcqt = Arrays.asList(21,22,23,24,29,30,42,43,69,87,90,97,100,105);
        //其他次要玩法
        List<Integer> sbczywf = Arrays.asList(17,18,19);

        if(sbcbf.contains(playId)){
            //上半场比分
            return "2:1";
        }
        if(sbcscjh.contains(playId)){
            //上半场双重机会
            return "2:2";
        }
        if(sbcqt.contains(playId)){
            //其他次要玩法
            return "2:3";
        }
        if(sbczywf.contains(playId)){
            //上半场主要玩法
            return "2:4";
        }

        /**
         * 下半场比分矩阵
         */
        //下半场比分
        List<Integer> xbcbf = Arrays.asList(74, 342);
        //下半场双重机会
        List<Integer> xbcscjh = Arrays.asList(72);
        //其他玩法
        List<Integer> xbcqt = Arrays.asList(25,26,71,72,73,75,76,88,89,98,99,106,142,143);

        if(xbcbf.contains(playId)){
            //下半场比分
            return "3:1";
        }
        if(xbcscjh.contains(playId)){
            //下半场双重机会
            return "3:2";
        }
        if(xbcqt.contains(playId)){
            //其他玩法
            return "3:3";
        }

        /**
         * 全场角球矩阵
         */
        //角球独赢
        List<Integer> jqdy = Arrays.asList(111);
        //角球让球
        List<Integer> jqrq = Arrays.asList(113);
        //角球大小
        List<Integer> jqdx = Arrays.asList(114);
        //其他
        List<Integer> jqqt = Arrays.asList(112,115,116,117,118,125,225,226,227,231,232,233);

        if(jqdy.contains(playId)){
            //角球独赢
            return "4:1";
        }
        if(jqrq.contains(playId)){
            //角球让球
            return "4:2";
        }
        if(jqdx.contains(playId)){
            //角球大小
            return "4:3";
        }
        if(jqqt.contains(playId)){
            //其他
            return "4:4";
        }

        /**
         * 半场角球矩阵
         */
        //上半场角球独赢
        List<Integer> sbcjqdy = Arrays.asList(119);
        //上半场角球让球
        List<Integer> sbcjqrq = Arrays.asList(121);
        //上半场角球大小
        List<Integer> sbcjqdx = Arrays.asList(122);
        //其他
        List<Integer> sbcjqqt = Arrays.asList(120,123,124,228,229,230);

        if(sbcjqdy.contains(playId)){
            //上半场角球独赢
            return "5:1";
        }
        if(sbcjqrq.contains(playId)){
            //上半场角球让球
            return "5:2";
        }
        if(sbcjqdx.contains(playId)){
            //上半场角球大小
            return "5:3";
        }
        if(sbcjqqt.contains(playId)){
            //其他
            return "5:4";
        }

        /**
         * 全场加时矩阵
         */
        //全场加时独赢
        List<Integer> qcjsdy = Arrays.asList(126);
        //全场加时让球
        List<Integer> qcjsrq = Arrays.asList(128);
        //全场加时大小
        List<Integer> qcjsdx = Arrays.asList(127);
        //其他
        List<Integer> qcjsqt = Arrays.asList(131,234,235,236,330);

        if(qcjsdy.contains(playId)){
            //全场加时独赢
            return "6:1";
        }
        if(qcjsrq.contains(playId)){
            //全场加时让球
            return "6:2";
        }
        if(qcjsdx.contains(playId)){
            //全场加时大小
            return "6:3";
        }
        if(qcjsqt.contains(playId)){
            //其他
            return "6:4";
        }

        /**
         * 半场加时矩阵
         */
        //半场加时独赢
        List<Integer> bcjsdy = Arrays.asList(129);
        //半场加时让球
        List<Integer> bcjsrq = Arrays.asList(130);
        //半场加时大小
        List<Integer> bcjsdx = Arrays.asList(332);
        //其他（无）

        if(bcjsdy.contains(playId)){
            //半场加时独赢
            return "7:1";
        }
        if(bcjsrq.contains(playId)){
            //半场加时让球
            return "7:2";
        }
        if(bcjsdx.contains(playId)){
            //半场加时大小
            return "7:3";
        }

        return "0:0";
    }
}
