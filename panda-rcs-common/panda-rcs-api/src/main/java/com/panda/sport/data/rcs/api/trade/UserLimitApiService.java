package com.panda.sport.data.rcs.api.trade;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.trade.RcsTradeRestrictMerchantSettingDto;
import com.panda.sport.data.rcs.dto.trade.UserIdDto;
import com.panda.sport.data.rcs.dto.trade.UserSpecialLimitDto;

/**
 * @Project Name : panda-rcs-trade-group
 * @Package Name : panda-rcs-trade-group
 * @Description : 用户限额服务
 * @Author : Paca
 * @Date : 2021-08-17 22:16
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface UserLimitApiService {

    /**
     * 修改用户特殊限额
     *
     * @param request
     * @return
     */
    @POST
    @Path("/updateUserSpecialLimit")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<Boolean> updateUserSpecialLimit(Request<UserSpecialLimitDto> request);
    
    /**
     * 修改用户特殊限额
     *
     * @param request
     * @return
     */
    @POST
    @Path("/getUserTradeRestrict")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<RcsTradeRestrictMerchantSettingDto> getUserTradeRestrict(Request<UserIdDto> request);
}
