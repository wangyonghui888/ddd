package com.panda.sport.data.rcs.api.tournament;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.tournament.StandardMarketSellQueryDto;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-common
 * @Package Name :  com.panda.sport.data.rcs.api.tournament
 * @Description :  赛事开售服务类
 * @Date: 2022-07-08 19:58
 * --------  ---------  --------------------------
 */
public interface TournamentOnSaleApi {
    /**
     * 取消订单
     * @param request 请求参数
     * @return 成功失败
     */
    @POST
    @Path("/confirmMarketCategorySell")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response confirmMarketCategorySell(Request<StandardMarketSellQueryDto> request);
}
