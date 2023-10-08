package com.panda.sport.rcs.mapper.tourTemplate;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargainHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


/**
 * @Description  联赛玩法margin历史配置表
 * @Param
 * @Author  carver
 * @Date  17:02 2020/11/5
 * @return
 **/
@Repository
public interface RcsTournamentTemplatePlayMargainHistoryMapper extends BaseMapper<RcsTournamentTemplatePlayMargainHistory> {

    /**
     * margin加入到历史表
     * @param list
     * @return
     */
    int insertOrUpdateBatch(@Param("list") List<RcsTournamentTemplatePlayMargainHistory> list);

}