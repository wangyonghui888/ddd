package com.panda.sport.data.rcs.api.limit;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.limit.*;
import com.panda.sport.data.rcs.dto.special.RcsUserSpecialBetLimitConfigDTO;
import com.panda.sport.data.rcs.vo.RcsTournamentTemplateAcceptConfig;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

/**
 * @Description 限额api dubbo接口
 * @Param
 * @Author lithan
 * @Date 2020-09-13 16:11:20
 * @return
 **/
public interface LimitApiService {

    /**
     * 获取 玩法盘口位置 限额
     *
     * @param request
     * @return Response
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<MarkerPlaceLimitAmountResVo> getMarketPlaceLimit(Request<MarkerPlaceLimitAmountReqVo> request);

    /**
     * 获取 商户限额 配置
     * 表:rcs_quota_business_limit
     *
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<RcsQuotaBusinessLimitResVo> getRcsQuotaBusinessLimit(String busId);

    /**
     * 根据赛事查询各维度限额数据
     *
     * @param request
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<MatchLimitDataVo> getMatchLimitData(Request<MatchLimitDataReqVo> request);

    /**
     * 获取用户投注限额 上限 参考值
     *
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<UserLimitReferenceResVo> getUserLimitReference(Request<Long> request);
    
    /**
     * 获取用户一级标签id
     *
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<Integer> getUserTag(Request<Long> request);

    /**
     * 获取用户一级标签id
     *
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<Boolean> queryRcsUserConfig(String userId);

    /**
     * 获取用户特殊投注限额配置
     *
     * @return
     */
    @POST
    @Path("queryUserSpecialBetLimitConfig")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<List<RcsUserSpecialBetLimitConfigDTO>> queryUserSpecialBetLimitConfig(Request<RcsUserSpecialBetLimitConfigDTO> request);


    /**
     * 获取用户特殊投注限额配置
     *
     * @return
     */
    @POST
    @Path("queryPlayInfoById")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> queryPlayInfoById(Integer sportId,Integer playId);

    /**
     * 查询限额紧急开关服务
     *
     * @return
     */
    @POST
    @Path("queryOrderLimitKeyValue")
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> queryOrderLimitKeyValue();

    /**
     * 获取标签限额
     *
     * @return
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> getTagPercentage(Request<Long> request);
}
