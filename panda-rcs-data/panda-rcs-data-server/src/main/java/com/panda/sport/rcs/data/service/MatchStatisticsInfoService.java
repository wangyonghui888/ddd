package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.MatchStatisticsInfo;
import com.panda.sport.rcs.pojo.dto.MatchStatisticsInfoDTO;

/**
* @ClassName MatchStatisticsInfoService
* @Description: TODO
* @Author Vector
* @Date 2019/10/11
**/
public interface MatchStatisticsInfoService extends IService<MatchStatisticsInfo> {


    int insertMatchStatisticsInfo(MatchStatisticsInfo matchStatisticsInfo);

    /**
     * @MethodName: getMatchInfoByMatchId
     * @Description: 获取赛事基本数据
     * @Param:
     * @Return:
    **/
    MatchStatisticsInfo getMatchInfoByMatchId(Long id);

    int insertOrUpdate(MatchStatisticsInfo matchStatisticsInfo);

    int insertOrUpdate(MatchStatisticsInfoDTO data);
}
