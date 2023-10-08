package com.panda.sport.rcs.mapper.credit;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSinglePlayLimit;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网玩法限额
 * @Author : Paca
 * @Date : 2021-04-30 19:01
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsCreditSinglePlayLimitMapper extends BaseMapper<RcsCreditSinglePlayLimit> {
    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(@Param("list") List<RcsCreditSinglePlayLimit> list);
}
