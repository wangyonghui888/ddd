package com.panda.sport.rcs.mapper.credit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.credit.RcsCreditSinglePlayBetLimit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网玩法单注限额
 * @Author : Paca
 * @Date : 2021-07-17 16:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsCreditSinglePlayBetLimitMapper extends BaseMapper<RcsCreditSinglePlayBetLimit> {
    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(@Param("list") List<RcsCreditSinglePlayBetLimit> list);
}
