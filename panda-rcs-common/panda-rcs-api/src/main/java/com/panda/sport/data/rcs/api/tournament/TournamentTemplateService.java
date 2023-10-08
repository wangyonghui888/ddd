package com.panda.sport.data.rcs.api.tournament;

import java.util.List;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.tournament.TournamentTemplateDTO;
import com.panda.sport.rcs.exeception.RcsServiceException;


/**
 * @author :  carver
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.mgr.wrapper
 * @Description :
 * @Date: 2020-09-12 20:50
 */
public interface TournamentTemplateService {

    /**
     * 根据赛种获取所有等级模板，且当前联赛等级的专用模板
     *
     * @param requestParam
     * @return
     */
    public Response queryTournamentLevelTemplate(Request<TournamentTemplateDTO> requestParam) throws RcsServiceException;

    /**
     * 生成赛事模板数据
     * @param requestParam
     * @return
     */
    public Response putTemplateToMatchTemplate(Request<TournamentTemplateDTO> requestParam) throws RcsServiceException;

    /**
     * 设置操盘手，取消开售
     * @param requestParam
     * @return
     */
    public Response putMatchTemplateCancel(Request<TournamentTemplateDTO> requestParam) throws RcsServiceException;
    
    
    /**
     * 修改操盘平台
    * @Title: putMatchReplaceRiskManagerCode 
    * @Description: TODO 
    * @param @param requestParam
    * @param @return
    * @param @throws RcsServiceException    设定文件 
    * @return Response    返回类型 
    * @throws
     */
    public Response putMatchReplaceRiskManagerCode(Request<TournamentTemplateDTO> requestParam) throws RcsServiceException;
    
    
    /**
     * 根据赛种和联赛id，获取所有模板
     * @param requestParam
     * @return
     */
    public Response queryAllTournamentTemplateById(Request<TournamentTemplateDTO> requestParam) throws RcsServiceException;
    
    /**
     * 在线编辑哪里，已开售的赛事，需要返回当前赛事引用的哪个模板
    * @Title: queryMatchTemplateByMatchId 
    * @Description: TODO 
    * @param @param requestParam
    * @param @return
    * @param @throws RcsServiceException    设定文件 
    * @return Response    返回类型 
    * @throws
     */
    public Response queryMatchTemplateByMatchId(Request<List<Long>> requestParam) throws RcsServiceException;
    
    /**
     * 赛程取消赛事关联，根据赛事id和数据源编码，判断接拒是否设置当前数据源
     * @param requestParam
     * @return
     */
    public Response queryAcceptConfigByMatchId(Request<TournamentTemplateDTO> requestParam) throws RcsServiceException;
}
