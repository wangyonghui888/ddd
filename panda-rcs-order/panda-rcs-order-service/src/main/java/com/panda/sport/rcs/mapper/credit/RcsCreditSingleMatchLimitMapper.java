package com.panda.sport.rcs.mapper.credit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.data.rcs.dto.credit.RcsCreditSingleMatchLimit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用网单场赛事限额
 * @Author : Paca
 * @Date : 2021-04-30 19:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Repository
public interface RcsCreditSingleMatchLimitMapper extends BaseMapper<RcsCreditSingleMatchLimit> {
    /**
     * 批量插入或更新
     *
     * @param list
     * @return
     */
    int batchInsertOrUpdate(@Param("list") List<RcsCreditSingleMatchLimit> list);
}
