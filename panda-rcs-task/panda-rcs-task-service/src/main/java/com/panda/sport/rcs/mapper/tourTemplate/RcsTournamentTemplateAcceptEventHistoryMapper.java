package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateAcceptEventHistory;
import feign.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Description   联赛模板事件从表
 * @Param 
 * @Author  toney
 * @Date  20:02 2020/5/10
 * @return 
 **/
@Repository
public interface RcsTournamentTemplateAcceptEventHistoryMapper extends BaseMapper<RcsTournamentTemplateAcceptEventHistory> {

    /**
     * 插入滚球玩法集接拒单参数配置数据
     *
     * @param acceptEventList
     * @return
     */
    int insertOrUpdateBatch(@Param("list") List<RcsTournamentTemplateAcceptEventHistory> acceptEventList);
}