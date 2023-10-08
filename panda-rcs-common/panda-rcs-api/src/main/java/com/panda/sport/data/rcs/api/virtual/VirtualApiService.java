package com.panda.sport.data.rcs.api.virtual;


import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.virtual.BetAmountLimitReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetAmountLimitResVo;
import com.panda.sport.data.rcs.dto.virtual.BetReqVo;
import com.panda.sport.data.rcs.dto.virtual.BetResVo;


/**
 * @Description 虚拟赛事api
 * @Param
 * @Author lithan
 * @Date  2020-12-22 14:31:17
 * @return
 **/
public interface VirtualApiService  {
    /**
     * 获取虚拟赛事   最大最小值限额
     *
     * @param request
     * @return Response
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<List<BetAmountLimitResVo>> getBetAmountLimit(Request<BetAmountLimitReqVo> request);

    /**
     *  虚拟赛事 投注 单关
     *
     * @param request
     * @return Response
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<BetResVo> bet(Request<BetReqVo> request);

}

