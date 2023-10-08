package com.panda.sport.rcs.third.external;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.MtsIsCacheEnum;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.third.common.Constants;
import com.panda.sport.rcs.third.config.BetGuardApiConfig;
import com.panda.sport.rcs.third.entity.betguard.dto.BetGuardBaseDto;
import com.panda.sport.rcs.third.entity.betguard.dto.FilterTransferDto;
import com.panda.sport.rcs.third.entity.betguard.dto.bts2pa.BetPlacedDto;
import com.panda.sport.rcs.third.entity.betguard.dto.bts2pa.BetResultedDto;
import com.panda.sport.rcs.third.entity.betguard.dto.bts2pa.RollbackDto;
import com.panda.sport.rcs.third.entity.common.ThirdOrderExt;
import com.panda.sport.rcs.third.enums.BetGuardErrorEnum;
import com.panda.sport.rcs.third.mapper.RcsCtsOrderExtMapper;
import com.panda.sport.rcs.third.service.handler.IOrderHandlerService;
import com.panda.sport.rcs.third.service.third.impl.BCServiceImpl;
import com.panda.sport.rcs.third.util.cache.RcsLocalCacheUtils;
import com.panda.sport.rcs.third.util.encrypt.HMACSHA256Util;
import com.panda.sport.rcs.third.util.encrypt.JwtUtils;
import com.panda.sport.rcs.third.util.encrypt.MD5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.panda.sport.rcs.enums.OrderInfoStatusEnum.EARLY_REFUSE;
import static com.panda.sport.rcs.enums.OrderInfoStatusEnum.SCROLL_REFUSE;
import static com.panda.sport.rcs.third.common.Constants.*;
import static com.panda.sport.rcs.third.enums.BetGuardErrorEnum.*;

/**
 * @author Beulah
 * @date 2023/3/29 16:40
 * @description 提供给BC-BetGuard的api
 */
@RestController
@Slf4j
@Api(value = "提供给BC-BetGuard的api接口")
@RequestMapping("/bc")
public class BCExternalApi {


    @Autowired
    BetGuardApiConfig betGuardApiConfig;
    @Resource
    TUserMapper tUserMapper;
    @Autowired
    TOrderDetailMapper orderDetailMapper;

    @Autowired
    RcsCtsOrderExtMapper rcsCtsOrderExtMapper;

    @Resource
    IOrderHandlerService iOrderHandlerService;
    @Resource
    BCServiceImpl bcService;

    @Resource
    RedisClient redisClient;


    /**
     * BC投注用户是否登陆校验
     *
     * @param dto betGuard入参
     * @return 校验结果
     */
    @PostMapping("/GetClientDetails")
    @ApiOperation(value = "用户检验接口")
    @ApiParam(name = "BetGuardBaseDto", value = "betGuard入参")
    public Map<String, Object> getClientDetails(@RequestBody BetGuardBaseDto dto) {
        log.info("::::收到BC用户信息检验【GetClientDetails】请求={}", JSONObject.toJSONString(dto));
        Map<String, Object> vo = new HashMap<>();
        try {
            //判断令牌有效性
            String authToken = dto.getAuthToken();
            if (authToken == null || !JwtUtils.checkToken(authToken)) {
                vo.put("errorCode", NotAuthorized.getErrorCode());
                vo.put("errorText", NotAuthorized.getErrorText());
                return vo;
            }
            //验签
            Map<String, Object> map = new TreeMap<>();
            map.put("AuthToken", dto.getAuthToken());
            map.put("TS", dto.getTS());
            if (checkSign2(map, dto.getHash(), null)) {
                vo.put("errorCode", NotAuthorized.getErrorCode());
                vo.put("errorText", NotAuthorized.getErrorText());
                return vo;
            }
            //解析令牌
            Map<String, Object> userInfo = JwtUtils.getMemberIdByJwtToken(authToken);
            String userId = String.valueOf(userInfo.get("id"));
            //判断用户是否登陆
            TUser isLogin = checkUserIsValid(userId, userId);
            if (isLogin == null) {
                log.warn("::{}::用户不存在", userId);
                vo.put("errorCode", "1008");
            }

            vo.put("Login", "player_login");
            vo.put("CurrencyId", StringUtils.isBlank(isLogin.getCurrencyCode()) ? "CNY" : isLogin.getCurrencyCode());
            vo.put("LanguageId", "en");
            vo.put("Email", isLogin.getEmail());
            //vo.put("BirthDate",isLogin.get);
            //vo.put("Gender",1);
            vo.put("ExternalId", isLogin.getUid() + "");
            //vo.put("CurrentIp","123.123.123.123");
            //vo.put("Phone","234324");
            //vo.put("Address","player address");
            vo.put("ErrorCode", "0");
            vo.put("ErrorText", "");
            log.info("::{}::【GetClientDetails】返回：{}", userId, JSONObject.toJSONString(vo));
        } catch (Exception e) {
            log.error("::用户检验接口::异常,参数:{}", JSONObject.toJSONString(dto), e);
            vo.put("errorCode", InternalError.getErrorCode());
            vo.put("errorText", InternalError.getErrorText());
        }
        return vo;
    }


    /**
     * BC投注前置校验
     *
     * @param dto betGuard入参
     * @return 订单结果
     */
    @PostMapping("/BetPlaced")
    @ApiOperation(value = "投注前置校验")
    @ApiParam(name = "BetPlacedDto", value = "betGuard入参")
    public Map<String, Object> betPlaced(@RequestBody BetPlacedDto dto) {
        String orderNo = String.valueOf(dto.getBetId());
        log.info("::{}::收到BC订单请求【betPlaced】参数={}", orderNo, JSONObject.toJSONString(dto));
        Map<String, Object> res = new TreeMap<>();
        try {
            String transactionId = dto.getTransactionId().toString();
            Map<String, Object> repeatRequest = isRepeatRequest(transactionId, orderNo);
            if (repeatRequest != null) {
                return repeatRequest;
            }
            //验签
            /*Map<String, Object> signMap = new TreeMap<>();
            signMap.put("AuthToken", dto.getAuthToken());
            signMap.put("TS", dto.getTS());
            signMap.put("TransactionId", dto.getTransactionId());
            signMap.put("BetId", dto.getBetId());
            signMap.put("Amount", dto.getAmount());
            signMap.put("Created", dto.getCreated());
            signMap.put("BetType", dto.getBetType());
            signMap.put("TotalPrice", dto.getTotalPrice());
            if (!checkSign2(signMap, dto.getHash(), dto.getBetId())) {
                log.warn("::{}::验签失败,不往下处理,TransactionId={}", orderNo, dto.getTransactionId());
                res.put("ErrorCode", NotAuthorized.getErrorCode());
                res.put("ErrorText", NotAuthorized.getErrorText());
                return res;
            }*/
            //解析令牌
            Map<String, Object> userInfo = JwtUtils.getMemberIdByJwtToken(dto.getAuthToken());
            String userId = String.valueOf(userInfo.get("id"));
            //判断用户是否登陆
            String errorCode = "0";
            String errorText = "";
            TUser tUser = checkUserIsValid(userId, String.valueOf(dto.getTransactionId()));
            if (tUser == null) {
                log.warn("::{}::未获取到用户信息,不往下处理,TransactionId={}", orderNo, dto.getTransactionId());
                errorCode = "1008";
            }
            //是否支持提前结算 IsBonus 为True，则意味着该投注将无法提前结算
            res.put("IsBonus", false);
            res.put("ErrorCode", errorCode);
            res.put("ErrorText", errorText);
            //投注处理完成标识 2分钟
            redisClient.setExpiry(transactionId, 1, 2 * 60L);
        } catch (Exception e) {
            log.error("::{}::投注结果确认::异常, 参数:{}", orderNo, JSONObject.toJSONString(dto), e);
            res.put("ErrorCode", InternalError.getErrorCode());
            res.put("ErrorText", InternalError.getErrorText());
        }
        log.info("::{}::【BetPlaced】返回：{}", orderNo, JSONObject.toJSONString(res));
        return res;
    }


    /**
     * 订单结果通知
     *
     * @param dto betGuard入参
     * @return 订单结果
     */
    @PostMapping("/BetResulted")
    @ApiOperation(value = "订单结果通知")
    @ApiParam(name = "BetResultedDto", value = "betGuard入参")
    public Map<String, Object> betResulted(@RequestBody BetResultedDto dto) {
        String betId = String.valueOf(dto.getBetId());
        log.info("::{}::收到BC订单结果通知【betResulted】参数={}", betId, JSONObject.toJSONString(dto));
        Map<String, Object> res = new TreeMap<>();
        String errorCode = "0";
        String errorText = "";
        try {
            //判断订单是否存在
            String bcOrderKey = String.format(THIRD_BC_ORDER_CACHE, dto.getBetId());
            String order = redisClient.get(bcOrderKey);
            ThirdOrderExt thirdOrderExt = JSONObject.parseObject(order, ThirdOrderExt.class);
            if (thirdOrderExt == null) {
                log.warn("::{}::订单信息不存", betId);
                res.put("ErrorCode", BetNotFoundError.getErrorCode());
                res.put("ErrorText", BetNotFoundError.getErrorText());
                return res;
            }
            String orderNo = thirdOrderExt.getOrderNo();
            log.info("::{}::收到BC订单结果通知【betResulted】请求={}", orderNo, JSONObject.toJSONString(dto));
            String transactionId = dto.getTransactionId().toString();
            Map<String, Object> repeatRequest = isRepeatRequest(transactionId, orderNo + "");
            if (repeatRequest != null) {
                return repeatRequest;
            }
            /*Map<String, Object> map = new TreeMap<>();
            map.put("AuthToken", dto.getAuthToken());
            map.put("TS", dto.getTS());
            map.put("TransactionId", dto.getTransactionId());
            map.put("BetId", dto.getBetId());
            map.put("BetState", dto.getBetState());
            map.put("Amount", dto.getAmount());
            if (!checkSign2(map, dto.getHash(), dto.getBetId())) {
                errorCode = NotAuthorized.getErrorCode();
                errorText = NotAuthorized.getErrorText();
                res.put("ErrorCode", errorCode);
                res.put("ErrorText", errorText);
                return res;
            }*/
            //解析令牌
            /*Map<String, Object> userInfo = JwtUtils.getMemberIdByJwtToken(dto.getAuthToken());
            String userId = String.valueOf(userInfo.get("id"));
            //判断用户是否登陆
            TUser tUser = checkUserIsValid(userId, String.valueOf(dto.getTransactionId()));
            if (tUser == null) {
                errorCode = "1008";
            }*/
            log.info("::{}::获取到订单信息={}", orderNo, JSONObject.toJSONString(thirdOrderExt));
            //处理我方接拒逻辑 （取消/拒绝 - 拒单   输赢 - 走pa接单）
            int betState = dto.getBetState();
            switch (betState) {
                //用户投注结果被撤销/取消，投注结果为未结算/初始状态- BetState：1，金额：0。
                case 1:
                    //用户投注被拒绝或退回，金额被退回用户账户（与下注时的金额完全相同）BetState：2，金额：BetStakeAmount。
                case 2:
                    thirdOrderExt.setThirdOrderStatus(2);
                    //是否已取消
                    String canceledKey = String.format(THIRD_ORDER_CANCELED, thirdOrderExt.getOrderNo());
                    String orderCanceled = redisClient.get(canceledKey);
                    if (StringUtils.isBlank(orderCanceled)) {
                        orderReject(thirdOrderExt);
                    } else {
                        log.warn("::{}::注单已取消,跳过", thirdOrderExt.getOrderNo());
                    }
                    break;
                //用户输了，BC后端通知输了- BetState:3, Amount:0。
                case 3:
                    //用户投注获胜（金额由BC后端计算，通常大于投注金额） - BetState:4, Amount: 获胜金额。
                case 4:
                    thirdOrderExt.setThirdOrderStatus(1);
                    //iOrderHandlerService.orderByPa(thirdOrderExt);
                    break;
                //用户投注提前结算（金额由BC后端计算，通常小于投注金额） - BetState：5，Amount：提前结算金额。
                case 5:
                    break;
            }
            //todo
            res.put("ErrorCode", errorCode);
            res.put("ErrorText", errorText);
            redisClient.setExpiry(transactionId, 1, 2 * 60L);
        } catch (Exception e) {
            log.error("::{}::订单结果通知::异常,参数:{}",betId, JSONObject.toJSONString(dto), e);
            res.put("ErrorCode", InternalError.getErrorCode());
            res.put("ErrorText", InternalError.getErrorText());
            FilterTransferDto filterTransferDto = new FilterTransferDto();
            bcService.resendFailedTransfers(filterTransferDto);
        }
        return res;
    }

    /**
     * BC投注回滚
     * 1.当需要在PM后端中回滚已处理的下注时，BC后端会调用此方法
     * 2.当用户下注等待PM确认且 BetPlaced 调用失败、发生某些通信错误或BC后端中的下注交易失败时
     * 该方法BC端可能会重试
     *
     * @param dto betGuard入参
     * @return 校验结果
     */
    @PostMapping("/Rollback")
    @ApiOperation(value = "BC投注回滚")
    @ApiParam(name = "RollbackDto", value = "betGuard入参")
    public Map<String, Object> rollback(@RequestBody RollbackDto dto) {
        Long orderNo = dto.getBetId();
        log.info("::{}::收到BC投注回滚【rollback】请求={}", orderNo, JSONObject.toJSONString(dto));
        Map<String, Object> map = new TreeMap<>();
        Map<String, Object> res = null;
        try {
            String transactionId = dto.getTransactionId().toString();
            Map<String, Object> repeatRequest = isRepeatRequest(transactionId, orderNo + "");
            if (repeatRequest != null) {
                return repeatRequest;
            }
            map.put("AuthToken", dto.getAuthToken());
            map.put("TS", dto.getTS());
            map.put("TransactionId", orderNo);
            res = new TreeMap<>();
            String errorCode = "0";
            String errorText = "";
            if (!checkSign2(map, dto.getHash(), dto.getBetId())) {
                errorCode = NotAuthorized.getErrorCode();
                errorText = NotAuthorized.getErrorText();
            }
            //判断订单是否存在
            String bcOrderKey = String.format(THIRD_BC_ORDER_CACHE, orderNo);
            String order = redisClient.get(bcOrderKey);
            ThirdOrderExt thirdOrderExt = JSONObject.parseObject(order, ThirdOrderExt.class);
            if (thirdOrderExt == null) {
                res.put("ErrorCode", BetGuardErrorEnum.BetNotFoundError.getErrorCode());
                res.put("ErrorText", BetGuardErrorEnum.BetNotFoundError.getErrorText());
                return res;
            }

            //是否已经取消过
            String canceledKey = String.format(THIRD_ORDER_CANCELED, thirdOrderExt.getOrderNo());
            String orderCanceled = redisClient.get(canceledKey);
            if (StringUtils.isBlank(orderCanceled)) {
                orderReject(thirdOrderExt);
                redisClient.setExpiry(canceledKey, 1, 2 * 60L);
            } else {
                log.warn("::{}::注单已取消,跳过", thirdOrderExt.getOrderNo());
            }
            //标识处理完成
            redisClient.setExpiry(transactionId, 1, 2 * 60L);
            res.put("ErrorCode", errorCode);
            res.put("ErrorText", errorText);
        } catch (Exception e) {
            log.error("::BC投注回滚::异常,参数:{}", JSONObject.toJSONString(dto), e);
            res.put("ErrorCode", InternalError.getErrorCode());
            res.put("ErrorText", InternalError.getErrorText());
            //重试
            FilterTransferDto filterTransferDto = new FilterTransferDto();
            bcService.resendFailedTransfers(filterTransferDto);
        }
        return res;
    }


    /**
     * 判断用户是否登陆
     *
     * @param userId 用户id
     * @return 检查结果
     */
    private TUser checkUserIsValid(String userId, String orderNo) {
        try {
            String userKey = String.format(Constants.USER_CACHE_KEY, userId);
            Object o = RcsLocalCacheUtils.timedCache.get(userKey);
            TUser tUser;
            if (Objects.isNull(o)) {
                LambdaQueryWrapper<TUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TUser::getUid, userId);
                tUser = tUserMapper.selectOne(wrapper);
                if (tUser != null) {
                    RcsLocalCacheUtils.timedCache.put(userKey, tUser);
                    return tUser;
                }
                return null;
            }
            return (TUser) o;
        } catch (Exception e) {
            log.error("::{}::获取用户{}信息异常:", orderNo, userId, e);
        }
        return null;
    }


    /**
     * 验签
     *
     * @param params 拼接的参数
     * @return 是否通过
     */
    public boolean checkSign(Map<String, Object> params, String hash, Long orderNo) {
        if (params == null || params.size() == 0) {
            return false;
        }
        params.remove("Hash");
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        String data;
        Object o = params.get("Amount");
        if (Objects.nonNull(o)) {
            BigDecimal amount = new BigDecimal(params.get("Amount").toString());
            String amountStr = amount.toPlainString();
            params.put("Amount", amountStr);
            //金额特殊处理 用于签名
            if (amountStr.indexOf(".") > 0) {
                String substring = amountStr.substring(amountStr.indexOf("."), amountStr.length() - 1);
                if (substring.length() == 1) {
                    amountStr = amountStr + "0";
                }
                if (substring.length() > 3) {
                    amountStr = amountStr.substring(0, amountStr.indexOf(".") + 4);
                }
            } else {
                amountStr = amountStr + ".00";
            }
            data = gson.toJson(params).replace(":\"" + amount + "\"", ":" + amountStr);
        } else {
            data = gson.toJson(params);
        }
        log.info("::{}::BC签名校验参数data={}", orderNo, data);
        String sign = null;
        try {
            sign = HMACSHA256Util.calculateHMac(betGuardApiConfig.getSharedKey(), data);
        } catch (Exception e) {
            log.info("::{}::BC签名校验异常,参数hash={}", orderNo, hash);
        }
        return hash.equals(sign);
    }

    private void orderReject(ThirdOrderExt ext) {
        ext.setOrderStatus(2);
        bcService.updateOrder(ext);
        Integer infoStatus = EARLY_REFUSE.getCode();
        String infoMsg = EARLY_REFUSE.getMode();
        if ("1".equalsIgnoreCase(ext.getList().get(0).getIsScroll())) {
            infoStatus = SCROLL_REFUSE.getCode();
            infoMsg = SCROLL_REFUSE.getMode();
        }
        iOrderHandlerService.updateOrder(ext, infoStatus, infoMsg, MtsIsCacheEnum.CTS.getValue());
    }


    @RequestMapping("/test")
    public Map<String, Object> test(@RequestBody BetPlacedDto dto) {
        Map<String, Object> res = new TreeMap<>();
        Map<String, Object> signMap = new TreeMap<>();
        try {
            String token = getToken("2023040122");
            //验签
            signMap.put("aAuthToken", token);
            long timeMillis = System.currentTimeMillis();
            signMap.put("TS", timeMillis);
            signMap.put("TransactionId", dto.getTransactionId());
            signMap.put("BetId", dto.getBetId());
            signMap.put("Amount", dto.getAmount());
            signMap.put("Created", dto.getCreated());
            signMap.put("BetType", dto.getBetType());
            signMap.put("TotalPrice", dto.getTotalPrice());
            String sign = createSign(signMap, betGuardApiConfig.getSharedKey());
            res.put("token", token);
            res.put("time", timeMillis);
            res.put("hash", sign);
        } catch (Exception e) {
            log.error("::签名异常:{}", JSONObject.toJSONString(dto), e);
            res.put("ErrorCode", InternalError.getErrorCode());
            res.put("ErrorText", InternalError.getErrorText());
        }
        return res;
    }


    /**
     * 创建令牌
     *
     * @param userId 用户id
     */
    private String getToken(String userId) {
        Object token = RcsLocalCacheUtils.timedCache.get(String.format(USER_TOKEN_CACHE_KEY, userId));
        if (Objects.isNull(token)) {
            //创建令牌
            token = JwtUtils.getJwtToken(userId, "panda_rcs_third");
            //放入缓存
            RcsLocalCacheUtils.timedCache.put(String.format(USER_TOKEN_CACHE_KEY, userId), token);
        }
        return token.toString();
    }

    /**
     * 生成签名
     */
    private String createSign(Map<String, Object> params, String key) {
        StringBuffer sb = new StringBuffer();
        params.forEach((k, v) -> {
            sb.append(k).append(v);
        });
        sb.append(key);
        String sbStr = sb.toString();
        log.info("::::签名串:{},key={}", sbStr, key);
        return MD5Util.md5Hex(sbStr);
    }

    /**
     * 验签
     *
     * @param params 拼接的参数
     * @return 是否通过
     */
    public boolean checkSign2(Map<String, Object> params, String hash, Long orderNo) {
        if (params == null || params.size() == 0) {
            return false;
        }
        params.remove("Hash");
        StringBuffer sb = new StringBuffer();
        params.forEach((k, v) -> {
            sb.append(k).append(v);
        });
        sb.append(betGuardApiConfig.getSharedKey());
        String sbStr = sb.toString();
        return hash.equals(MD5Util.md5Hex(sbStr));
    }


    /**
     * 判断是否重复提交
     *
     * @param transactionId
     * @param orderNo
     * @return
     */
    private Map<String, Object> isRepeatRequest(String transactionId, String orderNo) {
        try {
            Map<String, Object> res = new TreeMap<>();
            String repeat = redisClient.get(transactionId);
            if (StringUtils.isNotBlank(repeat)) {
                log.warn("::{}::重复请求投注,不往下处理,transactionId={}", orderNo, transactionId);
                res.put("ErrorCode", NotAuthorized.getErrorCode());
                res.put("ErrorText", NotAuthorized.getErrorText());
                return res;
            }
        } catch (Exception e) {
            log.error("::{}::判断TransactionId是否重复出现异常,transactionId={},", orderNo, transactionId, e);
        }
        return null;
    }


}
