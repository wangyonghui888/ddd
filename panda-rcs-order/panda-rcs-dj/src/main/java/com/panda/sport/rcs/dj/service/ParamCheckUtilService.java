package com.panda.sport.rcs.dj.service;

import cn.hutool.core.util.ObjectUtil;
import com.panda.sport.data.rcs.dto.dj.DJBetReqVo;
import com.panda.sport.data.rcs.dto.dj.DJLimitAmoutRequest;
import com.panda.sport.data.rcs.dto.dj.DjCancelOrderReqVo;
import com.panda.sport.rcs.dj.dto.ParamCheckException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName ParamCheckUtilService
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/17 16:30
 * @Version 1.0
 **/
@Service
public class ParamCheckUtilService {


    public void checkDJLimitAmoutParam(DJLimitAmoutRequest request) throws Exception{

        if (null == request){
            throw new ParamCheckException("请求体对象为空");
        }

        if (ObjectUtil.isNull(request.getUserId())){
            throw new ParamCheckException("用户id为空");
        }

        if (StringUtils.isEmpty(request.getTester())){
            throw new ParamCheckException("用户类型为空");
        }

        if (ObjectUtil.isNull(request.getMerchant())){
            throw new ParamCheckException("商户id为空");
        }

        if (ObjectUtil.isNull(request.getSeriesType())){
            throw new ParamCheckException("串关类型为空");
        }
    }

    public void checkDJBetParam(DJBetReqVo request) throws Exception{

        if (null == request){
            throw new ParamCheckException("请求体对象为空");
        }

        if (ObjectUtil.isNull(request.getBetNum())){
            throw new ParamCheckException("注单数量为空");
        }

        if (StringUtils.isEmpty(request.getAccountName())){
            throw new ParamCheckException("会员账号为空");
        }

        if (ObjectUtil.isNull(request.getDevice())){
            throw new ParamCheckException("设备为空");
        }

        if (StringUtils.isEmpty(request.getIp())){
            throw new ParamCheckException("IP为空");
        }

        if (ObjectUtil.isNull(request.getOddUpdateType())){
            throw new ParamCheckException("会员赔率接收方式为空");
        }

        if (ObjectUtil.isNull(request.getUserId())){
            throw new ParamCheckException("会员id为空");
        }

        if (CollectionUtils.isEmpty(request.getOrderList())){
            throw new ParamCheckException("订单集合为空");
        }


    }

    public void checkDJCancelParam(DjCancelOrderReqVo request) throws Exception {

        if (null == request) {
            throw new ParamCheckException("请求体对象为空");
        }

        if (request.getOrderType() != 1 || request.getOrderType() != 2 || request.getOrderType() != 3 || request.getOrderType() != 4) {
            throw new ParamCheckException("订单类型为空");
        }

        if (StringUtils.isEmpty(request.getOrderIds())) {
            throw new ParamCheckException("DJ订单号为空");
        }

        if (StringUtils.isEmpty(request.getOrderNo())) {
            throw new ParamCheckException("OB订单号为空");
        }

        if (request.getReasonCode() <= 0) {
            throw new ParamCheckException("取消原因编码为空");
        }

        if (request.getTime() <= 0) {
            throw new ParamCheckException("当前时间戳精确到秒没传");
        }
    }

}
