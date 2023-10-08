package com.panda.rcs.push.mapper;

import com.panda.rcs.push.entity.vo.MatchInfoBaseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchInfoMapper {

    /**
     * 查询制定时间更新的完赛赛事信息
     * @param selectTime
     * @return
     */
    List<MatchInfoBaseVo> getFinishMatch(@Param("selectTime") Long selectTime);

}
