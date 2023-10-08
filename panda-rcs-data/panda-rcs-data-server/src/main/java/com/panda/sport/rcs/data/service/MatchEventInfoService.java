package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.MatchEventInfo;

/**
 * @ClassName MatchEventInfoService
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/10/10 
**/
public interface MatchEventInfoService extends IService<MatchEventInfo> {

    int insertOrUpdate(MatchEventInfo record);

    /**
     * 查询最后一个事件
     * @param standardMatchId
     * @return
     */
    MatchEventInfo getLast(Long standardMatchId);

}
