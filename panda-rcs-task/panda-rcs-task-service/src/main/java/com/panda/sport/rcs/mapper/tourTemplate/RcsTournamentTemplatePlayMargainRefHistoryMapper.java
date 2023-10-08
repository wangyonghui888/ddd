package com.panda.sport.rcs.mapper.tourTemplate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainRefHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * @Description  联赛玩法margin历史配置引用表
 * @Param
 * @Author  carver
 * @Date  17:02 2020/11/5
 * @return
 **/
@Repository
public interface RcsTournamentTemplatePlayMargainRefHistoryMapper extends BaseMapper<RcsTournamentTemplatePlayMargainRefHistory> {

    /**
     * 插入分时margin参数配置数据
     *
     * @param marginRefList
     * @return
     */
    int insertOrUpdateBatch(@Param("list") List<RcsTournamentTemplatePlayMargainRefHistory> marginRefList);

}