package com.panda.sport.data.rcs.api.tournament;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.tournament.*;
import com.panda.sport.rcs.exeception.RcsServiceException;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

/**
 * @author :  Waldkir
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.mgr.wrapper
 * @Description :
 * @Date: 2023-01-02 14:50
 */
public interface TournamentTemplateByMatchService {

    /**
     * 查询赛事模板相关字段
     * @param requestParam
     * @return
     * @throws RcsServiceException
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<MatchTemplateDataResVo> queryMatchTemplateData(Request<MatchTemplateDataReqVo> requestParam) throws RcsServiceException;

    /**
     * 查询赛事模板中指定玩法相关字段
     * @param requestParam
     * @return
     * @throws RcsServiceException
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<MatchTemplatePlayMarginDataResVo> queryMatchTemplatePlayMarginData(Request<MatchTemplatePlayMarginDataReqVo> requestParam) throws RcsServiceException;

    /**
     * 查询赛事模板中指定玩法中所生效的分时节点相关字段
     * @param requestParam
     * @return
     * @throws RcsServiceException
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<MatchTemplatePlayMarginRefDataResVo> queryMatchTemplatePlayMarginRefData(Request<MatchTemplatePlayMarginRefDataReqVo> requestParam) throws RcsServiceException;

    /**
     * 根据联赛id查询相关联赛相关数据
     * @param requestParam
     * @return
     * @throws RcsServiceException
     */
    @POST
    @Consumes({ContentType.APPLICATION_JSON_UTF_8})
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    Response<String> queryTournamentPropertyData(Request<TournamentPropertyReqVo> requestParam) throws RcsServiceException;

}
