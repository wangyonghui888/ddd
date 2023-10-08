package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.data.rcs.dto.order.MatchEventInfoRes;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 *
 * 查询模板接距配置服务类
 *
 */
public interface TemplateAcceptConfigServer {

    /**
     * 查询玩法集ID
     *
     * @param request
     * @return
     */
    @POST
    @Path("/queryCategorySetByPlayId")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> queryCategorySetByPlayId(Request<OrderItem> request);

    /**
     * 查询模板事件接距配置
     *
     * @param request
     * @return
     */
    @POST
    @Path("/queryWaitTimeConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<List<RcsTournamentTemplateAcceptConfig>> queryWaitTimeConfig(Request<OrderItem> request);



    /**
     * 查询事件接距配置
     *
     * @param request
     * @return
     */
    @POST
    @Path("/queryAcceptConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<RcsTournamentTemplateAcceptConfig> queryAcceptConfig(Request<OrderItem> request);




    /**
     * 查询比赛阶段配置
     *
     * @param request
     * @return
     */
    @POST
    @Path("/queryMatchEventInfo")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> queryMatchEventInfo(Request<MatchEventInfoRes> request);


    /**
     * 查询综合赛种盘口位置等延迟秒数
     *
     * @param request
     * @return
     */
    @POST
    @Path("/queryMatchDelaySeconds")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> queryMatchDelaySeconds(Request<OrderItem> request);
}
