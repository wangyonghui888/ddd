package com.panda.sport.rcs.mgr.wrapper;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;

/**
 * @ClassName MatchStatisticsInfoService
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/11
 **/
public interface MatchStatisticsInfoService extends IService<MatchStatisticsInfo> {
    /**
     * @MethodName: getMatchInfoByMatchId
     * @Description: 获取赛事基本数据
     * @Param:
     * @Return:
     **/
    MatchStatisticsInfo getMatchInfoByMatchId(Long id);
}
