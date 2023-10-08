package com.panda.sport.rcs.trade.service;

import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import com.panda.sport.rcs.pojo.dto.SpecEventConfigDTO;
import com.panda.sport.rcs.pojo.param.AutoOpenMarketStatusParam;
import com.panda.sport.rcs.pojo.param.RcsSpecEventConfigParam;
import com.panda.sport.rcs.pojo.param.UpdateSpecEventStatusParam;
import com.panda.sport.rcs.vo.HttpResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AO特殊事件配置接口
 *
 * @author gray
 * @version 1.0.0
 * @date 2023/4/10 15:38
 */

public interface RcsSpecEventConfigService {
    
    /**
     * 根据ID修改
     * @param rcsSpecEventConfigParam
     * @return
     */
    HttpResponse<Integer> updateSpecEventConfigById(@Param("record") RcsSpecEventConfigParam rcsSpecEventConfigParam);
    
    
    /**
     * 根据赛事ID和时间编码修改激活时间、激活参数、激活次数
     * @param rcsSpecEventConfig
     * @return
     */
    HttpResponse<Integer> updateActiveByMatchId(@Param("record") RcsSpecEventConfig rcsSpecEventConfig);


    HttpResponse<Integer> updateSpecEventConfigProbByMatchId(RcsSpecEventConfig rcsSpecEventConfig);

    /**
     * 根据赛事ID查询AO事件配置
     * @param rcsSpecEventConfig
     * @return
     */
    List<RcsSpecEventConfig> querySpecEventConfigList(@Param("record") RcsSpecEventConfig rcsSpecEventConfig);
    
    /**
     * 批量增加事件配置
     *
     * @param srcObj
     * @param targetObj
     * @return
     */
    HttpResponse<Integer> batchInsert(SpecEventConfigDTO srcObj, SpecEventConfigDTO targetObj);
    
    /**
     * 赛事级事件开关状态修改
     * @param specEventStatusParam
     * @return
     */
    HttpResponse<Boolean> updateSpecEventStatus(UpdateSpecEventStatusParam specEventStatusParam);

    List<String> qryRcsTemplateEventInfoConfig();

    void pushMatchSpecEventStatus(Long matchId);

    /**
     * 查询自动玩法开盘开关状态
     * @param autoOpenMarketParam
     * @return
     */
    HttpResponse<Integer> getAutoOpenMarketStatus(AutoOpenMarketStatusParam autoOpenMarketParam);
    
    /**
     * 修改自动开盘开关状态
     * @param autoOpenMarketParam
     * @return
     */
    HttpResponse<Boolean> updateAutoOpenMarketStatus(AutoOpenMarketStatusParam autoOpenMarketParam);

    /**
     * 赛事是否特殊事件激活中
     * @param matchId
     * @return
     */
    boolean isMatchSpecEvent(Long matchId);
}
